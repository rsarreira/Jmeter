package com.pentaho.qa.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.pentaho.qa.SpiraTestcases;
import com.pentaho.services.api.SchedulerRequest;
import com.pentaho.services.api.http.ClientFactory;
import com.pentaho.services.schedules.Schedule;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

//https://spiratest.pentaho.com/6/TestCase/11239.aspx
@SpiraTestCase( projectId = 6, testCaseId = SpiraTestcases.SchedulingTest )
public class SchedulingTest extends APIBaseTest {

  protected static final Logger LOGGER = Logger.getLogger( SchedulingTest.class );
  private final String SCHEDULER_API_CSV = "CSV_data/SchedulerAPI/Scheduler_API.csv";

  Map<String, Schedule> schedules = new HashMap<String, Schedule>();

  @AfterClass
  public void clean() {
    SchedulerRequest request = new SchedulerRequest( adminUser );
    List<String> scheduleIDs = request.getJobs( "jobId" );
    LOGGER.info( "Schedules count:" + scheduleIDs.size() );
    for ( String scheduleID : scheduleIDs ) {
      LOGGER.info( "Schedule ID: " + scheduleID );
      if ( scheduleID.contains( "test_report_" ) ) {
        request.removeJob( scheduleID );
      }
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58235, 58235" )
  @CsvDataSourceParameters( dsUid = "Positive, Name, user, password", executeColumn = "TUID", executeValue = "P1",
      path = SCHEDULER_API_CSV )
  public void prerequsite( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    Schedule schedule = request.createSchedule( request.getName() ); // add request using api
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }

    schedules.put( schedule.getName(), schedule );
    ClientFactory.close();
  }

  @Test( dependsOnMethods = "prerequsite", dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58246" )
  @CsvDataSourceParameters( dsUid = "Positive, Name, user, password", executeColumn = "TestMethod",
      executeValue = "jobState", path = SCHEDULER_API_CSV )
  public void jobStateTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    // find existing schedule by name if any
    Schedule schedule = schedules.get( request.getName() );

    if ( schedule != null ) {
      request.jobState( schedule.getScheduleId() );
    } else {
      // implement logic for finding scheduleId if necessary
      request.jobState( "" );
    }
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58236" )
  @CsvDataSourceParameters( dsUid = "user, password", executeColumn = "TestMethod", executeValue = "canSchedule",
      path = SCHEDULER_API_CSV )
  public void canScheduleTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    request.canSchedule();

    if ( request.isPositive() ) {
      // toLowerCase is used to avoid problem with CSV, as Excel can automatically convert to UpperCase
      if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ).toLowerCase() ) ) {
        request.softAssert.assertAll();
      }
    } else {
      if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
        request.softAssert.assertAll();
      }
    }
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58237" )
  @CsvDataSourceParameters( dsUid = "Positive, user, password", executeColumn = "TestMethod",
      executeValue = "isScheduleAllowed", path = SCHEDULER_API_CSV )
  public void isScheduleAllowedTest( Map<String, String> args ) {

    SchedulerRequest request = new SchedulerRequest( args );
    String reportPath = args.get( "reportPath" );
    String fileID = request.fileProperty( adminUser, reportPath, "id" );
    ClientFactory.close();

    request.isScheduleAllow( fileID );
    if ( request.isPositive() ) {
      // toLowerCase is used to avoid problem with CSV, as Excel can automatically convert to UpperCase
      if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ).toLowerCase() ) ) {
        request.softAssert.assertAll();
      }
    } else {
      if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
        request.softAssert.assertAll();
      }
    }

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58238" )
  @CsvDataSourceParameters( dsUid = "Positive, Name, user, password", executeColumn = "TUID", executeValue = "S1",
      path = SCHEDULER_API_CSV )
  public void jobTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    int countBefore = request.getJobsCount( adminUser );
    LOGGER.info( "Jobs count before new Job is scheduled: " + countBefore );
    ClientFactory.close();

    Schedule schedule = request.createSchedule( request.getName() ); // add request using api
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }
    int countAfter = request.getJobsCount( adminUser );
    LOGGER.info( "Jobs count after new Job is scheduled: " + countAfter );

    String expectedJobName = request.getXMLvalue( request.getBody(), "jobName" );

    if ( schedule != null ) {
      // verify that scheduleNumber was incremented
      Assert.assertTrue( countAfter - countBefore == 1, "Number of jobs is not incremented properly!" );
      Assert.assertTrue( request.getJobs( adminUser, "jobName" ).contains( expectedJobName ), "Job with name '"
          + expectedJobName + "' was not created!" );
    } else {
      Assert.assertTrue( countAfter - countBefore == 0, "Number of jobs has incremented mistakenly!" );
      Assert.assertFalse( request.getJobs( adminUser, "jobName" ).contains( expectedJobName ), "Job with name '"
          + expectedJobName + "' has created!" );
    }
    ClientFactory.close();

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58239" )
  @CsvDataSourceParameters( dsUid = "Positive, user, password", executeColumn = "TestMethod", executeValue = "getJobs",
      path = SCHEDULER_API_CSV )
  public void getJobsTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    List<String> names = request.getJobs( "jobName" );

    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }

    if ( request.isPositive() ) {

      if ( !request.getName().isEmpty() ) {
        Schedule schedule = schedules.get( request.getName() );
        String scheduleName = schedule.getScheduleName();
        Assert.assertNotNull( names, "Bad response was returned" );
        Assert.assertTrue( names.size() > 0, "Strange, but no jobs were returned!" );
        Assert.assertTrue( names.contains( scheduleName ), "Job with name " + scheduleName + " was not found" );
      }
    }

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58240" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "jobInfo",
      path = SCHEDULER_API_CSV )
  public void jobInfoTest( Map<String, String> args ) {

    SchedulerRequest request = new SchedulerRequest( args );
    Schedule expectedSchedule = schedules.get( request.getName() );
    String scheduleId = expectedSchedule.getScheduleId();
    Schedule returnedSchedule = request.jobInfo( scheduleId );

    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) ) ) {
      request.softAssert.assertAll();
    }

    // toLowerCase is used to avoid problem with CSV, as Excel can automatically convert to UpperCase
    if ( !request.getName().isEmpty() & args.get( "Positive" ).toLowerCase().equals( "true" ) ) {
      Assert.assertTrue( returnedSchedule.getScheduleId().equals( expectedSchedule.getScheduleId() ),
          "Returned Schedule ID is not correct" );
    }

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58241" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "state",
      path = SCHEDULER_API_CSV )
  public void stateTest( Map<String, String> args ) {

    SchedulerRequest request = new SchedulerRequest( args );
    request.state();

    request.verify( Integer.valueOf( args.get( "expectedCode" ) ), args.get( "expectedBody" ) );

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58242" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "pause_start",
      path = SCHEDULER_API_CSV )
  public void pauseStartTest( Map<String, String> args ) {

    SchedulerRequest request = new SchedulerRequest( args );

    request.pause();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[0] ), args.get( "expectedBody" )
        .split( ";" )[0] ) ) {
      request.softAssert.assertAll();
    }

    request.state( adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[1] ), args.get( "expectedBody" )
        .split( ";" )[1] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

    request.start();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[2] ), args.get( "expectedBody" )
        .split( ";" )[2] ) ) {
      request.softAssert.assertAll();
    }

    request.state( adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[3] ), args.get( "expectedBody" )
        .split( ";" )[3] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

    request.start();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58243" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "pause_start",
      path = SCHEDULER_API_CSV )
  public void shutdownStartTest( Map<String, String> args ) {

    SchedulerRequest request = new SchedulerRequest( args );

    request.shutdown();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[0] ), args.get( "expectedBody" )
        .split( ";" )[0] ) ) {
      request.softAssert.assertAll();
    }

    request.state( adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[1] ), args.get( "expectedBody" )
        .split( ";" )[1] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

    request.start();
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[2] ), args.get( "expectedBody" )
        .split( ";" )[2] ) ) {
      request.softAssert.assertAll();
    }

    request.state( adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[3] ), args.get( "expectedBody" )
        .split( ";" )[3] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

    request.start();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58244" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "pauseJob",
      path = SCHEDULER_API_CSV )
  public void pauseJobTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );

    Schedule expectedSchedule = schedules.get( request.getName() );
    String scheduleId = expectedSchedule.getScheduleId();
    request.pauseJob( scheduleId );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[0] ), args.get( "expectedBody" )
        .split( ";" )[0] ) ) {
      request.softAssert.assertAll();
    }

    request.jobState( scheduleId, adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[1] ), args.get( "expectedBody" )
        .split( ";" )[1] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

    request.resumeJob( scheduleId );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[2] ), args.get( "expectedBody" )
        .split( ";" )[2] ) ) {
      request.softAssert.assertAll();
    }

    request.jobState( scheduleId, adminUser );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[3] ), args.get( "expectedBody" )
        .split( ";" )[3] ) ) {
      request.softAssert.assertAll();
    }
    ClientFactory.close();

  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58245" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "triggerNow",
      path = SCHEDULER_API_CSV )
  public void triggerJobTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    Schedule expectedSchedule = schedules.get( request.getName() );
    String scheduleId = expectedSchedule.getScheduleId();
    request.triggerJob( scheduleId );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[0] ), args.get( "expectedBody" )
        .split( ";" )[0] ) ) {
      request.softAssert.assertAll();
    }
  }

  @Test( dependsOnMethods = { "prerequsite", "jobStateTest", "jobInfoTest" }, alwaysRun = true,
      dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "58247" )
  @CsvDataSourceParameters( dsUid = "TUID, user", executeColumn = "TestMethod", executeValue = "removeJob",
      path = SCHEDULER_API_CSV )
  public void removeJobTest( Map<String, String> args ) {
    SchedulerRequest request = new SchedulerRequest( args );
    Schedule expectedSchedule = schedules.get( request.getName() );
    String scheduleId = expectedSchedule.getScheduleId();
    request.removeJob( scheduleId );
    if ( !request.verify( Integer.valueOf( args.get( "expectedCode" ).split( ";" )[0] ), args.get( "expectedBody" )
        .split( ";" )[0] ) ) {
      request.softAssert.assertAll();
    }
  }

}
