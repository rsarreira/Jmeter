package com.pentaho.services.api.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpHost;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;

public class Client {

  private static final Logger LOGGER = Logger.getLogger( Client.class );

  private CloseableHttpClient client;

  public Client() {
    client = HttpClientBuilder.create().build();

  }

  public Client( String proxyHost, int proxyPort, ProxyType proxyType ) {
    HttpClientBuilder clientBuilder = HttpClientBuilder.create();
    HttpHost proxy = new HttpHost( proxyHost, proxyPort, proxyType.getName() );
    clientBuilder.setProxy( proxy );
    client = clientBuilder.build();

  }

  /**
   * Send Get request
   *
   * @param url
   *          Request host
   * @param headers
   *          Array {@link com.pentaho.services.http.Header} {Optional}
   * @throws IOException
   * @throws Exception
   */
  public CustomResponse sendGet( String url, Header... headers ) {
    try {
      // URLDecoder.decode(url);

      HttpGet request = new HttpGet( url );

      for ( Header header : headers ) {
        request.addHeader( header.getHeaderName(), header.getHeaderValue() );
      }
      CloseableHttpResponse response = client.execute( request );

      LOGGER.info( "\nSending 'GET' request to URL : " + url );
      LOGGER.info( "Response Code : " + response.getStatusLine().getStatusCode() );

      BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

      StringBuffer result = new StringBuffer();
      String line = "";
      while ( ( line = rd.readLine() ) != null ) {
        result.append( line );
      }
      response.close();

      CustomResponse customResponce = new CustomResponse();
      customResponce.setHeaders( response.getAllHeaders() );
      customResponce.setResponseBody( result.toString() );
      customResponce.setStatusCode( response.getStatusLine().getStatusCode() );
      customResponce.setUrl( request.getURI().toURL().toString() );
      return customResponce;

    } catch ( Exception e ) {
      throw new RuntimeException( "Unable to perfom get request due: " + e.getMessage() );
    }
  }

  /**
   * @param url
   *          Request host
   * @param body
   *          Request body
   * @param encoding
   *          encoding type
   * @param headers
   *          Array {@link com.pentaho.services.http.Header} {Optional}
   * @throws Exception
   */

  public CustomResponse sendPost( String url, String body, String encoding, Header... headers ) {

    try {
      HttpPost post = new HttpPost( url );

      for ( Header header : headers ) {
        post.setHeader( header.getHeaderName(), header.getHeaderValue() );
      }
      post.setEntity( new StringEntity( body, encoding ) );
      CloseableHttpResponse response = client.execute( post );
      LOGGER.info( "Sending 'POST' request to URL : " + url );
      LOGGER.info( "Response Code : " + response.getStatusLine().getStatusCode() );

      BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

      StringBuffer result = new StringBuffer();
      String line = "";
      while ( ( line = rd.readLine() ) != null ) {
        result.append( line );
      }
      response.close();

      CustomResponse customResponce = new CustomResponse();
      customResponce.setHeaders( response.getAllHeaders() );
      customResponce.setResponseBody( result.toString() );
      customResponce.setRequestBody( body.toString() );
      customResponce.setStatusCode( response.getStatusLine().getStatusCode() );
      customResponce.setUrl( post.getURI().toURL().toString() );
      return customResponce;

    } catch ( Exception e ) {
      throw new RuntimeException( "Unable to perfom get request due: " + e.getMessage() );
    }

  }
  
  /*public CustomResponse sendPostFormData( String url, String body, String encoding, Header... headers ) {
    //TODO: finish this method or delete it at all
    try {
      
      HttpPost post = new HttpPost(url);
      for ( Header header : headers ) {
        post.setHeader( header.getHeaderName(), header.getHeaderValue() );
      }
      File file = new File("D:/JimMy/1/J1.kjb");
      FileBody fileBody = new FileBody(file);
      
      MultipartEntityBuilder builder = MultipartEntityBuilder.create();
      builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
      builder.addPart("fileUpload", fileBody);
      builder.addPart("overwriteFile", new StringBody("true", ContentType.MULTIPART_FORM_DATA));
      builder.addPart("logLevel", new StringBody("TRACE", ContentType.MULTIPART_FORM_DATA));
      builder.addPart("retainOwnership", new StringBody("true", ContentType.MULTIPART_FORM_DATA));
      builder.addPart("fileNameOverride", new StringBody(file.getName(), ContentType.MULTIPART_FORM_DATA));
      builder.addPart("importDir", new StringBody("/public", ContentType.MULTIPART_FORM_DATA));
      
      HttpEntity entity = builder.build();
      //
      post.setEntity(entity);
      CloseableHttpResponse response = client.execute( post );
      LOGGER.info( "Sending 'POST' request to URL : " + url );
      LOGGER.info( "Response Code : " + response.getStatusLine().getStatusCode() );

      BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

      StringBuffer result = new StringBuffer();
      String line = "";
      while ( ( line = rd.readLine() ) != null ) {
        result.append( line );
      }
      response.close();

      CustomResponse customResponce = new CustomResponse();
      customResponce.setHeaders( response.getAllHeaders() );
      customResponce.setResponseBody( result.toString() );
      customResponce.setRequestBody( body.toString() );
      customResponce.setStatusCode( response.getStatusLine().getStatusCode() );
      customResponce.setUrl( post.getURI().toURL().toString() );
      return customResponce;
      
    } catch ( Exception e ) {
      throw new RuntimeException( "Unable to perfom get request due: " + e.getMessage() );
    }
  }*/

  /**
   * @param url
   *          Request host
   * @param body
   *          Request body
   * @param encoding
   *          encoding type
   * @param headers
   *          Array {@link com.pentaho.services.http.Header} {Optional}
   * @throws Exception
   */

  public CustomResponse sendDelete( String url, String body, String encoding, Header... headers ) {

    try {
      HttpDeleteWithBody delete = new HttpDeleteWithBody( url );

      for ( Header header : headers ) {
        delete.setHeader( header.getHeaderName(), header.getHeaderValue() );
      }
      delete.setEntity( new StringEntity( body, encoding ) );
      CloseableHttpResponse response = client.execute( delete );
      LOGGER.info( "Sending 'POST' request to URL : " + url );
      LOGGER.info( "Response Code : " + response.getStatusLine().getStatusCode() );

      BufferedReader rd = new BufferedReader( new InputStreamReader( response.getEntity().getContent() ) );

      StringBuffer result = new StringBuffer();
      String line = "";
      while ( ( line = rd.readLine() ) != null ) {
        result.append( line );
      }
      response.close();

      CustomResponse customResponce = new CustomResponse();
      customResponce.setHeaders( response.getAllHeaders() );
      customResponce.setResponseBody( result.toString() );
      customResponce.setRequestBody( body.toString() );
      customResponce.setStatusCode( response.getStatusLine().getStatusCode() );
      customResponce.setUrl( delete.getURI().toURL().toString() );
      return customResponce;

    } catch ( Exception e ) {
      throw new RuntimeException( "Unable to perfom get request due: " + e.getMessage() );
    }

  }

  public void close() {
    try {
      this.client.close();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
  }

}
