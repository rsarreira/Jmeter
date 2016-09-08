package com.pentaho.qa.web.dashboard;

import java.util.Map;

import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.dashboard.DashboardPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.dashboard.Dashboard;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/22/TestCase/11890.aspx
@SpiraTestCase( projectId = 22, testCaseId = SpiraTestcases.DashboardSniffTest )
public class PDB_SniffTest extends WebBaseTest {

  private final String dashboardDataProvider = "CSV_data/PDB_DataProvider.csv";

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "DataProvider" )
  @CsvDataSourceParameters( executeColumn = "TUID", executeValue = "PDB01", path = dashboardDataProvider )
  @SpiraTestSteps( testStepsId = "63960" )
  public void createDashboard( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    // Initialize and create new dashboard
    Dashboard dashboard = new Dashboard( args );
    DashboardPage dashboardPage = dashboard.create();
  }
}
