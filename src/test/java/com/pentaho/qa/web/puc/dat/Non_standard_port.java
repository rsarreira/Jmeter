package com.pentaho.qa.web.puc.dat;

import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/9456.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.Non_standard_port )
public class Non_standard_port extends WebBaseTest {

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "45136" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC05" )
  public void createConnectionNonStandardPort( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    JdbcConnection conn = new JdbcConnection( args );
    LOGGER.info( conn.getName() );

    if ( !conn.create() ) {
      Assert.fail( "TS045136: DB Connection'" + conn.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createConnectionNonStandardPort" )
  @SpiraTestSteps( testStepsId = "45221" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT02" )
  public void createDBTablesDataSource( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    DBTableDataSource dbDataSource = new DBTableDataSource( args );

    if ( !dbDataSource.create() ) {
      Assert.fail( "TS045136: DB Tables datasource '" + dbDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createDBTablesDataSource" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "45221" )
  @SpiraTestSteps( testStepsId = "45221" )
  public void createAnalyzerReport( HashMap<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PAReport report = new PAReport( args );

    LOGGER.info( report.getName() );
    AnalyzerReportPage reportPage = report.create();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    report.save( folder );
    // Verification part
    if ( !reportPage.isSaved( report.getName() ) ) {
      Assert.fail( "TS045221: Analyzer report '" + report.getName() + "' wasn't saved!" );
    }
    // Reopen the report and close it again.
    report.close();
    report.open();
    report.close();

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", alwaysRun = true, dependsOnMethods = "createAnalyzerReport" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "45223" )
  @SpiraTestSteps( testStepsId = "45223" )
  public void createInteractiveReport( HashMap<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    PIRReport report = new PIRReport( args );

    LOGGER.info( report.getName() );
    PIRReportPage reportPage = report.create();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );

    report.save( folder );
    // Verification part
    if ( !reportPage.isSaved( report.getName() ) )
      Assert.fail( "TS045223: Interactive report '" + report.getName() + "' wasn't saved!" );

    // Reopen the report and close it again.
    report.close();
    report.open();
    report.close();

    softAssert.assertAll();
  }

}
