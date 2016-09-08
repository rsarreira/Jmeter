package com.pentaho.qa.web.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.puc.BasePage.View;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/8/TestCase/13025.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.SavingSmoketest )
public class SavingSmoketest extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private AnalyzerReportPage analyzerPage;
  private PAReport paReport;
  private PAReport paReportSaveAs;
  private HomePage homePage;
  private Folder folder;
  private String field;
  private Boolean overwrite;
  private List<String> initialState = new ArrayList<String>();
  private List<String> preservedState = new ArrayList<String>();

  @Test
  public void testLogin() {
    homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "testLogin" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR20" )
  @SpiraTestSteps( testStepsId = "71600" )
  public void saveReport( Map<String, String> args ) {
    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    analyzerPage.changeView( View.NAME );

    paReport.save( folder );
  }

  @Test( dependsOnMethods = "saveReport" )
  @SpiraTestSteps( testStepsId = "71601" )
  public void saveWithoutDlg() {
    analyzerPage = paReport.getReportPage();

    field = "Line";
    analyzerPage.moveFieldToTrash( field );

    paReport.save( folder );
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "saveWithoutDlg" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR19" )
  @SpiraTestSteps( testStepsId = "71602" )
  public void saveAs( Map<String, String> args ) {
    PAReport paReportEdit = new PAReport( args );

    paReport.edit( paReportEdit ); // update only name in the report;

    field = "Line";
    analyzerPage.addFieldToSection( GemBarType.ROWS, field );

    paReportSaveAs = paReport.saveAs( folder );
    paReportSaveAs.close();
  }

  @Test( dependsOnMethods = "saveAs" )
  @SpiraTestSteps( testStepsId = "71603" )
  public void savingFileWithoutUpdatingOld() {
    // open old report
    analyzerPage = paReport.open();
    List<String> oldReport = analyzerPage.getColumnsValues();
    paReport.close();

    // open updated report
    analyzerPage = paReportSaveAs.open();
    List<String> updatedReport = analyzerPage.getColumnsValues();

    if ( oldReport.equals( updatedReport ) ) {
      Assert.fail( "TS071603: Saving a new version of the report updates the previous version!" );
    }

    paReportSaveAs.close();
  }

  @Test( dependsOnMethods = "savingFileWithoutUpdatingOld" )
  @SpiraTestSteps( testStepsId = "71604" )
  public void verifyExtensionRewrittenFile() {
    field = "Type";
    overwrite = true;
    analyzerPage = paReport.open();

    analyzerPage.addFieldToSection( GemBarType.COLUMNS, field );

    // overwrite existing report
    paReport.saveAs( folder, overwrite );

    if ( analyzerPage.getSelectedTabName().contains( ".xanalyzer)" ) ) {
      Assert.fail( "TS071604: Report tab is displayed the report name with extension!" );
    }
  }

  @Test( dependsOnMethods = "verifyExtensionRewrittenFile" )
  @SpiraTestSteps( testStepsId = "71605" )
  public void preservedPanelsState() {
    analyzerPage = paReport.getReportPage();

    List<String> availableFieldsPanelOpenedPanel = analyzerPage.getAllAvailableFields();
    Boolean itemPresentOpenedPanel = analyzerPage.isLayoutItemPresent( field );

    // close all panels in report
    analyzerPage.hideAvailableFieldsPanel();
    analyzerPage.hideLayoutPanel( overwrite );

    // Save, close and reopen the report
    paReport.save();
    paReport.close();
    analyzerPage = paReport.open();

    List<String> availableFieldsPanelClosed = analyzerPage.getAllAvailableFields();
    Boolean itemPresentClosedPanel = analyzerPage.isLayoutItemPresent( field );

    // verification part
    if ( availableFieldsPanelOpenedPanel.equals( availableFieldsPanelClosed ) ) {
      Assert.fail( "TS071605:The state of the panels is not preserved!" );
    }

    if ( itemPresentOpenedPanel.equals( itemPresentClosedPanel ) ) {
      Assert.fail( "TS071605:The state of the panels is not preserved!" );
    }
    paReport.close();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "preservedPanelsState" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR13" )
  @SpiraTestSteps( testStepsId = "71606" )
  public void preservedChartsState( Map<String, String> args ) {

    folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReport = new PAReport( args );

    analyzerPage = paReport.create();
    paReport.save( folder );
    paReport.close();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "preservedChartsState" )
  @XlsDataSourceParameters( sheet = "Chart", dsUid = "TUID,ChartType", executeColumn = "TeststepId",
      executeValue = "41410" )
  @SpiraTestSteps( testStepsId = "71606" )
  public void testCharts( Map<String, String> args ) {
    String chart = args.get( "ChartType" );
    ChartType type = ChartType.valueOf( chart );
    paReport.open();
    pause( 3 );

    analyzerPage.changeChartType( type );
    pause( 3 );
    
    initialState = analyzerPage.getSizeCanvasValues();

    // Save, close and reopen the report
    paReport.save();
    paReport.close();
    analyzerPage = paReport.open();
    pause( 3 );
    preservedState = analyzerPage.getSizeCanvasValues();
    
    // verification part
    if ( !initialState.equals( preservedState ) ) {
      Assert.fail( "TS071606:The state of the " + type.name() + " is not preserved!" );
    }

    paReport.close();
  }

  @Test( dependsOnMethods = "testCharts" )
  @SpiraTestSteps( testStepsId = "71607" )
  public void verifyUnsavedReportDlg() {
    paReport.open();
    field = "Sales";
    analyzerPage.moveFieldToTrash( field );
    paReport.close();
  }
}
