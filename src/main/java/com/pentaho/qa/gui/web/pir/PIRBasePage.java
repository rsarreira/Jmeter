package com.pentaho.qa.gui.web.pir;

import java.text.DecimalFormat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.ReportPage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRBasePage extends ReportPage {

  public static final String DEFAULT_NUMBER_FORMAT = "$#,##0.00";

  @FindBy( css = "[widgetid=toolbar1\\.spinner]" )
  protected ExtendedWebElement busyIndicatorPIR;

  @FindBy( id = "reportContent" )
  protected ExtendedWebElement reportContentFrame;

  public PIRBasePage( WebDriver driver ) {
    super( driver, NEW_IR_REPORT_NAME );
  }

  public PIRBasePage( WebDriver driver, String name ) {
    super( driver, name );
    if ( !isElementPresent( reportContentFrame ) ) {
      LOGGER.error( "Interactive Reporting is broken, report area didn't appear!" );
    }
  }

  public void click( final ExtendedWebElement extendedWebElement, boolean wait ) {
    super.click( extendedWebElement, EXPLICIT_TIMEOUT / 10 );
    if ( wait ) {
      int i = 0;
      while ( ( busyIndicatorPIR.getClass().toString().contains( "ButtonDisabled" ) ) && ++i < 10 ) {
        LOGGER.info( "PIR busy indicator is present. Waiting for operation finalization..." );
        pause( EXPLICIT_TIMEOUT / 15 );
      }
    }
  }

  public void select( final ExtendedWebElement extendedWebElement, String text, boolean wait ) {
    super.select( extendedWebElement, text );
    if ( wait ) {
      int i = 0;
      while ( ( busyIndicatorPIR.getClass().toString().contains( "ButtonDisabled" ) ) && ++i < 10 ) {
        LOGGER.info( "PIR busy indicator is present. Waiting for operation finalization..." );
        pause( EXPLICIT_TIMEOUT / 15 );
      }
    }
  }

  protected boolean isCorrespondNumberFormat( String value, String pattern ) {
    String origValue = value;

    // Verification based on signes
    if ( value.startsWith( "$" ) || value.endsWith( "%" ) ) {
      if ( !( pattern.startsWith( "$" ) || pattern.endsWith( "%" ) ) ) {
        return false;
      }
      value = value.replace( "$", "" );
      value = value.replace( "%", "" );
    }

    // Verification the value format
    double doubleValue = 0;
    try {
      doubleValue = Double.parseDouble( value.replace( ",", "" ) );
    } catch ( NumberFormatException e ) {
      return false;
    }
    DecimalFormat df = new DecimalFormat( pattern );
    String formatValue = df.format( doubleValue );

    return origValue.equals( formatValue );
  }

}
