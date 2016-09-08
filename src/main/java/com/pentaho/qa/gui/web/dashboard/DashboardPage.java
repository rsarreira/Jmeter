package com.pentaho.qa.gui.web.dashboard;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.dashboard.DashboardResizeHandle.ResizeAxis;
import com.pentaho.qa.gui.web.puc.FilePage;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DashboardPage extends FilePage {
  private final String DASHBOARD_WIDGET_XPATH = "//*[contains(@id,'Panel_') and contains(@class,'widgetContainer')]";

  @FindBy( xpath = "//div[@class='pentaho-titled-toolbar-label' and text()='Browse']" )
  protected ExtendedWebElement lblBrowse;

  @FindBy( xpath = "//*[@id='solutionTree']//*[@role='treeitem']//*[text()='%s']" )
  protected ExtendedWebElement folder;

  // TODO: See if there is a better way to identify the folder container. The container currently uses the name of the
  // folder, which is not the same as what is displayed in the GUI. Ex: Pentaho Operations Mart is displayed, but the
  // container has pentaho-operations-mart.
  @FindBy(
      xpath = "//*[@id='solutionTree']//*[@role='treeitem']//*[text()='%s']//ancestor::*[contains(@class,'leaf-widget')][1]" )
  protected ExtendedWebElement folderContainer;

  @FindBy( xpath = "//img[contains(@class,'icon-refresh') and contains(@src,'spacer.gif')]" )
  protected ExtendedWebElement btnRefresh;

  @FindBy( xpath = "//div[@class='pentaho-titled-toolbar-label' and text()='Files']" )
  protected ExtendedWebElement lblFiles;

  @FindBy( xpath = "//*[@id='filesListPanel']//*[text()='%s']" )
  protected ExtendedWebElement file;

  @FindBy( name = "frame_0" )
  protected ExtendedWebElement dashboardFrame;

  @FindBy( id = "content-menu" )
  protected ExtendedWebElement contentMenuFrame;

  @FindBy( id = "titlebar" )
  protected ExtendedWebElement lblTitle;

  // Dashboard Items
  @FindBy( xpath = "//*[@id='widget-area']//*[contains(@id,'vbox') or contains(@id,'hbox')]" )
  protected List<ExtendedWebElement> dashboardLayoutContainers;

  @FindBy( xpath = "//*[@id='widget-area']//*[@id='%s']" )
  protected ExtendedWebElement dashboardLayoutContainer;

  @FindBy( xpath = DASHBOARD_WIDGET_XPATH )
  protected List<ExtendedWebElement> dashboardWidgets;

  @FindBy( xpath = "//*[@id='%s' and contains(@class,'widgetContainer')]" )
  protected ExtendedWebElement dashboardWidget;

  @FindBy( xpath = "//*[@id='%s' and contains(@class,'widgetContainer')]//*[contains(@class,'wgtHead')]" )
  protected ExtendedWebElement lblDashboardWidgetTitle;

  @FindBy( xpath = "//*[@id='%s' and contains(@class,'widgetContainer')]//*[@class='insert-btn']" )
  protected ExtendedWebElement btnDashboardWidgetInsert;

  @FindBy( id = "dropdown-menu-item-PentahoChartComponent" )
  protected ExtendedWebElement mnuInsertChart;

  @FindBy( id = "dropdown-menu-item-PentahoGridComponent" )
  protected ExtendedWebElement mnuInsertDataTable;

  @FindBy( id = "dropdown-menu-item-PentahoUrlComponent" )
  protected ExtendedWebElement mnuInsertUrl;

  @FindBy( id = "file_component_lbl" )
  protected ExtendedWebElement mnuInsertFile;

  @FindBy( xpath = "//*[@id='%s' and contains(@class,'widgetContainer')]//*[@class='edit-btn']" )
  protected ExtendedWebElement btnWidgetEdit;

  @FindBy( css = ".pentaho-dialog" )
  protected ExtendedWebElement dialogWindow;

  // The element contains "'" in French
  private static final String WEBSITE_ADDRESS = L10N.generateConcatForXPath( L10N.getText( "website_address" ) );
  @FindBy(
      xpath = "//*[@class='pentaho-dialog']//*[contains(text(),%s)]//..//..//following-sibling::*//input[@class='gwt-TextBox']" )
  protected ExtendedWebElement inputWebAddress;

  @FindBy( xpath = "//*[@class='pentaho-dialog']//*[text()='{L10N:warning.title}']" )
  protected ExtendedWebElement lblWarning;

  @FindBy( xpath = "//*[@class='pentaho-dialog']//*[text()='{L10N:discard_content}']" )
  protected ExtendedWebElement lblDiscardContent;

  // Parent elements for ok button are not used because this button would not be found by the same identifiers.
  @FindBy( xpath = "//button[text()='{L10N:okButton}']" )
  protected ExtendedWebElement btnOk;

  // Parent element for cancel button is not used because this button would not be found by the same identifiers.
  @FindBy( xpath = "//button[text()='{L10N:cancelButton}']" )
  protected ExtendedWebElement btnCancel;

  // Objects Panel
  @FindBy( id = "objectTree" )
  protected ExtendedWebElement pnlObjects;

  @FindBy( id = "objects_lbl" )
  protected ExtendedWebElement pnlObjectsTitle;

  @FindBy( id = "dashboarditem" )
  protected ExtendedWebElement lblGeneralSettings;

  @FindBy( id = "povpanelitem" )
  protected ExtendedWebElement lblPrompts;

  @FindBy( xpath = "//*[contains(@id,'widgetitem')]//*[text()='%s']" )
  protected ExtendedWebElement lblWidgetSettings;

  // General Settings
  @FindBy( id = "span-tab-layout" )
  protected ExtendedWebElement tabTemplates;

  @FindBy( id = "span-tab-theme" )
  protected ExtendedWebElement tabThemes;

  @FindBy( id = "span-tab-dashboard-settings" )
  protected ExtendedWebElement tabProperties;

  // General Settings - Templates Tab
  @FindBy( xpath = "//*[@id='templates-table']//*[@class='pickBoxSelected']" )
  protected ExtendedWebElement selectedTemplate;

  @FindBy( xpath = "//*[@id='templates-table']//*[@class='pickBoxSelected' or @class='pickBox']" )
  protected List<ExtendedWebElement> templates;

  @FindBy(
      xpath = "//*[@id='templates-table']//*[text()='%s']//ancestor::*[@class='pickBoxSelected' or @class='pickBox']" )
  protected ExtendedWebElement template;

  // General Settings - Themes Tab
  @FindBy( xpath = "//*[@id='themes-table']//*[@class='pickBoxSelected']" )
  protected ExtendedWebElement selectedTheme;

  @FindBy( xpath = "//*[@id='themes-table']//*[@class='pickBoxSelected' or @class='pickBox']" )
  protected List<ExtendedWebElement> themes;

  @FindBy(
      xpath = "//*[@id='themes-table']//*[text()='%s']//ancestor::*[@class='pickBoxSelected' or @class='pickBox']" )
  protected ExtendedWebElement theme;

  // General Settings - Properties Tab
  @FindBy( id = "dashboard-title-edit" )
  protected ExtendedWebElement inputDashboardTitle;

  @FindBy( id = "dashboard-refresh-edit" )
  protected ExtendedWebElement inputRefreshInterval;

  @FindBy( id = "resize_panels_lbl" )
  protected ExtendedWebElement btnResizePanels;

  // JIRA PDB-1919: This button is not localized. The L10N key "closeButton" will be used for now to allow for failures
  // until this is fixed. The key may need to be changed if dev doesn't use this key.
  @FindBy( xpath = "//*[@id='resize-exit-control']//button[text()='{L10N:closeButton}']" )
  protected ExtendedWebElement btnCloseResizePanels;

  @FindBy( css = "#resize-wrapper .resizeHandle" )
  protected List<ExtendedWebElement> dashboardResizeHandles;

  @FindBy( xpath = "//*[@item1='%s' and @item2='%s' and contains(@class,'resizeHandle')]" )
  protected ExtendedWebElement dashboardResizeHandle;

  // Prompts
  @FindBy( css = "#showPovToolbar input" )
  protected ExtendedWebElement chkShowPromptToolbar;

  @FindBy( id = "povTableMoveSelectedUp" )
  protected ExtendedWebElement btnMovePromptUp;

  @FindBy( id = "povTableMoveSelectedDown" )
  protected ExtendedWebElement btnMovePromptDown;

  @FindBy( id = "povTableEditSelected" )
  protected ExtendedWebElement btnEditPrompt;

  @FindBy( id = "povTableAdd" )
  protected ExtendedWebElement btnAddPrompt;

  @FindBy( id = "povTableRemoveSelected" )
  protected ExtendedWebElement btnRemovePrompt;

  @FindBy( xpath = "//*[@id='povTable']//*[@class='gwt-HTML' and text()='%s']" )
  protected ExtendedWebElement lblPromptName;

  // Widget Settings
  @FindBy( id = "addParametersToWidgetTitle" )
  protected ExtendedWebElement btnAddParametersToTitle;

  @FindBy( xpath = "//*[@id='tabbox']//*[text()='{L10N:propPanelParametersTabLabel}']" )
  protected ExtendedWebElement tabParameters;

  @FindBy(
      xpath = "//*[@id='propertiesPanelParameterAssignmentTable']//*[text()='%s']//..//following-sibling::*//div[@class='combo-arrow']" )
  protected ExtendedWebElement inputParameterSource;

  @FindBy( xpath = "//*[@class='drop-popup']//*[contains(text(),'%s - %s')]" )
  protected ExtendedWebElement inputParameterSourceValue;

  @FindBy( xpath = "//*[@id='tabbox']//*[text()='{L10N:ContentLinking.TabLabel}']" )
  protected ExtendedWebElement tabContentLinking;

  @FindBy(
      xpath = "//*[@id='contentLinkingAssignmentTable']//*[text()='%s']/../preceding-sibling::*//input[@type='checkbox']" )
  protected ExtendedWebElement chkContentLinking;

  @FindBy( xpath = "//*[text()='{L10N:applyButton}']//parent::button" )
  protected ExtendedWebElement btnApply;

  public DashboardPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  public DashboardPage( WebDriver driver ) {
    super( driver, NEW_DASHBOARD_NAME );
  }

  /**
   * Switches the active frame to the iframe that contains the dashboard and its settings panel.
   */
  private void switchToDashboardFrame() {
    switchToFrame( dashboardFrame );
  }

  /**
   * Switches the active frame to the iframe that contains the content menu that opens when clicking the insert content
   * button in a dashboard widget.
   */
  private void switchToContentMenuFrame() {
    switchToFrame( contentMenuFrame );
  }

  /**
   * Determines whether or not the browse section's label is present.
   * 
   * @return Returns true when the label is present.
   */
  public boolean isBrowseLabelPresent() {
    switchToDefault();
    return lblBrowse.isElementPresent();
  }

  /**
   * Determines whether or not the browse section's refresh button is present.
   * 
   * @return Returns true when the button is present.
   */
  public boolean isBrowseRefreshButtonPresent() {
    switchToDefault();
    return btnRefresh.isElementPresent();
  }

  /**
   * Determines whether or not the files section's label is present.
   * 
   * @return Returns true when the label is present.
   */
  public boolean isFilesLabelPresent() {
    switchToDefault();
    return lblFiles.isElementPresent();
  }

  /**
   * Retrieves the text of the dashboard Title.
   * 
   * @return Returns a String that contains the dashboard's title.
   */
  public String getDashboardTitle() {
    switchToDashboardFrame();
    return lblTitle.getText();
  }

  /**
   * Clicks the general settings label in the objects panel.
   */
  public void clickGeneralSettings() {
    switchToDashboardFrame();
    lblGeneralSettings.click();
  }

  /**
   * Clicks the templates tab within the general settings.
   */
  public void clickTemplatesTab() {
    switchToDashboardFrame();
    tabTemplates.click();
  }

  /**
   * Gets a list of all template names.
   * 
   * @return Returns List<String> that contains the template names.
   */
  public List<String> getTemplateNames() {
    return getElementListText( templates );
  }

  /**
   * Clicks the specified template to change the layout of the dashboard.
   * 
   * @param dashboardTemplate
   *          The template to select.
   */
  public void clickTemplate( DashboardTemplate dashboardTemplate ) {
    format( template, dashboardTemplate ).click();
  }

  /**
   * Clicks the themes tab within the general settings.
   */
  public void clickThemesTab() {
    switchToDashboardFrame();
    tabThemes.click();
  }

  /**
   * Gets a list of all theme names.
   * 
   * @return Returns List<String> that contains all theme names.
   */
  public List<String> getThemeNames() {
    return getElementListText( themes );
  }

  /**
   * Clicks the specified theme to change the aesthetics of the dashboard.
   * 
   * @param dashboardTheme
   *          The theme to select.
   */
  public void clickTheme( DashboardTheme dashboardTheme ) {
    format( theme, dashboardTheme ).click();
  }

  /**
   * Clicks the properties tab within the general settings.
   */
  public void clickPropertiesTab() {
    switchToDashboardFrame();
    tabProperties.click();
  }

  /**
   * Sets the title of the dashboard page.
   * 
   * @param title
   *          The new title of the page.
   */
  public void setPageTitle( String title ) {
    inputDashboardTitle.type( title );
  }

  /**
   * Clicks the resize panels button in the properties tab of the general settings.
   */
  public void clickResizePanels() {
    btnResizePanels.click();
  }

  /**
   * Clicks the close button from the resize panels view;
   */
  public void clickCloseResizePanels() {
    btnCloseResizePanels.click();
  }

  /**
   * Builds a list of dashboard resize handles that are currently present on the GUI.
   * 
   * @return Returns a List<DashboardResizeHandle> that contains all dashboard resize handles that are currently present
   *         on the GUI.
   */
  public List<DashboardResizeHandle> getResizeHandles() {
    List<DashboardResizeHandle> resizeHandles = new ArrayList<DashboardResizeHandle>();

    // Build the list of resize handles.
    if ( dashboardResizeHandles != null && !dashboardResizeHandles.isEmpty() ) {
      for ( ExtendedWebElement resizeHandle : dashboardResizeHandles ) {
        String firstPanel = resizeHandle.getAttribute( "item1" );
        String secondPanel = resizeHandle.getAttribute( "item2" );
        String[] classes = resizeHandle.getAttribute( "class" ).split( " " );

        ResizeAxis resizeAxis = null;

        // Find the correct class for the resize axis.
        for ( String resizeClass : classes ) {
          resizeAxis = ResizeAxis.getResizeAxis( resizeClass );

          if ( resizeAxis != null ) {
            break;
          }
        }

        resizeHandles.add( new DashboardResizeHandle( firstPanel, secondPanel, resizeAxis ) );
      }
    } else {
      LOGGER.warn( "No dashboard handles were found." );
    }

    return resizeHandles;
  }

  /**
   * Clicks and drags the specified resize handle by the specified number of pixels. The axis in which to move the
   * resize handle will automatically be determined based on the handle's stored resize axis.
   * 
   * @param resizeHandle
   *          The instance of ResizeHandle to click and drag.
   * @param numPixels
   *          The number of pixels to move the resize handle.
   */
  public void dragResizeHandle( DashboardResizeHandle resizeHandle, int numPixels ) {
    ExtendedWebElement resizeHandleElement =
        format( dashboardResizeHandle, resizeHandle.getFirstPanel(), resizeHandle.getSecondPanel() );

    int x = 0;
    int y = 0;

    // Set the number of pixels to move the resize handle depending on the axis that the resize handle affects.
    if ( resizeHandle.getResizeAxis().equals( ResizeAxis.x ) ) {
      x = numPixels;
    } else {
      y = numPixels;
    }

    Actions builder = new Actions( getDriver() );
    builder.clickAndHold( resizeHandleElement.getElement() ).moveByOffset( x, y ).release().perform();
  }

  /**
   * Clicks the prompts settings label in the objects panel.
   */
  public void clickPrompts() {
    switchToDashboardFrame();
    lblPrompts.click();
  }

  /**
   * Selects the Show Prompt Toolbar checkbox in the prompts panel.
   */
  public void showPromptToolbar() {
    setShowPromptToolbar( true );
  }

  /**
   * Deselects the Show Prompt Toolbar checkbox in the prompts panel.
   */
  public void hidePromptToolbar() {
    setShowPromptToolbar( false );
  }

  /**
   * Selects or deselects the Show Prompt Toolbar checkbox in the prompts panel.
   * 
   * @param select
   *          Determines whether or not to select the checkbox. When true, the checkbox will be selected. Otherwise, it
   *          will be deselected.
   */
  private void setShowPromptToolbar( boolean select ) {
    clickPrompts();

    // TODO: use the upcoming BasePage.setCheckBox method that is in a current pull request.
    if ( select ) {
      LOGGER.info( "Selecting Show Prompt Toolbar checkbox." );
      chkShowPromptToolbar.check();
    } else {
      LOGGER.info( "Deselecting Show Prompt Toolbar checkbox." );
      chkShowPromptToolbar.uncheck();
    }
  }

  /**
   * Clicks the move prompt up button in the prompts panel.
   */
  public void clickMovePromptUpButton() {
    btnMovePromptUp.click();
  }

  /**
   * Clicks the move prompt down button in the prompts panel.
   */
  public void clickMovePromptDownButton() {
    btnMovePromptDown.click();
  }

  /**
   * Clicks the edit prompt button in the prompts panel.
   */
  public void clickEditPromptButton() {
    btnEditPrompt.click();
  }

  /**
   * Clicks the add prompt button in the prompts panel.
   */
  public void clickAddPromptButton() {
    btnAddPrompt.click();
  }

  /**
   * Clicks the remove prompt button in the prompts panel.
   */
  public void clickRemovePromptButton() {
    btnRemovePrompt.click();
  }

  /**
   * Selects the specified prompt in the prompts panel, which makes it available for use with the corresponding prompt
   * buttons (move, edit, remove).
   */
  public void selectPrompt( String promptName ) {
    format( lblPromptName, promptName ).click();
  }

  /**
   * Clicks the dashboard widget's settings label in the objects panel.
   * 
   * @param id
   *          The value of the dashboard widget's id attribute.
   */
  public void clickWidgetSettings( String id ) {
    switchToDashboardFrame();

    String dashboardName = getDashboardWidgetTitle( id );
    format( lblWidgetSettings, dashboardName ).click();
  }

  /**
   * Clicks the add parameters to title button within the widget's settings panel.
   */
  public void clickAddParametersToTitleButton() {
    btnAddParametersToTitle.click();
  }

  /**
   * Clicks the parameters tab within the dashboard widget's settings.
   */
  public void clickParametersTab() {
    tabParameters.click();
  }

  /**
   * Clicks the specified parameter's source drop-down.
   * 
   * @param parameterName
   *          The name of the parameter that is associated with the source drop-down input to click.
   */
  public void clickSourceDropDown( String parameterName ) {
    format( inputParameterSource, parameterName ).click();
  }

  /**
   * Selects a value of the parameter's source drop-down based on the source widget's title and the field to link it to.
   * 
   * @param id
   *          The value of the id attribute of the source widget.
   * @param field
   *          The name of the field to be linked.
   */
  public void clickSourceDropDownValue( String id, String field ) {
    String widgetTitle = getDashboardWidgetTitle( id );
    format( inputParameterSourceValue, widgetTitle, field ).click();
  }

  /**
   * Clicks the content linking tab within the dashboard widget's settings.
   */
  public void clickContentLinkingTab() {
    tabContentLinking.click();
  }

  /**
   * Checks the content linking checkbox for the specified field.
   * 
   * @param fieldName
   *          The name of the field to check the content linking checkbox for.
   */
  public void checkContentLinking( String fieldName ) {
    // TODO: use the upcoming BasePage.setCheckBox method that is in a current pull request.
    format( chkContentLinking, fieldName ).check();
  }

  /**
   * Unchecks the content linking checkbox for the specified field.
   * 
   * @param fieldName
   *          The name of the field to uncheck the content linking checkbox for.
   */
  public void uncheckContentLinking( String fieldName ) {
    // TODO: use the upcoming BasePage.setCheckBox method that is in a current pull request.
    format( chkContentLinking, fieldName ).uncheck();
  }

  /**
   * Clicks the apply button within the widget's settings in the objects panel.
   */
  public void clickApply() {
    btnApply.click();
  }

  /**
   * Gets the ID values for the dashboard layout containers.
   * 
   * @param IdPrefix
   *          The prefix used in the layout container IDs.
   * 
   * @return Returns a List<String> that contains the ID values.
   */
  public List<String> getDashboardLayoutIDs() {
    switchToDashboardFrame();
    return getElementListAttribute( dashboardLayoutContainers, "id" );
  }

  /**
   * Gets the X position value for the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns an int that represents the X position of the element.
   */
  public int getDashboardLayoutXPosition( String id ) {
    return getDashboardLayoutPosition( id ).getX();
  }

  /**
   * Gets the Y position value for the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns an int that represents the Y position of the element.
   */
  public int getDashboardLayoutYPosition( String id ) {
    return getDashboardLayoutPosition( id ).getY();
  }

  /**
   * Gets the position of the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns a Point that contains the location of the element.
   */
  private Point getDashboardLayoutPosition( String id ) {
    switchToDashboardFrame();
    return format( dashboardLayoutContainer, id ).getElement().getLocation();
  }

  /**
   * Gets the height of the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns an int that represents the height of the element.
   */
  public int getDashboardLayoutHeight( String id ) {
    return getDashboardLayoutSize( id ).getHeight();
  }

  /**
   * Gets the width of the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns an int that represents the width of the element.
   */
  public int getDashboardLayoutWidth( String id ) {
    return getDashboardLayoutSize( id ).getWidth();
  }

  /**
   * Gets the size of the dashboard layout container.
   * 
   * @param id
   *          The ID of the layout container's element.
   * @return Returns a Dimension that contains the size of the element.
   */
  private Dimension getDashboardLayoutSize( String id ) {
    switchToDashboardFrame();
    return format( dashboardLayoutContainer, id ).getElement().getSize();
  }

  /**
   * Gets the values of the id attribute for all dashboard widgets that are contained within the specified dashboard
   * layout container.
   * 
   * @param id
   *          The ID of the dashboard layout container.
   * @return Returns a List<String> that contains the IDs of the dashboard widgets.
   */
  public List<String> getDashboardLayoutWidgetIds( String id ) {
    ExtendedWebElement container = format( dashboardLayoutContainer, id );
    List<ExtendedWebElement> widgets =
        findExtendedWebElements( By.xpath( container.getBy().toString().replace( "By.xpath:", "" ).trim()
            + DASHBOARD_WIDGET_XPATH ) );
    return getElementListAttribute( widgets, "id" );
  }

  /**
   * Retrieves the values of the id attribute of all dashboard widgets.
   * 
   * @return Returns a List<String> that contains the IDs of all dashboard widgets.
   */
  public List<String> getDashboardWidgetIds() {
    switchToDashboardFrame();
    return getElementListAttribute( dashboardWidgets, "id" );
  }

  /**
   * Retrieves the title of the dashboard that uses the specified ID.
   * 
   * @param id
   *          The value of the dashboard widget's id attribute.
   * @return Returns a String that contains the title of the dashboard widget.
   */
  public String getDashboardWidgetTitle( String id ) {
    return format( lblDashboardWidgetTitle, id ).getText();
  }

  /**
   * Gets the X position value for the dashboard widget.
   * 
   * @param id
   *          The ID of the dashboard widget.
   * @return Returns an int that represents the X position of the element.
   */
  public int getDashboardWidgetXPosition( String id ) {
    return getDashboardWidgetPosition( id ).getX();
  }

  /**
   * Gets the Y position value for the dashboard widget.
   * 
   * @param id
   *          The ID of the dashboard widget.
   * @return Returns an int that represents the Y position of the element.
   */
  public int getDashboardWidgetYPosition( String id ) {
    return getDashboardWidgetPosition( id ).getY();
  }

  /**
   * Gets the position of the dashboard widget.
   * 
   * @param id
   *          The ID of the dashboard widget.
   * @return Returns a Point that contains the location of the element.
   */
  private Point getDashboardWidgetPosition( String id ) {
    switchToDashboardFrame();
    return format( dashboardWidget, id ).getElement().getLocation();
  }

  /**
   * Gets the height of the dashboard widget.
   * 
   * @param id
   *          The ID of the dashboard widget.
   * @return Returns an int that represents the height of the element.
   */
  public int getDashboardWidgetHeight( String id ) {
    return getDashboardWidgetSize( id ).getHeight();
  }

  /**
   * Gets the width of the dashboard widget.
   * 
   * @param id
   *          The ID of the dashboard widget.
   * @return Returns an int that represents the width of the element.
   */
  public int getDashboardWidgetWidth( String id ) {
    return getDashboardWidgetSize( id ).getWidth();
  }

  /**
   * Gets the size of the dashboard widget.
   * 
   * @param id
   *          The id of the dashboard widget.
   * @return Returns a Dimension that contains the size of the element.
   */
  private Dimension getDashboardWidgetSize( String id ) {
    switchToDashboardFrame();
    return format( dashboardWidget, id ).getElement().getSize();
  }

  /**
   * Double-clicks the title of the specified dashboard widget. When the dashboard is in view mode, this will expand the
   * dashboard to fill the screen.
   * 
   * @param id
   *          The value of the id attribute for the dashboard that is to be double-clicked.
   */
  public void doubleClickDashboardWidgetTitle( String id ) {
    switchToDashboardFrame();
    format( lblDashboardWidgetTitle, id ).doubleClick();
  }

  /**
   * Clicks the insert content button for the specified dashboard widget.
   * 
   * @param widgetId
   *          The ID of the widget that contains the button to click.
   */
  public void clickInsertContent( String widgetId ) {
    switchToDashboardFrame();
    format( btnDashboardWidgetInsert, widgetId ).click();
  }

  /**
   * Clicks the edit content button for the specified dashboard widget.
   * 
   * @param widgetId
   *          The ID of the widget that contains the contend to modify.
   */
  public void clickEditContent( String widgetId ) {
    switchToDashboardFrame();
    format( btnWidgetEdit, widgetId ).click();
  }

  /**
   * Clicks the chart item in the insert content drop-down. The insert content button must be clicked before calling
   * this method.
   */
  public void clickInsertChart() {
    switchToContentMenuFrame();
    mnuInsertChart.click();
  }

  /**
   * Clicks the data table item in the insert content drop-down. The insert content button must be clicked before
   * calling this method.
   */
  public void clickInsertDataTable() {
    switchToContentMenuFrame();
    mnuInsertDataTable.click();
  }

  /**
   * Clicks the URL item in the insert content drop-down. The insert content button must be clicked before calling this
   * method.
   */
  public void clickInsertUrl() {
    switchToContentMenuFrame();
    mnuInsertUrl.click();
    switchToDefault();
    switchToDashboardFrame();
  }

  /**
   * Inputs the specified URL into the input box for the website address. The insert URL link must be clicked for a
   * dashboard widget before calling this method.
   * 
   * @param url
   *          The URL to input.
   */
  public void inputUrl( String url ) {
    format( inputWebAddress, WEBSITE_ADDRESS ).type( url );
  }

  /**
   * Clicks the OK button found in the insert URL dialog.
   */
  public void clickOk() {
    btnOk.click();
  }

  /**
   * Clicks the cancel button found in the insert URL dialog.
   */
  public void clickCancel() {
    btnCancel.click();
  }

  /**
   * Clicks the file item in the insert content drop-down. The insert content button must be clicked before calling this
   * method.
   */
  public void clickInsertFile() {
    switchToContentMenuFrame();
    mnuInsertFile.click();
  }

  /**
   * Selects the specified folder in the browse tree.
   * 
   * @param folderName
   *          The name of the folder to select. This should only be the name of a folder and not a path.
   */
  public void clickFolder( String folderName ) {
    switchToDefault();
    format( folder, folderName ).click();
  }

  /**
   * Double-clicks the specified folder to trigger the expand/collapse functionality.
   * 
   * @param folderName
   *          The name of the folder to expand/collapse. This should only be the name of a folder and not a path.
   */
  public void doubleClickFolder( String folderName ) {
    switchToDefault();
    format( folder, folderName ).doubleClick();
  }

  /**
   * Determines whether or not the specified folder is expanded by checking for the existance of the "open" class.
   * 
   * @param folderName
   *          The name of the folder. This should only be the name of a folder and not a path.
   * @return Returns true when the folder is expanded. Return false when it is collapsed.
   */
  public boolean isFolderExpanded( String folderName ) {
    switchToDefault();
    return format( folderContainer, folderName ).getAttribute( "class" ).contains( "open" );
  }

  /**
   * Drags and drops a file from the files panel to the specified dashboard widget container.
   * 
   * @param fileName
   *          The name of the file to drag to the widget.
   * @param widgetId
   *          The ID of the destination widget for the file.
   */
  public void dragFileToWidget( String fileName, String widgetId ) {
    // The source element is outside of the iframe that the destination element is located within.
    switchToDashboardFrame();

    // Get the location and size of the destination element.
    WebElement destinationWidget = format( dashboardWidget, widgetId ).getElement();
    int x = destinationWidget.getLocation().getX();
    int y = destinationWidget.getLocation().getY();
    int width = destinationWidget.getSize().getWidth();
    int height = destinationWidget.getSize().getHeight();

    switchToDefault();

    // 1. Click and hold the file.
    // 2. Move to the dashboard frame.
    // 3. offset the mouse by the X and Y coordinates of the destination element. Half of the width and height are added
    // to the coordinates to move the mouse to the center of the destination widget.
    // 4. Release mouse button, which will release the file on the dashboard widget.
    Actions dragAndDrop = new Actions( getDriver() );
    dragAndDrop.clickAndHold( format( file, fileName ).getElement() ).moveByOffset( 5, 5 ).moveToElement( dashboardFrame
        .getElement(), x + ( width / 2 ), y + ( height / 2 ) ).release().perform();
  }

  /**
   * Determines whether or not the Pentaho Dialog window is present.
   * 
   * @return Returns true when the dialog window is present.
   */
  public boolean isDialogWindowPresent() {
    switchToDashboardFrame();
    return dialogWindow.isElementPresent();
  }

  /**
   * Determines whether or not the warning text within the dialog window is present.
   * 
   * @return Returns true when the label is present.
   */
  public boolean isWarningLabelPresent() {
    switchToDashboardFrame();
    return lblWarning.isElementPresent();
  }

  /**
   * Determines whether or not the discard content text within the dialog window is present.
   * 
   * @return Returns true when the label is present.
   */
  public boolean isDiscardContentLabelPresent() {
    switchToDashboardFrame();
    return lblDiscardContent.isElementPresent();
  }
}
