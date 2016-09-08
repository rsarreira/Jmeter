//http://spiratest.pentaho.com/20/TestCase/13460.aspx
package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;

@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_DSW_Negative_Test )
public class PIR_DSW_Negative_Test extends WebBaseTest {
  private SQLDataSource sqlDataSource;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL001" )
  public void createNewSQLDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );
    sqlDataSource.create();

    if ( !sqlDataSource.verify() ) {
      Assert.fail( "SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "PIRReports", dsUid = "Title", executeColumn = "TUID", executeValue = "PIR09",
      spiraColumn = "TeststepId" )
  public void createInteractiveReport( Map<String, String> args ) {

    PIRReport pirReport = new PIRReport( args );
    PIRReportPage interactiveReportPage = pirReport.create( true );
    interactiveReportPage.switchToFrame();
    // Add columns
    pirReport.addColumns();

    // Add groups
    pirReport.addGroups();

    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    SoftAssert softAssert = interactiveReportPage.verifyContent( presentItem, notPresentItem );

    pirReport.close();

    softAssert.assertAll();
  }
}
