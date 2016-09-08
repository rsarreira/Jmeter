package com.pentaho.qa.sdk;

import java.io.File;

import org.apache.log4j.Logger;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.entry.JobEntryInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMeta;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.kettle.PDIJob;
import com.pentaho.services.kettle.PDITransformation;
import com.pentaho.services.kettle.entries.AbortEntry;
import com.pentaho.services.kettle.entries.StartEntry;
import com.pentaho.services.kettle.entries.SuccessEntry;
import com.pentaho.services.kettle.entries.WriteToLogEntry;
import com.pentaho.services.kettle.steps.AddSequenceStep;
import com.pentaho.services.kettle.steps.DummyStep;
import com.pentaho.services.kettle.steps.RowGeneratorStep;
import com.pentaho.services.kettle.steps.WriteToLogStep;
import com.qaprosoft.carina.core.foundation.report.ReportContext;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/5/TestCase/11549.aspx
@SpiraTestCase( projectId = 5, testCaseId = SpiraTestcases.SDKPDISniffTest )
public class SDKPDISniffTest extends SDKBaseTest {
  protected static final Logger LOGGER = Logger.getLogger( SDKPDISniffTest.class );

  private String transFilePath = null;
  private String jobFilePath = null;
  private final static String TRANSFORMATION_NAME_1 = "Trans1.ktr";
  private final static String TRANSFORMATION_NAME_2 = "Trans2.ktr";
  private final static String TRANSFORMATION_NAME_3 = "Trans3.ktr";
  private final static String JOB_NAME_1 = "Job1.kjb";
  private final static String JOB_NAME_2 = "Job2.kjb";
  private final static String JOB_NAME_3 = "Job3.kjb";
  private PDITransformation trans = null;
  private PDIJob job = null;
  private File tempDir = ReportContext.getTempDir();

  @Test( )
  @SpiraTestSteps( testStepsId = "60486" )
  public void createTransformationTest() {
    LOGGER.info( "Step1 - Creating new Transformation" );
    trans = new PDITransformation( "Generated Demo Transformation" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60476" )
  public void addTransformationSteps() {
    LOGGER.info( "Step1 - Adding 'RowGenerator' step to the Transformation" );
    RowGeneratorStep rowGeneratorStep =
        new RowGeneratorStep( "Generate Some Rows", "5", 2, new String[] { "field_1", "field_2" }, new String[] {
          "String", "Integer" }, new String[] { "Hello World", "100" }, 100, 100 );
    trans.addStep( rowGeneratorStep.getStepMeta() );

    /*
     * LOGGER.info("Step3 - Adding 'AddSequence' step to the Transformation"); // Create and Connect AddSequenceStep
     * AddSequenceStep addSequenceStep = new AddSequenceStep( "Add Counter Field", "counter", "counter_1", 1,
     * Long.MAX_VALUE, 1, 300, 100); trans.addStep(addSequenceStep.getStepMeta());
     * trans.addHop(rowGeneratorStep.getStepMeta(), addSequenceStep.getStepMeta());
     * 
     * // Create and Connect DummyStep LOGGER.info("Step4 - Adding 'Dummy' step to the Transformation"); DummyStep
     * dummyStep = new DummyStep(500, 100); trans.addStep(dummyStep.getStepMeta());
     * trans.addHop(addSequenceStep.getStepMeta(), dummyStep.getStepMeta());
     */
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60477" )
  public void saveTransformation() {
    LOGGER.info( "Step1 - Saving Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_1;
    trans.save( transFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60478" )
  public void updateSaveTransformation() {

    LOGGER.info( "Step1 - Looking for 'Generate Some Rows' step in the Transformation" );
    StepMeta rowGeneratorStep0 = trans.getStep( "Generate Some Rows" );

    LOGGER.info( "Step2 - Adding 'AddSequence' step to the Transformation" );
    // Create and Connect AddSequenceStep
    AddSequenceStep addSequenceStep =
        new AddSequenceStep( "Add Counter Field", "counter", "counter_1", 1, Long.MAX_VALUE, 1, 300, 100 );
    trans.addStep( addSequenceStep.getStepMeta() );
    trans.addHop( rowGeneratorStep0, addSequenceStep.getStepMeta() );

    LOGGER.info( "Step3 - Saving Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_1;
    trans.save( transFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60479" )
  public void updateSaveAsTransformation() {

    LOGGER.info( "Step1 - Looking for 'Add Counter Field' step in the Transformation" );
    StepMeta addSequenceStep0 = trans.getStep( "Add Counter Field" );

    // Create and Connect DummyStep
    LOGGER.info( "Step2 - Adding 'Dummy' step to the Transformation" );
    DummyStep dummyStep = new DummyStep( 500, 100 );
    trans.addStep( dummyStep.getStepMeta() );
    trans.addHop( addSequenceStep0, dummyStep.getStepMeta() );

    LOGGER.info( "Step3 - Saving Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_2;
    trans.save( transFilePath );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60480" )
  public void updateSaveAsTransformation2() {

    LOGGER.info( "Step1.1 - Looking for 'Add Counter Field' step in the Transformation" );
    StepMeta addSequenceStep0 = trans.getStep( "Add Counter Field" );
    LOGGER.info( "Step1.1 - Looking for 'Dummy' step in the Transformation" );
    StepMeta dummy0 = trans.getStep( "Dummy" );

    LOGGER.info( "Step2 - Adding 'WriteToLog' step to the Transformation" );
    WriteToLogStep writeToLogStep =
        new WriteToLogStep( "Write To Log",
            "Version: ${Internal.Kettle.Version}\nBuild Date: ${Internal.Kettle.Build.Date}", 400, 200 );
    trans.addStep( writeToLogStep.getStepMeta() );
    trans.addHop( addSequenceStep0, writeToLogStep.getStepMeta() );
    trans.addHop( writeToLogStep.getStepMeta(), dummy0 );
    trans.deleteHop( addSequenceStep0, dummy0 );

    LOGGER.info( "Step3 - Saving Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_3;
    trans.save( transFilePath );
  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60481" )
  public void runTransformation() {
    LOGGER.info( "Step1 - Starting Transformation" );
    // PDITransformation trans = new PDITransformation(transFilePath, null);
    Trans transExec = trans.runTransformation();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + transExec.getResult().getNrErrors() + "!" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60482" )
  public void reopenTransformation() {
    LOGGER.info( "Step1 - Reopening Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_3;
    trans = new PDITransformation( transFilePath, null, 4 );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60483" )
  public void runReopenedTransformation() {
    LOGGER.info( "Step1 - Starting Transformation" );
    Trans transExec = trans.runTransformation();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + transExec.getResult().getNrErrors() + "!" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60484" )
  public void reopenOldTransformation() {
    LOGGER.info( "Step1 - Reopening Previous version of Transformation" );
    transFilePath = tempDir.getAbsolutePath() + File.separator + TRANSFORMATION_NAME_1;
    trans = new PDITransformation( transFilePath, null, 2 );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60485" )
  public void runReopenedOldTransformation() {
    LOGGER.info( "Step1 - Starting Transformation" );
    Trans transExec = trans.runTransformation();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + transExec.getResult().getNrErrors() + "!" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60487" )
  public void createJobTest() {
    LOGGER.info( "Step1 - Creating new Job" );
    job = new PDIJob( "Generated Demo Job" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60488" )
  public void addJobSteps() {
    LOGGER.info( "Step1 - Adding 'Start' entry to the Job" );
    StartEntry start = new StartEntry( "START" );
    job.addEntry( start, 100, 100 );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60489" )
  public void saveJob() {
    LOGGER.info( "Step1 - Saving Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_1;
    job.save( jobFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60490" )
  public void updateSaveJob() {

    LOGGER.info( "Step1 - Looking for 'START' step in the Job" );
    JobEntryInterface startStep = job.getStep( "START" );

    LOGGER.info( "Step2 - Adding Write To Log entry to the Job" );
    WriteToLogEntry writeToLog =
        new WriteToLogEntry( "Output PDI Stats", "Logging PDI Build Information:",
            "Version: ${Internal.Kettle.Version} \n" + "Build Date: ${Internal.Kettle.Build.Date}", LogLevel.BASIC );
    job.addEntry( writeToLog, 300, 100 );
    job.addHop( startStep, writeToLog );

    LOGGER.info( "Step3 - Saving Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_1;
    job.save( jobFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60491" )
  public void updateSaveAsJob() {

    LOGGER.info( "Step1 - Looking for 'Output PDI Stats' step in the Job" );
    JobEntryInterface writeToLogStep = job.getStep( "Output PDI Stats" );

    LOGGER.info( "Step2 - Adding 'Success' entry to the Job" );
    SuccessEntry success = new SuccessEntry( "Success" );
    job.addEntry( success, 500, 100 );
    job.addHop( writeToLogStep, success, "onSuccess" );

    LOGGER.info( "Step3 - Saving Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_2;
    job.save( jobFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60492" )
  public void updateSaveAsJob2() {

    LOGGER.info( "Step1 - Looking for 'Success' step in the Job" );
    JobEntryInterface writeToLog = job.getStep( "Output PDI Stats" );

    LOGGER.info( "Step2 - Adding 'Abort' entry to the Job" );
    AbortEntry abort = new AbortEntry( "Abort", "Previous step '" + writeToLog.getName() + "' has failed" );
    job.addEntry( abort, 500, 200 );
    job.addHop( writeToLog, abort, "onFail" );

    LOGGER.info( "Step3 - Saving Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_3;
    job.save( jobFilePath );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60493" )
  public void runJob() {
    LOGGER.info( "Step1 - Starting Job" );
    Job jobExec = job.run();

    String logText = getPDILog( jobExec );

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( jobExec.getResult().getResult(), "Job is not succeeded. Number of Errors: "
        + jobExec.getResult().getNrErrors() + "!" );

    Assert.assertTrue( logText.contains( "Logging PDI Build Information: - Version: " ), "Actual PDI log is: "
        + logText );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60494" )
  public void reopenJob() {
    LOGGER.info( "Step1 - Reopening Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_3;
    job = new PDIJob( jobFilePath, null, 4 );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60495" )
  public void runReopenedJob() {
    LOGGER.info( "Step1 - Starting Job" );
    Job jobExec = job.run();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( jobExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + jobExec.getResult().getNrErrors() + "!" );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60496" )
  public void reopenOldJob() {
    LOGGER.info( "Step1 - Reopening Previous version of Job" );
    jobFilePath = tempDir.getAbsolutePath() + File.separator + JOB_NAME_1;
    job = new PDIJob( jobFilePath, null, 2 );

  }

  @Test( )
  @SpiraTestSteps( testStepsId = "60497" )
  public void runReopenedOldJob() {
    LOGGER.info( "Step1 - Starting Job" );
    Job jobExec = job.run();

    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( jobExec.getResult().getResult(), "Transformation is not succeeded. Number of Errors: "
        + jobExec.getResult().getNrErrors() + "!" );

  }

}
