package com.pentaho.services.puc.browse;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.FilePage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.ObjectPool;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class File extends BrowseObject {
  public static final String ARG_TYPE = "type";
  public static final String ARG_ALLOW_SCHEDULING = "allowScheduling";

  protected Type type;
  protected Boolean allowScheduling;

  protected CopyAction copyAction;

  // internal page object
  private FilePage filePage;

  public enum CopyAction {
    COPY, CUT;
  }

  public enum Type {
    PRPTI( ".prpti" ), PRPT( ".prpt" ), XANALYZER( ".xanalyzer" ), XDASH( ".xdash" );

    private String name;

    private Type( String type ) {
      this.name = type;
    }

    public String getName() {
      return this.name;
    }

    public static Type fromString( String type ) {
      if ( type != null ) {
        for ( Type t : Type.values() ) {
          if ( type.equalsIgnoreCase( t.getName() ) ) {
            return t;
          }
        }
      }
      return null;
    }
  }

  public File( Map<String, String> args ) {
    this( args.get( ARG_NAME ), Boolean.valueOf( args.get( ARG_HIDDEN ) ), Type.fromString( args.get( ARG_TYPE ) ),
        Boolean.valueOf( args.get( ARG_ALLOW_SCHEDULING ) ), args.get( ARG_ID ) );
  }

  public File( String name, Boolean hidden, Type type, Boolean allowScheduling, String id ) {
    super( L10N.getText( name ), hidden, id );
    this.type = type != null ? type : null;
    this.allowScheduling = allowScheduling != null ? allowScheduling : null;
    this.copyAction = null;
  }

  public File( String name, Boolean hidden, Type type, Boolean allowScheduling ) {
    this( name, hidden, type, allowScheduling, String.valueOf( INVALID_ID ) );
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public Type getType() {
    return type;
  }

  public void setType( Type type ) {
    this.type = type;
  }

  public Boolean getAllowScheduling() {
    return allowScheduling;
  }

  public void setAllowScheduling( Boolean allowScheduling ) {
    this.allowScheduling = allowScheduling;
  }

  public void setPage( FilePage page ) {
    this.filePage = page;
  }

  public FilePage getPage() {
    return this.filePage;
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  /**
   * Opens an existing file from the Browse Files perspective
   * 
   * @return returns the page for that object so it can be interacted with.
   */
  public FilePage open() {
    if ( !isValid() ) {
      Assert.fail( "Unable to open invalid report: " + getName() );
    }

    if ( getParent() == BrowseService.getTrash() ) {
      Assert.fail( "Unable to open file: '" + getName() + "' from Trash!" );
    }

    HomePage homePage = new HomePage( getDriver() );
    homePage.activateModuleEx( Module.BROWSE_FILES );

    select();

    BrowseService.getBrowseFilesPageEx().open();

    // verification path
    // TODO: implement verification that item is opened
    setPage( new FilePage( getDriver(), getName() ) );
    return filePage;
  }

  /**
   * Saves an existing file that has been modified. No pop-up is expected. This will fail if the file has never been
   * saved before.
   * 
   * @return true if the file was successfully saved, and false if the save operation failed.
   */
  public boolean save() {
    if ( !isValid() ) {
      Assert.fail(
          "First time save operation MUST be executed with Folder parameter! Current report was not saved yet! "
              + getName() );
    }
    return getPage().clickSave();
  }

  /**
   * Saves a new file to the specified folder. If the file has already been saved once, perform a save(). Override is
   * not handled in this method.
   * 
   * @param folder
   *          The folder to save the file to.
   */
  public void save( Folder folder ) {
    if ( !isValid() ) {
      save( folder, false );
    } else {
      save();
    }

    // TODO: object is valid, need to sync with snapshot
    ObjectPool.removeSnapshot( this.getId() );
  }

  /**
   * Saves a new file to the specified folder. If the file has already been saved once, perform a save()
   * 
   * @param folder
   *          The folder to save the file to.
   * @param overwrite
   *          overwrite an existing file with the same name with this new file being saved.
   */
  public File save( Folder folder, boolean overwrite ) {
    getPage().clickSave( this.getName(), folder.getPath(), overwrite );
    // Make object valid and add it to browseTree
    setId( ObjectPool.getUniqueId() );
    return addToBrowseTreeAndObjectPool( this, folder );
  }

  /**
   * This method performs a Save As for the current file opened.
   * 
   * @param folder
   *          The folder you want to save the file to.
   * @return An object for the file that was just created.
   */
  public File saveAs( Folder folder ) {
    return saveAs( folder, false );
  }

  /**
   * This method performs a Save As for the current file opened.
   * 
   * @param folder
   *          The folder you want to save the file to.
   * @param overwrite
   *          This flag is used to determine if we are looking to overwrite an existing file.
   * @return An object for the file that was just created.
   */
  public File saveAs( Folder folder, Boolean overwrite ) {
    String location = BrowseService.getPath( folder );
    getPage().clickSaveAs( this.getName(), location, overwrite );

    // Make object valid if this is the first time saving it.
    if ( !isValid() ) {
      setId( ObjectPool.getUniqueId() );
      return addToBrowseTreeAndObjectPool( this, folder );
    } else {
      // If file has been saved before, clone it and add it to the browse service.
      File temp = (File) this.clone();
      return addToBrowseTreeAndObjectPool( temp, folder );
    }
  }

  /**
   * Adds file to the browse tree and handles the ObjectPool snapshot.
   * 
   * @param file
   *          The file to add to the BrowseService and ObjectPool.
   * @param folder
   *          The location of the file.
   * @return The file after being added to the browse tree.
   */
  private File addToBrowseTreeAndObjectPool( File file, Folder folder ) {
    BrowseService.addItem( file, folder );
    File snap = ( ObjectPool.getSnapshot( getId() ) == null ? this : (File) ObjectPool.getSnapshot( getId() ) );
    this.copy( snap );
    return file;
  }

  /**
   * Close the file and don't save if file has been modified
   */
  public void close() {
    close( false, null );
  }

  /**
   * Close the file and save based on flag. This only applies to files that have been already saved once.
   * 
   * @param save
   *          If changes have been made to the file, perform a save, then close.
   */
  public void close( boolean save ) {
    close( save, null );
  }

  /**
   * Close and save the file to the folder specified.
   * 
   * @param folder
   *          The folder you want to save the file to.
   */
  public void close( Folder folder ) {
    close( true, folder );
  }

  /**
   * Closes and saves the file to the folder specified.
   * 
   * @param save
   *          If changes have been made to the file, perform a save, then close.
   * @param folder
   *          The folder you want to save the file to.
   */
  public void close( boolean save, Folder folder ) {
    FilePage filePage = getPage();
    // close the file based on the name it was saved with.
    if ( isValid() ) {
      filePage.clickClose( getName() );
    } else {
      // if the file has never been saved, close it by the default name.
      filePage.clickClose( getPage().getName() );
    }

    if ( filePage.isUnsavedContentDialogPresent() ) {
      if ( save ) {
        // dismiss dialog to with proceed with the save
        filePage.clickCancel();
        // perform a save
        if ( isValid() ) {
          save();
        } else {
          save( folder );
        }
        // now close the file
        filePage.clickClose( this.getName() );
        // TODO: remove from snapshots anyway
      } else {
        filePage.clickOK();
        // revert to original state from snapshot and remove it from snapshots
      }
    }
  }

  @Override
  public void select() {
    super.select();
    BrowseService.getBrowseFilesPageEx().activateFile( getName() );
  }

  @Override
  public boolean isExist() {
    super.isExist();
    return BrowseService.getBrowseFilesPageEx().isFilePresent( getName() );
  }

  /* -------------- COPY OBJECT -------------------------------- */

  public void copy() {
    if ( !isValid() || ( getParent() == BrowseService.getTrash() ) ) {
      Assert.fail( "File '" + getName() + "' is not valid or it's in Trash, it can't be copied!" );
    }

    select();
    BrowseService.getBrowseFilesPageEx().copy();

    // Save the type of file copy and put the object to buffer
    copyAction = CopyAction.COPY;
    BrowseService.clearBuffer();
    BrowseService.setBuffer( this );

    // Verification is performed on Page level
  }

  /* -------------- CUT OBJECT -------------------------------- */

  public void cut() {
    if ( !isValid() || ( getParent() == BrowseService.getTrash() ) ) {
      Assert.fail( "File '" + getName() + "' is not valid or it's in Trash, it can't be cut!" );
    }

    select();
    BrowseService.getBrowseFilesPageEx().cut();

    // Save the type of file copy and put the object to buffer
    copyAction = CopyAction.CUT;
    BrowseService.clearBuffer();
    BrowseService.setBuffer( this );

    // Verification is performed on Page level
  }

  /* -------------- PASTE OBJECT -------------------------------- */

  public File paste( Folder folder ) {
    if ( !isValid() || ( getParent() == BrowseService.getTrash() ) ) {
      Assert.fail( "File '" + getName() + "' is not valid or it's in Trash, it can't be pasted!" );
    }

    if ( !folder.isValid() || ( folder.getParent() == BrowseService.getTrash() ) ) {
      Assert.fail( "Folder '" + getName() + "' is not valid or it's in Trash, it can't be used for pasting file!" );
    }

    // Verification that current object is present in buffer
    if ( BrowseService.getBuffer() != this ) {
      Assert.fail( "File '" + this.getName() + "' is not present in buffer!" );
    }

    // UI actions
    folder.select();
    BrowseService.getBrowseFilesPageEx().paste();

    // Clone existing file
    File pastedFile = null;
    pastedFile = (File) this.clone();

    // Logical changes in browseTree
    switch ( copyAction ) {
      case COPY:
        // Make new file valid and add to browseTree
        pastedFile.setId( ObjectPool.getUniqueId() );
        BrowseService.addItem( pastedFile, folder );
        break;
      case CUT:
        BrowseService.moveItem( this, folder );
        pastedFile = this;
        break;
      default:
        Assert.fail( "File '" + this.getName() + "' was not pasted to '" + folder.getName()
            + "' folder, looks like it was not copy/cut previously!" );
        break;
    }
    return pastedFile;
  }
  
  @Override
  protected Object clone() {
    File cloned;
    try { 
    cloned = (File) super.clone();
    return cloned;
    } catch (CloneNotSupportedException e) {
      LOGGER.error( "Unable to clone file!" );
    }
    throw new RuntimeException( "Unable to clone file!" );
  }
  
  /**
   * Activates edit mode for the file by clicking the edit content button.
   */
  public void activateEditMode() {
    FilePage page = getPage();

    if ( !page.isEditFilePressed() ) {
      page.clickEditContentButton();
    } else {
      LOGGER.info( "The file is already in edit mode. The edit content button will not be pressed." );
    }
  }

  /**
   * Activates view mode for the file by clicking the view content button.
   */
  public void activateViewMode() {
    FilePage page = getPage();

    if ( page.isEditFilePressed() ) {
      page.clickEditContentButton();
    } else {
      LOGGER.info( "The file is already in view mode. The edit content button will not be pressed." );
    }
  }
}
