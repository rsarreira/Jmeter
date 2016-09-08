package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.analyzer.PAFilter;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AnalyzerFilterPanelPage extends AnalyzerReportPage {

  @FindBy( xpath = "//i[contains(@id,'remove_filter') and contains(@id,'%s')]" )
  protected ExtendedWebElement removeFilter;
  
  @FindBy( xpath = "//i[contains(@id,'edit_filter') and contains(@id,'%s')]" )
  protected ExtendedWebElement editFilter;
  
  @FindBy( xpath = "//div[@id='RPT001Filter']//span[contains(text(),'%s') ]" )
  protected ExtendedWebElement filterItem;

  public AnalyzerFilterPanelPage( WebDriver driver ) {
    super( driver );
  }

  public void deleteFilter( PAFilter filter ) {
    showFilterPanel();
    if ( isElementPresent( format( removeFilter, filter.getField() ), EXPLICIT_TIMEOUT / 5 ) ) {
      format( removeFilter, filter.getField() ).click();
    } else {
      Assert.fail( "No such filter: " + filter.getField() );
    }
    pause( EXPLICIT_TIMEOUT / 10 );
  }
  
  public AnalyzerFilterPage editFilter( PAFilter filter ) {
    if ( isElementPresent( format( editFilter, filter.getField() ), EXPLICIT_TIMEOUT / 5 ) ) {
      format( editFilter, filter.getField() ).click();
    } else {
      Assert.fail( "No such filter: " + filter.getField() );
    }
    return new AnalyzerFilterPage( getDriver() );
  }

  public boolean isFilterExists( PAFilter filter ) {
    refreshFilterPanel();
    return isElementPresent( format( filterItem, filter.getValue() ), EXPLICIT_TIMEOUT / 5 );
  }

}
