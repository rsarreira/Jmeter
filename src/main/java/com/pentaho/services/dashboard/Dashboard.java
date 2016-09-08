package com.pentaho.services.dashboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.dashboard.DashboardPage;
import com.pentaho.qa.gui.web.dashboard.DashboardTemplate;
import com.pentaho.qa.gui.web.dashboard.DashboardTheme;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.Report;
import com.pentaho.services.Report.Workflow;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class Dashboard extends File {
  private final static String ARG_REPORTS = "reports";
  List<Report> reports = new ArrayList<Report>();
  HashMap<Report, Integer> reportPlacement = new HashMap<Report, Integer>();

  // internal page objects
  // private DashboardPage dashboardPage;

  public Dashboard( Map<String, String> args ) {
    this( args.get( ARG_NAME ), Boolean.valueOf( args.get( ARG_HIDDEN ) ), args.get( ARG_ID ) );

    // add to reports all valid reports from BrowseService
    String reportsArg = args.get( ARG_REPORTS );
    if ( reportsArg != null && !reportsArg.isEmpty() ) {
      String[] reportNames = reportsArg.split( "," );
      for ( int i = 0; i < reportNames.length; i++ ) {
        String reportName = reportNames[i].trim();
        Report report = (Report) BrowseService.getTreeNodeByName( reportName ).getItem();
        reports.add( report );
        reportPlacement.put( report, i );
      }
    }

    // TODO: add additional properties aka template, theme etc
  }

  public Dashboard( String name, Boolean hidden, String id ) {
    super( L10N.getText( name ), hidden, Type.XDASH, false, id );

    if ( name != null && name.isEmpty() ) {
      name = DashboardPage.NEW_DASHBOARD_NAME;
    }
  }

  public Dashboard( String name, Boolean hidden ) {
    this( name, hidden, String.valueOf( INVALID_ID ) );
  }

  public void setDashboardPage( DashboardPage dashboardPage ) {
    super.setPage( dashboardPage );
  }

  public DashboardPage getDashboardPage() {
    return (DashboardPage) super.getPage();
  }

  /* -------------- CREATE DASHBOARD ------------------------------ */

  public DashboardPage create() {
    // Create dashboard
    HomePage homePage = new HomePage( getDriver() );
    setDashboardPage( homePage.openDashboardPage() );

    return getDashboardPage();
  }

  public void insert( Report report, int index ) {
    // insert report into the appropriate part of dashboard using CONTEXT_PANEL Workflow
    insert( report, index, Workflow.CONTEXT_PANEL );
  }

  public void insert( Report report, int index, Workflow workflow ) {
    // insert report into the appropriate part of dashboard using appropriate Workflow

    if ( !report.isValid() ) {
      Assert.fail( "Unable to insert invalid/non-existed report to Dashboard!" );
    }

    // TODO: implement insert operation

    // Verification
    if ( isReportExists( report, index ) ) {
      reports.add( report );
      reportPlacement.put( report, index );
    }
  }

  private boolean isReportExists( Report report ) {
    // verify report presence in any place of Dashboard
    Integer index = reportPlacement.get( report );
    if ( index != null ) {
      // verify with placement
      return isReportExists( report, index );
    } else {
      // TODO: verify just presence
    }
    return true;
  }

  private boolean isReportExists( Report report, int index ) {
    // TODO: verify report presence in appropriate place of Dashboard
    return true;
  }

  /**
   * Opens the templates tab within the general settings of the dashboard.
   */
  public void openTemplateList() {
    DashboardPage page = getDashboardPage();
    page.clickGeneralSettings();
    page.clickTemplatesTab();
  }

  /**
   * Opens the templates tab and selects the specified template.
   * 
   * @param template
   *          The template to select
   */
  public void setTemplate( DashboardTemplate template ) {
    openTemplateList();
    getDashboardPage().clickTemplate( template );
  }

  /**
   * Opens the themes tab within the general settings of the dashboard.
   */
  public void openThemesList() {
    DashboardPage page = getDashboardPage();
    page.clickGeneralSettings();
    page.clickThemesTab();
  }

  /**
   * Opens the themes tab and selects the specified theme.
   * 
   * @param theme
   *          The theme to select.
   */
  public void setTheme( DashboardTheme theme ) {
    openThemesList();
    getDashboardPage().clickTheme( theme );
  }

  /**
   * Opens the properties tab within the general settings of the dashboard.
   */
  public void openProperties() {
    DashboardPage page = getDashboardPage();
    page.clickGeneralSettings();
    page.clickPropertiesTab();
  }

  /**
   * Sets the title of the dashboard page.
   * 
   * @param title
   *          The new title for the dashboard page.
   */
  public void setPageTitle( String title ) {
    DashboardPage page = getDashboardPage();
    openProperties();
    page.setPageTitle( title );

    // Losing focus on the input control will cause the title to be updated.
    page.clickPropertiesTab();

  }

  /**
   * Click the insert chart link for the specified dashboard widget.
   * 
   * @param widgetTitle
   *          The title of the dashboard widget to insert a chart into.
   */
  public void insertChart( String widgetTitle ) {
    DashboardPage page = getDashboardPage();
    page.clickInsertContent( widgetTitle );
    page.clickInsertChart();
    // TODO: return an instance of the data source page. This may require reusing/refactoring PIRDataSourcePage or
    // making a separate page entirely.
  }

  /**
   * Click the insert data table link for the specified dashboard widget.
   * 
   * @param widgetTitle
   *          The title of the dashboard widget to insert a data table into.
   */
  public void insertDataTable( String widgetTitle ) {
    DashboardPage page = getDashboardPage();
    page.clickInsertContent( widgetTitle );
    page.clickInsertDataTable();
    // TODO: return an instance of the query editor page, which has not yet been implemented.
  }

  /**
   * Inserts a URL item for the specified dashboard widget.
   * 
   * @param widgetTitle
   *          The title of the dashboard widget to insert the URL into.
   * @param url
   *          The URL to insert.
   */
  public void insertUrl( String widgetTitle, String url ) {
    DashboardPage page = getDashboardPage();
    page.clickInsertContent( widgetTitle );
    page.clickInsertUrl();
    page.inputUrl( url );
    page.clickOk();
  }

  /**
   * Navigates the browse tree to the specified path.
   * 
   * @param path
   *          The path of the folder to navigate to. This should not contain a file name.
   */
  private void navigateToFile( String path ) {
    DashboardPage page = getDashboardPage();
    LOGGER.info( "Navigating to path '" + path + "'." );

    List<String> folders = Utils.parsePath( path );

    for ( int i = 0; i < folders.size(); i++ ) {
      String folder = folders.get( i );

      // BISERVER-13346: The English locale has 'home' with as lower case, but the other locales have a capital 'H'.
      if ( folder.equals( "home" ) ) {
        folder = L10N.getText( folder );
      }

      // Expand the folder if it is not the final destination of the path.
      if ( i + 1 < folders.size() ) {
        if ( !page.isFolderExpanded( folder ) ) {
          LOGGER.info( "Expanding folder '" + folder + "'." );
          page.doubleClickFolder( folder );

          if ( page.isFolderExpanded( folder ) ) {
            LOGGER.info( "Folder expanded successfully." );
          } else {
            LOGGER.error( "Folder failed to expand!" );
          }
        }
      } else {
        page.clickFolder( folder );
      }
    }
  }

  /**
   * Navigates to the specified folder and drags and drops the specified file to a dashboard widget.
   * 
   * @param path
   *          The path of the folder that contains the file.
   * @param fileName
   *          The name of the file to drag to the dashboard widget.
   * @param widgetTitle
   *          The title of the destination widget.
   */
  public void dragFileToWidget( String path, String fileName, String widgetTitle ) {
    navigateToFile( path );
    getDashboardPage().dragFileToWidget( fileName, widgetTitle );
  }

  /**
   * Determines whether or not the discard contents warning dialog is present with the expected text.
   * 
   * @return Returns true when all expected elements of the dialog are present.
   */
  public boolean isDiscardContentWarningPresent() {
    DashboardPage page = getDashboardPage();
    boolean isPresent = true;

    if ( page.isDialogWindowPresent() ) {
      LOGGER.info( "The dialog window is present." );

      // Only check for the labels if the dialog is present.
      if ( page.isWarningLabelPresent() ) {
        LOGGER.info( "The warning label is present." );
      } else {
        isPresent = false;
        LOGGER.error( "The warning label is not present!" );
      }

      if ( page.isDiscardContentLabelPresent() ) {
        LOGGER.info( "The discard content label is present" );
      } else {
        isPresent = false;
        LOGGER.error( "The discard content label is not present!" );
      }
    } else {
      isPresent = false;
      LOGGER.info( "The dialog window is not present!" );
    }

    return isPresent;
  }

  /**
   * Enables or disables full screen mode for the specified dashboard widget.
   * 
   * @param id
   *          The value of the dashboard widget's id attribute in the DOM.
   */
  public void setWidgetFullScreen( String id ) {
    activateEditMode();
    getDashboardPage().doubleClickDashboardWidgetTitle( id );
  }

  /**
   * Enables content linking for all specified fields for the widget.
   * 
   * @param id
   *          The value of the widget's id attribute.
   * @param fields
   *          The list of fields to enable content linking for.
   */
  public void enableContentLinking( String id, String... fields ) {
    setContentLinking( id, true, fields );
  }

  /**
   * Disables content linking for all specified fields for the widget.
   * 
   * @param id
   *          The value of the widget's id attribute.
   * @param fields
   *          The list of fields to disable content linking for.
   */
  public void disableContentLinking( String id, String... fields ) {
    setContentLinking( id, false, fields );
  }

  /**
   * Enables or disables content linking for all specified fields for the widget.
   * 
   * @param id
   *          The value of the widget's id attribute.
   * @param enable
   *          Used to determine whether to enable or disable content linking for the fields. When true, content linking
   *          will be enabled.
   * @param fields
   *          The list of fields to set content linking for.
   */
  private void setContentLinking( String id, boolean enable, String... fields ) {
    DashboardPage page = getDashboardPage();

    // Open the widget's settings and click on the content linking tab.
    page.clickWidgetSettings( id );
    page.clickContentLinkingTab();

    // Enable or disable content linking for all fields.
    for ( String field : fields ) {
      if ( enable ) {
        LOGGER.info( "Enabling content linking for field '" + field + "'." );
        page.checkContentLinking( field );
      } else {
        LOGGER.info( "Disabling content linking for field '" + field + "'." );
        page.uncheckContentLinking( field );
      }
    }

    page.clickApply();
  }

  /**
   * Selects the specified source value in the widget's settings parameters tab.
   * 
   * @param widgetId
   *          The value of the id attribute for the widget that contains the parameter to be set.
   * @param sourceWidgetId
   *          The value of the id attribute for the widget that is the source. This will define which value in the
   *          drop-down is set.
   * @param parameterName
   *          The name of the parameter that is to be set.
   * @param sourceFieldName
   *          The name of the field that has been enabled for content linking. This will define which value in the
   *          drop-down is set.
   */
  public void setParameterSource( String widgetId, String sourceWidgetId, String parameterName,
      String sourceFieldName ) {
    DashboardPage page = getDashboardPage();

    page.clickWidgetSettings( widgetId );
    page.clickParametersTab();
    page.clickSourceDropDown( parameterName );
    page.clickSourceDropDownValue( sourceWidgetId, sourceFieldName );
    page.clickApply();
  }

  /**
   * Clicks the Add Parameters to Title button for the specified widget.
   * 
   * @param id
   *          The value of the id attribute for the widget to click the button for.
   */
  public void addParametersToWidgetTitle( String id ) {
    DashboardPage page = getDashboardPage();

    page.clickWidgetSettings( id );
    page.clickAddParametersToTitleButton();
    page.clickApply();
  }
}
