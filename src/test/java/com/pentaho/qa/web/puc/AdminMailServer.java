package com.pentaho.qa.web.puc;

/**
 * Created by Ihar_Chekan on 8/5/2014.
 */

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.puc.AdministrationPage;
import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;

public class AdminMailServer extends WebBaseTest {

  @Test( )
  @Parameters( { "hostName", "portNumber", "userName", "password", "emailFromAddress", "emailFromName" } )
  public void testSetSMTPSettings( String hostName, String portNumber, String userName, String password,
      String emailFromAddress, String emailFromName ) {
    HomePage homePage = new HomePage( getDriver() );
    AdministrationPage administrationPage = (AdministrationPage) homePage.activateModule( Module.ADMINISTRATION );

    administrationPage.setSMTPSettings( hostName, portNumber, userName, password, emailFromAddress, emailFromName );
  }

}
