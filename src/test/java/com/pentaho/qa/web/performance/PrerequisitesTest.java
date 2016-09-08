package com.pentaho.qa.web.performance;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.connection.BaseConnection.Workflow;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.AnalysisDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;

public class PrerequisitesTest extends WebBaseTest {

  private AnalysisDataSource analysisDS;
  private JdbcConnection connection;

  @BeforeClass
  public void login() {
    webUser.login();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @CsvDataSourceParameters( dsUid = "dbType", executeColumn = "TUID", executeValue = "JDBC01",
      path = "CSV_data/Performance/Connections.csv" )
  public void createJDBCConnetion( Map<String, String> args ) {

    connection = new JdbcConnection( args );
    connection.openWizard( Workflow.WIZARD_SQL_QUERY );
    connection.setGeneralParameters();
    connection.setParameters();

    if ( connection.testConnection() ) {
      connection.finishWizard( Workflow.WIZARD_SQL_QUERY );
    } else {
      Assert.fail( "Invalid connection " + connection.getDbName() + " !" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createJDBCConnetion" )
  @CsvDataSourceParameters( dsUid = "name", executeColumn = "TestStepId", executeValue = "TS00001",
      path = "CSV_data/Performance/Mondrian.csv" )
  public void importAnalysis( Map<String, String> args ) {

    analysisDS = new AnalysisDataSource( args );
    analysisDS.open();
    analysisDS.setParameters( connection.getName() );
    analysisDS.finish();

    ManageDataSourcesPage manageDSPage = new ManageDataSourcesPage( getDriver() );
    manageDSPage.closeManageDataSources();
  }
}
