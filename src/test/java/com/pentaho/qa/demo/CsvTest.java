package com.pentaho.qa.demo;

import org.testng.annotations.Test;

import com.qaprosoft.carina.core.foundation.APITest;
import com.qaprosoft.carina.core.foundation.dataprovider.annotations.CsvDataSourceParameters;

public class CsvTest extends APITest {

  @Test( dataProvider = "DataProvider" )
  @CsvDataSourceParameters( path = "CSV_data/demo_provider.csv", dsArgs = "Grid, Node, Browser, Version, Count",
      dsUid = "Grid, Node, Browser, Version" )
  public void test( String grid, String node, String browser, String version, String count ) {
    LOGGER.info( grid + "; " + node + "; " + browser + "; " + version + "; " + count );
  }

}
