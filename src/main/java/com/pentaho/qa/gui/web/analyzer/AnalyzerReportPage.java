package com.pentaho.qa.gui.web.analyzer;

import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;
import org.testng.util.Strings;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.pentaho.qa.gui.web.analyzer.chart.ChartFactory;
import com.pentaho.qa.gui.web.analyzer.chart.ChartType;
import com.pentaho.qa.gui.web.analyzer.chart.CommonChart;
import com.pentaho.qa.gui.web.chart_options.ChartOptionsPage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.Report.Colour;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.analyzer.PAMeasure;
import com.pentaho.services.tree.Tree;
import com.pentaho.services.tree.Tree.TreeNode;
import com.pentaho.services.utils.ExportType;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AnalyzerReportPage extends AnalyzerBasePage {
  // Stores the background color information for various panel items within the report page.
  public enum PanelItem {
    LAYOUT_ROWS( "Rows", Colour.DROVER, Colour.TEXAS ),
    LAYOUT_COLUMNS( "Columns", Colour.DROVER, Colour.TEXAS ),
    LAYOUT_MEASURES( "Measures", Colour.LIGHT_CYAN, Colour.ONAHAU ),
    LAYOUT_SECTION_ATTRIBUTES( "Layout Attributes", Colour.SOLITUDE, Colour.CREAM, Colour.WHITE, Colour.CREAM ),
    LAYOUT_SECTION_MEASURES( "Layout Measures", Colour.SOLITUDE, Colour.ALICE_BLUE, Colour.WHITE, Colour.ALICE_BLUE ),
    AVAILABLE_FIELDS( "Available Fields", Colour.WHITE, Colour.OYSTER_BAY, Colour.WHITE, Colour.REEF );

    private String name;
    private Colour bgColorCrystal;
    private Colour bgColorOnyx;
    private Colour bgColorHoverCrystal;
    private Colour bgColorHoverOnyx;

    private PanelItem( String name, Colour bgColorCrystal, Colour bgColorHoverCrystal ) {
      this.name = name;
      this.bgColorCrystal = bgColorCrystal;
      this.bgColorHoverCrystal = bgColorHoverCrystal;
    }

    private PanelItem( String name, Colour bgColorCrystal, Colour bgColorHoverCrystal, Colour bgColorOnyx,
        Colour bgColorHoveredOnyx ) {
      this.name = name;
      this.bgColorCrystal = bgColorCrystal;
      this.bgColorOnyx = bgColorOnyx;
      this.bgColorHoverCrystal = bgColorHoverCrystal;
      this.bgColorHoverOnyx = bgColorHoveredOnyx;
    }

    /**
     * Gets background color of the panel item.
     * 
     * @param theme
     *          The theme that the application is using.
     * @return Returns a Colour object that contains the color information for the panel item.
     */
    public Colour getBackgroundColor( Theme theme ) {
      return theme == Theme.CRYSTAL ? bgColorCrystal : bgColorOnyx;
    }

    /**
     * Gets the background color of the panel item when it is in its hovered state.
     * 
     * @param theme
     *          The theme that the application is using.
     * @return Returns a Colour object that contains the color information for the panel item.
     */
    public Colour getBackgroundColorHover( Theme theme ) {
      return theme == Theme.CRYSTAL ? bgColorHoverCrystal : bgColorHoverOnyx;
    }

    /**
     * Gets the name of the panel item.
     * 
     * @return Returns a String of the name of the panel item.
     */
    public String getName() {
      return name;
    }

    /**
     * Finds and retrieves an instance of PanelItem where the name matches the specified name.
     * 
     * @param name
     *          The name of the PanelItems object to find.
     * @return Returns the matched PanelItems object.
     */
    public static PanelItem getPanelItemByName( String name ) {
      PanelItem matchedItem = null;

      for ( PanelItem panelItem : PanelItem.values() ) {
        if ( panelItem.getName().equals( name ) ) {
          matchedItem = panelItem;
        }
      }

      return matchedItem;
    }
  };

  public String getAvailableFieldBackgroundColor( String fieldName ) {
    return format( fieldItem, fieldName ).getElement().getCssValue( "background-color" );
  }

  AnalyzerFilterPage analyzerFilterPage;
  private final CharSequence AVAILABLE_FIELDS_BTN_SHORTCUT = Keys.chord( Keys.CONTROL, Keys.ALT, "f" );
  private final CharSequence LAYOUT_BTN_SHORTCUT = Keys.chord( Keys.CONTROL, Keys.ALT, "y" );
  private final CharSequence FILTER_BTN_SHORTCUT = Keys.chord( Keys.CONTROL, Keys.ALT, "t" );

  // This is used to find multiple field items by category in the getAllAvailableFields method.
  protected final String FIELD_ITEMS_BY_CATEGORY =
      "//div[@id='fieldListTreeContent']/div[contains(@class, 'folderContent') and contains(@id, '%s')]/div[contains(@class, 'field')]";

  // This is used to find multiple layout fields within a specified layout container.
  protected final String LAYOUT_GEM_BAR_FIELDS =
      "//div[@id='layoutPanel']//div[contains(@id, '%s')]//div[@type='measure' or @type='attribute']";

  // 'Select Data Source'
  @FindBy( xpath = "//div[text()='{L10N:selectSchemaTitle}']" )
  protected ExtendedWebElement dataSources;

  @FindBy( xpath = "//div[contains(@class,'field') and text()='%s']" )
  protected ExtendedWebElement fieldItem;

  @FindBy( xpath = "//div[contains(@class,'field') and text()='%s' and not(contains(@formula,'[Measures]'))]" )
  protected ExtendedWebElement fieldItemWhithoutMeasures;

  @FindBy( xpath = "//div[contains(@class,'field') and text()='%s' and contains(@formula,'[Measures]')]" )
  protected ExtendedWebElement measureItem;

  @FindBy( xpath = "//*[name()='svg']//*[name()='text'][text()='%s']" )
  protected ExtendedWebElement axisItem;

  @FindBy( xpath = "//div[@id='fieldListTreeContent']/div[contains(@class,'folder')]/div[text()='%s']" )
  protected ExtendedWebElement folderAvailableField;

  @FindBy( xpath = "//span[contains(., '%s')]" )
  protected ExtendedWebElement filterItem;

  @FindBy( xpath = "//iframe[contains(@name,'frame_')]" )
  protected ExtendedWebElement reportFrame;

  // Tool bar
  @FindBy( id = "cmdFields" )
  protected ExtendedWebElement btnFieldsPanel;

  @FindBy( xpath = "//div[@id='cmdFields' and contains(@title, '{L10N:btnTitleHideFields}')]" )
  protected ExtendedWebElement btnFieldsPanelTooltipVisible;

  @FindBy( xpath = "//div[@id='cmdFields' and contains(@title, '{L10N:btnTitleShowFields}')]" )
  protected ExtendedWebElement btnFieldsPanelTooltipInvisible;

  @FindBy( id = "cmdLayout" )
  protected ExtendedWebElement btnLayoutPanel;

  @FindBy( xpath = "//div[@id='cmdLayout' and contains(@title, '{L10N:btnTitleHideLayout}')]" )
  protected ExtendedWebElement btnLayoutPanelTooltipVisible;

  @FindBy( xpath = "//div[@id='cmdLayout' and contains(@title, '{L10N:btnTitleShowLayout}')]" )
  protected ExtendedWebElement btnLayoutPanelTooltipInvisible;

  @FindBy( id = "cmdFilters" )
  protected ExtendedWebElement btnFiltersPanel;

  @FindBy( xpath = "//div[@id='cmdFilters' and contains(@title, '{L10N:btnTitleHideFilters}')]" )
  protected ExtendedWebElement btnFiltersPanelTooltipVisible;

  @FindBy( xpath = "//div[@id='cmdFilters' and contains(@title, '{L10N:btnTitleShowFilters}')]" )
  protected ExtendedWebElement btnFiltersPanelTooltipInvisible;

  @FindBy( id = "RPT001FilterPaneToggle" )
  protected ExtendedWebElement accordionFilter;

  @FindBy( id = "RPT001FilterCountLabel" )
  protected ExtendedWebElement filterCountText;

  @FindBy( id = "RPT001HelpFilterPane" )
  protected ExtendedWebElement btnFilterHelp;

  @FindBy( id = "RPT001HideFilterPane" )
  protected ExtendedWebElement btnFilterClose;

  @FindBy( id = "cmdToggleRefresh" )
  protected ExtendedWebElement btnRefresh;

  @FindBy( xpath = "//div[@id='cmdToggleRefresh']/div[@class='auto-refresh-icon']" )
  protected ExtendedWebElement btnRefreshIcon;

  // Filter panel
  @FindBy( id = "RPT001Filter" )
  protected ExtendedWebElement panelFilter;

  // Available Fields
  @FindBy( id = "analysisArea" )
  protected ExtendedWebElement textCubeName;

  @FindBy( id = "fieldList" )
  protected ExtendedWebElement panelAvailableFields;

  @FindBy( xpath = "//div[contains(@id, 'ReportMain')]" )
  protected ExtendedWebElement reportMain;

  @FindBy( id = "dijit_layout_ContentPane_0" )
  protected ExtendedWebElement lblFindAavailableFields;

  @FindBy( id = "searchField" )
  protected ExtendedWebElement searchBox;

  @FindBy( id = "clearSearchField" )
  protected ExtendedWebElement clearSearchBtn;

  @FindBy( id = "viewFieldOptions" )
  protected ExtendedWebElement btnView;

  @FindBy( id = "viewFieldOptions2" )
  protected ExtendedWebElement btnView2;

  @FindBy( id = "CMDVIEWCATEGORY_text" )
  protected ExtendedWebElement lnkByCategory;

  @FindBy( id = "CMDVIEWTYPE_text" )
  protected ExtendedWebElement lnkByType;

  @FindBy( id = "CMDVIEWNAME_text" )
  protected ExtendedWebElement lnkByName;

  @FindBy( id = "CMDVIEWSCHEMA_text" )
  protected ExtendedWebElement lnkBySchema;

  @FindBy( id = "CMDSHOWHIDEFIELDS_text" )
  protected ExtendedWebElement lnkShowHiddenFields;

  @FindBy(
      xpath = "//table[@id='fieldViewMenu']//td[@id='%s']/preceding-sibling::td/span[not(contains(@class, 'unchecked'))]" )
  protected ExtendedWebElement lnkViewChecked;

  @FindBy( id = "fieldListTree" )
  protected ExtendedWebElement fieldListTree;

  @FindBy( css = "#fieldListTreeContent > .folder" )
  protected List<ExtendedWebElement> fieldFolderItems;

  @FindBy( css = "#fieldListTreeContent > .folderContent" )
  protected List<ExtendedWebElement> fieldFolderContentItems;

  @FindBy( css = "#fieldListTreeContent > .folderContent > .field" )
  protected List<ExtendedWebElement> fieldFieldItems;

  @FindBy( css = "#fieldListTreeContent > .field" )
  protected List<ExtendedWebElement> fieldsWithoutCategories;

  @FindBy( xpath = "//div[@id='fieldListTreeContent']/div[text() = '%s']" )
  protected ExtendedWebElement fieldListCategory;

  // Report panel
  @FindBy( id = "RPT001ReportArea" )
  protected ExtendedWebElement reportPanel;

  @FindBy( css = "#RPT001ReportArea > .reportEmpty" )
  protected ExtendedWebElement emptyReport;

  @FindBy( xpath = "//div[@id='RPT001ReportArea']//div[contains(@class,'viz-image')]" )
  protected ExtendedWebElement emptyReportImage;

  @FindBy( xpath = "//div[@id='RPT001ReportArea']//div[text()='{L10N:emptyVizArea}']" )
  protected ExtendedWebElement emptyReportDragFieldText;

  @FindBy( xpath = "//div[@class='noData']/span[@class='noDataHeader' and text()='{L10N:reportNoDataMsg}']" )
  protected ExtendedWebElement noDataMessage;

  @FindBy(
      xpath = "//div[@id='RPT001ReportArea']/div[text()='{L10N:emptyVizArea}']/div[string-length(text())>0 and not (contains(@class,'viz-image'))]" )
  protected ExtendedWebElement emptyReportAsteriskText;

  // View (filter for the available fields)
  @FindBy( id = "viewFieldOptions" )
  protected ExtendedWebElement viewFilterExpand;

  @FindBy( id = "CMDVIEWNAME_text" )
  protected ExtendedWebElement viewFilterAZ;

  @FindBy( xpath = "//tr[@id='CMDVIEWNAME']//span[@class='dijitInline dijitIcon dijitMenuItemIcon checked']" )
  protected ExtendedWebElement viewFilterAZChecked;

  @FindBy( id = "FT_filterOp_EQUAL" )
  public ExtendedWebElement filterOp_EQUAL;

  @FindBy( id = "cmdSelectChartType" )
  public ExtendedWebElement cmdSelectChartTypeBtn;

  @FindBy( id = "cmdShowPivot" )
  public ExtendedWebElement cmdShowPivotBtn;

  @FindBy( id = "cmdShowChart" )
  protected ExtendedWebElement cmdShowChartBtn;

  @FindBy( xpath = "//div[@id='cmdShowPivot' and contains(@class, 'reportDisplayFormatPressed')]" )
  public ExtendedWebElement cmdShowPivotBtnSelected;

  @FindBy(
      xpath = "//div[contains(@class,'reportEmpty')]//div[contains(@class,'viz-image') and contains(@class,'%s')]" )
  private ExtendedWebElement defaultChartImageElement;

  @FindBy( xpath = "//div[@id='layoutPanelWrapper']//div[@class='caption' and contains(.,'%s')]" )
  private ExtendedWebElement gemBarLabelElement;

  @FindBy( xpath = "//div[@id='layoutPanelWrapper']//div[contains(@id,'%s')]//div[contains(.,'%s')]" )
  private ExtendedWebElement gemBarMemberElement;

  @FindBy(
      xpath = "//div[@id='layoutPanelWrapper']//div[@class='caption' and contains(.,'%s')]/following-sibling::div[contains(@id, '%s')]//div[%s(contains(@class,'reqiured')) and contains(.,'%s')]" )
  protected ExtendedWebElement gemBarMemberRequired;

  @FindBy(
      xpath = "//div[@id='layoutPanelWrapper']//div[@class='caption' and contains(.,'%s')]/following-sibling::div[contains(@id, '%s')]//div[contains(.,'%s')]/span" )
  protected ExtendedWebElement gemBarMemberText;

  @FindBy(
      xpath = "//div[@id='layoutPanelWrapper']//div[@class='caption' and contains(.,'%s')]/*[contains(@class,'captionIcon') and contains(@class,'%s')]" )
  protected ExtendedWebElement gemBarIcon;

  @FindBy(
      xpath = "//div[@id='layoutPanelWrapper']//div[contains(@id,'%s')]//div[contains(@class,'gemPlaceholder')]//span" )
  protected ExtendedWebElement gemBarDropAreaElement;

  @FindBy( xpath = "//div[@id='layoutPanelWrapper']//div[contains(@id,'%s') and contains(@class,'propPanel_gemBar')]" )
  protected ExtendedWebElement gemBarDropAreaContainer;

  @FindBy(
      xpath = "//div[@id='layoutPanelWrapper']//div[contains(@id,'%s')]//div[@id='propertyPanelIndicator' and not(contains(@style,'display: none'))]" )
  protected ExtendedWebElement gemBarDropIndicator;

  @FindBy(
      xpath = "//div[@id='reportContainer']//div[@class='reportRefresh' and contains(.,'{L10N:warnAutoRefreshPanel}')]" )
  private ExtendedWebElement refreshMsgBarElement;

  @FindBy( xpath = "//div[@id='reportContainer']//div[@class='reportRefresh']//button[@id='cmdRefreshBtn']" )
  private ExtendedWebElement refreshButtonElement;

  // 5.4
  @FindBy( xpath = "//div[@id='pivotTable']//div[contains(.,'%s')]" )
  private ExtendedWebElement pivotTableHeaderMember;

  @FindBy( xpath = "//div[@id='pivotTable']//*[@type='attribute' or @type='measure']//*[text()='%s']" )
  protected ExtendedWebElement pivotTableColumnText;

  @FindBy( xpath = "//*[@id='pivotTable']//*[contains(@class,'pivotTableDataSection')]//*[@type='cell']" )
  protected List<ExtendedWebElement> pivotTableMeasureValues;

  // 5.4
  @FindBy( xpath = "//div[@id='pivotTable']//td[@type='member' and contains(.,'%s')]" )
  private ExtendedWebElement pivotTableMember;

  @FindBy( xpath = "//div[@id='pivotTable']//td[@type='member' and contains(@member,'%s')]/div[text()='%s']" )
  private ExtendedWebElement pivotTableField;

  // 5.4
  @FindBy( xpath = "//div[@id='pivotTable']//div[contains(@class,'pivotTableRowLabelContainer')]//tr" )
  private List<ExtendedWebElement> pivotTableCells;

  @FindBy( id = "PM:membKEEP" )
  private ExtendedWebElement includeFilterElem;

  @FindBy( xpath = "//i[contains(@id,'remove_filter')]" )
  private ExtendedWebElement removeFilterElem;

  @FindBy( id = "cmdUndo" )
  private ExtendedWebElement undoBtn;

  @FindBy( id = "cmdRedo" )
  private ExtendedWebElement redoBtn;

  @FindBy( id = "cmdMoreActions" )
  private ExtendedWebElement btnMoreActions;

  @FindBy( xpath = "//div[@id='cmdMoreActions']/div[@class='more-menu-icon']" )
  private ExtendedWebElement btnMoreActionsIcon;

  @FindBy( id = "cmdProps" )
  protected ExtendedWebElement btnAboutReport;

  @FindBy( id = "cmdOptions" )
  private ExtendedWebElement btnReportOptionsMoreMenu;

  @FindBy( xpath = "//*[@class='dijitReset']//*[text()='{L10N:administration}']" )
  private ExtendedWebElement btnAdministration;

  @FindBy( id = "cmdResetReport" )
  private ExtendedWebElement resetBtn;

  @FindBy( id = "cmdResetColumnSize" )
  private ExtendedWebElement cmdResetColumnSize;

  @FindBy( xpath = "//*[@class='dijitReset']//*[text()='{L10N:reportAdminActionXML}']" )
  private ExtendedWebElement cmdReportXml;

  @FindBy( id = "RD_reportDef" )
  private ExtendedWebElement reportDefinition;

  @FindBy( id = "exportMenuItem" )
  private ExtendedWebElement exportMenuElem;

  @FindBy( id = "cmdPDF" )
  private ExtendedWebElement exportPdfMenuElem;

  @FindBy( id = "cmdCSV" )
  private ExtendedWebElement exportCsvMenuElem;

  @FindBy( id = "cmdExcel" )
  private ExtendedWebElement exportExcelMenuElem;

  @FindBy( id = "dlgBtnDownload" )
  private ExtendedWebElement exportBtnOnPopup;

  @FindBy( id = "fieldListWrapper" )
  private ExtendedWebElement fieldListWrapperElem;

  @FindBy( id = "fieldListWrapper_splitter" )
  private ExtendedWebElement fieldListWrapperSplitterElem;

  @FindBy( id = "reportContainerWrapper" )
  private ExtendedWebElement reportContainerElem;

  @FindBy( id = "layoutPanelWrapper" )
  private ExtendedWebElement layoutPanelWrapperElem;

  @FindBy( id = "layoutPanelWrapper_splitter" )
  private ExtendedWebElement layoutPanelWrapperElemSplitterElem;

  @FindBy(
      xpath = "//*[@id='layoutPanelWrapper']//*[text()='{L10N:dropZoneLabels_LAYOUT}']/preceding-sibling::*[@data-dojo-attach-point='arrowNode' and contains(@class,'dijitArrowNode')]" )
  protected ExtendedWebElement layoutPanelLayoutTitleArrow;

  @FindBy(
      xpath = "//*[@id='layoutPanelWrapper']//*[text()='{L10N:dropZoneLabels_PROPERTIES}']/preceding-sibling::*[@data-dojo-attach-point='arrowNode' and contains(@class,'dijitArrowNode')]" )
  protected ExtendedWebElement layoutPanelPropertiesTitleArrow;

  // Layout panel
  @FindBy( xpath = "//*[contains(@id, 'rows')]" )
  private ExtendedWebElement rowsLayout;

  @FindBy( xpath = "//*[contains(@id, 'columns')]" )
  private ExtendedWebElement columnsLayout;

  @FindBy( xpath = "//*[contains(@id, 'measures')]" )
  private ExtendedWebElement measuresLayout;

  @FindBy( xpath = "//div[@class='dijitTitlePaneContentOuter']//*[contains(text(),'%s')]/parent::div" )
  private ExtendedWebElement layoutField;

  @FindBy(
      xpath = "//div[@id='layoutPanel']//div[@type='measure' and not(@type='attribute')]//*[text()='%s']/parent::div" )
  protected ExtendedWebElement layoutMeasure;

  // Used the name "attribute" instead of level to match the naming convention in the DOM.
  @FindBy(
      xpath = "//div[@id='layoutPanel']//div[not(@type='measure') and @type='attribute']//*[text()='%s']/parent::div" )
  protected ExtendedWebElement layoutAttribute;

  @FindBy( xpath = "//div[@id='layoutPanel']//div[@type='measure' or @type='attribute']" )
  protected List<ExtendedWebElement> layoutFields;

  @FindBy( xpath = "//div[@id='layoutPanel']//div[@type='measure' and not(@type='attribute')]" )
  protected List<ExtendedWebElement> layoutMeasures;

  // Used the name "attribute" instead of level to match the naming convention in the DOM.
  @FindBy( xpath = "//div[@id='layoutPanel']//div[not(@type='measure') and @type='attribute']" )
  protected List<ExtendedWebElement> layoutAttributes;

  @FindBy( xpath = "//div[@class='dijitTitlePaneContentOuter']//*[contains(@id,'%s')]" )
  private ExtendedWebElement layoutSection;

  @FindBy( xpath = "//div[@class='dijitTitlePaneContentOuter']//*[@title='%s']" )
  private ExtendedWebElement tooltipLayout;

  @FindBy(
      xpath = "//div[@class='dijitPopup dijitMenuPopup' and not(contains(@style, 'display: none'))]//*[text()='{L10N:popupMenuRemoveFromReport}']" )
  private ExtendedWebElement btnRemoveAttribute;

  @FindBy( xpath = "//div[@id='standardDialogHeader']" )
  private ExtendedWebElement dlgAlertSameAxis;

  @FindBy( id = "dlgBtnSave" )
  private ExtendedWebElement dlgBtnSave;

  @FindBy( id = "dlgBtnCancel" )
  private ExtendedWebElement dlgBtnCancel;

  @FindBy( xpath = "//*[@id='RPT001Trashcan' and not(contains(@class,'hidden'))]" )
  protected ExtendedWebElement trashCan;

  // Canvas
  @FindBy( xpath = "//td[@title]" )
  protected List<ExtendedWebElement> canvasColumnsHeader;

  @FindBy( xpath = "//td[@title='%s']" )
  protected ExtendedWebElement canvasColumnHeader;

  @FindBy( xpath = "//*[@class='pivotTableRowLabelHeaderContainer']//col[@width]" )
  protected List<ExtendedWebElement> rowsColumnsSize;

  @FindBy( xpath = "//*[@class='pivotTableColumnHeaderSection']//col[@width]" )
  protected List<ExtendedWebElement> measuresSize;

  @FindBy( xpath = "//*[text()='{L10N:popupMenuNumberUserDefNumber}']" )
  protected ExtendedWebElement userDefinedMeasure;

  @FindBy( xpath = "//*[text()='{L10N:popupMenuNumberSummary}']" )
  protected ExtendedWebElement rankRunningSum;

  @FindBy( xpath = "//*[@id='attributePopMenu']//*[text()='{L10N:popupMenuAttributeEdit}']" )
  protected ExtendedWebElement levelContextMenuEdit;

  @FindBy( xpath = "//td[@title='%s']/*[@resizeindex]" )
  protected ExtendedWebElement canvasColumnResizeIndex;

  // Dlg Rank Running Sum
  @FindBy( xpath = "//div[@id='theDialog']//*[text()='{L10N:dlgSummaryNewTitle}']" )
  protected ExtendedWebElement dlgRankRunningSum;

  @FindBy( id = "SM_PCTOF" )
  protected ExtendedWebElement btnPercentOfMeasure;

  @FindBy( id = "SM_RANK" )
  protected ExtendedWebElement btnRankByMeasure;

  @FindBy( id = "SM_RSUM" )
  protected ExtendedWebElement btnRunningSumOfMeasure;

  @FindBy( id = "SM_Group" )
  protected ExtendedWebElement btnPercentOfRunningSumMeasure;

  @FindBy( id = "dlgBtnNext" )
  protected ExtendedWebElement dlgBtnNext;

  @FindBy( id = "PM:measSortLowHi_text" )
  protected ExtendedWebElement sortLowToHigh;

  @FindBy( id = "PM:measSortHiLow_text" )
  protected ExtendedWebElement sortHighToLow;

  @FindBy( xpath = "//*[@type='cell']/*[text()]" )
  protected List<ExtendedWebElement> columnsValues;

  // Field edit dialog
  @FindBy( xpath = "//*[@id='dialogTitleBar']//*[text()='%s']" )
  protected ExtendedWebElement dlgLevelEditTitle;

  @FindBy( id = "ED_displayLabel" )
  protected ExtendedWebElement txtDisplayName;

  // Report options frame
  @FindBy( xpath = "//*[@id='standardDialog']//*[text()='{L10N:dlgReportOptionsTitle}']" )
  protected ExtendedWebElement dlgReportOptions;

  @FindBy( xpath = "//table[@id='fieldListMenu']//td[contains(@class, 'dijitMenuItemLabel') and contains(.,'%s')]" )
  private ExtendedWebElement contextMenuItem;

  @FindBy( xpath = "//div[@id='fieldList']//div[contains(@title, '%s')]" )
  private ExtendedWebElement availableFieldItem;

  @FindBy( xpath = "//span[contains(.,'{L10N:dropZoneLabels_REPORT_OPTIONS}')]" )
  protected ExtendedWebElement reportOptionsButton;

  @FindBy( xpath = "//button[text()='{L10N:btnLabelOK}']" )
  protected ExtendedWebElement btnOK;

  // Must use css because two elements use the same ID.
  @FindBy( css = "tr#cmdReportXml" )
  protected ExtendedWebElement btnAdminXml;

  @FindBy( id = "cmdAdminLog_text" )
  private ExtendedWebElement btnAdminLog;

  @FindBy( id = "cmdAdminMDX" )
  protected ExtendedWebElement btnAdminMdx;

  // Must use css because two elements use the same ID.
  @FindBy( css = "tr#cmdClearCache" )
  protected ExtendedWebElement btnAdminClearCache;

  @FindBy( xpath = "//tr[2]/td[5]" )
  protected ExtendedWebElement mdxQuery;

  @FindBy( id = "PM:arithNumber_text" )
  protected ExtendedWebElement popupMenuCreateCalculatedMeasure;

  @FindBy(
      xpath = "//*[contains(@id,'optionsBtn')]//*[text()='{L10N:dropZoneLabels_CHART_OPTIONS}']//ancestor::span[contains(@class,'pentaho-button')]" )
  protected ExtendedWebElement btnChartOptionsLayout;

  @FindBy( xpath = "//*[@id='dialogTitleBar']//*[text()='{L10N:dlgChartPropsTitle}']" )
  protected ExtendedWebElement dlgChartOptions;

  @FindBy( id = "cmdChartProps" )
  protected ExtendedWebElement optionChartOptions;

  @FindBy( xpath = "//*[@cursor='pointer']" )
  protected List<ExtendedWebElement> canvasValues;

  @FindBy( id = "PM:editMeasureArith" )
  protected ExtendedWebElement btnEditMeasure;

  @FindBy( id = "PM:removeMetric" )
  protected ExtendedWebElement btnRemoveMeasure;

  @FindBy( id = "cmdChartProps" )
  protected ExtendedWebElement btnChartOptionsMoreMenu;

  @FindBy( id = "PM:attrFilter_text" )
  protected ExtendedWebElement filterSubMenu;

  @FindBy( id = "cmdFieldFilter_text" )
  protected ExtendedWebElement contextMenuFilter;

  @FindBy( id = "RPT001filters" )
  protected ExtendedWebElement filterPanel;

  @FindBy( xpath = "//div[@class='gem-label' and text()='%s']" )
  protected ExtendedWebElement layoutItem;

  @FindBy( xpath = "//div[@class='sort' and text()='%s']" )
  protected ExtendedWebElement reportItem;

  @FindBy( css = "#refreshSummary .refresh-icon" )
  protected ExtendedWebElement reportRefreshIcon;

  @FindBy( xpath = "//div[@id='refreshSummary']//div[contains(text(),'{L10N:refreshing}')]" )
  protected ExtendedWebElement reportRefreshText;

  @FindBy( id = "progressPaneCancel" )
  protected ExtendedWebElement reportRefreshCancelBtn;

  // subtotals
  @FindBy( id = "PM:attrShowSub" )
  protected ExtendedWebElement btnAttrShowSub;

  @FindBy( id = "PM:measSubtotals" )
  protected ExtendedWebElement btnMeasSubtotals;

  @FindBy( id = "VF_showSum" )
  protected static ExtendedWebElement showSum;

  @FindBy( id = "VF_showAggregate" )
  protected static ExtendedWebElement showAggregate;

  @FindBy( id = "VF_showAverage" )
  protected static ExtendedWebElement showAverage;

  @FindBy( id = "VF_showMax" )
  protected static ExtendedWebElement showMax;

  @FindBy( id = "VF_showMin" )
  protected static ExtendedWebElement showMin;

  @FindBy( xpath = "//*[@id='standardDialog']//*[text()='{L10N:dlgNumberSubtotalsTitle}']" )
  protected ExtendedWebElement dlgTotalType;

  @FindBy( xpath = "//td[@id='PM:membEXCLUDE_text']/b[text()='%s']" )
  protected ExtendedWebElement exludeColumn;

  // filterRank
  @FindBy( id = "PM:attrFilterRank" )
  protected ExtendedWebElement attrFilterRank;

  @FindBy( id = "PM:measFilterRank" )
  protected ExtendedWebElement measFilterRank;

  @FindBy( id = "PM:measFilterCond" )
  protected ExtendedWebElement measFilterCond;

  public AnalyzerReportPage( WebDriver driver ) {
    this( driver, NEW_ANALYSIS_REPORT_NAME );
  }

  public AnalyzerReportPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  public void addFields( List<String> rows, List<String> columns, List<String> measures ) {
    addRows( rows );
    addColumns( columns );
    addMeasures( measures );
  }

  protected void addRows( List<String> rows ) {
    addRowsByDefault( rows );
  }

  protected void clearRows( List<String> rows ) {
    rows.clear();
  }

  protected void addColumns( List<String> columns ) {
    if ( columns != null ) {
      for ( String column : columns ) {
        fieldDragAndDrop( column, PanelItem.LAYOUT_COLUMNS );
      }
    }
  }

  protected void clearColumns( List<String> columns ) {
    columns.clear();
  }

  protected void addMeasures( List<String> measures ) {
    addMeasuresByDefault( measures );
  }

  protected void clearMeasures( List<String> measures ) {
    measures.clear();
  }

  public void addFieldsByDefault( List<String> fields ) {
    if ( fields != null ) {
      for ( String field : fields ) {
        doubleClick( format( EXPLICIT_TIMEOUT, fieldItem, field ) );
      }
    }
  }

  public void addRowsByDefault( List<String> fields ) {
    if ( fields != null ) {
      for ( String field : fields ) {
        doubleClick( format( EXPLICIT_TIMEOUT, fieldItemWhithoutMeasures, field ) );
      }
    }
  }

  public void addMeasuresByDefault( List<String> fields ) {
    if ( fields != null ) {
      for ( String field : fields ) {
        doubleClick( format( EXPLICIT_TIMEOUT, measureItem, field ) );
      }
    }
  }

  // Drill Through field by value
  public boolean chartDrillThrough( String value ) {

    doubleClick( format( axisItem, value ) );
    showFilterPanel();

    return isFilterApplied( value );
  }

  /**
   * Checks for the presence of the filter panel.
   * 
   * @return Returns true when the filter panel is present.
   */
  public boolean isFilterPanelPresent() {
    return isElementPresent( panelFilter, EXPLICIT_TIMEOUT / 10 );
  }

  /**
   * This method will show the filter panel by toggling the show/hide button in the toolbar if the filter panel is not
   * already present.
   */
  public void showFilterPanel() {
    showFilterPanel( false );
  }

  /**
   * This method will show the filter panel if the filter panel is not already present.
   * 
   * @param sendKeys
   *          Determines which method to toggle the filter panel. When true, it will use the key chord shortcut.
   *          Otherwise, it will toggle the button in the toolbar.
   */
  public void showFilterPanel( boolean sendKeys ) {
    if ( isFilterPanelPresent() ) {
      LOGGER.info( "Filter panel is already present. No action will be taken." );
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      LOGGER.info( "Sending keys to show filter panel." );
      sendKeysToReport( FILTER_BTN_SHORTCUT );
    } else {
      click( btnFiltersPanel, EXPLICIT_TIMEOUT / 10 );
    }

    if ( !isFilterPanelPresent() ) {
      CustomAssert.fail( "62514", "Filter panel did not appear!" );
      LOGGER.error( "Filter panel did not appear!" );
    }

    if ( isElementNotPresent( btnFiltersPanelTooltipVisible ) ) {
      CustomAssert.fail( "62514",
          "The show/hide filter button tooltip did not change to the correct value when the panel is visible!" );
      LOGGER.error(
          "The show/hide filter button tooltip did not change to the correct value when the panel is visible!" );
    }
  }

  /**
   * This method will hide the filter panel by toggling the show/hide button in the toolbar if the filter panel is not
   * already absent.
   */
  public void hideFilterPanel() {
    hideFilterPanel( false );
  }

  /**
   * This method will hide the filter panel if the filter panel is not already absent.
   * 
   * @param sendKeys
   *          Determines which method to toggle the filter panel. When true, it will use the key chord shortcut.
   *          Otherwise, it will toggle the button in the toolbar.
   */
  public void hideFilterPanel( boolean sendKeys ) {
    if ( !isFilterPanelPresent() ) {
      LOGGER.info( "The filter panel is already hidden. No action will be taken." );
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      LOGGER.info( "Sending keys to hide filter panel." );
      sendKeysToReport( FILTER_BTN_SHORTCUT );
    } else {
      click( btnFiltersPanel, EXPLICIT_TIMEOUT / 10 );
      pause( 1 );
    }

    if ( isFilterPanelPresent() ) {
      CustomAssert.fail( "62514", "Filter panel is present!" );
      LOGGER.error( "Filter panel is present!" );
    }

    if ( isElementNotPresent( btnFiltersPanelTooltipInvisible ) ) {
      CustomAssert.fail( "62514",
          "The show/hide filter button tooltip did not change to the correct value when the panel is hidden!" );
      LOGGER.error(
          "The show/hide filter button tooltip did not change to the correct value when the panel is hidden!" );
    }
  }

  public void refreshFilterPanel() {
    hideFilterPanel();
    showFilterPanel();
  }

  /**
   * Gets the height of the filter panel.
   * 
   * @return Returns the integer value of the height.
   */
  public int getFilterPanelHeight() {
    return getElementHeight( filterPanel );
  }

  private boolean isFilterApplied( String value ) {
    return isElementPresent( format( filterItem, value ) );
  }

  /**
   * Gets the tooltip of the show/hide filter accordion below the toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getFilterAccordionTooltip() {
    return accordionFilter.getAttribute( "title" );
  }

  /**
   * Gets the tooltip of the filter count text below the toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getFilterCountTooltip() {
    return filterCountText.getAttribute( "title" );
  }

  /**
   * Gets the tooltip for the help on filters button within the filter panel.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getFilterHelpTooltip() {
    return btnFilterHelp.getAttribute( "title" );
  }

  /**
   * Gets the tooltip for the hide filter pane button within the filter panel.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getFilterCloseTooltip() {
    return btnFilterClose.getAttribute( "title" );
  }

  public String getCubeName() {
    return textCubeName.getText();
  }

  /**
   * Gets the tooltip for the data source name in the available field panel.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getCubeTooltip() {
    return textCubeName.getAttribute( "title" );
  }

  public boolean isAvailableFieldsPanelPresent() {
    return isElementPresent( panelAvailableFields, EXPLICIT_TIMEOUT / 2 );
  }

  public boolean isLayoutPanelPresent() {
    return isElementPresent( layoutPanelWrapperElem, EXPLICIT_TIMEOUT / 2 );
  }

  public boolean isReportEmpty() {
    return isElementPresent( emptyReport );
  }

  /**
   * This method changes the view in the available fields pane to the specified view.
   * 
   * @param view
   *          An enumeration of which view to select. CATEGORY = "by Category" view. TYPE = "Measure - Level - Time"
   *          view. NAME = "A-Z" view. SCHEMA = "Schema" view. HIDDEN = "Show Hidden Fields" option.
   * @return Returns true when the visual cue (a checkbox next to the view name) is present for the specified view.
   */
  public boolean changeView( View view ) {
    if ( !isElementPresent( btnView ) ) {
      CustomAssert.fail( "41299", "'View' button is not present in the 'Available fields' panel!" );
    }

    click( btnView );

    ExtendedWebElement viewButton = lnkByCategory; // default View item

    switch ( view ) {
      case CATEGORY:
        viewButton = lnkByCategory;
        break;
      case TYPE:
        viewButton = lnkByType;
        break;
      case NAME:
        viewButton = lnkByName;
        break;
      case SCHEMA:
        viewButton = lnkBySchema;
        break;
      case HIDDEN:
        viewButton = lnkShowHiddenFields;
        break;
      default:
        Assert.fail( "Unsupported view schema trasfered: " + view );
        break;
    }

    if ( !isElementPresent( viewButton ) ) {
      Assert.fail( "'" + view + "' option is not present under 'View' popup!" );
    }

    click( viewButton );

    return isViewSelected( viewButton.getAttribute( "id" ) );
  }

  /**
   * This method determines whether or not the visual cue (a checkbox next to the view name) is present for the
   * specified view.
   * 
   * @param id
   *          The value of the id attribute for the view in the drop-down list.
   * @return Returns true when the checkbox is present next to the view name.
   */
  public boolean isViewSelected( String id ) {
    // Click the drop-down because the list has to be in view.
    click( btnView );

    Boolean isSelected = isElementPresent( format( lnkViewChecked, id ) );

    // [ML] Click away from the drop-down to close it. Selecting it again will close and re-open the drop-down.
    click( lblFindAavailableFields );

    return isSelected;
  }

  public void viewFilterSwitchToAZ() {
    click( viewFilterExpand );
    click( viewFilterAZ );

    click( viewFilterExpand );
    Assert.assertTrue( isElementPresent( viewFilterAZChecked ), "View Filter AZ is not checked" );
    click( viewFilterAZ );

  }

  // 1st parameter - field name to drag, 2nd parameter - layout to drop
  public void fieldDragAndDrop( String fieldDrag, PanelItem fieldDropTo ) {
    // layout to drop
    String dropRowLevel = "";
    String dropLevelHere = "dropZonePlaceholder_string";
    String dropMeasureHere = "dropZonePlaceholder_number";

    if ( fieldDropTo.equals( PanelItem.LAYOUT_ROWS ) ) {
      dropRowLevel = "//div[contains(@id,'rows_ui')]/div[contains (., '" + L10N.getText( dropLevelHere ) + "')]";
    } else if ( fieldDropTo.equals( PanelItem.LAYOUT_COLUMNS ) ) {
      dropRowLevel = "//div[contains(@id,'columns_ui')]/div[contains (., '" + L10N.getText( dropLevelHere ) + "')]";
    } else if ( fieldDropTo.equals( PanelItem.LAYOUT_MEASURES ) ) {
      dropRowLevel = "//div[contains(@id,'measures_ui')]/div[contains (., '" + L10N.getText( dropMeasureHere ) + "')]";
    } else {
      Assert.fail( "parameter fieldDropTo is not supported: " + fieldDropTo );
    }

    // field to drag
    String dragRowLevel = "//div[@id='fieldListTreeContent']//div[@dndtype][contains (., '" + fieldDrag + "')]";

    ExtendedWebElement listFields = findExtendedWebElement( By.xpath( dragRowLevel ) );
    ExtendedWebElement dropRows = findExtendedWebElement( By.xpath( dropRowLevel ) );

    Utils.dnd( listFields, dropRows );

    // Assert
    String assertIfDragged = "//div[@class='gem-label'][contains (., '" + fieldDrag + "')]";
    ExtendedWebElement addedRow = findExtendedWebElement( By.xpath( assertIfDragged ) );
    Assert.assertTrue( isElementPresent( addedRow ), "Drag and drop failed for the field " + fieldDrag );

  }

  protected Tree createTreeAvailableFields() {
    Tree tree = new Tree();
    TreeNode parentNode;
    String parentId = "";

    for ( ExtendedWebElement folderItem : fieldFolderItems ) {
      parentNode = tree.addNode( tree.getRoot(), new TreeNode( folderItem.getText() ) );
      parentId = folderItem.getElement().getAttribute( "id" );
      for ( ExtendedWebElement folderContentItem : fieldFolderContentItems ) {
        if ( folderContentItem.getAttribute( "id" ).contains( parentId ) ) {
          List<WebElement> fieldItems = getChildElements( folderContentItem, By.className( "field" ) );
          for ( WebElement fieldItem : fieldItems ) {
            tree.addNode( parentNode, new TreeNode( fieldItem.getText() ) );
          }

          break;
        }
      }
    }
    return tree;
  }

  public boolean isAvailableFieldFolderPresent( String folderName ) {
    return isElementPresent( format( folderAvailableField, folderName ) );
  }

  // TODO: Needs to create new method which will return List<ExtendedWebElement>
  private List<WebElement> getChildElements( ExtendedWebElement parentElement, By by ) {
    return parentElement.getElement().findElements( by );
  }

  /**
   * Change the report to the specified chart view.
   * 
   * @param type
   *          The chart type to switch to.
   * @return Returns an instance of the corresponding chart type's class. When ChartType.PIVOT is used, this method will
   *         return null.
   */
  public CommonChart changeChartType( ChartType type ) {
    CommonChart chart = null;

    if ( type == ChartType.PIVOT ) {
      changeToPivotView();
    } else {
      if ( !isElementPresent( cmdSelectChartTypeBtn ) ) {
        Assert.fail( "Button to switch a chart view is not present!" );
      }
      click( cmdSelectChartTypeBtn, EXPLICIT_TIMEOUT / 5 );
      pause( 1 );
      // [MG] Using JavascripExecutor as regular click fails intermittently and it is not due to timeout.
      executeJavaScript( "document.getElementById('" + type.getId() + "').click();" );
      // ( (JavascriptExecutor) driver ).executeScript( "document.getElementById('" + type.getId() + "').click();" );
      // pause( 1 );

      ChartFactory chartFactory = new ChartFactory();
      chart = (CommonChart) chartFactory.getChartVerifier( getDriver(), type );
    }

    return chart;
  }

  /**
   * Change the report to the pivot table view.
   */
  public void changeToPivotView() {
    // Do not click the button to switch to pivot view if pivot view is already selected. Otherwise, it will switch to a
    // chart view.
    if ( isElementNotPresent( cmdShowPivotBtnSelected, EXPLICIT_TIMEOUT / 15 ) ) {
      if ( isElementNotPresent( cmdShowPivotBtn ) ) {
        Assert.fail( "Button to switch a table view is not present!" );
      }
      click( cmdShowPivotBtn );
    } else {
      LOGGER.info( "The pivot view is already selected. The view will not be changed." );
    }
  }

  /**
   * Gets the tooltip for the switch to table button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getSwitchToTableTooltip() {
    return cmdShowPivotBtn.getAttribute( "title" );
  }

  /**
   * Gets the tooltip for the switch to chart button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getSwitchToChartTooltip() {
    return cmdShowChartBtn.getAttribute( "title" );
  }

  /**
   * Gets the tooltip for the select chart type drop-down in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getChooseChartTooltip() {
    return cmdSelectChartTypeBtn.getAttribute( "title" );
  }

  public boolean verifyDefaultChartImage( String className ) {
    return format( defaultChartImageElement, className ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  public boolean verifyGemBarLable( String label ) {
    return format( gemBarLabelElement, label ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  public boolean verifyGemBarMember( String gembarId, String memberName ) {
    return format( gemBarMemberElement, gembarId, memberName ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  public boolean verifyGemBarDropArea( String gembarId ) {
    return format( gemBarDropAreaElement, gembarId ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  /**
   * This method verifies that the gem bar drop area container's element has the correct class.
   * 
   * @param gemBar
   *          The gem bar object to verify.
   * @return Returns true when the gem bar drop area container's element has the correct class.
   */
  public boolean verifyGemBarDropContainer( GemBar gemBar ) {
    return isElementPresent( format( gemBarDropAreaContainer, gemBar.getType().getId() ) );
  }

  /**
   * This method verifies that the gem bar member text is correctly flagged (or not flagged) as required.
   * 
   * @param gemBar
   *          The gem bar object to verify.
   * @return Returns true when the gem bar member text is correctly flagged.
   */
  public boolean verifyGemBarMemberRequired( GemBar gemBar ) {
    return isElementPresent( formatGemBarElement( gemBarMemberRequired, gemBar, true ) );
  }

  /**
   * Gets the value of the content property for the specified gem bar's required class text.
   * 
   * @param gemBar
   *          The gem bar object to retrieve the content value from.
   * @return Returns the value of the content property.
   */
  public String getGemBarRequiredContent( GemBar gemBar ) {
    String content = getGemBarPropertyValue( gemBar, "content" );

    // The content property value will include the double quotes surrounding the string value, and should be removed.
    if ( content.length() >= 2 ) {
      content = content.substring( 1, content.length() - 1 );
    }

    return content;
  }

  /**
   * Gets the value of the color property for the specified gem bar's required class text.
   * 
   * @param gemBar
   *          The gem bar object to retrieve the color value from.
   * @return Returns the value of the color property.
   */
  public String getGemBarRequiredColor( GemBar gemBar ) {
    return getGemBarPropertyValue( gemBar, "color" );
  }

  /**
   * Gets the value of the specified property for the specified gem bar's required class text.
   * 
   * @param gemBar
   *          The gem bar object to retrieve the property value from.
   * @param property
   *          The name of the property to retrieve the value from.
   * @return Returns the value of the specified property.
   */
  private String getGemBarPropertyValue( GemBar gemBar, String property ) {
    WebElement element = formatGemBarElement( gemBarMemberText, gemBar, false ).getElement();

    return getPseudoAfterPropretyValue( element, property );
  }

  /**
   * Finds the element for the specified gem bar.
   * 
   * @param gemBarElement
   *          The gem bar element to find.
   * @param gemBar
   *          The gem bar object to find.
   * @param checkRequired
   *          Checks for the existence of the 'required' class
   * @return Returns the instance of the ExtendedWebElement.
   */
  private ExtendedWebElement formatGemBarElement( ExtendedWebElement gemBarElement, GemBar gemBar,
      boolean checkRequired ) {
    String label = gemBar.getLabel();
    String id = gemBar.getType().getId();
    String dndText = gemBar.getDndText();
    String isRequired = gemBar.isRequired() ? "" : "not";

    ExtendedWebElement element;

    if ( checkRequired ) {
      element = format( gemBarElement, label, id, isRequired, dndText );
    } else {
      element = format( gemBarElement, label, id, dndText );
    }

    return element;
  }

  /**
   * This method verifies that the correct icon exists for the specified gem bar.
   * 
   * @param gemBar
   *          The gem bar object to verify.
   * @return Returns true when the gem bar has the correct icon.
   */
  public boolean verifyGemBarIcon( GemBar gemBar ) {
    String label = gemBar.getLabel();
    String className = gemBar.getIconClassName();

    return isElementPresent( format( gemBarIcon, label, className ) );
  }

  public void disableAutoRefresh() {
    String classNames = btnRefresh.getAttribute( "class" );
    if ( !classNames.contains( "btnPressed" ) ) {
      click( btnRefresh );
    }
  }

  public void enableAutoRefresh() {
    String classNames = btnRefresh.getAttribute( "class" );
    if ( classNames.contains( "btnPressed" ) ) {
      click( btnRefresh );
    }
  }

  /**
   * Gets the tooltip for the auto refresh button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getAutoRefreshTooltip() {
    return btnRefreshIcon.getAttribute( "title" );
  }

  public boolean verifyRefreshingMsgBar() {
    return refreshMsgBarElement.isElementPresent() && refreshButtonElement.isElementPresent();
  }

  public void refreshReportBySpecialButton() {
    click( refreshButtonElement );
  }

  public boolean isPresentHeaderFieldForPivot( String fieldLabel ) {
    return format( pivotTableHeaderMember, fieldLabel ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isPresentFieldForPivot( String fieldLabel ) {
    return format( pivotTableMember, fieldLabel ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  public boolean verifyFilterPanel( String filterLabel ) {
    return panelFilter.isElementWithTextPresent( filterLabel, EXPLICIT_TIMEOUT / 5 );
  }

  public void addMemberIncludedFilter( String value ) {
    addMemberIncludedFilter( value, null );
  }

  public void addMemberIncludedFilter( String value, String columnName ) {
    ExtendedWebElement member;

    // If a column is not specified, then find the first instance of the value.
    // Otherwise, find the first instance of the value within the specified column.
    if ( Strings.isNullOrEmpty( columnName ) ) {
      member = format( pivotTableMember, value );
    } else {
      member = format( pivotTableField, columnName, value );
    }

    Actions clicker = new Actions( getDriver() );
    clicker.contextClick( member.getElement() ).perform();
    click( includeFilterElem );
  }

  public void removeMemberIncludedFilter() {
    click( removeFilterElem );
  }

  public List<ExtendedWebElement> getMembersFromPivot() {
    return pivotTableCells;
  }

  public void undo() {
    click( undoBtn );
    pause( EXPLICIT_TIMEOUT / 10 );
  }

  /**
   * Gets the tooltip of the undo button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getUndoTooltip() {
    return undoBtn.getAttribute( "title" );
  }

  public void redo() {
    click( redoBtn );
    pause( EXPLICIT_TIMEOUT / 10 );
  }

  /**
   * Gets the tooltip of the redo button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getRedoTooltip() {
    return redoBtn.getAttribute( "title" );
  }

  /**
   * Gets the tooltip of the more actions and options button in the report toolbar.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getMoreActionsTooltip() {
    return btnMoreActionsIcon.getAttribute( "title" );
  }

  public void resetColumnSizes() {
    click( btnMoreActions );
    pause( 1 );
    click( cmdResetColumnSize );
  }

  public void resetReport() {
    click( btnMoreActions );
    pause( 3 );
    click( resetBtn );
    loading();
  }

  public void exportAs( ExportType type ) {
    click( btnMoreActions );
    pause( 2 );
    click( exportMenuElem );
    switch ( type ) {
      case PDF:
        click( exportPdfMenuElem );
        click( exportBtnOnPopup );
        break;
      case CSV:
        click( exportCsvMenuElem );
        click( exportBtnOnPopup );
        break;
      case EXCEL:
        click( exportExcelMenuElem );
        click( exportBtnOnPopup );
        break;
      default:
        throw new IllegalArgumentException( "Used ansupported export type: " + type );
    }
    pause( 5 );
  }

  /**
   * This method resizes the specified panel by clicking and dragging the splitter by the specified width. A positive
   * byWidth will increase the size of the panel. A negative byWidth will decrease the size of the panel.
   * 
   * @param panel
   *          The panel to resize.
   * @param splitter
   *          The splitter that is use to click and drag to resize the panel.
   * @param byWidth
   *          The width to increase/decrease the panel by.
   * @return Returns true if the panel resized properly.
   */
  private boolean resizePanel( ExtendedWebElement panel, ExtendedWebElement splitter, int byWidth ) {
    int defaultWidth = getElementWidth( panel );
    dndByCoordinate( splitter, byWidth, 0 );
    int resizedWidth = getElementWidth( panel );

    boolean isValid;

    if ( byWidth > defaultWidth ) {
      isValid = resizedWidth > defaultWidth ? true : false;
    } else {
      isValid = resizedWidth < defaultWidth ? true : false;
    }

    return isValid;
  }

  /**
   * Increases the available field panel's width by 300 pixels.
   * 
   * @return Returns true if the available field panel resized properly.
   */
  public boolean resizeFieldPanel() {
    return resizeFieldPanel( 300 );
  }

  /**
   * Increases/decreases the available field panel's width by the specified number of pixels and verifies the
   * functionality. A positive byWidth will increase the size of the panel. A negative byWidth will decrease the size of
   * the panel. This method also performs additional custom asserts for specific test steps
   * 
   * @param byWidth
   *          The number of pixels to increase/decrease the width of the available field panel by.
   * @return Returns true if the available field panel resized properly.
   */
  public boolean resizeFieldPanel( int byWidth ) {
    Dimension originalFieldListSize = fieldListTree.getElement().getSize();
    Point originalViewDropDownLocation = btnView.getElement().getLocation();

    boolean isValid = resizePanel( fieldListWrapperElem, fieldListWrapperSplitterElem, byWidth );

    if ( byWidth > 0 ) {
      // Check that the view drop-down shifted to the right.
      if ( originalViewDropDownLocation.getX() >= btnView.getElement().getLocation().getX() ) {
        CustomAssert.fail( "62527", "The view drop-down did not shift to the right!" );
      }

      // Check that the field list tree increased in width.
      if ( originalFieldListSize.getWidth() >= fieldListTree.getElement().getSize().getWidth() ) {
        CustomAssert.fail( "62527", "The width of the available fields tree did not increase!" );
      }
    } else {
      // Check that the view drop-down shifted to the left.
      if ( originalViewDropDownLocation.getX() <= btnView.getElement().getLocation().getX() ) {
        CustomAssert.fail( "62529", "The view drop-down did not shift to the left!" );
      }

      // Check that the field list tree decreased in width.
      if ( originalFieldListSize.getWidth() <= fieldListTree.getElement().getSize().getWidth() ) {
        CustomAssert.fail( "62529", "The width of the available fields tree did not decrease!" );
      }
    }

    return isValid;
  }

  /**
   * Increases the layout panel's width by 300 pixels.
   * 
   * @return Returns true if the layout panel resized properly.
   */
  public boolean resizeLayoutPanel() {
    return resizeLayoutPanel( 300 );
  }

  /**
   * Increases/decreases the layout panel's width by the specified number of pixels and verifies the functionality. A
   * positive byWidth will increase the size of the panel. A negative byWidth will decrease the size of the panel. This
   * method also performs additional custom asserts for specific test steps.
   * 
   * @param byWidth
   *          The number of pixels to increase/decrease the width of the layout panel by.
   * @return Returns true if the layout panel resized properly.
   */
  public boolean resizeLayoutPanel( int byWidth ) {
    List<GemBar> gemBars = ChartType.PIVOT.getGemBars();
    List<Dimension> originalAreaSizes = new ArrayList<Dimension>();
    List<Point> originalLabelLocations = new ArrayList<Point>();
    Dimension originalReportOptionsBtnSize = getReportOptionsBtnSize();
    Point originalChartOptionsBtnLocation = reportOptionsButton.getElement().getLocation();

    // Retrieve the original sizes of the gem bars and their labels within the container.
    for ( GemBar gemBar : gemBars ) {
      originalAreaSizes.add( getGemBarDropAreaSize( gemBar ) );
      originalLabelLocations.add( getGemBarDropAreaLabelLocation( gemBar ) );
    }

    boolean isValid = resizePanel( layoutPanelWrapperElem, layoutPanelWrapperElemSplitterElem, byWidth );

    // Verify each gem bar's container width and label location.
    for ( int i = 0; i < gemBars.size(); i++ ) {
      GemBar gemBar = gemBars.get( i );
      Dimension originalAreaSize = originalAreaSizes.get( i );
      Dimension gemBarDropAreaSize = getGemBarDropAreaSize( gemBar );
      Point originalLabelLocation = originalLabelLocations.get( i );
      Point gemBarDropAreaLabelLocation = getGemBarDropAreaLabelLocation( gemBar );

      if ( byWidth > 0 ) {
        // Check that the width of the container increased.
        if ( originalAreaSize.getWidth() >= gemBarDropAreaSize.getWidth() ) {
          CustomAssert.fail( "62527", "The width of the gem bar '" + gemBar.getLabel() + "' did not increase!" );
        }

        // Check that the text within the container shifted to the right.
        if ( originalLabelLocation.getX() >= gemBarDropAreaLabelLocation.getX() ) {
          CustomAssert.fail( "62527", "The text within the gem bar '" + gemBar.getLabel()
              + "' did not shift to the right!" );
        }
      } else if ( byWidth < 0 ) {
        // Check that the width of the container decreased.
        if ( originalAreaSize.getWidth() <= gemBarDropAreaSize.getWidth() ) {
          CustomAssert.fail( "62528", "The width of the gem bar '" + gemBar.getLabel() + "' did not decrease!" );
        }

        // Check that the text within the container shifted to the left.
        if ( originalLabelLocation.getX() <= gemBarDropAreaLabelLocation.getX() ) {
          CustomAssert.fail( "62528", "The text within the gem bar '" + gemBar.getLabel()
              + "' did not shift to the left!" );
        }
      }
    }

    if ( byWidth > 0 ) {
      // Check that the chart options button shifted to the right.
      if ( originalChartOptionsBtnLocation.getX() >= reportOptionsButton.getElement().getLocation().getX() ) {
        CustomAssert.fail( "62527", "The chart options button did not shift to the right!" );
      }
    } else {
      // Check that the chart options button shifted to the left.
      if ( originalChartOptionsBtnLocation.getX() <= reportOptionsButton.getElement().getLocation().getX() ) {
        CustomAssert.fail( "62528", "The chart options button did not shift to the right!" );
      }
    }

    // Check that the width of the report options button is unchanged.
    if ( originalReportOptionsBtnSize.getWidth() != getReportOptionsBtnSize().getWidth() ) {
      CustomAssert.fail( "62528", "The report options button's width was incorrectly changed!" );
    }

    return isValid;
  }

  /**
   * Reduces the size of the web browser and verifies that various panels have properly resized.
   * 
   * @return Returns true when all panels resized properly.
   */
  public boolean resizeBrowser( int width, int height ) {
    boolean isValid = true;
    long timeout = EXPLICIT_TIMEOUT / 15;

    // Get the original widths of the panels.
    Dimension browserSize = getDriver().manage().window().getSize();
    Dimension fieldsPanelSize = panelAvailableFields.getElement().getSize();
    Dimension layoutPanelSize = layoutPanelWrapperElem.getElement().getSize();
    Dimension reportPanelSize = reportPanel.getElement().getSize();

    super.resizeBrowser( width, height );

    // Prevent verifying dimensions before the browser has resized.
    if ( isElementPresent( panelAvailableFields, timeout ) && isElementPresent( layoutPanelWrapperElem, timeout )
        && isElementPresent( reportPanel, timeout ) ) {

      // Verify that the width of the available fields and layout panels have not changed.
      if ( fieldsPanelSize.getWidth() != panelAvailableFields.getElement().getSize().getWidth() ) {
        isValid = false;
        LOGGER.error( "The available fields panel width was incorrectly changed after resizing the browser!" );
      }

      if ( layoutPanelSize.getWidth() != layoutPanelWrapperElem.getElement().getSize().getWidth() ) {
        isValid = false;
        LOGGER.error( "The layout panel width was inccorrectly changed after resizing the browser!" );
      }

      if ( width > browserSize.getWidth() ) {
        // Verify that the report panel's width has increased.
        if ( reportPanel.getElement().getSize().getWidth() <= reportPanelSize.getWidth() ) {
          isValid = false;
          LOGGER.error( "The report panel width did not increase after resizing the browser!" );
        }
      } else if ( width < browserSize.getWidth() ) {
        // Verify that the report panel's width has decreased.
        if ( reportPanel.getElement().getSize().getWidth() >= reportPanelSize.getWidth() ) {
          isValid = false;
          LOGGER.error( "The report panel width did not decrease after resizing the browser!" );
        }
      }

      if ( height > browserSize.getHeight() ) {
        // Verify that the height of the available fields, layout, and report panels have increased.
        if ( panelAvailableFields.getElement().getSize().getHeight() <= fieldsPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The available fields panel height did not increase after resizing the browser!" );
        }

        if ( layoutPanelWrapperElem.getElement().getSize().getHeight() <= layoutPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The layout panel height did not increase after resizing the browser!" );
        }

        if ( reportPanel.getElement().getSize().getHeight() <= reportPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The report panel height did not increase after resizing the browser!" );
        }
      } else {
        // Verify that the height of the available fields, layout, and report panels have decreased.
        if ( panelAvailableFields.getElement().getSize().getHeight() >= fieldsPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The available fields panel height did not decrease after resizing the browser!" );
        }

        if ( layoutPanelWrapperElem.getElement().getSize().getHeight() >= layoutPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The layout panel height did not decrease after resizing the browser!" );
        }

        if ( reportPanel.getElement().getSize().getHeight() >= reportPanelSize.getHeight() ) {
          isValid = false;
          LOGGER.error( "The report panel height did not decrease after resizing the browser!" );
        }
      }

      // JIRA# ANALYZER-2924: Verify that the position of the pivot table button does not overlap with the "more actions
      // and options" menu and that the Y position has not changed.
      if ( cmdShowPivotBtn.getElement().getLocation().getX() < btnMoreActions.getElement().getLocation().getX()
          + btnMoreActions.getElement().getSize().getWidth() ) {
        isValid = false;
        LOGGER.error(
            "The toolbar's 'switch to table format' button is overlapped with the 'more actions and options' button." );
      }
    }

    return isValid;
  }

  /**
   * Retrieves the size of the report options button in the layout panel.
   * 
   * @return Returns the Dimension object of the report options button.
   */
  public Dimension getReportOptionsBtnSize() {
    // [ML] Had to use getCssValue for width due to the decimal-value width rounding inconsistently with the getWidth
    // method.
    String strWidth = reportOptionsButton.getElement().getCssValue( "width" ).replace( "px", "" );

    int width = (int) Math.round( Double.parseDouble( strWidth ) );
    return new Dimension( width, reportOptionsButton.getElement().getSize().getHeight() );
  }

  /**
   * Determines whether or not the chart options button is present.
   * 
   * @return Returns true when the button is present.
   */
  public boolean isChartOptionsBtnPresent() {
    return isElementPresent( btnChartOptionsLayout, EXPLICIT_TIMEOUT / 15 );
  }

  /**
   * Retrieves the size of the chart options button in the layout panel.
   * 
   * @return Returns the Dimension object of the chart options button.
   */
  public Dimension getChartOptionsBtnSize() {
    return btnChartOptionsLayout.getElement().getSize();
  }

  /**
   * Retrieves the text displayed for the report options button.
   * 
   * @return Returns a String value of the displayed text.
   */
  public String getReportOptionsBtnText() {
    return reportOptionsButton.getText();
  }

  /**
   * Retrieve the size of the specified gem bar's drop area container.
   * 
   * @return Returns the Dimension object of the gem bar's drop area container.
   */
  protected Dimension getGemBarDropAreaSize( GemBar gemBar ) {
    return format( gemBarDropAreaContainer, gemBar.getType().getId() ).getElement().getSize();
  }

  /**
   * Retrieve the location of the specified gem bar's drop area text.
   * 
   * @return Returns the Point object of the gem bar's drop area text.
   */
  protected Point getGemBarDropAreaLabelLocation( GemBar gemBar ) {
    return format( gemBarDropAreaElement, gemBar.getType().getId() ).getElement().getLocation();
  }

  /**
   * Sets the position of the available field panel's horizontal scrollbar.
   * 
   * @param scrollPos
   *          The new position of the scrollbar. 0 = leftmost position.
   * @return
   */
  public boolean setFieldListHorizScrollPos( int scrollPos ) {
    int newPos;

    WebElement fieldList = fieldListTree.getElement();
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript( "return arguments[0].scrollLeft=" + scrollPos + ";", fieldList );

    newPos = getFieldListHorizScrollPos();

    return newPos == scrollPos;
  }

  /**
   * Gets the current position of the available field panel's horizontal scrollbar.
   * 
   * @return Returns the position of the horizontal scrollbar. 0 = leftmost position.
   */
  public int getFieldListHorizScrollPos() {
    WebElement fieldList = fieldListTree.getElement();
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    return Integer.parseInt( executor.executeScript( "return arguments[0].scrollLeft;", fieldList ).toString() );
  }

  /**
   * Sets the position of the available field panel's horizontal scrollbar to its leftmost position.
   * 
   * @return Returns the updated position of the horizontal scrollbar.
   */
  public void resetFieldListHorizScrollPos() {
    setFieldListHorizScrollPos( 0 );
  }

  public void verifyPresentingReportFields( List<String> fields ) {
    for ( String field : fields ) {
      if ( !isPresentHeaderFieldForPivot( field ) ) {
        fail( "The field '" + field + "' is not present in a report!" );
      }
    }
  }

  public void verifyNotPresentingReportFields( List<String> fields ) {
    for ( String field : fields ) {
      if ( isPresentHeaderFieldForPivot( field ) ) {
        fail( "The field '" + field + "' is present in a report!" );
      }
    }
  }

  public void verifyApplyFilter( String label ) {
    if ( !verifyFilterPanel( label ) ) {
      fail( "Filter area does not contain '" + label + "'!" );
    }
    if ( !isPresentFieldForPivot( label ) || getMembersFromPivot().size() != 1 ) {
      fail( "Pivot table has not filtered content!" );
    }
  }

  public void verifyRemovingFilter( String label ) {
    if ( verifyFilterPanel( label ) ) {
      fail( "Filter area contains '" + label + "'!" );
    }
    if ( getMembersFromPivot().size() == 1 ) {
      fail( "Pivot table should has not filtered content!" );
    }
  }

  protected ExtendedWebElement getFieldLayoutByString( String field ) {
    return ( format( layoutField, field ) );
  }

  public void removeAttribute( String field ) {
    if ( !isLayoutItemPresent( field ) ) {
      LOGGER.error( "Please make sure that calculated measure was specified is right!" );
    }
    openPopupMenuLayout( field );
    click( btnRemoveAttribute );
  }

  public Boolean isLayoutItemPresent( String field ) {
    ExtendedWebElement element;
    Boolean result = true;
    if ( !isElementPresent( element = ( format( layoutField, field ) ) ) ) {
      result = false;
      LOGGER.error( "'" + element + "' is not displayed!" );
    }
    return result;
  }

  public void moveItemInLayout( String item, PanelItem layout ) {
    switch ( layout ) {
      case LAYOUT_ROWS:
        Utils.dnd( ( format( layoutField, item ) ), rowsLayout );
        break;

      case LAYOUT_COLUMNS:
        Utils.dnd( ( format( layoutField, item ) ), columnsLayout );
        break;

      case LAYOUT_MEASURES:
        Utils.dnd( ( format( layoutField, item ) ), measuresLayout );
        break;

      default:
        LOGGER.error( "There is no specified type of layout!" );
        break;
    }
  }

  public Boolean isDlgAlertSameAxisOpened() {
    Boolean result = true;
    if ( !isElementPresent( dlgAlertSameAxis ) ) {
      result = false;
      LOGGER.error( "Dialog 'Alert' is not opened!" );
    }
    return result;
  }

  public void clickDlgBtnSave() {
    click( dlgBtnSave );
  }

  public void clickDlgBtnCancel() {
    click( dlgBtnCancel );
  }

  public void moveFieldToTrash( String field ) {
    Utils.dnd( format( layoutField, field ), trashCan );
  }

  public void dragFieldInAvailableFieldsList( String field ) {
    Utils.dnd( format( layoutField, field ), fieldListTree );
  }

  /**
   * This method builds a list of categories found in the available fields panel.
   * 
   * @return Returns a list of categories found in the available fields panel.
   */
  public List<String> getAvailableCategories() {
    List<String> categories = new ArrayList<String>();
    for ( ExtendedWebElement category : fieldFolderItems ) {
      categories.add( category.getText() );
    }
    return categories;
  }

  /**
   * This method will build a list of available fields separated by category. All fields in a given category are within
   * the same element and are delimited by \n.
   * 
   * @return Returns a list of the available fields in the available fields panel.
   */
  public List<String> getAvailableFieldsList() {
    // Fields do not have categories in the "A-Z" available fields view.
    List<ExtendedWebElement> elements =
        fieldFolderContentItems.size() > 0 ? fieldFolderContentItems : fieldsWithoutCategories;

    List<String> fields = new ArrayList<String>();
    for ( ExtendedWebElement field : elements ) {
      fields.add( field.getText() );
    }
    return fields;
  }

  /**
   * This method will build a list of fields that are available to be added to the report.
   * 
   * @return Returns a list of the available fields in the report.
   */
  public List<String> getAllAvailableFields() {
    return getAllAvailableFields( null );
  }

  /**
   * This method will build a list of fields, within a specified category, that are available to be added to the report.
   * 
   * @param category
   *          The name of the category that contains the fields. Using null will retrieve all fields within all
   *          categories.
   * @return Returns a list of fields available to be added to the report.
   */
  public List<String> getAllAvailableFields( String category ) {
    List<String> items = new ArrayList<String>();
    List<ExtendedWebElement> elements;

    if ( category == null ) {
      // Fields do not have categories in the "A-Z" available fields view.
      elements = getAvailableFieldElementList();
    } else {
      // Find the category.
      ExtendedWebElement element = format( fieldListCategory, category );

      // Find all fields within the category.
      elements =
          findExtendedWebElements( By.xpath( String.format( FIELD_ITEMS_BY_CATEGORY, element.getAttribute( "id" ) ) ) );
    }

    for ( ExtendedWebElement field : elements ) {
      // Only add the item to the list if it is not hidden from the search.
      String text = field.getText();
      if ( !Strings.isNullOrEmpty( text ) ) {
        items.add( text );
      }
    }

    return items;
  }

  public List<String> getAllMeasures() {
    return getAllAvailableFields( "Measures" );
  }

  /**
   * Gets a list of all available available fields excluding measures.
   * 
   * @return Returns a list of all available levels.
   */
  public List<String> getAllLevels() {
    List<String> levels = getAllAvailableFields();
    levels.removeAll( getAllMeasures() );

    return levels;
  }

  public void clearFieldFromLayout( List<String> sourceLayout, String field ) {
    if ( sourceLayout.contains( field ) ) {
      sourceLayout.remove( sourceLayout.indexOf( field ) );
    } else {
      LOGGER.error( "The field is not found in specified layout!" );
    }
  }

  public void clearAllFieldsFromLayout( List<String> sourceLayout ) {
    if ( !sourceLayout.isEmpty() ) {
      sourceLayout.clear();
    } else {
      LOGGER.info( "The field is not found in specified layout!" );
    }
  }

  /**
   * Gets the color of the specified measure in the layout panel when it is in its hovered state.
   * 
   * @param measureName
   *          The name of the measure in the layout panel.
   * @return Returns the RGBA value of the color as a String.
   */
  public String getLayoutMeasureHoverColor( String measureName ) {
    return getLayoutFieldHoverColor( measureName, layoutMeasure );
  }

  /**
   * Gets the color of the specified attribute field in the layout panel when it is in its hovered state.
   * 
   * @param fieldName
   *          The name of the attribute field in the layout panel.
   * @return Returns the RGBA value of the color as a String.
   */
  public String getLayoutAttributeHoverColor( String fieldName ) {
    return getLayoutFieldHoverColor( fieldName, layoutAttribute );
  }

  /**
   * Gets the color of the specified field in the layout panel when it is in its hovered state.
   * 
   * @param fieldName
   *          The name of the field in the layout panel.
   * @param element
   *          The element that contains the specified field.
   * @return Returns the RGBA value of the color as a String.
   */
  private String getLayoutFieldHoverColor( String fieldName, ExtendedWebElement element ) {
    ExtendedWebElement layoutElement = format( element, fieldName );
    hover( layoutElement );

    // [ML] Added pause to allow enough time for the action to complete before retrieving the color.
    pause( 1 );

    return getElementBackgroundColor( layoutElement );
  }

  /**
   * Gets the background color of the specified field in the available fields panel.
   * 
   * @param field
   *          The name of the field in the available fields list to retrieve the background color from.
   * @return Returns a String of the RGB value of the background color.
   */
  public String getAvailableFieldColor( String field ) {
    ExtendedWebElement element = getFieldFromAvailableListByString( field );
    pause( 1 );
    return getElementBackgroundColor( element );
  }

  /**
   * Gets the background color of the specified field in the layout panel.
   * 
   * @param field
   *          The name of the field in the layout panel to retrieve the background color from.
   * @return Returns a String of the RGB value of the background color.
   */
  public String getLayoutFieldColor( String field ) {
    ExtendedWebElement element = getFieldLayoutByString( field );
    pause( 3 );
    return getElementBackgroundColor( element );
  }

  protected ExtendedWebElement getFieldFromAvailableListByString( String field ) {
    return ( format( fieldItem, field ) );
  }

  public String getColorSelectedLayoutSetion( String field ) {
    Actions actions = new Actions( driver );
    actions.clickAndHold( getFieldLayoutByString( field ).getElement() );
    actions.moveByOffset( 1, 1 ).build().perform();
    pause( 3 );
    actions.moveByOffset( 1, 100 ).build().perform();
    pause( 3 );
    return columnsLayout.getElement().getCssValue( "background-color" );
  }

  public String getToolTipLayout( String field ) {
    ExtendedWebElement element = ( format( tooltipLayout, field ) );
    return element.getAttribute( "title" );
  }

  /**
   * Gets the tooltip for the specified available field in the available field panel.
   * 
   * @param field
   *          The name of the available field.
   * @return Returns the tooltip of the element.
   */
  public String getTooltipAvailableField( String field ) {
    return format( availableFieldItem, field ).getAttribute( "title" );
  }

  public List<ExtendedWebElement> getColumnsHeader() {
    return canvasColumnsHeader;
  }

  protected List<ExtendedWebElement> getListSizeColumns() {
    List<ExtendedWebElement> listSizes = new ArrayList<ExtendedWebElement>();
    listSizes.addAll( rowsColumnsSize );
    listSizes.addAll( measuresSize );
    return listSizes;
  }

  protected int getCurrentSizeColumn( List<ExtendedWebElement> columnsHeader, String column ) {
    String cssWidth = null;
    int subWidth = 0;

    // get list of columns from canvas and find specified column
    for ( int i = 0; i < getColumnsHeader().size(); i++ ) {
      if ( columnsHeader.get( i ).getText().equals( column ) ) {

        // get size of column according to name of list. rowsColumnsSize or measuresSize
        if ( getListSizeColumns().get( i ).getName().contains( "rows" ) ) {
          cssWidth = getListSizeColumns().get( i ).getAttribute( "width" );
          subWidth = Integer.parseInt( cssWidth.substring( 0, cssWidth.length() - 2 ) ); // ignore "px"
          break;
        }
        if ( getListSizeColumns().get( i ).getName().contains( "measure" ) ) {
          cssWidth = getListSizeColumns().get( i ).getAttribute( "width" );
          subWidth = Integer.parseInt( cssWidth );
          break;
        }

      } else {
        LOGGER.error( "The list of columns doesn't contain the specified column!" );
      }
    }
    return subWidth;
  }

  public void resizeColumnWidth( String column, HandleSide side, int pixels ) {

    // get current size of column
    int currentSizeColumn = getCurrentSizeColumn( getColumnsHeader(), column );
    ExtendedWebElement resizeHandle = getResizeHandleByColumnSide( column, side );

    Actions action = new Actions( driver );
    WebElement element = resizeHandle.getElement();

    // Resizing column handle with Javascript as a workaround due to inability to click on handle as element is too
    // small
    makeHandleClickable( getResizeHandleByColumnSide( column, side ), column );

    action.clickAndHold( element ).build().perform();
    action.moveToElement( element, element.getSize().getWidth() / 2, element.getSize().getHeight() / 2 ).perform();
    action.moveByOffset( pixels, 0 ).build().perform();
    currentSizeColumn = currentSizeColumn + pixels;
    action.release().perform();

    // reseting column handle to original size
    resetHandleStyle( getResizeHandleByColumnSide( column, side ), currentSizeColumn );
  }

  private void makeHandleClickable( ExtendedWebElement resizeHandle, String column ) {
    // the following method give option to escape JS events that contains webelement
    int currentSizeColumn = getCurrentSizeColumn( getColumnsHeader(), column );
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript( "document.getElementById('" + resizeHandle.getElement().getAttribute( "id" )
        + "').setAttribute('style', '" + resizeHandle.getAttribute( "style" ) + "; width: " + currentSizeColumn
        + "px; height: 21px;')" );
  }

  private void resetHandleStyle( ExtendedWebElement resizeHandle, int currentSizeColumn ) {
    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript( "document.getElementById('" + resizeHandle.getElement().getAttribute( "id" )
        + "').removeAttribute('style', '" + resizeHandle.getAttribute( "style" ) + "; width: " + currentSizeColumn
        + "px; height: 21px;')" );
  }

  public int verifyColumnWidth( String column ) {
    return getCurrentSizeColumn( getColumnsHeader(), column );
  }

  public ExtendedWebElement getResizeHandleByColumnSide( String column, HandleSide side ) {
    String handle = null;
    String columnName = null;
    ExtendedWebElement element = null;

    for ( int i = 0; i < getColumnsHeader().size(); i++ ) {
      if ( getColumnsHeader().get( i ).getText().equals( column ) ) {
        columnName = getColumnsHeader().get( i ).getText();
      } else {
        LOGGER.error( "The list of columns doesn't contain the specified column!" );
      }
    }

    List<ExtendedWebElement> list =
        findExtendedWebElements( By.xpath( "//td[@title='" + columnName + "']//child::div[@style]" ) );
    for ( int j = 0; j < list.size(); j++ ) {
      if ( list.get( j ).getAttribute( "style" ).contains( side.value ) ) {
        handle = list.get( j ).getAttribute( "id" );
        element = findExtendedWebElement( By.xpath( "//*[@id='" + handle + "']" ) );
      } else {
        LOGGER.error( "The column doesn't contains specified side!" );
      }
    }
    return element;
  }

  public enum HandleSide {
    RIGHT( "right" ),
    LEFT( "left" );
    private String value;

    private HandleSide( String value ) {
      this.value = value;
    }
  }

  public String changePartUrlOnParameter( String url ) {
    if ( !url.contains( "editor?" ) ) {
      LOGGER.error( "The URL doesn't contain part 'editor?' !" );
    }
    return url.replaceAll( "editor?", "parameter?" );
  }

  /**
   * This method will show the layout panel by toggling the show/hide button in the toolbar if the layout panel is not
   * already present.
   */
  public void showLayoutPanel() {
    showLayoutPanel( false );
  }

  /**
   * This method will show the layout panel if the layout panel is not already present.
   * 
   * @param sendKeys
   *          Determines which method to toggle the layout panel. When true, it will use the key chord shortcut.
   *          Otherwise, it will toggle the button in the toolbar.
   */
  public void showLayoutPanel( boolean sendKeys ) {
    if ( isLayoutPanelPresent() ) {
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      sendKeysToReport( LAYOUT_BTN_SHORTCUT );
    } else {
      click( btnLayoutPanel );
    }

    if ( !isLayoutPanelPresent() ) {
      CustomAssert.fail( "62513", "Layout panel did not appear!" );
      LOGGER.error( "Layout panel did not appear!" );
    }

    if ( isElementNotPresent( btnLayoutPanelTooltipVisible ) ) {
      CustomAssert.fail( "62513",
          "The show/hide layout panel button tooltip did not change to the correct value when the panel is visible!" );
      LOGGER.error(
          "The show/hide layout panel button tooltip did not change to the correct value when the panel is visible!" );
    }
  }

  /**
   * This method will hide the layout panel by toggling the show/hide button in the toolbar if the layout panel is not
   * already present.
   */
  public void hideLayoutPanel() {
    hideLayoutPanel( false );
  }

  /**
   * This method will hide the layout panel if the layout panel is not already present.
   * 
   * @param sendKeys
   *          Determines which method to toggle the layout panel. When true, it will use the key chord shortcut.
   *          Otherwise, it will toggle the button in the toolbar.
   */
  public void hideLayoutPanel( boolean sendKeys ) {
    if ( !isLayoutPanelPresent() ) {
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      sendKeysToReport( LAYOUT_BTN_SHORTCUT );
    } else {
      click( btnLayoutPanel );
    }

    if ( isLayoutPanelPresent() ) {
      CustomAssert.fail( "62513", "Layout panel is still present!" );
      LOGGER.error( "Layout panel is still present!" );
    }

    if ( isElementNotPresent( btnLayoutPanelTooltipInvisible ) ) {
      CustomAssert.fail( "62513",
          "The show/hide layout panel button tooltip did not change to the correct value when the panel is hidden!" );
      LOGGER.error(
          "The show/hide layout panel button tooltip did not change to the correct value when the panel is hidden!" );
    }
  }

  /**
   * This method will show the available fields panel by toggling the show/hide button in the toolbar if the available
   * fields panel is not already present.
   */
  public void showAvailableFieldsPanel() {
    showAvailableFieldsPanel( false );
  }

  /**
   * This method will show the available fields panel if the available fields panel is not already present.
   * 
   * @param sendKeys
   *          Determines which method to toggle the available fields panel. When true, it will use the key chord
   *          shortcut. Otherwise, it will toggle the button in the toolbar.
   */
  public void showAvailableFieldsPanel( boolean sendKeys ) {
    if ( isAvailableFieldsPanelPresent() ) {
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      sendKeysToReport( AVAILABLE_FIELDS_BTN_SHORTCUT );
    } else {
      click( btnFieldsPanel );
    }

    if ( !isAvailableFieldsPanelPresent() ) {
      CustomAssert.fail( "62512", "Available fields panel did not appear!" );
      LOGGER.error( "Available fields panel did not appear!" );
    }

    if ( isElementNotPresent( btnFieldsPanelTooltipVisible ) ) {
      CustomAssert.fail( "62512",
          "The show/hide available fields button tooltip did not change to the correct value when the panel is visible!" );
      LOGGER.error(
          "The show/hide available fields button tooltip did not change to the correct value when the panel is visible!" );
    }
  }

  /**
   * This method will hide the available fields panel by toggling the show/hide button in the toolbar if the available
   * fields panel is not already present.
   */
  public void hideAvailableFieldsPanel() {
    hideAvailableFieldsPanel( false );
  }

  /**
   * This method will hide the available fields panel if the available fields panel is not already present.
   * 
   * @param sendKeys
   *          Determines which method to toggle the available fields panel. When true, it will use the key chord
   *          shortcut. Otherwise, it will toggle the button in the toolbar.
   */
  public void hideAvailableFieldsPanel( boolean sendKeys ) {
    if ( !isAvailableFieldsPanelPresent() ) {
      return;
    }

    // use the shortcut keys or click to toggle the button.
    if ( sendKeys ) {
      sendKeysToReport( AVAILABLE_FIELDS_BTN_SHORTCUT );
    } else {
      click( btnFieldsPanel );
    }

    if ( isAvailableFieldsPanelPresent() ) {
      CustomAssert.fail( "62512", "Available fields panel is still present!" );
      LOGGER.error( "Available fields panel is still present!" );
    }

    if ( isElementNotPresent( btnFieldsPanelTooltipInvisible ) ) {
      CustomAssert.fail( "62512",
          "The show/hide available fields button tooltip did not change to the correct value when the panel is hidden!" );
      LOGGER.error(
          "The show/hide available fields button tooltip did not change to the correct value when the panel is hidden!" );
    }
  }

  /**
   * This method will replicate pressing keys, which can be used to toggle shortcut buttons in the report (such as
   * showing/hiding the filter panel).
   * 
   * @param charSequences
   *          The keys to be sent to the report.
   */
  private void sendKeysToReport( CharSequence charSequences ) {
    Actions action = new Actions( getDriver() );
    action.sendKeys( charSequences ).perform();
    pause( 1 ); // Pause is needed for some actions because verification sometimes happens before this action.
  }

  public boolean isDefaultViewValid() {
    boolean isValid = true;

    LOGGER.info( "Checking for new report defaults..." );

    if ( !isAvailableFieldsPanelPresent() ) {
      isValid = false;
      LOGGER.error( "The available fields list is not present!" );
    }

    if ( !isElementPresent( cmdShowPivotBtnSelected ) ) {
      isValid = false;
      LOGGER.error( "The table view is not selected!" );
    }

    if ( !isReportEmpty() ) {
      isValid = false;
      LOGGER.error( "The report is not empty!" );
    }

    if ( !isEmptyReportImagePresent( ChartType.PIVOT ) ) {
      isValid = false;
      LOGGER.error( "The report area image is incorrect or missing!" );
    }

    if ( isElementNotPresent( emptyReportDragFieldText ) ) {
      isValid = false;
      LOGGER.error(
          "The report area is missing default text for an empty report! Expected text: 'Drag an available field to the required layout zones.'" );
    }

    if ( isElementPresent( emptyReportAsteriskText ) ) {
      // Retrieve the expected and actual text, which includes the inner HTML.
      String actualText = emptyReportAsteriskText.getAttribute( "innerHTML" );
      String expectedText = L10N.getText( "emptyVizHelp" );

      // Parse the text and color from the inner HTML on both the locale text and the actual text.
      String parsedTextExpected = Utils.parseTextFromInnerHtml( expectedText, "span" );
      String parsedTextActual = Utils.parseTextFromInnerHtml( actualText, "span" );
      Colour expectedColor = Colour.parseColorFromProperty( expectedText );
      Colour actualColor = Colour.parseColorFromProperty( actualText );

      if ( !parsedTextExpected.equals( parsedTextActual ) ) {
        isValid = false;
        LOGGER.error(
            "The report area's default text for an empty report is incorrect! Expected text: '(Marked with a red asterisk in the layout panel.)'" );
      }

      if ( !expectedColor.equals( actualColor ) ) {
        isValid = false;
        LOGGER.error( "The report area's default text is not red for the 'red asterisk' text!" );
      }

    } else {
      isValid = false;
      LOGGER.error(
          "The report area is missing default text an empty report!. Expected text: '(Marked with a red asterisk in the layout panel.)'" );
    }

    if ( !isViewSelected( lnkByCategory.getAttribute( "id" ) ) ) {
      isValid = false;
      LOGGER.error( "The 'by Category' view is not selected for the available fields panel!" );
    }

    // [ML] Using WebElement.getSize().getWidth() was returning a value that including padding for one of the elements.
    // Had to use .getCssValue instead.
    int layoutWidth =
        Integer.parseInt( layoutPanelWrapperElem.getElement().getCssValue( "width" ).replace( "px", "" ) );
    int availableFieldsWidth =
        Integer.parseInt( panelAvailableFields.getElement().getCssValue( "width" ).replace( "px", "" ) );

    if ( layoutWidth != availableFieldsWidth ) {
      isValid = false;
      LOGGER.error( "The layout panel's width does not match the available field panel's width!" );
    }

    if ( isElementNotPresent( layoutPanelLayoutTitleArrow ) ) {
      isValid = false;
      LOGGER.error( "The layout panel's 'layout' title bar does not have a collapsible/expandable arrow!" );
    }

    if ( isElementNotPresent( layoutPanelPropertiesTitleArrow ) ) {
      isValid = false;
      LOGGER.error( "The layout panel's 'properties' title bar does not have a collapsible/expandable arrow!" );
    }

    LOGGER.info( "Finished checking for new report defaults." );

    return isValid;
  }

  public void addFieldToSection( GemBarType type, String field ) {
    Utils.dnd( getFieldFromAvailableListByString( field ), getLayoutSectionByString( type.getId() ) );
  }

  /**
   * Drags and drops one available field to another in an attempt to change the order of the fields. This functionality
   * should not actually change the order of the fields. This is used to make sure it does not.
   * 
   * @param fieldName
   *          The field to drag and drop.
   * @param destFieldName
   *          The destination field to drag and drop to.
   */
  public void reorderAvailableField( String fieldName, String destFieldName ) {
    Utils.dnd( getFieldFromAvailableListByString( fieldName ), getFieldFromAvailableListByString( destFieldName ) );
  }

  public ExtendedWebElement getLayoutSectionByString( String field ) {
    return ( format( layoutSection, field ) );
  }

  /**
   * Gets a list of all measures that were added to the layout panel.
   * 
   * @return Returns a list of Strings of the measure fields.
   */
  public List<String> getLayoutMeasures() {
    return getElementListText( layoutMeasures );
  }

  /**
   * Gets a list of all attribute fields that were added to the layout panel.
   * 
   * @return Returns a list of Strings of the attribute fields.
   */
  public List<String> getLayoutAttributes() {
    return getElementListText( layoutAttributes );
  }

  /**
   * Gets a list of all fields that were added to the layout panel.
   * 
   * @return Returns a list of Strings for all fields added to the layout panel.
   */
  public List<String> getLayoutFields() {
    return getElementListText( layoutFields );
  }

  public void openDlgMeasureRankRunningSum( String field ) {
    openPopupMenuLayout( field );
    click( userDefinedMeasure );
    click( rankRunningSum );
  }

  /**
   * Right-clicks on the specified level within the layout panel, and then selects the "Edit" option in the context
   * menu. The "Edit" option is only available for an level's context menu, and is unavailable for measures.
   * 
   * @param levelName
   *          The name of the level in the layout panel.
   */
  public void openEditLevelDialog( String levelName ) {
    LOGGER.info( "Opening the level edit dialog for the level '" + levelName + "'." );
    openPopupMenuLayout( levelName );
    click( levelContextMenuEdit );
  }

  /**
   * Determines whether or not the field edit dialog is present for the specified field.
   * 
   * @param levelName
   *          The name of the field that the edit dialog was opened for.
   * @return Returns true when the field edit dialog is present.
   */
  public boolean isLevelEditDialogPresent( String levelName ) {
    long timeout = EXPLICIT_TIMEOUT / 10;
    return isElementPresent( format( timeout, dlgLevelEditTitle, levelName ), timeout );
  }

  /**
   * Sets the display name in the field edit dialog to the specified text.
   * 
   * @param displayName
   *          The text that will replace the display name.
   */
  public void setLevelEditDialogDisplayName( String displayName ) {
    txtDisplayName.type( displayName );
  }

  /**
   * Clicks the OK button used in dialogs. This button is different from the OK button specified in the FilePage class.
   */
  public void clickOkBtn() {
    click( btnOK );
  }

  public void openPopupMenuLayout( String field ) {
    if ( isElementPresent( getFieldLayoutByString( field ), EXPLICIT_TIMEOUT / 2  )) {
      getFieldLayoutByString( field ).rightClick();
    } else {
      Assert.fail( "The specified field did not find!" );
    }
  }

  public boolean isDlgRankRunningSumOpened() {
    return isElementPresent( dlgRankRunningSum );
  }

  public void addCalculatedMeasurePercentOfField() {
    if ( isElementNotPresent( btnPercentOfMeasure ) ) {
      LOGGER.error( "The button " + btnPercentOfMeasure + " is not found!" );
    }
    click( btnPercentOfMeasure );
    if ( isElementNotPresent( dlgBtnNext ) ) {
      LOGGER.error( "The button " + dlgBtnNext + " is not found!" );
    }
    click( dlgBtnNext );
    if ( isElementNotPresent( dlgBtnSave ) ) {
      LOGGER.error( "The button " + dlgBtnSave + " is not found!" );
    }
    click( dlgBtnSave );
  }

  public List<String> getColumnsValues() {
    List<String> stringValues = new ArrayList<String>();
    for ( ExtendedWebElement values : columnsValues ) {
      stringValues.add( values.getText() );
    }
    return stringValues;
  }

  public void sortHighLow() {
    click( sortHighToLow );
  }

  public void sortLowHigh() {
    click( sortLowToHigh );
  }

  public void openReportOptions() {
    click( btnMoreActions );
    pause( 1 );
    click( btnReportOptionsMoreMenu );
  }

  public void applyAttributeFilter( String attributeName, List<String> filterValuesList ) {
    getAvailableFieldItem( attributeName ).rightClick();
    format( contextMenuItem, L10N.getText( "popupMenuFilter" ) ).click();

    analyzerFilterPage = new AnalyzerFilterPage( getDriver() );
    analyzerFilterPage.addItem( filterValuesList );

    btnOK.click();
  }

  public ExtendedWebElement getAvailableFieldItem( String fieldName ) {
    return format( availableFieldItem, fieldName );
  }

  public void openLog() {
    btnMoreActions.click();
    btnAdministration.click();
    btnAdminLog.click();
  }

  public String getMDX() {
    return mdxQuery.getText();
  }

  public void openDlgCreateCalculateMeasure( String field ) {
    openPopupMenuLayout( field );
    click( userDefinedMeasure );
    click( popupMenuCreateCalculatedMeasure );
  }

  public void moveLayoutFieldToSection( GemBarType type, String field ) {
    Utils.dnd( getFieldLayoutByString( field ), getLayoutSectionByString( type.getId() ) );
  }

  public void openDlgChartOptions() {
    click( btnChartOptionsLayout );
  }

  /**
   * Retrieves the text displayed for the chart options button.
   * 
   * @return Returns a String value of the displayed text.
   */
  public String getChartOptionsBtnText() {
    return btnChartOptionsLayout.getText();
  }

  public boolean isDlgChartOptionsOpened() {
    return isElementPresent( dlgChartOptions, EXPLICIT_TIMEOUT / 10 );
  }

  public boolean isSizedByAbsoluteOptionApplied( String dottedValues ) {
    pause( 5 );
    boolean result = false;
    for ( ExtendedWebElement values : canvasValues ) {
      if ( !values.getElement().getCssValue( "stroke" ).equals( dottedValues ) ) {
        LOGGER.info( "Negative values is shown on the canvas without a dotted line!" );
      } else {
        result = true;
        break;
      }
    }
    return result;
  }

  public void openXML() {
    click( btnMoreActions );
    click( btnAdministration, EXPLICIT_TIMEOUT / 10 );
    pause( 3 );
    click( cmdReportXml, EXPLICIT_TIMEOUT / 10 );
  }

  /**
   * This method verifies the XML attributes of chartOptions tag in the Administrator > XML dialog
   * 
   * @param softAssert
   *          the SoftAssert that will be "appended" via this method. Passing null will result in a new SoftAssert.
   * @param expectedAttributeValues
   *          HashMap<String,String> of expected attribute->value pairs
   * @return boolean
   */
  public boolean verifyChartOptionsXML( SoftAssert softAssert, HashMap<String, String> expectedAttributeValues ) {
    openXML();

    Utils.validateXMLTagAttributes( softAssert, "chartOptions", reportDefinition.getAttribute( "value" ),
        expectedAttributeValues );

    clickDlgBtnSave();

    return false;
  }

  public boolean verifyNegativeOptionInXML( String option ) {
    boolean result = true;
    if ( !reportDefinition.getAttribute( "value" ).contains( option ) ) {
      result = false;
      LOGGER.error( "The specified option did not found in XML file!" );
    }
    return result;
  }

  public List<String> getSizeCanvasValues() {
    List<String> sizes = new ArrayList<String>();
    if ( !canvasValues.isEmpty() ) {
      // read list of values on the canvas
      for ( int i = 0; i < canvasValues.size(); i++ ) {
        // verify that webelement contains the attribute radius
        if ( !( canvasValues.get( i ).getAttribute( "r" ) == null ) ) {
          sizes.add( canvasValues.get( i ).getAttribute( "r" ) );
        }
        // verify that webelement contains the attribute diameter
        else if ( !( canvasValues.get( i ).getAttribute( "d" ) == null ) ) {
          sizes.add( canvasValues.get( i ).getAttribute( "d" ) );
        }
      }
    }
    return sizes;
  }

  public void editCaclulatedMeasure( String field ) {
    if ( !isLayoutItemPresent( field ) ) {
      LOGGER.error( "Please make sure that calculated measure was specified is right!" );
    }
    openPopupMenuLayout( field );
    click( userDefinedMeasure );
    click( btnEditMeasure );
  }

  public void removeMeasure( String field ) {
    if ( !isLayoutItemPresent( field ) ) {
      LOGGER.error( "Please make sure that calculated measure was specified is right!" );
    }
    openPopupMenuLayout( field );
    click( btnRemoveMeasure );
  }

  /**
   * This method verifies the presence of all menu items within the "more actions and options" toolbar menu.
   * 
   * @return Returns a list of elements, with their locators, that were not found.
   */

  public List<String> verifyMoreActionsMenu() {
    List<String> missingElements = new ArrayList<String>();

    ExtendedWebElement[] mainMenuElements =
        { exportMenuElem, btnAboutReport, btnReportOptionsMoreMenu, btnChartOptionsMoreMenu, resetBtn,
          cmdResetColumnSize, btnAdministration };

    ExtendedWebElement[] exportElements = { exportPdfMenuElem, exportCsvMenuElem, exportExcelMenuElem };

    ExtendedWebElement[] adminElements = { btnAdminXml, btnAdminLog, btnAdminMdx, btnAdminClearCache };

    // Verify the presence of each item in the root of the more actions and options menu.
    click( btnMoreActions );
    missingElements.addAll( verifyElementsPresent( mainMenuElements ) );

    // Verify the presence of each item in the export sub-menu.
    hover( exportMenuElem );
    missingElements.addAll( verifyElementsPresent( exportElements ) );

    // Verify the presence of each item in the administration sub-menu.
    hover( btnAdministration );
    missingElements.addAll( verifyElementsPresent( adminElements ) );

    return missingElements;
  }

  /**
   * This method verifies that the array of elements are present. Any missing elements are added to the missingElements
   * list.
   * 
   * @param elements
   *          An array of web elements to verify their presence.
   * @return Returns a list of elements, with their locators, that were not found.
   */

  private List<String> verifyElementsPresent( ExtendedWebElement[] elements ) {
    List<String> missingElements = new ArrayList<String>();

    for ( ExtendedWebElement element : elements ) {
      String nameWithLocator = element.getNameWithLocator();

      if ( isElementNotPresent( element ) ) {
        LOGGER.error( "Failed to find element: " + nameWithLocator );
        missingElements.add( nameWithLocator );
      } else {
        LOGGER.info( "Found element: " + nameWithLocator );
      }
    }
    return missingElements;
  }

  public AnalyzerFilterPage addFilter( String fieldName, Workflow workflow ) {
    Actions clicker_context;
    switch ( workflow ) {
      case CONTEXT_LAYOUT:
        clicker_context = new Actions( driver );
        if ( isElementPresent( format( layoutItem, fieldName ), EXPLICIT_TIMEOUT / 2 ) ) {
          clicker_context.contextClick( format( layoutItem, fieldName ).getElement() ).perform();
        } else {
          Assert.fail( "The specified element " + format( layoutItem, fieldName ) + " doesn't exist!" );
        }
        click( filterSubMenu, true );

        break;
      case CONTEXT_PANEL:
        clicker_context = new Actions( driver );
        if ( isElementPresent( format( fieldItemWhithoutMeasures, fieldName ), EXPLICIT_TIMEOUT / 2 ) ) {
          clicker_context.contextClick( format( fieldItemWhithoutMeasures, fieldName ).getElement() ).perform();
        } else {
          Assert.fail( "The specified element " + format( fieldItemWhithoutMeasures, fieldName ) + " doesn't exist!" );
        }
        click( contextMenuFilter, true );

        break;

      case CONTEXT_REPORT:
        rightClickColumn( fieldName );
        if ( isElementPresent( filterSubMenu, EXPLICIT_TIMEOUT / 2 ) ) {
          click( filterSubMenu, true );
        } else {
          Assert.fail( "The specified element " + filterSubMenu + " doesn't exist!" );
        }

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

    return new AnalyzerFilterPage( getDriver() );
  }

  public void rightClickColumn( String columnName ) {
    if ( !isColumnOnWorkspace( columnName ) ) {
      LOGGER.error( "Field " + columnName
          + " is not added to the columns on Workspace please it before running this method!" );
      return;
    }

    Actions clicker = new Actions( driver );
    clicker.contextClick( format( reportItem, columnName ).getElement() ).perform();
  }

  public boolean isColumnOnWorkspace( String columnName ) {
    return isElementPresent( format( reportItem, columnName ), EXPLICIT_TIMEOUT / 5 );
  }

  public CalculatedMeasurePage addCalculatedMeasure( String field ) {
    openPopupMenuLayout( field );
    click( userDefinedMeasure );
    click( popupMenuCreateCalculatedMeasure );
    return new CalculatedMeasurePage( getDriver() );
  }

  public CalculatedMeasurePage editCalculatedMeasure( PAMeasure paMeasure ) {
    editCaclulatedMeasure( paMeasure.getName() );
    return new CalculatedMeasurePage( getDriver() );
  }

  public CalculatedMeasurePage verifyCalculatedMeasure( String name ) {
    editCaclulatedMeasure( name );
    return new CalculatedMeasurePage( getDriver() );
  }

  /**
   * Collapses all categories in the available field list. Any category that is already collapsed will be skipped.
   * 
   * @return Returns true if all categories successfully collapsed.
   */
  public boolean collapseCategory( String category ) {
    return accordionCategory( category, false );
  }

  /**
   * Expands all categories in the available field list. Any category that is already expanded will be skipped.
   * 
   * @return Returns true if all categories successfully expanded.
   */
  public boolean expandCategory( String category ) {
    return accordionCategory( category, true );
  }

  /**
   * Expands or collapses the specified category in the available field list. No action will be taken if the category is
   * already in the specified state.
   * 
   * @param expand
   *          Determines whether to expand the categories or to collapse them. When true, the categories will be
   *          expanded.
   * @return Returns true if all categories successfully expanded or collapsed.
   */
  private boolean accordionCategory( String category, boolean expand ) {
    final String EXPANDED = "arrow_open.png";
    final String COLLAPSED = "arrow_closed.png";

    boolean isValid = true;
    String action;
    String imageBefore;
    String imageAfter;

    // Set up the action and image names based on the expand argument.
    if ( expand ) {
      action = "expand";
      imageBefore = COLLAPSED;
      imageAfter = EXPANDED;
    } else {
      action = "collapse";
      imageBefore = EXPANDED;
      imageAfter = COLLAPSED;
    }

    ExtendedWebElement categoryElement = format( fieldListCategory, category );
    List<String> fields = getAllAvailableFields( category );

    if ( isElementPresent( categoryElement ) ) {
      String image = categoryElement.getElement().getCssValue( "background-image" );

      // Only perform the action if it is not already expanded/collapsed.
      if ( image.contains( imageBefore ) ) {
        if ( ( fields.size() > 0 && !expand ) || ( fields.size() == 0 && expand ) ) {
          LOGGER.info( "Attempting to " + action + " category '" + categoryElement.getText() + "'." );
          click( categoryElement );

          // Get the updated accordion image and available fields for the category.
          image = categoryElement.getElement().getCssValue( "background-image" );
          fields = getAllAvailableFields( category );

          // Verify the correct background image exists for the element.
          if ( !image.contains( imageAfter ) || ( ( fields.size() > 0 && !expand ) || ( fields.size() == 0
              && expand ) ) ) {
            isValid = false;
            LOGGER.error( "Failed to " + action + " category '" + categoryElement.getText() + "'!" );
          }
        } else {
          if ( expand ) {
            LOGGER.error( "Fields were found within the category '" + category
                + "'! The category may already be expanded!" );
          } else {
            LOGGER.error( "No fields were found within the category '" + category
                + "'! The category may already be collapsed!" );
          }

          isValid = false;
        }
      } else {
        LOGGER.error( "The expected image for the accordion was not found!" );
        isValid = false;
      }
    } else {
      LOGGER.error( "The category '" + category + "' was not found!" );
      isValid = false;
    }

    return isValid;
  }

  /**
   * Inputs the specified text into the search fields textbox in the available field panel.
   * 
   * @param textToFind
   *          The text to input into the search fields textbox.
   * @return Returns the updated list of available fields after the search.
   */
  public List<String> searchFields( String textToFind ) {
    searchBox.type( textToFind );

    return getAllAvailableFields();
  }

  /**
   * Clears the search textbox in the available fields list using the clear search button.
   */
  public void clearSearch() {
    click( clearSearchBtn );
  }

  /**
   * Gets the tooltip for the clear search button.
   * 
   * @return Returns the tooltip of the element.
   */
  public String getClearSearchTooltip() {
    return clearSearchBtn.getAttribute( "title" );
  }

  /**
   * Determines whether or not the button that cancels the report refresh is present.
   * 
   * @param timeout
   *          The timeout in seconds.
   * @return Returns true if the element is found.
   */
  public boolean isReportRefreshCancelPresent() {
    return isElementPresent( reportRefreshCancelBtn, EXPLICIT_TIMEOUT / 15 );
  }

  /**
   * Determines whether or not the report refresh icon is present.
   * 
   * @param timeout
   *          The timeout in seconds.
   * @return Returns true if the element is found.
   */
  public boolean isReportRefreshIconPresent() {
    return isElementPresent( reportRefreshIcon, EXPLICIT_TIMEOUT / 15 );
  }

  /**
   * Determines whether or not the report refresh text is present.
   * 
   * @param timeout
   *          The timeout in seconds.
   * @return Returns true if the element is found.
   */
  public boolean isReportRefreshTextPresent() {
    return isElementPresent( reportRefreshText, EXPLICIT_TIMEOUT / 15 );
  }

  /**
   * Finds the correct list of available field elements depending on whether or not categories exist in the available
   * fields panel.
   * 
   * @return Returns the list of ExtendedWebElements that represent the list of available fields.
   */
  protected List<ExtendedWebElement> getAvailableFieldElementList() {
    return fieldFieldItems.size() > 0 ? fieldFieldItems : fieldsWithoutCategories;
  }

  public boolean isDlgTotalTypeOpened() {
    return isElementPresent( dlgTotalType );
  }

  public void setMeasureSubtotal( String field, WorkArea workarea, TotalType type ) {
    openPopupMenu( field, workarea );
    btnMeasSubtotals.click();
    checkTotalType( type );
    clickDlgBtnSave();
  }

  public enum WorkArea {
    LAYOUT_PANEL,
    CANVAS;
  }

  public enum TotalType {
    SUM( showSum ),
    AGGREGATE( showAggregate ),
    AVERAGE( showAverage ),
    MAX( showMax ),
    MIN( showMin );

    ExtendedWebElement element;

    private TotalType( ExtendedWebElement element ) {
      this.element = element;
    }
  }

  protected void checkTotalType( TotalType type ) {
    if ( !type.element.isChecked() ) {
      type.element.check();
    }
  }

  protected void uncheckTotalType( TotalType type ) {
    if ( type.element.isChecked() ) {
      type.element.uncheck();
    }
  }

  public void openPopupMenuCanvas( String column ) {
    ExtendedWebElement element = getCanvasColumnHeaderByString( column );
    element.rightClick();
  }

  public void openPopupMenu( String field, WorkArea workarea ) {
    switch ( workarea ) {
      case LAYOUT_PANEL:
        openPopupMenuLayout( field );
        break;

      case CANVAS:
        openPopupMenuCanvas( field );
        break;

      default:
        Assert.fail( "Unsupported workflow type is selected: " + workarea );
        break;
    }
  }

  public ExtendedWebElement getCanvasColumnHeaderByString( String column ) {
    return format( canvasColumnHeader, column );
  }

  public void openDlgNumericFilter( String field, WorkArea workarea ) {
    openPopupMenu( field, workarea );
    if ( isElementPresent( attrFilterRank, EXPLICIT_TIMEOUT / 60 ) ) {
      attrFilterRank.click();
    } else if ( isElementPresent( measFilterRank, EXPLICIT_TIMEOUT / 60 ) ) {
      measFilterRank.click();
    } else {
      Assert.fail(
          "Impossible click on the button 'Top 10, etc...'. Please verify that the following button is displayed!" );
    }
  }

  public String getResizeIndex( String column ) {
    ExtendedWebElement index = format( canvasColumnResizeIndex, column );
    return index.getAttribute( "resizeindex" );
  }

  public List<String> getCanvasValuesForColumn( String column ) {
    List<ExtendedWebElement> columnValues =
        findExtendedWebElements( By.xpath( "//td[@colindex='" + getResizeIndex( column ) + "']/child::div" ) );
    List<String> columnValuesString = new ArrayList<String>();
    for ( ExtendedWebElement webElement : columnValues ) {
      columnValuesString.add( webElement.getName() );
    }
    return columnValuesString;
  }

  /**
   * Determines whether or not the image for an empty report is displayed in the report panel.
   * 
   * @param chartType
   *          The chart type that is currently selected for the report.
   * @return Returns true when the image is present.
   */
  public boolean isEmptyReportImagePresent( ChartType chartType ) {
    boolean isPresent;

    // Check if the element is present.
    if ( isElementPresent( emptyReportImage ) ) {
      // Check that the image element has the correct image.
      isPresent =
          emptyReportImage.getElement().getCssValue( "background-image" ).contains( chartType.getImageFileName() );
    } else {
      isPresent = false;
    }

    return isPresent;
  }

  /**
   * Determines whether or not the "Drag an available field to the required layout zones." text is present.
   * 
   * @return Returns true when the text is present.
   */
  public boolean isEmptyReportDragFieldTextPresent() {
    return isElementPresent( emptyReportDragFieldText );
  }

  /**
   * Determines whether or not the "(Marked with a red asterisk in the layout panel.)" text is present.
   * 
   * @return Returns true when the text is present.
   */
  public boolean isEmptyReportAsteriskTextPresent() {
    return isElementPresent( emptyReportAsteriskText );
  }

  /**
   * Gets the value of the text-align property of the specified column in the pivot table.
   * 
   * @param columnName
   *          The name of the column in the pivot table.
   * @return Returns the value of the text-align property value.
   */
  public String getColumnHeaderTextAlign( String columnName ) {
    // This is returning "start", which is the initial value. Check for "start" or "left".
    return format( pivotTableColumnText, columnName ).getElement().getCssValue( "text-align" );
  }

  /**
   * Determines whether or not the specified column name exists in the pivot table.
   * 
   * @param columnName
   *          The name of the pivot table column to find.
   * @return Returns true when the column is found in the pivot table.
   */
  public boolean isColumnHeaderPresent( String columnName ) {
    return isElementPresent( format( pivotTableColumnText, columnName ) );
  }

  /**
   * Gets the value of the cursor property of the specified column in the pivot table.
   * 
   * @param columnName
   *          The name of the column in the pivot table.
   * @return Returns the value of the cursor property value.
   */
  public String getColumnHeaderCursor( String columnName ) {
    return format( pivotTableColumnText, columnName ).getElement().getCssValue( "cursor" );
  }

  /**
   * Gets a list of the values in the pivot table for the specified row header.
   * 
   * @param rowHeader
   *          The row header to retrieve the values from.
   * @return Returns a list of the row header's values.
   */
  public List<String> getPivotTableRowValues( String rowHeader ) {
    List<String> elementsText = new ArrayList<String>();
    int retryCount = 5;

    for ( int i = 0; i < retryCount; i++ ) {
      try {
        // Use this instead of the @FindBy annotation due to the need for a dynamic columnName value.
        List<ExtendedWebElement> pivotTableValuesByRowHeader =
            findExtendedWebElements( By.xpath( "//*[@id='pivotTable']//*[@type='member' and contains(@formula,'.["
                + rowHeader + "]')]//div[string-length(text())>0]" ) );

        elementsText = getElementListText( pivotTableValuesByRowHeader );
      } catch ( StaleElementReferenceException staleException ) {
        // Log a warning if retrying. Otherwise, throw the exception.
        if ( i + 1 < retryCount ) {
          LOGGER.warn( "Stale element found when retrieving pivot table values. Retrying..." );
        } else {
          LOGGER.error( "Unable to recover from stale element exception." );
          throw staleException;
        }
      }
    }

    return elementsText;
  }

  public boolean verifyColumnElementsContainsText( String column, String values ) {
    boolean result = false;
    List<String> valuesList = Arrays.asList( values.split( ";" ) );
    List<ExtendedWebElement> list =
        findExtendedWebElements( By.xpath( "//td[contains(@formula, '" + column + "')]/div" ) );
    if ( list.size() > 0 ) {
      for ( int i = 0 + 1; i < list.size(); i++ ) {
        result = false;
        for ( String value : valuesList ) {
          if ( list.get( i ).getElement().getText().toLowerCase().contains( value ) ) {
            result = true;
            break;
          }
        }
        if ( result == false ) {
          return false;
        }
      }
    } else {
      verifyNoDataMessage();
      return true;
    }
    return result;
  }

  /**
   * Gets a list of all values for all measures in the report.
   * 
   * @return Returns a String list of the measures' values.
   */
  public List<String> getPivotTableAllMeasureValues() {
    return getElementListText( pivotTableMeasureValues );
  }

  public boolean verifyColumnElementsNotContainsText( String column, String values ) {
    boolean result = false;
    List<String> valuesList = Arrays.asList( values.split( ";" ) );
    List<ExtendedWebElement> list =
        findExtendedWebElements( By.xpath( "//td[contains(@formula, '" + column + "')]/div" ) );
    if ( list.size() > 0 ) {
      for ( int i = 0 + 1; i < list.size(); i++ ) {
        result = false;
        for ( String value : valuesList ) {
          if ( !list.get( i ).getElement().getText().toLowerCase().contains( value ) ) {
            result = true;
            break;
          }
        }
        if ( result == false ) {
          return false;
        }
      }
    } else {
      verifyNoDataMessage();
      return true;
    }
    return result;
  }

  /**
   * Drags an available field to the specified gem bar container without dropping it.
   * 
   * @param fieldName
   *          The name of the available field.
   * @param gemBar
   *          The gem bar to hover the field over.
   */
  public void dragAvailableFieldToGemBar( String fieldName, GemBar gemBar ) {
    dragFieldToGemBar( getAvailableFieldItem( fieldName ), gemBar );
  }

  /**
   * Drags a layout field to the specified gem bar container without dropping it.
   * 
   * @param fieldName
   *          The name of the available field.
   * @param gemBar
   *          The gem bar to hover the field over.
   */
  public void dragLayoutFieldToGemBar( String fieldName, GemBar gemBar ) {
    dragFieldToGemBar( format( layoutField, fieldName ), gemBar );
  }

  /**
   * Drags a field to the specified gem bar container without dropping it.
   * 
   * @param fieldElement
   *          The ExtendedWebElement object of the field.
   * @param gemBar
   *          The gem bar to hover the field over.
   */
  private void dragFieldToGemBar( ExtendedWebElement fieldElement, GemBar gemBar ) {
    ExtendedWebElement gemBarContainer = format( gemBarDropAreaElement, gemBar.getType().getId() );
    Utils.dragToElement( fieldElement, gemBarContainer );
  }

  /**
   * Drags a field from the specified gem bar to another field within the same gem bar without dropping the field.
   * 
   * @param fieldName
   *          The name of the layout field to move.
   * @param gemBar
   *          The gem bar that contains the field.
   * @param destinationFieldName
   *          The destination field to move the origin field to.
   */
  public void dragLayoutFieldToGemBar( String fieldName, GemBar gemBar, String destinationFieldName ) {
    dragLayoutFieldToGemBar( fieldName, gemBar, destinationFieldName, false );
  }

  /**
   * Drags a field from the specified gem bar to another field within the same gem bar without dropping the field.
   * 
   * @param fieldName
   *          The name of the layout field to move.
   * @param gemBar
   *          The gem bar that contains the field.
   * @param destinationFieldName
   *          The destination field to move the origin field to.
   * @param insertAboveDestination
   *          Determine where to move the row fieldName to: above or below the destination field. When true, fieldName
   *          will be moved above the destination field.
   */
  public void dragLayoutFieldToGemBar( String fieldName, GemBar gemBar, String destinationFieldName,
      boolean insertAboveDestination ) {
    ExtendedWebElement layoutField = format( this.layoutField, fieldName );
    ExtendedWebElement destination;
    int xOffset = 0;
    int yOffset = 0;

    // If no destination field is specified, then append the field to the end of the layout section.
    // Otherwise, insert the field above or below the destination field.
    if ( Strings.isNullOrEmpty( destinationFieldName ) ) {
      destination = format( gemBarDropAreaElement, gemBar.getType().getId() );
    } else {
      destination = format( this.layoutField, destinationFieldName );

      // Offset the destination by half the height of the destination field to insert below it rather than above it.
      if ( !insertAboveDestination ) {
        yOffset = destination.getElement().getSize().getHeight() / 2;
      }
    }

    Utils.dragToElement( layoutField, destination, xOffset, yOffset );
  }

  /**
   * Retrieves the RGB value of the background color for the specified gem bar.
   * 
   * @param gemBar
   *          The gem bar to retrieve the background color of.
   * @return Returns a String that contains the RGB value of the background color.
   */
  public String getGemBarBackgroundColor( GemBar gemBar ) {
    ExtendedWebElement gemBarContainer = format( gemBarDropAreaContainer, gemBar.getType().getId() );
    return getElementBackgroundColor( gemBarContainer );
  }

  /**
   * Retrieves a list of all fields within the specified gem bar.
   * 
   * @param gemBar
   *          The gem bar that contains the fields.
   * @return Returns a list of String values that represent each field within the gem bar.
   */
  public List<String> getGemBarFields( GemBar gemBar ) {
    List<ExtendedWebElement> gemBarFields =
        findExtendedWebElements( By.xpath( String.format( LAYOUT_GEM_BAR_FIELDS, gemBar.getType().getId() ) ) );

    return getElementListText( gemBarFields );
  }

  /**
   * Determines whether or not the drop indicator is present. The drop indicator is the horizontal line that appears
   * when changing the order of fields within a layout panel's gem bar.
   * 
   * @param gemBar
   *          The gem bar that should contain the drop indicator.
   * @return Returns true when the drop indicator is present.
   */
  public boolean isGemBarDropIndicatorPresent( GemBar gemBar ) {
    int timeout = (int) EXPLICIT_TIMEOUT / 10;

    return isElementPresent( format( timeout, gemBarDropIndicator, gemBar.getType().getId() ), timeout );
  }

  public void verifyNoDataMessage() {
    if ( !isElementPresent( noDataMessage, EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "No data message not present!" );
    }
  }

  public void excludeColumn( String name ) {
    format( tebleElementWithText, name ).rightClick();
    format( exludeColumn, name ).click();
  }

  /**
   * Hovers the mouse over the specified field in the available fields panel.
   * 
   * @param fieldName
   *          The name of the available field.
   */
  public void hoverAvailableField( String fieldName ) {
    hover( getFieldFromAvailableListByString( fieldName ) );
  }

  /**
   * Hovers the mouse over the specified field in the layout panel.
   * 
   * @param fieldName
   *          The name of the field.
   */
  public void hoverLayoutField( String fieldName ) {
    hover( getFieldLayoutByString( fieldName ) );
  }

  public void clickShowChartBtn() {
    click( cmdShowChartBtn );
  }

  public ChartOptionsPage openChartOptions() {
    click( btnMoreActions );
    click( optionChartOptions );
    return new ChartOptionsPage( getDriver(), dlgChartOptions );
  }

  /**
   * The edit content button is unavailable for analyzer reports.
   */
  @Override
  public void clickEditContentButton() {
    Assert.fail( "The edit content button is not available for analyzer reports." );
  }
}
