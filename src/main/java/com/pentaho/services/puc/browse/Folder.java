package com.pentaho.services.puc.browse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.pentaho.services.ObjectPool;

public class Folder extends BrowseObject {

  public Folder( Map<String, String> args ) {
    super( args );
  }

  public Folder( String name, Boolean hidden ) {
    super( name, hidden);
  }

  public Folder( String name, Boolean hidden, String id ) {
    super( name, hidden, id );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */

  public void copy( Folder folder ) {
    super.copy( folder );
    // TODO: specific folder parameters should be set here
  }

  /* -------------- CREATE FOLDER ------------------------------ */

  public void add( Folder parent ) {
    if ( isValid() ) {
      Assert.fail( "Folder '" + getName() + "' was already created!" );
    }
    if ( !parent.isValid() ) {
      Assert.fail( "Parent folder '" + parent.getName() + "' is not present, it's invalid!" );
    }
    parent.select();
    BrowseService.getBrowseFilesPageEx().createFolder( this );

    // Make folder valid and add to browseTree
    setId( ObjectPool.getUniqueId() );
    BrowseService.addItem( this, parent );

    // Verification
    isExist();
  }

  /* -------------- SELECT FOLDER ------------------------------ */

  @Override
  public void select() {
    super.select();
    if ( getParent() != BrowseService.getTrash() ) {
      // left selection
      BrowseService.getBrowseFilesPageEx().activateFolder( BrowseService.getPath( this ) );
    } else {
      // right selection
      BrowseService.getBrowseFilesPageEx().activateFile( getName() );
    }
  }

  /* -------------- EXPAND FOLDER ------------------------------ */

  public void expand() {
    BrowseService.getBrowseFilesPageEx().expandFolder( BrowseService.getPath( this ) );
  }

  /* -------------- VERIFY FOLDER ------------------------------ */

  @Override
  public boolean isExist() {
    super.isExist();

    if ( getParent() != BrowseService.getTrash() ) {
      // left selection
      getParent().expand();
      return BrowseService.getBrowseFilesPageEx().isFolderPresent( BrowseService.getPath( this ) );
    } else {
      // right selection
      return BrowseService.getBrowseFilesPageEx().isFilePresent( getName() );
    }
  }

  /* -------------- GET CHILDREN METHODS ---------------------------- */

  public List<File> getChildrenFiles() {
    List<File> files = new ArrayList<File>();
    for (BrowseObject object : BrowseService.getChildren( this )) {
      if (object instanceof File) {
        files.add( (File) object );
      }
    }
    return files;
  }
  
  public List<Folder> getChildrenFolders() {
    List<Folder> folders = new ArrayList<Folder>();
    for (BrowseObject object : BrowseService.getChildren( this )) {
      if (object instanceof Folder) {
        folders.add( (Folder) object );
      }
    }
    return folders;
  }
  
}
