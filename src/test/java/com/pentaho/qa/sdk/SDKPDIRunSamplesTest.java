package com.pentaho.qa.sdk;

import java.util.Map;

import org.apache.log4j.Logger;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.kettle.PDIJob;
import com.pentaho.services.kettle.PDITransformation;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;

//https://spiratest.pentaho.com/5/TestCase/12014.aspx
@SpiraTestCase( projectId = 5, testCaseId = SpiraTestcases.SDKPDIRunSamplesTest )
public class SDKPDIRunSamplesTest extends SDKPluginBaseTest {
  protected static final Logger LOGGER = Logger.getLogger( SDKPDIRunSamplesTest.class );
  protected static final String PENTAHO_HOME = System.getProperty( "pentaho_home" );
  //protected static final String PENTAHO_HOME = "D:/Pentaho";
  protected static final String PDI_SAMPLES_HOME = PENTAHO_HOME + "/design-tools/data-integration/samples/";

  @Test( dataProvider = "DataProvider" )
  @CsvDataSourceParameters( dsUid = "JobPath", jiraColumn = "Jira", spiraColumn = "SpiraID", path = "CSV_data/PDIRunSamples/PDIRunSamples_Jobs.csv"  )
  public void runJobs( Map<String, String> args ) {
    LOGGER.info( "Step1 - Starting Jobs" );
    String jobFilePath = PDI_SAMPLES_HOME + "jobs/" + args.get( "JobPath" );
    PDIJob job = new PDIJob( jobFilePath, null );
    Job jobExec = job.run();
    String logText = getPDILog( jobExec );

    LOGGER.info( "Step2 - Verification that Job execution is correct" );
    Assert.assertTrue( jobExec.getResult().getResult(), "Job is not succeeded. Number of Errors: "
        + jobExec.getResult().getNrErrors() + "!" );
    Assert.assertTrue( logText.contains( args.get( "ExpectedText" ) ), "Expected log is: '"
        + args.get( "ExpectedText" ) + "'\nActual PDI log is:\n" + logText );

  }

  @Test( dataProvider = "DataProvider" )
  @CsvDataSourceParameters( dsUid = "TransformationPath", jiraColumn = "Jira", spiraColumn = "SpiraID", path = "CSV_data/PDIRunSamples/PDIRunSamples_Transformations.csv"  )
  public void runTransformations( Map<String, String> args ) {
    LOGGER.info( "Step1 - Starting Transformation" );
    String transFilePath = PDI_SAMPLES_HOME + "transformations/" + args.get( "TransformationPath" );
    PDITransformation trans = new PDITransformation( transFilePath, null );

    if ( args.get( "Variables" ) != null && !args.get( "Variables" ).isEmpty() ) {
      String[] parameters = args.get( "Variables" ).split( "\n" );
      for ( int i = 0; i < parameters.length; i++ ) {
        trans.setParameter( parameters[i].split( "=" )[0].trim(), parameters[i].split( "=" )[1] );
      }
    }

    Trans transExec = trans.runTransformation();
    String logText = getPDILog( transExec );

    // String logText = getPDILog(transExec);
    LOGGER.info( "Step2 - Verification that execution is correct" );
    Assert.assertTrue( transExec.getResult().getResult() && transExec.getResult().getNrErrors() == 0,
        "Transformation is not succeeded. Number of Errors: " + transExec.getResult().getNrErrors() + "!" );
    Assert.assertTrue( logText.contains( args.get( "ExpectedText" ) ), "Expected log is: '"
        + args.get( "ExpectedText" ) + "'\nActual PDI log is:\n" + logText );

  }
}
