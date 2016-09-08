package com.pentaho.qa.sdk;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.kettle.PDIJob;
import com.pentaho.services.kettle.PDISlave;
import com.pentaho.services.kettle.PDITransformation;
import com.pentaho.services.kettle.Repo;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/5/TestCase/11736.aspx
@SpiraTestCase( projectId = 5, testCaseId = SpiraTestcases.EnterpriseRepositorySmokeTest )
public class EnterpriseRepositorySmokeTest extends SDKPluginBaseTest {
  protected static final Logger LOGGER = Logger.getLogger( EnterpriseRepositorySmokeTest.class );
  public static final String REPOSITORY_NAME = "SDK_Repo";
  Repo repo;

  @Test( )
  @SpiraTestSteps( testStepsId = "61991, 61992, 61993, 61994, 61995" )
  public void loginRepositoryTest() {
    LOGGER.info( "Step1 - Create new enterprise repository" );

    repo = new Repo();
    repo.create( REPOSITORY_NAME, "SDK_repo_descr", "http://localhost:9080/pentaho-di", "N" );

  }

  @Test( dependsOnMethods = "loginRepositoryTest" )
  @SpiraTestSteps( testStepsId = "61996" )
  public void verifyRepositoriesFileTest() {
    LOGGER.info( "Step1 - Verifying that created repo appear in repositories.xml" );
    LOGGER.info( SDKPluginBaseTest.userDir );
    String repoFilePath =
        System.getProperty( "user.home" ) + File.separator + ".kettle" + File.separator + "repositories.xml";
    File repoFile = new File( repoFilePath );
    String repositoryText = null;
    try {
      repositoryText = FileUtils.readFileToString( repoFile );
    } catch ( IOException e ) {
      LOGGER.info( "Reading of repositories.xml failed", e );
    }

    if ( !repositoryText.contains( REPOSITORY_NAME ) ) {
      Assert.fail( "Repositories.xml does not contain new repository " + REPOSITORY_NAME );
    }
  }

  @Test( dependsOnMethods = "loginRepositoryTest" )
  @SpiraTestSteps( testStepsId = "61997" )
  public void connectRepositoryTest() {
    LOGGER.info( "Step1 - Connect to an enterprise repository" );

    repo = new Repo();
    repo.connect( REPOSITORY_NAME, adminUser );

  }

  @Test( dependsOnMethods = "connectRepositoryTest" )
  @SpiraTestSteps( testStepsId = "61991" )
  @Parameters( { "SDK_job_file", "SDK_trans_file" } )
  public void saveJobTransformationTest( String job_file, String trans_file ) {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );
    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    job.save( repo, "/public" );

    LOGGER.info( "Step2 - Saving Transformation into an Enterprise repository" );
    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    trans.save( repo, "/public" );

  }

  @Test( dependsOnMethods = "connectRepositoryTest" )
  @SpiraTestSteps( testStepsId = "61999" )
  public void runJobTransformationLocallyTest() {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );
    PDIJob job = new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
    Job jobExec = job.run();
    String logText = getPDILog( jobExec );
    LOGGER.info( "Step2 - Verification that Job execution is correct" );
    Assert.assertTrue( jobExec.getResult().getResult(), "Job is not succeeded. Number of Errors: "
        + jobExec.getResult().getNrErrors() + "!" );
    Assert.assertTrue( logText.contains( "Logging PDI Build Information: - Version: " ), "Actual PDI log is: "
        + logText );

    LOGGER.info( "Step2 - Saving Transformation into an Enterprise repository" );
    PDITransformation trans = new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
    Trans transExec = trans.runTransformation();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + transExec.getResult().getNrErrors() + "!" );
  }

  @Test( dependsOnMethods = "connectRepositoryTest" )
  @SpiraTestSteps( testStepsId = "62000" )
  public void runJobTransformationRemotely() {

    LOGGER.info( "Step1 - Creating new Slave server in Enterprise repository" );
    PDISlave slave = new PDISlave( "DI_server_slave", "localhost", "9080", "pentaho-di", "admin", "password" );
    slave.save( repo );

    LOGGER.info( "Step1 - Getting Job from Enterprise repository and running it remotely" );
    PDIJob job = new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
    Result jobExecResult = job.run( slave );
    jobExecResult = job.refreshJobResult( slave );

    LOGGER.info( "Step3 - Verification that Job execution is correct" );
    Assert.assertTrue( jobExecResult.getResult(), "Job is not succeeded. Number of Errors: "
        + jobExecResult.getNrErrors() + "!" );
    Assert.assertTrue( jobExecResult.getLogText().contains( "Logging PDI Build Information: - Version: " ),
        "Actual PDI log is: " + jobExecResult.getLogText() );

    LOGGER.info( "Step4 - Getting Transformation from Enterprise repository and running it remotely" );
    PDITransformation trans = new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
    Result transExecResult = trans.runTransformation( slave );
    transExecResult = trans.refreshTransResult( slave );

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExecResult.getResult(), "Transformation is not succeeded. Number of Errors: "
        + transExecResult.getNrErrors() + "!" );
  }

  @Test( dependsOnMethods = "connectRepositoryTest" )
  @SpiraTestSteps( testStepsId = "62001" )
  public void disconnectRepositoryTest() {
    LOGGER.info( "Step1 - Disconnect from enterprise repository" );
    repo.disconnect();
  }

  @Test( dependsOnMethods = "disconnectRepositoryTest" )
  @SpiraTestSteps( testStepsId = "62002, 62003" )
  public void updateRepositoryTest() {
    LOGGER.info( "Step1 - Update enterprise repository name and description" );
    repo.update( REPOSITORY_NAME, REPOSITORY_NAME + "1", "SDK_repo_descr1", "http://localhost:9080/pentaho-di_invalid",
        "N" );
  }

  @Test( dependsOnMethods = "updateRepositoryTest" )
  @SpiraTestSteps( testStepsId = "62004, 62005, 62006, 62007" )
  public void deleteRepositoryTest() {
    LOGGER.info( "Step1 - Update enterprise repository name and description" );
    repo.delete( REPOSITORY_NAME + "1" );
  }

}
