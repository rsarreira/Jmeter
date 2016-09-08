package com.pentaho.qa.web.puc.dat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.datasource.DataSourceDBTablePage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.datasource.BaseDataSource.Workflow;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/8941.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.DB_TablesSmokeTest )
public class DB_TablesSmokeTest extends WebBaseTest {

  private DBTableDataSource dbTableDataSource;
  private DataSourceDBTablePage dsTablesPage;

  private static final String ORDERFACT = "ORDERFACT";
  private static final String CUSTOMERS = "CUSTOMERS";
  private static final String ORDERS = "ORDERS";
  private static final String CUSTOMER_W_TER = "CUSTOMER_W_TER";
  private static final String DIM_TIME = "DIM_TIME";

  @BeforeClass( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps(
      testStepsId = "40735, 40736, 40737, 40738, 40739, 40740, 40741, 40742, 40745, 40746, 40747, 40748, 40749, 40750, 40751, 40752, 40755, 40756, 40751, 40752, 40757, 40759, 40757, 40843, 40759, 40845" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT03" )
  public
    void createDBTableDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );
    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();

    dsTablesPage = new DataSourceDBTablePage( getDriver() );
    SoftAssert softAssert = new SoftAssert();

    // verify that
    // A list of available data connections is shown
    if ( !dsTablesPage.isConnectionPresent( dbTableDataSource.getConnection() ) ) {
      softAssert
          .fail( "TS040735: Connection '" + dbTableDataSource.getConnection() + "' is not available in the list!" );
    }

    // Add, Edit and Delete icon should be enabled inConnectionSection.
    if ( !dsTablesPage.isAddConnectionPresent() ) {
      softAssert.fail( "TS040735: Add Connection icon is unavailable!" );
    }

    if ( !dsTablesPage.isEditConnectionPresent() ) {
      softAssert.fail( "TS040735: Edit Connection icon is unavailable!" );
    }

    if ( !dsTablesPage.isRemoveConnectionPresent() ) {
      softAssert.fail( "TS040735: Remove Connection icon is unavailable!" );
    }

    // Radio buttons with "Reporting only" (default) and"Reporting & Analysis" options.
    if ( !dsTablesPage.isScopeRadioButtonReportingOnlySelected() ) {
      softAssert.fail( "TS040735: Reporting only is not selected by default!" );
    }

    softAssert.assertAll();

    // Next and Cancel button should be enabled.
    if ( !dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnNextLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnCancelLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnCancelLabel + "' is not enabled!" );
    }

    // Back and Finish button should be disabled.
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnBackLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnBackLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    softAssert.assertAll();

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();

    // The "Select Tables" window should appear:
    if ( !dsTablesPage.isPageSelectTablesSelected() ) {
      softAssert.fail( "TS040737: 'Select Tables' window doesn't appear!" );
    }

    // A Schema dropdown that shows the schemas in the database ex. Sampledata shows a "public" schema
    if ( !dsTablesPage.getSelectedSchema().equalsIgnoreCase( "public" ) ) {
      softAssert.fail( "TS040737: 'public' schema is not selected by default!" );
    }

    // Selected Tables on the right that is blank by default
    if ( !dsTablesPage.getSelectedRightTable().isEmpty() ) {
      softAssert.fail( "TS040737: Selected Tables on the right that is not blank!" );
    }

    // arrows between the panes to allow moving fields back and forth

    // A dropdown for Fact table is shown - It is populated by all tables in the Selected Tables pane
    if ( !dsTablesPage.isFactTableAvailable() ) {
      softAssert.fail( "TS040737: A dropdown for Fact table is not shown!" );
    }

    softAssert.assertAll();

    // 40738
    // Next and Finish button should be disabled.
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnNextLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    // Back and Cancel button should enabled.
    if ( !dsTablesPage.isButtonEnabled( BasePage.btnBackLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnBackLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnCancelLabel ) ) {
      softAssert.fail( "TS040736: Button '" + BasePage.btnCancelLabel + "' is not enabled!" );
    }

    softAssert.assertAll();

    // 40739, 40740
    if ( !dsTablesPage.selectTable( CUSTOMERS ) ) {
      Assert.fail( "TS040739: CUSTOMERS table was not moved under 'Selected Tables' section!" );
    }

    // Next and Finish button should be disabled.
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnNextLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    // Back and Cancel button should enabled.
    if ( !dsTablesPage.isButtonEnabled( BasePage.btnBackLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnBackLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnCancelLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnCancelLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.deselectTable( CUSTOMERS ) ) {
      Assert.fail( "CUSTOMERS table was not removed from 'Selected Tables' section!" );
    }

    softAssert.assertAll();

    // 40741, 40742
    List<String> tables = Arrays.asList( CUSTOMERS, CUSTOMER_W_TER, ORDERS );

    if ( !dsTablesPage.selectTables( tables, true ) ) {
      Assert.fail( "TS040741: multiply tables selection doesn't work!" );
    }

    if ( !dsTablesPage.deselectTables( tables, true ) ) {
      Assert.fail( "TS040741: multiply tables deselection doesn't work!" );
    }

    // Next and Finish button should be disabled.
    if ( !dsTablesPage.isButtonDisabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnNextLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040740: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    softAssert.assertAll();

    // 40745
    tables = Arrays.asList( ORDERFACT, CUSTOMER_W_TER, DIM_TIME );

    if ( !dsTablesPage.selectTables( tables, true ) ) {
      Assert.fail( "TS040745: multiply tables selection doesn't work!" );
    }

    // 40746
    if ( !dsTablesPage.selectFactTable( ORDERFACT ) ) {
      Assert.fail( "TS040746: Fact Table '" + ORDERFACT + "' was not selected!!" );
    }

    // 40747, 40748, 40749
    dsTablesPage.buttonNext();

    // A dropdown named Left table that contains the tables selected on the previous page
    if ( !dsTablesPage.getSelectedLeftTable().contains( ORDERFACT ) ) {
      softAssert.fail( "TS040747: Left table is not selected by default to " + ORDERFACT );
    }

    // A dropdown named right table that contains the tables selected on the previous page
    if ( !dsTablesPage.getSelectedRightTable().contains( CUSTOMER_W_TER ) ) {
      softAssert.fail( "TS040747: Right table is not selected by default to " + CUSTOMER_W_TER );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040748: Button '" + BasePage.btnNextLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040748: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    // Back and Cancel button should enabled.
    if ( !dsTablesPage.isButtonEnabled( BasePage.btnBackLabel ) ) {
      softAssert.fail( "TS040748: Button '" + BasePage.btnBackLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnCancelLabel ) ) {
      softAssert.fail( "TS040748: Button '" + BasePage.btnCancelLabel + "' is not enabled!" );
    }

    softAssert.assertAll();

    // 40750, 40751, 40752, 40755
    List<String> joins = Arrays.asList( ORDERFACT + ".CUSTOMERNUMBER," + CUSTOMER_W_TER + ".CUSTOMERNUMBER" );
    dsTablesPage.createJoins( joins );

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnNextLabel ) ) {
      softAssert.fail( "TS040752: Button '" + BasePage.btnNextLabel + "' is not disabled!" );
    }

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040752: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    // Back and Cancel button should enabled.
    if ( !dsTablesPage.isButtonEnabled( BasePage.btnBackLabel ) ) {
      softAssert.fail( "TS040752: Button '" + BasePage.btnBackLabel + "' is not enabled!" );
    }

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnCancelLabel ) ) {
      softAssert.fail( "TS040752: Button '" + BasePage.btnCancelLabel + "' is not enabled!" );
    }

    joins = Arrays.asList( ORDERFACT + ".TIME_ID," + DIM_TIME + ".TIME_ID" );
    dsTablesPage.createJoins( joins );

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040755: Button '" + BasePage.btnFinishLabel + "' is not enabled!" );
    }

    softAssert.assertAll();

    // 40756, 40751, 40752
    String join = ORDERFACT + ".TIME_ID," + DIM_TIME + ".TIME_ID";
    dsTablesPage.deleteJoin( join );

    if ( !dsTablesPage.isButtonDisabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040748: Button '" + BasePage.btnFinishLabel + "' is not disabled!" );
    }

    softAssert.assertAll();

    // 40757, 40759
    joins = Arrays.asList( ORDERFACT + ".TIME_ID," + DIM_TIME + ".TIME_ID" );
    dsTablesPage.createJoins( joins );

    if ( !dsTablesPage.isButtonEnabled( BasePage.btnFinishLabel ) ) {
      softAssert.fail( "TS040757: Button '" + BasePage.btnFinishLabel + "' is not enabled!" );
    }
    softAssert.assertAll();

    // 40757, 40843, 40759, 40845
    dbTableDataSource.finishWizard(); // TODO: TS040843 - add implementation for customized model
    // verify
    if ( !dbTableDataSource.verify() ) {
      Assert
          .fail( "TS040845: DB Tables datasource '" + dbTableDataSource.getName() + "' was not created successfully!" );
    }

  }

  @Test( dependsOnMethods = "createDBTableDataSource" )
  @SpiraTestSteps( testStepsId = "40846, 40848, 42038, 42041" )
  public void testEditDBTablesDataSource() {

    dbTableDataSource.openWizard( Workflow.EDIT );

    dsTablesPage = new DataSourceDBTablePage( getDriver() ); // to be able to retry execution
    dsTablesPage.selectConnection( dbTableDataSource.getConnection() );
    dsTablesPage.buttonNext();

    if ( !dsTablesPage.selectTable( ORDERS ) ) {
      Assert.fail( "TS040846: ORDERS table was not moved under 'Selected Tables' section!" );
    }

    dsTablesPage.buttonNext();

    List<String> joins = Arrays.asList( ORDERFACT + ".ORDERNUMBER," + ORDERS + ".ORDERNUMBER" );
    dsTablesPage.createJoins( joins );

    dbTableDataSource.finishWizard( Workflow.EDIT );

    if ( !dbTableDataSource.verify() ) {
      Assert.fail( "TS040848: 'DB Table' data source was removed after editing!" );
    }
  }

  @Test( dependsOnMethods = "testEditDBTablesDataSource" )
  @SpiraTestSteps( testStepsId = "40766, 40767" )
  public void testCancelRemovalDBTablesDataSource() {
    ManageDataSourcesPage manageDataSourcesPage = dbTableDataSource.openManageDataSource();
    manageDataSourcesPage.removeDataSource( dbTableDataSource.getName() );
    manageDataSourcesPage.confirmRemove( false );
    manageDataSourcesPage.closeManageDataSources();

    // verify
    if ( !dbTableDataSource.verify() ) {
      Assert.fail( "TS040767: DB Tables datasource '" + dbTableDataSource.getName()
          + "' was deleted after canceling removal!" );
    }
  }

  @Test( dependsOnMethods = "testCancelRemovalDBTablesDataSource" )
  @SpiraTestSteps( testStepsId = "40768" )
  public void testRemoveDBTablesDataSource() {
    dbTableDataSource.delete();

    // verify
    if ( dbTableDataSource.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040767: DB Tables datasource '" + dbTableDataSource.getName()
          + "' was deleted after canceling removal!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testRemoveDBTablesDataSource", alwaysRun = true )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT04" )
  @SpiraTestSteps( testStepsId = "40840, 40841, 40842, 40758, 40762" )
  public void testNewDBTablesDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );

    // TODO: 40844 implement Customize Model
    if ( !dbTableDataSource.create() ) {
      Assert
          .fail( "TS040762: DB Tables datasource '" + dbTableDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testNewDBTablesDataSource" )
  @SpiraTestSteps( testStepsId = "42763, 42764, 40765" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT04_EDIT" )
  public void testEditDBTablesDataSource2( Map<String, String> args ) {
    DBTableDataSource editedDataSource = new DBTableDataSource( args );
    dbTableDataSource.edit( editedDataSource );

    if ( !dbTableDataSource.verify() ) {
      Assert.fail( "TS042041: 'DB Table' data source was removed after editing!" );
    }
  }

  // TODO: 50798 verification should be inserted into the existing case
  @Test( dependsOnMethods = "testEditDBTablesDataSource2" )
  @SpiraTestSteps( testStepsId = "40849" )
  public void testRemoveDBTablesDataSource2() {
    dbTableDataSource.delete();

    // verify
    if ( dbTableDataSource.verify( EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "TS040767: DB Tables datasource '" + dbTableDataSource.getName()
          + "' was deleted after canceling removal!" );
    }
  }

}
