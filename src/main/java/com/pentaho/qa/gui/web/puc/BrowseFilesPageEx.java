package com.pentaho.qa.gui.web.puc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.services.CustomAssert;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.analyzer.PAReport;
import com.pentaho.services.pir.PIRReport;
import com.pentaho.services.puc.browse.File;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class BrowseFilesPageEx extends FramePage {

  private static final String HOME = "/" + L10N.getText( "home" );

  @FindBy( xpath = "//div[@class='title' and normalize-space(.)='%s']" )
  private ExtendedWebElement expandFolderItem;

  @FindBy( xpath = "//div[contains(@class,'folder') and @path='%s']/div[1]/div[@class='title']" )
  private ExtendedWebElement folderItem;

  @FindBy( xpath = "//div[contains(@class,'folder') and @path='%s']" )
  private ExtendedWebElement treeItem;

  @FindBy( xpath = "//div[contains(@class,'folder') and @path='%s']/div[1]/div[@class='expandCollapse']" )
  private ExtendedWebElement folderItemExpanded;

  @FindBy( xpath = "//div[contains(@class,'folder') and contains(@class,'selected')]" )
  private ExtendedWebElement selectedFolder;

  @FindBy( xpath = "//div[contains(@class,'file')]/div[contains(text(), '%s')]" )
  private ExtendedWebElement fileItem;

  @FindBy( xpath = "//div[contains(@class,'file') and div[contains(text(), normalize-space('%s'))]]/div[contains(@class, 'icon')]" )
  private ExtendedWebElement fileIconItem;

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

  // File Actions

  @FindBy( id = "fileBrowserFiles" )
  protected ExtendedWebElement filesPane;

  @FindBy( css = "#fileBrowserFiles > .body" )
  protected ExtendedWebElement filesListElement;

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
  
  @FindBy( id = "deleteFolderButton" )
  protected ExtendedWebElement lnkMoveFolderToTrash;

  // File Actions
  
  @FindBy( id = "deleteButton" )
  protected ExtendedWebElement lnkMoveFileToTrash;
  
  @FindBy( id = "scheduleButton" )
  protected ExtendedWebElement lnkSchedule;

  @FindBy( xpath = "//button[@id='copyButton' and text()='{L10N:contextAction_copy}']" )
  protected ExtendedWebElement lnkCopy;

  @FindBy( xpath = "//button[@id='cutbutton' and text()='{L10N:contextAction_cut}']" )
  protected ExtendedWebElement lnkCut;

  @FindBy( xpath = "//button[@id='pasteButton' and text()='{L10N:contextAction_paste}']" )
  protected ExtendedWebElement lnkPaste;

  // Trash specific actions

  @FindBy( xpath = "//button[@id='restore' and text()='{L10N:contextAction_restore}']" )
  protected ExtendedWebElement lnkRestore;

  @FindBy( xpath = "//button[@id='purge' and text()='{L10N:contextAction_purge}']" )
  protected ExtendedWebElement lnkEmptyTheTrash;

  @FindBy( xpath = "//button[@id='permDel' and text()='{L10N:contextAction_permDelete}']" )
  protected ExtendedWebElement lnkPermanentlyDelete;

  // New Folder dialog

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:newFolder}']" )
  protected ExtendedWebElement dlgNewFolder;

  @FindBy( id = "okButton" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "cancelButton" )
  protected ExtendedWebElement btnCancel;

  @FindBy( xpath = "//div[@class='pentaho-dialog']//input" )
  protected ExtendedWebElement inputNewFolderName;

  // Move to Trash dialog

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:moveToTrash}']" )
  protected ExtendedWebElement dlgMoveToTrash;

  // Permanently Delete dialog

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:permDelete}']" )
  protected ExtendedWebElement dlgPermanentlyDelete;

  @FindBy( xpath = "//button[@id='okButton' and text()='{L10N:yesPermDelete}']" )
  protected ExtendedWebElement btnPermanentlyDelete;

  // Empty Trash dialog

  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:emptyTrash}']" )
  protected ExtendedWebElement dlgEmptyTrash;

  @FindBy( xpath = "//div[@id='okButton' and text()='{L10N:yesEmptyTrash}']" )
  protected ExtendedWebElement btnYesEmptyTrash;

  // Rename dialog

  @FindBy( id = "dialogRename" )
  protected ExtendedWebElement dlgRename;

  @FindBy( id = "rename-field" )
  protected ExtendedWebElement inputRename;

  @FindBy( css = "div#dialogRename button.ok" )
  protected ExtendedWebElement btnRenameOK;

  @FindBy( css = "div#dialogRename button.cancel" )
  protected ExtendedWebElement btnRenameCancel;
  
  // Generic error dialog box with the text based on localization used in conjunction with concatenation and format
  @FindBy ( xpath = "//td[@class='dialog-content']//div[text()=%s]")
  protected ExtendedWebElement dlgErrorBox;

  @FindBy( id = "rename-error-dialog" )
  protected ExtendedWebElement renameErrorDialog;
  
  // Links Will Be Lost dialog

  // 'Links Will Be Lost'
  @FindBy(
      xpath = "//*[@id='override-description' and contains(text(), '{L10N:overrideDescription_folder}')]" )
  protected ExtendedWebElement dlgLinksWillBeLostFolder;

  @FindBy(
      xpath = "//*[@id='override-description' and contains(text(), '{L10N:overrideDescription_file}')]" )
  protected ExtendedWebElement dlgLinksWillBeLostFile;

  // Do not show message
  @FindBy( xpath = "//*[text()='{L10N:overrideCheckbox}']" )
  protected ExtendedWebElement textDoNotShowMessage;

  // Do not show message checkbox
  @FindBy( xpath = "//*[@id='do-not-show']" )
  protected ExtendedWebElement checkboxDoNotShowMessage;

  // 'Yes, Rename'
  @FindBy( xpath = "//button[text()='{L10N:overrideYesButton}']" )
  protected ExtendedWebElement btnYesRename;
  
  @FindBy( xpath = "//div[@id ='dialogOverrideFile']//button[@class='ok pentaho-button' and text()='{L10N:overrideYesButton}']" )
  protected ExtendedWebElement btnYesRenameFile;
  
  // 'No'
  @FindBy( xpath = "//button[text()='{L10N:overrideNoButton}']" )
  protected ExtendedWebElement btnNo;

  // Schedule
  @FindBy( id = "schedule-name-input" )
  protected ExtendedWebElement scheduleName;

  @FindBy( id = "generated-content-location" )
  protected ExtendedWebElement scheduleLocation;

  // 'New Schedule'
  @FindBy( xpath = "//div[@class='Caption' and contains(., '{L10N:newSchedule}')]" )
  protected ExtendedWebElement textNewSchedule;

  // 'Recurrence:'
  @FindBy( xpath = "//div[@class='schedule-label' and text()='{L10N:schedule.recurrenceColon}']" )
  protected ExtendedWebElement textScheduleReccurence;

  // 'This schedule will run using the following parameters:'
  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., '{L10N:scheduleWillRun}')]" )
  protected ExtendedWebElement textScheduleOutputType;

  // 'Would you like to email a copy when the schedule runs?'
  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., \"{L10N:wouldYouLikeToEmail}\")]" )
  protected ExtendedWebElement textScheduleEmail;

  // 'Yes' - '{L10N:Yes_txt}'
  @FindBy( xpath = "//span[@class='gwt-RadioButton']/label[contains(., 'Yes')]" )
  protected ExtendedWebElement radioYesBtn;

  // 'No' - '{L10N:No_txt}'
  @FindBy( xpath = "//span[@class='gwt-RadioButton' and contains(., 'No')]" )
  protected ExtendedWebElement radioNoBtn;

  @FindBy( xpath = "//input[@class='gwt-TextBox']" )
  protected List<ExtendedWebElement> emailSettings;

  @FindBy( xpath = "//textarea[@class='gwt-TextArea']" )
  protected ExtendedWebElement txtMessage;

  @FindBy( xpath = "//select[@class='gwt-ListBox']" )
  protected List<ExtendedWebElement> listBoxes;
  
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[text()='{L10N:containsIllegalCharacters}']" )
  protected ExtendedWebElement illegalCharactersMessage;

  @FindBy( xpath = "//div[@class='pentaho-dialog modal in']//div[@class='header-content' and text()='{L10N:cannotRenameDialogTitle}']" )
  protected ExtendedWebElement cannotRenameMessage;

  @FindBy( xpath = "//div[@class='pentaho-dialog modal in']//button[@class='ok pentaho-button']" )
  protected ExtendedWebElement closeButton;

  public BrowseFilesPageEx( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "BrowseFilesPageEx is not opened!" );
    }
  }

  public boolean isOpened() {
    switchToFrame( browserFrame );
    boolean res = isOpened( textFolders );
    switchToDefault();
    return res;
  }
  
  public void activateFile( String fileName, boolean multiSelection ) {
    switchToFrame( browserFrame );

    ExtendedWebElement fileElement = format( fileItem, fileName );

    if ( multiSelection ) {
      Actions builder = new Actions( driver );
      builder.keyDown( Keys.CONTROL );
      builder.click( fileElement.getElement() );
      builder.keyUp( Keys.CONTROL );
      builder.perform();
    } else {
      // [MG] changed click() to JavascriptExecutor as click() was not working for dashboard files.
      if ( fileElement.isElementPresent( EXPLICIT_TIMEOUT / 2 ) ) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript( "arguments[0].click();", fileElement.getElement() );
      }
    }

    // Verification part
    String fileActions = L10N.getText( "fileActions" );
    Assert.assertTrue( actionsTitle.getText().equals( fileActions ), "File Actions is not recognized! It looks like '"
        + fileName + "' file was not activated!" );

    switchToDefault();
  }

  public void activateFile( String fileName ) {
    activateFile( fileName, false );
  }

  public void expandFolder( String path ) {
    switchToFrame( browserFrame );
    if ( !isFolderExpanded( path ) ) {
      click( format( folderItemExpanded, path )/* , true */ );
    }
    switchToDefault();
  }

  public void activateFolder( String path ) {
    switchToFrame( browserFrame );
    // [MG] changed click() to JavascriptExecutor as click() was not working for dashboard files.
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript( "arguments[0].click();", format( folderItem, path, EXPLICIT_TIMEOUT / 15 ).getElement() );
    pause(2);
    switchToDefault();
  }

  public String getUserHome() {
    return HOME + "/" + getUserName();
  }

  public void openSchedule( String reportPath, String name ) {
    String[] items = reportPath.split( "/" );
    String filename = items[items.length - 1];

    switchToFrame( browserFrame );
    click( lnkSchedule );

    switchToDefault();

    if ( !isElementPresent( textNewSchedule ) ) {
      Assert.fail( "TS041998: New Schedule window is not opened!" );
    }

    if ( Configuration.get( Parameter.LOCALE ).contains( "en" ) ) {
      // do not verify on localized env as name in this case hasn't locale prefix like: Sales Trend (multi-chart) [DE]
      Assert.assertTrue( scheduleName.getAttribute( HTML.VALUE ).equals( filename ),
          "TS041998: Schedule Name default name is not populated correctly!" );
    }
    Assert.assertTrue( scheduleLocation.getAttribute( HTML.VALUE ).equals( "/home/admin" ),
        "TS041998: Generated Content Location doesn't show '/home/admin'!" );
    type( scheduleName, name );
  }

  public void setScheduleProperties() {
    Assert.assertTrue( isElementPresent( textScheduleReccurence ),
        "TS041999: Recurrence schedule dialog is not opened!" );

    LOGGER.warn(
        "TODO: Current schedule implementation doesn't use any input parameters and schedule using default 'Run Once' recurrence. Later it should be refactored!" );

    /*
     * ExtendedWebElement selectScheduleReccurence = findSelectByValue(listBoxes, "Run Once");
     * select(selectScheduleReccurence, "Run Once");
     * 
     * DateTime dt = new DateTime(); // current time dt.plusMinutes(2);
     * 
     * int hours = dt.getHourOfDay(); // gets hour of day int minutes = dt.getMinuteOfHour(); // gets hour of day
     * 
     * ExtendedWebElement selectScheduleHour = findSelectByValue(listBoxes, "01");
     */
  }

  public void setScheduleOutput( String output ) {
    Assert.assertTrue( isElementPresent( textScheduleOutputType ),
        "TS042000: Schedule Output format page is not opened!" );

    driver.switchTo().frame( schedulerParamsFrame.getElement() );
    // click(output, driver.findElement(By.xpath(String.format(
    // SCHEDULE_FORMAT_OUTPUT, output))));
    click( format( scheduleFormatItem, output ) );
    switchToDefault();
  }

  public void setScheduleEmail() {
    click( radioNoBtn );
  }

  public void setScheduleEmail( String emailTo, String subject, String attachName, String message ) {
    Assert.assertTrue( isElementPresent( textScheduleEmail ),
        "TS042176: 'Would you like to email a copy when the schedule runs?' is not opened!" );
    click( radioYesBtn );

    try {

      type( emailSettings.get( 0 ), emailTo );
      type( emailSettings.get( 1 ), subject );
      type( emailSettings.get( 2 ), attachName );
      type( txtMessage, message );
    } catch ( Exception e ) {
      Assert.fail( "TS042175: " + e.getMessage(), e.getCause() );
    }
  }

  /*
   * private ExtendedWebElement findSelectByValue(List<ExtendedWebElement> elements, String value) { ExtendedWebElement
   * listBox = null;
   * 
   * for (ExtendedWebElement element : elements) { if (isElementPresent(element, 1) &&
   * element.getElement().isDisplayed()) { if (getSelectedValue(element).equals(value)) { listBox = element; break; } }
   * } return listBox; }
   */

  public void createFolder( Folder folder ) {
    String folderName = folder.getName();
    String path = getSelectedFolderPath() + SEPARATOR + folderName;

    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkNewFolder ) && !lnkNewFolder.getElement().isEnabled() ) {
      CustomAssert.fail( "42012: New Folder button is not present or disabled!" );
    }
    click( lnkNewFolder );

    switchToDefault();

    // Verification part
    if ( !verifyNewFolderDialog() ) {
      CustomAssert.fail( "42012", "New Folder dialog is not opened!" );
      CustomAssert.fail( "64334",
          "New Folder dialog is not opened or the OK and Cancel buttons are not present in dialog" );
    }
    // Verification part
    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( btnOK.getElement().isEnabled(), "TS042012: Button OK is disabled on New Folder dialog" );
    softAssert.assertTrue( btnCancel.getElement().isEnabled(),
        "TS042012: Button Cancel is disabled on New Folder dialog" );

    type( inputNewFolderName, folderName );
    click( btnOK );

    // Verification part for SpiraTest Step Id 64437
    if ( !verifyCreateFolderPermissionDeniedDialogBox() ) {
      CustomAssert.fail( "64437",
          "The permission denied dialog box failed to appear with any content or the content was not matching the expected localized result!" );
    } else {
      LOGGER.info(
          "INFO: The folder was NOT created. Check if the user trying to create the new folder has the right permissions for content creation." );
      click( btnOK );
    }

    switchToFrame( browserFrame );

    // Verification part
    if ( !isElementPresent( format( folderItem, path ), 5 ) ) {
      CustomAssert.fail( "42178", "Folder " + folderName + " was not created" );
      CustomAssert.fail( "64335", "Folder " + folderName + " was not created" );
    }

    softAssert.assertAll();

    switchToDefault();
  }
  
  public void createFolderWithoutVerification( Folder folder ) {
    String folderName = folder.getName();
    switchToFrame( browserFrame );
    click( lnkNewFolder );
    switchToDefault();
    type( inputNewFolderName, folderName );
    click( btnOK );
  }

  public void open() {
    switchToFrame( browserFrame );
    // [MG] changed click() to JavascriptExecutor as click() was not working for dashboard files.
    JavascriptExecutor executor = (JavascriptExecutor) driver;
    executor.executeScript( "arguments[0].click();", lnkOpen.getElement() );

    loading();
    if ( isAlertPresent() ) {
      acceptAlert();
    }
    switchToDefault();
    PentahoUser.incOpenedTab();
  }

  public void copy() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkCopy ) && !lnkCopy.getElement().isEnabled() ) {
      Assert.fail( "Copy button is not present or disabled!" );
    }
    click( lnkCopy );

    // [BenF] - This pause helps ensure the Copy button has some time to actually disable while running in Jenkins.
    pause( 1 );

    // Verification part
    if ( lnkCopy.getElement().isEnabled() ) {
      Assert.fail( "Copy action was not performed!" );
    }

    switchToDefault();
  }

  public void cut() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkCut ) && !lnkCut.getElement().isEnabled() ) {
      Assert.fail( "Cut button is not present or disabled!" );
    }
    click( lnkCut );

    // Verification part
    if ( lnkCut.getElement().isEnabled() ) {
      Assert.fail( "Cut action was not performed!" );
    }

    switchToDefault();
  }

  public void paste() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkPaste ) && !lnkPaste.getElement().isEnabled() ) {
      Assert.fail( "Paste button is not present or disabled, check if you've copied any files!" );
    }
    click( lnkPaste );

    switchToDefault();

    // Verification part for SpiraTest Step Id 64436
    if ( !verifyCopyPasteDeniedDialogBox() ) {
      CustomAssert.fail( "64436",
          "The copy and paste denied dialog box failed to appear with any content or the content was not matching the localized result!" );
    } else {
      LOGGER.info(
          "INFO: The file or folder was NOT able to be pasted into the desired area. Check if the user trying to paste the content has the right permissions to do so." );
      click( btnOK );
    }

  }

  // Return true if folder is expanded, otherwise false
  protected boolean isFolderExpanded( String path ) {
    List<String> classItems = getFolderClass( path );
    return classItems.contains( "open" );
  }

  // Return true if folder is empty, otherwise false
  protected boolean isFolderHasChildren( String path ) {
    List<String> classItems = getFolderClass( path );
    return classItems.contains( "empty" );
  }

  // Return true if folder is selected, otherwise false
  protected boolean isFolderSelected( String path ) {
    List<String> classItems = getFolderClass( path );
    return classItems.contains( "selected" );
  }

  // Returns value of 'path' parameter for selected folder
  protected String getSelectedFolderPath() {
    switchToFrame( browserFrame );
    String path = selectedFolder.getAttribute( "path" );
    switchToDefault();
    return path;
  }

  private List<String> getFolderClass( String path ) {
    // Get class values
    String folderClass = format( treeItem, path ).getAttribute( "class" );
    if ( folderClass == null ) {
      throw new RuntimeException( "Unable to find folder '" + path + "' inside tree view!" );
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
    String folderClass = format( fileItem, fileName ).getAttribute( "class" );
    return new ArrayList<String>( Arrays.asList( folderClass.split( " " ) ) );
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

  // Return true if folder exists in current folder tree
  public boolean isFolderPresent( String path ) {
    switchToFrame( browserFrame );

    boolean res = false;
    if ( isElementPresent( format( folderItem, path ) ) ) {
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

  public void moveFileToTrash() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkMoveFileToTrash ) && !lnkMoveFileToTrash.getElement().isEnabled() ) {
      Assert.fail( "Move To Trash button is not present or disabled!" );
    }
    click( lnkMoveFileToTrash );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgMoveToTrash ) ) {
      Assert.fail( "Move to Trash dialog is not opened!" );
    }
    click( btnOK );
    pause(2);
  }
  
  public void moveFolderToTrash() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkMoveFolderToTrash ) && !lnkMoveFolderToTrash.getElement().isEnabled() ) {
      Assert.fail( "Move To Trash button is not present or disabled!" );
    }
    click( lnkMoveFolderToTrash );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgMoveToTrash ) ) {
      Assert.fail( "Move to Trash dialog is not opened!" );
    }
    click( btnOK );
  }

  // Return true if folder is empty, otherwise false
  public boolean isRightPaneEmpty() {

    return true;
  }

  public void restore() {
    switchToFrame( browserFrame );

    // Check that the Restore button is present and enabled
    if ( !isElementPresent( lnkRestore ) && !lnkRestore.getElement().isEnabled() ) {
      Assert.fail( "'Restore' button is not present or disabled!" );
    }
    click( lnkRestore );

    switchToDefault();
  }

  public void emptyTheTrash() {
    emptyTheTrash( true );
  }

  public boolean isEmptyTheTrashEnabled() {
    switchToFrame( browserFrame );
    boolean res = false;
    if ( isElementPresent( lnkEmptyTheTrash ) && lnkEmptyTheTrash.getElement().isEnabled() ) {
      res = true;
    }
    switchToDefault();
    return res;
  }

  public void emptyTheTrash( boolean confirm ) {
    switchToFrame( browserFrame );

    // Check that 'Empty The Trash' button is present and enabled
    if ( !isEmptyTheTrashEnabled() ) {
      Assert.fail( "'Empty the Trash' button is not present or disabled!" );
    }
    click( lnkEmptyTheTrash );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgEmptyTrash, 3 ) ) {
      LOGGER.error( "'Empty Trash' dialog is not appeared!" );
    }

    if ( confirm ) {
      // click 'Yes, Empty Trash'
      click( btnYesEmptyTrash );
    } else {
      // click No
      click( btnCancel );
    }

    // Verification part
    if ( !isElementPresent( dlgEmptyTrash, 3 ) ) {
      LOGGER.error( "Empty Trash dialog is not closed!" );
    }
  }

  public void permanentlyDelete() {
    permanentlyDelete( true );
  }

  public void permanentlyDelete( boolean confirm ) {
    switchToFrame( browserFrame );

    // Check that the Restore button is present and enabled
    if ( !isElementPresent( lnkPermanentlyDelete ) && !lnkPermanentlyDelete.getElement().isEnabled() ) {
      Assert.fail( "'Permanently Delete' button is not present or disabled!" );
    }
    click( lnkPermanentlyDelete );

    switchToDefault();

    // Verification part
    if ( !isElementPresent( dlgPermanentlyDelete, 3 ) ) {
      LOGGER.error( "'Permanently Delete' dialog is not appeared!" );
    }

    if ( confirm ) {
      // click 'Yes, Permanently Delete'
      click( btnPermanentlyDelete );
    } else {
      // click No
      click( btnCancel );
    }

    // Verification part
    if ( isElementPresent( dlgPermanentlyDelete, 3 ) ) {
      LOGGER.error( "'Permanently Delete' dialog is not closed!" );
    }
  }

  public void downloadFolder() {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkDownload ) && !lnkDownload.getElement().isEnabled() ) {
      Assert.fail( "Download button is not present or disabled!" );
    }
    click( lnkDownload );

    switchToDefault();
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
    if ( (isElementPresent( dlgLinksWillBeLostFolder )||isElementPresent( dlgLinksWillBeLostFile )) && isElementPresent( textDoNotShowMessage ) && ( isElementPresent(
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

  public void rename( String newName, boolean overwrite, boolean confirm ) {
    switchToFrame( browserFrame );

    if ( !isElementPresent( lnkRename ) || !isFileActionEnabled( lnkRename ) ) {
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
      type( inputRename, newName );

      // Verification part
      if ( !btnRenameOK.getElement().isEnabled() ) {
        CustomAssert.fail( "42014", "Button OK was not enabled in Rename dialog after entering new folder name!" );
        CustomAssert.fail( "40877", "OK button is disabled after entering new folder name" );
      }

      if ( confirm ) {
        // click OK and verify folder was renamed
        click( btnRenameOK );

        // Verification part
        if ( renameErrorDialog.isElementPresent() ) {
          Assert.fail( "Rename error dialog is present!" );
        }

        if ( isElementPresent( dlgRename, 3 ) ) {
          CustomAssert.fail( "42014", "Rename dialog is not closed!" );
        }
      } else {
        // click Cancel and verify folder was NOT renamed
        click( btnRenameCancel );
        if ( isElementPresent( dlgRename, 3 ) ) {
          CustomAssert.fail( "40877", "Rename dialog is still present!" );
        }
      }

    } else {
      click( btnNo );
    }

    switchToDefault();
  }
  
  public void renameWithoutVerification( String newName ) {
    switchToFrame( browserFrame );
    click( lnkRename );
    switchToDefault();

    if ( isElementPresent( dlgLinksWillBeLostFolder ) ) {
      click( btnYesRename );
      type( inputRename, newName );
      click( btnRenameOK );
    } else {
      if ( isElementPresent( dlgLinksWillBeLostFile ) ) {
        click( btnYesRenameFile );
        type( inputRename, newName );
        click( btnRenameOK );
      }
    }
  }
  
  public boolean verifyIcon( File fileObject ) {
    // Get class values
    switchToFrame( browserFrame );
    String iconClassValue = format( fileIconItem, fileObject.getName() ).getAttribute( "class" );
    List<String> iconClassList = Arrays.asList( iconClassValue.split( " " ) );
    String icon = iconClassList.get( iconClassList.size() - 1 );
    switchToDefault();

    boolean res = false;
    switch ( icon ) {
      case "xanalyzer":
        if ( fileObject instanceof PAReport ) {
          res = true;
        }
        break;
      case "prpti":
        if ( fileObject instanceof PIRReport ) {
          res = true;
        }
        break;
      case "prpt":
        // TODO: no PRPTReport object so far
        if ( fileObject instanceof File ) {
          res = true;
        }
        break;
      case "xdash":
        // TODO: no Dashboard object so far
        if ( fileObject instanceof File ) {
          res = true;
        }
        break;
      default:
        LOGGER.error( "'" + fileObject.getName() + "' contains icon of unknown type!" );
        break;
    }
    return res;
  }

  public boolean isHorizontalScrollBarPresentOnFilesPane() {
    switchToFrame( browserFrame );
    boolean res =
        (boolean) ( (JavascriptExecutor) driver ).executeScript(
            "return arguments[0].scrollWidth>arguments[0].clientWidth;", filesListElement.getElement() );
    switchToDefault();
    return res;
  }

  public void clickOkButon() {
    click( btnOK );
  }

  public void clickCloseButton() {
    click( closeButton );
  }
  
  public void clickRenameCancelButton() {
    click( btnRenameCancel );
  }
  
  public boolean verifyIllegalCharactersMessage() {
    return isElementPresent( illegalCharactersMessage );
  }

  public boolean verifyCannotRenameMessage() {
    return isElementPresent( cannotRenameMessage );
  }

  /**
   * Check if the copy and paste denied dialog box is present on screen based on its L10N text.
   * 
   * @return Returns true or false based on if the element is actually there.
   */
  public boolean verifyCopyPasteDeniedDialogBox() {
    return isElementPresent( format( dlgErrorBox, L10N.generateConcatForXPath( L10N.getText( "pasteFilesCommand.accessDenied" ) ) ) );
  }
    
  /**
   * Check if the create folder permissions denied dialog box is present on screen based on its L10N text.
   * 
   * @return Returns true or false based on if the element is actually there.
   */
  public boolean verifyCreateFolderPermissionDeniedDialogBox() {
    return isElementPresent ( format( dlgErrorBox, L10N.generateConcatForXPath( L10N.getText( "couldNotCreateFolder" ) ) ) );
  }
  
  public boolean isFileActionEnabled( ExtendedWebElement fileActionElement ) {
    boolean result = true;
    String attribute = "disabled";

    String value = fileActionElement.getElement().getAttribute( attribute );
    if ( value != null ) {
      result = false;
    }
    return result;
  }
}
