package com.pentaho.services.puc.connection;

import java.util.Map;

public class OdbcConnection extends BaseConnection {

  public OdbcConnection( String name, String dbType, String accessType, String dbName, String user,
      String password ) {
    super( name, dbType, accessType, dbName, user, password );
  }

  public OdbcConnection( Map<String, String> args ) {
    super( args );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( OdbcConnection copy ) {
    super.copy( copy );
  }

  @Override
  public void setParameters() {
    if ( user != null ) {
      connPage.setUser( user );
    }
    if ( password != null ) {
      connPage.setPassword( password );
    }
  }
}
