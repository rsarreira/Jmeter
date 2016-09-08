package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.pentaho.services.analyzer.PAMeasure.MeasureFormat;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class CalculatedMeasurePage extends AnalyzerReportPage {
  private final static String VALUE = "value";

  @FindBy( xpath = "//*[@id='dialogTitleText']" )
  protected ExtendedWebElement dlgCreateCalculatedMeasure;

  @FindBy( id = "MA_name" )
  protected ExtendedWebElement fieldDisplayName;

  @FindBy( xpath = "//option[@value='Default']" )
  protected static ExtendedWebElement defaultFormat;

  @FindBy( xpath = "//option[@value='General Number']" )
  protected static ExtendedWebElement generalNumberFormat;

  @FindBy( xpath = "//option[@value='Currency ($)']" )
  protected static ExtendedWebElement currencyFormat;

  @FindBy( xpath = "//option[@value='Percentage (%)']" )
  protected static ExtendedWebElement percentageFormat;

  @FindBy( xpath = "//option[@value='Expression']" )
  protected static ExtendedWebElement expressionFormat;

  @FindBy( id = "MA_formatCategory" )
  protected ExtendedWebElement measureFormat;

  @FindBy( id = "MA_formatScale" )
  protected ExtendedWebElement decimalPlaces;

  @FindBy( id = "MA_measures" )
  protected ExtendedWebElement measuresSection;

  @FindBy( id = "MA_content" )
  protected ExtendedWebElement formulaSection;

  @FindBy( id = "MA_addField" )
  protected ExtendedWebElement addField;

  // available operations

  @FindBy( id = "MA_ADD" )
  protected ExtendedWebElement btnAdd;

  @FindBy( id = "MA_SUB" )
  protected ExtendedWebElement btnSub;

  @FindBy( id = "MA_MUL" )
  protected ExtendedWebElement btnMul;

  @FindBy( id = "MA_DIV" )
  protected ExtendedWebElement btnDiv;

  @FindBy( id = "MA_LP" )
  protected ExtendedWebElement btnLP;

  @FindBy( id = "MA_RP" )
  protected ExtendedWebElement btnRP;

  @FindBy( id = "MA_clear" )
  protected ExtendedWebElement btnClear;

  // Subtotal Caclualation options

  @FindBy( id = "MA_calculateSubtotalsUsingFormula" )
  protected ExtendedWebElement calculateSubtotals;

  @FindBy( id = "MA_applyDataSource" )
  protected ExtendedWebElement applyDataSource;
  
  // Error dlg
  
  @FindBy( xpath = "//span[@class='dlgError']" )
  protected ExtendedWebElement dlgError;
  

  public CalculatedMeasurePage( WebDriver driver ) {
    super( driver );
    // TODO Auto-generated constructor stub
  }

  public CalculatedMeasurePage( WebDriver driver, String name ) {
    super( driver, name );
    // TODO Auto-generated constructor stub
  }

  public void setMeasureFormat( MeasureFormat format ) {
    select( measureFormat, format.getMeasureFormat() );
  }

  public void setMeasureName( String name ) {
    if ( !fieldDisplayName.isElementPresent() ) {
      LOGGER.error( "The field " + fieldDisplayName.getText() + " is not displayed!" );
    }
    fieldDisplayName.type( name );
  }

  public void setDecimalPlaces( String decimal ) {
    int value = Integer.parseInt( decimal );
    if ( value <= 0 & value >= 10 ) {
      LOGGER.error( "Unsupported Decimal value for Calculated Measure" );
    }
    select( decimalPlaces, decimal );
  }

  public void setMeasureFormula( String formula ) {
    formulaSection.type( formula );
  }

  public void setCalculateSubtotals( Boolean subTotals ) {
    if ( subTotals ) {
      if ( !calculateSubtotals.isChecked() ) {
        calculateSubtotals.check();
      } else {
        LOGGER.info( "The option " + calculateSubtotals + " is already checked" );
      }
    } else {
      if ( calculateSubtotals.isChecked() ) {
        calculateSubtotals.uncheck();
      } else {
        LOGGER.info( "The option " + calculateSubtotals + " is already unchecked" );
      }
    }
  }

  public void setApplyDataSource( Boolean dataSource ) {
    if ( dataSource ) {
      if ( !applyDataSource.isChecked() ) {
        applyDataSource.check();
      } else {
        LOGGER.info( "The option " + applyDataSource + " is already checked" );
      }
    } else {
      if ( calculateSubtotals.isChecked() ) {
        calculateSubtotals.uncheck();
      } else {
        LOGGER.info( "The option " + applyDataSource + " is already unchecked" );
      }
    }
  }

  public String getMeasureName() {
    return fieldDisplayName.getAttribute( VALUE );
  }

  public MeasureFormat getMeasureFormat() {
    Select select = new Select( measureFormat.getElement() );
    return MeasureFormat.fromString( select.getFirstSelectedOption().getText() );
  }

  public String getDecimalPlaces() {
    Select select = new Select( decimalPlaces.getElement() );
    return select.getFirstSelectedOption().getText();
  }

  public String getMeasureFormula() {
    return formulaSection.getAttribute( VALUE );
  }

  public boolean getCalculateSubtotals() {
    if ( calculateSubtotals.isChecked() ) {
      return true;
    }
    return false;
  }

  public boolean getApplyDataSource() {
    if ( applyDataSource.isChecked() ) {
      return true;
    }
    return false;
  }

  public String getDlgErrorMessage() {
    return dlgError.getText();
  }
}
