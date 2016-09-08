package com.pentaho.services.kettle.steps;

import org.apache.log4j.Logger;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.rowgenerator.RowGeneratorMeta;

public class RowGeneratorStep extends AbstractStep {
  protected static final Logger LOGGER = Logger.getLogger(RowGeneratorStep.class);  
  
  public RowGeneratorStep(String stepName, String rowLimit, int numRows, String[] fieldNames, String[] fieldTypes,
      String[] fieldValues, int posX, int posY) {
    LOGGER.info( "Creating RowGeneratorStep step: " + stepName );
    RowGeneratorMeta rowGeneratorMeta = new RowGeneratorMeta();
    String rowGeneratorPluginId = PluginRegistry.getInstance().getPluginId(StepPluginType.class, rowGeneratorMeta);

    rowGeneratorMeta.setDefault();
    LOGGER.info( "Row limit: " + rowLimit );
    rowGeneratorMeta.setRowLimit(rowLimit);
    rowGeneratorMeta.allocate(numRows);
    LOGGER.info( "Following fields will be added: " + fieldNames + ", fieldTypes: " + fieldTypes +", values: " + fieldValues);
    rowGeneratorMeta.setFieldName(fieldNames);
    rowGeneratorMeta.setFieldType(fieldTypes);
    rowGeneratorMeta.setValue(fieldValues);
    
    stepMeta = new StepMeta(rowGeneratorPluginId, stepName, rowGeneratorMeta);

    // make sure the step appears on the canvas and is properly placed in spoon
    stepMeta.setDraw(true);
    stepMeta.setLocation(posX, posY);

    }
}
