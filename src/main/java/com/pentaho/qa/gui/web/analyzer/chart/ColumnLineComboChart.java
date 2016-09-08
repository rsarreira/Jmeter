package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class ColumnLineComboChart extends BarChart {

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'rect' and @cursor='pointer']" )
  protected List<ExtendedWebElement> circleElems;

  public ColumnLineComboChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    List<ExtendedWebElement> elems = super.getBlockElems();
    elems.addAll( this.circleElems );
    return elems;
  }

  @Override
  protected int getExpectedSize() {
    return super.getExpectedSize() * 2;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return super.getDrillingExpectedSize() * 2;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return getDrillingExpectedSize() + 2;
  }
}
