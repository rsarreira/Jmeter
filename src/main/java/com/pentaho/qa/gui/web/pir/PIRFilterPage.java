package com.pentaho.qa.gui.web.pir;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.puc.FramePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.pir.PIRFilter;
import com.pentaho.services.Filter.Condition;
import com.pentaho.services.Filter.DataType;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRFilterPage extends FramePage {

  public static final String FILTER_TYPE_LIST = L10N.getText( "filterDialogTypePicklistSpan_content" );
  public static final String FILTER_TYPE_CONDITION = L10N.getText( "filterDialogTypeMatchSpan_content" );
  public static final String CHANGE_FILTER_DELETE_PROMPT = L10N.getText( "ChangeParameterNameDeletePromptMessage" );

  // 'Filter on'
  @FindBy( xpath = "//span[@class='dijitDialogTitle' and text()='{L10N:FilterDialogTitle} %s']" )
  protected ExtendedWebElement dialogFilterTitle;

  @FindBy( css = "select[data-dojo-attach-point=matchComparator]" )
  protected ExtendedWebElement selectComparator;

  @FindBy( css = "input[data-dojo-attach-point=matchValueInput]" )
  protected ExtendedWebElement inputValueCondition;

  @FindBy( id = "dijit_form_TextBox_1" )
  protected ExtendedWebElement inputParamName;

  @FindBy( css = "#dijit_Dialog_6 #button0" )
  protected ExtendedWebElement btnOK;

  @FindBy( css = "#dijit_Dialog_6 #button1" )
  protected ExtendedWebElement btnCancel;

  @FindBy( css = "input[value=PICKLIST]" )
  protected ExtendedWebElement radioBtnFilterTypeList;

  @FindBy(
      xpath = "//span[@class='filterDialogTypePicklistText' and text()='{L10N:filterDialogTypePicklistSpan_content}']" )
  protected ExtendedWebElement txtBtnFilterTypeList;

  @FindBy( css = "input[value=MATCH]" )
  protected ExtendedWebElement radioBtnFilterTypeCondition;

  @FindBy( xpath = "//span[@class='filterDialogTypeMatchText' and text()='{L10N:filterDialogTypeMatchSpan_content}']" )
  protected ExtendedWebElement txtBtnFilterTypeCondition;

  @FindBy( css = "[data-dojo-attach-point=matchFieldName]" )
  protected ExtendedWebElement txtFieldName;

  // Warning dialog
  @FindBy( xpath = "//div[@id='dijit_Dialog_1']" )
  protected ExtendedWebElement dlgWarning;

  @FindBy( xpath = "//div[@id='messageBoxDialog']//div[@class='pentaho-pir-dialog' and contains(text(),%s)]" )
  protected ExtendedWebElement warningLabel;

  @FindBy( css = "#dijit_Dialog_1 #button0" )
  protected ExtendedWebElement btnYes;

  @FindBy( css = "#dijit_Dialog_1 #button1" )
  protected ExtendedWebElement btnNo;

  /* -------------- SelectFromListWebElements ------------------------------ */
  @FindBy( xpath = "//img[@dojoattachevent='onclick:_picklistAddSelected']" )
  protected ExtendedWebElement btnPicklistAdd;

  @FindBy( xpath = "//img[@dojoattachevent='onclick:_picklistRemoveSelected']" )
  protected ExtendedWebElement btnPicklistRemove;

  @FindBy( xpath = "//img[@dojoattachevent='onclick:_picklistAddAll']" )
  protected ExtendedWebElement btnPicklistAddAll;

  @FindBy( xpath = "//img[@dojoattachevent='onclick:_picklistRemoveAll']" )
  protected ExtendedWebElement btnPicklistRemoveAll;

  @FindBy( xpath = "//button[contains(text(),'Find')]" )
  protected ExtendedWebElement btnFind;

  @FindBy( xpath = "//input[@id='dijit_form_TextBox_0']" )
  protected ExtendedWebElement findTextBox;

  @FindBy( xpath = "//select[@id='dijit_form_MultiSelect_0']" )
  protected ExtendedWebElement leftFilterSelect;

  @FindBy( xpath = "//select[@id='dijit_form_MultiSelect_0']/option[@value='%s']" )
  protected ExtendedWebElement leftFilterSelectItem;

  @FindBy( xpath = "//select[@id='dijit_form_MultiSelect_1']" )
  protected ExtendedWebElement rightFilterSelect;

  @FindBy( xpath = "//select[@id='dijit_form_MultiSelect_1']/option[@value='%s']" )
  protected ExtendedWebElement rightFilterSelectItem;

  @FindBy( xpath = "//option[@value='AND']" )
  protected ExtendedWebElement btnIncluded;

  @FindBy( xpath = "//option[@value='AND NOT']" )
  protected ExtendedWebElement btnExcluded;

  @FindBy( xpath = "//input[@value='PICKLIST']" )
  protected ExtendedWebElement btnSelectFromList;

  @FindBy( id = "dijit_form_TextBox_0" )
  protected ExtendedWebElement fieldChooseValueList;

  @FindBy( id = "dijit_form_MultiSelect_0" )
  protected ExtendedWebElement leftMutliSelectTabel;

  @FindBy( id = "dijit_form_MultiSelect_1" )
  protected ExtendedWebElement rightMutliSelectTabel;

  @FindBy( xpath = "//select[@data-dojo-attach-point='picklistCombinationType']" )
  protected ExtendedWebElement picklistCombinationType;

  @FindBy( xpath = "%s" )
  protected ExtendedWebElement contentElement;

  @FindBy( xpath = "//span[@class='filterDialogTypePicklistCombinationTypeLinksSpan' ]//a[contains(text(),'%s')]" )
  protected ExtendedWebElement picklistCombination;

  /* -------------- // ------------------------------ */

  public PIRFilterPage( WebDriver driver, String fieldName ) {
    super( driver );
    if ( !isOpened( fieldName ) ) {
      CustomAssert.fail( "60157", "Opening Filter dialog failed!" );
    }
  }

  public PIRFilterPage( WebDriver driver ) {
    super( driver );
  }

  private boolean isOpened( String fieldName ) {
    switchToFrame();
    return super.isOpened( format( dialogFilterTitle, fieldName ) );
  }

  public String getParamName() {
    return inputParamName.getAttribute( HTML.VALUE );
  }

  public void specifyCondition( Boolean specifyCondition ) {
    if ( specifyCondition == null ) {
      return;
    }

    if ( specifyCondition ) {
      click( radioBtnFilterTypeCondition );
    } else {
      click( radioBtnFilterTypeList );
    }
  }

  public boolean isSpecifyCondition() {
    if ( radioBtnFilterTypeCondition.isChecked() ) {
      return true;
    }
    return false;
  }

  public void setValue( String value ) {
    if ( value == null ) {
      return;
    }
    if ( isSpecifyCondition() ) {
      type( inputValueCondition, value );
    } else {
      List<String> values = splitContent( value );
      addItem( values );
    }
  }

  public String getValue() {
    return inputValueCondition.getAttribute( HTML.VALUE );
  }

  public void setParamName( String value ) {
    if ( value == null ) {
      return;
    }
    type( inputParamName, value );
  }

  public void setCondition( Condition condition ) {
    if ( condition == null ) {
      return;
    }
    if ( isSpecifyCondition() ) {
      select( selectComparator, condition.getName() );
    } else {
      click( format( picklistCombination, condition.getName() ) );
    }
  }

  public Condition getCondition() {
    Select select = new Select( selectComparator.getElement() );
    return Condition.fromString( select.getFirstSelectedOption().getText() );
  }

  public void clickOK() {
    click( btnOK, EXPLICIT_TIMEOUT / 10 );
  }

  public void clickCancel() {
    click( btnCancel, true );
  }

  public void clickCancel( String fieldName ) {
    click( btnCancel, true );
    if ( isOpened( fieldName ) ) {
      CustomAssert.fail( "60157", "Closing the Filter dialog failed!" );
    }
  }

  public void verify( DataType dataType, String fieldName ) {
    SoftAssert softAssert = new SoftAssert();

    // General verifications
    softAssert.assertTrue( isElementPresent( txtBtnFilterTypeCondition, 0 ), "Option '" + FILTER_TYPE_CONDITION
        + "' doesn't present in Filter dialog!" );
    softAssert.assertTrue( isElementPresent( btnOK, 0 ), "Button OK is not present in Filter dialog!" );
    softAssert.assertTrue( isElementPresent( btnCancel, 0 ), "Button Cancel is not present in Filter dialog!" );

    switch ( dataType ) {
      case STRING:
        softAssert.assertTrue( isElementPresent( txtBtnFilterTypeList, 0 ), "Option '" + FILTER_TYPE_LIST
            + "' doesn't present in Filter dialog!" );
        softAssert.assertTrue( isElementPresent( txtFieldName, 0 ) && txtFieldName.getText().equals( fieldName ),
            "Field Name static text is not equal to real field name!" );
        softAssert.assertTrue( selectComparator.getSelectedValue().equals( Condition.EXACTLY_MATCHES.getName() ),
            "Default comparatator is not: " + L10N.getText( "EXACTLY_MATCHES" ) );
        softAssert.assertTrue( inputValueCondition.getText().isEmpty(), "Value to compare field is not empty!" );
        softAssert.assertTrue( inputParamName.getText().isEmpty(), "Parameter field is not empty!" );
        break;
      case NUMBER:
        // TODO: TBD
        break;
      case DATE:
        // TODO: TBD
        break;
      default:
        Assert.fail( "Not implemented yet!" );
        break;
    }
    softAssert.assertAll();
  }

  public boolean isWarningDialogPresent( PIRFilter filter ) {
    boolean res = false;
    String[] params = { filter.getField() };
    String translatedMessage = L10N.generateConcatForXPath( L10N.formatString( CHANGE_FILTER_DELETE_PROMPT, params ) );

    if ( isElementPresent( dlgWarning, EXPLICIT_TIMEOUT / 5 )
        && isElementPresent( format( warningLabel, translatedMessage ) ) ) {
      LOGGER.info( "Warning message displayed: " + format( warningLabel, translatedMessage ).getText() );
      res = true;
    }

    return res;
  }

  public void confirmWarningDialog( boolean confirm ) {
    if ( confirm ) {
      clickYes();
    } else {
      clickNo();
      clickCancel();
    }
  }

  public void clickYes() {
    click( btnYes, true );
  }

  public void clickNo() {
    click( btnNo, true );
  }

  public void addItem( List<String> values ) {

    for ( String value : values ) {
      // Workaround for select value in Chrome browser
      Select select = new Select( leftFilterSelect.getElement() );
      select.deselectAll();

      select( leftFilterSelect, value );
      btnPicklistAdd.click();

      if ( !format( rightFilterSelectItem, value ).isElementPresent() ) {
        LOGGER.error( "Value '" + value + "' was not added to right filter panel!" );
      } else {
        LOGGER.info( "Value'" + value + "' was added to right panel!" );
      }
    }
  }

  public boolean isElementPresentRightTable( String value ) {
    boolean res = false;
    if ( isElementPresent( format( rightFilterSelectItem, value ) ) ) {
      res = true;
    }
    return res;
  }

  public void removeItem( List<String> values ) {
    for ( String value : values ) {
      select( rightFilterSelect, value );
      btnPicklistRemove.click();

      if ( isElementPresentRightTable( value ) ) {
        LOGGER.error( "Value '" + value + "' was not removed from the right Filter list!" );
      } else {
        LOGGER.info( "Value'" + value + "' was removed from the right Filter Filter list successfully." );
      }
    }
  }

  public void addAllItems() {
    btnPicklistAddAll.click();
  }

  public void removeAllItems() {
    btnPicklistRemoveAll.click();
  }

  public void setInclude() {
    btnIncluded.click();
  }

  public void setExclude() {
    btnExcluded.click();
  }

  public void setSelectFromList() {
    radioBtnFilterTypeList.click();
  }

  public void btnOkClicked() {
    btnOK.click();
  }

  public boolean ElementIncluded( String text ) {
    boolean res = false;
    if ( ( btnIncluded.getText() ).equals( text ) ) {
      res = true;
    }
    return res;
  }

  public List<String> splitContent( String values ) {
    return Arrays.asList( values.split( "; " ) );
  }

}
