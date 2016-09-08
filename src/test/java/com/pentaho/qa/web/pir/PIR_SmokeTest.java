package com.pentaho.qa.web.pir;

import java.awt.Color;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRFormattingPage;
import com.pentaho.qa.gui.web.pir.PIRGeneralPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Layout;
import com.pentaho.services.Report.Sort;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.pir.Prompt;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.utils.ExportType;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

//http://spiratest.pentaho.com/20/TestCase/11758.aspx
@SpiraTestCase( testCaseId = SpiraTestcases.PIR_SmokeTest, projectId = 20 )
public class PIR_SmokeTest extends WebBaseTest {
  private PIRReport pirReport;
  private PIRReportPage reportPage;
  private Prompt territoryPrompt;

  private final String promptsSheet = "Prompts";
  private final String filtersSheet = "Filters";
  private final String reportsSheet = "PIRReports";

  SoftAssert softAssert = new SoftAssert();

  @BeforeClass( )
  public void login() {
    webUser.login();

  }

  @BeforeMethod( )
  public void resetFrame() {
    if ( reportPage != null ) {
      reportPage.resetActiveFrame();
      // reportPage.switchToFrame();
    }
    softAssert = new SoftAssert();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "36147" )
  @SpiraTestSteps( testStepsId = "56878, 56879" )
  public
    void testCreateReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    // Initialize and create new PIR report
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( true );

    // Verification part
    softAssert.assertTrue( reportPage.isWorkspaceEmpty(),
        "TS056878: Workspace should be empty for newly created report!" );

    // Add fields to the report
    pirReport.addColumns();
    pirReport.addGroups();

    // Verification part
    Assert.assertTrue( reportPage.isCategoryExist( pirReport.getDataSource() ),
        "TS056878: There should be Available Fields for '" + pirReport.getDataSource() + "' data source!" );

    for ( String column : pirReport.getColumns() ) {
      softAssert.assertTrue( reportPage.isColumnOnWorkspace( column ), "TS056879: Column '" + column
          + "' should be added to the Workspace!" );

    }

    // pirReport.save( folder );
    // pirReport.close();
    // this method call will save AND close the report
    pirReport.close( folder );
    pirReport.open();
    pirReport.activateEditMode();

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56905" )
  public void testExportPDF() {
    reportPage.switchToFrame();
    pirReport.exportAs( ExportType.PDF );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56906" )
  public void testExportExcel97() {
    reportPage.switchToFrame();
    pirReport.exportAs( ExportType.EXCEL_97 );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56907" )
  public void testExportCSV() {
    reportPage.switchToFrame();
    pirReport.exportAs( ExportType.CSV );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56908" )
  public void testExportExcel() {
    reportPage.switchToFrame();
    pirReport.exportAs( ExportType.EXCEL );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field, Value", executeColumn = "TeststepId",
      executeValue = "36178" )
  @SpiraTestSteps( testStepsId = "56880, 56881" )
  public void testAddFilters( Map<String, String> args ) {
    // Initialize filter and add it to the report
    PIRFilter filter = new PIRFilter( args );
    reportPage.switchToFrame();
    reportPage.showFilterPanel();

    pirReport.addFilter( filter );

    // Verification part
    Assert.assertTrue( reportPage.showFilterPanel().isFilterExists( filter ), "TS056881: Filter '"
        + filter.getCondition() + " " + filter.getValue() + "' for field '" + filter.getField()
        + "' should be created!" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56881" )
  public void testModifyFilters() {
    // Get existing filters
    List<PIRFilter> filters = pirReport.getFilters();

    // Get Filter Panel page
    reportPage.switchToFrame();
    PIRFilterPanelPage filterPanelPage = reportPage.showFilterPanel();

    // Set OR operator for all existing filters
    filterPanelPage.setOperator( filters, PIRFilterPanelPage.OR_OPERATOR );
    reportPage.refreshFilterPanel();

    // // Workaround for showing Filter panel
    // reportPage.hideFilterPanel();
    // reportPage.showFilterPanel();

    // Grouping filters
    pirReport.indentFilter( filters.get( 0 ) );
    reportPage.refreshFilterPanel();
    pirReport.moveUpFilter( filters.get( 1 ) );
    reportPage.refreshFilterPanel();
    pirReport.moveUpFilter( filters.get( 1 ) );
    reportPage.refreshFilterPanel();

    // Preparation step for the next action: remove the last filter from the list as AND operator for the second group
    // doesn't include it
    filters.remove( 2 );

    // Set AND operator for Customer Number filters
    filterPanelPage.setOperator( filters, PIRFilterPanelPage.AND_OPERATOR );
    filterPanelPage.applyFilter();

    // // Workaround for showing Filter panel
    // reportPage.hideFilterPanel();
    // reportPage.showFilterPanel();

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "36147" )
  @SpiraTestSteps( testStepsId = "56881" )
  public
    void testFilterResults( Map<String, String> args ) {

    reportPage.switchToFrame();

    // Get elements for verification
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    // Add sorting for easier search
    pause( 2 );
    pirReport.sort( Layout.COLUMN, pirReport.getColumns().get( 0 ), Sort.ASC );

    // Verification part
    softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    // Close PIR report as it will be not used in the next steps
    pirReport.close();
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "36150" )
  public
    void prepareCreateFilterByDnD( Map<String, String> args ) {
    // Initialize and create new PIR report
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( true );
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field, Value", executeColumn = "TeststepId",
      executeValue = "36179" )
  @SpiraTestSteps( testStepsId = "56882, 56883" )
  public void testCreateFilterByDnD( Map<String, String> args ) {
    // Initialize filter and add it to the report
    PIRFilter filter = new PIRFilter( args );

    reportPage.switchToFrame();

    pirReport.addFilter( filter, Workflow.DND_PANEL );

    // Show Filters panel
    PIRFilterPanelPage filterPanelPage = reportPage.showFilterPanel();

    // Verification part
    Assert.assertTrue( filterPanelPage.isReadableFilterCorrect( filter.getField(), filter.getCondition().getName(),
        filter.getValue() ), "TS056883: Filter condition should be displayed or incorrect!" );

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filtersSheet, dsUid = "Field, Value", executeColumn = "TeststepId",
      executeValue = "36151" )
  @SpiraTestSteps( testStepsId = "56884" )
  public void testEditFilter( Map<String, String> args ) {
    // Initialize new filter
    PIRFilter newFilter = new PIRFilter( args );

    reportPage.switchToFrame();
    // Show filters panel
    PIRFilterPanelPage filterPanelPage = reportPage.showFilterPanel();

    // Get the last filter in the List
    PIRFilter filter = pirReport.getFilters().get( pirReport.getFilters().size() - 1 );

    // Edit the last filter with values from newFilter
    if ( getDriver().toString().contains( "firefox" ) ) {
      reportPage.makePanelVisible( filter.getField() );
    }

    pirReport.editFilter( filter, newFilter );
    reportPage.makePanelVisible( filter.getField() );

    // Verification part
    Assert.assertTrue( filterPanelPage.isReadableFilterCorrect( filter.getField(), filter.getCondition().getName(),
        filter.getValue() ), "TS056884: Filter condition should be displayed or incorrect!" );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56884" )
  public void testDeleteFilter() {
    reportPage.switchToFrame();

    // Get the last filter in the List
    PIRFilter filter = pirReport.getFilters().get( pirReport.getFilters().size() - 1 );

    // Verification of precondition
    PIRFilterPanelPage filterPanelPage = new PIRFilterPanelPage( getDriver() );
    Assert.assertTrue( filterPanelPage.isFilterExists( filter ), "TS056884: There should be filter for deletion!" );

    // Delete filter
    pirReport.deleteFilter( filter );

    // Verification part
    Assert.assertTrue( filterPanelPage.isFilterNotExist( filter ), "TS056884: Filter '" + filter.getCondition() + " "
        + filter.getValue() + "' for field '" + filter.getField() + "' should be deleted!" );

    pirReport.close();
  }

  // !!! There is no 36178 item in XLS dataprovider
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "36150" )
  @SpiraTestSteps( testStepsId = "56885, 56886, 56887" )
  public
    void testEditReportHeaders( Map<String, String> args ) {
    pirReport = new PIRReport( args );

    reportPage = pirReport.create( true );

    // Set report Title
    pirReport.setTitle();

    reportPage.makePanelVisible( pirReport.getColumns().get( 0 ) );

    // Verification part
    LOGGER.info( "Actual report title: " + reportPage.getTitleWithNotEmptyText() );
    LOGGER.info( "Expected report title: " + pirReport.getTitle() );
    LOGGER
        .info( "Actual title equals with Expected title: " + reportPage.isTitleWithTextPresent( pirReport.getTitle() ) );
    softAssert.assertTrue( reportPage.getTitle().equals( pirReport.getTitle() ), "TS056885: New name '"
        + pirReport.getTitle() + "' should appear in the title bar!" );

    // Preparation step: clear report header
    reportPage.clearReportHeader();
    pause( 2 );

    // Open report header
    Assert.assertTrue( reportPage.openEditReportHeader(),
        "TS056886: Popup window for changing report Header should appear!" );

    // Verification part
    Assert.assertTrue( reportPage.isDateButtonPresent(),
        "TS056886: Date button should be present on the popup window for changing report Header!" );
    reportPage.clickDateButton();
    Assert.assertTrue( reportPage.isPagesButtonPresent(),
        "TS056886: #/Pages button should be not present on the popup window for changing report Header!" );
    reportPage.clickPagesButton();
    reportPage.closeEditReportHeader();

    // Click on Date and Pages button in the window, Apply changes

    // reportPage.clickDateButton();
    // reportPage.clickPagesButton();
    // reportPage.closeEditReportHeader();
    pause( 2 );
    // Verification part

    softAssert.assertTrue( reportPage.isCorrespondCurrentDate( reportPage.getReportHeader(), "EEE MMM dd" ),
        "TS056887: Date should be present in the top left corner of the report!" );
    softAssert.assertTrue( reportPage.isPageCounterCorrectOnHeader( 1, 1 ),
        "TS056887: Pages counter should be present in the top left corner of the report!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56888" )
  public void testUndo() {
    reportPage.switchToFrame();

    // Click Undo
    reportPage.undo();

    // Verification part
    softAssert.assertFalse( reportPage.isCorrespondCurrentDate( reportPage.getReportHeader() ),
        "TS056888: Date should be disappeared from the top left corner of the report, so Undo doesn't work!" );

    softAssert.assertFalse( reportPage.isPageCounterCorrectOnHeader( 1, 1 ),
        "TS056888: Pages counter should be disappeared from the top left corner of the report, so Undo doesn't work!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56889" )
  public void testRedo() {
    reportPage.switchToFrame();

    // Click Redo
    reportPage.redo();

    // Verification part
    softAssert.assertTrue( reportPage.isCorrespondCurrentDate( reportPage.getReportHeader() ),
        "TS056889: Date should be appeared in the top left corner of the report, so Redo doesn't work!" );

    softAssert.assertTrue( reportPage.isPageCounterCorrectOnHeader( 1, 1 ),
        "TS056889: Pages counter should be  appeared in the top left corner of the report, so Redo doesn't work!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56890" )
  public void testLayoutPanel() {
    reportPage.switchToFrame();

    // Click Layout button
    reportPage.showLayoutPanel();

    // Verification part
    softAssert.assertTrue( reportPage.isLayoutPanelShown(), "TS056890: Groups and Columns panel should be open!" );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56891" )
  public void testAddGroupByDnd() {
    reportPage.switchToFrame();

    // Add group by drag and drop
    pirReport.addGroups( Workflow.DND_PANEL );

    // Verification part
    for ( String group : pirReport.getGroups() ) {
      softAssert.assertTrue( reportPage.isGroupOnWorkspace( group ), "TS056891: Group '" + group
          + "' should be added to the Workspace!" );
    }
    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56892" )
  public void testGroupSortingAppears() {
    reportPage.switchToFrame();

    // Verification part for default group sorting
    for ( String group : pirReport.getGroups() ) {
      softAssert.assertTrue( reportPage.isGroupOnWorkspace( group ), "TS056891: Group '" + group
          + "' should be added to the Workspace!" );
    }

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56893" )
  public void testColumnSortingAppears() {
    reportPage.switchToFrame();

    // Add group by drag and drop
    pirReport.addColumns( Workflow.DND_PANEL );

    // Add sorting for all columns by ASC
    for ( String column : pirReport.getColumns() ) {
      pirReport.sort( Layout.COLUMN, column, Sort.ASC );
      softAssert.assertTrue( reportPage.isFieldSortingPresent( column ), "TS056893: Field sorting for '" + column
          + "' should be added!" );
    }

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56894" )
  public void testSorting() {
    reportPage.switchToFrame();

    // Change sorting for one column to DESC
    pirReport.sort( Layout.COLUMN, pirReport.getColumns().get( 0 ), Sort.DESC, Workflow.CONTEXT_PANEL );

    // Verification part
    softAssert.assertTrue( reportPage.verifyGroupSort( pirReport.getGroups().get( 0 ), Sort.ASC ), "TS056894: Group '"
        + pirReport.getGroups().get( 0 ) + "' should be sorted by " + Sort.ASC.getName() + "!" );

    softAssert.assertTrue( reportPage.verifyColumnSort( pirReport.getColumns().get( 0 ), Sort.DESC ),
        "TS056894: Column '" + pirReport.getColumns().get( 0 ) + "' is not sorted by " + Sort.DESC.getName() + "!" );

    // Remove sorting for one column as we are going to test sorting another column
    pirReport.sort( Layout.COLUMN, pirReport.getColumns().get( 0 ), Sort.NONE, Workflow.CONTEXT_PANEL );

    // Verification part

    softAssert.assertTrue( reportPage.verifyColumnSort( pirReport.getColumns().get( 1 ), Sort.ASC ),
        "TS056894: Column '" + pirReport.getColumns().get( 1 ) + "' should be sorted by " + Sort.ASC.getName() + "!" );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56895" )
  public void testSelectAll() {
    reportPage.switchToFrame();

    // Click to Select all
    // TODO: invoke twice as in this particular test one column is already selected! 2ndary select will do correct
    // operation
    pirReport.selectAll();
    pirReport.selectAll();

    // Verification part
    for ( String column : pirReport.getColumns() ) {
      if ( !reportPage.isColumnBlockSelected( column ) )
        softAssert.fail( "TS056895: Values for '" + column + "' column were not selected!" );
      if ( !reportPage.isColumnHeaderSelected( column ) )
        softAssert.fail( "TS056895: Column headers for '" + column + "' column were not selected!" );
    }
    for ( String group : pirReport.getGroups() ) {
      if ( !reportPage.isGroupSelected( group ) )
        softAssert.fail( "TS056895: Group '" + group + "' was not selected!" );
    }

    softAssert.assertAll();

    // Click to Select all to deselect all items
    pirReport.selectAll();

    // Verification part
    for ( String column : pirReport.getColumns() ) {
      if ( reportPage.isColumnBlockSelected( column ) )
        softAssert.fail( "TS056895: Values for '" + column + "' column were not deselected!" );
      if ( reportPage.isColumnHeaderSelected( column ) )
        softAssert.fail( "TS056895: Column headers for '" + column + "' column were not deselected!" );
    }
    for ( String group : pirReport.getGroups() ) {
      if ( reportPage.isGroupSelected( group ) )
        softAssert.fail( "TS056895: Group '" + group + "' was not deselected!" );
    }

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56896" )
  public void testRemoveColumn() {
    reportPage.switchToFrame();

    // Remove column
    String column = pirReport.getColumns().get( 0 ); // TODO: improve hardcoded 0th
    pirReport.removeColumn( column );

    // Verification part
    if ( reportPage.isColumnOnWorkspace( column ) ) {
      softAssert.fail( "TS056896: Column '" + column + "' was not removed from the Workspace!" );
    }

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56897" )
  public void testFormatting() {
    reportPage.switchToFrame();

    // Format columns
    PIRFormattingPage formatPage = reportPage.openFormattingTab( pirReport.getColumns().get( 0 ) );

    formatPage.setFont( L10N.getText( "Editor_FONT_LABEL_2" ) );
    formatPage.setSize( L10N.getText( "Editor_FONT_SIZE_LABEL_2" ) );
    formatPage.setBackgroundColor( Color.RED );
    formatPage.setFontColor( Color.BLUE );
    reportPage.switchToFrame();
    reportPage
        .findExtendedWebElement( By.xpath( "//td[@id='rpt-column-header-0' and contains(@style,'color: blue')]" ) );
    reportPage.findExtendedWebElement( By
        .xpath( "//td[@id='rpt-column-header-0' and contains(@style,'background-color: rgb(255, 0, 0)')]" ) );
    // Verification part
    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "56898" )
  public void testGeneral() {
    reportPage.switchToFrame();

    String template = L10N.getText( "template_0_crystal_2_indented" );
    PIRGeneralPage generalPage = reportPage.openGeneralTab();
    generalPage.selectTemplate( template );

    // Verification part
    if ( !generalPage.isTemplateSelected( template ) ) {
      Assert.fail( "TS056898: Template '" + template + "' was not selected for the report!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "56899, 56900, 56901" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT03" )
  public void testPrompts( Map<String, String> args ) {
    territoryPrompt = new Prompt( args );
    reportPage.switchToFrame();

    reportPage.openDataTab();

    // TODO: maybe it can be removed later it territory is already exists
    // pirReport.addColumn( parameter );

    if ( !reportPage.isPageNumberPanelExists() ) {
      softAssert.fail( "TS056901: Page numbering panel is not present!" );
    }

    if ( !pirReport.addPrompt( territoryPrompt ) ) {
      softAssert.fail( "TS056899: Prompt '" + territoryPrompt.getName() + "/" + territoryPrompt.getValue()
          + "' was not applied!" );
    }

    // Workaround for firefox for showing Prompt panel
    if ( getDriver().toString().contains( "firefox" ) ) {
      reportPage.hidePromptsPanel();
      reportPage.showPromptsPanel();
    }

    if ( !pirReport.editPrompt( territoryPrompt ) ) {
      softAssert.fail( "TS056900: Prompt '" + territoryPrompt.getName() + "' was not updated with new value: "
          + territoryPrompt.getValue() );
    }

    // Workaround for firefox for showing Prompt panel
    if ( getDriver().toString().contains( "firefox" ) ) {
      reportPage.hidePromptsPanel();
      reportPage.showPromptsPanel();
    }

    if ( !pirReport.removePrompt( territoryPrompt ) ) {
      softAssert.fail( "TS056900: Prompt '" + territoryPrompt.getName() + "' was not removed!" );
    }

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "36150" )
  @SpiraTestSteps( testStepsId = "56903" )
  public
    void testSave( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    reportPage.switchToFrame();

    pirReport.save( folder );
    pirReport.close();

    // open again
    pirReport.open();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PIR02_EDIT" )
  @SpiraTestSteps( testStepsId = "56904" )
  public void testEdit( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PIRReport pirReportEdit = new PIRReport( args );
    pirReport.edit( pirReportEdit );

    pirReport.saveAs( folder );
  }
}
