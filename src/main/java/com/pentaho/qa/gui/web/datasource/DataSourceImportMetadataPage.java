package com.pentaho.qa.gui.web.datasource;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.google.common.base.Strings;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceImportMetadataPage extends DataSourceImportBasePage {

  private static final String IMPORT_METADATA = L10N.getText( "importDialog.IMPORT_METADATA" );
  @FindBy( id = "fileLabel" )
  protected ExtendedWebElement lblXmiFile;

  @FindBy( id = "uploadFileLabel" )
  protected ExtendedWebElement fakePathMetadataFile;

  private final String METADATA_FILE_UPLOAD_ID = "metaFileUpload";

  @FindBy( id = "uploadMetaButton" )
  protected ExtendedWebElement btnUploadMetadata;

  @FindBy( id = "domainIdLabel" )
  protected ExtendedWebElement lblDomainId;

  @FindBy( id = "domainIdText" )
  protected ExtendedWebElement txtDomainId;

  @FindBy( id = "localizationBundlesLabel" )
  protected ExtendedWebElement lblLocalizedBundles;

  @FindBy( css = "#localizedBundlesTree .dataWrapper .gwt-HTML" )
  protected List<ExtendedWebElement> localizedBundles;

  // Overwrite Confirmation dialog
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[contains(text(),'{L10N:Metadata.OVERWRITE_EXISTING_SCHEMA}')]" )
  protected ExtendedWebElement dlgOverwriteConfirmation;

  public DataSourceImportMetadataPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "'Import Metadata' is not opened!" );
    }
  }

  private boolean isOpened() {
    return super.isOpened( format( dlgImportTitle, L10N.generateConcatForXPath( IMPORT_METADATA ) ) );
  }

  public void setXmiFile( String filePath ) {
    setFile( METADATA_FILE_UPLOAD_ID, filePath );
  }

  public void setDomainId( String domainId ) {
    if ( !Strings.isNullOrEmpty( domainId ) ) {
      type( txtDomainId, domainId );
    }
  }

  public boolean isOverwriteConfirmationDialogPresent() {
    boolean res = false;

    if ( isElementPresent( dlgOverwriteConfirmation, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Overwrite message displayed: " + dlgOverwriteConfirmation.getText() );
      res = true;
    }

    return res;
  }

  @Override
  public void verify() {
    SoftAssert softAssert = new SoftAssert();

    softAssert.assertTrue( isElementPresent( lblXmiFile, 0 ),
        "'XMI File:' label is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isElementPresent( fakePathMetadataFile, 0 ),
        "'fakePath' field is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isElementPresent( btnUploadMetadata, 0 ),
        "'Browse' button is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isElementPresent( lblDomainId, 0 ),
        "'Domain ID:' label is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isElementPresent( txtDomainId, 0 ),
        "'Domain ID' field is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isElementPresent( lblLocalizedBundles, 0 ),
        "'Localized Bundles' label is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( ( localizedBundles.size() == 0 ),
        "There are localized bundles present, this list should be empty by default." );
    softAssert.assertTrue( isElementPresent( btnAdd, 0 ),
        "'Add' button is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isButtonEnabled( btnAdd ),
        "'Add' button is disabled, and it should be enabled by default!" );
    softAssert.assertTrue( isElementPresent( btnRemove, 0 ),
        "'Remove' button is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isButtonEnabled( btnRemove ),
        "'Remove' button is disabled, and it should be enabled by default!" );
    softAssert.assertTrue( isElementPresent( btnImport, 0 ),
        "'Import' button is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( !isButtonEnabled( btnImport ),
        "'Import' button is enabled, and it should be disabled by default!" );
    softAssert.assertTrue( isElementPresent( btnClose, 0 ),
        "'Close' button is not present in the Import Metadata dialog!" );
    softAssert.assertTrue( isButtonEnabled( btnClose ),
        "'Close' button is disabled, and it should be enabled by default!" );

    softAssert.assertAll();
  }

}
