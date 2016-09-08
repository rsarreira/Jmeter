package com.pentaho.services.api;

import java.util.Map;

import org.apache.log4j.Logger;

import com.pentaho.services.PentahoUser;
import com.pentaho.services.api.http.ClientFactory;
import com.pentaho.services.api.http.Header;
import com.pentaho.services.api.http.Header.HeaderType;

public class CarteRequest extends Request {
  protected static final Logger LOGGER = Logger.getLogger( CarteRequest.class );

  public CarteRequest( PentahoUser user ) {
    super( user );
  }

  public CarteRequest( Map<String, String> args ) {
    super( args );
  }

  // TODO: Optimize methods (initially it's created with a lack of time)

  public void root() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/", authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void status() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance()
    // .sendGet( URL + "/kettle/status/?xml=Y", authorizationHeader );
        .sendGet( URL + "/kettle/status/", authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void getSlaves() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/kettle/getSlaves/", authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void nextSequence( String name ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/kettle/nextSequence/?name=" + name, authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void listSocket( String host ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/kettle/listSocket/?host=" + host, authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void registerSlave() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "text/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/registerSlave/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void allocateSocket( String startPort, String host, String clusterId, String transName, String sourceSlave,
      String sourceStep, String sourceCopy, String targetSlave, String targetStep, String targetCopy ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet(
            URL + "/kettle/allocateSocket/" + "?xml=Y&rangeStart=" + startPort + "&host=" + host + "&id=" + clusterId
                + "&trans=" + transName + "&sourceSlave=" + sourceSlave + "&sourceStep=" + sourceStep + "&sourceCopy="
                + sourceCopy + "&targetSlave=" + targetSlave + "&targetStep=" + targetStep + "&targetCopy="
                + targetCopy, authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void stopCarte() {
    stopCarte( URL );
  }

  public void stopCarte( String URL ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse = ClientFactory.getInstance().sendGet( URL + "/kettle/stopCarte", authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void addTrans() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "text/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/addTrans/?xml=Y", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void transImage( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/kettle/transImage?name=" + transName, authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void transStatus( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/kettle/transStatus?name=" + transName + "&xml=Y",
            authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void prepareExec( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    customResponse =
        ClientFactory.getInstance().sendGet( URL + "/kettle/prepareExec?name=" + transName + "&xml=Y",
            authorizationHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void startExec( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/startExec/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void startTrans( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/startTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void runTrans( String transPath, String transName, String logLevel ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "trans=" + transPath.replace( "/", "%2F" ) + transName + "&level=" + logLevel;
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/runTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void executeTrans( String repoName, String repoUser, String repoPassword, String transPath, String transName,
      String logLevel ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "user=" + repoUser + "&pass=" + repoPassword + "&trans=" + transPath.replace( "/", "%2F" )
        + transName + "&level=" + logLevel + "&rep=" + repoName;
    
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/executeTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void pauseTrans( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/pauseTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void stopTrans( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/stopTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void removeTrans( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/removeTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void cleanupTrans( String transName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + transName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/cleanupTrans/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void sniffStep( String transName, String stepName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "trans=" + transName + "&step=" + stepName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/sniffStep/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void addJob() {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "text/xml" );
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/addJob/?xml=Y", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void jobImage( String jobName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + jobName;
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/jobImage/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void jobStatus( String jobName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + jobName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/jobStatus/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void runJob( String jobPath, String jobName, String logLevel ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "job=" + jobPath.replace( "/", "%2F" ) + jobName + "&level=" + logLevel;
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/runJob/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void executeJob( String repoName, String repoUser, String repoPassword, String jobPath, String jobName,
      String logLevel ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body ="user=" + repoUser + "&pass=" + repoPassword + "&job=" + jobPath.replace( "/", "%2F" ) 
        + jobName + "&level=" + logLevel + "&rep=" + repoName;
    
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/executeJob/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void startJob( String jobName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + jobName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/startJob/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void stopJob( String jobName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + jobName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/stopJob/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

  public void removeJob( String jobName ) {
    Header authorizationHeader = new Header( HeaderType.BasicAuthorization, user );
    Header contentTypeHeader = new Header( "Content-Type", "application/x-www-form-urlencoded" );
    body = "name=" + jobName + "&xml=Y";
    customResponse =
        ClientFactory.getInstance().sendPost( URL + "/kettle/removeJob/", body, "UTF-8", authorizationHeader,
            contentTypeHeader );

    LOGGER.info( "Status code=" + customResponse.getStatusCode() );
    LOGGER.info( "Response Body is: " + customResponse.getResponseBody() );
  }

}
