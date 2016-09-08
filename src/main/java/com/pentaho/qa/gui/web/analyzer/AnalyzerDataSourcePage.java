package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.qa.gui.web.puc.FramePage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class AnalyzerDataSourcePage extends FramePage {

  @FindBy( xpath = "//select[@id='datasources']/option[starts-with(text(),'%s')]" )
  protected ExtendedWebElement dataSourceItem;

  @FindBy( id = "btnNext" )
  // OK button has definitely btnNext as id value. it is not a mistake
  protected ExtendedWebElement btnOK;
  // 'Select Data Source'
  @FindBy( xpath = "//div[contains(@class,'pentaho-dialog')]/div[text()='{L10N:selectSchemaTitle}']" )
  protected ExtendedWebElement dlgSelectDialog;

  @FindBy( id = "inputAutoRefresh" )
  protected ExtendedWebElement chkBoxAutoRefresh;

  public AnalyzerDataSourcePage( WebDriver driver ) {
    super( driver );
    if ( !isOpened( EXPLICIT_TIMEOUT ) ) {
      Assert.fail( "'Select Data Source' is not opened!" );
    }
  }

  public boolean isOpened( long timeout ) {
    boolean res = false;
    makeClickable();
    switchToFrame();

    res = super.isOpened( dlgSelectDialog, timeout );

    switchToDefault();
    return res;
  }

  public boolean isDataSourcePresent( String dataSourceName ) {
    switchToFrame();
    boolean res = false;

    if ( isElementPresent( format( dataSourceItem, dataSourceName ) ) ) {
      res = true;
    }

    switchToDefault();
    return res;
  }

  public AnalyzerReportPage selectDataSource( String dataSourceName ) {
    makeClickable();
    switchToFrame();
    //makeClickable();
    click( format( dataSourceItem, dataSourceName ), EXPLICIT_TIMEOUT / 10 );

    switchToDefault();
    //makeClickable();
    switchToFrame();
    click( btnOK );
    switchToDefault();
    return new AnalyzerReportPage( driver, NEW_ANALYSIS_REPORT_NAME );

  }

  public boolean isRefreshEnabled() {
    switchToFrame();
    boolean res = false;

    if ( chkBoxAutoRefresh.getElement().isSelected() ) {
      res = true;
    }

    switchToDefault();
    return res;
  }

  public boolean enableAutoRefresh() {
    if ( !isRefreshEnabled() ) {
      switchToFrame();
      click( chkBoxAutoRefresh );
      switchToDefault();
    }

    return isRefreshEnabled();
  }

  public boolean disableAutoRefresh() {
    if ( isRefreshEnabled() ) {
      switchToFrame();
      click( chkBoxAutoRefresh );
      switchToDefault();
    }

    return !isRefreshEnabled();
  }

}
