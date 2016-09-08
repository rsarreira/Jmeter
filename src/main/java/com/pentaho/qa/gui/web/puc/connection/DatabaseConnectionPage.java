package com.pentaho.qa.gui.web.puc.connection;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.google.common.base.Strings;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.services.CustomAssert;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DatabaseConnectionPage extends BasePage {

  // 'Database Connection'
  @FindBy( xpath = "//div[text()='{L10N:DatabaseDialog.Shell.title}']" )
  protected ExtendedWebElement txtDatabaseConnection;

  @FindBy( id = "connection-name-text" )
  protected ExtendedWebElement txtName;

  @FindBy( xpath = "//div[text()='%s']" )
  protected ExtendedWebElement txtDatabaseType;

  @FindBy(
      xpath = "//*[@id='connection-type-list']//*[@class='custom-list-item custom-list-item-selected']//div[@class='gwt-Label']" )
  protected ExtendedWebElement selectedDatabaseType;

  @FindBy( xpath = "//div[text()='%s']" )
  protected ExtendedWebElement txtAccessType;

  @FindBy(
      xpath = "//*[@id='access-type-list']//*[@class='custom-list-item custom-list-item-selected']//div[@class='gwt-Label']" )
  protected ExtendedWebElement selectedAccessType;

  @FindBy( id = "server-host-name-text" )
  protected ExtendedWebElement txtHostName;

  @FindBy( id = "database-name-text" )
  protected ExtendedWebElement txtDbName;

  // it should be the same as dbName
  // @FindBy(id = "database-name-text")
  // protected ExtendedWebElement txtSID;

  @FindBy( id = "port-number-text" )
  protected ExtendedWebElement txtDbPort;

  @FindBy( id = "custom-url-text" )
  protected ExtendedWebElement txtCustomConnectionURL;

  @FindBy( id = "custom-driver-class-text" )
  protected ExtendedWebElement txtCustomDriverName;

  @FindBy( id = "username-text" )
  protected ExtendedWebElement txtUser;

  @FindBy( id = "password-text" )
  protected ExtendedWebElement txtPassword;

  @FindBy( id = "data-tablespace-text" )
  protected ExtendedWebElement txtDataTablespace;

  @FindBy( id = "index-tablespace-text" )
  protected ExtendedWebElement txtIndexTablespace;

  @FindBy( xpath = "//span[@id='decimal-separator-check']/input" )
  public ExtendedWebElement checkDecimalSeparator;

  @FindBy( id = "test-button" )
  protected ExtendedWebElement btnTest;

  @FindBy( id = "general-datasource-window_accept" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "general-datasource-window_ cancel" )
  protected ExtendedWebElement btnCancel;

  // Connection to database [ {0} ] failed
  // Connection to database [ {0} ] succeeded
  private final static String CONNECTION_FAILED = L10N.getText( "ConnectionServiceImpl.ERROR_0009_CONNECTION_FAILED" );
  private final static String CONNECTION_SUCCEED = L10N.getText( "ConnectionServiceImpl.INFO_0001_CONNECTION_SUCCEED" );

  // div[text()='ConnectionServiceImpl.ERROR_0009 - Connection to database [
  // %s ] failed']
  @FindBy( xpath = "//div[contains(text(),'%s')]" )
  public ExtendedWebElement txtConnectionMessage;

  // 'OK'
  @FindBy( xpath = "//button[text()='{L10N:customUpperCaseOKButton}']" )
  public ExtendedWebElement btnTestConnectionOK;

  // Tabs
  // 'General'
  @FindBy( xpath = "//div[text()='{L10N:DatabaseDialog.DbTab.title}']" )
  public ExtendedWebElement tabGeneral;

  // 'Advanced'
  @FindBy( xpath = "//div[text()='{L10N:DatabaseDialog.AdvancedTab.title}']" )
  public ExtendedWebElement tabAdvanced;

  // 'Options'
  @FindBy( xpath = "//div[text()='{L10N:options}']" )
  public ExtendedWebElement tabOptions;

  @FindBy( xpath = "//div[@id='options-parameter-tree']//input[@class='xul-textbox']" )
  public List<ExtendedWebElement> txtParams;

  @FindBy( xpath = "//*[@id='options-parameter-tree']//div[@class='dataWrapper']//tbody//tr[position()>1]" )
  public List<ExtendedWebElement> parameters;

  // 'Pooling'
  @FindBy( xpath = "//div[text()='{L10N:DatabaseDialog.PoolTab.title}']" )
  public ExtendedWebElement tabPooling;

  public enum ConnectionTab {
    GENERAL, ADVANCED, OPTIONS, POOLING;
  }

  public DatabaseConnectionPage( WebDriver driver ) {
    super( driver );

    if ( !isOpened( txtDatabaseConnection ) ) {
      Assert.fail( "DatabaseConnectionPage is not opened!" );
    }
  }

  public boolean testConnection( String dbName ) {
    click( btnTest );
    boolean res = true;
    int i = 0;
    // TODO: improve timeout for format on core level to decrease delay
    while ( ++i < 10 ) {
      String connectionSucceed = CONNECTION_SUCCEED.replace( "{0}", dbName );
      String connectionFailed = CONNECTION_FAILED.replace( "{0}", dbName );
      if ( format( 2, txtConnectionMessage, connectionSucceed ).getElement() != null ) {
        res = true;
        break;
      }

      if ( format( 2, txtConnectionMessage, connectionFailed ).getElement() != null ) {
        res = false;
        break;
      }
      pause( EXPLICIT_TIMEOUT / 10 );
    }
    click( btnTestConnectionOK );
    return res;
  }

  public void buttonOK() {
    pause( 1 );
    click( btnOK, true );
    pause( 1 );
  }

  public void buttonCancel() {
    click( btnCancel );
  }

  public void setName( String name ) {
    type( txtName, name );
  }

  public void setDbType( String type ) {
    ExtendedWebElement databaseType = format( txtDatabaseType, type ); 
    databaseType.click();
    pause(1);
    databaseType.click();
    pause(1);
  }

  public void setAccessType( String access ) {
    click( format( txtAccessType, access ) );
  }

  // Native (JDBC)
  public void setHostName( String hostname ) {
    type( txtHostName, hostname );
  }

  public void setDbName( String dbName ) {
    if ( dbName == null ) {
      return;
    }

    type( txtDbName, dbName );
  }

  public void setPort( String port ) {
    type( txtDbPort, port );
  }

  public void setCustomConnectionURL( String connectionURL ) {
    if ( !Strings.isNullOrEmpty( connectionURL ) ) {
      type( txtCustomConnectionURL, connectionURL );
    }
  }

  public void setCustomDriverName( String driverName ) {
    if ( !Strings.isNullOrEmpty( driverName ) ) {
      type( txtCustomDriverName, driverName );
    }
  }

  public void setUser( String user ) {
    type( txtUser, user );
  }

  public void setPassword( String password ) {
    type( txtPassword, password );
  }

  public void setCommaAsJndiSeparator( Boolean usage ) {
    if ( usage == null ) {
      return;
    }

    if ( usage ) {
      check( checkDecimalSeparator );
    } else {
      uncheck( checkDecimalSeparator );
    }
  }

  public ExtendedWebElement getTxtDatabaseConnection() {
    return txtDatabaseConnection;
  }

  public ExtendedWebElement getTxtName() {
    return txtName;
  }

  public ExtendedWebElement getTxtDatabaseType() {
    return txtDatabaseType;
  }

  public ExtendedWebElement getTxtAccessType() {
    return txtAccessType;
  }

  public ExtendedWebElement getTxtHostName() {
    return txtHostName;
  }

  public ExtendedWebElement getTxtDbName() {
    return txtDbName;
  }

  public ExtendedWebElement getTxtDbPort() {
    return txtDbPort;
  }

  public ExtendedWebElement getTxtUser() {
    return txtUser;
  }

  public ExtendedWebElement getTxtPassword() {
    return txtPassword;
  }

  public ExtendedWebElement getTxtConnectionMessage() {
    return txtConnectionMessage;
  }

  public List<ExtendedWebElement> getTxtParams() {
    return txtParams;
  }

  public ExtendedWebElement getSelectedDatabaseType() {
    return selectedDatabaseType;
  }

  public ExtendedWebElement getSelectedAccessType() {
    return selectedAccessType;
  }

  public void openTab( ConnectionTab tab ) {
    switch ( tab ) {
      case GENERAL:
        click( tabGeneral );
        break;
      case ADVANCED:
        click( tabAdvanced );
        break;
      case OPTIONS:
        click( tabOptions );
        break;
      case POOLING:
        click( tabPooling );
        break;
      default:
        Assert.fail( "need to implement!" );
        break;
    }
  }

  public void addOptions( Map<String, String> options ) {
    int index = 0;
    for ( Entry<String, String> entry : options.entrySet() ) {
      if ( index == 10 ) {
        Assert.fail( "PUC supports only 5 options for DB connection! You exceeded this value!" );
      }
      type( txtParams.get( index++ ), entry.getKey() );
      type( txtParams.get( index++ ), entry.getValue() );

    }
  }

  public boolean verifyOptions( Map<String, String> options ) {

    for ( Entry<String, String> entry : options.entrySet() ) {
      boolean found = false;
      for ( ExtendedWebElement parameter : parameters ) {
        List<ExtendedWebElement> txtParams =
            parameter.findExtendedWebElements( By.xpath( ".//td/input[@class='xul-textbox']" ) );
        // check that parameter key and value are present in the list of
        // Options
        if ( entry.getKey().equals( txtParams.get( 0 ).getAttribute( HTML.VALUE ) ) && entry.getValue().equals(
            txtParams.get( 1 ).getAttribute( HTML.VALUE ) ) ) {
          LOGGER.info( entry.getKey() + " param was found with a value of " + entry.getValue() );
          found = true;
          break;
        }

      }
      // if the param is never found, fail the step and/or return false
      if ( !found ) {
        CustomAssert.fail( "49882", "There is one or more parameter missing from the Options tab." );
        return false;
      }
    }
    return true;
  }
}
