package com.pentaho.qa.sdk.shims;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.version.BuildVersion;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.pentaho.qa.sdk.SDKBaseTest;
import com.pentaho.services.kettle.PDIJob;
import com.qaprosoft.carina.core.foundation.utils.R;

public class ShimsBaseTest extends SDKBaseTest {
  protected static final String PROJECT_ROOT = System.getProperty( "user.dir" );
  protected static final String PENTAHO_HOME = System.getProperty( "pentaho_home" );
  protected static final String PENTAHO_PLUGINS_HOME = PENTAHO_HOME + "/design-tools/data-integration/plugins";

  protected String shimName = "";
  protected boolean shimSecured;

  protected static final String SHIMS_DATA = PROJECT_ROOT + File.separator + "src/test/resources/shims_data";
  //protected static final String SHIMS_DATA = System.getProperty( "SHIMS_DATA" );


  protected static final Logger LOGGER = Logger.getLogger( ShimsBaseTest.class );

  @BeforeClass( )
  @Parameters()
  public void initialize() throws KettleException {

    if ( PENTAHO_HOME == null ) {
      Assert.fail( "PENTAHO_HOME is not declared! Unable to proceed with SHIMS testing!" );
    }
//    if ( SHIMS_DATA == null ) {
//      Assert.failAssert.fail
//    }

    LOGGER.info( "PROJECT_ROOT: " + PROJECT_ROOT );

    LOGGER.info( "PENTAHO_HOME: " + PENTAHO_HOME );
    LOGGER.info( "PENTAHO_PLUGINS_HOME: " + PENTAHO_PLUGINS_HOME );
    LOGGER.info( "SHIMS_DATA: " + SHIMS_DATA );

      // setting path to plugins dir for KettleEnvironment.init command
    System.setProperty( "KETTLE_PLUGIN_BASE_FOLDERS", PENTAHO_PLUGINS_HOME );
      // setting path to dir with karaf for  KettleEnvironment.init command
    System.setProperty( "osx.app.root.dir", PENTAHO_HOME + "/design-tools/data-integration" );

/*      identified shim and secured if it is:
      1. read active.hadoop.configuration property from <PENTAHO_HOME>/server/biserver-ee/pentaho-solutions/system/kettle/plugins/pentaho-big-data-plugin/plugin.properties
      2. read authentication.superuser.provider <PENTAHO_HOME>/server/biserver-ee/pentaho-solutions/system/kettle/plugins/pentaho-big-data-plugin/hadoop-configurations/<active.hadoop.configuration>/config.properties
      3. if authentication.superuser.provider=NO_AUTH then unsecured shim otherwise secured
*/
    shimName = getShimName( PENTAHO_HOME );
    shimSecured = isShimSecured( PENTAHO_HOME, shimName );


    if ( shimSecured ) {
      setSuiteNameAppender( shimName + "/secured" );
    } else {
      setSuiteNameAppender( shimName + "/unsecured" );
    }
      //setting path, which will be used in job
    System.setProperty( "SHIMS_DATA", SHIMS_DATA );
    String testPropertiesLocation = "%s/ClusterConfigs/testdata/%s_%s";

    if ( shimSecured ) {
      testPropertiesLocation = String.format( testPropertiesLocation, SHIMS_DATA, shimName, "secured" );
    } else {
      testPropertiesLocation = String.format( testPropertiesLocation, SHIMS_DATA, shimName, "unsecured" );
    }

    LOGGER.info( "SHIMS_TEST_PROPERTIES: " + testPropertiesLocation );
    System.setProperty( "SHIMS_TEST_PROPERTIES", testPropertiesLocation );

    ShimsClassLoader shimsClassLoader = new ShimsClassLoader();
    shimsClassLoader.populateClasspath();

    // Switch user.dir for pentaho loading
    System.setProperty( "user.dir", PENTAHO_HOME + "/design-tools/data-integration" );

    KettleEnvironment.init();

    // Returning user.dir to normal
    System.setProperty( "user.dir", PROJECT_ROOT );

      // retrieve logging appender
    appender = KettleLogStore.getAppender();
    BuildVersion version = BuildVersion.getInstance();
    LOGGER.info( "SDK build is " + version.getVersion() + version.getRevision() );
  }

  protected void runShimsJobTest( String jobPath ) throws KettleException, IOException {

    LOGGER.info( "Starting Job" );
    jobPath = SHIMS_DATA + File.separator + jobPath;
    PDIJob job = new PDIJob( jobPath, null );
    LogLevel logLevel = LogLevel.valueOf( R.TESTDATA.get( "shim_log_level" ) );
    Job jobExec = job.run( logLevel );

    String logText = getPDILog( jobExec );


    LOGGER.info( jobExec.getResult().getLogText() );
    if ( jobExec.getResult( ).getResult( ) ) { //it return true if job succeed
      LOGGER.info( "Job is succeeded. Number of Errors: " + jobExec.getResult( ).getNrErrors( ) + "!" );
    } else {
      LOGGER.info( "Log from PDI: " + logText );
      Assert.fail( "Job is not succeeded. Number of Errors: " + jobExec.getResult( ).getNrErrors( ) + "!" );
    }
  }

  private String getShimName( String pentahoHome ) {
    // read active.hadoop.configuration property from <PENTAHO_HOME>/server/biserver-ee/pentaho-solutions/system/kettle/plugins/pentaho-big-data-plugin/plugin.properties
    Properties prop = new Properties( );
    InputStream input = null;
    String shim = "";

    try {
      input = new FileInputStream( pentahoHome + "/design-tools/data-integration/plugins/pentaho-big-data-plugin/plugin.properties" );

      // load a properties file
      prop.load( input );

      // get the property value and print it out
      shim = prop.getProperty( "active.hadoop.configuration" );
      LOGGER.info( "Shim: " + shim );

    } catch ( IOException ex ) {
      ex.printStackTrace( );
    } finally {
      if ( input != null ) {
        try {
          input.close( );
        } catch ( IOException e ) {
          e.printStackTrace( );
        }
      }
    }

    return shim;
  }

  private boolean isShimSecured( String pentahoHome, String shim ) {
    /*
     * 2. read authentication.superuser.provider
     * <PENTAHO_HOME>/server/biserver-ee/pentaho-solutions/system/kettle/plugins
     * /pentaho-big-data-plugin/hadoop-configurations/<active.hadoop.configuration>/config.properties 3. if
     * authentication.superuser.provider=NO_AUTH then unsecured shim otherwise secired
     */

    Properties prop = new Properties();
    InputStream input = null;
    boolean secured = false;

    try {

      input =
          new FileInputStream( pentahoHome + "/design-tools/data-integration/plugins/pentaho-big-data-plugin/hadoop-configurations/" + shim + "/config.properties" );

      // load a properties file
      prop.load( input );

      // get the property value and print it out
      secured = !prop.getProperty( "authentication.superuser.provider" ).equalsIgnoreCase( "NO_AUTH" );
      LOGGER.info( shim + " shim is secured: " + secured );

    } catch ( IOException ex ) {
      ex.printStackTrace( );
    } finally {
      if ( input != null ) {
        try {
          input.close( );
        } catch ( IOException e ) {
          e.printStackTrace( );
        }
      }
    }
    return secured;
  }

}
