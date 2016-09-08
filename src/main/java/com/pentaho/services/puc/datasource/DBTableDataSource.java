package com.pentaho.services.puc.datasource;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.datasource.DataSourceDBTablePage;
import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.HomePage;

public class DBTableDataSource extends BaseDataSource {

  protected String connection;
  protected Boolean reportingOnly;
  protected String schema;
  protected List<String> tables;
  protected List<String> joins;

  protected String factTable;

  private DataSourceDBTablePage tablesPage;

  public DBTableDataSource( Map<String, String> args ) {
    super( args.get( "name" ), Boolean.valueOf( args.get( "customizedModel" ) ) );

    this.connection = args.get( "connection" );
    this.reportingOnly = args.get( "reportingOnly" ) != null ? Boolean.valueOf( args.get( "reportingOnly" ) ) : null;
    this.schema = args.get( "schema" );

    this.tables =
        args.get( "tables" ) != null ? Arrays.asList( args.get( "tables" ).replace( ",", ";" ).replace( " ", "" ).split(
            ";" ) ) : null;
    this.factTable = args.get( "factTable" );
    this.joins =
        args.get( "joins" ) != null ? Arrays.asList( args.get( "joins" ).replace( " ", "" ).split( ";" ) ) : null;

  }

  public DBTableDataSource( String name, String connection, Boolean reportingOnly, String schema, List<String> tables,
      String factTable, List<String> joins, Boolean customizedModel ) {
    super( name, customizedModel );

    this.connection = connection;

    this.reportingOnly = reportingOnly;
    this.schema = schema;
    this.tables = tables;
    this.factTable = factTable;
    this.joins = joins;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( DBTableDataSource copy ) {
    super.copy( copy );

    if ( copy.getConnection() != null ) {
      this.connection = copy.getConnection();
    }
    if ( copy.getReportingOnly() != null ) {
      this.reportingOnly = copy.getReportingOnly();
    }
    if ( copy.getSchema() != null ) {
      this.schema = copy.getSchema();
    }
    if ( copy.getTables() != null ) {
      this.tables = copy.getTables();
    }
    if ( copy.getJoins() != null ) {
      this.joins = copy.getJoins();
    }
    if ( copy.getFactTable() != null ) {
      this.factTable = copy.getFactTable();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public String getConnection() {
    return connection;
  }

  public void setConnection( String connection ) {
    this.connection = connection;
  }

  public Boolean getReportingOnly() {
    return reportingOnly;
  }

  public void setReportingOnly( Boolean reportingOnly ) {
    this.reportingOnly = reportingOnly;
  }

  public String getSchema() {
    return schema;
  }

  public void setSchema( String schema ) {
    this.schema = schema;
  }

  public List<String> getTables() {
    return tables;
  }

  public void setTables( List<String> tables ) {
    this.tables = tables;
  }

  public List<String> getJoins() {
    return joins;
  }

  public void setJoins( List<String> joins ) {
    this.joins = joins;
  }

  public String getFactTable() {
    return factTable;
  }

  public void setFactTable( String factTable ) {
    this.factTable = factTable;
  }

  /* ----------------------------------------------------------- */

  /* -------------- EDIT DATA SOURCE ----------------- */
  public void edit( DBTableDataSource newDataSource ) {
    super.edit( newDataSource );
    copy( newDataSource );
  }

  public DataSourceDBTablePage selectSourceType() {
    tablesPage = new DataSourceDBTablePage( getDriver() );
    tablesPage.selectConnection( connection );
    tablesPage.setReportingOnly( reportingOnly );
    return tablesPage;
  }

  @Override
  public void setParameters() {

    tablesPage = selectSourceType();

    tablesPage.buttonNext();

    if ( !tablesPage.isPageSelectTablesSelected() ) {
      softAssert.fail( "TS042232: 'Select Tables' page is not appeared!" );
    }

    if ( schema != null ) {
      if ( !tablesPage.selectSchema( schema ) ) {
        Assert.fail( "TS042232: " + schema + " schema was not selected!" );
      }
    }

    if ( tables != null ) {
      tablesPage.deselectAllTables();
      if ( !tablesPage.selectTables( tables ) ) {
        Assert.fail( "TS042032: '" + tables
            + "' tables were not moved from the 'Available Tables' section to the 'Selected Tables' section!" );
      }
    }

    if ( tablesPage.isFactTableAvailable() ) {
      LOGGER.info( "Fact table drop-down is available." );
      tablesPage.selectFactTable( factTable );
    }

    tablesPage.buttonNext();

    if ( !tablesPage.isPageDefineJoinsSelected() ) {
      Assert.fail( "TS042033: 'Define Joins' page is not appeared!" );
    }

    tablesPage.deleteAllJoins();
    tablesPage.createJoins( joins );

  }

  /* -------------- VERIFY DATA SOURCE ------------------------ */
  public boolean verifyDataSource() {
    return verifyDataSource( EXPLICIT_TIMEOUT );
  }

  public boolean verifyDataSource( long timeout ) {
    boolean res = false;
    HomePage homePage = new HomePage( getDriver() );
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );

    if ( !manageDataSourcesPage.isOpened( EXPLICIT_TIMEOUT / 10 ) ) {
      manageDataSourcesPage = homePage.openManageDataSources();
    }

    if ( manageDataSourcesPage.isDataSourcePresent( name, timeout ) ) {
      DataSourceModelEditorPage dsModelEditorPage = manageDataSourcesPage.editDataSource( this );
      dsModelEditorPage.editSource();

      // [MG]
      if ( getDriver().toString().contains( "firefox" ) ) {
        tablesPage.makeClickable();
      }

      // verify the values in the Select Source Type page.
      if ( !getConnection().equals( tablesPage.getSelectedConnection().getText() ) || !getReportingOnly() == tablesPage
          .isScopeRadioButtonReportingOnlySelected() ) {
        res = true;
      }

      tablesPage.buttonNext();

      // verify the values in the Select Tables page.
      if ( !getSchema().equals( tablesPage.getSelectedSchema() ) || !getTables().equals( tablesPage
          .getSelectedTables() ) ) {
        res = true;
      }

      tablesPage.buttonNext();

      // verify the joins
      if ( !getJoins().equals( tablesPage.getJoins() ) ) {
        res = true;
      }

      cancelWizard();

      dsModelEditorPage.buttonCancel();
    }

    manageDataSourcesPage.closeManageDataSources();

    return res;
  }
}
