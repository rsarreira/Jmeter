package com.pentaho.qa.web.analyzer;

import java.util.Map;

import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

//http://spiratest.pentaho.com/8/TestCase/11778.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.TabBehaviorEncoding )
public class TabBehaviorEncoding extends WebBaseTest {
  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private AnalyzerReportPage analyzerPage;
  private PAReport paReport;
  private String presentItem;
  private String notPresentItem;
  private SoftAssert softAssert;
  private HomePage homePage;

  @Test
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dependsOnMethods = "testLogin", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR14" )
  @SpiraTestSteps( testStepsId = "62558" )
  public void openReportAnalyzerInNewWindow( Map<String, String> args ) {
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    // save report
    //paReport.save( folder );
    //paReport.close( true );
    // this method call will save AND close the report.
    paReport.close( folder );
    
    homePage = new HomePage( getDriver() );
    BrowseFilesPage browsePage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );

    // open recently created report
    paReport.open();
    analyzerPage.openTabInNewWindow();

    String oldWindowsHandle = getDriver().getWindowHandle();

    // switching to lastOpenedWindow
    analyzerPage.switchToLastWindow();

    // need for activation iframe in newly opened tab
    analyzerPage.setupFrame();

    // verification part
    presentItem = args.get( "VerifyPresent" );
    notPresentItem = args.get( "VerifyNotPresent" );

    softAssert = analyzerPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();

    String newWindowsHandle = getDriver().getWindowHandle();

    if ( oldWindowsHandle.equals( newWindowsHandle ) ) {
      Assert.fail( "TS062558: The report is not opened in a new tab window!" );
    }
  }

  @Test( dependsOnMethods = "openReportAnalyzerInNewWindow", description = "JIRA# ANALYZER-3032" )
  @SpiraTestSteps( testStepsId = "62559" )
  public void generateXMLFile() {
    String currentURL = getDriver().getCurrentUrl();
    String newURL = analyzerPage.changePartUrlOnParameter( currentURL );
    getDriver().navigate().to( newURL );

    ExtendedWebElement xmlFile =
        analyzerPage
            .findExtendedWebElement( By
                .xpath( "//*[text()='This XML file does not appear to have any style information associated with it. The document tree is shown below.']" ) );

    // verification part
    if ( !xmlFile.isElementPresent() ) {
      Assert.fail( "TS062559: XML file is not generated in the browser!" );
    }
  }
}
