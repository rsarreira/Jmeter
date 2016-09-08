package com.pentaho.qa.sdk;

import java.io.File;

import org.apache.log4j.Logger;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.testng.Assert;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.services.kettle.PDIJob;
import com.pentaho.services.kettle.PDITransformation;
import com.pentaho.services.kettle.entries.AbortEntry;
import com.pentaho.services.kettle.entries.StartEntry;
import com.pentaho.services.kettle.entries.SuccessEntry;
import com.pentaho.services.kettle.entries.WriteToLogEntry;
import com.pentaho.services.kettle.steps.AddSequenceStep;
import com.pentaho.services.kettle.steps.DummyStep;
import com.pentaho.services.kettle.steps.RowGeneratorStep;
import com.qaprosoft.carina.core.foundation.report.ReportContext;

public class SDK_PDI_CI_test extends SDKBaseTest {

	protected static final Logger LOGGER = Logger
			.getLogger(SDK_PDI_CI_test.class);

	private String transFilePath = null;
	private String jobFilePath = null;

	@Test()
	@Parameters({ "transformationName" })
	public void generateTransformationTest(String transformationName) {

		LOGGER.info("Step1 - Creating new Transformation");
		PDITransformation trans = new PDITransformation(
				"Generated Demo Transformation");

		// TODO: step generation from data source
		LOGGER.info("Step2 - Adding 'RwoGenerator' step to the Transformation");
		RowGeneratorStep rowGeneratorStep = new RowGeneratorStep(
				"Generate Some Rows", "5", 2, new String[] { "field_1",
						"field_2" }, new String[] { "String", "Integer" },
				new String[] { "Hello World", "100" }, 100, 100);
		trans.addStep(rowGeneratorStep.getStepMeta());

		LOGGER.info("Step3 - Adding 'AddSequence' step to the Transformation");
		// Create and Connect AddSequenceStep
		AddSequenceStep addSequenceStep = new AddSequenceStep(
				"Add Counter Field", "counter", "counter_1", 1, Long.MAX_VALUE,
				1, 300, 100);
		trans.addStep(addSequenceStep.getStepMeta());
		trans.addHop(rowGeneratorStep.getStepMeta(),
				addSequenceStep.getStepMeta());

		// Create and Connect DummyStep
		LOGGER.info("Step4 - Adding 'Dummy' step to the Transformation");
		DummyStep dummyStep = new DummyStep(500, 100);
		trans.addStep(dummyStep.getStepMeta());
		trans.addHop(addSequenceStep.getStepMeta(), dummyStep.getStepMeta());

		LOGGER.info("Step5 - Saving Transformation");
		File tempDir = ReportContext.getTempDir();
		transFilePath = tempDir.getAbsolutePath() + File.separator
				+ transformationName;
		trans.save(transFilePath);

		LOGGER.info("Step6 - Verification that .ktr was created");
		File expectedFile = new File(transFilePath);
		Assert.assertTrue(expectedFile.exists(),
				"Transformation was not saved to " + transFilePath);

	}

	@Test()
	public void runTransformationTest() {

		LOGGER.info("Step1 - Starting Transformation");
		PDITransformation trans = new PDITransformation(transFilePath, null);
		Trans transExec = trans.runTransformation();

		//String logText = getPDILog(transExec);

		LOGGER.info("Step2 - Verification that execution is correct");
		Assert.assertTrue(transExec.getResult().getResult(),
				"Transformation is not succeeded. Number of Errors: "
						+ transExec.getResult().getNrErrors() + "!");

	}

	@Test()
	@Parameters({ "jobName" })
	public void generateJobTest(String jobName) {

		LOGGER.info("Step1 - Creating new Job");
		PDIJob job = new PDIJob("Generated Demo Job");

		LOGGER.info("Step2 - Adding 'Start' entry to the Job");
		StartEntry start = new StartEntry("START");
		job.addEntry(start, 100, 100);

		// LOGGER.info( "Step3 - Adding 'Dummy' entry to the Job" );
		// DummyEntry dummy = new DummyEntry("Dummy");
		// job.addEntry(dummy, 200, 100 );
		// job.addHop(start, dummy);

		LOGGER.info("Step3 - Adding Write To Log entry to the Job");
		WriteToLogEntry writeToLog = new WriteToLogEntry("Output PDI Stats",
				"Logging PDI Build Information:",
				"Version: ${Internal.Kettle.Version} \n"
						+ "Build Date: ${Internal.Kettle.Build.Date}");
		job.addEntry(writeToLog, 300, 100);
		job.addHop(start, writeToLog);

		LOGGER.info("Step4 - Adding 'Success' entry to the Job");
		SuccessEntry success = new SuccessEntry("Success");
		job.addEntry(success, 500, 100);
		job.addHop(writeToLog, success, "onSuccess");

		LOGGER.info("Step5 - Adding 'Abort' entry to the Job");
		AbortEntry abort = new AbortEntry("Abort", "Previous step '"
				+ writeToLog.getName() + "' has failed");
		job.addEntry(abort, 500, 200);
		job.addHop(writeToLog, abort, "onFail");

		LOGGER.info("Step6 - Saving Job");
		File tempDir = ReportContext.getTempDir();
		jobFilePath = tempDir.getAbsolutePath() + File.separator + jobName;
		job.save(jobFilePath);

		LOGGER.info("Step7 - Verification that .kjb was created");
		File expectedFile = new File(transFilePath);
		Assert.assertTrue(expectedFile.exists(), "Job was not saved to "
				+ transFilePath);

	}

	@Test()
	@Parameters({ "jobName" })
	public void runJobTest(String jobName) {

		LOGGER.info("Step1 - Starting Job");
		PDIJob job = new PDIJob(jobFilePath, null);
		Job jobExec = job.run();

		String logText = getPDILog(jobExec);

		LOGGER.info("Step2 - Verification that Job execution is correct");
		Assert.assertTrue(jobExec.getResult().getResult(),
				"Job is not succeeded. Number of Errors: "
						+ jobExec.getResult().getNrErrors() + "!");
		Assert.assertTrue(
				logText.contains("Logging PDI Build Information: - Version: "),
				"Actual PDI log is: " + logText);

	}

}
