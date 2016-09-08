//http://spiratest.pentaho.com/8/TestCase/11777.aspx
package com.pentaho.qa.web.analyzer;

import org.openqa.selenium.WebDriverException;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.PanelItem;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarDndType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.analyzer.chart.ScatterChart;
import com.pentaho.qa.gui.web.puc.BasePage.Theme;
import com.pentaho.qa.gui.web.puc.BasePage.View;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Filter.Condition;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.analyzer.PAFilter;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_UI_Smoketest )
public class Analyzer_UI_Smoketest extends WebBaseTest {
  private final String reportSheet = "AnalyzerReports";
  private final String csvDataSourceSheet = "CSV_DataSources";

  private PAReport report;
  private AnalyzerReportPage reportPage;
  private CSVDataSource csvDataSource;

  private String reportPath;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PAR16" )
  @SpiraTestSteps( testStepsId = "62510, 62511, 62512, 62515, 62524, 62541" )
  public void createNewReport( Map<String, String> args ) {
    report = new PAReport( args );

    reportPath = args.get( "location" );

    // TS062510, TS062511, TS062515, TS062524, TS062525, TS062541: check for available field list, that the table view
    // is selected by default, the available field and layout panels have an equal width, the layout panel's title bars
    // have collapsible/expandable arrows, and the empty report has the correct image and text in the report area.
    reportPage = report.create( true, true );

    // TS062512: show/hide the available fields panel and verify the button's tooltip changes.
    reportPage.hideAvailableFieldsPanel();
    reportPage.showAvailableFieldsPanel();

    // re-verify using the shortcut keys instead of clicking.
    reportPage.hideAvailableFieldsPanel( true );
    reportPage.showAvailableFieldsPanel( true );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62513" )
  public void checkLayoutPanel() {
    // show/hide the layout panel and verify the button's tooltip changes.
    reportPage.hideLayoutPanel();
    reportPage.showLayoutPanel();

    // re-verify using the shortcut keys instead of clicking.
    reportPage.hideLayoutPanel( true );
    reportPage.showLayoutPanel( true );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62514" )
  public void checkFilterPanel() {
    // show/hide the filter panel and verify the button's tooltip changes.
    reportPage.showFilterPanel();
    reportPage.hideFilterPanel();

    // re-verify using the shortcut keys instead of clicking.
    reportPage.showFilterPanel( true );
    reportPage.hideFilterPanel( true );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62554" )
  public void verifyReportOptionsBtn() {

    SoftAssert softAssert = new SoftAssert();

    if ( !reportPage.getReportOptionsBtnText().contains( "..." ) ) {
      // Ellipsis is missing for Japanese locale.
      if ( locale.equals( "ja" ) ) {
        Jira.setTickets( "BISERVER-13337" );
      }

      softAssert.fail( "The report options button does not contain an ellipsis!" );
    }

    // Make sure that the report options button size is within the expected range.
    softAssert.assertTrue( report.verifyReportOptionsBtnSize(), "The report options button is not the expected size!" );

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR16_EDIT_1" )
  @SpiraTestSteps( testStepsId = "62516" )
  public void verifyCategoryView( Map<String, String> args ) {
    // Recreate the report if using the Japanese locale due to a known issue in the previous method.
    if ( locale.equals( "ja" ) ) {
      reportPage = report.create( true );
    }

    SoftAssert softAssert = new SoftAssert();

    // Verify that categories exist in the available fields panel.
    List<String> categories = reportPage.getAvailableCategories();
    softAssert.assertTrue( categories.size() > 0, "TS062516: No categories were found in the available fields panel!" );

    // Verify that fields exist within categories in the available fields panel.
    List<String> fields = reportPage.getAllAvailableFields();
    softAssert.assertTrue( fields.size() > 0, "TS062516: No fields were found within any categories!" );

    // Verify that the categories are listed in alphabetical order.
    softAssert.assertTrue( isAlphabetical( categories ),
        "TS062516: The categories in the available field list are not in alphabetical order!." );

    // Retrieve the expected measures and the actual measures in the available fields panel.
    List<String> measures = Arrays.asList( args.get( "Measures" ).toString().split( "," ) );

    softAssert = verifyMeasures( softAssert, measures, "TS062516" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62515, 62517" )
  public void verifyMeasureLevelTimeView() {
    SoftAssert softAssert = new SoftAssert();

    // TS062515: This step is tested throughout the class when changing views.
    LOGGER.info( "Changing to 'Measure - Level - Time' available fields view." );
    softAssert.assertTrue( reportPage.changeView( View.TYPE ), "TS062515: Failed to switch views!" );

    List<String> categories = reportPage.getAvailableCategories();
    List<String> expectedCategories =
        Arrays.asList( L10N.getText( "fieldTypes_NUMBER" ), L10N.getText( "fieldTypeLabels_ATTRIBUTE" ), L10N.getText(
            "fieldTypeLabels_TIME" ) );

    // Verify the number of categories is correct.
    softAssert.assertTrue( categories.size() == 3,
        "TS062517: An unexpected number of categories were found in the 'Measure - Level - Time' view! Expected: 3, Found: "
            + categories.size() + "." );

    // Verify that all three categories that are found match what is expected.
    softAssert.assertTrue( categories.containsAll( expectedCategories ),
        "TS062517: Unexpected categories exist for the available fields panel in the 'Measure - Level - Time view! Expected: "
            + expectedCategories + ", Found: " + categories + "." );

    List<String> fields = reportPage.getAvailableFieldsList();

    // Verify that fields exist within categories.
    softAssert.assertTrue( fields.size() > 0, "TS062517: No fields were found within any categories!" );

    // Verify that each category contains a list of alphabetized fields.
    for ( String fieldList : fields ) {
      softAssert.assertTrue( isAlphabetical( Arrays.asList( fieldList.split( "\n" ) ) ),
          "TS062517: Fields are not alphabetical by category in the available fields panel: " + fieldList.split( "\n" )
              + "." );
    }

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62515, 62518" )
  public void verifyAlphabeticalView() {
    SoftAssert softAssert = new SoftAssert();

    // TS062515: This step is tested throughout the class when changing views.
    LOGGER.info( "Changing to 'A-Z' available fields view." );
    softAssert.assertTrue( reportPage.changeView( View.NAME ), "TS062515: Failed to switch views!" );

    List<String> categories = reportPage.getAvailableCategories();

    // Verify that no categories exist.
    softAssert.assertTrue( categories.size() == 0,
        "TS062518: An unexpected number of categories were found in the 'A-Z' view! Expected: 0, Found: " + categories
            .size() );

    List<String> fields = reportPage.getAvailableFieldsList();

    // Verify that fields exist within categories.
    softAssert.assertTrue( fields.size() > 0, "TS062518: No fields were found in the available fields panel!" );

    // Verify that the fields are alphabetized.
    softAssert.assertTrue( isAlphabetical( fields ),
        "TS062518: Fields are not alphabetized in the available fields panel: " + fields + "." );

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR16_EDIT_1" )
  @SpiraTestSteps( testStepsId = "62515, 62519" )
  public void verifySchemaView( Map<String, String> args ) {
    SoftAssert softAssert = new SoftAssert();

    final String[] expectedCategories = args.get( "VerifyPresent" ).split( "," );

    // TS062515: This step is tested throughout the class when changing views.
    LOGGER.info( "Changing to 'Schema' available fields view." );
    softAssert.assertTrue( reportPage.changeView( View.SCHEMA ), "TS062515: Failed to switch views!" );

    List<String> categories = reportPage.getAvailableCategories();

    // Verify that categories exist.
    softAssert.assertTrue( categories.size() == expectedCategories.length,
        "TS062519: An unexpected number of categories were found in the available fields panel! Expected "
            + expectedCategories.length + ", found: " + categories.size() + "." );

    // Verify that all categories that are found match what is expected.
    if ( categories.size() == expectedCategories.length ) {
      for ( int i = 0; i < categories.size(); i++ ) {
        softAssert.assertTrue( categories.get( i ).equals( expectedCategories[i] ),
            "TS062519: A category does not match the expected category! Expected: " + expectedCategories[i]
                + ", Found: " + categories.get( i ) + "." );
      }
    }

    List<String> fields = reportPage.getAvailableFieldsList();

    // Verify that fields exist within categories.
    softAssert.assertTrue( fields.size() > 0, "TS062519: No fields were found in the available fields panel!" );

    List<String> measures = Arrays.asList( args.get( "Measures" ).toString().split( "," ) );

    // Verify the measures exist within the measures category.
    softAssert = verifyMeasures( softAssert, measures, "TS062519" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62515" )
  public void showHiddenFields() {
    SoftAssert softAssert = new SoftAssert();

    softAssert.assertTrue( reportPage.changeView( View.HIDDEN ),
        "TS062515: Failed to toggle 'Show Hidden Fields' in the available fields view drop-down!" );

    // De-select the "Show Hidden Fields" option.
    reportPage.changeView( View.HIDDEN );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62520" )
  public void verifyMoreActionsMenu() {
    SoftAssert softAssert = new SoftAssert();

    List<String> missingMenuItems = reportPage.verifyMoreActionsMenu();

    softAssert.assertTrue( missingMenuItems.size() == 0,
        "The following items were found missing from the 'More Actions and Options' menu: " + missingMenuItems );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62521, 62522, 62542, 62543, 62555, 62556" )
  public void verifyChartList() {
    List<String> tickets = new ArrayList<String>();

    // Known translation issue for scatter plot's "Size By" gem bar.
    if ( !locale.equals( "en" ) ) {
      tickets.add( "BISERVER-12727" );
    }

    SoftAssert softAssert = new SoftAssert();

    for ( ChartType type : ChartType.values() ) {
      boolean chartLinkFound = true;
      String chartId = type.getId();

      try {
        // TS062521: Verify the chart drop-down list.
        if ( !type.equals( ChartType.PIVOT ) ) {
          reportPage.changeChartType( type );
        } else {
          reportPage.changeToPivotView();
        }
        LOGGER.info( "Found chart ID '" + chartId + "'" );

        // TS062522: Verify the required gem bar members, their icons, and the container class.
        // TS062543: Verify that the red asterisk is present when no fields exist in the gem bar, and is not present
        // when fields are present.
        softAssert = verifyGemBars( type, softAssert );
      } catch ( WebDriverException exception ) {
        // Catch exceptions when the chart elements are not found so that the other charts will be verified.
        chartLinkFound = false;
        LOGGER.error( "Failed to find the chart menu option with ID '" + chartId + "'!" );
      }

      softAssert.assertTrue( chartLinkFound, "Failed to switch to chart view with ID '" + chartId + "'!" );

      // TS062542: Verify that the default image/text is visible and correct for each chart type.
      if ( !reportPage.isEmptyReportImagePresent( type ) ) {
        // The empty report images used for charts are incorrect.
        tickets.add( "BACKLOG-9809" );
        softAssert.fail( "Empty report image is missing or incorrect for chart '" + chartId + "'!" );
      }

      softAssert.assertTrue( reportPage.isEmptyReportDragFieldTextPresent(),
          "Empty report drag field text is missing for chart '" + chartId + "'!" );
      softAssert.assertTrue( reportPage.isEmptyReportAsteriskTextPresent(),
          "Empty report image is missing or incorrect for chart '" + chartId + "'!" );

      if ( !type.equals( ChartType.PIVOT ) ) {
        if ( reportPage.isChartOptionsBtnPresent() ) {

          if ( !reportPage.getChartOptionsBtnText().contains( "..." ) ) {
            // Ellipsis is missing for Japanese locale.
            if ( locale.equals( "ja" ) ) {
              tickets.add( "BISERVER-13337" );
            }

            softAssert.fail( "The report options button does not contain an ellipsis for the chart '" + chartId
                + "'!" );
          }

          // Make sure that the chart options button size is within the expected range.
          softAssert.assertTrue( report.verifyChartOptionsBtnSize(),
              "The chart options button is not the expected size for the chart '" + chartId + "'!" );
        } else {
          // The geo map chart type is missing the chart options button.
          tickets.add( "ANALYZER-2203" );
          softAssert.fail( "The chart options button is not present for the chart '" + chartId + "'!" );
        }
      }
    }

    // Reset to the default view.
    reportPage.resetReport();

    Jira.setTickets( tickets );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62526" )
  public void resizeBrowser() {
    // Recreate the report due to a failure from a known issue in the previous step.
    reportPage = report.create( true );

    SoftAssert softAssert = new SoftAssert();

    // Resize the browser so that it is smaller.
    if ( !reportPage.resizeBrowser( 800, 800 ) ) {
      Jira.setTickets( "ANALYZER-2924" );
      softAssert.fail( "The panels did not resize properly after the browser's width and height were decreased!" );
    }

    // Resize the browser so that it is larger.
    softAssert.assertTrue( reportPage.resizeBrowser( 900, 900 ),
        "The panels did not resize properly after the browser's width and height were increased!" );

    reportPage.maximizeBrowser();

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62527" )
  public void increasePanelSize() {
    // Recreate the report because the previous step causes a failure from a known issue.
    reportPage = report.create( true );

    SoftAssert softAssert = new SoftAssert();

    // Verify the available fields panel resizes properly.
    softAssert.assertTrue( reportPage.resizeFieldPanel( 300 ),
        "The available fields panel did not increase in width when attempting to resize!" );

    // Verify the layout panel resizes properly.
    softAssert.assertTrue( reportPage.resizeLayoutPanel( 300 ),
        "The layout panel did not increase in width when attempting to resize!" );

    softAssert.assertAll();

    reportPage.resetReport();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62528" )
  public void decreaseLayoutPanelSize() {
    SoftAssert softAssert = new SoftAssert();

    // Verify the layout panel resizes properly.
    softAssert.assertTrue( reportPage.resizeLayoutPanel( -110 ),
        "The layout panel did not decrease in width when attempting to resize!" );

    softAssert.assertAll();

    reportPage.resetReport();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62529" )
  public void decreaseFieldsPanelSize() {
    SoftAssert softAssert = new SoftAssert();

    // Verify the available fields panel resizes properly.
    softAssert.assertTrue( reportPage.resizeFieldPanel( -90 ),
        "The available fields panel did not decrease in width when attempting to resize!" );

    softAssert.assertTrue( reportPage.setFieldListHorizScrollPos( 10 ),
        "The horizontal scrollbar in the available fields list failed to scroll to the right! The scrollbar may not be present." );

    // Reset the scroll position.
    reportPage.resetFieldListHorizScrollPos();

    // Check known issue (JIRA# ANALYZER-2995). This cannot be replicated when using Firefox with Selenium (even
    // though it can be replicated manually).
    // Get the current scrollbar position.
    int originalScrollPos = reportPage.getFieldListHorizScrollPos();

    // Drag and drop a field into the report.
    reportPage.fieldDragAndDrop( "Sales", PanelItem.LAYOUT_MEASURES );

    // Get the current scrollbar position to compare to the original position.
    int newScrollPos = reportPage.getFieldListHorizScrollPos();

    if ( newScrollPos != originalScrollPos ) {
      Jira.setTickets( "ANALYZER-2995" );
      softAssert.fail(
          "Dragging and dropping a field onto the report incorrectly caused the available fields list to scroll to the right!" );
    }

    softAssert.assertAll();

    reportPage.resetReport();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62530" )
  public void preservePanels() {
    Folder folder = (Folder) BrowseService.getBrowseItem( reportPath );
    // The known issue in the previous test method can only be replicated in the Chrome browser in Selenium.
    if ( getDriver().toString().contains( "chrome" ) ) {
      reportPage = report.create( true );
    }

    // Hide all panels.
    reportPage.hideAvailableFieldsPanel();
    reportPage.hideLayoutPanel();
    reportPage.hideFilterPanel();

    // Save, close, and reopen the report.
    report.save( folder );
    report.close();
    report.open();

    // Verify that all panels are still hidden.
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertFalse( reportPage.isAvailableFieldsPanelPresent(),
        "The report did not preserve the available field panel's closed state after reopening the report!" );

    softAssert.assertFalse( reportPage.isLayoutPanelPresent(),
        "The report did not preserve the layout panel's closed state after reopening the report!" );

    softAssert.assertFalse( reportPage.isFilterPanelPresent(),
        "The report did not preserve the filter panel's closed state after reopening the report!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62557" )
  public void verifyCloseWithoutSaving() {
    reportPage.showAvailableFieldsPanel();
    reportPage.showLayoutPanel();
    report.addFields();

    // Attempt to close the report without saving to show the warning dialog regarding unsaved content.
    reportPage.clickClose( reportPage.getName() );
    Assert.assertTrue( reportPage.isUnsavedContentDialogPresent(),
        "The unsaved content dialog window warning the user that continuing will cause changes to be lost did not appear!" );

    reportPage.clickOK();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( path = "XLS_data/PUC_DataProvider.xls", sheet = csvDataSourceSheet, dsUid = "name",
      executeColumn = "TUID", executeValue = "DSCSV03" )
  @SpiraTestSteps( testStepsId = "62531" )
  public void createCsvDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );
    csvDataSource.create();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PAR22" )
  @SpiraTestSteps( testStepsId = "62531" )
  public void verifyAddFieldRefresh( Map<String, String> args ) {
    report = new PAReport( args );

    reportPage = report.create( true );

    // Disable auto refresh so that the refresh indicator does not appear when adding fields.
    reportPage.disableAutoRefresh();

    // Add all fields that are specified in the data source.
    report.addFields();

    // Add all fields to the report, and then refresh the report.
    reportPage.refreshReportBySpecialButton();

    // Verify that the refresh report elements are present. Timeouts are reduced so that there is time to check all
    // elements if one doesn't exist.
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( reportPage.isReportRefreshIconPresent(),
        "The refresh report icon did not appear when refreshing the report!" );
    softAssert.assertTrue( reportPage.isReportRefreshTextPresent(),
        "The refresh report text did not appear when refreshing the report!" );
    softAssert.assertTrue( reportPage.isReportRefreshCancelPresent(),
        "The cancel refresh button did not appear when refreshing the report!" );

    report.close();

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PAR16" )
  @SpiraTestSteps( testStepsId = "62533" )
  public void verifyCategoryAccordion( Map<String, String> args ) {
    report = new PAReport( args );
    reportPage = report.create( true );

    SoftAssert softAssert = new SoftAssert();

    // Collapse categories.
    softAssert.assertTrue( report.collapseAllCategories(),
        "One or more categories in the available fields panel failed to collapse!" );

    // Expand categories.
    softAssert.assertTrue( report.expandAllCategories(),
        "One or more categories in the available fields panel failed to expand!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62534, 62535" )
  public void searchFields() {
    SoftAssert softAssert = new SoftAssert();
    String textToFind = "Terr";
    String fieldToFind = "Territory";

    // Get a list of all available fields before using the search feature.
    List<String> fieldsBeforeSearch = reportPage.getAllAvailableFields();

    // TS062534: Search the available fields and retrieve an updated list of available fields.
    List<String> fieldsAfterSearch = reportPage.searchFields( textToFind );

    // Only one field should be found.
    softAssert.assertEquals( fieldsAfterSearch.size(), 1,
        "An unexpected number of fields were found after searching for '" + textToFind + "'! Found: "
            + fieldsAfterSearch.size() + ", Expected: 1. Fields found: " + fieldsAfterSearch );

    // Verify that the Territory field was found.
    if ( fieldsAfterSearch.size() > 0 ) {
      softAssert.assertTrue( fieldsAfterSearch.contains( fieldToFind ), "Did not find the field '" + fieldToFind
          + "' after searching with the text '" + textToFind + "'! Fields found: " + fieldsAfterSearch );
    }

    // TS062535: Click the clear search button and retrieve an updated list of available fields.
    reportPage.clearSearch();
    fieldsAfterSearch = reportPage.getAllAvailableFields();

    // The available fields before the search and after the search is cleared should be the same.
    softAssert.assertEquals( fieldsBeforeSearch.size(), fieldsAfterSearch.size(),
        "The number of available fields before the search and the number of available fields after the search was cleared does not match. Number before search: "
            + fieldsBeforeSearch.size() + ", Number after search: " + fieldsAfterSearch.size() );

    softAssert.assertTrue( fieldsAfterSearch.containsAll( fieldsBeforeSearch ),
        "The list of available fields after the search was cleared does not match the list of available fields before the search. Fields before search: "
            + fieldsBeforeSearch + ". Fields after search: " + fieldsAfterSearch );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62536, 62537" )
  public void hoverOverAvailableFields() {
    SoftAssert softAssert = new SoftAssert();

    // Verify the background color and tooltip for each available field in the crystal theme.
    verifyAvailableFieldHover( Theme.CRYSTAL, softAssert );

    // Close the report before changing the theme, otherwise a prompt from the browser will appear.
    report.close();
    reportPage.setThemeOnyx();

    reportPage = report.create( true );

    // Known issue occurs on both themes. Redo the search after switching to the onyx theme to attempt to replicate the
    // issue again.
    reportPage.searchFields( "Terr" );
    reportPage.clearSearch();

    // Verify the background color and tooltip for each available field in the onyx theme.
    verifyAvailableFieldHover( Theme.ONYX, softAssert );

    report.close();
    reportPage.setThemeCrystal();

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62538" )
  public void verifyTooltips() {
    reportPage = report.create( true );

    SoftAssert softAssert = new SoftAssert();

    softAssert.assertTrue( report.verifyTooltips(), "One or more tooltips in the report are incorrect!" );

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62539" )
  public void verifyAvailableFieldsReorder() {
    SoftAssert softAssert = new SoftAssert();

    List<String> originalFields = reportPage.getAllAvailableFields();

    String fieldToReorder = originalFields.get( 0 );
    String fieldDestination = originalFields.get( 1 );

    // Attempt to change the order of available fields by dragging and dropping into a different category.
    reportPage.reorderAvailableField( fieldToReorder, fieldDestination );

    List<String> newFields = reportPage.getAllAvailableFields();

    softAssert.assertTrue( newFields.equals( originalFields ),
        "The available fields list was incorrectly able to be reordered by dragging and dropping a field into a separate category!" );

    softAssert.assertAll();

    report.close();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "62544, 62545, 62546" )
  public void verifyAlignmentFilterHeightAndBgColor() {
    SoftAssert softAssert = new SoftAssert();

    // Create the report with default rows/measures.
    reportPage = report.create();

    // Show the filter panel and retrieve its default height value.
    reportPage.showFilterPanel();
    int filterHeightOriginal = reportPage.getFilterPanelHeight();

    List<String> fields = reportPage.getLayoutAttributes();
    List<String> measures = reportPage.getLayoutMeasures();

    // For every row, verify its column header text alignment and create a filter.
    for ( String field : fields ) {
      // TS062544: Verify column header alignment.
      softAssert.assertTrue( report.isColumnHeaderProperlyAligned( field, false ), "The column header for the field '"
          + field + "' is not properly aligned!" );

      // TS062545: create a filter for each field to verify the height of the filter panel increases.
      PAFilter filter = new PAFilter( field );
      filter.setValue( reportPage.getPivotTableRowValues( field ).get( 0 ) );
      // The columns added should only be levels that are not measures nor time.
      filter.setCondition( Condition.CONTAINS );
      filter.create( Workflow.CONTEXT_LAYOUT );

      // TS062546: verify the background color when hovering over the layout field.
      String color = reportPage.getLayoutAttributeHoverColor( field );
      LOGGER.info( "Color found for element: " + color );

      // [ML] Added a pause because the verification was occurring before the action was finished.
      pause( 1 );

      boolean isValidColor;

      if ( report.getRows().contains( field ) ) {
        isValidColor = report.verifyLayoutRowHoverColor( color );
      } else {
        isValidColor = report.verifyLayoutColumnHoverColor( color );
      }

      softAssert.assertTrue( isValidColor, "The background color of the layout field '" + field + "' is incorrect!" );
    }

    // Get the updated filter panel height.
    int filterHeightNew = reportPage.getFilterPanelHeight();

    // Make sure that the filter panel's height increased.
    softAssert.assertTrue( filterHeightOriginal < filterHeightNew,
        "The filter panel's height did not increase after several filters that should cause the text to exceed the panel's height!" );

    // Verify the column header text alignment for each measure.
    for ( String measure : measures ) {
      // TS062544: Verify column header alignment.
      softAssert.assertTrue( report.isColumnHeaderProperlyAligned( measure, true ),
          "The column header for the measure '" + measure + "' is not properly aligned!" );

      // TS062546: verify the background color when hovering over the layout measure.
      String color = reportPage.getLayoutMeasureHoverColor( measure );
      softAssert.assertTrue( report.verifyLayoutMeasureHoverColor( color ), "The background color of the layout field '"
          + measure + "' is incorrect!" );
    }

    softAssert.assertAll();

    report.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR16_EDIT_2" )
  @SpiraTestSteps( testStepsId = "62547" )
  public void verifyChartTooltips( Map<String, String> args ) {
    SoftAssert softAssert = new SoftAssert();

    report = new PAReport( args );
    reportPage = report.create();

    // Retrieve the values from the pivot table, which will be used to compare to the tooltips in the chart.
    List<String> valuesTerritory = reportPage.getPivotTableRowValues( "Territory" );
    List<String> valuesMeasures = reportPage.getPivotTableAllMeasureValues();
    List<String> valuesQuantity = new ArrayList<String>();
    List<String> valuesSales = new ArrayList<String>();

    // Parse the measure values by column. There is currently no way to determine which measures belong to which columns
    // without hard-coding their position in the pivot table.
    for ( int i = 0; i < valuesMeasures.size(); i++ ) {
      if ( i % 2 == 0 ) {
        valuesQuantity.add( valuesMeasures.get( i ) );
      } else {
        valuesSales.add( valuesMeasures.get( i ) );
      }
    }

    ScatterChart scatterChart = (ScatterChart) reportPage.changeChartType( ChartType.SCATTER );

    // Loop through each data point, retrieve each tooltip, and compare the expected and actual tooltips.
    for ( int i = 0; i < scatterChart.getBlockElementSize(); i++ ) {
      String expectedTooltip =
          "Territory: " + valuesTerritory.get( i ) + "\nQuantity: " + valuesQuantity.get( i ) + "\nSales: "
              + valuesSales.get( i ) + "\n" + Utils.formatRemovePercent( L10N.getText( "chartTooltipFooterDrillDown" ),
                  "Country" );

      String actualTooltip = scatterChart.getBlockElementTooltip( i );

      softAssert.assertEquals( actualTooltip, expectedTooltip,
          "The tooltip for the data point does not match the expected tooltip!\nExpected:\n" + expectedTooltip
              + "\nFound:\n" + actualTooltip );
    }

    softAssert.assertAll();

    report.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR16_EDIT_3" )
  @SpiraTestSteps( testStepsId = "62548, 62549, 62550, 62551" )
  public void verifyGemBarDropIndicators( Map<String, String> args ) {
    SoftAssert softAssert = new SoftAssert();

    GemBar gemBarRows = null;
    GemBar gemBarColumns = null;
    GemBar gemBarMeasures = null;
    boolean isMeasure = false;

    report = new PAReport( args );
    reportPage = report.create( true );

    // Verify the background color for each gem bar.
    for ( GemBar gemBar : ChartType.PIVOT.getGemBars() ) {
      List<String> fields = new ArrayList<String>();

      // Retrieve the fields for the current gem bar.
      switch ( gemBar.getType() ) {
        case ROWS:
          gemBarRows = gemBar;
          fields = report.getRows();
          isMeasure = false;
          break;

        case COLUMNS:
          gemBarColumns = gemBar;
          fields = report.getColumns();
          isMeasure = false;
          break;

        case MEASURES:
          gemBarMeasures = gemBar;
          fields = report.getMeasures();
          isMeasure = true;
          break;

        default:
          Assert.fail( "Unexpected gem type was found in the pivot table: " + gemBar.getLabel() );
          break;
      }

      // Loop through each field to verify the gem bar's background color.
      for ( String field : fields ) {
        // Click and hold the current field, then drag it to the current gem bar.
        reportPage.dragAvailableFieldToGemBar( field, gemBar );

        String color = reportPage.getGemBarBackgroundColor( gemBar );

        // Release the mouse to add the field to the report.
        Utils.mouseRelease();

        // TS062548: Verify the background color.
        boolean isValidColor;

        if ( isMeasure ) {
          isValidColor = report.verifyLayoutMeasureContainerHoverColor( color );
        } else {
          isValidColor = report.verifyLayoutRowOrColumnContainerHoverColor( color );
        }
        softAssert.assertTrue( isValidColor, "The background color of the gem bar '" + gemBar.getLabel()
            + "' is incorrect when a field from the available field list is hovered over it!" );

        // TS062551: Verify the cursor when hovering over a field header in the pivot table.
        String cursorActual = reportPage.getColumnHeaderCursor( field );
        String cursorExpected = "pointer";

        if ( !cursorActual.equals( cursorExpected ) ) {
          Jira.setTickets( "ANALYZER-3093" );
          softAssert.fail(
              "The cursor that is used when hovering over a field on the pivot table is not the expected cursor type! Field: '"
                  + field + "', Expected Cursor: '" + cursorExpected + "', Actual Cursor: '" + cursorActual + "'" );
        }
      }
    }

    // There should be at least one field in each layout container.
    // Get the first field for rows and columns. These will be used for testing changes to layout and order of fields.
    String row = report.getRows().get( 0 );
    String column = report.getColumns().get( 0 );

    // Move a row to the columns section to verify the background color when moving fields between layout sections.
    report.moveFieldToLayoutContainer( row, gemBarRows, gemBarColumns );

    // Change the order of the fields within the columns container.
    report.reorderFieldInLayoutContainer( row, column, true, gemBarColumns );
    report.reorderFieldInLayoutContainer( row, column, false, gemBarColumns );

    // Move both fields to the rows container.
    report.moveFieldToLayoutContainer( row, gemBarColumns, gemBarRows );
    report.moveFieldToLayoutContainer( column, gemBarColumns, gemBarRows );

    // Change the order of the fields within the rows container.
    report.reorderFieldInLayoutContainer( row, column, false, gemBarRows );
    report.reorderFieldInLayoutContainer( row, column, true, gemBarRows );

    // There should be at least two measures in the Measures layout container.
    // Get the first two fields. These will be used for testing changes to the order of measures.
    String firstMeasure = report.getMeasures().get( 0 );
    String secondMeasure = report.getMeasures().get( 1 );

    report.reorderFieldInLayoutContainer( firstMeasure, secondMeasure, false, gemBarMeasures );
    report.reorderFieldInLayoutContainer( firstMeasure, secondMeasure, true, gemBarMeasures );

    softAssert.assertAll();

    report.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportSheet, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR16_EDIT_4" )
  @SpiraTestSteps( testStepsId = "62552, 62553" )
  public void verifyEditFieldDialog( Map<String, String> args ) {
    report = new PAReport( args );
    reportPage = report.create();

    // Get the first field in the rows container change its display name.
    String field = report.getRows().get( 0 );
    String displayName = field + "1";
    LOGGER.info( "Changing the display name for the field '" + field + " ' to '" + displayName + "'." );
    report.setLevelDisplayName( field, displayName );

    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( reportPage.isLayoutItemPresent( displayName ),
        "The display name was not updated in the layout panel!" );
    softAssert.assertTrue( reportPage.isColumnHeaderPresent( displayName ),
        "The display name was not updated in the pivot table!" );
    softAssert.assertAll();
  }

  /**
   * Determines whether or not the specified list of strings is in alphabetical order.
   * 
   * @param strings
   *          The list of strings to verify.
   * @return Returns true when the list is in alphabetical order.
   */
  private boolean isAlphabetical( List<String> strings ) {
    String previousText = "";
    boolean isAlphabetical = true;

    for ( String currentText : strings ) {
      // Only compare strings when beyond the first element.
      if ( !Strings.isNullOrEmpty( previousText ) ) {
        if ( previousText.compareToIgnoreCase( currentText ) > 0 ) {
          isAlphabetical = false;
          break;
        }
      }

      previousText = currentText;
    }

    return isAlphabetical;
  }

  /**
   * Verifies that the specified measures exist in the available fields panel of the report.
   * 
   * @param softAssert
   *          The value of the SoftAssert to be appended.
   * @param measures
   *          The list of measures that are expected in the available fields panel.
   * @param stepID
   *          The ID of the SpiraTest step.
   * @return Returns the appended SoftAssert.
   */
  private SoftAssert verifyMeasures( SoftAssert softAssert, List<String> measures, String stepID ) {
    // Retrieve the available measure fields.
    List<String> fields = reportPage.getAllAvailableFields( "Measures" );

    // Verify that the number of measures expected matches the number of measures found.
    softAssert.assertTrue( measures.size() == fields.size(), stepID
        + ": Unexpected number of measure fields are in the available field list! Expected " + measures.size()
        + ", but found " + fields.size() + "." );

    // Verify that the expected measures are the same as the measures found.
    LOGGER.info( "Checking for the following measure fields: " + measures );
    softAssert.assertTrue( measures.containsAll( fields ), stepID
        + ": Unexpected measure fields are in the available field list! Expected: [" + measures + "], but found: ["
        + fields + "]." );

    return softAssert;
  }

  /**
   * This method verifies that the specified chart type has the correct gem bar requirement flag, the correct icon, and
   * the correct class for the drop area container.
   * 
   * @param chartType
   *          The chart type to verify.
   * @param softAssert
   *          The value of the SoftAssert to be appended.
   * @return Returns the appended SoftAssert.
   */
  private SoftAssert verifyGemBars( ChartType chartType, SoftAssert softAssert ) {
    String measure = reportPage.getAllMeasures().get( 0 );
    String level = reportPage.getAllLevels().get( 0 );

    for ( GemBar gemBar : chartType.getGemBars() ) {
      // Verify required and non-required fields are correctly flagged.
      softAssert.assertTrue( reportPage.verifyGemBarMemberRequired( gemBar ), "Gem bar '" + gemBar.getLabel()
          + "' required/not required indicator was not found for chart type id '" + chartType.getId() + "'!" );

      // Verify that the correct icon exists for the gem bar.
      softAssert.assertTrue( reportPage.verifyGemBarIcon( gemBar ), "The icon for gem bar '" + gemBar.getLabel()
          + "' was not found!" );

      // Verify that the correct class is used for the gem bar drop area container.
      softAssert.assertTrue( reportPage.verifyGemBarDropContainer( gemBar ), "The drop area container for gem bar '"
          + gemBar.getLabel() + "' was not found!" );

      // If the gem bar is required, make sure that the required flags are initially present, and are not present after
      // adding a field.
      if ( gemBar.isRequired() ) {
        softAssert.assertTrue( report.isGemBarRequiredFlagPresent( gemBar ), "The gem bar '" + gemBar.getLabel()
            + "' is required, but is missing its required flag in the GUI!" );

        // Add a field to the gem bar.
        String field = gemBar.getDndType() == GemBarDndType.MEASURE ? measure : level;
        reportPage.addFieldToSection( gemBar.getType(), field );

        // Verify that the required flags are no longer present.
        softAssert.assertFalse( report.isGemBarRequiredFlagPresent( gemBar ), "The gem bar '" + gemBar.getLabel()
            + "' still has its required flag present after a field has been added to it!" );

        // Remove the added fields. This method is used since it does not require a field name. Some fields have a
        // number in parenthesis that is shown in the available field panel, but is not shown in the layout panel.
        report.removeAllFields();

      } else {
        softAssert.assertFalse( report.isGemBarRequiredFlagPresent( gemBar ), "The gem bar '" + gemBar.getLabel()
            + "' is not required, but incorrectly has a required flag in the GUI!" );
      }
    }

    return softAssert;
  }

  /**
   * Hovers over each available field and verifies that its background color changes accordingly.
   * 
   * @param crystalTheme
   *          A value of true indicates that the crystal theme is used. A value of false indicates that the onyx theme
   *          is used.
   * @param softAssert
   *          The value of the SoftAssert to be appended.
   * @return Returns the appended SoftAssert.
   */
  private SoftAssert verifyAvailableFieldHover( Theme theme, SoftAssert softAssert ) {
    String themeName = theme.equals( Theme.CRYSTAL ) ? "crystal" : "onyx";

    String previousField = "";

    for ( String field : reportPage.getAllAvailableFields() ) {
      reportPage.hoverAvailableField( field );

      // TS062536: verify the background color is correct for each theme when hovering over the available field.
      String hoverColor = reportPage.getAvailableFieldColor( field );

      if ( !report.verifyAvailableFieldHoverColor( hoverColor, theme ) ) {
        Jira.setTickets( "ANALYZER-3333" );
        softAssert.fail( "The background color for the hover state for the available field '" + field
            + "' is incorrect for the " + themeName + " theme! Found: " + hoverColor );
      }

      softAssert.assertEquals( reportPage.getTooltipAvailableField( field ), field, "The tooltip for the '" + field
          + "' available field is incorrect for the " + themeName + " theme!" );

      // Verify the background color changed back after hovering over the field.
      if ( !Strings.isNullOrEmpty( previousField ) ) {
        String color = reportPage.getAvailableFieldColor( previousField );
        softAssert.assertTrue( report.verifyAvailableFieldColor( color, theme ),
            "The background color did not restore to its original color after the mouse stopped hovering over the available field '"
                + previousField + "' for the " + themeName + " theme! Found: " + color );
      }

      previousField = field;
    }

    return softAssert;
  }
}
