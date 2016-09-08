package com.pentaho.qa.gui.web.chart_options;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class OtherPage extends ChartOptionsPage {

  @FindBy(
      xpath = "//*[@class='nowrapTabStrip pentahoTabContainerTop-tabs']//*[text()='{L10N:dlgChartPropsOtherTitle}']" )
  protected ExtendedWebElement dlgOther;

  @FindBy( xpath = "//*[@id='CP_sizeByNegativesMode']/*[@value]" )
  protected List<ExtendedWebElement> listSizeByNegativesMode;

  @FindBy( id = "CP_maxChartsPerRow" )
  private ExtendedWebElement selectChartsPerRow;

  @FindBy( id = "CP_multiChartRangeScope" )
  private ExtendedWebElement selectChartAutoRange;

  @FindBy( id = "CP_sizeByNegativesMode" )
  private ExtendedWebElement selectTreatNegativeValues;

  @FindBy( id = "CP_emptyCellMode" )
  private ExtendedWebElement selectEmptyCellTreatment;

  private final ExtendedWebElement[] otherTabElements =
      { selectChartsPerRow, selectChartAutoRange, selectTreatNegativeValues, selectEmptyCellTreatment };

  public enum ChartMaxChartsPerRow {

    ONE( "1" ),
    TWO( "2" ),
    THREE( "3" ),
    FOUR( "4" ),
    FIVE( "5" );

    private String chartMaxChartsPerRow;

    private ChartMaxChartsPerRow( String chartMaxChartsPerRow ) {
      this.chartMaxChartsPerRow = chartMaxChartsPerRow;
    }

    public String toString() {
      return chartMaxChartsPerRow;
    }
  }

  public enum ChartMultiChartRangeScope {

    SAME_RANGE_FOR_ALL( L10N.getText( "dlgChartPropsMultiChartRangeScope_GLOBAL" ) ),
    INDEPENDENT( L10N.getText( "dlgChartPropsMultiChartRangeScope_CELL" ) );

    private String chartMultiChartRangeScope;

    private ChartMultiChartRangeScope( String chartMultiChartRangeScope ) {
      this.chartMultiChartRangeScope = chartMultiChartRangeScope;
    }

    public String toString() {
      return chartMultiChartRangeScope;
    }
  }

  public enum ChartEmptyCellMode {

    SHOW_GAP( L10N.getText( "dlgChartPropsEmptyCellMode_GAP" ) ),
    CONNECT_WITH_DOTTED_LINE( L10N.getText( "dlgChartPropsEmptyCellMode_LINEAR" ) ),
    TREAT_AS_ZERO( L10N.getText( "dlgChartPropsEmptyCellMode_ZERO" ) );

    private String chartEmptyCellMode;

    private ChartEmptyCellMode( String chartEmptyCellMode ) {
      this.chartEmptyCellMode = chartEmptyCellMode;
    }

    public String toString() {
      return chartEmptyCellMode;
    }
  }

  public enum ChartSizeByNegativesMode {

    SMALLEST_VALUE( L10N.getText( "dlgChartPropsSizeByNegativesMode_NEG_LOWEST" ) ),
    ABSOLUTE( L10N.getText( "dlgChartPropsSizeByNegativesMode_USE_ABS" ) );

    private String chartSizeByNegativesMode;

    private ChartSizeByNegativesMode( String chartSizeByNegativesMode ) {
      this.chartSizeByNegativesMode = chartSizeByNegativesMode;
    }

    public String toString() {
      return chartSizeByNegativesMode;
    }
  }

  public OtherPage( WebDriver driver, ExtendedWebElement elementThatThisRepresents ) {
    super( driver, elementThatThisRepresents );
  }

  public ExtendedWebElement[] getOtherTabElements() {
    return otherTabElements;
  }

  public boolean isDlgOtherOpened() {
    return isElementPresent( dlgOther, EXPLICIT_TIMEOUT / 15 );
  }

  public boolean isSizeByNegativesModeAvailable() {
    boolean result = true;
    List<ChartSizeByNegativesMode> enumList = Arrays.asList( ChartSizeByNegativesMode.values() );
    for ( int i = 0; i < listSizeByNegativesMode.size(); i++ ) {
      if ( !enumList.get( i ).toString().equals( listSizeByNegativesMode.get( i ).getText() ) ) {
        result = false;
        LOGGER.error( "List of values is not matched Enum values!" );
      }
    }
    return result;
  }

  public String getSelectChartsPerRow() {
    return selectChartsPerRow.getSelectedValue();
  }

  public String getSelectChartAutoRange() {
    return selectChartAutoRange.getSelectedValue();
  }

  public String getSelectTreatNegativeValues() {
    return selectTreatNegativeValues.getSelectedValue();
  }

  public String getSelectEmptyCellTreatment() {
    return selectEmptyCellTreatment.getSelectedValue();
  }

  public void setMaxChartsPerRow( ChartMaxChartsPerRow chartsPerRow ) {
    select( selectChartsPerRow, chartsPerRow.toString() );
  }

  public void setSizeByNegativesMode( ChartSizeByNegativesMode mode ) {
    select( selectTreatNegativeValues, mode.toString() );
  }

  public void setSelectChartsPerRow( ChartMaxChartsPerRow inputChartMaxChartsPerRow ) {
    select( selectChartsPerRow, inputChartMaxChartsPerRow.toString() );
  }

  public void setSelectChartAutoRange( ChartMultiChartRangeScope inputChartMultiChartRangeScope ) {
    select( selectChartAutoRange, inputChartMultiChartRangeScope.toString() );
  }

  public void setSelectTreatNegativeValues( ChartSizeByNegativesMode inputChartSizeByNegativesMode ) {
    select( selectTreatNegativeValues, inputChartSizeByNegativesMode.toString() );
  }

  public void setSelectEmptyCellTreatment( ChartEmptyCellMode inputChartEmptyCellMode ) {
    select( selectEmptyCellTreatment, inputChartEmptyCellMode.toString() );
  }

}
