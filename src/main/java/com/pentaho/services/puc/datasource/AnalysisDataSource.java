package com.pentaho.services.puc.datasource;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.datasource.DataSourceImportAnalysisPage;

public class AnalysisDataSource extends BaseMetadataDataSource {

  public enum DataSourcePreference {
    SELECT_AVAILABLE, ENTER_MANUALLY
  }

  protected boolean selectAvailable;
  protected String dataSource;

  DataSourceImportAnalysisPage importAnalysisPage;

  public AnalysisDataSource( Map<String, String> args ) {
    super( args.get( "name" ), args.get( "filePath" ) );
    this.selectAvailable = Boolean.valueOf( args.get( "selectAvailable" ) );
    this.dataSource = args.get( "dataSource" );
  }

  public AnalysisDataSource( String name, String filePath, boolean selectAvailable, String dataSource ) {
    super( name, filePath );
    this.selectAvailable = selectAvailable;
    this.dataSource = dataSource;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( AnalysisDataSource copy ) {
    super.copy( copy );

    if ( copy.getFilePath() != null ) {
      this.filePath = copy.getFilePath();
    }

    if ( copy.isSelectAvailable() ) {
      this.selectAvailable = copy.isSelectAvailable();
    }

    if ( copy.getDataSource() != null ) {
      this.dataSource = copy.getDataSource();
    }

  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public boolean isSelectAvailable() {
    return selectAvailable;
  }

  public void setSelectAvailable( boolean selectAvailable ) {
    this.selectAvailable = selectAvailable;
  }

  public String getDataSource() {
    return dataSource;
  }

  public void setDataSource( String dataSource ) {
    this.dataSource = dataSource;
  }

  public void setParameters() {
    setParameters( DataSourcePreference.SELECT_AVAILABLE );
  }

  public void setParameters( DataSourcePreference preference ) {

    importAnalysisPage = new DataSourceImportAnalysisPage( getDriver() );

    switch ( preference ) {
      case ENTER_MANUALLY:
        // TODO: implement manually entering data source parameters
      case SELECT_AVAILABLE:
        importAnalysisPage.setMondrianFile( filePath );
        importAnalysisPage.setSelectAvailable( selectAvailable );
        importAnalysisPage.selectDataSource( dataSource );
        break;
      default:
        Assert.fail( "Unsupported preference is provided for creating Analysis data source:" + preference );
        break;
    }

  }
  
  public void setParameters( String dataSourceName ) {

    importAnalysisPage = new DataSourceImportAnalysisPage( getDriver() );
    importAnalysisPage.setMondrianFile( filePath );
    importAnalysisPage.setSelectAvailable( selectAvailable );
    importAnalysisPage.selectDataSource( dataSourceName );

  }

}
