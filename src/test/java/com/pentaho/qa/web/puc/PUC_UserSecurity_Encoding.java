package com.pentaho.qa.web.puc;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.administration.Role;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/6/TestCase/11902.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.PUC_User_Security_Encoding )
public class PUC_UserSecurity_Encoding extends WebBaseTest {

  private final String usersSheet = "Users";
  private final String rolesSheet = "Roles";
  private PentahoUser user;
  private Role role;

  @BeforeClass
  public void login() {
    webUser.login( getDriver() );
    HomePage homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.ADMINISTRATION );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = usersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "USR1" )
  @SpiraTestSteps( testStepsId = "64098" )
  public void createNewUser( Map<String, String> args ) {
    user = new PentahoUser( args );
    user.add();
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# BISERVER-13227" )
  @XlsDataSourceParameters( sheet = usersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "USR2" )
  @SpiraTestSteps( testStepsId = "70476" )
  public void createInvalidUser( Map<String, String> args ) {
    user = new PentahoUser( args );
    user.add();
  }

  @Test( dataProvider = "SingleDataProvider", description = "JIRA# BISERVER-12230" )
  @XlsDataSourceParameters( sheet = rolesSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "ROLE1" )
  @SpiraTestSteps( testStepsId = "70477" )
  public void createInvalidRole( Map<String, String> args ) {
    role = new Role( args );
    role.add();
  }

  @Test( dataProvider = "SingleDataProvider", dependsOnMethods = "createNewUser" )
  @XlsDataSourceParameters( sheet = rolesSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "ROLE2" )
  @SpiraTestSteps( testStepsId = "64101, 64102" )
  public void createNewRole( Map<String, String> args ) {
    role = new Role( args );
    user = AdministrationService.getUser( args.get( "User" ) );

    role.add();
    role.assignUser( user );
  }
  
  @Test( dependsOnMethods = "createNewRole" )
  @SpiraTestSteps( testStepsId = "64104" )
  public void newUserLogin() {
    webUser.logout();
    HomePage homePage = user.login();

    Assert.assertTrue( homePage.isLogged( user.getName() ), "Incorrect user is logged: '" + user.getName() + "'!" );
    user.logout();
  }
  
  @AfterClass
  public void cleanUp() {
    webUser.login( getDriver() );
    HomePage homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.ADMINISTRATION );
    role.delete();
    user.delete();
  }
}
