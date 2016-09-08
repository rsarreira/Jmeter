package com.pentaho.qa.web.puc.dat;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.datasource.DataSourceCSVPage;
import com.pentaho.qa.gui.web.datasource.DataSourceDBTablePage;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.BaseDataSource.Workflow;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8946.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.Negative_Test )
public class Negative_Tests extends WebBaseTest {

  private ManageDataSourcesPage manageDSPage;
  private JdbcConnection connection;
  private CSVDataSource csvDataSource;
  private DataSourceCSVPage dsCsvPage;
  private DBTableDataSource dbTableDataSource;
  private DataSourceDBTablePage dsTablesPage;
  private String specialCharacters = "(!@#$^(}{) - = +";
  private String reservedWord = "Select";
  private static String SALES = "SALES";
  private static String COUNTRY = "COUNTRY";
  private static String ORDERNUMBER = "ORDERNUMBER";

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40922" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSCSV02" )
  public
    void createCsvDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );

    if ( !csvDataSource.create() ) {
      Assert.fail( "TS040922: CSV data source '" + csvDataSource.getName() + "' was not created successfully!" );
    }

    csvDataSource.openWizard( Workflow.EDIT );
    dsCsvPage = new DataSourceCSVPage( getDriver() );

    // verification that the Data Source Wizard is shown happens on the page.
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createCsvDataSource", description = "JIRA# BISERVER-13142" )
  @SpiraTestSteps( testStepsId = "40926, 59717" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV02_EDIT" )
  public void editCsvDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );

    dsCsvPage.setStagingSettings( csvDataSource.getFilePath() );

    dsCsvPage.buttonNext();

    // verify that the new column is present in the Staging Settings page.
    String presentItem = args.get( "VerifyPresent" );
    if ( !dsCsvPage.isColumnPresent( presentItem ) ) {
      Assert.fail( "TS040926: columm '" + presentItem + "' is not present!" );
    }

    // change a column name with a reserved word such as SELECT, BETWEEN, etc
    dsCsvPage.editColumnName( SALES, reservedWord );

    // change one column name to use (!@#$^&(}{) _ - = + \\
    dsCsvPage.editColumnName( COUNTRY, specialCharacters );

    // TODO: temporary work-around, need to click away in order to validate
    dsCsvPage.selectColumn( ORDERNUMBER );

    // verify that the column names were changed
    if ( !dsCsvPage.isColumnPresent( reservedWord ) || !dsCsvPage.isColumnPresent( specialCharacters ) ) {
      Assert.fail( "TS059717: columms are not present!" );
    }

    // finish the wizard
    dsCsvPage.buttonFinish();

    DataSourceModelEditorPage dataSourceModelEditorPage = new DataSourceModelEditorPage( getDriver() );
    // auto populate analysis tab
    dataSourceModelEditorPage.autoPopulateModel();
    dataSourceModelEditorPage.switchToReportingTab();
    // autopopulate reporting tab
    dataSourceModelEditorPage.autoPopulateModel();
    dataSourceModelEditorPage.buttonOK();

    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( driver );
    manageDataSourcesPage.closeManageDataSources();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "editCsvDataSource", description = "JIRA# BISERVER-13043", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40927" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PIR06" )
  public void createInteractiveReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ));
    // create the report with the arguments from the xls file
    PIRReport pirReport = new PIRReport( args );

    LOGGER.info( pirReport.getName() );

    PIRReportPage interactiveReportPage = pirReport.create( true );

    interactiveReportPage.switchToFrame();
    // Add columns
    pirReport.addColumns();

    // Add groups
    pirReport.addGroups();

    // verify that the report now renders properly.
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = interactiveReportPage.verifyContent( presentItem, notPresentItem );

    pirReport.save( folder );
    // Verification part
    if ( !interactiveReportPage.isSaved( pirReport.getName() ) ) {
      Assert.fail( "TS040927: Interactive report '" + pirReport.getName() + "' wasn't saved!" );
    }

    pirReport.close();

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createInteractiveReport", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40928" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID", executeValue = "JDBC06" )
  public
    void createConnection( Map<String, String> args ) {
    manageDSPage = new ManageDataSourcesPage( getDriver() );
    connection = new JdbcConnection( args );
    LOGGER.info( connection.getName() );

    if ( !connection.create() ) {
      Assert.fail( "TS040928: DB Connection'" + connection.getName() + "' was not created successfully!" );
    }

  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createConnection" )
  @SpiraTestSteps( testStepsId = "40928, 41123, 41128" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC06_EDIT" )
  public void overwriteConnection( Map<String, String> args ) {
    connection = new JdbcConnection( args );

    // create a connection with the same name as previous connection
    connection.openWizard();
    connection.setGeneralParameters();
    connection.setParameters();
    connection.finishWizard();

    if ( !manageDSPage.isOverwriteConnectionDialogPresent() ) {
      Assert.fail( "TS040928: " + manageDSPage.getOverwriteConnectionDialog().getText() + " dialog is not present!" );
    }

    // click cancel
    manageDSPage.confirmConnectionOverwrite( false );
    manageDSPage.editDataSource( connection );

    // verify that the connection was NOT overwritten
    if ( !connection.verifyGeneral() ) {
      Assert.fail( "TS041123: Connection" + connection.getName() + " was overwritten!" );
    }

    // create a new connection with the same name as previous connection
    connection.openWizard();
    connection.setGeneralParameters();
    connection.setParameters();
    connection.finishWizard();

    // click OK
    manageDSPage.confirmConnectionOverwrite( true );
    manageDSPage.editDataSource( connection );

    // verify that the connection was overwritten
    if ( connection.verifyGeneral() ) {
      Assert.fail( "TS041128: Connection" + connection.getName() + " was not overwritten!" );
    }

  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "overwriteConnection", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "41125" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT08" )
  public void createDBTablesDataSource( Map<String, String> args ) {
    manageDSPage = new ManageDataSourcesPage( getDriver() );
    dbTableDataSource = new DBTableDataSource( args );

    if ( !dbTableDataSource.create() ) {
      Assert
          .fail( "TS041125: DB Table data source '" + dbTableDataSource.getName() + "' was not created successfully!" );
    }

  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createDBTablesDataSource" )
  @SpiraTestSteps( testStepsId = "41125, 41126, 60307, 41124" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT08_EDIT" )
  public void overwriteDBTablesDataSource( Map<String, String> args ) {
    manageDSPage = new ManageDataSourcesPage( getDriver() );
    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();
    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();

    List<String> tables = dbTableDataSource.getTables();
    dsTablesPage.selectTables( tables, true );

    if ( dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      dsTablesPage.buttonNext();
    }

    List<String> joins = dbTableDataSource.getJoins();
    dsTablesPage.createJoins( joins );

    if ( dsTablesPage.isButtonEnabled( BasePage.btnFinishLabel ) ) {
      dsTablesPage.buttonFinish();
    } else {
      LOGGER.error( "Finish button is disabled because not all tables haven been joined." );
    }

    if ( !manageDSPage.isOverwriteDataSourceDialogPresent() ) {
      Assert.fail( "TS041125: " + manageDSPage.getOverwriteDataSourceDialog().getText() + " dialog is not present!" );
    }

    // click cancel
    manageDSPage.confirmDataSourceOverwrite( false );

    dbTableDataSource.cancelWizard();
    dsTablesPage.makeClickable();

    // verify that the datasource was NOT overwritten
    if ( !dbTableDataSource.verifyDataSource() ) {
      Assert.fail( "TS041126: Datasource" + dbTableDataSource.getName() + " was overwritten!" );
    }

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dsTablesPage.makeClickable();
    dbTableDataSource.selectType();
    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();
    dsTablesPage.selectTables( tables, true );

    if ( dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      dsTablesPage.buttonNext();
    }

    dsTablesPage.createJoins( joins );

    if ( dsTablesPage.isButtonEnabled( BasePage.btnFinishLabel ) ) {
      dsTablesPage.buttonFinish();
    }

    if ( !manageDSPage.isOverwriteDataSourceDialogPresent() ) {
      Assert.fail( "TS060307: " + manageDSPage.getOverwriteDataSourceDialog().getText() + " dialog is not present!" );
    }

    manageDSPage.confirmDataSourceOverwrite( true );
    dbTableDataSource.getDsWizardPage().buttonOK();

    // verify that the datasource was overwritten
    if ( dbTableDataSource.verifyDataSource() ) {
      Assert.fail( "TS041124: Datasource" + dbTableDataSource.getName() + " was not overwritten!" );
    }
  }
}