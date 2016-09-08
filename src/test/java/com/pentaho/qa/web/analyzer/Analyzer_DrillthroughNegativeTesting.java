package com.pentaho.qa.web.analyzer;

import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.ReportOptionsPage;
import com.pentaho.qa.gui.web.analyzer.ReportOptionsPage.ReportOption;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.AnalysisDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.Spira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/8/TestCase/11789.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_DrillthroughNegativeTesting )
public class Analyzer_DrillthroughNegativeTesting extends WebBaseTest {
  private JdbcConnection connection;
  private AnalysisDataSource analysisDS;

  String presentItem;

  List<String> rows = new ArrayList<String>();
  List<String> columns = new ArrayList<String>();
  List<String> measures = new ArrayList<String>();

  @Test
  @Parameters( { "user", "password" } )
  public void testLogin( String user, String password ) {
    // login using admin credentials
    HomePage homePage = webUser.login();
    homePage.loading();
    assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "AnalyzerReports", dsUid = "Title", executeColumn = "TUID", executeValue = "PAR17" )
  @SpiraTestSteps( testStepsId = "62655" )
  public void createAnalyzerReportUsingSteelwheels( Map<String, String> args ) {

    List<String> filterValuesList;
    String presentItem = args.get( "VerifyPresent" );

    LOGGER.info( "Spira test steps ID: " + Spira.getSteps() );
    PAReport report = new PAReport( args );
    LOGGER.info( report.getName() );

    AnalyzerReportPage reportPage = report.create();

    filterValuesList = Arrays.asList( "APAC" );
    reportPage.applyAttributeFilter( "Territory", filterValuesList );

    reportPage.openReportOptions();
    ReportOptionsPage reportOptions = new ReportOptionsPage( getDriver() );
    reportOptions.check( ReportOption.SHOW_DRILL_LINS_ON_MEASURE_CELLS );
    reportOptions.clickDlgBtnSave();

    rows.add( "Line" );
    rows.add( "Product (3)" );
    reportPage.addFields( rows, columns, measures );
    reportPage.openLog();

    ArrayList<String> tabs = new ArrayList<String>( getDriver().getWindowHandles() );

    getDriver().switchTo().window( tabs.get( 1 ) );
    String log = reportPage.getMDX();

    assertTrue( log.toLowerCase().contains( presentItem.toLowerCase() ) );

    closeExtraTabs( tabs );
    getDriver().switchTo().window( tabs.get( 0 ) );
    
    // reportPage.close( false, "Analysis Report" );
    report.close();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "62656" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC01" )
  public void createConnection( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BACKLOG-7685" );
    }

    connection = new JdbcConnection( args );
    connection.create();
    LOGGER.info( "Connection name = " + connection.getName() );

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "Analysis_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSA02" )
  @SpiraTestSteps( testStepsId = "62655" )
  public void importAnalysis( Map<String, String> args ) {
    analysisDS = new AnalysisDataSource( args );
    analysisDS.create();

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "AnalyzerReports", dsUid = "Title", executeColumn = "TUID", executeValue = "PAR18" )
  @SpiraTestSteps( testStepsId = "62656" )
  public void createAnalyzerReportUsingFoodMart( Map<String, String> args ) {

    List<String> filterValuesList;
    String presentItem = args.get( "VerifyPresent" );

    LOGGER.info( "Spira test steps ID: " + Spira.getSteps() );
    PAReport report = new PAReport( args );
    LOGGER.info( report.getName() );

    AnalyzerReportPage reportPage = report.create();

    filterValuesList = Arrays.asList( "USA" );
    reportPage.applyAttributeFilter( "Country", filterValuesList );

    filterValuesList = Arrays.asList( "CA" );
    reportPage.applyAttributeFilter( "State Province", filterValuesList );

    reportPage.openReportOptions();
    ReportOptionsPage reportOptions1 = new ReportOptionsPage( getDriver() );
    reportOptions1.check( ReportOption.SHOW_DRILL_LINS_ON_MEASURE_CELLS );
    reportOptions1.clickDlgBtnSave();
    reportPage.openLog();

    ArrayList<String> tabs = new ArrayList<String>( getDriver().getWindowHandles() );
    getDriver().switchTo().window( tabs.get( 1 ) );
    String log = reportPage.getMDX();

    assertTrue( log.toLowerCase().contains( presentItem.toLowerCase() ) );

    closeExtraTabs( tabs );
    getDriver().switchTo().window( tabs.get( 0 ) );

    // reportPage.close( false, "Analysis Report" );
    report.close();
  }

  private void closeExtraTabs( ArrayList<String> tabs ) {
    for ( String tab : getDriver().getWindowHandles() ) {
      if ( !tab.equals( tabs.get( 0 ) ) ) {
        getDriver().switchTo().window( tab );
        getDriver().close();
      }
    }
  }

}
