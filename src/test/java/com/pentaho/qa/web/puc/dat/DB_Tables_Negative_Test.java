package com.pentaho.qa.web.puc.dat;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.datasource.DataSourceDBTablePage;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.BaseDataSource.Workflow;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8157.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.DB_Tables_Negative_Test )
public class DB_Tables_Negative_Test extends WebBaseTest {

  private DBTableDataSource dbTableDataSource;
  private DataSourceDBTablePage dsTablesPage;
  private PIRReportPage interactiveReportPage;
  private AnalyzerReportPage analyzerReportPage;
  private PAReport analyzerReport;
  private PIRReport pirReport;
  private JdbcConnection connection;
  private SoftAssert softAssert;
  private String presentItem;
  private String notPresentItem;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "32815" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC01" )
  public void createFoodmartConnection( Map<String, String> args ) {
    connection = new JdbcConnection( args );
    LOGGER.info( connection.getName() );

    if ( !connection.create() ) {
      Assert.fail( "TS032815: DB Connection'" + connection.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "32815" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT05" )
  public void createDBTablesDataSourceWithInvalidJoin( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();
    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();

    dsTablesPage.selectTables( dbTableDataSource.getTables(), true );

    if ( dsTablesPage.isFactTableAvailable() ) {
      dsTablesPage.selectFactTable( dbTableDataSource.getFactTable() );
    }

    if ( dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      dsTablesPage.buttonNext();
    }

    List<String> joins = dbTableDataSource.getJoins();
    dsTablesPage.createJoins( joins );
    // Click on Create Join again
    dsTablesPage.createJoins( joins );

    // Check that the Duplicate Joins Error dialog popped up.
    if ( !dsTablesPage.isDuplicateJoinsErrorDialogPresent() ) {
      Assert.fail( "TS032815: Duplicate Joins Error dialog is not present!" );
    }

    dsTablesPage.closeJoinsErrorDialog();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40885" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT" )
  public void tryToAddAnotherJoin( Map<String, String> args ) {
    DataSourceWizardPage dsWizardPage = dbTableDataSource.getDsWizardPage();
    dbTableDataSource = new DBTableDataSource( args );

    dsTablesPage.createJoins( dbTableDataSource.getJoins() );

    // Check that the Duplicate Joins Error dialog popped up.
    if ( !dsTablesPage.isDuplicateJoinsErrorDialogPresent() ) {
      Assert.fail( "TS040885: Duplicate Joins Error dialog is not present!" );
    }

    dsTablesPage.closeJoinsErrorDialog();
    // set DSWizard Page since it is null after getting the new arguments
    dbTableDataSource.setDsWizardPage( dsWizardPage );
    dbTableDataSource.finishWizard();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40882" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "40882" )
  public void createAnalyzerReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    // create the report with the arguments from the xls file
    analyzerReport = new PAReport( args );

    LOGGER.info( analyzerReport.getName() );
    analyzerReportPage = analyzerReport.create();

    analyzerReport.save( folder );

    // Verification part
    if ( !analyzerReportPage.isSaved( analyzerReport.getName() ) ) {
      Assert.fail( "TS040882: Analyzer report '" + analyzerReport.getName() + "' wasn't saved!" );
    }

    analyzerReport.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40882" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PIR05" )
  public void createInteractiveReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    // create the report with the arguments from the xls file
    pirReport = new PIRReport( args );

    LOGGER.info( pirReport.getName() );

    interactiveReportPage = pirReport.create( true );

    // Add columns
    pirReport.addColumns();

    // check if error dialog is present
    if ( interactiveReportPage.isErrorDialogPresent() ) {
      // dismiss error dialog
      interactiveReportPage.closeErrorDialog();
    }

    pirReport.save( folder );
    // Verification part
    if ( !interactiveReportPage.isSaved( pirReport.getName() ) ) {
      Assert.fail( "TS040882: Interactive report '" + pirReport.getName() + "' wasn't saved!" );
    }

    pirReport.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "58254" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "Title", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT2" )
  public void fixDBTableDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard( Workflow.EDIT );
    dsTablesPage = new DataSourceDBTablePage( getDriver() );
    dsTablesPage.selectConnection( dbTableDataSource.getConnection() );

    dsTablesPage.buttonNext();
    dsTablesPage.buttonNext();
    // remove invalid join
    dsTablesPage.deleteAllJoins();
    // create valid join
    dsTablesPage.createJoins( dbTableDataSource.getJoins() );
    // finish the wizard and close the manage data sources window
    dbTableDataSource.finishWizard( Workflow.EDIT );
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( driver );
    manageDataSourcesPage.closeManageDataSources();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "58254" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "40882" )
  public void checkAnalyzerReport( Map<String, String> args ) {
    // verify that the report now renders properly.
    analyzerReport.open();
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    analyzerReportPage = new AnalyzerReportPage( getDriver() );
    analyzerReportPage.switchToDefault();
    softAssert = analyzerReportPage.verifyContent( presentItem, notPresentItem );

    analyzerReport.close();

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "58254" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PIR05" )
  public void checkInteractiveReport( Map<String, String> args ) {
    // Open report and set it to edit mode
    pirReport.open();
    pirReport.activateEditMode();
    pirReport.getReportPage().switchToFrame();
    // Add column that failed to be added before.
    List<String> columns = pirReport.getColumns();
    pirReport.addColumn( columns.get( 1 ) );

    // verify that the report now renders properly.
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    softAssert = interactiveReportPage.verifyContent( presentItem, notPresentItem );

    pirReport.save();

    pirReport.close();

    softAssert.assertAll();

  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40888" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "Title", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT3" )
  public void changeDBTablesDataSourceConnection( Map<String, String> args ) {
    // DataSourceWizardPage dsWizardPage = dbTableDataSource.getDsWizardPage();
    dbTableDataSource = new DBTableDataSource( args );
    List<String> tables = dbTableDataSource.getTables();
    List<String> joins = dbTableDataSource.getJoins();
    dbTableDataSource.openWizard( Workflow.EDIT );

    dsTablesPage = new DataSourceDBTablePage( getDriver() );
    dsTablesPage.selectConnection( connection.getName() );
    dsTablesPage.buttonNext();
    dsTablesPage.selectTables( tables, true );
    dsTablesPage.selectFactTable( dbTableDataSource.getFactTable() );
    dsTablesPage.buttonNext();

    // remove invalid join
    dsTablesPage.deleteAllJoins();
    // create valid join
    dsTablesPage.createJoins( joins );
    // check that Finish button is disabled
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      Assert.fail( "TS040888: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40889" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "Title", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT4" )
  public void addJoinAndCheckFinishButton( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    List<String> joins = dbTableDataSource.getJoins();

    // create valid join
    dsTablesPage.createJoins( joins );
    // check that Finish button is enabled
    if ( dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      Assert.fail( "TS040889: Button '" + BasePage.btnFinishLabel + "' is disabled!" );
    }

    LOGGER.info( "The finish button is enabled as expected." );

  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40890" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "Title", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT5" )
  public void removeJoinAndCheckFinishButton( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    List<String> join = dbTableDataSource.getJoins();

    // delete the join that was just created
    dsTablesPage.deleteJoin( join.get( 0 ) );

    // check that Finish button is disabled
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      Assert.fail( "TS040890: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    LOGGER.info( "The finish button is disabled as expected." );

  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# BISERVER-13142" )
  @SpiraTestSteps( testStepsId = "40883" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "Title", executeColumn = "TUID",
      executeValue = "DSDBT05_EDIT5" )
  public void addJoinAndSaveDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    List<String> joins = dbTableDataSource.getJoins();

    // add the join one more time
    dsTablesPage.createJoins( joins );
    // finish the wizard and close the manage data sources window
    dsTablesPage.buttonFinish();
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

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40883" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "40883" )
  public void checkAnalyzerReportAvailableFields( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PAReport ar = new PAReport( args );

    analyzerReport.open();

    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    analyzerReportPage = new AnalyzerReportPage( getDriver() );
    analyzerReportPage.addFields( ar.getRows(), ar.getColumns(), ar.getMeasures() );

    analyzerReportPage.switchToDefault();

    softAssert = analyzerReportPage.verifyContent( presentItem, notPresentItem );

    analyzerReport.save( folder );
    analyzerReport.close();

    softAssert.assertAll();

  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# PIR-1162" )
  @SpiraTestSteps( testStepsId = "40883" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "40883" )
  public void checkInteractiveReportAvailableFields( Map<String, String> args ) {
    pirReport.open();

    if ( pirReport.getReportPage().isErrorDialogPresent() ) {
      pirReport.getReportPage().closeErrorDialog();
    }

    pirReport.activateEditMode();

    if ( pirReport.getReportPage().isErrorDialogPresent() ) {
      pirReport.getReportPage().closeErrorDialog();
    }

    // TODO: Verify that the report available fields is updated with the new datasource settings.
    // This will fail due to PIR-1162
    pirReport.close();

  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "32817" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT06" )
  public void createDBTablesDataSourceWithSelfJoin( Map<String, String> args ) {
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

    // Check that the Self Join Detected dialog popped up.
    if ( !dsTablesPage.isSelfJoinsErrorDialogPresent() ) {
      Assert.fail( "TS032817: Self Join Error dialog is not present!" );
    }

    dsTablesPage.closeJoinsErrorDialog();
    dsTablesPage.buttonCancel();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40896" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT07" )
  public void createDBTableDataSourceWithOneTable( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();
    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();
    dsTablesPage.selectTables( dbTableDataSource.getTables() );

    if ( dsTablesPage.isFactTableAvailable() ) {
      dsTablesPage.selectFactTable( "ORDERS" );
    }

    if ( dsTablesPage.isButtonEnabled( BasePage.btnFinishLabel ) ) {
      dbTableDataSource.finishWizard();
    }

  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "40896" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT07_EDIT" )
  public void addTableAndCheckForDuplicateFields( Map<String, String> args ) {
    // Get the new arguments
    dbTableDataSource = new DBTableDataSource( args );
    dbTableDataSource.openWizard( Workflow.EDIT );

    dsTablesPage.selectConnection( dbTableDataSource.getConnection() );
    dsTablesPage.buttonNext();
    dsTablesPage.selectTables( dbTableDataSource.getTables() );
    dsTablesPage.buttonNext();

    if ( dsTablesPage.checkLeftKeyFieldsForDuplicates() ) {
      Assert.fail( "TS040896: There are duplicate fields in the list." );
    }

    dbTableDataSource.cancelWizard();

  }

}
