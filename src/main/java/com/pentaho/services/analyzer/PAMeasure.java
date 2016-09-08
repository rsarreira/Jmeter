package com.pentaho.services.analyzer;

import java.util.Map;

import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.CalculatedMeasurePage;
import com.pentaho.services.BaseObject;
import com.pentaho.services.CustomAssert;

public class PAMeasure extends BaseObject {

  public static final String ARG_FIELD = "Field";
  public static final String ARG_NAME = "Name";
  public static final String ARG_FORMAT = "Format";
  public static final String ARG_DECIMAL_PLACES = "DecimalPlaces";
  public static final String ARG_FORMULA = "Formula";
  public static final String ARG_CALCULATE_SUBTOTALS = "CalculateSubtotals";
  public static final String ARG_APPLY_DATA_SOURCE = "ApplyDataSource";

  protected String field;
  protected String name;
  protected String format;
  protected String decimalPlaces;
  protected String formula;
  protected Boolean calculateSubtotals;
  protected Boolean applyDataSource;
  private MeasureFormat measureFormat;

  private CalculatedMeasurePage calculatedMeasurePage;

  public enum MeasureFormat {
    DEFAULT( "Default" ), GENERAL_NUMBER( "General Number" ), CURRENCY( "Currency" ), PERCENTAGE( "Percentage (%)" ), EXPRESSION(
        "Expression" );

    String measureFormat;

    private MeasureFormat( String measureFormat ) {
      this.measureFormat = measureFormat;
    }

    public String getMeasureFormat() {
      return ( this.measureFormat );
    }

    public static MeasureFormat fromString( String name ) {
      if ( name != null ) {
        for ( MeasureFormat c : MeasureFormat.values() ) {
          if ( name.equalsIgnoreCase( c.getMeasureFormat() ) ) {
            return c;
          }
        }
      }
      return null;
    }
  }

  public PAMeasure( Map<String, String> args ) {
    super( args );
    field = args.get( ARG_FIELD );
    name = args.get( ARG_NAME );
    measureFormat = args.get( ARG_FORMAT ) != null ? MeasureFormat.valueOf( args.get( ARG_FORMAT ) ) : null;
    decimalPlaces = args.get( ARG_DECIMAL_PLACES );
    formula = args.get( ARG_FORMULA );
    calculateSubtotals =
        args.get( ARG_CALCULATE_SUBTOTALS ) != null ? Boolean.valueOf( args.get( ARG_CALCULATE_SUBTOTALS ) ) : null;
    applyDataSource =
        args.get( ARG_APPLY_DATA_SOURCE ) != null ? Boolean.valueOf( args.get( ARG_APPLY_DATA_SOURCE ) ) : null;
  }

  // Copy constructor
  public void copy( PAMeasure measure ) {
    if ( measure.getName() != null ) {
      this.name = measure.getName();
    }

    if ( measure.getMeasureFormat() != null ) {
      this.measureFormat = measure.getMeasureFormat();
    }

    if ( measure.getDecimalPlaces() != null ) {
      this.decimalPlaces = measure.getDecimalPlaces();
    }

    if ( measure.getFormula() != null ) {
      this.formula = measure.getFormula();
    }

    if ( measure.getFormula() != null ) {
      this.formula = measure.getFormula();
    }

    if ( measure.getCalculateSubtotals() != null ) {
      this.calculateSubtotals = measure.getCalculateSubtotals();
    }

    if ( measure.getApplyDataSource() != null ) {
      this.applyDataSource = measure.getApplyDataSource();
    }
  }

  // getter/setter

  public String getField() {
    return field;
  }

  public void setField( String field ) {
    this.field = field;
  }

  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public MeasureFormat getMeasureFormat() {
    return measureFormat;
  }

  public void setMeasureFormat( MeasureFormat measureFormat ) {
    this.measureFormat = measureFormat;
  }

  public String getFormat() {
    return format;
  }

  public void setFormat( String format ) {
    this.format = format;
  }

  public String getDecimalPlaces() {
    return decimalPlaces;
  }

  public void setDecimalPlaces( String decimalPlaces ) {
    this.decimalPlaces = decimalPlaces;
  }

  public String getFormula() {
    return formula;
  }

  public void setFormula( String formula ) {
    this.formula = formula;
  }

  public Boolean getCalculateSubtotals() {
    return calculateSubtotals;
  }

  public void setCalculateSubtotals( Boolean calculateSubtotals ) {
    this.calculateSubtotals = calculateSubtotals;
  }

  public Boolean getApplyDataSource() {
    return applyDataSource;
  }

  public void setApplyDataSource( Boolean applyDataSource ) {
    this.applyDataSource = applyDataSource;
  }

  public CalculatedMeasurePage getCaclulatedMeasurePage() {
    return calculatedMeasurePage;
  }

  private void setParameters() {
    calculatedMeasurePage.setMeasureName( name );
    calculatedMeasurePage.setMeasureFormat( measureFormat );
    calculatedMeasurePage.setDecimalPlaces( decimalPlaces );
    calculatedMeasurePage.setMeasureFormula( formula );
    calculatedMeasurePage.setCalculateSubtotals( calculateSubtotals );
    calculatedMeasurePage.setApplyDataSource( applyDataSource );
    calculatedMeasurePage.clickDlgBtnSave();
  }

  /* -------------- CREATE MEASURE ------------------------------ */

  public void create() {
    // Get report page
    AnalyzerReportPage analyzerReportPage = new AnalyzerReportPage( getDriver() );

    calculatedMeasurePage = analyzerReportPage.addCalculatedMeasure( field );
    setParameters();
  }

  /* -------------- EDIT MEASURE ------------------------------ */

  public CalculatedMeasurePage edit( PAMeasure newPaMeasure ) {
    // Get report page
    AnalyzerReportPage analyzerReportPage = new AnalyzerReportPage( getDriver() );

    newPaMeasure.calculatedMeasurePage = analyzerReportPage.editCalculatedMeasure( this );
    
    newPaMeasure.setParameters();
    copy( newPaMeasure );
    
    return newPaMeasure.calculatedMeasurePage;
  }

  /* -------------- DELETE MEASURE ------------------------------ */

  public void delete() {
    // Get report page
    AnalyzerReportPage analyzerReportPage = new AnalyzerReportPage( getDriver() );
    analyzerReportPage.removeMeasure( name );
  }

  /* -------------- VERIFY MEASURE ------------------------------ */

  public boolean verifyParameters() {
    if ( !calculatedMeasurePage.getMeasureName().equals( name ) ) {
      CustomAssert.fail( "Measure name is not correspond to previously set!!" );
      return false;
    }

    if ( !calculatedMeasurePage.getMeasureFormat().equals( measureFormat ) ) {
      CustomAssert.fail( "Measure format is not correspond to previously set!!" );
      return false;
    }

    if ( !calculatedMeasurePage.getDecimalPlaces().equals( decimalPlaces ) ) {
      CustomAssert.fail( "Decimal places is not correspond to previously set!!" );
      return false;
    }

    if ( !calculatedMeasurePage.getMeasureFormula().equals( formula ) ) {
      CustomAssert.fail( "Measure formula is not correspond to previously set!!" );
      return false;
    }

    if ( calculatedMeasurePage.getCalculateSubtotals() != calculateSubtotals ) {
      CustomAssert.fail( "Calculated Subtotals is not correspond to previously set!!" );
      return false;
    }

    if ( calculatedMeasurePage.getApplyDataSource() != applyDataSource ) {
      CustomAssert.fail( "Apply data source is not correspond to previously set!!" );
      return false;
    }
    return true;
  }

  public boolean verify() {
    AnalyzerReportPage analyzerReportPage = new AnalyzerReportPage( getDriver() );
    calculatedMeasurePage = analyzerReportPage.verifyCalculatedMeasure( name );

    boolean res = verifyParameters();
    analyzerReportPage.clickDlgBtnCancel();

    return res;
  }
}
