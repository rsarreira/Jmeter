package com.pentaho.services.utils;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.testng.asserts.SoftAssert;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.qaprosoft.carina.core.foundation.report.ReportContext;
import com.qaprosoft.carina.core.foundation.utils.resources.L10N;
import com.qaprosoft.carina.core.foundation.webdriver.DriverPool;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class Utils {

  protected static final Logger LOGGER = Logger.getLogger( Utils.class );

  public static String getDownloadsFolder() {

    if ( !ReportContext.getAllArtifacts().isEmpty() ) {
      return ReportContext.getArtifactsFolder().getAbsolutePath() + File.separator;
    }

    String downloadsFolder = System.getProperty( "user.home" ) + File.separator + "Downloads" + File.separator;
    LOGGER.info( "Downloads folder: " + downloadsFolder );
    return downloadsFolder;
  }

  public static void clearDownloadsFile( String file ) {
    File artifact = ReportContext.getArtifact( file );
    if ( artifact != null ) {
      LOGGER.info( "Removing file: " + artifact.getAbsolutePath() );
      artifact.delete();
      return;
    }

    String downloadFolder = getDownloadsFolder();
    File fl = new File( downloadFolder + file );
    if ( fl.exists() ) {
      LOGGER.info( "Removing file: " + fl.getAbsolutePath() );
      fl.delete();
    }
  }

  public static boolean checkDownloadsFile( String file ) {
    if ( ReportContext.getArtifact( file ) != null ) {
      return true;
    }

    String downloadFolder = getDownloadsFolder();
    File fl = new File( downloadFolder + file );

    boolean res = fl.exists();
    if ( !res ) {
      LOGGER.error( "File " + file + " doesn't exist!" );
    }
    return res;
  }

  public static void unzip( String file ) {
    String source = getDownloadsFolder() + file;
    checkDownloadsFile( file );
    ZipFile zipFile = null;
    try {
      zipFile = new ZipFile( source );
    } catch ( ZipException e1 ) {
      e1.printStackTrace();
    }
    try {
      zipFile.extractAll( getDownloadsFolder() );
    } catch ( ZipException e ) {
      e.printStackTrace();
    }
  }

  public static String getValueByTag( String xmlFile, String tagName, String tagValue, String attributeName ) {
    File file = new File( getDownloadsFolder() + xmlFile );
    String attributeValue = null;
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
      db = dbf.newDocumentBuilder();
    } catch ( ParserConfigurationException e ) {
      e.printStackTrace();
    }
    Document document = null;
    try {
      document = db.parse( file );
    } catch ( SAXException e ) {
      e.printStackTrace();
    } catch ( IOException e ) {
      e.printStackTrace();
    }
    NodeList nodeList = document.getElementsByTagName( tagName );
    for ( int x = 0, size = nodeList.getLength(); x < size; x++ ) {
      Node node = nodeList.item( x ).getAttributes().getNamedItem( "name" );
      if ( node != null && tagValue.equals( node.getNodeValue() ) ) {
        System.out.println( ( nodeList.item( x ).getAttributes().getNamedItem( attributeName ) ) );
        attributeValue = nodeList.item( x ).getAttributes().getNamedItem( attributeName ).getTextContent();
      }
    }
    return attributeValue;
  }

  public static final String toHexString( Color colour ) throws NullPointerException {
    String hexColour = Integer.toHexString( colour.getRGB() & 0xffffff );
    if ( hexColour.length() < 6 ) {
      hexColour = "000000".substring( 0, 6 - hexColour.length() ) + hexColour;
    }
    return "#" + hexColour.toLowerCase();
  }

  public static String localizeNumberString( Locale locale, String value, String pattern ) {
    // Convert String to double, removing currency sign and separator is a prerequisite
    value = value.replace( "$", "" );
    value = value.replace( "%", "" );
    value = value.replace( ",", "" );
    double doubleValue = Double.parseDouble( value );

    // Create locale dependent formatter
    NumberFormat nf = NumberFormat.getInstance( locale );
    DecimalFormat df = (DecimalFormat) nf;
    df.applyPattern( pattern );

    // Apply formatter
    value = df.format( doubleValue );
    return value;
  }

  public static List<String> parsePath( String path ) {
    List<String> items = new ArrayList<String>( Arrays.asList( path.split( "/" ) ) );

    if ( items.size() > 0 ) {
      if ( items.get( 0 ).isEmpty() ) {
        items.remove( 0 );
      }
    }
    return items;
  }

  public static void dnd( ExtendedWebElement source, ExtendedWebElement dest ) {
    // Need moveByOffset( 1, 1 ) to workaround DND
    Actions dragAndDrop = new Actions( DriverPool.getDriverByThread() );
    Action action =
        dragAndDrop.clickAndHold( source.getElement() ).moveByOffset( 1, 1 ).release( dest.getElement() ).build();
    try {
      action.perform();
    } catch ( MoveTargetOutOfBoundsException e ) {
      LOGGER.error( "Exception occurs during drag and drop!: " + e.toString() );
      dragAndDrop.release().build().perform();
    }
  }

  /**
   * Removes the % sign from locale strings that are used for formatting, and then formats the string using the
   * specified args.
   * 
   * @param format
   *          The string to format.
   * @param args
   *          The strings used to replace the format specifiers.
   * @return Returns the formatted string.
   */
  public static String formatRemovePercent( String format, String... args ) {
    return L10N.formatString( format.replace( "%", "" ), args );
  }

  /**
   * Parses all text that has an inner element (i.e. "Some text <span> more text </span> end text").
   * 
   * @param text
   *          The HTML to be parsed.
   * @param tagName
   *          The name of the tag within the HTML.
   * @return Returns the text that is before, within, and after the specified tag (i.e. "Some text more text end text").
   */
  public static String parseTextFromInnerHtml( String text, String tagName ) {
    String parsedText = "";
    String closeElement = ">";
    String endTag = "</" + tagName + ">";

    int startIndex = text.indexOf( "<" + tagName );
    int midIndex = text.indexOf( closeElement );
    int endIndex = text.indexOf( endTag );

    if ( startIndex > 0 && endIndex > 0 ) {
      // Parse all text before the beginning of the tag.
      parsedText = text.substring( 0, startIndex );

      // Parse all text within the element.
      parsedText += text.substring( midIndex + closeElement.length(), endIndex );

      // Parse all text after the end of the tag.
      parsedText += text.substring( endIndex + endTag.length(), text.length() );
    }

    return parsedText;
  }

  /**
   * Drags an element to another element without releasing it at the destination.
   * 
   * @param source
   *          The element to drag.
   * @param destination
   *          The destination element to drag the source to.
   */
  public static void dragToElement( ExtendedWebElement source, ExtendedWebElement destination ) {
    dragToElement( source, destination, 0, 0 );
  }

  /**
   * Drags an element to another element without releasing it at the destination.
   * 
   * @param source
   *          The element to drag.
   * @param destination
   *          The destination element to drag the source to.
   * @param xOffset
   *          The horizontal offset for the destination. A negative value will move the mouse to the left after the
   *          field has been moved to the destination.
   * @param yOffset
   *          The vertical offset for the destination. A negative value will move the mouse up after the field has been
   *          moved to the destination.
   */
  public static void dragToElement( ExtendedWebElement source, ExtendedWebElement destination, int xOffset,
      int yOffset ) {
    Actions builder = new Actions( DriverPool.getDriverByThread() );
    Action dragField =
        builder.clickAndHold( source.getElement() ).moveByOffset( 1, 1 ).moveToElement( destination.getElement() )
            .moveByOffset( xOffset, yOffset ).build();
    dragField.perform();
  }

  /**
   * Performs a mouse up action.
   */
  public static void mouseRelease() {
    Actions builder = new Actions( DriverPool.getDriverByThread() );
    Action release = builder.release().build();
    release.perform();
  }

  public String convertTimeForDefaultTimeZone( String date, String zone ) {
    Date inputDate = null;
    String first = "0";
    String convertedDate = null;
    SimpleDateFormat sdf1 = new SimpleDateFormat( "hh:mm a" );
    sdf1.setTimeZone( java.util.TimeZone.getTimeZone( zone ) );

    SimpleDateFormat sdf2 = new SimpleDateFormat( "hh:mm a" );
    sdf2.setTimeZone( java.util.TimeZone.getTimeZone( java.util.TimeZone.getDefault().getID() ) );
    try {
      inputDate = sdf1.parse( date );
    } catch ( ParseException e ) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    // By default if the date contains the first symbol 'zero', PUC doesn't display it.
    if ( sdf2.format( inputDate ).startsWith( first ) ) {
      convertedDate = sdf2.format( inputDate ).substring( 1 );
    } else
      convertedDate = sdf2.format( inputDate );
    return convertedDate;
  }

  // method serves to adding any time values to the current time.
  public List<String> getCalculatedTime( String hours, String minutes ) {
    SimpleDateFormat df = new SimpleDateFormat( "hh:mm a" );
    Calendar cal = Calendar.getInstance(); // creates calendar
    cal.setTime( new Date() ); // sets calendar time/date
    cal.add( Calendar.HOUR_OF_DAY, ( Integer.parseInt( hours ) ) ); // adds one hour
    cal.add( Calendar.MINUTE, ( Integer.parseInt( minutes ) ) );

    String calculatedTime = df.format( cal.getTime() );
    List<String> parsedTime = Arrays.asList( calculatedTime.split( "[:,\\s]" ) ); // split date
    return parsedTime;
  }

  public String getCurrentDateTime() {
    SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
    // get current date time with Calendar()
    Calendar cal = Calendar.getInstance();
    return dateFormat.format( cal.getTime() );
  }

  public static void actionClickMiddle( WebDriver driver, ExtendedWebElement elementToClick ) {
    int widthElement = elementToClick.getElement().getSize().getWidth();
    int heightElement = elementToClick.getElement().getSize().getHeight();
    Actions actions = new Actions( driver );
    actions.moveToElement( elementToClick.getElement(), widthElement / 2, heightElement / 2 ).click().perform();
  }

  /**
   * This method converts a specified XML tag's attributes into a attribute -> value HashMap. NOTE: The current
   * implementation is "greedy" and "stupid"; it only operates on the first instance of the specified tag, and there is,
   * currently, no mechanism to be more specific
   * 
   * @param targetTag
   *          String name of the tag whose attributes are to be parsed
   * @param rawText
   *          String representation of XML to be parsed
   * @return output HashMap containing attribute -> value pairs
   */
  public static HashMap<String, String> parseXMLTagAttributes( String targetTag, String rawText ) {
    HashMap<String, String> output = null;
    ArrayList<String> list = null;
    String[] tmp = null;
    String theXML = null;
    int nAttributes;

    Pattern p = Pattern.compile( "<" + targetTag + " (.*)\\/>" );
    Matcher m = p.matcher( rawText );
    
    // see if we can find the target tag 
    if ( !m.find() ) {
      LOGGER.error( "XML tag <" + targetTag + "> not found! Returning null!" );
      return null;
    }

    // if we got this far, the tag was found
    // get the attributes and remove all quotes
    theXML = m.group( 1 ).replaceAll( "\"", "" );
    
    // convert the attributes into a list based on delimiter of "whitespace"
    list = new ArrayList<String>( Arrays.asList( theXML.split( "[\\s]+" ) ) );

    // get the size of the resulting list (number of attributes)
    nAttributes = list.size();

    // setup the output HashMap
    output = new HashMap<String, String>( nAttributes );

    // for each "attribute=value" pair...
    for ( String s : list ) {
      // split on "="
      tmp = s.split( "=" );
      
      // add attribute -> val to the HashMap
      output.put( tmp[0], tmp[1]);
    }
    return output;
  }

  /***
   * This method validates the attribute values of an XML element.
   * 
   * @param softAssert the SoftAssert that will be "appended" via this method. Passing null will result in a new SoftAssert.
   * @param targetTag String that contains the targeted XML tag
   * @param theXML String that contains the XML to be operated on
   * @param expectedAttributeValues HashMap<String,String> of expected attribute->value pairs
   * @return softAssert
   */
  public static SoftAssert validateXMLTagAttributes( SoftAssert softAssert, String targetTag, String theXML,
      HashMap<String, String> expectedAttributeValues ) {
    // work on the passed in softAssert unless it is null
    // if null, create a new one
    if ( softAssert == null ) {
      softAssert = new SoftAssert();
    }

    // get the "actual" mapped attribute -> values
    HashMap<String, String> actualAttributeValues = parseXMLTagAttributes( targetTag, theXML );

    LOGGER.info("Start verification of XML attributes.");
    
    // loop through each "expected" entry and make sure it has a matching "actual" entry
    for ( Map.Entry<String, String> entry : expectedAttributeValues.entrySet() ) {
      LOGGER.debug( "validateXMLTagAttributes: key: " + entry.getKey() + "\t expected: " + entry.getValue()
          + "\t actual:" + actualAttributeValues.get( entry.getKey() ) );
      softAssert.assertTrue( actualAttributeValues.get( entry.getKey() ).equals( entry.getValue() ),
          "Expected attribute->value pair not found in XML: " + entry.getKey() + "->" + entry.getValue() );
    }
    
    LOGGER.info("End verification of XML attributes.");
    
    return softAssert;
  }
  
  // TODO move findFromElem to an appropriate home
  /***
   * This method is designed to find an element based off of another.
   * 
   * The Carina methods, in ExtendedWebElement.java, "findExtendedWebElement" fall short: - cannot access parents via
   * xpath ".." - pathing appears to get messed up when you do multiple hops, i.e. get elem B from A, then C from B
   * 
   * @param theParent
   * @param pathToChild
   * @return ExtendedWebElement
   */
  public static ExtendedWebElement findFromElem( ExtendedWebElement theElem, String targetPath ) {
    String byString, byMethod, byPath;
    String thePath = "";

    // ASSUME IT IS A SIMPLE STRING FOR NOW
    // IT COULD CONTAIN MANY BRACKETS AND ARROWS
    // IN WHICH CASE, MORE LOGIC NEEDED

    byString = theElem.getBy().toString();

    // expected byString like:
    // 'By.xpath: //div[contains(@class,'field') and text()='Sales' and contains(@formula,'[Measures]')]'
    // use regex to isolate 'By.xpath' and
    // '//div[contains(@class,'field') and text()='Sales' and contains(@formula,'[Measures]')]';

    Pattern p = Pattern.compile( "(By.{2,16}): (.*)" );
    Matcher m = p.matcher( byString );

    m.find();

    byMethod = m.group( 1 ).substring( 3 );
    byPath = m.group( 2 );

    // TODO implement logic for all byMethods
    switch ( byMethod ) {
      case "id":
        thePath += "//*[@id='" + byPath + "']";
        break;
      case "xpath":
        thePath += byPath;
        break;
      case "linkText":
        //thePath += "";
        //break;
      case "partialLinkText":
        //thePath += "";
        //break;
      case "name":
        //thePath += "";
        //break;
      case "tagName":
        //thePath += "";
        //break;
      case "className":
        //thePath += "";
        //break;
      case "cssSelector":
        //thePath += "";
        LOGGER.error( "By method not yet implemented: " + byMethod );
        break;
      default:
        LOGGER.error( "By method not found: " + byMethod );
    }

    // if the targetPath does not start with a "/" i.e. "..", add the "/"
    if ( targetPath.charAt( 0 ) != '/' ) {
      thePath += "/" + targetPath;
    } else {
      thePath += targetPath;
    }

    return new ExtendedWebElement( DriverPool.getDriverByThread().findElement( By.xpath( thePath ) ), ("ChildOf"+theElem.getName()), By.xpath( thePath ) );

  }
}
