package com.pentaho.qa.web.puc.dat;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.datasource.DataSourceDBTablePage;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.connection.JdbcConnection;
import com.pentaho.services.puc.datasource.DBTableDataSource;
import com.pentaho.services.puc.datasource.SQLDataSource;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/6/TestCase/11922.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.Model_Smoketest )
public class ModelSmoketest extends WebBaseTest {
  private SQLDataSource sqlDataSource;
  private DBTableDataSource dbTableDataSource;
  private DataSourceDBTablePage dsTablesPage;
  private DataSourceModelEditorPage modelEditorPage;
  private String fieldID, fieldID2;
  private ManageDataSourcesPage manage;
  private String modelItem;
  public static final String ADDRESSLINE1_LEVEL = "/Dimensions/ADDRESSLINE1/ADDRESSLINE1/";
  public static final String ADDRESSLINE2_LEVEL = "/Dimensions/ADDRESSLINE2/ADDRESSLINE2/";
  public static final String CREDITLIMIT_MEASURE = "/Measures/CREDITLIMIT";
  public static final String PHONE_DIMENSION = "/Dimensions/PHONE";
  public static final String STATE = "STATE";

  private static String fieldName;

  @Test( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "83561" )
  @XlsDataSourceParameters( path = "XLS_data/PUC_DataProvider.xls", sheet = "JDBC_Connections", executeColumn = "TUID",
      executeValue = "JDBC04" )
  public void testNewHypersonicConnection( Map<String, String> args ) {
    JdbcConnection conn = new JdbcConnection( args );
    LOGGER.info( conn.getName() );

    if ( !conn.create() ) {
      Assert.fail( "TS083561: Hypersonic-SampleData connection'" + conn.getName() + "' was not created successfully!" );
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "83561" )
  @XlsDataSourceParameters( path = "XLS_data/PUC_DataProvider.xls", sheet = "SQL_DataSources", dsUid = "name",
      executeColumn = "TUID", executeValue = "DSSQL05" )
  public void testNewSQLDataSource( Map<String, String> args ) {
    sqlDataSource = new SQLDataSource( args );

    // create workflow
    sqlDataSource.openWizard();
    sqlDataSource.setName();
    sqlDataSource.selectType();
    sqlDataSource.setParameters();
    sqlDataSource.finishWizard();

    if ( !sqlDataSource.verify() ) {
      Assert.fail( "TS083561: SQL Query datasource '" + sqlDataSource.getName() + "' was not created successfully!" );
    }
  }

  @Test( dependsOnMethods = "testNewSQLDataSource" )
  @SpiraTestSteps( testStepsId = "83561" )
  public void openEdit() {

    HomePage homePage = new HomePage( getDriver() );
    manage = homePage.openManageDataSources();

    modelEditorPage = manage.editDataSource( sqlDataSource );
  }

  @Test( dependsOnMethods = "openEdit" )
  @SpiraTestSteps( testStepsId = "83562" )
  @Parameters( { "addNewItem" } )
  public void addMeasure( String name ) {
    fieldName = name;
    modelEditorPage.expandAllAnalysis();
    modelEditorPage.addItem( BasePage.MEASURE, fieldName );

    // Verification part
    if ( !modelEditorPage.isItemPresent( BasePage.MEASURE + fieldName ) ) {
      Assert.fail( "TS083562: Measure " + fieldName + " was not created" );
    }
  }

  @Test( dependsOnMethods = "addMeasure", description = "JIRA# BACKLOG-7684", priority = 0 )
  @SpiraTestSteps( testStepsId = "83563" )
  @Parameters( { "renameItem" } )
  public void renameMeasure( String renameItem ) {
    modelEditorPage.renameItem( BasePage.MEASURE + fieldName, renameItem );

    // Verification part
    if ( !modelEditorPage.isItemPresent( BasePage.MEASURE + renameItem ) ) {
      Assert.fail( "TS083563: Measure " + renameItem + " was not renamed" );
    } else {
      fieldName = renameItem;
    }
  }

  @Test( dependsOnMethods = "addMeasure", priority = 1 )
  @SpiraTestSteps( testStepsId = "83564" )
  public void deleteMeasure() {
    modelEditorPage.deleteAnalysisItem( BasePage.MEASURE + fieldName );

    // Verification part
    if ( modelEditorPage.isItemPresent( BasePage.MEASURE + fieldName ) ) {
      Assert.fail( "TS083564: Measure " + fieldName + " was not deleted" );
    }
  }

  @Test( dependsOnMethods = "deleteMeasure" )
  @SpiraTestSteps( testStepsId = "83565" )
  @Parameters( { "addNewItem" } )
  public void addLevel( String name ) {
    fieldName = name;
    modelEditorPage.addItem( ADDRESSLINE1_LEVEL, fieldName );

    // Verification part
    if ( !modelEditorPage.isItemPresent( ADDRESSLINE1_LEVEL + fieldName ) ) {
      Assert.fail( "TS083565: Level " + fieldName + " was not added" );
    }
  }

  @Test( dependsOnMethods = "addLevel" )
  @SpiraTestSteps( testStepsId = "83566" )
  public void deleteLevel() {
    modelEditorPage.deleteAnalysisItem( ADDRESSLINE1_LEVEL + fieldName );

    // Verification part
    if ( modelEditorPage.isItemPresent( ADDRESSLINE1_LEVEL + fieldName ) ) {
      Assert.fail( "TS083566: Level " + fieldName + " was not deleted" );
    }
  }

  @Test( dependsOnMethods = "deleteLevel" )
  @SpiraTestSteps( testStepsId = "83567" )
  @Parameters( { "addNewItem" } )
  public void addDimension( String name ) {
    fieldName = name;
    modelEditorPage.addItem( BasePage.DIMENSION, fieldName );

    // Verification part
    if ( !modelEditorPage.isItemPresent( BasePage.DIMENSION + fieldName ) ) {
      Assert.fail( "TS083567: Dimension " + fieldName + " was not added" );
    }
  }

  @Test( dependsOnMethods = "addDimension" )
  @SpiraTestSteps( testStepsId = "83568" )
  @Parameters( { "renameItem" } )
  public void renameDimension( String renameItem ) {
    modelEditorPage.renameItem( BasePage.DIMENSION + fieldName, renameItem );

    // Verification part
    if ( !modelEditorPage.isItemPresent( BasePage.DIMENSION + renameItem ) ) {
      Assert.fail( "TS083568: Dimension " + renameItem + " was not renamed" );
    } else {
      fieldName = renameItem;
    }
  }

  @Test( dependsOnMethods = "addDimension" )
  @SpiraTestSteps( testStepsId = "83569" )
  public void deleteDimension() {
    modelEditorPage.deleteAnalysisItem( BasePage.DIMENSION + fieldName );

    // Verification part
    if ( modelEditorPage.isItemPresent( BasePage.DIMENSION + fieldName ) ) {
      Assert.fail( "TS083569: Dimension " + fieldName + " was not deleted" );
    }
  }

  @Test( dependsOnMethods = "deleteDimension" )
  @SpiraTestSteps( testStepsId = "83570" )
  public void dlgAutoPopulateModel() {
    modelEditorPage.openDlgAutoPopulateModel();

    // Verification part
    if ( !modelEditorPage.isDlgAutoPopulateModelOpened() ) {
      Assert.fail( "TS083570: Dialog 'Auto Populate Model' is not opened: " );
    }
  }

  @Test( dependsOnMethods = "dlgAutoPopulateModel" )
  @SpiraTestSteps( testStepsId = "83571" )
  public void cancelAutoPopulate() {
    modelEditorPage.cancelAutoPopulate();

    // Verification part
    if ( modelEditorPage.isDlgAutoPopulateModelOpened() ) {
      Assert.fail( "TS083571: Dialog 'Auto Populate Model' is not closed: " );
    }
  }

  @Test( dependsOnMethods = "cancelAutoPopulate" )
  @SpiraTestSteps( testStepsId = "83572" )
  @Parameters( { "addNewItem" } )
  public void confirmAutoPopulate( String dimensionName ) {
    modelEditorPage.addItem( BasePage.DIMENSION, dimensionName );
    modelEditorPage.autoPopulateModel();

    // Verification part
    if ( modelEditorPage.isItemPresent( BasePage.DIMENSION + dimensionName ) ) {
      Assert.fail( "TS083572: Model was not populated with default dimensions and measures" );
    }
  }

  @Test( dependsOnMethods = "confirmAutoPopulate" )
  @SpiraTestSteps( testStepsId = "83573" )
  public void reportingTabCancelAutoPopulate() {
    modelEditorPage.switchToReportingTab();
    modelEditorPage.expandAllReporting();
    modelEditorPage.openDlgAutoPopulateModel();
    modelEditorPage.cancelAutoPopulate();

    // Verification part
    if ( modelEditorPage.isDlgAutoPopulateModelOpened() ) {
      Assert.fail( "TS083573: Dialog 'Auto Populate Model' is not closed: " );
    }
  }

  @Test( dependsOnMethods = "reportingTabCancelAutoPopulate" )
  @SpiraTestSteps( testStepsId = "83574" )
  @Parameters( { "addNewItem" } )
  public void reportingTabConfirmAutoPopulate( String categoryItem ) {
    modelEditorPage.addItem( BasePage.CATEGORY, categoryItem );
    modelEditorPage.autoPopulateModel();

    // Verification part
    if ( modelEditorPage.isItemPresent( BasePage.CATEGORY + " " + categoryItem ) ) {
      Assert.fail( "TS083574: Model was not populated with default dimensions and measures" );
    }
  }

  @Test( dependsOnMethods = "reportingTabConfirmAutoPopulate" )
  @SpiraTestSteps( testStepsId = "83575" )
  public void clearModelDialogIsOpened() {
    modelEditorPage.openDlgReportingClearModel();

    // Verification part
    if ( !modelEditorPage.isDlgClearModelOpened() ) {
      Assert.fail( "TS083575: Dialog 'Clear Model' is not opened: " );
    }
  }

  @Test( dependsOnMethods = "clearModelDialogIsOpened" )
  @SpiraTestSteps( testStepsId = "83576" )
  public void cancelClearModelDialog() {
    modelEditorPage.cancelClearModel();

    // Verification part
    if ( modelEditorPage.isDlgClearModelOpened() ) {
      Assert.fail( "TS083576: Dialog 'Clear Model' is not closed: " );
    }
  }

  @Test( dependsOnMethods = "cancelClearModelDialog" )
  @SpiraTestSteps( testStepsId = "83577" )
  public void confirmClearModelDialog() {
    modelEditorPage.openDlgReportingClearModel();
    modelEditorPage.confirmClearModel();

    // Verification part
    if ( modelEditorPage.isDlgClearModelOpened() ) {
      Assert.fail( "TS083577: Dialog 'Clear Model' is not closed: " );
    }
  }

  @Test( dependsOnMethods = "confirmClearModelDialog" )
  @SpiraTestSteps( testStepsId = "83578" )
  public void okButtonIsNotEnabled() {
    if ( modelEditorPage.isButtonEnabled( DataSourceModelEditorPage.btnOKLabel ) ) {
      Assert.fail( "TS083578: Button '" + DataSourceModelEditorPage.btnOKLabel + "' is not enabled!" );
    }
  }

  @Test( dependsOnMethods = "okButtonIsNotEnabled" )
  @SpiraTestSteps( testStepsId = "83578" )
  public void autoPopulateModel() {
    modelEditorPage.autoPopulateClearedModel();
  }

  @Test( dependsOnMethods = "autoPopulateModel" )
  @SpiraTestSteps( testStepsId = "83579" )
  public void downArrow() {
    fieldID = modelEditorPage.getFieldId( CREDITLIMIT_MEASURE );
    modelEditorPage.moveDownField();
    fieldID2 = modelEditorPage.getFieldId( CREDITLIMIT_MEASURE );

    // verification part
    if ( !modelEditorPage.isElementMoved( fieldID, fieldID2 ) ) {
      Assert.fail( "TS083579: The field " + CREDITLIMIT_MEASURE + " was not moved!" );
    }
  }

  @Test( dependsOnMethods = "downArrow" )
  @SpiraTestSteps( testStepsId = "83580" )
  public void upArrow() {
    fieldID = modelEditorPage.getFieldId( PHONE_DIMENSION );
    modelEditorPage.moveUpField();
    fieldID2 = modelEditorPage.getFieldId( PHONE_DIMENSION );

    // verification part
    if ( !modelEditorPage.isElementMoved( fieldID, fieldID2 ) ) {
      Assert.fail( "TS083580: The field " + PHONE_DIMENSION + " was not moved!" );
    }
  }

  @Test( dependsOnMethods = "upArrow" )
  @SpiraTestSteps( testStepsId = "83581" )
  public void collapseAllAnalysisFields() {
    modelEditorPage.switchToAnalysisTab();
    modelEditorPage.expandAllAnalysis();
    modelEditorPage.collapseAllAnalysis();

    // Verification part
    if ( modelEditorPage.isItemPresent( CREDITLIMIT_MEASURE ) ) {
      Assert.fail( "TS083581: All the expanded fields is not collapsed." );
    }
  }

  @Test( dependsOnMethods = "collapseAllAnalysisFields" )
  @SpiraTestSteps( testStepsId = "83582" )
  public void expandAllAnalysisFields() {
    modelEditorPage.switchToAnalysisTab();
    modelEditorPage.collapseAllAnalysis();
    modelEditorPage.expandAllAnalysis();

    // Verification part
    if ( !modelEditorPage.isItemPresent( CREDITLIMIT_MEASURE ) ) {
      Assert.fail( "TS083582: All the measures and dimensions is not expanded" );
    }
  }

  @Test( dependsOnMethods = "expandAllAnalysisFields" )
  @SpiraTestSteps( testStepsId = "83583" )
  public void propertySectionForString() {

    String ADDRESSLINE = "/ADDRESSLINE1";
    modelItem = BasePage.CATEGORY + sqlDataSource.getName() + ADDRESSLINE;
    modelEditorPage.selectItem( modelItem );
    List<String> expectedTypes = Arrays.asList( "NONE", "COUNT", "COUNT_DISTINCT" );
    List<String> actualTypes = modelEditorPage.getAggregationTypes();

    // verification part
    if ( !expectedTypes.equals( actualTypes ) ) {
      Assert
          .fail( "TS083583: String data types don't correspond to the expected ones. Expected ones are the following, Actual are: "
              + expectedTypes );
    }
  }

  @Test( dependsOnMethods = "propertySectionForString" )
  @SpiraTestSteps( testStepsId = "83584" )
  public void propertySectionForNumeric() {

    String CREDITLIMIT = "/CREDITLIMIT";
    modelItem = BasePage.CATEGORY + sqlDataSource.getName() + CREDITLIMIT;
    modelEditorPage.selectItem( modelItem );
    List<String> expectedTypes =
        Arrays.asList( "NONE", "SUM", "AVERAGE", "MINIMUM", "MAXIMUM", "COUNT", "COUNT_DISTINCT" );
    List<String> actualTypes = modelEditorPage.getAggregationTypes();

    // verification part
    if ( !expectedTypes.equals( actualTypes ) ) {
      Assert
          .fail( "TS083584: String data types don't correspond to the expected ones. Expected ones are the following, Actual are: "
              + expectedTypes );
    }
  }

  @Test( dependsOnMethods = "propertySectionForNumeric" )
  @SpiraTestSteps( testStepsId = "83585,83586" )
  @Parameters( { "addNewItem" } )
  public void addCategory( String categoryName ) {
    modelEditorPage.selectItem( BasePage.CATEGORY );
    modelEditorPage.addItem( BasePage.CATEGORY, categoryName );

    // Verification part
    if ( !modelEditorPage.isItemPresent( BasePage.CATEGORY + categoryName ) ) {
      Assert.fail( "TS083585,TS083586: Category " + categoryName + " was not created" );
    }
  }

  @Test( dependsOnMethods = "addCategory" )
  @SpiraTestSteps( testStepsId = "83587" )
  @Parameters( { "addNewItem", "addNewItem2" } )
  public void dragAndDropLevel( String addLevel, String addLevel2 ) {

    String CREDITLIMIT_HIERARCHY = "/Dimensions/CREDITLIMIT/";

    // Add Level1
    modelEditorPage.addItem( ADDRESSLINE1_LEVEL, addLevel );
    modelEditorPage.selectItem( ADDRESSLINE1_LEVEL + addLevel );
    modelEditorPage.fixLevelBtn();

    // Verification "FixSourceColumn frame"
    if ( !modelEditorPage.isSourceColumnFrameIsOpened() ) {
      Assert.fail( "TS083587: Dialog 'Select Source Column' is not opened!" );
    }
    modelEditorPage.selectSourceColumn( STATE );
    modelEditorPage.resolveColumnsAccept();

    // Verification 'Level Source Column' is added on the 'Data Source Model Editor'.

    if ( !modelEditorPage.isLevelSourceColumnAdded( STATE ) ) {
      Assert.fail( "TS083587: 'Level Source Column is added on the 'Data Source Model Editor'." );
    }

    Utils.dnd( modelEditorPage.getItemByPath( ADDRESSLINE1_LEVEL + addLevel ), modelEditorPage
        .getItemByPath( CREDITLIMIT_HIERARCHY ) );

    // Verification
    if ( modelEditorPage.isItemPresent( ADDRESSLINE1_LEVEL + addLevel ) ) {
      Assert.fail( "TS083587: Item 'level' " + addLevel + " was not moved" );
    }

    // Add Level2
    modelEditorPage.addItem( ADDRESSLINE2_LEVEL, addLevel2 );
    modelEditorPage.selectItem( ADDRESSLINE2_LEVEL + addLevel2 );
    modelEditorPage.fixLevelBtn();

    // Verification "FixSourceColumn frame"
    if ( !modelEditorPage.isSourceColumnFrameIsOpened() ) {
      Assert.fail( "TS083587: Dialog 'Select Source Column' is not opened!" );
    }
    modelEditorPage.selectSourceColumn( STATE );
    modelEditorPage.resolveColumnsAccept();

    // Verification 'Level Source Column is added on the 'Data Source Model Editor'.

    if ( !modelEditorPage.isLevelSourceColumnAdded( STATE ) ) {
      Assert.fail( "TS083587: 'Level Source Column is added on the 'Data Source Model Editor'." );
    }

    Utils.dnd( modelEditorPage.getItemByPath( ADDRESSLINE2_LEVEL + addLevel2 ), modelEditorPage
        .getItemByPath( CREDITLIMIT_HIERARCHY ) );

    // Verification
    if ( modelEditorPage.isItemPresent( ADDRESSLINE2_LEVEL + addLevel2 ) ) {
      Assert.fail( "TS083587: Item 'level' " + addLevel2 + " was not moved" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "dragAndDropLevel" )
  @SpiraTestSteps( testStepsId = "83589" )
  @XlsDataSourceParameters( path = "XLS_data/PUC_DataProvider.xls", sheet = "DBTable_DataSources", dsUid = "name",
      executeColumn = "TUID", executeValue = "DSDBT05" )
  public void dlgInvalidModel( Map<String, String> args ) {

    String tree = "Measures";

    // close all dlgs
    modelEditorPage.buttonCancel();
    modelEditorPage.clickBtnCloseManageDS();

    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();

    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();

    dsTablesPage.selectTables( dbTableDataSource.getTables(), true );

    if ( dsTablesPage.isFactTableAvailable() ) {
      dsTablesPage.selectFactTable( dbTableDataSource.getFactTable() );
    }

    if ( dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      dsTablesPage.buttonNext();
    }

    List<String> joins = dbTableDataSource.getJoins();
    dsTablesPage.createJoins( joins );

    dsTablesPage.buttonFinish();
    dsTablesPage.buttonOK();

    if ( !dbTableDataSource.verify() ) {
      Assert
          .fail( "TS083589: DB_Tables datasource '" + dbTableDataSource.getName() + "' was not created successfully!" );
    }

    manage = dbTableDataSource.openManageDataSource();
    modelEditorPage = manage.editDataSource( dbTableDataSource );
    modelEditorPage.clearAnalysisModel();
    Utils.dnd( modelEditorPage.getAvailableField( STATE ), modelEditorPage.getClearTree( tree ) );

    // Verification part
    if ( !modelEditorPage.isDlgInvalidModel() ) {
      Assert.fail( "TS083589: Dialog 'Invalid Model' is not opened!" );
    }
  }

  @Test( dependsOnMethods = "dlgInvalidModel" )
  @SpiraTestSteps( testStepsId = "83589" )
  public void unableAddDimenFromDiffTableToLevel() {

    String tree = "Dimensions";
    String YEAR_ID = "YEAR ID";
    String YEAR_ID_LVL = "/Dimensions/YEAR ID/YEAR ID/";

    modelEditorPage.clickBtnOKInvalidModelDlg();

    if ( modelEditorPage.isDlgInvalidModel() ) {
      Assert.fail( "TS083589: Dialog 'Invalid Model' is not closed!" );
    }

    Utils.dnd( modelEditorPage.getAvailableField( YEAR_ID ), modelEditorPage.getClearTree( tree ) );
    Utils.dnd( modelEditorPage.getAvailableField( STATE ), modelEditorPage.getItemByPath( YEAR_ID_LVL ) );

    if ( !modelEditorPage.isDlgInvalidModel() ) {
      Assert.fail( "TS083589: Dialog 'Invalid Model' is not opened!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "unableAddDimenFromDiffTableToLevel" )
  @SpiraTestSteps( testStepsId = "83590" )
  @XlsDataSourceParameters( path = "XLS_data/PUC_DataProvider.xls", sheet = "DBTable_DataSources", dsUid = "name",
      executeColumn = "TUID", executeValue = "DSDBT10" )
  public void createPirReportWithJoinFieldRemovedFromModel( Map<String, String> args ) {

    String CUSTOMERNUMBER = "CUSTOMERNUMBER";
    // close all dlgs

    modelEditorPage.clickBtnOKInvalidModelDlg();
    modelEditorPage.buttonCancel();
    modelEditorPage.clickBtnCloseManageDS();

    dbTableDataSource = new DBTableDataSource( args );

    dbTableDataSource.openWizard();
    dbTableDataSource.setName();
    dbTableDataSource.selectType();

    dsTablesPage = new DataSourceDBTablePage( getDriver() );

    dbTableDataSource.selectSourceType();
    dsTablesPage.buttonNext();

    dsTablesPage.selectTables( dbTableDataSource.getTables(), true );

    if ( dsTablesPage.isFactTableAvailable() ) {
      dsTablesPage.selectFactTable( dbTableDataSource.getFactTable() );
    }

    if ( dsTablesPage.isButtonEnabled( BasePage.btnNextLabel ) ) {
      dsTablesPage.buttonNext();
    }

    List<String> joins = dbTableDataSource.getJoins();
    dsTablesPage.createJoins( joins );

    dsTablesPage.buttonFinish();
    dsTablesPage.buttonOK();

    if ( !dbTableDataSource.verify() ) {
      Assert
          .fail( "TS083592: DB_Tables datasource '" + dbTableDataSource.getName() + "' was not created successfully!" );
    }

    manage = dbTableDataSource.openManageDataSource();
    modelEditorPage = manage.editDataSource( dbTableDataSource );

    modelEditorPage.expandAllReporting();

    modelEditorPage.deleteFieldCategory( CUSTOMERNUMBER );
    modelEditorPage.buttonOK();
    modelEditorPage.clickBtnCloseManageDS();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createPirReportWithJoinFieldRemovedFromModel" )
  @SpiraTestSteps( testStepsId = "83590" )
  @XlsDataSourceParameters( path = "XLS_data/PIR_DataProvider.xls", sheet = "PIRReports", dsUid = "Title",
      executeColumn = "TUID", executeValue = "PIR13", spiraColumn = "TeststepId" )
  public void createInteractiveReport( Map<String, String> args ) {

    PIRReport pirReport = new PIRReport( args );
    PIRReportPage interactiveReportPage = pirReport.create( true );
    interactiveReportPage.switchToFrame();

    // Add columns
    pirReport.addColumns();

    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );
    SoftAssert softAssert = interactiveReportPage.verifyContent( presentItem, notPresentItem );

    pirReport.close();

    softAssert.assertAll();
  }
}
