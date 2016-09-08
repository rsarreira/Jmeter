package com.pentaho.qa.gui.web.puc.schedules;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.qa.gui.modules.table.Table;
import com.pentaho.qa.gui.modules.table.Table.TableRow;
import com.pentaho.qa.gui.modules.table.TableFactory;
import com.pentaho.qa.gui.web.puc.BasePage;
import com.pentaho.services.schedules.BlockoutTime;
import com.pentaho.services.schedules.Schedule;
import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class SchedulesPage extends BasePage {
  private String scheduleName;

  // 'Manage Schedules'
  @FindBy( xpath = "//div[@class='workspaceHeading' and contains(., '{L10N:manageSchedules}')]" )
  protected ExtendedWebElement textManageSchedule;

  @FindBy( xpath = "//table/tbody/tr/td/img[@class='gwt-Image icon-small icon-refresh']" )
  protected List<ExtendedWebElement> imagesRefresh;

  @FindBy( xpath = "//tr/td/div[text()='%s']" )
  protected ExtendedWebElement scheduleRowByText;

  public static final String[] SCHEDULE_TABLE_HEADERS =
      { "Schedule Name", "Repeats", "Source File", "Output Location", "Last Run", "Next Run", "Created By", "Status" };

  @FindBy( id = "schedule-table" )
  public ExtendedWebElement scheduleTable;

  @FindBy( id = "" )
  protected ExtendedWebElement scheduleRow;

  @FindBy( xpath = "//*[text()='{L10N:createBlockoutTime}']" )
  protected ExtendedWebElement btnBlockoutTime;

  @FindBy( xpath = "//*[@class='dataTable']//*[text()]" )
  protected List<ExtendedWebElement> blockoutTable;

  @FindBy( xpath = "//*[@class='gwt-Image pentaho-addbutton']" )
  protected ExtendedWebElement btnAddBlockout;

  @FindBy(
      xpath = "//*[@class='schedulesPanel blockout-schedules-panel-wrapper']//*[@class='gwt-Image pentaho-editbutton']" )
  protected ExtendedWebElement btnEditBlockout;

  @FindBy( xpath = "//*[@class='schedulesPanel schedules-panel-wrapper']//*[@class='gwt-Image pentaho-editbutton']" )
  protected ExtendedWebElement btnEditSchedule;

  @FindBy( xpath = "//*[@class='schedulesPanel schedules-panel-wrapper']//*[@class='gwt-Image pentaho-deletebutton']" )
  protected ExtendedWebElement btnRemoveSchedule;

  @FindBy(
      xpath = "//*[@class='schedulesPanel blockout-schedules-panel-wrapper']//*[@class='gwt-Image pentaho-deletebutton']" )
  protected ExtendedWebElement btnDeleteBlockout;

  @FindBy( xpath = "//*[contains(@class,'cellTableCell')]/div[text()]" )
  protected List<ExtendedWebElement> schedulesTable;

  public SchedulesPage( WebDriver driver ) {
    this( driver, "" );
  }

  public SchedulesPage( WebDriver driver, String scheduleName ) {
    super( driver );
    if ( !isOpened( textManageSchedule ) ) {
      Assert.fail( "SchedulesPage is not opened!" );
    }
    this.scheduleName = scheduleName;
    scheduleRow = format( scheduleRowByText, scheduleName );
  }

  Table readScheduleTable() {
    Table table = null;
    // reinitialize tableRoot element as after disappearing schedule row DOM structure is different
    ExtendedWebElement tableRoot = findExtendedWebElement( scheduleTable.getBy() );
    try {
      table = new TableFactory().create( tableRoot, SCHEDULE_TABLE_HEADERS );
    } catch ( Exception e ) {
      if ( !e.getMessage().contains( "stale element reference" ) && !e.getMessage().contains(
          "Element not found in the cache" ) && !e.getMessage().contains( "Element is no longer attached to the DOM" )
          && !e.getMessage().contains( "Element is no longer valid" ) && !e.getMessage().contains(
              "Element is no longer" ) ) {

        // if 'stale element reference' present then schedule disappeared as expected
        Assert.fail( "Unrecognized exception during schedule table data populating! " + e.getMessage() );
      }
    }
    return table;
  }

  public void verifyScheduledItem() {
    // verify that schedule is present in ManageSchedules module and wait until it completes.
    if ( isElementPresent( scheduleRow ) ) {
      LOGGER.info( "Schedule '" + scheduleName + "' is listed in the Manage Schedules window" );
    } else {
      Assert.fail( "TS042177: Schedule '" + scheduleName + "' is not listed in the Manage Schedules window" );
    }

    int n = 0;
    boolean found = true; // true as there is no Assert above
    boolean completed = false;
    while ( !completed && ++n < 20 ) {
      Table tableSchedule = readScheduleTable();
      if ( tableSchedule == null ) {
        LOGGER.warn( "Schedule table was not rendered correctly. Trying one more time..." );
        continue;
      }
      TableRow scheduleRow = tableSchedule.findRowByCellValue( "Schedule Name", scheduleName );
      if ( scheduleRow == null ) {
        continue;
      }

      String status = scheduleRow.getCell( "Status" ).getValue();
      if ( status.equals( "COMPLETE" ) ) {
        LOGGER.info( "COMPLETED status is detected for '" + scheduleName + "'" );
        completed = true;
        break;
      } else {
        LOGGER.info( "Current '" + scheduleName + "' schedule status is: " + status );
        pause( EXPLICIT_TIMEOUT / 10 );
      }
      // first refresh image is inside hidden block
      click( imagesRefresh.get( 1 ) );
    }

    if ( !completed && !found ) {
      Assert.fail( "TS042005: '" + scheduleName + "' schedule status is not completed!" );
    } else {
      LOGGER.info( "'" + scheduleName + "' schedule status is completed." );
    }

    completed = true; // completed true as there is no Assert above
    boolean disappeared = false;
    n = 0;
    while ( !disappeared && ++n < 20 ) {
      Table tableSchedule = readScheduleTable();
      if ( tableSchedule == null ) {
        LOGGER.warn( "Unable to parse schedule table." );
        continue;
      }
      TableRow scheduleRow = tableSchedule.findRowByCellValue( "Schedule Name", scheduleName );
      if ( scheduleRow == null ) {
        LOGGER.info( scheduleName + " disappeared from schdule module." );
        disappeared = true;
        break;
      }

      pause( EXPLICIT_TIMEOUT / 10 );
      // first refresh image is inside hidden block
      click( imagesRefresh.get( 1 ) );
    }

    if ( !disappeared ) {
      LOGGER.error( "TS042005: '" + scheduleName + "' schedule is not disappeared after " + EXPLICIT_TIMEOUT / 10 * 20
          + " sec!" );
    }
  }

  public void clickBtnBlockoutTime() {
    if ( isElementPresent( btnBlockoutTime, EXPLICIT_TIMEOUT / 10 ) ) {
      btnBlockoutTime.click();
    } else {
      // if any blockout has already created the 'btnBlockoutTime' is absent and button 'addBlockout' is displayed.
      btnAddBlockout.click();
    }
  }

  public BlockoutTimePage addBlockoutTime() {
    clickBtnBlockoutTime();
    return new BlockoutTimePage( getDriver() );
  }

  public boolean verifyListItems( BlockoutTime blockoutTime ) {
    Utils utils = new Utils();
    boolean res = false;
    String convertedTime =
        utils.convertTimeForDefaultTimeZone( blockoutTime.getStartTimeHours() + ":" + blockoutTime.getStartTimeMinutes()
            + " " + blockoutTime.getStartTimeFormat(), blockoutTime.getTimeZone().name() );
    for ( ExtendedWebElement item : blockoutTable ) {
      if ( item.getText().contains( convertedTime.trim() ) ) {
        item.click();
        res = true;
        break;
      }
    }
    return res;
  }

  public BlockoutTimePage editBlockoutTime( BlockoutTime blockoutTime ) {
    if ( verifyListItems( blockoutTime ) ) {
      clickBtnEditBlockout();
    } else
      Assert.fail( "Please make sure that recently created 'blockout time' is displayed!" );
    return new BlockoutTimePage( getDriver() );
  }

  public void deleteBlockoutTime( BlockoutTime blockoutTime ) {
    if ( verifyListItems( blockoutTime ) ) {
      clickBtnDeleteBlockout();
      btnOK.click();
    }
  }

  public void clickBtnEditBlockout() {
    btnEditBlockout.click();
  }

  public void clickBtnDeleteBlockout() {
    btnDeleteBlockout.click();
  }

  public void deleteSchedule( Schedule schedule ) {
    if ( isScheduleExist( schedule ) ) {
      btnRemoveSchedule.click();
      buttonOK();
    } else {
      Assert.fail( "There is no specified schedule!" );
    }
  }

  public boolean isScheduleExist( Schedule schedule ) {
    boolean res = false;
    for ( ExtendedWebElement scheduleName : schedulesTable ) {
      if ( scheduleName.getText().contains( schedule.getScheduleName() ) ) {
        scheduleName.click();
        res = true;
        break;
      }
    }
    return res;
  }

  public SchedulePage editSchedule( Schedule newSchedule ) {
    if ( isScheduleExist( newSchedule ) ) {
      btnEditSchedule.click();
    } else {
      LOGGER.info( "Please make sure that specified schedule is right!" );
    }
    return new SchedulePage( getDriver() );
  }
}
