package com.pentaho.services.schedules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RangeRecurrency;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.Recurrence;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.RecurrencyPattern;
import com.pentaho.qa.gui.web.puc.schedules.ScheduleCommonPage.TimeFormat;
import com.pentaho.qa.gui.web.puc.schedules.SchedulePage;
import com.pentaho.qa.gui.web.puc.schedules.SchedulesPage;
import com.pentaho.services.utils.ExportType;

public class Schedule extends BaseSchedule {
  protected static final Logger LOGGER = Logger.getLogger( Schedule.class );
  protected static final String ARG_REPORT_PATH = "reportPath";
  protected static final String ARG_REPORT_NAME = "reportName";
  protected static final String ARG_SCHEDULE_NAME = "scheduleName";
  protected static final String ARG_SCHEDULE_FORMAT_TYPE = "scheduleFormatType";
  protected static final String ARG_RUN_MAIL_OPTION = "runMailOption";
  protected static final String ARG_MAIL_TO = "mailTo";
  protected static final String ARG_MAIL_SUBJECT = "mailSubject";
  protected static final String ARG_MAIL_ATTACHMENT_NAME = "mailAttachmentName";
  protected static final String ARG_MAIL_MESSAGE = "mailMessage";

  protected String scheduleId;
  protected String reportPath;
  protected String scheduleName;
  protected String scheduleFormatType;
  protected Boolean runMailOption;
  protected String mailTo;
  protected String mailSubject;
  protected String mailAttachmentName;
  protected String mailMessage;

  protected ScheduleCommonPage commonPage;
  protected SchedulePage schedulePage;

  public Schedule( String name, String scheduleId ) {
    super( name );
    this.scheduleId = scheduleId;
    this.scheduleName = scheduleId.split( "\t" )[1];
  }

  public Schedule( Map<String, String> args ) {
    super( args );
    reportPath = args.get( ARG_REPORT_PATH );
    setScheduleName( args.get( ARG_SCHEDULE_NAME ) );
    recurrence = args.get( ARG_RECURRENCE ) != null ? Recurrence.valueOf( args.get( ARG_RECURRENCE ) ) : null;
    calculatedTime = args.get( ARG_CALCULATED_TIME );
    startTimeHours = args.get( ARG_START_TIME_HOURS );
    startTimeMinutes = args.get( ARG_START_TIME_MINUTES );
    startTimeFormat =
        args.get( ARG_START_TIME_FORMAT ) != null ? TimeFormat.valueOf( args.get( ARG_START_TIME_FORMAT ) ).getFormat()
            : null;
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
    scheduleFormatType = args.get( ARG_SCHEDULE_FORMAT_TYPE );
    runMailOption = args.get( ARG_RUN_MAIL_OPTION ) != null ? Boolean.valueOf( args.get( ARG_RUN_MAIL_OPTION ) ) : null;
    mailTo = args.get( ARG_MAIL_TO );
    mailSubject = args.get( ARG_MAIL_SUBJECT );
    mailAttachmentName = args.get( ARG_MAIL_ATTACHMENT_NAME );
    mailMessage = args.get( ARG_MAIL_MESSAGE );
  }

  /* -------------- GETTERS & SETTERS ------------------------------ */

  public String getReportPath() {
    return reportPath;
  }

  public void setReportPath( String reportPath ) {
    this.reportPath = reportPath;
  }

  public String getScheduleName() {
    return scheduleName;
  }

  public void setScheduleName( String scheduleName ) {
    this.scheduleName = scheduleName;
  }

  public String getScheduleFormatType() {
    return scheduleFormatType;
  }

  public void setScheduleFormatType( ExportType scheduleFormatType ) {
    this.scheduleFormatType = scheduleFormatType.name();
  }

  public String getMailSubject() {
    return mailSubject;
  }

  public void setMailSubject( String mailSubject ) {
    this.mailSubject = mailSubject;
  }

  public String getMailAttachmentName() {
    return mailAttachmentName;
  }

  public void setMailAttachmentName( String mailAttachmentName ) {
    this.mailAttachmentName = mailAttachmentName;
  }

  public String getMailMessage() {
    return mailMessage;
  }

  public void setMailMessage( String mailMessage ) {
    this.mailMessage = mailMessage;
  }

  public String getScheduleId() {
    return scheduleId;
  }

  public void setScheduleId( String scheduleId ) {
    this.scheduleId = scheduleId;
  }

  public Boolean getRunMailOption() {
    return runMailOption;
  }

  public void setRunMailOption( Boolean runMailOption ) {
    this.runMailOption = runMailOption;
  }

  public String getMailTo() {
    return mailTo;
  }

  public void setMailTo( String mailTo ) {
    this.mailTo = mailTo;
  }

  /* -------------- CREATE SCHEDULE ------------------------------ */

  public void create() {
    // Get page
    HomePage homePage = new HomePage( getDriver() );
    BrowseFilesPage browseFilesPage = (BrowseFilesPage) homePage.activateModule( Module.BROWSE_FILES );
    schedulePage = browseFilesPage.openSchedule( this.getReportPath() );
    setParameters();
  }

  /* -------------- EDIT SCHEDULE ------------------------------ */

  public SchedulePage edit( Schedule newSchedule ) {
    // Get page
    HomePage homePage = new HomePage( getDriver() );
    SchedulesPage schedulesPage = (SchedulesPage) homePage.activateModule( Module.SCHEDULES );
    newSchedule.schedulePage = schedulesPage.editSchedule( this );
    newSchedule.setParameters();

    copy( newSchedule );

    return newSchedule.schedulePage;
  }

  /* -------------- DELETE SCHEDULE ------------------------------ */

  public void delete() {
    // Get page
    HomePage homePage = new HomePage( getDriver() );
    SchedulesPage schedulesPage = (SchedulesPage) homePage.activateModule( Module.SCHEDULES );
    schedulesPage.deleteSchedule( this );
  }

  /* -------------- VERIFY SCHEDULE ------------------------------ */

  public void verify() {
    // Get page
    SchedulesPage schedulesPage = new SchedulesPage( getDriver(), this.getScheduleName() );
    schedulesPage.verifyScheduledItem();
  }

  /* -------------- COPY ------------------------------ */

  public void copy( Schedule schedule ) {
    if ( schedule.getScheduleName() != null ) {
      this.scheduleName = schedule.getScheduleName();
    }

    if ( schedule.getRecurrence() != null ) {
      this.recurrence = schedule.getRecurrence();
    }

    if ( schedule.getCalculatedTime() != null ) {
      this.calculatedTime = schedule.getCalculatedTime();
    }

    if ( schedule.getStartTimeHours() != null ) {
      this.startTimeHours = schedule.getStartTimeHours();
    }

    if ( schedule.getStartTimeMinutes() != null ) {
      this.startTimeMinutes = schedule.getStartTimeMinutes();
    }

    if ( schedule.getStartTimeFormat() != null ) {
      this.startTimeFormat = schedule.getStartTimeFormat();
    }

    if ( schedule.getStartDate() != null ) {
      this.startDate = schedule.getStartDate();
    }

    if ( schedule.getRecurrencyPattern() != null ) {
      this.recurrencyPattern = schedule.getRecurrencyPattern();
    }

    if ( schedule.getRecurrencyValues() != null ) {
      this.recurrencyValues = schedule.getRecurrencyValues();
    }

    if ( schedule.getRangeRecurrencyStart() != null ) {
      this.rangeRecurrencyStart = schedule.getRangeRecurrencyStart();
    }

    if ( schedule.getRangeRecurrency() != null ) {
      this.rangeRecurrency = schedule.getRangeRecurrency();
    }

    if ( schedule.getRangeRecurrencyEnd() != null ) {
      this.rangeRecurrencyEnd = schedule.getRangeRecurrencyEnd();
    }

    if ( schedule.getScheduleFormatType() != null ) {
      this.scheduleFormatType = schedule.getScheduleFormatType();
    }

    if ( schedule.getRunMailOption() != null ) {
      this.scheduleFormatType = schedule.getScheduleFormatType();
    }

    if ( schedule.getMailTo() != null ) {
      this.mailTo = schedule.getMailTo();
    }

    if ( schedule.getMailSubject() != null ) {
      this.mailSubject = schedule.getMailSubject();
    }

    if ( schedule.getMailAttachmentName() != null ) {
      this.mailAttachmentName = schedule.getMailAttachmentName();
    }

    if ( schedule.getMailMessage() != null ) {
      this.mailMessage = schedule.getMailMessage();
    }
  }

  @Override
  public void setParameters() {
    schedulePage.setScheduleName( scheduleName );
    commonPage = new ScheduleCommonPage( getDriver() );
    commonPage.buttonNext();
    commonPage.setRecurrence( recurrence );
    // there are two conditions is available. 1 = set calculated time (current time + specified time in CSV file). 2 =
    // set time without calculating.
    if ( commonPage.setCalculatedTime( calculatedTime ) ) {
      schedulePage.setStartTime( this );
    } else {
      commonPage.setStartTimeHours( startTimeHours );
      commonPage.setStartTimeMinutes( startTimeMinutes );
      commonPage.setStartTimeFormat( startTimeFormat );
    }
    setStartDate( startDate );
    setRecurrencePattern( recurrencyPattern );
    setRecurrenceValues( recurrencyValues, recurrence );
    setRangeRecurrency( rangeRecurrency );
    setRangeRecurrenceStartDate( rangeRecurrencyStart );
    setRangeRecurrenceEndDate( rangeRecurrencyEnd );
    commonPage.buttonNext();
    // for the PIR reports must be set report format type.(PDF,CSV,)
    if ( schedulePage.isDlgScheduleFormatTypeOpened() ) {
      schedulePage.setScheduleFormatType( scheduleFormatType );
      commonPage.buttonNext();
    } else {
      commonPage.buttonNext();
    }
    // if the email settings were configured it is possible to send the report by mail
    if ( schedulePage.isDlgScheduleEmailOpened() ) {
      schedulePage.setScheduleMailOption( runMailOption );
      if ( schedulePage.isRadioBtnYesSelected() ) {
        schedulePage.setMailTo( mailTo );
        schedulePage.setMailSubject( mailSubject );
        schedulePage.setMailAttachmentName( mailAttachmentName );
        schedulePage.setMailMessage( mailMessage );
        commonPage.buttonNext();
      }
    }
    // by default in Dialog 'Schedule Created' we click on 'Cancel' button that escape navigation on the schedules
    // page. By default you can add additional option that navigate on schedules page.
    if ( schedulePage.isDlgScheduleCreatedOpened() ) {
      schedulePage.buttonNext();
    }
    // for validating and closing dlgUpdated schedule.
    if ( schedulePage.isDlgScheduleUpdatedOpened() ) {
      commonPage.buttonNext();
    }
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
  public void setStartTimeFormat( String timeFormat ) {
    commonPage.setStartTimeFormat( timeFormat );
  }

  @Override
  public void setStartDate( String startDate ) {
    commonPage.setStartDate( startDate );
  }

  @Override
  public void setRecurrencePattern( RecurrencyPattern recurrencyPattern ) {
    commonPage.setRecurrencePattern( recurrencyPattern );
  }

  @Override
  public void setRecurrenceValues( List<String> recurrencyValues, Recurrence recurrence ) {
    schedulePage.setRecurrenceValues( recurrencyValues, recurrence );
  }

  @Override
  public void setRangeRecurrenceStartDate( String rangeRecurrencyStart ) {
    schedulePage.setRangeRecurrenceStartDate( rangeRecurrencyStart );
  }

  @Override
  public void setRangeRecurrency( RangeRecurrency rangeRecurrency ) {
    commonPage.setRangeRecurrency( rangeRecurrency );
  }

  @Override
  public void setRangeRecurrenceEndDate( String rangeRecurrencyEnd ) {
    schedulePage.setRangeRecurrenceEndDate( rangeRecurrencyEnd );
  }
}
