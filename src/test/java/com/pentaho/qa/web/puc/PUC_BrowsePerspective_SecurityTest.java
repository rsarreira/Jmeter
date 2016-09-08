package com.pentaho.qa.web.puc;

import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.administration.Permissions;
import com.pentaho.services.puc.administration.Role;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.PentahoUser;
import com.pentaho.qa.gui.web.puc.AdministrationPage;
import com.pentaho.qa.gui.web.puc.BrowseFilesPageEx;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.qa.SpiraTestcases;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.Map;

/**
 * A test class to verify that creating a new folder on a user with insufficient permissions for content creation is not
 * allowed and that a user with read only permissions is not allowed to copy and paste files around.
 * 
 * @author Ben Freed
 * @version Last Updated: July 8th, 2016
 * @see http://spiratest.pentaho.com/6/TestCase/11940.aspx
 * 
 */

@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.PUC_BrowsePerspective_SecurityTest )
public class PUC_BrowsePerspective_SecurityTest extends WebBaseTest {

  private PentahoUser adminUser, readOnlyUser, patUser;
  private Role readContentRole;

  /**
   * Create the PentahoUsers for use later.
   */
  @BeforeClass( )
  private void initializeUsersAndRoles() {
    adminUser = new PentahoUser( "admin", "password", false );
    patUser = new PentahoUser( "pat", "password", false );
    readOnlyUser = new PentahoUser( "readonly", "password", false );
    readContentRole = new Role( "auto", "readonly", new Permissions( Permissions.READ_CONTENT ) );
  }

  /**
   * Verify that creating a new folder is NOT allowed.
   */
  @Test( )
  @SpiraTestSteps( testStepsId = "64437" )
  public void verifyCreateNewFolderIsNotAllowed() {
    HomePage homePage = patUser.login();
    if ( patUser.isLogged() ) {
      webUser = patUser;
      BrowseFilesPageEx browseFilesPage = (BrowseFilesPageEx) homePage.activateModuleEx( Module.BROWSE_FILES );

      browseFilesPage.activateFolder( getUserHome().getPath() );
      browseFilesPage.createFolder( new Folder( "Permissions Denied Folder", false ) );

      patUser.logout();
    } else {
      Assert.fail( "The user " + patUser.getName() + " was unable to login!" );
    }

  }

  /**
   * Depending on whether or not the "readonly" user was created, verify that the user can NOT copy and paste a file.
   * 
   * @param args
   *          - The specified file sheet from the XlsDataSourceParameters annotation
   */
  @Test( dataProvider = "SingleDataProvider" )
  @SpiraTestSteps( testStepsId = "66436" )
  @XlsDataSourceParameters( sheet = "Files", dsUid = "TUID", executeColumn = "StepId", executeValue = "66436" )
  public void verifyCopyAndPasteReportIsNotAllowed( Map<String, String> args ) {

    createUserWithReadOnlyPermissions();

    HomePage homePage = readOnlyUser.login();

    if ( readOnlyUser.isLogged() ) {
      webUser = readOnlyUser;
      BrowseFilesPageEx browseFilesPage = (BrowseFilesPageEx) homePage.activateModuleEx( Module.BROWSE_FILES );
      File file = (File) BrowseService.getBrowseItem( args.get( "location" ) + "/" + args.get( "Name" ) );

      if ( file.isExist() ) {
        file.copy();
        browseFilesPage.activateFolder( getUserHome().getPath() );
        browseFilesPage.paste();
      } else {
        Assert.fail( "TS064436: " + file.getName() + " does not exist at the following path: " + file.getPath() );
      }

      readOnlyUser.logout();

    } else {
      Assert.fail( "The created read only user " + readOnlyUser.getName() + " was unable to login!" );
    }

    removeUserWithReadOnlyPermissions();

  }

  /**
   * Create a user called "readonly" with only the "Read Content" permission active.
   */
  private void createUserWithReadOnlyPermissions() {

    HomePage homePage = adminUser.login();

    if ( adminUser.isLogged() ) {
      webUser = adminUser;
      AdministrationService.setAdministrationPage( (AdministrationPage) homePage.activateModule(
          Module.ADMINISTRATION ) );

      AdministrationService.addUser( readOnlyUser );
      AdministrationService.addRole( readContentRole );
      AdministrationService.assignUserToRole( readContentRole, readOnlyUser );

      adminUser.logout();
    } else {
      Assert.fail( "The admin user was unable to login!" );
    }

  }

  /**
   * Depending on whether or not the "readonly" user was created, clean up the created read only user and role.
   */
  private void removeUserWithReadOnlyPermissions() {

    HomePage homePage = adminUser.login();

    if ( adminUser.isLogged() ) {
      webUser = adminUser;
      AdministrationService.setAdministrationPage( (AdministrationPage) homePage.activateModule(
          Module.ADMINISTRATION ) );
      AdministrationService.deleteRole( readContentRole );
      AdministrationService.deleteUser( readOnlyUser );

      adminUser.logout();
    } else {
      Assert.fail( "The admin user was unable to login!" );
    }

  }

}
