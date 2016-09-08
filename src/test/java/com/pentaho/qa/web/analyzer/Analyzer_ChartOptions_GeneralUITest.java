// http://spiratest.pentaho.com/8/TestCase/8853.aspx
package com.pentaho.qa.web.analyzer;

import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartVerifier;
import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartBackgroundFillType;
import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartLabelType;
import com.pentaho.qa.gui.web.chart_options.ChartOptionsPage;
import com.pentaho.qa.gui.web.chart_options.ChartOptionsPage.ChartOptionsTab;
import com.pentaho.qa.gui.web.chart_options.GeneralPage;
import com.pentaho.qa.gui.web.chart_options.LegendPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.PaletteColour;
import com.pentaho.services.Report.FontFamily;
import com.pentaho.services.Report.FontSize;
import com.pentaho.services.Report.FontStyle;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

public class Analyzer_ChartOptions_GeneralUITest extends WebBaseTest {

  private final String dataProviderSheetName = "AnalyzerReports";

  private PAReport report;
  private AnalyzerReportPage parPage;

  @Test( )
  public void login() {
    webUser.login();
  }

  /*
   * #1 Description: -Create an analyzer report using the steel wheels schema
   * 
   * -Select Years, Territory and Sales fields
   * 
   * -Switch to each chart type and look at the layout panel.
   * 
   * Expectation: Chart options button should be visible.
   */
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = dataProviderSheetName, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR23" )
  @SpiraTestSteps( testStepsId = "40152" )
  public void Step_1( Map<String, String> args ) {

    SoftAssert softAssert = new SoftAssert();

    FontFamily expectedPlotFontFamily = FontFamily.ARIAL;
    FontSize expectedPlotFontSize = FontSize.THIRTY_TWO;
    FontStyle expectedPlotFontStyle = FontStyle.ITALIC;
    PaletteColour expectedPlotFontColour = PaletteColour.LEMON_CHIFFON;

    FontFamily expectedLegendFontFamily = FontFamily.WINGDINGS;
    FontSize expectedLegendFontSize = FontSize.EIGHT;
    FontStyle expectedLegendFontStyle = FontStyle.BOLD;
    PaletteColour expectedLegendFontColour = PaletteColour.LAVENDER;

    PaletteColour expectedSolidChartFillColour = PaletteColour.CHARTREUSE;
    PaletteColour expectedGradientStartChartFillColour = PaletteColour.DARK_ORCHID;
    PaletteColour expectedGradientEndChartFillColour = PaletteColour.LIGHT_SEA_GREEN;

    // build the expectedChartOptionsAttributeValues HashMap for XML verification
    HashMap<String, String> expectedChartOptionsAttributeValues = new HashMap<String, String>( 9 );
    expectedChartOptionsAttributeValues.put( "backgroundColor", expectedSolidChartFillColour.getHexValue() );
    expectedChartOptionsAttributeValues.put( "labelColor", expectedPlotFontColour.getHexValue() );
    expectedChartOptionsAttributeValues.put( "labelSize", expectedPlotFontSize.toString() );
    expectedChartOptionsAttributeValues.put( "labelStyle", expectedPlotFontStyle.name() );
    expectedChartOptionsAttributeValues.put( "legendSize", expectedLegendFontSize.toString() );
    expectedChartOptionsAttributeValues.put( "legendColor", expectedLegendFontColour.getHexValue() );
    expectedChartOptionsAttributeValues.put( "legendStyle", expectedLegendFontStyle.name() );
    expectedChartOptionsAttributeValues.put( "labelFontFamily", expectedPlotFontFamily.toString() );
    expectedChartOptionsAttributeValues.put( "legendFontFamily", expectedLegendFontFamily.toString() );

    // initialize a new Analysis Report with arguments from the XLS Data Provider
    report = new PAReport( args );

    // actually create the Analyzer Report
    parPage = report.create();

    // switch to the scatter chart so that "X-Axis", "Y-Axis", "Points", and "Color By" are set correctly.
    // Switching to other charts first will guarantee incorrect initialization of scatter.
    parPage.changeChartType( ChartType.SCATTER );

    // open chart options
    ChartOptionsPage chartOptionsPage = parPage.openChartOptions();

    // switch to general tab (even though we are there by default)
    GeneralPage generalTab = (GeneralPage) chartOptionsPage.changeTab( ChartOptionsTab.GENERAL );

    // set the "Fill Type" to "Solid"
    generalTab.setSelectBackgroundFill( ChartBackgroundFillType.SOLID );

    // set a "Fill Color"
    generalTab.setPickChartBackgroundColor( expectedSolidChartFillColour );

    // set the plot label fontFamily, fontSize, fontStyle, and fontColour
    generalTab.setSelectLabelFontFamily( expectedPlotFontFamily );
    generalTab.setSelectLabelFontSize( expectedPlotFontSize );
    generalTab.setSelectLabelFontStyle( expectedPlotFontStyle );
    generalTab.setPickLabelFontColor( expectedPlotFontColour );

    // change the tab to edit the legend label's style
    LegendPage legendTab = (LegendPage) chartOptionsPage.changeTab( ChartOptionsTab.LEGEND );

    // set the legend label fontFamily, fontSize, fontStyle, and fontColour
    legendTab.setSelectLegendFontFamily( expectedLegendFontFamily );
    legendTab.setSelectLegendFontSize( expectedLegendFontSize );
    legendTab.setSelectLegendFontStyle( expectedLegendFontStyle );
    legendTab.setPickLegendFontColor( expectedLegendFontColour );

    // save changes by clicking "OK"
    chartOptionsPage.clickDlgBtnSave();

    // TODO look for "Refreshing..." message

    // open "More actions and options" > "Administration" > "XML" and verify the "chartOptions" tag
    parPage.verifyChartOptionsXML( softAssert, expectedChartOptionsAttributeValues );

    // now verify the label styles, that the "Chart Options..." button is visible, and the solid background color for
    // all chart types
    for ( ChartType chartType : ChartType.values() ) {

      // do not test "pivot" since it is not actually a chart type
      // do not test "Geo Map" since it is not affected by chart options
      if ( chartType == ChartType.PIVOT || chartType == ChartType.GEO_MAP )
        continue;

      // change the chart type and get the chart verifier for this type
      ChartVerifier verifier = parPage.changeChartType( chartType );

      // verify the button is there and assert
      verifier.verifyChartOptionsButton( softAssert );

      // verify the plot labels are correct
      verifier.verifySpecifiedChartLabels( softAssert, ChartLabelType.PLOT, expectedPlotFontStyle, expectedPlotFontSize,
          expectedPlotFontFamily, expectedPlotFontColour );

      // verify the legend labels are correct
      verifier.verifySpecifiedChartLabels( softAssert, ChartLabelType.LEGEND, expectedLegendFontStyle,
          expectedLegendFontSize, expectedLegendFontFamily, expectedLegendFontColour );

      // verify the solid background fill
      verifier.verifyChartBackgroundFill( softAssert, expectedSolidChartFillColour );
    }

    /**
     * Since opening the "Chart Options..." dialog and configuring the chart background fill for Solid and Gradient
     * would be inefficient if done for each chart type, we do another loop here to handle the Gradient option.
     */

    // open the options again
    chartOptionsPage = parPage.openChartOptions();

    // switch to the general tab
    generalTab = (GeneralPage) chartOptionsPage.changeTab( ChartOptionsTab.GENERAL );

    // set the "Fill Type" to "Gradient"
    generalTab.setSelectBackgroundFill( ChartBackgroundFillType.GRADIENT );

    // set the "Start" and "End" Gradient colours
    generalTab.setPickChartGradientStartColor( expectedGradientStartChartFillColour );
    generalTab.setPickChartGradientEndColor( expectedGradientEndChartFillColour );

    // click "OK"
    chartOptionsPage.clickDlgBtnSave();

    // change to each ChartType and verify the gradient
    for ( ChartType chartType : ChartType.values() ) {
      // do not test "pivot" since it is not actually a chart type
      // do not test "Geo Map" since it is not affected by chart options
      if ( chartType == ChartType.PIVOT || chartType == ChartType.GEO_MAP )
        continue;

      // change the chart type and get chart verifier for this type
      ChartVerifier verifier = parPage.changeChartType( chartType );

      // TODO put this in the right spot
      ArrayList<String> tickets = new ArrayList<String>();
      tickets.add( "ANALYZER-3361" );
      Jira.setTickets( tickets );

      // verify the gradient background fill
      // This will fail on multi-charts/pies because gradients are broken for multi's
      verifier.verifyChartBackgroundFill( softAssert, expectedGradientStartChartFillColour,
          expectedGradientEndChartFillColour );
    }
    // assert all assertions!
    softAssert.assertAll();
  }
}


/*TODO NOTE:
* Determining whether or not a chart is "MULTI" [field(s) placed in the "Multi-____" GemBar]
* is useless until I can differentiate between Plot, Legend, and Multi-Plot-Title labels.
* 
* Thus I have commented out code pertaining to determining that distinction
* 
* As it stands, the inconsistencies in charts are too numerous:
*  - no meaningful id or class attributes in the DOM
*  - Pie Chart's "Multi-Pie" GemBar is not of GemBarType.MULTI as all the other charts are
* 
* I could work around the issue by adding more customized logic, but instead will wait for DET
* as there will, allegedly, be changes to charts
* 
* 
* boolean MULTI_FLAG;

 initially assume that each chart is without a "Multi-Chart" GemBar that is populated
MULTI_FLAG = false;

determine if there is a Multi-Chart GemBar that is populated
 
for( GemBar gemBar : chartType.getGemBars()){
  System.out.println("GEMBAR_DEBUG_CHARTOPTIONS: "+gemBar.getType()+" and gemBar.getType()==GemBarType.MULTI evals to: "+(gemBar.getType()==GemBarType.MULTI));
  if(gemBar.getType()==GemBarType.MULTI && theAnalyzerReportPage.getGemBarFields( gemBar ).size() == 0){
    MULTI_FLAG = true;
    break;
  }
}
* 
*/
