package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class EndDatePickerPage extends DatePickerPage {
  
  @CacheLookup
  @FindBy( id = "DP_range2_lookup_popup_year" )
  protected ExtendedWebElement calendarSelectedYear;

  @CacheLookup
  @FindBy( xpath = "//table[@id='DP_range2_lookup_popup']//span[@class='dijitInline dijitCalendarPreviousYear']" )
  protected ExtendedWebElement calendarPreviousYear;

  @CacheLookup
  @FindBy( xpath = "//table[@id='DP_range2_lookup_popup']//span[@class='dijitInline dijitCalendarNextYear']" )
  protected ExtendedWebElement calendarNextYear;

  @FindBy( xpath = "//div[@id='DP_range2_lookup_popup_mddb_mdd']/div[@month='%s']" )
  protected ExtendedWebElement monthElement;

  @FindBy(
      xpath = "//span[@id='DP_range2_lookup_popup_mddb']/span[@class='dijitReset dijitInline dijitArrowButtonInner']" )
  protected ExtendedWebElement showMonthArrow;

  @FindBy(
      xpath = "//div[@id='widget_DP_range2_lookup_dropdown']//td[contains(@class,'dijitCalendarEnabledDate dijitCalendarCurrentMonth dijitCalendarDateTemplate')]/span[text()='%s']" )
  protected ExtendedWebElement dayElement;

  public EndDatePickerPage( WebDriver driver ) {
    super( driver );
  }

  public void selectYear( int year ) {
    if ( year >= Integer.parseInt( calendarSelectedYear.getElement().getText() ) ) {
      while ( year != Integer.parseInt( calendarSelectedYear.getElement().getText() ) ) {
        calendarNextYear.click();
      }
    } else {
      while ( year != Integer.parseInt( calendarSelectedYear.getElement().getText() ) ) {
        calendarPreviousYear.click();
      }
    }
  }

  public void selectMonth( int month ) {
    showMonthArrow.click();
    format( monthElement, month ).click();
  }
  
  public void selectDay( int day){
    format( dayElement, day ).click();
  }

}
