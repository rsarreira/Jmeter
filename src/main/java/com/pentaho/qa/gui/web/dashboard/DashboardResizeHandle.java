package com.pentaho.qa.gui.web.dashboard;

import org.apache.log4j.Logger;

public class DashboardResizeHandle {
  protected static final Logger LOGGER = Logger.getLogger( DashboardResizeHandle.class );

  private String firstPanel = "";
  private String secondPanel = "";
  private ResizeAxis resizeAxis;

  public enum ResizeAxis {
    x, y;

    /**
     * Gets an instance of ResizeAxis that matches the specified axis. This matches by the enum name, but also accepts a
     * value with "resize" in the name, such as "resizex".
     * 
     * @param axis
     *          The name of the axis to match.
     * @return Returns an instance of ResizeAxis that contains the resize handles axis.
     */
    public static ResizeAxis getResizeAxis( String axis ) {
      ResizeAxis resizeAxis = null;

      for ( ResizeAxis findAxis : ResizeAxis.values() ) {
        if ( findAxis.name().equals( axis.toLowerCase().replace( "resize", "" ) ) ) {
          resizeAxis = findAxis;
          break;
        }
      }

      if ( resizeAxis == null ) {
        LOGGER.warn( "ResizeAxis not found: unknown axis '" + axis + "'." );
      } else {
        LOGGER.info( "ResizeAxis found: " + resizeAxis.name() );
      }

      return resizeAxis;
    }
  }

  public DashboardResizeHandle( String firstPanel, String secondPanel, ResizeAxis resizeAxis ) {
    this.firstPanel = firstPanel;
    this.secondPanel = secondPanel;
    this.resizeAxis = resizeAxis;
  }

  /**
   * Gets the ID of the first panel affected by the resize handle (the top or the left panel).
   * 
   * @return Returns a String that contains the ID of the panel.
   */
  public String getFirstPanel() {
    return firstPanel;
  }

  /**
   * Gets the ID of the second panel affected by the resize handle (the bottom or the right panel).
   * 
   * @return Returns a String that contains the ID of the panel.
   */
  public String getSecondPanel() {
    return secondPanel;
  }

  /**
   * Gets the axis that is affected by the resize panel.
   * 
   * @return Returns the instance of ResizeAxis that represents the axis in which the resize handle moves.
   */
  public ResizeAxis getResizeAxis() {
    return resizeAxis;
  }

  /**
   * Determines whether or not the specified panel is a dashboard widget.
   * 
   * @return Returns true when the panel is a dashboard widget. Otherwise, if the panel is a layout container, it will
   *         return false.
   */
  private boolean panelIsWidget( String panel ) {
    // Only dashboard widgets have "panel" in their IDs. The layout containers have "hbox" or "vbox".
    return panel.toLowerCase().contains( "panel" );
  }

  /**
   * Determines whether or not the first panel is a dashboard widget.
   * 
   * @return Returns true when the first panel is a dashboard widget. Otherwise, if the panel is a layout container, it
   *         will return false.
   */
  public boolean firstPanelIsWidget() {
    return panelIsWidget( firstPanel );
  }

  /**
   * Determines whether or not the second panel is a dashboard widget.
   * 
   * @return Returns true when the second panel is a dashboard widget. Otherwise, if the panel is a layout container, it
   *         will return false.
   */
  public boolean secondPanelIsWidget() {
    return panelIsWidget( secondPanel );
  }
}
