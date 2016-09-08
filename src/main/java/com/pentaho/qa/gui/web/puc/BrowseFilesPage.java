package com.pentaho.qa.gui.web.puc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.puc.schedules.SchedulePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.puc.browse.Folder;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class BrowseFilesPage extends FramePage {

  private static final String HOME = "/" + L10N.getText( "home" );
  private static final String TRASH = "/" + L10N.getText( "trash" );

  // private static final String EXPAND_FOLDER_ITEM_TAG = "//div[@class='title' and text()='%s']";
  // @FindBy( xpath = "//div[@class='title' and contains(text(),'%s')]" )
  @FindBy( xpath = "//div[@class='title' and normalize-space(.)='%s']" )
  private ExtendedWebElement expandFolderItem;

  private static final String FOLDER_ITEM_TAG =
      "//div[contains(@class,'folder')]/div[1]/div[text()='%s' and @class='title']";
  @FindBy( xpath = "//div[contains(@class,'folder')]/div[1]/div[text()='%s']" )
  private ExtendedWebElement folderItem;

  @FindBy( xpath = "//div[contains(@class,'folder')]/div[1]/div[text()='%s']/../.." )
  private ExtendedWebElement treeItem;

  @FindBy( xpath = "//div[contains(@class,'folder')]/div[1]/div[text()='%s']/../div[@class='expandCollapse']" )
  private ExtendedWebElement folderItemExpanded;

  // private static final String FILE_ITEM_TAG = "//div[contains(@class,'file')]/div[text()='%s']";
  @FindBy( xpath = "//div[contains(@class,'file')]/div[contains(text(), '%s')]" )
  private ExtendedWebElement fileItem;

  // private static final String SCHEDULE_FORMAT_OUTPUT = "//input[@name='REPORT_FORMAT_TYPE' and @value='%s']";
  @FindBy( xpath = "//input[@name='REPORT_FORMAT_TYPE' and @value='%s']" )
  private ExtendedWebElement scheduleFormatItem;

  @FindBy( id = "foldersHeader" )
  protected ExtendedWebElement textFolders;

  @FindBy( id = "buttonsHeader" )
  protected ExtendedWebElement actionsTitle;

  @FindBy( css = "#fileBrowserFiles .file > .title" )
  protected ExtendedWebElement fileBrowserFiles;

  // Folders
  @FindBy( id = "fileBrowserFolders" )
  protected ExtendedWebElement foldersPane;

  @FindBy( id = "refreshBrowserIcon" )
  protected ExtendedWebElement btnRefresh;

  @FindBy( xpath = "//div[@path='/home']//div[text()='{L10N:home}']" )
  protected ExtendedWebElement homeFolder;

  @FindBy( xpath = "//div[@path='/public']//div[text()='Public']" )
  protected ExtendedWebElement publicFolder;

  @FindBy( xpath = "//div[@path='.trash']//div[text()='{L10N:trash}']" )
  protected ExtendedWebElement trashFolder;

  // Files
  @FindBy( id = "fileBrowserFiles" )
  protected ExtendedWebElement filesPane;

  @FindBy( xpath = "//div[@id='fileBrowserFiles']//div[@class='emptyFolder']/span[text()='{L10N:emptyFolder}']" )
  protected ExtendedWebElement emptyFolder;

  // Folder Actions
  @FindBy( id = "fileBrowserButtons" )
  protected ExtendedWebElement folderActionsPane;

  @FindBy( id = "newFolderButton" )
  protected ExtendedWebElement lnkNewFolder;

  @FindBy( id = "deleteFolderButton" )
  protected ExtendedWebElement lnkMoveToTrashFolder;

  @FindBy( id = "renameButton" )
  protected ExtendedWebElement lnkRename;

  @FindBy( id = "uploadButton" )
  protected ExtendedWebElement lnkUpload;

  @FindBy( id = "downloadButton" )
  protected ExtendedWebElement lnkDownload;

  @FindBy( id = "propertiesButton" )
  protected ExtendedWebElement lnkProperties;

  // File Actions
  @FindBy( id = "scheduleButton" )
  protected ExtendedWebElement lnkSchedule;

  @FindBy( id = "copyButton" )
  protected ExtendedWebElement lnkCopy;

  @FindBy( id = "pasteButton" )
  protected ExtendedWebElement lnkPaste;

  @FindBy( id = "restore" )
  protected ExtendedWebElement lnkRestore;

  @FindBy( id = "permDel" )
  protected ExtendedWebElement lnkPermanentlyDelete;

  // Actions For Trash
  @FindBy( id = "purge" )
  protected ExtendedWebElement lnkEmptyTheTrash;

  // New Folder dialog

  // 'New Folder'
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:newFolder}']" )
  protected ExtendedWebElement dlgNewFolder;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancel;

  @FindBy( xpath = "//div[@class='pentaho-dialog']//input" )
  protected ExtendedWebElement inputNewFolderName;

  // Move to Trash dialog

  // 'Move to Trash'
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:moveToTrash}']" )
  protected ExtendedWebElement dlgMoveToTrash;

  // Rename dialog

  @FindBy( id = "dialogRename" )
  protected ExtendedWebElement dlgRename;

  @FindBy( id = "rename-field" )
  protected ExtendedWebElement inputRename;

  @FindBy( css = "div#dialogRename button.ok" )
  protected ExtendedWebElement btnRenameOK;

  @FindBy( css = "div#dialogRename button.cancel" )
  protected ExtendedWebElement btnRenameCancel;

  // Links Will Be Lost dialog

  // 'Links Will Be Lost'
  @FindBy(
      xpath = "//div[@id='dialogOverrideFolder']//*[@id='override-description' and contains(text(), '{L10N:overrideDescription_folder}')]" )
  protected ExtendedWebElement dlgLinksWillBeLostFolder;

  @FindBy(
      xpath = "//div[@id='dialogOverrideFolder']//*[@id='override-description' and contains(text(), '{L10N:overrideDescription_file}')]" )
  protected ExtendedWebElement dlgLinksWillBeLostFile;

  // Do not show message
  @FindBy( xpath = "//div[@id='dialogOverrideFolder']//*[text()='{L10N:overrideCheckbox}']" )
  protected ExtendedWebElement textDoNotShowMessage;

  // Do not show message checkbox
  @FindBy( xpath = "//div[@id='dialogOverrideFolder']//*[@id='do-not-show']" )
  protected ExtendedWebElement checkboxDoNotShowMessage;

  // 'Yes, Rename'
  @FindBy( xpath = "//div[@id='dialogOverrideFolder']//button[text()='{L10N:overrideYesButton}']" )
  protected ExtendedWebElement btnYesRename;

  // 'No'
  @FindBy( xpath = "//div[@id='dialogOverrideFolder']//button[text()='{L10N:overrideNoButton}']" )
  protected ExtendedWebElement btnNo;

  //
  // Schedule
  @FindBy( id = "schedule-name-input" )
  protected ExtendedWebElement scheduleName;

  @FindBy( id = "generated-content-location" )
  protected ExtendedWebElement scheduleLocation;

  // 'New Schedule'
  @FindBy( xpath = "//div[@class='Caption' and contains(., '{L10N:newSchedule}')]" )
  protected ExtendedWebElement textNewSchedule;

  @FindBy( xpath = "//select[@class='gwt-ListBox']" )
  protected List<ExtendedWebElement> listBoxes;

  public BrowseFilesPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "BrowseFilesPage is not opened!" );
    }
  }

  public boolean isOpened() {
    switchToFrame( browserFrame );
    boolean res = isOpened( textFolders );
    switchToDefault();
    return res;
  }

  public String openReport( String reportPath ) {
    activateReport( reportPath );
    switchToFrame( browserFrame );
    click( lnkOpen, true );
    if ( isAlertPresent() ) {
      acceptAlert();
    }
    switchToDefault();
    PentahoUser.incOpenedTab();
    return getFileName( reportPath );
  }

  // TODO: Remove this method. refactor existing tests
  public void activateFolder( String path ) {
    List<String> items = Utils.parsePath( path );
    activateFolder( items );
  }

  public void activateFolder( Folder folder ) {
    List<String> items = Utils.parsePath( folder.getPath() );
    activateFolder( items );
  }

  public void activateReport( String reportPath ) {
    List<String> items = Utils.parsePath( reportPath );
    String filename = getFileName( reportPath );

    // exclude filename from the list
    items.remove( items.size() - 1 );

    activateFolder( items );
    switchToFrame( browserFrame );

    click( format( expandFolderItem, filename ) );

    // verification part
    String fileActions = L10N.getText( "fileActions" );
    Assert.assertTrue( actionsTitle.getText().equals( fileActions ), "File Actions is not recognized!" );

    switchToDefault();
  }

  public void expandFolder( String folderName ) {
    switchToFrame( browserFrame );
    if ( !isFolderExpanded( folderName ) ) {
      click( format( folderItemExpanded, folderName ), true );
    }
    switchToDefault();
  }

  /**
   * Activates several reports
   * 
   * @folderPath base folder path which contains reports to activate
   * @reportNames the list of report names which are going to be activated
   **/
  /*
   * public void activateSeveralReports(String folderPath, List<String> reportNames) {
   * 
   * activateFolder(folderPath); driver.switchTo().frame(browserFrame.getElement());
   * 
   * for (int i = 0; i < reportNames.size() - 1; i++) { LOGGER.info("Selecting '" + reportNames.get(i) + "' file...");
   * String xpath = String.format(FILE_ITEM_TAG, reportNames.get(i)); ExtendedWebElement reportItem = new
   * ExtendedWebElement(driver.findElement(By.xpath(xpath)), "reportItem"); click(reportItem); }
   * 
   * 
   * 
   * // verification part Assert.assertTrue(actionsTitle.getText().equals("File Actions"),
   * "File Actions is not recognized!");
   * 
   * switchToDefault(); }
   */

  protected void activateFolder( List<String> items ) {
    switchToFrame( browserFrame );

    for ( int i = 0; i < items.size(); i++ ) {
      String item = items.get( i );
      // if (item.equalsIgnoreCase( "home" )) {
      // item = L10N.getText( items.get( i ) );
      // }

      LOGGER.info( "Activating '" + item + "' folder." );

      // Select the folder to activate files if any in the right panel
      // click(format(folderItem, items.get(i)));
      boolean recognizedFolders = false;
      int count = 0;
      // execute 5 attempts to read folders path
      while ( !recognizedFolders & ++count < 5 ) {
        List<ExtendedWebElement> elements =
            findExtendedWebElements( By.xpath( String.format( FOLDER_ITEM_TAG, item ) ) );
        if ( elements.size() > 1 ) {
          for ( int j = elements.size() - 1; j >= 0; j-- ) {
            if ( elements.get( j ).getElement().isDisplayed() ) {
              click( elements.get( j ) );
              break;
            }
          }
          recognizedFolders = true;
        } else if ( ( elements.size() == 1 ) ) {
          click( elements.get( elements.size() - 1 ) );
          recognizedFolders = true;
        } else /* size == 0 */ {
          LOGGER.error( "Unable to recognize FOLDER_ITEM_TAG value! count: " + count );
          pause( EXPLICIT_TIMEOUT / 30 );
        }
      }

      if ( isFolderExpanded( item ) ) {
        LOGGER.info( "Folder '" + item + "' has been already expanded, no actions required." );
        continue;
      }

      // Break if folder doesn't have subfolders
      // Also there is no sense to expand our target folder as it will be activated forcibly
      if ( isFolderEmpty( item ) || ( i == ( items.size() - 1 ) ) ) {
        break;
      }

      click( format( folderItemExpanded, item ), true );

      Assert.assertTrue( isFolderExpanded( item ), "Unable to activate " + item + " folder" );
    }

    if ( !isFolderEmpty() ) {
      CustomAssert.fail( "40879", "This newly created folder is not empty" );
    }

    switchToDefault();
  }

  public void activateTrashBin() {
    switchToFrame( browserFrame );
    click( trashFolder, EXPLICIT_TIMEOUT / 10 );

    if ( !isElementPresent( lnkEmptyTheTrash ) && !lnkEmptyTheTrash.getElement().isEnabled() ) {
      CustomAssert.fail( "40668", "The 'Empty the Trash' button is not present or enabled!" );
    }

    switchToDefault();
  }

  public String getFileName( String path ) {
    List<String> items = Utils.parsePath( path );
    return items.get( items.size() - 1 );
  }

  public String removeLastItemPath( String path ) {
    String splitPath = "";
    List<String> items = Utils.parsePath( path );
    items.remove( items.size() - 1 );
    for ( int i = 0; i < items.size() - 1; i++ ) {
      splitPath = items.get( 0 ) + "/";
    }
    return splitPath;
  }

  public String getUserHome() {
    return HOME + "/" + getUserName();
  }

  public List<String> getListFileNames() {
    // TODO: add additional filtering to open only prpt or xaction reports etc

    switchToFrame( browserFrame );

    // List of all file web elements
    List<WebElement> files = driver.findElements( By.cssSelector( "#fileBrowserFiles .file > .title" ) );

    // List of file names
    List<String> listFiles = new ArrayList<String>();

    for ( WebElement checkbox : files ) {
      String name = checkbox.getText();
      if ( !name.contains( "/" ) ) {
        listFiles.add( name );
      }
    }

    switchToDefault();

    return listFiles;
  }

  public SchedulePage openSchedule( String reportPath ) {
    List<String> items = Utils.parsePath( reportPath );
    String filename = getFileName( reportPath );

    // exclude filename from the list
    items.remove( items.size() - 1 );

    activateFolder( items );
    switchToFrame( browserFrame );

    click( format( expandFolderItem, filename ) );

    click( lnkSchedule );

    switchToDefault();
    
    if ( !isElementPresent( textNewSchedule ) ) {
      Assert.fail( "TS041998: New Schedule window is not opened!" );
    }

    if ( Configuration.get( Parameter.LOCALE ).contains( "en" ) ) {
      // do not verify on localized env as name in this case hasn't locale prefix like: Sales Trend (multi-chart) [DE]
      // the method was changed from 'equal' to 'contains' because during creation schedule by default the puc adds
      // additional text for report name.
      Assert.assertTrue( scheduleName.getAttribute( HTML.VALUE ).contains( filename ),
          "TS041998: Schedule Name default name is not populated correctly!" );
    }
    Assert.assertTrue( scheduleLocation.getAttribute( HTML.VALUE ).equals( "/home/admin" ),
        "TS041998: Generated Content Location doesn't show '/home/admin'!" );

    return new SchedulePage( getDriver() );
  }

  /*
   * public void openSchedule( String name ) { switchToFrame( browserFrame ); click( lnkSchedule );
   * 
   * switchToDefault();
   * 
   * if ( !isElementPresent( textNewSchedule ) ) { Assert.fail( "TS041998: New Schedule window is not opened!" ); } }
   */

  /*
   * private ExtendedWebElement findSelectByValue(List<ExtendedWebElement> elements, String value) { ExtendedWebElement
   * listBox = null;
   * 
   * for (ExtendedWebElement element : elements) { if (isElementPresent(element, 1) &&
   * element.getElement().isDisplayed()) { if (getSelectedValue(element).equals(value)) { listBox = element; break; } }
   * } return listBox; }
   */

  public void createFolder( Folder folder ) {
    createFolder( folder.getName() );
  }

  public void createFolderByPath( String folderPath, String folderName ) {

    // Use user home if folderPath is empty
    if ( folderPath.isEmpty() ) {
      folderPath = getUserHome();
    }

    activateFolder( folderPath );
    createFolder( folderName );
  }

  public void createFolder( String folderName ) {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkNewFolder ) && !lnkNewFolder.getElement().isEnabled() ) {
      CustomAssert.fail( "42012: New Folder button is not present or disabled!" );
    }
    click( lnkNewFolder );

    switchToDefault();

    // Verification part
    if ( !verifyNewFolderDialog() ) {
      CustomAssert.fail( "42012", "New Folder dialog is not opened!" );
      CustomAssert.fail( "40665",
          "New Folder dialog is not opened or the OK and Cancel buttons are not present in dialog" );
    }
    // Verification part
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( btnOK.getElement().isEnabled(), "TS042012: Button OK is disabled on New Folder dialog" );
    softAssert.assertTrue( btnCancel.getElement().isEnabled(),
        "TS042012: Button Cancel is disabled on New Folder dialog" );

    type( inputNewFolderName, folderName );
    click( btnOK );

    switchToFrame( browserFrame );

    // Verification part
    if ( !isElementPresent( format( folderItem, folderName ), 5 ) ) {
      CustomAssert.fail( "42178", "Folder " + folderName + " was not created" );
      CustomAssert.fail( "40666", "Folder " + folderName + " was not created" );
    }

    softAssert.assertAll();

    switchToDefault();
  }

  public void copy( File file ) {
    copy( file.getPath() );
  }

  public void copy( String filePath ) {

    activateReport( filePath );

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkCopy ) && !lnkCopy.getElement().isEnabled() ) {
      Assert.fail( "Copy button is not present or disabled!" );
    }
    click( lnkCopy );

    // TODO: verification part
    /*
     * if (!lnkCopy.getElement().isEnabled()) Assert.fail("Copy button was not disabled!");
     */

    switchToDefault();
  }

  public void paste( File file, Folder folder ) {
    paste( file.getName(), folder.getPath() );
  }

  public void paste( String fileName, String targetFolder ) {

    activateFolder( targetFolder );

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkPaste ) && !lnkPaste.getElement().isEnabled() ) {
      Assert.fail( "Paste button is not present or disabled, check if you've copied any files!" );
    }
    click( lnkPaste );

    // Verification part
    if ( !isElementPresent( format( fileItem, fileName ), EXPLICIT_TIMEOUT / 10 ) ) {
      // try to refresh folder and verify again
      btnRefresh.click();
      if ( !isElementPresent( format( fileItem, fileName ), EXPLICIT_TIMEOUT / 10 ) ) {
        Assert.fail( "File " + fileName + " was not pasted under " + targetFolder );
      }
    }

    switchToDefault();
  }

  // Return true if folder is expanded, otherwise false
  protected boolean isFolderExpanded( String folderName ) {
    List<String> classItems = getFolderClass( folderName );
    return classItems.contains( "open" );
  }

  // Return true if folder is empty, otherwise false
  protected boolean isFolderEmpty( String folderName ) {
    List<String> classItems = getFolderClass( folderName );
    return classItems.contains( "empty" );
  }

  // Return true if folder is selected, otherwise false
  protected boolean isFolderSelected( String folderName ) {
    List<String> classItems = getFolderClass( folderName );
    return classItems.contains( "selected" );
  }

  private List<String> getFolderClass( String folderName ) {
    // Get class values
    String folderClass = format( treeItem, folderName ).getAttribute( "class" );
    if ( folderClass == null ) {
      throw new RuntimeException( "Unable to find folder '" + folderName + "' inside tree view!" );
    }
    return new ArrayList<String>( Arrays.asList( folderClass.split( " " ) ) );
  }

  // Return true if folder is selected, otherwise false
  protected boolean isFileSelected( String fileName ) {
    List<String> classItems = getFileClass( fileName );
    return classItems.contains( "selected" );
  }

  private List<String> getFileClass( String fileName ) {
    // Get class values
    String fileClass = format( fileItem, fileName ).getAttribute( "class" );
    return new ArrayList<String>( Arrays.asList( fileClass.split( " " ) ) );
  }

  // Return true if file is present under specified folder
  public boolean isFilePresentByPath( String filePath ) {
    List<String> items = Utils.parsePath( filePath );
    String fileName = getFileName( filePath );

    // exclude fileName from the list
    items.remove( items.size() - 1 );

    activateFolder( items );
    return isFilePresent( fileName );
  }

  // Return true if file is present under specified folder
  public boolean isFilePresent( String fileName ) {
    switchToFrame( browserFrame );

    boolean res = false;
    if ( isElementPresent( format( fileItem, fileName ) ) ) {
      res = true;
    }

    switchToDefault();
    return res;
  }

  public void deleteItem( String reportPath ) {
    activateReport( reportPath );
    switchToFrame( browserFrame );
    click( deleteBtn );
    switchToDefault();
    if ( isElementPresent( okBtnDeleteDialog, EXPLICIT_TIMEOUT / 10 ) ) {
      click( okBtnDeleteDialog );
    }
  }

  // Return true if folder exists in the system
  public boolean isFolderPresentByPath( String folderPath ) {
    List<String> items = Utils.parsePath( folderPath );
    String lastFolder = items.get( items.size() - 1 );

    // exclude last folder from the list
    items.remove( items.size() - 1 );
    activateFolder( items );

    return isFolderPresent( lastFolder );
  }

  // Return true if folder exists in current folder tree
  public boolean isFolderPresent( String folderName ) {
    switchToFrame( browserFrame );

    boolean res = false;
    if ( isElementPresent( format( folderItem, folderName ) ) ) {
      res = true;
    }
    switchToDefault();
    return res;
  }

  public boolean isFolderEmpty() {
    switchToFrame( browserFrame );

    boolean res = false;
    if ( isElementPresent( emptyFolder, EXPLICIT_TIMEOUT / 20 ) ) {
      res = true;
    }
    switchToDefault();
    return res;
  }

  public void renameFolder( Folder folder, Folder newFolder, boolean overwrite, boolean confirm ) {
    renameFolder( folder.getPath(), newFolder.getName(), overwrite, confirm );
  }

  public void renameFolder( String folderPath, String newFolderName ) {
    renameFolder( folderPath, newFolderName, true, true );
  }

  public void renameFolder( String folderPath, String newFolderName, boolean overwrite, boolean confirm ) {
    // If folderPath is not path but folderName than it will be considered as folder under UserHome
    if ( !isPath( folderPath ) ) {
      folderPath = getUserHome() + "/" + folderPath;
    }
    activateFolder( folderPath );

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkRename ) && !lnkRename.getElement().isEnabled() ) {
      Assert.fail( "Rename button is not present or disabled!" );
    }
    click( lnkRename );

    switchToDefault();

    // If Links Will Be Lost dialog appears click 'Yes, Rename' for closing it
    if ( verifyLinksWillBeLostDialog() && overwrite ) {
      click( btnYesRename );

      // Verification part
      if ( !verifyRenameDialog() ) {
        CustomAssert.fail( "42014", "Rename dialog is not opened!" );
        CustomAssert.fail( "40876", "Rename dialog is not opened!" );
      }
      type( inputRename, newFolderName );

      // Verification part
      if ( !btnRenameOK.getElement().isEnabled() ) {
        CustomAssert.fail( "42014", "Button OK was not enabled in Rename dialog after entering new folder name!" );
        CustomAssert.fail( "40877", "OK button is disabled after entering new folder name" );
      }

      if ( confirm ) {
        // click OK and verify folder was renamed
        click( btnRenameOK );

        // Verification part
        if ( isElementPresent( dlgRename, 3 ) ) {
          CustomAssert.fail( "42014", "Rename dialog is not closed!" );
        }
        // Verification part
        String newFolderPath = removeLastItemPath( folderPath ) + newFolderName;
        if ( !isFolderPresentByPath( newFolderPath ) ) {
          CustomAssert.fail( "42014", "Folder " + folderPath + " was not renamed to " + newFolderName );
          CustomAssert.fail( "40878", "Folder was NOT renamed to " + newFolderName );
        }
      } else {
        // click Cancel and verify folder was NOT renamed
        click( btnRenameCancel );
        if ( isElementPresent( dlgRename, 3 ) ) {
          CustomAssert.fail( "40877", "Rename dialog is still present!" );
          // Verify that folder was NOT renamed
          String newFolderPath = removeLastItemPath( folderPath ) + newFolderName;
          if ( isFolderPresentByPath( newFolderPath ) ) {
            CustomAssert.fail( "40877", "Folder was renamed to " + newFolderName );
          }
        }
      }

    } else {
      click( btnNo );

      // Verification part
      String newFolderPath = removeLastItemPath( folderPath ) + newFolderName;
      if ( isFolderPresentByPath( newFolderPath ) ) {
        CustomAssert.fail( "40875", "Folder was renamed to " + newFolderName );
      }
    }

    switchToDefault();
  }

  public void moveToTrash( Folder folder ) {
    moveToTrash( folder.getPath() );
  }

  public boolean moveToTrash( String folderPath ) {

    boolean res = true;
    // If folderPath is not path but folderName than it will be considered as folder under UserHome
    if ( !isPath( folderPath ) ) {
      folderPath = getUserHome() + "/" + folderPath;
    }

    activateFolder( folderPath );

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkMoveToTrashFolder ) && !lnkMoveToTrashFolder.getElement().isEnabled() ) {
      Assert.fail( "Move To Trash button is not present or disabled!" );
    }
    click( lnkMoveToTrashFolder );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgMoveToTrash ) ) {
      Assert.fail( "Move to Trash dialog is not opened!" );
    }
    click( btnOK );

    // Verification part
    if ( isElementPresent( dlgMoveToTrash, 3 ) ) {
      Assert.fail( "Move to Trash dialog is not closed!" );
    }
    if ( isFolderPresentByPath( folderPath ) ) {
      CustomAssert.fail( "40667", "Folder was not successfully deleted!" );
      res = false;
    }
    switchToDefault();

    return res;
  }

  public void moveToTrash() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkMoveToTrashFolder ) && !lnkMoveToTrashFolder.getElement().isEnabled() ) {
      Assert.fail( "Move To Trash button is not present or disabled!" );
    }
    click( lnkMoveToTrashFolder );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgMoveToTrash ) ) {
      Assert.fail( "Move to Trash dialog is not opened!" );
    }
    click( btnOK );
  }

  public void restoreFolder( Folder folder ) {
    restoreFolder( folder.getName() );
  }

  public void restoreFolder( String folder ) {
    // go to Trash and select folder
    activateFolder( TRASH );
    activateReport( folder );

    switchToFrame( browserFrame );

    // Check that the Restore button is present and enabled
    if ( !isElementPresent( lnkRestore ) && !lnkRestore.getElement().isEnabled() ) {
      Assert.fail( "Restore button is not present or disabled!" );
    }
    click( lnkRestore );

    switchToDefault();

    // Verify that the restored folder is no longer present in Trash
    if ( isFilePresentByPath( folder ) ) {
      CustomAssert.fail( "40669", "The folder was not restored!" );
    }
  }

  public void restore() {
    switchToFrame( browserFrame );

    // Check that the Restore button is present and enabled
    if ( !isElementPresent( lnkRestore ) && !lnkRestore.getElement().isEnabled() ) {
      Assert.fail( "Restore button is not present or disabled!" );
    }
    click( lnkRestore );

    switchToDefault();
  }

  public void downloadFolder( String folderPath ) {

    // If folderPath is not path but folderName than it will be considered as folder under UserHome
    if ( !isPath( folderPath ) ) {
      folderPath = getUserHome() + "/" + folderPath;
    }
    activateFolder( folderPath );

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkDownload ) && !lnkDownload.getElement().isEnabled() ) {
      Assert.fail( "Download button is not present or disabled!" );
    }
    click( lnkDownload );

    switchToDefault();
  }

  private boolean isPath( String text ) {
    return text.contains( "/" );
  }

  public boolean verifyNewFolderDialog() {
    return ( isElementPresent( dlgNewFolder ) && isElementPresent( btnOK ) && isElementPresent( btnCancel ) );
  }

  public boolean verifyRenameDialog() {
    boolean res = false;
    if ( isElementPresent( dlgRename ) && isElementPresent( inputRename ) && ( isElementPresent( btnRenameOK )
        && !btnRenameOK.getElement().isEnabled() ) && ( isElementPresent( btnRenameCancel ) && btnRenameCancel
            .getElement().isEnabled() ) ) {
      res = true;
      CustomAssert.fail( "40876", "Rename dialog is not present or valid." );
    }
    return res;
  }

  public boolean verifyLinksWillBeLostDialog() {
    boolean res = false;
    if ( isElementPresent( dlgLinksWillBeLostFolder ) && isElementPresent( textDoNotShowMessage ) && ( isElementPresent(
        checkboxDoNotShowMessage ) && !checkboxDoNotShowMessage.isChecked() ) && isElementPresent( btnYesRename )
        && isElementPresent( btnNo ) ) {
      res = true;
      CustomAssert.fail( "40855", "Links Will Be Lost dialog is not present or valid." );
    }
    return res;
  }

  public void verifyBrowsePerspective() {
    switchToFrame( browserFrame );
    long timeout = EXPLICIT_TIMEOUT / 15;

    SoftAssert softAssert = new SoftAssert();
    // Folder pane
    softAssert.assertTrue( isElementPresent( foldersPane, timeout ), "'Folders Pane' is not present!" );
    softAssert.assertTrue( isElementPresent( btnRefresh, timeout ), "'Refresh' button is not present!" );
    softAssert.assertTrue( isElementPresent( homeFolder, timeout ), "'Home' folder is not present!" );
    softAssert.assertTrue( isElementPresent( publicFolder, timeout ), "'Public' folder is not present!" );
    softAssert.assertTrue( isElementPresent( trashFolder, timeout ), "'Trash' folder is not present!" );

    // File pane
    softAssert.assertTrue( isElementPresent( filesPane, timeout ), "'Files Pane' is not present!" );

    // Folder Actions pane
    softAssert.assertTrue( isElementPresent( folderActionsPane, timeout ), "'Folder Actions Pane' is not present!" );
    softAssert.assertTrue( isElementPresent( lnkNewFolder, timeout ), "'New Folder' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkMoveToTrashFolder, timeout ),
        "'Move to Trash' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkRename, timeout ), "'Rename' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkPaste, timeout ), "'Paste' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkUpload, timeout ), "'Upload' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkDownload, timeout ), "'Download' button is not present!" );
    softAssert.assertTrue( isElementPresent( lnkProperties, timeout ), "'Properties' button is not present!" );

    switchToDefault();
    softAssert.assertAll();
  }
}
