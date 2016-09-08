package com.pentaho.qa.gui.web.pir;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRGeneralPage extends PIRReportPage {

  @FindBy( id = "prevTemplateIcon" )
  protected ExtendedWebElement prevTemplateIcon;

  @FindBy( id = "nextTemplateIcon" )
  protected ExtendedWebElement nextTemplateIcon;

  @FindBy( id = "currentTemplateName" )
  protected ExtendedWebElement currentTemplateName;

  @FindBy( id = "templatePicker" )
  protected ExtendedWebElement btnSelect;

  @FindBy( xpath = "//div[@id='templatePickerDialog']//td[contains(text(), '%s')]" )
  protected ExtendedWebElement template;
  
  //'Yes'
  @FindBy( xpath = "//button[text()='{L10N:Yes_txt}']" )
  protected ExtendedWebElement buttonYes;


  public PIRGeneralPage( WebDriver driver ) {
    super( driver );
  }

  public PIRGeneralPage( WebDriver driver, String name ) {
    super( driver, name );
  }

  public void selectTemplate( String name ) {
    click( btnSelect );
    click( format( template, name ) );
    
    clickIfPresent(buttonYes, EXPLICIT_TIMEOUT/20);
  }

  public boolean isTemplateSelected( String name ) {
    return currentTemplateName.getText().equals( name );
  }

}
