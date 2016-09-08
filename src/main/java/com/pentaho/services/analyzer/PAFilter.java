package com.pentaho.services.analyzer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerFilterPanelPage;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.services.Filter;
import com.pentaho.services.Report.Workflow;

public class PAFilter extends Filter {
  
  public static final String CONDITION_TYPE = "FilterConditionType";
  public static final String TIME_PERIOD = "TimePeriod";
  
  private AnalyzerFilterPage filterPage;
  
  private FilterConditionType conditionType;
  
  private TimePeriod timePeriod;

  public enum TimePeriod {
    CURRENT_YEAR( "FT_CURRENT_TIME" ),
    PREVIOUS_YEAR( "FT_PREVIOUS_TIME" ),
    NEXT_YEAR( "FT_NEXT_TIME" ),
    PREVIOUS( "FT_TIME_RANGE_PREV" ),
    NEXT( "FT_TIME_RANGE_NEXT" ),
    YEARS_AGO( "FT_TIME_AGO" ),
    YEARS_AHEAD( "FT_TIME_AHEAD" );
  
    public String value;
    
    private TimePeriod( String value ) {
      this.value = value;
    }
  }
   
  public enum FilterConditionType {
    COMONNLY_USED_TIME_PERIOD( "FT_filterTypePreset" ),
    SELECT_FROM_LIST( "FT_filterTypeSelect" ),
    SELECT_RANGE("FT_filterTypeRange" );

    public String value;

    private FilterConditionType( String value ) {
      this.value = value;
    }
  }

  public PAFilter( String fieldName ) {
    super( fieldName );
  }

  public PAFilter( Map<String, String> args ) {
    super( args );
    conditionType =
        args.get( CONDITION_TYPE ) != null ? FilterConditionType.valueOf( args.get( CONDITION_TYPE ) ) : null;
    timePeriod = args.get( TIME_PERIOD ) != null ? TimePeriod.valueOf( args.get( TIME_PERIOD ) ) : null;

  }

  public AnalyzerFilterPage getFilterPage() {
    return filterPage;
  }

  public void setFilterPage( AnalyzerFilterPage filterPage ) {
    this.filterPage = filterPage;
  }
  
  public FilterConditionType getConditionType() {
    return conditionType;
  }

  public void setConditionType( FilterConditionType conditionType ) {
    this.conditionType = conditionType;
  }
  
  public TimePeriod getTimePeriod() {
    return timePeriod;
  }

  public void setTimePeriod( TimePeriod timePeriod ) {
    this.timePeriod = timePeriod;
  }

  public List<String> getValues(){
    return Arrays.asList( value.split( ";" ) );
  }

  @Override
  protected void setParameters() {
    if (conditionType!=null){
      filterPage.setConditionType( conditionType );
      setFilterByDateParameters();
      
    }else{
      filterPage.specifyCondition( specifyCondition );
      filterPage.setCondition( condition );
      filterPage.setValue( value ); 
    }
    
    filterPage.setParamName( paramName );
    filterPage.clickOK();
  }

  @Override
  public void create( Workflow workflow ) {
    AnalyzerReportPage analyzerReportPage= new AnalyzerReportPage( getDriver() );
    filterPage = analyzerReportPage.addFilter( field, workflow );
    //filterPage.verify( dataType, field );
    setParameters();
  }

  @Override
  public void delete() {
    AnalyzerFilterPanelPage filterPanelPage = new AnalyzerFilterPanelPage( getDriver() );
    filterPanelPage.deleteFilter( this );
  }

  public AnalyzerFilterPage edit( PAFilter newFilter ) {
    AnalyzerFilterPanelPage filterPanelPage = new AnalyzerFilterPanelPage( getDriver() );
    newFilter.filterPage = filterPanelPage.editFilter( this );

    // Typing new values
    newFilter.setParameters();

    // Update current filter
    copy( newFilter );

    return newFilter.filterPage;
  }
  
  // Copy constructor
  public void copy( PAFilter filter ) {
    super.copy( filter );

    if ( filter.getSpecifyCondition() != null ) {
      this.conditionType = filter.getConditionType();
    }

    if ( filter.getSpecifyCondition() != null ) {
      this.timePeriod = filter.getTimePeriod();
    }
  }

  private void setFilterByDateParameters() {

    switch ( conditionType ) {
      case COMONNLY_USED_TIME_PERIOD:
        filterPage.setTimePeriod( this.timePeriod );
        filterPage.setTimePeriodValue( this );
        break;
      case SELECT_FROM_LIST:
        filterPage.setListCondition( this.condition );
        filterPage.setListValue( this );
        break;
      case SELECT_RANGE:
        filterPage.setListCondition( this.condition );
        filterPage.setRangeValue( this );
        break;
    }
  }
  
  @Override
  public boolean verify() {
    boolean res = true;
    AnalyzerReportPage reportPage = new AnalyzerReportPage( getDriver() );
    reportPage.showFilterPanel();
    AnalyzerFilterPanelPage filterPanelPage = new AnalyzerFilterPanelPage( getDriver() );
    filterPage = filterPanelPage.editFilter( this );

    if ( conditionType != null ) {
      switch ( conditionType ) {
        case COMONNLY_USED_TIME_PERIOD:
          res = verifyPeriodParameters();
          break;

        case SELECT_FROM_LIST:
          res = verifyListParameters();
          break;

        case SELECT_RANGE:
          res = verifyRangeParameters();
          break;
      }
    } else {
      // Verify not Time fields parameters
      res = verifyParameters();
    }

    filterPage.clickCancel();
    return res;
  }

  @Override
  public boolean verifyParameters() {
    if ( filterPage.isSpecifyCondition() != specifyCondition ) {
      Assert.fail( "Filter type is not correspond to previously set!" );
      return false;
    }
    if ( !filterPage.getCondition().equals( condition ) ) {
      Assert.fail( "Condition value is not correspond to previously set!" );
      return false;
    }
    if ( !filterPage.getValue().equals( value ) ) {
      Assert.fail( "Value is not correspond to previously set!" );
      return false;
    }
    if ( !filterPage.getParamName().equals( paramName ) ) {
      Assert.fail( "Parameter value is not correspond to previously set!" );
      return false;
    }
    return true;
  }

  public boolean verifyPeriodParameters() {
    if ( !filterPage.verifyTimePeriod( this.getTimePeriod() ) ) {
      Assert.fail( "Incorrect timePeriod selected!" );
      return false;
    }
    if ( this.value != null ) {
      if ( !filterPage.verifyTimePeriodValue( this ) ) {
        Assert.fail( "Incorrect filter value!" );
        return false;
      }
    }
    return true;
  }

  public boolean verifyListParameters() {
    if ( !filterPage.verifyListItems( this ) ) {
      Assert.fail( "Incorrect filter parameters!" );
      return false;
    }
    return true;
  }
  
  public boolean verifyRangeParameters() {
    if ( !filterPage.verifyRangeItems( this ) ) {
      Assert.fail( "Incorrect filter parameters!" );
      return false;
    }
    return true; 
  }
}
