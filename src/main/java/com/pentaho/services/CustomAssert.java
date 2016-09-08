package com.pentaho.services;

import java.util.List;

import org.apache.log4j.Logger;
import org.testng.Assert;

import com.qaprosoft.carina.core.foundation.report.spira.Spira;

public class CustomAssert extends Assert {

  
  protected static final Logger LOGGER = Logger.getLogger(CustomAssert.class);
  
  /**
   * Generate assert if current spira step is the same as spiraStepId.
   * 
   * @param spiraStepId
   * 
   */

  public static void fail( String spiraStepId, String message ) {
    List<String> steps = Spira.getSteps();
    if (steps.contains( spiraStepId )) {
      Assert.fail( String.format( "TS0%s: %s", spiraStepId, message ));
    } else {
      LOGGER.warn(String.format( "Do not generate assert as provided spiraStepId '%s' is not current step!", spiraStepId));
    }
  }
}
