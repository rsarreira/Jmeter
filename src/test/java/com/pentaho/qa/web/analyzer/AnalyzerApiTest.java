package com.pentaho.qa.web.analyzer;

import static org.testng.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.webdriver.DriverHelper;

//DESIRED SETTINGS FOR EXECUTION:
//driver_mode=method_mode
//recovery=false

public class AnalyzerApiTest extends WebBaseTest {
  private static final String FOLDER_NAME_FROM_PATH_DELIMETR = "/";
  private static final String CSV_FILE_PATH = "CSV_data/analyzer_api.csv";
  private static final String URL_TO_REPLACE = "http://localhost:8080/pentaho";
  private static final String PUC_URL = R.CONFIG.get( "url" );
  private static final String ASYNC_KEY = "y";
  private static final long ASYNC_TIMEOUT_MS = 1000;

  private AnalyzerReportPage openReport( String datasourceName, String reportPath ) {
    Map<String, String> args = Collections.emptyMap();

    webUser.login( getDriver() );

    AnalyzerReportPage reportPage;
    if ( isUrl( reportPath ) ) {
      String reportUrl = completeUrl( reportPath );
      DriverHelper page = new DriverHelper( getDriver() );
      page.openURL( reportUrl );

      reportPage = new AnalyzerReportPage( getDriver() );
    } else {
      args = getReportPathArgs( reportPath );
      if ( args.isEmpty() ) {
        reportPage = openNewReport( datasourceName );
      } else {
        PAReport report = new PAReport( args );
        reportPage = report.open();
      }
    }
    return reportPage;
  }

  @Test( dataProvider = "DataProvider" )
  @CsvDataSourceParameters( path = CSV_FILE_PATH,
      dsArgs = "id, report_path, async, js_script, expected_result, expected_xpath, not_present_xpath",
      staticArgs = "datasourceName", dsUid = "id", executeColumn = "Execute", executeValue = "y" )
  public void testApi( String id, String reportPath, String async, String jsScript, String expectedResult,
      String expectedXpath, String notPresentXpath, String datasourceName ) {

    AnalyzerReportPage reportPage = openReport( datasourceName, reportPath );
    reportPage.switchToDefault();

    if ( StringUtils.isNotEmpty( jsScript ) ) {
      JavascriptExecutor executor = ( (JavascriptExecutor) getDriver() );
      Object result;
      if ( ASYNC_KEY.equals( async ) ) {
        getDriver().manage().timeouts().setScriptTimeout( ASYNC_TIMEOUT_MS, TimeUnit.MILLISECONDS );
        result = executor.executeAsyncScript( jsScript );
      } else {
        result = executor.executeScript( jsScript );
      }

      if ( StringUtils.isNotBlank( expectedResult ) ) {
        assertEquals( result.toString(), expectedResult );
      }
    }

    SoftAssert softAssert = reportPage.verifyContent( expectedXpath, notPresentXpath );
    softAssert.assertAll();
  }

  private AnalyzerReportPage openNewReport( String datasourceName ) {
    HomePage homePage = new HomePage( getDriver() );
    AnalyzerDataSourcePage selectDataSourcePage = homePage.openAnalyzerDataSourcePage();
    AnalyzerReportPage analysisReport = selectDataSourcePage.selectDataSource( datasourceName );
    return analysisReport;
  }

  private Map<String, String> getReportPathArgs( String path ) {
    Map<String, String> args = Collections.emptyMap();
    if ( StringUtils.isNotBlank( path ) && path.contains( FOLDER_NAME_FROM_PATH_DELIMETR ) ) {
      int indexDelimetr = path.lastIndexOf( FOLDER_NAME_FROM_PATH_DELIMETR );
      String reportFolder = path.substring( 0, indexDelimetr );
      String reportName = path.substring( indexDelimetr + 1 );
      if ( StringUtils.isNotBlank( reportFolder ) && StringUtils.isNotBlank( reportName ) ) {
        args = new HashMap<String, String>( 3 );
        args.put( Report.ARG_ID, Report.AUTO_ID );
        //args.put( Report.ARG_LOCATION, reportFolder );
        args.put( Report.ARG_NAME, reportName );
      }
    }
    return args;
  }

  private boolean isUrl( String path ) {
    boolean result = true;
    try {
      new URL( path );
    } catch ( MalformedURLException e ) {
      result = false;
    }
    return result;
  }

  private String completeUrl( String path ) {
    return path.replace( URL_TO_REPLACE, PUC_URL );
  }
}
