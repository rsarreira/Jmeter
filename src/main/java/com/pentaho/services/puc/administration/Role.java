package com.pentaho.services.puc.administration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pentaho.services.BaseObject;
import com.pentaho.services.PentahoUser;

public class Role extends BaseObject {

  public static final String ARG_NAME = "Name";
  public static final String ARG_ADMINISTER_SECURIRY = "AdministerSecurity";
  public static final String ARG_SCHEDULE_CONTENT = "ScheduleContent";
  public static final String ARG_READ_CONTENT = "ReadContent";
  public static final String ARG_PUBLISH_CONTENT = "PublishContent";
  public static final String ARG_CREATE_CONTENT = "CreateContent";
  public static final String ARG_EXECUTE = "Execute";
  public static final String ARG_MANAGE_DATA_SOURCES = "ManageDataSources";

  protected List <PentahoUser> users;
  
  protected Permissions permissions;

  public Role( String id, String name, Permissions permissions ) {
    super( name, id );
    this.name = name;
    this.permissions = permissions;
    
    users = new ArrayList<PentahoUser> ();
  }

  public Role( String name ) {
    this( null, name, new Permissions( Permissions.NO_PERMISSIONS ) );
  }

  public Role( Map<String, String> args ) {
    this( args.get( ARG_ID ), args.get( ARG_NAME ), 
        new Permissions( ( Integer.parseInt( args.get( ARG_ADMINISTER_SECURIRY ) ) != 0 ? Permissions.ADMINISTER_SECURITY : 0)
            |( Integer.parseInt( args.get( ARG_SCHEDULE_CONTENT ) ) != 0 ? Permissions.SCHEDULE_CONTENT : 0)
            |( Integer.parseInt( args.get( ARG_READ_CONTENT ) ) != 0 ? Permissions.READ_CONTENT : 0)
            |( Integer.parseInt( args.get( ARG_PUBLISH_CONTENT ) ) != 0 ? Permissions.PUBLISH_CONTENT : 0)
            |( Integer.parseInt( args.get( ARG_CREATE_CONTENT ) ) != 0 ? Permissions.CREATE_CONTENT : 0)
            |( Integer.parseInt( args.get( ARG_EXECUTE ) ) != 0 ? Permissions.EXECUTE : 0)
            |( Integer.parseInt( args.get( ARG_MANAGE_DATA_SOURCES ) ) != 0 ? Permissions.MANAGE_DATA_SOURCES : 0) ) );
  }

  public void add() {
    AdministrationService.addRole( this );
  }

  public void assignUser( PentahoUser newUser ) {
    AdministrationService.assignUserToRole( this, newUser );
  }

  public void assignUsers( List<PentahoUser> newUsersList ) {
    AdministrationService.assignUsersToRole( this, newUsersList );
  }

  public void unAssignUser( PentahoUser user ) {
    AdministrationService.unassignUserFromRole( this, user );
  }

  public void unAssignUsers( List<PentahoUser> usersList ) {
    AdministrationService.unassignUsersFromRole( this, usersList );
  }

  public void delete() {
    AdministrationService.deleteRole( this );
  }

  public boolean verify() {
    return AdministrationService.verifyRole( this );
  }

  public boolean verifyDelete() {
    return AdministrationService.verifyDeletedRole( this );
  }

  public boolean isExist() {
    return AdministrationService.getRoles().containsKey( this.getName() );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public List<PentahoUser> getUsers() {
    return users;
  }

  public void setUsers( List<PentahoUser> users ) {
    this.users = users;
  }

  public Permissions getPermissions() {
    return permissions;
  }

  public void setPermissions( Permissions permissions ) {
    this.permissions = permissions;
  }
}
