//http://spiratest.pentaho.com/6/TestCase/13680.aspx

package com.pentaho.qa.web.puc;

import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPageEx;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.administration.Role;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

@SpiraTestCase( projectId = 20, testCaseId = SpiraTestcases.PUC_BrowsePerspectiveEncoding )
public class PUC_BrowsePerspectiveEncoding extends WebBaseTest {

  private BrowseFilesPageEx browsePage;
  private final String foldersSheet = "Folders";
  private final String filesSheet = "Files";
  private final String usersSheet = "Users";
  private HomePage homePage;
  private PentahoUser user;
  private PIRReport pirReport;
  private Folder steelWheelsFolder;
  private Folder newFolder;
  private Folder illegalFolder;
  private Folder newNameFolder;
  private File report;

  @BeforeClass
  public void login() {
    webUser.login( getDriver() );
    homePage = new HomePage( getDriver() );
    homePage.switchToDefault();
    browsePage = (BrowseFilesPageEx) homePage.activateModuleEx( Module.BROWSE_FILES );
    steelWheelsFolder = (Folder) BrowseService.getBrowseItem( "/public/Steel Wheels" );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "FLDR03" )
  @SpiraTestSteps( testStepsId = "77289" )
  public void addIllegalFolder( Map<String, String> args ) {
    illegalFolder = new Folder( args );
    steelWheelsFolder.select();
    browsePage.createFolderWithoutVerification( illegalFolder );
    if ( !browsePage.verifyIllegalCharactersMessage() ) {
      Assert.fail( "TS077289: Illegal characters message dialog not shown!" );
    }
    browsePage.clickOkButon();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "FLDR05" )
  public void addCorrectFolder( Map<String, String> args ) {
    newFolder = new Folder( args );
    newFolder.add( steelWheelsFolder );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "FLDR04" )
  @SpiraTestSteps( testStepsId = "77290,77291" )
  public void renameFolder( Map<String, String> args ) {
    newNameFolder = new Folder( args );
    newFolder.select();
    browsePage.renameWithoutVerification( newNameFolder.getName() );
    if ( !browsePage.verifyCannotRenameMessage() ) {
      Assert.fail( "TS077290: Illegal characters message dialog not shown!" );
    }
    browsePage.clickCloseButton();
    browsePage.clickRenameCancelButton();

    // Set valid folder name
    newNameFolder.setName( args.get( "validName" ) );
    newFolder.rename( newNameFolder.getName(), true, true );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filesSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "FILE05" )
  @SpiraTestSteps( testStepsId = "77292" )
  public void renameFile( Map<String, String> args ) {
    report = (File) BrowseService.getBrowseItem( args.get( "location" ) + "/" + L10N.getText( args.get( "Name" ) ) );
    report.select();
    browsePage.renameWithoutVerification( args.get( "invalidName" ) );

    if ( !browsePage.verifyCannotRenameMessage() ) {
      Assert.fail( "TS077292: Illegal characters message dialog not shown!" );
    }
    browsePage.clickCloseButton();
    browsePage.clickRenameCancelButton();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = usersSheet, dsUid = "TUID", executeColumn = "TUID", executeValue = "USR3" )
  @SpiraTestSteps( testStepsId = "77293" )
  public void addNewUser( Map<String, String> args ) {
    homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.ADMINISTRATION );
    user = new PentahoUser( args );
    List<Role> roles = AdministrationService.parseRoles( args.get( "Roles" ) );
    user.add();
    user.assignRoles( roles );

    webUser.logout();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = "PIRreport", dsUid = "TUID", executeColumn = "TUID", executeValue = "PIR01" )
  @SpiraTestSteps( testStepsId = "77293" )
  public void newUserLogin( Map<String, String> args ) {
    homePage = user.login();
    Assert.assertTrue( homePage.isLogged( user.getName() ), "Incorrect user is logged: '" + user.getName() + "'!" );
    pirReport = new PIRReport( args );
    pirReport.create( true );
    Folder folder = (Folder) BrowseService.getBrowseItem( args.get( "location" ) );
    pirReport.save( folder );
    pirReport.close();
    pirReport.open();
    pirReport.close();
    user.logout();
  }

  @Test( description = "JIRA# BACKLOG-10030" )
  @Parameters( { "newReportName" } )
  @SpiraTestSteps( testStepsId = "77294,77295" )
  public void renameReport( String newReportName ) {
    homePage = user.login();
    Assert.assertTrue( homePage.isLogged( user.getName() ), "Incorrect user is logged: '" + user.getName() + "'!" );
    homePage.activateModuleEx( Module.BROWSE_FILES );

    String reportName = pirReport.getName();

    pirReport.rename( newReportName, true, true );
    pirReport.rename( reportName, true, true );
  }
  
  @AfterClass
  public void cleanUp() {
    homePage.switchToDefault();
    homePage.activateModuleEx( Module.BROWSE_FILES );
    pirReport.remove();
    pirReport.deletePermanently();
    
    
    user.logout();
    webUser.login( getDriver() );
    homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.ADMINISTRATION );
    user.delete();
  }
}
