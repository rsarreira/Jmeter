package com.pentaho.qa.gui.web.datasource;

import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.connection.DatabaseConnectionPage;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceWizardPage extends BasePage {

  public static final String DEFAULT_SOURCE_TYPE = L10N.getText( "datasourceDialog.SelectDatabaseType" ); // "Select
                                                                                                          // Source
                                                                                                          // Type"
  public static final String SOURCE_TYPE_CSV = L10N.getText( "csv.datasource.name" ); // "CSV File"
  public static final String SOURCE_TYPE_SQL_QUERY = L10N.getText( "sql.datasource.name" ); // "SQL Query"
  public static final String SOURCE_TYPE_DB_TABLES = L10N.getText( "multitable.DATABASE_TABLES" ); // "Database
                                                                                                   // Table(s)"

  @FindBy( xpath = "//div[text()='%s']" )
  private ExtendedWebElement datasourceType;

  @FindBy( xpath = "//table[@id='connectionList']//div[text()='%s']" )
  private ExtendedWebElement connectionItem;

  @FindBy(
      xpath = "//table[@id='connectionList']//table[@class='custom-list-item custom-list-item-selected']//div[@class='gwt-Label']" )
  protected ExtendedWebElement selectedConnectionItem;

  // 'Deleting connection will cause any report(s) using this connection to not render correctly.'
  protected static final String REMOVE_CONFIRMATION_DIALOG = L10N.getText( "removeConfirmationDialog.confirmMessage" );
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[contains(text(), normalize-space(%s))]" )
  protected ExtendedWebElement dlgRemoveDataSource;

  // 'Successfully deleted the connection'
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:ConnectionController.CONNECTION_DELETED}']" )
  protected ExtendedWebElement dlgSuccess;

  // Model radio buttons
  private static final String MODEL_RADIO_BUTTON_DEFAULT = L10N.getText( "summaryDialog.useDefaultModel" ); // "Keep
                                                                                                            // default
                                                                                                            // model"
  private static final String MODEL_RADIO_BUTTON_CUSTOMIZE = L10N.getText( "summaryDialog.editNewModel" ); // "Customize
                                                                                                           // model now"
  @FindBy( xpath = "//label[@for='%s']" )
  private ExtendedWebElement modelLabel;

  @FindBy( xpath = "//label[text()='%s']" )
  private ExtendedWebElement model;

  // 'Data Source Wizard'
  @FindBy( xpath = "//div[text()='{L10N:WIZARD_TITLE}']" )
  protected ExtendedWebElement textDataSourceWizard;

  @FindBy( id = "datasourceName" )
  protected ExtendedWebElement dataSourceName;

  // 'Select Source Type'
  @FindBy( xpath = "//div[@class='gwt-Label' and text()='{L10N:datasourceDialog.SelectDatabaseType}']" )
  protected ExtendedWebElement selectDataSourceType;

  @FindBy( css = "#datatypeMenuList .gwt-Label" )
  protected ExtendedWebElement selectedDataSourceType;

  @FindBy( id = "main_wizard_window_accept" )
  public ExtendedWebElement btnWizardFinish;

  @FindBy( id = "main_wizard_window_cancel" )
  public ExtendedWebElement btnCancelWizard;

  @FindBy( id = "main_wizard_window_extra2" )
  public ExtendedWebElement btnWizardNext;

  @FindBy( id = "summaryDialog_accept" )
  public ExtendedWebElement btnOK;

  @FindBy( id = "datasourceAdminDialog_cancel" )
  public ExtendedWebElement btnClose;

  @FindBy( css = "#addConnection .pentaho-addbutton" )
  protected ExtendedWebElement btnAddConnection;

  @FindBy( css = "#editConnection .pentaho-updatebutton" )
  protected ExtendedWebElement btnEditConnection;

  @FindBy( css = "#removeConnection .pentaho-deletebutton" )
  protected ExtendedWebElement btnRemoveConnection;

  @FindBy( id = "removeConfirmationDialog_accept" )
  protected ExtendedWebElement btnConfirmationDialogOK;

  @FindBy( id = "successDialog_accept" )
  protected ExtendedWebElement btnSuccessDialogClose;

  // Data Source Created dialog
  // 'Data Source Created'
  @FindBy( xpath = "//div[text()='{L10N:summaryDialog.title}']" )
  protected ExtendedWebElement textDataSourceCreated;

  @FindBy( css = "#modelerDecision [type=radio]" )
  protected List<ExtendedWebElement> modelRadioButtons;

  @FindBy( css = "#modelerDecision .gwt-RadioButton label" )
  protected List<ExtendedWebElement> modelRadioButtonLabels;

  // Preview
  @FindBy( id = "previewLimit" )
  public ExtendedWebElement txtLimit;

  // 'Data Preview'
  @FindBy( xpath = "//span[text()='{L10N:datasourceDialog.PREVIEW}']" )
  protected ExtendedWebElement lnkDataPreview;

  @FindBy( id = "previewResultsDialog_accept" )
  public ExtendedWebElement btnClosePreview;

  @FindBy( id = "errorDialog_accept" )
  public ExtendedWebElement btnCloseError;

  @FindBy( id = "errorDetailsLabel" )
  public ExtendedWebElement errorDetailsLabel;

  @FindBy( id = "errorDetailsDialog_accept" )
  public ExtendedWebElement btnCloseDetailedError;

  public boolean preview( int limit ) {

    type( txtLimit, String.valueOf( limit ) );
    click( lnkDataPreview, true );

    boolean res = isElementPresent( btnClosePreview, EXPLICIT_TIMEOUT / 5 );
    if ( res ) {
      click( btnClosePreview );
    } else {
      click( btnCloseError );
    }

    return res;
  }
  
  public DataSourceWizardPage( WebDriver driver ) {
    super( driver );
    // [MG] Data Source Wizard moves upwards and the title is no longer visible even though the element is still
    // present. Actions did not work for me.
    if ( getDriver().toString().contains( "firefox" ) ) {
      ( (JavascriptExecutor) driver ).executeScript( "arguments[0].scrollIntoView(true);", textDataSourceWizard
          .getElement() );
    }
    if ( !isOpened( textDataSourceWizard ) ) {
      Assert.fail( "'Data Source Wizard' window is not recognized!" );
    }
  }

  public void buttonNext() {
    if ( isElementPresent( btnWizardNext ) && !isButtonEnabled( btnNextLabel ) ) {
      LOGGER.error( "Next button is not enabled!" );
    }
    click( btnWizardNext, true );
  }

  public void buttonFinish() {
    if ( isElementPresent( btnWizardFinish ) && !isButtonEnabled( btnFinishLabel ) ) {
      LOGGER.error( "Finish button is not enabled!" );
    }
    click( btnWizardFinish, true );

    if ( isElementPresent( btnCloseError, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.error( "Error message displayed!" );
      click( btnCloseError );
    }

    if ( isElementPresent( errorDetailsLabel, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.error( "Error message displayed: " + errorDetailsLabel.getText() );
      click( btnCloseDetailedError );
    }
  }

  public void buttonCancel() {
    click( btnCancelWizard );
  }

  public void buttonOK() {
    click( btnOK, true );
  }

  public void buttonClose() {
    click( btnClose, true );
  }

  public ExtendedWebElement getSelectedConnection() {
    return selectedConnectionItem;
  }

  public void setDataSourceName( String name ) {
    type( dataSourceName, name );
  }

  public DataSourceWizardPage setDataSourceType( String type ) {
    click( selectedDataSourceType );
    click( format( datasourceType, type ) );

    DataSourceWizardPage baseWizardPage = null;
    if ( type.equals( DataSourceWizardPage.SOURCE_TYPE_SQL_QUERY ) ) {
      baseWizardPage = new DataSourceSQLQueryPage( driver );
    } else if ( type.equals( DataSourceWizardPage.SOURCE_TYPE_CSV ) ) {
      baseWizardPage = new DataSourceCSVPage( driver );
    } else if ( type.equals( DataSourceWizardPage.SOURCE_TYPE_DB_TABLES ) ) {
      baseWizardPage = new DataSourceDBTablePage( driver );
    }

    return baseWizardPage;
  }

  protected String getSelectedDataSourceType() {
    return selectedDataSourceType.getText();
  }

  public void selectConnection( String connection ) {
    if ( connection == null ) {
      return;
    }

    if ( !isConnectionPresent( connection ) ) {
      Assert.fail( "TS042045: Data source '" + connection + "' doesn't exist, please create it before running test!" );
    }

    click( format( connectionItem, connection ) );
  }

  public boolean isDataSourceCreatedDialogAppears() {
    return isElementPresent( textDataSourceCreated, EXPLICIT_TIMEOUT );
  }

  public boolean isDataSourceCreatedDialogAppears( long timeout) {
    return isElementPresent( textDataSourceCreated, timeout );
  }

  public boolean verifyDatabaseSourceTypes() {
    boolean res = true;

    // click(selectDataSourceType);
    click( selectedDataSourceType );

    if ( !isElementPresent( format( datasourceType, SOURCE_TYPE_CSV ) ) ) {
      LOGGER.error( "Source Type '" + SOURCE_TYPE_CSV + "' doesn't exist in the drop down!" );
      res = false;
    }

    if ( !isElementPresent( format( datasourceType, SOURCE_TYPE_SQL_QUERY ) ) ) {
      LOGGER.error( "Source Type '" + SOURCE_TYPE_SQL_QUERY + "' doesn't exist in the drop down!" );
      res = false;
    }

    if ( !isElementPresent( format( datasourceType, SOURCE_TYPE_DB_TABLES ) ) ) {
      LOGGER.error( "Source Type '" + SOURCE_TYPE_DB_TABLES + "' doesn't exist in the drop down!" );
      res = false;
    }

    click( format( datasourceType, DEFAULT_SOURCE_TYPE ) );

    return res;
  }

  public void setCustomizeModel( Boolean customizeModel ) {

    if ( customizeModel == null ) {
      return;
    }

    if ( customizeModel ) {
      click( format( model, MODEL_RADIO_BUTTON_CUSTOMIZE ) );
    }
  }

  protected String getSelectedModelRadioButtonName() {
    String id = null;

    for ( int i = 0; i < modelRadioButtons.size(); i++ ) {
      if ( modelRadioButtons.get( i ).getElement().isSelected() ) {
        id = modelRadioButtons.get( i ).getElement().getAttribute( "id" );
      }
    }

    ExtendedWebElement labelItem = format( modelLabel, id );
    return labelItem.getText();
  }

  public boolean isModelRadioButtonDefaultSelected() {
    return ( getSelectedModelRadioButtonName().equals( MODEL_RADIO_BUTTON_DEFAULT ) );
  }

  public boolean isModelRadioButtonCustomizeSelected() {
    return ( getSelectedModelRadioButtonName().equals( MODEL_RADIO_BUTTON_CUSTOMIZE ) );
  }

  public boolean verifyModelRadioButtons() {
    boolean res = true;

    if ( !modelRadioButtonLabels.get( 0 ).getText().equals( MODEL_RADIO_BUTTON_DEFAULT ) ) {
      LOGGER.error( MODEL_RADIO_BUTTON_DEFAULT + " radio button doesn't exist in the dialog!" );
      res = false;
    }

    if ( !modelRadioButtonLabels.get( 1 ).getText().equals( MODEL_RADIO_BUTTON_CUSTOMIZE ) ) {
      LOGGER.error( MODEL_RADIO_BUTTON_CUSTOMIZE + " radio button doesn't exist in the dialog!" );
      res = false;
    }

    return res;
  }

  public boolean isConnectionPresent( String connection ) {
    return isElementPresent( format( connectionItem, connection ), EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isAddConnectionPresent() {
    return isElementPresent( btnAddConnection, EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isEditConnectionPresent() {
    return isElementPresent( btnEditConnection, EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isRemoveConnectionPresent() {
    return isElementPresent( btnRemoveConnection, EXPLICIT_TIMEOUT / 5 );
  }

  public DatabaseConnectionPage newConnection() {
    click( btnAddConnection );
    return new DatabaseConnectionPage( driver );
  }

  public DatabaseConnectionPage editConnection( String dsName ) {
    selectConnection( dsName );
    click( btnEditConnection );
    return new DatabaseConnectionPage( driver );
  }

  public void delete( String dsName ) {
    selectConnection( dsName );
    click( btnRemoveConnection );

    isWarningDialogPresent();

    click( btnConfirmationDialogOK );
    // obligatory pause as no spinner here

    if ( isElementPresent( dlgSuccess, EXPLICIT_TIMEOUT / 10 ) ) {
      click( btnSuccessDialogClose );
    } else {
      Assert.fail( "'Success' dialog is not opened for DataSource removal!" );
    }
  }

  public boolean isWarningDialogPresent() {
    boolean res = true;
    String concatenatedMessage = L10N.generateConcatForXPath( REMOVE_CONFIRMATION_DIALOG );

    if ( !isElementPresent( format( dlgRemoveDataSource, concatenatedMessage ), EXPLICIT_TIMEOUT / 10 ) ) {
      Assert.fail( "'Warning' dialog is not opened for DataSource removal!" );
      res = false;
    }

    return res;
  }
}
