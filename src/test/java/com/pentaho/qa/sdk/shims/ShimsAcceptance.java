package com.pentaho.qa.sdk.shims;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.pentaho.qa.SpiraTestcases;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import org.apache.log4j.Logger;
import org.pentaho.di.core.exception.KettleException;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.utils.SpecialKeywords;

//https://spiratest.pentaho.com/28/TestCase/14719.aspx
@SpiraTestCase( projectId = 28, testCaseId = SpiraTestcases.ShimsAcceptance )
public class ShimsAcceptance extends ShimsBaseTest {
  protected static final Logger LOGGER = Logger.getLogger(ShimsAcceptance.class);
 
  @DataProvider(name = "Shim_DataProvider")
  public Object[][] customShimDataProvider(final Method testMethod, ITestContext context) {
    Object[][] args = createDataSingeThread(testMethod, context);
    String yes = "y";
    String scoopJob = "sqoop";
    
    // verify that shimName is populated in ShimsBaseTest
    if (shimName.isEmpty()) {
      Assert.fail( "shimName value was not populated from environment. Please review above log for additional details!" );
    }
    String shim_job_name = R.TESTDATA.get( "shim_job_name" );
    Boolean shimSqoopInclude = R.TESTDATA.getBoolean( "shim_include_sqoop" );
    

    if (!shim_job_name.isEmpty()) {
      LOGGER.info( "Additional filtering is on by job name: " + shim_job_name );
    }
    
    if (shimSqoopInclude) {
      LOGGER.info( "Include scoop jobs into the test run!" );
      //TODO: if java_version is 1.8 warn user about potential failure or reset property to "n"
    }
    
    /* filter DataRows based on obligatory shimName, shimSecured, shim_java_version and optional parameter shim_job_name */
    int index = 0;
    for (int i=0; i<args.length; i++) {
      @SuppressWarnings( "unchecked" )
      HashMap<String, String> parameters = (HashMap<String, String>) args[i][0];
      if ( parameters.get( shimName ) == null ) {
        Assert.fail( "Shims Acceptance DataProvider doesn't have appropriate Shim Column details: " + shimName );
      }
      if ( parameters.get( shimName ).equalsIgnoreCase( yes ) ) {
        if ( !shim_job_name.isEmpty() ) {
          if ( parameters.get( "JobName" ).contains( shim_job_name ) ) {
            index++;
          }
        } else {
          if ( !shimSqoopInclude ) {
            // exclude if job has "scoop" in the name
            if ( parameters.get( "JobName" ).toLowerCase().contains( scoopJob ) ) {
              continue;
            }
          }
          index++;
        }
      }
    }
    
    if ( index == 0 ) {
      Assert.fail("Unable to find Shim Jobs based on shim: " + shimName + "; secured: " + shimSecured + "; shim_job_name: " + shim_job_name);
    }
    
    Object[][] shimArgs = new Object[index][3];
    index = 0;
    for (int i=0; i<args.length; i++) {
      @SuppressWarnings( "unchecked" )
      HashMap<String, String> parameters = (HashMap<String, String>) args[i][0];
      if ( parameters.get( shimName ).equalsIgnoreCase( "y" ) ) {
        if ( !shim_job_name.isEmpty() ) {
          if ( parameters.get( "JobName" ).contains( shim_job_name ) ) {
            shimArgs[index][0] = SpecialKeywords.TUID + ":" + parameters.get( "JobName" );
            shimArgs[index][1] = parameters.get( "JobName" );
            shimArgs[index][2] = parameters.get( "SpiraID" );
            index++;
          }
        } else {
          if ( !shimSqoopInclude ) {
            // exclude if job has "scoop" in the name
            if ( parameters.get( "JobName" ).toLowerCase().contains( scoopJob ) ) {
              continue;
            }
          }

          shimArgs[index][0] = SpecialKeywords.TUID + ":" + parameters.get( "JobName" );
          shimArgs[index][1] = parameters.get( "JobName" );
          shimArgs[index][2] = parameters.get( "SpiraID" );
          index++;
        }
      }
    }
    return shimArgs;
  }

  
  @XlsDataSourceParameters( sheet = "Shim_Jobs", dsUid="JobName", spiraColumn = "SpiraID")
  @Test( dataProvider = "Shim_DataProvider" )
  public void runJob( String TUID, String jobName, String SpiraID ) throws KettleException, IOException {
    LOGGER.info( jobName );
    runShimsJobTest(jobName);
  }

}
