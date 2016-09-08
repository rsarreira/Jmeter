package com.pentaho.services.schedules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.schedules.BlockoutTimePage;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.EndsType;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RangeRecurrency;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.Recurrence;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RecurrencyPattern;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.TimeFormat;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.TimeZone;
import com.pentaho.qa.gui.web.puc.schedules.SchedulesPage;

public class BlockoutTime extends BaseSchedule {

  protected static final String ARG_TIME_ZONE = "timeZone";
  protected static final String ARG_ENDS_TYPE = "endsType";
  protected static final String ARG_END_DAYS = "durationEndDays";
  protected static final String ARG_END_HOURS = "durationEndHours";
  protected static final String ARG_END_MINUTES = "durationEndMinutes";
  protected static final String ARG_END_FORMAT = "durationEndFormat";

  protected TimeZone timeZone;
  protected EndsType endsType;
  protected String endDays;
  protected String endHours;
  protected String endMinutes;
  protected TimeFormat endFormat;

  private BlockoutTimePage blockoutTimePage;
  private ScheduleCommonPage commonPage;

  public BlockoutTime( Map<String, String> args ) {
    super( args );
    name = args.get( ARG_NAME );
    recurrence = args.get( ARG_RECURRENCE ) != null ? Recurrence.valueOf( args.get( ARG_RECURRENCE ) ) : null;
    calculatedTime = args.get( ARG_CALCULATED_TIME );
    startTimeHours = args.get( ARG_START_TIME_HOURS );
    startTimeMinutes = args.get( ARG_START_TIME_MINUTES );
    startTimeFormat =
        args.get( ARG_START_TIME_FORMAT ) != null ? TimeFormat.valueOf( args.get( ARG_START_TIME_FORMAT ) ).getFormat()
            : null;
    timeZone = args.get( ARG_TIME_ZONE ) != null ? TimeZone.valueOf( args.get( ARG_TIME_ZONE ) ) : null;
    endsType = args.get( ARG_ENDS_TYPE ) != null ? EndsType.valueOf( args.get( ARG_ENDS_TYPE ) ) : null;
    endDays = args.get( ARG_END_DAYS );
    endHours = args.get( ARG_END_HOURS );
    endMinutes = args.get( ARG_END_MINUTES );
    endFormat = args.get( ARG_END_FORMAT ) != null ? TimeFormat.valueOf( args.get( ARG_END_FORMAT ) ) : null;
    startDate = args.get( ARG_START_DATE );
    recurrencyPattern =
        args.get( ARG_RECURRENCY_PATTERN ) != null ? RecurrencyPattern.valueOf( args.get( ARG_RECURRENCY_PATTERN ) )
            : null;
    if ( args.get( ARG_RECURRENCY_VALUES ) != null && !args.get( ARG_RECURRENCY_VALUES ).isEmpty() ) {
      recurrencyValues = Arrays.asList( args.get( ARG_RECURRENCY_VALUES ).split( "," ) );
    }
    rangeRecurrencyStart = args.get( ARG_RANGE_RECURRENCY_START );
    rangeRecurrency =
        args.get( ARG_RANGE_RECURRENCY ) != null ? RangeRecurrency.valueOf( args.get( ARG_RANGE_RECURRENCY ) ) : null;
    rangeRecurrencyEnd = args.get( ARG_RANGE_RECURRENCY_END );
  }

  /* -------------- CREATE BLOCKOUT ------------------------------ */

  public void create() {
    // Get page
    SchedulesPage schedulesPage = new SchedulesPage( getDriver() );
    blockoutTimePage = schedulesPage.addBlockoutTime();

    setParameters();

    blockoutTimePage.buttonFinish();
  }

  /* -------------- EDIT BLOCKOUT ------------------------------ */

  public BlockoutTimePage edit( BlockoutTime newBlockout ) {
    // Get page
    SchedulesPage schedulesPage = new SchedulesPage( getDriver() );
    newBlockout.blockoutTimePage = schedulesPage.editBlockoutTime( this );
    newBlockout.setParameters();
    copy( newBlockout );

    blockoutTimePage.buttonFinish();

    return newBlockout.blockoutTimePage;
  }

  /* -------------- DELETE BLOCKOUT ------------------------------ */

  public void delete() {
    // Get page
    SchedulesPage schedulesPage = new SchedulesPage( getDriver() );
    schedulesPage.deleteBlockoutTime( this );
  }

  /* -------------- VERIFY BLOCKOUT ------------------------------ */

  public boolean verify() {
    SchedulesPage schedulesPage = new SchedulesPage( getDriver() );
    if ( !schedulesPage.verifyListItems( this ) ) {
      Assert.fail( "Incorrect Blockout parameters!" );
      return false;
    }
    return true;
  }

  /* -------------- COPY ------------------------------ */

  public void copy( BlockoutTime blockout ) {

    if ( blockout.getRecurrence() != null ) {
      this.recurrence = blockout.getRecurrence();
    }

    if ( blockout.getRecurrence() != null ) {
      this.recurrence = blockout.getRecurrence();
    }

    if ( blockout.getCalculatedTime() != null ) {
      this.calculatedTime = blockout.getCalculatedTime();
    }

    if ( blockout.getStartTimeHours() != null ) {
      this.startTimeHours = blockout.getStartTimeHours();
    }

    if ( blockout.getStartTimeMinutes() != null ) {
      this.startTimeMinutes = blockout.getStartTimeMinutes();
    }

    if ( blockout.getStartTimeFormat() != null ) {
      this.startTimeFormat = blockout.getStartTimeFormat();
    }

    if ( blockout.getStartDate() != null ) {
      this.startDate = blockout.getStartDate();
    }

    if ( blockout.getRecurrencyPattern() != null ) {
      this.recurrencyPattern = blockout.getRecurrencyPattern();
    }

    if ( blockout.getRecurrencyValues() != null ) {
      this.recurrencyValues = blockout.getRecurrencyValues();
    }

    if ( blockout.getRangeRecurrencyStart() != null ) {
      this.rangeRecurrencyStart = blockout.getRangeRecurrencyStart();
    }

    if ( blockout.getRangeRecurrency() != null ) {
      this.rangeRecurrency = blockout.getRangeRecurrency();
    }

    if ( blockout.getRangeRecurrencyEnd() != null ) {
      this.rangeRecurrencyEnd = blockout.getRangeRecurrencyEnd();
    }

    if ( blockout.getTimeZone() != null ) {
      this.timeZone = blockout.getTimeZone();
    }

    if ( blockout.getEndsType() != null ) {
      this.endsType = blockout.getEndsType();
    }

    if ( blockout.getEndDays() != null ) {
      this.endDays = blockout.getEndDays();
    }

    if ( blockout.getEndHours() != null ) {
      this.endHours = blockout.getEndHours();
    }

    if ( blockout.getEndMinutes() != null ) {
      this.endMinutes = blockout.getEndMinutes();
    }

    if ( blockout.getEndFormat() != null ) {
      this.endFormat = blockout.getEndFormat();
    }
  }

  /* -------------- GETTERS & SETTERS ------------------------------ */

  public TimeZone getTimeZone() {
    return timeZone;
  }

  public void setTimeZone( TimeZone timeZone ) {
    this.timeZone = timeZone;
  }

  public EndsType getEndsType() {
    return endsType;
  }

  public void setEndsType( EndsType endsType ) {
    this.endsType = endsType;
  }

  public String getEndDays() {
    return endDays;
  }

  public void setEndDays( String endDays ) {
    this.endDays = endDays;
  }

  public String getEndHours() {
    return endHours;
  }

  public void setEndHours( String endHours ) {
    this.endHours = endHours;
  }

  public String getEndMinutes() {
    return endMinutes;
  }

  public void setEndMinutes( String endMinutes ) {
    this.endMinutes = endMinutes;
  }

  public TimeFormat getEndFormat() {
    return endFormat;
  }

  public void setEndFormat( TimeFormat endFormat ) {
    this.endFormat = endFormat;
  }

  @Override
  public void setParameters() {
    commonPage = new ScheduleCommonPage( getDriver() );
    commonPage.setRecurrence( recurrence );
    // there are two conditions is available. 1 = set calculated time (current time + specified time in CSV file). 2 =
    // set time without calculating.
    if ( commonPage.setCalculatedTime( calculatedTime ) ) {
      blockoutTimePage.setStartTime( this );
    } else {
      commonPage.setStartTimeHours( startTimeHours );
      commonPage.setStartTimeMinutes( startTimeMinutes );
      commonPage.setStartTimeFormat( startTimeFormat );
    }
    blockoutTimePage.setTimeZone( timeZone );
    blockoutTimePage.setEndsType( endsType );
    blockoutTimePage.setDurationDays( endDays );
    blockoutTimePage.setDurationEndsHours( endHours );
    blockoutTimePage.setDurationEndsMinutes( endMinutes );
    blockoutTimePage.setEndsFormat( endFormat );
    setStartDate( startDate );
    setRecurrencePattern( recurrencyPattern );
    setRecurrenceValues( recurrencyValues, recurrence );
    setRangeRecurrenceStartDate( rangeRecurrencyStart );
    setRangeRecurrency( rangeRecurrency );
    setRangeRecurrenceEndDate( rangeRecurrencyEnd );
  }

  @Override
  public void setStartDate( String startDate ) {
    blockoutTimePage.setStartDate( startDate );
  }

  @Override
  public void setRecurrencePattern( RecurrencyPattern recurrencyPattern ) {
    blockoutTimePage.setRecurrencePattern( recurrencyPattern );
  }

  @Override
  public void setRecurrenceValues( List<String> recurrencyValues, Recurrence recurrence ) {
    blockoutTimePage.setRecurrenceValues( recurrencyValues, recurrence );
  }

  @Override
  public void setRangeRecurrenceStartDate( String rangeRecurrencyStart ) {
    blockoutTimePage.setRangeRecurrenceStartDate( rangeRecurrencyStart );
  }

  @Override
  public void setRangeRecurrency( RangeRecurrency rangeRecurrency ) {
    commonPage.setRangeRecurrency( rangeRecurrency );
  }

  @Override
  public void setRangeRecurrenceEndDate( String rangeRecurrencyEnd ) {
    blockoutTimePage.setRangeRecurrenceEndDate( rangeRecurrencyEnd );
  }

  @Override
  public void setStartTimeHours( String hours ) {
    commonPage.setStartTimeHours( hours );
  }

  @Override
  public void setStartTimeMinutes( String minutes ) {
    commonPage.setStartTimeMinutes( minutes );
  }

  @Override
  public void setStartTimeFormat( String format ) {
    commonPage.setStartTimeFormat( format );
  }
}
