package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRFilterPage;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPanelPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.pir.Prompt;
import com.pentaho.services.pir.Prompt.Control;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_PromptingFunctionalTest )
public class PIR_PromptingFunctionalTest extends WebBaseTest {

  private PIRReport pirReport;
  private Prompt territoryPrompt;
  private Prompt productLinePrompt;
  private Prompt customerNamePrompt;
  private PIRFilter productLineFilter;
  private PIRFilter customerNameFilter;
  private PIRFilter territoryFilter;
  private PIRReportPage reportPage;
  private PIRPromptPage promptPage;
  private PIRFilterPage filterPage;
  private PIRFilterPanelPage filterPanelPage;
  private PIRPromptPanelPage promptPanelPage;

  private final String reportsSheet = "PIRReports";
  private final String promptsSheet = "Prompts";
  private final String editedName = "EditedPromptName";

  private String presentItem;
  private String notPresentItem;
  private Folder folder;
  private SoftAssert softAssert = new SoftAssert();

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PIR12" )
  public void prepareTest( Map<String, String> args ) {
    webUser.login( getDriver() );

    pirReport = new PIRReport( args );
    reportPage = pirReport.create( false );

    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "prepareTest" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT01" )
  @SpiraTestSteps( testStepsId = "75074,75075,75076,75077,75078,75079" )
  public void addTerritoryPrompt( Map<String, String> args ) {
    territoryPrompt = new Prompt( args );
    pirReport.addPrompt( Workflow.CONTEXT_PANEL, territoryPrompt );
    promptPanelPage = pirReport.getPromptPanelPage();

    if ( !reportPage.isPromptsPanelShown() ) {
      Assert.fail( "TS031452: Prompt Panel is not present!" );
    }

    if ( !promptPanelPage.isPromptExists( territoryPrompt ) ) {
      Assert.fail( "TS031452: " + territoryPrompt.getName() + " prompt is not present!" );
    }

    // verify Prompt Items
    if ( !territoryPrompt.verifyItems( args.get( "VerifyPresent" ) ) ) {
      Assert.fail( "TS031452: " + territoryPrompt.getName() + " prompt is missing values!" );
    }

    if ( !reportPage.isAutoSubmitChecked() ) {
      Assert.fail( "TS031452: Auto-Submit is not checked by default!" );
    }

    // TS075076: verify that APAC is the only territory in the report.
    softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();

    reportPage.showFilterPanel();
    filterPanelPage = pirReport.getFilterPanelPage();

    territoryFilter = pirReport.getFilters().get( 0 );

    // TS075075 verify that Territory filter was added.
    if ( !filterPanelPage.isFilterExists( territoryFilter ) ) {
      Assert.fail( "TS075075: Filter for " + territoryFilter.getValue() + " is not present in Filter panel!" );
    }

    // TS075077 check human readable filter
    if ( !pirReport.verifyHumanReadableFilterCreatedByPrompt( territoryPrompt.getField(), territoryFilter
        .getParamName() ) ) {
      Assert.fail( "TS075077: Human readable filter is not correct, it should be: " + filterPanelPage
          .getReadableFilter() );
    }

    // TS075078 and TS075079
    reportPage.hidePromptsPanel();
    if ( reportPage.isPromptsPanelShown() ) {
      Assert.fail( "TS075078: The Prompts dialog is not hidden " );
    }
    reportPage.showPromptsPanel();
    if ( !reportPage.isPromptsPanelShown() ) {
      Assert.fail( "TS075079: The Prompts dialog is not shown " );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "addTerritoryPrompt" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT02" )
  @SpiraTestSteps( testStepsId = "75080,75081,75082,75083,75084,75085" )
  public void addProductLinePrompt( Map<String, String> args ) {
    productLinePrompt = new Prompt( args );
    pirReport.addPrompt( Workflow.CONTEXT_PANEL, productLinePrompt );
    // TS075080 verify prompt was added successfully.
    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS075080: " + productLinePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    // TS075081 verify edit prompt page was opened successfully.
    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.setControl( Control.LIST );
    productLinePrompt.setControl( Control.LIST );
    if ( !promptPage.verifyListControl() ) {
      Assert.fail( "TS075081: Control List not opened!" );
    }

    // TS075085
    promptPage.setDisplayedValues( args.get( "DisplayedValues" ) );
    promptPage.clickOK();

    if ( !promptPanelPage.verifyDisplayedValues( productLinePrompt.getName(), args.get( "DisplayedValues" ) ) ) {
      Assert.fail( "TS075085: wrong count Displayed Values !" );
    }
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "addProductLinePrompt" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT02" )
  @SpiraTestSteps( testStepsId = "75086,75087,75088,75089" )
  public void multipleSelection( Map<String, String> args ) {
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( true, Control.LIST );
    promptPanelPage.selectElements( productLinePrompt.getName(), args.get( "SelectElements" ) );
    // TS075088 Select multiple products
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );

    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( false, Control.LIST );
    // TS075089 The user will only be able to make one selection
    softAssert = promptPanelPage.verifyPromptErrorMessage();

    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( true, Control.LIST );

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "multipleSelection" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT03" )
  @SpiraTestSteps( testStepsId = "75090,75091,75092,75093,75094,75095" )
  public void addCustomerNamePrompt( Map<String, String> args ) {
    customerNamePrompt = new Prompt( args );
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
    pirReport.addPrompt( Workflow.CONTEXT_PANEL, customerNamePrompt );

    // TS075090 verify prompt was added successfully.
    if ( !promptPanelPage.isPromptExists( customerNamePrompt ) ) {
      Assert.fail( "TS075090: " + customerNamePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    promptPage = promptPanelPage.editPrompt( customerNamePrompt );
    promptPage.setControl( Control.RADIO_BUTTONS );
    customerNamePrompt.setControl( Control.RADIO_BUTTONS );
    promptPage.clickOK();
    // TS075093 prompt will update with the new control type radio buttons
    softAssert = promptPanelPage.verifyPromptControlType( customerNamePrompt );

    promptPage = promptPanelPage.editPrompt( customerNamePrompt );
    promptPage.clickOK();
    // TS075094 prompt should be shown as before
    softAssert = promptPanelPage.verifyPromptControlType( customerNamePrompt );

    promptPanelPage.selectElementRadioButton( args.get( "SelectElements" ) );
    // TS07595 verify selection through the list of Radio Buttons
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );

    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "addCustomerNamePrompt" )
  @SpiraTestSteps( testStepsId = "75096,75097,75098,75099" )
  public void testCheckboxOption() {

    promptPage = promptPanelPage.editPrompt( territoryPrompt );
    promptPage.setControl( Control.CHECKBOX );
    territoryPrompt.setControl( Control.CHECKBOX );
    // TS075098 select the specify radio button
    promptPage.selectSpecify();
    if ( !promptPage.checkSpecifyTextbox() ) {
      Assert.fail( "TS075098: specify area not editable !" );
    }
    promptPage.clickOK();
    // TS075099 prompt will update with the new control type checkbox
    softAssert = promptPanelPage.verifyPromptControlType( territoryPrompt );

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testCheckboxOption" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT04" )
  @SpiraTestSteps( testStepsId = "75100,75101,75102,75103,75104,75105,75106,75107" )
  public void testButtonOption( Map<String, String> args ) {
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.setControl( Control.BUTTONS );
    productLinePrompt.setControl( Control.BUTTONS );
    promptPage.clickOK();

    // TS075102 prompt will update with the new control type buttons
    softAssert = promptPanelPage.verifyPromptControlType( productLinePrompt );

    promptPanelPage.selectElementButton( args.get( "Value" ) );
    // TS075103 verify selection through the list of Radio Buttons
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );

    // TS75104,75105,75106 Button multiple selection
    reportPage.refreshPromptsPanel();
    presentItem = args.get( "SelectElements" );

    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( true, Control.BUTTONS );
    promptPanelPage.selectButtonElements( presentItem );
    reportPage.refreshPromptsPanel();
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );

    // TS75107 The user will only be able to make one selection
    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( false, Control.BUTTONS );
    softAssert = promptPanelPage.verifyPromptErrorMessage();

    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.checkMultipleSelection( true, Control.BUTTONS );
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testButtonOption" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT03" )
  @SpiraTestSteps( testStepsId = "75108,75109,75110,75111" )
  public void testSpecifyBox( Map<String, String> args ) {
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    promptPage = promptPanelPage.editPrompt( customerNamePrompt );
    promptPage.setControl( Control.TEXT_FIELD );
    customerNamePrompt.setControl( Control.TEXT_FIELD );

    promptPage.selectSpecify();
    if ( !promptPage.checkSpecifyTextbox() ) {
      Assert.fail( "TS075110: specify area not editable !" );
    }
    promptPage.setSpecify( args.get( "SelectElements" ) );
    promptPage.clickOK();

    softAssert = promptPanelPage.verifyPromptControlType( customerNamePrompt );
    // TS075111 The report will update with the proper customer name
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );
    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testSpecifyBox" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT03" )
  @SpiraTestSteps( testStepsId = "75112,75113,75114" )
  public void testDeleteCustomerNamePrompt( Map<String, String> args ) {
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
    
    // Added this refresh due to the report area scrolling down, causing the prompt buttons to be hidden.
    promptPanelPage.refreshPromptsPanel();
    
    // TS075112 The prompt disappears but the report will remain the same
    promptPanelPage.deletePrompt( customerNamePrompt );
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );

    // TS075113,TS075114 Delete the Customer Name Filter
    reportPage.showFilterPanel();
    customerNameFilter = pirReport.getFilters().get( 2 );
    filterPanelPage.deleteFilter( customerNameFilter );
    presentItem = notPresentItem;
    notPresentItem = "";
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "testDeleteCustomerNamePrompt" )
  @SpiraTestSteps( testStepsId = "75115,75116,75117,75118" )
  public void testDeleteProductLinePrompt() {
    productLineFilter = pirReport.getFilters().get( 1 );

    // TS075117,TS075118 Rename the filter parameter from Product Line
    filterPage = filterPanelPage.editFilter( productLineFilter );
    filterPage.setParamName( editedName );
    filterPage.clickOK();

    if ( !filterPage.isWarningDialogPresent( productLineFilter ) ) {
      Assert.fail( "TS075117: Warning message not displayed!" );
    }
    filterPage.clickYes();
    if ( !filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS075118: Filter is not present!" );
    }
    reportPage.showPromptsPanel();
    if ( promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS075118: Prompt is present!" );
    }

    reportPage.showFilterPanel();
    filterPanelPage.deleteFilter( productLineFilter );

    // TS075115, TS075116 Delete prompt
    pirReport.addPrompt( Workflow.CONTEXT_PANEL, productLinePrompt );
    reportPage.showFilterPanel();
    filterPanelPage.deleteFilter( productLineFilter );
    if ( !filterPanelPage.isDeleteFilterAndParameterDialogPresent() ) {
      Assert.fail( "TS075115: dialog is not shown !" );
    }
    filterPanelPage.clickYes();
    pause( 2 );
    if ( !filterPanelPage.isFilterNotExist( productLineFilter ) ) {
      Assert.fail( "TS075116: Filter is present!" );
    }
    reportPage.showPromptsPanel();
    if ( promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS075116: Prompt is present!" );
    }
  }

  @Test( dependsOnMethods = "testDeleteProductLinePrompt" )
  @SpiraTestSteps( testStepsId = "75119,75120,75121" )
  public void testEditProductLinePrompt() {
    pirReport.addPrompt( Workflow.CONTEXT_PANEL, productLinePrompt );
    promptPage = promptPanelPage.editPrompt( productLinePrompt );
    promptPage.setName( editedName );
    productLinePrompt.setName( editedName );
    promptPage.clickOK();

    if ( !promptPage.isWarningDialogPresent( productLinePrompt ) ) {
      Assert.fail( "TS075120: Warning message not displayed!" );
    }
    filterPage.clickYes();
    reportPage.refreshPromptsPanel();
    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS075121: Prompt is not present!" );
    }
    productLineFilter = pirReport.getFilters().get( 1 );
    reportPage.showFilterPanel();
    filterPage = filterPanelPage.editFilter( productLineFilter );
    if ( !filterPage.getParamName().equals( editedName ) ) {
      Assert.fail( "TS075121: Filter paramName is not updated!" );
    }
    filterPage.clickOK();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testEditProductLinePrompt" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT03" )
  @SpiraTestSteps( testStepsId = "75122,75123,75124" )
  public void testCloseSaveReopen( Map<String, String> args ) {
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    pirReport.save( folder );
    pirReport.close();
    pirReport.open();
    reportPage.showPromptsPanel();

    // TS075123 Verify that the prompts are still working correctly
    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS075123: Prompt is not present!" );
    }
    if ( !promptPanelPage.isPromptExists( territoryPrompt ) ) {
      Assert.fail( "TS075123: Prompt is not present!" );
    }
    softAssert = reportPage.verifyContentByValue( presentItem, notPresentItem );
    pirReport.close();
  }

  @AfterClass( )
  public void removeReport() {
    HomePage homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.BROWSE_FILES );
    pirReport.remove();
    pirReport.deletePermanently();
  }
}
