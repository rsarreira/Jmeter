package com.pentaho.qa.sdk;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.RepositoryElementMetaInterface;
import org.pentaho.di.repository.RepositoryObjectType;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.kettle.PDIJob;
import com.pentaho.services.kettle.PDITransformation;
import com.pentaho.services.kettle.Repo;
import com.qaprosoft.carina.core.foundation.jira.Jira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//http://spiratest.pentaho.com/5/TestCase/12708.aspx
@SpiraTestCase( projectId = 5, testCaseId = SpiraTestcases.RepositoryExplorerSmokeTest )
public class RepositoryExplorerSmokeTest extends SDKPluginBaseTest {
  protected static final Logger LOGGER = Logger.getLogger( RepositoryExplorerSmokeTest.class );

  public static final String REPOSITORY_NAME = "SDK_Repo";
  Repo repo;

  @Test( )
  @SpiraTestSteps( testStepsId = "69665" )
  public void precondition() {
    LOGGER.info( "Step1 - Create and connect to new enterprise repository" );

    repo = new Repo();
    repo.create( REPOSITORY_NAME, "SDK_repo_descr", "http://localhost:9080/pentaho-di", "N" );
    repo.connect( REPOSITORY_NAME, adminUser );
  }

  @Test( dependsOnMethods = "precondition" )
  @SpiraTestSteps( testStepsId = "69666" )
  @Parameters( { "SDK_job_file", "SDK_trans_file" } )
  public void saveJobTransformationTest( String job_file, String trans_file ) {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );
    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    job.save( repo, "/public" );

    LOGGER.info( "Step2 - Saving Transformation into an Enterprise repository" );
    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    trans.save( repo, "/public" );

  }

  @Test( dependsOnMethods = "saveJobTransformationTest" )
  @SpiraTestSteps( testStepsId = "69667" )
  @Parameters( { "SDK_job_file", "SDK_trans_file" } )
  public void verifyFoldersTest( String job_file, String trans_file ) {
    LOGGER.info( "Step1 - Verification that /home folder exists" );
    RepositoryDirectoryInterface dir = repo.findDir( "/home" );
    softAssert.assertNotNull( dir, "Test is failed as /home dir not found" );

    LOGGER.info( "Step2 - Verification that /public folder exists" );
    RepositoryDirectoryInterface dir2 = repo.findDir( "/public" );
    softAssert.assertNotNull( dir2, "Test is failed as /public dir not found" );

    LOGGER.info( "Step3 - Verification that /Trash folder exists" );
    LOGGER.info( "Skipping this verification as actually /Trash folder exists only in UI of Spoon,"
        + " not a real repository folder." );

    LOGGER.info( "Step4 - Verification that previously saved job exists" );
    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    Boolean isJobExist = repo.isExist( job.getName(), "/public", RepositoryObjectType.JOB );
    softAssert.assertTrue( isJobExist, "Job was not find in repository!" );

    LOGGER.info( "Step5 - Verification that previously saved transformation exists" );
    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    Boolean isTransExist = repo.isExist( trans.getName(), "/public", RepositoryObjectType.TRANSFORMATION );
    softAssert.assertTrue( isTransExist, "Transformation was not find in repository!" );

    softAssert.assertAll();

  }

  @Test
  @SpiraTestSteps( testStepsId = "69668" )
  public void createFolderTest() {
    LOGGER.info( "Step1 - Creating /home/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.createDir( "/home/New Folder" );
    LOGGER.info( "Step2 - Verifying that /home/New Folder folder exists" );
    Assert.assertNotNull( "Folder was not created in repository!", dir );
  }

  @Test( dependsOnMethods = "createFolderTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69669" )
  public void renameFolderTest() {
    LOGGER.info( "Step1 - Trying to rename /home/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.renameDir( "/home/New Folder", "New Folder1" );

    LOGGER.info( "Step2 - Verifying that /home/New Folder folder renamed" );
    if ( !dir.getName().equals( "New Folder" ) ) {
      Assert.fail( "Folder was not renamed in repository!" );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "69670" )
  public void createFolder2Test() {
    LOGGER.info( "Step1 - Creating /home/admin/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.createDir( "/home/admin/New Folder" );
    LOGGER.info( "Step2 - Verifying that /home/admin/New Folder folder exists" );
    Assert.assertNotNull( "Folder was not created in repository!", dir );
  }

  @Test( dependsOnMethods = "createFolder2Test" )
  @SpiraTestSteps( testStepsId = "69671" )
  public void renameFolder2Test() {
    LOGGER.info( "Step1 - Trying to rename /home/admin/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.renameDir( "/home/admin/New Folder", "New Folder1" );

    LOGGER.info( "Step2 - Verifying that /home/admin/New Folder folder renamed" );
    if ( !dir.getName().equals( "New Folder1" ) ) {
      Assert.fail( "Folder was not renamed in repository!" );
    }
  }

  @Test( dependsOnMethods = "renameFolderTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69672" )
  public void deleteFolderTest() {
    LOGGER.info( "Step1 - Trying to delete /home/New Folder folder" );
    repo.deleteDir( "/home/New Folder" );

    LOGGER.info( "Step2 - Verifying that /home/New Folder folder does not exist" );
    RepositoryDirectoryInterface dir = repo.findDir( "/home/New Folder" );
    Assert.assertNotNull( "Folder was deleted in repository. Previously it was not possible.", dir );
  }

  @Test( dependsOnMethods = "renameFolder2Test" )
  @SpiraTestSteps( testStepsId = "69673" )
  public void deleteFolder2Test() {
    LOGGER.info( "Step1 - Trying to delete /home/admin/New Folder folder" );
    repo.deleteDir( "/home/admin/New Folder1" );

    LOGGER.info( "Step2 - Verifying that /home/admin/New Folder folder does not exist" );
    RepositoryDirectoryInterface dir = repo.findDir( "/home/admin/New Folder1" );
    Assert.assertNull( "Folder was not deleted in repository!", dir );
  }

  @Test( dependsOnMethods = "saveJobTransformationTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69674" )
  public void deleteJobTest() {
    LOGGER.info( "Step1 - Trying to delete 'Generated Demo Job'" );
    PDIJob job = new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
    repo.deleteObject( job );

    LOGGER.info( "Step2 - Verifying that 'Generated Demo Job' does not exist" );
    job = new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
  }

  @Test( dependsOnMethods = "saveJobTransformationTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69944" )
  public void deleteTransTest() {
    LOGGER.info( "Step1 - Trying to delete 'Generated Demo Transformation'" );
    PDITransformation trans = new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
    repo.deleteObject( trans );

    LOGGER.info( "Step2 - Verifying that 'Generated Demo Job' does not exist" );
    trans = new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
  }

  @Test( dependsOnMethods = "deleteFolder2Test" )
  @SpiraTestSteps( testStepsId = "69819" )
  public void restoreFolder() {
    LOGGER.info( "Step1 - Trying to find /home/admin/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.findDir( "/home/admin/New Folder" );

    if ( dir == null ) {
      Jira.setTickets( "PDI-14907" );
      Assert.fail( "Bug PDI-14907 is still actual. Deleted folder could not be found in repo." );
    }
  }

  @Test( dependsOnMethods = "deleteTransTest" )
  @SpiraTestSteps( testStepsId = "69820" )
  public void restoreTransformation() {
    List<RepositoryElementMetaInterface> transformations =
        repo.getDeletedObjects( "/public", RepositoryObjectType.TRANSFORMATION );
    RepositoryElementMetaInterface transformationsDeleted = transformations.get( 0 );
    repo.restoreObject( transformationsDeleted );

    LOGGER.info( "Step2 - Verifying that 'Generated Demo Transformation' does not restored." );
    new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
  }

  @Test( dependsOnMethods = "deleteJobTest" )
  @SpiraTestSteps( testStepsId = "69821" )
  public void restoreJob() {
    List<RepositoryElementMetaInterface> jobs = repo.getDeletedJobs( "/public" );
    RepositoryElementMetaInterface jobDeleted = jobs.get( 0 );
    repo.restoreObject( jobDeleted );

    LOGGER.info( "Step2 - Verifying that 'Generated Demo Job' does not restored." );
    new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
  }

  @Test( dependsOnMethods = "saveJobTransformationTest" )
  @SpiraTestSteps( testStepsId = "69822" )
  public void permanentlyDeleteFolderTest() {
    LOGGER.info( "Step1 - Trying to delete /home/admin/New Folder folder" );
    RepositoryDirectoryInterface dir = repo.createDir( "/home/admin/New Folder" );
    repo.deleteDir( "/home/admin/New Folder" );

    dir = repo.findDir( "/home/admin/New Folder" );
    if ( dir == null ) {
      Jira.setTickets( "PDI-14907" );
      Assert.fail( "Bug PDI-14907 is still actual. Deleted folder could not be found in repo." );
    }
  }

  @Test( dependsOnMethods = "restoreTransformation" )
  @SpiraTestSteps( testStepsId = "69823" )
  public void permanentlyDeleteTransTest() {
    LOGGER.info( "Step1 - Trying to delete 'Generated Demo Transformation'" );
    PDITransformation trans = new PDITransformation( "/public", "Generated Demo Transformation", repo.getRepository() );
    repo.deleteObject( trans );

    List<RepositoryElementMetaInterface> transformations =
        repo.getDeletedObjects( "/public", RepositoryObjectType.TRANSFORMATION );
    RepositoryElementMetaInterface transformationsDeleted = transformations.get( 0 );
    try {
      repo.deleteObject( transformationsDeleted );
    } catch ( RuntimeException e ) {
      Jira.setTickets( "PDI-14899" );
      Assert.fail( "bug PDI-14899 is still actual. Delete operation from trash is not possible." );
    }
  }

  @Test( dependsOnMethods = "restoreJob" )
  @SpiraTestSteps( testStepsId = "69824" )
  public void permanentlyDeleteJobTest() {
    LOGGER.info( "Step1 - Trying to delete 'Generated Demo Job'" );
    PDIJob job = new PDIJob( "/public", "Generated Demo Job", repo.getRepository() );
    repo.deleteObject( job );

    List<RepositoryElementMetaInterface> jobs = repo.getDeletedObjects( "/public", RepositoryObjectType.JOB );
    RepositoryElementMetaInterface jobDeleted = jobs.get( 0 );
    try {
      repo.deleteObject( jobDeleted );
    } catch ( RuntimeException e ) {
      Jira.setTickets( "PDI-14899" );
      Assert.fail( "bug PDI-14899 is still actual. Delete operation from trash is not possible." );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "70385" )
  @Parameters( { "SDK_job_file" } )
  public void moveJobTest( String job_file ) {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );
    RepositoryDirectoryInterface dir1 = repo.createDir( "/public/Folder1" );
    RepositoryDirectoryInterface dir2 = repo.createDir( "/public/Folder2" );

    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    job.save( repo, "/public/Folder1" );

    LOGGER.info( "Step2 - Verification that previously saved job exists" );
    Boolean isJobExist = repo.isExist( job.getName(), dir1.getPath(), RepositoryObjectType.JOB );
    Assert.assertTrue( "Job was not found in repository!", isJobExist );

    Boolean isJobExist2 = repo.isExist( job.getName(), dir2.getPath(), RepositoryObjectType.JOB );
    Assert.assertFalse( "Job was found in repository!", isJobExist2 );

    PDIJob jobToMove = new PDIJob( "/public/Folder1", "Generated Demo Job", repo.getRepository() );
    LOGGER.info( "Step3 - Moving job from /public/Folder1 to /public/Folder2" );
    PDIJob jobMoved = (PDIJob) repo.move( jobToMove, "/public/Folder2" );
    if ( jobMoved == null ) {
      Assert.fail( "Job was not moved." );
    }
  }

  @Test( dependsOnMethods = "moveJobTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69825" )
  @Parameters( { "SDK_job_file" } )
  public void moveJobsNegativeTest( String job_file ) {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );

    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    job.save( repo, "/public/Folder1" );

    LOGGER.info( "Step2 - Verification that previously saved job exists" );
    Boolean isJobExist = repo.isExist( job.getName(), "/public/Folder1", RepositoryObjectType.JOB );
    softAssert.assertTrue( isJobExist, "Job was not found in repository!" );

    Boolean isJobExist2 = repo.isExist( job.getName(), "/public/Folder2", RepositoryObjectType.JOB );
    softAssert.assertTrue( isJobExist2, "Job was not found in repository!" );

    PDIJob jobToMove = new PDIJob( "/public/Folder1", "Generated Demo Job", repo.getRepository() );
    LOGGER.info( "Step3 - Moving job from /public/Folder1 to /public/Folder2" );
    PDIJob jobMoved = (PDIJob) repo.move( jobToMove, "/public/Folder2" );
    if ( jobMoved != null ) {
      Assert.fail( "Job was moved and it's incorrect." );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "70394" )
  @Parameters( { "SDK_trans_file" } )
  public void moveTransTest( String trans_file ) {
    LOGGER.info( "Step1 - Saving Transformation into an Enterprise repository" );
    RepositoryDirectoryInterface dir1 = repo.createDir( "/public/Folder1" );
    RepositoryDirectoryInterface dir2 = repo.createDir( "/public/Folder2" );

    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    trans.save( repo, "/public/Folder1" );

    LOGGER.info( "Step2 - Verification that previously saved transformation exists" );
    Boolean isTransExist = repo.isExist( trans.getName(), dir1.getPath(), RepositoryObjectType.TRANSFORMATION );
    Assert.assertTrue( "Transformation was not found in repository!", isTransExist );

    Boolean isTransExist2 = repo.isExist( trans.getName(), dir2.getPath(), RepositoryObjectType.TRANSFORMATION );
    Assert.assertFalse( "Transformation was found in repository!", isTransExist2 );

    PDITransformation transToMove =
        new PDITransformation( "/public/Folder1", "Generated Demo Transformation", repo.getRepository() );
    LOGGER.info( "Step3 - Moving transformation from /public/Folder1 to /public/Folder2" );
    PDITransformation transMoved = (PDITransformation) repo.move( transToMove, "/public/Folder2" );

    if ( transMoved == null ) {
      Assert.fail( "Transformation was not moved." );
    }
  }

  @Test( dependsOnMethods = "moveTransTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69859" )
  @Parameters( { "SDK_trans_file" } )
  public void moveTransNegativeTest( String trans_file ) {
    LOGGER.info( "Step1 - Saving Transformation into an Enterprise repository" );

    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    trans.save( repo, "/public/Folder1" );

    LOGGER.info( "Step2 - Verification that previously saved transformation exists" );
    Boolean isTransExist = repo.isExist( trans.getName(), "/public/Folder1", RepositoryObjectType.TRANSFORMATION );
    Assert.assertTrue( "Transformation was not found in repository!", isTransExist );

    Boolean isTransExist2 = repo.isExist( trans.getName(), "/public/Folder2", RepositoryObjectType.TRANSFORMATION );
    Assert.assertTrue( "Transformation was not found in repository!", isTransExist2 );

    PDITransformation transToMove =
        new PDITransformation( "/public/Folder1", "Generated Demo Transformation", repo.getRepository() );
    LOGGER.info( "Step3 - Moving transformation from /public/Folder1 to /public/Folder2" );
    PDITransformation transMoved = (PDITransformation) repo.move( transToMove, "/public/Folder2" );

    if ( transMoved != null ) {
      Assert.fail( "Transformation was moved and it's incorrect." );
    }
  }

  @Test
  @SpiraTestSteps( testStepsId = "70409" )
  public void moveFolderTest() {
    LOGGER.info( "Step1 - Saving new Folder into an Enterprise repository" );
    RepositoryDirectoryInterface dir1 = repo.createDir( "/public/Folder1/Folder" );
    RepositoryDirectoryInterface dir2 = repo.createDir( "/public/Folder2" );

    LOGGER.info( "Step2 - Moving Folder from /public/Folder1/Folder to /public/Folder2" );
    RepositoryDirectoryInterface dirMoved = repo.moveDir( dir1, dir2.getPath() );
    Assert.assertNotNull( "Folder was not moved in repository!", dirMoved );
  }

  @Test( dependsOnMethods = "moveFolderTest", expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69860" )
  public void moveFolderNegativeTest() {
    LOGGER.info( "Step1 - Saving new Folder into an Enterprise repository" );
    RepositoryDirectoryInterface dir1 = repo.createDir( "/public/Folder1/Folder" );
    RepositoryDirectoryInterface dir2 = repo.createDir( "/public/Folder2" );

    LOGGER.info( "Step2 - Moving Folder from /public/Folder1/Folder to /public/Folder2" );
    RepositoryDirectoryInterface dirMoved = repo.moveDir( dir1, dir2.getPath() );
    Assert.assertNotNull( "Folder was not moved in repository!", dirMoved );
  }

  @Test( expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69861" )
  public void renameUserHomeTest() {
    LOGGER.info( "Step1 - Trying to rename /home/admin folder" );
    RepositoryDirectoryInterface dir = repo.renameDir( "/home/admin", "adminRenamed" );

    LOGGER.info( "Step2 - Verifying that /home/adminRenamed folder renamed" );
    if ( dir.getName().equals( "adminRenamed" ) ) {
      Assert.fail( "Folder was renamed in repository! It should not be possible!" );
    }
  }

  @Test( expectedExceptions = RuntimeException.class )
  @SpiraTestSteps( testStepsId = "69862" )
  public void deleteUserHomeTest() {
    LOGGER.info( "Step1 - Trying to rename /home/admin folder" );
    repo.deleteDir( "/home/admin" );

    LOGGER.info( "Step2 - Verifying that /home/adminRenamed folder renamed" );
    RepositoryDirectoryInterface dir = repo.findDir( "/home/admin" );
    if ( dir != null ) {
      Assert.fail( "Folder was deleted from repository! It should not be possible!" );
    }
  }

  @Test( dependsOnMethods = "precondition" )
  @SpiraTestSteps( testStepsId = "69666" )
  @Parameters( { "SDK_job_file", "SDK_trans_file" } )
  public void saveJobTransformationRootTest( String job_file, String trans_file ) {
    LOGGER.info( "Step1 - Saving Job into an Enterprise repository" );
    PDIJob job = new PDIJob( getUserdir() + job_file, repo.getRepository() );
    job.save( repo, "/" );

    LOGGER.info( "Step2 - Saving Transformation into an Enterprise repository" );
    PDITransformation trans = new PDITransformation( getUserdir() + trans_file, repo.getRepository() );
    trans.save( repo, "/" );

    LOGGER.info( "Step3 - Verification that previously saved job exists" );
    Boolean isJobExist = repo.isExist( job.getName(), "/", RepositoryObjectType.JOB );
    softAssert.assertTrue( isJobExist, "Job was not find in repository!" );

    LOGGER.info( "Step4 - Verification that previously saved transformation exists" );
    Boolean isTransExist = repo.isExist( trans.getName(), "/", RepositoryObjectType.TRANSFORMATION );
    softAssert.assertTrue( isTransExist, "Transformation was not find in repository!" );

    softAssert.assertAll();
  }
}
