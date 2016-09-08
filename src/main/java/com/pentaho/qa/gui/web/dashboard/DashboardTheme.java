package com.pentaho.qa.gui.web.dashboard;

public enum DashboardTheme {
  // TODO: Add specific theme details, such as color, so that tests can verify specific theme attributes.
  CRYSTAL( "Crystal" ), ONYX( "Onyx" ), SLATE( "Slate" ), SIMPLE( "Simple" ), ROUNDED( "Rounded" ), COOL_BLUE(
      "Cool Blue" ), GRADIENT( "Gradient" ), SUBTLE( "Subtle" );

  private String name;

  private DashboardTheme( String name ) {
    this.name = name;
  }

  /**
   * Gets the name of the dashboard theme that is used in the GUI.
   * 
   * @return Returns a String that contains the name of the theme.
   */
  public String toString() {
    return name;
  }
}
