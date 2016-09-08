package com.pentaho.qa.web.puc.dat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.datasource.DataSourceCSVPage;
import com.pentaho.qa.gui.web.datasource.DataSourceCSVPage.RadioBox;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.datasource.CSVDataSource;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

//http://spiratest.pentaho.com/6/TestCase/8939.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.Data_Source_Smoketest_CSV )
public class Data_Source_Smoketest_CSV extends WebBaseTest {
  private ManageDataSourcesPage manageDataSourcesPage;
  private DataSourceModelEditorPage modelEditorPage;
  private CSVDataSource csvDataSource;
  private HomePage homePage;
  private DataSourceCSVPage dsCsvPage;
  private SoftAssert softAssert;
  private String textFilePreview;
  private List<ExtendedWebElement> oldStagingColumns;
  private List<ExtendedWebElement> newStagingColumns;

  @BeforeClass( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSCSV04" )
  @SpiraTestSteps( testStepsId = "40676" )
  public
    void openManageDataSource( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );
    csvDataSource.openWizard();
    csvDataSource.setName();
    csvDataSource.selectType();

    dsCsvPage = new DataSourceCSVPage( getDriver() );

    softAssert = new SoftAssert();
    List<String> delimeterList = new ArrayList<String>();
    {
      delimeterList.add( "Comma" );
      delimeterList.add( "Semicolon" );
      delimeterList.add( "Tab" );
      delimeterList.add( "Space" );
      delimeterList.add( "Other" );
    }

    List<String> enclosureList = new ArrayList<String>();
    {
      enclosureList.add( "Double Quote" );
      enclosureList.add( "Single Quote" );
      enclosureList.add( "None" );
    }

    // verification part.
    if ( !dsCsvPage.getUploadedFile().equals( "" ) ) {
      softAssert.fail( "TS040676: File name is not blank by default!" );
    }

    if ( !dsCsvPage.getEncodingType().equals( "" ) ) {
      softAssert.fail( "Encoding is not blank by default!" );
    }

    if ( !delimeterList.equals( dsCsvPage.getListDelimeterBoxes() ) ) {
      softAssert.fail( "Delimeter list of boxes doesn't match with teamplate!" );
    }

    if ( !dsCsvPage.isRadioBoxSelectedWithoutFile( RadioBox.COMMA ) ) {
      softAssert.fail( "RadioBox " + RadioBox.COMMA.name() + " is not selected by default!" );
    }

    if ( !enclosureList.equals( dsCsvPage.getListEnclosureBoxes() ) ) {
      softAssert.fail( "Enclosure list of boxes doesn't match with teamplate!" );
    }

    if ( !dsCsvPage.isRadioBoxSelectedWithoutFile( RadioBox.DOUBLE_QUOTE ) ) {
      softAssert.fail( "RadioBox " + RadioBox.COMMA.name() + " is not selected by default!" );
    }

    if ( !dsCsvPage.isHeaderCheckBoxDisplayed() ) {
      softAssert.fail( "The checkbox 'First row is header' is not displayed on the page!" );
    }

    if ( !dsCsvPage.isCsvTextPreviewDisplayed() ) {
      softAssert.fail( "The section 'File Preview' is not displayed!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40677" )
  public void validatingButtonsState() {
    // verification part.
    softAssert = new SoftAssert();
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnBackLabel ) ) {
      softAssert.fail( "TS040677: Button '" + BasePage.btnBackLabel + "' is enabled!" );
    }
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnNextLabel ) ) {
      softAssert.fail( "TS040677: Button '" + BasePage.btnNextLabel + "' is enabled!" );
    }
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnFinishLabel ) ) {
      softAssert.fail( "TS040677: Button '" + BasePage.btnFinishLabel + "' is enabled!" );
    }
    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnCancelLabel ) ) {
      softAssert.fail( "TS040677: Button '" + BasePage.btnCancelLabel + "' is disabled!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40678" )
  public void dlgImportFile() {
    dsCsvPage.openDlgImportFile();

    // verification part.
    if ( !dsCsvPage.isDlgImportFileOpened() ) {
      Assert.fail( "TS040678: Dialog 'Import File' is not opened!" );
    }
    dsCsvPage.btnCancelImportFrame();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSCSV04" )
  @SpiraTestSteps( testStepsId = "40679,40680,40681" )
  public
    void automaticallyDeterminedSettings( Map<String, String> args ) {
    dsCsvPage = new DataSourceCSVPage( getDriver() );
    dsCsvPage.setStagingSettings( args.get( "filePath" ) );
    softAssert = new SoftAssert();

    String encoding = "ISO-8859-1";
    textFilePreview = "12345678";

    // verification part.
    if ( !dsCsvPage.isHeaderCheckBoxChecked() ) {
      softAssert.fail( "TS40679,40680,40681: Checkbox 'First Row is header' is not checked!" );
    }

    if ( !dsCsvPage.isRadioBoxSelected( RadioBox.COMMA ) ) {
      softAssert.fail( "TS40679,40680,40681: RadioBox " + RadioBox.COMMA.name() + " is not selected!" );
    }

    if ( !dsCsvPage.getEncodingType().equals( encoding ) ) {
      softAssert.fail( "TS40679,40680,40681: Enconding type " + dsCsvPage.getEncodingType()
          + " dosen't match with expected " + encoding + " value!" );
    }

    if ( !dsCsvPage.isRadioBoxSelected( RadioBox.DOUBLE_QUOTE ) ) {
      softAssert.fail( "TS40679,40680,40681: RadioBox " + RadioBox.DOUBLE_QUOTE.name() + " is not selected!" );
    }

    if ( !dsCsvPage.getTextFilePreview().contains( textFilePreview ) ) {
      softAssert.fail( "TS40679,40680,40681: File Preview is not populated with the contents of the file!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40682" )
  public void validatingButtonsStateWithUploadedFile() {
    // verification part.
    softAssert = new SoftAssert();
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnBackLabel ) ) {
      softAssert.fail( "TS040682: Button '" + BasePage.btnBackLabel + "' is enabled!" );
    }
    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnNextLabel ) ) {
      softAssert.fail( "TS040682: Button '" + BasePage.btnNextLabel + "' is not active!" );
    }
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnFinishLabel ) ) {
      softAssert.fail( "TS040682: Button '" + BasePage.btnFinishLabel + "' is enabled!" );
    }
    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnCancelLabel ) ) {
      softAssert.fail( "TS040682: Button '" + BasePage.btnCancelLabel + "' is not active!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40683" )
  public void filePreviewReflectNewEnclosure() {
    textFilePreview = "\"123";

    dsCsvPage.setEnclosure( RadioBox.SINGLE_QUOTE );
    pause( 3 );
    // verification part.
    if ( !dsCsvPage.getTextFilePreview().contains( textFilePreview ) ) {
      Assert.fail( "TS040683: The File Preview changes doesn't reflect the new enclosure!" );
    }

    dsCsvPage.setEnclosure( RadioBox.DOUBLE_QUOTE );
  }

  @Test
  @SpiraTestSteps( testStepsId = "40684" )
  public void filePreviewReflectNewEncoding() {
    String encodingType = "UTF-16LE";

    dsCsvPage.setEncoding( encodingType );
    pause( 3 );
    textFilePreview = "潎浲污㈬敤";

    // verification part.
    if ( !dsCsvPage.getTextFilePreview().contains( textFilePreview ) ) {
      Assert.fail( "TS040684: The File Preview changes doesn't reflect the new encoding!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40685" )
  public void filePreviewReflectNewDelimeter() {
    String encodingType = "ISO-8859-1";
    dsCsvPage.setEncoding( encodingType );

    dsCsvPage.setDelimiter( RadioBox.SEMICOLON );
    textFilePreview = "1,1.23,1,1.23,1 ,1.23 ,$1";

    // verification part
    if ( !dsCsvPage.getTextFilePreview().contains( textFilePreview ) ) {
      Assert.fail( "TS040685: The File Preview changes doesn't reflect the new encoding!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40686" )
  public void stagingSettingsPage() {
    dsCsvPage.setDelimiter( RadioBox.COMMA );
    dsCsvPage.buttonNext();

    // verification part.
    oldStagingColumns = dsCsvPage.getStagingColumns();
    dsCsvPage.verifyStagingColumns( dsCsvPage.getStagingColumns() );
  }

  @Test
  @SpiraTestSteps( testStepsId = "40687" )
  public void deSelectAllCheckBoxes() {
    dsCsvPage.deSelectAllCheckBoxes();

    dsCsvPage.isDeselectedAllCheckBoxes();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40688" )
  public void validatingButtonsStateDeselectedCheckboxes() {
    // verification part.
    softAssert = new SoftAssert();
    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnBackLabel ) ) {
      softAssert.fail( "TS040688: Button '" + BasePage.btnBackLabel + "' is disabled!" );
    }
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnNextLabel ) ) {
      softAssert.fail( "TS040688: Button '" + BasePage.btnNextLabel + "' is enabled!" );
    }
    if ( dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnFinishLabel ) ) {
      softAssert.fail( "TS040688: Button '" + BasePage.btnFinishLabel + "' is enabled!" );
    }
    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnCancelLabel ) ) {
      softAssert.fail( "TS040688: Button '" + BasePage.btnCancelLabel + "' is disabled!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40689" )
  public void selectAllCheckBoxes() {
    dsCsvPage.selectAllCheckBoxes();

    softAssert = new SoftAssert();

    // verification part.
    dsCsvPage.isSelectedAllCheckBoxes();

    if ( !dsCsvPage.isButtonEnabled( AnalyzerReportPage.btnFinishLabel ) ) {
      softAssert.fail( "TS040689: The Button '" + BasePage.btnFinishLabel + "' is disabled!" );
    }
    softAssert.assertAll();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40690" )
  public void typePopupContent() {
    List<String> typePopup = new ArrayList<String>();
    {
      typePopup.add( "STRING" );
      typePopup.add( "DATE" );
      typePopup.add( "BOOLEAN" );
      typePopup.add( "NUMERIC" );
    }

    dsCsvPage.openTypeContent();
    if ( !dsCsvPage.verifyPopupContent( typePopup ) ) {
      Assert.fail( "TS040690: Column type values don't match with teamplate!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40691" )
  public void sourceFormatPopupContent() {
    List<String> sourceFormatPopup = new ArrayList<String>();
    {
      sourceFormatPopup.add( "0.00" );
      sourceFormatPopup.add( "#.#" );
      sourceFormatPopup.add( "#" );
      sourceFormatPopup.add( "#,##0.###" );
      sourceFormatPopup.add( "###,###,###.#" );
      sourceFormatPopup.add( "$#,###" );
      sourceFormatPopup.add( "$#,###.00;($#,###.00)" );
    }
    dsCsvPage.openFormatContent();
    if ( !dsCsvPage.verifyPopupContent( sourceFormatPopup ) ) {
      Assert.fail( "TS040691: Column type values don't match with teamplate!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40692" )
  public void editableLengthColumn() {
    String value = "2";
    String oldValue = dsCsvPage.getColumnLength();

    dsCsvPage.setColumnLength( value );

    String newValue = dsCsvPage.getColumnLength();

    // verification part.
    verifyEditedColumn( oldValue, newValue );
  }

  @Test
  @SpiraTestSteps( testStepsId = "40693" )
  public void editableNameColumn() {
    String value = "name1";

    String oldValue = dsCsvPage.getColumnName();

    dsCsvPage.setColumnName( value );

    String newValue = dsCsvPage.getColumnName();

    // verification part.
    verifyEditedColumn( oldValue, newValue );
  }

  @Test
  @SpiraTestSteps( testStepsId = "40694" )
  public void editablePrecisionColumn() {
    String value = "precision_1";
    String oldValue = dsCsvPage.getColumnPrecision();

    dsCsvPage.setColumnPrecision( value );

    String newValue = dsCsvPage.getColumnPrecision();

    // verification part. editable field
    verifyEditedColumn( oldValue, newValue );

    // verification part 2. columns values
    // columns names
    String numericColumn = "2decimals";
    String stringColumn = "Percent";
    String dateColumn = "MM/dd/yyyy";
    String dateStringValue = "0";
    String numericValue = "2";

    dsCsvPage.selectColumn( numericColumn );

    if ( !dsCsvPage.getColumnPrecision().equals( numericValue ) ) {
      Assert.fail( "TS040694: The received value doesn't match with expected value!" );
    }

    dsCsvPage.selectColumn( stringColumn );

    if ( !dsCsvPage.getColumnPrecision().equals( dateStringValue ) ) {
      Assert.fail( "TS040694: The received value doesn't match with expected value!" );
    }
    dsCsvPage.selectColumn( dateColumn );

    if ( !dsCsvPage.getColumnPrecision().equals( dateStringValue ) ) {
      Assert.fail( "TS040694: The received value doesn't match with expected value!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40695" )
  public void sourceFormatDate() {
    String formatColumn = "MM/dd/yyyy";

    // verification part.
    dsCsvPage.openFormatContent();
    if ( !dsCsvPage.getPopupContentString().contains( formatColumn ) ) {
      Assert.fail( "TS040695: Column format value don't match with teamplate!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40697" )
  public void showFileContentTenRows() {
    dsCsvPage.openShowFileContent();

    // verification part.
    dsCsvPage.verifyShowFileContentTenRows( dsCsvPage.getShowFileContent() );
    dsCsvPage.closeCsvPreviewDlg();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40698,40699,40700,40701" )
  public void finishCreatingCsvDataSource() {
    // verification part.
    csvDataSource.finishWizard();

    if ( !csvDataSource.verify() ) {
      Assert.fail( "TS040698,TS040699,TS040700,TS040701: CSV datasource '" + csvDataSource.getName()
          + "' was not created successfully!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40705" )
  public void editCreatedCsvDataSource() {
    homePage = new HomePage( getDriver() );
    manageDataSourcesPage = homePage.openManageDataSources();

    modelEditorPage = manageDataSourcesPage.editDataSource( csvDataSource );

    // verification part.
    modelEditorPage.verifyPopulatedAnalysisSection( modelEditorPage.getAnalysisItems() );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV04_1" )
  @SpiraTestSteps( testStepsId = "40706,40707" )
  public void editDataSourceFromModel( Map<String, String> args ) {
    modelEditorPage.editSource();
    dsCsvPage = new DataSourceCSVPage( getDriver() );

    // part1. get Old settings
    String textFilePreviewOld = dsCsvPage.getTextFilePreview();
    // part2. set upload new CSV file.

    dsCsvPage.setStagingSettings( args.get( "filePath" ) );

    String textFilePreviewNew = dsCsvPage.getTextFilePreview();

    // verification part.
    if ( textFilePreviewOld.equals( textFilePreviewNew ) ) {
      Assert
          .fail( "TS040706,TS040707: File Preview and the Staging Settings doesn't show the added fields in the new file!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40708" )
  public void preservedChanges() {
    dsCsvPage.buttonNext();

    // verification part.
    newStagingColumns = dsCsvPage.getStagingColumns();
    if ( oldStagingColumns.equals( newStagingColumns ) ) {
      Assert.fail( "TS040708: The chanes is not preserved!" );
    }
    dsCsvPage.buttonFinish();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSCSV01" )
  @SpiraTestSteps( testStepsId = "40712" )
  public
    void editDataSourceFromModel2( Map<String, String> args ) {
    modelEditorPage.editSource();
    dsCsvPage = new DataSourceCSVPage( getDriver() );
    softAssert = new SoftAssert();

    // part1. get Old settings
    String textFilePreviewOld = dsCsvPage.getTextFilePreview();
    // part2. set upload new CSV file.

    dsCsvPage.setStagingSettings( args.get( "filePath" ) );

    String textFilePreviewNew = dsCsvPage.getTextFilePreview();

    // verification part.
    if ( textFilePreviewOld.equals( textFilePreviewNew ) ) {
      softAssert
          .fail( "TS040712: File Preview and the Staging Settings doesn't show the added fields in the new file!" );
    }

    if ( !dsCsvPage.isRadioBoxSelected( RadioBox.COMMA ) ) {
      softAssert
          .fail( "TS040712: File Preview and the Staging Settings doesn't show the added fields in the new file!" );
    }
    if ( !dsCsvPage.isRadioBoxSelected( RadioBox.DOUBLE_QUOTE ) ) {
      softAssert
          .fail( "TS040712: File Preview and the Staging Settings doesn't show the added fields in the new file!" );
    }
    softAssert.assertAll();

    // close all opened frames.
    csvDataSource.cancelWizard();
    modelEditorPage.buttonCancel();
    manageDataSourcesPage.closeManageDataSources();
  }

  @Test
  @SpiraTestSteps( testStepsId = "40713,40714" )
  public void cancelDeleteCsvDataSource() {
    csvDataSource.delete( false );

    if ( !csvDataSource.verify() ) {
      Assert.fail( "TS040713,TS040714: CSV datasource '" + csvDataSource.getName() + "' deleted!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "40715,49699" )
  public void confirmlDeleteCsvDataSource() {
    csvDataSource.delete( true );

    if ( csvDataSource.verify() ) {
      Assert.fail( "TS040715,TS049699: CSV datasource '" + csvDataSource.getName() + "' is not deleted!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSCSV04_1" )
  @SpiraTestSteps( testStepsId = "60319" )
  public void createCsvSourceUsingTxtFile( Map<String, String> args ) {
    csvDataSource = new CSVDataSource( args );
    csvDataSource.create();

    // verification part.
    csvDataSource.verify();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "CSV_DataSources", dsUid = "name", executeColumn = "TUID", executeValue = "DSCSV04" )
  @SpiraTestSteps( testStepsId = "60317" )
  public
    void defineNumericFields( Map<String, String> args ) {
    String commasneg = "CommasNeg";
    String commas2decimalNeg = "Commas2decimalNeg";
    String numericType = "NUMERIC";

    csvDataSource = new CSVDataSource( args );
    csvDataSource.openWizard();
    csvDataSource.setName();
    csvDataSource.selectType();

    dsCsvPage = new DataSourceCSVPage( getDriver() );
    dsCsvPage.setStagingSettings( args.get( "filePath" ) );

    dsCsvPage.buttonNext();

    // verification part.
    softAssert = new SoftAssert();

    dsCsvPage.selectColumn( commasneg );
    if ( !dsCsvPage.getColumnType().equals( numericType ) ) {
      softAssert.fail( "TS060317: " + commasneg + " is not defined as Numeric type while creating csv datasource!" );
    }

    dsCsvPage.selectColumn( commas2decimalNeg );
    if ( !dsCsvPage.getColumnType().equals( numericType ) ) {
      softAssert.fail( "TS060317: " + commas2decimalNeg
          + " is not defined as Numeric type while creating csv datasource!" );
    }
    softAssert.assertAll();
  }

  public boolean verifyEditedColumn( String oldValue, String newValue ) {
    boolean res = true;
    if ( oldValue.equals( newValue ) ) {
      res = false;
      Assert.fail( "The field is not editable!" );
    }
    return res;
  }
}
