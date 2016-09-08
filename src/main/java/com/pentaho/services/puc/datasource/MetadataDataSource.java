package com.pentaho.services.puc.datasource;

import java.util.Map;

import com.pentaho.qa.gui.web.datasource.DataSourceImportMetadataPage;

public class MetadataDataSource extends BaseMetadataDataSource {

  private String domainId;
  DataSourceImportMetadataPage importMetadataPage;

  public MetadataDataSource( Map<String, String> args ) {
    this( args.get( "name" ), args.get( "filePath" ), args.get( "domainId" ) );
  }

  public MetadataDataSource( String name, String filePath, String domainId ) {
    super( name, filePath );
    this.filePath = filePath;
    this.domainId = domainId;
    // [VD] prohibit to add counter into the name during retry execution as metadata can be imported only!
    addCounterToName = false; 
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public String getDomainId() {
    return domainId;
  }

  public void setDomainId( String domainId ) {
    this.domainId = domainId;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( MetadataDataSource copy ) {
    super.copy( copy );

    if ( copy.getFilePath() != null ) {
      this.filePath = copy.getFilePath();
    }

    if ( copy.getDomainId() != null ) {
      this.domainId = copy.getDomainId();
    }
  }

  /* -------------- EDIT DATA SOURCE ----------------- */
  public void edit( MetadataDataSource newDataSource ) {
    super.edit( newDataSource );
    copy( newDataSource );
  }

  public void setParameters() {

      importMetadataPage = new DataSourceImportMetadataPage( getDriver() );
      importMetadataPage.setXmiFile( filePath );
      importMetadataPage.setDomainId( domainId );
   
  }
}
