package com.pentaho.services.api.http;

public enum ProxyType {

  HTTP( "http" ), HTTPS( "https" );

  private String name;

  ProxyType( String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public ProxyType get( String name ) {
    return valueOf( name );
  }
}
