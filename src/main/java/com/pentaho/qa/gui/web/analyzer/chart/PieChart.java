package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public class PieChart extends CommonChart {

  private static final List<String> DRILLED_AXIS_LABELS = new ArrayList<String>( 3 ) {
    {
      add( "Australia" );
      add( "New Zealand" );
      add( "Singapore" );
    }
  };

  private static final List<String> FILTER_LABELS = new ArrayList<String>( 2 ) {
    {
      add( "Territory" );
      String[] params = { "APAC" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), params ) );
    }
  };

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'path' and @cursor='pointer']" )
  protected List<ExtendedWebElement> areaElems;

  public PieChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    return this.areaElems;
  }

  @Override
  protected int getExpectedSize() {
    return 4;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return 3;
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return DRILLED_AXIS_LABELS;
  }

  protected List<String> getFilterLabels() {
    return FILTER_LABELS;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return 6;
  }
}
