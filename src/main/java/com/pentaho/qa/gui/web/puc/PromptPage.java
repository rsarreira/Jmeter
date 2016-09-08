package com.pentaho.qa.gui.web.puc;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.services.pir.Prompt.Control;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PromptPage extends FramePage {
  @FindBy( xpath = "//div[@class='pentaho-dialog']//div[@class='Caption' and contains(text(),'%s')]" )
  protected ExtendedWebElement dialogPromptTitle;

  @FindBy( id = "displayNameText" )
  protected ExtendedWebElement promptName;

  // Buttons
  @FindBy( id = "editFilterDialog_accept" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "editFilterDialog_cancel" )
  protected ExtendedWebElement btnCancel;

  // Control section
  // Drop Down
  @FindBy( id = "dropDownFilterButton" )
  protected ExtendedWebElement btnDropDown;

  @FindBy( xpath = "//*[@id='listFilterButton']/img" )
  protected ExtendedWebElement btnList;

  @FindBy( xpath = "//*[@id='radioFilterButton']/img" )
  protected ExtendedWebElement btnRadioButtons;

  @FindBy( xpath = "//*[@id='checkboxFilterButton']/img" )
  protected ExtendedWebElement btnCheckbox;

  @FindBy( xpath = "//*[@id='buttonFilterButton']/img" )
  protected ExtendedWebElement btnButtons;

  @FindBy( xpath = "//*[@id='textfieldFilterButton']/img" )
  protected ExtendedWebElement btnTextField;

  @FindBy( xpath = "//*[@id='filterControlsGroupbox']//img[contains(@class, 'disabled')]" )
  protected ExtendedWebElement selectedControl;

  // filterTypeList
  @FindBy( id = "filterTypeList" )
  protected ExtendedWebElement inputDataType;

  @FindBy( id = "mqlFilterSelectDataSourceBtn" )
  protected ExtendedWebElement btnSelect;

  // Control properties
  @FindBy( xpath = "//*[@id='filterDropDownGroupbox']//label[text()='{L10N:EditFilter.Prop.Default.Specify}']" )
  protected ExtendedWebElement specifyButton;

  @FindBy( id = "specifyTextbox" )
  protected ExtendedWebElement specifyTextbox;

  @FindBy( xpath = "//span[@id = 'editFilterPropListIsMultiple']//input" )
  protected ExtendedWebElement multipleListSelectionCheckbox;

  @FindBy( xpath = "//span[@id = 'editFilterPropButtonIsMultiple']//input" )
  protected ExtendedWebElement multipleButtonSelectionCheckbox;

  @FindBy( id = "editFilterPropListSize" )
  protected ExtendedWebElement numberOfDisplayedValues;

  public PromptPage( WebDriver driver ) {
    super( driver );
  }

  /**
   * Determines whether or not the prompt page appeared with the correct title. If the page was not opened, then it will
   * fail the test.
   * 
   * @param promptTitle
   *          The expected title of the prompt page.
   */
  protected void isOpened( String promptTitle ) {
    switchToFrame();

    if ( !super.isOpened( format( dialogPromptTitle, promptTitle ) ) ) {
      Assert.fail( "Opening Prompt dialog failed!" );
    }
  }

  /**
   * Retrieves the value of the prompt's name from the input textbox.
   * 
   * @return Returns a String that contains the prompt's name.
   */
  public String getPromptName() {
    return promptName.getAttribute( HTML.VALUE );
  }

  /**
   * Gets the control type that is currently selected on the prompt page.
   * 
   * @return Returns an ExtendedWebElement that represents the selected control type.
   */
  public ExtendedWebElement getSelectedControl() {
    return selectedControl;
  }

  /**
   * Changes the prompt's name to the specified value.
   */
  public void setName( String value ) {
    if ( value == null ) {
      return;
    }

    type( promptName, value );
  }

  /**
   * Selects the specified control type to be used for the prompt.
   * 
   * @param control
   *          The type of control to use for the prompt.
   */
  public void setControl( Control control ) {
    switch ( control ) {
      case DROP_DOWN:
        btnDropDown.click();
        break;
      case LIST:
        btnList.click();
        break;
      case RADIO_BUTTONS:
        btnRadioButtons.click();
        break;
      case CHECKBOX:
        btnCheckbox.click();
        break;
      case BUTTONS:
        btnButtons.click();
        break;
      case TEXT_FIELD:
        btnTextField.click();
        break;
      default:
        Assert.fail( "Unsupported Control type for PIR Prompt" );
    }
  }

  /**
   * Clicks the OK button on the prompt window to apply the changes and close the window.
   */
  public void clickOK() {
    click( btnOK, true );
  }

  /**
   * Clicks the Cancel button on the prompt window to undo any changes and close the window.
   */
  public void clickCancel() {
    click( btnCancel, true );
  }

  // TODO: Finish the verify method and add Javadoc.
  public void verify( Control control, String fieldName ) {
    SoftAssert softAssert = new SoftAssert();

    // General verifications
    softAssert.assertTrue( isElementPresent( btnDropDown, 0 ), "Drop Down button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnList, 0 ), "List button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnRadioButtons, 0 ),
        "Radio Buttons button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnCheckbox, 0 ), "Checkbox button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnButtons, 0 ), "Buttons button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnTextField, 0 ), "Text Field button is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnOK, 0 ), "Button OK is not present in Prompt dialog!" );
    softAssert.assertTrue( isElementPresent( btnCancel, 0 ), "Button Cancel is not present in Prompt dialog!" );

    softAssert.assertAll();
  }

  /**
   * Determines whether or not the Allow Multiple Selections checkbox for the list control is present.
   * 
   * @return Returns true when the element is present. Otherwise, returns false.
   */
  public boolean verifyListControl() {
    // TODO: Verify the rest of the elements that should be available for the list control.
    return isElementPresent( multipleListSelectionCheckbox, EXPLICIT_TIMEOUT / 5 );
  }

  /**
   * Sets the specified number of displayed values in the control properties panel.
   * 
   * @param number
   *          The number of displayed values.
   */
  public void setDisplayedValues( String number ) {
    numberOfDisplayedValues.type( number );
  }

  /**
   * Checks or unchecks the Allow Multiple Selections checkbox for either the list control or the buttons control.
   * 
   * @param isCheck
   *          Determines whether to check or uncheck the checkbox. When true, the checkbox will be checked.
   * @param control
   *          The control type that contains the checkbox. Only LIST and BUTTONS contain the checkbox.
   */
  public void checkMultipleSelection( Boolean isCheck, Control control ) {
    ExtendedWebElement element = null;
    switch ( control ) {
      case LIST:
        element = multipleListSelectionCheckbox;
        break;
      case BUTTONS:
        element = multipleButtonSelectionCheckbox;
        break;
      default:
        Assert.fail( "The control type '" + control.name()
            + "' does not have an 'Allow multiple selections' checkbox. Only LIST and BUTTONS are accepted." );
    }

    if ( isCheck )
      element.check();
    else if ( element.isChecked() )
      element.uncheck();
    clickOK();
  }

  /**
   * Determines whether or not the "Specify" textbox in the control properties panel is enabled or disabled.
   * 
   * @return Returns true when the element is enabled. Otherwise, returns false.
   */
  public boolean checkSpecifyTextbox() {
    return specifyTextbox.getElement().isEnabled();
  }

  /**
   * Selects the "Specify" radio button in the control properties panel.
   */
  public void selectSpecify() {
    specifyButton.click();
  }

  /**
   * Sets the value of the specify textbox to the specified text.
   * 
   * @param text
   *          The new value of the specify textbox.
   */
  public void setSpecify( String text ) {
    specifyTextbox.type( text );
  }
}
