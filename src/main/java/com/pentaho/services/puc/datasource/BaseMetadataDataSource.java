package com.pentaho.services.puc.datasource;

import java.util.Map;

import com.pentaho.qa.gui.web.datasource.DataSourceImportAnalysisPage;
import com.pentaho.qa.gui.web.datasource.DataSourceImportBasePage;
import com.pentaho.qa.gui.web.datasource.DataSourceImportMetadataPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage.DataSourceType;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.BaseObject;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.utils.Utils;

public abstract class BaseMetadataDataSource extends BaseObject {

  protected String filePath;
  private DataSourceImportBasePage dsImportPage;

  public BaseMetadataDataSource( Map<String, String> args ) {
    super( args.get( "name" ) );
    this.filePath = args.get( "filePath" );
  }

  public BaseMetadataDataSource( String name, String filePath ) {
    super( name );
    this.filePath = filePath;
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( BaseMetadataDataSource copy ) {
    super.copy( copy );

    if ( copy.getFilePath() != null ) {
      this.filePath = copy.getFilePath();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public String getFilePath() {
    return filePath;
  }

  public void setFilePath( String filePath ) {
    this.filePath = filePath;
  }

  public DataSourceImportBasePage getImportPage() {
    return this.dsImportPage;
  }

  public ManageDataSourcesPage openManageDataSource() {
    HomePage homePage = new HomePage( getDriver() );
    return homePage.openManageDataSources();
  }

  /* -------------- EDIT DATA SOURCE ----------------- */
  public void edit( BaseMetadataDataSource newDataSource ) {
    open();
    newDataSource.setParameters();
    finish();

    softAssert.assertAll();
    copy( newDataSource );
  }

  public boolean create() {
    open();
    verifyDialog();
    setParameters();
    finish();

    boolean res = verify();
    softAssert.assertAll();

    return res;
  }

  public void open() {
    ManageDataSourcesPage manageDSPage = openManageDataSource();
    manageDSPage.openDataSourceContextMenu();
    if ( this instanceof AnalysisDataSource ) {
      manageDSPage.clickImportAnalysisButton();
      dsImportPage = new DataSourceImportAnalysisPage( getDriver() );
    } else if ( this instanceof MetadataDataSource ) {
      manageDSPage.clickImportMetadataButton();
      dsImportPage = new DataSourceImportMetadataPage( getDriver() );
    }
  }

  public abstract void setParameters();

  public void finish() {
    // overwrite by default
    finish( true );
  }

  public void finish( boolean confirm ) {
    dsImportPage.clickImport();
    if ( dsImportPage.isOverwriteConfirmationDialogPresent() ) {
      if ( confirm ) {
        LOGGER.info( "yes" );
        dsImportPage.clickOk();
      } else {
        dsImportPage.clickCancel();
      }
    }
  }

  /* -------------- DELETE DATA SOURCE WORKFLOW --------------- */
  public void delete() {
    delete( true );
  }

  public void delete( boolean remove ) {
    ManageDataSourcesPage manageDataSourcesPage = openManageDataSource();
    manageDataSourcesPage.removeDataSource( name );
    manageDataSourcesPage.confirmRemove( remove );
    manageDataSourcesPage.closeManageDataSources();
  }

  /* -------------- VERIFY DATA SOURCE ------------------------ */
  public boolean verify() {
    return verify( EXPLICIT_TIMEOUT );
  }

  public boolean verify( long timeout ) {
    // Verification part
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    if ( !manageDataSourcesPage.isOpened( EXPLICIT_TIMEOUT ) ) {
      HomePage homePage = new HomePage( getDriver() );
      manageDataSourcesPage = homePage.openManageDataSources();
    }
    boolean res = false;
    if ( this instanceof AnalysisDataSource ) {
      res = manageDataSourcesPage.isDataSourcePresent( name, DataSourceType.ANALYSIS.getName() );
    } else if ( this instanceof MetadataDataSource ) {
      res = manageDataSourcesPage.isDataSourcePresent( name, DataSourceType.METADATA.getName() );
    }
    manageDataSourcesPage.closeManageDataSources();

    return res;
  }

  public void verifyDialog() {
    dsImportPage.verify();
  }

  /* -------------- EXPORT DATA SOURCE ----------------- */
  public String export() {
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    if ( !manageDataSourcesPage.isOpened( EXPLICIT_TIMEOUT / 10 ) ) {
      HomePage homePage = new HomePage( getDriver() );
      manageDataSourcesPage = homePage.openManageDataSources();
    }
    // build file name based on type
    String fileName = null;
    if ( this instanceof AnalysisDataSource ) {
      // hard-coded name because Analysis are always exported with that name 
      fileName = name + ".mondrian.xml";
    } else if ( this instanceof MetadataDataSource ) {
      fileName = name + ".xmi";
    }
    // clear any file with the same name to ensure export worked.
    Utils.clearDownloadsFile( fileName );
    
    // perform export and verify file is present in Downloads folder
    manageDataSourcesPage.export( name );
    if ( !Utils.checkDownloadsFile( fileName ) ) {
      CustomAssert.fail( "51752", "Metadata " + name + " was not exported!" );
      CustomAssert.fail( "51753", "Analysis " + name + " was not exported!" );
      return null;
    }
    
    // clean up
    Utils.clearDownloadsFile( fileName );
    
    manageDataSourcesPage.closeManageDataSources();

    return fileName;
  }
}
