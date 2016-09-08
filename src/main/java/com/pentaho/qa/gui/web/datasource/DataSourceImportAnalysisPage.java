package com.pentaho.qa.gui.web.datasource;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceImportAnalysisPage extends DataSourceImportBasePage {

  private static final String IMPORT_ANALYSIS = L10N.getText( "importDialog.IMPORT_MONDRIAN" );
  @FindBy( id = "analysisFileLabel" )
  protected ExtendedWebElement lblMondrianFile;

  @FindBy( id = "schemaNameLabel" )
  protected ExtendedWebElement fakePathMondrianFile;

  private final String ANALYSIS_FILE_UPLOAD_ID = "analysisFileUpload";

  @FindBy( id = "uploadAnalysisButton" )
  protected ExtendedWebElement btnUploadAnalysis;

  // Data Source radio buttons
  private static final String DATA_SOURCE_RADIO_BUTTON_SELECT_AVAILABLE =
      L10N.getText( "importDialog.SELECT_AVAILABLE_RADIO" );
  private static final String DATA_SOURCE_RADIO_BUTTON_ENTER_MANUAL = L10N.getText( "importDialog.ENTER_MANUAL_RADIO" );

  @FindBy( xpath = "//label[@for='%s']" )
  private ExtendedWebElement dataSourcePreferenceLabel;

  @FindBy( xpath = "//label[text()='%s']" )
  private ExtendedWebElement dataSourcePreference;

  @FindBy( css = "#analysisPreferencesRadioGroup [type=radio]" )
  protected List<ExtendedWebElement> dsPreferenceRadioButtons;

  @FindBy( css = "#analysisPreferencesRadioGroup .gwt-RadioButton label" )
  protected List<ExtendedWebElement> dsPreferenceRadioButtonLabels;

  @FindBy(
      xpath = "//div[@id='analysisPreferencesDeck']//div[@id='datasourceLabel' and contains(text(),'importDialog.DATA_SOURCE')]" )
  protected List<ExtendedWebElement> lblDataSource;

  @FindBy( css = ".drop-popup .popupContent .custom-list-item .gwt-Label" )
  protected List<ExtendedWebElement> availableConnectionList;

  @FindBy( id = "connectionList" )
  protected ExtendedWebElement availableConnectionDropDown;

  @FindBy( id = "editbutton" )
  protected ExtendedWebElement btnEdit;

  // Overwrite Confirmation dialog
  @FindBy( xpath = "//div[contains(text(),'{L10N:Mondrian.OVERWRITE_EXISTING_SCHEMA}')]" )
  protected ExtendedWebElement dlgOverwriteConfirmation;

  @FindBy( xpath = "//div[contains(text(),'{L10N:Mondrian.OVERWRITE_EXISTING_CATALOG}')]" )
  protected ExtendedWebElement dlgOverwriteConfirmationXmla;

  public DataSourceImportAnalysisPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "'Import Analysis' is not opened!" );
    }
  }

  private boolean isOpened() {
    return super.isOpened( format( dlgImportTitle, L10N.generateConcatForXPath( IMPORT_ANALYSIS ) ) );
  }

  public void setMondrianFile( String filePath ) {
    setFile( ANALYSIS_FILE_UPLOAD_ID, filePath );
  }

  public void setSelectAvailable( Boolean selectAvailable ) {
    if ( selectAvailable == null ) {
      LOGGER.error( "Unable to select data source preference due to invalid parameter." );
      return;
    }

    if ( selectAvailable ) {
      click( format( dataSourcePreference, DATA_SOURCE_RADIO_BUTTON_SELECT_AVAILABLE ) );
    } else {
      click( format( dataSourcePreference, DATA_SOURCE_RADIO_BUTTON_ENTER_MANUAL ) );
    }
  }

  private String getSelectedDsPreferenceRadioButtonName() {
    String id = null;

    for ( int i = 0; i < dsPreferenceRadioButtons.size(); i++ ) {
      if ( dsPreferenceRadioButtons.get( i ).getElement().isSelected() ) {
        id = dsPreferenceRadioButtons.get( i ).getElement().getAttribute( "id" );
      }
    }

    return format( dataSourcePreferenceLabel, id ).getText();
  }

  public boolean isRadioButtonSelectAvailableSelected() {
    return ( getSelectedDsPreferenceRadioButtonName().equals( DATA_SOURCE_RADIO_BUTTON_SELECT_AVAILABLE ) );
  }

  public void selectDataSource( String dataSourceName ) {
    selectPopup( availableConnectionDropDown, dataSourceName );
  }

  public boolean isOverwriteConfirmationDialogPresent() {
    boolean res = false;

    if ( isElementPresent( dlgOverwriteConfirmation, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Overwrite message displayed: " + dlgOverwriteConfirmation.getText() );
      res = true;
    }
    // Workaround for various type of messages, Mondrian file overwrite confirmation
    else if ( isElementPresent( dlgOverwriteConfirmationXmla, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Overwrite message displayed: " + dlgOverwriteConfirmationXmla.getText() );
      res = true;
    }
    return res;

  }

  public void verify() {
    SoftAssert softAssert = new SoftAssert();

    softAssert.assertTrue( isElementPresent( lblMondrianFile, 0 ),
        "'Mondrian File:' label is not present in the Import Analysis dialog!" );
    softAssert.assertTrue( isElementPresent( fakePathMondrianFile, 0 ),
        "'fakePath' field is not present in the Import Analysis dialog!" );
    softAssert.assertTrue( isElementPresent( btnUploadAnalysis, 0 ),
        "'Browse' button is not present in the Import Analysis dialog!" );
    softAssert.assertTrue( verifyDataSourcePreferenceRadioButtons(),
        "'Select from available data sources.' and 'Manually enter data source parameter values' radio buttons are not present!" );
    softAssert.assertTrue( isRadioButtonSelectAvailableSelected(),
        "'Select from available data sources.' radio button is not selected by default" );
    click( availableConnectionDropDown );
    softAssert.assertTrue( ( availableConnectionList.size() > 0 ), "There are no available data sources" );
    // [MG] click away so the available connection list does not get in the way of other elements in the page.
    click( format( dlgImportTitle, L10N.generateConcatForXPath( IMPORT_ANALYSIS ) ) );
    softAssert.assertTrue( isElementPresent( btnImport, 0 ),
        "'Import' button is not present in the Import Analysis dialog!" );
    softAssert.assertTrue( !isButtonEnabled( btnImport ),
        "'Import' button is enabled, and it should be disabled by default!" );
    softAssert.assertTrue( isElementPresent( btnClose, 0 ),
        "'Close' button is not present in the Import Analysis dialog!" );
    softAssert.assertTrue( isButtonEnabled( btnClose ),
        "'Close' button is disabled, and it should be enabled by default!" );

    softAssert.assertAll();
  }

  public boolean verifyDataSourcePreferenceRadioButtons() {
    boolean res = true;

    if ( !dsPreferenceRadioButtonLabels.get( 0 ).getText().equals( DATA_SOURCE_RADIO_BUTTON_SELECT_AVAILABLE ) ) {
      LOGGER.error( DATA_SOURCE_RADIO_BUTTON_SELECT_AVAILABLE + " radio button doesn't exist in the dialog!" );
      res = false;
    }

    if ( !dsPreferenceRadioButtonLabels.get( 1 ).getText().equals( DATA_SOURCE_RADIO_BUTTON_ENTER_MANUAL ) ) {
      LOGGER.error( DATA_SOURCE_RADIO_BUTTON_ENTER_MANUAL + " radio button doesn't exist in the dialog!" );
      res = false;
    }

    return res;
  }

}
