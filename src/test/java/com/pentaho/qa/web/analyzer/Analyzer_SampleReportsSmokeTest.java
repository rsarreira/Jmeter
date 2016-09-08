package com.pentaho.qa.web.analyzer;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage.DataSourceType;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/8/TestCase/8980.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_SampleReportsSmokeTest )
public class Analyzer_SampleReportsSmokeTest extends WebBaseTest {

  private final String sampleDataSourcesSheet = "SampleDataSources";
  private final String sampleReportsSheet = "SampleReports";
  private final String reportsSheet = "AnalyzerReports";

  @BeforeClass( )
  public void enableHiddenFiles() {
    HomePage homePage = webUser.login( createExtraDriver( "enableHiddenFiles" ) );
    homePage.showHiddenFile( true );
    quitExtraDriver();
  }

  @BeforeMethod( )
  public void testPreparation() {
    webUser.login( getDriver() );
  }

  @Test( description = "Verify Samples DataSources", dataProvider = "DataProvider" )
  @XlsDataSourceParameters( sheet = sampleDataSourcesSheet, dsUid = "Datasource, Type" )
  @SpiraTestSteps( testStepsId = "52996" )
  public void verifyDataSources( Map<String, String> dynamicAgrs ) {
    String dsnName = dynamicAgrs.get( "Datasource" );
    String dsnType = dynamicAgrs.get( "Type" );

    switch ( dsnType ) {
      case "JDBC":
        dsnType = DataSourceType.JDBC.getName();
        break;
      case "Analysis":
        dsnType = DataSourceType.ANALYSIS.getName();
        break;
      case "Metadata":
        dsnType = DataSourceType.METADATA.getName();
        break;
    }

    HomePage homePage = new HomePage( getDriver() );
    ManageDataSourcesPage manageDataSourcesPage = homePage.openManageDataSources();

    if ( !manageDataSourcesPage.isDataSourcePresent( dsnName, dsnType ) ) {
      Assert.fail( "TS042021: '" + dsnName + "' datasource is not present!" );
    }

    manageDataSourcesPage.closeManageDataSources();
  }

  @Test( dataProvider = "DataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsArgs = "location, Name, VerifyPresent, VerifyNotPresent",
      dsUid = "Name", jiraColumn = "Jira" )
  @SpiraTestSteps( testStepsId = "41555,41556,41557" )
  public void openSampleReports( String location, String name, String presentItem, String notPresentItem ) {
    Report report = (Report) BrowseService.getBrowseItem( location + "/" + name );
    AnalyzerReportPage reportPage = (AnalyzerReportPage) report.open();

    // verification part
    SoftAssert softAssert = reportPage.verifyContent( presentItem.replaceAll( "%{0}", "" ), notPresentItem );
    report.close(); // close without saving

    softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "5" )
  @SpiraTestSteps( testStepsId = "41558" )
  public void verifyChartsFont( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.open();

    // verification part
    SoftAssert softAssert = reportPage.verifyChartFont( "Default", "12", "Normal" );
    report.close(); // close without saving

    softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TestcaseId", executeValue = "8980" )
  @SpiraTestSteps( testStepsId = "52998, 41559" )
  public void createAnalyzerReport( HashMap<String, String> args ) {
    // TODO: use id values from xls dataprovider!
    PAReport report = new PAReport( args );

    // TODO: wrap into create method for Report business object

    LOGGER.info( report.getName() );

    // TS052998: verification happens when selecting the data source.
    AnalyzerReportPage reportPage = report.create();

    // TS041559: verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    if ( !report.getName().isEmpty() ) {
      LOGGER.error( "TODO: implement save logic here!" );
    }

    softAssert.assertAll();

  }
}
