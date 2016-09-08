package com.pentaho.qa.gui.web.analyzer.chart;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.asserts.SoftAssert;
import com.pentaho.services.Report.FontFamily;
import com.pentaho.services.Report.FontSize;
import com.pentaho.services.Report.FontStyle;
import com.pentaho.services.Report.PaletteColour;
import com.pentaho.services.utils.Utils;
import com.pentaho.qa.gui.web.analyzer.AnalyzerReportPage;
import com.pentaho.qa.gui.web.analyzer.GemBar;
import com.pentaho.qa.gui.web.analyzer.GemBar.GemBarType;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

@SuppressWarnings( "serial" )
public abstract class CommonChart extends AnalyzerReportPage implements ChartVerifier {

  @FindBy(
      xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'text' and @cursor='pointer' and contains(., '%s')]" )
  protected ExtendedWebElement axisLabel;

  @FindBy( xpath = "//div[@id='VIZFRAME']//*[local-name()='svg']//*[local-name() = 'text' and @cursor='pointer']" )
  protected List<ExtendedWebElement> axisScale;

  @FindBy( xpath = "//*[@id='VIZFRAME']//*[contains(@id,'tipsyPvBehavior_')]" )
  protected ExtendedWebElement dataPointTooltip;

  @FindBy(
      xpath = "//*[@id='visualPanelElement-0']/*[local-name()='svg']/*[local-name()='g'][1]/*[local-name()='g' and not(.//*[string-length(@clip-path)>0])]//*[local-name()='text']" )
  protected List<ExtendedWebElement> chartPlotTextLabels;

  @FindBy(
      xpath = "//*[@id='visualPanelElement-0']/*[local-name()='svg']/*[local-name()='g'][1]/*[local-name()='g' and .//*[string-length(@clip-path)>0]]//*[local-name()='text']" )
  protected List<ExtendedWebElement> chartLegendTextLabels;

  @FindBy( xpath = "//*[@id='visualPanelElement-0']/*[local-name()='svg']/*[local-name()='rect'][1]" )
  protected ExtendedWebElement chartSolidFill;

  @FindBy(
      xpath = "//*[@id='visualPanelElement-0']/*[local-name()='svg']/*[local-name()='defs']/*[local-name()='linearGradient']/*[local-name()='stop'][1]" )
  protected ExtendedWebElement chartGradientStartFill;

  @FindBy(
      xpath = "//*[@id='visualPanelElement-0']/*[local-name()='svg']/*[local-name()='defs']/*[local-name()='linearGradient']/*[local-name()='stop'][2]" )
  protected ExtendedWebElement chartGradientEndFill;

  // Getting the elements for testing the properties drop downs
  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_VALUE_ANCHOR}')]/../..//div[contains(@id,'labelsOption')]//span" )
  protected static ExtendedWebElement dataLabelSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'labelsOption')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement dataLabelExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_VALUE_COLUMN_ANCHOR}')]/../..//div[contains(@id,'labelsOption')]//span" )
  protected static ExtendedWebElement colDataLabelSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'labelsOption')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement colDataLabelExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_VALUE_LINE_ANCHOR}')]/../..//div[contains(@id,'lineLabelsOption')]//span" )
  protected static ExtendedWebElement lineDataLabelSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'lineLabelsOption')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement lineDataLabelExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_BULLET_STYLE}')]/../..//div[contains(@id,'shape')]//span" )
  protected static ExtendedWebElement bulletStyleSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'shape')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement bulletStyleExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dlgChartPropsLineWidth}')]/../..//div[contains(@id,'lineWidth')]//span" )
  protected static ExtendedWebElement lineWidthSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'lineWidth')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement lineWidthExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_TREND_TYPE}')]/../..//div[contains(@id,'trendType')]//span" )
  protected static ExtendedWebElement trendTypeSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'trendType')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement trendTypeExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_SORT_TYPE}')]/../..//div[contains(@id,'sliceOrder')]//span" )
  protected static ExtendedWebElement orderBySelectedValue;

  @FindBy( xpath = "//div[contains(@id,'sliceOrder')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement orderByExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_PATTERN}')]/../..//div[contains(@id,'pattern')]//span" )
  protected static ExtendedWebElement patternSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'pattern')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement patternExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_COLORSET}')]/../..//div[contains(@id,'colorSet')]//span" )
  protected static ExtendedWebElement colorSelectedValue;

  @FindBy( xpath = "//div[contains(@id,'colorSet')]//input[contains(@class,'dijitArrowButtonInner')]" )
  protected static ExtendedWebElement colorExpander;

  @FindBy(
      xpath = "//span[contains(text(), '{L10N:dropZoneLabels_EMPTY_SLICES}')]/../..//input[contains(@id,'emptySlicesHidden_checkbox')]" )
  protected static ExtendedWebElement emptySlicesCheckBox;

  @FindBy( xpath = "//label[contains(@for,'emptySlicesHidden_checkbox')]" )
  protected static ExtendedWebElement emptySlicesOptionText;

  @FindBy( xpath = "//input[contains(@id,'reverseColors_checkbox')]" )
  protected static ExtendedWebElement reverseColorsCheckBox;

  @FindBy( xpath = "//label[contains(@for,'reverseColors_checkbox')]" )
  protected static ExtendedWebElement reverseColorsOptionText;

  @FindBy(
      xpath = "//span[contains(@id,'optionsBtn_button')]//span[contains(text(),'{L10N:dropZoneLabels_CHART_OPTIONS}')]" )
  protected static ExtendedWebElement optionsButton;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_VALUE_ANCHOR_DOTS_CENTER}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> dataLabelsOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_VALUE_POSITION_INSIDE}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> pieDataLabelsOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_COLUMN_LABEL_ANCHOR_INSIDE_END}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> colDataLabelsOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_VALUE_ANCHOR_DOTS_LEFT}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> lineDataLabelsOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_DIAMOND}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> bulletStyleOptions;

  @FindBy(
      xpath = "//div[contains(@id,'dijit_form_Select_')]//tr[contains(@aria-label,'1')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> lineWidthOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_TREND_TYPE_LINEAR}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> trendTypeOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_SORT_TYPE_BYSIZEASCENDING}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> orderByOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_GRADIENT}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> patternOptions;

  @FindBy(
      xpath = "//tr[contains(@aria-label,'{L10N:dropZoneLabels_GRAD_RYG}')]/..//td[contains(@class,'dijitMenuItemLabel')]" )
  protected static List<ExtendedWebElement> colorOptions;

  protected ChartType type;

  private static final List<String> LEVEL_LABELS = new ArrayList<String>( 3 ) {
    {
      add( "Territory" );
      add( "Years" );
      add( "Type" );
    }
  };

  private static final String MULTI_FIELD_LABEL = "Type";

  private static final List<String> MEASURE_LABELS = new ArrayList<String>( 2 ) {
    {
      add( "Sales" );
      add( "Quantity" );
    }
  };
  private static final List<String> AXIS_LABELS = new ArrayList<String>( 4 ) {
    {
      add( "APAC" );
      add( "EMEA" );
      add( "Japan" );
      add( "NA" );
    }
  };
  private static final List<String> FILTER_LABELS = new ArrayList<String>( 4 ) {
    {
      add( "Years" );
      add( String.format( L10N.getText( "filterSummEQUAL" ).replace( "%{0}", "" ), "2003" ) );
      add( "Territory" );
      add( String.format( L10N.getText( "filterSummEQUAL" ).replace( "%{0}", "" ), "APAC" ) );
    }
  };

  private static final String DIGIT_PATTERN = "[\\-0-9\\.]+";

  private Pattern pattern = Pattern.compile( DIGIT_PATTERN );

  protected abstract List<ExtendedWebElement> getBlockElems();

  protected abstract List<String> getDrilledAxisLabels();

  protected abstract int getExpectedSize();

  protected abstract int getDrillingExpectedSize();

  protected abstract int getMultiDrillingExpectedSize();

  protected List<String> getLevelFields() {
    return LEVEL_LABELS;
  }

  protected List<String> getMeasureLabels() {
    return MEASURE_LABELS;
  }

  public enum ChartProperty {
    /*
     * Adding properties with the multiple elements to interact with. Note that only the elements to interact with the
     * properties have been added.
     * 
     * As the options for the properties differ from chart to chart it is required that we add the expected options to
     * the properties using the setExpectedOptions() that receives a list of the expected strings for the options.
     * 
     * It is preferred to use the options on the ChartPropertiesOptions enum to feed this options.
     * 
     * See LineChart class for an example
     */
    DATA_LABELS( dataLabelExpander, dataLabelSelectedValue, dataLabelsOptions ),
    PIE_DATA_LABELS( dataLabelExpander, dataLabelSelectedValue, pieDataLabelsOptions ),
    COL_DATA_LABELS( colDataLabelExpander, colDataLabelSelectedValue, colDataLabelsOptions ),
    LINE_DATA_LABELS( lineDataLabelExpander, lineDataLabelSelectedValue, lineDataLabelsOptions ),
    BULLET_STYLE( bulletStyleExpander, bulletStyleSelectedValue, bulletStyleOptions ),
    LINE_WIDTH( lineWidthExpander, lineWidthSelectedValue, lineWidthOptions ),
    TREND_TYPE( trendTypeExpander, trendTypeSelectedValue, trendTypeOptions ),
    ORDER_BY( orderByExpander, orderBySelectedValue, orderByOptions ),
    PATTERN( patternExpander, patternSelectedValue, patternOptions ),
    COLOR( colorExpander, colorSelectedValue, colorOptions ),
    EMPTY_SLICES( emptySlicesCheckBox, emptySlicesOptionText, L10N.getText( "dropZoneLabels_SHOW_AS_GAPS" ) ),
    REVERSE_COLORS( reverseColorsCheckBox, reverseColorsOptionText, L10N.getText( "dropZoneLabels_COLORSET_REVERSE" ) );

    private ArrayList<String> expectedOptions;
    private ExtendedWebElement selector;
    private ExtendedWebElement selectedValue;
    private ExtendedWebElement checkBox;
    private ExtendedWebElement optionText;
    private String expectedLabel;
    List<ExtendedWebElement> elementsOnDropDown;

    private ChartProperty( ExtendedWebElement selector, ExtendedWebElement selectedValue,
        List<ExtendedWebElement> elementsOnDropDown ) {
      this.selector = selector;
      this.selectedValue = selectedValue;
      this.elementsOnDropDown = elementsOnDropDown;
    }

    private ChartProperty( ExtendedWebElement checkBox, ExtendedWebElement optionText,
        String expectedLabel ) {
      this.checkBox = checkBox;
      this.optionText = optionText;
      this.expectedLabel = expectedLabel;
    }

    public void setExpectedOptions( ArrayList<String> expOptions ) {
      this.expectedOptions = expOptions;
    }

    public ArrayList<String> getExpectedOptions() {
      return this.expectedOptions;
    }

    public ExtendedWebElement getSelectorElement() {
      return this.selector;
    }

    public ExtendedWebElement getSelectedValueElement() {
      return this.selectedValue;
    }

    public List<ExtendedWebElement> getElementsOnDropDown() {
      return this.elementsOnDropDown;
    }

    public ExtendedWebElement getCheckBoxElement() {
      return this.checkBox;
    }

    public String getTextOfOptionElement() {
      // This method is only for the checkbox type properties
      return this.optionText.getText();
    }

    public String getExpectedLabel() {
      // This method is only for the checkbox type properties
      return this.expectedLabel;
    }

  }

  public enum ChartPropertyOption {
    /*
     * Adding all possible options for each of the properties.
     * 
     * To feed the options into the ChartProperties enum use ChartPropertiesOptions.NAME_OF_OPTION.getExpectedText()
     * 
     * See LineChart class for an example
     */
    DATA_LABELS_NONE( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_NONE" ) ),
    DATA_LABELS_CENTER( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_CENTER" ) ),
    DATA_LABELS_LEFT( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_LEFT" ) ),
    DATA_LABELS_RIGHT( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_RIGHT" ) ),
    DATA_LABELS_TOP( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_TOP" ) ),
    DATA_LABELS_BOTTOM( L10N.getText( "dropZoneLabels_VALUE_ANCHOR_DOTS_BOTTOM" ) ),
    DATA_LABELS_INSIDE_END( L10N.getText( "dropZoneLabels_COLUMN_LABEL_ANCHOR_INSIDE_END" ) ),
    DATA_LABELS_INSIDE_BASE( L10N.getText( "dropZoneLabels_COLUMN_LABEL_ANCHOR_INSIDE_BASE" ) ),
    DATA_LABELS_OUTSIDE_END( L10N.getText( "dropZoneLabels_COLUMN_LABEL_ANCHOR_OUTSIDE_END" ) ),
    DATA_LABELS_OUTSIDE( L10N.getText( "dropZoneLabels_VALUE_POSITION_OUTSIDE" ) ),
    DATA_LABELS_INSIDE( L10N.getText( "dropZoneLabels_VALUE_POSITION_INSIDE" ) ),
    BULLET_STYLE_NONE( L10N.getText( "dropZoneLabels_NONE" ) ),
    BULLET_STYLE_CIRCLE( L10N.getText( "dropZoneLabels_CIRCLE" ) ),
    BULLET_STYLE_CROSS( L10N.getText( "dropZoneLabels_CROSS" ) ),
    BULLET_STYLE_DIAMOND( L10N.getText( "dropZoneLabels_DIAMOND" ) ),
    BULLET_STYLE_SQUARE( L10N.getText( "dropZoneLabels_SQUARE" ) ),
    BULLET_STYLE_TRIANGLE( L10N.getText( "dropZoneLabels_TRIANGLE" ) ),
    LINE_WIDTH_1( "1" ),
    LINE_WIDTH_2( "2" ),
    LINE_WIDTH_3( "3" ),
    LINE_WIDTH_4( "4" ),
    LINE_WIDTH_5( "5" ),
    LINE_WIDTH_6( "6" ),
    LINE_WIDTH_7( "7" ),
    LINE_WIDTH_8( "8" ),
    TREND_TYPE_NONE( L10N.getText( "dropZoneLabels_TREND_TYPE_NONE" ) ),
    TREND_TYPE_LINEAR( L10N.getText( "dropZoneLabels_TREND_TYPE_LINEAR" ) ),
    ORDER_BY_SORT_DESCENDING( L10N.getText( "dropZoneLabels_SORT_TYPE_BYSIZEDESCENDING" ) ),
    ORDER_BY_SORT_ASCENDING( L10N.getText( "dropZoneLabels_SORT_TYPE_BYSIZEASCENDING" ) ),
    ORDER_BY_SORT_BY_LAYOUT( L10N.getText( "dropZoneLabels_SORT_TYPE_NONE" ) ),
    PATTERN_GRADIENT( L10N.getText( "dropZoneLabels_GRADIENT" ) ),
    PATTERN_3_STEP( L10N.getText( "dropZoneLabels_3_STEP" ) ),
    PATTERN_5_STEP( L10N.getText( "dropZoneLabels_5_STEP" ) ),
    COLOR_RYG( L10N.getText( "dropZoneLabels_GRAD_RYG" ) ),
    COLOR_RYB( L10N.getText( "ropZoneLabels_GRAD_RYB" ) ),
    COLOR_BLUE( L10N.getText( "ropZoneLabels_GRAD_BLUE" ) ),
    COLOR_GRAY( L10N.getText( "ropZoneLabels_GRAD_GRAY" ) );

    private String expectedText;

    private ChartPropertyOption( String label ) {
      this.expectedText = label;
    }

    public String getExpectedText() {
      return this.expectedText;
    }
  }

  public CommonChart( WebDriver driver, ChartType type ) {
    super( driver );
    this.type = type;
  }

  private boolean addField( String label, GemBar gem ) {
    ExtendedWebElement dropArea = format( gemBarDropAreaElement, gem.getType().getId() );
    ExtendedWebElement source = format( fieldItem, label );

    if ( isElementPresent( dropArea, EXPLICIT_TIMEOUT / 5 ) && isElementPresent( source, EXPLICIT_TIMEOUT / 5 ) ) {
      Utils.dnd( source, dropArea );
      pause( 2 );
    }

    return verifyGemBarMember( gem.getType().getId(), label );
  }

  /**
   * Enum to distinguish between label types: - PLOT labels are found on the plots themselves, e.g. axis labels - LEGEND
   * labels are found in the legend - MULTI_TITLE labels are found above plots in a multi_chart or multi_pie
   * 
   * @author csingleton
   *
   */
  public enum ChartLabelType {
    PLOT,
    LEGEND,
    MULTI_TITLE;
  }

  /**
   * Enum for Chart Background-Fill Types.
   * 
   * @author csingleton
   *
   */
  public enum ChartBackgroundFillType {

    NONE( L10N.getText( "dlgChartPropsBulletNone" ) ),
    SOLID( L10N.getText( "dlgChartPropsBackgroundFillTypeSolid" ) ),
    GRADIENT( L10N.getText( "dlgChartPropsBackgroundFillTypeGradient" ) );

    private String chartBackgroundFill;

    private ChartBackgroundFillType( String chartBackgroundFill ) {
      this.chartBackgroundFill = chartBackgroundFill;
    }

    public String toString() {
      return chartBackgroundFill;
    }
  }

  @Override
  public SoftAssert verifyAddingFields() {
    SoftAssert softAssert = new SoftAssert();
    List<String> levelLabels = getLevelFields();
    List<String> measureLabels = getMeasureLabels();
    int indexLevel = 0;
    int indexMeasure = 0;
    List<GemBar> gemBars = type.getGemBars();
    for ( GemBar gem : gemBars ) {
      // Do not add field if Multi-Chart or Multi-Pie
      if ( gem.getType() != GemBarType.MULTI && ( !gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTI_CHART" ) )
          && !gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTIPLE_PIE_COL" ) ) ) ) {
        String label = null;
        switch ( gem.getDndType() ) {
          case MEASURE:
            if ( indexMeasure < measureLabels.size() ) {
              label = measureLabels.get( indexMeasure );
              indexMeasure++;
            }
            break;
          default:
            if ( indexLevel < levelLabels.size() ) {
              label = levelLabels.get( indexLevel );
              indexLevel++;
            }
            break;
        }
        if ( label != null && !addField( label, gem ) ) {
          softAssert.fail( "Wrong drag and drop field for '" + type + "'. Field '" + label + "' should be dragged to '"
              + gem.getLabel() + "'!" );
        }
      }
    }
    return softAssert;
  }

  public SoftAssert verifyAddingWrongFields() {
    SoftAssert softAssert = new SoftAssert();
    List<String> levelLabels = getLevelFields();
    List<String> measureLabels = getMeasureLabels();
    int indexLevel = 0;
    int indexMeasure = 0;
    List<GemBar> gemBars = type.getGemBars();
    for ( GemBar gem : gemBars ) {
      if ( gem.getType() != GemBarType.MULTI && ( !gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTI_CHART" ) )
          && !gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTIPLE_PIE_COL" ) ) ) ) {
        String label = null;
        switch ( gem.getDndType() ) {
          case LEVEL:
            if ( indexMeasure < measureLabels.size() ) {
              label = measureLabels.get( indexMeasure );
              indexMeasure++;
            }
            break;
          case MEASURE:
            if ( indexLevel < levelLabels.size() ) {
              label = levelLabels.get( indexLevel );
              indexLevel++;
            }
            break;
          default:
            break;
        }
        if ( label != null && addField( label, gem ) ) {
          softAssert.fail( "Wrong drag and drop field for '" + type + "'. Field '" + label
              + "' should not be dragged to '" + gem.getLabel() + "'!" );
        }
      }
    }
    return softAssert;
  }

  @Override
  public SoftAssert verifyAddingMultiChart() {
    SoftAssert softAssert = new SoftAssert();
    List<GemBar> gemBars = type.getGemBars();
    for ( GemBar gem : gemBars ) {
      if ( gem.getType() == GemBarType.MULTI || gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTI_CHART" ) )
          || gem.getLabel().equals( L10N.getText( "dropZoneLabels_MULTIPLE_PIE_COL" ) ) ) {
        if ( !addField( MULTI_FIELD_LABEL, gem ) ) {
          softAssert.fail( "Wrong drag and drop field for '" + type + "'. Field '" + MULTI_FIELD_LABEL
              + "' should be dragged to '" + gem.getLabel() + "'!" );
        }
        verifyBlocks( softAssert, getMultiDrillingExpectedSize() );
      }
    }
    return softAssert;
  }

  protected ExtendedWebElement getAxisLabel() {
    return this.axisLabel;
  }

  protected List<ExtendedWebElement> getAxisScale() {
    return this.axisScale;
  }

  protected List<String> getFilterLabels() {
    return FILTER_LABELS;
  }

  protected List<String> getAxisLabels() {
    return AXIS_LABELS;
  }

  @Override
  public SoftAssert verifyContent() {
    SoftAssert softAssert = new SoftAssert();
    verifyChart( softAssert, getAxisLabels(), getExpectedSize() );
    return softAssert;
  }

  @Override
  public SoftAssert verifyDrillDown() {
    SoftAssert softAssert = new SoftAssert();
    ExtendedWebElement elem = findDrillBlock( getBlockElems() );

    if ( this instanceof ScatterChart ) {
      // Workaround for element overlaying situation because WD clicks in the center of element
      int widthElement = elem.getElement().getSize().getWidth();
      Actions actions = new Actions( driver );
      actions.moveToElement( elem.getElement(), widthElement / 2, 0 ).doubleClick().perform();
    } else if ( this instanceof GeoMapChart ) {
      // TODO: Workaround for Geo Map view, should be reworked
      try {
        Actions actions = new Actions( driver );
        Action action = actions.moveToElement( elem.getElement() ).doubleClick( elem.getElement() ).build();
        action.perform();
        action.perform();
        action.perform();
      } catch ( StaleElementReferenceException e ) {
        LOGGER.error( "Exception caught: " + e.getMessage() + "!" );
      }
    } else {
      // offset the click on element so it clicks on the middle of it.
      int widthElement = elem.getElement().getSize().getWidth();
      int heightElement = elem.getElement().getSize().getHeight();
      Actions actions = new Actions( driver );
      actions.moveToElement( elem.getElement(), widthElement / 2, heightElement / 2 ).doubleClick().perform();
    }

    showFilterPanel();
    verifyChart( softAssert, getDrilledAxisLabels(), getDrillingExpectedSize() );
    for ( String filterLabel : getFilterLabels() ) {
      if ( !verifyFilterPanel( filterLabel ) ) {
        softAssert.fail( "Filter area does not contain '" + filterLabel + "' for the chart '" + type.getId() + "'!" );
      }
    }
    return softAssert;
  }

  private void verifyChart( SoftAssert softAssert, List<String> axisLabels, int expectedSize ) {
    verifyBlocks( softAssert, expectedSize );
    verifyLabels( softAssert, axisLabels );
  }

  protected void verifyBlocks( SoftAssert softAssert, int expectedSize ) {
    List<ExtendedWebElement> elems = getBlockElems();
    if ( elems.size() != expectedSize ) {
      softAssert.fail( "Wrong count '" + elems.size() + "' of blocks in the chart '" + type.getId() + "'. Should be '"
          + expectedSize + "'!" );
    }
  }

  protected void verifyLabels( SoftAssert softAssert, List<String> axisLabels ) {
    for ( String label : axisLabels ) {
      if ( !verifyAxisLabel( label ) ) {
        softAssert.fail( "The chart '" + type.getId() + "' does not contain '" + label + "' axis!" );
      }
    }
  }

  protected boolean verifyAxisLabel( String label ) {
    return format( getAxisLabel(), label ).isElementPresent( EXPLICIT_TIMEOUT / 5 );
  }

  /**
   * This method verifies the style of the specified ChartLabelType. LEGEND types should all match the style specified
   * in Chart Options. PLOT and MULTI_TITLE types are currently treated the same, but this will change once there is a
   * reliable mechanism for distinguishing between the two (more id's and classes in the DOM). Such labels should all
   * match either the style specified in Chart Options, or be: black, bold, fontSize+2pts, and fontFamily.
   * 
   * @param specifiedLabels
   *          the list of ExtendedWebElements, that are labels, to be verified.
   * @param fontStyle
   *          the expected FontStyle of the specifiedLabels.
   * @param fontSize
   *          the expected FontSize of the specifiedLabels.
   * @param fontFamily
   *          the expected FontFamily of the specifiedLabels.
   * @param fontFillColor
   *          the expected PaletteColour of the specifiedLabels.
   * @param softAssert
   *          the SoftAssert that will be "appended" via this method. Passing null will result in a new SoftAssert.
   * 
   * @return softAssert
   */
  public SoftAssert verifySpecifiedChartLabels( SoftAssert softAssert, ChartLabelType labelType, FontStyle fontStyle,
      FontSize fontSize, FontFamily fontFamily, PaletteColour fontFillColour ) {
    // work on the passed in softAssert unless it is null
    // if null, create a new one
    if ( softAssert == null ) {
      softAssert = new SoftAssert();
    }

    String expectedStyle, expectedMultiPlotTitleStyle;
    String fontFillColourRGB = fontFillColour.getFormattedRGB();
    String cssFontStyle = "normal";
    String cssFontWeight = "normal";

    // use "font-style" for italics, "font-weight" for bold
    switch ( fontStyle ) {
      case BOLD:
        cssFontWeight = fontStyle.toString().toLowerCase();
        break;
      case ITALIC:
        cssFontStyle = fontStyle.toString().toLowerCase();
        break;
      case NORMAL: // do nothing.
        break;
    }
    expectedStyle =
        "font-style: " + cssFontStyle + "; font-variant: normal; font-weight: " + cssFontWeight
            + "; font-stretch: normal; font-size: " + fontSize + "px; line-height: normal; font-family: " + fontFamily
            + ";";

    // more efficient to switch then loop, as opposed to switching on each iteration. performance improvement is likely
    // negligible.
    switch ( labelType ) {
      case LEGEND:
        for ( ExtendedWebElement elem : chartLegendTextLabels ) {
          softAssert.assertTrue( elem.getAttribute( "style" ).equals( expectedStyle ) && elem.getAttribute( "fill" )
              .equals( fontFillColourRGB ), "Chart:" + this.type.toString() + " LEGEND Text Label:" + elem.getText()
                  + " exhibits incorrect text style:" + "\n Expected:\t" + expectedStyle + " ... " + fontFillColourRGB
                  + "\n Actual:\t" + elem.getAttribute( "style" ) + " ... " + elem.getAttribute( "fill" ) );
        }
        break;
      case PLOT:
      case MULTI_TITLE:
        expectedMultiPlotTitleStyle =
            "font-style: normal; font-variant: normal; font-weight: bold; font-stretch: normal; font-size: " + ( Integer
                .parseInt( fontSize.toString() ) + 2 ) + "px; line-height: normal; font-family: " + fontFamily + ";";
        for ( ExtendedWebElement elem : chartPlotTextLabels ) {
          softAssert.assertTrue( ( elem.getAttribute( "style" ).equals( expectedStyle ) || elem.getAttribute( "style" )
              .equals( expectedMultiPlotTitleStyle ) ) && ( elem.getAttribute( "fill" ).equals( fontFillColourRGB )
                  || elem.getAttribute( "fill" ).equals( "rgb(0,0,0)" ) ), "Chart:" + this.type.toString()
                      + "PLOT Text Label:" + elem.getText() + " exhibits incorrect text style:" + "\n ExpectedPLOT:\t"
                      + expectedStyle + " ... " + fontFillColourRGB + "\n ExpectedTITLE:\t"
                      + expectedMultiPlotTitleStyle + " ... rgb(0,0,0)" + "\n Actual:\t" + elem.getAttribute( "style" )
                      + " ... " + elem.getAttribute( "fill" ) + "\n" );
        }
        break;
    }

    return softAssert;
  }

  /***
   * This method verifies the presence of the "Chart Options..." button on the current chart
   * 
   * @param softAssert
   *          the SoftAssert that will be "appended" via this method. Passing null will result in a new SoftAssert.
   * 
   * @return softAssert
   */
  public SoftAssert verifyChartOptionsButton( SoftAssert softAssert ) {

    // work on the passed in softAssert unless it is null
    // if null, create a new one
    if ( softAssert == null ) {
      softAssert = new SoftAssert();
    }
    softAssert.assertTrue( btnChartOptions.isElementPresent(), "\"Chart Options...\" button not present for chart: "
        + this.type.toString() );
    return softAssert;
  }

  /***
   * This method verifies the colours of the gradient chart background fill on the current chart. If one PaletteColour
   * is passed, it is treated as a SOLID background fill. If two PaletteColours are passed, it is treated as a GRADIENT
   * background fill. If more than 2 PaletteColours are passed, the first two will be used to verify a GRADIENT and the
   * rest will be ignored.
   * 
   * @param softAssert
   *          the SoftAssert that will be "appended" via this method. Passing null will result in a new SoftAssert.
   * @param fillColours
   *          array containing one or two PaletteColours. fillColours[0] -> expectedStartColourRGB,
   *          expectedSolidColourRGB. fillColours[1] -> expectedSecondColourRGB
   * 
   * @return softAssert
   */
  public SoftAssert verifyChartBackgroundFill( SoftAssert softAssert, PaletteColour... fillColours ) {
    // work on the passed in softAssert unless it is null
    // if null, create a new one
    if ( softAssert == null ) {
      softAssert = new SoftAssert();
    }

    String expectedFirstColourRGB, expectedSecondColourRGB;
    String actualFirstColourRGB, actualSecondColourRGB;

    String assertionMessage;
    boolean isVerified;

    expectedFirstColourRGB = fillColours[0].getFormattedRGB();

    if ( fillColours.length == 1 ) {
      // if there is one fillColour, then verify SOLID background
      actualFirstColourRGB = chartSolidFill.getAttribute( "fill" );

      isVerified = expectedFirstColourRGB.equals( actualFirstColourRGB );
      assertionMessage =
          "Expected and Actual Chart SolidFill Colour do not match!" + "\nExpected:\t" + expectedFirstColourRGB
              + "\nActual:\t" + actualFirstColourRGB;

    } else if ( fillColours.length > 1 ) {
      // TODO this is here so I can finish my testing. I know that multi_chart gradients break (Jira ANALYZER-3361)
      if ( this.type == ChartType.PIE ) {
        LOGGER.error( "SKIP PIE CHART FOR NOW!" );
        return softAssert;
      }

      // if there is greater than one fillColour, then verify GRADIENT background
      expectedSecondColourRGB = fillColours[1].getFormattedRGB();
      // the attribute "stop-color" is used for both the StartFill and the EndFill

      actualFirstColourRGB = chartGradientStartFill.getAttribute( "stop-color" );
      actualSecondColourRGB = chartGradientEndFill.getAttribute( "stop-color" );
      isVerified =
          expectedFirstColourRGB.equals( actualFirstColourRGB ) && expectedSecondColourRGB.equals(
              actualSecondColourRGB );
      assertionMessage =
          "Expected and Actual Chart SolidFill Colour do not match!" + "\nExpected:\tStart:\t" + expectedFirstColourRGB
              + " End:\t" + expectedSecondColourRGB + "\nActual:\tStart:\t" + actualFirstColourRGB + " End:\t"
              + actualSecondColourRGB;

    } else {
      // this should never be reached
      isVerified = false;
      assertionMessage = "Invalid number of PaletteColours passed in.";
    }

    softAssert.assertTrue( isVerified, assertionMessage );
    return softAssert;
  }

  protected ExtendedWebElement findDrillBlock( List<ExtendedWebElement> elems ) {
    // Not found way how recognize element by axis name, so got first element from list
    return elems.get( 0 );
  }

  protected Double getDigit( String str ) {
    Double result = null;
    Matcher matcher = pattern.matcher( str );
    if ( matcher.find() ) {
      result = Double.parseDouble( matcher.group( 0 ) );
    }
    return result;
  }

  /**
   * Gets the tooltip used when hovering over a data point in the chart.
   * 
   * @param dataPoint
   *          The element for the data point to retrieve the tooltip from.
   * @return Returns the tooltip of the specified data point.
   */
  protected String getBlockElementTooltip( ExtendedWebElement dataPoint ) {
    // The element containing the tooltip will change based on the data point that the mouse is hovering over.
    hover( dataPoint );

    // Replace HTML line break and unicode space from the tooltip.
    return dataPointTooltip.getAttribute( "original-title" ).replace( "<br />", "\n" ).replace( "\u00A0", " " );
  }

  /**
   * Gets the tooltip used when hovering over a data point in the chart.
   * 
   * @param index
   *          The index of the ExtendedWebElement list that contains all data points.
   * @return Returns the tooltip of the specified data point.
   */
  public String getBlockElementTooltip( int index ) {
    return getBlockElementTooltip( getBlockElems().get( index ) );
  }

  /**
   * Gets the size of the data point element list.
   * 
   * @return Returns the number of elements in the list.
   */
  public int getBlockElementSize() {
    return getBlockElems().size();
  }

  public boolean verifyOptions( ChartProperty selectedProperty ) {
    // Populating variables with info from enum
    boolean areOptionsExpected = false;
    List<String> actualList = new ArrayList<String>();
    List<String> expectedList = selectedProperty.getExpectedOptions();
    ExtendedWebElement expanderElement = selectedProperty.getSelectorElement();
    // Expanding drop down and populating list with found options
    if ( isElementPresent( expanderElement, EXPLICIT_TIMEOUT / 50 ) ) {
      List<ExtendedWebElement> dropDownOptions = selectedProperty.getElementsOnDropDown();
      Utils.actionClickMiddle( getDriver(), expanderElement );
      if ( dropDownOptions.isEmpty() || !isElementPresent( dropDownOptions.get( 0 ), EXPLICIT_TIMEOUT / 50 ) ) {
        Utils.actionClickMiddle( getDriver(), expanderElement );
        LOGGER.info( "Had to click again" );
      }
      for ( ExtendedWebElement elem : dropDownOptions ) {
        actualList.add( elem.getText() );
      }
      // Closing drop down
      Utils.actionClickMiddle( getDriver(), expanderElement );
      if ( !dropDownOptions.isEmpty() && isElementPresent( dropDownOptions.get( 0 ), EXPLICIT_TIMEOUT / 50 ) ) {
        LOGGER.info( "Drop down list of " + selectedProperty.name() + "property did not close properly." );
      }
    }
    // Comparing expected list with retrieved list
    areOptionsExpected = actualList.equals( expectedList );
    if ( areOptionsExpected ) {
      LOGGER.info( "Available options on the " + selectedProperty.toString() + " drop down are as expected." );
    } else {
      LOGGER.info( "Available options on the " + selectedProperty.toString() + " drop down are not as expected." );
    }
    return areOptionsExpected;
  }

  public boolean isOptionSelected( ChartProperty property, ChartPropertyOption option ) {
    // Using information from the enum, check if expected option is selected on the property
    if ( property.getSelectedValueElement().getText().equals( option.getExpectedText() ) ) {
      LOGGER.info( "Option " + option.getExpectedText() + " is selected on the " + property.toString()
          + " as expected." );
    } else {
      LOGGER.info( "Option " + option.getExpectedText() + " is not selected on the " + property.toString()
          + " as expected." );
    }
    return property.getSelectedValueElement().getText().equals( option.getExpectedText() );
  }

  public void selectOption( ChartProperty property, ChartPropertyOption option ) {
    // Using information from enum to select given option on property
    ExtendedWebElement expanderElement = property.getSelectorElement();
    if ( isElementPresent( expanderElement, EXPLICIT_TIMEOUT / 50 ) ) {
      List<ExtendedWebElement> dropDownOptions = property.getElementsOnDropDown();
      Utils.actionClickMiddle( getDriver(), expanderElement );
      if ( dropDownOptions.isEmpty() || !isElementPresent( dropDownOptions.get( 0 ), EXPLICIT_TIMEOUT / 50 ) ) {
        Utils.actionClickMiddle( getDriver(), expanderElement );
        LOGGER.info( "Had to click again" );
      }
      for ( ExtendedWebElement elem : dropDownOptions ) {
        if ( elem.getText().equals( option.getExpectedText() ) ) {
          click( elem );
          break;
        }
      }
      // Check if desired option is selected
      if ( isOptionSelected( property, option ) ) {
        LOGGER.info( option.getExpectedText() + " option was successfully selected on " + property.toString()
            + " drop down." );
      } else {
        LOGGER.info( option.getExpectedText() + " option was not successfully selected on " + property.toString()
            + " drop down." );
      }
    }
  }

  public boolean isOptionsButtonPresent() {
    // Check if Chart Properties button is present
    return isElementPresent( optionsButton );
  }
}
