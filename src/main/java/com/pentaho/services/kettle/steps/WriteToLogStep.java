package com.pentaho.services.kettle.steps;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.writetolog.WriteToLogMeta;

public class WriteToLogStep extends AbstractStep {
  protected static final Logger LOGGER = Logger.getLogger( WriteToLogStep.class );

  public WriteToLogStep( String stepName, String message, int posX, int posY ) {
    LOGGER.info( "Creating WriteToLog step: " + stepName );

    WriteToLogMeta writeToLogMeta = new WriteToLogMeta();
    String writeToLogPluginId = PluginRegistry.getInstance().getPluginId( StepPluginType.class, writeToLogMeta );

    // configure counter options
    writeToLogMeta.setDefault();
    LOGGER.info( "Write Message: " + message );
    writeToLogMeta.setLogMessage( message );
    
    stepMeta = new StepMeta( writeToLogPluginId, stepName, writeToLogMeta );

    // make sure the step appears on the canvas and is properly placed in spoon
    stepMeta.setDraw( true );
    stepMeta.setLocation( posX, posY );

  }

  public WriteToLogStep( HashMap<String, String> args ) {
    this( args.get( "stepName" ), args.get( "message" ), Integer.valueOf( args.get( "x" ) ), Integer.valueOf( args
        .get( "y" ) ) );
  }
}
