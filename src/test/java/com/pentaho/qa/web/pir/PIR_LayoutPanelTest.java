//http://spiratest.pentaho.com/20/TestCase/13433.aspx

//DESIRED SETTINGS FOR EXECUTION:
//driver_mode=method_mode
//recovery=true
//retry_count=1
//thread_count=4

package com.pentaho.qa.web.pir;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRReport;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_LayoutPanelTest )
public class PIR_LayoutPanelTest extends WebBaseTest {
  private static ThreadLocal<PIRReport> pirReports = new ThreadLocal<PIRReport>();
  private static ThreadLocal<PIRReportPage> reportPages = new ThreadLocal<PIRReportPage>();

  @BeforeMethod
  @Parameters( { "columns", "groups", "dataSource", "title", "name" } )
  @SpiraTestSteps( testStepsId = "74992,74993" )
  public void prepareTest( String columns, String groups, String dataSource, String title, String name ) {
    webUser.login( getDriver() );

    setReport( new PIRReport( name, null, null, null, title, dataSource, null, columns, groups ) );
    PIRReport pirReport = getReport();
    setReportPage( pirReport.create( true ) );
    pirReport.addGroups( Workflow.DND_PANEL );
    pirReport.addColumns( Workflow.DND_PANEL );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74994" )
  public void testLayoutPanel() {
    PIRReportPage reportPage = getReportPage();
    reportPage.showLayoutPanel();

    // Verification part
    if ( !reportPage.verifyLayoutPanel() )
      Assert.fail( "TS063403: Groups and Columns panel didn't open!" );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74995" )
  public void reorderTest() {
    PIRReport pirReport = getReport();
    PIRReportPage reportPage = getReportPage();

    String fromGroupField = pirReport.getGroups().get( 0 );
    String toGroupField = pirReport.getGroups().get( 1 );

    String fromColumnField = pirReport.getColumns().get( 0 );
    String toColumnField = pirReport.getColumns().get( 1 );

    reportPage.showLayoutPanel();
    // reorder groups
    reportPage.reorderGroupFields( fromGroupField, toGroupField );
    pause( 2 );
    if ( !reportPage.verifyReorderedGroups( toGroupField ) ) {
      Assert.fail( "Groups were not reordered successfully!" );
    }
    // reorder columns
    reportPage.reorderColumnFields( fromColumnField, toColumnField );
    pause( 2 );
    if ( !reportPage.verifyReorderedColumns( toColumnField ) ) {
      Assert.fail( "Columns were not reordered successfully!" );
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74996" )
  public void moveTest() {
    PIRReport pirReport = getReport();
    PIRReportPage reportPage = getReportPage();

    String groupField = pirReport.getGroups().get( 0 );
    String columnField = pirReport.getColumns().get( 0 );
    reportPage.showLayoutPanel();
    // from group to column
    if ( !reportPage.moveToColumnsInLayoutPanel( groupField ) ) {
      Assert.fail( "Group: " + groupField + " was not moved successfully!" );
    }

    // from column to group
    if ( !reportPage.moveToGroupsInLayoutPanel( columnField ) ) {
      Assert.fail( "Column: " + columnField + " was not moved successfully!" );
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74997" )
  public void removeTest() {
    PIRReport pirReport = getReport();
    PIRReportPage reportPage = getReportPage();

    String groupField = pirReport.getGroups().get( 1 );
    reportPage.removeGroup( groupField, Workflow.DND_PANEL );
    reportPage.showLayoutPanel();
    if ( reportPage.isGroupOnWorkspace( groupField ) ) {
      Assert.fail( "Group: " + groupField + " was not removed successfully!" );
    }
  }

  private static PIRReport getReport() {
    return pirReports.get();
  }

  private static void setReport( PIRReport report ) {
    pirReports.set( report );
  }

  private static PIRReportPage getReportPage() {
    return reportPages.get();
  }

  private static void setReportPage( PIRReportPage report ) {
    reportPages.set( report );
  }
}
