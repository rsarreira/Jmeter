package com.pentaho.qa.web.analyzer;

import static org.testng.Assert.fail;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.pentaho.qa.gui.web.analyzer.ReportOptionsPage;
import com.pentaho.qa.gui.web.analyzer.ReportOptionsPage.ReportOption;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

//http://spiratest.pentaho.com/8/TestCase/11785.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.CreatedMeasuresSmoketest )
public class CreatedMeasuresSmoketest extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private AnalyzerReportPage analyzerPage;
  private String field;
  private String tsID;
  private PAReport paReport;
  private HomePage homePage;
  private Folder folder;
  private ReportOptionsPage reportOptions;

  @Test
  public void testLogin() {
    homePage = webUser.login();

    Assert.assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName()
        + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testLogin" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR05" )
  @SpiraTestSteps( testStepsId = "62614" )
  public void createReport( Map<String, String> args ) {
    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    field = "Territory";
    analyzerPage.addFieldToSection( GemBarType.ROWS, field );

    field = "Sales";
    analyzerPage.changeChartType( ChartType.SCATTER );
    analyzerPage.addFieldToSection( GemBarType.Y, field );

    AnalyzerReportPage analyzerPage = new AnalyzerReportPage( getDriver() );

    analyzerPage.openDlgMeasureRankRunningSum( field );
    if ( !analyzerPage.isDlgRankRunningSumOpened() ) {
      Assert.fail( "TS062615: The dialog 'New % of, Rank, Running Sum, etc' is not opened!" );
    }
  }

  @Test( dependsOnMethods = "createReport" )
  @SpiraTestSteps( testStepsId = "62615" )
  public void addCalculatedMeasure() {
    field = L10N.getText( "dlgSummaryEditPctOf" ) + " Sales";

    analyzerPage.addCalculatedMeasurePercentOfField();
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      Assert.fail( "TS062615: The field " + field + " is not added!" );
    }
  }

  @Test( dependsOnMethods = "addCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "62616" )
  public void sortCalculatedMeasure() {
    analyzerPage.changeToPivotView();
    List<String> beforeSort = analyzerPage.getColumnsValues();

    analyzerPage.openPopupMenuLayout( field );
    analyzerPage.sortHighLow();

    List<String> afterSort = analyzerPage.getColumnsValues();

    if ( beforeSort.equals( afterSort ) ) {
      Assert.fail( "TS062616: Calculated member " + field + " is not sorted successfully!" );
    }
  }

  @Test( dependsOnMethods = "sortCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "62617" )
  public void verifyCalculatedMemberOnEachChart() {
    tsID = "TS062617";

    // verification part
    verifyInEachChartField( tsID, field );

    paReport.save( folder );
    paReport.close();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "verifyCalculatedMemberOnEachChart" )
  @SpiraTestSteps( testStepsId = "62618" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR05" )
  public void grandTotal( Map<String, String> args ) {
    String total = "100.00%";
    reportOptions = new ReportOptionsPage( getDriver() );
    homePage = new HomePage( getDriver() );
    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    analyzerPage.changeChartType( ChartType.SCATTER );
    field = "Line";
    analyzerPage.addFieldToSection( GemBarType.ROWS, field );

    field = "Quantity";
    analyzerPage.addFieldToSection( GemBarType.X, field );

    // add CalculatedMeasurePercentOfField
    analyzerPage.openDlgMeasureRankRunningSum( field );
    analyzerPage.addCalculatedMeasurePercentOfField();

    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      Assert.fail( "TS062618: The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeToPivotView();

    analyzerPage.openReportOptions();

    if ( !reportOptions.isOpened() ) {
      Assert.fail( "TS062618: The dialog ' Report Options' is not opened!" );
    }

    reportOptions.check( ReportOption.SHOW_COLUMNS_GRAND_TOTALS );
    analyzerPage.clickDlgBtnSave();

    List<String> afterSort = analyzerPage.getColumnsValues();
    if ( !afterSort.contains( total ) ) {
      Assert.fail( "TS062618: The Grand Total doesn't show 100.00%" );
    }
  }

  @Test( dependsOnMethods = "grandTotal" )
  @SpiraTestSteps( testStepsId = "62619" )
  public void verifyCalculatedMemberOnEachChart2() {
    field = L10N.getText( "dlgSummaryEditPctOf" ) + " Quantity";
    tsID = "TS062619";

    // verification part
    verifyInEachChartField( tsID, field );
  }

  private void verifyInEachChartField( String tsId, String field ) {
    SoftAssert softAssert = new SoftAssert();

    analyzerPage.changeChartType( ChartType.AREA );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.COLUMN );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.COLUMN_LINE_COMBO );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.NORMALIZED_STACKED_COLUMN );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.STACKED_COLUMN );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.HEATGRID );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.BAR );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.NORMALIZED_STACKED_BAR );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.STACKED_BAR );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.LINE );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.PIE );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.SCATTER );
    if ( !analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }

    analyzerPage.changeChartType( ChartType.SUNBURST );
    if ( analyzerPage.isLayoutItemPresent( field ) ) {
      softAssert.fail( tsId + " : The field " + field + " is not displayed on the current chart!" );
    }
    softAssert.assertAll();
  }
}
