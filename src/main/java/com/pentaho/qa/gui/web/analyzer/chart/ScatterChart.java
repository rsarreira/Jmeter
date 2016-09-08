package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ScatterChart extends CommonChart {

  @SuppressWarnings( "serial" )
  private static final List<String> FILTER_LABELS = new ArrayList<String>( 4 ) {
    {
      add( "Years" );
      String[] yearsParams = { "2004" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), yearsParams ) );
      add( "Territory" );
      String[] territoryParams = { "APAC" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), territoryParams ) );
    }
  };

  // @FindBy( xpath =
  // "//div[@id='VIZFRAME']//*[local-name()='svg']/*[local-name()='g'][1]/*[local-name()='g'][2]//*[local-name() = 'circle' and @cursor='pointer']"
  // )
  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[name()='g'][1]/*[name()='g'][2]//*[name()='circle' and @cursor='pointer' and @r='4.5']" )
  protected List<ExtendedWebElement> circleElems;

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'circle' and @cursor='pointer']" )
  protected List<ExtendedWebElement> drilledCircleElems;

  public ScatterChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    return this.circleElems;
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return Collections.emptyList();
  }

  @Override
  protected List<String> getAxisLabels() {
    return Collections.emptyList();
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
  protected List<String> getFilterLabels() {
    return FILTER_LABELS;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return 4;
  }
}
