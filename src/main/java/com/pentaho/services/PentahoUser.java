package com.pentaho.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.WebDriver;

import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.LoginPage;
import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.administration.Role;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;

public class PentahoUser extends BaseObject {  
  public static final String USER_NAME = "Name";
  public static final String USER_PASSWORD = "Password";
  
  private String password;

  private Boolean isLogged;

  private HomePage homePage;
  
  private List <Role> roles;
  
  //private static Integer openedTabs;
  protected static ThreadLocal<Integer> openedTabs = new ThreadLocal<Integer>();

  public PentahoUser( String name, String password ) {
    this( name, password, false);
  }
  
  public PentahoUser( String name, String password, Boolean isLogged ) {
    super(name);
    this.password = password;
    this.isLogged = isLogged;
    roles = new ArrayList<Role> ();
  }
  
  public PentahoUser( Map<String, String> args ) {
    this( args.get( USER_NAME ), args.get( USER_PASSWORD ) );
  }

  public void add() {
    AdministrationService.addUser( this );
  }

  public void assignRole( Role newRole ) {
    AdministrationService.assignRoleToUser( this, newRole );
  }

  public void assignRoles( List<Role> newRolesList ) {
    AdministrationService.assignRolesToUser( this, newRolesList );
  }

  public void unAssignRole( Role role ) {
    AdministrationService.unassignRoleFromUser( this, role );
  }

  public void unAssignRoles( List<Role> rolesList ) {
    AdministrationService.unassignRolesFromUser( this, rolesList );
  }

  public void delete() {
    AdministrationService.deleteUser( this );
  }

  public boolean verify() {
    return AdministrationService.verifyUser( this );
  }

  public boolean verifyDelete() {
    return AdministrationService.verifyDeletedUser( this );
  }

  public boolean isExist() {
    return AdministrationService.getUsers().containsKey( this.getName() );
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  public Boolean isLogged() {
    return isLogged;
  }

  public void setLogged( Boolean isLogged ) {
    this.isLogged = isLogged;
  }
  
  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles( List<Role> roles ) {
    this.roles = roles;
  }

  public HomePage login( WebDriver customDriver ) {
    LoginPage loginPage = new LoginPage( customDriver );

    homePage = loginPage.login( name, password );
    isLogged = true;
    
    homePage.selectLanguage(Configuration.get( Parameter.LOCALE ));
    
    resetOpenedTab();
    

    return homePage;
  }

  public HomePage login() {
    return login( getDriver() );
  }

  public LoginPage logout() {
    LoginPage loginPage = homePage.logout();
    isLogged = false;
    return loginPage;
  }
 
  public static Integer getOpenedTab() {
    LOGGER.info( "get tabs for " + Thread.currentThread().getId() );
    return openedTabs.get();
  }
  
  public static void incOpenedTab() {
    Integer index = openedTabs.get();
    if (index != null) {
      openedTabs.set( ++index );
      LOGGER.info( "increment tabs for " + Thread.currentThread().getId() + "; current value: " + openedTabs );
    } else {
      LOGGER.error( "Reset instead of increment as current value is null! for " + Thread.currentThread().getId() + "; current value: " + openedTabs );
    }
  }
   
  public static void resetOpenedTab() {
    openedTabs.set( 0 );
  }
}
