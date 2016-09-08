package com.pentaho.services.kettle.steps;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.addsequence.AddSequenceMeta;

public class AddSequenceStep extends AbstractStep {
  protected static final Logger LOGGER = Logger.getLogger( AddSequenceStep.class );

  public AddSequenceStep( String stepName, String valueName, String counterName, long startAt, long maxValue,
      long increment, int posX, int posY ) {
    LOGGER.info( "Creating AddSequenceStep step: " + stepName );
    AddSequenceMeta addSequenceMeta = new AddSequenceMeta();
    String addSequencePluginId = PluginRegistry.getInstance().getPluginId( StepPluginType.class, addSequenceMeta );

    // configure counter options
    addSequenceMeta.setDefault();
    LOGGER.info( "Value Name: " + valueName );
    addSequenceMeta.setValuename( valueName );
    LOGGER.info( "Counter Name: " + counterName );
    addSequenceMeta.setCounterName( counterName );
    LOGGER.info( "Starts at: " + startAt );
    addSequenceMeta.setStartAt( startAt );
    LOGGER.info( "Max value: " + maxValue );
    addSequenceMeta.setMaxValue( maxValue );
    LOGGER.info( "Increment by: " + increment );
    addSequenceMeta.setIncrementBy( increment );

    stepMeta = new StepMeta( addSequencePluginId, stepName, addSequenceMeta );

    // make sure the step appears on the canvas and is properly placed in
    // spoon
    stepMeta.setDraw( true );
    stepMeta.setLocation( posX, posY );

  }

  public AddSequenceStep( HashMap<String, String> args ) {
    this( args.get( "stepName" ), args.get( "valueName" ), args.get( "counterName" ), Long.valueOf( args.get(
        "startAt" ) ), Long.valueOf( args.get( "maxValue" ) ), Long.valueOf( args.get( "increment" ) ), Integer.valueOf(
            args.get( "posX" ) ), Integer.valueOf( args.get( "posY" ) ) );
  }

}
