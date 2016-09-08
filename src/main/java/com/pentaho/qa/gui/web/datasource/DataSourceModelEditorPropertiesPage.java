package com.pentaho.qa.gui.web.datasource;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.FindBy;

import com.pentaho.qa.gui.web.puc.BasePage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class DataSourceModelEditorPropertiesPage extends BasePage {

  @FindBy( id = "dimension_name" )
  protected ExtendedWebElement textDimensionName;

  @FindBy( id = "hierarchy_name" )
  protected ExtendedWebElement textHierarchyName;

  @FindBy( id = "level_name" )
  protected ExtendedWebElement textLevelName;

  @FindBy( id = "memberprops_name" )
  protected ExtendedWebElement textPropertyName;

  @FindBy( id = "memberprops_desc" )
  protected ExtendedWebElement textPropertyDescription;

  public DataSourceModelEditorPropertiesPage( WebDriver driver ) {
    super( driver );
  }

}
