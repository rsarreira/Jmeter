package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class LoginPage extends BasePage {

  @FindBy( xpath = "//div[@class='custom-dropdown-label' and contains(., '{L10N:admin}')]" )
  protected ExtendedWebElement adminPage;
  
  @FindBy( id = "j_username" )
  protected ExtendedWebElement userName;

  @FindBy( id = "j_password" )
  protected ExtendedWebElement userPassword;

  //'Login' - {L10N:login}
  // As we don't update lang for the browser Login page is in English!
  @FindBy( xpath = "//button[contains(., 'Login')]" )
  protected ExtendedWebElement btnLogin;

  @FindBy( id = "loginError" )
  protected ExtendedWebElement loginError;

  //'OK'
  @FindBy( xpath = "//button[contains(., '{L10N:btnLabelOK}')]" )
  protected ExtendedWebElement btnOK;

  public LoginPage( WebDriver driver ) {
    super( driver );
    open();

    // isOpened();
  }

  public HomePage login( String user, String password ) {
    type( userName, user );
    type( userPassword, password );
    click( btnLogin, true );

    return new HomePage( driver );
  }

  public boolean isOpened(long timeout) {
    return super.isOpened( userName, timeout);
  }
  
}
