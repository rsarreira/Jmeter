package com.pentaho.qa.sdk.shims;

import com.pentaho.qa.sdk.SDKBaseTest;
import com.pentaho.services.kettle.PDIJob;
import org.apache.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.job.Job;
import org.pentaho.di.version.BuildVersion;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

/**
 * Created by Ihar_Chekan on 4/14/2015.
 */
public class ShimsDemo extends SDKBaseTest {
    protected static final String PROJECT_ROOT = System.getProperty("user.dir");
    protected static final String PENTAHO_HOME = System.getProperty("pentaho_home");
    protected static final String PENTAHO_PLUGINS_HOME = PENTAHO_HOME + "/design-tools/data-integration/plugins";

    // TODO: it is definitely hardcoded path. Need to move it into Base SDK class for Shims
    protected static String SHIMS_DATA = PROJECT_ROOT + File.separator + "src/test/resources/shims_data";;


    protected static final Logger LOGGER = Logger.getLogger(ShimsDemo.class);

    @Override
    public void initialize() {}

    @BeforeClass( )
    @Parameters({"shim", "secured"})
    public void initialize(String shim, String secured) throws KettleException, IOException {

        if (PENTAHO_HOME == null) {
            throw new RuntimeException( "PENTAHO_HOME is not declared! Unable to proceed with SHIMS testing!");
        }
        LOGGER.info("PROJECT_ROOT: " + PROJECT_ROOT);

        LOGGER.info("PENTAHO_HOME: " + PENTAHO_HOME);
        LOGGER.info("PENTAHO_PLUGINS_HOME: " + PENTAHO_PLUGINS_HOME);
        LOGGER.info("SHIMS_DATA: " + SHIMS_DATA);

        // setting path to plugins dir for KettleEnvironment.init command
        System.setProperty("KETTLE_PLUGIN_BASE_FOLDERS", PENTAHO_PLUGINS_HOME);

        //setting path, which will be used in job
        System.setProperty("SHIMS_DATA", SHIMS_DATA);
        String testPropertiesLocation = "%s/test_properties/%s/%s";
        System.setProperty("SHIMS_TEST_PROPERTIES", String.format(testPropertiesLocation, SHIMS_DATA, shim, secured));


        KettleEnvironment.init();

        // retrieve logging appender
        appender = KettleLogStore.getAppender();
        BuildVersion version = BuildVersion.getInstance();
        LOGGER.info( "SDK build is " + version.getVersion() + version.getRevision() );
    }

    @Test()
    @Parameters({"jobPath"})
    public void runShimsJobTest(String jobPath) throws KettleException, IOException {

        LOGGER.info("Starting Job");
        jobPath = SHIMS_DATA + File.separator + jobPath;
        PDIJob job = new PDIJob(jobPath, null);
        Job jobExec = job.run();

        String logText = getPDILog(jobExec);

        LOGGER.info("Need to add some verification");
        //Assert.assertTrue(jobExec.getResult().getResult(), "Job is not succeeded. Number of Errors: " + jobExec.getResult().getNrErrors() + "!");
        //Assert.assertTrue(logText.contains( "Logging PDI Build Information: - Version: " ), "Actual PDI log is: " + logText);

    }

}
