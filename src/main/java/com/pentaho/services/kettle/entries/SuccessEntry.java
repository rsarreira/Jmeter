package com.pentaho.services.kettle.entries;

import org.apache.log4j.Logger;
import org.pentaho.di.job.entries.success.JobEntrySuccess;

public class SuccessEntry extends JobEntrySuccess {
  protected static final Logger LOGGER = Logger.getLogger(SuccessEntry.class);

  public SuccessEntry(String name) {
    LOGGER.info( "Creating 'Success' job entry" );
    setName(name);
    setPluginId("SUCCESS");
  }
}
