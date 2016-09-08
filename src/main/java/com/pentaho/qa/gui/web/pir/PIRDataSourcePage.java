package com.pentaho.qa.gui.web.pir;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.DataSourceWizardPage;
import com.pentaho.qa.gui.web.puc.FramePage;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class PIRDataSourcePage extends FramePage {

  @FindBy( xpath = "//table[starts-with(@id, 'pentaho_common_Menu')]//td[text()='%s']" )
  protected ExtendedWebElement dateSourceItem;

  @FindBy( xpath = "//tr[@class = 'dijitReset dijitMenuItem pentaho-listitem']" )
  protected List<ExtendedWebElement> dataSourceList;

  // Select Data Source

  @FindBy( xpath = "//div[contains(@class,'pentaho-dialog')]/div/span[@id='dijit_Dialog_0_title']" )
  protected ExtendedWebElement dlgSelectDialog;

  @FindBy( css = ".pentaho-listitemSelected > .pentaho-menuitem-label" )
  protected ExtendedWebElement selectedDataSource;

  @FindBy( css = ".pentaho-listitemSelected > .pentaho-menuitem-label" )
  protected List<ExtendedWebElement> selectedDataSources;

  @FindBy( id = "button0" )
  protected ExtendedWebElement btnOK;

  @FindBy( id = "button1" )
  protected ExtendedWebElement btnCancel;

  @FindBy( id = "pentaho_common_SmallImageButton_0" )
  protected ExtendedWebElement editButton;

  @FindBy( id = "pentaho_common_SmallImageButton_1" )
  protected ExtendedWebElement addButton;

  @FindBy( id = "pentaho_common_SmallImageButton_2" )
  protected ExtendedWebElement deleteButton;

  // warning dialog
  @FindBy( xpath = "//div[@class='dijitDialogTitleBar Caption']//span[text()='{L10N:Warning}']" )
  protected ExtendedWebElement dlgRemoveDataSource;

  @FindBy(
      xpath = "//div[@class='dijitDialogTitleBar Caption']//span[text()='{L10N:Warning}']/../..//button[@id ='button1']" )
  protected ExtendedWebElement cancelRemoveDlg;

  @FindBy(
      xpath = "//div[@class='dijitDialogTitleBar Caption']//span[text()='{L10N:Warning}']/../..//button[@id ='button0']" )
  protected ExtendedWebElement okRemoveDlg;

  private final String disabledAttribute = "pentaho-imagebutton-disabled";

  public PIRDataSourcePage( WebDriver driver ) {
    super( driver );
    if ( !isOpened() ) {
      LOGGER.error( "'Select Data Source' is not opened!" );
    }
  }

  public boolean isOpened() {
    activateFrame();
    boolean res = dlgSelectDialog.isElementPresent( EXPLICIT_TIMEOUT );
    switchToDefault();
    return res;
  }

  public void clickOK() {
    click( btnOK, true );
  }

  public void clickCancel() {
    click( btnCancel, true );
  }

  public void doubleClick( String dataSourceName ) {
    activateFrame();
    format( dateSourceItem, dataSourceName ).doubleClick();
  }

  public DataSourceWizardPage clickAdd() {
    clickImageButton( addButton );
    switchToDefault();
    pause( 1 );
    return new DataSourceWizardPage( driver );
  }

  public DataSourceModelEditorPage clickEdit() {
    clickImageButton( editButton );
    switchToDefault();
    pause( 1 );
    return new DataSourceModelEditorPage( driver );
  }

  public void clickDelete() {
    Actions actions = new Actions( driver );
    actions.moveToElement( deleteButton.getElement() ).build().perform();
    pause( 1 );
    driver.findElement( By.className( "pentaho-imagebutton-hover" ) ).click();
  }

  public void clickImageButton( ExtendedWebElement extendedWebElement ) {
    Actions actions = new Actions( driver );
    actions.moveToElement( extendedWebElement.getElement() ).build().perform();
    pause( EXPLICIT_TIMEOUT / 3 );
    driver.findElement( By.className( "pentaho-imagebutton-hover" ) ).click();
    pause( EXPLICIT_TIMEOUT / 3 );
    final JavascriptExecutor executor = (JavascriptExecutor) driver;
    String clickImageScript =
        "return document.getElementById('" + extendedWebElement.getElement().getAttribute( "id" ) + "').click()";
    executor.executeScript( clickImageScript );
  }

  public List<ExtendedWebElement> getDataSourceList() {
    return dataSourceList;
  }

  public boolean isDataSourcePresent( String dataSourceName ) {
    return isElementPresent( format( dateSourceItem, dataSourceName ), EXPLICIT_TIMEOUT / 5 );
  }

  public boolean isDataSourceNotPresent( String dataSourceName ) {
    return isElementNotPresent( format( dateSourceItem, dataSourceName ) );
  }

  private String getSelectedDataSourceName() {
    return selectedDataSource.getText().trim();
  }

  public PIRReportPage selectDataSource( String dataSourceName ) {
    activateFrame();

    if ( !isDataSourcePresent( dataSourceName ) ) {
      LOGGER.error( "Required Data Source does not exists!" );
    }

    click( format( dateSourceItem, dataSourceName ) );

    if ( !getSelectedDataSourceName().equals( dataSourceName ) ) {
      LOGGER.error( "Required Data Source value was not selected!" );
    }

    click( btnOK );

    return new PIRReportPage( driver );
  }

  public void selectDataSourceWithoutOK( String dataSourceName ) {
    activateFrame();

    if ( !isDataSourcePresent( dataSourceName ) ) {
      LOGGER.error( "Required Data Source does not exists!" );
    }
    click( format( dateSourceItem, dataSourceName ) );
  }

  public boolean isAddButtonDisabled() {
    if ( addButton.getElement().getAttribute( "class" ).contains( disabledAttribute ) ) {
      return true;
    }
    return false;
  }

  public boolean isEditButtonDisabled() {
    if ( editButton.getElement().getAttribute( "class" ).contains( disabledAttribute ) ) {
      return true;
    }
    return false;
  }

  public boolean isDeleteButtonDisabled() {
    if ( deleteButton.getElement().getAttribute( "class" ).contains( disabledAttribute ) ) {
      return true;
    }
    return false;
  }

  public SoftAssert checkButtonsVisibility( boolean enabled ) {
    activateFrame();
    SoftAssert softAssert = new SoftAssert();

    // check edit and delete buttons
    if ( enabled ) {
      if ( isEditButtonDisabled() || isDeleteButtonDisabled() ) {
        softAssert.fail( "Buttons Edit and Delete - disabled !" );
      }
    } else {
      if ( !( isEditButtonDisabled() && isDeleteButtonDisabled() ) ) {
        softAssert.fail( "Buttons Edit and Delete - enabled !" );
      }
    }

    // check add button
    if ( isAddButtonDisabled() ) {
      softAssert.fail( "Add button - disabled !" );
    }
    return softAssert;
  }

  public SoftAssert checkMultipleSelection() {
    activateFrame();
    SoftAssert softAssert = new SoftAssert();

    if ( selectedDataSources.size() > 1 ) {
      softAssert.fail( "Can not be selected more then one DataSource !" );
    }
    return softAssert;
  }

  public boolean verifyRemoveDataSourceDialog() {
    return isElementPresent( dlgRemoveDataSource );
  }

  public void clickOKRemoveDlg() {
    click( okRemoveDlg, true );
  }

  public void clickCancelRemoveDlg() {
    click( cancelRemoveDlg, true );
  }

  public void activateFrame() {
    makeClickable();
    switchToFrame();
  }
}
