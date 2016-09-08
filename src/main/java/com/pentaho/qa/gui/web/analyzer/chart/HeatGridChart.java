package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.WebDriver;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public class HeatGridChart extends PieChart {

  private static final List<String> DRILLED_AXIS_LABELS = new ArrayList<String>() {
    {
      add( "APAC" );
      add( "2003" );
      add( "QTR2" );
      add( "QTR3" );
      add( "QTR4" );
    }
  };

  private static final String ATTR_NAME = "transform";

  protected Comparator<ExtendedWebElement> comparator = new Comparator<ExtendedWebElement>() {
    public int compare( ExtendedWebElement elem1, ExtendedWebElement elem2 ) {
      Double d1 = getDigit( elem1.getAttribute( ATTR_NAME ) );
      Double d2 = getDigit( elem2.getAttribute( ATTR_NAME ) );
      return d1.compareTo( d2 );
    }
  };

  public HeatGridChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<String> getAxisLabels() {
    List<String> labels = new ArrayList<String>( super.getAxisLabels() );
    labels.add( "2003" );
    labels.add( "2004" );
    labels.add( "2005" );
    return labels;
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return DRILLED_AXIS_LABELS;
  }

  @Override
  protected int getExpectedSize() {
    return 12;
  }

  @Override
  protected List<String> getFilterLabels() {
    List<String> labels = new ArrayList<String>( super.getFilterLabels() );
    labels.add( "Years" );
    String[] params = { "2003" };
    labels.add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), params ) );
    
    return labels;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return 0;
  }
}
