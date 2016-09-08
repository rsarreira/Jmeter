package com.pentaho.qa.gui.web.analyzer;

import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.puc.FramePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.Filter.Condition;
import com.pentaho.services.Filter.DataType;
import com.pentaho.services.analyzer.PAFilter;
import com.pentaho.services.analyzer.PAFilter.FilterConditionType;
import com.pentaho.services.analyzer.PAFilter.TimePeriod;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AnalyzerFilterPage extends FramePage {

  public static final String FILTER_TYPE_LIST = L10N.getText( "dlgAttributeFilterSelectLabel" );
  public static final String FILTER_TYPE_STRING = L10N.getText( "dlgAttributeFilterMatchLabel" );

  // 'Filter on'
  @FindBy(
      xpath = "//div[@id='standardDialogHeader']/div[@id='dialogTitleBar']/table/tbody/tr/td[contains(text(),'{L10N:dlgAttributeFilterTitle} %s')]" )
  protected ExtendedWebElement dialogFilterTitle;

  @FindBy( xpath = "//select[@id='FT_matchOp']" )
  protected ExtendedWebElement matchSelector;
  
  @FindBy( xpath = "//select[@id='FT_picklistOp']" )
  protected ExtendedWebElement listConditionSelector;
  
  @FindBy( xpath = "//select[@id='FT_rangeOp']" )
  protected ExtendedWebElement rangeConditionSelector;
  
  @FindBy( xpath = "//select[@id='FT_rangeOp']/option[@value='BETWEEN']" )
  protected ExtendedWebElement betweenOption;
  
  @FindBy( xpath = "//select[@id='FT_rangeOp']/option[@value='AFTER']" )
  protected ExtendedWebElement afterOption;
  
  @FindBy( xpath = "//select[@id='FT_rangeOp']/option[@value='BEFORE']" )
  protected ExtendedWebElement beforeOption;
  
  @FindBy( xpath = "//input[@id='FT_expression_0']" )
  protected ExtendedWebElement inputValueCondition;
  
  @FindBy( id = "FT_openDatePicker" )
  protected ExtendedWebElement datePickerLink;

  @FindBy( id = "FT_PARAMETER_NAME" )
  protected ExtendedWebElement inputParamName;
  
  @FindBy( id = "FT_PARAMETER_ENABLE" )
  protected ExtendedWebElement checkboxParamName;

  @FindBy( xpath = "//button[@id='dlgBtnSave']" )
  protected ExtendedWebElement btnOK;

  @FindBy( xpath = "//button[@id='dlgBtnCancel']" )
  protected ExtendedWebElement btnCancel;

  @FindBy( xpath = "//input[@value='FILTER_PICKLIST']" )
  protected ExtendedWebElement radioBtnFilterTypeList;

  @FindBy( xpath = "//input[@value='FILTER_MATCH']" )
  protected ExtendedWebElement radioBtnFilterTypeCondition;
  
  @FindBy( xpath = "//div[@id='standardDialogHeader']//td[contains(text(),'%s')]" )
  protected ExtendedWebElement txtFieldName;

  /* -------------- SelectFromListWebElements ------------------------------ */
  @FindBy( xpath = "//div[@id='FT_select_add']" )
  protected ExtendedWebElement btnPicklistAdd;

  @FindBy( xpath = "//div[@id='FT_select_remove']" )
  protected ExtendedWebElement btnPicklistRemove;

  @FindBy( xpath = "//div[@id='FT_select_addAll']" )
  protected ExtendedWebElement btnPicklistAddAll;

  @FindBy( xpath = "//div[@id='FT_select_removeAll'" )
  protected ExtendedWebElement btnPicklistRemoveAll;

  @FindBy( xpath = "//button[@id='FT_searchBy']" )
  protected ExtendedWebElement btnFind;

  @FindBy( xpath = "//input[@id='FT_searchText']" )
  protected ExtendedWebElement findTextBox;

  @FindBy( xpath = "//div[@id='FT_valueList']" )
  protected ExtendedWebElement leftFilterSelect;

  @FindBy( xpath = "//div[@id='FT_valueList']/div[contains(text(),'%s')]" )
  protected ExtendedWebElement leftFilterSelectItem;
  
  @FindBy( xpath = "//div[@id='FT_valueList']/div" )
  protected List<ExtendedWebElement> availableToSelectItems;

  @FindBy( xpath = "//div[@id='FT_memberList']" )
  protected ExtendedWebElement rightFilterSelect;

  @FindBy( xpath = "//div[@id='FT_memberList']/div[contains(text(),'%s')]" )
  protected ExtendedWebElement rightFilterSelectItem;

  @FindBy( xpath = "//option[@value='EQUAL']" )
  protected ExtendedWebElement btnIncluded;

  @FindBy( xpath = "//option[@value='NOT_EQUAL']" )
  protected ExtendedWebElement btnExcluded;
  

  /*
   * @FindBy( xpath = "//input[@value='FILTER_PICKLIST']" ) protected ExtendedWebElement btnSelectFromList;
   */

  @FindBy( id = "FT_PARAMETER_NAME" )
  protected ExtendedWebElement fieldChooseValueList;
  @FindBy( id = "FT_valueList" )
  protected ExtendedWebElement leftMutliSelectTabel;

  @FindBy( id = "FT_memberList" )
  protected ExtendedWebElement rightMutliSelectTabel;

  @FindBy( xpath = "//select[@id='FT_picklistOp']" )
  protected ExtendedWebElement picklistCombinationType;
  
  @FindBy( xpath = "//select[@id='FT_picklistOp']/option[@value='EQUAL']" )
  protected ExtendedWebElement includeOption;
  
  @FindBy( xpath = "//select[@id='FT_picklistOp']/option[@value='NOT_EQUAL']" )
  protected ExtendedWebElement excludeOption;

  @FindBy( xpath = "%s" )
  protected ExtendedWebElement contentElement;

  @FindBy( xpath = "//span[@class='filterOpLink' ]//a[contains(text(),'%s')]" )
  protected ExtendedWebElement picklistCombination;
  
  @FindBy( id = "FT_filterTypeSelect" )
  protected ExtendedWebElement txtBtnFilterTypeList;
  
  @FindBy( xpath = "//div[@id='FT_filterTypeTime']/input[@id='%s']" )
  protected ExtendedWebElement radioBtnConditionType;
  
  @FindBy( xpath = "//table[@id='FT_FILTER_PRESET']//input[@id='%s']" )
  protected ExtendedWebElement timePeriodCheckbox;
  
  @FindBy( xpath = "//input[@id='FT_TIME_RANGE_PREV_NUM']" )
  protected ExtendedWebElement previousTimeInput;
  
  @FindBy( xpath = "//input[@id='FT_TIME_RANGE_NEXT_NUM']" )
  protected ExtendedWebElement nextTimeInput;
  
  @FindBy( xpath = "//input[@id='FT_TIME_AGO_NUM']" )
  protected ExtendedWebElement agoTimeInput;
  
  @FindBy( xpath = "//input[@id='FT_TIME_AHEAD_NUM']" )
  protected ExtendedWebElement aheadTimeInput;
  
  @FindBy( xpath = "//a[@id='%s']" )
  protected ExtendedWebElement listConditionLink;
  
  @FindBy( id = "FT_range1" )
  protected ExtendedWebElement rangeSelect1;
  
  @FindBy( id = "FT_range2" )
  protected ExtendedWebElement rangeSelect2;
  
  @FindBy( id = "FT_exp_add" )
  protected ExtendedWebElement addAnotherValueLink;
  
  @FindBy( xpath = "//td[@class='FT_expressionOR']" )
  protected ExtendedWebElement orPreposition ;
  
  @FindBy( xpath = "//input[contains(@id,'FT_expression_1')]" )
  protected ExtendedWebElement addAnotherValueTextBox ;
  
  @FindBy( xpath = "//td[contains(@id,'FT_exp_remove_1')]" )
  protected ExtendedWebElement removeAnotherValueButton ;
  
  @FindBy( id = "DP_range1_lookup" )
  protected ExtendedWebElement beginDateInput;

  @FindBy( xpath = "//td[@id='DP_range1']//input[@type='hidden']" )
  protected ExtendedWebElement beginDateHiddenInput;

  @FindBy( id = "DP_range2_lookup" )
  protected ExtendedWebElement endDateInput;

  @FindBy( xpath = "//td[@id='DP_range2']//input[@type='hidden']" )
  protected ExtendedWebElement endDateHiddenInput;
  
  @FindBy( xpath = "//td[@id='DP_range1']//input[@class = 'dijitReset dijitInputField dijitArrowButtonInner']" )
  protected ExtendedWebElement beginDatePicker;
  
  @FindBy( xpath = "//td[@id='DP_range2']//input[@class = 'dijitReset dijitInputField dijitArrowButtonInner']" )
  protected ExtendedWebElement endDatePicker;
  
  /* -------------- Numeric Filter ------------------------------ */
  @FindBy( xpath = "//*[@id='dialogTitleBar']//*[text()='{L10N:dlgNumericFilterTitle}']" )
  protected ExtendedWebElement dlgNumericFilter;
  
  @FindBy( xpath = "//select[@id='FT_attribute']" )
  protected ExtendedWebElement attributeNumericFilter;
   
  public AnalyzerFilterPage( WebDriver driver ) {
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
    checkboxParamName.check();
    type( inputParamName, value );
  }

  public void setCondition( Condition condition ) {
    if ( condition == null ) {
      return;
    }
    if ( isSpecifyCondition() ) {
      select( matchSelector, condition.getName() );
    } else {
      setListCondition(condition);
    }
  }

  public Condition getCondition() {
    Select select = new Select( matchSelector.getElement() );
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

  public void addItem( List<String> values ) {

    for ( String value : values ) {

      format( leftFilterSelectItem, value ).doubleClick();

      if ( !format( rightFilterSelectItem, value ).isElementPresent( EXPLICIT_TIMEOUT / 2 ) ) {
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
    return Arrays.asList( values.split( ";" ) );
  }

  public void verify( DataType dataType, String fieldName ) {
    SoftAssert softAssert = new SoftAssert();

    // General verifications
    softAssert.assertTrue( isElementPresent( btnOK, 0 ), "Button OK is not present in Filter dialog!" );
    softAssert.assertTrue( isElementPresent( btnCancel, 0 ), "Button Cancel is not present in Filter dialog!" );

    switch ( dataType ) {
      case STRING:
        softAssert.assertTrue( isElementPresent( txtBtnFilterTypeList, 0 ), "Option '" + FILTER_TYPE_LIST
            + "' doesn't present in Filter dialog!" );
        softAssert.assertTrue( isElementPresent( format( txtFieldName, fieldName ) ),
            "Field Name static text is not equal to real field name!" );
        softAssert.assertTrue( !checkboxParamName.isChecked(), "Param name field is enabled! " );
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

  public void setConditionType( FilterConditionType conditionType ) {
    if ( isElementPresent( format( radioBtnConditionType, conditionType.value ), EXPLICIT_TIMEOUT / 2 ) ) {
      format( radioBtnConditionType, conditionType.value ).check();
    } else {
      Assert.fail( "The specified condition did not found!" );
    }
  }

  public void setTimePeriod( TimePeriod timeperiod ) {
    format( timePeriodCheckbox, timeperiod.value ).check();
  }

  public void setListCondition( Condition condition ) {
    if ( condition != null ) {
      format( listConditionLink, condition.getName() ).click();
    }
  }
  
  public void setTimePeriodValue( PAFilter filter ) {
    if ( filter.getValue() != null ) {
      switch ( filter.getTimePeriod() ) {
        case PREVIOUS:
          type( previousTimeInput, filter.getValue() );
          break;

        case NEXT:
          type( nextTimeInput, filter.getValue() );
          break;

        case YEARS_AGO:
          type( agoTimeInput, filter.getValue() );
          break;

        case YEARS_AHEAD:
          type( aheadTimeInput, filter.getValue() );
          break;

        default:
          break;
      }
    }
  }

  public boolean verifyTimePeriod( TimePeriod timeperiod ) {
    return isChecked( format( timePeriodCheckbox, timeperiod.value ) );
  }

  public boolean verifyTimePeriodValue( PAFilter filter ) {
    boolean res = true;
    switch ( filter.getTimePeriod() ) {
      case PREVIOUS:
        res = previousTimeInput.getAttribute( HTML.VALUE ).equals( filter.getValue() );
        break;

      case NEXT:
        res = nextTimeInput.getAttribute( HTML.VALUE ).equals( filter.getValue() );
        break;

      case YEARS_AGO:
        res = agoTimeInput.getAttribute( HTML.VALUE ).equals( filter.getValue() );
        break;

      case YEARS_AHEAD:
        res = aheadTimeInput.getAttribute( HTML.VALUE ).equals( filter.getValue() );
        break;

      default:
        break;
    }
    return res;
  }

  public void setListValue( PAFilter filter ) {
    if ( filter.getValue() != null ) {
      List<String> values = filter.getValues();
      addItem( values );
    }
  }

  public boolean verifyListItems( PAFilter filter ) {
    List<String> values = filter.getValues();
    for ( String value : values ) {
      if ( !isElementPresent( format( rightFilterSelectItem, value ), EXPLICIT_TIMEOUT / 5 ) ) {
        return false;
      }
    }

    return true;
  }
  
  public void setRangeValue( PAFilter filter ) {
    List<String> values = filter.getValues();
    if ( values.size() <= 2 ) {
      switch ( filter.getCondition() ) {
        case BETWEEN:
          select( rangeSelect1, values.get( 0 ) );
          select( rangeSelect2, values.get( 1 ) );
          break;

        case AFTER:
        case BEFORE:
          select( rangeSelect1, values.get( 0 ) );
          break;

        default:
          break;
      }
    }
  }

  public void typeInFilterField( String cndition ) {
    findTextBox.getElement().sendKeys( cndition );
  }

  public void clickFindButton() {
    btnFind.click();
  }

  public void clearFilterField() {
    findTextBox.getElement().clear();
  }

  public void verifyFilterField( String value ) {
    if ( ( availableToSelectItems.size() != 1 ) || !isElementPresent( format( leftFilterSelectItem, value ),
        EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "Available fields list is not filtered by the substring!" );
    }
  }

  public void verifyEmptyFilterField() {
    if ( ( availableToSelectItems.size() <= 1 ) ) {
      Assert.fail( "Substring is not reset!" );
    }
  }
  
  public boolean verifyRangeItems( PAFilter filter ) {
    boolean res = true;
    Select select;
    List<String> values = filter.getValues();
    switch ( filter.getCondition() ) {
      case BETWEEN:
        verifyBetweenRangeItems( values.get( 0 ), values.get( 1 ) );
        break;

      case AFTER:
      case BEFORE:
        select = new Select( rangeSelect1.getElement() );
        if ( !select.getFirstSelectedOption().getText().equals( values.get( 0 ) ) ) {
          res = false;
        }
        break;

      default:
        break;
    }
    return res;
  }

  public boolean verifyBetweenRangeItems( String value1, String value2 ) {
    boolean res = true;
    Select select;
    select = new Select( rangeSelect1.getElement() );
    if ( !select.getFirstSelectedOption().getText().equals( value1 ) ) {
      res = false;
    }

    select = new Select( rangeSelect2.getElement() );
    if ( !select.getFirstSelectedOption().getText().equals( value2 ) ) {
      res = false;
    }

    return res;
  }

  public void setFilterOnAttrNumFilter( String value ) {
    select( attributeNumericFilter, value );
  }

  public SoftAssert verifyFilterOptions() {
    SoftAssert softAssert = new SoftAssert();
    for ( FilterConditionType conditionType : PAFilter.FilterConditionType.values() ) {
      if ( !isElementPresent( format( radioBtnConditionType, conditionType.value ), EXPLICIT_TIMEOUT / 5 ) ) {
        softAssert.fail( conditionType + " option is not present!" );
      }
    }
    softAssert.assertAll();
    return softAssert;
  }
  
  public SoftAssert verifyTimePeriodOptions() {
    SoftAssert softAssert = new SoftAssert();
    for ( TimePeriod timePeriod : PAFilter.TimePeriod.values() ) {
      if ( !isElementPresent( format( timePeriodCheckbox, timePeriod.value ), EXPLICIT_TIMEOUT / 5 ) ) {
        softAssert.fail( timePeriod + " option is not present!" );
      }
    }
    softAssert.assertAll();
    return softAssert;
  }

  public SoftAssert verifySelectFromListOption() {
    SoftAssert softAssert = new SoftAssert();
    long timeout = EXPLICIT_TIMEOUT / 5;

    if ( !isElementPresent( findTextBox, timeout ) ) {
      softAssert.fail( "Search field option is not present!" );
    }

    if ( !isElementPresent( picklistCombinationType, timeout ) ) {
      softAssert.fail( "Options selector is not present!" );
    }

    if ( !isElementPresent( includeOption, timeout ) ) {
      softAssert.fail( "Include option is not present!" );
    }

    if ( !isElementPresent( excludeOption, timeout ) ) {
      softAssert.fail( "Exclude option is not present!" );
    }

    if ( !isElementPresent( leftFilterSelect, timeout ) ) {
      softAssert.fail( "Possible values list is not present!" );
    }

    if ( !isElementPresent( rightFilterSelect, timeout ) ) {
      softAssert.fail( "Selected values list is not present!" );
    }

    if ( !isElementPresent( btnPicklistAdd, timeout ) ) {
      softAssert.fail( "Add arrow is not present!" );
    }

    if ( !isElementPresent( btnPicklistRemove, timeout ) ) {
      softAssert.fail( "Remove arrow is not present!" );
    }

    softAssert.assertAll();
    return softAssert;
  }

  public SoftAssert verifySelectRangeOption() {
    SoftAssert softAssert = new SoftAssert();
    long timeout = EXPLICIT_TIMEOUT / 5;

    if ( !isElementPresent( rangeConditionSelector, timeout ) ) {
      softAssert.fail( "Year drop-down is not present!" );
    }

    if ( !isElementPresent( betweenOption, timeout ) ) {
      softAssert.fail( "Between option is not present!" );
    }

    if ( !isElementPresent( afterOption, timeout ) ) {
      softAssert.fail( "After option is not present!" );
    }

    if ( !isElementPresent( beforeOption, timeout ) ) {
      softAssert.fail( "Before option is not present!" );
    }

    if ( !isElementPresent( rangeSelect1, timeout ) ) {
      softAssert.fail( "range1 drop-down is not present!" );
    }

    if ( !isElementPresent( rangeSelect2, timeout ) ) {
      softAssert.fail( "range2 drop-down is not present!" );
    }

    if ( !isElementPresent( datePickerLink, timeout ) ) {
      softAssert.fail( "Date picker link is not present!" );
    }

    softAssert.assertAll();
    return softAssert;
  }

  public void addAnotherValue( String value ) {
    addAnotherValueLink.click();
    verifyAnotherValueTextBox();
    addAnotherValueTextBox.type( value );
  }

  public void removeAnotherValue() {
    removeAnotherValueButton.click();
  }

  public void verifyAnotherValueTextBox() {
    if ( !isElementPresent( orPreposition, EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "OR preposition is not present!" );
    }

    if ( !isElementPresent( addAnotherValueTextBox, EXPLICIT_TIMEOUT / 5 ) ) {
      Assert.fail( "Another value textbox is not present!" );
    }
  }

  public boolean isAnotherValueTextBoxPresent() {
    return isElementPresent( addAnotherValueTextBox, EXPLICIT_TIMEOUT / 5 );
  }

  public void clickSelectFromDatePicker() {
    datePickerLink.click();
  }
  
  public void setDatePickerValues( String value ) {
    List<String> values = Arrays.asList( value.split( ";" ) );
    if ( values.size() == 2 ) {
      BeginDatePickerPage datePickerPage = openBeginDatePicker();
      datePickerPage.selectDate( values.get( 0 ) );

      EndDatePickerPage endDatePickerPage = openEndDatePicker();
      endDatePickerPage.selectDate( values.get( 1 ) );
    } else {
      Assert.fail( "Incorrect number of values!" );
    }
  }

  public BeginDatePickerPage openBeginDatePicker() {
    Actions actions = new Actions( getDriver() );
    actions.click( beginDatePicker.getElement() ).build().perform();

    return new BeginDatePickerPage( getDriver() );
  }

  public EndDatePickerPage openEndDatePicker() {
    Actions actions = new Actions( getDriver() );
    actions.click( endDatePicker.getElement() ).build().perform();

    return new EndDatePickerPage( getDriver() );
  }
}
