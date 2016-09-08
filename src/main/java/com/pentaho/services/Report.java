package com.pentaho.services;

import java.awt.Color;
import java.util.Arrays;
import java.util.Map;

import com.pentaho.qa.gui.web.puc.FilePage;
import com.pentaho.services.puc.browse.File;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;

public abstract class Report extends File implements IReport {

  protected String dataSource;
  protected String title;

  // TODO: should be removed after completion BrowseService
  // protected String location;

  // internal page objects
  // private ReportPage reportPage;

  public static final String ARG_TITLE = "Title";
  public static final String ARG_DATASOUCRE = "Datasource";

  public enum Layout {
    ROW, COLUMN, MEASURE, GROUP,
    // TODO: CHARTS LAYOUTS SHOULD BE HERE TOO
    NONE;
  }

  public enum Sort {
    ASC( "Ascending" ), DESC( "Descending" ), NONE( "None" );

    private String name;

    private Sort( String sort ) {
      this.name = sort;
    }

    public String getName() {
      return this.name;
    }
  }

  abstract public void sort( Layout layout, String field, Sort sort, Workflow workflow );

  abstract public void sorterUp( Layout layout, String field );

  abstract public void sorterDown( Layout layout, String field );

  public Report( Map<String, String> args ) {
    this( args.get( ARG_NAME ), Boolean.valueOf( args.get( ARG_HIDDEN ) ), Type.fromString( args.get( ARG_TYPE ) ),
        Boolean.valueOf( args.get( ARG_ALLOW_SCHEDULING ) ), args.get( ARG_TITLE ), args.get( ARG_DATASOUCRE ), args
            .get( ARG_ID ) );
  }

  public Report( String name, Boolean hidden, Type type, Boolean allowScheduling, String title, String dataSource,
      String id ) {
    super( name, hidden, type, allowScheduling, id );
    this.title = title;
    this.dataSource = dataSource;
  }

  /**
   * The following workflows are supported: CONTEXT_PANEL = Right click in available fields area. CONTEXT_REPORT = Right
   * click in report area. DND_PANEL = Drag and drop to panels (prompts/filters/groups). DND_REPORT = Drag and drop to
   * the report area. CONTEXT_REPORT_ARROW = click on the arrow in report area (actual only for filters)
   */
  public enum Workflow {
    CONTEXT_PANEL, DND_PANEL, DND_REPORT, CONTEXT_REPORT, CONTEXT_REPORT_ARROW, CONTEXT_LAYOUT;
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource( String dataSource ) {
    this.dataSource = dataSource;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( Report report ) {
    super.copy( report );

    if ( report.getDataSource() != null ) {
      this.dataSource = report.getDataSource();
    }

    if ( report.getTitle() != null ) {
      this.title = report.getTitle();
    }
  }

  /* ------------------- OPERATIONS ------------------------- */

  @Override
  public void edit( IReport iReport ) {
    LOGGER.info( "Object name: " + this.getName() );

    // TODO: need valid deep cloning
    // Assert.fail("need valid deep cloning");

    ObjectPool.putSnapshot( (BaseObject) this.clone() );
  }

  public FilePage getPage() {
    super.getPage().switchToFrame();
    return super.getPage();
  }

  /***
   * This is called "Colour" to differentiate from Java's "Color" enum
   * 
   * In the future, it might be nice to refactor this into a relationship that allows for BasicColor and CustomColor
   * enums
   * 
   */
  public enum Colour {
    ALICE_BLUE( "alice blue", new Color( 239, 250, 254 ), "#effafe" ),
    BISQUE( "bisque", new Color( 255, 228, 196 ), "#ffe4c4" ),
    BLACK( "black", new Color( 0, 0, 0 ), "#000000" ),
    BLUE( "blue", new Color( 0, 0, 255 ), "#0000ff" ),
    BLUE_VIOLET( "blue-violet", new Color( 138, 43, 226 ), "#8a2be2" ),
    BROWN( "brown", new Color( 165, 42, 42 ), "#a52a2a" ),
    CELLO( "cello", new Color( 70, 79, 87 ), "#464f57" ),
    CHARTREUSE( "chartreuse", new Color( 127, 255, 0 ), "#7fff00" ),
    CHOCOLATE( "chocolate", new Color( 210, 105, 30 ), "#d2691e" ),
    CORAL( "coral", new Color( 255, 127, 80 ), "#ff7f50" ),
    CORNFLOWER_BLUE( "cornflower blue", new Color( 100, 149, 237 ), "#6495ed" ),
    CORNSILK( "cornsilk", new Color( 255, 248, 220 ), "#fff8dc" ),
    CREAM( "cream", new Color( 254, 249, 212 ), "#fef9d4" ),
    CRIMSON( "crimson", new Color( 220, 20, 60 ), "#dc143c" ),
    DARK_CYAN( "dark cyan", new Color( 0, 139, 139 ), "#008b8b" ),
    DARK_GREEN( "dark green", new Color( 0, 100, 0 ), "#006400" ),
    DARK_GREY( "dark grey", new Color( 170, 170, 170 ), "#aaaaaa" ),
    DARK_MAGENTA( "dark magenta", new Color( 139, 0, 139 ), "#8b008b" ),
    DARK_OLIVE_GREEN( "dark olive green", new Color( 85, 107, 47 ), "#556b2f" ),
    DARK_ORANGE( "dark orange", new Color( 255, 140, 0 ), "#ff8c00" ),
    DARK_ORCHID( "dark orchid", new Color( 153, 50, 204 ), "#9932cc" ),
    DARK_RED( "dark red", new Color( 139, 0, 0 ), "#8b0000" ),
    DARK_SEA_GREEN( "dark sea green", new Color( 143, 188, 143 ), "#8fbc8f" ),
    DARK_SLATE_BLUE( "dark slate blue", new Color( 72, 61, 139 ), "#483d8b" ),
    DARK_SLATE_GRAY( "dark slate gray", new Color( 47, 79, 79 ), "#2f4f4f" ),
    DIM_GRAY( "dim gray", new Color( 105, 105, 105 ), "#696969" ),
    DROVER( "drover", new Color( 253, 242, 150 ), "#fdf296" ),
    FIRE_BRICK( "fire brick", new Color( 178, 34, 34 ), "#b22222" ),
    FOREST_GREEN( "forest green", new Color( 34, 139, 34 ), "#228b22" ),
    GOLD( "gold", new Color( 255, 215, 0 ), "#ffd700" ),
    GRAY( "gray", new Color( 128, 128, 128 ), "#808080" ),
    GREEN( "green", new Color( 0, 128, 0 ), "#008000" ),
    INDIGO( "indigo", new Color( 75, 0, 130 ), "#4b0082" ),
    KHAKI( "khaki", new Color( 240, 230, 140 ), "#f0e68c" ),
    LAVENDER( "lavender", new Color( 230, 230, 250 ), "#e6e6fa" ),
    LEMON_CHIFFON( "lemon chiffon", new Color( 255, 250, 205 ), "#fffacd" ),
    LIGHT_CORAL( "light coral", new Color( 240, 128, 128 ), "#f08080" ),
    LIGHT_CYAN( "light cyan", new Color( 224, 255, 255 ), "#e0ffff" ),
    LIGHT_GRAY( "light gray", new Color( 211, 211, 211 ), "#d3d3d3" ),
    LIGHT_GREEN( "light green", new Color( 144, 238, 144 ), "#90ee90" ),
    LIGHT_SEA_GREEN( "light sea green", new Color( 32, 178, 170 ), "#20b2aa" ),
    LIGHT_SKY_BLUE( "light sky blue", new Color( 135, 206, 250 ), "#87cefa" ),
    LIGHT_YELLOW( "light yellow", new Color( 255, 255, 224 ), "#ffffe0" ),
    LIME_GREEN( "lime green", new Color( 50, 205, 50 ), "#32cd32" ),
    MAGENTA( "magenta", new Color( 255, 0, 255 ), "#ff00ff" ),
    MAROON( "maroon", new Color( 128, 0, 0 ), "#800000" ),
    MATISSE( "matisse", new Color( 61, 100, 128 ), "#3d6480" ),
    MEDIUM_BLUE( "medium blue", new Color( 0, 0, 205 ), "#0000cd" ),
    MEDIUM_ORCHID( "medium orchid", new Color( 186, 85, 211 ), "#ba55d3" ),
    MEDIUM_SLATE_BLUE( "medium slate blue", new Color( 123, 104, 238 ), "#7b68ee" ),
    MEDIUM_TURQUOISE( "medium turquoise", new Color( 72, 209, 204 ), "#48d1cc" ),
    MIDNIGHT_BLUE( "midnight blue", new Color( 25, 25, 112 ), "#191970" ),
    MOCCASIN( "moccasin", new Color( 255, 228, 181 ), "#ffe4b5" ),
    NAVY( "navy", new Color( 0, 0, 128 ), "#000080" ),
    OLIVE( "olive", new Color( 128, 128, 0 ), "#808000" ),
    ONAHAU( "onahau", new Color( 186, 223, 236 ), "#badfec" ),
    ORANGE( "orange", new Color( 255, 165, 0 ), "#ffa500" ),
    ORANGE_RED( "orange red", new Color( 255, 69, 0 ), "#ff4500" ),
    ORCHID( "orchid", new Color( 218, 112, 214 ), "#da70d6" ),
    OYSTER_BAY( "oyster bay", new Color( 220, 231, 237 ), "#dce7ed" ),
    PALE_GOLDENROD( "pale goldenrod", new Color( 238, 232, 170 ), "#eee8aa" ),
    PALE_GREEN( "pale green", new Color( 152, 251, 152 ), "#98fb98" ),
    PALE_TURQUOISE( "pale turquoise", new Color( 175, 238, 238 ), "#afeeee" ),
    PINK( "pink", new Color( 255, 192, 203 ), "#ffc0cb" ),
    PLUM( "plum", new Color( 221, 160, 221 ), "#dda0dd" ),
    PURPLE( "purple", new Color( 128, 0, 128 ), "#800080" ),
    RED( "red", new Color( 255, 0, 0 ), "#ff0000" ),
    REEF( "reef", new Color( 203, 239, 163 ), "#cbefa3" ),
    ROYAL_BLUE( "royal blue", new Color( 65, 105, 225 ), "#4169e1" ),
    SADDLE_BROWN( "saddle brown", new Color( 139, 69, 19 ), "#8b4513" ),
    SANDY_BROWN( "sandy brown", new Color(144, 164, 96), "#f4a460" ),
    SEA_GREEN( "sea green", new Color( 46, 139, 87 ), "#2e8b57" ),
    SEASHELL( "seashell", new Color( 255, 245, 238 ), "#fff5ee" ),
    SIENNA( "sienna", new Color( 160, 82, 45 ), "#a0522d" ),
    SILVER( "silver", new Color( 192, 192, 192 ), "#c0c0c0" ),
    SKY_BLUE( "sky blue", new Color( 135, 206, 235 ), "#87ceeb" ),
    SLATE_BLUE( "slate blue", new Color( 106, 90, 205 ), "#6a5acd" ),
    SOLITUDE( "solitude", new Color( 245, 246, 248 ), "#f5f6f8" ),
    TEXAS( "texas", new Color( 232, 220, 120 ), "#e8dc78" ),
    VIOLET( "violet", new Color( 238, 130, 238 ), "#ee82ee" ),
    WHITE( "white", new Color( 255, 255, 255 ), "#ffffff" ),
    WHITE_SMOKE( "white smoke", new Color( 241, 241, 241 ), "#f1f1f1" ),
    YELLOW( "yellow", new Color( 255, 255, 0 ), "#ffff00" );

    private String name;
    private Color rgba;
    private String hex;

    private Colour( String color, Color rgba, String hexValue ) {
      this.name = color;
      this.rgba = rgba;
      this.hex = hexValue;
    }

    public String getName() {
      return this.name;
    }

    public Color getRgba() {
      return this.rgba;
    }

    public String getHexValue() {
      return this.hex;
    }

    public void setHex( String hex ) {
      this.hex = hex;
    }

    public void setName( String name ) {
      this.name = name;
    }

    public void setRgba( Color rgba ) {
      this.rgba = rgba;
    }

    public String getHex() {
      return hex;
    }

    /***
     * Returns rgb string formatted as "rgb(<redInt>,<greenInt>,<blueInt>)"
     * 
     * @return
     */
    public String getFormattedRGB() {
      int r, g, b;
      r = this.rgba.getRed();
      g = this.rgba.getGreen();
      b = this.rgba.getBlue();
      return "rgb(" + r + "," + g + "," + b + ")";
    }

    public static Colour getColour( Object obj ) {
      if ( obj != null ) {
        for ( Colour colour : Colour.values() ) {
          // check against RGBA value when object is an instance of Color
          if ( obj instanceof Color ) {
            if ( obj.equals( colour.getRgba() ) ) {
              return colour;
            }
          } else if ( obj.toString().contains( "rgb" ) ) {
            // Parse the int values for RGB.
            String rgba = obj.toString().replace( "rgba(", "" ).replace( "rgb(", "" ).replace( ")", "" );
            int[] rgbaValues = Arrays.stream( rgba.split( ", " ) ).mapToInt( Integer::parseInt ).toArray();

            if ( new Color( rgbaValues[0], rgbaValues[1], rgbaValues[2] ).equals( colour.getRgba() ) ) {
              return colour;
            }
          } else {
            // check hex or name value when object is an instance of String
            if ( ( (String) obj ).equalsIgnoreCase( colour.getHexValue() ) || ( (String) obj ).equalsIgnoreCase( colour
                .getName() ) ) {
              return colour;
            }
          }
        }
      } else {
        LOGGER.error( "Unable to get Colour from null!" );
      }
      return null;
    }

    /**
     * Parses the name of the color from an element's color property.
     * 
     * @param html
     *          The HTML text containing the element.
     * @return Returns an instance of a Colour object found from the color property.
     */
    public static Colour parseColorFromProperty( String html ) {
      String propertyName = "color:";
      int colorIndex = html.indexOf( propertyName );

      // Find the end of the color property.
      int colorEndIndex = html.indexOf( ";", colorIndex );

      // If the color is the only property, then find the end of the tag.
      if ( colorEndIndex < 0 ) {
        colorEndIndex = html.indexOf( "\">" );

        // Localization files with translations that contain html use single quotes.
        if ( colorEndIndex < 0 ) {
          colorEndIndex = html.indexOf( "'>" );
        }
      }

      Colour color = null;

      if ( colorIndex > 0 ) {
        String parsedColor = html.substring( colorIndex + propertyName.length(), colorEndIndex ).trim();
        color = Colour.getColour( parsedColor );
      }

      return color;
    }
  }

  /*
   * TODO are fonts localize-able? Are there fonts for Japanese Characters?
   */
  public enum FontFamily {
    DEFAULT( "Default" ),
    AGENCY_FB( "Agency FB" ),
    ALGERIAN( "Algerian" ),
    ARIAL( "Arial" ),
    ARIAL_BLACK( "Arial Black" ),
    ARIAL_NARROW( "Arial Narrow" ),
    ARIAL_ROUNDED_MT_BOLD( "Arial Rounded MT Bold" ),
    BASKERVILLE_OLD_FACE( "Baskerville Old Face" ),
    BAUHAUS_93( "Bauhaus 93" ),
    BELL_MT( "Bell MT" ),
    BERLIN_SANS_FB( "Berlin Sans FB" ),
    BERLIN_SANS_FB_DEMI( "Berlin Sans FB Demi" ),
    BERNARD_MT_CONDENSED( "Bernard MT Condensed" ),
    BLACKADDER_ITC( "Blackadder ITC" ),
    BODONI_MT( "Bodoni MT" ),
    BODONI_MT_BLACK( "Bodoni MT Black" ),
    BODONI_MT_CONDENSED( "Bodoni MT Condensed" ),
    BODONI_MT_POSTER_COMPRESSED( "Bodoni MT Poster Compressed" ),
    BOOK_ANTIQUA( "Book Antiqua" ),
    BOOKMAN_OLD_STYLE( "Bookman Old Style" ),
    BOOKSHELF_SYMBOL_7( "Bookshelf Symbol 7" ),
    BRADLEY_HAND_ITC( "Bradley Hand ITC" ),
    BRITANNIC_BOLD( "Britannic Bold" ),
    BROADWAY( "Broadway" ),
    BRUSH_SCRIPT_MT( "Brush Script MT" ),
    CALIBRI( "Calibri" ),
    CALIBRI_LIGHT( "Calibri Light" ),
    CALIFORNIAN_FB( "Californian FB" ),
    CALISTO_MT( "Calisto MT" ),
    CAMBRIA( "Cambria" ),
    CAMBRIA_MATH( "Cambria Math" ),
    CANDARA( "Candara" ),
    CASTELLAR( "Castellar" ),
    CENTAUR( "Centaur" ),
    CENTURY( "Century" ),
    CENTURY_GOTHIC( "Century Gothic" ),
    CENTURY_SCHOOLBOOK( "Century Schoolbook" ),
    CHILLER( "Chiller" ),
    COLONNA_MT( "Colonna MT" ),
    COMIC_SANS_MS( "Comic Sans MS" ),
    CONSOLAS( "Consolas" ),
    CONSTANTIA( "Constantia" ),
    COOPER_BLACK( "Cooper Black" ),
    COPPERPLATE_GOTHIC_BOLD( "Copperplate Gothic Bold" ),
    COPPERPLATE_GOTHIC_LIGHT( "Copperplate Gothic Light" ),
    CORBEL( "Corbel" ),
    COURIER_NEW( "Courier New" ),
    CURLZ_MT( "Curlz MT" ),
    DIALOG( "Dialog" ),
    DIALOGINPUT( "DialogInput" ),
    EBRIMA( "Ebrima" ),
    EDWARDIAN_SCRIPT_ITC( "Edwardian Script ITC" ),
    ELEPHANT( "Elephant" ),
    ENGRAVERS_MT( "Engravers MT" ),
    ERAS_BOLD_ITC( "Eras Bold ITC" ),
    ERAS_DEMI_ITC( "Eras Demi ITC" ),
    ERAS_LIGHT_ITC( "Eras Light ITC" ),
    ERAS_MEDIUM_ITC( "Eras Medium ITC" ),
    FELIX_TITLING( "Felix Titling" ),
    FOOTLIGHT_MT_LIGHT( "Footlight MT Light" ),
    FORTE( "Forte" ),
    FRANKLIN_GOTHIC_BOOK( "Franklin Gothic Book" ),
    FRANKLIN_GOTHIC_DEMI( "Franklin Gothic Demi" ),
    FRANKLIN_GOTHIC_DEMI_COND( "Franklin Gothic Demi Cond" ),
    FRANKLIN_GOTHIC_HEAVY( "Franklin Gothic Heavy" ),
    FRANKLIN_GOTHIC_MEDIUM( "Franklin Gothic Medium" ),
    FRANKLIN_GOTHIC_MEDIUM_COND( "Franklin Gothic Medium Cond" ),
    FREESTYLE_SCRIPT( "Freestyle Script" ),
    FRENCH_SCRIPT_MT( "French Script MT" ),
    GABRIOLA( "Gabriola" ),
    GADUGI( "Gadugi" ),
    GARAMOND( "Garamond" ),
    GEORGIA( "Georgia" ),
    GIGI( "Gigi" ),
    GILL_SANS_MT( "Gill Sans MT" ),
    GILL_SANS_MT_CONDENSED( "Gill Sans MT Condensed" ),
    GILL_SANS_MT_EXT_CONDENSED_BOLD( "Gill Sans MT Ext Condensed Bold" ),
    GILL_SANS_ULTRA_BOLD( "Gill Sans Ultra Bold" ),
    GILL_SANS_ULTRA_BOLD_CONDENSED( "Gill Sans Ultra Bold Condensed" ),
    GLOUCESTER_MT_EXTRA_CONDENSED( "Gloucester MT Extra Condensed" ),
    GOUDY_OLD_STYLE( "Goudy Old Style" ),
    GOUDY_STOUT( "Goudy Stout" ),
    HAETTENSCHWEILER( "Haettenschweiler" ),
    HARLOW_SOLID_ITALIC( "Harlow Solid Italic" ),
    HARRINGTON( "Harrington" ),
    HIGH_TOWER_TEXT( "High Tower Text" ),
    IMPACT( "Impact" ),
    IMPRINT_MT_SHADOW( "Imprint MT Shadow" ),
    INFORMAL_ROMAN( "Informal Roman" ),
    JAVANESE_TEXT( "Javanese Text" ),
    JOKERMAN( "Jokerman" ),
    JUICE_ITC( "Juice ITC" ),
    KRISTEN_ITC( "Kristen ITC" ),
    KUNSTLER_SCRIPT( "Kunstler Script" ),
    LEELAWADEE( "Leelawadee" ),
    LEELAWADEE_UI( "Leelawadee UI" ),
    LEELAWADEE_UI_SEMILIGHT( "Leelawadee UI Semilight" ),
    LUCIDA_BRIGHT( "Lucida Bright" ),
    LUCIDA_CALLIGRAPHY( "Lucida Calligraphy" ),
    LUCIDA_CONSOLE( "Lucida Console" ),
    LUCIDA_FAX( "Lucida Fax" ),
    LUCIDA_HANDWRITING( "Lucida Handwriting" ),
    LUCIDA_SANS( "Lucida Sans" ),
    LUCIDA_SANS_TYPEWRITER( "Lucida Sans Typewriter" ),
    LUCIDA_SANS_UNICODE( "Lucida Sans Unicode" ),
    MAGNETO( "Magneto" ),
    MAIANDRA_GD( "Maiandra GD" ),
    MALGUN_GOTHIC( "Malgun Gothic" ),
    MALGUN_GOTHIC_SEMILIGHT( "Malgun Gothic Semilight" ),
    MARLETT( "Marlett" ),
    MATURA_MT_SCRIPT_CAPITALS( "Matura MT Script Capitals" ),
    MICROSOFT_HIMALAYA( "Microsoft Himalaya" ),
    MICROSOFT_JHENGHEI( "Microsoft JhengHei" ),
    MICROSOFT_JHENGHEI_LIGHT( "Microsoft JhengHei Light" ),
    MICROSOFT_JHENGHEI_UI( "Microsoft JhengHei UI" ),
    MICROSOFT_JHENGHEI_UI_LIGHT( "Microsoft JhengHei UI Light" ),
    MICROSOFT_NEW_TAI_LUE( "Microsoft New Tai Lue" ),
    MICROSOFT_PHAGSPA( "Microsoft PhagsPa" ),
    MICROSOFT_SANS_SERIF( "Microsoft Sans Serif" ),
    MICROSOFT_TAI_LE( "Microsoft Tai Le" ),
    MICROSOFT_UIGHUR( "Microsoft Uighur" ),
    MICROSOFT_YAHEI( "Microsoft YaHei" ),
    MICROSOFT_YAHEI_LIGHT( "Microsoft YaHei Light" ),
    MICROSOFT_YAHEI_UI( "Microsoft YaHei UI" ),
    MICROSOFT_YAHEI_UI_LIGHT( "Microsoft YaHei UI Light" ),
    MICROSOFT_YI_BAITI( "Microsoft Yi Baiti" ),
    MINGLIU_EXTB( "MingLiU-ExtB" ),
    MINGLIU_HKSCS_EXTB( "MingLiU_HKSCS-ExtB" ),
    MISTRAL( "Mistral" ),
    MODERN_NO_20( "Modern No. 20" ),
    MONGOLIAN_BAITI( "Mongolian Baiti" ),
    MONOSPACED( "Monospaced" ),
    MONOTYPE_CORSIVA( "Monotype Corsiva" ),
    MS_GOTHIC( "MS Gothic" ),
    MS_OUTLOOK( "MS Outlook" ),
    MS_PGOTHIC( "MS PGothic" ),
    MS_REFERENCE_SANS_SERIF( "MS Reference Sans Serif" ),
    MS_REFERENCE_SPECIALTY( "MS Reference Specialty" ),
    MS_UI_GOTHIC( "MS UI Gothic" ),
    MT_EXTRA( "MT Extra" ),
    MV_BOLI( "MV Boli" ),
    MYANMAR_TEXT( "Myanmar Text" ),
    NIAGARA_ENGRAVED( "Niagara Engraved" ),
    NIAGARA_SOLID( "Niagara Solid" ),
    NIRMALA_UI( "Nirmala UI" ),
    NIRMALA_UI_SEMILIGHT( "Nirmala UI Semilight" ),
    NSIMSUN( "NSimSun" ),
    OCR_A_EXTENDED( "OCR A Extended" ),
    OLD_ENGLISH_TEXT_MT( "Old English Text MT" ),
    ONYX( "Onyx" ),
    PALACE_SCRIPT_MT( "Palace Script MT" ),
    PALATINO_LINOTYPE( "Palatino Linotype" ),
    PAPYRUS( "Papyrus" ),
    PARCHMENT( "Parchment" ),
    PERPETUA( "Perpetua" ),
    PERPETUA_TITLING_MT( "Perpetua Titling MT" ),
    PLAYBILL( "Playbill" ),
    PMINGLIU_EXTB( "PMingLiU-ExtB" ),
    POOR_RICHARD( "Poor Richard" ),
    PRISTINA( "Pristina" ),
    RAGE_ITALIC( "Rage Italic" ),
    RAVIE( "Ravie" ),
    ROCKWELL( "Rockwell" ),
    ROCKWELL_CONDENSED( "Rockwell Condensed" ),
    ROCKWELL_EXTRA_BOLD( "Rockwell Extra Bold" ),
    SANSSERIF( "SansSerif" ),
    SCRIPT_MT_BOLD( "Script MT Bold" ),
    SEGOE_MDL2_ASSETS( "Segoe MDL2 Assets" ),
    SEGOE_PRINT( "Segoe Print" ),
    SEGOE_SCRIPT( "Segoe Script" ),
    SEGOE_UI( "Segoe UI" ),
    SEGOE_UI_BLACK( "Segoe UI Black" ),
    SEGOE_UI_EMOJI( "Segoe UI Emoji" ),
    SEGOE_UI_HISTORIC( "Segoe UI Historic" ),
    SEGOE_UI_LIGHT( "Segoe UI Light" ),
    SEGOE_UI_SEMIBOLD( "Segoe UI Semibold" ),
    SEGOE_UI_SEMILIGHT( "Segoe UI Semilight" ),
    SEGOE_UI_SYMBOL( "Segoe UI Symbol" ),
    SERIF( "Serif" ),
    SHOWCARD_GOTHIC( "Showcard Gothic" ),
    SIMSUN( "SimSun" ),
    SIMSUN_EXTB( "SimSun-ExtB" ),
    SITKA_BANNER( "Sitka Banner" ),
    SITKA_DISPLAY( "Sitka Display" ),
    SITKA_HEADING( "Sitka Heading" ),
    SITKA_SMALL( "Sitka Small" ),
    SITKA_SUBHEADING( "Sitka Subheading" ),
    SITKA_TEXT( "Sitka Text" ),
    SNAP_ITC( "Snap ITC" ),
    STENCIL( "Stencil" ),
    SYLFAEN( "Sylfaen" ),
    SYMBOL( "Symbol" ),
    TAHOMA( "Tahoma" ),
    TEMPUS_SANS_ITC( "Tempus Sans ITC" ),
    TIMES_NEW_ROMAN( "Times New Roman" ),
    TREBUCHET_MS( "Trebuchet MS" ),
    TW_CEN_MT( "Tw Cen MT" ),
    TW_CEN_MT_CONDENSED( "Tw Cen MT Condensed" ),
    TW_CEN_MT_CONDENSED_EXTRA_BOLD( "Tw Cen MT Condensed Extra Bold" ),
    VERDANA( "Verdana" ),
    VINER_HAND_ITC( "Viner Hand ITC" ),
    VIVALDI( "Vivaldi" ),
    VLADIMIR_SCRIPT( "Vladimir Script" ),
    WEBDINGS( "Webdings" ),
    WIDE_LATIN( "Wide Latin" ),
    WINGDINGS( "Wingdings" ),
    WINGDINGS_2( "Wingdings 2" ),
    WINGDINGS_3( "Wingdings 3" ),
    YU_GOTHIC( "Yu Gothic" ),
    YU_GOTHIC_LIGHT( "Yu Gothic Light" ),
    YU_GOTHIC_MEDIUM( "Yu Gothic Medium" ),
    YU_GOTHIC_UI( "Yu Gothic UI" ),
    YU_GOTHIC_UI_LIGHT( "Yu Gothic UI Light" ),
    YU_GOTHIC_UI_SEMIBOLD( "Yu Gothic UI Semibold" ),
    YU_GOTHIC_UI_SEMILIGHT( "Yu Gothic UI Semilight" );

    private String fontFamily;

    private FontFamily( String fontFamily ) {
      this.fontFamily = fontFamily;
    }

    public String toString() {
      return this.fontFamily;
    }
  }

  public enum FontSize {

    SEVEN( "7" ),
    EIGHT( "8" ),
    NINE( "9" ),
    TEN( "10" ),
    ELEVEN( "11" ),
    TWELVE( "12" ),
    FOURTEEN( "14" ),
    SIXTEEN( "16" ),
    EIGHTEEN( "18" ),
    TWENTY( "20" ),
    TWENTY_FOUR( "24" ),
    TWENTY_EIGHT( "28" ),
    THIRTY_TWO( "32" ),
    THIRTY_SIX( "36" ),
    FORTY( "40" );

    private String fontSize;

    private FontSize( String fontSize ) {
      this.fontSize = fontSize;
    }

    public String toString() {
      return fontSize;
    }
  }

  // Settled on "FontStyle" even though italics are handled by "font-style" css property,
  // and boldness is handled by "font-weight"
  public enum FontStyle {

    NORMAL( L10N.getText( "dlgChartPropsLabelStyleTypeNormal" )),
    BOLD( L10N.getText( "dlgChartPropsLabelStyleTypeBold" )),
    ITALIC( L10N.getText( "dlgChartPropsLabelStyleTypeItalic" ));

    private String fontStyle;

    private FontStyle( String fontStyle ) {
      this.fontStyle = fontStyle;
    }

    public String toString() {
      return fontStyle;
    }
  }

  // This enum contains the 70 Colours that appear in a color palette (10x7 table)
  // This set of Colours is specified to constrain the input of setColorPicker(...) and other methods to acceptable
  // values
  public enum PaletteColour{
    BISQUE( Colour.BISQUE ),
    BLACK( Colour.BLACK ),
    BLUE( Colour.BLUE ),
    BLUE_VIOLET( Colour.BLUE_VIOLET ),
    BROWN( Colour.BROWN ),
    CHARTREUSE( Colour.CHARTREUSE ),
    CHOCOLATE( Colour.CHOCOLATE ),
    CORAL( Colour.CORAL ),
    CORNFLOWER_BLUE( Colour.CORNFLOWER_BLUE ),
    CORNSILK( Colour.CORNSILK ),
    CRIMSON( Colour.CRIMSON ),
    DARK_CYAN( Colour.DARK_CYAN ),
    DARK_GREEN( Colour.DARK_GREEN ),
    DARK_MAGENTA( Colour.DARK_MAGENTA ),
    DARK_OLIVE_GREEN( Colour.DARK_OLIVE_GREEN ),
    DARK_ORANGE( Colour.DARK_ORANGE ),
    DARK_ORCHID( Colour.DARK_ORCHID ),
    DARK_RED( Colour.DARK_RED ),
    DARK_SEA_GREEN( Colour.DARK_SEA_GREEN ),
    DARK_SLATE_BLUE( Colour.DARK_SLATE_BLUE ),
    DARK_SLATE_GRAY( Colour.DARK_SLATE_GRAY ),
    DIM_GRAY( Colour.DIM_GRAY ),
    FIRE_BRICK( Colour.FIRE_BRICK ),
    FOREST_GREEN( Colour.FOREST_GREEN ),
    GOLD( Colour.GOLD ),
    GRAY( Colour.GRAY ),
    GREEN( Colour.GREEN ),
    INDIGO( Colour.INDIGO ),
    KHAKI( Colour.KHAKI ),
    LAVENDER( Colour.LAVENDER ),
    LEMON_CHIFFON( Colour.LEMON_CHIFFON ),
    LIGHT_CORAL( Colour.LIGHT_CORAL ),
    LIGHT_CYAN( Colour.LIGHT_CYAN ),
    LIGHT_GRAY( Colour.LIGHT_GRAY ),
    LIGHT_GREEN( Colour.LIGHT_GREEN ),
    LIGHT_SEA_GREEN( Colour.LIGHT_SEA_GREEN ),
    LIGHT_SKY_BLUE( Colour.LIGHT_SKY_BLUE ),
    LIGHT_YELLOW( Colour.LIGHT_YELLOW ),
    LIME_GREEN( Colour.LIME_GREEN ),
    MAROON( Colour.MAROON ),
    MEDIUM_BLUE( Colour.MEDIUM_BLUE ),
    MEDIUM_ORCHID( Colour.MEDIUM_ORCHID ),
    MEDIUM_SLATE_BLUE( Colour.MEDIUM_SLATE_BLUE ),
    MEDIUM_TURQUOISE( Colour.MEDIUM_TURQUOISE ),
    MIDNIGHT_BLUE( Colour.MIDNIGHT_BLUE ),
    MOCCASIN( Colour.MOCCASIN ),
    NAVY( Colour.NAVY ),
    OLIVE( Colour.OLIVE ),
    ORANGE( Colour.ORANGE ),
    ORANGE_RED( Colour.ORANGE_RED ),
    ORCHID( Colour.ORCHID ),
    PALE_GOLDENROD( Colour.PALE_GOLDENROD ),
    PALE_GREEN( Colour.PALE_GREEN ),
    PALE_TURQUOISE( Colour.PALE_TURQUOISE ),
    PINK( Colour.PINK ),
    PLUM( Colour.PLUM ),
    PURPLE( Colour.PURPLE ),
    RED( Colour.RED ),
    ROYAL_BLUE( Colour.ROYAL_BLUE ),
    SADDLE_BROWN( Colour.SADDLE_BROWN ),
    SANDY_BROWN( Colour.SANDY_BROWN ),
    SEA_GREEN( Colour.SEA_GREEN ),
    SEASHELL( Colour.SEASHELL ),
    SIENNA( Colour.SIENNA ),
    SILVER( Colour.SILVER ),
    SKY_BLUE( Colour.SKY_BLUE ),
    SLATE_BLUE( Colour.SLATE_BLUE ),
    VIOLET( Colour.VIOLET ),
    WHITE( Colour.WHITE ),
    YELLOW( Colour.YELLOW );

    private Colour c;

    private PaletteColour( Colour c ) {
      this.c = c;
    }

    public Colour getColour() {
      return c;
    }

    public String getFormattedRGB() {
      return this.c.getFormattedRGB();
    }

    public String getHexValue() {
      return this.c.getHexValue();
    }
  }

  public enum CustomColor {
    RGB, HEX, COLOR_FIELD;
  }
}
