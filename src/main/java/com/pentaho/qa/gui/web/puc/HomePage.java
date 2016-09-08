package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerDataSourcePage;
import com.pentaho.qa.gui.web.dashboard.DashboardPage;
import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.pir.PIRDataSourcePage;
import com.pentaho.services.PentahoUser;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class HomePage extends FramePage {

  @FindBy( id = "filemenu" )
  protected ExtendedWebElement fileMenu;

  @FindBy( css = "#pucUserDropDown > table > tbody > tr > td:nth-child(1) > div" )
  protected ExtendedWebElement userName;

  // 'Browse Files'
  // @FindBy( xpath = "//button[normalize-space(.)='{L10N:browse}']" )
  @FindBy( xpath = "//button[contains(normalize-space(.), '{L10N:browse}')]" )
  protected ExtendedWebElement btnBrowseFiles;

  // Buttons
  // 'Manage Data Sources'
  @FindBy( xpath = "//button[contains(., '{L10N:manage_datasources}')]" )
  public ExtendedWebElement btnManageDataSources;

  // 'Documentation'
  @FindBy( xpath = "//button[contains(., '{L10N:documentation}')]" )
  protected ExtendedWebElement btnDocumentation;

  @FindBy( id = "getting-started" )
  protected ExtendedWebElement gettingStarted;

  // 'Welcome'
  @FindBy( xpath = "//li[contains(., '{L10N:welcome}')]" )
  protected ExtendedWebElement tabWelcome;

  // 'Samples'
  @FindBy( xpath = "//li[contains(., '{L10N:getting_started_tab2}')]" )
  protected ExtendedWebElement tabSamples;

  // 'Tutorials'
  @FindBy( xpath = "//li[contains(., '{L10N:getting_started_tab3}')]" )
  protected ExtendedWebElement tabTutorials;

  @FindBy( id = "recents" )
  protected ExtendedWebElement widgetRecents;

  @FindBy( id = "favorites" )
  protected ExtendedWebElement widgetFavorites;

  public HomePage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      throw new RuntimeException( "Home Page is not recognized!" );
    }
  }

  public boolean isOpened() {
    long timeout = EXPLICIT_TIMEOUT * 4;

    switchToDefault();

    // [MG] Scroll to the fileMenu element to prevent failures due to makeClickable issues.
    fileMenu.scrollTo();

    boolean res = isElementPresent( fileMenu, timeout );
    if ( res ) {
      return res;
    }
    if ( homeFrame.isElementPresent( EXPLICIT_TIMEOUT / 15 ) ) {
      switchToFrame( homeFrame );
    }
    res |= isElementPresent( btnBrowseFiles, timeout );
    switchToDefault();
    return res;
  }

  public void verifyHomePerspective() {

    switchToFrame( homeFrame );
    long timeout = EXPLICIT_TIMEOUT / 15;

    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( isElementPresent( btnBrowseFiles, timeout ), "'Browse Files' button is not recognized!" );
    softAssert.assertTrue( isElementPresent( btnCreateNew, timeout ), "'Create New' button is not recognized!" );
    softAssert.assertTrue( isElementPresent( btnManageDataSources, timeout ),
        "'Manage Data Sources' button is not recognized!" );
    softAssert.assertTrue( isElementPresent( btnDocumentation, timeout ), "'Documentation' button is not recognized!" );
    softAssert.assertTrue( isElementPresent( gettingStarted, timeout ),
        "'Getting Started ' caption is not recognized!" );
    softAssert.assertTrue( isElementPresent( tabWelcome, timeout ), "'Welcome' tab is not recognized!" );
    softAssert.assertTrue( isElementPresent( tabSamples, timeout ), "'Samples' tab is not recognized!" );
    softAssert.assertTrue( isElementPresent( tabTutorials, timeout ), "'Tutorials' tab is not recognized!" );
    softAssert.assertTrue( isElementPresent( widgetRecents, timeout ), "'Recents' widget is not recognized!" );
    softAssert.assertTrue( isElementPresent( widgetFavorites, timeout ), "'Favorites' widget is not recognized!" );

    switchToDefault();
    softAssert.assertAll();
  }

  public DataSourceWizardPage openDataSourceWizard() {
    LOGGER.info( "Opening Data Source Wizard Page..." );
    if ( isElementPresent( btnManageDataSources, 2 ) ) {
      click( btnManageDataSources );
    } else {
      // activate through menu item
      menuPick( Menu.DATASOURCE );
    }
    return new DataSourceWizardPage( getDriver() );
  }

  public ManageDataSourcesPage openManageDataSources() {
    LOGGER.info( "Opening Manage Data Source Page..." );
    menuPick( Menu.MANAGE_DATASOURCE );
    return new ManageDataSourcesPage( getDriver() );
  }

  public AnalyzerDataSourcePage openAnalyzerDataSourcePage() {
    LOGGER.info( "Opening Analyzer Data Source Page..." );
    menuPick( Menu.ANALYSIS_REPORT );
    loading();
    PentahoUser.incOpenedTab();
    return new AnalyzerDataSourcePage( getDriver() );
  }

  public PIRDataSourcePage openPIRDataSourcePage() {
    LOGGER.info( "Opening PIR Data Source Page..." );
    menuPick( Menu.INTERACTIVE_REPORT );
    if ( getDriver().toString().contains( "firefox" ) ) {
      pause( 3 );
    }
    loading();
    PentahoUser.incOpenedTab();
    return new PIRDataSourcePage( getDriver() );
  }

  public DashboardPage openDashboardPage() {
    LOGGER.info( "Opening Dashboard Page..." );
    menuPick( Menu.DASHBOARD );
    if ( getDriver().toString().contains( "firefox" ) ) {
      pause( 3 );
    }
    loading();
    PentahoUser.incOpenedTab();
    makeClickable();
    return new DashboardPage( getDriver() );
  }

  public LoginPage logout() {
    menuPick( Menu.LOGOUT );
    return new LoginPage( getDriver() );
  }

  public void selectLanguage( String language ) {
    if ( language.equalsIgnoreCase( "de" ) ) {
      menuPick( Menu.DEUTSCH );
    } else if ( language.equalsIgnoreCase( "fr" ) ) {
      menuPick( Menu.FRENCH );
    } else if ( language.equalsIgnoreCase( "ja" ) ) {
      menuPick( Menu.JAPANESE );
    }

  }

}
