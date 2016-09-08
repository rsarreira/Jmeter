package com.pentaho.qa.web.analyzer;

import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar;
import com.pentaho.qa.gui.web.analyzer.chart.ChartFactory;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartVerifier;
import com.pentaho.qa.gui.web.puc.BasePage.View;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.analyzer.PAFilter;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.utils.ExportType;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.Spira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//DESIRED SETTINGS FOR EXECUTION:
//driver_mode=method_mode
//recovery=true
//retry_count=1
//thread_count=3

//https://spiratest.pentaho.com/6/TestCase/8970.aspx
@SuppressWarnings( "serial" )
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.PA_SniffTest )
public class PA_SniffTest extends WebBaseTest {
  private Map<String, String> chartReportArgs = null;
  
  private static final String REPORT_NAME = "sniff_filters";
  private static final String REPORT_DATASOURCE = "SteelWheels: SteelWheelsSales";
  private static final String REPORT_ID = "auto";
  
  private static final String ANALYZER_FILTERS_SHEET = "Filters";
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private static final List<String> DEFAULT_FIELDS = new ArrayList<String>( 6) {
    {
      add( "Territory" );
      add( "Country" );
      add( "Quantity" );
      add( "Sales" );
      add( "Line" );
      add( "Vendor" );
    }
  };
  private static final List<String> DEFAULT_SHORT_FIELDS = new ArrayList<String>( 2) {
    {
      /* add( "Territory" ); */
      add( "Sales" );
    }
  };
  private static final List<String> MODIFIED_FIELDS_CITY = new ArrayList<String>( 1) {
    {
      add( "City" );
    }
  };
  private static final List<String> MODIFIED_FIELDS_YEARS = new ArrayList<String>( 1) {
    {
      add( "Years" );
    }
  };
  
  private static ThreadLocal<PAReport> paReports = new ThreadLocal<PAReport>();
  private static PAReport paReportSaved;

  private ChartFactory chartFactory = new ChartFactory();

  @BeforeMethod
  @Parameters( { "user", "password" } )
  public void prepareEnvironment( String user, String password ) {
    // login using admin credentials
    HomePage homePage = webUser.login();
    homePage.loading();
    assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  /**
   * Step #1
   */
  @Test()
  @SpiraTestSteps( testStepsId = "41295" )
  public void testMenuAbout() {
    HomePage homePage = new HomePage( getDriver() );
    homePage.verifyMenuAbout( appVersion );
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# BACKLOG-10136" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR05" )
  @SpiraTestSteps( testStepsId = "41296,41297,41299,41300,41394,41395,41301,41302,41303" )
  public void testReportCreationOptions( Map<String, String> args ) {
    
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ));
    
    /**
     * Step #2-3
     */
    LOGGER.info( Spira.getSteps() );
    setReport( new PAReport( args ) );
    AnalyzerReportPage analyzerPage = getReport().create(); // create empty PA report

    /**
     * Step #4-5
     */
    LOGGER.info( Spira.getSteps() );
    analyzerPage.changeView( View.TYPE );
    analyzerPage.changeView( View.NAME );
    analyzerPage.changeView( View.SCHEMA );
    analyzerPage.changeView( View.CATEGORY );

    /**
     * Step #6-7. Checks default images and drop areas of layout panel for all chart types.
     */
    for ( ChartType type : ChartType.values() ) {
      analyzerPage.changeChartType( type );

      // checks images
      if ( !analyzerPage.verifyDefaultChartImage( type.getId().toUpperCase() ) ) {
        fail( "TS041394: Start image is not present for '" + type + "!'" );
      }

      // checks gembars
      List<GemBar> gems = type.getGemBars();
      for ( GemBar gem : gems ) {
        if ( !analyzerPage.verifyGemBarLable( gem.getLabel() ) ) {
          fail( "TS041395: Layout panel does not contain '" + gem.getLabel() + "' label for chart '" + type.getId()
              + "'!" );
        }
        if ( !analyzerPage.verifyGemBarDropArea( gem.getType().getId() ) ) {
          fail( "TS041395: Layout panel does not contain '" + gem.getType().getId() + "' drop area for chart '" + type
              .getId() + "'!" );
        }
      }
    }

    /**
     * Step #8
     */
    analyzerPage.disableAutoRefresh();
    analyzerPage.changeView( View.NAME );
    analyzerPage.addFieldsByDefault( DEFAULT_FIELDS );
    if ( !analyzerPage.verifyRefreshingMsgBar() ) {
      fail(
          "TS041301: Report definition has been modified message bar does not appear at the top with the Refresh button!" );
    }

    /**
     * Step #9
     */
    analyzerPage.refreshReportBySpecialButton();
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );

    /**
     * Step #10
     */
    getReport().save( folder );
    getReport().close();
    analyzerPage = getReport().open();
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );
    getReport().close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "41304,41305,41308" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR06" )
  public void testAutoRefresh( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReportSaved = new PAReport( args );

    /**
     * Step #11
     */
    AnalyzerReportPage analyzerPage = paReportSaved.create();
    analyzerPage.enableAutoRefresh();
    analyzerPage.changeView( View.SCHEMA );
    analyzerPage.addFieldsByDefault( DEFAULT_FIELDS );
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );

    /**
     * Step #12
     */
    paReportSaved.save( folder );
    paReportSaved.close();
    analyzerPage = paReportSaved.open();
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );

    /**
     * Step #13
     */
    analyzerPage.addFieldsByDefault( MODIFIED_FIELDS_CITY );
    paReportSaved.save();

    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );
  }

  @Test( dataProvider = "SingleDataProvider" , dependsOnMethods="testAutoRefresh" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR06_EDIT" )
  @SpiraTestSteps( testStepsId = "41397" )
  public void testSaveAs( Map<String, String> args ) {
    /**
     * Step #14
     */
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    setReport( new PAReport( args ) );

    paReportSaved.open();
    paReportSaved.edit( getReport() ); // update only name in the report
    AnalyzerReportPage analyzerPage = paReportSaved.getReportPage();

    analyzerPage.enableAutoRefresh();
    analyzerPage.addFieldsByDefault( MODIFIED_FIELDS_YEARS );

    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_YEARS );

    PAReport paReportSavedAs = (PAReport) paReportSaved.saveAs(folder);
    paReportSavedAs.close();

    /**
     * Step #15
     */
    analyzerPage = paReportSaved.open();
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );
    analyzerPage.verifyNotPresentingReportFields( MODIFIED_FIELDS_YEARS );
    paReportSaved.close();

    analyzerPage = paReportSavedAs.open();
    analyzerPage.verifyPresentingReportFields( DEFAULT_FIELDS );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_YEARS );
    paReportSavedAs.close();
  }

  /**
   * Step #16-20.
   */
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR07" )
  @SpiraTestSteps( testStepsId = "41309" )
  public void testPrepareCharts( Map<String, String> args ) {
    chartReportArgs = args;
  }

  @Test( dataProvider = "DataProvider" , dependsOnMethods="testPrepareCharts" )
  @XlsDataSourceParameters( sheet = "Chart", dsUid = "TUID,ChartType", dsArgs = "ChartType, location",
      executeColumn = "TeststepId", executeValue = "41410" )
  @SpiraTestSteps( testStepsId = "41408, 41419, 41410, 41413, 41417" )
  public void testCharts( String chartType, String location ) {
    Folder folder = getUserHome();
    setReport( new PAReport( chartReportArgs ) );

    ChartType type = ChartType.valueOf( chartType );

    getReport().setName( getReport().getName() + "_" + chartType ); // to resolve issue with overwrite existing

    AnalyzerReportPage analyzerPage = getReport().create();
    analyzerPage.changeChartType( type );

    ChartVerifier chartVerifier = chartFactory.getChartVerifier( getDriver(), type );
    SoftAssert softAssert = chartVerifier.verifyAddingFields();
    softAssert.assertAll();

    softAssert = chartVerifier.verifyAddingWrongFields();
    softAssert.assertAll();

    softAssert = chartVerifier.verifyContent();
    softAssert.assertAll();

    getReport().save( folder );
    getReport().close();
    getReport().open();
    softAssert = chartVerifier.verifyContent();
    softAssert.assertAll();

    softAssert = chartVerifier.verifyDrillDown();
    softAssert.assertAll();

    softAssert = chartVerifier.verifyAddingMultiChart();
    softAssert.assertAll();

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR09" )
  @SpiraTestSteps( testStepsId = "41384,41385" )
  public void testToggleAndExport( Map<String, String> args ) {
    /**
     * Step #21
     */
    setReport( new PAReport( args ) );
    AnalyzerReportPage analyzerPage = getReport().create();

    analyzerPage.enableAutoRefresh();
    analyzerPage.addFieldsByDefault( MODIFIED_FIELDS_CITY );
    analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );

    analyzerPage.disableAutoRefresh();
    analyzerPage.addFieldsByDefault( DEFAULT_SHORT_FIELDS );
    analyzerPage.verifyNotPresentingReportFields( DEFAULT_SHORT_FIELDS );
    // analyzerPage.addFieldsByDefault( MODIFIED_FIELDS_YEARS );
    // analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_CITY );
    // analyzerPage.verifyNotPresentingReportFields( MODIFIED_FIELDS_YEARS );
    analyzerPage.verifyRefreshingMsgBar();

    analyzerPage.refreshReportBySpecialButton();
    // analyzerPage.verifyPresentingReportFields( MODIFIED_FIELDS_YEARS );
    analyzerPage.verifyPresentingReportFields( DEFAULT_SHORT_FIELDS );

    Folder folder = getUserHome();
    getReport().save( folder );

    /**
     * Step #22 for pivot
     */
    verifyExporting( getReport() );

    /**
     * Step #22 for chart
     */
    // analyzerPage.addFieldsByDefault( DEFAULT_SHORT_FIELDS );
    analyzerPage.changeChartType( ChartType.COLUMN );
    analyzerPage.refreshReportBySpecialButton();
    getReport().save();
    verifyExporting( getReport() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "FLTR28" )
  @SpiraTestSteps( testStepsId = "41386,41387,41388,41389,41390" )
  public void testFilters( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );

    setReport( new PAReport( REPORT_NAME, false, null, false, REPORT_DATASOURCE, REPORT_ID ) );
    getReport().setRows( MODIFIED_FIELDS_CITY );
    AnalyzerReportPage analyzerPage = getReport().create();

    paFilter.create( Workflow.CONTEXT_PANEL );
    pause( 3 );
    analyzerPage.verifyTableContent( args.get( "VerifyPresent" ), args.get( "VerifyNotPresent" ) ).assertAll();

    paFilter.delete();
    analyzerPage.verifyTableContent( args.get( "VerifyPresent" + ";" + args.get( "VerifyNotPresent" ) ), "" )
        .assertAll();

    analyzerPage.undo();
    analyzerPage.verifyTableContent( args.get( "VerifyPresent" ), args.get( "VerifyNotPresent" ) ).assertAll();

    analyzerPage.redo();
    analyzerPage.verifyTableContent( args.get( "VerifyPresent" + ";" + args.get( "VerifyNotPresent" ) ), "" )
        .assertAll();

    analyzerPage.resetReport();
    if ( !analyzerPage.getMembersFromPivot().isEmpty() ) {
      fail( "Report is not reseted!" );
    }

    SoftAssert softAssert = new SoftAssert();
    if ( !analyzerPage.resizeFieldPanel() ) {
      softAssert.fail( "Field layout panel is not resized!" );
    }
    if ( !analyzerPage.resizeLayoutPanel() ) {
      softAssert.fail( "Layout panel is not resized!" );
    }
    softAssert.assertAll();
  }

  private void verifyExporting( PAReport report ) {
    // TODO: implement soft assert to test all three exports and only after that raise exception in case of any issue
    getReport().exportAs( ExportType.PDF );
    getReport().exportAs( ExportType.CSV );
    getReport().exportAs( ExportType.EXCEL );
  }

  private static PAReport getReport() {
    return paReports.get();
  }

  private static void setReport( PAReport report ) {
    paReports.set( report );
  }
}
