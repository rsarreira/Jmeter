package com.pentaho.qa.listener;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.qaprosoft.carina.core.foundation.listeners.UITestListener;
import com.qaprosoft.carina.core.foundation.utils.R;
import com.qaprosoft.carina.core.foundation.webdriver.DriverFactory;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;

//VALID location for pentaho listener is under test folder otherwise CI doesn't work

public class PentahoListener extends UITestListener {
  private static final Logger LOGGER = Logger.getLogger( PentahoListener.class );

  @Override
  public void onStart( ITestContext testContext ) {
  }

  @Override
  public void onTestStart( ITestResult result ) {
  }

  @Override
  public void onTestSkipped( ITestResult result ) {
  }

  @Override
  public void onTestSuccess( ITestResult result ) {
  }

  @Override
  public void onTestFailure( ITestResult result ) {
    // super.onTestFailure(result);

    if ( !R.CONFIG.getBoolean( "recovery_mode" ) ) {
      LOGGER.info( "Recovery mode is OFF. Relogin is disabled." );
      // recovery mode is off. there
      return;
    }
    try {
      // re-login into application
      // TODO: add current Application into PentahoUser class to re-login into valid project automatically
      long threadId = Thread.currentThread().getId();
      WebDriver drv = DriverPool.getDriverByThread( threadId );

      if (drv != null) {
        drv.quit();
      }
      
      drv = DriverFactory.create( result.getTestContext().getSuite().getName() );

      DriverPool.registerDriver2Thread( drv, threadId );

      if ( WebBaseTest.webUser == null ) {
        LOGGER.warn( "Unable to relogon as current user is unknown. Login page is opened." );
        return;
      } else if (!WebBaseTest.webUser.isLogged()){
        LOGGER.warn( "Unable to relogon as current user is not logged yet." );
        return;
        
      }

      String user = WebBaseTest.webUser.getName();
      String password = WebBaseTest.webUser.getPassword();
      LOGGER.info( String.format( "Relogin into PUC application using %s/%s.", user, password ) );

      // relogin using static webUser instance
      HomePage homePage = WebBaseTest.webUser.login();
      if ( !homePage.isLogged( user ) ) {
        throw new RuntimeException( "Incorrect user is logged: '" + user + "'!" );
      }

    } catch ( Exception e ) {
      LOGGER.error( "Issue discovered during relogon: " + e.getMessage() );
      e.printStackTrace();
    }
  }

}
