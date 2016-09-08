package com.pentaho.services.api.http;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.testng.Assert;

import com.pentaho.services.PentahoUser;

public class Header {
  public enum HeaderType {
    BasicAuthorization
  }

  protected static final Logger LOGGER = Logger.getLogger( Header.class );

  String headerName;

  String headerValue;

  public Header( String headerName, String headerValue ) {
    this.headerName = headerName;
    this.headerValue = headerValue;
    LOGGER.info( "New Header is created. Name: " + headerName + " Value: " + headerValue );

  }

  public Header( HeaderType type, PentahoUser user ) {
    switch ( type ) {
      case BasicAuthorization:
        String auth = user.getName() + ":" + user.getPassword();
        byte[] encoding = Base64.encodeBase64( auth.getBytes() );
        String authStringEnc = new String( encoding );

        this.headerName = "Authorization";
        this.headerValue = "Basic " + authStringEnc;
        break;
      default:
        Assert.fail( "Type '" + type + "' is not  supported! Header can't be created" );
    }

    LOGGER.info( "New Header is created" );

  }

  public String getHeaderName() {
    return headerName;
  }

  public String getHeaderValue() {
    return headerValue;
  }

}
