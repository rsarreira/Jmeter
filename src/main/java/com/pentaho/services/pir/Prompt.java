package com.pentaho.services.pir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.pentaho.qa.gui.web.pir.PIRPromptPage;
import com.pentaho.qa.gui.web.pir.PIRPromptPanelPage;
import com.pentaho.services.BaseObject;
import com.pentaho.services.Report.Workflow;
import com.qaprosoft.carina.core.foundation.utils.HTML;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class Prompt extends BaseObject {

  public static final String ARG_FIELD = "Field";
  public static final String ARG_CONTROL = "Control";
  public static final String ARG_DATA_TYPE = "DataType";
  public static final String ARG_VALUE = "Value";

  protected String field;
  protected DataType dataType;
  protected Control control;
  protected PIRFilter filter;

  protected String value;

  private PIRPromptPage promptPage;

  public enum DataType {
    STATIC_LIST( "Static List" ), METADATA_LIST( "Metadata List" );

    private String name;

    private DataType( String dataType ) {
      this.name = dataType;
    }

    public String getName() {
      return this.name;
    }
  }

  public enum Control {
    DROP_DOWN( "EditFilter.DropDown.label" ), LIST( "EditFilter.Prop.List.label" ), RADIO_BUTTONS(
        "EditFilter.Prop.Radio.label" ), CHECKBOX( "EditFilter.Prop.Checkbox.label" ), BUTTONS(
            "EditFilter.Prop.Button.label" ), TEXT_FIELD( "EditFilter.Prop.Text.label" ), DATE_PICKER(
                "EditFilter.Prop.Date.label" );

    private String name;

    private Control( String control ) {
      this.name = control;
    }

    public String getName() {
      return L10N.getText( this.name );
    }
  }

  public Prompt( Map<String, String> args ) {
    super( args );

    name = args.get( ARG_NAME );
    value = args.get( ARG_VALUE );
    dataType = args.get( ARG_DATA_TYPE ) != null ? DataType.valueOf( args.get( ARG_DATA_TYPE ) ) : null;
    field = args.get( ARG_FIELD );
    control = args.get( ARG_CONTROL ) != null ? Control.valueOf( args.get( ARG_CONTROL ) ) : null;
    filter = new PIRFilter( field );
  }

  public Prompt( String promptName ) {
    super( promptName );
    field = name;
    name = promptName;
    dataType = DataType.METADATA_LIST;
    control = Control.DROP_DOWN;
    filter = new PIRFilter( promptName );
  }

  // Copy constructor
  public void copy( Prompt prompt ) {
    if ( prompt.getField() != null ) {
      this.field = prompt.getField();
    }

    if ( prompt.getDataType() != null ) {
      this.dataType = prompt.getDataType();
    }

    if ( prompt.getControl() != null ) {
      this.control = prompt.getControl();
    }

    if ( prompt.getValue() != null ) {
      this.value = prompt.getValue();
    }

    if ( prompt.getName() != null ) {
      this.name = prompt.getName();
    }
  }

  // getter/setter
  public String getName() {
    return name;
  }

  public void setName( String name ) {
    this.name = name;
  }

  public String getField() {
    return field;
  }

  public void setField( String field ) {
    this.field = field;
  }

  public DataType getDataType() {
    return dataType;
  }

  public void setDataType( DataType dataType ) {
    this.dataType = dataType;
  }

  public Control getControl() {
    return control;
  }

  public void setControl( Control control ) {
    this.control = control;
  }

  public String getValue() {
    return value;
  }

  public void setValue( String value ) {
    this.value = value;
  }

  /* -------------- CREATE PROMPT ------------------------------ */

  public PIRFilter getFilter() {
    return filter;
  }

  public void setFilter( PIRFilter filter ) {
    this.filter = filter;
  }

  public void create( Workflow workflow ) {
    // Get report page
    PIRPromptPanelPage promptPanel = new PIRPromptPanelPage( getDriver() );
    promptPanel.applyPrompt( this );
  }

  public PIRPromptPage edit( Prompt newPrompt ) {
    // Get Prompt panel page
    PIRPromptPanelPage promptPanelPage = new PIRPromptPanelPage( getDriver() );

    // Click Edit on current prompt and return promptPage object which is going to be used in setParameters()
    newPrompt.promptPage = promptPanelPage.editPrompt( this );
    newPrompt.setParameters();
    // Update current filter
    copy( newPrompt );

    return newPrompt.promptPage;
  }

  protected void setParameters() {
    promptPage.setName( name );
    promptPage.setControl( control );
    promptPage.makeClickable();
    promptPage.clickOK();
  }

  public void delete() {
    PIRPromptPanelPage promptPanelPage = new PIRPromptPanelPage( getDriver() );
    promptPanelPage.deletePrompt( this );
  }

  public boolean verify() {
    boolean res = false;
    PIRPromptPanelPage promptPanelPage = new PIRPromptPanelPage( getDriver() );
    promptPage = promptPanelPage.editPrompt( this );

    // if page and object are different in ANY field, connection was not overwritten.
    if ( !getName().equals( promptPage.getPromptName() ) || !getControl().getName().equals( promptPage
        .getSelectedControl().getAttribute( HTML.TITLE ) ) ) {
      res = true;
    }

    promptPage.clickCancel();

    return res;
  }

  public boolean verifyItems( String items ) {
    boolean res = true;

    List<String> promptItems = parse( items );
    PIRPromptPanelPage promptPanelPage = new PIRPromptPanelPage( getDriver() );

    for ( String promptItem : promptItems ) {
      if ( !promptPanelPage.isPromptItemExists( this, promptItem ) ) {
        LOGGER.error( "Prompt item: " + promptItem + " is not present in the Prompt dropdown" );
        res = false;
      }
    }

    return res;
  }

  private List<String> parse( String joinString ) {
    return new ArrayList<String>( Arrays.asList( joinString.replaceAll( " ", "" ).split( "," ) ) );
  }

}
