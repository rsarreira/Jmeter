package com.pentaho.services.kettle;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobExecutionConfiguration;
import org.pentaho.di.job.JobHopMeta;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.testng.Assert;

public class PDIJob extends PDIBaseObject {

  protected static final Logger LOGGER = Logger.getLogger( PDIJob.class );
  protected JobMeta jobMeta;
  protected Result result;
  private String carteObjectId;

  protected int entryCount;

  public PDIJob( String name ) {
    LOGGER.info( "Creating new Job: " + name );
    jobMeta = new JobMeta();
    jobMeta.setName( name );

    this.name = name;
    entryCount = 0;

    // logical verification
    verifyEntryCount();
  }

  public PDIJob( String filePath, Repository repo ) {
    this( filePath, repo, -1 );
  }

  public PDIJob( String filePath, Repository repo, int entryCount ) {
    try {
      jobMeta = new JobMeta( filePath, repo );
      this.name = jobMeta.getName();
    } catch ( KettleXMLException e ) {
      LOGGER.error( "Failed to create new JobMeta", e );
    }
    this.entryCount = entryCount;
    this.name = jobMeta.getName();
    this.objType = RepositoryObjectType.JOB;

    // logical verification
    verifyEntryCount();
  }

  public PDIJob( String directory, String jobName, Repository repo ) {

    RepositoryDirectoryInterface repdir;
    RepositoryDirectoryInterface tree = null;

    try {
      tree = repo.loadRepositoryDirectoryTree();
    } catch ( KettleException e ) {
      LOGGER.error( "Cannot find directory '" + directory + "' in repository.", e );
      throw new RuntimeException( e );
    }

    repdir = tree.findDirectory( directory );
    try {
      jobMeta = repo.loadJob( jobName, repdir, null, null );
    } catch ( KettleException e ) {
      LOGGER.error( "Cannot load job '" + jobName + "' in repository.", e );
      throw new RuntimeException( e );
    }

    this.name = jobMeta.getName();
    this.objectId = jobMeta.getObjectId();
    this.objType = RepositoryObjectType.JOB;

  }

  public JobMeta getJobMeta() {
    return jobMeta;
  }

  protected Result getResult() {
    return result;
  }

  public void addEntry( JobEntryInterface entry ) {
    addEntry( entry, 100, 100 );
  }

  public void addEntry( JobEntryInterface entry, int posX, int posY ) {

    LOGGER.info( "Adding entry to the Job" );
    JobEntryCopy entryCopy = new JobEntryCopy( entry );
    entryCopy.setDrawn( true );
    entryCopy.setLocation( posX, posY );

    jobMeta.addJobEntry( entryCopy );
    entryCount++;

    // logical verification
    verifyEntryCount();
  }

  public void addHop( JobEntryInterface hopStart, JobEntryInterface hopFinish ) {
    addHop( hopStart, hopFinish, "null" );
  }

  public void addHop( JobEntryInterface hopStart, JobEntryInterface hopFinish, String evaluation ) {

    LOGGER.info( "Creating Hop between steps: '" + hopStart.getName() + "' and '" + hopFinish.getName() + "'" );

    JobEntryCopy entryStartCopy = new JobEntryCopy( hopStart );
    JobEntryCopy entryFinishCopy = new JobEntryCopy( hopFinish );
    JobHopMeta jobHopMeta = new JobHopMeta( entryStartCopy, entryFinishCopy );

    if ( evaluation.equalsIgnoreCase( "onFail" ) ) {
      LOGGER.info( "Hop has onFail evaluation" );
      jobHopMeta.setEvaluation( false );
    } else {
      LOGGER.info( "Hop has onSuccess evaluation" );
      jobHopMeta.setEvaluation( true );
    }

    jobMeta.addJobHop( jobHopMeta );

  }

  public void save( String path ) {
    // get the xml of the definition and save it to a file for inspection in spoon
    LOGGER.info( "Saving to " + path );
    String xml = jobMeta.getXML();
    File file = new File( path );
    try {
      FileUtils.writeStringToFile( file, xml, "UTF-8" );
      filePath = path;
    } catch ( IOException e ) {
      LOGGER.error( "Saving to file has failed.", e );
    }

    // logical verification
    if ( !isSaved() ) {
      Assert.fail( "Job " + name + " was not saved to " + filePath );
    } else {
      LOGGER.info( "Job " + name + " was saved to " + filePath + " correctly." );
    }
    verifyEntryCount();

  }

  public void save( Repo repo, String path ) {
    LOGGER.info( "Saving to " + path );

    repo.save( this, path );

    // logical verification
    // TODO investigate if possible to combine with isSaved method
    if ( !repo.isExist( this.getName(), path, RepositoryObjectType.JOB ) ) {
      Assert.fail( "Job " + name + " was not saved to " + path );
    } else {
      LOGGER.info( "Job " + name + " was saved to " + path + " correctly." );
    }

    verifyEntryCount();
  }

  public Job run() {
    return run( LogLevel.BASIC );
  }

  public Job run( LogLevel level ) {
    LOGGER.info( "Attempting to run Job " + jobMeta.getName() + " from file system" );

    LOGGER.info( "Attempting to read and set named parameters" );
    String[] declaredParameters = jobMeta.listParameters();
    for ( int i = 0; i < declaredParameters.length; i++ ) {
      String parameterName = declaredParameters[i];

      try {
        // determine the parameter description and default values for display purposes
        String description = jobMeta.getParameterDescription( parameterName );
        String defaultValue = jobMeta.getParameterDefault( parameterName );
        // set the parameter value to an arbitrary string
        String parameterValue = RandomStringUtils.randomAlphanumeric( 10 );

        String output =
            "Setting parameter " + parameterName + " to \"" + parameterValue + "\" [description: \"" + description
                + "\", default: \"" + defaultValue + "\"]";
        LOGGER.info( output );

        // assign the value to the parameter on the job
        jobMeta.setParameterValue( parameterName, parameterValue );
      } catch ( UnknownParamException e ) {
        LOGGER.info( "Parameter is not resolved", e );
      }

    }

    // Creating a Job object which is the programmatic representation of a job
    // A Job object can be executed, report success, etc.
    Job job = new Job( null, jobMeta );

    // adjust the log level
    job.setLogLevel( level );

    LOGGER.info( "Starting job" );
    job.start();

    LOGGER.info( "Waiting until finish job execution" );
    job.waitUntilFinished();

    // retrieve the result object, which captures the success of the job
    result = job.getResult();

    // report on the outcome of the job
    String outcome =
        "Job " + jobMeta.getName() + " executed with result: " + result.getResult() + " and " + result.getNrErrors()
            + " errors";
    LOGGER.info( outcome );

    return job;
  }

  public Result run( PDISlave slave ) {
    JobExecutionConfiguration execConf = new JobExecutionConfiguration();
    execConf.setRemoteServer( slave.getSlaveServer() );
    Repo repo = slave.getRepo();
    try {
      carteObjectId = Job.sendToSlaveServer( jobMeta, execConf, repo.getRepository(), null );
      Thread.sleep( 1000 );
      refreshJobResult( slave );

    } catch ( KettleException e ) {
      LOGGER.error( "Execution failed on " + slave.getName(), e );
    } catch ( Exception e ) {
      LOGGER.error( "Failed to obtain Result of Job execution.", e );
    }
    return result;
  }

  public Result refreshJobResult( PDISlave slave ) {
    return refreshJobResult( slave, EXPLICIT_TIMEOUT );
  }

  public Result refreshJobResult( PDISlave slave, long timeout ) {
    if ( carteObjectId == null ) {
      LOGGER.error( "Carte Object ID of this Job is null! Impossible to retrieve its status." );
    }

    long i = 0;
    int step = 10;

    while ( i++ < step ) {
      try {
        result = slave.getSlaveServer().getJobStatus( jobMeta.getName(), carteObjectId, 0 ).getResult();
        if ( result != null ) {
          break;
        }
        pause( timeout / step );
      } catch ( Exception e ) {
        LOGGER.error( "Failed to obtain Result of Job execution.", e );
        break;
      }
    }

    if ( result == null ) {
      // TODO: I prefer exception here. investigate pls.
      LOGGER.error( "Failed to obtain Result of Job execution after " + timeout + " seconds." );
    }

    return result;
  }

  public List<JobEntryCopy> getSteps() {

    return jobMeta.getJobCopies();
  }

  public JobEntryInterface getStep( String jobEntryName ) {
    JobEntryInterface jobEntry = jobMeta.findJobEntry( jobEntryName ).getEntry();
    LOGGER.info( "Job Entry is found: " + jobEntry );
    return jobEntry;
  }

  private void verifyEntryCount() {
    if ( entryCount == -1 ) {
      LOGGER.warn( "Entry count verification is disabled. entryCount=" + entryCount );
      return;
    }
    LOGGER.info( "Verifying Count of Job steps" );
    int actualCount = getSteps().size();

    if ( actualCount != entryCount ) {
      LOGGER.info( "Number of steps is not correct: " + actualCount + " instead of " + entryCount );
      LOGGER.info( getSteps() );
      Assert.fail( "Number of steps is not correct: " + actualCount + " instead of " + entryCount );
    } else {
      LOGGER.info( "Number of steps is correct: " + actualCount );
    }
  }

}
