package com.pentaho.qa.gui.web.puc.schedules;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.CustomAssert;
import com.pentaho.services.schedules.Schedule;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class SchedulePage extends ScheduleCommonPage {
  protected Utils utils;

  // Schedule
  @FindBy( id = "schedule-name-input" )
  protected ExtendedWebElement scheduleName;

  @FindBy( id = "generated-content-location" )
  protected ExtendedWebElement scheduleLocation;

  // 'New Schedule'
  @FindBy( xpath = "//div[@class='Caption' and contains(., '{L10N:newSchedule}')]" )
  protected ExtendedWebElement textNewSchedule;

  // 'Buttons'
  @FindBy( xpath = "//*[@class='pentaho-button' and text()='Select']" )
  protected ExtendedWebElement btnSelect;

  // 'Recurrence:'
  @FindBy( xpath = "//div[@class='schedule-label' and text()='{L10N:schedule.recurrenceColon}']" )
  protected ExtendedWebElement textScheduleReccurence;

  // 'This schedule will run using the following parameters:'
  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., '{L10N:scheduleWillRun}')]" )
  protected ExtendedWebElement textScheduleOutputType;

  // 'Would you like to email a copy when the schedule runs?'
  @FindBy( xpath = "//div[@class='gwt-Label' and contains(., \"{L10N:wouldYouLikeToEmail}\")]" )
  protected ExtendedWebElement textScheduleEmail;

  @FindBy( xpath = "//*[@value='%s']" )
  protected ExtendedWebElement reportFormatType;

  @FindBy( xpath = "//*[text()='{L10N:parameterUiDesc']" )
  protected ExtendedWebElement dlgScheduleFormatType;

  @FindBy( xpath = "(//*[@class='pentaho-wizard-panel pentaho-schedule-create']//*[@type='text'])[1]" )
  protected ExtendedWebElement mailTo;

  @FindBy( xpath = "(//*[@class='pentaho-wizard-panel pentaho-schedule-create']//*[@type='text'])[2]" )
  protected ExtendedWebElement mailSubject;

  @FindBy( xpath = "(//*[@class='pentaho-wizard-panel pentaho-schedule-create']//*[@type='text'])[3]" )
  protected ExtendedWebElement mailAttachmentName;

  @FindBy( xpath = "//*[@class='pentaho-wizard-panel pentaho-schedule-create']//*[@class='gwt-TextArea']" )
  protected ExtendedWebElement mailMessage;

  @FindBy( xpath = "//*[text()='{L10N:Yes_txt}']/preceding-sibling::*[@type='radio']" )
  protected ExtendedWebElement radioBtnYes;

  @FindBy( xpath = "//*[text()='{L10N:No_txt}']/preceding-sibling::*[@type='radio']" )
  protected ExtendedWebElement radioBtnNo;

  @FindBy( xpath = "//*[text()='{L10N:scheduleCreated}']" )
  protected ExtendedWebElement dlgScheduleCreated;

  @FindBy( xpath = "//*[text()='{L10N:scheduleUpdatedTitle}']" )
  protected ExtendedWebElement dlgScheduleUpdated;

  public SchedulePage( WebDriver driver ) {
    super( driver );
  }

  public void setRecurrenceValues( List<String> values, Recurrence recurrence ) {
    if ( values.isEmpty() ) {
      LOGGER.info( "The step is skipped because argument is null!" );
    } else
      switch ( recurrence ) {
        case SECONDS:
          setEveryForSecondsReccurence( values.get( 0 ) );
          break;

        case MINUTES:
          setEveryForMinutesReccurence( values.get( 0 ) );
          break;

        case HOURS:
          setEveryForHoursReccurence( values.get( 0 ) );
          break;

        case DAILY:
          setEveryForDailyReccurence( values.get( 0 ) );
          break;

        case WEEKLY:
          setReccurencyDay( values.get( 0 ) );
          break;

        case MONTHLY:
          if ( isRadioBtnSelected( radioBtnDay ) ) {
            setDayValueForPatternMonthlyReccurence( values.get( 0 ) );
          } else {
            setNumberWeekForThePatternMonthlyReccurence( values.get( 0 ) );
            setDayForThePatternMonthlyReccurence( values.get( 1 ) );
          }
          break;

        case YEARLY:
          if ( isRadioBtnSelected( yearlyRadioBtnThe ) ) {
            setNumberWeekForThePatternYearlyReccurence( values.get( 0 ) );
            setDayForThePatternYearlyReccurence( values.get( 1 ) );
            setMonthForThePatternYearlyReccurence( values.get( 2 ) );
          } else {
            setMonthForEveryPatternYearlyReccurence( values.get( 0 ) );
            setEveryValuePatternYearlyReccurence( values.get( 1 ) );
          }
          break;

        case CRON:
          // TODO: If necessary, anyone can add.
          break;

        default:
          CustomAssert.fail( "Please make sure that specified field is right!" );
          break;
      }
  }

  public void setEveryForHoursReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 3 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 3 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setEveryForSecondsReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 1 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 1 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setEveryForMinutesReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 2 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 2 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setEveryForDailyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 4 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 4 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayValueForPatternMonthlyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 5 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 5 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setEveryValuePatternYearlyReccurence( String value ) {
    if ( isElementPresent( cellsRecurrencyPattern.get( 6 ), EXPLICIT_TIMEOUT / 10 ) ) {
      cellsRecurrencyPattern.get( 6 ).type( value );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setMonthForEveryPatternYearlyReccurence( String month ) {
    if ( popupItems.get( 7 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 7 ), month );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setNumberWeekForThePatternYearlyReccurence( String numberWeek ) {
    if ( popupItems.get( 8 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 8 ), numberWeek );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForThePatternYearlyReccurence( String day ) {
    if ( popupItems.get( 9 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 9 ), day );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setMonthForThePatternYearlyReccurence( String month ) {
    if ( popupItems.get( 10 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 10 ), month );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setNumberWeekForThePatternMonthlyReccurence( String numberWeek ) {
    if ( popupItems.get( 5 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 5 ), numberWeek );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setDayForThePatternMonthlyReccurence( String day ) {
    if ( popupItems.get( 6 ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      select( popupItems.get( 6 ), day );
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setStartTime( Schedule schedule ) {
    // time format is defined automatically according to current hour + expected started hour in the future.
    utils = new Utils();
    // index 0 = hours, 1 = minutes, 2 = format.
    List<String> parserTime = utils.getCalculatedTime( schedule.getStartTimeHours(), schedule.getStartTimeMinutes() );

    setStartTimeHours( parserTime.get( 0 ) );
    setStartTimeMinutes( parserTime.get( 1 ) );
    setStartTimeFormat( parserTime.get( 2 ) );
  }

  public void setScheduleName( String name ) {
    if ( name != null ) {
      scheduleName.type( name );
    } else
      LOGGER.info( "Default Schedule name will be used!" );
  }

  public void setRangeRecurrenceStartDate( String date ) {
    if ( date != null ) {
      if ( isElementPresent( cellsRecurrencyPattern.get( 7 ), EXPLICIT_TIMEOUT / 10 ) ) {
        cellsRecurrencyPattern.get( 7 ).type( date );
      }
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setRangeRecurrenceEndDate( String date ) {
    if ( isRadioBtnSelected( radioBtnEndBy ) ) {
      if ( isElementPresent( cellsRecurrencyPattern.get( 8 ), EXPLICIT_TIMEOUT / 10 ) ) {
        cellsRecurrencyPattern.get( 8 ).type( date );
      }
    } else
      LOGGER.info( "Please make sure that specified field is displayed!" );
  }

  public void setScheduleFormatType( String scheduleFormatType ) {
    if ( format( reportFormatType, scheduleFormatType ).isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      format( reportFormatType, scheduleFormatType ).click();
    } else
      Assert.fail( "Please make sure that the specified scheduleFormat is right!" );
  }

  public boolean isDlgScheduleFormatTypeOpened() {
    return ( dlgScheduleFormatType.isElementPresent( EXPLICIT_TIMEOUT / 10 ) );
  }

  public boolean isDlgScheduleEmailOpened() {
    return textScheduleEmail.isElementPresent( EXPLICIT_TIMEOUT / 10 );
  }

  public void setScheduleMailOption( Boolean option ) {
    if ( option && option != null ) {
      radioBtnYes.click();
    } else if ( option ) {
      radioBtnNo.click();
    } else
      LOGGER.info( "The step is skipped because argument is null!" );
  }

  public boolean isRadioBtnYesSelected() {
    return radioBtnYes.getElement().isSelected();
  }

  public void setMailSubject( String subject ) {
    if ( mailSubject.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      mailSubject.type( subject );
    } else
      Assert.fail( "The following field doesn't exist!" );
  }

  public void setMailAttachmentName( String attachmentName ) {
    if ( mailAttachmentName.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      mailAttachmentName.type( attachmentName );
    } else
      Assert.fail( "The following field doesn't exist!" );
  }

  public void setMailMessage( String message ) {
    if ( mailMessage.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      mailMessage.type( message );
    } else
      Assert.fail( "The following field doesn't exist!" );
  }

  public void setMailTo( String mail ) {
    if ( mailTo.isElementPresent( EXPLICIT_TIMEOUT / 10 ) ) {
      mailTo.type( mail );
    } else
      Assert.fail( "The following field doesn't exist!" );
  }

  public void clickBtnCancel() {
    btnCancel.click();
  }

  public boolean isDlgScheduleCreatedOpened() {
    return dlgScheduleCreated.isElementPresent( EXPLICIT_TIMEOUT / 10 );
  }

  public boolean isDlgScheduleUpdatedOpened() {
    return dlgScheduleUpdated.isElementPresent( EXPLICIT_TIMEOUT / 10 );
  }
}
