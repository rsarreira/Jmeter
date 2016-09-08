package com.pentaho.services.spira;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.testng.ITestResult;

import com.inflectra.spiratest.addons.testnglistener.RunnerName;
import com.inflectra.spiratest.addons.testnglistener.SpiraTestExecute;
import com.inflectra.spiratest.addons.testnglistener.TestRun;
import com.qaprosoft.carina.core.foundation.crypto.CryptoTool;
import com.qaprosoft.carina.core.foundation.report.TestResultType;
import com.qaprosoft.carina.core.foundation.report.spira.ISpiraUpdater;
import com.qaprosoft.carina.core.foundation.report.spira.Spira;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.SpecialKeywords;

public class CustomSpiraUpdater implements ISpiraUpdater {
  private static String url;
  private static String user;
  private static String password;
  private static int releaseId;
  private static int testsetId;

  private static final Logger LOGGER = Logger.getLogger( CustomSpiraUpdater.class );

  private static CryptoTool cryptoTool;
  private static Pattern CRYPTO_PATTERN = Pattern.compile( SpecialKeywords.CRYPT );

  private static final LinkedHashMap<String, String> spiraStepsResult = new LinkedHashMap<String, String>();

  public enum Status {
    // 1 - Failed; 2 - Passed; 3 - Not Run; 4 - N/A; 5 - Blocked; 6 -
    // Caution
    PASS( 2 ), FAIL( 1 ), SKIP( 3 );

    private int status;

    private Status( int status ) {
      this.status = status;
    }

    public int getStatus() {
      return this.status;
    }
  }

  public CustomSpiraUpdater() {
    try {
      cryptoTool = new CryptoTool();
      user = cryptoTool.decryptByPattern( Configuration.get( Parameter.SPIRA_USER ), CRYPTO_PATTERN );
      password = cryptoTool.decryptByPattern( Configuration.get( Parameter.SPIRA_PASSWORD ), CRYPTO_PATTERN );
    } catch ( Exception e ) {
      LOGGER.error( "Unable to decrypt spira credentials!" );
      e.printStackTrace();
    }
    url = Configuration.get( Parameter.SPIRA_URL );

    try {
      releaseId = Configuration.getInt( Parameter.SPIRA_RELEASE_ID );
    } catch ( NumberFormatException e ) {
      LOGGER.warn( "Spira Updater is disabled as release id is not valid: "
          + Configuration.get( Parameter.SPIRA_RELEASE_ID ) );
      releaseId = -1;
    }
    try {
      testsetId = Configuration.getInt( Parameter.SPIRA_TESTSET_ID );
    } catch ( NumberFormatException e ) {
      // there is no warn as NULL value means absence of test set
      testsetId = -1;
    }
  }

  @Override
  public void updateAfterSuite( String testClass, TestResultType testResult, String message, String testName,
      long startDate ) {
    if ( releaseId == -1 ) {
      return;
    }
    // Create a new test run
    TestRun testRun = new TestRun();

    // get from class annotation!
    testRun.testCaseId = getTestcaseId( testClass );
    if ( testRun.testCaseId == -1 ) {
      return;
    }
    testRun.projectId = getProjectId( testClass );
    if ( testRun.projectId == -1 ) {
      return;
    }

    testRun.message = message;
    testRun.testName = testName;

    if (testResult.getName().equals( "PASS (KNOWN ISSUES)" )) {
      //for Spira Known Issue is FAIL anyway!
      testRun.executionStatusId = Status.valueOf( TestResultType.FAIL.getName() ).getStatus();
    } else {
      testRun.executionStatusId = Status.valueOf( testResult.getName() ).getStatus();
    }

    testRun.url = url;
    testRun.userName = user;
    testRun.password = password;
    testRun.releaseId = releaseId;
    testRun.testSetId = testsetId;

    testRun.stackTrace = getStepResults( testRun.projectId );

    testRun.runner = RunnerName.TestNG;

    LOGGER.info( SpecialKeywords.SPIRA_RELEASE_ID + "::" + releaseId );
    LOGGER.info( SpecialKeywords.SPIRA_TESTSET_ID + "::" + testsetId );
    LOGGER.info( SpecialKeywords.SPIRA_TESTCASE_ID + "::" + testRun.testCaseId );

    if ( testRun.url != null ) {
      // Get the current date/time
      Date now = new Date();

      // Instantiate the web service proxy class
      SpiraTestExecute spiraTestExecute = new SpiraTestExecute();

      // Populate the web service proxy with the connection info, then
      // execute the API method
      spiraTestExecute.url = testRun.url;
      spiraTestExecute.userName = testRun.userName;
      spiraTestExecute.password = testRun.password;
      spiraTestExecute.projectId = testRun.projectId;
      int testRunId = spiraTestExecute.recordTestRun( testRun, testResult.getFailed(), new Date( startDate ), now );

      LOGGER.info( "testRunId: " + testRunId );
      String spiraRunUrl = "%s/%d/TestRun/%d.aspx?testCaseId=%d";
      LOGGER.info( "spira URL: " + String.format( spiraRunUrl, url, testRun.projectId, testRunId, testRun.testCaseId ) );

      if ( testRunId == -1 ) {
        LOGGER.error( "Test result was not transfered to Spira!" );
      } else {
        LOGGER.info( "Test result was transfered to Spira." );
      }
    }
  }

  @Override
  public synchronized void updateAfterTest( ITestResult result, String errorMessage, List<String> jiraTickets ) {

    List<String> steps = Spira.getSteps();
    if ( steps.size() == 0 ) {
      return;
    }
    
    String jiraTicket = "";
    if (jiraTickets != null) {
    	if (jiraTickets.size() > 0) {
    		jiraTicket = " - " + jiraTickets.get(0);
    	}
    }

    if ( errorMessage == null ) {
      errorMessage = "";
    }

    switch ( result.getStatus() ) {
      case ITestResult.SUCCESS:
        // all steps passed
        for ( String step : steps ) {
          LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Passed" );
          spiraStepsResult.put( step, "Passed" );
        }
        break;
      case ITestResult.SKIP:
        for ( String step : steps ) {
          LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Skipped" );
          spiraStepsResult.put( step, "Skipped" );
        }
        break;
      case ITestResult.FAILURE:
        boolean foundFailureStep = false;
        // identify failure step and mark before steps as Passed, after them
        // as Blocked
        // if there is no way to identify failure step then mark all steps
        // as Failed
        for ( String step : steps ) {
          if ( errorMessage.contains( step ) ) {
            foundFailureStep = true;
          }
        }
        if ( !foundFailureStep ) {
          for ( String step : steps ) {
            LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Failed::" + errorMessage );
            spiraStepsResult.put( step, "Failed" + jiraTicket );
          }
        } else {
          foundFailureStep = false;
          for ( String step : steps ) {
            if ( errorMessage.contains( step ) ) {
              foundFailureStep = true;
              LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Failed::" + errorMessage );
              spiraStepsResult.put( step, "Failed" + jiraTicket );
            } else {
              if ( !foundFailureStep ) {
                LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Passed" );
                spiraStepsResult.put( step, "Passed" );
              } else {
                LOGGER.info( SpecialKeywords.SPIRA_TESTSTEP_ID + "::" + step + "::Blocked::" + errorMessage );
                spiraStepsResult.put( step, "Blocked" );
              }
            }
          }
        }
        break;
      default:
        break;
    }
  }

  private int getTestcaseId( String className ) {
    int testcaseId = -1;
    Class<?> testClass;
    try {
      testClass = Class.forName( className );
      if ( testClass.isAnnotationPresent( SpiraTestCase.class ) ) {
        SpiraTestCase classAnnotation = testClass.getAnnotation( SpiraTestCase.class );
        testcaseId = classAnnotation.testCaseId();

      }
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    }

    return testcaseId;
  }

  private int getProjectId( String className ) {
    int projectId = -1;
    Class<?> testClass;
    try {
      testClass = Class.forName( className );
      if ( testClass.isAnnotationPresent( SpiraTestCase.class ) ) {
        SpiraTestCase classAnnotation = testClass.getAnnotation( SpiraTestCase.class );
        projectId = classAnnotation.projectId();
      }
    } catch ( ClassNotFoundException e ) {
      e.printStackTrace();
    }

    return projectId;
  }

  private String getStepResults( int projectId ) {
    // combine all test steps into single String
    String res = "";
    for ( Map.Entry<String, String> entry : spiraStepsResult.entrySet() ) {
      String status = entry.getValue();
      if ( !status.equals( "Passed" ) ) {
        String stepURL = url + "/%d/TestStep/%s.aspx";
        stepURL = String.format( stepURL, projectId, entry.getKey() );
        res = res + entry.getKey() + " : " + entry.getValue() + " : " + stepURL + "\n";
      } else {
        res = res + entry.getKey() + " : " + entry.getValue() + "\n";
      }
    }
    return res;
  }

}
