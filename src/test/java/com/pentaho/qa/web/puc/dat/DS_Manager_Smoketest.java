package com.pentaho.qa.web.puc.dat;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.datasource.DataSourceImportAnalysisPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage.DataSourceType;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.connection.DatabaseConnectionPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.connection.BaseConnection.Workflow;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.AnalysisDataSource;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.puc.datasource.MetadataDataSource;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/6/TestCase/10375.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.DS_Manager_Smoketest )
public class DS_Manager_Smoketest extends WebBaseTest {

  private DatabaseConnectionPage connPage;
  private DBTableDataSource dbTableDataSource;
  private DataSourceImportAnalysisPage importAnalysisPage;
  private AnalysisDataSource analysisDS;
  private MetadataDataSource metadataDS;

  @Test( )
  public void login() {
    webUser.login();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "51724" )
  public void verifyManageDataSources() {
    HomePage homePage = new HomePage( getDriver() );
    // CustomAssert verification for TS051724 happens when manageDSPage is initialized
    ManageDataSourcesPage manageDSPage = homePage.openManageDataSources();
    manageDSPage.verify();
    manageDSPage.closeManageDataSources();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "51725" )
  public void editAuditJDBC() {
    HomePage homePage = new HomePage( getDriver() );
    ManageDataSourcesPage manageDSPage = homePage.openManageDataSources();
    manageDSPage.selectDataSource( "Audit" );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      connPage = new DatabaseConnectionPage( getDriver() );
      connPage.buttonCancel();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65606" )
  public void editBAPom() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );

    manageDSPage.selectDataSource( "ba-pom" );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( manageDSPage.isCannotEditDialogPresent() ) {
      manageDSPage.confirmCannotEditDialog();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65607" )
  public void editPDIOpsMartSampleReports() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "PDI Operations Mart Sample Reports/metadata.xmi" );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( manageDSPage.isCannotEditDialogPresent() ) {
      manageDSPage.confirmCannotEditDialog();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65608" )
  public void editPentahoOperationsMartAnalysis() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "pentaho_operations_mart", DataSourceType.ANALYSIS.getName() );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      importAnalysisPage = new DataSourceImportAnalysisPage( getDriver() );
      importAnalysisPage.clickClose();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65609" )
  public void editPentahoOperationsMartJDBC() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "pentaho_operations_mart", DataSourceType.JDBC.getName() );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      connPage = new DatabaseConnectionPage( getDriver() );
      connPage.buttonCancel();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65610" )
  public void editSampleDataAnalysis() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "SampleData", DataSourceType.ANALYSIS.getName() );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      importAnalysisPage = new DataSourceImportAnalysisPage( getDriver() );
      importAnalysisPage.clickClose();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65611" )
  public void editSampleDataJDBC() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "SampleData", DataSourceType.JDBC.getName() );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      connPage = new DatabaseConnectionPage( getDriver() );
      connPage.buttonCancel();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65612" )
  public void editSteelWheelsMetadata() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "steel-wheels" );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( manageDSPage.isCannotEditDialogPresent() ) {
      manageDSPage.confirmCannotEditDialog();
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "65613" )
  public void editSteelWheelsAnalysis() {
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.selectDataSource( "SteelWheels" );
    manageDSPage.openDataSourceContextMenu();
    manageDSPage.clickEditButton();
    if ( !manageDSPage.isCannotEditDialogPresent() ) {
      importAnalysisPage = new DataSourceImportAnalysisPage( getDriver() );
      importAnalysisPage.clickClose();
    }

    manageDSPage.closeManageDataSources();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT05" )
  @SpiraTestSteps( testStepsId = "51726, 51727, 65688, 65689, 51754" )
  public void createDBTablesDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    dbTableDataSource.create();

    if ( dbTableDataSource.verifyDataSource() ) {
      Assert.fail( dbTableDataSource.getName() + " does not have all the specified data!" );
    }

    // TS051754
    // Export data source, unzip it and check its contents.
    String zipFile = dbTableDataSource.exportDS();
    Utils.unzip( zipFile );
    String mondrianFile = dbTableDataSource.getName() + ".mondrian.xml";
    String metadataFile = dbTableDataSource.getName() + ".xmi";
    // verify unzipped files
    if ( !Utils.checkDownloadsFile( mondrianFile ) ) {
      Assert.fail( mondrianFile + " is not present!" );
    }
    if ( !Utils.checkDownloadsFile( metadataFile ) ) {
      Assert.fail( metadataFile + " is not present!" );
    }
    // clean up
    Utils.clearDownloadsFile( mondrianFile );
    Utils.clearDownloadsFile( metadataFile );
    Utils.clearDownloadsFile( zipFile );
    // manageDSPage = new ManageDataSourcesPage( getDriver() );
    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.closeManageDataSources();

    // TS051728 and TS065688
    dbTableDataSource.delete( false );
    if ( !dbTableDataSource.verify() ) {
      Assert.fail( dbTableDataSource.getName() + " was deleted!" );
    }
    // TS065689
    dbTableDataSource.delete();
    if ( dbTableDataSource.verify() ) {
      Assert.fail( "Data Source: " + dbTableDataSource.getName() + " was not deleted!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "Analysis_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSA01" )
  @SpiraTestSteps( testStepsId = "51732, 51733, 51734, 51753" )
  public void importAndExportAnalysis( Map<String, String> args ) {
    analysisDS = new AnalysisDataSource( args );
    analysisDS.open();
    // TS051732
    analysisDS.verifyDialog();
    // TS051733
    analysisDS.setParameters();
    analysisDS.getImportPage().clickClose();
    if ( analysisDS.verify() ) {
      Assert.fail( "Analysis: " + analysisDS.getName() + " was created!" );
    }
    // TS051734
    analysisDS.create();
    analysisDS.export();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PAR12" )
  @SpiraTestSteps( testStepsId = "51735" )
  public void createAnalysisReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PAReport analysisReport = new PAReport( args );
    analysisReport.create();

    SoftAssert softAssert;
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    analysisReport.getReportPage().switchToDefault();
    softAssert = analysisReport.getReportPage().verifyContent( presentItem, notPresentItem );

    analysisReport.save( folder );
    analysisReport.close();
    // Clean up as there is no way to specify unique Analysis name
    analysisDS.delete();
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "Metadata_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSMD01" )
  @SpiraTestSteps( testStepsId = "51747, 51748, 51749, 51752" )
  public void importAndExportMetadata( Map<String, String> args ) {
    metadataDS = new MetadataDataSource( args );
    metadataDS.open();
    // TS051747
    metadataDS.verifyDialog();
    // TS051748
    metadataDS.setParameters();
    metadataDS.getImportPage().clickClose();
    if ( metadataDS.verify() ) {
      Assert.fail( "Metadata: " + metadataDS.getName() + " was created!" );
    }
    // TS051749
    metadataDS.create();
    metadataDS.export();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PIR08" )
  @SpiraTestSteps( testStepsId = "51750" )
  public void createInteractiveReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PIRReport pirReport = new PIRReport( args );
    pirReport.create();

    SoftAssert softAssert;
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    pirReport.getReportPage().switchToDefault();
    softAssert = pirReport.getReportPage().verifyContent( presentItem, notPresentItem );

    pirReport.save( folder );
    pirReport.close();
    // Clean up
    // metadataDS.delete();
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC07" )
  @SpiraTestSteps( testStepsId = "51751" )
  public void createJDBCConnetion( Map<String, String> args ) {
    JdbcConnection connection = new JdbcConnection( args );
    connection.openWizard( Workflow.WIZARD_SQL_QUERY );
    connection.setGeneralParameters();
    connection.setParameters();
    connection.finishWizard( Workflow.WIZARD_SQL_QUERY );
    connection.delete( Workflow.WIZARD_SQL_QUERY );
  }
}
