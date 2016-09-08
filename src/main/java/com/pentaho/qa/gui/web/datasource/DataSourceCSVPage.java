package com.pentaho.qa.gui.web.datasource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.CustomAssert;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceCSVPage extends DataSourceWizardPage {

  private static final String ATTRIBUTE_VALUE = "value";

  // private static final String ENCODING_LIST_ITEM =
  // "//div[@class='popupContent']//div[@class='gwt-Label' and text()='%s']";
  @FindBy( xpath = "//div[@class='popupContent']//div[@class='gwt-Label' and text()='%s']" )
  private ExtendedWebElement encodingItem;

  @FindBy( id = "uploadFormElement" )
  protected ExtendedWebElement txtCsvFilePath;

  @FindBy( id = "uploadedFile" )
  protected ExtendedWebElement csvFileUploaded;

  @FindBy( id = "addFileButton" )
  protected ExtendedWebElement btnImport;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOK;

  @FindBy( css = "#encodingTypeMenuList .combo-arrow > div" )
  protected ExtendedWebElement comboArrowEncodingType;

  @FindBy( css = "#encodingTypeMenuList .custom-list-textbox" )
  protected ExtendedWebElement txtEncoding;

  @FindBy( css = "td:nth-child(2) > div.gwt-HTML, td:nth-child(2) > input.xul-textbox" )
  private List<ExtendedWebElement> columnNames;

  // Radio Boxes
  @FindBy( xpath = "//*[@id='delimiterRadioGroup']//*[text()='%s']/preceding-sibling::input" )
  private ExtendedWebElement delimeterRadio;

  @FindBy( xpath = "//*[@id='delimiterRadioGroup']//*[text()]" )
  private List<ExtendedWebElement> listDelimeterBoxes;

  @FindBy( xpath = "//*[@id='enclosureRadioGroup']//*[text()='%s']/preceding-sibling::input" )
  private ExtendedWebElement enclosureRadio;

  @FindBy( xpath = "//*[@id='enclosureRadioGroup']//label[text()]" )
  private List<ExtendedWebElement> listEnclosureBoxes;

  @FindBy( xpath = "//*[@id='isHeaderCheckBox']/*[@type='checkbox']" )
  private ExtendedWebElement isHeaderCheckBox;

  @FindBy( xpath = "//*[@id='csvTextPreview']//following-sibling::*" )
  private ExtendedWebElement csvTextPreview;

  @FindBy( id = "doubleQuote" )
  protected ExtendedWebElement doubleQuote;

  @FindBy( id = "singleQuote" )
  protected ExtendedWebElement singleQuote;

  @FindBy( id = "noneQuote" )
  protected ExtendedWebElement noneQuote;

  @FindBy( xpath = "//*[@id='commaRadio']//*[@name='delimiterRadioGroup']" )
  protected ExtendedWebElement commaRadio;

  @FindBy( id = "tabRadio" )
  protected ExtendedWebElement tabRadio;

  @FindBy( id = "otherRadio" )
  protected ExtendedWebElement otherRadio;

  @FindBy( id = "semicolonRadio" )
  protected ExtendedWebElement semicolonRadio;

  @FindBy( id = "spaceRadio" )
  protected ExtendedWebElement spaceRadio;

  // buttons
  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancelImportFrame;

  @FindBy( id = "main_wizard_window_extra1" )
  private ExtendedWebElement btnBack;

  @FindBy( id = "main_wizard_window_extra2" )
  private ExtendedWebElement btnNext;

  @FindBy( id = "main_wizard_window_accept" )
  private ExtendedWebElement btnFinish;

  @FindBy( id = "main_wizard_window_cancel" )
  private ExtendedWebElement btnWizardCancel;

  @FindBy( xpath = "//*[@class='dialogTopCenterInner']/div[text()='{L10N:fileImportDialog.TITLE}']" )
  private ExtendedWebElement dlgImportFile;

  @FindBy( xpath = "//*[@class='pentaho-dialog pentaho-dialog-buttonless']" )
  private ExtendedWebElement frameImportFile;

  // staging settings page
  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[@class='overflowHide' and contains(text(),'')]" )
  private List<ExtendedWebElement> listStagingColumns;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[text()='%s']" )
  private ExtendedWebElement stagingColumn;

  @FindBy( xpath = "//*[@id='selectAll']/*[text()='{L10N:stateDataStep.SELECT_ALL}']" )
  private ExtendedWebElement selectAll;

  @FindBy( xpath = "//*[@id='selectAll']/*[text()='{L10N:stateDataStep.DESELECT_ALL}']" )
  private ExtendedWebElement deSelectAll;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//input[@type='checkbox']" )
  private List<ExtendedWebElement> listCheckBoxes;

  @FindBy( xpath = "//div[@class='popupContent']//*[text()]" )
  private List<ExtendedWebElement> popupContent;

  @FindBy( xpath = "//div[@class='popupContent']//*[text()='%s']" )
  private ExtendedWebElement columnTypeValue;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[2]/*" )
  private ExtendedWebElement columnName;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[1]/*" )
  private ExtendedWebElement checkboxColumn;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[3]//*[text()]" )
  private ExtendedWebElement columnType;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[3]//*[@class='combo-arrow']" )
  private ExtendedWebElement columnTypeArrow;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[4]//*[@type='text']" )
  private ExtendedWebElement columnFormat;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[4]//*[@class='combo-arrow']" )
  private ExtendedWebElement columnFormatArrow;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[5]/*" )
  private ExtendedWebElement columnLength;

  @FindBy( xpath = "//*[@id='csvModelDataTable']//*[contains(@class,'selected')]/td[6]/*" )
  private ExtendedWebElement columnPrecision;

  @FindBy( id = "tempPreviewButton" )
  private ExtendedWebElement tempPreviewButton;

  @FindBy( xpath = "//*[@id='csvTextPreviewLabel']//following-sibling::*" )
  private ExtendedWebElement textPreviewLabel;

  @FindBy( id = "csvPreviewDialog_accept" )
  private ExtendedWebElement btnPreviewDlgAccept;

  public DataSourceCSVPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      CustomAssert.fail( "40922", "'CVS File' data source type is not opened!" );
    }
  }

  protected boolean isOpened() {
    return getSelectedDataSourceType().equals( SOURCE_TYPE_CSV );
  }

  public void buttonOK() {
    click( btnOK, true );
  }

  // TODO: This method is similar to the existing method "setFile" in DataSourceImportBasePage class
  public void setStagingSettings( String csvFile ) {
    if ( csvFile == null ) {
      return;
    }

    // get script_home location and append filePath to this path
    String current = "";
    try {
      current = new java.io.File( "." ).getCanonicalPath();
    } catch ( IOException e ) {
      throw new RuntimeException( "Unable to identify canonical path for current project directory!", e.getCause() );
    }

    current = current.replace( "\\", "/" );

    LOGGER.info( "current path is: " + current );
    csvFile = current + "/" + csvFile;

    if ( !getDriver().toString().contains( "internet explorer" ) && !getDriver().toString().contains( "LINUX" ) ) {
      // filePath = filePath.replace("\\", "\\\\");
      csvFile = csvFile.replace( "/", "\\\\" );
    }
    LOGGER.info( "csfFile path is: " + csvFile );

    ( (JavascriptExecutor) driver )
        .executeScript( "arguments[0].value='" + csvFile + "'", csvFileUploaded.getElement() );
    pause( EXPLICIT_TIMEOUT / 20 );
    click( btnImport, true );
    pause( EXPLICIT_TIMEOUT / 20 );
    // ((JavascriptExecutor)driver).executeScript("arguments[0].value='" + csvFile + "'", txtCsvFilePath.getElement());
    txtCsvFilePath.getElement().sendKeys( csvFile );
    pause( 1 );
    buttonOK();
    pause( 5 ); // added hardcoded delay as there is no ability to handle using spinner
  }

  public void setDelimiter( String delimiter ) {
    if ( delimiter != null ) {
      for ( RadioBox radioBox : RadioBox.values() ) {
        if ( radioBox.getId().equals( delimiter ) ) {
          setDelimiter( radioBox );
        }
      }
    }
  }

  public void setEnclosure( String enclosure ) {
    if ( enclosure != null ) {
      for ( RadioBox radioBox : RadioBox.values() ) {
        if ( radioBox.getId().equals( enclosure ) ) {
          setEnclosure( radioBox );
        }
      }
    }
  }

  public void setFirstHeaderRow( Boolean headerRow ) {
    if ( headerRow == null ) {
      return;
    }

    // TOFO: need to implement
    LOGGER.error( "Need to implement!" );
  }

  public void setEncoding( String encoding ) {
    if ( encoding == null ) {
      return;
    }

    if ( getEncoding().equals( encoding ) ) {
      LOGGER.warn( "Encoding has been already set to '" + encoding + "', no actions required" );
      return;
    }

    // Open drop down
    click( comboArrowEncodingType );
    click( format( encodingItem, encoding ) );

    return;
  }

  protected String getEncoding() {
    return txtEncoding.getAttribute( ATTRIBUTE_VALUE );
  }

  public boolean isColumnPresent( String columnName ) {
    boolean res = false;

    for ( int i = 0; i < columnNames.size(); i++ ) {
      // check if the column exists
      if ( columnNames.get( i ).getText().equals( columnName ) ) {
        res = true;
      }
    }

    return res;
  }

  public int getColumnNameIndex( String columnName ) {

    for ( int i = 0; i < columnNames.size(); i++ ) {
      // check if the column exists
      if ( columnNames.get( i ).getText().equals( columnName ) ) {
        return i;
      }
    }

    return -1;
  }

  public void editColumnName( String oldName, String newName ) {
    int index = getColumnNameIndex( oldName );
    // click on the column name field to edit it
    selectColumn( oldName );
    type( columnNames.get( index ), newName );

  }

  public void selectColumn( String column ) {
    int index = getColumnNameIndex( column );
    click( columnNames.get( index ) );
  }

  public String getUploadedFile() {
    return csvFileUploaded.getAttribute( ATTRIBUTE_VALUE );
  }

  public String getEncodingType() {
    return txtEncoding.getAttribute( ATTRIBUTE_VALUE );
  }

  public enum RadioBox {
    // delimeter boxes
    COMMA( "fileImportDialog.COMMA" ), TAB( "fileImportDialog.TAB" ), OTHER( "fileImportDialog.OTHER" ), SEMICOLON(
        "fileImportDialog.SEMICOLON" ), SPACE( "fileImportDialog.SPACE" ),

    // encoding boxes
    DOUBLE_QUOTE( "fileImportDialog.DOUBLE" ), SINGLE_QUOTE( "fileImportDialog.SINGLE" ), NONE( "fileImportDialog.NONE" );

    private final String id;

    private RadioBox( String radioBox ) {
      id = radioBox;
    }

    public String getId() {
      return id;
    }
  }

  public void checkRowHeaderCheckBox() {
    if ( !isHeaderCheckBoxChecked() ) {
      isHeaderCheckBox.check();
    }
  }

  public void uncheckRowHeaderCheckBox() {
    if ( isHeaderCheckBoxChecked() ) {
      isHeaderCheckBox.uncheck();
    }
  }

  public Boolean isHeaderCheckBoxChecked() {
    return isHeaderCheckBox.isChecked();
  }

  public Boolean isHeaderCheckBoxDisplayed() {
    return isElementPresent( isHeaderCheckBox );
  }

  public List<String> getListDelimeterBoxes() {
    List<String> listDelimeters = new ArrayList<String>();
    for ( ExtendedWebElement element : listDelimeterBoxes ) {
      listDelimeters.add( element.getText() );
    }
    return listDelimeters;
  }

  public List<String> getListEnclosureBoxes() {
    List<String> listEnclosures = new ArrayList<String>();
    for ( ExtendedWebElement element : listEnclosureBoxes ) {
      listEnclosures.add( element.getText() );
    }
    return listEnclosures;
  }

  public Boolean isCsvTextPreviewDisplayed() {
    return isElementPresent( csvTextPreview );
  }

  public Boolean isRadioBoxSelected( RadioBox radioBox ) {
    Boolean selected;
    if ( isElementPresent( format( delimeterRadio, L10N.getText( radioBox.id ) ) ) ) {
      selected = format( delimeterRadio, L10N.getText( radioBox.id ) ).getElement().isSelected();
    } else {
      selected = format( enclosureRadio, L10N.getText( radioBox.id ) ).getElement().isSelected();
    }
    return selected;
  }

  public boolean isRadioBoxSelectedWithoutFile( RadioBox radioBox ) {
    Boolean result = false;
    if ( isElementPresent( format( delimeterRadio, L10N.getText( radioBox.id ) ) ) ) {
      if ( format( delimeterRadio, L10N.getText( radioBox.id ) ).getAttribute( "disabled" ) != null ) {
        result = true;
      }
    } else {
      if ( format( enclosureRadio, L10N.getText( radioBox.id ) ).getAttribute( "disabled" ) != null ) {
        result = true;
      }
    }
    return result;
  }

  public void openDlgImportFile() {
    btnImport.click();
  }

  public Boolean isDlgImportFileOpened() {
    return isElementPresent( dlgImportFile );
  }

  public void btnCancelImportFrame() {
    btnCancelImportFrame.click();
  }

  public String getTextFilePreview() {
    return csvTextPreview.getText();
  }

  public void setEnclosure( RadioBox radioBox ) {
    switch ( radioBox ) {
      case DOUBLE_QUOTE:
        doubleQuote.click();
        break;

      case SINGLE_QUOTE:
        singleQuote.click();
        break;

      case NONE:
        noneQuote.click();
        break;

      default:
        LOGGER.error( "Please make sure that enclosure was specified is right!" );
        break;
    }
  }

  public void setDelimiter( RadioBox radioBox ) {
    switch ( radioBox ) {
      case COMMA:
        commaRadio.click();
        break;

      case TAB:
        tabRadio.click();
        break;

      case OTHER:
        otherRadio.click();
        break;

      case SEMICOLON:
        semicolonRadio.click();
        break;

      case SPACE:
        spaceRadio.click();
        break;

      default:
        LOGGER.error( "Please make sure that delimeter was specified is right!" );
        break;
    }
  }

  public List<ExtendedWebElement> getStagingColumns() {
    return listStagingColumns;
  }

  public enum StagingColumn {
    NAME( "stageDataStep.COLUMN_NAME" ), TYPE( "stageDataStep.COLUMN_TYPE" ), SOURCE_FORMAT(
        "stageDataStep.COLUMN_FORMAT" ), LENGTH( ( "stageDataStep.COLUMN_LENGTH" ) ), PRECISION(
        "stageDataStep.COLUMN_PRECISION" );

    String id;

    private StagingColumn( String name ) {
      this.id = name;
    }
  }

  public String getStagingColumnName( StagingColumn column ) {
    return format( stagingColumn, L10N.getText( column.id ) ).getText();
  }

  public void verifyStagingColumns( List<ExtendedWebElement> columns ) {
    int i = 1;
    for ( StagingColumn stagingColumn : StagingColumn.values() ) {
      if ( getStagingColumnName( stagingColumn ).equals( columns.get( i ).getText() ) ) {
        LOGGER.info( "The received column matches with column from template!" );
        i++;
      } else {
        Assert.fail( "The received column doesn't match with column from template!" );
      }
    }
  }

  public void selectAllCheckBoxes() {
    selectAll.click();
  }

  public void deSelectAllCheckBoxes() {
    deSelectAll.click();
  }

  public void isDeselectedAllCheckBoxes() {
    // This case need in order to prevent program freezing if large files are uploaded
    for ( int i = 0; i < 3; i++ ) {
      if ( listCheckBoxes.get( i ).isChecked() ) {
        Assert.fail( "The checkbox is checked!" );
      }
    }
  }

  public void isSelectedAllCheckBoxes() {
    // This case need in order to prevent program freezing if large files are uploaded
    for ( int i = 0; i < 3; i++ ) {
      if ( !listCheckBoxes.get( i ).isChecked() ) {
        Assert.fail( "The checkbox is unchecked!" );
      }
    }
  }

  public Boolean verifyPopupContent( List<String> content ) {
    for ( int i = 0; i < popupContent.size(); i++ ) {
      if ( !content.get( i ).equals( popupContent.get( i ).getText() ) ) {
       return false;
      }
    }
    return true;
  }

  public List<String> getPopupContentString() {
    List<String> popupContentString = new ArrayList<String>();
    for ( ExtendedWebElement extendedWebElement : popupContent ) {
      popupContentString.add( extendedWebElement.getText() );
    }
    return popupContentString;
  }

  public enum ColumnType {
    STRING( "STRING" ), DATE( "DATE" ), BOOLEAN( "BOOLEAN" ), NUMERIC( "NUMERIC" );

    String name;

    private ColumnType( String name ) {
      this.name = name;
    }
  }

  public void openTypeContent() {
    columnTypeArrow.click();
  }

  public void setColumnType( String name, ColumnType type ) {
    selectColumn( name );
    openTypeContent();
    format( columnTypeValue, type.name ).click();
  }

  public String getColumnType() {
    return columnType.getText();
  }

  public void setColumnLength( String value ) {
    columnLength.type( value );
  }

  public String getColumnLength() {
    return columnLength.getAttribute( ATTRIBUTE_VALUE );
  }

  public String getColumnName() {
    return columnName.getAttribute( ATTRIBUTE_VALUE );
  }

  public String getColumnPrecision() {
    return columnPrecision.getAttribute( ATTRIBUTE_VALUE );
  }

  public void setColumnName( String value ) {
    columnName.type( value );
  }

  public void setColumnPrecision( String value ) {
    columnPrecision.type( value );
  }

  public void openShowFileContent() {
    tempPreviewButton.click();
  }

  public String getShowFileContent() {
    return textPreviewLabel.getText();
  }

  public void verifyShowFileContentTenRows( String text ) {
    int res = 0;
    String year = "Shipped";
    String[] s = text.split( " " );
    for ( int i = 0; i < s.length; i++ ) {
      if ( year.equals( s[i] ) ) {
        res++;
      }
    }
    if ( res != 9 ) {
      Assert.fail( "A dialog shows the first " + res + " records of the file!" );
    }
  }

  public void openFormatContent() {
    columnFormatArrow.click();
  }

  public void closeCsvPreviewDlg() {
    btnPreviewDlgAccept.click();
  }
}
