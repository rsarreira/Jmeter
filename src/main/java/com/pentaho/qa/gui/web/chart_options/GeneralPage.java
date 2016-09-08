package com.pentaho.qa.gui.web.chart_options;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartBackgroundFillType;
import com.pentaho.services.Report.FontFamily;
import com.pentaho.services.Report.FontSize;
import com.pentaho.services.Report.FontStyle;
import com.pentaho.services.Report.PaletteColour;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class GeneralPage extends ChartOptionsPage {

  @FindBy( id = "CP_backgroundColorDiv" )
  private ExtendedWebElement pickChartBackgroundColor;

  @FindBy( id = "CP_backgroundColorDiv" )
  private ExtendedWebElement pickChartGradientStartColor;

  @FindBy( id = "CP_backgroundColorEndDiv" )
  private ExtendedWebElement pickChartGradientEndColor;

  @FindBy( id = "CP_labelColorDiv" )
  private ExtendedWebElement pickLabelFontColor;

  @FindBy( id = "CP_backgroundFill" )
  private ExtendedWebElement selectBackgroundFill;

  @FindBy( id = "CP_labelFontFamily" )
  private ExtendedWebElement selectLabelFontFamily;

  @FindBy( id = "CP_labelSize" )
  private ExtendedWebElement selectLabelFontSize;

  @FindBy( id = "CP_labelStyle" )
  private ExtendedWebElement selectLabelFontStyle;

  @FindBy( id = "CP_maxValues" )
  private ExtendedWebElement selectMaxValues;

  private final ExtendedWebElement[] generalTabElements =
      { selectBackgroundFill, pickChartBackgroundColor, pickChartGradientEndColor, selectLabelFontFamily,
        selectLabelFontSize, selectLabelFontStyle, pickLabelFontColor, selectMaxValues };

  public enum ChartMaxNumberOfValues {

    ONE_HUNDRED( "100" ),
    ONE_HUNDRED_AND_FIFTY( "150" ),
    TWO_HUNDRED( "200" ),
    TWO_HUNDRED_AND_FIFTY( "250" );

    private String chartMaxNumberOfValues;

    private ChartMaxNumberOfValues( String chartMaxNumberOfValues ) {
      this.chartMaxNumberOfValues = chartMaxNumberOfValues;
    }

    public String toString() {
      return chartMaxNumberOfValues;
    }
  }

  public GeneralPage( WebDriver driver, ExtendedWebElement elementThatThisRepresents ) {
    super( driver, elementThatThisRepresents );
  }

  public ExtendedWebElement[] getGeneralTabElements() {
    return generalTabElements;
  }

  public String getPickChartBackgroundColor() {
    return pickChartBackgroundColor.getAttribute( "value" );
  }

  public String getPickChartGradientStartColor() {
    return pickChartGradientStartColor.getAttribute( "value" );
  }

  public String getPickChartGradientEndColor() {
    return pickChartGradientEndColor.getAttribute( "value" );
  }

  public String getPickLabelFontColor() {
    return pickLabelFontColor.getAttribute( "value" );
  }

  public String getSelectBackgroundFill() {
    return selectBackgroundFill.getSelectedValue();
  }

  public String getSelectLabelFontFamily() {
    return selectLabelFontFamily.getSelectedValue();
  }

  public String getSelectLabelFontSize() {
    return selectLabelFontSize.getSelectedValue();
  }

  public String getSelectLabelFontStyle() {
    return selectLabelFontStyle.getSelectedValue();
  }

  public String getSelectMaxValues() {
    return selectMaxValues.getSelectedValue();
  }

  public void setPickChartBackgroundColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickChartBackgroundColor, inputPaletteColour );
  }

  public void setPickChartGradientStartColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickChartGradientStartColor, inputPaletteColour );
  }

  public void setPickChartGradientEndColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickChartGradientEndColor, inputPaletteColour );
  }

  public void setPickLabelFontColor( PaletteColour inputPaletteColour ) {
    setColorPickerPaletteColour( pickLabelFontColor, inputPaletteColour );
  }

  public void setSelectBackgroundFill( ChartBackgroundFillType inputChartBackgroundFillType ) {
    select( selectBackgroundFill, inputChartBackgroundFillType.toString() );
  }

  public void setSelectLabelFontFamily( FontFamily inputFontFamily ) {
    select( selectLabelFontFamily, inputFontFamily.toString() );
  }

  public void setSelectLabelFontSize( FontSize inputFontSize ) {
    select( selectLabelFontSize, inputFontSize.toString() );
  }

  public void setSelectLabelFontStyle( FontStyle inputFontStyle ) {
    select( selectLabelFontStyle, inputFontStyle.toString() );
  }

  public void setSelectMaxValues( ChartMaxNumberOfValues inputChartMaxNumberOfValues ) {
    select( selectMaxValues, inputChartMaxNumberOfValues.toString() );
  }

}
