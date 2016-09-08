package com.pentaho.services.puc.connection;

import java.util.Map;

import com.pentaho.qa.gui.web.puc.connection.DatabaseConnectionPage;
import com.qaprosoft.carina.core.foundation.utils.HTML;

public class JdbcConnection extends BaseConnection {

  protected String host;
  protected Integer port;

  public JdbcConnection( String name, String dbType, String accessType, String host, String dbName, Integer port,
      String user, String password ) {
    super( name, dbType, accessType, dbName, user, password );

    this.host = host;
    this.port = port;
  }

  public JdbcConnection( Map<String, String> args ) {
    super( args );

    this.host = args.get( "host" );
    if ( args.get( "port" ) != null ) {
      this.port = Integer.valueOf( args.get( "port" ) );
    }

  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( JdbcConnection copy ) {
    super.copy( copy );
    if ( copy.getHost() != null ) {
      this.host = copy.getHost();
    }
    if ( copy.getPort() != null ) {
      this.port = copy.getPort();
    }

  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public String getHost() {
    return host;
  }

  public void setHost( String host ) {
    this.host = host;
  }

  public Integer getPort() {
    return port;
  }

  public void setPort( Integer port ) {
    this.port = port;
  }

  public void setParameters() {
    if ( host != null ) {
      connPage.setHostName( host );
    }
    if ( port != null ) {
      connPage.setPort( String.valueOf( port ) );
    }
    if ( user != null ) {
      connPage.setUser( user );
    }
    if ( password != null ) {
      connPage.setPassword( password );
    }
  }

  public DatabaseConnectionPage getConnectionPage() {
    return (DatabaseConnectionPage) super.getConnectionPage();
  }

  public boolean verifyGeneral() {
    boolean res = false;
    connPage.openTab( DatabaseConnectionPage.ConnectionTab.GENERAL );

    // if page and object are different in ANY field, connection was not overwritten.
    if ( !getDbType().equals( connPage.getSelectedDatabaseType().getText() )
        || !getAccessType().equals( connPage.getSelectedAccessType().getText() )
        || !getHost().equals( connPage.getTxtHostName().getAttribute( HTML.VALUE ) )
        || !getDbName().equals( connPage.getTxtDbName().getAttribute( HTML.VALUE ) )
        || !getPort().toString().equals( connPage.getTxtDbPort().getAttribute( HTML.VALUE ) )
        || !getUser().equals( connPage.getTxtUser().getAttribute( HTML.VALUE ) ) ) {
      res = true;
    }

    cancelWizard();

    return res;
  }
}
