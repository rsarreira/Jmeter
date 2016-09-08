//http://spiratest.pentaho.com/8/TestCase/11708.aspx

//DESIRED SETTINGS FOR EXECUTION:
//driver_mode=method_mode
//recovery=true
//retry_count=1
//thread_count=3

package com.pentaho.qa.web.analyzer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.BeginDatePickerPage;
import com.pentaho.qa.gui.web.analyzer.EndDatePickerPage;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Filter.DataType;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.analyzer.PAFilter;
import com.pentaho.services.analyzer.PAFilter.FilterConditionType;
import com.pentaho.services.analyzer.PAFilter.TimePeriod;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.ParameterGenerator;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_FilterSmoketest )
public class Analyzer_FilterSmoketest extends WebBaseTest {

  private static final String ANALYZER_FILTERS_SHEET = "Filters";
  private static final String SAPLE_FIELD_NAME = "Year";
  private static final String SAPLE_CUSTOMER_FIELD_NAME = "CUSTOMERNAME";

  // data source
  private static final Boolean CUSTOMIZED_MODEL = false;
  private static final String FILE_PATH = "src/test/resources/pentaho_data/TestCSV(Added YEARS 2011-2018).csv";
  private static final String ITEM_LEVEL1 = "/Dimensions/Year/";
  private static final String ITEM_LEVEL3 = "/Dimensions/Year/Year/Year/";
  private static final String LEVEL_TYPE = "Years";
  private static final String COLUMN_FORMAT = "yyyy";
  private static final String VALUE = "auto-moto";

  // analyzer report
  private static final String LOCATION = "/home/admin";
  private static final String DATA_SOURCE = "CSV_{generate_uuid}";
  private static final String ROWS = "CUSTOMERNAME,QUANTITYORDERED,Year";
  private static final boolean AUTOREFRESH = true;
  
  // analyzer report SteelWheels data source
  private static final String REPORT_NAME_SW = "analyzerReport_sw";
  private static final String DATA_SOURCE2 = "SteelWheels";
  private static final String ROWS2 = "Territory,Country,Years";
  private static final String ROWS3 = "Years,Quarters,Months";
  private static final String COLUMNS = "Type";
  private static final String MEASURES = "Quantity,Sales";
  private static final String COLUMN_TO_EXLUDE = "Disputed";
  
  private static final String PRESENT_ITEMS = "2003;2004;2005";
  private static final String NOT_PRESENT_ITEMS = "2002;2006";
  
  private static final String QUATER = "QTR1";
  private static final String NEW_BETWEEN_RANGE_END_DATE = "2005-01-01";
  
  private static ThreadLocal<PAReport> paReports = new ThreadLocal<PAReport>();
  private static ThreadLocal<AnalyzerReportPage> reportPages = new ThreadLocal<AnalyzerReportPage>();
  private static CSVDataSource csvDataSource;
  private static int currentYear;
  private static PAReport paReport;
  
  //combine filter
  private static final String FILTER_NAME = "combineFilter";
  private static final boolean SPECIFY_CONDITION = false;
  private static final FilterConditionType FILTER_CONDITION_TYPE = FilterConditionType.COMONNLY_USED_TIME_PERIOD;
  private static final TimePeriod TIME_PERIOD = TimePeriod.NEXT_YEAR;

  @BeforeClass
  @Parameters( { "DS_name", "Report_name" } )
  public void prepareTests( String dsName, String reportName ) {
    webUser.login( createExtraDriver( "extraDriver" ) );
    createCSVDataSource( dsName );
    createAndSaveAnalyzerReport( reportName );

    Calendar now = Calendar.getInstance();
    currentYear = now.get( Calendar.YEAR );

    quitExtraDriver();
  }

  @BeforeMethod
  public void login() {
    webUser.login( getDriver() );
    setReport( paReport );
    setReportPage( paReport.open() );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "61575,61576" )
  public void verifyFilterPageOptions() {
    
    AnalyzerFilterPage filterPage = getReportPage().addFilter( SAPLE_FIELD_NAME, Workflow.CONTEXT_PANEL );
    filterPage.verifyFilterOptions();
    filterPage.verifyTimePeriodOptions();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR02" )
  @SpiraTestSteps( testStepsId = "61577" )
  public void currentYearFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( String.valueOf( currentYear ), ( ( currentYear + 1 ) + ";" + ( currentYear
        - 1 ) ) ).assertAll();;
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR09" )
  @SpiraTestSteps( testStepsId = "61578" )
  public void previousYearFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( String.valueOf( currentYear - 1 ), String.valueOf( currentYear ) ).assertAll();;
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR10" )
  @SpiraTestSteps( testStepsId = "61579" )
  public void nextYearFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( String.valueOf( currentYear + 1 ), String.valueOf( currentYear ) ).assertAll();;
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR11" )
  @SpiraTestSteps( testStepsId = "61580" )
  public void previousXYearsFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(2);
    getReportPage().verifyTableContent( ( currentYear - 1 ) + ";" + ( currentYear - 2 ), currentYear + ";"
        + ( currentYear - 3 ) ).assertAll();;
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR12" )
  @SpiraTestSteps( testStepsId = "61581" )
  public void nextXYearsFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( ( currentYear + 1 ) + ";" + ( currentYear + 2 ), String.valueOf(
        currentYear ) ).assertAll();;
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR13" )
  @SpiraTestSteps( testStepsId = "61582" )
  public void xYearsAgoFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( String.valueOf( currentYear - 2 ), ( ( currentYear + 1 ) + ";" + ( currentYear
        - 1 ) + ";" + currentYear ) ).assertAll();;
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR14" )
  @SpiraTestSteps( testStepsId = "61583" )
  public void xYearsAheadFilter( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( String.valueOf( currentYear + 2 ), ( ( currentYear + 1 ) + ";" + ( currentYear
        - 1 ) + ";" + currentYear ) ).assertAll();;
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR02" )
  @SpiraTestSteps( testStepsId = "61584" )
  public void combineFilters( Map<String, String> args ) {
    // add first filter
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );

    PAFilter paFilter2 = new PAFilter( "Year" );
    paFilter2.setName( FILTER_NAME );
    paFilter2.setSpecifyCondition( SPECIFY_CONDITION );
    paFilter2.setConditionType( FILTER_CONDITION_TYPE );
    paFilter2.setTimePeriod( TIME_PERIOD );
    // add second filter
    paFilter2.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().verifyTableContent( ( currentYear + ";" + ( currentYear + 1 ) ), ( ( currentYear + 2 ) + ";"
        + ( currentYear - 1 ) ) ).assertAll();;
  }
  
  @Test( )
  @SpiraTestSteps( testStepsId = "61585" )
  public void verifyListOption() {
    AnalyzerFilterPage filterPage = getReportPage().addFilter( SAPLE_FIELD_NAME, Workflow.CONTEXT_PANEL );
    filterPage.setConditionType( FilterConditionType.SELECT_FROM_LIST );
    filterPage.verifySelectFromListOption();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "61586,61587" )
  public void filterFieldTest() {
    AnalyzerFilterPage filterPage = getReportPage().addFilter( SAPLE_FIELD_NAME, Workflow.CONTEXT_PANEL );
    filterPage.setConditionType( FilterConditionType.SELECT_FROM_LIST );
    filterPage.typeInFilterField( String.valueOf( currentYear ) );
    filterPage.clickFindButton();
    filterPage.verifyFilterField( String.valueOf( currentYear ) );
    
    filterPage.clearFilterField();
    filterPage.clickFindButton();
    filterPage.verifyEmptyFilterField();
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "Name",
      executeValue = "filterOptions", spiraColumn = "TeststepId" )
  public void listAndRangeFilterOptionsYears( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().removeAttribute( "CUSTOMERNAME" );
    getReportPage().removeAttribute( "QUANTITYORDERED" );
    pause(3);
    getReportPage().verifyTableContent( args.get( "VerifyPresent" ), args.get( "VerifyNotPresent" ) ).assertAll();;
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "Name",
      executeValue = "filterOptions_customername", spiraColumn = "TeststepId" )
  public void listAndRangeFilterOptions( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    getReportPage().removeAttribute( "Year" );
    getReportPage().removeAttribute( "QUANTITYORDERED" );
    pause(3);
    getReportPage().verifyTableContent( args.get( "VerifyPresent" ), args.get( "VerifyNotPresent" ) ).assertAll();;
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "61590" )
  public void verifySelectRangeOption() {
    AnalyzerFilterPage filterPage = getReportPage().addFilter( SAPLE_FIELD_NAME, Workflow.CONTEXT_PANEL );
    filterPage.setConditionType( FilterConditionType.SELECT_RANGE );
    filterPage.verifySelectRangeOption();
  }
  
  @Test( )
  @SpiraTestSteps( testStepsId = "61594" )
  public void verifyCustomerFieldFilterPage() {
    AnalyzerFilterPage filterPage = getReportPage().addFilter( SAPLE_CUSTOMER_FIELD_NAME, Workflow.CONTEXT_PANEL );
    filterPage.verify( DataType.STRING, SAPLE_CUSTOMER_FIELD_NAME );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "Name",
      executeValue = "containsSubstringField", spiraColumn = "TeststepId" )
  public void containsSubstringFieldTest( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    if ( !getReportPage().verifyColumnElementsContainsText( paFilter.getField(), paFilter.getValue() ) ) {
      Assert.fail( "Column does not match the filter!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR23" )
  @SpiraTestSteps( testStepsId = "61598" )
  public void notContainsSubstringFieldTest( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    pause(3);
    if ( !getReportPage().verifyColumnElementsNotContainsText( paFilter.getField(), paFilter.getValue() ) ) {
      Assert.fail( "Column does not match the filter!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR25" )
  @SpiraTestSteps( testStepsId = "69801,69802,69803" )
  public void addAnotherValueTest( Map<String, String> args ) {
    PAFilter paFilter = new PAFilter( args );

    AnalyzerReportPage analyzerReportPage = new AnalyzerReportPage( getDriver() );
    AnalyzerFilterPage filterPage = analyzerReportPage.addFilter( paFilter.getField(), Workflow.CONTEXT_PANEL );
    filterPage.specifyCondition( paFilter.getSpecifyCondition() );
    filterPage.setCondition( paFilter.getCondition() );
    filterPage.setValue( paFilter.getValue() );
    filterPage.addAnotherValue( VALUE );

    filterPage.removeAnotherValue();
    if ( filterPage.isAnotherValueTextBoxPresent() ) {
      Assert.fail( "Another value text box was not removed!" );
    }

    filterPage.addAnotherValue( VALUE );
    filterPage.clickOK();
    pause(3);
    getReportPage().removeAttribute( "Year" );
    getReportPage().removeAttribute( "QUANTITYORDERED" );
    pause(3);
    if (!getReportPage().verifyColumnElementsContainsText( paFilter.getField(), paFilter.getValue() + ";" + VALUE )){
      Assert.fail( "Values of the report are incorrect!" );
    }
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR26" )
  @SpiraTestSteps( testStepsId = "61600,61601" )
  public void typeFieldFilterTest( Map<String, String> args ) {
    getReport().close();
    PAReport report = new PAReport( REPORT_NAME_SW, false, null, true, DATA_SOURCE2, null );
    report.setRows( Arrays.asList( ROWS2.split( "," ) ) );
    report.setColumns( Arrays.asList( COLUMNS.split( "," ) ) );
    report.setMeasures( Arrays.asList( MEASURES.split( "," ) ) );
    report.setAutoRefresh( AUTOREFRESH );
    AnalyzerReportPage analyzerReportPage = report.create();

    PAFilter paFilter = new PAFilter( args );
    paFilter.create( Workflow.CONTEXT_PANEL );
    
    analyzerReportPage.excludeColumn( COLUMN_TO_EXLUDE );
    
    pause(3);
    analyzerReportPage.verifyTableContent( args.get( "VerifyPresent" ), args.get( "VerifyNotPresent" ) ).assertAll();
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_FILTERS_SHEET, dsUid = "Name", executeColumn = "TUID",
      executeValue = "FLTR27" )
  @SpiraTestSteps( testStepsId = "61602,61603,61604,61605,61606,61607" )
  public void quartersFieldFilterTest( Map<String, String> args ) {
    getReport().close();
    PAReport report = new PAReport( REPORT_NAME_SW, false, null, true, DATA_SOURCE2, null );
    report.setRows( Arrays.asList( ROWS3.split( "," ) ) );
    report.setAutoRefresh( AUTOREFRESH );
    AnalyzerReportPage analyzerReportPage = report.create();
    SoftAssert softAssert = analyzerReportPage.verifyTableContent( PRESENT_ITEMS, NOT_PRESENT_ITEMS );

    PAFilter paFilter = new PAFilter( args );

    AnalyzerFilterPage filterPage = analyzerReportPage.addFilter( paFilter.getField(), Workflow.CONTEXT_PANEL );
    filterPage.setConditionType( paFilter.getConditionType() );
    filterPage.clickSelectFromDatePicker();

    List<String> values = Arrays.asList( paFilter.getValue().split( ";" ) );
    List<String> verifyPresentValues = Arrays.asList( args.get( "VerifyPresent" ).split( ";" ) );

    // set beginDatePicker value
    BeginDatePickerPage beginDatePickerPage = filterPage.openBeginDatePicker();
    beginDatePickerPage.selectDate( values.get( 0 ) );
    softAssert.assertTrue( beginDatePickerPage.vrifyErrorMessage( verifyPresentValues.get( 0 ) ),
        "Begin date error message does not appear!" );

    // set endDatePicker value
    EndDatePickerPage endDatePickerPage = filterPage.openEndDatePicker();
    endDatePickerPage.selectDate( values.get( 1 ) );
    softAssert.assertTrue( endDatePickerPage.vrifyErrorMessage( verifyPresentValues.get( 1 ) ),
        "End date error message does not appear!" );

    // set new endDatePicker value
    endDatePickerPage = filterPage.openEndDatePicker();
    endDatePickerPage.selectDate( NEW_BETWEEN_RANGE_END_DATE );
    endDatePickerPage.clickApplyButton();
    softAssert.assertTrue( filterPage.verifyBetweenRangeItems( QUATER, QUATER ),
        "The Filter dialog values are incorrect!" );
    filterPage.clickOK();

    // verify report
    analyzerReportPage = new AnalyzerReportPage( getDriver() );

    List<ExtendedWebElement> elements =
        analyzerReportPage.findExtendedWebElements( By.xpath(
            "//td[contains(@formula,'[Time].[Quarters]') and contains(@member,'[2003]')]" ) );
    softAssert.assertTrue( elements.size() == 4, "In 2003 year wrong number of quarters!" );

    elements =
        analyzerReportPage.findExtendedWebElements( By.xpath(
            "//td[contains(@formula,'[Time].[Quarters]') and contains(@member,'[2004]')]" ) );
    softAssert.assertTrue( elements.size() == 4, "In 2004 year wrong number of quarters!" );

    elements =
        analyzerReportPage.findExtendedWebElements( By.xpath(
            "//td[contains(@formula,'[Time].[Quarters]') and contains(@member,'[2005]')]" ) );
    softAssert.assertTrue( elements.size() == 1, "In 2005 year wrong number of quarters!" );

    softAssert.assertAll();
  }
  
  
  @AfterClass
  public void cleanUp() {
    webUser.login( createExtraDriver( "extraDriver" ) );
    HomePage homePage = new HomePage( getExtraDriver() );
    homePage.activateModuleEx( Module.BROWSE_FILES );
    PAReport paReport = getReport();
    paReport.remove();
    paReport.deletePermanently();
    csvDataSource.delete();
    quitExtraDriver();
  }

  private static PAReport getReport() {
    return paReports.get();
  }

  private static void setReport( PAReport report ) {
    paReports.set( report );
  }

  private static AnalyzerReportPage getReportPage() {
    return reportPages.get();
  }

  private static void setReportPage( AnalyzerReportPage report ) {
    reportPages.set( report );
  }

  private void createCSVDataSource( String dsName ) {
    csvDataSource = new CSVDataSource( getUuidName( dsName ), FILE_PATH, null, null, null, null, CUSTOMIZED_MODEL );
    csvDataSource.create();
    HomePage homePage = new HomePage( getExtraDriver() );

    // customize model
    DataSourceModelEditorPage modelEditorPage = homePage.openManageDataSources().editDataSource( csvDataSource );

    modelEditorPage.selectItem( ITEM_LEVEL1 );
    modelEditorPage.checkTimeDimension();
    modelEditorPage.selectItem( ITEM_LEVEL3 );

    modelEditorPage.selectTimeLevelType( LEVEL_TYPE );
    modelEditorPage.selectColumnFormat( COLUMN_FORMAT );
    modelEditorPage.buttonOK();
    modelEditorPage.clickBtnCloseManageDS();
  }

  private void createAndSaveAnalyzerReport( String reportName ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( LOCATION );
    paReport = new PAReport( getUuidName( reportName ), false, null, true, getUuidName( DATA_SOURCE ), null );
    paReport.setRows( Arrays.asList( ROWS.split( "," ) ) );
    paReport.setAutoRefresh( AUTOREFRESH );
    
    paReport.create();
    paReport.save( folder );
    paReport.close( false );
  }
  
  private String getUuidName( String name ) {
    if ( name.contains( "{generate_uuid}" ) ) {
      name = name.replace( "{generate_uuid}", ParameterGenerator.getUUID() );
    }
    return name;
  }
}
