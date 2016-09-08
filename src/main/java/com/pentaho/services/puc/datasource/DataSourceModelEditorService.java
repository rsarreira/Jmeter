package com.pentaho.services.puc.datasource;

import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;

public class DataSourceModelEditorService {
  private final DataSourceModelEditorPage page;

  public DataSourceModelEditorService( DataSourceModelEditorPage page ) {
    this.page = page;
  }

  public void autoPopulate() {
    page.autoPopulateModel();
  }

  public void clearAnalysisModel() {
    page.clearAnalysisModel();
  }
  
  public void clearReportingModel() {
    page.clearReportingModel();
  }

  public Item addItem( String path, String name ) {
    return new LevelItem();
  }

  public void getItem( String path ) {
    page.selectItem( "/Dimensions/Name/Name/Name" );
    page.selectItem( "/Measures/Stops" );
    page.selectItem(  "/Categories/Test/City" );
  }
  
  public void setDisplayName(String displayName) {
    
  }

  public void deleteItem( String path ) {
  }

  public void buttonOK() {
    page.buttonOK();
  }

  public void buttonCancel() {
    page.buttonCancel();
  }

  interface Item {
  }

  public class LevelItem implements Item {
    public String getLevelName() {
      return null;
    }

    public void setLevelName( String name ) {
    }

    public String getGeoType() {
      return null;
    }

    public void setGeographyType() {
    }

    public void setOrdinalColumn() {
    }
  }

}
