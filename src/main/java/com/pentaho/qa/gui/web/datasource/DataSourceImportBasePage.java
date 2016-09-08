package com.pentaho.qa.gui.web.datasource;

import java.io.IOException;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.google.common.base.Strings;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public abstract class DataSourceImportBasePage extends BasePage {

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()=%s]" )
  protected ExtendedWebElement dlgImportTitle;

  @FindBy( id = "importDialog_cancel" )
  protected ExtendedWebElement btnClose;

  @FindBy( id = "importDialog_accept" )
  protected ExtendedWebElement btnImport;

  @FindBy( id = "addButton" )
  protected ExtendedWebElement btnAdd;

  @FindBy( id = "removeButton" )
  protected ExtendedWebElement btnRemove;

  // Overwrite Confirmation dialog (no localization on those button labels yet)
  @FindBy( xpath = "//button[@class='pentaho-button' and text()='Ok']" )
  protected ExtendedWebElement btnOK;

  @FindBy( xpath = "//button[@class='pentaho-button' and text()='Cancel']" )
  protected ExtendedWebElement btnCancel;

  @FindBy( id = "%s" )
  protected ExtendedWebElement txtUploadFile;
  

  public DataSourceImportBasePage( WebDriver driver ) {
    super( driver );
  }

  public void clickAdd() {
    click( btnAdd, true );
  }

  public void clickRemove() {
    click( btnRemove, true );
  }

  public void clickImport() {
    click( btnImport, true );
  }

  public void clickClose() {
    click( btnClose, true );
  }

  public void clickOk() {
    click( btnOK );
  }

  public void clickCancel() {
    click( btnCancel );
  }

  protected boolean isButtonEnabled( ExtendedWebElement btn ) {
    return btn.getElement().isEnabled();
  }

  public abstract boolean isOverwriteConfirmationDialogPresent();

  public abstract void verify();
  
  public void setFile( String hiddenElementId, String filePath ) {
    if ( !Strings.isNullOrEmpty( filePath ) ) {

      // get script_home location and append filePath to this path
      String current = "";
      try {
        current = new java.io.File( "." ).getCanonicalPath();
      } catch ( IOException e ) {
        throw new RuntimeException( "Unable to identify canonical path for current project directory!", e.getCause() );
      }

      current = current.replace( "\\", "/" );

      LOGGER.info( "current path is: " + current );
      filePath = current + "/" + filePath;

      if ( !getDriver().toString().contains( "internet explorer" ) && !getDriver().toString().contains( "LINUX" ) ) {
        filePath = filePath.replace( "/", "\\\\" );
      }
      
      JavascriptExecutor jse = (JavascriptExecutor)getDriver();
      // [YD] Workaround to the "Element is not currently visible and so may not be interacted with" before sending keys
      String jsCmd = "document.getElementById('"+hiddenElementId+"').style.visibility = 'visible';";
      jse.executeScript(jsCmd);
      
      LOGGER.info( "File path is: " + filePath );
      // type the file path to the hidden input element
      format(txtUploadFile, hiddenElementId).getElement().sendKeys( filePath );
      //txtHiddenPath.getElement().sendKeys( filePath );
      pause( 1 );
    }
  }

}
