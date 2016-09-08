package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class SunburstChart extends PieChart {

  private static final String ATTR_NAME = "d";

  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'text' and @pointer-events='none' and contains(., '%s')]" )
  protected ExtendedWebElement axisLabel;

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'text' and @pointer-events='none']" )
  protected List<ExtendedWebElement> axisScale;

  protected Comparator<ExtendedWebElement> comparator = new Comparator<ExtendedWebElement>() {
    public int compare( ExtendedWebElement elem1, ExtendedWebElement elem2 ) {
      Double d1 = getDigit( elem1.getAttribute( ATTR_NAME ) );
      Double d2 = getDigit( elem2.getAttribute( ATTR_NAME ) );
      return d1.compareTo( d2 );
    }
  };

  public SunburstChart( WebDriver driver, ChartType type ) {
    super( driver, type );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    List<ExtendedWebElement> superElems = super.getBlockElems();
    List<ExtendedWebElement> elements = new ArrayList<ExtendedWebElement>( superElems );
    Collections.sort( elements, comparator );
    return elements;
  }

  @Override
  protected ExtendedWebElement getAxisLabel() {
    return this.axisLabel;
  }

  @Override
  protected List<ExtendedWebElement> getAxisScale() {
    return this.axisScale;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return 4;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return 10;
  }

  @Override
  protected void verifyLabels( SoftAssert softAssert, List<String> axisLabels ) {
  }
}
