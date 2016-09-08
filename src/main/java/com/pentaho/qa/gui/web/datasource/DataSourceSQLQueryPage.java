package com.pentaho.qa.gui.web.datasource;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceSQLQueryPage extends DataSourceWizardPage {

  @FindBy( id = "query" )
  protected ExtendedWebElement txtAreaQuery;

  @FindBy( css = "#preview > span" )
  protected ExtendedWebElement btnPreview;

  @FindBy( css = "#preview > span" )
  protected ExtendedWebElement dlgPreviewResults;

  public DataSourceSQLQueryPage( WebDriver driver ) {
    super( driver );
    if (!isOpened()) {
      Assert.fail( "'SQL Query' data source type is not recognized!" );
    }
  }

  protected boolean isOpened() {
    return getSelectedDataSourceType().equals( SOURCE_TYPE_SQL_QUERY );
  }

  public boolean setQuery( String query ) {
    if ( query == null ) {
      return true;
    }

    type( txtAreaQuery, query );

    if ( !getQuery().equals( query ) ) {
      return false;
    }

    return true;
  }

  private String getQuery() {
    return txtAreaQuery.getAttribute( "value" );
  }

  public boolean openDataPreview() {
    click( btnPreview );
    if ( !isElementPresent( dlgPreviewResults ) ) {
      return false;
    }
    return true;
  }

}
