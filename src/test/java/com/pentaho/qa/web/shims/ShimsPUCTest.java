package com.pentaho.qa.web.shims;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.puc.datasource.BaseDataSource;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;

/**
 * Created by Ihar_Chekan on 6/4/2015.
 */
//https://spiratest.pentaho.com/5/TestCase/11549.aspx
@SpiraTestCase( projectId = 28, testCaseId = SpiraTestcases.ShimsPUCTest )
public class ShimsPUCTest extends WebBaseTest {
  private ShimsPUCGetSettings shimsPUCGetSettings;
  private Map<String, String> databaseOptions;

  private JdbcConnection jdbcConnection;
  private SQLDataSource sqlDataSource;
  private DBTableDataSource dbDataSource;

  private String shimName;
  private String shimSecured;

  @BeforeClass( )
  public void login() {
    webUser.login( getDriver() );

    shimsPUCGetSettings = new ShimsPUCGetSettings();

    shimName = shimsPUCGetSettings.getShimName();
    shimSecured = shimsPUCGetSettings.getShimSecured();

    setSuiteNameAppender( shimName + "/" + shimSecured );
  }

  @DataProvider( name = "ShimsPUCTest_DataProvider" )
  public Object[][] customShimDataProvider( final Method testMethod, ITestContext context ) {
    Object[][] args = createDataSingeThread( testMethod, context );

    //if shim is other, than cdh - changing data provider to exclude tests for impala
    int index = 0;
    for ( int i = 0; i < args.length; i++ ) {
      @SuppressWarnings( "unchecked" )
      HashMap<String, String> parameters = (HashMap<String, String>) args[i][0];
      if ( ( shimName.substring( 0, shimName.length() - 2 ) ).equalsIgnoreCase( "cdh" ) ) {
        index++;
      } else if ( parameters.get( "dbType" ).toLowerCase().contains( "hadoop hive 2" ) ) {
        index++;
      }
    }

    if ( index == 0 ) {
      Assert.fail( "Unable to create Data Provider for the test, using shim: " + shimName + "; secured: " + shimSecured );
    }

    Object[][] shimArgs = new Object[index][1];
    index = 0;
    for ( int i = 0; i < args.length; i++ ) {
      @SuppressWarnings( "unchecked" )
      HashMap<String, String> parameters = (HashMap<String, String>) args[i][0];
      if ( ( shimName.substring( 0, shimName.length() - 2 ) ).equalsIgnoreCase( "cdh" ) ) {
        shimArgs[index][0] = parameters;
        index++;
      } else if ( parameters.get( "dbType" ).toLowerCase().contains( "hadoop hive 2" ) ) {
        shimArgs[index][0] = parameters;
        index++;
      }
    }
    return shimArgs;
  }

  // creating hive or impala JDBC connection based on test.properties file
  @Test( dataProvider = "ShimsPUCTest_DataProvider" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC07", spiraColumn = "SpiraID" )
  public void createShimConnection( HashMap<String, String> args ) {
    // reading DB options from test.properties file
    databaseOptions = shimsPUCGetSettings.getDatabaseSettings( args.get( "dbType" ) );

    databaseOptions.put( "name", args.get( "name" ) );

    jdbcConnection = new JdbcConnection( databaseOptions );
    LOGGER.info( jdbcConnection.getName() );
    // adding principal for hive or impala
    Map<String, String> principalOptions = new HashMap<String, String>();
    if ( args.get( "dbType" ).toLowerCase().contains( "hadoop hive 2" ) ) {
      principalOptions.put( databaseOptions.get( "hive2_option" ), databaseOptions.get( "hive2_principal" ) );
    } else if ( args.get( "dbType" ).toLowerCase().contains( "cloudera impala" ) ) {
      principalOptions.put( "KrbRealm", databaseOptions.get( "impala_KrbRealm" ) );
      principalOptions.put( "KrbHostFQDN", databaseOptions.get( "impala_KrbHostFQDN" ) );
      principalOptions.put( "KrbServiceName", databaseOptions.get( "impala_KrbServiceName" ) );
    }
    jdbcConnection.setOptions( principalOptions );

    jdbcConnection.openWizard();
    jdbcConnection.setGeneralParameters();
    jdbcConnection.setParameters();
    jdbcConnection.addOptions();
    if ( !jdbcConnection.testConnection() ) {
      Assert.fail( "Unable to create Database connection'" + jdbcConnection.getName()
          + "' for particular shim! Verify server settings!" );
    }

    jdbcConnection.finishWizard();

    if ( !jdbcConnection.verify() ) {
      Assert.fail( "Database connection'" + jdbcConnection.getName() + "' was not created successfully!" );
    }
  }

  // creating SQL data source, based on JDBC connection
  @Test( dataProvider = "ShimsPUCTest_DataProvider", dependsOnMethods = "createShimConnection" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL05", spiraColumn = "SpiraID"  )
  public void createSQLShimDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    sqlDataSource.openWizard();
    sqlDataSource.setName();
    sqlDataSource.selectType();
    sqlDataSource.setParameters();
    sqlDataSource.finishWizard( BaseDataSource.Workflow.NEW, EXPLICIT_TIMEOUT * 2 );

    if ( !sqlDataSource.verify() ) {
      Assert.fail( "SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
  }

  // creating database table connection, based on JDBC connection
  @Test( dataProvider = "ShimsPUCTest_DataProvider", dependsOnMethods = "createShimConnection" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT02", spiraColumn = "SpiraID" )
  public void createDbShimDataSource( Map<String, String> args ) {
    dbDataSource = new DBTableDataSource( args );

    dbDataSource.openWizard();
    dbDataSource.setName();
    dbDataSource.selectType();
    dbDataSource.setParameters();
    dbDataSource.finishWizard( BaseDataSource.Workflow.NEW, EXPLICIT_TIMEOUT * 2 );

    if ( !dbDataSource.verify() ) {
      Assert.fail( "DB Tables datasource '" + dbDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "ShimsPUCTest_DataProvider", dependsOnMethods = { "createSQLShimDataSource",
      "createDbShimDataSource" } )
  @XlsDataSourceParameters( sheet = "AnalyzerReports", dsUid = "Title", executeColumn = "TUID", executeValue = "PAR01", spiraColumn = "SpiraID" )
  public void testShimAnalyzerReport( HashMap<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    // creating new analyzer report with SQL shim connection
    PAReport report = new PAReport( args );

    AnalyzerReportPage analyzerReportPage = report.create();
    analyzerReportPage.disableAutoRefresh();
    // save, close and reopen
    report.save( folder );
    // analyzerReportPage.close( false, args.get( "Name" ) );
    report.close();
    report.open();
    analyzerReportPage.resetActiveFrame();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = analyzerReportPage.verifyContent( presentItem, notPresentItem );
    //analyzerReportPage.close( false, args.get( "Name" ) );
    report.close();
    
    softAssert.assertAll();
  }

  @Test( dataProvider = "ShimsPUCTest_DataProvider", dependsOnMethods = "createSQLShimDataSource" )
  @XlsDataSourceParameters( sheet = "PIRReports", dsUid = "Title", executeColumn = "TUID", executeValue = "PIR02", spiraColumn = "SpiraID" )
  public void testShimInteractiveReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );

    // creating new interactive report with SQL shim connection
    PIRReport report = new PIRReport( args );
    PIRReportPage reportPage = report.create();
    // save, close and reopen
    report.save( folder );
    report.close();
    report.open();
    report.activateEditMode();
    reportPage.switchToFrame();

    // verification part
    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    // reportPage.close( false, args.get( "Name" ) );
    report.close();
    
    softAssert.assertAll();
  }

}
