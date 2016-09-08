package com.pentaho.qa.web.puc;

import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8982.aspx
@SpiraTestCase( projectId = 6, testCaseId = 8982 )
public class PUC_RunSamples extends WebBaseTest {

  @Test( )
  @SpiraTestSteps( testStepsId = "41573" )
  public void testPreparation() {
    HomePage homePage = webUser.login();
    homePage.showHiddenFile( true );
  }

  @Test( dataProvider = "SingleDataProvider",
      description = "Run all the samples in the 'BI Developer Examples/Reporting'" )
  @SpiraTestSteps( testStepsId = "41579" )
  @XlsDataSourceParameters( sheet = "SampleReports", dsUid = "Name", executeColumn = "TestcaseId",
      executeValue = "41579" )
  public void openSampleReports( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    AnalyzerReportPage reportPage = report.open();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    report.close(); // close without saving

    softAssert.assertAll();
  }

}
