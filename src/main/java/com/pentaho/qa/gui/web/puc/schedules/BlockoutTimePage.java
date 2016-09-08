package com.pentaho.qa.gui.web.puc.schedules;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.services.CustomAssert;
import com.pentaho.services.schedules.BlockoutTime;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class BlockoutTimePage extends ScheduleCommonPage {
  protected Utils utils;

  @FindBy( xpath = "//select[@class='gwt-ListBox']" )
  protected List<ExtendedWebElement> popupItems;

  @FindBy( xpath = "//*[@class='timeZonePicker']" )
  protected ExtendedWebElement timeZonePicker;

  @FindBy( xpath = "//*[text()='{L10N:schedule.duration}']" )
  protected ExtendedWebElement radioBtnDuration;

  @FindBy( xpath = "//*[text()='{L10N:schedule.endTime}']" )
  protected ExtendedWebElement radioBtnEndTime;

  @FindBy( xpath = "//*[@class='schedule-editor-caption-panel']//*[@type='text']" )
  protected List<ExtendedWebElement> cellsRecurrencyPattern;

  @FindBy( xpath = "//*[@class='start-date-picker']" )
  protected ExtendedWebElement startDatePicker;

  @FindBy( xpath = "//*[@class='end-date-picker']" )
  protected ExtendedWebElement endDatePicker;

  public BlockoutTimePage( WebDriver driver ) {
    super( driver );
  }

  public void setDurationDays( String day ) {
    if ( day != null ) {
      select( popupItems.get( 9 ), day );
    } else
      LOGGER.info( "The step is skipped because argument is null!" );
  }

  public boolean setDurationHours( String hours ) {
    boolean res = false;
    if ( popupItems.get( 10 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      if ( hours != null ) {
        select( popupItems.get( 10 ), hours );
        res = true;
      }
    }
    return res;
  }

  public boolean setDurationMinutes( String minutes ) {
    boolean res = false;
    if ( popupItems.get( 11 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      if ( minutes != null ) {
        select( popupItems.get( 11 ), minutes );
        res = true;
      }
    }
    return res;
  }

  public boolean setEndsHours( String hours ) {
    boolean res = false;
    if ( popupItems.get( 6 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      if ( hours != null ) {
        select( popupItems.get( 6 ), hours );
        res = true;
      }
    }
    return res;
  }

  public boolean setEndsMinutes( String minutes ) {
    boolean res = false;
    if ( popupItems.get( 7 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      if ( minutes != null ) {
        select( popupItems.get( 7 ), minutes );
        res = true;
      }
    }
    return res;
  }

  public void setEndsFormat( TimeFormat endsFormat ) {
    if ( endsFormat != null ) {
      select( popupItems.get( 8 ), endsFormat.getFormat() );
    } else
      LOGGER.info( "The step is skipped because argument is null!" );
  }

  public void setTimeZone( TimeZone timeZone ) {
    if ( timeZone != null ) {
      select( timeZonePicker, timeZone.getZone() );
    } else
      LOGGER.info( "The step is skipped because argument is null!" );
  }

  public void selectEndTime() {
    radioBtnEndTime.click();
  }

  public void setEndsType( EndsType endsType ) {
    switch ( endsType ) {
      case END_TIME:
        selectEndTime();
        break;

      case DURATION:
        selectDuration();
        break;

      default:
        LOGGER.error( "Please make sure that type is specified right!" );
        break;
    }
  }

  public void selectDuration() {
    radioBtnDuration.click();
  }

  public void setDurationEndsHours( String hours ) {
    if ( !setDurationHours( hours ) ) {
      if ( !setEndsHours( hours ) ) {
        CustomAssert.fail( "Please make that the following field is displayed in the frame!" );
      }
    }
  }

  public void setDurationEndsMinutes( String minutes ) {
    if ( !setDurationMinutes( minutes ) ) {
      if ( !setEndsMinutes( minutes ) ) {
        CustomAssert.fail( "Please make that the following field is displayed in the frame!" );
      }
    }
  }

  public void setEveryForDailyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 4 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 4 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForMonthlyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 5 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 5 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForYearlyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 6 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 6 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setMonthForEveryPattern( String month ) {
    if ( popupItems.get( 14 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 14 ), month );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setNumberWeekForThePatternYearlyReccurence( String numberWeek ) {
    if ( popupItems.get( 15 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 15 ), numberWeek );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForThePatternYearlyReccurence( String day ) {
    if ( popupItems.get( 16 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 16 ), day );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setMonthForThePatternYearlyReccurence( String month ) {
    if ( popupItems.get( 17 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 17 ), month );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForThePatternMonthlyReccurence( String day ) {
    if ( popupItems.get( 13 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 13 ), day );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setNumberWeekForThePatternMonthlyReccurence( String numberWeek ) {
    if ( popupItems.get( 12 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 12 ), numberWeek );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setRangeRecurrenceStartDate( String date ) {
    if ( date != null ) {
      startDatePicker.type( date );
    } else
      LOGGER.info( "The step is skipped because argument is null!" );
  }

  public void setRangeRecurrenceEndDate( String date ) {
    if ( isRadioBtnSelected( radioBtnEndBy ) ) {
      endDatePicker.type( date );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setRecurrenceValues( List<String> values, Recurrence recurrence ) {
    if ( values.isEmpty() ) {
      LOGGER.info( "The step is skipped because argument is null!" );
    } else
      switch ( recurrence ) {
        case DAILY:
          setEveryForDailyReccurence( values.get( 0 ) );
          break;

        case WEEKLY:
          setReccurencyDay( values.get( 0 ) );
          break;

        case MONTHLY:
          if ( isRadioBtnSelected( radioBtnDay ) ) {
            setDayForMonthlyReccurence( values.get( 0 ) );
          } else {
            setNumberWeekForThePatternMonthlyReccurence( values.get( 0 ) );
            setDayForThePatternMonthlyReccurence( values.get( 1 ) );
          }
          break;

        case YEARLY:
          if ( !isRadioBtnSelected( yearlyRadioBtnThe ) ) {
            setDayForYearlyReccurence( values.get( 0 ) );
          } else {
            setNumberWeekForThePatternYearlyReccurence( values.get( 0 ) );
            setDayForThePatternYearlyReccurence( values.get( 1 ) );
            setMonthForThePatternYearlyReccurence( values.get( 2 ) );
          }
          break;

        default:
          CustomAssert.fail( "Please make sure that specified field is right!" );
          break;
      }
  }

  public void setStartTime( BlockoutTime blockoutTime ) {
    // time format is defined automatically according to current hour + expected started hour in the future.
    utils = new Utils();
    // index 0 = hours, 1 = minutes, 2 = format.
    List<String> parserTime =
        utils.getCalculatedTime( blockoutTime.getStartTimeHours(), blockoutTime.getStartTimeMinutes() );
    setStartTimeHours( parserTime.get( 0 ) );
    setStartTimeMinutes( parserTime.get( 1 ) );
    setStartTimeFormat( parserTime.get( 2 ) );
  }
}
