package com.pentaho.services.puc.administration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.puc.AdministrationPage;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.browse.BrowseObject;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.tree.Tree.TreeNode;

public class AdministrationService {
  private static Map<String, PentahoUser> users = new HashMap<String, PentahoUser>();
  private static Map<String, Role> roles = new HashMap<String, Role>();

  private static ThreadLocal<AdministrationPage> administrationPages = new ThreadLocal<AdministrationPage>();
  
  
  public static void addSampleContent() {
    roles.put( "Administrator", new Role( "auto", "Administrator", new Permissions (Permissions.ALL_PERMISSIONS) ) );
    roles.put( "Business Analyst", new Role( "auto", "Business Analyst", new Permissions (Permissions.PUBLISH_CONTENT) ) );
    roles.put( "Power User", new Role( "auto", "Power User", new Permissions (Permissions.SCHEDULE_CONTENT | Permissions.READ_CONTENT | Permissions.PUBLISH_CONTENT | Permissions.CREATE_CONTENT | Permissions.EXECUTE) ) );
    roles.put( "Report Author", new Role( "auto", "Report Author", new Permissions (Permissions.SCHEDULE_CONTENT | Permissions.PUBLISH_CONTENT) ) );
    
    users.put( "admin", new PentahoUser( "admin", "password" ) );
    users.put( "pat", new PentahoUser( "pat", "password" ) );
    users.put( "suzy", new PentahoUser( "suzy", "password" ) );
    users.put( "tiffany", new PentahoUser( "tiffany", "password" ) );
    
    // set roles to default users
    users.get( DefaultUserNames.ADMIN_USER.name ).setRoles( Arrays.asList( roles.get(
        DefaultRoleNames.ADMINISTRATOR_ROLE.name ) ) );
    users.get( DefaultUserNames.PAT_USER.name ).setRoles( Arrays.asList( roles.get(
        DefaultRoleNames.BUSINESS_ANALYST_ROLE.name ) ) );
    users.get( DefaultUserNames.SUZY_USER.name ).setRoles( Arrays.asList( roles.get(
        DefaultRoleNames.POWER_USER_ROLE.name ) ) );
    users.get( DefaultUserNames.TIFFANY_USER.name ).setRoles( Arrays.asList( roles.get(
        DefaultRoleNames.REPORT_AUTHOR_ROLE.name ) ) );

    // set users to default roles
    roles.get( DefaultRoleNames.ADMINISTRATOR_ROLE.name ).setUsers( Arrays.asList( users.get(
        DefaultUserNames.ADMIN_USER.name ) ) );
    roles.get( DefaultRoleNames.BUSINESS_ANALYST_ROLE.name ).setUsers( Arrays.asList( users.get(
        DefaultUserNames.PAT_USER.name ) ) );
    roles.get( DefaultRoleNames.POWER_USER_ROLE.name ).setUsers( Arrays.asList( users.get(
        DefaultUserNames.SUZY_USER.name ) ) );
    roles.get( DefaultRoleNames.REPORT_AUTHOR_ROLE.name ).setUsers( Arrays.asList( users.get(
        DefaultUserNames.TIFFANY_USER.name ) ) );
    
  }
   
  public static enum DefaultRoleNames {
    ADMINISTRATOR_ROLE( "Administrator"), 
    BUSINESS_ANALYST_ROLE( "Business Analyst"), 
    POWER_USER_ROLE( "Power User"), 
    REPORT_AUTHOR_ROLE( "Report Author");

    public String name;

    private DefaultRoleNames( String name) {
      this.name = name;
    }
  }
  
  public static enum DefaultUserNames {
    ADMIN_USER( "admin"), 
    PAT_USER( "pat"), 
    SUZY_USER( "suzy"), 
    TIFFANY_USER( "tiffany");

    public String name;

    private DefaultUserNames( String name) {
      this.name = name;
    }
  }

  public static AdministrationPage getAdministrationPage() {
    return administrationPages.get();
  }

  public static void setAdministrationPage( AdministrationPage adminPage ) {
    administrationPages.set( adminPage );
  }

  //ROLES

  public static Map<String, Role> getRoles() {
    return roles;
  }

  public static Role getRole( String name ) {
    Role role = roles.get( name );
    if ( role == null ) {
      Assert.fail( "There is no role with that name: " + name );
    }
    return role;
  }

  public static Role getRole( int id ) {
    Role role = null;
    for ( Map.Entry<String, Role> entryRole : roles.entrySet() ) {
      if ( entryRole.getValue().getId() == id ) {
        role = entryRole.getValue();
        break;
      }
    }
    if ( role == null ) {
      Assert.fail( "There is no role with that id " + id );
    }
    return role;
  }

  public static void addRole( Role role ) {
    getAdministrationPage().createRole( role );
    if ( getAdministrationPage().isErrorDialogPresent( role.getName() ) ) {
      getAdministrationPage().dismissErrorDialog( role.getName() );
    } else {
      if ( role.verify() ) {
        roles.put( role.getName(), role );
      }
    }
  }

  public static void deleteRole( Role role ) {
    if ( checkRole( role ) ) {
      getAdministrationPage().deleteRole( role );
      if ( role.verifyDelete() ) {
        roles.remove( role.getName() );
        List<PentahoUser> usersList = role.getUsers();
        if ( usersList != null ) {
          for ( PentahoUser user : usersList ) {
            List<Role> rolesList = user.getRoles();
            rolesList.remove( role );
            user.setRoles( rolesList );
            users.put( user.getName(), user );
          }
        }
      }
    }
  }

  public static void deleteRole( String name ) {
    Role role = getRole( name );
    deleteRole( role );
  }

  public static void deleteRole( int id ) {
    Role role = getRole( id );
    deleteRole( role );
  }

  public static void assignUserToRole( Role role, PentahoUser newUser ) {
    if ( checkUser( newUser ) ) {
      getAdministrationPage().assignUserToRole( role, newUser );
      // add user to role
      List<PentahoUser> usersList = role.getUsers();
      usersList.add( newUser );
      role.setUsers( usersList );
      if ( getAdministrationPage().verifyRoleUsers( role ) ) {
        roles.put( role.getName(), role );
      }
      // add role to user
      addRoleToUser( role, newUser );
    }
  }

  public static void assignUsersToRole( Role role, List<PentahoUser> newUsersList ) {
    if ( checkUserList( newUsersList ) ) {
      getAdministrationPage().assignUsersToRole( role, newUsersList );
      // add users to role
      List<PentahoUser> usersList = role.getUsers();
      for ( PentahoUser newUser : newUsersList ) {
        usersList.add( newUser );
      }
      role.setUsers( usersList );
      if ( getAdministrationPage().verifyRoleUsers( role ) ) {
        roles.put( role.getName(), role );
      }

      // add role to user
      for ( PentahoUser newUser : newUsersList ) {
        addRoleToUser( role, newUser );
      }
    }
  }

  private static void addRoleToUser( Role role, PentahoUser newUser ) {
    List<Role> rolesList = new ArrayList<>( newUser.getRoles() );
    rolesList.add( role );
    newUser.setRoles( rolesList );
    users.put( newUser.getName(), newUser );
  }

  private static void removeRoleFromUser( Role role, PentahoUser user ) {
    List<Role> rolesList = new ArrayList<>( user.getRoles() );
    rolesList.remove( role );
    user.setRoles( rolesList );
    users.put( user.getName(), user );
  }

  public static void unassignUserFromRole( Role role, PentahoUser user ) {
    getAdministrationPage().unassignUserFromeRole( role, user );
    // remove user from role
    List<PentahoUser> usersList = role.getUsers();
    usersList.remove( user );
    role.setUsers( usersList );
    if ( getAdministrationPage().verifyRoleUsers( role ) ) {
      roles.put( role.getName(), role );
    }
    // remove role from user
    removeRoleFromUser( role, user );
  }

  public static void unassignUsersFromRole( Role role, List<PentahoUser> usersToRemove ) {
    getAdministrationPage().unassignUsersFromRole( role, usersToRemove );
    // remove users from role
    List<PentahoUser> usersList = role.getUsers();
    for ( PentahoUser userToRemove : usersToRemove ) {
      usersList.remove( userToRemove );
    }
    role.setUsers( usersList );
    if ( getAdministrationPage().verifyRoleUsers( role ) ) {
      roles.put( role.getName(), role );
    }
    // remove role from user
    for ( PentahoUser user : usersToRemove ) {
      removeRoleFromUser( role, user );
    }
  }

  public static boolean verifyRole( Role role ) {
    return getAdministrationPage().verifyRole( role );
  }

  public static boolean verifyDeletedRole( Role role ) {
    return getAdministrationPage().verifyDeletedRole( role );
  }

  public static boolean checkRole( Role role ) {
    boolean res = true;
    if ( !role.isExist() ) {
      Assert.fail( "Role " + role.getName() + " not exist!" );
      res = false;
    }
    return res;
  }

  public static boolean checkRoleList( List<Role> roles ) {
    boolean res = true;
    SoftAssert softAssert = new SoftAssert();
    for ( Role role : roles ) {
      if ( !role.isExist() ) {
        softAssert.fail( "Role " + role.getName() + " not exist!" );
        res = false;
      }
    }
    softAssert.assertAll();
    return res;
  }

  public static List<Role> parseRoles( String rolesStrList ) {
    List<String> userRoles = new ArrayList<String>( Arrays.asList( rolesStrList.split( ";" ) ) );
    List<Role> rolesList = new ArrayList<Role>();
    for ( String role : userRoles ) {
      for ( DefaultRoleNames defRole : DefaultRoleNames.values() ) {
        if ( defRole.name.equals( role ) ) {
          rolesList.add( roles.get( role ) );
        }
      }
    }
    return rolesList;
  }
  
  //USERS

  public static Map<String, PentahoUser> getUsers() {
    return users;
  }

  public static PentahoUser getUser( String name ) {
    PentahoUser user = users.get( name );
    if ( user == null ) {
      Assert.fail( "There is no user with that name: " + name );
    }
    return user;
  }

  public static PentahoUser getUser( int id ) {
    PentahoUser user = null;
    for ( Map.Entry<String, PentahoUser> entryUser : users.entrySet() ) {
      if ( entryUser.getValue().getId() == id ) {
        user = entryUser.getValue();
        break;
      }
    }
    if ( user == null ) {
      Assert.fail( "There is no user with that id " + id );
    }
    return user;
  }

  public static void addUser( PentahoUser user ) {
    getAdministrationPage().createUser( user );
    if ( getAdministrationPage().isErrorDialogPresent( user.getName() ) ) {
      getAdministrationPage().dismissErrorDialog( user.getName() );
    } else {
      if ( user.verify() ) {
        users.put( user.getName(), user );
        addUserToTreeNode( user );
      }
    }
  }

  public static void deleteUser( PentahoUser user ) {
    if ( checkUser( user ) ) {
      getAdministrationPage().deleteUser( user );
      if ( user.verifyDelete() ) {
        users.remove( user.getName() );
        List<Role> rolesList = user.getRoles();
        if ( rolesList != null ) {
          for ( Role role : rolesList ) {
            List<PentahoUser> usersList = role.getUsers();
            usersList.remove( user );
            role.setUsers( usersList );
            roles.put( role.getName(), role );
          }
        }
        deleteUserFromTreeNode( user );
      }
    }
  }

  public static void deleteUser( int id ) {
    PentahoUser user = getUser( id );
    deleteUser( user );
  }

  public static void deleteUser( String name ) {
    PentahoUser user = getUser( name );
    deleteUser( user );
  }
  
  public static void assignRoleToUser( PentahoUser user, Role newRole ) {
    if ( checkRole( newRole ) ) {
      getAdministrationPage().assignRoleToUser( user, newRole );
      // add role to user
      List<Role> rolesList = user.getRoles();
      rolesList.add( newRole );
      user.setRoles( rolesList );
      if ( getAdministrationPage().verifyUserRoles( user ) ) {
        users.put( user.getName(), user );
      }
      // add user to role
      addUserToRole( user, newRole );
    }
  }

  public static void assignRolesToUser( PentahoUser user, List<Role> newRoleList ) {
    if ( checkRoleList( newRoleList ) ) {
      getAdministrationPage().assignRolesToUser( user, newRoleList );
      // add role to user
      List<Role> rolesList = user.getRoles();
      for ( Role newRole : newRoleList ) {
        rolesList.add( newRole );
      }
      user.setRoles( rolesList );
      if ( getAdministrationPage().verifyUserRoles( user ) ) {
        users.put( user.getName(), user );
      }
      // add user to role
      for ( Role newRole : newRoleList ) {
        addUserToRole( user, newRole );
      }
    }
  }

  private static void addUserToRole( PentahoUser user, Role newRole ) {
    List<PentahoUser> usersList = new ArrayList<>( newRole.getUsers() );
    usersList.add( user );
    newRole.setUsers( usersList );
    roles.put( newRole.getName(), newRole );
  }
  
  private static void removeUserFromRole( PentahoUser user, Role role ) {
    List<PentahoUser> usersList = new ArrayList<>( role.getUsers() );
    usersList.remove( user );
    role.setUsers( usersList );
    roles.put( role.getName(), role );
  }

  public static void unassignRoleFromUser( PentahoUser user, Role role ) {
    getAdministrationPage().unassignRoleFromeUser( user, role );
    // remove Role from user
    List<Role> rolesList = user.getRoles();
    rolesList.remove( role );
    user.setRoles( rolesList );
    if ( getAdministrationPage().verifyUserRoles( user ) ) {
      users.put( user.getName(), user );
    }
    // remove user from role
    removeUserFromRole( user, role );
  }
  
  public static void unassignRolesFromUser( PentahoUser user, List<Role> rolesToRemove ) {
    getAdministrationPage().unassignRolesFromUser( user, rolesToRemove );
    // remove roles from user
    List<Role> rolesList = user.getRoles();
    for ( Role roleToRemove : rolesToRemove ) {
      rolesList.remove( roleToRemove );
    }
    user.setRoles( rolesList );
    if ( getAdministrationPage().verifyUserRoles( user ) ) {
      users.put( user.getName(), user );
    }
    // remove user from role
    for ( Role role : rolesToRemove ) {
      removeUserFromRole( user, role );
    }
  }

  public static boolean verifyUser( PentahoUser user ) {
    return getAdministrationPage().verifyUser( user );
  }
  
  public static boolean verifyDeletedUser( PentahoUser user ) {
    return getAdministrationPage().verifyDeletedUser( user );
  }
  
  public static boolean checkUser( PentahoUser user ) {
    boolean res = true;
    if ( !user.isExist() ) {
      Assert.fail( "User " + user.getName() + " not exist!" );
      res = false;
    }
    return res;
  }
  
  public static boolean checkUserList( List<PentahoUser> users ) {
    boolean res = true;
    SoftAssert softAssert = new SoftAssert();
    for ( PentahoUser user : users ) {
      if ( !user.isExist() ) {
        softAssert.fail( "User " + user.getName() + " not exist!" );
        res = false;
      }
    }
    softAssert.assertAll();
    return res;
  }

  public static List<PentahoUser> parseUsers( String usersStrList ) {
    List<String> usersStr = new ArrayList<String>( Arrays.asList( usersStrList.split( " " ) ) );
    List<PentahoUser> usersList = new ArrayList<PentahoUser>();
    for ( String user : usersStr ) {
      for ( DefaultUserNames defUser : DefaultUserNames.values() ) {
        if ( defUser.name.equals( user ) ) {
          usersList.add( users.get( user ) );
        }
      }
    }
    return usersList;
  }

  private static void addUserToTreeNode( PentahoUser newUser ) {
    TreeNode<BrowseObject> nodeNewUser = new TreeNode<BrowseObject>( new Folder( newUser.getName(), false, "auto" ) );
    BrowseService.addNodeToTree( nodeNewUser );
  }

  private static void deleteUserFromTreeNode( PentahoUser user ) {
    TreeNode<BrowseObject> node = BrowseService.getTreeNodeByName( user.getName() );
    BrowseService.deleteNodeFromTree( node );
  }
}
