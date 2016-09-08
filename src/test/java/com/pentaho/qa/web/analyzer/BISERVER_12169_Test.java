package com.pentaho.qa.web.analyzer;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;

/**
 * This is a test that covers a Chrome-specific bug BISERVER-12169
 *
 * @author Andrey Khayrutdinov
 */
public class BISERVER_12169_Test extends WebBaseTest {

  private static final String STEEL_WHEELS_DS = "SteelWheels: SteelWheelsSales";
  
  @Test
  public void checkChromeHack() {
    final JavascriptExecutor executor = (JavascriptExecutor) getDriver();

    HomePage homePage = webUser.login();
    assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );

    // create two Analyzer's tabs
    homePage.openAnalyzerDataSourcePage();
    homePage.openAnalyzerDataSourcePage();

    AnalyzerDataSourcePage dsPage = homePage.openAnalyzerDataSourcePage();

    // selecting SteelWheels
    AnalyzerReportPage reportPage = dsPage.selectDataSource( STEEL_WHEELS_DS );
    
    // expanding filters' area
    reportPage.showFilterPanel();

    // top location of the tabs' panel
    int before = pickUpTabPanelTopLocation( reportPage, executor );
    assertTrue( before > 0 );


    reportPage.switchToFrame();
    // I didn't manage to the filter both via DnD and right-click context menu
    // so, let's set it programmatically and invoke edit dialog then
    String setFilterScript = "" +
      "cv.api.report.setFilters(\"[Markets].[Country]\", [{\"operator\":\"NOT_CONTAIN\",\"members\":[\"1\"]}]);\n" +
      "cv.api.operation.refreshReport();";
    executor.executeScript( setFilterScript );

    String editFilterScript = "" +
      "cv.getActiveReport().filterDlg.show(\"filter_[Markets].[Country]_1\")";
    executor.executeScript( editFilterScript );

    // click on the hyperlink inside iframe somehow leads to incorrect tabs' panel layouting in Chrome
    reportPage.click( reportPage.filterOp_EQUAL, true );

    // top location of the tabs' panel after clicking on the hyperlink should be the same
    int after = pickUpTabPanelTopLocation( reportPage, executor );
    assertTrue( after > 0 );
    // sometimes there is an artifact in repainting, it disappears after moving mouse above the menu panel
    assertTrue( (before - after) < 10, String.format( "Before: %d, After: %d", before, after ) );
  }

  private int pickUpTabPanelTopLocation( AnalyzerReportPage reportPage, JavascriptExecutor executor ) {
    reportPage.switchToDefault();
    Number top = (Number) executor
      .executeScript( "return document.getElementsByClassName('pentaho-tab-bar')[0].getBoundingClientRect().top" );
    assertNotNull( top );
    return top.intValue();
  }
}
