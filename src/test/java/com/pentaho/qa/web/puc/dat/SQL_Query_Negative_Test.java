package com.pentaho.qa.web.puc.dat;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

/**
 * @author Vadim Delendik Date: 01.05.15
 * 
 */

/*
 * ------- PREREQUISITES ------- 
 * driver_mode=suite_mode
 */

//https://spiratest.pentaho.com/6/TestCase/8945.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.SQL_Query_Negative_Test )
public class SQL_Query_Negative_Test extends WebBaseTest {

  @Test( )
  @SpiraTestSteps( testStepsId = "40893" )
  @Parameters( { "suzy_user", "suzy_password" } )
  public void testSuzyUserPermissions( String userName, String password ) {
    PentahoUser suzyUser = new PentahoUser( userName, password, false );
    HomePage homePage = suzyUser.login();

    if ( !homePage.isLogged( suzyUser.getName() ) ) {
      Assert.fail( "TS040893: unable to login using suzy user!" );
    }

    // verify that Managa Data Sources button is absent for suzy user
    if ( homePage.isElementPresent( homePage.btnManageDataSources, EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040893: suzy user has access to Manage Data Sources!" );
    }

    suzyUser.logout();
  }

  @Test( dependsOnMethods = "testSuzyUserPermissions", alwaysRun = true )
  @SpiraTestSteps( testStepsId = "40891" )
  public void adminLogin() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "DataProvider", dependsOnMethods = "adminLogin", description = "JIRA# BACKLOG-5274" )
  @SpiraTestSteps( testStepsId = "40891" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL04_EDIT" )
  public void testInvalidSQLDataSourceCreation( Map<String, String> args ) {
    SQLDataSource sqlDataSource = new SQLDataSource( args );

    // create workflow
    sqlDataSource.openWizard();
    sqlDataSource.setName();
    sqlDataSource.selectType();
    sqlDataSource.setParameters();

    if ( sqlDataSource.preview( 10 ) ) {
      Assert
        .fail( "TS040891: Error message doesn't appear as expected! Expected message is 'Query validation failed:Your query returns non-unique column names. Please make sure to alias any duplicated column names before continuing!'" );
    }

  }
}
