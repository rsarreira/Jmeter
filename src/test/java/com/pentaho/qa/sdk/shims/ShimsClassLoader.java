package com.pentaho.qa.sdk.shims;


import org.pentaho.commons.launcher.config.Configuration;
import org.pentaho.commons.launcher.config.Parameters;
import org.pentaho.commons.launcher.util.FileUtil;
import org.pentaho.di.core.exception.KettleException;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Ihar_Chekan on 10/21/2015.
 */
public class ShimsClassLoader extends ShimsBaseTest {


  final File appDir = new File( PENTAHO_HOME + "/design-tools/data-integration/launcher" );

  final File configurationFile = new File( appDir, "launcher.properties" );

  Properties configProperties = new Properties( );

  final String[] args = new String[]{"-Dhttps.protocols=TLSv1,TLSv1.1,TLSv1.2"};

  Parameters parameters = Parameters.fromArgs( args, System.err );

  public void populateClasspath( ) throws KettleException {

    try {
      configProperties.load( new FileReader( configurationFile ) );
    } catch ( Exception e ) {
      LOGGER.info( "launcher.properties file was not loaded: " + e );
    }

    Configuration configuration = Configuration.create( configProperties, appDir, parameters );
    if ( configuration.isUninstallSecurityManager( ) ) {
      System.setSecurityManager( null );
    }

    for ( Map.Entry<String, String> systemProperty : configuration.getSystemProperties( ).entrySet( ) ) {
      System.setProperty( systemProperty.getKey( ), systemProperty.getValue( ) );
    }

    final List<URL> jars = FileUtil.populateClasspath( configuration.getClasspath( ), appDir, System.err );
    jars.addAll( FileUtil.populateLibraries( configuration.getLibraries(), appDir, System.err ) );
    final URL[] classpathEntries = (URL[]) jars.toArray( new URL[jars.size( )] );
    final ClassLoader cl = new URLClassLoader( classpathEntries );
    Thread.currentThread( ).setContextClassLoader( cl );

  }
}
