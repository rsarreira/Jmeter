package com.pentaho.qa.gui.web.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceDBTablePage extends DataSourceWizardPage {
  public enum TablesSource {
    AVAILABLE_TABLES, SELECTED_TABLES;
  }

  private static final String DB_TABLES_PAGE_SELECT_TABLES_NAME = L10N.getText( "multitable.SELECT_TABLES" ); // "Select
                                                                                                              // Tables";
  private static final String DB_TABLES_PAGE_DEFINE_JOINS_NAME = L10N.getText( "multitable.DEFINE_JOINS" ); // "Define
                                                                                                            // Joins";
  private static final String DB_TABLES_PAGE_DUPLICATE_JOINS_ERROR_MESSAGE =
      L10N.getText( "multitable.DUPLICATE_JOIN_ERROR" );
  private static final String DB_TABLES_PAGE_SELF_JOIN_ERROR_MESSAGE = L10N.getText( "multitable.SELF_JOIN_ERROR" );

  @FindBy( xpath = "//label[@for='%s']" )
  private ExtendedWebElement scopeRadioButton;

  @FindBy( xpath = "//label[text()='%s']" )
  private ExtendedWebElement dsType;

  private static final String DS_TYPE_REPORTING_ONLY = L10N.getText( "multitable.REPORTING_METADATA_LABEL" ); // "Reporting
                                                                                                              // only";
  private static final String DS_TYPE_REPORTING_AND_ANALYSIS =
      L10N.getText( "multitable.REPORTING_METADATA_ANALYSIS_LABEL" ); // "Reporting and Analysis (Requires Star
                                                                      // Schema)";

  // Select Tables page
  @FindBy( css = "#availableTables .custom-list-item-selected .gwt-Label" )
  private ExtendedWebElement txtAvailableTable;

  @FindBy( css = "#selectedTables .custom-list-item-selected .gwt-Label" )
  private ExtendedWebElement txtSelectedTable;

  @FindBy( xpath = "//table[@id='availableTables']//div[@class='gwt-Label' and contains(text(), '%s')]" )
  private ExtendedWebElement availableTableItem;

  @FindBy( xpath = "//table[@id='selectedTables']//div[@class='gwt-Label' and contains(text(), '%s')]" )
  private ExtendedWebElement selectedTableItem;

  @FindBy( css = "#availableTables .gwt-Label" )
  private List<ExtendedWebElement> availableTables;

  @FindBy( css = "#selectedTables .gwt-Label" )
  private List<ExtendedWebElement> selectedTables;

  // TODO: maybe combine schemaTable and selectedSchema
  // @FindBy( id = "schemas" )
  @FindBy( css = "#schemas .gwt-Label" )
  protected ExtendedWebElement schemaTable;

  @FindBy( css = "#schemas .gwt-Label" )
  protected ExtendedWebElement selectedSchema;

  @FindBy( css = "#metadataRadioGrup [type=radio]" )
  protected List<ExtendedWebElement> scopeRadioButtons;

  @FindBy( css = "img.pentaho-chevron[aria-hidden=false]" )
  protected ExtendedWebElement pageArrow;

  @FindBy( css = "#schemaTableVbox .gwt-Label" )
  protected ExtendedWebElement txtSelectedSchema;

  @FindBy( css = "#addButton > img" )
  protected ExtendedWebElement addTableButton;

  @FindBy( css = "#removeButton > img" )
  protected ExtendedWebElement removeTableButton;

  // Define Joins page
  @FindBy( css = "#leftTables .combo-arrow" )
  protected ExtendedWebElement comboArrowLeftTable;

  @FindBy( css = "#rightTables .combo-arrow" )
  protected ExtendedWebElement comboArrowRightTable;

  @FindBy( css = "#leftKeyField .gwt-Label" )
  protected List<ExtendedWebElement> leftKeyFields;

  @FindBy( css = "#rightKeyField .gwt-Label" )
  protected List<ExtendedWebElement> rightKeyFields;

  @FindBy( xpath = "//table[@id='leftKeyField']//div[@class='gwt-Label' and text()='%s']" )
  private ExtendedWebElement joinLeftTableItem;

  @FindBy( xpath = "//table[@id='rightKeyField']//div[@class='gwt-Label' and text()='%s']" )
  private ExtendedWebElement joinRightTableItem;

  @FindBy( css = "#leftTables .gwt-Label" )
  protected ExtendedWebElement txtSelectedLeftTable;

  @FindBy( css = "#rightTables .gwt-Label" )
  protected ExtendedWebElement txtSelectedRightTable;

  @FindBy( css = "#leftKeyField .custom-list-item-selected .gwt-Label" )
  protected ExtendedWebElement txtSelectedLeftField;

  @FindBy( css = "#rightKeyField .custom-list-item-selected .gwt-Label" )
  protected ExtendedWebElement txtSelectedRightField;

  @FindBy( xpath = "//*[@id='errorDialog_accept']" )
  protected ExtendedWebElement btnJoinsErrorClose;

  @FindBy( id = "errorLabel" )
  public ExtendedWebElement joinsErrorLabel;

  @FindBy( id = "createJoinButton" )
  protected ExtendedWebElement createJoinButton;

  @FindBy( id = "deleteJoinButton" )
  protected ExtendedWebElement deleteJoinButton;

  @FindBy( css = "#joins .custom-list-item .gwt-Label" )
  protected List<ExtendedWebElement> joins;

  @FindBy( css = "#joins .custom-list-item-selected .gwt-Label" )
  protected ExtendedWebElement joinSelected;

  @FindBy( css = "#factTables .gwt-Label" )
  protected ExtendedWebElement factTables;

  @FindBy( css = "#factTables .gwt-Label" )
  protected ExtendedWebElement selectedFact;

  public DataSourceDBTablePage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "'DB Tables' data source type is not recognized!" );
    }
  }

  protected boolean isOpened() {
    return getSelectedDataSourceType().equals( SOURCE_TYPE_DB_TABLES );
  }

  public void setReportingOnly( Boolean reportingOnly ) {
    if ( reportingOnly == null ) {
      return;
    }

    if ( reportingOnly ) {
      click( format( dsType, DS_TYPE_REPORTING_ONLY ) );
    } else {
      click( format( dsType, DS_TYPE_REPORTING_AND_ANALYSIS ) );
    }
  }

  private String getSelectedScopeRadioButtonName() {
    String id = null;

    for ( int i = 0; i < scopeRadioButtons.size(); i++ ) {
      if ( scopeRadioButtons.get( i ).getElement().isSelected() ) {
        id = scopeRadioButtons.get( i ).getElement().getAttribute( "id" );
      }
    }

    return format( scopeRadioButton, id ).getText();
    // return labelItem.getText();
  }

  public boolean isScopeRadioButtonReportingOnlySelected() {
    return ( getSelectedScopeRadioButtonName().equals( DS_TYPE_REPORTING_ONLY ) );
  }

  public boolean isPageSelectTablesSelected() {
    return getSelectedPageName().equals( DB_TABLES_PAGE_SELECT_TABLES_NAME );
  }

  public boolean isPageDefineJoinsSelected() {
    return getSelectedPageName().equals( DB_TABLES_PAGE_DEFINE_JOINS_NAME );
  }

  // Select Tables wizard page
  public boolean selectTables( List<String> tables ) {
    return selectTables( tables, false );
  }

  public boolean selectTable( String table ) {
    boolean res = true;
    chooseTable( table, TablesSource.AVAILABLE_TABLES, false );
    addSelectedTables();

    if ( !isTableSelected( table ) ) {
      LOGGER.error( table + " table was not moved under 'Selected Tables' section!" );
      res = false;
    }
    return res;
  }

  public void selectAllTables() {
    chooseAllTables( TablesSource.AVAILABLE_TABLES );
    addSelectedTables();
  }

  public boolean selectTables( List<String> tables, boolean multiply ) {
    if ( tables == null ) {
      return true;
    }

    if ( multiply ) {
      chooseTables( tables, TablesSource.AVAILABLE_TABLES );
      addSelectedTables();
    } else {
      for ( String table : tables ) {
        chooseTable( table, TablesSource.AVAILABLE_TABLES, false );
        addSelectedTables();
      }
    }

    // verification part
    boolean res = true;

    for ( String table : tables ) {
      if ( !isTableSelected( table ) ) {
        LOGGER.error( table + " table was not moved under 'Selected Tables' section!" );
        res = false;
      } else {
        LOGGER.info( table + " table was moved successfully into 'Selected Tables' section." );
      }
    }
    return res;
  }

  public List<String> getSelectedTables() {
    List<String> selectedTablesList = new ArrayList<String>();

    for ( int i = 0; i < selectedTables.size(); i++ ) {
      selectedTablesList.add( parse( selectedTables.get( i ).getText(), "\\." ).get( 1 ).replaceAll( "\"", "" ) );
    }

    return selectedTablesList;
  }

  private boolean isTableSelected( String table ) {
    if ( isElementPresent( txtSelectedTable, 2 ) ) {
      return format( EXPLICIT_TIMEOUT / 15, selectedTableItem, table ).getElement() != null;
    }

    return false;
  }

  public boolean chooseAllTables( TablesSource source ) {
    // get first and last table names and choose all of them using multiply selection
    List<ExtendedWebElement> elements = null;
    ExtendedWebElement element = null;
    switch ( source ) {
      case AVAILABLE_TABLES:
        elements = availableTables;
        element = txtAvailableTable;
        break;
      case SELECTED_TABLES:
        elements = selectedTables;
        element = txtSelectedTable;
        break;
    }
    if ( isElementPresent( element, 2 ) ) {
      if ( !element.getText().isEmpty() ) {
        List<String> tables = new ArrayList<String>();
        for ( int i = 0; i < elements.size(); i++ ) {
          tables.add( elements.get( i ).getText() );
        }
        chooseTables( tables, source );
      }
    }
    return true;
  }

  private void chooseTables( List<String> tables, TablesSource source ) {
    int i = 0;
    for ( String table : tables ) {
      if ( i++ == 0 ) {
        chooseTable( table, source, false );
      } else {
        chooseTable( table, source, true );
      }
    }
  }

  private void chooseTable( String table, TablesSource source, boolean multiply ) {
    ExtendedWebElement element = null;
    switch ( source ) {
      case AVAILABLE_TABLES:
        element = availableTableItem;
        break;
      case SELECTED_TABLES:
        element = selectedTableItem;
        break;
    }

    if ( multiply ) {
      LOGGER.info( "Choosing '" + table + "' table in 'Available Tables' using Ctrl." );
      Actions ctrlClick = new Actions( driver );
      ctrlClick.keyDown( Keys.CONTROL ).click( format( element, table ).getElement() ).keyUp( Keys.CONTROL ).perform();
    } else {
      LOGGER.info( "Choosing '" + table + "' table in 'Available Tables'." );
      // click and choose table from the 'Available Tables' section
      click( format( element, table ) );
    }
  }

  private void addSelectedTables() {
    click( addTableButton );
  }

  public boolean deselectTables( List<String> tables ) {
    return deselectTables( tables, false );
  }

  public boolean deselectTable( String table ) {
    List<String> tables = Arrays.asList( table );
    return deselectTables( tables, false );
  }

  public boolean deselectTables( List<String> tables, boolean multiply ) {
    if ( tables == null ) {
      return true;
    }

    if ( multiply ) {
      chooseTables( tables, TablesSource.SELECTED_TABLES );
      removeSelectedTables();
    } else {
      for ( String table : tables ) {
        chooseTable( table, TablesSource.SELECTED_TABLES, false );
        removeSelectedTables();
      }
    }

    /*
     * int i = 0; for (String table : tables) { //first click should be without Ctrl obligatory otherwise selected by
     * default element will be chosen too if (i++ == 0) chooseTable(table, TablesSource.SELECTED_TABLES, false); else
     * chooseTable(table, TablesSource.SELECTED_TABLES, multiply);
     * 
     * 
     * if (!multiply) { // remove each single table into selected list removeSelectedTables(); } }
     * 
     * if (multiply) { //remove all multiply tables using one click removeSelectedTables(); }
     */

    // verification part
    boolean res = true;
    for ( String table : tables ) {
      if ( isTableSelected( table ) ) {
        LOGGER.error( table + " table was not removed from 'Selected Tables' section!" );
        res = false;
      } else {
        LOGGER.info( table + " table was removed successfully from 'Selected Tables' section." );
      }
    }
    return res;
  }

  public void deselectAllTables() {
    chooseAllTables( TablesSource.SELECTED_TABLES );
    removeSelectedTables();
  }

  private void removeSelectedTables() {
    click( removeTableButton );
  }

  private List<String> parse( String joinString, String separator ) {
    return new ArrayList<String>( Arrays.asList( joinString.split( separator ) ) );
  }

  private String getSelectedPageName() {
    return pageArrow.getAttribute( "id" );
  }

  public String getSelectedSchema() {
    // Additional action for make this method workable because sometimes it doesn't have time to load
    if ( !isElementPresent( txtSelectedSchema, EXPLICIT_TIMEOUT / 2 ) ) {
      LOGGER.warn( "'Schema' dropdown was not loaded!" );
    }

    return txtSelectedSchema.getText();
  }

  public String getSelectedLeftTable() { // need separate public method to check selected values by default
    return txtSelectedLeftTable.getText();
  }

  public String getSelectedRightTable() { // need separate public method to check selected values by default
    return txtSelectedRightTable.getText();
  }

  private boolean selectLeftTable( String tableName ) {
    if ( getSelectedLeftTable().contains( tableName ) ) {
      return true;
    }

    selectPopup( comboArrowLeftTable, tableName );
    if ( !getSelectedLeftTable().contains( tableName ) ) {
      LOGGER.error( "'" + tableName + "' table was not selected in the 'Left Table' drop down!" );
      return false;
    }
    return true;
  }

  private boolean selectRightTable( String tableName ) {
    if ( getSelectedRightTable().contains( tableName ) ) {
      return true;
    }

    selectPopup( comboArrowRightTable, tableName );

    if ( !getSelectedRightTable().contains( tableName ) ) {
      LOGGER.error( "'" + tableName + "' table was not selected in the 'Right Table' drop down!" );
      return false;
    }
    return true;
  }

  /**
   * Create join
   * 
   * @param join
   *          should be in format "left_table.left_field, right_table.right_field"
   * @return true if join is created otherwise false
   */
  private boolean createJoin( String join ) {
    LOGGER.info( "Creating join: " + join );

    boolean res = true;
    List<String> pairItems = parse( join, "," );

    String leftTable = parse( pairItems.get( 0 ), "\\." ).get( 0 );
    String leftField = parse( pairItems.get( 0 ), "\\." ).get( 1 );
    String rightTable = parse( pairItems.get( 1 ), "\\." ).get( 0 );
    String rightField = parse( pairItems.get( 1 ), "\\." ).get( 1 );

    // Select value for Left table
    res &= selectLeftTable( leftTable );

    // Select value for Left field
    res &= selectLeftJoinField( leftField );

    // Select value for Right table
    res &= selectRightTable( rightTable );

    // Select value for Right field
    res &= selectRightJoinField( rightField );

    click( createJoinButton );

    res &= isJoinExist( leftTable, leftField, rightTable, rightField );

    return res;
  }

  public boolean selectLeftJoinField( String fieldName ) {
    ExtendedWebElement fieldItem = format( joinLeftTableItem, fieldName );

    click( fieldItem );

    if ( !getSelectedLeftField().equals( fieldItem.getText() ) ) {
      LOGGER.error( "'" + fieldItem.getText() + "' field was not selected in the join left 'Key Field' list!" );
      return false;
    }
    return true;
  }

  public boolean selectRightJoinField( String fieldName ) {
    ExtendedWebElement fieldItem = format( joinRightTableItem, fieldName );
    click( fieldItem );

    if ( !getSelectedRightField().equals( fieldItem.getText() ) ) {
      LOGGER.error( "'" + fieldItem.getText() + "' field was not selected in the join right 'Key Field' list!" );
      return false;
    }
    return true;
  }

  public String getSelectedLeftField() {
    return txtSelectedLeftField.getText();
  }

  public String getSelectedRightField() {
    return txtSelectedRightField.getText();
  }

  public List<String> getJoins() {
    List<String> joins = new ArrayList<String>();
    List<String> tempJoins = new ArrayList<String>();
    String tempJoin;
    String actualJoin = "";

    for ( ExtendedWebElement join : this.joins ) {
      // split the string in two and remove -INNER JOIN-
      tempJoins = parse( join.getText(), "- " + L10N.getText( "multitable.INNER_JOIN" ) + " -" );
      // clean up each element of the list
      for ( int i = 0; i < tempJoins.size(); i++ ) {
        // remove unnecessary quotes, spaces and the schema name
        tempJoin = tempJoins.get( i ).replaceAll( "\"", "" ).replaceFirst( "^[^.]*.", "" ).trim();
        // put the join back together and add a comma in between so it matches the way we provide it in the xls file
        if ( i == 0 ) {
          actualJoin = tempJoin + ",";
        } else {
          actualJoin += tempJoin;
        }
      }
      joins.add( actualJoin );
    }

    return joins;
  }

  private boolean isJoinExist( String leftTable, String leftField, String rightTable, String rightField ) {
    String joinValue;
    boolean res = false;

    for ( ExtendedWebElement joinItem : joins ) {
      joinValue = joinItem.getText();
      if ( ( joinValue.contains( leftTable ) && joinValue.contains( leftField ) && joinValue.contains( rightTable )
          && joinValue.contains( rightField ) ) ) {
        res = true;
      }
    }
    return res;
  }

  public boolean selectJoin() {
    if ( joins.size() > 0 ) {
      click( joins.get( 0 ) );
      return true;
    }
    return false;
  }

  public void selectJoin( String join ) {
    LOGGER.info( "Selecting join: " + join );

    List<String> pairItems = parse( join, "," );

    String leftTable = parse( pairItems.get( 0 ), "\\." ).get( 0 );
    String leftField = parse( pairItems.get( 0 ), "\\." ).get( 1 );
    String rightTable = parse( pairItems.get( 1 ), "\\." ).get( 0 );
    String rightField = parse( pairItems.get( 1 ), "\\." ).get( 1 );

    if ( !isJoinExist( leftTable, leftField, rightTable, rightField ) ) {
      LOGGER.error( "Join '" + join + "' doesn't exist!" );
      return;
    }

    if ( isJoinSelected( leftTable, leftField, rightTable, rightField ) ) {
      return;
    }

    String joinValue;
    for ( ExtendedWebElement joinItem : joins ) {
      joinValue = joinItem.getText();
      if ( ( joinValue.contains( leftTable ) && joinValue.contains( leftField ) && joinValue.contains( rightTable )
          && joinValue.contains( rightField ) ) ) {
        click( joinItem );
      }
    }
  }

  private boolean isJoinSelected( String leftTable, String leftField, String rightTable, String rightField ) {
    String selectedJoinValue = joinSelected.getText();
    return ( selectedJoinValue.contains( leftTable ) && selectedJoinValue.contains( leftField ) && selectedJoinValue
        .contains( rightTable ) && selectedJoinValue.contains( rightField ) );
  }

  /**
   * Deletes join
   * 
   * @param join
   *          should in format "left_table.left_field, right_table.right_field"
   * @return true if join is deleted otherwise false
   */
  public boolean deleteJoin( String join ) {
    selectJoin( join );

    LOGGER.info( "Deletion join: " + join );

    List<String> pairItems = parse( join, "," );

    String leftTable = parse( pairItems.get( 0 ), "\\." ).get( 0 );
    String leftField = parse( pairItems.get( 0 ), "\\." ).get( 1 );
    String rightTable = parse( pairItems.get( 1 ), "\\." ).get( 0 );
    String rightField = parse( pairItems.get( 1 ), "\\." ).get( 1 );

    click( deleteJoinButton );
    return !isJoinExist( leftTable, leftField, rightTable, rightField );
  }

  public void deleteJoin() {
    click( deleteJoinButton );
  }

  public void createJoins( List<String> joins ) {
    if ( joins == null ) {
      return;
    }

    for ( String join : joins ) {
      if ( !createJoin( join ) ) {
        // Assert.fail( "TS042034: '" + join + "' inner join was not created!" );
        LOGGER.error( join + " inner join was not created!" );
      }
    }
  }

  public void deleteAllJoins() {
    while ( selectJoin() ) {
      deleteJoin();
    }
  }

  public boolean selectSchema( String schema ) {
    if ( schema == null ) {
      return true;
    }
    selectPopup( schemaTable, schema );
    return selectedSchema.getText().contains( schema );
  }

  public boolean isFactTableAvailable() {
    return isElementPresent( factTables );
  }

  public boolean selectFactTable( String table ) {
    selectPopup( factTables, table );

    // if (!isFactTableSelected(table)) {
    // click(factTables);
    // click(format(comboFactTable, table));
    // }
    return selectedFact.getText().contains( table );
  }

  public boolean checkLeftKeyFieldsForDuplicates() {
    boolean res = false;

    for ( int j = 0; j < leftKeyFields.size(); j++ ) {
      for ( int k = j + 1; k < leftKeyFields.size(); k++ ) {
        if ( k != j && leftKeyFields.get( k ).getText().equals( leftKeyFields.get( j ).getText() ) ) {
          res = true;
        }
      }
    }

    return res;
  }

  public boolean isDuplicateJoinsErrorDialogPresent() {
    boolean res = false;

    if ( isElementPresent( joinsErrorLabel, EXPLICIT_TIMEOUT / 5 ) && joinsErrorLabel.getText().contains(
        DB_TABLES_PAGE_DUPLICATE_JOINS_ERROR_MESSAGE ) ) {
      LOGGER.info( "Error message displayed: " + joinsErrorLabel.getText() );
      res = true;
    }

    return res;
  }

  public boolean isSelfJoinsErrorDialogPresent() {
    boolean res = false;

    if ( isElementPresent( joinsErrorLabel, EXPLICIT_TIMEOUT / 5 ) && joinsErrorLabel.getText().contains(
        DB_TABLES_PAGE_SELF_JOIN_ERROR_MESSAGE ) ) {
      LOGGER.info( "Error message displayed: " + joinsErrorLabel.getText() );
      res = true;
    }

    return res;
  }

  public void closeJoinsErrorDialog() {
    click( btnJoinsErrorClose );
  }
}
