package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Created by Ihar_Chekan on 5/6/2015.
 */
public class OpenPage extends BasePage {

  public OpenPage( WebDriver driver ) {
        super( driver );

    if ( !isOpened( dlgOpen ) ) {
      Assert.fail("OpenPage is not opened!" );
    }
  }

  public boolean isOpened( long timeout ) {
    return super.isOpened( dlgOpen, timeout );
  }

}
