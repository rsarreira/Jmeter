//http://spiratest.pentaho.com/20/TestCase/13409.aspx

//SETTINGS FOR EXECUTION:
//driver_mode=method_mode
//recovery=true
//retry_count=1
//thread_count=4

package com.pentaho.qa.web.pir;

import java.util.Map;

import org.openqa.selenium.Keys;
import org.openqa.selenium.interactions.Actions;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.pir.PIRDataSourcePage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_DataSourceSmoketest )
public class PIR_DataSourceSmoketest extends WebBaseTest {

  private static ThreadLocal<HomePage> homePages = new ThreadLocal<HomePage>();

  private SQLDataSource sqlDataSource;
  private CSVDataSource csvDataSource;
  private DBTableDataSource dbTableDataSource;

  private SoftAssert softAssert = new SoftAssert();
  private final String systemDataSource = "PDI_Operations_Mart";

  @BeforeMethod
  public void prepareTest() {
    webUser.login( getDriver() );
    setHomePage( new HomePage( getDriver() ) );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74629,74630" )
  public void buttonsCheck() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();

    if ( !selectDataSourcePage.isOpened() ) {
      Assert.fail( "  TS074629: Data Source Page is not opened!" );
    }

    softAssert = selectDataSourcePage.checkButtonsVisibility( false );
    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74631" )
  public void selectSystemDS() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();

    selectDataSourcePage.selectDataSourceWithoutOK( systemDataSource );
    softAssert = selectDataSourcePage.checkButtonsVisibility( true );
    selectDataSourcePage.clickCancel();
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL002" )
  @SpiraTestSteps( testStepsId = "74632" )
  public void selectUserDS( Map<String, String> args ) {
    SQLDataSource userDataSource = new SQLDataSource( args );
    userDataSource.create();

    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();

    selectDataSourcePage.selectDataSourceWithoutOK( userDataSource.getName() );
    softAssert = selectDataSourcePage.checkButtonsVisibility( true );
    softAssert.assertAll();
  }
  
  @Test( )
  @SpiraTestSteps( testStepsId = "74633" )
  public void dataSourceMultipleSelection() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();

    selectDataSourcePage.activateFrame();
    //multiple selection using CTRL key
    Actions actions = new Actions( getDriver() );
    actions.keyDown(Keys.CONTROL)
      .click(selectDataSourcePage.getDataSourceList().get( 0 ).getElement() )
      .click(selectDataSourcePage.getDataSourceList().get( 1 ).getElement() )
      .keyUp(Keys.CONTROL)
      .build()
      .perform();
    softAssert = selectDataSourcePage.checkMultipleSelection();
    
    //multiple selection using SHIFT key
    actions = new Actions( getDriver() );
    actions.keyDown( Keys.SHIFT )
      .click( selectDataSourcePage.getDataSourceList().get( 0 ).getElement() )
      .click( selectDataSourcePage.getDataSourceList().get( 1 ).getElement() )
      .keyUp( Keys.SHIFT )
      .build()
      .perform();
    softAssert = selectDataSourcePage.checkMultipleSelection();

    softAssert.assertAll();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74634" )
  public void dataSourceDoubleClick() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.doubleClick( systemDataSource );

    PIRReportPage pirReportPage = new PIRReportPage( getDriver() );
    pirReportPage.closeGetStarted();
    pirReportPage.disableHints();

    if ( !pirReportPage.checkDataSourceName( systemDataSource ) ) {
      Assert.fail( "  TS074634: A new PIR report is not created using the selected data source !" );
    }
  }
  
  @Test( )
  @SpiraTestSteps( testStepsId = "74635" )
  public void createPIRReportOK() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.selectDataSource( systemDataSource );

    PIRReportPage pirReportPage = new PIRReportPage( getDriver() );
    pirReportPage.closeGetStarted();
    pirReportPage.disableHints();

    if ( !pirReportPage.checkDataSourceName( systemDataSource ) ) {
      Assert.fail( "  TS074635: A new PIR report is not created using the selected data source !" );
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "74636" )
  public void createPIRReportCancel() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.selectDataSourceWithoutOK( systemDataSource );
    selectDataSourcePage.clickCancel();

    if ( !homePage.isOpened() ) {
      Assert.fail( "  TS074636: Home Page is not opened !" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL003" )
  @SpiraTestSteps( testStepsId = "74637,74638" )
  public void createSQLDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.switchToFrame();
    DataSourceWizardPage dataSourceWizardPage = selectDataSourcePage.clickAdd();
    dataSourceWizardPage = sqlDataSource.createWithoutOpen( dataSourceWizardPage );
    dataSourceWizardPage.buttonFinish();
    if ( !dataSourceWizardPage.isModelRadioButtonDefaultSelected() ) {
      Assert.fail( "  TS074638:(SQL) Model editing window opens 'Keep Default Model' not opened !" );
    }
    dataSourceWizardPage.buttonOK();
    dataSourceWizardPage.makeClickable();
    selectDataSourcePage.switchToFrame();

    PIRReportPage pirReportPage = new PIRReportPage( getDriver() );

    if ( !pirReportPage.checkDataSourceName( sqlDataSource.getName() ) ) {
      Assert.fail( "  TS074638:(SQL) PIR report not opened using created data source !" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV01" )
  @SpiraTestSteps( testStepsId = "74637,74638" )
  public void createCSVDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );

    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.switchToFrame();
    DataSourceWizardPage dataSourceWizardPage = selectDataSourcePage.clickAdd();
    dataSourceWizardPage = csvDataSource.createWithoutOpen( dataSourceWizardPage );
    dataSourceWizardPage.buttonFinish();
    if ( !dataSourceWizardPage.isModelRadioButtonDefaultSelected() ) {
      Assert.fail( "  TS074638:(CSV) Model editing window opens 'Keep Default Model' not opened !" );
    }
    dataSourceWizardPage.buttonOK();
    dataSourceWizardPage.makeClickable();
    selectDataSourcePage.switchToFrame();

    PIRReportPage pirReportPage = new PIRReportPage( getDriver() );

    if ( !pirReportPage.checkDataSourceName( csvDataSource.getName() ) ) {
      Assert.fail( "  TS074638:(CSV) PIR report not opened using created data source !" );
    }
  }
  
  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT01" )
  @SpiraTestSteps( testStepsId = "74637,74638" )
  public void createDBTableDataSource( Map<String, String> args ) {
    dbTableDataSource = new DBTableDataSource( args );

    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.switchToFrame();
    DataSourceWizardPage dataSourceWizardPage = selectDataSourcePage.clickAdd();
    dataSourceWizardPage = dbTableDataSource.createWithoutOpen( dataSourceWizardPage );
    dataSourceWizardPage.buttonFinish();
    if ( !dataSourceWizardPage.isModelRadioButtonDefaultSelected() ) {
      Assert.fail( "  TS074638:(DBTable) Model editing window opens 'Keep Default Model' not opened !" );
    }
    dataSourceWizardPage.buttonOK();
    dataSourceWizardPage.makeClickable();
    selectDataSourcePage.switchToFrame();

    PIRReportPage pirReportPage = new PIRReportPage( getDriver() );

    if ( !pirReportPage.checkDataSourceName( dbTableDataSource.getName() ) ) {
      Assert.fail( "  TS074638:(DBTable) PIR report not opened using created data source !" );
    }
  }
  
  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createSQLDataSource" )
  @XlsDataSourceParameters( sheet = "SQL_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSSQL003_EDIT" )
  @SpiraTestSteps( testStepsId = "74639,74640,74641,74645,74646" )
  public void editDataSource( Map<String, String> args ) {
    SQLDataSource editedDataSource = new SQLDataSource( args );

    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.selectDataSourceWithoutOK( sqlDataSource.getName() );
    selectDataSourcePage.switchToFrame();
    DataSourceModelEditorPage dataSourceModelEditorPage = selectDataSourcePage.clickEdit();
    if ( !dataSourceModelEditorPage.isDataSourceModelEditorOpened() ) {
      Assert.fail( "  TS074639: Data Source Model Editor is not opened !" );
    }

    selectDataSourcePage.switchToDefault();
    DataSourceWizardPage dataSourceWizardPage = dataSourceModelEditorPage.editSource();
    editedDataSource.setParameters();
    dataSourceWizardPage.buttonFinish();
    dataSourceModelEditorPage.buttonOK();
    selectDataSourcePage.switchToFrame();

    if ( !selectDataSourcePage.isDataSourcePresent( sqlDataSource.getName() ) ) {
      Assert.fail( "TS074641: 'SQL Query' data source was removed after editing!" );
    }
  }

  @Test( dependsOnMethods = "createCSVDataSource" )
  @SpiraTestSteps( testStepsId = "74642,74643,74644" )
  public void deleteDataSource() {
    HomePage homePage = getHomePage();
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();
    selectDataSourcePage.selectDataSourceWithoutOK( csvDataSource.getName() );
    selectDataSourcePage.switchToFrame();
    selectDataSourcePage.clickDelete();
    if ( !selectDataSourcePage.verifyRemoveDataSourceDialog() ) {
      Assert.fail( "TS074642: 'Remove data source dialog not shown !" );
    }

    selectDataSourcePage.clickCancelRemoveDlg();
    if ( !selectDataSourcePage.isDataSourcePresent( csvDataSource.getName() ) ) {
      Assert.fail( "TS074643: 'CSV data source was removed !" );
    }

    selectDataSourcePage.selectDataSourceWithoutOK( csvDataSource.getName() );
    selectDataSourcePage.clickDelete();
    selectDataSourcePage.clickOKRemoveDlg();
    pause( EXPLICIT_TIMEOUT / 2 );
    if ( !selectDataSourcePage.isDataSourceNotPresent( csvDataSource.getName() ) ) {
      Assert.fail( "TS074644: 'CSV data source was not removed !" );
    }
  }

  private static HomePage getHomePage() {
    return homePages.get();
  }

  private static void setHomePage( HomePage homePage ) {
    homePages.set( homePage );
  }
}
