package com.pentaho.services;

import java.util.Map;

import com.pentaho.services.Report.Workflow;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public abstract class Filter extends BaseObject {
  
  public static final String ARG_FIELD = "Field";
  public static final String ARG_SPECIFY_CONDITION = "SpecifyCondition";
  public static final String ARG_CONDITION = "Condition";
  public static final String ARG_DATA_TYPE = "DataType";
  public static final String ARG_VALUE = "Value";
  public static final String ARG_PARAM_NAME = "ParamName";

  protected String field;
  protected Boolean specifyCondition;
  protected Condition condition;

  protected DataType dataType;

  protected String value;
  protected String paramName;
  
  public enum DataType {
    NUMBER, DATE, STRING;
  }

  public enum Condition {
    // Number
    /*
     * EQUALS( "Equals" ), GREATER_OR_EQUALS( "Greater Than or Equals" ), LESS_OR_EQUALS( "Less Than or Equals" ),
     * GREATER( "Greater Than" ), LESS( "Less Than" ), // String EXACTLY_MATCHES( "Exactly matches" ),
     * EXACTLY_MATCHES_VALUE_OF_PROMPT( "Exactly matches value of Prompt" ), CONTAINS( "Contains" ), ENDS( "Ends with"
     * ), BEGINS_WITH( "Begins with" ), DOESNT_CONTAIN( "Doesn't contains" ), INCLUDES( "Includes" ), EXCLUDES(
     * "Excludes" ), // Date // General NULL( "Is null" ), NOT_NULL( "Is not null" );
     */

    // Number
    EQUALS( "filterConditionOperators_EQUAL" ), GREATER_OR_EQUALS( "filterConditionOperators_GREATER_THAN_EQUAL" ), LESS_OR_EQUALS(
        "filterConditionOperators_LESS_THAN_EQUAL" ), GREATER( "filterConditionOperators_GREATER_THAN" ), LESS(
        "filterConditionOperators_LESS_THAN" ),
    // String
    EXACTLY_MATCHES( "EXACTLY_MATCHES" ), CONTAINS( "dlgAttributeFilterContains" ), ENDS( "ENDS_WITH" ), BEGINS_WITH( "BEGINS_WITH" ), DOESNT_CONTAIN(
        "dlgAttributeFilterNotContains" ),

    // Filter<->Prompt item
    VALUE_OF_PROMPT( "FilterTextValueFromPrompt" ),

    // Date
    INCLUDES( "FT_filterOp_EQUAL" ), EXCLUDES( "FT_filterOp_NOT_EQUAL" ), BETWEEN( "FT_filterOp_BETWEEN" ), AFTER( "FT_filterOp_AFTER" ), BEFORE( "FT_filterOp_BEFORE" ),
    
    // General
    NULL( "Is null" ), NOT_NULL( "Is not null" );

    private String name;

    private Condition( String condition ) {
      this.name = condition;
    }

    public String getName() {
      return L10N.getText( this.name );
    }

    public static Condition fromString( String name ) {
      if ( name != null ) {
        for ( Condition c : Condition.values() ) {
          if ( name.equalsIgnoreCase( c.getName() ) ) {
            return c;
          }
        }
      }
      return null;
    }
  }
  
  public Filter( Map<String, String> args ) {
    super( args );

    field = args.get( ARG_FIELD );

    specifyCondition =
        args.get( ARG_SPECIFY_CONDITION ) != null ? Boolean.valueOf( args.get( ARG_SPECIFY_CONDITION ) ) : null;

    condition = args.get( ARG_CONDITION ) != null ? Condition.valueOf( args.get( ARG_CONDITION ) ) : null;

    dataType = args.get( ARG_DATA_TYPE ) != null ? DataType.valueOf( args.get( ARG_DATA_TYPE ) ) : null;

    value = args.get( ARG_VALUE );
    paramName = args.get( ARG_PARAM_NAME );
  }

  // Used for Filters that get automatically created when a prompt is applied
  public Filter( String fieldName ) {
    super( fieldName );
    field = fieldName;
    specifyCondition = true;
    condition = Condition.EXACTLY_MATCHES;
    value = fieldName;
    paramName = fieldName;
  }

  // Copy constructor
  public void copy( Filter filter ) {
    super.copy( filter );
    
    if ( filter.getField() != null ) {
      this.field = filter.getField();
    }

    if ( filter.getSpecifyCondition() != null ) {
      this.specifyCondition = filter.getSpecifyCondition();
    }

    if ( filter.getCondition() != null ) {
      this.condition = filter.getCondition();
    }

    if ( filter.getDataType() != null ) {
      this.dataType = filter.getDataType();
    }

    if ( filter.getValue() != null ) {
      this.value = filter.getValue();
    }

    if ( filter.getParamName() != null ) {
      this.paramName = filter.getParamName();
    }
  }

  // getter/setter
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getField() {
    return field;
  }

  public void setField( String field ) {
    this.field = field;
  }

  public Boolean getSpecifyCondition() {
    return specifyCondition;
  }

  public void setSpecifyCondition( Boolean specifyCondition ) {
    this.specifyCondition = specifyCondition;
  }

  public Condition getCondition() {
    return condition;
  }

  public void setCondition( Condition condition ) {
    this.condition = condition;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType( DataType dataType ) {
    this.dataType = dataType;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  public String getParamName() {
    return paramName;
  }

  public void setParamName( String paramName ) {
    this.paramName = paramName;
  }
  
  protected abstract void setParameters();
  
  protected abstract void create( Workflow workflow );
  
  protected abstract boolean verifyParameters();
  
  protected abstract void delete();
  
  protected abstract boolean verify();
  
}
