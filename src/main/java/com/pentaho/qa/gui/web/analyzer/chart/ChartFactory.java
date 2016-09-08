package com.pentaho.qa.gui.web.analyzer.chart;

import org.openqa.selenium.WebDriver;

public class ChartFactory {

  public ChartVerifier getChartVerifier( WebDriver driver, ChartType type ) throws IllegalArgumentException {
    ChartVerifier result;
    switch ( type ) {
      case COLUMN:
      case STACKED_COLUMN:
      case NORMALIZED_STACKED_COLUMN:
      case BAR:
      case NORMALIZED_STACKED_BAR:
      case STACKED_BAR:
        result = new BarChart( driver, type );
        break;
      case COLUMN_LINE_COMBO:
        result = new ColumnLineComboChart( driver, type );
        break;
      case LINE:
        result = new LineChart( driver, type );
        break;
      case AREA:
        result = new AreaChart( driver, type );
        break;
      case PIE:
        result = new PieChart( driver, type );
        break;
      case SUNBURST:
        result = new SunburstChart( driver, type );
        break;
      case HEATGRID:
        result = new HeatGridChart( driver, type );
        break;
      case SCATTER:
        result = new ScatterChart( driver, type );
        break;
      case GEO_MAP:
        result = new GeoMapChart( driver, type );
        break;
      default:
        throw new IllegalArgumentException( "Used ansupported chart type: " + type.getId() );
    }
    return result;
  }
}
