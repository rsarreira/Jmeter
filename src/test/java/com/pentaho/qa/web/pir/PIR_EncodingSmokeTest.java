package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/20/TestCase/11757.aspx
@SpiraTestCase( testCaseId = SpiraTestcases.PIR_EncodingSmokeTest, projectId = 20 )
public class PIR_EncodingSmokeTest extends WebBaseTest {

  private PIRReport pirReport;
  private PIRReport pirReportUpdated;
  private PIRReportPage reportPage;

  private final String reportsSheet = "PIRReports";

  @BeforeClass( )
  public void login() {
    webUser.login();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "62263" )
  @SpiraTestSteps( testStepsId = "62263" )
  public void testCreateReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ));
    pirReport = new PIRReport( args );
    reportPage = pirReport.create();
    pirReport.save( folder );

    // Verification part
    if ( !reportPage.isSaved( pirReport.getName() ) )
      Assert.fail( "TS062263: PIR report with special chars wasn't saved!" );
  }

  @Test( dependsOnMethods = "testCreateReport" )
  @SpiraTestSteps( testStepsId = "62264" )
  public void testReopen() {
    pirReport.close();
    if ( !reportPage.isClosed( pirReport.getName() ) ) {
      Assert.fail( "TS062264: PIR report with special chars wasn't closed!" );
    }

    reportPage = pirReport.open();
    if ( !reportPage.isOpened(EXPLICIT_TIMEOUT) ) {
      Assert.fail( "TS062264: PIR report with special chars wasn't opened!" );
    }
  }

  @Test( dependsOnMethods = "testReopen", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "62265" )
  @SpiraTestSteps( testStepsId = "62265" )
  public void testEdit( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ));
    PIRReport tempReport = new PIRReport( args );
    pirReport.edit( tempReport );
    pirReportUpdated = pirReport.saveAs(folder);
    
    reportPage.resetActiveFrame();
    reportPage.switchToFrame();

    // Verification part
    if ( !pirReportUpdated.verify() ) {
      Assert.fail( "TS062265: PIR report was updated inproperly!" );
    }
  }

  @Test( dependsOnMethods = "testEdit" )
  @SpiraTestSteps( testStepsId = "62266" )
  public void testOpenOld() {
    pirReport.open();

    reportPage.resetActiveFrame();
    reportPage.switchToFrame();
    
    if ( !pirReport.verify() ) {
      Assert.fail( "TS062266: There are errors in original report!" );
    }
  }

  @Test( dependsOnMethods = "testOpenOld" )
  @SpiraTestSteps( testStepsId = "62267" )
  public void testClose() {
    pirReport.close();

    if ( !reportPage.isClosed( pirReport.getName() ) ) {
      Assert.fail( "TS062267: PIR report with special chars wasn't closed!" );
    }
  }
}
