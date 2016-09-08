package com.pentaho.qa.web.puc.dat;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.pentaho.qa.SpiraTestcases;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/7979.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.DB_Tables_NonMatching_Joins_Test )
public class DB_Tables_NonMatching_Joins extends WebBaseTest {

  private DBTableDataSource dbTableDataSource;
  private AnalyzerReportPage analyzerReportPage;
  private PAReport analyzerReport;
  private JdbcConnection connection;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "32815" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID", executeValue = "JDBC07" )
  public
    void createSampleData_alterConnection( Map<String, String> args ) {
    connection = new JdbcConnection( args );
    LOGGER.info( connection.getName() );

    if ( !connection.create() ) {
      Assert.fail( "TS032815: DB Connection'" + connection.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "30210" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT09" )
  public void createDBTablesDataSourceWithInvalidJoin( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    if ( !dbTableDataSource.create() ) {
      Assert.fail( "TS032815: DB tables connection'" + dbTableDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "30211" )
  @XlsDataSourceParameters( path = "XLS_data/Analyzer_DataProvider.xls", sheet = "AnalyzerReports", dsUid = "Title",
      executeColumn = "TeststepId", executeValue = "30211" )
  public void createAnalyzerReport( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ));
    analyzerReport = new PAReport( args );
    LOGGER.info( analyzerReport.getName() );
    analyzerReportPage = analyzerReport.create();

    analyzerReport.save(folder);
    // Verification part
    if ( !analyzerReportPage.isSaved( analyzerReport.getName() ) ) {
      Assert.fail( "TS040882: Analyzer report '" + analyzerReport.getName() + "' wasn't saved!" );
    }
    analyzerReport.close();
  }

  @Test
  @SpiraTestSteps( testStepsId = "30212" )
  public void exportDBTablesDataSourceWithInvalidJoin() {
    String zipFile = dbTableDataSource.exportDS();
    Utils.unzip( zipFile );
    String mondrianFile = dbTableDataSource.getName() + ".mondrian.xml";
    String primaryKey = Utils.getValueByTag( mondrianFile, "Hierarchy", "CUSTOMERNAME", "primaryKey" );
    if ( !primaryKey.contains( "CUSTOMERNUM" ) ) {
      Assert.fail( "TS030211: PrimaryKey '" + primaryKey + "'is not used in the mondrian schema!" );
    }
  }
}
