package com.pentaho.qa.gui.web.datasource;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.connection.DatabaseConnectionPage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.puc.connection.BaseConnection;
import com.pentaho.services.puc.datasource.BaseDataSource;
import com.qaprosoft.carina.core.foundation.log.TestLogCollector;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.Screenshot;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ManageDataSourcesPage extends BasePage {
  // Data Source Types
  public enum DataSourceType {
    JDBC( L10N.getText( "DatasourceInfo.DisplayType.JDBC" ) ), ANALYSIS( L10N.getText(
        "DatasourceInfo.DisplayType.Analysis" ) ), METADATA( L10N.getText(
            "DatasourceInfo.DisplayType.Metadata" ) ), DATA_SOURCE_WIZARD( L10N.getText(
                "DatasourceInfo.DisplayType.Data_Source_Wizard" ) );

    private String name;

    private DataSourceType( String name ) {
      this.name = name;
    }

    public String getName() {
      return L10N.getText( this.name );
    }

  }

  // 'Manage Data Sources'
  @FindBy( xpath = "//div[@class='dialogTopCenterInner' and //div[text()='{L10N:datasourceAdminDialog.title}']]" )
  protected ExtendedWebElement dlgManageDataSources;

  @FindBy( xpath = "//div[@class='gwt-HTML' and text()='%s']" )
  protected ExtendedWebElement dataSourceItem;

  // 'Edit...'
  @FindBy( xpath = "//td[@class='gwt-MenuItem' and text()='{L10N:datasourceAdminDialog.EDIT}']" )
  public ExtendedWebElement btnEditDataSource;

  // 'New Connection...
  @FindBy( xpath = "//td[@class='gwt-MenuItem' and text()='{L10N:datasourceAdminDialog.NEW_CONNECTION}']" )
  public ExtendedWebElement btnNewConnection;

  // 'Export...'
  @FindBy( xpath = "//td[@class='gwt-MenuItem' and text()='{L10N:datasourceAdminDialog.EXPORT}']" )
  public ExtendedWebElement btnExportDataSource;

  // 'Delete'
  @FindBy( xpath = "//td[@class='gwt-MenuItem' and text()='{L10N:datasourceAdminDialog.REMOVE}']" )
  public ExtendedWebElement btnRemoveDataSource;

  // Import buttons for Analysis and Metadata
  private static final String IMPORT = L10N.getText( "datasourceAdminDialog.IMPORT" );
  @FindBy( xpath = "//td[@class='gwt-MenuItem' and text()='%s']" )
  protected ExtendedWebElement btnImport;

  /*
   * @FindBy(css = ".dataTable tr.selected div") public ExtendedWebElement selectedDataSource;
   */

  @FindBy( id = "datasourceAdminDialog_cancel" )
  public ExtendedWebElement closeManageDS;

  @FindBy( id = "newDataSourceButton" )
  public ExtendedWebElement btnNewDataSource;

  // Remove Data Source dialog

  // 'Remove Data Source'
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:removeDatasourceConfirmationDialog.title}']" )
  protected ExtendedWebElement dlgRemoveDataSource;

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:removeDatasourceConfirmationDialog.message}']" )
  protected ExtendedWebElement dlgRemoveDataSourceMessage;

  @FindBy( id = "removeDatasourceConfirmationDialog_accept" )
  protected ExtendedWebElement btnOKRemoveDataSource;

  @FindBy( id = "removeDatasourceConfirmationDialog_ cancel" )
  protected ExtendedWebElement btnCancelRemoveDataSource;

  @FindBy( xpath = "//div[@id='datasourceDropdownButton']/img" )
  protected ExtendedWebElement btnDataSoureDropdown;

  @FindBy( xpath = "//tr[td/div[text()='%s'] and td/div[text()='%s']]" )
  private ExtendedWebElement datasourceItemWithType;

  // Dialogs
  @FindBy( xpath = "//div[text()='{L10N:overwriteConnectionConfirmationDialog.title}']" )
  protected ExtendedWebElement dlgOverwriteConnection;

  @FindBy( xpath = "//div[text()='{L10N:overwrite.title}']" )
  protected ExtendedWebElement dlgOverwriteDataSource;

  @FindBy( id = "overwriteConnectionConfirmationDialogConnectionController_accept" )
  protected ExtendedWebElement btnOKOverwriteConnection;

  @FindBy( id = "overwriteConnectionConfirmationDialogConnectionController_cancel" )
  protected ExtendedWebElement btnCancelOverwriteConnection;

  @FindBy( id = "overwriteDialog_accept" )
  protected ExtendedWebElement btnOKOverwriteDataSource;

  @FindBy( id = "overwriteDialog_ cancel" )
  protected ExtendedWebElement btnCancelOverwriteDataSource;

  @FindBy(
      xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:datasourceAdminErrorDialog.CANNOT_EDIT_HEADER}']/../../../..//div[text()='{L10N:datasourceAdminErrorDialog.CANNOT_EDIT_TEXT}']" )
  protected ExtendedWebElement dlgCannotEditDataSource;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOK;

  public ManageDataSourcesPage( WebDriver driver ) {
    super( driver );
    // [VD] Don't incorporate default verification there as we should support different workflows for DataSource
    // creation and sometimes this window is not opened
  }

  public boolean isOpened( long timeout ) {
    return super.isOpened( dlgManageDataSources, timeout );
  }

  public boolean isDataSourcePresent( String dsName ) {
    return isDataSourcePresent( dsName, EXPLICIT_TIMEOUT );
  }

  public boolean isDataSourcePresent( String dsName, long timeout ) {
    return isElementPresent( format( dataSourceItem, dsName ), timeout );
  }

  public void selectDataSource( String dsName ) {
    click( format( dataSourceItem, dsName ) );
  }

  public void selectDataSource( String dsName, String type ) {
    click( format( datasourceItemWithType, dsName, type ) );
  }

  public boolean isEditDataSourceButtonEnabled() {
    return isButtonEnabled( btnEditDataSource );
  }

  public void clickEditButton() {
    if ( !isEditDataSourceButtonEnabled() ) {
      CustomAssert.fail( "51726", "Edit... button is not enabled!" );
    }
    click( btnEditDataSource );
  }

  public boolean isRemoveDataSourceButtonEnabled() {
    return isButtonEnabled( btnRemoveDataSource );
  }

  public void clickDeleteButton() {
    if ( !isRemoveDataSourceButtonEnabled() ) {
      LOGGER.error( "Delete button is not enabled!" );
    }
    click( btnRemoveDataSource );
  }

  public void clickExportButton() {
    if ( !btnExportDataSource.getElement().isEnabled() ) {
      LOGGER.error( "Export... button is not enabled!" );
    }
    click( btnExportDataSource );
  }

  public boolean isImportAnalysisButtonEnabled() {
    return isButtonEnabled( getImportAnalysisButton() );
  }

  public void clickImportAnalysisButton() {
    if ( !isImportAnalysisButtonEnabled() ) {
      LOGGER.error( "Import Analysis... button is not enabled!" );
    }
    click( getImportAnalysisButton() );
  }

  public void clickImportMetadataButton() {
    if ( !getImportMetadataButton().getElement().isEnabled() ) {
      LOGGER.error( "Import Metadata... button is not enabled!" );
    }
    click( getImportMetadataButton() );
  }

  public void clickNewConnectionButton() {
    if ( !btnNewConnection.getElement().isEnabled() ) {
      LOGGER.error( "New Connection... button is not enabled!" );
    }
    click( btnNewConnection );
  }

  public void openDataSourceContextMenu() {
    // opening context menu clicking on datasourceDropdownButton button
    click( btnDataSoureDropdown );
  }

  private boolean isButtonEnabled( ExtendedWebElement btn ) {
    return btn.getElement().isEnabled();
  }

  public DataSourceModelEditorPage editDataSource( BaseDataSource dataSource ) {
    selectDataSource( dataSource.getName() );
    openDataSourceContextMenu();
    clickEditButton();

    return new DataSourceModelEditorPage( driver );
  }

  public DatabaseConnectionPage editDataSource( BaseConnection conn ) {
    selectDataSource( conn.getName() );
    openDataSourceContextMenu();
    clickEditButton();

    return new DatabaseConnectionPage( driver );
  }

  public DatabaseConnectionPage newConnection() {
    click( btnDataSoureDropdown );
    click( btnNewConnection );
    return new DatabaseConnectionPage( driver );
  }

  public void removeDataSource( String dsName ) {
    selectDataSource( dsName );
    openDataSourceContextMenu();
    click( btnRemoveDataSource );
  }

  public void confirmRemove( boolean confirm ) {
    verifyRemoveDataSourceDialog();

    if ( confirm ) {
      click( btnOKRemoveDataSource );
    } else {
      click( btnCancelRemoveDataSource );
    }
    // obligatory pause as no spinner here
    pause( 2 );
  }

  public boolean verifyRemoveDataSourceDialog() {
    boolean res = true;
    // verify the text and the buttons on the dialog
    if ( !isElementPresent( dlgRemoveDataSource ) ) {
      CustomAssert.fail( "51728", "'Remove Data Source' dialog is not opened!" );
      LOGGER.error( "'Remove Data Source' dialog is not opened!" );
      res = false;
    }

    if ( !isElementPresent( dlgRemoveDataSourceMessage ) ) {
      CustomAssert.fail( "51728", "Remove Data Source message is incorrect!" );
      LOGGER.error( "Remove Data Source message is incorrect!" );
      res = false;
    }

    if ( !isElementPresent( btnOKRemoveDataSource ) ) {
      CustomAssert.fail( "51728", "Remove button is not present!" );
      LOGGER.error( "Remove button is not present!" );
      res = false;
    }

    if ( !isElementPresent( btnCancelRemoveDataSource ) ) {
      CustomAssert.fail( "51728", "Cancel button is not present!" );
      LOGGER.error( "Cancel button is not present!" );
      res = false;
    }

    return res;
  }

  public void closeManageDataSources() {
    TestLogCollector.addScreenshotComment( Screenshot.capture( getDriver(), true ),
        "Closing ManageDataSource dialog." );
    click( closeManageDS, true );
    pause( 2 );
    if ( isElementPresent( closeManageDS, EXPLICIT_TIMEOUT / 2 ) ) {
      LOGGER.warn( "'Manage Data Sources' is still opened! Trying to click again." );
      click( closeManageDS, true );
    }

    if ( !isElementNotPresent( dlgManageDataSources, IMPLICIT_TIMEOUT / 2 ) ) {
      LOGGER.error( "'Manage Data Sources' window was not closed!" );
    }
    makeClickable();
  }

  public boolean isDataSourcePresent( String dsName, String dsnType ) {
    return isElementPresent( format( datasourceItemWithType, dsName, dsnType ), EXPLICIT_TIMEOUT / 2 );
  }

  public DataSourceWizardPage newDataSource() {
    click( btnNewDataSource );
    return new DataSourceWizardPage( driver );
  }

  public void confirmConnectionOverwrite( boolean confirm ) {
    if ( !isOverwriteConnectionDialogPresent() ) {
      LOGGER.error( "Overwrite Connection dialog is not present!" );
    }

    if ( confirm ) {
      click( btnOKOverwriteConnection );
    } else {
      click( btnCancelOverwriteConnection );
    }
    // obligatory pause as no spinner here
    pause( 2 );
  }

  public void confirmDataSourceOverwrite( boolean confirm ) {
    if ( !isOverwriteDataSourceDialogPresent() ) {
      LOGGER.error( "Overwrite Data Source dialog is not present!" );
    }

    if ( confirm ) {
      click( btnOKOverwriteDataSource );
    } else {
      click( btnCancelOverwriteDataSource );
    }
    // obligatory pause as no spinner here
    pause( 2 );
  }

  public boolean isOverwriteConnectionDialogPresent() {
    return isElementPresent( dlgOverwriteConnection, EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isOverwriteDataSourceDialogPresent() {
    pause ( 1 );
    return isElementPresent( dlgOverwriteDataSource, EXPLICIT_TIMEOUT );
  }

  public ExtendedWebElement getOverwriteConnectionDialog() {
    return dlgOverwriteConnection;
  }

  public ExtendedWebElement getOverwriteDataSourceDialog() {
    return dlgOverwriteDataSource;
  }

  public boolean isCannotEditDialogPresent() {
    boolean res;

    if ( isElementNotPresent( dlgCannotEditDataSource ) ) {
      CustomAssert.fail( "65606", "The Cannot Edit dialog is not present!" );
      CustomAssert.fail( "65607", "The Cannot Edit dialog is not present!" );
      CustomAssert.fail( "65612", "The Cannot Edit dialog is not present!" );
      res = false;
    } else {
      CustomAssert.fail( "51725", "Cannot Edit dialog is present instead of Database Connection dialog!" );
      CustomAssert.fail( "65608", "Cannot Edit dialog is present instead of Import Analysis dialog!" );
      CustomAssert.fail( "65609", "Cannot Edit dialog is present instead of Database Connection dialog!" );
      CustomAssert.fail( "65610", "Cannot Edit dialog is present instead of Import Analysis dialog!" );
      CustomAssert.fail( "65611", "Cannot Edit dialog is present instead of Database Connection dialog!" );
      CustomAssert.fail( "65613", "Cannot Edit dialog is present instead of Import Analysis dialog!" );
      res = true;
    }

    return res;
  }

  public void confirmCannotEditDialog() {
    click( btnOK );
  }

  public void export( String dsName ) {
    click( format( dataSourceItem, dsName ) );
    openDataSourceContextMenu();
    clickExportButton();
    pause( 5 );
  }

  public void verify() {
    if ( isElementNotPresent( btnNewDataSource ) ) {
      LOGGER.error( "New Data Source button is missing!" );
      CustomAssert.fail( "51724", "New Data Source button is missing!" );
    }

    if ( isElementNotPresent( btnDataSoureDropdown ) ) {
      LOGGER.error( "Data Source drop-down button is missing!" );
      CustomAssert.fail( "51724", "Data Source drop-down button is missing!" );
    } else {
      // proceed to verify drop down only if drop-down button is present
      click( btnDataSoureDropdown );
    
      if ( isElementNotPresent( btnEditDataSource ) || isElementNotPresent( btnRemoveDataSource )
          || isElementNotPresent( btnExportDataSource ) || isElementNotPresent( getImportAnalysisButton() ) || isElementNotPresent(
              getImportMetadataButton() ) || isElementNotPresent( btnNewConnection ) ) {
        LOGGER.error( "At least one of the drop-down items is missing!" );
        CustomAssert.fail( "51724", "At least one of the drop-down items is missing!" );
      }
    }

    if ( isElementNotPresent( closeManageDS ) ) {
      LOGGER.error( "Close button is missing!" );
      CustomAssert.fail( "51724", "Close button is missing!" );
    }
  }
  
  private ExtendedWebElement getImportAnalysisButton(){
    String[] analysisParams = { DataSourceType.ANALYSIS.getName() };
    ExtendedWebElement importAnalysis =
        format( btnImport, L10N.formatString( IMPORT, analysisParams ) );
    return importAnalysis;
  }
  
  private ExtendedWebElement getImportMetadataButton(){
    String[] metadataParams = { DataSourceType.METADATA.getName() };
    ExtendedWebElement importMetada =
        format( btnImport, L10N.formatString( IMPORT, metadataParams ) );
    return importMetada;
  }
}
