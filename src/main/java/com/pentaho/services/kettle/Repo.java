package com.pentaho.services.kettle;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.RepositoryPluginType;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.RepositoriesMeta;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryImporter;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.RepositoryObjectType;
import org.w3c.dom.Node;

import com.pentaho.services.PentahoUser;

public class Repo {

  protected static final Logger LOGGER = Logger.getLogger( Repo.class );
  private Repository repository;
  private static PluginRegistry registry;
  private RepositoriesMeta repositoriesMeta;
  public static final String XML_TAG = "repository";
  public Boolean isConnected = false;

  public Repo() {
    // use the plug-in system to get the correct repository implementation
    // the actual implementation will vary depending on the type of
    // given repository (File-based, DB-based, EE, etc.)
    registry = PluginRegistry.getInstance();

    // read the repositories.xml file to determine available repositories
    repositoriesMeta = new RepositoriesMeta();
    try {
      repositoriesMeta.readData();
    } catch ( KettleException e ) {
      LOGGER.error( "Reading repositories.xml has failed", e );
    }
  }

  public Repository getRepository() {
    return repository;
  }

  public void setRepository( Repository repository ) {
    this.repository = repository;
  }

  public Boolean getIsConnected() {
    return isConnected;
  }

  public void setIsConnected() {
    setIsConnected( repository.isConnected() );
  }

  public void setIsConnected( Boolean isConnected ) {
    this.isConnected = isConnected;
  }

  public RepositoryMeta findRepository( String repositoryName ) {

    // find the repository definition using its name
    RepositoryMeta repositoryMeta = repositoriesMeta.findRepository( repositoryName );

    if ( repositoryMeta == null ) {
      LOGGER.info( "Repository '" + repositoryName + "' not found in your " + Const.getKettleUserRepositoriesFile()
          + " file" );
    }
    return repositoryMeta;
  }

  public void connect( String repositoryName, PentahoUser user ) {
    RepositoryMeta repositoryMeta = findRepository( repositoryName );
    try {
      repository = registry.loadClass( RepositoryPluginType.class, repositoryMeta, Repository.class );
      repository.init( repositoryMeta );
    } catch ( KettlePluginException e ) {
      LOGGER.error( "Getting repository has failed!", e );
    }
    LOGGER.info( "Repository " + repository.getName() + " successfully initialized" );

    try {
      repository.connect( user.getName(), user.getPassword() );
      LOGGER.info( "Successfully connected to repository " + repository.getName() );
    } catch ( KettleException e ) {
      LOGGER.error( "Connection to Repository '" + repositoryName + "' was not created!" );
      throw new RuntimeException( e );
    }
    setIsConnected();

    // logical verification
    if ( !isConnected ) {
      LOGGER.error( "You are not connected to repository" );
    }
  }

  public void disconnect() {
    repository.disconnect();
    setIsConnected();

    // logical verification
    if ( isConnected ) {
      throw new RuntimeException( "You are still connected to repository '" + repository.getName() );
    } else {
      LOGGER.info( "You are successfully disconnected from repository " + repository.getName() );
    }
  }

  public void create( String repositoryName, String descr, String url, String commentMandatory ) {

    RepositoryMeta repositoryMeta =
        (RepositoryMeta) initPurRepositoryMeta( repositoryName, descr, url, commentMandatory );
    if ( findRepository( repositoryName ) != null ) {
      LOGGER.info( "Repository with name " + repositoryName + " already exists." );
      return;
    }
    repositoriesMeta.addRepository( repositoryMeta );
    writeRepositoriesData();

    // logical verification
    if ( findRepository( repositoryName ) == null ) {
      throw new RuntimeException( "Repository '" + repositoryName + "' was not created!" );
    } else {
      LOGGER.info( "Repository '" + repositoryName + "' successfully created" );
    }
  }

  public void update( String oldRepositoryName, String newRepositoryName, String newDescr, String newUrl,
      String commentMandatory ) {

    delete( oldRepositoryName );
    create( newRepositoryName, newDescr, newUrl, commentMandatory );
  }

  public void delete( String repositoryName ) {

    RepositoryMeta repositoryMeta = findRepository( repositoryName );

    if ( repositoryMeta == null ) {
      LOGGER.error( "Could not delete repository " + repositoryName + " as it does not exist." );
      return;
    }
    repositoriesMeta.removeRepository( repositoriesMeta.indexOfRepository( repositoryMeta ) );
    writeRepositoriesData();

    // logical verification
    if ( findRepository( repositoryName ) != null ) {
      throw new RuntimeException( "Repository was not deleted!" );
    } else {
      LOGGER.info( "Repository '" + repositoryName + "' successfully deleted." );
    }
  }

  private void writeRepositoriesData() {
    try {
      repositoriesMeta.writeData();
    } catch ( KettleException e ) {
      LOGGER.error( "Saving to repositories.xml has failed", e );
    }
  }

  private RepositoryMeta initPurRepositoryMeta( String name, String descr, String location, String commentMandatory ) {
    String retval = "";
    RepositoryMeta repMeta;
    try {
      repMeta = registry.loadClass( RepositoryPluginType.class, "PentahoEnterpriseRepository", RepositoryMeta.class );
    } catch ( KettlePluginException e1 ) {
      throw new RuntimeException( "Unable to load PurRepositoryMeta", e1 );
    }

    retval = retval.concat( "  " ).concat( XMLHandler.openTag( XML_TAG ) );
    retval = retval.concat( "    " ).concat( XMLHandler.addTagValue( "id", "PentahoEnterpriseRepository" ) );
    retval = retval.concat( "    " ).concat( XMLHandler.addTagValue( "name", name ) );
    retval = retval.concat( "    " ).concat( XMLHandler.addTagValue( "description", descr ) );

    retval = retval.concat( "    " ).concat( XMLHandler.addTagValue( "repository_location_url", location ) );
    retval = retval.concat( "    " ).concat( XMLHandler.addTagValue( "version_comment_mandatory", commentMandatory ) );
    retval = retval.concat( "  " ).concat( XMLHandler.closeTag( XML_TAG ) );

    Node node;
    try {
      node = XMLHandler.loadXMLString( retval ).getDocumentElement();
    } catch ( KettleXMLException e1 ) {
      throw new RuntimeException( "Unable to parse string to XML: " + retval, e1 );
    }
    List<DatabaseMeta> databases = new ArrayList<DatabaseMeta>();
    try {
      repMeta.loadXML( node, databases );
    } catch ( KettleException e ) {
      LOGGER.error( "Parsing of RepositoryMeta values has failed", e );
    }

    return repMeta;
  }

  public void save( PDIJob job, String directory ) {
    save( job.getJobMeta(), directory );
  }

  public void save( PDITransformation trans, String directory ) {
    save( trans.getTransMeta(), directory );
  }

  private void save( AbstractMeta meta, String directory ) {
    RepositoryDirectoryInterface tree;
    RepositoryDirectoryInterface dir;
    try {
      tree = repository.loadRepositoryDirectoryTree();
      dir = tree.findDirectory( directory );
      meta.setRepositoryDirectory( dir );
      meta.setRepository( repository );
    } catch ( KettleException e ) {
      LOGGER.error( "Cannot find directory '" + directory + "' in repository.", e );
      throw new RuntimeException( e );
    }

    RepositoryImporter repoImp = (RepositoryImporter) repository.getImporter();
    try {
      repository.save( meta, "SDK: Initial checkin", repoImp );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to save object " + meta.getName() + " into repository", e );
      // throw new RuntimeException( e );
    }
  }

  public void save( RepositoryElementInterface object ) {
    RepositoryImporter repoImp = (RepositoryImporter) repository.getImporter();
    try {
      repository.save( object, "SDK: Initial checkin", repoImp );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to save object " + object.getName() + " into repository", e );
      throw new RuntimeException( e );
    }
  }

  public Boolean isExist( String name, String path, RepositoryObjectType objectType ) {
    Boolean isExist = false;
    RepositoryDirectoryInterface dir = findDir( path );
    try {
      isExist = repository.exists( name, dir, objectType );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to find object '" + name + "' in folder '" + path + "'!" );
    }
    return isExist;
  }

  public RepositoryDirectoryInterface findDir( String path ) {
    RepositoryDirectoryInterface tree;
    RepositoryDirectoryInterface dir = null;
    try {
      tree = repository.loadRepositoryDirectoryTree();
      dir = tree.findDirectory( path );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to find directory: " + path + " into repository", e );
    }
    if ( dir == null ) {
      LOGGER.warn( "Directory " + path + " was not found in repository." );
    }
    return dir;
  }

  private RepositoryDirectoryInterface findDir( ObjectId objectId ) {
    RepositoryDirectoryInterface tree;
    RepositoryDirectoryInterface dir = null;
    try {
      tree = repository.loadRepositoryDirectoryTree();
      dir = tree.findDirectory( objectId );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to find directory with ID: " + objectId + " into repository", e );
    }
    if ( dir == null ) {
      LOGGER.info( "Directory with ID " + objectId + " was not found in repository." );
    }
    return dir;
  }

  public RepositoryDirectoryInterface createDir( String path ) {
    RepositoryDirectoryInterface tree;
    RepositoryDirectoryInterface dir = null;
    try {
      tree = repository.loadRepositoryDirectoryTree();
      dir = repository.createRepositoryDirectory( tree, path );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to create directory: " + path + " into repository", e );
      throw new RuntimeException( e );
    }
    LOGGER.info( "Folder " + path + " successfully created." );
    return dir;
  }

  public RepositoryDirectoryInterface renameDir( String oldPath, String name ) {
    RepositoryDirectoryInterface dir = findDir( oldPath );

    try {
      ObjectId dirId = repository.renameRepositoryDirectory( dir.getObjectId(), dir.getParent(), name );
      dir = findDir( dirId );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to save directory: " + name + " into repository", e );
      throw new RuntimeException( e );
    }
    LOGGER.info( "Folder " + dir.getPath() + " successfully renamed." );
    return dir;
  }

  public RepositoryDirectoryInterface moveDir( RepositoryDirectoryInterface dir, String path ) {
    RepositoryDirectoryInterface targetDir = findDir( path );
    ObjectId dirId;
    try {
      dirId = repository.renameRepositoryDirectory( dir.getObjectId(), targetDir, dir.getName() );
      dir = findDir( dirId );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to move directory: " + dir.getName() + " into new folder " + path, e );
      throw new RuntimeException( e );
    }

    if ( !dir.getParent().getPath().equals( path ) ) {
      throw new RuntimeException( "New directory: " + dir.getName() + " is in incorrect place "
          + dir.getParent().getPath() );
    }

    return dir;
  }

  public void deleteDir( String path ) {
    LOGGER.info( "Attempting to delete " + path + " directory." );
    RepositoryDirectoryInterface dir = findDir( path );
    deleteDir( dir );

  }

  public void deleteDir( RepositoryDirectoryInterface dir ) {
    if ( dir != null ) {
      try {
        repository.deleteRepositoryDirectory( dir );
      } catch ( KettleException e ) {
        LOGGER.error( "Failed to delete directory: " + dir.getPath() + " into repository", e );
        throw new RuntimeException();
      }
    } else {
      throw new RuntimeException( "Folder does not exist. Skipping delete operation." );
    }
    LOGGER.info( "Folder " + dir.getPath() + " successfully deleted." );
  }

  public void deleteObject( PDIBaseObject obj ) {
    if ( obj != null ) {
      try {
        switch ( obj.objType ) {
          case JOB:
            repository.deleteJob( obj.getObjectId() );
            break;
          case TRANSFORMATION:
            repository.deleteTransformation( obj.getObjectId() );
            break;
          default:
            LOGGER.warn( "The type of object is unknown. Operation is skipped." );
            break;
        }
      } catch ( KettleException e ) {
        LOGGER.error( "Failed to delete Object: " + obj.getName() + " from repository", e );
        throw new RuntimeException( e );
      }
    } else {
      LOGGER.error( "Object does not exist. Skipping delete operation." );
    }
  }

  public void deleteObject( RepositoryElementMetaInterface object ) {
    try {
      switch ( object.getObjectType() ) {
        case JOB:
          repository.deleteJob( object.getObjectId() );
          break;
        case TRANSFORMATION:
          repository.deleteTransformation( object.getObjectId() );
          break;
        default:
          LOGGER.warn( "The type of object is unknown. Operation is skipped." );
          break;
      }
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to delete Object: " + object.getName() + " from repository", e );
      throw new RuntimeException( e );
    }

  }

  public List<RepositoryElementMetaInterface> getDeletedJobs( String path ) {
    RepositoryDirectoryInterface dir = findDir( path );
    List<RepositoryElementMetaInterface> jobs = null;
    try {
      jobs = repository.getJobObjects( dir.getObjectId(), true );
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to get all Job objects from repository", e );
    }

    for ( RepositoryElementMetaInterface job : jobs ) {
      LOGGER.info( job.getName() );
      if ( !job.isDeleted() ) {
        jobs.remove( job );
      }
    }

    LOGGER.info( "The folder " + dir.getName() + " contains the following Deleted jobs:" );
    for ( RepositoryElementMetaInterface job : jobs ) {
      LOGGER.info( job.getName() );
    }

    return jobs;
  }

  public List<RepositoryElementMetaInterface> getDeletedObjects( String path, RepositoryObjectType type ) {
    RepositoryDirectoryInterface dir = findDir( path );
    List<RepositoryElementMetaInterface> objects = null;
    List<RepositoryElementMetaInterface> deletedObjects = new ArrayList<RepositoryElementMetaInterface>();
    ;
    try {
      switch ( type ) {
        case JOB:
          objects = repository.getJobObjects( dir.getObjectId(), true );
          break;
        case TRANSFORMATION:
          objects = repository.getTransformationObjects( dir.getObjectId(), true );
          break;
        default:
          LOGGER.warn( "The type of object is unknown. Operation is skipped." );
          break;
      }
    } catch ( KettleException e ) {
      LOGGER.error( "Failed to get all objects from repository for folder: " + dir.getPath(), e );
    }

    LOGGER.info( "The folder " + dir.getName() + " contains the following Deleted jobs:" );
    for ( RepositoryElementMetaInterface obj : objects ) {
      if ( obj.isDeleted() ) {
        deletedObjects.add( obj );
        LOGGER.info( obj.getName() );
      }
    }

    return deletedObjects;
  }

  public void restoreObject( RepositoryElementMetaInterface object ) {
    try {
      repository.undeleteObject( object );
    } catch ( KettleException e ) {
      LOGGER.error( "Unable to restore object '" + object.getName() + "' to '"
          + object.getRepositoryDirectory().getPath() + "' directory", e );
    }
    LOGGER.info( "Object '" + object.getName() + "' successfully restored to '"
        + object.getRepositoryDirectory().getPath() + "' directory" );
  }

  public PDIBaseObject move( PDIBaseObject object, String path ) {
    RepositoryDirectoryInterface dir = findDir( path );
    PDIBaseObject newObject = null;
    ObjectId newObjId = null;
    switch ( object.objType ) {
      case JOB:
        try {
          newObjId = repository.renameJob( object.getObjectId(), dir, object.getName() );
        } catch ( KettleException e ) {
          throw new RuntimeException( "Unable to move Job", e );
        }
        newObject = new PDIJob( path, "Generated Demo Job", repository );
        break;
      case TRANSFORMATION:
        try {
          newObjId = repository.renameTransformation( object.getObjectId(), dir, object.getName() );
        } catch ( KettleException e ) {
          throw new RuntimeException( "Unable to move Transformation", e );
        }
        newObject = new PDITransformation( path, "Generated Demo Transformation", repository );
        break;
      default:
        LOGGER.warn( "The type of object is unknown. Operation is skipped." );
        break;
    }

    if ( newObject == null ) {
      throw new RuntimeException( "Renamed object wasn't found in repository!" );
    }

    if ( !newObject.getObjectId().equals( newObjId ) ) {
      LOGGER.warn( "Strange, but IDs are not equal." );
    }

    return newObject;
  }

}
