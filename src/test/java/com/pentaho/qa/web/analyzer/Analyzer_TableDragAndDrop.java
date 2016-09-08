package com.pentaho.qa.web.analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage.PanelItem;
import com.pentaho.qa.gui.web.puc.BasePage.Theme;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.analyzer.PAReport;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/8/TestCase/11718.aspx
@SpiraTestCase( projectId = 8, testCaseId = SpiraTestcases.Analyzer_StandaloneSmokeTest )
public class Analyzer_TableDragAndDrop extends WebBaseTest {

  private static final String ANALYZER_REPORTS_SHEET = "AnalyzerReports";
  private static String field;
  private List<String> rows;
  private List<String> columns;
  private List<String> measures;

  private AnalyzerReportPage analyzerPage;
  private PAReport paReport;

  @Test( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert.assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName()
        + "'!" );
  }

  @Test( dependsOnMethods = "testLogin", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR13" )
  @SpiraTestSteps( testStepsId = "61713" )
  public void createAnalyzerReport( Map<String, String> args ) {

    paReport = new PAReport( args );
    analyzerPage = paReport.create();

    String presentItem = args.get( "VerifyPresent" );
    String notPresentItem = args.get( "VerifyNotPresent" );

    field = args.get( "Rows" );

    analyzerPage.hoverLayoutField( field );

    SoftAssert softAssert = analyzerPage.verifyContent( presentItem, notPresentItem );
    softAssert.assertAll();
  }

  @Test( dependsOnMethods = "createAnalyzerReport", dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = ANALYZER_REPORTS_SHEET, dsUid = "Title", executeColumn = "TUID",
      executeValue = "PAR13" )
  @SpiraTestSteps( testStepsId = "61714" )
  public void reorderFieldsByDnD( Map<String, String> args ) {

    field = args.get( "Rows" );

    analyzerPage.removeAttribute( field );
    rows = new ArrayList<String>( paReport.getRows() );

    // clear Object Rows.
    analyzerPage.clearFieldFromLayout( rows, field );

    // verification part
    if ( analyzerPage.isLayoutItemPresent( field ) ) {
      Assert.fail( "TS061714: Item " + field + " is not moved" );
    }
  }

  @Test( dependsOnMethods = "reorderFieldsByDnD" )
  @SpiraTestSteps( testStepsId = "61715,61716,61717,61718" )
  public void warningMessageMoveHierarchyToOtherAxis() {
    rows = new ArrayList<String>( Arrays.asList( "City", "Country" ) );
    columns = new ArrayList<String>();
    measures = new ArrayList<String>();

    analyzerPage.addFields( rows, columns, measures );

    field = "Country";
    analyzerPage.moveItemInLayout( field, PanelItem.LAYOUT_COLUMNS );

    // verification part
    if ( !analyzerPage.isDlgAlertSameAxisOpened() ) {
      Assert.fail( "TS061715: Dialog 'Alert' is not opened! " );
    }

    analyzerPage.clickDlgBtnCancel();

    // verification part. Moved fields
    if ( PanelItem.LAYOUT_ROWS.getName().equals( columns ) ) {
      Assert.fail( "TS061717: The Fields are moved to the other Axis!" );
    }

    analyzerPage.moveItemInLayout( field, PanelItem.LAYOUT_COLUMNS );
    analyzerPage.clickDlgBtnSave();

    columns.addAll( rows );

    // verification part. Moved fields
    if ( !rows.equals( columns ) ) {
      Assert.fail( "TS061717,TS061718: The Fields are moved to the other Axis!" );
    }
    analyzerPage.clearAllFieldsFromLayout( rows );
  }

  @Test( dependsOnMethods = "warningMessageMoveHierarchyToOtherAxis" )
  @SpiraTestSteps( testStepsId = "61719" )
  public void verifyToolTip() {
    // TODO find best solution for verification tooltips

    rows = new ArrayList<String>( Arrays.asList( "Territory" ) );
    columns = new ArrayList<String>();
    measures = new ArrayList<String>();

    analyzerPage.addFields( rows, columns, measures );
    analyzerPage.hoverLayoutField( field );

    if ( !analyzerPage.getToolTipLayout( field ).equals( field ) ) {
      Assert.fail( "TS061719: The tooltip does not correspond the field name!" );
    }
  }

  @Test( dependsOnMethods = "verifyToolTip" )
  @SpiraTestSteps( testStepsId = "61720" )
  public void moveFieldToTrash() {
    field = "Territory";
    analyzerPage.moveFieldToTrash( field );

    // verification part
    if ( analyzerPage.isLayoutItemPresent( field ) ) {
      Assert.fail( "TS061720: Item " + field + " is not deleted" );
    }
  }

  @Test( dependsOnMethods = "moveFieldToTrash" )
  @SpiraTestSteps( testStepsId = "61721" )
  public void dragFiledInAvailableFieldsList() {
    field = "City";

    List<String> beforeDrag = analyzerPage.getAvailableFieldsList();

    analyzerPage.dragFieldInAvailableFieldsList( field );

    List<String> afterDrag = analyzerPage.getAvailableFieldsList();

    // verification part
    if ( analyzerPage.isLayoutItemPresent( field ) ) {
      Assert.fail( "TS061721: Item " + field + " is moved" );
    }

    if ( !beforeDrag.equals( afterDrag ) ) {
      Assert.fail( "TS061721: The fields are re-ordered!" );
    }
  }

  @Test( dependsOnMethods = "dragFiledInAvailableFieldsList" )
  @SpiraTestSteps( testStepsId = "61722" )
  public void verifyColorDifferentItems() {
    String color;
    analyzerPage.resetReport();
    Theme theme;
    theme = analyzerPage.getTheme();

    rows = new ArrayList<String>( paReport.getRows() );
    columns = new ArrayList<String>( paReport.getColumns() );
    measures = new ArrayList<String>( paReport.getMeasures() );

    analyzerPage.clearAllFieldsFromLayout( rows );
    analyzerPage.clearAllFieldsFromLayout( columns );
    analyzerPage.clearAllFieldsFromLayout( measures );

    rows = new ArrayList<String>( Arrays.asList( "Territory" ) );
    columns = new ArrayList<String>( Arrays.asList( "Type" ) );
    measures = new ArrayList<String>( Arrays.asList( "Sales", "Quantity" ) ); 

    analyzerPage.addFields( rows, columns, measures );

    field = "Territory";

    analyzerPage.hoverAvailableField( field );
    pause( 3 );
    color = analyzerPage.getAvailableFieldColor( field );

    // verification part. The color of field from available list section
    if ( !paReport.verifyAvailableFieldHoverColor( color, theme ) ) {
      Assert.fail( "TS061722: The color of item does not correspond template!" );
    }

    // verification part. The color of field from layout section
    analyzerPage.hoverLayoutField( field );
    pause( 3 );
    color = analyzerPage.getLayoutFieldColor( field );

    if ( !paReport.verifyLayoutRowHoverColor( color, theme ) ) {
      Assert.fail( "TS061722: The color of item does not correspond  template!" );
    }

    // verification part. The color of selected layout section
    color = analyzerPage.getColorSelectedLayoutSetion( field );
    pause( 3 );
    if ( !paReport.verifyLayoutRowOrColumnContainerHoverColor( color, theme ) ) {
      Assert.fail( "TS061722: The color of item does not correspond  template!" );
    }
  }
}
