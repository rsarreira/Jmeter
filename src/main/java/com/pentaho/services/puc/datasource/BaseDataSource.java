package com.pentaho.services.puc.datasource;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.services.BaseObject;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.utils.Utils;

public abstract class BaseDataSource extends BaseObject {
  protected DataSourceWizardPage dsWizardPage;

  protected Boolean customizedModel;

  public enum Workflow {
    // New
    NEW,
    // Edit
    EDIT,
    // Remove
    REMOVE;
  }

  public BaseDataSource( String name, Boolean customizedModel ) {
    super( name );
    this.customizedModel = customizedModel;
  }

  public BaseDataSource( Map<String, String> args ) {
    super( args.get( "name" ) );
    this.customizedModel = Boolean.valueOf( args.get( "customizedModel" ) );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( BaseDataSource copy ) {
    super.copy( copy );
    if ( copy.getCustomizedModel() != null ) {
      this.customizedModel = copy.getCustomizedModel();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */
  public Boolean getCustomizedModel() {
    return customizedModel;
  }

  public void setCustomizedModel( Boolean customizedModel ) {
    this.customizedModel = customizedModel;
  }

  public DataSourceWizardPage getDsWizardPage() {
    return dsWizardPage;
  }

  public void setDsWizardPage( DataSourceWizardPage dsWizardPage ) {
    this.dsWizardPage = dsWizardPage;
  }

  /* ----------------------------------------------------------- */

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

  /* -------------- EDIT DATA SOURCE WORKFLOW ----------------- */
  public void edit( BaseDataSource newDataSource ) {
    openWizard( Workflow.EDIT );

    newDataSource.setParameters();

    finishWizard( Workflow.EDIT );

    softAssert.assertAll();
  }

  public ManageDataSourcesPage openManageDataSource() {
    HomePage homePage = new HomePage( getDriver() );
    return homePage.openManageDataSources();
  }

  /* -------------- CREATE DATA SOURCE WORKFLOW --------------- */
  public boolean create() {
    openWizard();
    setName();
    selectType();
    setParameters();
    finishWizard();

    boolean res = verify();
    softAssert.assertAll();

    return res;
  }

  public DataSourceWizardPage createWithoutOpen( DataSourceWizardPage dataSourceWizardPage ) {
    dsWizardPage = dataSourceWizardPage;
    setName();
    selectType();
    setParameters();
    return dsWizardPage;
  }

  public void openWizard() {
    openWizard( Workflow.NEW );
  }

  public void openWizard( Workflow workflow ) {
    HomePage homePage = new HomePage( getDriver() );

    switch ( workflow ) {
      case NEW:
        dsWizardPage = homePage.openDataSourceWizard();
        break;
      case EDIT:
        dsWizardPage = homePage.openManageDataSources().editDataSource( this ).editSource();
        break;
      default:
        Assert.fail( "Unsupported workflow is provided for opening data sources " + workflow );
        break;
    }
  }

  public void selectType() {
    if ( this instanceof SQLDataSource ) {
      dsWizardPage.setDataSourceType( DataSourceWizardPage.SOURCE_TYPE_SQL_QUERY );
    } else if ( this instanceof CSVDataSource ) {
      dsWizardPage.setDataSourceType( DataSourceWizardPage.SOURCE_TYPE_CSV );
    } else if ( this instanceof DBTableDataSource ) {
      dsWizardPage.setDataSourceType( DataSourceWizardPage.SOURCE_TYPE_DB_TABLES );
    }
  }

  public void setName() {
    dsWizardPage.setDataSourceName( name );
  }

  public abstract void setParameters();

  public boolean preview( int count ) {
    return dsWizardPage.preview( count );
  }

  public void finishWizard() {
    finishWizard( Workflow.NEW, EXPLICIT_TIMEOUT );
  }

  public void finishWizard( Workflow workflow ) {
    finishWizard( workflow, EXPLICIT_TIMEOUT );
  }

  public void finishWizard( Workflow workflow, long timeout ) {
    dsWizardPage.buttonFinish();
    switch ( workflow ) {
      case NEW:
        if ( !dsWizardPage.isDataSourceCreatedDialogAppears( timeout ) ) {
          CustomAssert.fail( "42021", "'Data Source Created' dialog is not opened!" );
          CustomAssert.fail( "40843", "'Data Source Created' dialog is not opened!" );
        }

        if ( !dsWizardPage.verifyModelRadioButtons() ) {
          CustomAssert.fail( "42233", "One or both Model radio buttons don't exist!" );
        }

        if ( !dsWizardPage.isModelRadioButtonDefaultSelected() ) {
          CustomAssert.fail( "42233", "'Keep Default Model' radio buttons isn't selected by default!" );
          CustomAssert.fail( "40843", "'Keep Default Model' radio buttons isn't selected by default!" );
        }

        dsWizardPage.setCustomizeModel( getCustomizedModel() );
        dsWizardPage.buttonOK();
        if ( getCustomizedModel() ) {
          DataSourceModelEditorPage dataSourceModelEditorPage = new DataSourceModelEditorPage( getDriver() );
          dataSourceModelEditorPage.buttonOK();
        }
        dsWizardPage.makeClickable();
        break;
      case EDIT:
        DataSourceModelEditorPage dataSourceModelEditorPage = new DataSourceModelEditorPage( getDriver() );
        // dataSourceModelEditorPage.autoPopulateModel();
        dataSourceModelEditorPage.buttonOK();
        break;
      default:
        Assert.fail( "Unsupported workflow is provided for closing data sources " + workflow );
        break;
    }
  }

  public void cancelWizard() {
    dsWizardPage.buttonCancel();
  }

  /* -------------- VERIFY DATA SOURCE ------------------------ */
  public boolean verify() {
    return verify( EXPLICIT_TIMEOUT );
  }

  public boolean verify( long timeout ) {
    // Verification part
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    if ( !manageDataSourcesPage.isOpened( EXPLICIT_TIMEOUT / 10 ) ) {
      HomePage homePage = new HomePage( getDriver() );
      manageDataSourcesPage = homePage.openManageDataSources();
    }

    boolean res = manageDataSourcesPage.isDataSourcePresent( name, timeout );
    manageDataSourcesPage.closeManageDataSources();

    return res;
  }

  /* -------------- IMPORT/EXPORT DATA SOURCE ----------------- */
  public void importDS() {
    // TODO: implement when Pentaho implements this functionally
    Assert.fail( "Currently not supported/implemented by Pentaho." );
  }

  public String exportDS() {
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    if ( !manageDataSourcesPage.isOpened( EXPLICIT_TIMEOUT / 10 ) ) {
      HomePage homePage = new HomePage( getDriver() );
      manageDataSourcesPage = homePage.openManageDataSources();
    }

    String fileName = name + ".zip";
    // clear any file with the same name to ensure export worked.
    Utils.clearDownloadsFile( fileName );

    // perform export and verify file is present in Downloads folder
    manageDataSourcesPage.export( name );
    if ( !Utils.checkDownloadsFile( fileName ) ) {
      LOGGER.error( "Data Source " + name + " was not exported!" );
      return null;
    }

    return fileName;
  }
}
