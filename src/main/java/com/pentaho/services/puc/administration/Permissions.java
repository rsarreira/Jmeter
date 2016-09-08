package com.pentaho.services.puc.administration;

import java.util.HashMap;

public class Permissions {
  public static int NO_PERMISSIONS      = 0b00000000; //0;
  public static int ADMINISTER_SECURITY = 0b00000001; //1
  public static int SCHEDULE_CONTENT    = 0b00000010; //2;
  public static int READ_CONTENT        = 0b00000100; //4;
  public static int PUBLISH_CONTENT     = 0b00001000; //8;
  public static int CREATE_CONTENT      = 0b00010000; //16;
  public static int EXECUTE             = 0b00100000; //32;
  public static int MANAGE_DATA_SOURCES = 0b01000000; //64;
  public static int ALL_PERMISSIONS     = 0b01111111; //127;


  public static final HashMap<Integer, String> names = new HashMap<Integer, String>();
  static {
    names.put( ADMINISTER_SECURITY, "org.pentaho.security.administerSecurity" );
    names.put( SCHEDULE_CONTENT, "org.pentaho.scheduler.manage" );
    names.put( READ_CONTENT, "org.pentaho.repository.read" );
    names.put( PUBLISH_CONTENT, "org.pentaho.security.publish" );
    names.put( CREATE_CONTENT, "org.pentaho.repository.create" );
    names.put( EXECUTE, "org.pentaho.repository.execute" );
    names.put( MANAGE_DATA_SOURCES, "manage_datasources" );
  }

  int permissions;

  public Permissions( int permissions ) {
    this.permissions = permissions;
  }

  public boolean hasPermission( int roleId ) {
    return ( permissions & roleId ) != 0;
  }

  public void addPermission( int permission ) {
    permissions = permissions | permission;
  }

  String getName( int permission ) {
    return names.get( permission );
  }
}
