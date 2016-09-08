package com.pentaho.services.kettle.entries;

import org.apache.log4j.Logger;
import org.pentaho.di.job.entries.abort.JobEntryAbort;

public class AbortEntry extends JobEntryAbort {
  protected static final Logger LOGGER = Logger.getLogger( AbortEntry.class );

  public AbortEntry( String name ) {
    LOGGER.info( "Creating 'Abort' job entry" );
    setName( name );
    setPluginId( "ABORT" );
  }

  public AbortEntry( String name, String message ) {
    this( name );
    setMessageabort( message );
  }
}
