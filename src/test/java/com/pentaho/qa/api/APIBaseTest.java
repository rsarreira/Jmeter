package com.pentaho.qa.api;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import com.pentaho.services.PentahoUser;
import com.qaprosoft.carina.core.foundation.APITest;

public class APIBaseTest extends APITest {
  protected static final Logger LOGGER = Logger.getLogger( APIBaseTest.class );

  public static PentahoUser adminUser = null;

  @BeforeClass( )
  @Parameters( { "user", "password" } )
  public void initializeServices( String userName, String password ) {
    adminUser = new PentahoUser( userName, password, false );
  }

}
