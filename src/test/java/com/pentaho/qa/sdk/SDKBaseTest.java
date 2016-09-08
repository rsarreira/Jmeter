package com.pentaho.qa.sdk;

import org.apache.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.core.logging.LoggingObjectInterface;
import org.pentaho.di.version.BuildVersion;
import org.testng.annotations.BeforeClass;
import org.testng.asserts.SoftAssert;

import com.qaprosoft.carina.core.foundation.APITest;

public class SDKBaseTest extends APITest {
  protected static final Logger LOGGER = Logger.getLogger( SDKBaseTest.class );
  public SoftAssert softAssert = new SoftAssert();
  
  protected LoggingBuffer appender;

  @BeforeClass( )
  public void initialize() throws KettleException {
    KettleEnvironment.init();

    // retrieve logging appender
    appender = KettleLogStore.getAppender();
    BuildVersion version = BuildVersion.getInstance();
    LOGGER.info( "SDK build is " + version.getVersion() + version.getRevision() );
  }

  public String getPDILog( LoggingObjectInterface obj ) {
    String logText = appender.getBuffer( obj.getLogChannelId(), false ).toString();
    LOGGER.info( "PDI log of executed job is:\n" + logText );
    return logText;
  }

}
