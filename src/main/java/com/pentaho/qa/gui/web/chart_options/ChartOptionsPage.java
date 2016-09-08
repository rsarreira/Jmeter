package com.pentaho.qa.gui.web.chart_options;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.pentaho.platform.util.logging.Logger;
import org.testng.Assert;

import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ChartOptionsPage extends AnalyzerReportPage {

  @FindBy( xpath = "//*[@id='standardDialog']//*[text()='{L10N:dlgChartPropsTitle}']" )
  protected ExtendedWebElement dlgChartOptions;

  // -----Tabs-----
  @FindBy( xpath = "//div[@widgetid ='mainTabContainer_tablist_tab1']" )
  private static ExtendedWebElement tabGeneral;

  @FindBy( xpath = "//div[@widgetid ='mainTabContainer_tablist_tab2']" )
  private static ExtendedWebElement tabAxis;

  @FindBy( xpath = "//div[@widgetid ='mainTabContainer_tablist_tab3']" )
  private static ExtendedWebElement tabLegend;

  @FindBy( xpath = "//div[@widgetid ='mainTabContainer_tablist_tab4']" )
  private static ExtendedWebElement tabOther;

  private final ExtendedWebElement[] tabElements = { tabGeneral, tabAxis, tabLegend, tabOther };

  // -----Dialog Buttons-----
  @FindBy( id = "dlgBtnApply" )
  private ExtendedWebElement btnApply;

  @FindBy( id = "dlgBtnSave" )
  private ExtendedWebElement btnSave;

  @FindBy( id = "dlgBtnCancel" )
  private ExtendedWebElement btnCancel;

  private final ExtendedWebElement[] dlgButtons = { btnApply, btnSave, btnCancel };

  public enum ChartOptionsTab {
    GENERAL( tabGeneral ),
    AXIS( tabAxis ),
    LEGEND( tabLegend ),
    OTHER( tabOther );

    private ExtendedWebElement extendedWebElement;

    private ChartOptionsTab( ExtendedWebElement elem ) {
      this.extendedWebElement = elem;
    }

    public ExtendedWebElement getExtendedWebElement() {
      return extendedWebElement;
    }
  }

  // TODO refactor and optimize this for re-usability
  public ChartOptionsPage( WebDriver driver, ExtendedWebElement pageRepresentation ) {
    super( driver );

    if ( this instanceof ChartOptionsPage && ChartOptionsPage.class != this.getClass() ) {
      // apply logic only to children of this class, not the class itself
      // children of ChartOptionsPage are tabs in the dialog, the selected tab has the class
      // "pentaho-tabWidget-selected"
      if ( !pageRepresentation.getAttribute( "class" ).contains( "pentaho-tabWidget-selected" ) ) {
        Assert.fail( "The tab, " + pageRepresentation.getName() + ", is not selected!" );
      }
    } else if ( !isElementPresent( pageRepresentation ) ) {
      Assert.fail( "The EWE that this object represents is not present!" );
    }

  }

  public ChartOptionsPage changeTab( ChartOptionsTab tab ) {

    ChartOptionsPage theChartOptionsTabPage = null;

    click( tab.getExtendedWebElement() );

    /*
     * A tab must be clicked and "visited" before we can create the object that represents it, but we do not want to
     * click if the logic for the specified tab has not been implemented (what if a newer version has more tabs?). This,
     * of course won't give us a helpful error message if a tab is removed...
     */
    switch ( tab ) {
      case GENERAL:
        theChartOptionsTabPage = new GeneralPage( getDriver(), tab.getExtendedWebElement() );
        break;
      case AXIS:
        theChartOptionsTabPage = new AxisPage( getDriver(), tab.getExtendedWebElement() );
        break;
      case LEGEND:
        theChartOptionsTabPage = new LegendPage( getDriver(), tab.getExtendedWebElement() );
        break;
      case OTHER:
        theChartOptionsTabPage = new OtherPage( getDriver(), tab.getExtendedWebElement() );
        break;
      default:
        LOGGER.error( "Unimplemented tab (" + tab.name() + ") selected, returning null!" );
    }

    return theChartOptionsTabPage;
  }
}
