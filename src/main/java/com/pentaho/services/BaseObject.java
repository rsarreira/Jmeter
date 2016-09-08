package com.pentaho.services;

import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.asserts.SoftAssert;

import com.qaprosoft.carina.core.foundation.retry.RetryCounter;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;
import com.qaprosoft.carina.core.foundation.utils.ParameterGenerator;
import com.qaprosoft.carina.core.foundation.utils.naming.TestNamingUtil;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;

public abstract class BaseObject implements Cloneable {

  protected static final Logger LOGGER = Logger.getLogger( BaseObject.class );

  protected static final long IMPLICIT_TIMEOUT = Configuration.getLong( Parameter.IMPLICIT_TIMEOUT );
  protected static final long EXPLICIT_TIMEOUT = Configuration.getLong( Parameter.EXPLICIT_TIMEOUT );

  protected boolean addCounterToName = true;

  public static final String ARG_NAME = "Name";
  public static final String ARG_ID = "Id";
  public static final String AUTO_ID = "auto";
  public static final int INVALID_ID = -1;

  public SoftAssert softAssert = new SoftAssert();

  protected String name;
  protected int id;

  public BaseObject( String nameValue ) {
    this( nameValue, String.valueOf( INVALID_ID ) );
  }
  
  public BaseObject( String nameValue, String id ) {
    this.name = nameValue;

    if ( id == null ) {
      this.id = INVALID_ID;
    } else if ( id.isEmpty() ) {
      this.id = INVALID_ID;
    } else if (id.equalsIgnoreCase( AUTO_ID )){
      this.id = ObjectPool.getUniqueId();
    } else {
      this.id = Integer.valueOf( id );
    }

    String uuid = ParameterGenerator.getUUID();
    if ( addCounterToName && nameValue != null && nameValue.contains( uuid ) ) {
      // QUALITY-1032 turn on adding retry appender to the name
      String test = TestNamingUtil.getTestNameByThread();
      int count = RetryCounter.getRunCount( test );
      if ( count > 0 ) {
        this.name = nameValue + "_" + count;
      }
    }
  }


  public BaseObject( Map<String, String> args ) {
    this( args.get( ARG_NAME ), args.get( ARG_ID ) );
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public int getId() {
    return id;
  }

  public void setId( int id ) {
    this.id = id;
  }
  
  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( BaseObject copy ) {
    if ( copy.getName() != null ) {
      this.name = copy.getName();
    }
  }

  public WebDriver getDriver() {
    WebDriver drv = DriverPool.getDriverByThread();
    if ( drv == null ) {
      LOGGER.warn( "Unable to find driver in pool. Looking in extraDriver pool..." );
      drv = DriverPool.getExtraDriverByThread();
      if ( drv == null ) {
        LOGGER.error( "Unable to find valid driver in ExtraDriverPool as well!" );
      }
    }
    return drv;
  }

  public boolean isValid() {
    return id != INVALID_ID;
  }

}
