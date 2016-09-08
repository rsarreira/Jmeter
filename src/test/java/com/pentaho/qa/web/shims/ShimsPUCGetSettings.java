package com.pentaho.qa.web.shims;

import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.utils.SpecialKeywords;

import junit.framework.Assert;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Ihar_Chekan on 8/12/2015.
 */

// read active configuration from test.properties file
public class ShimsPUCGetSettings {
  private String shimName = "";

  private String shimSecured = "";

  protected String testPropertiesLocation = "%s/ClusterConfigs/testdata/%s_%s";

  protected static final String PROJECT_ROOT = System.getProperty( "user.dir" );
  protected static final String SHIMS_DATA = PROJECT_ROOT + File.separator + "src/test/resources/shims_data";

  protected static final Logger LOGGER = Logger.getLogger( ShimsPUCGetSettings.class );

  public ShimsPUCGetSettings() {
    shimName = R.TESTDATA.get( "shim" );

    if ( shimName.equalsIgnoreCase( SpecialKeywords.NULL ) ) {
      Assert.fail( "SHIM NAME is not defined in _testdata.properties!" );
    }
    shimSecured = R.TESTDATA.get( "secured" );

    LOGGER.info( "SHIMS_DATA: " + SHIMS_DATA );
    LOGGER.info( "shimName: " + shimName );
    LOGGER.info( "shimSecured: " + shimSecured );

    if ( shimSecured.equals( "secured" ) ) {
      testPropertiesLocation = String.format( testPropertiesLocation, SHIMS_DATA, shimName, "secured" );
    } else {
      testPropertiesLocation = String.format( testPropertiesLocation, SHIMS_DATA, shimName, "unsecured" );
    }

    LOGGER.info( "SHIMS_TEST_PROPERTIES: " + testPropertiesLocation );

  }

  public Map<String, String> getDatabaseSettings( String dbType ) {

    Properties prop = new Properties();
    InputStream input = null;

    Map<String, String> options = new HashMap<String, String>();

    try {

      input = new FileInputStream( testPropertiesLocation + "/test.properties" );

      // load a properties file
      prop.load( input );

      // get the property value, put in map and print it out
      if ( dbType.toLowerCase().contains( "hadoop hive 2" ) ) {
        options.put( "host", prop.getProperty( "hive2_hostname" ) );
        LOGGER.info( "hive2_hostname: " + prop.getProperty( "hive2_hostname" ) );
        options.put( "user", prop.getProperty( "hive2_user" ) );
        LOGGER.info( "hive2_user: " + prop.getProperty( "hive2_user" ) );
        options.put( "password", prop.getProperty( "hive2_password" ) );
        LOGGER.info( "hive2_password: " + prop.getProperty( "hive2_password" )  );
        options.put( "port", prop.getProperty( "hive2_port" ) );
        LOGGER.info( "hive2_port: " + prop.getProperty( "hive2_port" ) );
        options.put( "dbName", prop.getProperty( "hive2_db" ) );
        LOGGER.info( "hive2_db: " + prop.getProperty( "hive2_db" ) );
        options.put( "hive2_option", prop.getProperty( "hive2_option" ) );
        LOGGER.info( "hive2_option: " + prop.getProperty( "hive2_option" ) );
        options.put( "hive2_principal", prop.getProperty( "hive2_principal" ) );
        LOGGER.info( "hive2_principal: " + prop.getProperty( "hive2_principal" ) );
      } else if ( dbType.toLowerCase().contains( "cloudera impala" ) ) {
        options.put( "host", prop.getProperty( "impala_hostname" ) );
        LOGGER.info( "impala_hostname: " + prop.getProperty( "impala_hostname" ) );
        options.put( "user", prop.getProperty( "impala_user" ) );
        LOGGER.info( "impala_user: " + prop.getProperty( "impala_user" ) );
        options.put( "password", prop.getProperty( "impala_password" ) );
        LOGGER.info( "impala_password: " + prop.getProperty( "impala_password" ) );
        options.put( "port", prop.getProperty( "impala_port" ) );
        LOGGER.info( "impala_port: " + prop.getProperty( "impala_port" ) );
        options.put( "dbName", prop.getProperty( "impala_db" ) );
        LOGGER.info( "impala_db: " + prop.getProperty( "impala_db" ) );
        options.put( "impala_KrbRealm", prop.getProperty( "impala_KrbRealm" ) );
        LOGGER.info( "impala_KrbRealm: " + prop.getProperty( "impala_KrbRealm" ) );
        options.put( "impala_KrbHostFQDN", prop.getProperty( "impala_KrbHostFQDN" ) );
        LOGGER.info( "impala_KrbHostFQDN: " + prop.getProperty( "impala_KrbHostFQDN" ) );
        options.put( "impala_KrbServiceName", prop.getProperty( "impala_KrbServiceName" ) );
        LOGGER.info( "impala_KrbServiceName: " + prop.getProperty( "impala_KrbServiceName" ) );
      }

    } catch ( IOException ex ) {
      ex.printStackTrace();
    } finally {
      if ( input != null ) {
        try {
          input.close();
        } catch ( IOException e ) {
          e.printStackTrace();
        }
      }
    }

    // adding hardcoded and common properties to map
    options.put( "dbType", dbType );
    LOGGER.info( "dbType: " + dbType );
    options.put( "accessType", "Native (JDBC)" );

    return options;
  }

  public String getShimName() {
    return shimName;
  }

  public void setShimName( String shimName ) {
    this.shimName = shimName;
  }

  public String getShimSecured() {
    return shimSecured;
  }

  public void setShimSecured( String shimSecured ) {
    this.shimSecured = shimSecured;
  }
}
