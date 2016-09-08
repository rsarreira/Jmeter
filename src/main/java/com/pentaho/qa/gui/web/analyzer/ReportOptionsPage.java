package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ReportOptionsPage extends AnalyzerReportPage {
  private static final String ReportOption = L10N.getText( "dlgReportOptions" );

  @FindBy( xpath = "//*[@id='standardDialog']//*[text()='{L10N:dlgReportOptionsTitle}']" )
  protected ExtendedWebElement dlgReportOptions;

  @FindBy( id = "RO_showEmptyCells" )
  protected static ExtendedWebElement showEmptyCells;

  @FindBy( id = "RO_showRowGrandTotal" )
  protected static ExtendedWebElement chkShowRowGrandTotal;

  @FindBy( id = "RO_showColumnGrandTotal" )
  protected static ExtendedWebElement chkShowColumnGrandTotal;

  @FindBy( id = "RO_useNonVisualTotals" )
  protected static ExtendedWebElement chkUseNonVisualTotals;

  @FindBy( id = "RO_showDrillLinks" )
  protected static ExtendedWebElement chkShowDrillLinks;

  @FindBy( id = "RO_freezeColumns" )
  protected static ExtendedWebElement chkFreezeColumns;

  @FindBy( id = "RO_freezeRows" )
  protected static ExtendedWebElement chkFreezeRows;

  public ReportOptionsPage( WebDriver driver ) {
    super( driver );
    // TODO Auto-generated constructor stub
  }

  public ReportOptionsPage( WebDriver driver, String name ) {
    super( driver, ReportOption );
    // TODO Auto-generated constructor stub
  }

  public enum ReportOption {
    SHOW_ROWS_GRAND_TOTALS( chkShowRowGrandTotal ), SHOW_COLUMNS_GRAND_TOTALS( chkShowColumnGrandTotal ), TOTALS_INCLUDE_FILTERED_OUT_VALUES(
        chkUseNonVisualTotals ), SHOW_DRILL_LINS_ON_MEASURE_CELLS( chkShowDrillLinks ), FREEZE_COLUMN_HEADERS(
        chkFreezeColumns ), FREEZE_ROW_LABELS( chkFreezeRows );

    private ExtendedWebElement webElement;

    private ReportOption( ExtendedWebElement webElement ) {
      this.webElement = webElement;
    }
  }

  public void check( ReportOption option ) {
    if ( !option.webElement.isChecked() ) {
      option.webElement.check();
    }
  }

  public void uncheck( ReportOption option ) {
    if ( option.webElement.isChecked() ) {
      option.webElement.uncheck();
    }
  }

  public boolean isOpened() {
    return isElementPresent( dlgReportOptions );
  }

  public void setBlankCell( String text ) {
    showEmptyCells.type( text );
  }
}
