package com.pentaho.qa.gui.web.pir;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.asserts.SoftAssert;

import com.pentaho.services.pir.Prompt;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRPromptPanelPage extends PIRReportPage {

  // Prompts
  @FindBy( xpath = "//div[@class='parameter-label' and text()='%s']" )
  protected ExtendedWebElement promptName;

  @FindBy( xpath = "//*[@class='edit-buttons-container']//div[text()='%s']/../../../../..//select/option[text()='%s']" )
  protected ExtendedWebElement promptItem;

  @FindBy( xpath = "//li[@class='toggleGroup horizontal']//label[text()='%s']" )
  protected ExtendedWebElement promptItemRadioButton;

  @FindBy( xpath = "//div[text()='Product Line']/../../../../..//button[text()='%s']" )
  protected ExtendedWebElement promptItemButton;

  @FindBy(
      xpath = "//div[starts-with(@class,'pentaho-toggle-button pentaho-toggle-button-down pentaho-toggle-button-horizontal')]" )
  protected List<ExtendedWebElement> activetedPromptItemButtons;

  @FindBy( xpath = "//div[table//div[text()='%s']]//select" )
  protected ExtendedWebElement promptDropdown;

  @FindBy( xpath = "//div[text()='%s']/../../../../..//li[@class='toggleGroup horizontal']//input[@type='radio']" )
  protected ExtendedWebElement promptRadioButton;

  @FindBy( xpath = "//div[text()='%s']/../../../../..//li[@class='toggleGroup horizontal']//input[@type='checkbox']" )
  protected ExtendedWebElement promptCheckbox;

  @FindBy( xpath = "//div[text()='Product Line']/../../../../..//button" )
  protected ExtendedWebElement promptButton;

  @FindBy( xpath = "//div/input[@value='%s' and  @type='text']" )
  protected ExtendedWebElement promptTextField;

  @FindBy( xpath = "//div[@class='parameter-label' and text() = 'This prompt value is of an invalid type']" )
  protected ExtendedWebElement promptErrorMessage;

  // 'Edit parameter'
  @FindBy( xpath = "//*[@class='edit-buttons-container']//div[text()='%s']/../..//div[@title='{L10N:EditParameter}']" )
  protected ExtendedWebElement promptEdit;

  // 'Remove parameter'
  @FindBy(
      xpath = "//*[@class='edit-buttons-container']//div[text()='%s']/../..//div[@title='{L10N:RemoveParameter}']" )
  protected ExtendedWebElement promptRemove;

  public PIRPromptPanelPage( WebDriver driver ) {
    super( driver );
  }

  public boolean isPromptExists( Prompt prompt ) {
    pause( 2 );
    if ( getDriver().toString().contains( "firefox" ) ) {
      hidePromptsPanel();
      showPromptsPanel();
    }
    return isElementPresent( format( EXPLICIT_TIMEOUT / 10, promptName, prompt.getName() ) );
  }

  public PIRPromptPage editPrompt( Prompt prompt ) {
    // workaround to get prompt panel to show up due to issues with WebDriver
    if ( getDriver().toString().contains( "firefox" ) ) {
      refreshPromptsPanel();
    }

    Actions action = new Actions( getDriver() );
    action.moveToElement( format( promptName, prompt.getName() ).getElement() ).moveToElement( format( promptEdit,
        prompt.getName() ).getElement() ).click().build().perform();
    return new PIRPromptPage( getDriver(), prompt.getField() );
  }

  public boolean applyPrompt( Prompt prompt ) {
    boolean res = false;

    select( format( promptDropdown, prompt.getName() ), prompt.getValue() );

    // verify that prompt was applied
    if ( isPromptApplied( prompt ) ) {
      res = true;
    }

    return res;
  }

  protected boolean isPromptApplied( Prompt prompt ) {

    return getSelectedValue( format( promptDropdown, prompt.getName() ) ).equals( prompt.getValue() );
  }

  public boolean isPromptItemExists( Prompt prompt, String item ) {
    return isElementPresent( format( EXPLICIT_TIMEOUT / 10, promptItem, prompt.getName(), item ) );
  }

  public void deletePrompt( Prompt prompt ) {
    hover( format( promptName, prompt.getName() ) );
    click( format( promptRemove, prompt.getName() ) );
  }

  public boolean verifyDisplayedValues( String name, String count ) {
    ExtendedWebElement element =
        findExtendedWebElement( By.xpath( "//div[table//div[text()='" + name + "']]//select[@size = '" + count
            + "']" ) );
    return isElementPresent( element, EXPLICIT_TIMEOUT / 5 );
  }

  public void selectElements( String promptName, String selectElements ) {
    Select select = new Select( format( promptDropdown, promptName ).getElement() );

    String[] elements = selectElements.split( ";" );
    for ( String element : elements ) {
      select.selectByValue( element );
    }
    pause( 1 );
  }

  public void selectButtonElements( String selectButtonElements ) {
    // deselect buttons
    if ( activetedPromptItemButtons != null ) {
      for ( ExtendedWebElement element : activetedPromptItemButtons ) {
        element.click();
      }
    }
    // select buttons
    String[] names = selectButtonElements.split( ";" );
    for ( String name : names ) {
      click( format( promptItemButton, name ) );
    }
    pause( 1 );
  }

  public void selectElementRadioButton( String name ) {
    click( format( promptItemRadioButton, name ) );
  }

  public void selectElementButton( String name ) {
    click( format( promptItemButton, name ) );
  }

  public SoftAssert verifyPromptErrorMessage() {
    SoftAssert softAssert = new SoftAssert();

    if ( !isElementPresent( promptErrorMessage, EXPLICIT_TIMEOUT / 5 ) ) {
      softAssert.fail( "Error message was not found!" );
    } else {
      LOGGER.info( "Error message was found as expected." );
    }
    return softAssert;
  }

  public SoftAssert verifyPromptControlType( Prompt prompt ) {
    SoftAssert softAssert = new SoftAssert();
    ExtendedWebElement element = null;

    switch ( prompt.getControl() ) {
      case RADIO_BUTTONS:
        element = format( promptRadioButton, prompt.getName() );
        break;
      case CHECKBOX:
        element = format( promptCheckbox, prompt.getName() );
        break;
      case BUTTONS:
        element = format( promptButton, prompt.getName() );
      case TEXT_FIELD:
        element = format( promptTextField, prompt.getName() );
    }

    if ( element == null ) {
      softAssert.fail( "Incorrect control type!" );
    }
    return softAssert;
  }
}
