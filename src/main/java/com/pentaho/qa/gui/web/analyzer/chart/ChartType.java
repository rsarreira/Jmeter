package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.List;

import com.pentaho.qa.gui.web.analyzer.GemBar;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarDndType;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;


/* The enum below uses the UI equivalent (or close to it) as the enum name, and the DOM equivalent as the first argument:
 * 
 * e.g. "<UI_FRIENDLY_NAME>( '<dom_id>', ..."
 * 
 * The enums are also ordered by their appearance in the chart drop down menu in PAR
 * 
 */
@SuppressWarnings( "serial" )
public enum ChartType {
  COLUMN( "ccc_bar", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_VERTICAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_VERTICAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_VERTICAL_BAR_NUM" ),
          true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), STACKED_COLUMN( "ccc_barstacked", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_STACKED_VERTICAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_STACKED_VERTICAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_STACKED_VERTICAL_BAR_NUM" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), NORMALIZED_STACKED_COLUMN( "ccc_barnormalized", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_PCT_STACKED_VERTICAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_PCT_STACKED_VERTICAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_PCT_STACKED_VERTICAL_BAR_NUM" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), COLUMN_LINE_COMBO( "ccc_barline", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_VERTICAL_BAR_LINE_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_VERTICAL_BAR_LINE_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_VERTICAL_BAR_LINE_NUMCOL" ), true ) );
      add( new GemBar( GemBarType.MEASURES_LINE, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_VERTICAL_BAR_LINE_NUMLINE" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), BAR( "ccc_horzbar", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_HORIZONTAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_HORIZONTAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_HORIZONTAL_BAR_NUM" ),
          true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), STACKED_BAR( "ccc_horzbarstacked", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_STACKED_HORIZONTAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_STACKED_HORIZONTAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_STACKED_HORIZONTAL_BAR_NUM" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), NORMALIZED_STACKED_BAR( "ccc_horzbarnormalized", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_PCT_STACKED_HORIZONTAL_BAR_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText(
          "dropZoneLabels_PCT_STACKED_HORIZONTAL_BAR_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText(
          "dropZoneLabels_PCT_STACKED_HORIZONTAL_BAR_NUM" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), LINE( "ccc_line", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_LINE_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_LINE_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_LINE_NUM" ), true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), AREA( "ccc_area", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_STACKED_AREA_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_STACKED_AREA_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_STACKED_AREA_NUM" ),
          true ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), PIE( "ccc_pie", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTIPLE_PIE_ROW" ) ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTIPLE_PIE_COL" ) ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_MULTIPLE_PIE_NUM" ),
          true ) );
    };
  } ), SUNBURST( "ccc_sunburst", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_SUNBURST_ROW" ), true ) );
      add( new GemBar( GemBarType.SIZE, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_SUNBURST_SIZE" ) ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), SCATTER( "ccc_scatter", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.X, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_SCATTER_X" ), true ) );
      add( new GemBar( GemBarType.Y, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_SCATTER_Y" ), true ) );
      add( new GemBar( GemBarType.SIZE, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_SCATTER_Z" ) ) );
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_SCATTER_ROW" ), true ) );
      add( new GemBar( GemBarType.COLOR, GemBarDndType.BOTH, L10N.getText( "dropZoneLabels_SCATTER_COL" ) ) );
      add( new GemBar( GemBarType.MULTI, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_MULTI_CHART" ) ) );
    };
  } ), HEATGRID( "ccc_heatgrid", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_HEATGRID_ROW" ), true ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_HEATGRID_COL" ) ) );
      add( new GemBar( GemBarType.COLOR, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_HEATGRID_COLOR" ),
          true ) );
      add( new GemBar( GemBarType.SIZE, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_HEATGRID_SIZE" ), true ) );
    };
  } ), GEO_MAP( "open_layers", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_ROW" ), true ) );
      add( new GemBar( GemBarType.COLOR, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_HEATGRID_COLOR" ),
          true ) );
      add( new GemBar( GemBarType.SIZE, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_HEATGRID_SIZE" ), true ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "agg_by" ) ) );
    };
  } ), PIVOT( "pivot", new ArrayList<GemBar>() {
    {
      add( new GemBar( GemBarType.ROWS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_PIVOT_ROW" ), true ) );
      add( new GemBar( GemBarType.COLUMNS, GemBarDndType.LEVEL, L10N.getText( "dropZoneLabels_PIVOT_COL" ), true ) );
      add( new GemBar( GemBarType.MEASURES, GemBarDndType.MEASURE, L10N.getText( "dropZoneLabels_PIVOT_NUM" ), true ) );
    };
  } );

  private String id;
  private List<GemBar> gemBars;

  private ChartType( String id, List<GemBar> gemBars ) {
    this.id = id;
    this.gemBars = gemBars;
  }

  public String getId() {
    return this.id;
  }

  public List<GemBar> getGemBars() {
    return this.gemBars;
  }

  /**
   * Retrieves the name of the image file that is shown in an empty report when the corresponding chart type is selected
   * in the report.
   * 
   * @return Returns the name of the image file.
   */
  public String getImageFileName() {
    return this.id.toUpperCase() + ".png";
  }
};
