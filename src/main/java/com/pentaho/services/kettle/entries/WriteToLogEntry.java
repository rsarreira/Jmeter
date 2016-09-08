package com.pentaho.services.kettle.entries;

import org.apache.log4j.Logger;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.entries.writetolog.JobEntryWriteToLog;

public class WriteToLogEntry extends JobEntryWriteToLog {

  protected static final Logger LOGGER = Logger.getLogger(WriteToLogEntry.class);

  public WriteToLogEntry(String name, String subject, String message) {
    this(name, subject, message, LogLevel.BASIC);
  }
  
  public WriteToLogEntry(String name, String subject, String message, LogLevel loglevel) {
    //super();
    LOGGER.info( "Creating 'Write to log' job entry" );
    
    setName(name);
    setLogSubject(subject);
    setLogMessage(message);
    setEntryLogLevel( loglevel );
    setPluginId("WRITE_TO_LOG");
  }
}
