package com.pentaho.services.api.http;

import org.apache.http.Header;

public class CustomResponse {

  private String Url;

  private String requestBody;

  private String responseBody;

  private org.apache.http.Header[] headers;

  private int statusCode;

  public String getUrl() {
    return Url;
  }

  public void setUrl( String url ) {
    Url = url;
  }

  public String getRequestBody() {
    return requestBody;
  }

  public void setRequestBody( String requestBody ) {
    this.requestBody = requestBody;
  }

  public String getResponseBody() {
    return responseBody;
  }

  public void setResponseBody( String responsBody ) {
    this.responseBody = responsBody;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public void setHeaders( Header[] headers ) {
    this.headers = headers;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
  }

}
