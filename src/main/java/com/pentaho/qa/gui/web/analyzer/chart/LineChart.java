package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public class LineChart extends CommonChart {
  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'circle']" )
  protected static List<ExtendedWebElement> iconsCircle;

  // Using the "d" attribute as it is the only one that is different between bullet styles and constant in every
  // instantiation of the Line chart
  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'path' and @d='M-3.75,-3.75L3.75,3.75M3.75,-3.75L-3.75,3.75']" )
  protected static List<ExtendedWebElement> iconsCross;

  // Using the "d" attribute as it is the only one that is different between bullet styles and constant in every
  // instantiation of the Line chart
  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'path' and @d='M0,-5.303300858899107L5.303300858899107,0 0,5.303300858899107 -5.303300858899107,0Z']" )
  protected static List<ExtendedWebElement> iconsDiamond;

  // Using the "d" attribute as it is the only one that is different between bullet styles and constant in every
  // instantiation of the Line chart
  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'path' and @d='M-3.75,-3.75L3.75,-3.75 3.75,3.75 -3.75,3.75Z']" )
  protected static List<ExtendedWebElement> iconsSquare;

  // Using the "d" attribute as it is the only one that is different between bullet styles and constant in every
  // instantiation of the Line chart
  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']/*[local-name()='g']/*[local-name()='g']//*[local-name() = 'path' and @d='M0,3.75L4.330127018922194,-3.75 -4.330127018922194,-3.75Z']" )
  protected static List<ExtendedWebElement> iconsTriangle;

  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name()='g'][7]//*[local-name() = 'circle' and @cursor='pointer']" )
  protected List<ExtendedWebElement> circleElems;

  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name()='g'][6]//*[local-name() = 'circle' and @cursor='pointer']" )
  protected List<ExtendedWebElement> multiElems;

  private static final List<String> DRILLED_AXIS_LABELS = new ArrayList<String>( 3 ) {
    {
      add( "Australia" );
      add( "New Zealand" );
      add( "Singapore" );
    }
  };

  private static final List<String> FILTER_LABELS = new ArrayList<String>( 4 ) {
    {
      add( "Years" );
      String[] yearsParams = { "2004" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), yearsParams ) );
      add( "Territory" );
      String[] territoryParams = { "APAC" };
      add( L10N.formatString( L10N.getText( "customFilterSummEQUAL" ), territoryParams ) );
    }
  };

  // Creating list with expected options to feed the ChartProperties enum
  private ArrayList<String> dataLabelsExpectedOptions = new ArrayList<String>() {
    {
      add( ChartPropertyOption.DATA_LABELS_NONE.getExpectedText() );
      add( ChartPropertyOption.DATA_LABELS_CENTER.getExpectedText() );
      add( ChartPropertyOption.DATA_LABELS_LEFT.getExpectedText() );
      add( ChartPropertyOption.DATA_LABELS_RIGHT.getExpectedText() );
      add( ChartPropertyOption.DATA_LABELS_TOP.getExpectedText() );
      add( ChartPropertyOption.DATA_LABELS_BOTTOM.getExpectedText() );
    }
  };

  // Creating list with expected options to feed the ChartProperties enum
  private ArrayList<String> bulletStyleExpectedOptions = new ArrayList<String>() {
    {
      add( ChartPropertyOption.BULLET_STYLE_NONE.getExpectedText() );
      add( ChartPropertyOption.BULLET_STYLE_CIRCLE.getExpectedText() );
      add( ChartPropertyOption.BULLET_STYLE_CROSS.getExpectedText() );
      add( ChartPropertyOption.BULLET_STYLE_DIAMOND.getExpectedText() );
      add( ChartPropertyOption.BULLET_STYLE_SQUARE.getExpectedText() );
      add( ChartPropertyOption.BULLET_STYLE_TRIANGLE.getExpectedText() );
    }
  };

  // Creating list with expected options to feed the ChartProperties enum
  private ArrayList<String> lineWidthExpectedOptions = new ArrayList<String>() {
    {
      add( ChartPropertyOption.LINE_WIDTH_1.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_2.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_3.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_4.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_5.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_6.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_7.getExpectedText() );
      add( ChartPropertyOption.LINE_WIDTH_8.getExpectedText() );
    }
  };

  // Creating list with expected options to feed the ChartProperties enum
  private ArrayList<String> trendTypeExpectedOptions = new ArrayList<String>() {
    {
      add( ChartPropertyOption.TREND_TYPE_NONE.getExpectedText() );
      add( ChartPropertyOption.TREND_TYPE_LINEAR.getExpectedText() );
    }
  };

  private static final String ATTR_NAME_X = "cx";
  private static final String ATTR_NAME_Y = "cy";

  protected Comparator<ExtendedWebElement> comparator = new Comparator<ExtendedWebElement>() {
    public int compare( ExtendedWebElement elem1, ExtendedWebElement elem2 ) {
      int result = 0;
      String x1 = elem1.getAttribute( ATTR_NAME_X );
      String x2 = elem2.getAttribute( ATTR_NAME_X );
      if ( StringUtils.isNotEmpty( x1 ) && StringUtils.isNotBlank( x2 ) ) {
        Double d1 = getDigit( elem1.getAttribute( ATTR_NAME_X ) );
        Double d2 = getDigit( elem2.getAttribute( ATTR_NAME_X ) );
        result = d1.compareTo( d2 );
      }
      if ( result == 0 ) {
        String y1 = elem1.getAttribute( ATTR_NAME_Y );
        String y2 = elem2.getAttribute( ATTR_NAME_Y );
        if ( StringUtils.isNotEmpty( y1 ) && StringUtils.isNotBlank( y2 ) ) {
          Double d1 = getDigit( elem1.getAttribute( ATTR_NAME_Y ) );
          Double d2 = getDigit( elem2.getAttribute( ATTR_NAME_Y ) );
          result = d1.compareTo( d2 );
        }
      }
      return result;
    }
  };

  public LineChart( WebDriver driver, ChartType type ) {
    super( driver, type );
    // Adding the expected options for the properties of the Line Chart
    ChartProperty.DATA_LABELS.setExpectedOptions( dataLabelsExpectedOptions );
    ChartProperty.BULLET_STYLE.setExpectedOptions( bulletStyleExpectedOptions );
    ChartProperty.LINE_WIDTH.setExpectedOptions( lineWidthExpectedOptions );
    ChartProperty.TREND_TYPE.setExpectedOptions( trendTypeExpectedOptions );
  }

  @Override
  protected List<ExtendedWebElement> getBlockElems() {
    List<ExtendedWebElement> elems;
    if ( this.circleElems.isEmpty() ) {
      elems = new ArrayList<ExtendedWebElement>( this.multiElems );
    } else {
      elems = new ArrayList<ExtendedWebElement>( this.circleElems );
      Collections.sort( elems, comparator );
    }
    return elems;
  }

  @Override
  protected int getExpectedSize() {
    return 12;
  }

  @Override
  protected int getDrillingExpectedSize() {
    return 3;
  }

  @Override
  protected List<String> getDrilledAxisLabels() {
    return DRILLED_AXIS_LABELS;
  }

  protected List<String> getFilterLabels() {
    return FILTER_LABELS;
  }

  @Override
  protected int getMultiDrillingExpectedSize() {
    return getDrillingExpectedSize() + 1;
  }

  public boolean isLegendIconExpected( ChartPropertyOption option, Integer expectedSize ) {
    // Depending on the desired bullet style option get the size of the list of elements
    // Expected size should be the sum of every dot on the chart plus the legends
    Integer size = 0;
    switch ( option ) {
      case BULLET_STYLE_CIRCLE:
        size = iconsCircle.size();
        break;
      case BULLET_STYLE_CROSS:
        size = iconsCross.size();
        break;
      case BULLET_STYLE_DIAMOND:
        size = iconsDiamond.size();
        break;
      case BULLET_STYLE_SQUARE:
        size = iconsSquare.size();
        break;
      case BULLET_STYLE_TRIANGLE:
        size = iconsTriangle.size();
        break;
      default:
        size = 0;
        break;
    }
    // Compare size of list of elements with the expected
    if ( size == expectedSize ) {
      LOGGER.info( "Number of elements with the " + option.getExpectedText() + " marker is as expected" );
    } else {
      LOGGER.info( "Number of elements with the " + option.getExpectedText() + " marker is not as expected" );
    }
    return size == expectedSize;
  }

  public boolean isLineWidthWorking( ChartPropertyOption widthOption, Integer expectedSize ) {
    // Given a specific width check if lines on chart reflect the selected option
    String width = widthOption.getExpectedText();
    List<ExtendedWebElement> widthOfLines =
        findExtendedWebElements( By.xpath( "//div[@id='VIZFRAME']//*[local-name()='svg']//*[@stroke-width = '" + width
            + "']" ) );
    if ( widthOfLines.size() == expectedSize ) {
      LOGGER.info( "Lines on chart are shown with " + widthOption.getExpectedText() + "px of width" );
    } else {
      LOGGER.info( "Lines on chart are not shown with " + widthOption.getExpectedText() + "px of width" );
    }
    return widthOfLines.size() == expectedSize;
  }
}
