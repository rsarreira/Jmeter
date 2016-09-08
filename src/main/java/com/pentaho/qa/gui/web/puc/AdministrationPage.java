package com.pentaho.qa.gui.web.puc;

/**
 * Created by Ihar_Chekan on 8/5/2014.
 */

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.services.CustomAssert;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.administration.Permissions;
import com.pentaho.services.puc.administration.Role;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AdministrationPage extends BasePage {
  private static String ATTRIBUTE_VALUE = "value";

  public static enum RoleL10NPermissions {
    ADMINISTER_SECURITY( "org.pentaho.security.administerSecurity" ), 
    SCHEDULE_CONTENT("org.pentaho.scheduler.manage" ), 
    READ_CONTENT( "org.pentaho.scheduler.manage" ),
    PUBLISH_CONTENT("org.pentaho.security.publish" ),
    CREATE_CONTENT( "org.pentaho.repository.create" ),
    EXECUTE("org.pentaho.repository.execute" ),
    MANAGE_DATA_SOURCES( "manage_datasources" );

    private String name;

    private RoleL10NPermissions( String name ) {
      this.name = name;
    }

    public String getName() {
      return this.name;
    }
  }
  
  // 'Administration'
  @FindBy( xpath = "//div[@class='custom-dropdown-label' and contains(., '{L10N:administration}')]" )
  protected ExtendedWebElement adminPage;

  /* ------------------- Users & Roles ------------------- */
  private static final String MANAGE_USERS_AND_ROLES =
      StringEscapeUtils.unescapeHtml( L10N.getText( "manageUsersAndRoles" ) );

  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., '%s')]" )
  protected ExtendedWebElement btnUsersAndRolesPage;

  @FindBy( xpath = "//div[@class='pentaho-tabWidgetLabel' and contains(., '{L10N:manageUsers}')]" )
  protected ExtendedWebElement manageUsersTab;

  @FindBy( xpath = "//div[@class='pentaho-tabWidgetLabel' and contains(., '{L10N:manageRoles}')]" )
  protected ExtendedWebElement manageRolesTab;

  // Manage Users tab
  @FindBy( xpath = "//*[@id='admin-users-panel']//select[@class='gwt-ListBox users-roles-list']" )
  protected ExtendedWebElement users;

  @FindBy( xpath = "//*[@id='admin-users-panel']//select[@class='gwt-ListBox users-roles-list']/option[text()='%s']" )
  protected ExtendedWebElement user;

  @FindBy( css = "#admin-users-panel .pentaho-addbutton" )
  protected ExtendedWebElement btnNewUser;

  @FindBy( css = "#admin-users-panel .pentaho-deletebutton" )
  protected ExtendedWebElement btnDeleteUser;

  // New User dialog
  private static final String USER_NAME = L10N.getText( "_user" );
  @FindBy(
      xpath = "//div[@class ='pentaho-dialog pentaho-dialog-buttonless']//div[contains(text(),%s)]/../../following-sibling::tr[1]//input" )
  protected ExtendedWebElement txtUserName;

  @FindBy(
      xpath = "//div[@class ='pentaho-dialog pentaho-dialog-buttonless']//div[contains(text(),'{L10N:password}')]/../../..//input[@type='password']" )
  protected List<ExtendedWebElement> txtPassword;

  /* [BenF]
   * Changed {L10N:Ok_txt} to {L10N:customUpperCaseOKButton} because JA locale returns 
   * an incorrect value for the OK button when creating a new user.
   */
  @FindBy( xpath = "//button[@class='pentaho-button' and text()='{L10N:customUpperCaseOKButton}']" )
  protected ExtendedWebElement btnOK;

  @FindBy( xpath = "//button[@class='pentaho-button' and text()='{L10N:adminPageCancel}']" )
  protected ExtendedWebElement btnCancel;

  // Error message dialog
  private static final String ERROR_MESSAGE = L10N.getText( "prohibitedNameSymbols" );

  private static final String RESTRICTED_CHARACTERS = "/\\";

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[@class='gwt-HTML gwt-Label' and contains(text(),%s)]" )
  protected ExtendedWebElement dlgNewUserError;

  @FindBy( xpath = "//div[@class='pentaho-dialog']//button[@class='pentaho-button' and text()='{L10N:Ok_txt}']" )
  protected ExtendedWebElement btnOKErrorDialog;

  // Delete User Dialog
  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnConfirmDelete;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancelDelete;

  // Manage Roles tab
  @FindBy( css = "#admin-roles-panel .pentaho-addbutton" )
  protected ExtendedWebElement btnNewRole;

  @FindBy( css = "#admin-roles-panel .pentaho-deletebutton" )
  protected ExtendedWebElement btnDeleteRole;

  // Get the first instance of Operations permissions as they are identical for System Roles
  // and there is no way to differentiate the two due to lack of IDs on the web elements
  @FindBy( xpath = "(//span[@class='gwt-CheckBox']/label[text()='%s'])[1]/../input" )
  protected ExtendedWebElement permissionCheckBox;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//select[@class='gwt-ListBox users-roles-selection-list']/option" )
  protected List<ExtendedWebElement> availableUsers;
  
  @FindBy( xpath = "//*[@id='admin-users-panel']//select[@class='gwt-ListBox users-roles-selection-list']/option" )
  protected List<ExtendedWebElement> availableRoles;
  
  @FindBy(
      xpath = "//*[@id='admin-roles-panel']//div[text() = '{L10N:administration.selected}']/../../..//select[@class='gwt-ListBox users-roles-selection-list']/option[text()='%s']" )
  protected ExtendedWebElement selectedUser;

  @FindBy(
      xpath = "//*[@id='admin-roles-panel']//div[text() = '{L10N:administration.available}']/../../..//select[@class='gwt-ListBox users-roles-selection-list']/option[text()='%s']" )
  protected ExtendedWebElement availableToSelectUser;

  @FindBy(
      xpath = "//*[@id='admin-users-panel']//div[text() = '{L10N:administration.selected}']/../../..//select[@class='gwt-ListBox users-roles-selection-list']/option[text()='%s']" )
  protected ExtendedWebElement selectedRole;

  @FindBy(
      xpath = "//*[@id='admin-users-panel']//div[text() = '{L10N:administration.available}']/../../..//select[@class='gwt-ListBox users-roles-selection-list']/option[text()='%s']" )
  protected ExtendedWebElement availableToSelectRole;
 
  @FindBy(
      xpath = "//*[@id='admin-roles-panel']//select[@class='gwt-ListBox users-roles-selection-list']/option[text()=%s]" )
  protected ExtendedWebElement availableUser;
  
  @FindBy(
      xpath = "//*[@id='admin-users-panel']//select[@class='gwt-ListBox users-roles-selection-list']/option[text()=%s]" )
  protected ExtendedWebElement availableRole;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//select[@class='gwt-ListBox users-roles-list']" )
  protected ExtendedWebElement roles;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//select[@class='gwt-ListBox users-roles-list']/option[text()='%s']" )
  protected ExtendedWebElement role;
  
  @FindBy( xpath = "//*[@id='admin-users-panel']//img[@class='image-button icon-small icon-accum-add']" )
  protected ExtendedWebElement addRolesButton;
  
  @FindBy( xpath = "//*[@id='admin-users-panel']//img[@class='image-button icon-small icon-accum-remove']" )
  protected ExtendedWebElement removeRolesButton;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//img[@class='image-button icon-small icon-accum-add']" )
  protected ExtendedWebElement addUsersButton;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//img[@class='image-button icon-small icon-accum-remove']" )
  protected ExtendedWebElement removeUsersButton;

  @FindBy( xpath = "//*[@id='admin-roles-panel']//img[@class='image-button icon-small icon-accum-add-all']" )
  protected ExtendedWebElement addAllUsersButton;

  // New Role dialog
  private static final String ROLE_NAME = L10N.getText( "newFolderName" );
  @FindBy(
      xpath = "//div[@class ='pentaho-dialog pentaho-dialog-buttonless']//div[contains(text(),%s)]/../../following-sibling::tr[1]//input" )
  protected ExtendedWebElement txtRoleName;

  /* ---------------------- Mail Server ---------------------- */

  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., '{L10N:mailServer}')]" )
  protected ExtendedWebElement btnMailServerPage;

  // Mail Server tab
  @FindBy( xpath = "//input[@class='gwt-TextBox']" )
  protected List<ExtendedWebElement> txtInputs;

  // 'Use Authentication'
  @FindBy( xpath = "//span[contains(., '{L10N:useAuthentication}')]/input[@type='checkbox']" )
  protected ExtendedWebElement useAuthenticationCheckbox;

  @FindBy( xpath = "//input[@class='gwt-PasswordTextBox'][@type='password'][not(@disabled='')]" )
  protected ExtendedWebElement passwordInput;

  // 'Use Start TLS'
  @FindBy( xpath = "//span[contains(., '{L10N:useStartTLS}')]/input[@type='checkbox']" )
  protected ExtendedWebElement useStartTlsInputCheckbox;

  // 'Use SSL'
  @FindBy( xpath = "//span[contains(., '{L10N:useSSL}')]/input[@type='checkbox']" )
  protected ExtendedWebElement useSslCheckbox;

  // 'Save'
  @FindBy( xpath = "//button[@class='pentaho-button' and contains(., '{L10N:save}')]" )
  protected ExtendedWebElement saveButton;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement okButton;

  public AdministrationPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened( adminPage ) ) {
      Assert.fail( "AdministrationPage is not opened!" );
    }
  }

  public void clickMailServer() {
    click( btnMailServerPage );
  }

  protected void setHostName( String hostName ) {
    type( txtInputs.get( 0 ), hostName );
  }

  protected void setPort( String portNumber ) {
    type( txtInputs.get( 1 ), portNumber );
    if ( isElementPresent( okButton, 3 ) ) {
      click( okButton );
      type( txtInputs.get( 1 ), portNumber );
    }
  }

  protected void setUserName( String userName ) {
    type( txtInputs.get( 2 ), userName );
  }

  protected void setPassword( String password ) {
    type( passwordInput, password );
  }

  protected void setEmailFromAddress( String emailFromAddress ) {
    type( txtInputs.get( 3 ), emailFromAddress );
  }

  protected void setEmailFromName( String emailFromName ) {
    type( txtInputs.get( 4 ), emailFromName );
  }

  protected void setStartTlsInputCheckbox( boolean value ) {
    if ( value ) {
      check( useStartTlsInputCheckbox );
    } else {
      uncheck( useStartTlsInputCheckbox );
    }
  }

  protected void setUseSslCheckbox( boolean value ) {
    if ( value ) {
      check( useSslCheckbox );
    } else {
      uncheck( useSslCheckbox );
    }
  }

  protected void buttonSave() {
    Assert.assertTrue( isElementPresent( saveButton ), "Save button is not there!" );
    click( saveButton );
  }

  public void setSMTPSettings( String hostName, String portNumber, String userName, String password,
      String emailFromAddress, String emailFromName ) {

    clickMailServer();

    setHostName( hostName );
    setPort( portNumber );
    setUserName( userName );
    setPassword( password );
    setEmailFromAddress( emailFromAddress );
    setEmailFromName( emailFromName );
    setStartTlsInputCheckbox( true );
    setUseSslCheckbox( true );
    buttonSave();
  }

  protected void clickUsersAndRoles() {
    click( format( btnUsersAndRolesPage, MANAGE_USERS_AND_ROLES ) );
  }

  protected void clickManageUsersTab() {
    click( manageUsersTab );
  }

  protected void clickNewUserButton() {
    click( btnNewUser );
  }

  protected void selectUser( String user ) {
    /* [BenF]
     * doubleClick() helps to ensure the selection of a single user from the users list in Chrome and FireFox.
     * Without doubleClick(), two users will be selected because admin is already selected by default preventing
     * the admin user from being able to actually delete the other selected user.
     */ 
    doubleClick( format( this.user, user ) );
    select( users, user );
  }

  protected void clickDeleteUserButton() {
    click( btnDeleteUser );
  }

  protected void setNewUserName( String userName ) {
    String userNameField = StringUtils.substringBeforeLast( USER_NAME, " :" );
    type( format( txtUserName, L10N.generateConcatForXPath( userNameField ) ), userName );
  }

  protected void setNewUserPassword( String password ) {
    type( txtPassword.get( 0 ), password );
  }

  protected void confirmNewUserPassword( String password ) {
    type( txtPassword.get( 1 ), password );
  }

  public boolean isErrorDialogPresent( String name ) {
    boolean res = false;

    String[] params = { name, RESTRICTED_CHARACTERS };
    ExtendedWebElement dlgError =
        format( dlgNewUserError, L10N.generateConcatForXPath( L10N.formatString( ERROR_MESSAGE, params ) ) );

    if ( isElementPresent( dlgError, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Error message displayed: " + dlgError.getText() );
      res = true;
    } else {
      CustomAssert.fail( "70476", "Error dialog did not appear!" );
      CustomAssert.fail( "70477", "Error dialog did not appear!" );
    }

    return res;
  }

  protected void clickOK() {
    click( btnOK );
  }

  public void dismissErrorDialog( String name ) {
    click( btnOKErrorDialog );
    click( btnCancel );
  }

  protected void clickConfirmDeleteButton() {
    click( btnConfirmDelete );
  }

  protected void clickCancelDeleteUserButton() {
    click( btnCancelDelete );
  }
  
  public void createUser(PentahoUser user){
    clickUsersAndRoles();
    clickManageUsersTab();
    clickNewUserButton();
    setNewUserName( user.getName() );
    setNewUserPassword( user.getPassword() );
    confirmNewUserPassword( user.getPassword() );
    clickOK();
  }
  
  public void deleteUser( PentahoUser user ) {
    activateUser( user );
    clickDeleteUserButton();
    clickConfirmDeleteButton();
  }
  
  public boolean verifyUser( PentahoUser user ) {
    boolean res = true;
    clickUsersAndRoles();
    clickManageUsersTab();

    if ( !isElementPresent( format( this.user, user.getName() ) ) ) {
      Assert.fail( "User " + user.getName() + " not exist!" );
      res = false;
    }
    return res;
  }
  
  public boolean verifyDeletedUser( PentahoUser user ) {
    boolean res = true;
    clickUsersAndRoles();
    clickManageUsersTab();
    if ( !isElementNotPresent( format( this.user, user.getName() ) ) ) {
      Assert.fail( "User " + user.getName() + " is exist!" );
      res = false;
    }
    return res;
  }

  protected void clickManageRolesTab() {
    click( manageRolesTab );
  }

  protected void clickNewRoleButton() {
    click( btnNewRole );
  }

  protected void clickDeleteRoleButton() {
    click( btnDeleteRole );
  }

  protected void setRoleName( String name ) {
    String roleName = StringUtils.substringBeforeLast( ROLE_NAME, " :" );
    type( format( txtRoleName, L10N.generateConcatForXPath( roleName ) ), name );
  }

  public void setPermissions( Permissions permissions ) {
    for ( Map.Entry<Integer, String> entry : Permissions.names.entrySet() ) {
      int roleId = entry.getKey();
      String name = L10N.getText( entry.getValue() ).trim();
      ExtendedWebElement roleElement = format( permissionCheckBox, L10N.getText( name ) );
      if ( permissions.hasPermission( roleId ) ) {
        // check that permission is not checked before trying to click on it, otherwise it will uncheck it
        checkPermission( roleElement );
      } else {
        uncheckPermission( roleElement );
      }
    }
  }
  
  public boolean verifyRolePermissions( Permissions permissions ) {
    boolean res = true;
    for ( Map.Entry<Integer, String> entry : Permissions.names.entrySet() ) {
      int roleId = entry.getKey();
      String name = L10N.getText( entry.getValue() ).trim();
      ExtendedWebElement roleElement = format( permissionCheckBox, L10N.getText( name ) );
      if ( permissions.hasPermission( roleId ) ) {
        // check that permissions were selected.
        if ( !roleElement.isChecked() ) {
          Assert.fail( name + " was not checked!" );
          res = false;
        }
      }
    }
    return res;
  }

  private void checkPermission( ExtendedWebElement element ) {
    if ( !element.isChecked() ) {
      click( element );
    }
    // check that permissions were selected.
    if ( !element.isChecked() ) {
      Assert.fail( name + " was not checked!" );
    }
  }

  private void uncheckPermission( ExtendedWebElement element ) {
    if ( element.isChecked() ) {
      click( element );
    }
    // check that permissions were selected.
    if ( element.isChecked() ) {
      Assert.fail( name + " was not unchecked!" );
    }
  }
  
  protected void clickAddUsers() {
    click( addUsersButton );
  }
  
  protected void clickRemoveUsers() {
    click( removeUsersButton );
  }
  
  protected void clickAddRoles(){
    click( addRolesButton );
  }
  
  protected void clickRemoveRoles() {
    click( removeRolesButton );
  }

  protected void selectRole( String role ) {
    /* [BenF]
     * doubleClick() helps to ensure the selection of a single role from the roles list in Chrome and FireFox.
     * Without doubleClick(), two roles will be selected because Administrator is already selected by default preventing
     * the admin user from being able to actually delete the other selected role.
     */ 
    doubleClick( format( this.role, role ) );
    select( roles, role );
  }

  public void createRole( Role role ) {
    clickUsersAndRoles();
    clickManageRolesTab();
    clickNewRoleButton();
    setRoleName( role.getName() );
    clickOK();
    if ( !isErrorDialogPresent( role.getName() ) ) {
      if ( role.getPermissions() != null ) {
        setPermissions( role.getPermissions() );
      }
    }
  }

  public boolean verifyRole( Role role ) {
    boolean res = true;
    clickUsersAndRoles();
    clickManageRolesTab();
    SoftAssert softAssert = new SoftAssert();
    if ( !isElementPresent( format( this.role, role.getName() ) ) ) {
      softAssert.fail( "Role " + role.getName() + " not exist!" );
      res = false;
    }
    res = verifyRolePermissions( role.getPermissions() );
    softAssert.assertTrue( verifyRoleUsers( role ), "Incorrect list of users in the role " + role.getName() + " !" );
    softAssert.assertAll();
    return res;
  }
  
  public boolean verifyDeletedRole( Role role ) {
    boolean res = true;
    clickUsersAndRoles();
    clickManageRolesTab();
    if ( !isElementNotPresent( format( this.role, role.getName() ) ) ) {
      Assert.fail( "Role " + role.getName() + " is exist!" );
      res = false;
    }
    return res;
  }
  
  public boolean verifyRoleUsers( Role role ) {
    boolean res = true;
    activateRole( role );
    for ( PentahoUser user : role.getUsers() ) {
      if ( !isElementPresent( format( selectedUser, user.getName() ), EXPLICIT_TIMEOUT / 5 ) ) {
        Assert.fail( "User " + user.getName() + " not assigned to role " + role.getName() + "!" );
        res = false;
      }
    }
    return res;
  }
  
  public void deleteRole( Role role ) {
    activateRole( role );

    if ( role.getPermissions() != null ) {
      setPermissions( new Permissions( Permissions.NO_PERMISSIONS ) );
    }

    clickDeleteRoleButton();
    clickConfirmDeleteButton();
  }

  public void assignUserToRole( Role role, PentahoUser newUser ) {
    activateRole( role );
    addUserToRole( newUser );
  }

  public void unassignUserFromeRole( Role role, PentahoUser user ) {
    activateRole( role );
    removeUserFromRole( user );
  }

  public void assignUsersToRole( Role role, List<PentahoUser> newUsersList ) {
    activateRole( role );
    addUsersToRole( newUsersList );
  }

  public void unassignUsersFromRole( Role role, List<PentahoUser> usersList ) {
    activateRole( role );
    removeUsersFromRole( usersList );
  }

  public void addUserToRole( PentahoUser newUser ) {
    /* [BenF]
     * Added a pause for QUALITY-1632 because FireFox was having trouble with isElementPresent even with an explicit timeout
     * causing the click actions to be skipped.
     */
    pause( 1 );
    if ( isElementPresent( format( availableToSelectUser, newUser.getName() ) ) ) {
      click( format( availableToSelectUser, newUser.getName() ) );
      clickAddUsers();
    }
  }

  public void removeUserFromRole( PentahoUser user ) {
    if ( isElementPresent( format( selectedUser, user.getName() ) ) ) {
      click( format( selectedUser, user.getName() ) );
      clickRemoveUsers();
    }
  }

  public void addUsersToRole( List<PentahoUser> usersList ) {
    for ( PentahoUser user : usersList ) {
      addUserToRole( user );
    }
  }

  public void removeUsersFromRole( List<PentahoUser> usersList ) {
    for ( PentahoUser user : usersList ) {
      removeUserFromRole( user );
    }
  }

  public void unassignRoleFromeUser( PentahoUser user, Role role ) {
    activateUser( user );
    removeRoleFromUser( role );
  }

  public void unassignRolesFromUser( PentahoUser user, List<Role> rolesList ) {
    activateUser( user );
    removeRolesFromUser( rolesList );
  }

  public void removeRoleFromUser( Role role ) {
    if ( isElementPresent( format( selectedRole, role.getName() ) ) ) {
      click( format( selectedRole, role.getName() ) );
      clickRemoveRoles();
    }
  }

  public void removeRolesFromUser( List<Role> roles ) {
    for ( Role role : roles ) {
      removeRoleFromUser( role );
    }
  }
  
  private void activateRole( Role role ) {
    clickUsersAndRoles();
    clickManageRolesTab();
    selectRole( role.getName() );
  }

  private void activateUser( PentahoUser user ) {
    clickUsersAndRoles();
    clickManageUsersTab();
    selectUser( user.getName() );
  }

  public void assignRoleToUser( PentahoUser user, Role newRole ) {
    activateUser( user );
    addRoleToUser( newRole );
  }

  public void assignRolesToUser( PentahoUser user, List<Role> newRoleList ) {
    activateUser( user );
    addRolesToUser( newRoleList );
  }

  public void addRoleToUser( Role newRole ) {
    if ( isElementPresent( format( availableToSelectRole, newRole.getName() ) ) ) {
      click( format( availableToSelectRole, newRole.getName() ) );
      clickAddRoles();
    }
  }

  public void addRolesToUser( List<Role> newRoleList ) {
    for ( Role newRole : newRoleList ) {
      addRoleToUser( newRole );
    }
  }

  public boolean verifyUserRoles(PentahoUser user){
    boolean res = true;
    activateUser( user );
    for(Role role: user.getRoles()){
      if(!isElementPresent( format( selectedRole, role.getName() ) )){
        Assert.fail( "Role "+role.getName()+" not assigned to user "+user.getName()+"!" );
        res = false;
      }
    }
    return res;
  }

  public String getHostName() {
    return txtInputs.get( 0 ).getAttribute( ATTRIBUTE_VALUE );
  }

  public String getPort() {
    return txtInputs.get( 1 ).getAttribute( ATTRIBUTE_VALUE );
  }

  public String getUserName() {
    return txtInputs.get( 2 ).getAttribute( ATTRIBUTE_VALUE );
  }

  public String getPassword() {
    return passwordInput.getAttribute( ATTRIBUTE_VALUE );
  }

  public String getEmailFromAddress() {
    return txtInputs.get( 3 ).getAttribute( ATTRIBUTE_VALUE );
  }

  public String getEmailFromName() {
    return txtInputs.get( 4 ).getAttribute( ATTRIBUTE_VALUE );
  }
}
