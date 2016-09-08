package com.pentaho.qa.web.puc;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.PanelItem;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;

public class BrowsersSniff extends WebBaseTest {

  @Test( dataProvider = "DataProvider" )
  @XlsDataSourceParameters( staticArgs = "user, password, reportName, dataSourceName, fieldDrag_01, fieldDropTo_01" )
  /*
   * @Parameters({ "browser", "version", "selenium", "user", "password", "reportName", "dataSourceName", "fieldDrag_01",
   * "fieldDropTo_01"})
   */
  public void testBrowser( String browser, String version, String selenium, String user, String password,
      String reportName, String dataSourceName, String fieldDrag_01, String fieldDropTo_01 ) {
    // open driver
    System.setProperty( "browser", browser );
    System.setProperty( "selenium_host", selenium );
    System.setProperty( "platform", "MAC" );
    WebDriver extraDriver = createExtraDriver( browser );

    // Login to PUC
    HomePage homePage = webUser.login( extraDriver );

    // Test DnD
    // Create Analysis report
    homePage.menuPick( BasePage.Menu.ANALYSIS_REPORT );
    AnalyzerDataSourcePage selectDataSourcePage = new AnalyzerDataSourcePage( extraDriver );
    // Select specified Data Source
    AnalyzerReportPage analysisReport = selectDataSourcePage.selectDataSource( dataSourceName );

    // switch fields view filter to A-Z
    analysisReport.viewFilterSwitchToAZ();
    // drag and drop fields
    
    PanelItem panelItem = PanelItem.getPanelItemByName( fieldDropTo_01 );
    analysisReport.fieldDragAndDrop( fieldDrag_01, panelItem );
  }
}
