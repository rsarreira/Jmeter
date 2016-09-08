package com.pentaho.qa.gui.web.chart_options;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AxisPage extends ChartOptionsPage {

  @FindBy( id = "CP_autoRange" )
  private ExtendedWebElement cbAutoRange;

  @FindBy( id = "CP_autoRangeSecondary" )
  private ExtendedWebElement cbAutoRangeSecondary;

  @FindBy( id = "CP_valueAxisLowerLimit" )
  private ExtendedWebElement txtStart;

  @FindBy( id = "CP_valueAxisUpperLimit" )
  private ExtendedWebElement txtStop;

  @FindBy( id = "CP_valueAxisLowerLimitSecondary" )
  private ExtendedWebElement txtStartSecondary;

  @FindBy( id = "CP_valueAxisUpperLimitSecondary" )
  private ExtendedWebElement txtStopSecondary;

  @FindBy( id = "CP_displayUnits" )
  private ExtendedWebElement selectScale;

  @FindBy( id = "CP_displayUnitsSecondary" )
  private ExtendedWebElement selectScaleSecondary;

  private final ExtendedWebElement[] axisTabElements =
      { cbAutoRange, txtStart, txtStop, selectScale, cbAutoRangeSecondary, txtStartSecondary, txtStopSecondary,
        selectScaleSecondary };

  public enum ChartDisplayUnit {

    NONE( L10N.getText( "dlgChartPropsBulletNone" ) ),
    HUNDREDS( L10N.getText( "dlgChartOption_UNITS_2" ) ),
    THOUSANDS( L10N.getText( "dlgChartOption_UNITS_3" ) ),
    TEN_THOUSANDS( L10N.getText( "dlgChartOption_UNITS_4" ) ),
    HUNDRED_THOUSANDS( L10N.getText( "dlgChartOption_UNITS_5" ) ),
    MILLIONS( L10N.getText( "dlgChartOption_UNITS_6" ) );

    private String chartDisplayUnit;

    private ChartDisplayUnit( String chartDisplayUnits ) {
      this.chartDisplayUnit = chartDisplayUnits;
    }

    public String toString() {
      return chartDisplayUnit;
    }
  }

  public AxisPage( WebDriver driver, ExtendedWebElement elementThatThisRepresents ) {
    super( driver, elementThatThisRepresents );
  }

  public ExtendedWebElement[] getAxisTabElements() {
    return axisTabElements;
  }

  public boolean isCbAutoRange() {
    return cbAutoRange.isChecked();
  }

  public boolean isCbAutoRangeSecondary() {
    return cbAutoRangeSecondary.isChecked();
  }

  public String getTxtStart() {
    return txtStart.getAttribute( "value" );
  }

  public String getTxtStop() {
    return txtStop.getAttribute( "value" );
  }

  public String getTxtStartSecondary() {
    return txtStartSecondary.getAttribute( "value" );
  }

  public String getTxtStopSecondary() {
    return txtStopSecondary.getAttribute( "value" );
  }

  public String getSelectScale() {
    return selectScale.getSelectedValue();
  }

  public String getSelectScaleSecondary() {
    return selectScaleSecondary.getSelectedValue();
  }

  public void setCbAutoRange( boolean inputCbAutoRange ) {
    setCheckBox( cbAutoRange, inputCbAutoRange );
  }

  public void setCbAutoRangeSecondary( boolean inputCbAutoRangeSecondary ) {
    setCheckBox( cbAutoRangeSecondary, inputCbAutoRangeSecondary );
  }

  public void setTxtStart( String inputTxtStart ) {
    type( txtStart, inputTxtStart );
  }

  public void setTxtStop( String inputTxtStop ) {
    type( txtStop, inputTxtStop );
  }

  public void setTxtStartSecondary( String inputTxtStartSecondary ) {
    type( txtStartSecondary, inputTxtStartSecondary );
  }

  public void setTxtStopSecondary( String inputTxtStopSecondary ) {
    type( txtStopSecondary, inputTxtStopSecondary );
  }

  public void setSelectScale( ChartDisplayUnit inputChartDisplayUnit ) {
    select( selectScale, inputChartDisplayUnit.toString() );
  }

  public void setSelectScaleSecondary( ChartDisplayUnit inputChartDisplayUnit ) {
    select( selectScaleSecondary, inputChartDisplayUnit.toString() );
  }

}
