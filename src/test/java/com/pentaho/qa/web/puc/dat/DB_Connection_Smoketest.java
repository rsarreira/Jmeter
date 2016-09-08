package com.pentaho.qa.web.puc.dat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.connection.BaseConnection;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8864.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.DB_Connection_Smoketest )
public class DB_Connection_Smoketest extends WebBaseTest {

  private JdbcConnection connNegative;
  private JdbcConnection connEdit;
  private JdbcConnection connInvalidHost;

  private JdbcConnection connJdbc;

  @Test( )
  public void testLogin() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testLogin" )
  @SpiraTestSteps( testStepsId = "40311, 40293" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC02" )
  public void testConnectionNegativePassword( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connNegative = new JdbcConnection( args );
    LOGGER.info( connNegative.getName() );

    connNegative.openWizard();
    connNegative.setGeneralParameters();
    connNegative.setParameters();

    // verification
    if ( connNegative.testConnection() ) {
      Assert.fail( "TS040293: Connection to " + connNegative.getName() + " is valid in spite of invalid password!" );
    }
  }

  @Test( dataProvider = "DataProvider", alwaysRun = true, dependsOnMethods = "testConnectionNegativePassword" )
  @SpiraTestSteps( testStepsId = "40294" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC01" )
  public void testConnectionValidPassword( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    JdbcConnection conn = new JdbcConnection( args );
    connNegative.copy( conn );
    LOGGER.info( connNegative.getName() );

    connNegative.setGeneralParameters();
    connNegative.setParameters();

    // verification
    if ( !connNegative.testConnection() ) {
      Assert.fail( "TS040294: Connection to " + conn.getName() + " failed!" );
    }
  }

  @Test( dependsOnMethods = "testConnectionValidPassword", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40295" )
  public void testConnectionCancel() {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    // verification
    connNegative.cancelWizard();
    if ( connNegative.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040295: Connection " + connNegative.getName() + " was created after cancel wizard operation!" );
    }
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testConnectionCancel", alwaysRun = true )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testConnectionValidHost( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connInvalidHost = new JdbcConnection( args );
    LOGGER.info( connInvalidHost.getName() );

    connInvalidHost.openWizard();
    connInvalidHost.setGeneralParameters();
    connInvalidHost.setParameters();

    // verification
    connInvalidHost.testConnection();
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testConnectionValidHost" )
  @SpiraTestSteps( testStepsId = "40296" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC03" )
  public void testConnectionInvalidHost( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    JdbcConnection conn = new JdbcConnection( args );
    connInvalidHost.copy( conn );
    LOGGER.info( conn.getName() );

    connInvalidHost.setGeneralParameters();
    connInvalidHost.setParameters();

    // verification
    if ( connInvalidHost.testConnection() ) {
      connInvalidHost.cancelWizard();
      Assert.fail( "TS040296: Connection to " + conn.getName() + " is valid in spite of invalid password!" );
    }

    connInvalidHost.cancelWizard();
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testConnectionInvalidHost", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "50505" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testConnectionValid( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connJdbc = new JdbcConnection( args );
    LOGGER.info( connJdbc.getName() );

    if ( !connJdbc.create() ) {
      Assert.fail( "TS050505: DB Connection'" + connJdbc.getName() + "' was not created successfully!" );
    }
  }

  @Test( dependsOnMethods = "testConnectionValid" )
  @SpiraTestSteps( testStepsId = "40300" )
  public void testConnectionValidRemove() {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connJdbc.delete();

    if ( connJdbc.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040300: Data Source '" + connJdbc.getName() + "' was not removed!" );
    }
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testConnectionValidRemove", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40316" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testNewConnectionFromDSWizard( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connEdit = new JdbcConnection( args );

    connEdit.openWizard( BaseConnection.Workflow.WIZARD_DB_TABLES );
    connEdit.setGeneralParameters();
    connEdit.setParameters();
    connEdit.testConnection();
    connEdit.finishWizard( BaseConnection.Workflow.WIZARD_DB_TABLES );

    if ( !connEdit.verify() ) {
      Assert.fail( "TS040316: DB Connection'" + connEdit.getName() + "' was not created successfully!" );
    }

  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testNewConnectionFromDSWizard", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40297, 40298, 40299" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04_EDIT" )
  public void testEditConnection( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    JdbcConnection conn = new JdbcConnection( args );
    connEdit.edit( conn );
    if ( !connEdit.verify() ) {
      Assert.fail( "TS40297: DB Connection'" + connEdit.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testEditConnection", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40316" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04_EDIT2" )
  public void testEditConnectionDSWizard( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connEdit = new JdbcConnection( args );

    connEdit.openWizard( BaseConnection.Workflow.WIZARD_DB_TABLES_EDIT );
    connEdit.setGeneralParameters();
    connEdit.setParameters();
    connEdit.testConnection();
    connEdit.finishWizard( BaseConnection.Workflow.WIZARD_DB_TABLES );
  }

  @Test( dependsOnMethods = "testEditConnectionDSWizard", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40318" )
  public void testConnectionEditedRemove() {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    connEdit.delete( BaseConnection.Workflow.WIZARD_DB_TABLES );
    connEdit.finishWizard( BaseConnection.Workflow.WIZARD_DB_TABLES_REMOVE );

    if ( connEdit.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040318: Data Source '" + connEdit.getName() + "' was not removed!" );
    }
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "testConnectionEditedRemove", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "49882" )
  @XlsDataSourceParameters( sheet = "JDBC_Connections", dsUid = "name", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testConnectionWithOptions( Map<String, String> args ) {
    if ( !locale.equals( "en" ) ) {
      Jira.setTickets( "BISERVER-12964" );
    }

    JdbcConnection conn = new JdbcConnection( args );

    conn.openWizard();
    conn.setGeneralParameters();
    conn.setParameters();

    LinkedHashMap<String, String> opts = new LinkedHashMap<String, String>();
    opts.put( "key1", "value1" );
    opts.put( "key2", "value2" );
    opts.put( "key3", "value3" );
    opts.put( "key4", "value4" );
    conn.setOptions( opts );

    conn.addOptions();
    conn.finishWizard();

    if ( !conn.verify( EXPLICIT_TIMEOUT ) ) {
      Assert.fail( "TS049882: Data Source '" + conn.getName() + "' was not created!" );
    }

    // open edit dialog->activate Options and verify that all values exists
    conn.openEditWizard();
    if ( !conn.verifyOptions() ) {
      Assert.fail( "TS049882: param/options was not saved!" );
    }
    conn.finishWizard();
  }
}
