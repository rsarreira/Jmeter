package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.pir.PIRReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

//http://spiratest.pentaho.com/20/TestCase/7914.aspx
@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_Hint_tooltips )
public class PIR_Hint_tooltips extends WebBaseTest {

  private PIRReport pirReport;
  private PIRReportPage reportPage;
  private final String reportsSheet = "PIRReports";

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
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( true );
    reportPage.openGeneralTab();
    reportPage.enableHints();
    reportPage.openDataTab();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId",
      executeValue = "75395" )
  @SpiraTestSteps( testStepsId = "75395" )
  public void verifyHintTooltips( Map<String, String> args ) {
    notPresentItem = args.get( "VerifyNotPresent" );
    presentItem = args.get( "VerifyPresent" );
    String[] presentItems = presentItem.split( ";" );
    String elementFirst = L10N.getText( presentItems[0] );
    String[] elementFirstParts = elementFirst.split( "<br>" );

    // Verify 'Edit inline' hint tooltip
    reportPage.clickEditInlineHint();

    for ( int i = 0; i < elementFirstParts.length; i++ ) {
      String elementPresent = elementFirstParts[i].trim();
      SoftAssert softAssert = reportPage.verifyContent( elementPresent, notPresentItem, reportPage.tooltip );
      softAssert.assertAll();
    }

    // Verify 'Swap / drop columns' hint tooltip
    String elementSecond = L10N.getText( presentItems[1] );
    String[] elementSecondParts = elementSecond.split( "<br/>" );
    reportPage.clickSwapDropColumnsHint();

    for ( int i = 0; i < elementSecondParts.length; i++ ) {
      String elementPresent = elementSecondParts[i].trim();
      SoftAssert softAssert = reportPage.verifyContent( elementPresent, notPresentItem, reportPage.tooltip );
      softAssert.assertAll();
    }

    // Verify 'Drop groups' hint tooltip
    String elementThird = L10N.getText( presentItems[2] );
    String[] elementThirdParts = elementThird.split( "<br/>" );
    reportPage.clickDropGroupsHint();

    for ( int i = 0; i < elementThirdParts.length; i++ ) {
      String elementPresent = elementThirdParts[i].trim();
      SoftAssert softAssert = reportPage.verifyContent( elementPresent, notPresentItem, reportPage.tooltip );
      softAssert.assertAll();
    }

    // Verify 'Drag fields' hint tooltip
    String elementFourth = L10N.getText( presentItems[3] );
    String[] elementFourthParts = elementFourth.split( "<br/>" );
    reportPage.clickDragFieldsHint();

    for ( int i = 0; i < elementFourthParts.length; i++ ) {
      String elementPresent = elementFourthParts[i].trim();
      SoftAssert softAssert = reportPage.verifyContent( elementPresent, notPresentItem, reportPage.tooltip );
      softAssert.assertAll();
    }
  }
}
