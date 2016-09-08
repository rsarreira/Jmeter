package com.pentaho.qa.gui.web.puc;

import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;
import com.pentaho.services.utils.Utils;
import com.pentaho.services.Report.Colour;
import com.pentaho.services.Report.PaletteColour;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public abstract class ReportPage extends FilePage {

  @FindBy( xpath = "//iframe[contains(@name,'frame_')]" )
  protected ExtendedWebElement reportFrame;

  @FindBy( tagName = "iframe" )
  protected List<ExtendedWebElement> frames;

  @FindBy(
      xpath = "//div[@id='layoutPanel']//span[contains(@id, 'optionsBtn_button') and text()='{L10N:popupMenuActionChartProps}']" )
  protected ExtendedWebElement btnChartOptions;

  @FindBy( id = "CP_labelFontFamily" )
  protected ExtendedWebElement fontFamily;

  @FindBy( id = "CP_labelSize" )
  protected ExtendedWebElement fontSize;

  @FindBy( id = "CP_labelStyle" )
  protected ExtendedWebElement fontStyle;

  @FindBy( id = "dlgBtnSave" )
  protected ExtendedWebElement btnOkSave;

  @FindBy( id = "cmdNew" )
  public ExtendedWebElement btnNewReport;

  @FindBy( id = "cmdOpen" )
  public ExtendedWebElement btnOpenReport;

  @FindBy( id = "pageLoadingMessage" )
  protected ExtendedWebElement pageLoadingMessage;

  @FindBy( xpath = "%s" )
  protected ExtendedWebElement contentElement;

  @FindBy( xpath = "//td[text()='%s']" )
  public ExtendedWebElement contentElementWithText;

  @FindBy( xpath = "//div[text()='%s']" )
  public ExtendedWebElement tebleElementWithText;

  @FindBy(
      xpath = "//div[@id = 'dijit__MasterTooltip_0']/div[contains(@class,'dijitTooltipContents')]/div[contains(.,'%s')]" )
  public ExtendedWebElement tooltip;

  public ReportPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  protected boolean isOpened() {
    return isOpened( EXPLICIT_TIMEOUT / 2 );
    /*
     * switchToFrame(); // TODO: identify do we need to switch back into default frame or not return
     * isElementNotPresent( warningSorryMessage, EXPLICIT_TIMEOUT / 30 );
     */
  }

  public boolean isOpened( long timeout ) {
    return super.isOpened( timeout );
    /*
     * switchToFrame(); // TODO: identify do we need to switch back into default frame or not return
     * isElementNotPresent( warningSorryMessage, timeout );
     */
  }

  public SoftAssert verifyContent( String elementsPresent, String elementsNotPresent ) {
    return verifyContent( elementsPresent, elementsNotPresent, contentElement );
  }

  public SoftAssert verifyTableContent( String elementsPresent, String elementsNotPresent ) {
    return verifyContent( elementsPresent, elementsNotPresent, tebleElementWithText );
  }

  public SoftAssert verifyContent( String elementsPresent, String elementsNotPresent,
      ExtendedWebElement contentElement ) {
    switchToFrame();
    SoftAssert softAssert = new SoftAssert();

    String[] elements;

    // Only check presence if argument has a value.
    if ( !Strings.isNullOrEmpty( elementsPresent ) ) {
      elements = elementsPresent.split( ";" );
      for ( int i = 0; i < elements.length; i++ ) {
        String elementPresent = elements[i];

        if ( !elementPresent.isEmpty() && !format( contentElement, elementPresent ).isElementPresent(
            EXPLICIT_TIMEOUT ) ) {
          softAssert.fail( "Element with xpath '" + elementPresent + "' was not found!" );
        } else {
          LOGGER.info( "Element with xpath '" + elementPresent + "' was found as expected." );
        }
      }
    } else {
      LOGGER.info( "No elements were specified to verify presence." );
    }

    // Only check absence if argument has a value.
    if ( !Strings.isNullOrEmpty( elementsNotPresent ) ) {
      elements = elementsNotPresent.split( ";" );
      for ( int i = 0; i < elements.length; i++ ) {
        String elementNotPresent = elements[i];
        if ( !elementNotPresent.isEmpty() && format( contentElement, elementNotPresent ).isElementPresent(
            EXPLICIT_TIMEOUT / 20 ) ) {
          softAssert.fail( "Element with xpath '" + elementNotPresent + "' was mistakenly found!" );
        } else {
          LOGGER.info( "Element with xpath '" + elementNotPresent + "' was not found as expected." );
        }
      }
    } else {
      LOGGER.info( "No elements were specified to verify absence." );
    }

    // switchToDefault();

    return softAssert;
  }

  public SoftAssert verifyChartFont( String family, String size, String style ) {
    getDriver().switchTo().frame( reportFrame.getElement() );
    SoftAssert softAssert = new SoftAssert();

    click( btnChartOptions );

    String actualFontFamily = getSelectedValue( fontFamily );
    String actualFontSize = getSelectedValue( fontSize );
    String actualFontStyle = getSelectedValue( fontStyle );
    if ( !actualFontFamily.equals( family ) ) {
      softAssert.fail( "TS041558: font family is invalid! Expected '" + family + "'; actual '" + actualFontFamily );
    }

    if ( !actualFontSize.equals( size ) ) {
      softAssert.fail( "TS041558: font size is invalid! Expected '" + size + "'; actual '" + actualFontSize );
    }

    if ( !actualFontStyle.equals( style ) ) {
      softAssert.fail( "TS041558: font style is invalid! Expected '" + style + "'; actual '" + actualFontStyle );
    }

    click( btnOkSave );

    switchToDefault();

    return softAssert;
  }

  // Check if file has been already saved
  public boolean isSaved( String fileName ) {
    switchToDefault();
    boolean res = getSelectedTabName().equals( fileName );
    switchToFrame();
    return res;
  }

  public void dndByCoordinate( ExtendedWebElement source, int x, int y ) {
    Actions dragAndDrop = new Actions( getDriver() );
    Action action = dragAndDrop.dragAndDropBy( source.getElement(), x, y ).build();
    try {
      action.perform();
    } catch ( MoveTargetOutOfBoundsException e ) {
      LOGGER.error( "Exception occurs during drag and drop!: " + e.toString() );
      dragAndDrop.release().build().perform();
    }
  }

  /***
   * Set the colour of the passed in colorPicker
   * 
   * @param theColorPicker
   * @param p
   */
  protected void setColorPickerPaletteColour( ExtendedWebElement theColorPicker, PaletteColour p ) {
    ExtendedWebElement thePaletteTable = null;
    ExtendedWebElement targetColor = null;

    Colour c = p.getColour();

    // is the passed in ExtendedWebElement a table.dijitPaletteTable? (PIR)
    if ( theColorPicker.getAttribute( "class" ).toString().contains( "digitPaletteTable" ) ) {
      thePaletteTable = theColorPicker;
    }
    // else, is there a sibling's whose eventual offspring is a table.dijitPaletteTable? (PAR)
    else {

      /*
       * TODO thePaletteTable is assigned via breaking out of the Carina framework because the
       * "ExtendedWebElement.findExtendedWebElement( By by )" method cannot find parents, only children.
       * 
       * Additionally, the following will throw an exception if it cannot be found, so no error checking here
       */

      thePaletteTable = Utils.findFromElem( theColorPicker, "..//table[@class='dijitPaletteTable']");
    }

    // make sure the table is present
    // if the palette isn't present, see if we can click it to open it up (PAR)
    if ( !thePaletteTable.isElementPresent() ) {
      // assume it is from PAR and try to open it
      theColorPicker.click();

    }

    // else the palette is present, search for the specific color
    targetColor =
        Utils.findFromElem( thePaletteTable, "//img[@style='background-color: " + c.getHexValue() + "']");

    // click the targetColor
    targetColor.click();

  }
}
