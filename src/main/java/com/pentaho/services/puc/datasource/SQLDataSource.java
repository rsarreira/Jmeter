package com.pentaho.services.puc.datasource;

import java.util.Map;

import com.pentaho.qa.gui.web.datasource.DataSourceSQLQueryPage;

public class SQLDataSource extends BaseDataSource {

  protected String connection;
  protected String query;

  public SQLDataSource( Map<String, String> args ) {
    super( args.get( "name" ), Boolean.valueOf( args.get( "customizedModel" ) ) );

    this.connection = args.get( "connection" );
    this.query = args.get( "query" );
  }

  public SQLDataSource( String name, String connection, String query, Boolean customizedModel ) {
    super( name, customizedModel );

    this.connection = connection;
    this.query = query;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( SQLDataSource copy ) {
    super.copy( copy );

    if ( copy.getConnection() != null ) {
      this.connection = copy.getConnection();
    }
    if ( copy.getQuery() != null ) {
      this.query = copy.getQuery();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public String getConnection() {
    return connection;
  }

  public void setConnection( String connection ) {
    this.connection = connection;
  }

  public String getQuery() {
    return query;
  }

  public void setQuery( String query ) {
    this.query = query;
  }

  /* -------------- EDIT DATA SOURCE ----------------- */
  public void edit( SQLDataSource newDataSource ) {
    super.edit( newDataSource );
    copy( newDataSource );
  }
  
  public void setParameters() {
    DataSourceSQLQueryPage sqlDsPage = new DataSourceSQLQueryPage( getDriver() );

    sqlDsPage.selectConnection( connection );
    sqlDsPage.setQuery( query );
  }

}
