package com.pentaho.qa.web.analyzer;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.chart_options.ChartOptionsPage;

import com.pentaho.qa.gui.web.chart_options.OtherPage;
import com.pentaho.qa.gui.web.chart_options.ChartOptionsPage.ChartOptionsTab;
import com.pentaho.qa.gui.web.chart_options.OtherPage.ChartSizeByNegativesMode;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAMeasure;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/8/TestCase/9252.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.SizeByNegativeMeasuresInCharts )
public class SizeByNegativeMeasuresInCharts extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private static final String ANALYZER_CALCULATED_MEASURE_SHEET = "CalculatedMeasures";
  private static final String PLOTTED_VALUE = "rgb(0, 0, 0)";
  private String sizeByNegativesMode;
  private AnalyzerReportPage analyzerPage;
  private PAReport paReport;
  private HomePage homePage;
  private String field;
  private OtherPage otherPage;
  private ChartOptionsPage chartOptionsPage;
  private List<String> oldSizesHeatGrid;
  private List<String> oldSizesScatter;

  @Test
  public void testLogin() {
    homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testLogin" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR19" )
  @SpiraTestSteps( testStepsId = "43739" )
  public void createReport( Map<String, String> args ) {
    // create report
    paReport = new PAReport( args );
    analyzerPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testLogin", description = "JIRA#ANALYZER-3348" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURE_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "CM01" )
  @SpiraTestSteps( testStepsId = "43739" )
  public void addCalculatedMeasure( Map<String, String> args ) {
    // add calculated measure
    PAMeasure measure = new PAMeasure( args );
    measure.create();

    // verification part
    field = measure.getName();
    measure.verify();

    analyzerPage.changeChartType( ChartType.SCATTER );

    // move the calculated measure to the size by field
    analyzerPage.moveLayoutFieldToSection( GemBarType.SIZE, field );

    chartOptionsPage = analyzerPage.openChartOptions();

    // verification part
    if ( !analyzerPage.isDlgChartOptionsOpened() ) {
      Assert.fail( "TS043739: The Dialog 'Chart Options' is not opened!" );
    }

    otherPage = (OtherPage)chartOptionsPage.changeTab( ChartOptionsTab.OTHER );
    
    // verification part
    //TODO refactor isDlgOtherOpened()
    if ( !otherPage.isDlgOtherOpened() ) {
      Assert.fail( "TS043739: The Dialog 'Other' is not opened!" );
    }

    if ( !otherPage.isSizeByNegativesModeAvailable() ) {
      Assert.fail( "TS043739: The list of available modes is not matched template values!" );
    }
  }

  @Test( dependsOnMethods = "addCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "43751" )
  public void applyAbsoluteNegativeOption() {
    otherPage.setSizeByNegativesMode( ChartSizeByNegativesMode.ABSOLUTE );
    analyzerPage.clickDlgBtnSave();
    field = "Quantity";

    // part 1. verify dotted values on the scatter chart.
    analyzerPage.moveLayoutFieldToSection( GemBarType.Y, field );

    if ( !analyzerPage.isSizedByAbsoluteOptionApplied( PLOTTED_VALUE ) ) {
      Assert.fail( "TS043751: The canvas doesn't contain plotted values!" );
    }

    // part 2. verify dotted values on the head chart.
    analyzerPage.changeChartType( ChartType.HEATGRID );
    if ( !analyzerPage.isSizedByAbsoluteOptionApplied( PLOTTED_VALUE ) ) {
      Assert.fail( "TS043751: The canvas doesn't contain plotted values!" );
    }
  }

  @Test( dependsOnMethods = "applyAbsoluteNegativeOption" )
  @SpiraTestSteps( testStepsId = "43750" )
  public void verifyAbsoluteOptionInXML() {
    sizeByNegativesMode = "USE_ABS";
    analyzerPage.openXML();

    // verification part
    if ( !analyzerPage.verifyNegativeOptionInXML( sizeByNegativesMode ) ) {
      Assert.fail( "TS043750: The option is not set correctly!" );
    }

    analyzerPage.clickDlgBtnSave();
  }

  @Test( dependsOnMethods = "verifyAbsoluteOptionInXML", description = "JIRA#ANALYZER-3348" )
  @SpiraTestSteps( testStepsId = "43749" )
  public void applySmallestNegativeOption() {

    // get size of values without applying 'Smallest' option for scatter chart.
    oldSizesHeatGrid = analyzerPage.getSizeCanvasValues();
    analyzerPage.changeChartType( ChartType.SCATTER );

    // get size of values without applying 'Smallest' option for heatgrid chart.
    oldSizesScatter = analyzerPage.getSizeCanvasValues();
    analyzerPage.changeChartType( ChartType.HEATGRID );

    chartOptionsPage = analyzerPage.openChartOptions();
    otherPage = (OtherPage)chartOptionsPage.changeTab( ChartOptionsTab.OTHER );

    otherPage.setSizeByNegativesMode( ChartSizeByNegativesMode.SMALLEST_VALUE );
    analyzerPage.clickDlgBtnSave();

    // part 1. verification for scatter chart
    analyzerPage.changeChartType( ChartType.SCATTER );
    List<String> newSizesScatter = analyzerPage.getSizeCanvasValues();

    if ( oldSizesScatter.equals( newSizesScatter ) ) {
      Assert.fail( "TS043749: The size of values is not changed after applying 'Smallest' option!" );
    }

    // part 2. verification for head grid chart
    analyzerPage.changeChartType( ChartType.HEATGRID );
    List<String> newSizesHeatGrid = analyzerPage.getSizeCanvasValues();

    if ( oldSizesHeatGrid.equals( newSizesHeatGrid ) ) {
      Assert.fail( "TS043749: The size of values is not changed after applying 'Smallest' option!" );
    }
  }

  @Test( dependsOnMethods = "applySmallestNegativeOption" )
  @SpiraTestSteps( testStepsId = "43748" )
  public void verifySmallestOptionInXML() {
    sizeByNegativesMode = "NEG_LOWEST";
    analyzerPage.openXML();

    // verification part
    if ( !analyzerPage.verifyNegativeOptionInXML( sizeByNegativesMode ) ) {
      Assert.fail( "TS043748: The option is not set correctly!" );
    }
  }
}
