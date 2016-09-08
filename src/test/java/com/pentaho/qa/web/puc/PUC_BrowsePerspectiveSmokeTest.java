package com.pentaho.qa.web.puc;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPageEx;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

//http://spiratest.pentaho.com/6/TestCase/11933.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.PUC_Browse_Perspective_SmokeTest )
public class PUC_BrowsePerspectiveSmokeTest extends WebBaseTest {

  private BrowseFilesPageEx browsePage;
  private final String foldersSheet = "Folders";
  private final String filesSheet = "Files";
  private Folder steelWheelsFolder;
  private Folder newFolder;
  private Folder childFolder;
  private Folder newNameFolder;

  @Test( )
  public void login() {
    webUser.login( getDriver() );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64332" )
  public void openBrowsePerspectiveAndCheckPanes() {

    HomePage homePage = new HomePage( getDriver() );
    homePage.switchToDefault();
    browsePage = (BrowseFilesPageEx) homePage.activateModuleEx( Module.BROWSE_FILES );

    // Verification part
    browsePage.verifyBrowsePerspective();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64333" )
  public void selectSteelWheelsAndValidateFilesPane() {
    steelWheelsFolder = (Folder) BrowseService.getBrowseItem( "/public/Steel Wheels" );
    steelWheelsFolder.select();

    // Verification part
    SoftAssert softAssert = new SoftAssert();
    for ( File report : steelWheelsFolder.getChildrenFiles() ) {
      // Verify the list of sample reports is shown
      if ( !browsePage.isFilePresent( report.getName() ) ) {
        softAssert.fail( "TS064333: Sample report '" + report.getName() + "' is not present under '"
            + steelWheelsFolder.getPath() + "' location!" );
      }
      // Verify each report type shows the correct icon
      if ( !browsePage.verifyIcon( report ) ) {
        softAssert.fail( "TS064333: '" + report.getName() + "' contains incorrect icon!" );
      }
    }

    // Verify horizontal scroll bar is shown if the report title is too long
    if ( !browsePage.isHorizontalScrollBarPresentOnFilesPane() ) {
      softAssert.fail( "TS064333: Horizontal scroll bar is not shown inspite of there are files with long titles!" );
    }

    softAssert.assertAll();
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "TUID", executeColumn = "StepId", executeValue = "64334" )
  @SpiraTestSteps( testStepsId = "64334, 64335" )
  public void createNewFolder( Map<String, String> args ) {
    // Verification of TS064334 and TS064335 happen on Page level
    newFolder = new Folder( args );
    newFolder.add( steelWheelsFolder );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64336" )
  public void checkFolderIsEmpty() {
    // Select folder
    newFolder.select();

    // Verify that there are no files in it
    if ( !browsePage.isFolderEmpty() ) {
      Assert.fail( "TS064336: New folder is not empty!" );
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = filesSheet, dsUid = "TUID", executeColumn = "StepId", executeValue = "64337" )
  @SpiraTestSteps( testStepsId = "64337" )
  public void copyReportOfEachType( Map<String, String> args ) {
    File report = (File) BrowseService.getBrowseItem( args.get( "location" ) + "/" + L10N.getText( args.get( "Name" ) ) );
    report.copy();
    report.paste( newFolder );
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "TUID", executeColumn = "StepId", executeValue = "64337" )
  @SpiraTestSteps( testStepsId = "64337" )
  public void createChildFolderAndRemoveParentFolder( Map<String, String> args ) {
    // Create a sub folder
    childFolder = new Folder( args );
    childFolder.add( newFolder );

    // Copy all types of files under childFolder
    for ( File report : newFolder.getChildrenFiles() ) {
      report.copy();
      report.paste( childFolder );
    }

    // Remove parent folder
    newFolder.remove();
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64338" )
  public void checkTrash() {
    BrowseService.getTrash().select();

    // Verification part
    if ( !newFolder.isExist() ) {
      Assert.fail( "TS064338: Folder is not present in under Trash bin!" );
    }
    BrowseService.getTrash().select();
    if ( !browsePage.isEmptyTheTrashEnabled() ) {
      Assert.fail( "TS064338: 'Empty The Trash' button is not present or disabled!" );
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64339" )
  public void restoreFolder() {
    newFolder.restore();

    // Verification part
    BrowseService.getTrash().select();
    if ( browsePage.isFilePresent( newFolder.getName() ) ) {
      Assert.fail( "TS064339: Restored folder '" + newFolder.getName() + "' was not removed from Trash!" );
    }
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64340" )
  public void checkFolderAndContentsWereRestored() {
    // Check newFolder was restored
    if ( !newFolder.isExist() ) {
      Assert.fail( "TS064340: Folder '" + newFolder.getName() + "' was not restored from Trash!" );
    }

    // Check childFolder was restored
    if ( !childFolder.isExist() ) {
      Assert.fail( "TS064340: Subfolder '" + childFolder.getName() + "' was not restored from Trash!" );
    }

    // Check all files were restored under newFolder
    for ( File report : newFolder.getChildrenFiles() ) {
      if ( !report.isExist() ) {
        Assert.fail( "TS064340: Report '" + report.getName() + "' was not restored from Trash!" );
      }
    }

    // Check all files were restored under childFolder
    for ( File report : childFolder.getChildrenFiles() ) {
      if ( !report.isExist() ) {
        Assert.fail( "TS064340: Report '" + report.getName() + "' was not restored from Trash!" );
      }
    }
  }

  @Test( dataProvider = "SingleDataProvider" )
  @XlsDataSourceParameters( sheet = foldersSheet, dsUid = "name", executeColumn = "TUID", executeValue = "FLDR02_EDIT" )
  @SpiraTestSteps( testStepsId = "64341, 64342" )
  public void renameParentFolderAndChooseNo( Map<String, String> args ) {
    newNameFolder = new Folder( args );
    newFolder.rename( newNameFolder.getName(), false, false );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64343, 64344" )
  public void renameParentFolderAndChooseYesThenCancel() {
    newFolder.rename( newNameFolder.getName(), true, false );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "64345" )
  public void renameParentFolderAndChooseYesThenOK() {
    newFolder.rename( newNameFolder.getName(), true, true );
  }

}
