package com.pentaho.services.puc.connection;

import java.util.Map;

import org.testng.Assert;

import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.gui.web.puc.connection.DatabaseConnectionPage;
import com.pentaho.services.BaseObject;

public abstract class BaseConnection extends BaseObject {
  protected String dbType;
  protected String accessType;

  protected String dbName;

  protected String user;
  protected String password;

  protected Map<String, String> options = null;

  protected DatabaseConnectionPage connPage;

  public enum Workflow {
    // New
    MANAGE_DS_POPUP, WIZARD_SQL_QUERY, WIZARD_DB_TABLES,
    // Edit
    WIZARD_DB_TABLES_EDIT, WIZARD_SQL_QUERY_EDIT,
    // Remove
    WIZARD_DB_TABLES_REMOVE, WIZARD_SQL_QUERY_REMOVE;
  }

  // Advanced
  // Options
  // Pooling

  public BaseConnection( String name, String dbType, String accessType, String dbName, String user, String password ) {
    super( name );
    this.dbType = dbType;
    this.accessType = accessType;
    this.dbName = dbName;
    this.user = user;
    this.password = password;
  }

  public BaseConnection( Map<String, String> args ) {
    super( args.get( "name" ) );
    this.dbType = args.get( "dbType" );
    this.accessType = args.get( "accessType" );

    this.dbName = args.get( "dbName" );

    this.user = args.get( "user" );
    this.password = args.get( "password" );
  }

  /* ------------------- COPY CONSTRUCTOR ---------------------- */
  public void copy( BaseConnection copy ) {
    super.copy( copy );

    if ( copy.getDbType() != null ) {
      this.dbType = copy.getDbType();
    }
    if ( copy.getAccessType() != null ) {
      this.accessType = copy.getAccessType();
    }
    if ( copy.getDbName() != null ) {
      this.dbName = copy.getDbName();
    }
    if ( copy.getUser() != null ) {
      this.user = copy.getUser();
    }
    if ( copy.getPassword() != null ) {
      this.password = copy.getPassword();
    }
  }

  /* ------------------- GETTER/SETTER ------------------------- */

  public String getDbType() {
    return dbType;
  }

  public void setDbType( String dbType ) {
    this.dbType = dbType;
  }

  public String getAccessType() {
    return accessType;
  }

  public void setAccessType( String accessType ) {
    this.accessType = accessType;
  }

  public String getDbName() {
    return dbName;
  }

  public void setDbName( String dbName ) {
    this.dbName = dbName;
  }

  public String getUser() {
    return user;
  }

  public void setUser( String user ) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword( String password ) {
    this.password = password;
  }

  public Map<String, String> getOptions() {
    return options;
  }

  public void setOptions( Map<String, String> options ) {
    this.options = options;
  }

  public DatabaseConnectionPage getConnectionPage() {
    connPage.switchWindow();
    return connPage;
  }

  public void setConnectionPage( DatabaseConnectionPage connPage ) {
    this.connPage = connPage;
  }

  /* -------------- DELETE DATA SOURCE WORKFLOW --------------- */
  public void delete() {
    delete( Workflow.MANAGE_DS_POPUP );
  }

  public void delete( Workflow workflow ) {
    HomePage homePage = new HomePage( getDriver() );

    switch ( workflow ) {
      case MANAGE_DS_POPUP:
        ManageDataSourcesPage manageDataSourcesPage = homePage.openManageDataSources();
        manageDataSourcesPage.removeDataSource( name );
        manageDataSourcesPage.confirmRemove( true );
        manageDataSourcesPage.closeManageDataSources();
        break;
      case WIZARD_SQL_QUERY:
        homePage.openManageDataSources().newDataSource().setDataSourceType( DataSourceWizardPage.SOURCE_TYPE_SQL_QUERY )
            .delete( name );
        break;
      case WIZARD_DB_TABLES:
        homePage.openManageDataSources().newDataSource().setDataSourceType( DataSourceWizardPage.SOURCE_TYPE_DB_TABLES )
            .delete( name );
        break;
      default:
        Assert.fail( "Unsupported workflow for opening DB Connection Wizard: " + workflow );
    }
  }

  /* -------------- EDIT DATA SOURCE WORKFLOW ----------------- */
  public void edit( BaseConnection newConn ) {

    openEditWizard();
    newConn.connPage = connPage;
    newConn.setGeneralParameters();
    newConn.setParameters();
    newConn.testConnection();

    newConn.finishWizard( Workflow.MANAGE_DS_POPUP );

    softAssert.assertAll();
  }

  /* -------------- CREATE DATA SOURCE WORKFLOW --------------- */
  public boolean create() {
    openWizard();
    setGeneralParameters();
    setParameters();
    addOptions();
    if ( !testConnection() ) {
      LOGGER.warn( "Unable to connect to database using connection: " + name );
    }

    finishWizard();
    // incorporate verification method into the create function by default
    boolean res = verify();

    softAssert.assertAll();

    return res;
  }

  public void openWizard( Workflow workflow ) {
    HomePage homePage = new HomePage( getDriver() );
    switch ( workflow ) {
      case MANAGE_DS_POPUP:
        connPage = homePage.openManageDataSources().newConnection();
        break;
      // Open New Connection wizard from Data Source Wizard
      case WIZARD_SQL_QUERY:
        connPage =
            homePage.openManageDataSources().newDataSource().setDataSourceType(
                DataSourceWizardPage.SOURCE_TYPE_SQL_QUERY ).newConnection();
        break;
      // Open New Connection wizard from Data Source Wizard
      case WIZARD_DB_TABLES:
        connPage =
            homePage.openManageDataSources().newDataSource().setDataSourceType(
                DataSourceWizardPage.SOURCE_TYPE_DB_TABLES ).newConnection();
        break;
      case WIZARD_DB_TABLES_EDIT:
        connPage =
            homePage.openManageDataSources().newDataSource().setDataSourceType(
                DataSourceWizardPage.SOURCE_TYPE_DB_TABLES ).editConnection( name );
        break;
      case WIZARD_SQL_QUERY_EDIT:
        connPage =
            homePage.openManageDataSources().newDataSource().setDataSourceType(
                DataSourceWizardPage.SOURCE_TYPE_SQL_QUERY ).editConnection( name );
        break;
      default:
        Assert.fail( "Unsupported workflow for opening DB Connection Wizard: " + workflow );
    }
  }

  public void openWizard() {
    openWizard( Workflow.MANAGE_DS_POPUP );
  }

  public void openEditWizard() {
    HomePage homePage = new HomePage( getDriver() );
    connPage = homePage.openManageDataSources().editDataSource( this );
  }

  public void setGeneralParameters() {
    if ( name != null ) {
      connPage.setName( name );
    }
    if ( dbType != null ) {
      connPage.setDbType( dbType );
    }
    if ( accessType != null ) {
      connPage.setAccessType( accessType );
    }
    if ( dbName != null ) {
      connPage.setDbName( dbName );
    }
  }

  public abstract void setParameters();

  public void addOptions() {
    if ( options == null ) {
      return;
    }

    connPage.openTab( DatabaseConnectionPage.ConnectionTab.OPTIONS );
    connPage.addOptions( options );
  }

  public boolean verifyOptions() {

    connPage.openTab( DatabaseConnectionPage.ConnectionTab.OPTIONS );
    return connPage.verifyOptions( options );
  }

  public void finishWizard() {
    finishWizard( Workflow.MANAGE_DS_POPUP );
  }

  public void finishWizard( Workflow workflow ) {

    DataSourceWizardPage dataSourceWizardPage = null;
    switch ( workflow ) {
      case MANAGE_DS_POPUP:
        connPage.buttonOK();
        break;
      case WIZARD_SQL_QUERY:
      case WIZARD_DB_TABLES:
        connPage.buttonOK();
        dataSourceWizardPage = new DataSourceWizardPage( getDriver() );
        dataSourceWizardPage.buttonCancel();
        closeManageDataSources();
        break;
      case WIZARD_DB_TABLES_REMOVE:
      case WIZARD_SQL_QUERY_REMOVE:
        dataSourceWizardPage = new DataSourceWizardPage( getDriver() );
        dataSourceWizardPage.buttonCancel();
        closeManageDataSources();
        break;
      default:
        Assert.fail( "Unsupported workflow for closing DB Connection Wizard: " + workflow );
    }
  }

  public void cancelWizard() {
    connPage.buttonCancel();
    closeManageDataSources();
  }

  public void closeManageDataSources() {
    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    manageDataSourcesPage.closeManageDataSources();
  }

  /* -------------- TEST CONNECTION ------------------------- */
  public boolean testConnection() {
    return connPage.testConnection( dbName );
  }

  /* -------------- VERIFY CONNECTION ------------------------ */
  public boolean verify() {
    return verify( EXPLICIT_TIMEOUT / 10 );
  }

  public boolean verify( long timeout ) {
    // Verification part

    ManageDataSourcesPage manageDataSourcesPage = new ManageDataSourcesPage( getDriver() );
    if ( !manageDataSourcesPage.isOpened( timeout ) ) {
      HomePage homePage = new HomePage( getDriver() );
      manageDataSourcesPage = homePage.openManageDataSources();
    }

    boolean res = manageDataSourcesPage.isDataSourcePresent( name, timeout );
    manageDataSourcesPage.closeManageDataSources();

    // dsnPage.buttonClose();
    return res;
  }

}
