package com.pentaho.qa.web.puc.schedules;

import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.puc.BasePage.Module;
import com.pentaho.qa.gui.web.puc.BrowseFilesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.schedules.SchedulesPage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.schedules.BlockoutTime;
import com.pentaho.services.schedules.Schedule;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestSteps;

public class Scheduling_Smoketest extends WebBaseTest {
  protected SchedulesPage schedulesPage;
  protected BrowseFilesPage browsePage;
  protected HomePage homePage;
  protected BlockoutTime blockoutTime;
  protected Schedule schedule;

  @Test( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert.assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName()
        + "'!" );
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "61670" )
  @CsvDataSourceParameters( dsUid = "name", executeColumn = "TestColumn", executeValue = "schedule_8",
      path = "CSV_data/PUC_Schedules/Schedule.csv" )
  public void dlgSchduleItems( Map<String, String> args ) {
    schedule = new Schedule( args );  
    schedule.create();

    // verification part
    schedule.verify();
  }

  @Test( dataProvider = "DataProvider" )
  @SpiraTestSteps( testStepsId = "61670" )
  @CsvDataSourceParameters( dsUid = "name", executeColumn = "TestColumn", executeValue = "schedule_7",
      path = "CSV_data/PUC_Schedules/Schedule.csv" )
  public void dlgEdit( Map<String, String> args ) {

    /*
     * AdministrationPage administrationPage = (AdministrationPage) homePage.activateModule( Module.ADMINISTRATION );
     * administrationPage.clickMailServer();
     * 
     * // set mail settings. administrationPage.setSMTPSettings( "-", "1100", "-", "-", "-", "-" );
     * 
     * schedulesPage = (SchedulesPage) homePage.activateModule( Module.SCHEDULES );
     * 
     * blockoutTime = new BlockoutTime( args ); blockoutTime.create();
     */

    Schedule newSchedule = new Schedule( args );
    Schedule name = new Schedule( args );
    schedule.edit( newSchedule );

    schedule.delete();
  }
}
