package com.pentaho.qa.sdk;

import org.apache.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.version.BuildVersion;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.pentaho.services.PentahoUser;

public class SDKPluginBaseTest extends SDKBaseTest {

  protected static final Logger LOGGER = Logger.getLogger( SDKPluginBaseTest.class );
  protected static final String PENTAHO_HOME = System.getProperty( "pentaho_home" );
  protected static final String PENTAHO_KETTLE_HOME = PENTAHO_HOME + "/design-tools/data-integration";

  protected static final String userDir = System.getProperty( "user.dir" );

  public static PentahoUser adminUser = null;

  @Override( )
  @BeforeClass( )
  public void initialize() throws KettleException {
    System.setProperty( "KETTLE_PLUGIN_BASE_FOLDERS", PENTAHO_KETTLE_HOME + "/plugins" );
    System.setProperty( "user.dir", PENTAHO_KETTLE_HOME );
    // System.setProperty( "KETTLE_LAZY_REPOSITORY", "false" );

    if ( PENTAHO_HOME == null ) {
      Assert.fail( "PENTAHO_HOME is not declared! Unable to proceed with testing!" );
    }

    KettleEnvironment.init();

    // retrieve logging appender
    appender = KettleLogStore.getAppender();
    BuildVersion version = BuildVersion.getInstance();
    LOGGER.info( "SDK build is " + version.getVersion() + version.getRevision() );
  }

  @BeforeClass( )
  @Parameters( { "user", "password" } )
  public void initializeServices( String userName, String password ) {
    adminUser = new PentahoUser( userName, password, false );
  }

  @AfterClass( )
  public void deinitialize() {
    System.setProperty( "user.dir", userDir );
  }

  public static String getUserdir() {
    return userDir;
  }

}
