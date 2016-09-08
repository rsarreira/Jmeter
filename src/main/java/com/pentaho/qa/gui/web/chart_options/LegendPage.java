package com.pentaho.qa.gui.web.chart_options;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.services.Report.PaletteColour;
import com.pentaho.services.Report.FontFamily;
import com.pentaho.services.Report.FontSize;
import com.pentaho.services.Report.FontStyle;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class LegendPage extends ChartOptionsPage {

  @FindBy( id = "CP_showLegend" )
  private ExtendedWebElement cbShowLegend;

  @FindBy( id = "CP_legendBackgroundColorDiv" )
  private ExtendedWebElement pickLegendBackgroundColor;

  @FindBy( id = "CP_legendColorDiv" )
  private ExtendedWebElement pickLegendFontColor;

  @FindBy( id = "CP_legendPosition" )
  private ExtendedWebElement selectLegendPosition;

  @FindBy( id = "CP_legendFontFamily" )
  private ExtendedWebElement selectLegendFontFamily;

  @FindBy( id = "CP_legendSize" )
  private ExtendedWebElement selectLegendFontSize;

  @FindBy( id = "CP_legendStyle" )
  private ExtendedWebElement selectLegendFontStyle;

  private final ExtendedWebElement[] legendTabElements =
      { cbShowLegend, selectLegendPosition, pickLegendBackgroundColor, selectLegendFontFamily, selectLegendFontSize,
        selectLegendFontStyle, pickLegendFontColor };

  public enum ChartLegendPosition {

    TOP( L10N.getText( "dlgChartPropsLegendPosTop" ) ),
    RIGHT( L10N.getText( "dlgChartPropsLegendPosRight" ) ),
    BOTTOM( L10N.getText( "dlgChartPropsLegendPosBottom" ) ),
    LEFT( L10N.getText( "dlgChartPropsLegendPosLeft" ) );

    private String chartLegendPosition;

    private ChartLegendPosition( String chartLegendPosition ) {
      this.chartLegendPosition = chartLegendPosition;
    }

    public String toString() {
      return chartLegendPosition;
    }
  }

  public LegendPage( WebDriver driver, ExtendedWebElement elementThatThisRepresents ) {
    super( driver, elementThatThisRepresents );
  }

  public ExtendedWebElement[] getLegendTabElements() {
    return legendTabElements;
  }

  public boolean isCbShowLegend() {
    return cbShowLegend.isChecked();
  }

  public String getPickLegendBackgroundColor() {
    return pickLegendBackgroundColor.getAttribute( "value" );
  }

  public String getPickLegendFontColor() {
    return pickLegendFontColor.getAttribute( "value" );
  }

  public String getSelectLegendPosition() {
    return selectLegendPosition.getSelectedValue();
  }

  public String getSelectLegendFontFamily() {
    return selectLegendFontFamily.getSelectedValue();
  }

  public String getSelectLegendFontSize() {
    return selectLegendFontSize.getSelectedValue();
  }

  public String getSelectLegendFontStyle() {
    return selectLegendFontStyle.getSelectedValue();
  }

  public void setCbShowLegend( boolean inputCbShowLegend ) {
    setCheckBox( cbShowLegend, inputCbShowLegend );
  }

  public void setPickLegendBackgroundColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickLegendBackgroundColor, inputPaletteColour );
  }

  public void setPickLegendFontColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickLegendFontColor, inputPaletteColour );
  }

  public void setSelectLegendPosition( ChartLegendPosition inputChartLegendPosition ) {
    select( selectLegendPosition, inputChartLegendPosition.toString() );
  }

  public void setSelectLegendFontFamily( FontFamily inputFontFamily ) {
    select( selectLegendFontFamily, inputFontFamily.toString() );
  }

  public void setSelectLegendFontSize( FontSize inputFontSize ) {
    select( selectLegendFontSize, inputFontSize.toString() );
  }

  public void setSelectLegendFontStyle( FontStyle inputFontStyle ) {
    select( selectLegendFontStyle, inputFontStyle.toString() );
  }

}
