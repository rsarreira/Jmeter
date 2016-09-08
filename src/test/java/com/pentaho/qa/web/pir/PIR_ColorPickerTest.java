package com.pentaho.qa.web.pir;

import java.util.Map;

import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.Report.Colour;
import com.pentaho.services.Report.CustomColor;
import com.pentaho.services.pir.PIRReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/20/TestCase/11896.aspx
@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PIR_ColorPicker )
public class PIR_ColorPickerTest extends WebBaseTest {

  private PIRReport pirReport;
  private PIRReportPage reportPage;
  private final String reportsSheet = "PIRReports";

  String presentItem;
  String notPresentItem;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PIR07" )
  @SpiraTestSteps( testStepsId = "64011, 64012, 64013, 64014, 64023" )
  public void createReportAndChangeFormatting( Map<String, String> args ) {
    // Initialize and create new PIR report
    pirReport = new PIRReport( args );
    reportPage = pirReport.create( false );

    reportPage.openFormattingTab();
    reportPage.selectColumnHeader( pirReport.getColumns().get( 0 ) );

    // TS064011 verification
    pirReport.verifyDefaultFormattingColors();

    // TS064012, TS064013, TS064023 verification
    pirReport.editColumnHeaderFormattingInPaletteTab( pirReport.getColumns().get( 0 ), Colour.RED, Colour.YELLOW );
  }

  @Test( description = "JIRA# PIR-1061" )
  @SpiraTestSteps( testStepsId = "64015, 64023" )
  public void changeFormattingWithInUseColor() {
    reportPage.clickColor( Colour.BLACK, true );
    pirReport.verifyCurrentColor( Colour.BLACK );
    reportPage.selectColumnHeader( pirReport.getColumns().get( 0 ) );
    reportPage.openBackgroundColorPicker();
    reportPage.clickColor( Colour.MATISSE );
    pirReport.verifyCurrentColor( Colour.MATISSE );
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# PIR-1061" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PIR07" )
  @SpiraTestSteps( testStepsId = "64016, 64017" )
  public void verifyCurrentColorInColorPicker( Map<String, String> args ) {
    // TODO add TS064023 verification
    pirReport = new PIRReport( args );
    reportPage = pirReport.create();
    // TS64016
    pirReport.verifyColumnFormatting( pirReport.getColumns().get( 1 ) );
    // TS064017
    pirReport.verifyColumnFormatting( pirReport.getColumns().get( 0 ) );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = reportsSheet, dsUid = "Title", executeColumn = "TUID", executeValue = "PIR07" )
  @SpiraTestSteps( testStepsId = "64018, 64023" )
  public void setCustomColorWithRgbValues( Map<String, String> args ) {
    pirReport = new PIRReport( args );
    reportPage = pirReport.create();
    // TS64018 verification
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 0 ), Colour.BLACK,
        Colour.GREEN );
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 1 ), Colour.RED,
        Colour.BLACK );
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 2 ), Colour.GREEN,
        Colour.RED );
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 0 ), Colour.BLUE,
        Colour.YELLOW );
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 1 ), Colour.WHITE,
        Colour.BLUE );
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.RGB, pirReport.getColumns().get( 2 ), Colour.YELLOW,
        Colour.WHITE );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64019, 64023" )
  public void setCustomColorWithHexValue() {
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.HEX, pirReport.getColumns().get( 0 ), Colour.MAGENTA,
        Colour.RED );
  }

  @Test( description = "JIRA# PIR-1061" )
  @SpiraTestSteps( testStepsId = "64020, 64021, 64023" )
  public void setCustomColorWithColorField() {
    // TODO: once this bug is fixed, implement more verifications
    pirReport.editColumnHeaderFormattingInCustomTab( CustomColor.COLOR_FIELD, pirReport.getColumns().get( 0 ),
        Colour.RED, Colour.CELLO );
  }
}
