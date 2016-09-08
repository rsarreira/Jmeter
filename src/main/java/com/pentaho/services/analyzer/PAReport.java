package com.pentaho.services.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.Dimension;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPanelPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.PanelItem;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarDndType;
import com.pentaho.qa.gui.web.puc.BasePage.Theme;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.IReport;
import com.pentaho.services.Report;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.utils.ExportType;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class PAReport extends Report {
  private static final String ARG_ROWS = "Rows";
  private static final String ARG_COLUMNS = "Columns";
  private static final String ARG_MEASURES = "Measures";

  protected boolean autoRefresh;
  protected List<String> rows = new ArrayList<String>();
  protected List<String> columns = new ArrayList<String>();
  protected List<String> measures = new ArrayList<String>();
  protected List<PAFilter> filters = new ArrayList<PAFilter>();

  private AnalyzerFilterPanelPage filterPanelPage;

  public SoftAssert softAssert = new SoftAssert();

  public PAReport( Map<String, String> args ) {
    // TODO: launch "this (...)" to remove duplicates
    super( args );

    if ( name != null && name.isEmpty() ) {
      name = AnalyzerReportPage.NEW_ANALYSIS_REPORT_NAME;
    }

    autoRefresh = Boolean.valueOf( args.get( "AutoRefresh" ) );

    if ( args.get( ARG_ROWS ) != null && !args.get( ARG_ROWS ).isEmpty() ) {
      rows = Arrays.asList( args.get( ARG_ROWS ).split( "," ) );
    }
    if ( args.get( ARG_COLUMNS ) != null && !args.get( ARG_COLUMNS ).isEmpty() ) {
      columns = Arrays.asList( args.get( ARG_COLUMNS ).split( "," ) );
    }
    if ( args.get( ARG_MEASURES ) != null && !args.get( ARG_MEASURES ).isEmpty() ) {
      measures = Arrays.asList( args.get( ARG_MEASURES ).split( "," ) );
    }

  }

  public PAReport( String name, Boolean hidden, Type type, Boolean allowScheduling, String dataSource, String id ) {
    super( name, hidden, type, allowScheduling, "", dataSource, id );

    if ( name != null && name.isEmpty() ) {
      name = AnalyzerReportPage.NEW_ANALYSIS_REPORT_NAME;
    }

    // TODO: implement setters for below params as well
    /*
     * autoRefresh = Boolean.valueOf( args.get( "AutoRefresh" ) );
     * 
     * if ( args.get( ARG_ROWS ) != null && !args.get( ARG_ROWS ).isEmpty() ) { rows = Arrays.asList( args.get( ARG_ROWS
     * ).split( "," ) ); } if ( args.get( ARG_COLUMNS ) != null && !args.get( ARG_COLUMNS ).isEmpty() ) { columns =
     * Arrays.asList( args.get( ARG_COLUMNS ).split( "," ) ); } if ( args.get( ARG_MEASURES ) != null && !args.get(
     * ARG_MEASURES ).isEmpty() ) { measures = Arrays.asList( args.get( ARG_MEASURES ).split( "," ) ); }
     */

  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( PAReport report ) {
    super.copy( report );

    this.autoRefresh = report.autoRefresh; // it is always defined

    if ( report.getRows() != null && !report.getRows().isEmpty() )
      this.rows = report.getRows();

    if ( report.getColumns() != null && !report.getColumns().isEmpty() )
      this.columns = report.getColumns();

    if ( report.getMeasures() != null && !report.getMeasures().isEmpty() )
      this.columns = report.getColumns();
  }

  public boolean isAutoRefresh() {
    return autoRefresh;
  }

  public void setAutoRefresh( boolean autoRefresh ) {
    this.autoRefresh = autoRefresh;
  }

  public List<String> getRows() {
    return rows;
  }

  public void setRows( List<String> rows ) {
    this.rows = rows;
  }

  public List<String> getColumns() {
    return columns;
  }

  public void setColumns( List<String> columns ) {
    this.columns = columns;
  }

  public List<String> getMeasures() {
    return measures;
  }

  public void setMeasures( List<String> measures ) {
    this.measures = measures;
  }

  public List<PAFilter> getFilters() {
    return filters;
  }

  public void setFilters( List<PAFilter> filters ) {
    this.filters = filters;
  }

  public AnalyzerFilterPanelPage getFilterPanelPage() {
    return filterPanelPage;
  }

  public void setFilterPanelPage( AnalyzerFilterPanelPage filterPanelPage ) {
    this.filterPanelPage = filterPanelPage;
  }

  public void setReportPage( AnalyzerReportPage reportPage ) {
    super.setPage( reportPage );
  }

  public AnalyzerReportPage getReportPage() {
    return (AnalyzerReportPage) super.getPage();
  }

  public AnalyzerReportPage open() {
    super.open();

    setReportPage( new AnalyzerReportPage( getDriver(), name ) );
    return getReportPage();
  }

  public AnalyzerReportPage create() {
    return create( false );
  }

  public AnalyzerReportPage create( boolean empty ) {
    return create( empty, false );
  }

  public AnalyzerReportPage create( boolean empty, boolean checkDefaults ) {
    // Create Analysis report
    HomePage homePage = new HomePage( getDriver() );
    AnalyzerDataSourcePage selectDataSourcePage = homePage.openAnalyzerDataSourcePage();

    // Select specified Data Source
    AnalyzerReportPage reportPage = selectDataSourcePage.selectDataSource( getDataSource() );
    reportPage.switchToFrame();
    super.setPage( reportPage );

    // Verify default settings for the new report.
    if ( checkDefaults ) {
      Assert.assertTrue( reportPage.isDefaultViewValid(), "Default view is invalid!" );
    }

    // Create the report without adding rows, columns, and measures.
    if ( !empty ) {
      getReportPage().addFields( rows, columns, measures );
    }

    return getReportPage();
  }

  /**
   * Adds all rows, columns, and measures to the report. These fields are added in the constructor or by using the
   * setRows, setColumns, and setMeasures methods.
   */
  public void addFields() {
    LOGGER.info( "Adding all rows, columns, and measures defined in the data provider." );
    getReportPage().addFields( rows, columns, measures );
  }

  public AnalyzerReportPage openDirectly( String url ) {
    HomePage homePage = new HomePage( getDriver() );
    // add parameter to url for localization, ensure it is in lower case.
    String language = Configuration.get( Parameter.LOCALE ).toLowerCase();
    if ( url.contains( "?" ) ) {
      url += "&locale=" + language;
    } else {
      url += "?locale=" + language;
    }

    homePage.openURL( url );

    return new AnalyzerReportPage( getDriver(), name );
    // return getReportPage();
  }

  @Override
  public void edit( IReport newReport ) {
    super.edit( newReport ); // put original report state into the ObjectPool

    // TODO: implement edit logic for different Analysis Report fields
    copy( (PAReport) newReport );
  }

  /* -------------- SAVE REPORT --------------- */

  public PAReport saveAs( Folder folder ) {
    return (PAReport) super.saveAs( folder );
  }

  @Override
  public void sort( Layout layout, String field, Sort sort, Workflow workflow ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sorterUp( Layout layout, String field ) {
    // TODO Auto-generated method stub

  }

  @Override
  public void sorterDown( Layout layout, String field ) {
    // TODO Auto-generated method stub

  }

  @Override
  protected Object clone() {
    PAReport cloned;
    cloned = (PAReport) super.clone();

    // TODO: verify that simple boolean is cloned successfully for autoRefresh;

    List<String> rowsTmp = new ArrayList<String>();
    rowsTmp.addAll( this.rows );

    List<String> columnsTmp = new ArrayList<String>();
    columnsTmp.addAll( this.columns );

    List<String> measuresTmp = new ArrayList<String>();
    measuresTmp.addAll( this.measures );

    cloned.setRows( rowsTmp );
    cloned.setColumns( columnsTmp );
    cloned.setMeasures( measuresTmp );
    return cloned;
  }

  public void exportAs( ExportType exportType ) {
    // String fileName = getName() + "." + exportType.getExtension();
    String fileName = "default." + exportType.getExtension();
    Utils.clearDownloadsFile( fileName );
    AnalyzerReportPage paReportPage = getReportPage();

    paReportPage.exportAs( exportType );
    // to be sure OS has managed to persist file on disc

    boolean res = false;
    for ( int i = 0; i < 3; i++ ) {
      if ( Utils.checkDownloadsFile( fileName ) ) {
        res = true;
      } else {
        paReportPage.pause( 5 ); // simple delay
      }
    }
    Utils.clearDownloadsFile( fileName );

    if ( !res ) {
      Assert.fail( "No file downloaded: " + fileName );
    }
  }

  /* -------------- FILTER RELATED OPERATIONS ------------------------- */
  public SoftAssert verifyFilters() {

    for ( PAFilter filter : filters ) {
      if ( !filterPanelPage.isFilterExists( filter ) ) {
        softAssert.fail( "Filter '" + filter.toString() + "' is not displayed on Filter panel!" );
      }
    }
    return softAssert;
  }

  public void addFilter( PAFilter filter, Workflow worflow ) {
    filter.create( worflow );
    filterPanelPage = new AnalyzerFilterPanelPage( getDriver() );
    filters.add( filter );
  }

  public void addFilter( PAFilter filter ) {
    addFilter( filter, Workflow.CONTEXT_PANEL );
  }

  public AnalyzerFilterPage editFilter( PAFilter filter, PAFilter newFilter ) {
    return filter.edit( newFilter );
  }

  public void deleteFilter( PIRFilter filter ) {
    filters.remove( filter );
    filter.delete();
  }

  /**
   * Collapses all categories in the available fields panel.
   * 
   * @return Returns true if all categories collapsed successfully.
   */
  public boolean collapseAllCategories() {
    boolean isValid = true;
    AnalyzerReportPage reportPage = getReportPage();

    for ( String category : reportPage.getAvailableCategories() ) {
      // Flag as invalid if one or more categories fail to collapse.
      if ( !reportPage.collapseCategory( category ) ) {
        isValid = false;
      }
    }

    return isValid;
  }

  /**
   * Expands all categories in the available fields panel.
   * 
   * @return Returns true if all categories expanded successfully.
   */
  public boolean expandAllCategories() {
    boolean isValid = true;
    AnalyzerReportPage reportPage = getReportPage();

    for ( String category : reportPage.getAvailableCategories() ) {
      if ( !reportPage.expandCategory( category ) ) {
        isValid = false;
      }
    }

    return isValid;
  }

  /**
   * Verifies various tooltips throughout the analyzer report page.
   * 
   * @return Returns true if all tooltips are correct.
   */
  public boolean verifyTooltips() {
    boolean isValid = true;

    AnalyzerReportPage reportPage = getReportPage();

    // Get the first measure in the report to be used with the undo/redo buttons. If no measures exist, then something
    // is wrong and a failure is acceptable.
    String measureName = reportPage.getAllMeasures().get( 0 );
    String action;

    // Verify tooltip for the data source name in the available field panel.
    String expectedTooltip = L10N.getText( "fieldTooltipNone" );
    String actualTooltip = reportPage.getCubeTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the data source name in the available fields panel is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the clear search button in the available field panel.
    reportPage.searchFields( "a" );

    expectedTooltip = L10N.getText( "ttClearSearch" );
    actualTooltip = reportPage.getClearSearchTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the clear search button in the available fields panel is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    reportPage.clearSearch();

    // Verify the tooltip for the undo button in the report toolbar.
    action = Utils.formatRemovePercent( L10N.getText( "actionAddMeasure" ), measureName );
    reportPage.addFieldsByDefault( Arrays.asList( measureName ) );

    expectedTooltip = Utils.formatRemovePercent( L10N.getText( "actionUndo" ), action );
    actualTooltip = reportPage.getUndoTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the undo button in the report toolbar is incorrect! Expected: '" + expectedTooltip
          + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    reportPage.undo();

    // Verify the tooltip for the redo button in the report toolbar.
    expectedTooltip = Utils.formatRemovePercent( L10N.getText( "actionRedo" ), action );
    actualTooltip = reportPage.getRedoTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the redo button in the report toolbar is incorrect! Expected: '" + expectedTooltip
          + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the auto refresh button when auto refresh is enabled.
    expectedTooltip = L10N.getText( "disableAutoRefresh" );
    actualTooltip = reportPage.getAutoRefreshTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the disable auto refresh button in the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the auto refresh button when auto refresh is disabled.
    reportPage.disableAutoRefresh();

    expectedTooltip = L10N.getText( "enableAutoRefresh" );
    actualTooltip = reportPage.getAutoRefreshTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the enable auto refresh button in the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    reportPage.enableAutoRefresh();

    // Verify the tooltip for the more actions and options button in the report toolbar.
    expectedTooltip = L10N.getText( "ttMoreAction" );
    actualTooltip = reportPage.getMoreActionsTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error(
          "The tooltip for the more actions and options button in the report toolbar is incorrect! Expected: '"
              + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the choose chart type drop-down in the report toolbar.
    expectedTooltip = L10N.getText( "ttChartType" );
    actualTooltip = reportPage.getChooseChartTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the choose chart type drop-down in the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the switch to chart button in the report toolbar.
    expectedTooltip = L10N.getText( "ttSwitchToChart" );
    actualTooltip = reportPage.getSwitchToChartTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the switch to chart button in the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the switch to table button in the report toolbar.
    expectedTooltip = L10N.getText( "ttSwitchToTable" );
    actualTooltip = reportPage.getSwitchToTableTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the switch to table button in the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the show/hide filters accordion below the report toolbar.
    expectedTooltip = L10N.getText( "ttShowHideFilters" );
    actualTooltip = reportPage.getFilterAccordionTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for show/hide filters accordion below the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the tooltip for the filter count text below the report toolbar. Expected tooltip does not need to change
    // from the previous element.
    actualTooltip = reportPage.getFilterAccordionTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for filter count text below the report toolbar is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the help on filters button within the filter panel.
    expectedTooltip = L10N.getText( "ttHelpOnFilters" );
    actualTooltip = reportPage.getFilterHelpTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the help on filters button within the filter panel is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    // Verify the hide filters button within the filter panel.
    expectedTooltip = L10N.getText( "ttHideFilterPane" );
    actualTooltip = reportPage.getFilterCloseTooltip();
    if ( !actualTooltip.equals( expectedTooltip ) ) {
      LOGGER.error( "The tooltip for the hide filter pane button within the filter panel is incorrect! Expected: '"
          + expectedTooltip + "', found '" + actualTooltip + "'." );
      isValid = false;
    }

    return isValid;
  }

  /**
   * Determines whether or not the flags that indicate a required gem bar are present.
   * 
   * @param gemBar
   *          The gem bar to check for the required flags.
   * @return Returns true when the required flags are present.
   */
  public boolean isGemBarRequiredFlagPresent( GemBar gemBar ) {
    boolean isPresent;

    AnalyzerReportPage reportPage = getReportPage();

    // Find the asterisk and its color next to the gem bar's text.
    String content = reportPage.getGemBarRequiredContent( gemBar );
    String color = reportPage.getGemBarRequiredColor( gemBar );

    // Verify that the asterisk exists and is red.
    if ( content.trim().equals( "*" ) && Colour.getColour( color ).equals( Colour.RED ) ) {
      isPresent = true;
      LOGGER.info( "Required flag is present for the gem bar '" + gemBar.getLabel() + "'." );
    } else {
      isPresent = false;
      LOGGER.info( "Required flag for the gem bar '" + gemBar.getLabel() + "' was not found!" );
    }

    return isPresent;
  }

  /**
   * Determines whether or not the text of the specified pivot table's column is properly aligned. Non-measures should
   * be aligned to the left, and measures should be centered.
   * 
   * @param columnName
   *          The name of the column in the pivot table.
   * @param isMeasure
   *          When true, the column is treated as a measure. Otherwise, the column is treated as a level.
   * @return Returns true when the specified column is properly aligned.
   */
  public boolean isColumnHeaderProperlyAligned( String columnName, boolean isMeasure ) {
    AnalyzerReportPage reportPage = getReportPage();
    String textAlign = reportPage.getColumnHeaderTextAlign( columnName ).toLowerCase();
    boolean isPropertyAligned;

    if ( ( isMeasure && textAlign.equals( "center" ) ) || ( !isMeasure && Arrays.asList( new String[] { "start",
      "left" } ).contains( textAlign ) ) ) {
      isPropertyAligned = true;
      LOGGER.info( "The text for column '" + columnName + "' is properly aligned." );
    } else {
      isPropertyAligned = false;
      LOGGER.error( "The text for column '" + columnName + "' is not properly aligned!" );
    }

    return isPropertyAligned;
  }

  /**
   * Removes all fields from all gem bars in the layout panel.
   */
  public void removeAllFields() {
    for ( String field : getReportPage().getLayoutFields() ) {
      getReportPage().moveFieldToTrash( field );
    }
  }

  /**
   * Moves the specified field from one layout container to another layout container.
   * 
   * @param field
   *          The field to be moved.
   * @param gemBarOrigin
   *          The layout container that the field is in.
   * @param gemBarDestination
   *          The layout container that the field is to be moved to.
   */
  public void moveFieldToLayoutContainer( String field, GemBar gemBarOrigin, GemBar gemBarDestination ) {
    // Drag the field to the destination without dropping it.
    getReportPage().dragLayoutFieldToGemBar( field, gemBarDestination );

    // Get the hover color and then release the mouse to drop the field.
    String color = getReportPage().getGemBarBackgroundColor( gemBarDestination );
    Utils.mouseRelease();

    // Verify the background color when hovered and that the field had switched layout containers.
    boolean isValidColor;
    if ( gemBarDestination.getDndType().equals( GemBarDndType.MEASURE ) ) {
      isValidColor = verifyLayoutMeasureHoverColor( color );
    } else {
      isValidColor = verifyLayoutRowOrColumnContainerHoverColor( color );
    }

    if ( !isValidColor ) {
      CustomAssert.fail( "62548", "The background color of the gem bar '" + gemBarDestination.getLabel()
          + "' is incorrect when a field is hovered over it!" );
    }

    // Verify that the field no longer exists in the original container.
    if ( getReportPage().getGemBarFields( gemBarOrigin ).contains( field ) ) {
      CustomAssert.fail( "62549", "The field '" + field + "' is still a row in the report, but should be a column!" );
    }

    // Verify that the field exists in the destination container.
    if ( !getReportPage().getGemBarFields( gemBarDestination ).contains( field ) ) {
      CustomAssert.fail( "62549", "The field '" + field + "' was not found as a column in the report!" );
    }
  }

  /**
   * Change the order of fields in the specified gem bar by moving a single field.
   * 
   * @param fieldOrigin
   *          The field to move.
   * @param fieldDestination
   *          The field to move to.
   * @param insertAboveDestination
   *          Determines where to insert the field: above or below the destination. When true, the field will be moved
   *          above the destination field.
   * @param gemBar
   *          The gem bar that contains the fields.
   */
  public void reorderFieldInLayoutContainer( String fieldOrigin, String fieldDestination,
      boolean insertAboveDestination, GemBar gemBar ) {
    List<String> fieldsOriginal = getReportPage().getGemBarFields( gemBar );

    // Drag the layout field above or below its destination field without dropping it.
    getReportPage().dragLayoutFieldToGemBar( fieldOrigin, gemBar, fieldDestination, insertAboveDestination );

    // Verify that the drop indicator is present.
    if ( !getReportPage().isGemBarDropIndicatorPresent( gemBar ) ) {
      CustomAssert.fail( "62550",
          "The drop indicator is not present when changing the order of fields in the layout panel!" );
    }

    // Get the background color of the container and release the mouse to drop the field.
    String color = getReportPage().getGemBarBackgroundColor( gemBar );
    Utils.mouseRelease();

    // Verify that the drop container is hidden after reordering the fields.
    if ( getReportPage().isGemBarDropIndicatorPresent( gemBar ) ) {
      CustomAssert.fail( "62550",
          "The drop indicator is still present after changing the order of fields in the layout panel!" );
    }

    List<String> fieldsUpdated = getReportPage().getGemBarFields( gemBar );

    // Verify the background color when hovered when changing the order of fields within the same gem bar.
    boolean isValidColor;
    if ( gemBar.getDndType().equals( GemBarDndType.MEASURE ) ) {
      isValidColor = verifyLayoutMeasureContainerHoverColor( color );
    } else {
      isValidColor = verifyLayoutRowOrColumnContainerHoverColor( color );
    }

    if ( !isValidColor ) {
      CustomAssert.fail( "62548", "The background color of the gem bar '" + gemBar.getLabel()
          + "' is incorrect when a field is hovered over it!" );
    }

    // Verify that the fields were reordered.
    if ( !fieldsUpdated.get( 0 ).equals( fieldsOriginal.get( 1 ) ) || !fieldsUpdated.get( 1 ).equals( fieldsOriginal
        .get( 0 ) ) ) {
      CustomAssert.fail( "62550", "The fields were not correctly reordered for the gem bar '" + gemBar.getLabel()
          + "'!" );
    }
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for the rows gem bar in the layout panel.
   * The hover color changes when hovering the mouse over a field within the gem bar.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @return Returns true when the specified color matches the expected color.
   */

  public boolean verifyLayoutRowHoverColor( String color, Theme theme ) {
    Colour expectedColor = PanelItem.LAYOUT_ROWS.getBackgroundColorHover( theme );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  public boolean verifyLayoutRowHoverColor( String color ) {
    return verifyLayoutRowHoverColor( color, getReportPage().getTheme() );
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for the columns gem bar in the layout
   * panel. The hover color changes when hovering the mouse over a field within the gem bar.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyLayoutColumnHoverColor( String color ) {
    Colour expectedColor = PanelItem.LAYOUT_COLUMNS.getBackgroundColorHover( getReportPage().getTheme() );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for the measures gem bar in the layout
   * panel. The hover color changes when hovering the mouse over a field within the gem bar.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyLayoutMeasureHoverColor( String color ) {
    Colour expectedColor = PanelItem.LAYOUT_MEASURES.getBackgroundColorHover( getReportPage().getTheme() );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  public boolean verifyAvailableFieldHoverColor( String color ) {
    return verifyAvailableFieldHoverColor( color, getReportPage().getTheme() );
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for the fields in the available fields
   * panel. The hover color changes when the mouse hovers over the field.
   * 
   * @param color
   *          The color to compare the expected color to.
   * 
   * @param theme
   *          The PUC theme that is currently in use.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyAvailableFieldHoverColor( String color, Theme theme ) {
    Colour expectedColor = PanelItem.AVAILABLE_FIELDS.getBackgroundColorHover( theme );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  public boolean verifyAvailableFieldColor( String color ) {
    return verifyAvailableFieldColor( color, getReportPage().getTheme() );
  }

  /**
   * Verifies that the specified color is the same as the expected background color for the field in the available
   * fields panel.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @param theme
   *          The PUC theme that is currently in use.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyAvailableFieldColor( String color, Theme theme ) {
    Colour expectedColor = PanelItem.AVAILABLE_FIELDS.getBackgroundColor( theme );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for attribute gem bars in the layout
   * panel. The hover color changes when a field is being dragged within the gem bar.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyLayoutRowOrColumnContainerHoverColor( String color ) {
    return verifyLayoutRowOrColumnContainerHoverColor( color, getReportPage().getTheme() );
  }

  public boolean verifyLayoutRowOrColumnContainerHoverColor( String color, Theme theme ) {
    Colour expectedColor = PanelItem.LAYOUT_SECTION_ATTRIBUTES.getBackgroundColorHover( theme );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  /**
   * Verifies that the specified color is the same as the expected hover color for measure gem bars in the layout panel.
   * The hover color changes when a field is being dragged within the gem bar.
   * 
   * @param color
   *          The color to compare the expected color to.
   * @return Returns true when the specified color matches the expected color.
   */
  public boolean verifyLayoutMeasureContainerHoverColor( String color ) {
    Colour expectedColor = PanelItem.LAYOUT_SECTION_MEASURES.getBackgroundColorHover( getReportPage().getTheme() );
    LOGGER.info( "Expecting Colour: " + expectedColor.getName() );
    return verifyColor( expectedColor, color );
  }

  /**
   * Compares the expected color to the actual color and logs the result.
   * 
   * @param expectedColor
   *          An instance of Colour that the actual color is expected to be.
   * @param actualColorRgb
   *          An RGB or RGBA value of the actual color found in the GUI.
   * @return Returns true if the actual color is the same as the expected color.
   */
  private boolean verifyColor( Colour expectedColor, String actualColorRgb ) {
    Colour actualColor = Colour.getColour( actualColorRgb );
    boolean isEqual = expectedColor.equals( actualColor );

    if ( isEqual ) {
      LOGGER.info( "Actual color matches the expected color." );
    } else {
      LOGGER.error( "Actual color does not match the expected color! Expected: " + expectedColor.getRgba()
          + ", Actual: " + actualColor.getRgba() );
    }

    return isEqual;
  }

  /**
   * Opens the field edit dialog for the specified field, and then changes its display name.
   * 
   * @param levelName
   *          The name of the field to modify.
   * @param displayName
   *          The new display name to set for the specified field.
   */
  public void setLevelDisplayName( String levelName, String displayName ) {
    AnalyzerReportPage reportPage = getReportPage();
    reportPage.openEditLevelDialog( levelName );

    if ( !reportPage.isLevelEditDialogPresent( levelName ) ) {
      CustomAssert.fail( "62552", "The edit dialog did not open for the level '" + levelName + "'!" );
    }

    reportPage.setLevelEditDialogDisplayName( displayName );
    reportPage.clickOkBtn();
    reportPage.pause( 1 );

    if ( reportPage.isLevelEditDialogPresent( levelName ) ) {
      CustomAssert.fail( "62553", "The edit dialog did not close after clicking the OK button!" );
    }
  }

  /**
   * Checks the report options button size and determines whether or not its width and height fall within the expected
   * range.
   * 
   * @return Returns true when both the width and the height fall within the expected range.
   */
  public boolean verifyReportOptionsBtnSize() {
    LOGGER.info( "Verifying report options button size..." );

    Dimension btnSize = getReportPage().getReportOptionsBtnSize();

    // Defines the minimum and maximum width/height allowed for the button.
    Dimension expectedSizeMin = new Dimension( 135, 25 );
    Dimension expectedSizeMax = new Dimension( 172, 35 );

    return verifyButtonSize( expectedSizeMin, expectedSizeMax, btnSize );
  }

  /**
   * Checks the chart options button size and determines whether or not its width and height fall within the expected
   * range.
   * 
   * @return Returns true when both the width and the height fall within the expected range.
   */
  public boolean verifyChartOptionsBtnSize() {
    LOGGER.info( "Verifying chart options button size..." );

    Dimension btnSize = getReportPage().getChartOptionsBtnSize();

    // Defines the minimum and maximum width/height allowed for the button.
    Dimension expectedSizeMin = new Dimension( 126, 25 );
    Dimension expectedSizeMax = new Dimension( 189, 35 );

    return verifyButtonSize( expectedSizeMin, expectedSizeMax, btnSize );
  }

  /**
   * Compares the expected size against the actual size of an element's width and height.
   * 
   * @param expectedSizeMin
   *          A Dimension that contains the minimum expected size of the element.
   * @param expectedSizeMax
   *          A Dimension that contains the maximum expected size of the element.
   * @param actualSize
   *          A Dimension that contains the actual size of the element.
   * @return Returns true when both the width and the height fall within the expected range.
   */
  private boolean verifyButtonSize( Dimension expectedSizeMin, Dimension expectedSizeMax, Dimension actualSize ) {
    // TODO: consider moving this method to a parent class.
    boolean isValidSize = true;

    int widthExpectedMin = expectedSizeMin.getWidth();
    int widthExpectedMax = expectedSizeMax.getWidth();
    int widthActual = actualSize.getWidth();
    if ( widthActual >= widthExpectedMin && widthActual <= widthExpectedMax ) {
      LOGGER.info( "The width of the button is within the expected range." );
    } else {
      isValidSize = false;
      LOGGER.error( "The width of the button is not within the expected range!" );
    }

    int heightExpectedMin = expectedSizeMin.getHeight();
    int heightExpectedMax = expectedSizeMax.getHeight();
    int heightActual = actualSize.getHeight();
    if ( heightActual >= heightExpectedMin && heightActual <= heightExpectedMax ) {
      LOGGER.info( "The height of the button is within the expected range." );
    } else {
      isValidSize = false;
      LOGGER.error( "The height of the button is not within the expected range!" );
    }

    return isValidSize;
  }

  /**
   * The edit content button is unavailable for analyzer reports.
   */
  @Override
  public void activateEditMode() {
    Assert.fail( "The edit content button is not available for analyzer reports." );
  }

  /**
   * The edit content button is unavailable for analyzer reports.
   */
  @Override
  public void activateViewMode() {
    Assert.fail( "The edit content button is not available for analyzer reports." );
  }
}
