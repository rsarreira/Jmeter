package com.pentaho.qa.web.analyzer;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.HandleSide;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.utils.ExportType;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/8/TestCase/11793.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Column_Sizes )
public class Column_Sizes extends WebBaseTest {

  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";

  private AnalyzerReportPage analyzerPage;
  private PAReport paReport;
  private int afterResize;
  private int beforeResize;
  private static String Sales = "Sales";

  @Test( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dependsOnMethods = "testLogin", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR13" )
  @SpiraTestSteps( testStepsId = "62679" )
  public void createAnalyzerReport( Map<String, String> args ) {
    String Country = "Country";
    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    beforeResize = analyzerPage.verifyColumnWidth( Country );

    // The border between the two fields will be resized.

    analyzerPage.resizeColumnWidth( Sales, HandleSide.LEFT, -50 );

    // verification part
    afterResize = analyzerPage.verifyColumnWidth( Country );
    if ( beforeResize == afterResize ) {
      Assert.fail( "TS062679: The columns cannot be resized smaller than the default! " );
    }
  }

  @Test( dependsOnMethods = "createAnalyzerReport" )
  @SpiraTestSteps( testStepsId = "62680" )
  public void exprotFileInPDF() {
    analyzerPage.exportAs( ExportType.PDF );
  }

  @Test( dependsOnMethods = "exprotFileInPDF" )
  @SpiraTestSteps( testStepsId = "62681" )
  public void resetColumnSizes() {
    analyzerPage.resetColumnSizes();

    beforeResize = analyzerPage.verifyColumnWidth( Sales );

    // verification part
    if ( beforeResize == afterResize ) {
      Assert
          .fail( "TS062681: The columns don't reset to the default size where all of the data and the column header is visible! " );
    }
  }

  @Test( dependsOnMethods = "resetColumnSizes" )
  @SpiraTestSteps( testStepsId = "62682" )
  public void exceedsLongestValue() {

    beforeResize = analyzerPage.verifyColumnWidth( Sales );

    // The border between the two fields will be resized.

    analyzerPage.resizeColumnWidth( Sales, HandleSide.RIGHT, 500 );

    // verification part
    afterResize = analyzerPage.verifyColumnWidth( Sales );
    if ( beforeResize == afterResize ) {
      Assert.fail( "TS062682: The columns cannot be resized larger than the default!" );
    }
  }

  @Test( dependsOnMethods = "exceedsLongestValue" )
  @SpiraTestSteps( testStepsId = "62683" )
  public void exprotFileInPDF2() {
    analyzerPage.exportAs( ExportType.PDF );
  }

  @Test( dependsOnMethods = "exprotFileInPDF2" )
  @SpiraTestSteps( testStepsId = "62684" )
  public void returnToDefaultWidths() {
    // The columns are returned to their default widths
    analyzerPage.resetColumnSizes();

    afterResize = analyzerPage.verifyColumnWidth( Sales );

    // verification part
    if ( beforeResize != afterResize ) {
      Assert
          .fail( "TS062681: The columns is not reseted to the default size where all of the data and the column header is visible! " );
    }
  }
}
