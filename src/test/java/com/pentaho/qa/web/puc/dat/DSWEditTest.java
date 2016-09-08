package com.pentaho.qa.web.puc.dat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pentaho.qa.gui.web.datasource.DataSourceModelEditorPage;
import com.pentaho.qa.gui.web.datasource.ManageDataSourcesPage;
import com.pentaho.qa.gui.web.puc.HomePage;
import com.pentaho.qa.web.WebBaseTest;
import com.pentaho.services.puc.datasource.BaseDataSource;
import com.pentaho.services.puc.datasource.DataSourceModelEditorService;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.XlsDataSourceParameters;
import com.qaprosoft.carina.core.foundation.report.spira.SpiraTestCase;

@SpiraTestCase( projectId = 1111, testCaseId = 11111 )
public class DSWEditTest extends WebBaseTest {

  @BeforeClass( )
  public void testLogin() {
    HomePage homePage = webUser.login();

    Assert
        .assertTrue( homePage.isLogged( webUser.getName() ), "Incorrect user is logged: '" + webUser.getName() + "'!" );
  }

  @Test
  @XlsDataSourceParameters( sheet = "DBTable_DataSources", dsUid = "name", executeColumn = "TUID",
      executeValue = "DSDBT01_EDIT" )
  public void openEdit() {


    HomePage homePage = new HomePage( getDriver() );
    ManageDataSourcesPage manage = homePage.openManageDataSources();
    DataSourceModelEditorPage page = manage.editDataSource( new BaseDataSource( "test", false ) {
      @Override
      public void setParameters() {
        // TODO Auto-generated method stub

      }
    });
    DataSourceModelEditorService service=  new DataSourceModelEditorService(page);
    service.getItem( "ID" );

    System.out.println();

  }

}
