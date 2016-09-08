package com.pentaho.qa.gui.web.dashboard;

public enum DashboardTemplate {
  // TODO: Find localization file in the Pentaho repository to localize the names.
  TWO_AND_ONE( "2 and 1" ), TWO_OVER_ONE( "2 over 1" ), ONE_AND_TWO( "1 and 2" ), ONE_OVER_TWO(
      "1 over 2" ), ONE_OVER_ONE( "1 over 1" ), TWO_COLUMN( "2 Column" ), THREE_COLUMN( "3 Column" ), THREE_OVER_ONE(
          "3 over 1" ), THREE_BY_THREE( "3 by 3" ), SINGLE( "Single" ), THREE_OVER_THREE( "3 over 3" ), THREE_OVER_TWO(
              "3 over 2" ), TWO_BY_TWO( "2 by 2" );

  private String name;

  private DashboardTemplate( String name ) {
    this.name = name;
  }

  /**
   * Gets the name used for the template in the GUI.
   * 
   * @return Returns the name of the template.
   */
  public String toString() {
    return name;
  }
}
