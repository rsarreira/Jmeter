package com.pentaho.qa.web.puc;

import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;

public class PUC_CI extends WebBaseTest {

  @Test( )
  @Parameters( { "validCredentials" } )
  public void testLogin( @Optional( "true" ) boolean validCredentials ) {

    HomePage homePage = webUser.login();

    if ( validCredentials ) {
      Assert.assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
    }
  }
}
