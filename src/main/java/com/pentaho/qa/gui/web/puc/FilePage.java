package com.pentaho.qa.gui.web.puc;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.util.Strings;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class FilePage extends FramePage {
  protected String fileName;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOk;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancel;

  @FindBy( css = "div.warning-header" )
  protected ExtendedWebElement warningSorryMessage;

  @FindBy( css = "div.pentaho-tabWidget-selected div" )
  protected ExtendedWebElement openedFileTitle;

  @FindBy( xpath = "//td/div[text()='%s']" )
  protected ExtendedWebElement fileTitleItem;

  @FindBy( css = "div.pentaho-busy-indicator-msg-container" )
  protected ExtendedWebElement msgInProgress;

  @FindBy( id = "editContentButton_btn" )
  protected ExtendedWebElement btnToolBarEdit;

  @FindBy( css = "#saveButton_img" )
  protected ExtendedWebElement btnToolBarSave;

  @FindBy( xpath = ".//*[@id='cmdSaveAs' or @id='saveAsButton_btn']" )
  public ExtendedWebElement btnToolBarSaveAs;

  @FindBy( xpath = "//div[text()='{L10N:confirm}']/../../../..//div[text()='{L10N:confirmTabClose}']" )
  public ExtendedWebElement dlgWarningUnsavedContent;

  @FindBy( xpath = "//td/div[text()='%s']/../../td/img" )
  protected ExtendedWebElement imgCloseFile;

  @FindBy( id = "openTabInNewWindow" )
  protected ExtendedWebElement openTabInNewWindow;

  public FilePage( WebDriver driver, String name ) {
    super( driver );
    setName( name );
    fileName = name;
  }

  protected boolean isOpened() {
    return isOpened( EXPLICIT_TIMEOUT / 2 );
  }

  protected boolean isOpened( long timeout ) {
    while ( msgInProgress.isElementPresent( EXPLICIT_TIMEOUT / 20 ) ) {
      LOGGER.info( "Operation in progress detected. Waiting 3 sec..." );
      pause( 3 );
    }

    switchToDefault();
    // TODO: maybe perform verification if frame exists
    return isElementWithTextPresent( openedFileTitle, fileName, EXPLICIT_TIMEOUT / 10 );
  }

  private void clickToolbarButton( ExtendedWebElement element ) {
    switchToDefault();
    click( element );
  }
  
  /**
   * Clicks the edit content button in the toolbar to go into edit or view mode.
   */
  public void clickEditContentButton() {
    switchToDefault();
    btnToolBarEdit.click();
  }

  /**
   * Determines whether or not the edit file button is already pressed.
   * 
   * @return Returns true when the button is pressed (in edit mode). Otherwise, returns false.
   */
  public boolean isEditFilePressed() {
    switchToDefault();
    return btnToolBarEdit.getAttribute( "class" ).contains( "toolbar-toggle-button-down" );
  }

  private boolean handleSaveDialog( String fileName, String folderPath, boolean isOverwrite ) {
    if ( isElementPresent( dlgSave, EXPLICIT_TIMEOUT / 2 ) ) {
      if ( !Strings.isNullOrEmpty( fileName ) && !Strings.isNullOrEmpty( folderPath ) ) {
        setParameters( fileName, folderPath, isOverwrite );
        setName( fileName );
      } else {
        LOGGER.error( "You tried to save a modified or new file with invalid parameters" );
      }
    } else {
      switchToFrame();
    }
    return isElementNotPresent( dlgSave, EXPLICIT_TIMEOUT / 2 );
  }

  public boolean clickSave() {
    return clickSave( null, null, false );
  }

  public boolean clickSave( String fileName, String folderPath, boolean isOverwrite ) {
    clickToolbarButton( btnToolBarSave );
    return handleSaveDialog( fileName, folderPath, isOverwrite );
  }

  public boolean clickSaveAs( String fileName, String folderPath ) {
    return clickSaveAs( fileName, folderPath, false );
  }

  public boolean clickSaveAs( String fileName, String folderPath, boolean isOverwrite ) {
    clickToolbarButton( btnToolBarSaveAs );
    return handleSaveDialog( fileName, folderPath, isOverwrite ); // TODO: ensure that saveAs is not required
  }

  protected void setParameters( String fileName, String folderPath ) {
    setParameters( fileName, folderPath, false );
  }

  protected void setParameters( String fileName, String folderPath, boolean isOverride ) {
    SavePage savePage = new SavePage( getDriver() );
    savePage.setFolder( folderPath );
    savePage.setFileName( fileName );
    savePage.buttonSave();
    if ( !pentahoDialogs.isEmpty() ) {
      if ( isOverride ) {
        savePage.buttonSave();
      } else {
        savePage.buttonCancel();
      }
    }
    // [MG] This method causes screen to scroll up, and the only way to set it back to normal is by scrolling to a
    // "non-visible" element that should be visible but isn't due to this weird webdriver behavior.
    btnToolBarSave.scrollTo();
  }

  public void clickClose( String fileName ) {
    switchToDefault();
    // [MG] had to switch to JavascriptExecutor as a regular click did not work for all files, it works for reports, but
    // not for dashboards.
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript( "arguments[0].click();", format( imgCloseFile, fileName ).getElement() );
  }

  public void clickCancel() {
    click( btnCancel );
  }

  public void clickOK() {
    click( btnOk );
  }

  public boolean isClosed( String fileName ) {
    return isElementNotPresent( format( fileTitleItem, fileName ) );
  }

  public boolean isUnsavedContentDialogPresent() {
    return isElementPresent( dlgWarningUnsavedContent, EXPLICIT_TIMEOUT / 15 );
  }

  public void openTabInNewWindow() {
    switchToDefault();
    getActiveTab().rightClick();
    openTabInNewWindow.click();
    click( btnOK );
  }

  public void switchToLastWindow() {
    ArrayList<String> tabs = new ArrayList<String>( driver.getWindowHandles() );
    driver.switchTo().window( tabs.get( tabs.size() - 1 ) );
  }

  public void switchToWindowByTitle( String title ) {
    // Is applicable only for one window with the same name
    driver.switchTo().window( title );
  }

  /**
   * Gets the text of each element in the specified list.
   * 
   * @param elements
   *          The list of elements to retrieve the text from.
   * @return Returns a String list of the elements' text.
   */
  public List<String> getElementListText( List<ExtendedWebElement> elements ) {
    List<String> elementText = new ArrayList<String>();

    if ( elements != null && !elements.isEmpty() ) {
      for ( ExtendedWebElement element : elements ) {
        // The text will not be retrieved if the element is not in view.
        element.scrollTo();
        elementText.add( element.getElement().getText().trim() );
      }
    }

    return elementText;
  }

  /**
   * Gets the values for the specified attribute for all elements in the specified list.
   * 
   * @param elements
   *          The list of elements to retrieve the attribute value from.
   * @param attribute
   *          The name of the attribute to retrieve the value from.
   * @return Returns a List<String> that contains the values of the elements' attributes.
   */
  protected List<String> getElementListAttribute( List<ExtendedWebElement> elements, String attribute ) {
    List<String> attributeValues = new ArrayList<String>();

    if ( elements != null && !elements.isEmpty() ) {
      for ( ExtendedWebElement element : elements ) {
        attributeValues.add( element.getAttribute( attribute ) );
      }
    }

    return attributeValues;
  }
}
