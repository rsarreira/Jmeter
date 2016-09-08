package com.pentaho.services.puc.datasource;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.datasource.DataSourceCSVPage;

public class CSVDataSource extends BaseDataSource {

  protected String filePath;
  protected String encoding;
  protected String delimiter;
  protected String enclosure;
  protected Boolean headerRow;

  public CSVDataSource( Map<String, String> args ) {
    super( args );

    this.filePath = args.get( "filePath" );
    this.encoding = args.get( "encoding" );
    this.delimiter = args.get( "delimiter" );
    this.enclosure = args.get( "enclosure" );
    this.headerRow = args.get( "headerRow" ) != null ? Boolean.valueOf( args.get( "headerRow" ) ) : null;
  }

  public CSVDataSource( String name, String filePath, String encoding, String delimiter,
      String enclosure, Boolean headerRow, Boolean customizedModel ) {
    super( name, customizedModel );

    this.filePath = filePath;
    this.encoding = encoding;
    this.delimiter = delimiter;
    this.enclosure = enclosure;
    this.headerRow = headerRow;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( CSVDataSource copy ) {
    super.copy( copy );

    if ( copy.getFilePath() != null ) {
      this.filePath = copy.getFilePath();
    }
    if ( copy.getEncoding() != null ) {
      this.encoding = copy.getEncoding();
    }
    if ( copy.getDelimiter() != null ) {
      this.delimiter = copy.getDelimiter();
    }
    if ( copy.getEnclosure() != null ) {
      this.enclosure = copy.getEnclosure();
    }
    if ( copy.getHeaderRow() != null ) {
      this.headerRow = copy.getHeaderRow();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public String getFilePath() {
    return filePath;
  }

  public void setFilePath( String filePath ) {
    this.filePath = filePath;
  }

  public String getEncoding() {
    return encoding;
  }

  public void setEncoding( String encoding ) {
    this.encoding = encoding;
  }

  public String getDelimiter() {
    return delimiter;
  }

  public void setDelimiter( String delimiter ) {
    this.delimiter = delimiter;
  }

  public String getEnclosure() {
    return enclosure;
  }

  public void setEnclosure( String enclosure ) {
    this.enclosure = enclosure;
  }

  public Boolean getHeaderRow() {
    return headerRow;
  }

  public void setHeaderRow( Boolean headerRow ) {
    this.headerRow = headerRow;
  }

  /* -------------- EDIT DATA SOURCE ----------------- */
  public void edit( CSVDataSource newDataSource ) {
    super.edit( newDataSource );
    copy( newDataSource );
  }

  public void setParameters() {

    DataSourceCSVPage csvDsPage = null;
    try {
      csvDsPage = new DataSourceCSVPage( getDriver() );
    } catch ( AssertionError e ) {
      Assert.fail( "TS042017: " + e.getMessage() );
    } catch ( Exception e ) {
      Assert.fail( e.getMessage() );
    }

    csvDsPage.setStagingSettings( filePath );

    csvDsPage.setEncoding( encoding );
    csvDsPage.setDelimiter( delimiter );
    csvDsPage.setEnclosure( enclosure );
    csvDsPage.setFirstHeaderRow( headerRow );

    csvDsPage.buttonNext();
    // TODO: implement columns unselection
  }

}
