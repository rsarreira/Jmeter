package com.pentaho.services.kettle.steps;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.dummytrans.DummyTransMeta;

public class DummyStep extends AbstractStep {
  protected static final Logger LOGGER = Logger.getLogger(DummyStep.class);
  
  public DummyStep(int posX, int posY) {
    LOGGER.info( "Creating Dummy step" );
    DummyTransMeta dummyMeta = new DummyTransMeta();
    String dummyPluginId = PluginRegistry.getInstance().getPluginId(StepPluginType.class, dummyMeta);
    
    stepMeta = new StepMeta(dummyPluginId, "Dummy", dummyMeta);
    
    // make sure the step appears alright in spoon
    stepMeta.setDraw(true);
    stepMeta.setLocation(posX, posY);
  }
    
  public DummyStep(HashMap<String, String> args) {
    this(Integer.valueOf( args.get( "x" ) ), Integer.valueOf( args.get( "y" ) ));
  }
}
