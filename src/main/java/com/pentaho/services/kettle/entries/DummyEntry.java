package com.pentaho.services.kettle.entries;

import org.apache.log4j.Logger;
import org.pentaho.di.job.entries.special.JobEntrySpecial;

public class DummyEntry extends JobEntrySpecial {
  protected static final Logger LOGGER = Logger.getLogger(StartEntry.class);

  public DummyEntry(String name) {
    super(name, false, true);
    LOGGER.info( "Creating 'Dummy' job entry" );
    setPluginId("SPECIAL");
  }
}
