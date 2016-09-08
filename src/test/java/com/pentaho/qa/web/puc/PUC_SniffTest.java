package com.pentaho.qa.web.puc;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.AdministrationPage;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.LoginPage;
import com.pentaho.qa.gui.web.puc.PRPTReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.pentaho.services.schedules.Schedule;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;

//https://spiratest.pentaho.com/6/TestCase/9008.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.PUC_SniffTest )
public class PUC_SniffTest extends WebBaseTest {
  private CSVDataSource csvDataSource;
  private DBTableDataSource dbDataSource;
  private SQLDataSource sqlDataSource;

  @BeforeClass
  public void prepareEnvironment() {
    HomePage homePage = webUser.login( getDriver() );
    AdministrationPage administrationPage = (AdministrationPage) homePage.activateModule( Module.ADMINISTRATION );

    administrationPage.setSMTPSettings( "smtp.gmail.com", "587", Configuration.get( Parameter.SENDER_EMAIL ),
        Configuration.get( Parameter.SENDER_PASSWORD ), Configuration.get( Parameter.SENDER_EMAIL ), "QA_Auto" );
    webUser.logout();
    homePage = webUser.login( getDriver() );

    Assert.assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName()
        + "'!" );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "41997" )
  public void testVerifyHomePerspective() {
    HomePage homePage = new HomePage( getDriver() );
    homePage.verifyHomePerspective();
  }

  @Test( dependsOnMethods = "testVerifyHomePerspective", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42016, 44592" )
  public void testMenuAbout() {
    HomePage homePage = new HomePage( getDriver() );
    homePage.verifyMenuAbout( appVersion );
  } 

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testMenuAbout", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "41998, 41999, 42000, 42176, 42174, 42175, 42001, 42177, 42005" )
  @CsvDataSourceParameters( dsUid = "name", executeColumn = "TestColumn", executeValue = "schedule_10",
      path = "CSV_data/PUC_Schedules/Schedule.csv" )
  public void testSchedule( Map<String, String> args ) {
    Schedule schedule = new Schedule( args );
    schedule.create();
    schedule.verify();
  }

  @Test( dependsOnMethods = "testSchedule", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42012, 42178" )
  @Parameters( { "createFolderName" } )
  public void testCreateNewFolder( String folderName ) {
    HomePage homePage = new HomePage( getDriver() );

    // Create new folder under User Home
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    browsePage.createFolderByPath( "", folderName );

  }

  @Test( dependsOnMethods = "testCreateNewFolder" )
  @SpiraTestSteps( testStepsId = "42013" )
  @Parameters( { "folderName", "createFolderName", "reportCountryPeformance" } )
  public void testCopyFolderItem( String sourceFolder, String targetFolder, String reportCountryPeformance ) {
    HomePage homePage = new HomePage( getDriver() );

    // Activate source folder which are going to be used for copying files
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );

    // Additional verification that folder tree is expanded to targetFolder
    if ( !browsePage.isFolderPresent( targetFolder ) ) {
      browsePage.activateFolder( targetFolder );
    }

    browsePage.activateFolder( sourceFolder );
    browsePage.copy( sourceFolder + "/" + reportCountryPeformance );
    browsePage.paste( reportCountryPeformance, targetFolder );

    // Verification part
    browsePage.isFilePresentByPath( targetFolder + "/" + reportCountryPeformance );
  }

  @Test( dependsOnMethods = "testCopyFolderItem" )
  @SpiraTestSteps( testStepsId = "42045" )
  @Parameters( { "analysisReportPath", "drillthroughValue" } )
  public void testSaveExistingReport( String reportPath, String fieldName ) {
    HomePage homePage = new HomePage( getDriver() );

    // Open report
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    reportPath = browsePage.getUserHome() + "/" + reportPath;
    String fileName = browsePage.openReport( reportPath );

    AnalyzerReportPage analysisReportPage = new AnalyzerReportPage( getDriver(), fileName );

    SoftAssert softAssert = new SoftAssert();

    analysisReportPage.switchToFrame();
    // Change parameter value
    if ( !analysisReportPage.chartDrillThrough( fieldName ) ) {
      softAssert.fail( "TS042045: Filter '" + fieldName + "' is not applied!" );
    }

    if ( !analysisReportPage.clickSave() ) {
      softAssert.fail( "TS042045: Save dialog is opened in case saving of existing file!" );
    }

    softAssert.assertAll();

    analysisReportPage.clickClose( fileName );
  }

  @Test( dependsOnMethods = "testSaveExistingReport" )
  @SpiraTestSteps( testStepsId = "42014" )
  @Parameters( { "createFolderName", "renameFolderName" } )
  public void testRenameFolder( String folderPath, String renameFolderName ) {
    HomePage homePage = new HomePage( getDriver() );

    // Activate source folder which are going to be used for copying files
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    browsePage.renameFolder( folderPath, renameFolderName );
  }

  @Test( dependsOnMethods = "testRenameFolder" )
  @SpiraTestSteps( testStepsId = "42014" )
  @Parameters( { "renameFolderName" } )
  public void testRemoveFolder( String renameFolderName ) {
    HomePage homePage = new HomePage( getDriver() );

    // Activate source folder which are going to be used for copying files
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );

    // Move to Trash
    if ( !browsePage.moveToTrash( renameFolderName ) ) {
      Assert.fail( "TS042014: Folder was not removed!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testRemoveFolder", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42017, 42018, 42019, 42020, 42021" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV01" )
  public void testNewCSVDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );

    if ( !csvDataSource.create() ) {
      Assert.fail( "TS042021: CSV datasource '" + csvDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testNewCSVDataSource" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV01_EDIT" )
  @SpiraTestSteps( testStepsId = "42039" )
  public void testEditCSVDataSource( Map<String, String> args ) {
    CSVDataSource editedDataSource = new CSVDataSource( args );
    csvDataSource.edit( editedDataSource );

    if ( !csvDataSource.verify() ) {
      Assert.fail( "TS042039: 'CSV File' data source was removed after editing!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testEditCSVDataSource", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42022, 42027, 42028, 42232, 42032, 42033, 42034, 42035, 42036, 42233, 42234" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT01" )
  public void testNewDBTablesDataSource( Map<String, String> args ) {
    dbDataSource = new DBTableDataSource( args );

    if ( !dbDataSource.create() ) {
      Assert.fail( "TS042234: DB Tables datasource '" + dbDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testNewDBTablesDataSource" )
  @SpiraTestSteps( testStepsId = "42037, 42235, 42038, 42041" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT01_EDIT" )
  public void testEditDBTablesDataSource( Map<String, String> args ) {
    DBTableDataSource editedDataSource = new DBTableDataSource( args );
    dbDataSource.edit( editedDataSource );

    if ( !dbDataSource.verify() ) {
      Assert.fail( "TS042041: 'DB Table' data source was removed after editing!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testEditDBTablesDataSource", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42040" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL01" )
  public void testNewSQLDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    if ( !sqlDataSource.create() ) {
      Assert.fail( "TS042040: SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testNewSQLDataSource" )
  @SpiraTestSteps( testStepsId = "42040" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL01_EDIT" )
  public void testEditSQLDataSource( Map<String, String> args ) {
    SQLDataSource editedDataSource = new SQLDataSource( args );
    sqlDataSource.edit( editedDataSource );

    if ( !sqlDataSource.verify() ) {
      Assert.fail( "TS042040: 'SQL Query' data source was removed after editing!" );
    }
  }

  @Test( dependsOnMethods = "testEditSQLDataSource" )
  @SpiraTestSteps( testStepsId = "42042" )
  public void testRemoveDataSource() {
    sqlDataSource.delete();

    if ( sqlDataSource.verify( EXPLICIT_TIMEOUT / 10 ) ) {
      Assert.fail( "TS042042: Data Source '" + sqlDataSource.getName() + "' was not removed!" );
    }
  }

  @Test( description = "JIRA# BACKLOG-3538", dependsOnMethods = "testRemoveDataSource", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "44649" )
  @Parameters( { "folderName" } )
  public void testTabBarForManyReports( String folderPath ) {
    openReports( folderPath, false );
  }

  @Test( dependsOnMethods = "testTabBarForManyReports", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "42046, 42244" )
  @Parameters( { "user", "password" } )
  public void testRelogin( String user, String password ) {
    LoginPage loginPage = webUser.logout();
    Assert.assertTrue( loginPage.isOpened( EXPLICIT_TIMEOUT ), "TS042046: LoginPage is not displayed after logout!" );

    HomePage homePage = webUser.login();
    Assert.assertTrue( homePage.isLogged( user ), "Incorrect user is logged: '" + user + "'!" );
  }

  @Test( dependsOnMethods = "testRelogin" )
  @SpiraTestSteps( testStepsId = "42050" )
  @Parameters( { "prptReportPath", "parameterName", "parameterValue" } )
  public void testGlassPane( String reportPath, String parameterName, String parameterValue ) {
    HomePage homePage = new HomePage( getDriver() );

    // Open report
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    String fileName = browsePage.openReport( reportPath );

    PRPTReportPage prptReportPage = new PRPTReportPage( driver, fileName );

    SoftAssert softAssert = new SoftAssert();
    // Change parameter value
    if ( !prptReportPage.changeParameter( parameterName, parameterValue ) ) {
      softAssert.fail( "TS042050: Blocking glass pane element is not working as expected! See above logs!" );
    }

    // Check report is displayed correctly
    if ( !prptReportPage.isReportDisplayedCorrectly() ) {
      softAssert.fail( "TS042050: The report renders bad!" );
    }

    prptReportPage.clickClose( fileName );
    softAssert.assertAll();
  }

  /*
   * //"BI Developer Examples/Chart"
   * 
   * @Test(dependsOnMethods="testRemoveDataSource", alwaysRun=true)
   * 
   * @SpiraTestSteps(testStepsId="41573")
   * 
   * @Parameters({ "folderCharts" }) public void testBIDeveloperChartReports(String folderPath) {
   * openReports(folderPath, true); }
   * 
   * //"BI Developer Examples/Reporting"
   * 
   * @Test(dependsOnMethods="testBIDeveloperChartReports", alwaysRun=true)
   * 
   * @SpiraTestSteps(testStepsId="41579")
   * 
   * @Parameters({ "folderReporting" }) public void testBIDeveloperReportingReports(String folderPath) {
   * openReports(folderPath, true); }
   * 
   * //BI Developer Examples/Steel Wheels (Legacy)
   * 
   * @Test(dependsOnMethods="testBIDeveloperReportingReports", alwaysRun=true)
   * 
   * @SpiraTestSteps(testStepsId="41579")
   * 
   * @Parameters({ "folderSteelWheelsLegacy" }) public void testSteelWheelsLegacyReports(String folderPath) {
   * openReports(folderPath, true); }
   * 
   * //BI Developer Examples/Steel Wheels (Legacy)
   * 
   * @Test(dependsOnMethods="testSteelWheelsLegacyReports", alwaysRun=true)
   * 
   * @SpiraTestSteps(testStepsId="41575")
   * 
   * @Parameters({ "folderSteelWheels" }) public void testSteelWheelsReports(String folderPath) {
   * openReports(folderPath, true); }
   */
  protected void openReports( String folderPath, boolean closeReport ) {
    HomePage homePage = new HomePage( getDriver() );

    // Open report
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    browsePage.activateFolder( folderPath );

    List<String> filenames = browsePage.getListFileNames();

    // Added to exclude from reading the following report:CTools Dashboard
    int i = 0;
    for ( String fileName : filenames ) {
      if ( fileName.contains( "CTools" ) ) {
        continue;
      }
      browsePage.openReport( folderPath + "/" + fileName );
      if ( closeReport ) {
        PRPTReportPage prptReportPage = new PRPTReportPage( driver, fileName );
        prptReportPage.clickClose( fileName );
      }
      pause( 5 );
      homePage.activateModule( Module.BROWSE_FILES );
      if ( ++i > 9 ) {

        // open only first 9 reports. It is enough to hame multi lines on the tab
        break;
      }
    }
  }

  // @Test()
  // @SpiraTestSteps(testStepsId="42179")
  // @Parameters({ "folderPath"})
  public void testDownload( String folderPath ) {
    HomePage homePage = new HomePage( getDriver() );

    // Open report
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    browsePage.activateFolder( folderPath );
  }

}
