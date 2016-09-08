package com.pentaho.qa.web.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.analyzer.chart.LineChart;
import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartProperty;
import com.pentaho.qa.gui.web.analyzer.chart.CommonChart.ChartPropertyOption;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/7564.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_LineChartSpecific )
public class Analyzer_LineChartSpecific extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private PAReport paReport;
  private HomePage homePage;
  private AnalyzerReportPage analyzerPage;
  private LineChart line;
  List<String> rows = new ArrayList<String>();
  List<String> columns = new ArrayList<String>();
  List<String> measures = new ArrayList<String>();

  @Test
  public void testLogin() {
    // Log in
    homePage = webUser.login();
    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testLogin" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR08" )
  @SpiraTestSteps( testStepsId = "26982" )
  public void createReport( Map<String, String> args ) {
    // Create analyzer report
    paReport = new PAReport( args );
    analyzerPage = paReport.create();
  }

  @Test( dependsOnMethods = "createReport" )
  @SpiraTestSteps( testStepsId = "26982" )
  public void checkDefaults() {
    // Create line chart
    SoftAssert softAssert = new SoftAssert();
    line = (LineChart) analyzerPage.changeChartType( ChartType.LINE );
    // Assert all default values are selected and Chart Options button is present
    softAssert.assertTrue( line
        .isOptionSelected( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_CIRCLE ),
        "TS026982: The default option on the Bullet Style property is not as expected!" );
    softAssert.assertTrue( line.isOptionSelected( ChartProperty.LINE_WIDTH, ChartPropertyOption.LINE_WIDTH_1 ),
        "TS026982: The default option on the Line Width property is not as expected!" );
    softAssert.assertTrue( line.isOptionSelected( ChartProperty.DATA_LABELS, ChartPropertyOption.DATA_LABELS_NONE ),
        "TS026982: The default option on the Data Labels property is not as expected!" );
    softAssert.assertTrue( line.isOptionSelected( ChartProperty.TREND_TYPE, ChartPropertyOption.TREND_TYPE_NONE ),
        "TS026982: The default option on the Bullet Style property is not as expected!" );
    softAssert.assertTrue( line.isOptionsButtonPresent(), "TS026982: The Chart Properties button is not present!" );

    // Assert options on drop downs of properties are as expected
    softAssert.assertTrue( line.verifyOptions( ChartProperty.BULLET_STYLE ),
        "TS026982: Options on the Bullet Style property are not as expected!" );
    softAssert.assertTrue( line.verifyOptions( ChartProperty.LINE_WIDTH ),
        "TS026982: Options on the Line Width property are not as expected!" );
    softAssert.assertTrue( line.verifyOptions( ChartProperty.DATA_LABELS ),
        "TS026982: Options on the Data Labels property are not as expected!" );
    softAssert.assertTrue( line.verifyOptions( ChartProperty.TREND_TYPE ),
        "TS026982: Options on the Trend Type property are not as expected!" );

    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "checkDefaults" )
  @SpiraTestSteps( testStepsId = "26983, 26984, 31104, 31103, 31102, 31101, 31100, 38092, 86713" )
  public void optionsTests() {
    // Add fields to chart
    SoftAssert softAssert = new SoftAssert();
    Integer expectedSize = 32;
    Integer numberOfLines = 8;
    columns.add( "Territory" );
    rows.add( "Line" );
    measures.add( "Sales" );
    analyzerPage.addFields( rows, columns, measures );
    // Assert Circle bullets are shown both on chart and on legend
    softAssert.assertTrue( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_CIRCLE, expectedSize ),
        "TS026983: Legend is not showing circle icon!" );
    // Select "Cross" on Bullet Style drop down and assert Cross bullets are shown both on chart and on legend
    line.selectOption( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_CROSS );
    softAssert.assertTrue( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_CROSS, expectedSize ),
        "TS086713: Legend is not showing cross icon!" );
    // Select "Diamond" on Bullet Style drop down and assert Diamond bullets are shown both on chart and on legend
    line.selectOption( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_DIAMOND );
    softAssert.assertTrue( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_DIAMOND, expectedSize ),
        "TS031101: Legend is not showing diamond icon!" );
    // Select "Square" on Bullet Style drop down and assert Square bullets are shown both on chart and on legend
    line.selectOption( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_SQUARE );
    softAssert.assertTrue( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_SQUARE, expectedSize ),
        "TS031102: Legend is not showing square icon!" );
    // Select "Triangle" on Bullet Style drop down and assert Triangle bullets are shown both on chart and on legend
    line.selectOption( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_TRIANGLE );
    softAssert.assertTrue( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_TRIANGLE, expectedSize ),
        "TS026983: Legend is not showing triangle icon!" );
    // Select "None" on Bullet Style drop down and assert no bullets are shown both on chart and on legend
    line.selectOption( ChartProperty.BULLET_STYLE, ChartPropertyOption.BULLET_STYLE_NONE );
    softAssert.assertFalse( line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_CIRCLE, expectedSize )
        || line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_CROSS, expectedSize )
        || line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_DIAMOND, expectedSize )
        || line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_SQUARE, expectedSize )
        || line.isLegendIconExpected( ChartPropertyOption.BULLET_STYLE_TRIANGLE, expectedSize ),
        "TS031104: Legend is showing something more then just the line icon!" );

    line.selectOption( ChartProperty.LINE_WIDTH, ChartPropertyOption.LINE_WIDTH_5 );
    softAssert.assertTrue( line.isLineWidthWorking( ChartPropertyOption.LINE_WIDTH_5, numberOfLines ),
        "TS026984: Line width option is not working!" );
    softAssert.assertAll();
  }
}
