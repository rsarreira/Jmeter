package com.pentaho.qa.web.puc.dat;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

/**
 * @author Pavel_Mikhnevich Date: 01.05.15
 * 
 */

/*
 * ------- PREREQUISITES ------- driver_mode - suite mode only as steps are dependent
 */
//https://spiratest.pentaho.com/6/TestCase/8940.aspx
@SpiraTestCase(  projectId = 6, testCaseId = SpiraTestcases.SQL_Query )
public class SQL_Query extends WebBaseTest {
  private SQLDataSource sqlDataSource;

  @Test( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
      .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "40720" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testNewHypersonicConnection( Map<String, String> args ) {
    JdbcConnection conn = new JdbcConnection( args );
    LOGGER.info( conn.getName() );

    
    if ( !conn.create() ) {
      Assert.fail( "TS040720: Hypersonic-SampleData connection'" + conn.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "40719, 40721, 40722, 40724, 40725, 40726" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSSQL02" )
  public void testNewSQLDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    // create workflow
    sqlDataSource.openWizard();
    sqlDataSource.setName();
    sqlDataSource.selectType();
    sqlDataSource.setParameters();

    if ( !sqlDataSource.preview( 100 ) ) {
      Assert.fail( "TS040722: DataPreview dialog was not recognized!" );
    }

    sqlDataSource.finishWizard();
    
    if ( !sqlDataSource.verify() ) {
      Assert.fail( "TS040719: SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
    
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "40729" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL02_EDIT" )
  public void testEditSQLDataSource( Map<String, String> args ) {
    SQLDataSource editedDataSource = new SQLDataSource( args );
    sqlDataSource.edit( editedDataSource );

    if ( !sqlDataSource.verify() ) {
      Assert.fail( "TS040729: 'SQL Query' data source was removed after editing!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "40730" )
  public void testRemoveDataSource( Map<String, String> args ) {
    sqlDataSource.delete();

    if ( sqlDataSource.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040730: Data Source '" + sqlDataSource.getName() + "' was not removed!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "40723" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSSQL04" )
  public void testNewSQLDataSourceCancel( Map<String, String> args ) {
    SQLDataSource sqlDataSource = new SQLDataSource( args );
    sqlDataSource.openWizard();
    sqlDataSource.setName();
    sqlDataSource.selectType();
    sqlDataSource.setParameters();
    sqlDataSource.cancelWizard();
    if ( sqlDataSource.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040723: SQL Query datasource '" + sqlDataSource.getName()
          + "' was created after Cancel button was clicked!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "41294" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC01" )
  public void testNewFoodmartConnection( Map<String, String> args ) {
    JdbcConnection conn = new JdbcConnection( args );
    LOGGER.info( conn.getName() );

    
    if ( !conn.create() ) {
      Assert.fail( "TS041294: Foodmart connection'" + conn.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "41294" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL03" )
  public void testNewSQLDataSourceFoodmart( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    if ( !sqlDataSource.create() ) {
      Assert.fail( "TS041294: SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "41294" )
  public void testRemoveDataSourceFoodmart( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );
    sqlDataSource.delete();

    if ( sqlDataSource.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS041294: Data Source '" + sqlDataSource.getName() + "' was not removed!" );
    }
  }

}
