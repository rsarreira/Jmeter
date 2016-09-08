package com.pentaho.qa.web.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.TotalType;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.WorkArea;
import com.pentaho.qa.gui.web.analyzer.CalculatedMeasurePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.analyzer.PAFilter;
import com.pentaho.services.analyzer.PAMeasure;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.AnalysisDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/8/TestCase/11714.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.CalculatedMembersFunctions )
public class CalculatedMembersFunctions extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private static final String ANALYZER_FILTERS_SHEET = "Filters";
  private static final String ANALYZER_CALCULATED_MEASURES_SHEET = "CalculatedMeasures";

  private PAReport paReport;
  private AnalyzerReportPage analyzerReportPage;
  private PAMeasure paMeasure;
  private String salesField;
  private AnalyzerFilterPage analyzerFilterPage;
  private CalculatedMeasurePage calculatedMeasurePage;
  private Folder folder;
  private JdbcConnection connection;
  private AnalysisDataSource analysisDS;
  private String canvasValue;

  @Test
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "testLogin" )
  @SpiraTestSteps( testStepsId = "61670" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR21" )
  public void createStheelWheelsReport( Map<String, String> args ) {
    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReport = new PAReport( args );
    analyzerReportPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createStheelWheelsReport" )
  @SpiraTestSteps( testStepsId = "61670" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08" )
  public void addFilterYear( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    paFilter.verify();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "addFilterYear" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08_1" )
  public void addFilterMonths( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    paFilter.verify();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "addFilterMonths" )
  @SpiraTestSteps( testStepsId = "61670" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02" )
  public void addCalculatedMeasure( Map<String, String> args ) {
    paMeasure = new PAMeasure( args );
    paMeasure.create();
    paMeasure.verify();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "addCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "61671" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02" )
  public void numericFilterTopTen( Map<String, String> args ) {
    salesField = args.get( "Field" );
    String valueNumberFilter = "Product";

    // set 'Sum' subtotals for both of the measures
    analyzerReportPage.setMeasureSubtotal( salesField, WorkArea.CANVAS, TotalType.SUM );
    analyzerReportPage.setMeasureSubtotal( paMeasure.getName(), WorkArea.CANVAS, TotalType.SUM );

    List<String> columnsValueOld = analyzerReportPage.getColumnsValues();

    // set value in Numeric filter
    analyzerReportPage.openDlgNumericFilter( paMeasure.getName(), WorkArea.LAYOUT_PANEL );
    analyzerFilterPage = new AnalyzerFilterPage( getDriver() );
    analyzerFilterPage.setFilterOnAttrNumFilter( valueNumberFilter );
    analyzerReportPage.clickDlgBtnSave();

    // verification part
    List<String> columnsValueFiltered = analyzerReportPage.getColumnsValues();

    if ( columnsValueOld.equals( columnsValueFiltered ) ) {
      Assert.fail( "TS061671: Filter 'Top 10, etc...' is not applied!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "numericFilterTopTen" )
  @SpiraTestSteps( testStepsId = "61672,61673" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_1" )
  public void invalidFormulaDefinition( Map<String, String> args ) {
    String formula = args.get( "Formula" );
    String errorMsg =
        "Invalid formula definition. Details: Mondrian Error:Syntax error at line 1, column 10, token '[Measures]'";

    calculatedMeasurePage = analyzerReportPage.editCalculatedMeasure( paMeasure );
    calculatedMeasurePage.setMeasureFormula( formula );
    analyzerReportPage.clickDlgBtnSave();

    // verification part
    if ( !errorMsg.equals( calculatedMeasurePage.getDlgErrorMessage() ) ) {
      Assert.fail( "TS061672: The error message is not matched expected error message!" );
    }

    analyzerReportPage.clickDlgBtnCancel();

    if ( !paMeasure.verify() ) {
      Assert.fail( "TS061673: The calculated measure was changed!!" );
    }

    paReport.save( folder );
    paReport.close();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "invalidFormulaDefinition" )
  @SpiraTestSteps( testStepsId = "61674" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR17" )
  public void createReportOnlyWithMeasures( Map<String, String> args ) {
    paReport = new PAReport( args );
    analyzerReportPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createReportOnlyWithMeasures" )
  @SpiraTestSteps( testStepsId = "61674" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_2" )
  public void expressionFormulaCalculatedMeasure( Map<String, String> args ) {
    paMeasure = new PAMeasure( args );
    paMeasure.create();
    paMeasure.verify();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "expressionFormulaCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "61674" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08_2" )
  public
    void applyingFilterWithoutAddingFieldToCanvas( Map<String, String> args ) {
    PAFilter paFilter2 = new PAFilter( args );
    paFilter2.create( Workflow.CONTEXT_PANEL );
    paFilter2.verify();

    // verification part
    String[] values = { "49,578", "5,008,224", "16,666" };
    pause( 3 );
    List<String> columnsValues = new ArrayList<String>( Arrays.asList( values ) );

    if ( !columnsValues.equals( analyzerReportPage.getColumnsValues() ) ) {
      Assert.fail( "TS061674: Expected values do not match with the received!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true,
      dependsOnMethods = "applyingFilterWithoutAddingFieldToCanvas" )
  @SpiraTestSteps( testStepsId = "61675" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_3" )
  public void aggregateFormulaCalculatedMeasure( Map<String, String> args ) {
    paMeasure = new PAMeasure( args );
    paMeasure.create();
    paMeasure.verify();

    // verification part
    canvasValue = "-";

    if ( !analyzerReportPage.getColumnsValues().contains( canvasValue ) ) {
      Assert.fail( "TS061675: Expected value " + canvasValue + " do not match with the received!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "aggregateFormulaCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "61676" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08" )
  public void addFilterYear2( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    canvasValue = "23,630";
    pause( 3 );
    // verification part
    if ( !analyzerReportPage.getColumnsValues().contains( canvasValue ) ) {
      Assert.fail( "TS061675: Expected value " + canvasValue + " do not match with the received!" );
    }
    paReport.close( false );
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "addFilterYear2" )
  @XlsDataSourceParameters( sheet = "Analysis_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSA03" )
  @SpiraTestSteps( testStepsId = "61677" )
  public void importSteelWheelsMbsAnalysis( Map<String, String> args ) {
    analysisDS = new AnalysisDataSource( args );
    analysisDS.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "importSteelWheelsMbsAnalysis" )
  @SpiraTestSteps( testStepsId = "61677" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR21_3" )
  public void createSteelWheelsMbsReport( Map<String, String> args ) {
    paReport = new PAReport( args );
    analyzerReportPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# ANALYZER-3330",
      dependsOnMethods = "createSteelWheelsMbsReport" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08_3" )
  public void addFilterMonths2( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    paFilter.verify();
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# ANALYZER-3330", alwaysRun = true,
      dependsOnMethods = "addFilterMonths2" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR08_4" )
  public void addFilterType( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    paFilter.verify();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "addFilterType" )
  @SpiraTestSteps( testStepsId = "61678" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID", executeValue = "JDBC01" )
  public
    void createFoodmartConnection( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BACKLOG-7685" );
    }

    connection = new JdbcConnection( args );
    connection.create();
    LOGGER.info( "Connection name = " + connection.getName() );
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createFoodmartConnection" )
  @XlsDataSourceParameters( sheet = "Analysis_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSA02" )
  @SpiraTestSteps( testStepsId = "61678" )
  public void importFoodmartAnalysis( Map<String, String> args ) {
    analysisDS = new AnalysisDataSource( args );
    analysisDS.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "importFoodmartAnalysis" )
  @SpiraTestSteps( testStepsId = "61678" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR21_1" )
  public void createFoodmartReport( Map<String, String> args ) {
    paReport = new PAReport( args );
    analyzerReportPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createFoodmartReport" )
  @SpiraTestSteps( testStepsId = "61678" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_4" )
  public void medianFormulaCalculatedMeasure( Map<String, String> args ) {

    paMeasure = new PAMeasure( args );
    paMeasure.create();
    paMeasure.verify();

    List<String> expectedValues = new ArrayList<String>( Arrays.asList( "74,748", "74,748", "74,748" ) );
    List<String> measureValues = analyzerReportPage.getCanvasValuesForColumn( args.get( "Name" ) );

    // verification part
    if ( !expectedValues.equals( measureValues ) ) {
      Assert.fail( "TS061678: Expected values do not match with the received!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "medianFormulaCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "61679" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_5" )
  public void editMedianCalculatedMeasure( Map<String, String> args ) {
    List<String> unitSalesValues = analyzerReportPage.getCanvasValuesForColumn( args.get( "Field" ) );

    PAMeasure paMeasureEdited = new PAMeasure( args );
    paMeasure.edit( paMeasureEdited );

    List<String> measureValues = analyzerReportPage.getCanvasValuesForColumn( args.get( "Name" ) );

    // verification part
    if ( !unitSalesValues.equals( measureValues ) ) {
      Assert.fail( "TS061679: Expected valuess do not match with the received!" );
    }

    paReport.save( folder );
    paReport.close();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "editMedianCalculatedMeasure" )
  @SpiraTestSteps( testStepsId = "61678" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR21_2" )
  public void createSteelWheelsReport2( Map<String, String> args ) {
    paReport = new PAReport( args );
    analyzerReportPage = paReport.create();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createSteelWheelsReport2" )
  @SpiraTestSteps( testStepsId = "61680" )
  @XlsDataSourceParameters( sheet = ANALYZER_CALCULATED_MEASURES_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "CM02_6" )
  public void devineByZeroFormula( Map<String, String> args ) {
    paMeasure = new PAMeasure( args );
    paMeasure.create();
    paMeasure.verify();

    String devineZero = "N/A";
    List<String> measureValues = analyzerReportPage.getCanvasValuesForColumn( args.get( "Name" ) );

    // verification part
    if ( !measureValues.contains( devineZero ) ) {
      Assert.fail( "TS061680: Expected values do not match with the received!" );
    }
  }
}
