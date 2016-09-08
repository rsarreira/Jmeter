package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public class GeoMapChart extends CommonChart {

  private static final List<String> FILTER_LABELS = new ArrayList<String>( 2 ) {
    {
      add( "Territory" );
      String[] params = { "APAC" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), params ) );
    }
  };

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'circle']" )
  protected List<ExtendedWebElement> blocks;

  // The element that is used to store the tooltip for a datapoint is different for GeoMapChart than it is for all
  // other charts.
  @FindBy( id = "featurePopup_contentDiv" )
  protected ExtendedWebElement dataPointTooltip;

  public GeoMapChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    return this.blocks;
  }

  /**
   * Gets the tooltip used when hovering over a data point in the chart.
   * 
   * @param index
   *          The index of the ExtendedWebElement list that contains all data points.
   * @return Returns the tooltip of the specified data point.
   */
  public String getBlockElementTooltip( int index ) {
    // The element containing the tooltip will change based on the data point that the mouse is hovering over.
    hover( getBlockElems().get( index ) );
    return dataPointTooltip.getText();
  }

  @Override
  protected int getExpectedSize() {
    return 4;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return 2;
  }

  @Override
  protected List<String> getAxisLabels() {
    return Collections.emptyList();
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return Collections.emptyList();
  }

  @Override
  protected List<String> getFilterLabels() {
    return FILTER_LABELS;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return 0;
  }
}
