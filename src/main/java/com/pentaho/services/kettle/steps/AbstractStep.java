package com.pentaho.services.kettle.steps;

import org.pentaho.di.trans.step.StepMeta;

public class AbstractStep {

  protected StepMeta stepMeta;

  public StepMeta getStepMeta() {
    return stepMeta;
  }

  public void setStepMeta( StepMeta stepMeta ) {
    this.stepMeta = stepMeta;
  }

}
