package com.pentaho.qa.gui.web.puc.schedules;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ScheduleCommonPage extends BasePage {
  protected Utils utils;

  @FindBy( xpath = "//select[@class='gwt-ListBox']" )
  protected List<ExtendedWebElement> popupItems;

  @FindBy( xpath = "//*[@class='schedule-editor-caption-panel']//*[@type='text']" )
  protected List<ExtendedWebElement> cellsRecurrencyPattern;

  @FindBy( xpath = "//*[text()='{L10N:schedule.day}']//preceding-sibling::*[@name='monthly-group']" )
  protected ExtendedWebElement radioBtnDay;

  @FindBy( xpath = "//*[text()='{L10N:schedule.the}']//preceding-sibling::*[@name='monthly-group']" )
  protected ExtendedWebElement montnlyRadioBtnThe;

  @FindBy( xpath = "//*[text()='{L10N:schedule.the}']//preceding-sibling::*[@name='yearly-group']" )
  protected ExtendedWebElement yearlyRadioBtnThe;

  @FindBy( xpath = "//*[@class='weeklyRecurrencePanel']//*[text()='%s']" )
  protected ExtendedWebElement recurrencyDay;

  @FindBy( xpath = "//*[@class='weeklyRecurrencePanel']" )
  protected ExtendedWebElement weeklyRecurrencePanel;

  @FindBy( xpath = "//*[text()='{L10N:schedule.every}']//preceding-sibling::*[@name='daily-group']" )
  protected ExtendedWebElement dailyRadioBtnEvery;

  @FindBy( xpath = "//*[text()='{L10N:schedule.every}']//preceding-sibling::*[@name='yearly-group']" )
  protected ExtendedWebElement yearlyRadioBtnEvery;

  @FindBy( xpath = "//*[text()='{L10N:schedule.everyWeekDay}']/preceding-sibling::*[@class='recurrenceRadioButton']" )
  protected ExtendedWebElement radioBtnEveryWeekDay;

  @FindBy( xpath = "//*[text()='{L10N:DateRangeEditor.noEndDateLabel}']/preceding-sibling::*" )
  protected ExtendedWebElement radioBtnNoEndDate;

  @FindBy( xpath = "//*[text()='{L10N:DateRangeEditor.endByLabel}']/preceding-sibling::*" )
  protected ExtendedWebElement radioBtnEndBy;

  @FindBy( xpath = "//*[text()='{L10N:schedule.endTime}']" )
  protected ExtendedWebElement radioBtnEndTime;

  public ScheduleCommonPage( WebDriver driver ) {
    super( driver );
  }

  public void setRecurrence( Recurrence recurrence ) {
    select( popupItems.get( 1 ), recurrence.getId() );
  }

  public void setStartTimeHours( String hours ) {
    select( popupItems.get( 2 ), hours );
  }

  public void setStartTimeMinutes( String minutes ) {
    select( popupItems.get( 3 ), minutes );
  }

  public void setStartTimeFormat( String timeFormat ) {
    select( popupItems.get( 4 ), timeFormat );
  }

  public boolean setCalculatedTime( String calculatedTime ) {
    boolean res = false;
    if ( calculatedTime.equals( "yes" ) ) {
      res = true;
    }
    return res;
  }

  public void setStartDate( String date ) {
    String today = "today";
    utils = new Utils();
    if ( date == null ) {
      LOGGER.info( "The steps skipped because argument is null!" );
    } else if ( !date.equals( today ) && date != null ) {
      cellsRecurrencyPattern.get( 1 ).type( date );
    } else if ( date.equals( today ) )
      cellsRecurrencyPattern.get( 1 ).type( utils.getCurrentDateTime() );
  }

  public void selectReccurencyPatternDay() {
    radioBtnDay.click();
  }

  public void selectReccurencePatternThe() {
    if ( montnlyRadioBtnThe.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      montnlyRadioBtnThe.click();
    } else
      yearlyRadioBtnThe.click();
  }

  public void setRangeRecurrency( RangeRecurrency range ) {
    if ( range == null ) {
      LOGGER.info( "The step is skipped because argument is null!" );
    } else
      switch ( range ) {
        case END_BY:
          radioBtnEndBy.click();
          break;

        case NO_END_DATE:
          radioBtnNoEndDate.click();
          break;

        default:
          CustomAssert.fail( "Please make sure that specified field is right!" );
          break;
      }
  }

  public void selectReccurencyPatternEveryWeek() {
    radioBtnEveryWeekDay.click();
  }

  public void selectEndTime() {
    radioBtnEndTime.click();
  }

  public void selectRecurrencePatternEvery() {
    if ( dailyRadioBtnEvery.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      dailyRadioBtnEvery.click();
    } else
      yearlyRadioBtnEvery.click();
  }

  public void selectRecurrenceEveryWeekday() {
    radioBtnEveryWeekDay.click();
  }

  public void setRecurrencePattern( RecurrencyPattern pattern ) {
    if ( pattern == null ) {
      LOGGER.info( "The step is skipped because argument is null!" );
    } else
      switch ( pattern ) {
        case DAY:
          selectReccurencyPatternDay();
          break;

        case EVERY:
          selectRecurrencePatternEvery();
          break;

        case EVERY_WEEK_DAY:
          selectReccurencyPatternEveryWeek();
          break;

        case THE:
          selectReccurencePatternThe();
          break;

        default:
          CustomAssert.fail( "Please make sure that specified pattern is exist!" );
          break;
      }
  }

  public boolean isWeeklyRecurrencyPanelDisplayed() {
    return weeklyRecurrencePanel.isElementPresent( EXPLICIT_TIMEOUT / 10 );
  }

  public void setReccurencyDay( String day ) {
    if ( !format( recurrencyDay, day ).isChecked() ) {
      format( recurrencyDay, day ).check();
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public boolean isRadioBtnSelected( ExtendedWebElement radioBtn ) {
    boolean res = false;

    if ( radioBtn.getElement().isSelected() ) {
      res = true;
    }
    return res;
  }

  public enum Recurrence {
    RUN_ONCE( "Run Once" ), SECONDS( "Seconds" ), MINUTES( "Minutes" ), HOURS( "Hours" ), DAILY( "Daily" ), WEEKLY(
        "Weekly" ), MONTHLY( "Monthly" ), YEARLY( "Yearly" ), CRON( "Cron" );

    String id;

    private Recurrence( String id ) {
      this.id = id;
    }

    public String getId() {
      return id;
    }
  }

  public enum TimeFormat {
    PM( "PM" ), AM( "AM" );

    String format;

    private TimeFormat( String format ) {
      this.format = format;
    }

    public String getFormat() {
      return format;
    }
  }

  public enum EndsType {
    DURATION( "Duration" ), END_TIME( "End Time" );

    String type;

    private EndsType( String type ) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }

  public enum RecurrencyPattern {
    EVERY( "Every" ), EVERY_WEEK_DAY( "Every weekday" ), DAY( "Day" ), THE( "The" );

    String pattern;

    private RecurrencyPattern( String pattern ) {
      this.pattern = pattern;
    }

    public String getPattern() {
      return pattern;
    }
  }

  public enum RecurrencyValue {
    FIRST( "first" ), SECOND( "second" ), THIRD( "third" ), FOURTH( "fourth" ), LAST( "last" );

    String value;

    private RecurrencyValue( String value ) {
      this.value = value;
    }
  }

  public enum RecurrencyDay {
    SUNDAY( "Sunday" ), MONDAY( "Monday" ), TUESDAY( "Tuesday" ), WEDNESDAY( "Wednesday" ), THURSDAY(
        "Thursday" ), FRIDAY( "Friday" ), SATURDAY( "Saturday" );

    String day;

    private RecurrencyDay( String day ) {
      this.day = day;
    }
  }

  public enum RecurrencyMonth {
    JANUARY( "January" ), FEBRUARY( "February" ), MARCH( "March" ), APRIL( "April" ), MAY( "May" ), JUNE(
        "June" ), JULY( "July" ), AUGUST( "August" ), SEPTEMBER( "September" ), OCTOBER( "October" ), NOVEMBER(
            "November" ), DECEMBER( "December" );

    String month;

    private RecurrencyMonth( String month ) {
      this.month = month;
    }
  }

  // you could add a new time zone if will be need
  public enum TimeZone {
    JST( "Japan Daylight Time (UTC+0900)" ), EAT( "Eastern African Summer Time (UTC+0300)" ), MST(
        "Mountain Daylight Time (UTC-700)" ), UCT( "Central European Summer Time (UTC+0200)" ), ECT(
            "Central European Summer Time (UTC+0200)" );

    String zone;

    private TimeZone( String zone ) {
      this.zone = zone;
    }

    public String getZone() {
      return zone;
    }
  }

  public enum RangeRecurrency {
    NO_END_DATE( "No end date" ), END_BY( "End by" );

    String type;

    private RangeRecurrency( String type ) {
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }
}
