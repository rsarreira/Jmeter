package com.pentaho.qa.web.analyzer;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.OpenPage;
import com.pentaho.qa.gui.web.puc.SavePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/8/TestCase/8828.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_StandaloneSmokeTest )
public class Analyzer_StandaloneSmokeTest extends WebBaseTest {

  @BeforeMethod( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "DataProvider", description = "JIRA#BACKLOG-1777" )
  /*@SpiraTestSteps( testStepsId = "56714,39847,39848,39849,39850" )*/
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerURLs", dsUid = "TUID, Name",
      spiraColumn = "TeststepId", executeColumn = "TestcaseId", executeValue = "8828" )
  public void openReportDirectly( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    LOGGER.info( report.getName() );
    AnalyzerReportPage reportPage = report.openDirectly( args.get( "URL" ) );

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    // report.close(false); //close without saving

    softAssert.assertAll();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "39851" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerURLs", dsUid = "TUID, Name",
          executeColumn = "TeststepId", executeValue = "39858" )
  public void analyzerClickNewReportTest( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.openDirectly( args.get( "URL" ) );

    reportPage.click(reportPage.btnNewReport );

    AnalyzerDataSourcePage dataSourcePage = new AnalyzerDataSourcePage( getDriver() );
    Assert.assertTrue(dataSourcePage.isOpened(EXPLICIT_TIMEOUT ), "'Select Data Source' is not opened!" );
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "39852" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerURLs", dsUid = "TUID, Name",
          executeColumn = "TeststepId", executeValue = "39858" )
  public void analyzerClickOpenTest( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.openDirectly( args.get( "URL" ) );

    reportPage.click(reportPage.btnOpenReport );

    OpenPage openPage = new OpenPage( getDriver()  );
    Assert.assertTrue(openPage.isOpened(EXPLICIT_TIMEOUT ), "'Open Page' is not opened!" );
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "39854" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerURLs", dsUid = "TUID, Name",
          executeColumn = "TeststepId", executeValue = "39858" )
  public void analyzerClickSaveAsTest( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.openDirectly( args.get( "URL" ) );

    reportPage.click(reportPage.btnToolBarSaveAs );

    SavePage savePage = new SavePage( getDriver() );
    Assert.assertTrue( savePage.isOpened( EXPLICIT_TIMEOUT ), "'Save Page' is not opened!" );
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "39855,39856" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerURLs", dsUid = "TUID, Name",
          executeColumn = "TeststepId", executeValue = "39858" )
  public void analyzerBackForwardButtonsTest( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.openDirectly( args.get( "URL" ) );

    driver.navigate().back();
    HomePage homePage = new HomePage( getDriver() );
    Assert.assertTrue( homePage.isOpened(), "'HomePage' has not opened, possibly 'Back' button in browser failed" );  // Better error message can be written
    driver.navigate().forward();

    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();
  }


}
