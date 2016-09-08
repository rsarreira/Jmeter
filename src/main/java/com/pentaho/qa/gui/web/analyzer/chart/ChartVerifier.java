package com.pentaho.qa.gui.web.analyzer.chart;

import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartLabelType;
import com.pentaho.services.Report.FontFamily;
import com.pentaho.services.Report.FontSize;
import com.pentaho.services.Report.FontStyle;
import com.pentaho.services.Report.PaletteColour;

public interface ChartVerifier {

  SoftAssert verifyAddingFields();

  SoftAssert verifyAddingWrongFields();

  SoftAssert verifyContent();

  SoftAssert verifyDrillDown();

  SoftAssert verifyAddingMultiChart();

  SoftAssert verifySpecifiedChartLabels( SoftAssert softAssert, ChartLabelType labelType, FontStyle fontStyle,
      FontSize fontSize, FontFamily fontFamily, PaletteColour fontFillColour );

  SoftAssert verifyChartBackgroundFill( SoftAssert softAssert, PaletteColour... fillColours );

  SoftAssert verifyChartOptionsButton( SoftAssert softAssert );
}
