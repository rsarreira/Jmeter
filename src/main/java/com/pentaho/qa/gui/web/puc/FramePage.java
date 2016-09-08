package com.pentaho.qa.gui.web.puc;

import java.util.List;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.PentahoUser;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public abstract class FramePage extends BasePage {
  protected static final Logger LOGGER = Logger.getLogger(FramePage.class);
  
  protected boolean inFrame;
  protected String activeFrame;
  
  @FindBy( xpath = "//iframe[contains(@name,'frame_')]" )
  protected ExtendedWebElement frameItem;
  
  @FindBy( name = "%s" )
  public ExtendedWebElement reportFrame;

  public FramePage( WebDriver driver ) {
    super( driver );
    
    // switch into default frame to verify if iframe exists
    //driver.switchTo().defaultContent();
    setupFrame();
    //activeFrame = "";
  }

  public void setupFrame() {
    if ( activeFrame == null ) {
      activeFrame = "";
    }

    WebElement frameElement = (WebElement) ( (JavascriptExecutor) driver ).executeScript( "return window.frameElement" );
    if ( frameElement != null ) {
      // already inside the frame
      inFrame = true;

      driver.switchTo().defaultContent();
      activeFrame = "";
      
      activeFrame = switchToFrame();
      LOGGER.info( "Verification performed inside the frame: " + activeFrame );
    } else {
      inFrame = isElementPresent( frameItem, EXPLICIT_TIMEOUT / 10 );
    }
  }
  
  public void resetActiveFrame() {
    activeFrame = "";
    driver.switchTo().defaultContent();
  }
  
  public String switchToFrame() {
    if ( !inFrame ) {
      //LOGGER.info( "Item '" + name + "' supposed to be not in frame!" );
      return "";
    }
    
    //LOGGER.info( "Item '" + name + "' supposed to be in frame!" );
    
    if ( inFrame && !activeFrame.isEmpty() ) {
        LOGGER.info( "Already in frame: " + activeFrame );
        return activeFrame;
    }
    
    driver.switchTo().defaultContent();
    if ( !isElementPresent( activeTab ) ) {
      LOGGER.error( "Unable to recognized active tab!" );
    }

    int index = 0;
    int openedTab = PentahoUser.getOpenedTab();
    if (openedTab == 0) {
      Assert.fail("There is no active tab for switching!");
    }
    
    // get currently opened Tab index
    if ( listTabs.size() > 0 &&
        listTabs.get( listTabs.size() - 1 ).getElement().equals( activeTab.getElement() ) ) {
      index = openedTab - 1; // implementation supports open/close tabs with the only restriction: we can activate the
                                 // latest tab now!
    } else {
      Assert.fail("Unable to detect active tab window by counter openedTab=" + openedTab);
    }
    pause( 1 );
    return switchToFrame( "frame_" + index );
  }
  
  public String switchToFrame( int index ) {
    driver.switchTo().defaultContent(); //reset to default one
    
    List<ExtendedWebElement> existingFrames =
      findExtendedWebElements( By.xpath( "//iframe[contains(@name,'frame_')]" ) );
    if ( index >= existingFrames.size() ) {
      LOGGER.error( "Trying to get non-existing frame: " + index + "; total is: " + existingFrames.size() );
      return "";
    }

    if ( !activeFrame.isEmpty() ) {
      LOGGER.info( "Already in frame: " + activeFrame );
      return activeFrame;
    }

    activeFrame = switchToFrame( "frame_" + ( index ) );
    return activeFrame;
  }

  public String switchToFrame( String name ) {

      ExtendedWebElement iframeItem = format( reportFrame, name );
      if ( !isElementPresent( iframeItem ) ) {
        throw new RuntimeException( "Unable to identify frame: " + iframeItem.getNameWithLocator() );
      }
      // Switch to the report iframe
      driver.switchTo().frame( iframeItem.getElement() );
      activeFrame = name;
      return activeFrame;
  }
  
  public String switchToFrame( ExtendedWebElement frameElement) {
    if ( activeFrame.equals( frameElement.getName() ) ) {
      LOGGER.info( "Already in frame: " + activeFrame );
      return activeFrame;
    }
    
    LOGGER.info( "Active frame before switch: " + activeFrame );
    if ( !frameElement.isElementPresent( 3 ) ) {
      LOGGER.info( "Active frame during runtime exception: " + activeFrame );
      LOGGER.error("Unable to find frame: " + frameElement.getNameWithLocator() + "\n" + getDriver().getPageSource());
    }

    driver.switchTo().frame( frameElement.getElement() );
    activeFrame = frameElement.getName();
    
    LOGGER.info( "Active frame after switch: " + activeFrame );
    return activeFrame;
}
  
  public void switchToDefault(  ) {
    driver.switchTo().defaultContent();
    activeFrame = "";
  }
  
  /**
   * Gets the current theme of PUC by determining which theme menu item is selected.
   * 
   * @return Returns the current theme.
   */
  @Override
  public Theme getTheme() {
    Theme currentTheme = super.getTheme();

    // Switch to default before switching back to the frame. Otherwise, switchToFrame will see that the frame needed to
    // be selected is already selected.
    switchToDefault();
    switchToFrame();

    return currentTheme;
  }
}
