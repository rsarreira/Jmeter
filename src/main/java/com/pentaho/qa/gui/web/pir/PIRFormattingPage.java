package com.pentaho.qa.gui.web.pir;

import java.awt.Color;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.pentaho.services.utils.Utils;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRFormattingPage extends PIRReportPage {

  @FindBy( id = "fontlist1" )
  protected ExtendedWebElement dropDownFont;

  // @FindBy( css = "#fontlist1_menu > [aria-label|=%s]" )
  // protected ExtendedWebElement fontItem;

  @FindBy( xpath = "//tr[contains(@class, 'pentaho-menuitem')]/td[text()='%s']" )
  protected ExtendedWebElement fontItem;

  @FindBy( id = "fontsize1" )
  protected ExtendedWebElement dropDownSize;

  // @FindBy( css = "#fontlist1_menu > [aria-label|=%s]" )
  // protected ExtendedWebElement sizeItem;

  @FindBy( xpath = "//tr[contains(@class, 'pentaho-menuitem')]/td[text()='%s']" )
  protected ExtendedWebElement sizeItem;

  @FindBy( id = "toolbar1.foreColorBtn_arrow" )
  protected ExtendedWebElement dropDownFontColor;

  @FindBy( id = "toolbar1.backColorBtn_arrow" )
  protected ExtendedWebElement dropDownBackgroundColor;

  @FindBy( id = "numericformats" )
  protected ExtendedWebElement dropDownNumericFormats;

  @FindBy( xpath = "//div[@id='dijit_ColorPalette_1']//img[contains(@style, '%s')]" )
  ExtendedWebElement bgColor;

  @FindBy( xpath = "//div[@id='dijit_ColorPalette_0']//img[contains(@style, '%s')]" )
  ExtendedWebElement fontColor;

  public PIRFormattingPage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      Assert.fail( "Formatting tab was not selected!" );
    }
  }

  protected boolean isOpened() {
    String classValue = tabFormatting.getAttribute( "class" );
    return classValue.contains( "pentaho-tabWidget-selected" );
  }

  public void setFont( String font ) {
    click( dropDownFont );
    click( format( fontItem, font ) );
  }

  public void setSize( String size ) {
    click( dropDownSize );
    click( format( sizeItem, size ) );
  }

  public void setBackgroundColor( Color color ) {
    click( dropDownBackgroundColor );
    click( format( bgColor, Utils.toHexString( color ) ) );
    click( dropDownBackgroundColor );
  }

  public void setFontColor( Color color ) {
    click( dropDownFontColor );
    click( format( fontColor, Utils.toHexString( color ) ) );
    click( dropDownFontColor );
  }

}
