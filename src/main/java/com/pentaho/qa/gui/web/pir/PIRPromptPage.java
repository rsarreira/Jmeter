package com.pentaho.qa.gui.web.pir;

import java.text.MessageFormat;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.PromptPage;
import com.pentaho.services.pir.Prompt;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRPromptPage extends PromptPage {

  private static final String CHANGE_PROMPT_UPDATE_FILTER =
      L10N.getText( "ChangePromptNameUpdateFilterParameterNameMessage" );
  private static final String PROMPT_TITLE = L10N.getText( "ParameterFilterDialogTitle" );

  // Warning dialog and buttons
  @FindBy( id = "dijit_Dialog_1" )
  protected ExtendedWebElement dlgWarning;

  @FindBy( xpath = "//div[@id='messageBoxDialog']//div[@class='pentaho-pir-dialog' and contains(text(),%s)]" )
  protected ExtendedWebElement warningLabel;

  @FindBy( xpath = "//*[@id='button0' and @class='pentaho-button' and text()='{L10N:Yes_txt}']" )
  protected ExtendedWebElement btnYes;

  @FindBy( xpath = "//*[@id='button1' and @class='pentaho-button' and text()='{L10N:No_txt}']" )
  protected ExtendedWebElement btnNo;

  public PIRPromptPage( WebDriver driver, String fieldName ) {
    super( driver );

    String translatedMessage = MessageFormat.format( PROMPT_TITLE, fieldName );

    super.isOpened( translatedMessage );
  }

  public boolean isWarningDialogPresent( Prompt prompt ) {
    boolean res = false;
    String[] params = { prompt.getField() };
    String translatedMessage = L10N.generateConcatForXPath( L10N.formatString( CHANGE_PROMPT_UPDATE_FILTER, params ) );

    if ( isElementPresent( dlgWarning, EXPLICIT_TIMEOUT / 5 ) && isElementPresent( format( warningLabel,
        translatedMessage ), EXPLICIT_TIMEOUT / 5 ) ) {
      LOGGER.info( "Warning message displayed: " + format( warningLabel, translatedMessage ).getText() );
      res = true;
    }

    return res;
  }

  public void clickYes() {
    click( btnYes );
  }

  public void clickNo() {
    click( btnNo );
  }
}
