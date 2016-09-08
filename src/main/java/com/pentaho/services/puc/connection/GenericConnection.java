package com.pentaho.services.puc.connection;

import java.util.Map;

import com.google.common.base.Strings;

public class GenericConnection extends BaseConnection {

  protected String customURL;
  protected String customDriver;

  public GenericConnection( String name, String dbType, String accessType, String user, String password,
      String customURL, String customDriver ) {
    super( name, dbType, accessType, null, user, password );
    this.customURL = customURL;
    this.customDriver = customDriver;
  }

  public GenericConnection( Map<String, String> args ) {
    super( args );
    this.customURL = args.get( "customURL" );
    this.customDriver = args.get( "customDriver" );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( GenericConnection copy ) {
    super.copy( copy );
    if ( copy.getCustomURL() != null ) {
      this.customURL = copy.getCustomURL();
    }
    if ( copy.getCustomDriver() != null ) {
      this.customDriver = copy.getCustomDriver();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public String getCustomURL() {
    return customURL;
  }

  public void setCustomURL( String customURL ) {
    this.customURL = customURL;
  }

  public String getCustomDriver() {
    return customDriver;
  }

  public void setCustomDriver( String customDriver ) {
    this.customDriver = customDriver;
  }

  @Override
  public void setParameters() {
    // Setting the four fields relevant to Generic database connection
    if ( !Strings.isNullOrEmpty( customURL ) ) {
      connPage.setCustomConnectionURL( customURL );
    }
    if ( !Strings.isNullOrEmpty( customDriver ) ) {
      connPage.setCustomDriverName( customDriver );
    }
    if ( !Strings.isNullOrEmpty( user ) ) {
      connPage.setUser( user );
    }
    if ( !Strings.isNullOrEmpty( password ) ) {
      connPage.setPassword( password );
    }
  }
}
