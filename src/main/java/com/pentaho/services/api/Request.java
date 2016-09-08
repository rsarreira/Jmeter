package com.pentaho.services.api;

import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.sf.json.JSONException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.pentaho.services.BaseObject;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.api.http.CustomResponse;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
//import org.json.JSONException;

public class Request extends BaseObject {
  protected static final Logger LOGGER = Logger.getLogger( Request.class );
  protected final static String URL = Configuration.get( Parameter.URL );

  protected boolean positive;
  PentahoUser user;
  protected String body;
  private String responseBody;

  protected CustomResponse customResponse;

  public Request( PentahoUser user ) {
    super( "" );
    this.user = user;
  }

  public Request( Map<String, String> args ) {
    super( args );

    if ( args.get( "Positive" ) != null && !args.get( "Positive" ).isEmpty() ) {
      this.positive = Boolean.valueOf( args.get( "Positive" ) );
    }

    this.user = new PentahoUser( args.get( "user" ), args.get( "password" ) );
    this.body = args.get( "requestBody" );

    customResponse = null;
  }

  public boolean isPositive() {
    return positive;
  }

  public void setPositive( boolean positive ) {
    this.positive = positive;
  }

  public PentahoUser getUser() {
    return user;
  }

  public void setUser( PentahoUser user ) {
    this.user = user;
  }

  public String getBody() {
    return body;
  }

  public void setBody( String body ) {
    this.body = body;
  }

  public void setResponseBody( CustomResponse customResponse ) {
    this.responseBody = customResponse.getResponseBody();
  }
  
  public String getResponseBody() {
    if (customResponse != null){
      setResponseBody(customResponse);
    }
    return responseBody;
  }

  protected int getStatusCode() {
    return customResponse.getStatusCode();
  }

  public boolean verify( int expectedCode) {
    return verify(expectedCode, "");
  }
  
  public boolean verify( String expectedBody, Boolean isEquals) {
    boolean res = true;
    
    if (isEquals){
     
      if ( !customResponse.getResponseBody().equals( expectedBody ) ) {
        res = false;
        LOGGER.error( "Verify: Response doesn't equal to expected body" );
        softAssert.fail( "Response is not correct for this user: " + customResponse.getResponseBody()
            + "\n Expecting: " + expectedBody );
      } else {
        LOGGER.info( "Verify: Response equals to expected body" );
      }
    } else {
      if ( !customResponse.getResponseBody().contains( expectedBody ) ) {
        res = false;
        LOGGER.error( "Verify: Response doesn't contain expected body" );
        softAssert.fail( "Response is not correct for this user: " + customResponse.getResponseBody()
            + "\n Expecting: " + expectedBody );
      } else {
        LOGGER.info( "Verify: Response contains expected body" );
      }
    }
    return res;
  }
  
  public boolean verify( int expectedCode, String expectedBody ) {

    boolean res = true;
    if ( getStatusCode() != Integer.valueOf( expectedCode ) ) {
      res = false;
      softAssert.fail( "Status code is not correct! " + getStatusCode() + " instead of " + expectedCode );
    } else {
      LOGGER.info( "Verify: Response status code is similar to expected." );
    }

    // Skip body verification if nothing is transfered
    if ( !expectedBody.isEmpty() ) {
      if ( positive ) {
        res = verify( expectedBody, true );
      } else {
        res = verify( expectedBody, false );
      }
    }

    return res;
  }

  protected List<String> getListFromJSON( JSONObject json, String keyObjectName, String keyName ) {
    List<String> values = new ArrayList<String>();

    JSONArray jsonObjects;
    try {
      jsonObjects = json.getJSONArray( keyObjectName );

      for ( int i = 0; i < jsonObjects.length(); i++ ) {
        JSONObject obj = jsonObjects.getJSONObject( i );
        String value = obj.getString( keyName );
        values.add( value );
      }
    } catch ( JSONException e ) {
      LOGGER.error( "Faled to work with JSON. JSONException arises." );
      e.printStackTrace();
    }

    return values;
  }

  protected JSONObject convertToJSON( String str ) {

    JSONObject json = null;
    try {
      try {
        json = new JSONObject( str );
      } catch ( ParseException e ) {
        e.printStackTrace();
      }
    } catch ( JSONException e ) {
      LOGGER.error( "Faled to work with JSON. JSONException arises." );
      // e.printStackTrace();
    }
    return json;
  }

  public String getXMLvalue( String inputString, String key ) {

    try {
      DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
      InputSource is = new InputSource();
      is.setCharacterStream( new StringReader( inputString ) );
      Document doc = db.parse( is );
      Element element = doc.getDocumentElement();
      NodeList node = element.getElementsByTagName( key );
      Element line = (Element) node.item( 0 );
      String value = getCharacterDataFromXMLElement( line );

      return value;
    } catch ( Exception e ) {
      LOGGER.error( "Failed to parse data from XML", e );
      e.printStackTrace();
    }
    return null;
  }

  private String getCharacterDataFromXMLElement( Element e ) {
    Node child = e.getFirstChild();
    if ( child instanceof CharacterData ) {
      CharacterData cd = (CharacterData) child;
      return cd.getData();
    }
    return "";
  }
}
