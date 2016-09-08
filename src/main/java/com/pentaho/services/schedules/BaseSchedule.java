package com.pentaho.services.schedules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RangeRecurrency;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.Recurrence;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RecurrencyPattern;
import com.pentaho.services.BaseObject;

public abstract class BaseSchedule extends BaseObject {

  protected static final String ARG_RECURRENCE = "recurrence";
  protected static final String ARG_CALCULATED_TIME = "calculatedTime";
  protected static final String ARG_START_TIME_HOURS = "startTimeHours";
  protected static final String ARG_START_TIME_MINUTES = "startTimeMinutes";
  protected static final String ARG_START_TIME_FORMAT = "startTimeFormat";
  protected static final String ARG_START_DATE = "startDate";
  protected static final String ARG_RECURRENCY_PATTERN = "recurrencyPattern";
  protected static final String ARG_RECURRENCY_VALUES = "recurrencyValues";
  protected static final String ARG_RANGE_RECURRENCY_START = "rangeRecurrencyStart";
  protected static final String ARG_RANGE_RECURRENCY = "rangeRecurrency";
  protected static final String ARG_RANGE_RECURRENCY_END = "rangeReccurencyEnd";

  protected String name;
  protected Recurrence recurrence;
  protected String calculatedTime;
  protected String startTimeHours;
  protected String startTimeMinutes;
  protected String startTimeFormat;
  protected String startDate;
  protected RecurrencyPattern recurrencyPattern;
  protected List<String> recurrencyValues = new ArrayList<String>();
  protected String rangeRecurrencyStart;
  protected RangeRecurrency rangeRecurrency;
  protected String rangeRecurrencyEnd;

  public BaseSchedule( String nameValue ) {
    super( nameValue );
  }

  public BaseSchedule( Map<String, String> args ) {
    super( args );
  }

  public RecurrencyPattern getRecurrencyPattern() {
    return recurrencyPattern;
  }

  public void setRecurrencyPattern( RecurrencyPattern recurrencyPattern ) {
    this.recurrencyPattern = recurrencyPattern;
  }

  public List<String> getRecurrencyValues() {
    return recurrencyValues;
  }

  public void setRecurrencyValues( List<String> recurrencyValues ) {
    this.recurrencyValues = recurrencyValues;
  }

  public String getRangeRecurrencyStart() {
    return rangeRecurrencyStart;
  }

  public void setRangeRecurrencyStart( String rangeRecurrencyStart ) {
    this.rangeRecurrencyStart = rangeRecurrencyStart;
  }

  public String getRangeRecurrencyEnd() {
    return rangeRecurrencyEnd;
  }

  public void setRangeRecurrencyEnd( String rangeRecurrencyEnd ) {
    this.rangeRecurrencyEnd = rangeRecurrencyEnd;
  }

  public Recurrence getRecurrence() {
    return recurrence;
  }

  public String getCalculatedTime() {
    return calculatedTime;
  }

  public String getStartTimeHours() {
    return startTimeHours;
  }

  public String getStartTimeMinutes() {
    return startTimeMinutes;
  }

  public String getStartTimeFormat() {
    return startTimeFormat;
  }

  public String getStartDate() {
    return startDate;
  }

  public RangeRecurrency getRangeRecurrency() {
    return rangeRecurrency;
  }

  public static String getArgName() {
    return ARG_NAME;
  }

  public static String getArgRecurrence() {
    return ARG_RECURRENCE;
  }

  public static String getArgStartTimeHours() {
    return ARG_START_TIME_HOURS;
  }

  public static String getArgStartTimeMinutes() {
    return ARG_START_TIME_MINUTES;
  }

  public static String getArgStartTimeFormat() {
    return ARG_START_TIME_FORMAT;
  }

  public static String getArgStartDate() {
    return ARG_START_DATE;
  }

  public static String getArgRecurrencyPattern() {
    return ARG_RECURRENCY_PATTERN;
  }

  public static String getArgRecurrencyValues() {
    return ARG_RECURRENCY_VALUES;
  }

  public static String getArgRangeRecurrencyStart() {
    return ARG_RANGE_RECURRENCY_START;
  }

  public static String getArgRangeRecurrency() {
    return ARG_RANGE_RECURRENCY;
  }

  public static String getArgRangeRecurrencyEnd() {
    return ARG_RANGE_RECURRENCY_END;
  }

  public abstract void setParameters();

  public abstract void setStartTimeHours( String hours );

  public abstract void setStartTimeMinutes( String minutes );

  public abstract void setStartTimeFormat( String format );

  public abstract void setStartDate( String startDate );

  public abstract void setRecurrencePattern( RecurrencyPattern recurrencyPattern );

  public abstract void setRecurrenceValues( List<String> recurrencyValues, Recurrence recurrence );

  public abstract void setRangeRecurrenceStartDate( String rangeRecurrencyStart );

  public abstract void setRangeRecurrency( RangeRecurrency rangeRecurrency );

  public abstract void setRangeRecurrenceEndDate( String rangeRecurrencyEnd );

}
