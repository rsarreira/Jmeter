package com.pentaho.qa.gui.web.analyzer;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public abstract class DatePickerPage extends BasePage {

  @FindBy( xpath = "//button[@id='dlgBtnSave2']" )
  protected ExtendedWebElement applyButton;
  
  @FindBy( xpath = "//div[@id='dialogMessageBar2']/span[contains(text(),'%s')]" )
  protected ExtendedWebElement errorMessage;

  public DatePickerPage( WebDriver driver ) {
    super( driver );
  }

  public void selectDate( String date ) {
    List<String> values = Arrays.asList( date.split( "-" ) );
    int year = Integer.valueOf( values.get( 0 ) );
    int month = Integer.valueOf( values.get( 1 ) );
    int day = Integer.valueOf( values.get( 2 ) );

    selectYear( year );
    selectMonth( month - 1 );
    selectDay( day );

  }

  public void clickApplyButton() {
    applyButton.click();
  }
  
  public boolean vrifyErrorMessage( String str ) {
    return isElementPresent( format( errorMessage, str ), EXPLICIT_TIMEOUT / 5 );
  }

  public abstract void selectYear( int year );

  public abstract void selectMonth( int month );

  public abstract void selectDay( int day );
}
