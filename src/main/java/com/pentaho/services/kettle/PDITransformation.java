package com.pentaho.services.kettle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleMissingPluginsException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.DuplicateParamException;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransExecutionConfiguration;
import org.pentaho.di.trans.TransHopMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.testng.Assert;

public class PDITransformation extends PDIBaseObject {
  protected static final Logger LOGGER = Logger.getLogger( PDITransformation.class );
  protected TransMeta transMeta;
  protected Result result;
  protected int stepsCount;
  private String carteObjectId;

  public PDITransformation( String name ) {
    LOGGER.info( "Creating new Transformation: " + name );
    transMeta = new TransMeta();
    transMeta.setName( name );

    this.name = name;
    stepsCount = 0;

    // logical verification
    verifyStepsCount();
  }

  public PDITransformation( String filePath, Repository repo ) {
    this( filePath, repo, -1 );
  }

  public PDITransformation( String filePath, Repository repo, int stepsCount ) {
    try {
      transMeta = new TransMeta( filePath, repo );
      this.name = transMeta.getName();
    } catch ( KettleXMLException e ) {
      LOGGER.error( "Was not able to create new Transformation. Seemingly problem with XML (.ktr file).", e );
    } catch ( KettleMissingPluginsException e ) {
      LOGGER.error( "Was not able to create new Transformation. Seemingly problem with plugins.", e );
    }
    this.stepsCount = stepsCount;
    this.name = transMeta.getName();
    this.objType = RepositoryObjectType.TRANSFORMATION;

    // logical verification
    verifyStepsCount();
  }

  public PDITransformation( String directory, String transName, Repository repo ) {

    RepositoryDirectoryInterface repdir;
    RepositoryDirectoryInterface tree = null;
    try {
      tree = repo.loadRepositoryDirectoryTree();
    } catch ( KettleException e ) {
      LOGGER.error( "Cannot find directory '" + directory + "' in repository.", e );
    }

    repdir = tree.findDirectory( directory );

    try {
      transMeta = repo.loadTransformation( transName, repdir, null, true, null );
    } catch ( KettleException e ) {
      LOGGER.error( "Cannot load Transformation '" + transName + "' in repository.", e );
      throw new RuntimeException( e );
    }

    this.name = transMeta.getName();
    this.objectId = transMeta.getObjectId();
    this.objType = RepositoryObjectType.TRANSFORMATION;
  }

  public TransMeta getTransMeta() {
    return transMeta;
  }

  protected Result getResult() {
    return result;
  }

  public void addStep( StepMeta step ) {
    // include step in transformation
    LOGGER.info( "Adding step to the Transformation" );
    transMeta.addStep( step );
    stepsCount++;

    // logical verification
    verifyStepsCount();

  }

  public void addHop( StepMeta stepFrom, StepMeta stepTo ) {
    LOGGER.info( "Creating Hop between steps: '" + stepFrom.getName() + "' and '" + stepTo.getName() + "'" );
    transMeta.addTransHop( new TransHopMeta( stepFrom, stepTo ) );
  }

  public void deleteHop( StepMeta stepFrom, StepMeta stepTo ) {
    LOGGER.info( "Deleting Hop between steps: '" + stepFrom.getName() + "' and '" + stepTo.getName() + "'" );
    TransHopMeta hop = transMeta.findTransHop( stepFrom, stepTo );
    int index = transMeta.indexOfTransHop( hop );
    transMeta.removeTransHop( index );
  }

  public void save( String path ) {
    // get the xml of the definition and save it to a file for inspection in spoon
    LOGGER.info( "Saving to " + path );
    File file = new File( path );
    String xml = null;
    try {
      xml = transMeta.getXML();
      FileUtils.writeStringToFile( file, xml, "UTF-8" );
      filePath = path;
    } catch ( KettleException e ) {
      LOGGER.error( "Was not able to obtaint XML", e );
    } catch ( IOException e ) {
      LOGGER.error( "Failed to save XML to File", e );
    }

    // logical verification
    if ( !isSaved() ) {
      Assert.fail( "Transformation " + name + " was not saved to " + filePath );
    } else {
      LOGGER.info( "Transformation " + name + " was saved to " + filePath + " correctly." );
    }
    verifyStepsCount();

  }

  public void save( Repo repo, String path ) {
    LOGGER.info( "Saving to " + path );

    repo.save( this, path );

    // logical verification
    // TODO: verification isSaved
    if ( !repo.isExist( this.getName(), path, RepositoryObjectType.TRANSFORMATION ) ) {
      Assert.fail( "Transformation " + name + " was not saved to " + path );
    } else {
      LOGGER.info( "Transformation " + name + " was saved to " + path + " correctly." );
    }
    verifyStepsCount();
  }

  public Trans runTransformation() {
    return runTransformation( LogLevel.BASIC );
  }

  public Trans runTransformation( LogLevel level ) {
    LOGGER.info( "Attempting to run transformation '" + transMeta.getName() + "' from file system" );
    // Loading the transformation file from file system into the TransMeta object.

    LOGGER.info( "Attempting to read and set named parameters" );
    String[] declaredParameters = transMeta.listParameters();
    if ( declaredParameters.length > 0 ) {
      LOGGER.info( "Running transformation with parameters:" );

      for ( int i = 0; i < declaredParameters.length; i++ ) {
        String parameterName = declaredParameters[i];
        String parameterDefault = null;

        try {
          parameterDefault = transMeta.getParameterDefault( parameterName );
        } catch ( UnknownParamException e ) {
          LOGGER.info( "Parameter is not initialized", e );
        }
        LOGGER.info( parameterName + "=" + parameterDefault );
      }
    }

    // Creating a transformation object which is the programmatic representation of a transformation
    // A transformation object can be executed, report success, etc.
    Trans transformation = new Trans( transMeta );

    // adjust the log level
    transformation.setLogLevel( level );

    LOGGER.info( "Starting transformation" );

    // starting the transformation, which will execute asynchronously
    try {
      transformation.execute( new String[0] );
    } catch ( KettleException e ) {
      LOGGER.info( "Execution has failed", e );
    }

    LOGGER.info( "Waiting until finish transformation" );
    transformation.waitUntilFinished();

    // retrieve the result object, which captures the success of the transformation
    Result result = transformation.getResult();

    // report on the outcome of the transformation
    String outcome =
        "Trans " + transMeta.getName() + " executed "
            + ( result.getNrErrors() == 0 ? "successfully" : "with " + result.getNrErrors() + " errors" );
    LOGGER.info( outcome );

    return transformation;
  }

  public Result runTransformation( PDISlave slave ) {
    TransExecutionConfiguration execConf = new TransExecutionConfiguration();
    execConf.setRemoteServer( slave.getSlaveServer() );
    Repo repo = slave.getRepo();
    try {
      carteObjectId = Trans.sendToSlaveServer( transMeta, execConf, repo.getRepository(), null );
      result = refreshTransResult( slave );
    } catch ( KettleException e ) {
      LOGGER.error( "Execution failed on " + slave.getName(), e );
    } catch ( Exception e ) {
      LOGGER.error( "Failed to obtain Result of Job execution.", e );
    }
    return result;
  }

  public Result refreshTransResult( PDISlave slave ) {
    return refreshTransResult( slave, EXPLICIT_TIMEOUT );
  }

  public Result refreshTransResult( PDISlave slave, long timeout ) {

    if ( carteObjectId == null ) {
      LOGGER.error( "Carte Object ID of this Job is null! Impossible to retrieve its status." );
    }

    long i = 0;
    int step = 10;

    while ( i++ < step ) {
      try {
        result = slave.getSlaveServer().getTransStatus( transMeta.getName(), carteObjectId, 0 ).getResult();
        if ( result != null ) {
          break;
        }
        pause( timeout / step );
      } catch ( Exception e ) {
        LOGGER.error( "Failed to obtain Result of Transformation execution.", e );
        break;
      }
    }

    if ( result == null ) {
      // TODO: I prefer exception here. investigate pls.
      LOGGER.error( "Failed to obtain Result of Job execution after " + timeout + " seconds." );
    }

    return result;
  }

  public List<StepMeta> getSteps() {

    return transMeta.getSteps();
  }

  public StepMeta getStep( String stepName ) {
    StepMeta step = transMeta.findStep( stepName );
    LOGGER.info( "Step is found: " + step );
    return step;
  }

  public void setParameter( String paramName, String paramValue ) {
    try {
      transMeta.addParameterDefinition( paramName, paramValue, "" );
    } catch ( DuplicateParamException e ) {
      LOGGER.error( "Was not able to set parameter", e );
    }
  }

  private void verifyStepsCount() {
    if ( stepsCount == -1 ) {
      LOGGER.warn( "Steps count verification is disabled. stepsCount=" + stepsCount );
      return;
    }
    LOGGER.info( "Verifying Count of Transformation steps" );
    int actualCount = getSteps().size();

    if ( actualCount != stepsCount ) {
      LOGGER.info( "Number of steps is not correct: " + actualCount + " instead of " + stepsCount );
      LOGGER.info( getSteps() );
      Assert.fail( "Number of steps is not correct: " + actualCount + " instead of " + stepsCount );
    } else {
      LOGGER.info( "Number of steps is correct: " + actualCount );
    }
  }

}
