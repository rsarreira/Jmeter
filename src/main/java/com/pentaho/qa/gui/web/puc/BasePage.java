package com.pentaho.qa.gui.web.puc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringEscapeUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.puc.schedules.SchedulesPage;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.Report.Sort;
import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.tree.Tree;
import com.pentaho.services.tree.Tree.TreeNode;
import com.qaprosoft.carina.core.foundation.log.TestLogCollector;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.Screenshot;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;
import com.qaprosoft.carina.core.gui.AbstractPage;

public abstract class BasePage extends AbstractPage {

  public static final String NEW_ANALYSIS_REPORT_NAME = L10N.getText( "analysis_report" );
  public static final String NEW_IR_REPORT_NAME = L10N.getText( "interactiveReportLabel" );
  public static final String NEW_DASHBOARD_NAME = L10N.getText( "dashboard" );
  public static final String SEPARATOR = "/";
  public static final String MEASURE = "/Measures/";
  public static final String DIMENSION = "/Dimensions/";
  public static final String CATEGORY = "/Categories/";
  By by;

  // TODO: Come up with a better solution to deal with encoded characters such as < and >
  // "Next >"
  public static final String btnNextLabel = StringEscapeUtils.unescapeHtml( L10N.getText( "nextStep" ) );
  // "< Back"
  public static final String btnBackLabel = StringEscapeUtils.unescapeHtml( L10N.getText( "previousStep" ) );

  public static final String btnFinishLabel = L10N.getText( "FINISH" );

  public static final String btnCancelLabel = L10N.getText( "CANCEL" );

  @FindBy( xpath = "//td[text()='%s']" )
  public ExtendedWebElement moduleItem;

  @FindBy( css = ".pentaho-tab-bar > .pentaho-tabWidget" )
  protected List<ExtendedWebElement> listTabs;

  @FindBy( css = ".pentaho-tab-bar > .pentaho-tabWidget-selected" )
  protected ExtendedWebElement activeTab;

  @FindBy( css = "div.pentaho-tabWidget-selected div" )
  protected ExtendedWebElement openedFileTitle;

  @FindBy( css = "#pucUserDropDown > table > tbody > tr > td:nth-child(1) > div" )
  protected ExtendedWebElement dropdownUserName;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancel;

  @FindBy( id = "wizard-finish-button" )
  public ExtendedWebElement btnWizardFinish;

  @FindBy( id = "openButton" )
  public ExtendedWebElement lnkOpen;

  @FindBy( id = "navigationListBox" )
  public ExtendedWebElement location;

  @FindBy( id = "fileNameTextBox" )
  public ExtendedWebElement fileName;

  @FindBy( xpath = "//*[@id='mantle-perspective-switcher']/table/tbody/tr/td[1]/div" )
  public ExtendedWebElement perspectiveSwitcher;

  @FindBy( css = "#mantle-perspective-switcher div.custom-dropdown-label" )
  public ExtendedWebElement currentModule;

  @FindBy( id = "home.perspective" )
  protected ExtendedWebElement homeFrame;

  @FindBy( id = "browser.perspective" )
  protected ExtendedWebElement browserFrame;

  @FindBy( id = "DatasourceEditor" )
  protected ExtendedWebElement datasourceEditorFrame;

  @FindBy( id = "schedulerParamsFrame" )
  protected ExtendedWebElement schedulerParamsFrame;

  // View->Show Hidden Files
  @FindBy( id = "showHiddenFilesMenuItem" )
  protected ExtendedWebElement showHiddenFilesMenuItem;

  // Help->About
  // 'About Pentaho User Console'
  @FindBy( xpath = "//*[contains(., '{L10N:aboutDialogTitle}')]" )
  protected ExtendedWebElement textPentahoAbout;

  // 'Release' in resource file it has colon as well!
  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., '{L10N:release} %s')]" )
  protected ExtendedWebElement textReleaseNumber;

  // @FindBy( xpath = "//*[contains(., 'Copyright')]" )
  @FindBy( xpath = "//*[contains(., '%s')]" )
  protected ExtendedWebElement textCopyright;

  // Create buttons
  @FindBy( id = "btnCreateNew" )
  protected ExtendedWebElement btnCreateNew;

  @FindBy( css = ".popover-content > #createNewanalyzerButton" )
  protected ExtendedWebElement btnCreateNewAnalyzer;

  @FindBy( css = ".popover-content > #createNewinteractiveReportButton" )
  protected ExtendedWebElement btnCreateNewIR;

  @FindBy( css = ".popover-content > #createNewdashboardButton" )
  protected ExtendedWebElement btnCreateNewDashboard;

  @FindBy( css = "body > .pentaho-dialog" )
  protected List<ExtendedWebElement> pentahoDialogs;

  @FindBy( css = ".pentaho-busy-indicator-message" )
  public ExtendedWebElement busyIndicator;

  @FindBy( css = ".pentaho-busy-indicator-spinner" )
  public ExtendedWebElement busyIndicatorSpinner;

  @FindBy( css = ".pageLoadingSpinner" )
  public ExtendedWebElement loadingIndicatorSpinner;

  @FindBy( id = "pageLoadingSpinner" )
  public ExtendedWebElement loadingIndicatorSpinnerId;

  @FindBy( xpath = "//button[@class='pentaho-button disabled']/span[text()='%s']" )
  private ExtendedWebElement disabledButton;

  @FindBy( xpath = "//button[@class='pentaho-button']/span[text()='%s']" )
  private ExtendedWebElement enabledButton;

  @FindBy( xpath = "//div[@class='popupContent']//div[@class='gwt-Label' and contains(text(),'%s')]" )
  private ExtendedWebElement dropdownItem;

  // Save dialog
  // @FindBy( css = ".pentaho-dialog > [title^=Save]" )
  @FindBy(
      xpath = "//div[@class='dialogTopCenterInner']/div[@class='Caption' and contains(text(),'{L10N:btnLabelSave}')]" )
  protected ExtendedWebElement dlgSave;

  // Open dialog
  // 'Open'
  @FindBy( xpath = "//div[@class='dialogTopCenterInner']/div[@class='Caption' and contains(text(),'{L10N:open}')]" )
  protected ExtendedWebElement dlgOpen;

  @FindBy( id = "deleteButton" )
  protected ExtendedWebElement deleteBtn;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement okBtnDeleteDialog;
  
  @FindBy( css = "#themesmenu .gwt-MenuItem-checkbox-checked" )
  private ExtendedWebElement menuSelectedTheme;

  public BasePage( WebDriver driver ) {
    super( driver );
  }

  protected boolean isOpened( ExtendedWebElement element ) {
    return isOpened( element, EXPLICIT_TIMEOUT );
  }

  protected boolean isOpened( ExtendedWebElement element, long timeout ) {
    return isElementPresent( element, timeout );
  }

  public void click( final ExtendedWebElement extendedWebElement, boolean wait ) {
    super.click( extendedWebElement );

    if ( wait ) {
      loading();
    }
  }

  public void loading() {
    int i = 0;
    while ( ( !findExtendedWebElements( busyIndicator.getBy(), 1 ).isEmpty() || !findExtendedWebElements(
        busyIndicatorSpinner.getBy(), 1 ).isEmpty() || !findExtendedWebElements( loadingIndicatorSpinner.getBy(), 1 )
            .isEmpty() || !findExtendedWebElements( loadingIndicatorSpinnerId.getBy(), 1 ).isEmpty() ) && ++i < 10 ) {
      LOGGER.info( "Busy indicator is present. Waiting for operation finalization..." );
      pause( EXPLICIT_TIMEOUT / 10 );
    }
  }

  public static Tree<String> menu = new Tree<String>();

  // Menu
  /*
   * Usage example: menuPick(Menu.DATASOURCE);
   */
  public enum Menu {
    FILE( menu.addNode( menu.getRoot(), new TreeNode<String>( "filemenu" ) ) ), 
      NEW( menu.addNode( FILE.getNode(), new TreeNode<String>( "newmenu" ) ) ), 
      ANALYSIS_REPORT( menu.addNode( NEW.getNode(), new TreeNode<String>( "new-analyzer" ) ) ), 
      INTERACTIVE_REPORT(menu.addNode( NEW.getNode(), new TreeNode<String>( "iadhoc" ) ) ), 
      DASHBOARD( menu.addNode( NEW.getNode(), new TreeNode<String>( "dashboardMenuItem" ) ) ), 
      DATASOURCE( menu.addNode( NEW.getNode(), new TreeNode<String>( "newDatasourceItem" ) ) ), 
      OPEN( menu.addNode( FILE.getNode(), new TreeNode<String>( "openMenuItem" ) ) ), 
      MANAGE_DATASOURCE( menu.addNode( FILE.getNode(), new TreeNode<String>( "manageDatasourceItem" ) ) ), 
      RECENT( menu.addNode( FILE.getNode(), new TreeNode<String>( "recentmenu" ) ) ), 
      FAVORITES( menu.addNode( FILE.getNode(), new TreeNode<String>( "favoritesmenu" ) ) ), 
      SAVE( menu.addNode( FILE.getNode(), new TreeNode<String>( "saveMenuItem" ) ) ), 
      SAVE_AS( menu.addNode( FILE.getNode(), new TreeNode<String>( "saveAsMenuItem" ) ) ), 
      LOGOUT( menu.addNode( FILE.getNode(), new TreeNode<String>( "logoutMenuItem" ) ) ), 
    VIEW(menu.addNode( menu.getRoot(), new TreeNode<String>( "viewmenu" ) ) ), 
      USE_DESCRIPTIONS_FOR_TOOLTIPS( menu.addNode( VIEW.getNode(), new TreeNode<String>( "useDescriptionsMenuItem" ) ) ), 
      SHOW_HIDDEN_FILES( menu.addNode( VIEW.getNode(), new TreeNode<String>( "showHiddenFilesMenuItem" ) ) ), 
      LANGUAGES( menu.addNode( VIEW.getNode(), new TreeNode<String>("languagemenu" ) ) ),
        JAPANESE( menu.addNode( LANGUAGES.getNode(), new TreeNode<String>( L10N.getText( "MenuLanguage" ) ) ) ),
        DEUTSCH( menu.addNode( LANGUAGES.getNode(), new TreeNode<String>( L10N.getText( "MenuLanguage" ) ) ) ),
        FRENCH( menu.addNode( LANGUAGES.getNode(), new TreeNode<String>( L10N.getText( "MenuLanguage" ) ) ) ),
        ENGLISH( menu.addNode( LANGUAGES.getNode(), new TreeNode<String>( L10N.getText( "MenuLanguage" ) ) ) ),
      THEMES( menu.addNode( VIEW.getNode(), new TreeNode<String>( "themesmenu" ) ) ), 
      ONYX( menu.addNode( THEMES.getNode(), new TreeNode<String>( "onyx_menu_item" ) ) ), 
      CRYSTAL( menu.addNode( THEMES.getNode(), new TreeNode<String>( "crystal_menu_item" ) ) ), 
    TOOLS( menu.addNode( menu.getRoot(), new TreeNode<String>( "toolsmenu" ) ) ), 
      REFRESH(menu.addNode( TOOLS.getNode(), new TreeNode<String>( "refreshmenu" ) ) ), 
      SYSTEM_SETTINGS( menu.addNode( REFRESH.getNode(), new TreeNode<String>( "refreshSystemSettingsMenuItem" ) ) ), 
      REPORTING_METADATA( menu.addNode( REFRESH.getNode(), new TreeNode<String>( "refreshReportingMetadataMenuItem" ) ) ), 
      GLOBAL_VARIABLES( menu.addNode( REFRESH.getNode(), new TreeNode<String>( "executeGlobalActionsMenuItem" ) ) ), 
      MONDRIAN_SCEMA_CACHE( menu.addNode( REFRESH.getNode(), new TreeNode<String>( "purgeMondrianSchemaCacheMenuItem" ) ) ), 
      REPORTING_DATA_CACHE( menu.addNode( REFRESH.getNode(), new TreeNode<String>( "purgeReportingDataCacheMenuItem" ) ) ), 
    HELP( menu.addNode( menu.getRoot(),new TreeNode<String>( "helpmenu" ) ) ), 
      DOCUMENTATION( menu.addNode( HELP.getNode(), new TreeNode<String>("documentationMenuItem" ) ) ), 
      PENTAHO( menu.addNode( HELP.getNode(), new TreeNode<String>( "pentahoDotComMenuItem" ) ) ), 
      ABOUT(menu.addNode( HELP.getNode(), new TreeNode<String>( "aboutMenuItem" ) ) );

    private TreeNode<String> node;

    private Menu( TreeNode<String> node ) {
      this.node = node;
    }

    public TreeNode<String> getNode() {
      return this.node;
    }
  }
  
  public enum Theme {
    CRYSTAL( "Crystal", Menu.CRYSTAL ), ONYX( "Onyx", Menu.ONYX );

    String name;
    Menu menuItem;

    private Theme( String name, Menu menuItem ) {
      this.name = name;
      this.menuItem = menuItem;
    }

    /**
     * Retrieves the corresponding Menu value for the theme.
     * 
     * @return Returns the Menu item for the theme.
     */
    public Menu getMenuItem() {
      return menuItem;
    }

    /**
     * Finds the correct Theme from the specified theme name.
     * 
     * @param name
     *          The name of the theme.
     * @return Returns the matched Theme.
     */
    public static Theme getTheme( String name ) {
      Theme matchedTheme = null;

      for ( Theme theme : Theme.values() ) {
        if ( theme.name.equals( name ) ) {
          matchedTheme = theme;
        }
      }

      return matchedTheme;
    }
  }

  public void menuPick( Menu item ) {
    ExtendedWebElement menuItem = getMenuItemElement( item );

    hoverMenuItem( menuItem );
    click( menuItem );
  }
  
  protected ExtendedWebElement getMenuItemElement( Menu item ) {
    driver.switchTo().defaultContent();
    List<TreeNode<String>> parents = item.getNode().getParents();
    int size = parents.size() - 1;
    for ( int i = parents.size() - 1; i >= 0; i-- ) {
      String parentValue = parents.get( i ).getItem();
      LOGGER.info( "Activating menu item: " + parentValue );
      ExtendedWebElement menuItem = findExtendedWebElement( By.id( parentValue ), parentValue );

      if ( i == size ) {
        // add additional verification that after clicking on the global menu item next level is opened.
        click( menuItem );
        boolean foundChild = false;
        int index = 0;
        while ( !foundChild && ++index < 4 ) {
          if ( i > 1 ) {
            String childValue = parents.get( i - 1 ).getItem();
            try {
              findExtendedWebElement( By.id( childValue ), childValue );
              foundChild = true;
            } catch ( Exception e ) {
              LOGGER.error( "child menu item '" + childValue + "' is not found after attempt #" + index );
              // do nothing
            }
          }
        }
      } else {
        hoverMenuItem( menuItem );
      }

    }

    // 3rd level menu level should be activated by hovering first child at the beginning (to fix issue with FF)
    String firstChild = parents.get( 0 ).getFirstChild().getItem();
    String itemValue = (String) item.getNode().getItem();
    
    // [BenF] - If we are only trying to logout, skip this because it may fail on users without creation permission (newmenu is not present).
    if ( !itemValue.equals( Menu.LOGOUT.getNode().getItem() ) ) {
      if (!firstChild.equals( itemValue ) && !firstChild.equals(Menu.FILE.getNode().getItem())) {
        //there is no need to hover first child in menu
        ExtendedWebElement menuFirstChild = findExtendedWebElement( By.id( firstChild ), firstChild );
        hover( menuFirstChild, 5, 5 );
      }

    }

    LOGGER.info( "Activating menu item: " + itemValue );
    return findExtendedWebElement( By.id( itemValue ), itemValue );
  }
  
  protected void hoverMenuItem( ExtendedWebElement menuItem ) {
    hover( menuItem, 5, 5 );
  }

  public enum Module {
    HOME( "home_module" ), BROWSE_FILES( "browse_module" ), OPENED( "opened" ), SCHEDULES(
        "schedules_module" ), ADMINISTRATION( "administration" );

    private String module;

    private Module( String module ) {
      this.module = module;
    }

    public String getModule() {
      return this.module;
    }

    public static Module getModuleByValue( String value ) {
      if ( value != null ) {
        for ( Module mod : Module.values() ) {
          if ( value.equals( L10N.getText( mod.module ) ) ) {
            return mod;
          }
        }
      }
      return null;
    }
  }

  public BasePage activateModuleEx( Module module ) {
    if ( getCurrentModule() != null && !getCurrentModule().equals( module ) ) {
      click( perspectiveSwitcher );

      format( EXPLICIT_TIMEOUT / 10, moduleItem, L10N.getText( module.getModule() ) ).click();

      // verification part
      LOGGER.warn( "TODO: add module verification here!" );
    }

    BasePage modulePage = null;
    switch ( module ) {
      case BROWSE_FILES:
        modulePage = new BrowseFilesPageEx( getDriver() );
        BrowseService.setBrowseFilesPageEx( (BrowseFilesPageEx) modulePage );
        break;
      case SCHEDULES:
        modulePage = new SchedulesPage( getDriver() );
        break;
      case ADMINISTRATION:
        modulePage = new AdministrationPage( getDriver() );
        AdministrationService.setAdministrationPage( (AdministrationPage) modulePage );
        break;
      default:
        break;
    }

    return modulePage;
  }

  public BasePage activateModule( Module module ) {
    if ( getCurrentModule() != null && !getCurrentModule().equals( module ) ) {
      click( perspectiveSwitcher );

      format( moduleItem, L10N.getText( module.getModule() ) ).click();

      // verification part
      LOGGER.warn( "TODO: add module verification here!" );
    }

    BasePage modulePage = null;
    switch ( module ) {
      case BROWSE_FILES:
        modulePage = new BrowseFilesPage( getDriver() );
        break;
      case SCHEDULES:
        modulePage = new SchedulesPage( getDriver() );
        break;
      case ADMINISTRATION:
        modulePage = new AdministrationPage( getDriver() );
        break;
      default:
        break;
    }

    return modulePage;
  }

  public Module getCurrentModule() {
    Module module = Module.HOME;
    if ( isElementPresent( currentModule ) ) {
      module = Module.getModuleByValue( currentModule.getText() );
    } else {
      LOGGER.warn(
          "Unable to identify current Module as appropriate control is absent! HOME is specified by default." );
    }
    return module;
  }

  /*
   * public enum ReportType { ANALYZER("Analysis Report"), IR("Interactive Report"), DASHBOARD("Dashboard");
   * 
   * private String reportType;
   * 
   * private ReportType(String reportType) { this.reportType = reportType; }
   * 
   * public String getReportType() { return this.reportType; } }
   */

  public enum View {
    CATEGORY( "by Category" ), TYPE( "Measure - Level - Time" ), NAME( "A&#8594;Z" ), SCHEMA( "Schema" ), HIDDEN(
        "Show Hidden Fields" );

    private String view;

    private View( String view ) {
      this.view = view;
    }

    public String getView() {
      return this.view;
    }
  }

  /*
   * public BasePage createReport(Menu menu1) {
   * 
   * activateModule(Module.HOME); menuPick(Menu.ANALYSIS_REPORT); driver.switchTo().frame(homeFrame.getElement());
   * 
   * if (!isElementPresent(btnCreateNew)) Assert.fail("'Create New' button is not present on Home page!");
   * pause(EXPLICIT_TIMEOUT/10); click(btnCreateNew);
   * 
   * if (!isElementPresent(btnCreateNewAnalyzer)) Assert.fail(
   * "'Analysis Report' popover button is not appeared after clicking 'Create New' button!");
   * 
   * BasePage reportTypePage = null; switch (reportType) { case ANALYZER: click(btnCreateNewAnalyzer);
   * driver.switchTo().defaultContent(); reportTypePage = new AnalyzerDataSourcePage(driver); break; case IR:
   * click(btnCreateNewIR); driver.switchTo().defaultContent(); reportType = new IRDataSourcePage(driver); break; case
   * DASHBOARD: click(btnCreateNewDashboard); driver.switchTo().defaultContent(); reportType = new
   * DashboardPage(driver); break; default: break; }
   * 
   * return new AnalyzerDataSourcePage(driver); }
   */
  public void buttonOK() {
    click( btnOK );
  }

  public void buttonOpen() {
    click( btnOK ); // the same id
  }

  public void buttonYes() {
    click( btnOK ); // the same id
  }

  public void buttonNext() {
    if ( isElementPresent( btnWizardFinish, 1 ) ) {
      click( btnWizardFinish );
    } else if ( isElementPresent( btnOK, 1 ) ) {
      click( btnOK );
    }
  }

  public void buttonFinish() {
    click( btnWizardFinish );
  }

  public void menuCheck( Menu item ) {
    String itemValue = item.getNode().getItem();
    ExtendedWebElement menuItem = findExtendedWebElement( By.id( itemValue ), itemValue );
    click( menuItem );
  }

  public void showHiddenFile( boolean show ) {
    menuCheck( Menu.VIEW );
    String checked = "gwt-MenuItem-checkbox-checked";
    String attrClass = showHiddenFilesMenuItem.getElement().getAttribute( HTML.CLASS );

    boolean changeState = false;
    if ( show ) { // show hidden
      if ( !attrClass.contains( checked ) ) {
        changeState = true;
      }
    } else { // hide hidden
      if ( attrClass.contains( checked ) ) {
        changeState = true;
      }
    }

    if ( changeState ) {
      String itemValue = Menu.SHOW_HIDDEN_FILES.getNode().getItem();
      ExtendedWebElement menuItem = findExtendedWebElement( By.id( itemValue ), itemValue );
      menuItem.click();
    } else {
      // close opened View menu
      menuCheck( Menu.VIEW );
    }
    pause( 1 );
  }

  public boolean verifyMenuAbout( String appVersion ) {

    menuPick( Menu.ABOUT );

    boolean res = isElementPresent( textPentahoAbout );
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( res, "TS042016: About Pentaho User Console is not opened!" );

    if ( !appVersion.isEmpty() && !appVersion.equalsIgnoreCase( "null" ) && !appVersion.equalsIgnoreCase( "n/a" ) ) {
      // replace all dashed with dots to be compatible with Help-About setting
      appVersion = appVersion.replace( "-", "." );
      if ( !format( textReleaseNumber, appVersion ).isElementPresent() ) {
        // make screenshot
        TestLogCollector.addScreenshotComment( Screenshot.capture( getDriver(), true ),
            "TS042016: Release number is not valid!" );
        softAssert.fail( "TS042016: Release number is not valid!" );
      }
    }
    String copyright = L10N.getText( "licenseInfo" );
    // 1. get subtext between '<BR>' tags
    copyright = copyright.split( "<BR>" )[1];
    // 2. replace '{0}' with the current year
    copyright = copyright.replace( "{0}", getCurrentYear() ).trim();

    softAssert.assertTrue( format( textCopyright, copyright ).isElementPresent(),
        "TS042016: Copyright years are not valid!" );

    click( btnOK );
    makeClickable();

    softAssert.assertAll();
    return res;
  }

  public void makeClickable() {
    // magic fix to make element clickable if div is on the front
    try {
      String css = "body > div[style*='display: block']";
      List<WebElement> elements = getDriver().findElements( By.cssSelector( css ) );
      if ( !elements.isEmpty() ) {
        WebElement elem = elements.get( 0 );
        click( "makeClickable", elem );
      }
    } catch ( Exception e ) {
      LOGGER.warn( "'Exception appeared during makeClickable operation: " + e.getMessage() );
    }
  }

  protected String getCurrentYear() {
    int currentYear = Calendar.getInstance( Locale.US ).get( Calendar.YEAR );
    return String.valueOf( currentYear );
  }

  protected String getCurrentDate() {
    int currentDate = Calendar.getInstance( Locale.US ).get( Calendar.DATE );
    return String.valueOf( currentDate );
  }

  // Short value will be returned
  protected String getCurrentMonth() {
    return new SimpleDateFormat( "MMM ", Locale.US ).format( Calendar.getInstance().getTime() );
  }

  /**
   * Verifies if an argument string contains current date (YYYY, MMM, dd)
   * 
   * @param date
   *          string with date
   * @return true if an argument string contains current date
   */
  public boolean isCorrespondCurrentDate( String date ) {
    LOGGER.info( "Expected date: " + date );
    LOGGER.info( String.format( "Actual date: %s ", new SimpleDateFormat( "dd MMM YYYY", Locale.US ).format( Calendar
        .getInstance().getTime() ) ) );

    return ( date.contains( getCurrentDate() ) && date.contains( getCurrentYear() ) && date
        .contains( getCurrentMonth() ) );
  }

  /**
   * Verifies if an argument string contains current date in pattern format string with date
   * 
   * @param pattern
   * @return true if an argument string contains current date
   */
  public boolean isCorrespondCurrentDate( String date, String pattern ) {
    String dateString = new SimpleDateFormat( pattern, Locale.US ).format( Calendar.getInstance().getTime() );
    LOGGER.info( String.format( "Actual date: %s, Expected date:  %s", dateString, date ) );
    return date.contains( dateString );
  }

  public boolean isLogged( String user ) {
    return getUserName().equals( user );
  }

  public String getUserName() {
    String curUser = "";
    if ( isElementPresent( dropdownUserName ) ) {
      curUser = dropdownUserName.getText();
    }
    LOGGER.info( "Currently logged user is: " + curUser );
    return curUser;
  }

  public String getSelectedTabName() {
    return openedFileTitle.getText();
  }

  /**
   * Return the title of top level dialog
   * 
   * @return Dialog title value
   */
  public String getTopPentahoDialog() {
    String zIndex;
    int maxValue = 0, maxIndex = 0;
    int[] zIndexs = new int[pentahoDialogs.size()];

    for ( int i = 0; i < pentahoDialogs.size(); i++ ) {
      zIndex = pentahoDialogs.get( i ).getElement().getCssValue( "z-index" );
      zIndexs[i] = Integer.parseInt( zIndex );
      if ( maxValue < zIndexs[i] ) {
        maxValue = zIndexs[i];
        maxIndex = i;
      }
    }
    return pentahoDialogs.get( maxIndex ).getElement().findElement( By.cssSelector( ".Caption" ) ).getText();
  }

  public boolean isButtonEnabled( String caption ) {
    return format( enabledButton, caption ).getElement() != null;
  }

  public boolean isButtonDisabled( String caption ) {
    return format( disabledButton, caption ).getElement() != null;
  }

  // custom Pentaho dropdown selection

  public void selectPopup( ExtendedWebElement popup, String value ) {

    click( popup );

    ExtendedWebElement tableItem = format( dropdownItem, value );
    click( tableItem );
  }

  // Works only for Strings
  protected boolean isSorted( List<String> actualList, Sort sort ) {
    List<String> tmp = new ArrayList<String>( actualList );
    Collections.sort( tmp );
    if ( sort == Sort.DESC ) {
      Collections.reverse( tmp );
    }
    return tmp.equals( actualList );
  }

  public ExtendedWebElement getActiveTab() {
    return activeTab;
  }

  /**
   * Maximizes the browser window.
   */
  public void maximizeBrowser() {
    getDriver().manage().window().maximize();
  }

  /**
   * Resize the browser window to the specified width and height in pixels.
   * 
   * @param width
   *          The width of the browser in pixels.
   * @param height
   *          The height of the browser in pixels.
   * @return Returns true if the browser properly resized to the expected dimensions.
   */
  public boolean resizeBrowser( int width, int height ) {
    Dimension newSize;

    getDriver().manage().window().setSize( new Dimension( width, height ) );

    newSize = getDriver().manage().window().getSize();

    return newSize.getWidth() == width && newSize.getHeight() == height;
  }

  /**
   * Changes the theme to the onyx theme.
   */
  public void setThemeOnyx() {
    setTheme( Theme.ONYX );
  }

  /**
   * Changes the theme to the crystal theme.
   */
  public void setThemeCrystal() {
    setTheme( Theme.CRYSTAL );
  }

  /**
   * Changes the theme to the specified theme.
   * 
   * @param menuItem
   *          The theme to change to.
   */
  private void setTheme( Theme theme ) {
    menuPick( theme.getMenuItem() );
    btnOK.click();

    // The frame number resets to 0 after switching the theme.
    PentahoUser.resetOpenedTab();
  }
  
  /**
   * Gets the current theme of PUC by determining which theme menu item is selected.
   * 
   * @return Returns the current theme.
   */
  public Theme getTheme() {
    // The selected theme can only be found when the menu is present.
    hoverMenuItem( getMenuItemElement( Menu.THEMES ) );

    String selectedTheme = menuSelectedTheme.getText();
    Theme currentTheme = Theme.getTheme( selectedTheme );

    makeClickable();

    return currentTheme;
  }

  /**
   * Gets the property value of the specified element's "after" CSS pseudo-element.
   * 
   * @param element
   *          The element that contains a pseudo element.
   * @param property
   *          The name of the property to retrieve the value from.
   * @return Returns the value of the "after" pseudo-element's property.
   */
  protected String getPseudoAfterPropretyValue( WebElement element, String property ) {
    return getPseudoElementPropretyValue( element, property, "after" );
  }

  /**
   * Gets the property value of the specified element's CSS pseudo-element.
   * 
   * @param element
   *          The element that contains a pseudo element.
   * @param property
   *          The name of the property to retrieve the value from.
   * @param pseudoElement
   *          The name of the pseudo-element (i.e. "after" or "before").
   * @return Returns the value of the pseudo-element's property.
   */
  private String getPseudoElementPropretyValue( WebElement element, String property, String pseudoElement ) {
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    return executor.executeScript( "return window.getComputedStyle(arguments[0], ':" + pseudoElement
        + "').getPropertyValue('" + property + "');", element ).toString();
  }
  
  /**
   * Gets the width of the specified element.
   * 
   * @param element
   *          The element to retrieve the width from.
   * @return Returns an integer value of the width.
   */
  protected int getElementWidth( ExtendedWebElement element ) {
    return getElementDimension( element, true );
  }

  /**
   * Gets the height of the specified element.
   * 
   * @param element
   *          The element to retrieve the height from.
   * @return Returns an integer value of the height.
   */
  protected int getElementHeight( ExtendedWebElement element ) {
    return getElementDimension( element, false );
  }

  /**
   * Gets the width or the height of the specified element.
   * 
   * @param element
   *          The element to retrieve the dimension from.
   * @param getWidth
   *          Flag that indicates which dimension to return: the width or the height. When true, the width will be
   *          returned.
   * @return Returns the integer value of the specified element's width or height.
   */
  private int getElementDimension( ExtendedWebElement element, boolean getWidth ) {
    Dimension dimension = element.getElement().getSize();
    return getWidth ? dimension.getWidth() : dimension.getHeight();
  }
  
  /**
   * Gets the background color of the specified element.
   * 
   * @param elem
   *          The element to retrieve the background color from.
   * @return Returns a String of the RGB value of the background color.
   */
  protected String getElementBackgroundColor( ExtendedWebElement elem ) {
    return elem.getElement().getCssValue( "background-color" );
  }
  
  /**
   * Sets the specified Checkbox EWE via click.
   * 
   * @param theCheckBox the Checkbox EWE to be operated on.
   * @param check the boolean value theCheckBox should be set to. 
   */
  protected void setCheckBox( ExtendedWebElement theCheckBox, boolean check ) {
    // if the state of theCheckBox does not currently match "check", toggle the state
    // TODO error checking to make sure theCheckBox is a checkBox
    if ( check )
      theCheckBox.check();
    else
      theCheckBox.uncheck();
  }

  protected void executeJavaScript( String script ) {
    ( (JavascriptExecutor) driver ).executeScript( script );
    pause( IMPLICIT_TIMEOUT / 5 );
  }
}
