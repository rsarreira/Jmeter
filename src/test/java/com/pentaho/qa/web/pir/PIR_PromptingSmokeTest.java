package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRFilterPage;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPanelPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.pir.Prompt;
import com.pentaho.services.pir.Prompt.Control;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/20/TestCase/8047.aspx
@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_PromptingSmokeTest )
public class PIR_PromptingSmokeTest extends WebBaseTest {

  private PIRReport pirReport;
  private Prompt territoryPrompt;
  private Prompt productLinePrompt;
  private PIRFilter newFilter;
  private PIRReportPage reportPage;
  private PIRFilterPanelPage filterPanelPage;
  private PIRPromptPanelPage promptPanelPage;
  private final String reportsSheet = "PIRReports";
  private final String promptsSheet = "Prompts";

  String presentItem;
  String notPresentItem;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TeststepId", executeValue = "31452" )
  @SpiraTestSteps( testStepsId = "31452" )
  public
    void createInteractiveReport( Map<String, String> args ) {
    // Initialize and create new PIR report
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( false );

    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT01" )
  @SpiraTestSteps( testStepsId = "31452, 31453, 31454, 31456, 31457" )
  public void addTerritoryPrompt( Map<String, String> args ) {

    territoryPrompt = new Prompt( args );

    pirReport.addPrompt( Workflow.CONTEXT_REPORT, territoryPrompt );

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

    // TS031453: verify that APAC is the only territory in the report.
    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();

    reportPage.showFilterPanel();
    filterPanelPage = pirReport.getFilterPanelPage();

    PIRFilter territoryFilter = pirReport.getFilters().get( 0 );

    // verify that Territory filter was added.
    if ( !filterPanelPage.isFilterExists( territoryFilter ) ) {
      Assert.fail( "TS031454: Filter for " + territoryFilter.getValue() + " is not present in Filter panel!" );
    }

    // check human readable filter
    if ( !pirReport.verifyHumanReadableFilterCreatedByPrompt( territoryPrompt.getField(), territoryFilter
        .getParamName() ) ) {
      Assert.fail( "TS031454: Human readable filter is not correct, it should be: "
          + filterPanelPage.getReadableFilter() );
    }

    // verification for TS031456 and TS031457 happen at the page level.
    reportPage.showPromptsPanel();
    reportPage.hidePromptsPanel();

  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT02" )
  @SpiraTestSteps( testStepsId = "60155, 31460, 60157, 60159, 60160, 31461, 31462, 60161, 31463, 31464, 31465" )
  public void addProductLinePrompt( Map<String, String> args ) {
    productLinePrompt = new Prompt( args );

    pirReport.addPrompt( Workflow.CONTEXT_REPORT, productLinePrompt );
    // verify prompt was added successfully.
    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS060155: " + productLinePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    // verify filter panel and new filter for product line are present.
    reportPage.showFilterPanel();
    // get product line filter
    PIRFilter productLineFilter = pirReport.getFilters().get( 1 );

    if ( !filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS031460: Filter for " + productLineFilter.getValue() + " is not present in Filter panel!" );
    }

    // Edit Product Line filter, TS060157 verification happens in Page
    PIRFilterPage filterPage = filterPanelPage.editFilter( productLineFilter );

    // TS060159 verification happens in Page
    filterPage.clickCancel( filterPage.getName() );

    filterPanelPage.deleteFilter( productLineFilter );
    // a confirmation dialog should appear.
    if ( !filterPanelPage.isDeleteFilterAndParameterDialogPresent() ) {
      Assert.fail( "TS060160: Delete Filter and Prompt dialog is not present!" );
    }

    filterPanelPage.clickNo();
    // verify that the filter and prompt are not removed.
    if ( !filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS031461: Filter for " + productLineFilter.getValue() + " is not present in Filter panel!" );
    }

    reportPage.showPromptsPanel();

    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS031461: " + productLinePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    reportPage.showFilterPanel();

    pirReport.deleteFilter( productLinePrompt );
    filterPanelPage.clickYes();
    
    // verify that the filter and prompt are removed.
    if ( filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS031462: Filter for " + productLineFilter.getValue() + " is present in Filter panel!" );
    }

    reportPage.showPromptsPanel();

    if ( promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS031462: " + productLinePrompt.getName() + " prompt is present in the Prompt panel!" );
    }

    pirReport.addPrompt( Workflow.CONTEXT_REPORT, productLinePrompt );
    // verify prompt was added successfully and report updates.
    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS060161: " + productLinePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    // TS060161: verify that the report is filtering by Territory and Product Line.
    SoftAssert softAssert = reportPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();

    reportPage.showFilterPanel();
    newFilter = productLineFilter;
    newFilter.setParamName( "Some Other Name" );

    // TS031463 verification happens in the page
    filterPage = pirReport.editFilter( productLinePrompt, newFilter, false );

    // verify that the filter and prompt are preserved.
    if ( !filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS031464: Filter for " + productLineFilter.getValue() + " is not present in Filter panel!" );
    }

    reportPage.showPromptsPanel();

    if ( !promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS031464: " + productLinePrompt.getName() + " prompt is not present in the Prompt panel!" );
    }

    reportPage.showFilterPanel();

    filterPage = pirReport.editFilter( productLinePrompt, newFilter );

    // verify that filter is still present and prompt is deleted.
    if ( !filterPanelPage.isFilterExists( productLineFilter ) ) {
      Assert.fail( "TS031465: Filter for " + productLineFilter.getValue() + " is not present in Filter panel!" );
    }

    if ( !newFilter.verify() ) {
      Assert.fail( "TS031465: The filter was not updated!" );
    }

    reportPage.showPromptsPanel();

    if ( promptPanelPage.isPromptExists( productLinePrompt ) ) {
      Assert.fail( "TS031465: " + productLinePrompt.getName() + " prompt is present in the Prompt panel!" );
    }

    reportPage.showPromptsPanel();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = promptsSheet, dsUid = "Name", executeColumn = "TUID", executeValue = "PRMPT01_EDIT" )
  @SpiraTestSteps( testStepsId = "31466, 31468, 60175" )
  public
    void editTerritoryPrompt( Map<String, String> args ) {
    Prompt newPrompt = new Prompt( args );

    PIRPromptPage promptPage = pirReport.editPrompt( territoryPrompt, newPrompt );

    // verify a dialog is shown saying that the filter will be updated
    if ( !promptPage.isWarningDialogPresent( territoryPrompt ) ) {
      Assert.fail( "TS031466: The warning dialog is not present!" );
    }
    
    promptPage.clickYes();
    
    // the prompt is renamed and the filter parameter is updated.
    if ( newPrompt.verify() ) {
      Assert.fail( "TS031468: The prompt was not updated!" );
    }

    reportPage.showFilterPanel();

    if ( !newFilter.verifyFilterPromptNameUpdate( newPrompt ) ) {
      Assert.fail( "TS031468: The filter was not updated to use the new param name: " + newPrompt.getName() );
    }

    // edit the prompt again and change the prompt control type
    newPrompt.setControl( Control.LIST );
    reportPage.showPromptsPanel();
    promptPage = pirReport.editPrompt( territoryPrompt, newPrompt );

    if ( newPrompt.verify() ) {
      Assert.fail( "TS060175: The prompt was not updated!" );
    }
  }
}
