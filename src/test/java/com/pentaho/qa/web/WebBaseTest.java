package com.pentaho.qa.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Parameters;

import com.pentaho.qa.listener.PentahoListener;
import com.pentaho.services.PentahoUser;
import com.pentaho.services.puc.administration.AdministrationService;
import com.pentaho.services.puc.browse.BrowseService;
import com.pentaho.services.puc.browse.Folder;
import com.qaprosoft.carina.core.foundation.UITest;
import com.qaprosoft.carina.core.foundation.utils.Configuration;
import com.qaprosoft.carina.core.foundation.utils.Configuration.Parameter;

@Listeners( { PentahoListener.class } )
public class WebBaseTest extends UITest {
  protected static final Logger LOGGER = Logger.getLogger( WebBaseTest.class );

  public static PentahoUser webUser = null;
  public static String appVersion = Configuration.get( Parameter.APP_VERSION );
  public static String locale = Configuration.get( Parameter.LOCALE );
  
  @BeforeClass( )
  @Parameters( { "user", "password" } )
  public void initializeServices( String userName, String password ) {
    webUser = new PentahoUser( userName, password, false );
    
    if ( !( locale.equals( "en" ) ) ) {
      setSuiteNameAppender( locale.toUpperCase() );
    }

    int count = StringUtils.countMatches(appVersion, ".");
    if (count < 4) {
      //only bightly builds
      if (appVersion.lastIndexOf('.') > 0) {
        appVersion = appVersion.substring(0, appVersion.lastIndexOf('.'));
      }
    }
    BrowseService.addSampleContent();
    AdministrationService.addSampleContent();
  }
  
  protected Folder getUserHome() {
     return (Folder) BrowseService.getBrowseItem( "/home/" + webUser.getName() );
  }
  
}
