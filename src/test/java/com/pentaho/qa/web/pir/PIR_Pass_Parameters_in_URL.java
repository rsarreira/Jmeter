package com.pentaho.qa.web.pir;

import java.util.ArrayList;
import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/20/TestCase/13467.aspx
@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_Pass_Parameters_in_URL )
public class PIR_Pass_Parameters_in_URL extends WebBaseTest {

  private PIRReport pirReport;
  private PIRFilter newFilter;
  private PIRReportPage reportPage;
  private final String reportsSheet = "PIRReports";
  private final String filtersSheet = "Filters";
  private Folder folder;

  String presentItem;
  String notPresentItem;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId",
      executeValue = "75476" )
  public void createInteractiveReport( Map<String, String> args ) {
    // Initialize and create new PIR report
    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( false );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "TeststepId", executeColumn = "TeststepId",
      executeValue = "75476" )
  @SpiraTestSteps( testStepsId = "75476" )
  public void createInteractiveReportFilter( Map<String, String> args ) {
    // Initialize and create new PIR filter
    newFilter = new PIRFilter( args );
    newFilter.create( Workflow.CONTEXT_PANEL );

    pirReport.save( folder );
    pirReport.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "TeststepId", executeColumn = "TeststepId",
      executeValue = "75476" )
  @SpiraTestSteps( testStepsId = "75477" )
  public void openInteractiveReportinSeparateTab( Map<String, String> args ) {
    // Open saved PIR report in separate window
    reportPage = pirReport.open();
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.switchToFrame( 1 );
    reportPage.getActiveTab().rightClick();
    reportPage.selectOpenTabInNewWindow();
    reportPage.getOkButton().click();

    ArrayList<String> tabs = new ArrayList<String>( getDriver().getWindowHandles() );
    getDriver().switchTo().window( tabs.get( 1 ) );

    //Verify that just created filter is applied
    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem, reportPage.contentElementWithText );
    softAssert.assertAll();

    String currentURL = getDriver().getCurrentUrl();
    String newURL = currentURL + "&" + args.get( "ParamName" ) + "=" + args.get( "VerifyNotPresent" );
    getDriver().get( newURL );

    // Verify that new filter is applied after passing parameter into URL
    //Parameters in method are in correct order because of swapping presentItem and notPresentItem
    SoftAssert softAssert1 = reportPage.verifyContent( notPresentItem, presentItem, reportPage.contentElementWithText );
    softAssert1.assertAll();
  }

}
