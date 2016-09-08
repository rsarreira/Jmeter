package com.pentaho.services.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.testng.Assert;

import com.pentaho.services.PentahoUser;
import com.pentaho.services.api.http.ClientFactory;
import com.pentaho.services.api.http.CustomResponse;
import com.pentaho.services.api.http.Header;
import com.pentaho.services.api.http.Header.HeaderType;
import com.pentaho.services.schedules.Schedule;

public class SchedulerRequest extends Request {

  protected static final Logger LOGGER = Logger.getLogger( SchedulerRequest.class );
  private final static String SCHEDULE_ID = "scheduleId";

  public SchedulerRequest( PentahoUser user ) {
    super( user );
  }

  public SchedulerRequest( Map<String, String> args ) {
    super( args );
  }
  
  public List<String> getJobs( String keyString ) {
    return getJobs( user, keyString );
  }

  public List<String> getJobs( PentahoUser user, String keyString ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/api/scheduler/getJobs", authorizationHeader );

    JSONObject allJobsJson = convertToJSON( customResponse.getResponseBody() );
    List<String> names = new ArrayList<String>();
    if ( allJobsJson != null ) {
      // names = getListFromJSON( allJobsJson, "job", "jobName" );
      names = getListFromJSON( allJobsJson, "job", keyString );
      LOGGER.info( "Jobs names: " + names );
    }

    return names;
  }

  public int getJobsCount() {
    List<String> names = getJobs( "jobName" );
    LOGGER.info( "Jobs count: " + names.size() );
    return names.size();
  }

  public int getJobsCount( PentahoUser user ) {
    List<String> names = getJobs( user, "jobName" );
    LOGGER.info( "Jobs count: " + names.size() );
    return names.size();
  }

  public Schedule createSchedule( String name ) {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/job", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

    String expectedJobName = getXMLvalue( body, "jobName" );

    if ( positive ) {
      if ( !customResponse.getResponseBody().contains( user.getName() + "\t" + expectedJobName ) ) {
        Assert.fail( "Response doesn't contain expectedJobName: " + expectedJobName );
      }
      return new Schedule( name, customResponse.getResponseBody() );
    }

    // TODO: #2parse id and get name
    return null;
  }

  public void jobState( String scheduleId ) {
    jobState( scheduleId, user );
  }

  public void jobState( String scheduleId, PentahoUser user ) {
    if ( body == null || body.isEmpty() ) {
      // set it to default value for getState
      body = "<jobRequest><jobId>scheduleId</jobId></jobRequest>";
    }

    body = body.replace( SCHEDULE_ID, scheduleId );
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/jobState", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

  }

  public Schedule jobInfo( String scheduleId ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/api/scheduler/jobinfo?jobId=" + scheduleId.replace( "\t", "%09" ),
            authorizationHeader );
    Schedule newSchedule = null;

    if ( positive ) {
      JSONObject json = convertToJSON( customResponse.getResponseBody() );
      String schedulerJobId;
      try {
        schedulerJobId = json.getString( "jobId" );
        String schedulerJobName = json.getString( "jobName" );
        newSchedule = new Schedule( schedulerJobName, schedulerJobId );
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
    return newSchedule;

  }

  public void canSchedule() {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/api/scheduler/canSchedule", authorizationHeader );
    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Can this user schedule? =" + customResponse.getResponseBody() );

  }

  public void isScheduleAllow( String fileID ) {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance()
            .sendGet( URL + "/api/scheduler/isScheduleAllowed?id=" + fileID, authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void state() {
    state( user );
  }

  public void state( PentahoUser user ) {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/api/scheduler/state", authorizationHeader );
    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "BA scheduler state is: " + customResponse.getResponseBody() );

  }

  public void pause() {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/pause", "This POST body does not contain data.",
            "UTF-8", authorizationHeader, contentTypeHeader );
    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "BA scheduler state is: " + customResponse.getResponseBody() );
  }

  public void start() {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/start", "This POST body does not contain data.",
            "UTF-8", authorizationHeader, contentTypeHeader );
    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "BA scheduler state is: " + customResponse.getResponseBody() );
  }

  public void shutdown() {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/shutdown", "This POST body does not contain data.",
            "UTF-8", authorizationHeader, contentTypeHeader );
    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "BA scheduler state is: " + customResponse.getResponseBody() );
  }

  public void pauseJob( String scheduleId ) {

    if ( body == null || body.isEmpty() ) {
      // set it to default value for getState
      body = "<jobRequest><jobId>scheduleId</jobId></jobRequest>";
    }

    body = body.replace( SCHEDULE_ID, scheduleId );
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/pauseJob", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

  }

  public void resumeJob( String scheduleId ) {

    if ( body == null ) {
      // set it to default value for getState
      body = "<jobRequest><jobId>scheduleId</jobId></jobRequest>";
    }

    body = body.replace( SCHEDULE_ID, scheduleId );
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/resumeJob", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

  }

  public void triggerJob( String scheduleId ) {

    if ( body == null || body.isEmpty() ) {
      // set it to default value for getState
      body = "<jobRequest><jobId>scheduleId</jobId></jobRequest>";
    }

    body = body.replace( SCHEDULE_ID, scheduleId );
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/api/scheduler/triggerNow", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

  }

  public void removeJob( String scheduleId ) {

    body = "<jobRequest><jobId>scheduleId</jobId></jobRequest>";
    body = body.replace( SCHEDULE_ID, scheduleId );
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/xml" );
    customResponse =
        ClientFactory.getInstance().sendDelete( URL + "/api/scheduler/removeJob", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Body: " + customResponse.getResponseBody() );

  }

  public String fileProperty( String reportPath, String property ) {
    return fileProperty( user, reportPath, property );
  }

  public String fileProperty( PentahoUser user, String reportPath, String property ) {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header acceptHeader = new Header( "Accept", "application/json" );

    // getting ID for report file
    CustomResponse customResponseFile =
        ClientFactory.getInstance().sendGet( URL + "/api/repo/files/:" + reportPath + "/properties",
            authorizationHeader, acceptHeader );
    JSONObject json = null;
    String fileProperty = null;
    json = convertToJSON( customResponseFile.getResponseBody() );
    try {
      fileProperty = json.getString( property );
    } catch ( Exception e ) {
      LOGGER.error( "Faled to work with JSON. JSONException arises." );
      e.printStackTrace();
    }
    LOGGER.info( "File property '" + property + "' is: " + fileProperty );
    return fileProperty;
  }
  
  /*public void fileImport( String reportPath) {
    fileImport( user, reportPath );
  }*/
  
  /*public void fileImport( PentahoUser user, String filePath) {

    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    //Header contentTypeHeader = new Header( "Content-Type", "application/octet-stream" );
    Header contentTypeHeader = new Header( "Content-Type", "multipart/form-data" );
    //Header contentDispositionHeader = new Header( "Content-Disposition", "form-data; name=\"fileUpload\"; filename=\"generated_job.kjb\"" );
    //Header contentDispositionHeader2 = new Header( "Content-Disposition", "form-data; name=\"importDir\"" );
    //Header acceptHeader = new Header( "Accept", "application/json" );
    
    File importFile = new File (filePath);
    //String body = Files.readAllLines( filePath, Charsets.UTF_8 );
    //String body = Files.toString(new File(filePath), Charsets.UTF_8);
    try {
      body = FileUtils.readFileToString( importFile);
    } catch ( IOException e ) {
      LOGGER.error( "Not able to read file!" );
      e.printStackTrace();
    }

    // getting ID for report file
    customResponse =
        ClientFactory.getInstance().sendPostFormData( URL + "/api/repo/files/import", body, "UTF-8", authorizationHeader,
            contentTypeHeader);
  }*/
}

