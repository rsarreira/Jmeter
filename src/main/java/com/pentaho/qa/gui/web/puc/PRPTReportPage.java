package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PRPTReportPage extends ReportPage {
  @FindBy( xpath = "//div[div[@class='parameter-label'and contains(text(),'%s')]]//select" )
  protected ExtendedWebElement dropdownParameterItem;

  // @FindBy( xpath = "//title[text()='Report Web Viewer']" )
  // protected ExtendedWebElement reportWebViewer;

  @FindBy( css = "#glasspane[style*='display: block']" )
  protected ExtendedWebElement glassPaneActive;

  @FindBy( id = "toppanel" )
  protected ExtendedWebElement reportTopPanel;

  @FindBy( id = "reportContent" )
  protected ExtendedWebElement reportContentFrame;

  public PRPTReportPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  protected boolean isOpened() {
    return super.isOpened();
  }

  // Change parameter by selecting specified value from dropdown
  public boolean changeParameter( String parameterName, String parameterValue ) {

    driver.switchTo().frame( reportFrame.getElement() );

    // Find dropdown element by parameterName
    select( format( dropdownParameterItem, parameterName ), parameterValue );
    // [MG] Workaround for firefox
    pause( 2 );
    boolean res = isGlassPaneVisible();
    if ( !res ) {
      LOGGER.error( "Blocking glass pane element is not working as expected!" );
    }
    pause( 1 );

    // Get selected in dropdown value
    // PRPTReportPage tempPage = new PRPTReportPage(getDriver(), fileName); //just to resolve
    // "stale element reference: element is not attached to the page document"
    String selectedValue = getSelectedValue( format( dropdownParameterItem, parameterName ) );
    if ( !selectedValue.equals( parameterValue ) ) {
      LOGGER.error( "Required value was not selected for " + parameterName + " parameter!" );
    }

    // Return false if specified value was not selected
    return res && selectedValue.equals( parameterValue );
  }

  private boolean isGlassPaneVisible() {
    // [MG] Workaround for firefox, the EXPLICIT_TIMEOUT wait was not enough.
    pause( 3 );
    return findExtendedWebElements( glassPaneActive.getBy(), EXPLICIT_TIMEOUT ).isEmpty();
  }

  public boolean isReportDisplayedCorrectly() {
    boolean displayCriteria_1 = isElementPresent( reportTopPanel );
    boolean displayCriteria_2 = isElementPresent( reportContentFrame );

    SoftAssert softAssert = new SoftAssert();
    softAssert.assertTrue( displayCriteria_1, "Report top panel is not displayed" );
    softAssert.assertTrue( displayCriteria_2, "Report content is not displayed" );
    softAssert.assertAll();

    return ( displayCriteria_1 && displayCriteria_2 );
  }
}
