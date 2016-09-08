package com.pentaho.services.puc.connection;

import java.util.Map;

public class JndiConnection extends BaseConnection {

  public JndiConnection( String name, String dbType, String accessType, String dbName ) {
    super( name, dbType, accessType, dbName, null, null );
  }

  public JndiConnection( Map<String, String> args ) {
    super( args );
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  @Override
  public void setParameters() {
    // do nothing as everything is covered in BaseConnection
  }
}
