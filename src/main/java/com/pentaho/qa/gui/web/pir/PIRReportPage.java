package com.pentaho.qa.gui.web.pir;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.services.CustomAssert;
import com.pentaho.services.Report.Colour;
import com.pentaho.services.Report.Sort;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.utils.ExportType;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRReportPage extends PIRBasePage {

  public static final String PAGES_SEPARATOR_ON_HEADER = " / ";
  public static final String FILTERS_NUMBER_INDICATOR = "filterIndicatorBackground_";
  public static final String SELECTION_INDICATOR = "background-image: url";
  private static final String BACKGROUND_COLOR = "background-color";
  private static final String TEXT_COLOR = "color";

  // 'Get Started'
  @FindBy( xpath = "//button[text()='{L10N:splash_button}']" )
  protected ExtendedWebElement btnGetStarted;

  @FindBy( id = "hintsCheckBox" )
  protected ExtendedWebElement checkShowTipsOnGeneralTab;

  @FindBy( id = "startupHintsCheckBox" )
  protected ExtendedWebElement checkStartupHintsOnGeneralTab;

  @FindBy( id = "startupHintsCheckBox2" )
  protected ExtendedWebElement checkStartupHints;

  @FindBy( id = "hintspanel" )
  protected ExtendedWebElement hintsPanel;

  @FindBy( id = "hidehintsbtn" )
  protected ExtendedWebElement btnHideHints;

  @FindBy( xpath = "//span[@id='datasourcelbl' and text()='{L10N:datasourcelbl_content}']" )
  protected ExtendedWebElement labelAvailableFields;

  // Available Fields
  @FindBy( xpath = "//div[@id='fieldlist']//span[starts-with(@id, 'category') and text()='%s']" )
  protected ExtendedWebElement categoryItem;

  @FindBy( xpath = "//div[@id='fieldlist']/div[starts-with(@id, 'field') and text()='%s']" )
  protected ExtendedWebElement availableFieldItem;

  @FindBy( id = "fieldAddColumn_text" )
  protected ExtendedWebElement menuAddToColumn;

  @FindBy( id = "fieldAddGroup_text" )
  protected ExtendedWebElement menuAddToGroup;

  @FindBy( id = "fieldParameter_text" )
  protected ExtendedWebElement menuAddToPrompts;

  @FindBy( id = "fieldFilter_text" )
  protected ExtendedWebElement contextMenuFilter;

  @FindBy( css = "[tabid=datatab]" )
  protected ExtendedWebElement tabData;

  @FindBy( xpath = "//div[contains(@class, 'pentaho-tabWidgetLabel')]" )
  protected ExtendedWebElement pirReportPageTab;

  // Formatting
  @FindBy( css = "[tabid=formattab]" )
  protected ExtendedWebElement tabFormatting;

  @FindBy( id = "foreColorSample" )
  protected ExtendedWebElement textColor;

  @FindBy( xpath = "//*[@id='toolbar1.foreColorBtn_arrow']/div[contains(@class,'dijitArrowButtonInner')]" )
  protected ExtendedWebElement btnTextColorDownArrow;

  @FindBy( id = "foreColorPicker" )
  protected ExtendedWebElement textColorPicker;

  @FindBy( id = "backColorSample" )
  protected ExtendedWebElement backgroundColor;

  @FindBy( xpath = "//*[@id='toolbar1.backColorBtn_arrow']/div[contains(@class,'dijitArrowButtonInner')]" )
  protected ExtendedWebElement btnBackgroundColorDownArrow;

  @FindBy( id = "backColorPicker" )
  protected ExtendedWebElement backgroundColorPicker;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[@class='label' and text()='{L10N:currentColor_content}']" )
  protected ExtendedWebElement lblCurrent;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[@class='colorboxOuter']/div[@class='colorbox']" )
  protected ExtendedWebElement currentColor;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[@tabid='palettetab' and text()='{L10N:Palette_txt}']" )
  protected ExtendedWebElement tabPalette;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[@tabid='customtab' and text()='{L10N:Custom_txt}']" )
  protected ExtendedWebElement tabCustom;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[@class='dojoxColorPickerBox']/img[@class='dojoxColorPickerPoint']" )
  protected ExtendedWebElement saturationSelector;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[@class='dojoxColorPickerBox']/img[@class='dojoxColorPickerUnderlay']" )
  protected ExtendedWebElement saturationPicker;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[@class='dojoxHuePicker']/img[@class='dojoxHuePickerPoint']" )
  protected ExtendedWebElement hueHandle;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[@class='dojoxHuePickerUnderlay']/img" )
  protected ExtendedWebElement huePicker;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//div[contains(@class,'dojoxColorPickerRgb')]//input" )
  protected List<ExtendedWebElement> customRgbValues;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//label[contains(text(), 'hex')]/following-sibling::input[contains(@id, 'pentaho_common_CustomColorPicker_')]" )
  protected ExtendedWebElement customHexValue;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[contains(@id,'dijit_ColorPalette_')]" )
  protected ExtendedWebElement paletteGrid;

  @FindBy(
      xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[@class='dijitColorPaletteSwatch' and contains(@style,'background-color: %s')]" )
  protected ExtendedWebElement colorIcon;

  private static final String IN_USE = L10N.getText( "inUseColors_content" );
  @FindBy( xpath = "//div[contains(@id,'ColorBtn_dropdown') and not(contains(@style,'display: none'))]//*[text()=%s]" )
  protected ExtendedWebElement lblInUse;

  @FindBy(
      xpath = "//div[not(contains(@style,'display: none')) and contains(@id,'ColorBtn_dropdown')]//*[@class='usedColorTable']//div[contains(@id,'usedcolor')]" )
  protected List<ExtendedWebElement> colorsInUse;

  @FindBy(
      xpath = "//div[not(contains(@style,'display: none')) and contains(@id,'ColorBtn_dropdown')]//*[@class='usedColorTable']//div[contains(@id,'usedcolor') and @color='%s']" )
  protected ExtendedWebElement colorInUse;

  @FindBy( css = "[tabid=generaltab]" )
  protected ExtendedWebElement tabGeneral;

  // Sorting
  @FindBy( xpath = "//table[@id='groupsorttable' and //td[text()='%s']]//select" )
  protected ExtendedWebElement groupSortingItem;

  @FindBy( xpath = "//table[@id='fieldsorttable']//tr[td[text()='%s']]//select" )
  protected ExtendedWebElement fieldSortingItem;

  @FindBy( xpath = "//table[@id='groupsorttable']//tr[td[text()='%s']]/td[@class='sortTableCell']/div" )
  protected ExtendedWebElement groupSortingDeleteItem;

  @FindBy( xpath = "//table[@id='fieldsorttable']//tr[td[text()='%s']]/td[@class='sortTableCell']/div" )
  protected ExtendedWebElement fieldSortingDeleteItem;

  // Tool bar
  @FindBy( id = "toolbar1.undo" )
  protected ExtendedWebElement btnUndo;

  @FindBy( id = "toolbar1.redo" )
  protected ExtendedWebElement btnRedo;

  @FindBy( id = "toolbar1.filterToggle" )
  protected ExtendedWebElement btnShowFilters;

  @FindBy( id = "filterToolbarIndicator" )
  protected ExtendedWebElement btnFilterIndicator;

  @FindBy( id = "toolbar1.layoutToggle" )
  protected ExtendedWebElement btnShowLayout;

  @FindBy( id = "toolbar1.parameterToggle" )
  protected ExtendedWebElement btnShowPrompts;

  @FindBy( id = "toolbar1.selectAll" )
  protected ExtendedWebElement btnSelectAll;

  @FindBy( id = "pageNumberControl" )
  protected ExtendedWebElement pageNumberControl;

  // Panels
  @FindBy( id = "layoutPanelColumnHint" )
  protected ExtendedWebElement fieldLayoutColumns;

  @FindBy( id = "filterPanel" )
  protected ExtendedWebElement filterPanel;

  @FindBy( id = "layoutPanel" )
  protected ExtendedWebElement layoutPanel;

  @FindBy( id = "parameterPanel" )
  protected ExtendedWebElement promptsPanel;

  @FindBy( id = "layoutPanelGroupDropArea" )
  protected ExtendedWebElement layoutPanelGroupDropArea;

  @FindBy( id = "layoutPanelColumnDropArea" )
  protected ExtendedWebElement layoutPanelColumnDropArea;

  @FindBy( xpath = "//div[@id='layoutPanelGroupList']//div[@fieldid='%s']" )
  protected ExtendedWebElement panelGroupItem;

  @FindBy( xpath = "//div[@id='layoutPanelColumnList']//div[@fieldid='%s']" )
  protected ExtendedWebElement panelColumnItem;

  // Workspace
  @FindBy( xpath = "//td[starts-with(@id,'rpt-column-header') and text()='%s']" )
  protected ExtendedWebElement columnItem;

  @FindBy( xpath = "//div[starts-with(@id,'column-block') and @fieldid='%s']" )
  protected ExtendedWebElement columnBlockItem;

  @FindBy( xpath = "//td[starts-with(@id,'rpt-group-header') and @fieldid='%s']" )
  protected ExtendedWebElement groupItem;

  @FindBy( css = "#reportContent > table > tbody > tr > td" )
  protected List<ExtendedWebElement> recordItems;

  @FindBy( xpath = "//td[starts-with(@id,'rpt-column-header') and @fieldidx]" )
  protected ExtendedWebElement columnHeader;

  @FindBy( xpath = "//td[starts-with(@id,'rpt-column-header') and @fieldidx]" )
  protected List<ExtendedWebElement> columnHeaders;

  @FindBy( xpath = "//td[starts-with(@id,'rpt-group-header') and @groupidx]" )
  protected ExtendedWebElement groupHeader;

  @FindBy( xpath = "//tr[@id='column-dnd-target']/td" )
  protected ExtendedWebElement columnDndTarget;

  @FindBy( css = ".dropIndicatorVertical" )
  protected ExtendedWebElement dropIndicatorVertical;

  @FindBy( css = ".dropIndicatorHorizontal " )
  protected ExtendedWebElement dropIndicatorHorizontal;

  @FindBy( xpath = "//div[@id='reportContent']//div[@class='hidden' and div[contains(@class,'dropIndicatorVertical')]]" )
  protected List<ExtendedWebElement> dropZonesColumns;

  @FindBy(
      xpath = "//div[@id='reportContent']//div[@class='hidden' and div[contains(@class,'dropIndicatorHorizontal')]]" )
  protected List<ExtendedWebElement> dropZonesGroups;

  @FindBy( id = "rpt-report-header-label-0" )
  protected ExtendedWebElement txtReportTitle;

  @FindBy( id = "rpt-page-header-1" )
  protected ExtendedWebElement txtReportHeader;

  @FindBy( id = "labelseditbox" )
  protected ExtendedWebElement inputEditPopup;

  @FindBy( id = "labeleditcloseBtn" )
  protected ExtendedWebElement btnCloseEditPopup;

  @FindBy( id = "labeloptionsdate" )
  protected ExtendedWebElement btnDatePopup;

  @FindBy( id = "labeloptionsdatepage" )
  protected ExtendedWebElement btnPagesPopup;

  @FindBy( css = "div[style*=\"background-image: url\"]" )
  protected ExtendedWebElement selectionItem;

  @FindBy( id = "rpt-menu-image-button" )
  protected ExtendedWebElement btnMenuArrow;

  @FindBy( xpath = "//td[contains(@class, 'rpt-column-value') and text()='%s']" )
  protected ExtendedWebElement columnValueItem;

  @FindBy( id = "trash-dnd-target" )
  protected ExtendedWebElement trashCan;

  @FindBy( xpath = "//span[@id = 'datasourcespan' and text()='%s']" )
  protected ExtendedWebElement dataSourceName;

  // Submenu

  @FindBy( id = "sortsubmenu" )
  protected ExtendedWebElement sortSubMenu;

  @FindBy( id = "rpt-menu-sort-desc" )
  protected ExtendedWebElement sortSubMenuDesc;

  @FindBy( id = "rpt-menu-sort-asc" )
  protected ExtendedWebElement sortSubMenuAsc;

  @FindBy( id = "rpt-menu-sort-none" )
  protected ExtendedWebElement sortSubMenuNone;

  @FindBy( id = "removesubmenu_text" )
  protected ExtendedWebElement subMenuRemove;

  @FindBy( id = "filterContextMenu" )
  protected ExtendedWebElement filterSubMenu;

  @FindBy( id = "parameterContextMenu" )
  protected ExtendedWebElement promptSubMenu;

  // Columns/Groups layout
  @FindBy( id = "layoutPanelGroupHint" )
  protected ExtendedWebElement layoutPanelGroup;

  @FindBy( id = "layoutPanelColumnDropArea" )
  protected ExtendedWebElement layoutPanelColumn;

  // Error dialogs and buttons
  @FindBy( id = "dijit_Dialog_1" )
  protected ExtendedWebElement errorDialog;

  // OK
  @FindBy( xpath = "//button[contains(@class,'pentaho-button') and contains(text(),'{L10N:btnLabelOK}')]" )
  protected ExtendedWebElement btnOKErrorDialog;

  // Prompts
  @FindBy( xpath = "//*[@class='edit-buttons-container']//div[text()='%s']" )
  protected ExtendedWebElement promptName;

  @FindBy(
      xpath = "//div[@widgetid='toolbar1']/span[@widgetid='toolbar1.exportReportButton']//span[contains(@class, 'pentaho_dijitEditorIconExport')]" )
  protected ExtendedWebElement exportButton;

  @FindBy( id = "toolbar1.exportPDF_text" )
  protected ExtendedWebElement exportToPDFMenu;

  @FindBy( id = "toolbar1.exportCSV_text" )
  protected ExtendedWebElement exportToCSVMenu;

  @FindBy( id = "toolbar1.exportExcel2007_text" )
  protected ExtendedWebElement exportToExcelMenu;

  @FindBy( id = "toolbar1.exportExcel_text" )
  protected ExtendedWebElement exportToExcel97Menu;

  @FindBy( xpath = "//*[@class='auto-complete-checkbox']/input" )
  protected ExtendedWebElement autoSubmitCheckbox;

  // Tab context menu
  @FindBy( id = "openTabInNewWindow" )
  protected ExtendedWebElement openTabInNewWindow;

  // Dialog pop-up
  @FindBy( xpath = "//*[@class='pentaho-dialog']" )
  protected ExtendedWebElement dialogPopUp;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement okButton;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement cancelButton;

  // Hints
  @FindBy( xpath = "//span[contains(.,'{L10N:inlineEditFlag}')]" )
  public ExtendedWebElement editInlineHint;

  @FindBy( xpath = "//span[contains(.,'{L10N:swapDropFlag}')]" )
  public ExtendedWebElement swapDropColumnsHint;

  @FindBy( xpath = "//span[contains(.,'{L10N:dropGroupsFlag}')]" )
  public ExtendedWebElement dropGroupsHint;

  @FindBy( xpath = "//span[contains(.,'{L10N:fieldListFlag}')]" )
  public ExtendedWebElement dragFieldsHint;

  public PIRReportPage( WebDriver driver ) {
    this( driver, NEW_IR_REPORT_NAME );
  }

  public PIRReportPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  public void disableHints() {
    if ( isHintsPanelPresent() ) {
      uncheckStartupHints();
      hideHints();
    }
  }

  protected void uncheckStartupHints() {
    click( checkStartupHints );
  }

  protected boolean isHintsPanelPresent() {
    return isElementPresent( hintsPanel, EXPLICIT_TIMEOUT / 10 );
  }

  protected void hideHints() {
    click( btnHideHints );
  }

  public void enableHints() {
    if ( !isHintsPanelPresent() ) {
      checkStartupHints();
      showHints();
    }
  }

  protected void checkStartupHints() {
    click( checkStartupHintsOnGeneralTab );
  }

  protected void showHints() {
    click( checkShowTipsOnGeneralTab );
  }

  public void clickEditInlineHint() {
    click( editInlineHint );
  }

  public void clickSwapDropColumnsHint() {
    click( swapDropColumnsHint );
  }

  public void clickDropGroupsHint() {
    click( dropGroupsHint );
  }

  public void clickDragFieldsHint() {
    click( dragFieldsHint );
  }

  public void closeGetStarted() {
    if ( isGetStartedPresent() ) {
      click( btnGetStarted );
    }
  }

  public boolean isGetStartedPresent() {
    return isElementPresent( btnGetStarted, EXPLICIT_TIMEOUT / 10 );
  }

  public boolean isCategoryExist( String categoryName ) {
    return isElementPresent( format( categoryItem, categoryName ) );
  }

  public boolean isWorkspaceEmpty() {
    return !isElementPresent( columnHeader, EXPLICIT_TIMEOUT / 10 )
        && !isElementPresent( groupHeader, EXPLICIT_TIMEOUT / 10 );
  }

  public void addColumn( String field, Workflow workflow ) {

    switch ( workflow ) {

      case CONTEXT_PANEL:
        Actions clicker = new Actions( driver );
        clicker.contextClick( format( availableFieldItem, field ).getElement() ).perform();

        click( menuAddToColumn, true );
        break;

      case DND_PANEL:
        // show layout panel, drag and drop element to column section, and hide layout panel
        showLayoutPanel();
        Utils.dnd( format( availableFieldItem, field ), layoutPanelColumnDropArea );
        hideLayoutPanel();
        break;

      case DND_REPORT:
        // define drop zone for columns, and drag and drop field into report
        final ExtendedWebElement dropElement = format( dropZonesColumns.get( 0 ) );
        Utils.dnd( format( availableFieldItem, field ), dropElement );

        break;
      default:
        Assert.fail( "Unsupported workflow type is selected: " + workflow );
        break;
    }

    if ( !isColumnOnWorkspace( field ) ) {
      LOGGER.error( "Field '" + field + "' was not added to the Workspace!" );
    }
    pause( 1 );
  }

  public void removeColumn( String field, Workflow workflow ) {

    switch ( workflow ) {

      // Right click on column header
      case CONTEXT_REPORT:
        rightClickColumn( field );
        click( subMenuRemove, true );
        break;

      // DnD from Layout panel to can
      case DND_PANEL:
        Assert.fail( "Need to implement" );
        break;

      // DnD column header to can
      case DND_REPORT:
        Assert.fail( "Need to implement" );
        break;

      default:
        Assert.fail( "Unsupported workflow type is selected: " + workflow );
        break;
    }

    if ( isColumnOnWorkspace( field ) ) {
      LOGGER.error( "Column '" + field + "' was not removed from the Workspace!" );
    }

  }

  public void addGroup( String field, Workflow workflow ) {

    switch ( workflow ) {
      case CONTEXT_PANEL:
        // right click field from Available fields and select Add to Groups
        Actions clicker = new Actions( driver );
        clicker.contextClick( format( availableFieldItem, field ).getElement() ).perform();

        click( menuAddToGroup, true );
        break;
      case DND_PANEL:
        // show layout panel, drag and drop element to group section, and hide layout panel
        showLayoutPanel();

        Utils.dnd( format( availableFieldItem, field ), layoutPanelGroupDropArea );
        hideLayoutPanel();
        break;
      case DND_REPORT:
        // define drop zone for columns, and drag and drop field into report
        final ExtendedWebElement dropElement = format( dropZonesGroups.get( 0 ) );

        Utils.dnd( format( availableFieldItem, field ), dropElement );
        break;
      default:
        Assert.fail( "Unsupported workflow type is selected: " + workflow );
        break;
    }

    if ( !isGroupOnWorkspace( field ) ) {
      LOGGER.error( "Field '" + field + "' was not added to the Workspace!" );
    }
  }

  public void removeGroup( String field, Workflow workflow ) {
    switch ( workflow ) {

    // DnD from Layout panel to can
      case DND_PANEL:
        // show layout panel, remove group and hide layout panel
        showLayoutPanel();
        Utils.dnd( format( panelGroupItem, getFieldId( field ) ), trashCan );
        hideLayoutPanel();
        break;

      // DnD column header to can
      case DND_REPORT:
        // click on group header and drag it to the trash can

        Utils.dnd( format( groupHeader, getFieldId( field ) ), trashCan );
        break;

      default:
        Assert.fail( "Unsupported workflow type is selected: " + workflow );
        break;
    }

    if ( isGroupOnWorkspace( field ) ) {
      LOGGER.error( "Column '" + field + "' was not removed from the Workspace!" );
    }

  }

  // TODO: combine addColumns/addGroups and addPrompts into the single page object method as it has absolutely the same
  // functionality!
  public void addPrompt( String field, Workflow workflow ) {

    switch ( workflow ) {
      case CONTEXT_REPORT:
        rightClickColumn( field );
        click( promptSubMenu, true );
        break;
      case CONTEXT_PANEL:
        Actions clicker = new Actions( driver );
        clicker.contextClick( format( availableFieldItem, field ).getElement() ).perform();

        click( menuAddToPrompts, true );
        break;
      case DND_PANEL:
        Assert.fail( "Need to implement" );
        break;
      case DND_REPORT:
        Assert.fail( "Need to implement" );
        break;
      default:
        Assert.fail( "Unsupported workflow type is selected: " + workflow );
        break;
    }

    if ( !isElementPresent( format( promptName, field ) ) ) {
      LOGGER.error( "Prompt '" + field + "' was not added to the Workspace!" );
    }
  }

  public boolean isColumnOnWorkspace( String columnName ) {
    return format( EXPLICIT_TIMEOUT, columnItem, columnName ).isElementPresent( EXPLICIT_TIMEOUT );
  }

  public PIRFilterPage addFilter( String fieldName, Workflow workflow ) {
    switch ( workflow ) {
      case CONTEXT_REPORT:
        rightClickColumn( fieldName );
        click( filterSubMenu, true );
        break;
      case CONTEXT_PANEL:
        Actions clicker_context = new Actions( driver );
        clicker_context.contextClick( format( availableFieldItem, fieldName ).getElement() ).perform();
        click( contextMenuFilter, true );
        break;
      case CONTEXT_REPORT_ARROW:
        hover( format( columnItem, fieldName ) );
        if ( !isElementPresent( btnMenuArrow ) ) {
          LOGGER.error( "Menu arrow button didn't appear after hovering!" );
        }
        btnMenuArrow.click();
        click( filterSubMenu, true );
        break;
      case DND_PANEL:
        // show filter panel and drag field into filter panel
        showFilterPanel();

        Utils.dnd( format( availableFieldItem, fieldName ), filterPanel );
        break;
      default:
        Assert.fail( "need to implement" );
        break;
    }

    return new PIRFilterPage( driver, fieldName );
  }

  public PIRFilterPanelPage showFilterPanel() {
    if ( !isFilterPanelShown() ) {
      // Workaround for Chrome broswer, direct click to btnShowFilters doesn't work
      clickAny( btnFilterIndicator, btnShowFilters );
      // check that the panel is now shown
      if ( !isFilterPanelShown() ) {
        CustomAssert.fail( "31460", "'Filters' panel is not present in the report!" );
      }
    }
    return new PIRFilterPanelPage( driver );
  }

  public void hideFilterPanel() {
    if ( isFilterPanelShown() ) {
      // Workaround for Chrome broswer, direct click to btnShowFilters doesn't work
      clickAny( btnFilterIndicator, btnShowFilters );
    }
  }

  public void refreshFilterPanel() {
    // Workaround for Furefox broswer, it makes visible filters panel
    hideFilterPanel();
    showFilterPanel();
  }

  public boolean isFilterPanelShown() {
    // return isElementPresent( filterPanel, IMPLICIT_TIMEOUT / 5 );
    return !filterPanel.getAttribute( "class" ).contains( "hidden" );
  }

  public PIRFilterPanelPage showLayoutPanel() {
    if ( !isLayoutPanelShown() ) {
      click( btnShowLayout );
    }
    return new PIRFilterPanelPage( driver );
  }

  public void hideLayoutPanel() {
    if ( isLayoutPanelShown() ) {
      click( btnShowLayout );
    }
  }

  public boolean isLayoutPanelShown() {
    return isElementPresent( layoutPanel, IMPLICIT_TIMEOUT / 10 );
  }

  public boolean verifyLayoutPanel() {
    Long delay = IMPLICIT_TIMEOUT / 10;
    return isElementPresent( layoutPanelGroupDropArea, delay ) && isElementPresent( layoutPanelColumnDropArea, delay );
  }

  public void reorderGroupFields( String fromField, String toField ) {

    Utils.dnd( format( panelGroupItem, getFieldId( fromField ) ), format( panelGroupItem, getFieldId( toField ) ) );

  }

  public void reorderColumnFields( String fromField, String toField ) {

    Utils.dnd( format( panelColumnItem, getFieldId( fromField ) ), format( panelColumnItem, getFieldId( toField ) ) );

  }

  public boolean moveToGroupsInLayoutPanel( String column ) {

    Utils.dnd( format( panelColumnItem, getFieldId( column ) ), layoutPanelGroupDropArea );

    pause( 1 );

    return isGroupPresent( column );
  }

  public boolean moveToColumnsInLayoutPanel( String group ) {

    Utils.dnd( format( panelGroupItem, getFieldId( group ) ), layoutPanelColumnDropArea );

    pause( 1 );

    return isColumnPresent( group );
  }

  public boolean verifyReorderedGroups( String toField ) {
    ExtendedWebElement element = findExtendedWebElement( By.xpath( "id('rpt-group-header-label-0')" ) );
    String header = element.getElement().getText();
    return header.contains( toField );
  }

  public boolean verifyReorderedColumns( String toField ) {
    ExtendedWebElement element = findExtendedWebElement( By.xpath( "id('rpt-column-header-0')" ) );
    String header = element.getElement().getText();
    return header.contains( toField );
  }

  public boolean isGroupPresent( String group ) {
    return findExtendedWebElement( By.xpath( "id('layoutPanelGroupList')//*[@title='" + group + "']" ) )
        .isElementPresent( IMPLICIT_TIMEOUT / 10 );
  }

  public boolean isColumnPresent( String column ) {
    return findExtendedWebElement( By.xpath( "id('layoutPanelColumnList')//*[@title='" + column + "']" ) )
        .isElementPresent( IMPLICIT_TIMEOUT / 10 );
  }

  public void showPromptsPanel() {
    if ( !isPromptsPanelShown() ) {
      click( btnShowPrompts );
      // check that the panel is now shown
      if ( !isPromptsPanelShown() ) {
        CustomAssert.fail( "31456", "'Prompts' panel is not present in the report!" );
      }
    }
  }

  public void hidePromptsPanel() {
    if ( isPromptsPanelShown() ) {
      click( btnShowPrompts );
      // check that the panel is now hidden
      if ( isPromptsPanelShown() ) {
        CustomAssert.fail( "31457", "'Prompts' panel is present in the report!" );
      }
    }
  }

  public void refreshPromptsPanel() {
    hidePromptsPanel();
    showPromptsPanel();
  }

  public boolean isPromptsPanelShown() {
    return isElementPresent( promptsPanel, IMPLICIT_TIMEOUT / 10 );
  }

  public void setTitle( String newTitle ) {

    // Double click on element
    Actions clicker = new Actions( driver );
    clicker.doubleClick( txtReportTitle.getElement() ).perform();

    // Check that popup appears
    if ( isElementPresent( inputEditPopup ) ) {
      type( inputEditPopup, newTitle );
    } else {
      LOGGER.error( "Dialog for header modification didn't appear!" );
      return;
    }

    // Close popup for applying changes
    closeEditReportHeader();
  }

  public String getTitle() {
    if ( !txtReportTitle.isElementPresent( IMPLICIT_TIMEOUT ) )
      Assert.fail( "Element is not present: " + txtReportTitle.getNameWithLocator() );
    return txtReportTitle.getText();
  }

  public boolean isTitleWithTextPresent( String text ) {
    return isTitleWithTextPresent( text, IMPLICIT_TIMEOUT );
  }

  public boolean isTitleWithTextPresent( String text, long count ) {
    return txtReportTitle.isElementWithTextPresent( text, count );
  }

  public String getTitleWithNotEmptyText() {
    waitForElementToBeClickable( txtReportTitle, (int) IMPLICIT_TIMEOUT );
    Assert.assertTrue( txtReportTitle.isElementPresent( IMPLICIT_TIMEOUT ), " Element : '"
        + txtReportTitle.getNameWithLocator() + "' should be present!" );
    int count = 5;
    while ( count-- > 0 && txtReportTitle.getText().isEmpty() ) {
      LOGGER.info( String.format( "Trying get non empty text: %s  Title text: %s", count, txtReportTitle.getText() ) );
      pause( 1 );
    }
    return txtReportTitle.getText();
  }

  public boolean openEditReportHeader() {
    // Double click on element
    Actions clicker = new Actions( driver );
    clicker.doubleClick( txtReportHeader.getElement() ).perform();
    int count = 0;
    while ( count++ > 5 ) {
      if ( !isElementPresent( inputEditPopup, EXPLICIT_TIMEOUT / 20 ) ) {
        LOGGER.info( "Edit Popup was not opened saccessfully! Click attempt: " + count );
        clicker.doubleClick( txtReportHeader.getElement() ).perform();
      } else
        break;
    }
    // Check that popup appears
    return isElementPresent( inputEditPopup );
  }

  public boolean isDateButtonPresent() {
    return isElementPresent( btnDatePopup, 0 );
  }

  public void clickDateButton() {
    click( btnDatePopup );
  }

  public boolean isPagesButtonPresent() {
    return isElementPresent( btnPagesPopup, 0 );
  }

  public void clickPagesButton() {
    click( btnPagesPopup );
  }

  public void closeEditReportHeader() {
    click( btnCloseEditPopup, true );
  }

  public void setReportHeader( String newHeader ) {
    if ( openEditReportHeader() ) {
      type( inputEditPopup, newHeader );
    }
    closeEditReportHeader();
  }

  public void clearReportHeader() {
    if ( openEditReportHeader() ) {
      inputEditPopup.getElement().clear();
    }
    closeEditReportHeader();
  }

  public String getReportHeader() {
    waitForElementToBeClickable( txtReportHeader, (int) IMPLICIT_TIMEOUT );
    return findExtendedWebElement( txtReportHeader.getBy() ).getText();
  }

  public boolean isPageCounterCorrectOnHeader( int currentPage, int totalPages ) {
    boolean res = false;
    String actualHeader = getReportHeader();
    LOGGER.info( "Actual Header: " + actualHeader );
    if ( !actualHeader.contains( PAGES_SEPARATOR_ON_HEADER ) ) {
      return false;
    }

    int index = actualHeader.indexOf( PAGES_SEPARATOR_ON_HEADER );
    String leftPartHeader = actualHeader.substring( 0, index );
    String rightPartHeader = actualHeader.substring( index + PAGES_SEPARATOR_ON_HEADER.length() );

    if ( leftPartHeader.endsWith( String.valueOf( currentPage ) )
        && rightPartHeader.startsWith( String.valueOf( totalPages ) ) ) {
      res = true;
    }

    return res;
  }

  public void undo() {
    click( btnUndo, true );
  }

  public void redo() {
    click( btnRedo, true );
  }

  public boolean isGroupOnWorkspace( String fieldName ) {
    boolean res = false;
    String fieldId = getFieldId( fieldName );

    driver.manage().timeouts().implicitlyWait( 1, TimeUnit.SECONDS );

    if ( isElementPresent( format( groupItem, fieldId ) ) ) {
      res = true;
    }
    driver.manage().timeouts().implicitlyWait( IMPLICIT_TIMEOUT, TimeUnit.SECONDS );

    return res;
  }

  protected String getFieldId( String fieldName ) {
    return format( availableFieldItem, fieldName ).getAttribute( "fieldid" );
  }

  public boolean isGroupSortingPresent( String fieldName ) {
    return isElementPresent( format( groupSortingItem, fieldName ) );
  }

  public boolean isFieldSortingPresent( String fieldName ) {
    ExtendedWebElement element = format( 2 * EXPLICIT_TIMEOUT, fieldSortingItem, fieldName );
    waitForElementToBeClickable( element, (int) ( 2 * EXPLICIT_TIMEOUT ) );
    return isElementPresent( element, IMPLICIT_TIMEOUT );
  }

  public void sortColumn( String column, Sort sort, Workflow workflow ) {
    if ( !isColumnOnWorkspace( column ) ) {
      Assert.fail( "Impossible to sort column '" + column
          + "' , please verify open pop-menu of sorting or add Field to Columns first!" );
      return;
    }

    switch ( workflow ) {
      case CONTEXT_REPORT:
        rightClickColumn( column );
        waitForElementToBeClickable( sortSubMenu, (int) ( EXPLICIT_TIMEOUT ) );
        click( sortSubMenu, EXPLICIT_TIMEOUT / 5 );

        switch ( sort ) {
          case ASC:
            click( sortSubMenuAsc, true );
            break;
          case DESC:
            click( sortSubMenuDesc, true );
            break;
          case NONE:
            click( sortSubMenuNone, true );
            break;
        }
        break;
      case CONTEXT_PANEL:
        if ( !isFieldSortingPresent( column ) ) {
          LOGGER.error( column + " is not present on Field sorting area!" );
          return;
        }

        switch ( sort ) {
          case ASC:
            select( format( fieldSortingItem, column ), sort.getName(), true );
            break;
          case DESC:
            select( format( fieldSortingItem, column ), sort.getName(), true );
            break;
          case NONE:
            click( format( fieldSortingDeleteItem, column ), true );
            break;
        }
        break;
      default:
        Assert.fail( "Unsupported workflow type!" );
        break;
    }
    Boolean isColumnVisible = isColumnOnWorkspace( column );
    LOGGER.info( "Column on workspase is visible :" + isColumnVisible );
  }

  /**
   * Verifies sorting for specified column under all groups on the current page is correct
   * 
   * @param columnName
   *          values will be verified for sorting
   * @param sort
   *          type of sorting: ASC or DESC
   * @return true if sorting for specified column under all groups on the current page is correct
   */
  public boolean verifyColumnSort( String columnName, Sort sort ) {

    // Get column position number in the table
    String position = format( columnItem, columnName ).getAttribute( "fieldidx" );

    // Get list of valuable records
    List<String> textRecords = new ArrayList<String>();
    for ( ExtendedWebElement record : recordItems ) {
      String id = record.getAttribute( "id" );
      // Select only records which are related with required column
      if ( record.getText() != null && id.contains( "column" ) && id.endsWith( position ) )
        textRecords.add( record.getText() );
    }

    // Verification part
    int i = 0;
    List<String> sortValues = new ArrayList<String>();
    for ( String record : textRecords ) {
      // sortValues list will collect column values for one group if any
      if ( !record.trim().equals( columnName ) ) {
        sortValues.add( record );
        continue;
      }
      i++;
      if ( i == 2 ) {
        // Verification that exact group of column values is sorted correctly
        if ( !isSorted( sortValues, sort ) )
          return false;
        sortValues.clear();
        i--;
      }
    }

    // Verification that the last group of column values is sorted correctly
    return isSorted( sortValues, sort );
  }

  /**
   * Verifies sorting for specified Group on the current page is correct
   * 
   * @param groupName
   *          values will be verified for sorting
   * @param sort
   *          type of sorting: ASC or DESC
   * @return true if sorting for specified Group on the current page is correct
   */
  public boolean verifyGroupSort( String groupName, Sort sort ) {

    // Get group position number in the table
    String position = format( groupItem, getFieldId( groupName ) ).getAttribute( "groupidx" );

    // Get list of group values
    List<String> sortValues = new ArrayList<String>();
    List<ExtendedWebElement> groupHeaders =
        findExtendedWebElements( By.xpath( "//td[starts-with(@id,'rpt-group-header') and @groupidx]" ) );
    for ( ExtendedWebElement group : groupHeaders ) {
      if ( group.getAttribute( "id" ).endsWith( position ) )
        sortValues.add( group.getText() );
    }

    return isSorted( sortValues, sort );
  }

  public void selectColumnHeader( String columnName ) {

    click( format( columnItem, columnName ) );

    if ( !isColumnHeaderSelected( columnName ) ) {
      LOGGER.error( "Column header for '" + "+ column was not selected!" );
    }
  }

  public boolean isColumnHeaderSelected( String columnName ) {
    return format( columnItem, columnName ).getAttribute( "style" ).contains( SELECTION_INDICATOR );
  }

  public void selectColumn( String columnName ) {

    click( format( columnBlockItem, getFieldId( columnName ) ), EXPLICIT_TIMEOUT / 10 );

    if ( !isColumnBlockSelected( columnName ) ) {
      LOGGER.error( "Column block for '" + "+ column was not selected!" );
    }
  }

  public boolean isColumnBlockSelected( String columnName ) {
    boolean res = false;

    ExtendedWebElement item = format( columnBlockItem, getFieldId( columnName ), EXPLICIT_TIMEOUT / 10 );

    if ( item.getAttribute( "style" ) != null && item.getAttribute( "style" ).contains( SELECTION_INDICATOR ) ) {
      res = true;
    }

    return res;
  }

  public void selectGroup( String groupName ) {
    click( format( groupItem, getFieldId( groupName ) ) );

    if ( !isGroupSelected( groupName ) ) {
      LOGGER.error( "Group '" + "+ was not selected!" );
    }
  }

  public boolean isGroupSelected( String groupName ) {
    return format( groupItem, getFieldId( groupName ) ).getAttribute( "style" ).contains( SELECTION_INDICATOR );
  }

  public void selectAll() {
    click( btnSelectAll );
  }

  public PIRFormattingPage openFormattingTab() {
    click( tabFormatting );
    return new PIRFormattingPage( driver );
  }

  public PIRFormattingPage openFormattingTab( String column ) {
    selectColumnHeader( column );
    click( tabFormatting );
    return new PIRFormattingPage( driver );
  }

  public void clickTextColorDownArrow() {
    click( btnTextColorDownArrow );
  }

  public boolean isTextColorPickerPresent() {
    return isElementPresent( textColorPicker );
  }

  public void openTextColorPicker() {
    // check if the color picker is present
    if ( isTextColorPickerPresent() ) {
      return;
    }
    clickTextColorDownArrow();
    if ( !isTextColorPickerPresent() ) {
      CustomAssert.fail( "64012", "Text Color Picker did not appear!" );
    }
    verifyColorPickerPopup();
  }

  public void clickBackgroundColorDownArrow() {
    click( btnBackgroundColorDownArrow );
  }

  public boolean isBackgroundColorPickerPresent() {
    return isElementPresent( backgroundColorPicker );
  }

  public void openBackgroundColorPicker() {
    // check if the color picker is present
    if ( isBackgroundColorPickerPresent() ) {
      return;
    }
    clickBackgroundColorDownArrow();
    if ( !isBackgroundColorPickerPresent() ) {
      CustomAssert.fail( "64022", "Background Color Picker did not appear!" );
    }
    verifyColorPickerPopup();
  }

  public void clickColor( Colour color ) {
    clickColor( color, false );
  }

  public void clickColor( Colour color, boolean inUse ) {
    // find color by HEX value
    if ( !inUse && isElementPresent( format( colorIcon, color.getHexValue() ) ) ) {
      click( format( colorIcon, color.getHexValue() ) );
    } else if ( inUse && isElementPresent( format( colorInUse, color.getHexValue() ) ) ) {
      click( format( colorInUse, color.getHexValue() ) );
    } else {
      LOGGER.error( "Unable to click color: " + color.getName() );
    }
    // required pause here because it takes a second for current color and In Use list to update
    pause( 1 );
  }

  public void setRgbColor( Colour colour ) {
    Color rgb = colour.getRgba();

    // set rgb values
    clearAndSetValue( customRgbValues.get( 0 ), String.valueOf( rgb.getRed() ) );
    clearAndSetValue( customRgbValues.get( 1 ), String.valueOf( rgb.getGreen() ) );
    clearAndSetValue( customRgbValues.get( 2 ), String.valueOf( rgb.getBlue() ) );
    // required pause here because it takes a second for current color and In Use list to update
    pause( 2 );
  }

  public void setHexColor( Colour hexValue ) {
    clearAndSetValue( customHexValue, hexValue.getHexValue() );
    pause( 1 );
  }

  public void setHue( int pixels ) {
    // get saturation picker color before changing the hue
    Colour before = parseBackgroundColorRgba( saturationPicker );
    // move hue handle
    Actions action = new Actions( getDriver() );
    action.moveToElement( hueHandle.getElement() ).clickAndHold().build().perform();
    action.moveByOffset( 0, pixels ).build().perform();
    action.release().perform();
    // get saturation picker color after changing the hue to compare it with the previous color
    Colour after = parseBackgroundColorRgba( saturationPicker );

    if ( before.equals( after ) ) {
      CustomAssert.fail( "64020", "Moving the hue handle does not update the color palette!" );
      CustomAssert.fail( "64023", "Moving the hue handle does not update the color palette!" );
    }
  }

  public void setSaturation() {
    // [MG] click on the top right corner to get a specific color
    Actions action = new Actions( getDriver() );
    int x = saturationPicker.getElement().getSize().getWidth();
    action.moveToElement( saturationPicker.getElement() ).click().perform();
    action.moveToElement( saturationPicker.getElement(), x, 0 ).click().click().build().perform();
    pause( 2 );
  }

  private void clearAndSetValue( ExtendedWebElement element, String value ) {
    // select and delete previous value
    element.getElement().sendKeys( Keys.CONTROL, "a", Keys.BACK_SPACE );
    // type new value and press Enter so it persists
    element.getElement().sendKeys( value, Keys.ENTER );
  }

  public void clickPaletteTab() {
    click( tabPalette );
  }

  public void clickCustomTab() {
    click( tabCustom );
  }

  public Colour getColumnHeaderTextColor( String name ) {
    return parseTextColorRgba( format( columnItem, name ) );
  }

  public Colour getColumnBackgroundColor( String name ) {
    return parseBackgroundColorRgba( format( columnItem, name ) );
  }

  public Colour getCurrentColor() {
    return parseBackgroundColorRgba( this.currentColor );
  }

  public boolean isColorInUse( Colour color ) {
    boolean res = false;
    for ( ExtendedWebElement colorInUse : colorsInUse ) {
      if ( parseBackgroundColorRgba( colorInUse ).equals( color ) ) {
        res = true;
        break;
      }
    }
    return res;
  }

  public Colour getForeColorSample() {
    return parseBackgroundColorRgba( textColor );
  }

  public Colour getBackColorSample() {
    return parseBackgroundColorRgba( backgroundColor );
  }

  private void verifyColorPickerPopup() {
    long timeout = EXPLICIT_TIMEOUT / 15;
    // verify that tabs and elements within tabs are present
    if ( !isElementPresent( lblCurrent, timeout ) || !isElementPresent( currentColor, timeout ) ) {
      CustomAssert.fail( "64012", "Current color label and/or box are not present!" );
      CustomAssert.fail( "64022", "Current color label and/or box are not present!" );
    }

    if ( !isElementPresent( tabPalette, timeout ) ) {
      CustomAssert.fail( "64012", "Palette tab is not present!" );
      CustomAssert.fail( "64022", "Palette tab is not present!" );
    } else {
      if ( !isElementPresent( paletteGrid, timeout ) ) {
        CustomAssert.fail( "64012", "Palette color grid is not present!" );
        CustomAssert.fail( "64022", "Palette color grid is not present!" );
      }
    }

    if ( !isElementPresent( format( lblInUse, L10N.generateConcatForXPath( IN_USE ) ), timeout ) ) {
      CustomAssert.fail( "64012", "In Use label is not present!" );
      CustomAssert.fail( "64022", "In Use label is not present!" );
    } else {
      if ( colorsInUse.size() < 5 ) {
        CustomAssert.fail( "64012", "List of Colors in use is incomplete!" );
        CustomAssert.fail( "64022", "List of Colors in use is incomplete!" );
      }
    }

    if ( !isElementPresent( tabCustom, timeout ) ) {
      CustomAssert.fail( "64012", "Custom tab is not present!" );
      CustomAssert.fail( "64022", "Custom tab is not present!" );
    }
  }

  private Colour parseBackgroundColorRgba( ExtendedWebElement element ) {
    return parseRgba( element, BACKGROUND_COLOR );
  }

  private Colour parseTextColorRgba( ExtendedWebElement element ) {
    return parseRgba( element, TEXT_COLOR );
  }

  private Colour parseRgba( ExtendedWebElement element, String type ) {
    // get RGBA values
    String rgba = element.getElement().getCssValue( type );
    return Colour.getColour( rgba );
  }

  public PIRGeneralPage openGeneralTab() {
    click( tabGeneral );
    return new PIRGeneralPage( driver );
  }

  public void openDataTab() {
    click( tabData );
  }

  public void openPirReportPageTabContextMenu() {
    makeClickable();
    rightClick( pirReportPageTab );
  }

  public boolean isErrorDialogPresent() {
    boolean res = false;
    switchToFrame();

    if ( isElementPresent( errorDialog, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Error message displayed" );
      res = true;
    }

    return res;
  }

  public void closeErrorDialog() {
    click( btnOKErrorDialog );
  }

  public boolean isPageNumberPanelExists() {
    return isElementPresent( pageNumberControl, EXPLICIT_TIMEOUT / 10 );
  }

  // TODO: analyze and remove duplicate with ReportPage->verifyContent
  public SoftAssert verifyContent( String elementsPresent, String elementsNotPresent ) {
    try {
      switchToFrame( reportContentFrame );
    } catch ( NoSuchFrameException e ) {
      LOGGER.info( "TODO: Analyze possibility to support Operations Mart & PIR Sniff for data verification" );
    }
    SoftAssert softAssert = new SoftAssert();

    String[] elements = elementsPresent.split( ";" );
    for ( int i = 0; i < elements.length; i++ ) {
      String elementPresent = elements[i];

      pause( 1 );
      if ( !elementPresent.isEmpty() && !format( contentElement, elementPresent ).isElementPresent( EXPLICIT_TIMEOUT ) ) {
        softAssert.fail( "Element with xpath '" + elementPresent + "' was not found!" );
      } else {
        LOGGER.info( "Element with xpath '" + elementPresent + "' was found as expected." );
      }
    }

    elements = elementsNotPresent.split( ";" );
    // TODO: improve logic for notFound elements, too long
    for ( int i = 0; i < elements.length; i++ ) {
      String elementNotPresent = elements[i];
      if ( !elementNotPresent.isEmpty()
          && format( contentElement, elementNotPresent, EXPLICIT_TIMEOUT / 20 )
              .isElementPresent( EXPLICIT_TIMEOUT / 20 ) ) {
        softAssert.fail( "Element with xpath '" + elementNotPresent + "' was mistakenly found!" );
      } else {
        LOGGER.info( "Element with xpath '" + elementNotPresent + "' was not found as expected." );
      }
    }

    return softAssert;
  }

  public SoftAssert verifyContentByValue( String valuesPresent, String valuesNotPresent ) {
    try {
      switchToFrame( reportContentFrame );
    } catch ( NoSuchFrameException e ) {
      LOGGER.info( "TODO: Analyze possibility to support Operations Mart & PIR Sniff for data verification" );
    }
    SoftAssert softAssert = new SoftAssert();
    Locale locale = new Locale( Configuration.get( Parameter.LOCALE ) );

    String[] elements = valuesPresent.split( ";" );
    for ( int i = 0; i < elements.length; i++ ) {
      String locValue = elements[i];
      if ( isCorrespondNumberFormat( locValue, DEFAULT_NUMBER_FORMAT ) ) {
        locValue = Utils.localizeNumberString( locale, locValue, DEFAULT_NUMBER_FORMAT );
      }
      if ( !format( columnValueItem, locValue ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
        softAssert.fail( "Element with value '" + locValue + "' was not found!" );
      } else {
        LOGGER.info( "Element with value '" + locValue + "' was found as expected." );
      }
    }

    elements = valuesNotPresent.split( ";" );
    for ( int i = 0; i < elements.length; i++ ) {
      String locValue = elements[i];
      if ( isCorrespondNumberFormat( locValue, DEFAULT_NUMBER_FORMAT ) ) {
        locValue = Utils.localizeNumberString( locale, locValue, DEFAULT_NUMBER_FORMAT );
      }
      if ( format( columnValueItem, locValue, EXPLICIT_TIMEOUT / 20 ).isElementPresent( EXPLICIT_TIMEOUT / 20 ) ) {
        softAssert.fail( "Element with value '" + locValue + "' was mistakenly found!" );
      } else {
        LOGGER.info( "Element with value '" + locValue + "' was not found as expected." );
      }
    }

    return softAssert;
  }

  public void exportAs( ExportType exportType ) {
    click( exportButton );
    switch ( exportType ) {
      case PDF:
        click( exportToPDFMenu );
        break;
      case CSV:
        click( exportToCSVMenu );
        break;
      case EXCEL:
        click( exportToExcelMenu );
        break;
      case EXCEL_97:
        click( exportToExcel97Menu );
        break;
    }
    pause( 5 );
  }

  public void rightClickColumn( String columnName ) {
    if ( !isColumnOnWorkspace( columnName ) ) {
      LOGGER.error( "Field " + columnName
          + " is not added to the columns on Workspace please it before running this method!" );
      return;
    }

    Actions clicker = new Actions( driver );
    clicker.click( format( columnItem, columnName ).getElement() ).perform(); // to make clickable the column
    pause( 1 );
    clicker.contextClick( format( columnItem, columnName ).getElement() ).perform();
    pause( 1 );
  }

  public boolean isAutoSubmitChecked() {
    boolean res = false;
    res = autoSubmitCheckbox.isChecked();
    return res;
  }

  public int getFiltersNumber() {
    String text = null;
    List<String> classItems = Arrays.asList( btnFilterIndicator.getAttribute( "class" ).toString().split( " " ) );

    for ( String classItem : classItems ) {
      if ( classItem.contains( "hidden" ) ) {
        return 0;
      }
      if ( classItem.contains( FILTERS_NUMBER_INDICATOR ) ) {
        text = classItem;
      }
    }

    int startIndex = text.lastIndexOf( "_" ) + 1;
    return Integer.parseInt( text.substring( startIndex, text.length() ) );
  }

  public boolean isFilterIconPresent( String field ) {
    return format( availableFieldItem, field ).getAttribute( "class" ).contains( "fieldlist-filtered-field" );
  }

  // Workaround for FF for making panel visible
  public void makePanelVisible( String field ) {

    if ( isElementPresent( labelAvailableFields ) ) {

      Utils.dnd( format( availableFieldItem, field ), labelAvailableFields );
    }

    if ( isElementPresent( tabData ) ) {

      Utils.dnd( format( availableFieldItem, field ), tabData );
    }

  }

  public void selectOpenTabInNewWindow() {

    openTabInNewWindow.click();
  }

  public ExtendedWebElement getOkButton() {

    return okButton;
  }

  public ExtendedWebElement getCancelButton() {

    return cancelButton;
  }

  public boolean checkDataSourceName( String name ) {
    return isElementPresent( format( dataSourceName, name ) );
  }
}
