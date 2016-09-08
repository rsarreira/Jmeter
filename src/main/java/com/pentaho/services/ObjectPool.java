package com.pentaho.services;

import java.util.HashMap;

public class ObjectPool {

  protected static HashMap<Integer, BaseObject> snapshots = new HashMap<Integer, BaseObject> ();
  protected static int id = 0;

  public static int getUniqueId() {
    return ++id;
  }
  
  
  public static void putSnapshot(BaseObject object) {
    snapshots.put( object.getId(), object );
  }
  
  public static BaseObject getSnapshot(int id) {
    return snapshots.get( id );
  }
  
  public static BaseObject removeSnapshot(int id) {
    return snapshots.remove( id );
  }
  
}
