package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public class AreaChart extends CommonChart {

  private static final List<String> DRILLED_AXIS_LABELS = new ArrayList<String>( 3 ) {
    {
      add( "Australia" );
      add( "New Zealand" );
    }
  };

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'circle' and @cursor='pointer']" )
  protected List<ExtendedWebElement> pathElems;

  public AreaChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    return this.pathElems;
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
  protected int getDrillingExpectedSize() {
    return 2;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return getDrillingExpectedSize() + 1;
  }
}
