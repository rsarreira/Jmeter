package com.pentaho.services.kettle.entries;

import org.apache.log4j.Logger;
import org.pentaho.di.job.entries.special.JobEntrySpecial;

public class StartEntry extends JobEntrySpecial {
  protected static final Logger LOGGER = Logger.getLogger(StartEntry.class);

  public StartEntry(String name) {
    super(name, true, false);
    LOGGER.info( "Creating 'Start' job entry" );
    setPluginId("SPECIAL");
  }
  //TODO: add additional parameters like Repeat and Scheduling
}
