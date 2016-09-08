package com.pentaho.services.pir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.pir.PIRDataSourcePage;
import com.pentaho.qa.gui.web.pir.PIRFilterPage;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPanelPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.Filter.Condition;
import com.pentaho.services.ObjectPool;
import com.pentaho.services.Report;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.utils.ExportType;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class PIRReport extends Report {

  public static final String ARG_GROUPS = "Groups";
  public static final String ARG_COLUMNS = "Columns";

  protected List<String> groups = new ArrayList<String>();
  protected List<String> columns = new ArrayList<String>();

  protected List<PIRFilter> filters = new ArrayList<PIRFilter>();
  protected List<Prompt> prompts = new ArrayList<Prompt>();

  protected Map<String, Sort> sortColumns = new LinkedHashMap<String, Sort>();
  protected Map<String, Sort> sortGroups = new LinkedHashMap<String, Sort>();


  // internal page objects
  private PIRFilterPanelPage filterPanelPage;
  private PIRPromptPanelPage promptPanelPage;

  public PIRReport( Map<String, String> args ) {
    this( args.get( ARG_NAME ), Boolean.valueOf( args.get( ARG_HIDDEN ) ), Type.fromString( args.get( ARG_TYPE ) ),
        Boolean.valueOf( args.get( ARG_ALLOW_SCHEDULING ) ), args.get( ARG_TITLE ), args.get( ARG_DATASOUCRE ), args
            .get( ARG_ID ), args.get( ARG_COLUMNS ), args.get( ARG_GROUPS ) );
  }

  public PIRReport( String name, Boolean hidden, Type type, Boolean allowScheduling, String title, String dataSource,
      String id, String columns, String groups ) {
    super( name, hidden, type, allowScheduling, title, dataSource, id );

    if ( this.name != null && this.name.isEmpty() ) {
      this.name = PIRReportPage.NEW_IR_REPORT_NAME;
    }

    if ( columns != null && !columns.isEmpty() ) {
      this.columns.addAll( Arrays.asList( columns.split( ", " ) ) );
    }

    if ( groups != null && !groups.isEmpty() )
      this.groups.addAll( Arrays.asList( groups.split( ", " ) ) );

  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */

  public void copy( PIRReport report ) {
    super.copy( report );

    if ( report.getGroups() != null && !report.getGroups().isEmpty() )
      this.groups = report.getGroups();

    if ( report.getColumns() != null && !report.getColumns().isEmpty() )
      this.columns = report.getColumns();
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public List<String> getGroups() {
    return groups;
  }

  public void setGroups( List<String> groups ) {
    this.groups = groups;
  }

  public List<String> getColumns() {
    return columns;
  }

  public void setColumns( List<String> columns ) {
    this.columns = columns;
  }

  public List<Prompt> getPrompts() {
    return prompts;
  }

  public void setPrompts( List<Prompt> prompts ) {
    this.prompts = prompts;
  }

  public List<PIRFilter> getFilters() {
    return filters;
  }

  public void setFilters( List<PIRFilter> filters ) {
    this.filters = filters;
  }

  /* -------------- OPEN REPORT ------------------------------ */
  
  public void setReportPage( PIRReportPage reportPage ) {
    super.setPage( reportPage );
  }
  
  public PIRReportPage getReportPage() {
    return (PIRReportPage) super.getPage();
  }

  public PIRFilterPanelPage getFilterPanelPage() {
    return filterPanelPage;
  }

  public void setFilterPanelPage( PIRFilterPanelPage filterPanelPage ) {
    this.filterPanelPage = filterPanelPage;
  }

  public PIRPromptPanelPage getPromptPanelPage() {
    return promptPanelPage;
  }

  public void setPromptPanelPage( PIRPromptPanelPage promptPanelPage ) {
    this.promptPanelPage = promptPanelPage;
  }

  public PIRReportPage open() {
    super.open();

    setReportPage( new PIRReportPage( getDriver(), name ) );
    return getReportPage();
  }

  /* -------------- CREATE REPORT ------------------------------ */

  public PIRReportPage create( boolean empty ) {
    // Create PIR report
    HomePage homePage = new HomePage( getDriver() );
    PIRDataSourcePage selectDataSourcePage = homePage.openPIRDataSourcePage();

    // Select specified Data Source
    setReportPage( selectDataSourcePage.selectDataSource( dataSource ) );

    // Actions for the first time running
    getReportPage().closeGetStarted();
    getReportPage().disableHints();

    if ( !empty ) {
      // Add fields to the Workspace
      addColumns();
      addGroups();

      // reportPage.setTitle( title );
    }
    return getReportPage();
  }

  public PIRReportPage create() {
    return create( false );
  }

  /* -------------- EDIT REPORT --------------- */

  public void edit( PIRReport newReport ) {
    super.edit( newReport ); // put original report state into the ObjectPool

    activateEditMode();
    editColumns( newReport.getColumns() );
    editGroups( newReport.getGroups() );

    copy( newReport );
  }

  /* -------------- SAVE REPORT --------------- */

  public PIRReport saveAs( Folder folder ) {
    super.saveAs( folder );

    PIRReport temp = (PIRReport) this.clone();
    BrowseService.addItem( temp, folder );
    PIRReport snap =
        (PIRReport) ( ObjectPool.getSnapshot( getId() ) == null ? this : ObjectPool.getSnapshot( getId() ) );
    this.copy( snap );
    return temp;
  }

  /* -------------- VERIFY REPORT --------------- */

  public boolean verify() {
    for ( String column : columns ) {
      if ( !getReportPage().isColumnOnWorkspace( column ) ) {
        softAssert.fail( "Column '" + column + "' is not present on the Workspace!" );
        return false;
      }
    }
    for ( String group : groups ) {
      if ( !getReportPage().isGroupOnWorkspace( group ) ) {
        softAssert.fail( "Group '" + group + "' is not present on the Workspace!" );
        return false;
      }
    }
    return true;
  }

  public SoftAssert verifyFilters() {
    for ( PIRFilter filter : filters ) {
      if ( !filterPanelPage.isFilterExists( filter ) ) {
        softAssert.fail( "Filter '" + filter.toString() + "' is displayed incorrectly on Filter panel!" );
      }
      if ( !filterPanelPage.isReadableFilterCorrect( filter.getField(), filter.getCondition().getName(), filter
          .getValue() ) ) {
        softAssert.fail( "Filter '" + filter.toString() + "' is displayed incorrectly in readable form!" );
      }
    }
    return softAssert;
  }

  /* -------------- ADD/EDIT/REMOVE COLUMN OPERATIONS ----------- */

  // ADD

  public void addColumn( String newColumn ) {
    addColumn( newColumn, Workflow.CONTEXT_PANEL );
  }

  public void addColumn( String newColumn, Workflow workflow ) {
    getReportPage().addColumn( newColumn, workflow );
    columns.add( newColumn );
  }

  // Adding new columns from arguments, UI and Business action
  public void addColumns( List<String> newColumns ) {
    addColumns( newColumns, Workflow.CONTEXT_PANEL );
  }

  public void addColumns( List<String> newColumns, Workflow workflow ) {
    for ( String newColumn : newColumns ) {
      addColumn( newColumn, workflow );
    }
  }

  // Adding new columns from this.columns, only UI action
  public void addColumns() {
    addColumns( Workflow.CONTEXT_PANEL );
  }

  public void addColumns( Workflow workflow ) {
    for ( String newColumn : columns ) {
      getReportPage().addColumn( newColumn, workflow );
    }
  }

  // Adding new prompt from this.columns, only UI action
  public boolean addPrompt( Prompt prompt ) {
    return addPrompt( Workflow.CONTEXT_PANEL, prompt );
  }

  public boolean addPrompt( Workflow workflow, Prompt prompt ) {
    boolean res = false;
    getReportPage().showPromptsPanel();
    setPromptPanelPage( new PIRPromptPanelPage( getDriver() ) );
    getReportPage().addPrompt( prompt.getField(), workflow );

    // if prompt was applied, proceed to set filter in the object.
    if ( getPromptPanelPage().applyPrompt( prompt ) ) {
      filterPanelPage = new PIRFilterPanelPage( getDriver() );
      prompts.add( prompt );
      filters.add( prompt.getFilter() );
      res = true;
    }
    return res;
  }

  // EDIT
  // click on edit button on a prompt
  public PIRPromptPage editPrompt( Prompt prompt, Prompt newPrompt ) {
    return prompt.edit( newPrompt );
  }

  // edit the prompt value within the prompt panel page
  public boolean editPrompt( Prompt prompt ) {
    getReportPage().showPromptsPanel();
    return getPromptPanelPage().applyPrompt( prompt );
  }

  public void deletePrompt( Prompt prompt ) {
    prompts.remove( prompt );
    prompt.delete();
  }

  public void editColumns( List<String> newColumns ) {
    getReportPage().switchToFrame();
    editColumns( newColumns, Workflow.CONTEXT_PANEL, Workflow.CONTEXT_REPORT );
  }

  public void editColumns( List<String> newColumns, Workflow addWorkflow, Workflow removeWorkflow ) {
    // Execute logic for removal old columns
    List<String> temp = new ArrayList<String>( columns );
    for ( String oldColumn : temp ) {
      if ( !newColumns.contains( oldColumn ) ) {
        getReportPage().removeColumn( oldColumn, removeWorkflow );
        columns.remove( oldColumn );
      }
    }
    // Execute logic for adding new columns
    for ( String newColumn : newColumns ) {
      if ( !columns.contains( newColumn ) ) {
        getReportPage().addColumn( newColumn, addWorkflow );
        columns.add( newColumn );
      }
    }
  }

  public void editGroups( List<String> newGroups ) {
    getReportPage().switchToFrame();
    editGroups( newGroups, Workflow.CONTEXT_PANEL, Workflow.DND_REPORT );
  }

  public void editGroups( List<String> newGroups, Workflow addWorkflow, Workflow removeWorkflow ) {
    // Execute logic for removal of old groups
    List<String> temp = new ArrayList<String>( groups );
    for ( String oldGroup : temp ) {
      if ( !newGroups.contains( oldGroup ) ) {
        getReportPage().removeGroup( oldGroup, removeWorkflow );
        groups.remove( oldGroup );
      }
    }
    // Execute logic for adding new groups
    for ( String newGroup : newGroups ) {
      if ( !groups.contains( newGroup ) ) {
        getReportPage().addGroup( newGroup, addWorkflow );
        groups.add( newGroup );
      }
    }
  }

  // REMOVE

  public boolean removePrompt( Prompt prompt ) {
    getReportPage().showPromptsPanel();
    getPromptPanelPage().deletePrompt( prompt );
    return !getPromptPanelPage().isPromptExists( prompt );
  }

  public void removeColumn( String deleteColumn ) {
    removeColumn( deleteColumn, Workflow.CONTEXT_REPORT );
  }

  public void removeColumn( String deleteColumn, Workflow workflow ) {
    getReportPage().removeColumn( deleteColumn, workflow );
    columns.remove( deleteColumn );
  }

  public void removeColumns( List<String> columns ) {
    removeColumns( columns, Workflow.CONTEXT_REPORT );
  }

  public void removeColumns( List<String> deleteColumns, Workflow workflow ) {
    for ( Iterator<String> iter = deleteColumns.iterator(); iter.hasNext(); ) {
      String deleteColumn = iter.next();
      removeColumn( deleteColumn, workflow );
    }
  }

  /* -------------- ADD/EDIT/REMOVE GROUP OPERATIONS ----------- */

  // ADD

  // Adding new groups from arguments, UI and Business action
  public void addGroups( List<String> newGroups ) {
    addGroups( newGroups, Workflow.CONTEXT_PANEL );
  }

  public void addGroups( List<String> newGroups, Workflow workflow ) {
    for ( String newGroup : newGroups ) {
      getReportPage().addGroup( newGroup, workflow );
      groups.add( newGroup );
    }
  }

  // Adding new groups from this.groups, UI and Business action
  public void addGroups() {
    addGroups( Workflow.CONTEXT_PANEL );
  }

  public void addGroups( Workflow workflow ) {
    for ( String newGroup : groups ) {
      getReportPage().addGroup( newGroup, workflow );
    }
  }

  /* -------------- FILTER RELATED OPERATIONS ------------------------- */

  public void addFilter( PIRFilter filter, Workflow worflow ) {
    filter.create( worflow );
    filterPanelPage = new PIRFilterPanelPage( getDriver() );
    filters.add( filter );
  }

  public void addFilter( PIRFilter filter ) {
    addFilter( filter, Workflow.CONTEXT_PANEL );
  }

  public PIRFilterPage editFilter( PIRFilter filter, PIRFilter newFilter ) {
    return filter.edit( newFilter );
  }

  public PIRFilterPage editFilter( Prompt prompt, PIRFilter newFilter ) {
    return editFilter( prompt, newFilter, true );
  }

  public PIRFilterPage editFilter( Prompt prompt, PIRFilter newFilter, boolean overwrite ) {
    if ( prompt.getFilter().getParamName().equals( newFilter.getParamName() ) && overwrite ) {
      prompts.remove( prompt );
    }
    return prompt.getFilter().edit( newFilter, overwrite );
  }

  public void deleteFilter( PIRFilter filter ) {
    filters.remove( filter );
    filter.delete();
  }

  public void deleteFilter( Prompt prompt ) {
    filters.remove( prompt.getFilter() );
    prompts.remove( prompt );
    prompt.getFilter().delete();
  }

  public void indentFilter( PIRFilter filter ) {
    filterPanelPage.indentFilter( filter );
  }

  public void moveUpFilter( PIRFilter filter ) {
    filterPanelPage.moveUpFilter( filter );
  }

  /*
   * public void moveDownFilter(Filter filter) { filterPanelPage.moveDownFilter( filter ); }
   */

  /* -------------- SORTING RELATED OPERATIONS ------------------------- */

  @Override
  public void sort( Layout layout, String field, Sort sort, Workflow workflow ) {
    switch ( layout ) {
      case COLUMN:
        sortColumn( field, sort, workflow );
        break;
      case GROUP:
        // sortGroup(field, sort, workflow);
        Assert.fail( "Not implemented yet!" );
        break;
      default:
        Assert.fail( "Unsupported layout type for PIR Report: " + layout );
    }
  }

  public void sort( Layout layout, String field, Sort sort ) {
    sort( layout, field, sort, Workflow.CONTEXT_REPORT );
  }

  private void sortColumn( String field, Sort sort, Workflow workflow ) {
    if ( !sortColumns.containsKey( field ) ) {
      LOGGER.warn( "Adding sorting is possible only using popup menu!" );
      getReportPage().sortColumn( field, sort, Workflow.CONTEXT_REPORT );
      // Add sorting to the map
      sortColumns.put( field, sort );
    } else {
      getReportPage().sortColumn( field, sort, workflow );
      if ( sort == Sort.NONE ) {
        sortColumns.remove( field );
      } else {
        sortColumns.put( field, sort );
      }
    }
  }

  public void sorterUp( Layout layout, String field ) {
    // click
    // sortColumns
  }

  public void sorterDown( Layout layout, String field ) {

  }

  /* -------------- OTHER OPERATIONS ---------------------------- */

  public void exportAs( ExportType exportType ) {
    String fileName = getName() + "." + exportType.getExtension();
    Utils.clearDownloadsFile( fileName );
    PIRReportPage pirReportPage = getReportPage();

    pirReportPage.exportAs( exportType );
    // to be sure OS has managed to persist file on disc

    if ( !Utils.checkDownloadsFile( fileName ) ) {
      Assert.fail( "No file downloaded: " + fileName );
    }
    Utils.clearDownloadsFile( fileName );
  }

  public void selectAll() {
    getReportPage().selectAll();
  }

  public void setTitle() {
    getReportPage().setTitle( title );
  }

  public boolean verifyHumanReadableFilterCreatedByPrompt( String promptName, String filterName ) {
    boolean res = false;
    // EXACTLY_MATCHES_VALUE_OF_PROMPT( "Exactly matches value of Prompt" ),
    String humanReadableFilter = getFilterPanelPage().getReadableFilter();

    String condition = Condition.EXACTLY_MATCHES.getName();

    String[] params = { filterName };

    String filterValue = L10N.formatString( Condition.VALUE_OF_PROMPT.getName(), params );
    res = humanReadableFilter.equals( promptName + " " + condition + " " + filterValue );

    return res;
  }

  public void editColumnHeaderFormattingInPaletteTab( String columnHeader, Colour textColor ) {
    editColumnHeaderFormatting( null, columnHeader, textColor, null );
  }

  public void editColumnHeaderFormattingInPaletteTab( String columnHeader, Colour textColor, Colour backgroundColor ) {
    editColumnHeaderFormatting( null, columnHeader, textColor, backgroundColor );
  }

  public void editColumnHeaderFormattingInCustomTab( CustomColor type, String columnHeader, Colour textColor ) {
    editColumnHeaderFormatting( type, columnHeader, textColor, null );
  }

  public void editColumnHeaderFormattingInCustomTab( CustomColor type, String columnHeader, Colour textColor,
      Colour backgroundColor ) {
    editColumnHeaderFormatting( type, columnHeader, textColor, backgroundColor );
  }

  private void editColumnHeaderFormatting( CustomColor type, String column, Colour textColor, Colour backGroundColor ) {
    PIRReportPage pirReportPage = getReportPage();
    pirReportPage.openFormattingTab( column );

    if ( textColor != null ) {
      pirReportPage.openTextColorPicker();
      if ( type != null ) {
        pirReportPage.clickCustomTab();
        switch ( type ) {
          case RGB:
            pirReportPage.setRgbColor( textColor );
            break;
          case HEX:
            pirReportPage.setHexColor( textColor );
            break;
          case COLOR_FIELD:
            // [MG] hard coded value as there is no way to programmatically determine colors on the Hue scale.
            pirReportPage.setHue( -50 );
            pirReportPage.setSaturation();
            break;
        }
      } else {
        pirReportPage.clickPaletteTab();
        pirReportPage.clickColor( textColor );
      }
      verifyCurrentColor( textColor );
    }

    if ( backGroundColor != null ) {
      pirReportPage.openBackgroundColorPicker();
      if ( type != null ) {
        pirReportPage.clickCustomTab();
        switch ( type ) {
          case RGB:
            pirReportPage.setRgbColor( backGroundColor );
            break;
          case HEX:
            pirReportPage.setHexColor( backGroundColor );
            break;
          case COLOR_FIELD:
            // [MG] hard coded value as there is no way to easily determine colors on the Hue scale
            pirReportPage.setHue( -50 );
            pirReportPage.setSaturation();
            break;
        }
      } else {
        pirReportPage.clickPaletteTab();
        pirReportPage.clickColor( backGroundColor );
      }
      verifyCurrentColor( backGroundColor );
    }
  }

  public void verifyDefaultFormattingColors() {
    PIRReportPage pirReportPage = getReportPage();
    // the default color is white.
    Colour defaultColor = Colour.WHITE;
    Colour textColor = pirReportPage.getForeColorSample();
    Colour backgroundColor = pirReportPage.getBackColorSample();
    // verify that text and background color default to white
    if ( !textColor.equals( defaultColor ) || !backgroundColor.equals( defaultColor ) ) {
      Assert.fail( "TS064011: Column text and background color did not default to white!" );
    }
  }

  public void verifyColumnFormatting( String column ) {
    PIRReportPage pirReportPage = getReportPage();
    pirReportPage.openFormattingTab( column );
    // verify text color formatting
    Colour columnTextColor = pirReportPage.getColumnHeaderTextColor( column );
    pirReportPage.clickTextColorDownArrow();
    verifyCurrentColor( columnTextColor );
    // verify background color formatting
    Colour columnBackground = pirReportPage.getColumnBackgroundColor( column );
    pirReportPage.clickBackgroundColorDownArrow();
    verifyCurrentColor( columnBackground );
  }

  public void verifyCurrentColor( Colour expectedColor ) {
    PIRReportPage pirReportPage = getReportPage();
    Colour currentColor = pirReportPage.getCurrentColor();
    // check if the current color selected matches the color we applied
    if ( !currentColor.equals( expectedColor ) ) {
      CustomAssert.fail( "64013", "Current color does not match selected color!" );
      CustomAssert.fail( "64015", "Current color does not match selected color!" );
      CustomAssert.fail( "64016", "Current column color does not match with the color picker values!" );
      CustomAssert.fail( "64017", "Current column color does not match with the color picker values!" );
      CustomAssert.fail( "64018", "Current color does not match the custom rgb value set!" );
      CustomAssert.fail( "64019", "Current color does not match the custom hex value set!" );
      CustomAssert.fail( "64021", "Current color does not match the custom color set!" );
      CustomAssert.fail( "64023", "Current background color does not match the expected color!" );
    }
    // verify that color was added to the "In Use" list
    if ( !isColorInUse( expectedColor ) ) {
      CustomAssert.fail( "64014", "Selected color is not present in the 'In Use' list!" );
      CustomAssert.fail( "64016", "Current column color is not in the In Use list!" );
      CustomAssert.fail( "64017", "Current column color is not in the In Use list!" );
      CustomAssert.fail( "64018", "Custom rgb color is not in the In Use list!" );
      CustomAssert.fail( "64019", "Custom hex color is not in the In Use list!" );
      CustomAssert.fail( "64023", "Custom background color is not in the In Use list!" );
    }
  }

  private boolean isColorInUse( Colour color ) {
    // check to see if newly selected color is not in the "In Use" list
    PIRReportPage pirReportPage = getReportPage();
    return pirReportPage.isColorInUse( color );
  }

  @Override
  protected Object clone() {
    PIRReport cloned;
    cloned = (PIRReport) super.clone();

    List<String> groupsTmp = new ArrayList<String>();
    groupsTmp.addAll( this.groups );

    List<String> columnsTmp = new ArrayList<String>();
    columnsTmp.addAll( this.columns );

    cloned.setGroups( groupsTmp );
    cloned.setColumns( columnsTmp );
    return cloned;
  }

}
