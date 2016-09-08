package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class SavePage extends BasePage {

  @FindBy( id = "fileNameTextBox" )
  private ExtendedWebElement txtFileName;

  @FindBy( id = "navigationListBox" )
  private ExtendedWebElement selectFolder;

  @FindBy( xpath = "//div[contains(@id, 'fileChooser') and @title='%s']" )
  private ExtendedWebElement folderItem;

  public SavePage( WebDriver driver ) {
    super( driver );
    if ( !isOpened( dlgSave ) ) {
      Assert.fail( "SavePage is not opened!" );
    }
  }

  public boolean isOpened( long timeout ) {
    return super.isOpened( dlgSave, timeout );
  }

  public void setFileName( String fileName ) {
    type( txtFileName, fileName );
  }

  public void setFolder( String folderPath ) {
    //TODO: verify maybe required folder is already selected!!
    if ( selectFolder.getText().equals( folderPath ) ) {
      LOGGER.info( "Current path is already activated. There is no sense to reselect it again. " + folderPath );
      return;
    }
    
    // Select root folder
    select( selectFolder, "/" );

    for ( String folder : Utils.parsePath( folderPath ) ) {
      if (folder.equals( "home" )) {
        //workaround to activate not by physical name but using caption
        folder = L10N.getText( folder ); 
      }
      doubleClick( format( folderItem, folder ) );
      // doubleClick( folderItem );
    }

    // Verification
    /*
     * Temporary disabled as display name and actual name are not the same if (!selectFolder.getText().equals(
     * folderPath )) { Assert.fail("Required path was not selected in Save As dialog!"); }
     */
  }

  public void buttonSave() {
    click( btnOK );
  }

  public void buttonCancel() {
    click( btnCancel );
  }
}
