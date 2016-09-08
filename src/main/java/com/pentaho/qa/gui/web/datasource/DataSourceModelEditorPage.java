package com.pentaho.qa.gui.web.datasource;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceModelEditorPage extends BasePage {

  public final static String btnOKLabel = L10N.getText( "Ok_txt" );
  private final static String DIMENSION_TREE = "dimensionTree/";
  private final static String CATEGORY_TREE = "categoriesTree/";

  /**
   * We need so large xpath because it's impossible to just load all children of tree - it required ~80 sec.
   * 
   * These xpath can't be optimized using '//' because it will find wrong path.
   */
  @FindBy( xpath = "//div[@id='%s']/div/div/div/table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']" )
  private ExtendedWebElement dimensionItemLevel1;

  @FindBy(
      xpath = "//div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/div/table/tbody/tr/td/div[text()='%s'] | //div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']" )
  private ExtendedWebElement dimensionItemLevel2;

  @FindBy(
      xpath = "//div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/div/table/tbody/tr/td/div[text()='%s'] | //div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']" )
  private ExtendedWebElement dimensionItemLevel3;

  @FindBy(
      xpath = "//div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/div/table/tbody/tr/td/div[text()='%s'] | //div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']" )
  private ExtendedWebElement dimensionItemLevel4;

  @FindBy(
      xpath = "//div[@id='%s']/div/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div[table/tbody/tr/td/div/table/tbody/tr/td/div[text()='%s']]/div/div/div/table/tbody/tr/td/div[text()='%s']" )
  private ExtendedWebElement dimensionItemLevel5;

  @FindBy(
      xpath = "//div[@id='%s']//tr[td/img[contains(@class, 'pentaho-smallhierarchybutton')]]/td[2]/div[text()='%s']" )
  private ExtendedWebElement hierarchyItem;

  @FindBy(
      xpath = "//div[@id='dimensionTree']//tr[td/img[contains(@class, 'pentaho-smalldimensionbutton')]]/td[2]/div[text()='%s']" )
  private ExtendedWebElement levelItem;

  // Data Source Model Editor
  @FindBy( xpath = "//div[text()='{L10N:modeler.title}']" )
  protected ExtendedWebElement dlgDataSourceModelEditor;

  @FindBy( css = "#edit-icon_btn img" )
  protected ExtendedWebElement btnEditSource;

  @FindBy(
      xpath = "//table[@id='modelTabbox']//div[@class='pentaho-tabWidgetLabel' and text()='{L10N:analysisTabLabel}']" )
  protected ExtendedWebElement pageAnalysis;

  @FindBy(
      xpath = "//table[@id='modelTabbox']//div[@class='pentaho-tabWidgetLabel' and contains(text(),substring-before('{L10N:reportingTabLabel}',' '))]" )
  protected ExtendedWebElement pageReporting;

  @FindBy( id = "expand-all_img" )
  protected ExtendedWebElement btnExpandAllAnalysis;

  @FindBy( id = "collapse-all_img" )
  protected ExtendedWebElement btnCollapseAllAnalysis;

  @FindBy( id = "reporting-expand-all-btn_img" )
  protected ExtendedWebElement btnExpandAllReporting;

  @FindBy( id = "reporting-collapse-all-btn_img" )
  protected ExtendedWebElement btnCollapseAllReporting;

  // 'Yes'
  @FindBy( xpath = "//button[text()='{L10N:Yes_txt}']" )
  protected ExtendedWebElement btnYes;

  @FindBy( xpath = "//button[text()='{L10N:No_txt}']" )
  protected ExtendedWebElement btnNo;

  @FindBy( css = "#modeler_dialog_accept > span" )
  protected ExtendedWebElement btnOK;

  @FindBy( css = "#modeler_dialog_cancel > span" )
  protected ExtendedWebElement btnCancel;

  @FindBy( id = "autoPopulateButton_btn" )
  protected ExtendedWebElement autoPopulateModel;

  @FindBy( id = "clearModel_btn" )
  protected ExtendedWebElement btnClearAnalysisModel;

  @FindBy( id = "clearRelationalModel_btn" )
  protected ExtendedWebElement btnClearReportingModel;

  @FindBy( id = "remove-btn_btn" )
  protected ExtendedWebElement btnRemoveFieldAnalysis;

  @FindBy( id = "reporting-remove-btn_img" )
  protected ExtendedWebElement btnRemoveFieldReporting;

  @FindBy( id = "levelBtn_btn" )
  protected ExtendedWebElement btnAddLevel;

  @FindBy( id = "categoryBtn_btn" )
  protected ExtendedWebElement btnAddCategory;

  @FindBy( id = "fieldBtn_btn" )
  protected ExtendedWebElement btnAddField;

  // New Measure Frame
  @FindBy( xpath = "//input[@type='text' and @class='gwt-TextBox']" )
  protected ExtendedWebElement inputTextBox;

  @FindBy( xpath = "//td[@align='right']/button[contains(.,'OK')]" )
  protected ExtendedWebElement btnOkFrame;

  @FindBy( xpath = "//td[@align='left']/table/tbody/tr/td/button[@type='button' and contains(.,'Cancel')]" )
  protected ExtendedWebElement btnCancelMeasureFrame;

  @FindBy( id = "measureBtn_btn" )
  protected ExtendedWebElement btnAddMeasure;

  @FindBy( id = "dimensionBtn_btn" )
  protected ExtendedWebElement btnAddDimension;

  @FindBy( id = "hierarchyBtn_btn" )
  protected ExtendedWebElement btnAddHierarchy;

  @FindBy( id = "memberPropBtn_btn" )
  protected ExtendedWebElement btnAddMemberProp;

  // properties section
  @FindBy( id = "displayname" )
  protected ExtendedWebElement inputDisplayName;

  @FindBy( id = "level_name" )
  protected ExtendedWebElement inputLevelName;

  @FindBy( id = "memberprops_name" )
  protected ExtendedWebElement inputMemberPropsName;

  @FindBy( id = "category_name" )
  protected ExtendedWebElement inputCategoryName;

  @FindBy( id = "fielddisplayname" )
  protected ExtendedWebElement inputFieldDisplayName;

  @FindBy( id = "dimension_name" )
  protected ExtendedWebElement inputDimensionName;

  @FindBy( id = "hierarchy_name" )
  protected ExtendedWebElement inputHierarchyName;

  @FindBy( id = "down-btn_btn" )
  protected ExtendedWebElement btnDown;

  @FindBy( id = "up-btn_btn" )
  protected ExtendedWebElement btnUp;

  // auto populate dialog
  @FindBy( xpath = "//div[@class='pentaho-dialog' and //div[text()='{L10N:auto_populate_title}']]" )
  protected ExtendedWebElement dlgAutoPopulateModel;

  // clear model dialog
  @FindBy( xpath = "//div[@class='pentaho-dialog' and //div[text()='{L10N:clear_model_title}']]" )
  protected ExtendedWebElement dlgClearModel;

  // aggregations list
  @FindBy( xpath = "//table[@id='field_optionalAggregationTypes']//div[@class='gwt-Label']" )
  protected List<ExtendedWebElement> aggregationsList;

  // SourceColumn frame
  @FindBy( id = "fixLevelColumnsBtn" )
  protected ExtendedWebElement fixLevelColumnsBtn;

  @FindBy( xpath = "//div[text()='{L10N:ColResolverController.source_column_selection_dialog}']" )
  protected ExtendedWebElement SourceColumnsFrame;

  @FindBy( id = "resolveColumnsDialog_accept" )
  protected ExtendedWebElement resolveColumnsDialogAccept;

  @FindBy( id = "resolveColumnsDialog_ cancel" )
  protected ExtendedWebElement resolveColumnsDialogCancel;

  @FindBy( xpath = "//div[@id='level_source_col' and contains(text(),'%s')]" )
  protected ExtendedWebElement levelSourceColumn;

  @FindBy( xpath = "//div[@id='resolveColumnsTree']//div[text()='%s']" )
  protected ExtendedWebElement resolveSourceColumn;

  // clearModelTree
  @FindBy( xpath = " //div[@id='dimensionTree']//div[text()='%s']" )
  protected ExtendedWebElement dimensionTree;

  @FindBy( xpath = "//div[@id='categoriesTree']//div[text()='%s']" )
  protected ExtendedWebElement categoriesTree;

  // available list fields
  @FindBy( xpath = "//div[@id='fieldList']//div[text()='%s']" )
  protected ExtendedWebElement availableField;

  // invalid model dialog

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:invalid_model}']" )
  protected ExtendedWebElement dlgInvalidModel;

  @FindBy( xpath = "//button[@class='pentaho-button' and contains(text(),'OK')]" )
  protected ExtendedWebElement btnOKInvalidModelDlg;

  @FindBy( xpath = "//table[@id='reportingModelPanel']//div[text()='%s']" )
  protected ExtendedWebElement fieldCategory;

  @FindBy( id = "datasourceAdminDialog_cancel" )
  protected ExtendedWebElement btnCloseManageDS;

  @FindBy( xpath = "//span[@id='is_time_dimension']/input" )
  protected ExtendedWebElement timeDimensionCheckbox;

  @FindBy( xpath = "//div[@class='drop-popup']//tr//div[@class='gwt-Label' and text() = '%s']" )
  protected ExtendedWebElement detailsDropdownItem;

  @FindBy(
      xpath = "(//table[@id = 'time_level_elements']//table[@class='custom-list']//div[@class = 'combo-arrow']/div)[1]" )
  protected ExtendedWebElement timeLevelArrow;

  @FindBy(
      xpath = "(//table[@id = 'time_level_elements']//table[@class='custom-list']//div[@class = 'combo-arrow']/div)[2]" )
  protected ExtendedWebElement columnFormatArrow;

  @FindBy( xpath = "//*[@id='dimensionTree']//*[text()]" )
  protected List<ExtendedWebElement> analysisItems;

  private final DataSourceModelEditorPropertiesPage propertiesPage;

  public DataSourceModelEditorPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened( dlgDataSourceModelEditor ) ) {
      Assert.fail( "'Data Source Model Editor' window is not opened!" );
    }
    propertiesPage = new DataSourceModelEditorPropertiesPage( driver );
  }

  public DataSourceWizardPage editSource() {
    click( btnEditSource );
    return new DataSourceWizardPage( driver );
  }

  public void buttonOK() {
    click( btnOK, true );
    pause( 1 );
  }

  public void buttonYes() {
    click( btnYes );
  }

  public void buttonCancel() {
    click( btnCancel );
  }

  public DataSourceModelEditorPropertiesPage getPropertiesPage() {
    return propertiesPage;
  }

  public void selectItem( String path ) {
    String treeItem = getTree( path );
    switchToTab( treeItem );
    expandAll( treeItem );
    getWebElementForTreeItem( treeItem + path ).click();
  }

  public boolean isItemSelected( String path ) {
    String treeItem = getTree( path );
    switchToTab( treeItem );
    return getWebElementForTreeItem( treeItem + path ).clickIfPresent();
  }

  public ExtendedWebElement getItemByPath( String path ) {
    String treeItem = getTree( path );
    switchToTab( treeItem );
    expandAll( treeItem );
    ExtendedWebElement element = getWebElementForTreeItem( treeItem + path );
    return element;
  }

  public boolean isItemPresent( String path ) {
    String treeItem = getTree( path );
    isItemSelected( path );
    return getWebElementForTreeItem( treeItem + path ).isElementPresent( EXPLICIT_TIMEOUT / 10);
  }

  private ExtendedWebElement getAddElementForTreeItem( String path ) {
    ExtendedWebElement dimensions[] = { btnAddDimension, btnAddHierarchy, btnAddLevel, btnAddMemberProp };
    ExtendedWebElement categories[] = { btnAddCategory, btnAddField };

    String[] split = path.split( "/" );
    int count = split.length;

    if ( split.length < 2 ) {
      throw new RuntimeException( "Invalid data source modeler path: " + path );
    }

    if ( split[1].equalsIgnoreCase( "Measures" ) ) {
      return btnAddMeasure;
    }

    if ( split[1].equalsIgnoreCase( "Dimensions" ) ) {
      return dimensions[count - 2];
    }

    if ( split[1].equalsIgnoreCase( "Categories" ) ) {
      return categories[count - 2];
    }

    throw new RuntimeException( "Unable to parse data source modeler path: " + path );
  }

  private ExtendedWebElement getNameElement( String path ) {
    ExtendedWebElement dimensions[] = { inputDimensionName, inputHierarchyName, inputLevelName, inputMemberPropsName };
    ExtendedWebElement categories[] = { inputCategoryName, inputFieldDisplayName };

    String[] split = path.split( "/" );
    int count = split.length;

    if ( split.length < 2 ) {
      throw new RuntimeException( "Invalid data source modeler path: " + path );
    }

    if ( split[1].equalsIgnoreCase( "Measures" ) ) {
      return inputDisplayName;
    }

    if ( split[1].equalsIgnoreCase( "Dimensions" ) ) {
      return dimensions[count - 3];
    }

    if ( split[1].equalsIgnoreCase( "Categories" ) ) {
      return categories[count - 3];
    }

    throw new RuntimeException( "Unable to parse data source modeler path: " + path );
  }

  private ExtendedWebElement getWebElementForTreeItem( String path ) {
    String[] pp = path.split( "/" );
    List<Object> v = new ArrayList<Object>();
    for ( String p : pp ) {
      if ( !p.isEmpty() ) {
        v.add( p );
      }
    }
    for ( String p : pp ) {
      if ( !p.isEmpty() ) {
        v.add( p );
      }
    }
    Object[] va = v.toArray( new Object[0] );
    switch ( va.length ) {
      case 4:
        return format( dimensionItemLevel1, va );
      case 6:
        return format( dimensionItemLevel2, va );
      case 8:
        return format( dimensionItemLevel3, va );
      case 10:
        return format( dimensionItemLevel4, va );
      case 12:
        return format( dimensionItemLevel5, va );
      default:
        LOGGER.error( "Path '" + path + "' has unknown count of items" );
        throw new RuntimeException( "Path '" + path + "' has unknown count of items" );
    }
  }

  private String getTree( String path ) {
    String[] split = path.split( "/" );

    if ( split.length < 2 ) {
      throw new RuntimeException( "Invalid data source modeler path: " + path );
    }

    String tree = DIMENSION_TREE;

    if ( split[1].equalsIgnoreCase( "Categories" ) ) {
      tree = CATEGORY_TREE;
    }
    return tree;
  }

  public void autoPopulateModel() {
    click( autoPopulateModel );
    buttonYes();
  }

  public void autoPopulateClearedModel() {
    click( autoPopulateModel );
  }

  public void switchToTab( String tree ) {
    if ( tree.equals( DIMENSION_TREE ) ) {
      click( pageAnalysis );
    } else if ( tree.equals( CATEGORY_TREE ) ) {
      switchToReportingTab();
    } else {
      throw new RuntimeException( "Unrecognized tree is provided! " + tree );
    }
  }

  public String getPathWebElements( String path ) {
    String result = "//div[@id='dimensionTree']//div[@class='gwt-TreeItem']";
    String tree = getTree( path );
    if ( tree.equals( DIMENSION_TREE ) ) {
      return result;
    } else if ( tree.equals( CATEGORY_TREE ) ) {
      return result = "//div[@id='categoriesTree']//div[@class='gwt-TreeItem']";
    } else {
      throw new RuntimeException( "Unrecognized tree is provided! " + tree );
    }
  }

  public void switchToAnalysisTab() {
    click( pageAnalysis );
  }

  public void switchToReportingTab() {
    pageReporting.click();
  }

  public void expandAll( String tree ) {
    if ( tree.equals( DIMENSION_TREE ) ) {
      click( btnExpandAllAnalysis );
    } else if ( tree.equals( CATEGORY_TREE ) ) {
      click( btnExpandAllReporting );
    } else {
      throw new RuntimeException( "Unrecognized tree is provided! " + tree );
    }
  }

  public void expandAllAnalysis() {
    click( btnExpandAllAnalysis );
  }

  public void collapseAllAnalysis() {
    click( btnCollapseAllAnalysis );
  }

  public void expandAllReporting() {
    click( btnExpandAllReporting );
  }

  public void collapseAllReporting() {
    click( btnCollapseAllReporting );
  }

  public void clearAnalysisModel() {
    click( btnClearAnalysisModel );
    buttonYes();
  }

  public void clearReportingModel() {
    click( btnClearReportingModel );
    buttonYes();
  }

  public void confirmClearModel() {
    click( btnYes );
  }

  public void cancelClearModel() {
    click( btnNo );
  }

  public void openDlgAnalysisClearModel() {
    click( btnClearAnalysisModel );
  }

  public void openDlgReportingClearModel() {
    click( btnClearReportingModel );
  }

  public boolean isDlgClearModelOpened() {
    boolean res = true;
    if ( !isElementPresent( dlgClearModel, EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Dialog 'Clear Model?' is not present!" );
      res = false;
    }
    return res;
  }

  /**
   * Adds the specified field to the specified destination path.
   * 
   * @param fieldName
   *          The field to be added to the model.
   * @param destinationPath
   *          The destination to add the field to.
   */
  public void addAvailableField( String fieldName, String destinationPath ) {
    Utils.dnd( format( availableField, fieldName ), getItemByPath( destinationPath ) );
  }

  public void addItem( String itemParentPath, String itemName ) {
    selectItem( itemParentPath );
    getAddElementForTreeItem( itemParentPath ).click();
    type( inputTextBox, itemName );
    click( btnOkFrame );
  }

  public void deleteAnalysisItem( String measure ) {
    selectItem( measure );
    click( btnRemoveFieldAnalysis );
  }

  public void renameItem( String path, String name ) {
    selectItem( path );
    ExtendedWebElement elementName = getNameElement( path );
    elementName.type( name );
  }

  public String getFieldId( String path ) {
    String fieldID = null;
    selectItem( path );
    String lastToken = path.split( "/" )[path.split( "/" ).length - 1];
    String pathElement = getPathWebElements( path );
    List<WebElement> list = driver.findElements( By.xpath( pathElement ) );
    for ( WebElement webElement : list ) {
      if ( webElement.getText().equals( lastToken ) ) {
        webElement.getAttribute( "id" );
        fieldID = webElement.getAttribute( "id" );
      }
    }
    return fieldID;
  }

  public boolean isElementMoved( String fieldID, String fieldID2 ) {
    boolean res = true;
    if ( fieldID.equalsIgnoreCase( fieldID2 ) ) {
      LOGGER.warn( "Please verify that you have selected not the first and not the last item of the list!" );
      LOGGER.error( "Please validate field with the following ID: " + fieldID + " " + fieldID2 );
      res = false;
    }
    LOGGER.info( "The field moved successfully!" );
    return res;
  }

  public void openDlgAutoPopulateModel() {
    click( autoPopulateModel );
  }

  public boolean isDlgAutoPopulateModelOpened() {
    if ( !isElementPresent( dlgAutoPopulateModel, EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Dialog 'Auto Populate this Model?' is not opened!" );
      return false;
    }
    return true;
  }

  public void cancelAutoPopulate() {
    click( btnNo );
  }

  public void confirmAutoPopulate() {
    click( btnYes );
  }

  public void moveUpField() {
    click( btnUp );
  }

  public void moveDownField() {
    click( btnDown );
  }

  public void fixLevelBtn() {
    click( fixLevelColumnsBtn );
  }

  public List<String> getAggregationTypes() {
    List<String> typesList = new ArrayList<String>();
    for ( ExtendedWebElement element : aggregationsList ) {
      typesList.add( element.getText() );
    }
    return typesList;
  }

  public boolean isSourceColumnFrameIsOpened() {
    if ( !isElementPresent( SourceColumnsFrame, EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Dialog 'Select Source Column' is not opened!" );
      return false;
    }
    return true;
  }

  public void selectSourceColumn( String column ) {
    click( format( resolveSourceColumn, column ) );
  }

  public void resolveColumnsAccept() {
    click( resolveColumnsDialogAccept );
  }

  public boolean isLevelSourceColumnAdded( String column ) {
    if ( !isElementPresent( format( levelSourceColumn, column ), EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Level Source Column is not added on the 'Data Source MOdel Editor Page'." );
      return false;
    }
    return true;
  }

  public ExtendedWebElement getAvailableField( String field ) {
    ExtendedWebElement element = ( format( availableField, field ) );
    return element;
  }

  public boolean isDlgInvalidModel() {
    boolean res = true;
    if ( !isElementPresent( dlgInvalidModel, EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Dialog 'Invalid Model' is not present!" );
      res = false;
    }
    return res;
  }

  public ExtendedWebElement getClearTree( String tree ) {
    ExtendedWebElement element = null;

    if ( MEASURE.contains( tree ) || DIMENSION.contains( tree ) ) {
      element = format( dimensionTree, tree );
    } else if ( CATEGORY.contains( tree ) ) {
      element = format( categoriesTree, tree );
    }
    return element;
  }

  public void clickBtnCloseManageDS() {
    click( btnCloseManageDS );
  }

  public void deleteFieldCategory( String field ) {
    if ( isElementPresent( format( fieldCategory, field ), EXPLICIT_TIMEOUT / 10 ) ) {
      click( btnRemoveFieldReporting );
    }
  }

  public void clickBtnOKInvalidModelDlg() {
    click( btnOKInvalidModelDlg );
  }

  public boolean isDataSourceModelEditorOpened() {
    if ( !isElementPresent( dlgDataSourceModelEditor, EXPLICIT_TIMEOUT / 10 ) ) {
      LOGGER.error( "Data Source Model Editor is not opened!" );
      return false;
    }
    return true;
  }

  public void checkTimeDimension() {
    timeDimensionCheckbox.check();
  }

  public List<ExtendedWebElement> getAnalysisItems() {
    return analysisItems;
  }

  public void selectTimeLevelType( String type ) {
    timeLevelArrow.click();
    format( detailsDropdownItem, type ).click();
  }

  public void selectColumnFormat( String format ) {
    columnFormatArrow.click();
    format( detailsDropdownItem, format ).click();
  }

  public void verifyPopulatedAnalysisSection( List<ExtendedWebElement> analysisItems ) {
    if ( analysisItems.size() <= 2 ) {
      Assert.fail( "The model is not populated!" );
    }
  }
}
