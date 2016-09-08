package com.pentaho.services.pir;

import java.util.Map;

import com.pentaho.qa.gui.web.pir.PIRFilterPage;
import com.pentaho.qa.gui.web.pir.PIRFilterPanelPage;
import com.pentaho.qa.gui.web.pir.PIRReportPage;
import com.pentaho.services.CustomAssert;
import com.pentaho.services.Filter;
import com.pentaho.services.Report.Workflow;

public class PIRFilter extends Filter {

  private PIRFilterPage filterPage;
 
  public PIRFilter( String fieldName ) {
    super( fieldName );
  }
  
  public PIRFilter( Map<String, String> args ) {
    super( args );
  }

  public PIRFilterPage getFilterPage() {
    return filterPage;
  }


  /* -------------- CREATE FILTER ------------------------------ */

  @Override
  public void create( Workflow workflow ) {
    // Get report page
    PIRReportPage reportPage = new PIRReportPage( getDriver() );

    filterPage = reportPage.addFilter( field, workflow );
    filterPage.verify( dataType, field );
    setParameters();
  }

  public PIRFilterPage edit( PIRFilter newFilter ) {
    return edit( newFilter, false );
  }

  public PIRFilterPage edit( PIRFilter newFilter, boolean overwrite ) {
    // Get Filter panel page
    PIRFilterPanelPage filterPanelPage = new PIRFilterPanelPage( getDriver() );

    // Click Edit on current filter and return filterPage object which is going to be used in setParameters()
    newFilter.filterPage = filterPanelPage.editFilter( this );

    // Verification Filter dialog is populated with old values
    verifyParameters();

    // Typing new values
    newFilter.setParameters();

    // Verify a dialog is shown saying that the associated prompt will be deleted.
    if ( !filterPage.isWarningDialogPresent( this ) ) {
      CustomAssert.fail( "31463", "Warning dialog is not present!" );
    } else {
      filterPage.confirmWarningDialog( overwrite );
    }

    // Update current filter
    copy( newFilter );

    return newFilter.filterPage;
  }

  @Override
  protected boolean verifyParameters() {
    if ( filterPage.isSpecifyCondition() != specifyCondition ) {
      CustomAssert.fail( "62385", "Filter type is not correspond to previously set!" );
      return false;
    }

    if ( !filterPage.getCondition().equals( condition ) ) {
      CustomAssert.fail( "62385", "Condition value is not correspond to previously set!" );
      return false;
    }
    if ( !filterPage.getValue().equals( value ) ) {
      CustomAssert.fail( "62385", "Value is not correspond to previously set!" );
      return false;
    }
    if ( !filterPage.getParamName().equals( paramName ) ) {
      CustomAssert.fail( "62385", "Parameter value is not correspond to previously set!" );
      return false;
    }

    // TODO: checks for List type should be added
    return true;
  }

  @Override
  public void delete() {
    PIRFilterPanelPage filterPanelPage = new PIRFilterPanelPage( getDriver() );
    filterPanelPage.deleteFilter( this );
  }

  @Override
  public boolean verify() {
    PIRFilterPanelPage filterPanelPage = new PIRFilterPanelPage( getDriver() );
    filterPage = filterPanelPage.editFilter( this );

    // TODO: add more checks to this.
    // if page and object are different in ANY field, connection was not overwritten.
    boolean res = verifyParameters();

    filterPage.clickCancel();

    return res;
  }

  public boolean verifyFilterPromptNameUpdate( Prompt prompt ) {
    boolean res = false;
    PIRFilterPanelPage filterPanelPage = new PIRFilterPanelPage( getDriver() );
    filterPage = filterPanelPage.editFilter( this );

    if ( prompt.getName().equals( filterPage.getParamName() ) ) {
      res = true;
    }

    filterPage.clickCancel();

    return res;
  }
  
  @Override
  protected void setParameters() {
    filterPage.specifyCondition( specifyCondition );
    filterPage.setCondition( condition );
    filterPage.setValue( value );
    filterPage.setParamName( paramName );
    filterPage.makeClickable();
    filterPage.clickOK();
  }

}
