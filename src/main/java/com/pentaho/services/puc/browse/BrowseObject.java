package com.pentaho.services.puc.browse;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.services.BaseObject;
import com.pentaho.services.CustomAssert;

public class BrowseObject extends BaseObject {

  public static final String ARG_HIDDEN = "hidden";

  protected Boolean hidden;

  public BrowseObject( String name, Boolean hidden, String id ) {
    super( name, id );
    this.hidden = hidden != null ? hidden : null;

  }

  public BrowseObject( Map<String, String> args ) {
    this( args.get( ARG_NAME ), args.get( ARG_HIDDEN ) != null ? Boolean.valueOf( args.get( ARG_HIDDEN ) ) : null, args
        .get( ARG_ID ) );
  }

  public BrowseObject( String name, Boolean hidden ) {
    this( name, hidden, String.valueOf( INVALID_ID ) );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */

  public void copy( Folder folder ) {
    super.copy( folder );
    if ( folder.getHidden() != null ) {
      this.hidden = folder.getHidden();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public Boolean getHidden() {
    return hidden;
  }

  public void setHidden( Boolean hidden ) {
    this.hidden = hidden;
  }

  /* ------------------- GET PATH ------------------------------ */

  public String getPath() {
    if ( !isValid() ) {
      Assert.fail( "Path can't be returned for invalid browse object '" + getName() + "'!" );
    }
    return BrowseService.getPath( this );
  }

  public Folder getParent() {
    return (Folder) BrowseService.getParent( this );
  }

  /* -------------- SELECT OBJECT ------------------------------ */

  public void select() {
    if ( !isValid() ) {
      Assert.fail( "BrowseObject '" + getName() + "' is not valid, so it can't be selected!" );
    }

    if ( getParent() != null ) {
      getParent().select();
      getParent().expand();
    }
  }

  /* -------------- REMOVE OBJECT ------------------------------ */

  public void remove() {
    if ( !isValid() ) {
      Assert.fail( "BrowseObject '" + getName() + "' is not valid!" );
    }

    if ( getParent() == BrowseService.getTrash() ) {
      Assert.fail( "BrowseObject '" + getName() + "' can't be removed because it's already in Trash!" );
    }

    select();

    if ( this instanceof File ) {
      BrowseService.getBrowseFilesPageEx().moveFileToTrash();
    } else if ( this instanceof Folder ) {
      BrowseService.getBrowseFilesPageEx().moveFolderToTrash();
    }

    // Move element in browseTree
    BrowseService.moveItem( this, BrowseService.getTrash() );
    // Make children invalid
    BrowseService.removeChildren( this );

    // Verification
    if ( !isExist() ) {
      Assert.fail( getName() + " was not removed!" );
    }
  }

  /* -------------- DELETE OBJECT PERMANENTLY ------------------------------ */

  public void deletePermanently() {
    if ( !isValid() ) {
      Assert.fail( "BrowseObject '" + getName() + "' is not valid!" );
    }

    if ( getParent() != BrowseService.getTrash() ) {
      Assert.fail( "BrowseObject '" + getName() + "' can't be permanently deleted because it's not in Trash!" );
    }

    select();

    BrowseService.getBrowseFilesPageEx().permanentlyDelete();

    // Delete item from browseTree and make it removed (invalid)
    BrowseService.deleteItem( this );

    // Verification
    if ( BrowseService.getBrowseFilesPageEx().isFilePresent( getName() ) ) {
      Assert.fail( getName() + " was not permanently deleted!" );
    }
  }

  /* -------------- RENAME OBJECT ------------------------------ */

  public void rename( String newName, boolean overwrite, boolean confirm ) {
    if ( !isValid() ) {
      Assert.fail( "Item '" + getName() + "' is not valid!" );
    }
    select();

    BrowseService.getBrowseFilesPageEx().rename( newName, overwrite, confirm );

    // TODO: analyze cases when name should not be updated!! For example when confirm == false
    if ( confirm ) {
      setName( newName );
    }

    // Verification part
    if ( !isExist() ) {
      CustomAssert.fail( "42014", "Item " + getName() + " was not renamed to " + newName );
      CustomAssert.fail( "40878", "Item " + getName() + " was not renamed to " + newName );
      Assert.fail( getName() + " was not renamed!" );
    }
  }

  /* -------------- RESTORE OBJECT ------------------------------ */

  public void restore() {
    if ( !isValid() ) {
      Assert.fail( "BrowseObject '" + getName() + "' is not valid!" );
    }

    // Only object from Trash can be restored
    if ( getParent() != BrowseService.getTrash() ) {
      Assert.fail( "BrowseObject '" + getName() + "' can't be restored because it's NOT in Trash!" );
    }

    select();
    BrowseService.getBrowseFilesPageEx().restore();

    // Move element in browseTree
    BrowseService.restoreItem( this );

    // Mark children valid
    BrowseService.restoreChildren( this );

    // Verification
    if ( !isExist() ) {
      Assert.fail( getName() + " was not restored!" );
    }
  }

  /* --------------- VERIFY OBJECT ------------------------------ */

  public boolean isExist() {
    if ( !isValid() ) {
      Assert.fail( "BrowseObject '" + getName() + "' is not valid, so it can't be verified!" );
    }
    getParent().select();

    return true;
  }

}
