package com.pentaho.qa.gui.web.pir;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.pir.PIRFilter;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRFilterPanelPage extends PIRReportPage {

  public static final String CSS_ARROW_ITEM = "input.dijitArrowButtonInner";
  public static final String XPATH_OPERATOR_ITEM =
      "../div[@data-dojo-attach-point='conditionsNode']//div[@class='gem-label']";

  public static final String OR_OPERATOR = L10N.getText( "OR" );
  public static final String AND_OPERATOR = L10N.getText( "AND" );

  public PIRFilterPanelPage( WebDriver driver ) {
    super( driver );
  }

  @FindBy(
      xpath = "//div[@class='gem-label' and contains(text(),'%s') and contains(text(),'%s') and contains(text(),'%s')]" )
  protected ExtendedWebElement filterItem;

  // 'OR'
  @FindBy( xpath = "//table[contains(@class, 'dijitMenuActive')]//td[text()='{L10N:OR}']" )
  protected ExtendedWebElement popupOperatorOR;

  // 'AND
  @FindBy( xpath = "//table[contains(@class, 'dijitMenuActive')]//td[text()='{L10N:AND}']" )
  protected ExtendedWebElement popupOperatorAND;

  @FindBy( css = ".pentaho-operator" )
  protected List<ExtendedWebElement> operators;

  @FindBy(
      xpath = "//div[@class='gem-label' and contains(text(),'%s') and contains(text(),'%s') and contains(text(), '%s')]/../div[@class='gemMenuHandle']" )
  protected ExtendedWebElement filterItemArrow;

  @FindBy( id = "filterActionContextMenu" )
  protected ExtendedWebElement filterPopup;

  @FindBy( id = "filterIndentMenu" )
  protected ExtendedWebElement filterIndentMenu;

  @FindBy( id = "filterMoveDownMenu" )
  protected ExtendedWebElement filterMoveDownMenu;

  @FindBy( id = "filterMoveUpMenu" )
  protected ExtendedWebElement filterMoveUpMenu;

  @FindBy( id = "filterDeleteMenu" )
  protected ExtendedWebElement filterDeleteMenu;

  @FindBy( id = "filterEditMenu" )
  protected ExtendedWebElement filterEditMenu;

  @FindBy( id = "applyFilterButton" )
  protected ExtendedWebElement btnApplyFilter;

  @FindBy( css = ".pentaho-human-readable-filter-string" )
  protected ExtendedWebElement txtReadableFilter;

  @FindBy( id = "filterPanelHint" )
  protected ExtendedWebElement txtNoFilters;

  // Delete Filter and Parameter
  @FindBy( xpath = "//div[@id='dijit_Dialog_1']" )
  protected ExtendedWebElement dlgDeleteFilterAndParameter;

  @FindBy( css = "#dijit_Dialog_1 #button0" )
  protected ExtendedWebElement btnYes;

  @FindBy( css = "#dijit_Dialog_1 #button1" )
  protected ExtendedWebElement btnNo;

  public boolean isFilterExists( PIRFilter filter ) {
    refreshFilterPanel();
    return isElementPresent(
        format( filterItem, filter.getField(), filter.getCondition().getName(), filter.getValue() ),
        EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isFilterNotExist( PIRFilter filter ) {
    pause( 1 );
    return isElementNotPresent( format( filterItem, filter.getField(), filter.getCondition().getName(), filter
        .getValue() ), EXPLICIT_TIMEOUT / 5 );
  }

  private ExtendedWebElement selectFilterOperator( List<PIRFilter> filters ) {
    int match;

    pause( 1 );
    LOGGER.info( "operators size: " + operators.size() );
    for ( ExtendedWebElement operator : operators ) {
      LOGGER.info( "selectFilterOperator->analyzing " + operator.getName() );
      List<WebElement> child_items = operator.getElement().findElements( By.xpath( XPATH_OPERATOR_ITEM ) );
      int childs_number = child_items.size();
      if ( childs_number != filters.size() ) {
        LOGGER.error( "childs_number: '" + childs_number + "' is not equal filter.size(): '" + filters.size() + "'" );
        continue;
      }
      match = 0;
      for ( int i = 0; i < filters.size(); i++ ) {
        for ( int j = 0; j < child_items.size(); j++ ) {
          if ( child_items.get( j ).getText().contains( filters.get( i ).getField() )
              && child_items.get( j ).getText().contains( filters.get( i ).getValue() ) ) {
            LOGGER.info( "Finding match #" + match );
            match++;
            child_items.remove( j );
            break;
          }
        }
      }
      if ( childs_number == match ) {
        return operator;
      }
    }
    throw new RuntimeException( "Unable to detect filter operator!" );
  }

  /**
   * Set operator for group of filters
   * 
   * @param fieldNames
   *          field names of filters which form filter group
   * @param values
   *          values for corresponding fields
   * @param operatorValue
   *          OR or AND operator
   * @return true if operator was changed successfully
   */
  public boolean setOperator( List<PIRFilter> filters, String operatorValue ) {
    ExtendedWebElement operator = selectFilterOperator( filters );

    ExtendedWebElement arrow =
        new ExtendedWebElement( operator.getElement().findElement( By.cssSelector( CSS_ARROW_ITEM ) ) );
    click( arrow );

    if ( operatorValue.equals( OR_OPERATOR ) ) {
      click( popupOperatorOR );
    } else if ( operatorValue == AND_OPERATOR ) {
      click( popupOperatorAND );
    }

    return true;
  }

  // Needs to be updated
  protected String getValueFromOperator( ExtendedWebElement operator ) {
    return operator.getElement().findElement( By.cssSelector( ".pentaho-operatorSelect span[role=option]" ) ).getText();
  }

  private void popupFilter( PIRFilter filter ) {
    ExtendedWebElement filterArrow =
        format( filterItemArrow, filter.getField(), filter.getCondition().getName(), filter.getValue() );
    if ( isElementPresent( filterArrow, EXPLICIT_TIMEOUT / 10 ) ) {
      click( filterArrow );
    } else {
      Assert.fail( "Unable to open popup menu for filter: '" + filter.getField() + "' or filter arrow doesn't exist!" );
    }
  }

  public boolean verifyFilterPopup( PIRFilter filter ) {
    boolean res = true;
    popupFilter( filter );
    if ( !isElementPresent( filterPopup ) ) {
      res = false;
    }
    applyFilter();
    return res;
  }

  public void indentFilter( PIRFilter filter ) {
    int indentSize = operators.size();
    popupFilter( filter );
    filterIndentMenu.click();

    // Double check that indent really applied.
    if ( operators.size() != indentSize + 1 ) {
      LOGGER.warn( "Indent operation failed from 1st attempt!" );
      popupFilter( filter );
      filterIndentMenu.click();
    }
    // TODO: obligatory add verification points otherwise any mistake here leads to RuntimeException in
    // selectFilterOperator
    if ( operators.size() != indentSize + 1 ) {
      throw new RuntimeException( String.format( "Indent operation for '%s' was not applied!", filter.getName() ) );
    }
  }

  public void moveUpFilter( PIRFilter filter ) {
    popupFilter( filter );
    click( filterMoveUpMenu );
    // TODO: verification items
  }

  public PIRFilterPage editFilter( PIRFilter filter ) {
    popupFilter( filter );
    click( filterEditMenu );
    return new PIRFilterPage( getDriver(), filter.getField() );
  }

  public void deleteFilter( PIRFilter filter ) {
    popupFilter( filter );
    click( filterDeleteMenu );
  }

  public boolean isDeleteFilterAndParameterDialogPresent() {
    boolean res = false;
    switchToFrame();

    if ( isElementPresent( dlgDeleteFilterAndParameter, EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Delete Filter and Parameter dialog is present" );
      res = true;
    }

    return res;
  }

  public void clickYes() {
    click( btnYes, true );
  }

  public void clickNo() {
    click( btnNo, true );
  }

  public void applyFilter() {
    click( btnApplyFilter );
  }

  public String getReadableFilter() {
    return txtReadableFilter.getText();
  }

  public boolean isReadableFilterCorrect( String field, String condition, String value ) {
    // Attempt to get one string from three values
    String filter_text = condition + " " + value;

    return ( getReadableFilter().contains( field ) && getReadableFilter().contains( filter_text ) );
  }

  public String getNoFiltersMessage() {
    // Java Script is used for getting text because getText() method make a trim
    String value =
        (String) ( (JavascriptExecutor) driver )
            .executeScript( "return document.getElementById('filterPanelHint').innerHTML" );
    value = value.replaceAll( "<br>", "<br/>" );
    return value;
  }

}
