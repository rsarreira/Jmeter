package com.pentaho.qa.gui.web.analyzer;

import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public class GemBar {
  public enum GemBarType {
    ROWS( "rows_ui" ), COLUMNS( "columns_ui" ), MEASURES( "measures_ui" ), MULTI( "multi_ui" ), MEASURES_LINE(
        "measuresLine_ui" ), SIZE( "size_ui" ), X( "x_ui" ), Y( "y_ui" ), COLOR( "color_ui" );

    private String id;

    private GemBarType( String id ) {
      this.id = id;
    }

    public String getId() {
      return this.id;
    }
  }

  public enum GemBarDndType {
    LEVEL, MEASURE, BOTH;
  }

  private GemBarType type;
  private GemBarDndType dndType;
  private String label;
  private boolean isRequired;

  public GemBar( GemBarType type, GemBarDndType dndType, String label ) {
    super();
    this.type = type;
    this.dndType = dndType;
    this.label = label;
    this.isRequired = false;
  }

  public GemBar( GemBarType type, GemBarDndType dndType, String label, boolean isRequired ) {
    super();
    this.type = type;
    this.dndType = dndType;
    this.label = label;
    this.isRequired = isRequired;
  }

  public GemBarType getType() {
    return type;
  }

  public GemBarDndType getDndType() {
    return dndType;
  }

  /**
   * This method returns the translated text for the gem bar member (i.e. "Drop Measure Here" and "Drop Level Here").
   * 
   * @return Returns the translated text for the gem bar member.
   */
  public String getDndText() {
    String dndText = "";

    if ( dndType.equals( GemBarDndType.MEASURE ) ) {
      dndText = L10N.getText( "dropZonePlaceholder_number" );
    } else {
      dndText = L10N.getText( "dropZonePlaceholder_string" );
    }

    return dndText;
  }

  public String getLabel() {
    return label;
  }

  public boolean isRequired() {
    return isRequired;
  }

  /**
   * This method determines the GemBarDndType and sets the gem bar's icon class name based on the type.
   * 
   * @return Returns the gem bar's icon class name.
   */
  public String getIconClassName() {
    String className = "";

    switch ( dndType ) {
      case LEVEL:
        className = "gem-bar-level";
        break;
      case MEASURE:
        className = "gem-bar-measure";
        break;
      case BOTH:
        className = "gem-bar-filter";
        break;
    }

    return className;
  }
};
