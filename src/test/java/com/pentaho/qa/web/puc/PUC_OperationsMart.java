package com.pentaho.qa.web.puc;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8916.aspx
@SpiraTestCase( projectId = 6, testCaseId = 8916 )
public class PUC_OperationsMart extends WebBaseTest {
  private final String sampleDataSourcesSheet = "OM_DataSources";
  private final String sampleReportsSheet = "OM_Reports";

  @BeforeClass( )
  public void login() {
    webUser.login();
  }

  @Test( description = "Verify Samples DataSources", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleDataSourcesSheet, dsUid = "Datasource, Type", staticArgs = "user, password" )
  @SpiraTestSteps( testStepsId = "" )
  public void verifyDataSources( Map<String, String> dynamicAgrs, String user, String password ) {
    String dsnName = dynamicAgrs.get( "Datasource" );
    String dsnType = dynamicAgrs.get( "Type" );
    HomePage homePage = new HomePage( getDriver() );
    ManageDataSourcesPage manageDataSourcesPage = homePage.openManageDataSources();

    if ( !manageDataSourcesPage.isDataSourcePresent( dsnName, dsnType ) ) {
      Assert.fail( "TS042021: CSV datasource was not created successfully!" );
    }

    manageDataSourcesPage.closeManageDataSources();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name", executeColumn="TeststepId", executeValue="40851" )
  @SpiraTestSteps( testStepsId = "40851" )
  public void openBAAuditReports( Map<String, String> args ) {
    PIRReport report = new PIRReport( args );
    
    PIRReportPage reportPage = report.open();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    report.close(); // close without saving

    softAssert.assertAll();
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name", executeColumn="TeststepId", executeValue="40852_PIR" )
  @SpiraTestSteps( testStepsId = "40852" )
  public void openDIAuditReportsPIR( Map<String, String> args ) {
    PIRReport report = new PIRReport( args );
    
    PIRReportPage reportPage = report.open();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    report.close(); // close without saving

    softAssert.assertAll();
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name", executeColumn="TeststepId", executeValue="40852_PA" )
  @SpiraTestSteps( testStepsId = "40852" )
  public void openDIAuditReportsAnalysis( Map<String, String> args ) {
    PAReport report = new PAReport( args );
    
    AnalyzerReportPage reportPage = report.open();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    report.close(); // close without saving

    softAssert.assertAll();
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name", executeColumn="TeststepId", executeValue="40852_D" )
  @SpiraTestSteps( testStepsId = "40852" )
  public void openDIAuditReportsDash( Map<String, String> args ) {
    Assert.fail("TODO");
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = sampleReportsSheet, dsUid = "Name" )
  @SpiraTestSteps( testStepsId = "41555,41556,41557" )
  public void openOperationsMartReports( Map<String, String> args ) {
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
