package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class BarChart extends CommonChart {

  @SuppressWarnings( "serial" )
  private static final List<String> DRILLED_AXIS_LABELS = new ArrayList<String>( 4 ) {
    {
      add( "APAC" );
    }
  };

  private static final String ATTR_NAME_X = "x";
  private static final String ATTR_NAME_Y = "y";

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'rect' and @cursor='pointer']" )
  protected List<ExtendedWebElement> rectElems;

  class XYComparator implements Comparator<ExtendedWebElement> {
    @Override
    public int compare( ExtendedWebElement elem1, ExtendedWebElement elem2 ) {
      int result = 0;
      // X
      String x1 = elem1.getAttribute( ATTR_NAME_X );
      String x2 = elem2.getAttribute( ATTR_NAME_X );
      if ( StringUtils.isNotEmpty( x1 ) && StringUtils.isNotBlank( x2 ) ) {
        Double d1 = Double.parseDouble( x1 );
        Double d2 = Double.parseDouble( x2 );
        result = d1.compareTo( d2 );
      }
      // Y
      if ( result == 0 ) {
        String y1 = elem1.getAttribute( ATTR_NAME_Y );
        String y2 = elem2.getAttribute( ATTR_NAME_Y );
        Double d1;
        Double d2;

        // [MG] Workaround for the first block element as it has no Y coordinate. Set to 0 in order to compare.
        if ( StringUtils.isNotEmpty( y1 ) && StringUtils.isNotBlank( y1 ) ) {
          d1 = Double.parseDouble( y1 );
        } else {
          // set Y to 0 as there is no Y coordinate for that element, and we need to compare it to other elements.
          d1 = 0.00;
        }

        if ( StringUtils.isNotEmpty( y2 ) && StringUtils.isNotBlank( y2 ) ) {
          d2 = Double.parseDouble( y2 );
        } else {
          // set Y to 0 as there is no Y coordinate for that element, and we need to compare it to other elements.
          d2 = 0.00;
        }

        result = d1.compareTo( d2 );
      }
      return result;
    }
  }

  public BarChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    List<ExtendedWebElement> elems = new ArrayList<ExtendedWebElement>( rectElems );
    Collections.sort( elems, new XYComparator() );
    return elems;
  }

  @Override
  protected int getExpectedSize() {
    return 12;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return 3;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return getDrillingExpectedSize() + 1;
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return DRILLED_AXIS_LABELS;
  }

}
