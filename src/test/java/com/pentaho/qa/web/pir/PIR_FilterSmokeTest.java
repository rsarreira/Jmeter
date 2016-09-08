package com.pentaho.qa.web.pir;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRFilterPage;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

//http://spiratest.pentaho.com/20/TestCase/11767.aspx
@SpiraTestCase( testCaseId = SpiraTestcases.PIR_FilterSmokeTest, projectId = 20 )
public class PIR_FilterSmokeTest extends WebBaseTest {

  private PIRReport pirReport;
  private PIRReportPage reportPage;
  private PIRFilterPanelPage filterPanelPage;
  private final String filtersSheet = "Filters";
  private final String reportsSheet = "PIRReports";

  SoftAssert softAssert = new SoftAssert();

  @BeforeClass( )
  public void login() {
    webUser.login();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "62367" )
  @SpiraTestSteps( testStepsId = "62367" )
  public
    void testFilterPanelShow( Map<String, String> args ) {
    // Initialize and create new PIR report
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( false );

    String noFilterMessage = L10N.getText( "filterPanelHint_content" );

    filterPanelPage = reportPage.showFilterPanel();

    if ( !filterPanelPage.getNoFiltersMessage().equals( noFilterMessage ) ) {
      Assert.fail( "TS062367: No initial Filter Panel message or it's incorrect!" );
    }
  }

  @Test( dependsOnMethods = "testFilterPanelShow" )
  @SpiraTestSteps( testStepsId = "62368" )
  public void testFilterPanelHide() {
    filterPanelPage.hideFilterPanel();
    if ( filterPanelPage.isFilterPanelShown() ) {
      Assert.fail( "TS062368: Filter Panel didn't disappear!" );
    }
  }

  @Test( description = "JIRA# BACKLOG-5089", dependsOnMethods = "testFilterPanelHide",
      dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62370" )
  @SpiraTestSteps( testStepsId = "62369, 62370" )
  public
    void testCreateFilterContextReportArrow( Map<String, String> args ) {
    PIRFilter filter = new PIRFilter( args );
    pirReport.addFilter( filter, Workflow.CONTEXT_REPORT_ARROW );
    filterPanelPage.showFilterPanel();

    // Verification that filter displays correct text
    softAssert = pirReport.verifyFilters();

    // Verification that dropdown works
    softAssert.assertTrue( filterPanelPage.verifyFilterPopup( pirReport.getFilters().get( 0 ) ),
        "TS062370: Filter popup didn't appear!" );

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "testCreateFilterContextReportArrow", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62371" )
  @SpiraTestSteps( testStepsId = "62371, 62372" )
  public
    void testCreateFilterContextReport( Map<String, String> args ) {
    PIRFilter filter = new PIRFilter( args );
    pirReport.addFilter( filter, Workflow.CONTEXT_REPORT );
    filterPanelPage.showFilterPanel();

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "testCreateFilterContextReport", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62373" )
  @SpiraTestSteps( testStepsId = "62373, 62374, 62375" )
  public
    void testCreateFilterContextField( Map<String, String> args ) {
    PIRFilter filter = new PIRFilter( args );
    pirReport.addFilter( filter, Workflow.CONTEXT_PANEL );

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testCreateFilterContextField", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62376" )
  @SpiraTestSteps( testStepsId = "62376, 62377" )
  public
    void testCreateFilterDnD( Map<String, String> args ) {
    PIRFilter filter = new PIRFilter( args );
    pirReport.addFilter( filter, Workflow.DND_PANEL );

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testCreateFilterDnD", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62378" )
  @SpiraTestSteps( testStepsId = "62378" )
  public
    void testCreateFilterDnDNotPresent( Map<String, String> args ) {
    PIRFilter filter = new PIRFilter( args );
    pirReport.addFilter( filter, Workflow.DND_PANEL );

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testCreateFilterDnD" )
  @SpiraTestSteps( testStepsId = "62379" )
  public void testFiltersNumber() {
    // Verification that filters have been added to the Filter panel
    softAssert = pirReport.verifyFilters();

    // Verification that the filter icon shows the number of Filters applied
    softAssert.assertEquals( reportPage.getFiltersNumber(), pirReport.getFilters().size(),
        "TS062379: The filter icon shows incorrect number of applied filters!" );
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "testFiltersNumber" )
  @SpiraTestSteps( testStepsId = "62380" )
  public void testFilterIcon() {
    // Verification that each field that is being filtered has a filter icon next to it
    for ( PIRFilter filter : pirReport.getFilters() ) {
      String field = filter.getField();
      softAssert.assertTrue( reportPage.isFilterIconPresent( field ),
          "TS062380: there is no filter icon next to field: " + field );
    }
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "testFilterIcon", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62386" )
  @SpiraTestSteps( testStepsId = "62385, 62386" )
  public
    void testFilterEdit( Map<String, String> args ) {
    // Edit existing filter
    PIRFilter newFilter = new PIRFilter( args );
    PIRFilter filter = pirReport.getFilters().get( 4 );
    filter.edit( newFilter );

    // Verification filter on Filter panel
    pirReport.verifyFilters().assertAll();

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testFilterEdit", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62387" )
  @SpiraTestSteps( testStepsId = "62387" )
  public
    void testFilterDelete( Map<String, String> args ) {
    // Delete existing filter
    PIRFilter filter = pirReport.getFilters().get( 1 );
    pirReport.deleteFilter( filter );

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testFilterDelete", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field", executeColumn = "TeststepId", executeValue = "62382" )
  @SpiraTestSteps( testStepsId = "62381, 62382" )
  public
    void testSelectFromList( Map<String, String> args ) {
    filterPanelPage.showFilterPanel();
    PIRFilterPage filterPage = new PIRFilterPage( getDriver() );

    String filterValue = args.get( "Field" );
    List<String> values = Arrays.asList( args.get( "Value" ).split( "; " ) );

    reportPage.addFilter( filterValue, Workflow.CONTEXT_PANEL );
    filterPage.setSelectFromList();
    filterPage.addAllItems();
    filterPage.removeAllItems();
    filterPage.addItem( values );
    filterPage.removeItem( values );
    filterPage.addItem( values );
    filterPage.btnOkClicked();

    // Verification that report is filtered correctly
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    reportPage.verifyContentByValue( presentItem, notPresentItem ).assertAll();
  }

  @Test( dependsOnMethods = "testSelectFromList" )
  @SpiraTestSteps( testStepsId = "62388" )
  public void testSaveReopen() {
    // Save and reopen
    Folder folder = getUserHome();
    pirReport.save(folder);
    pirReport.close();
    pirReport.open();

    // Verification that all of the filter information intact
    reportPage.showFilterPanel();
    pirReport.verifyFilters().assertAll();
  }
}
