package com.pentaho.qa.gui.web.analyzer;

import org.openqa.selenium.WebDriver;

import com.pentaho.qa.gui.web.puc.ReportPage;

public abstract class AnalyzerBasePage extends ReportPage {

  public AnalyzerBasePage( WebDriver driver ) {
    this( driver, NEW_ANALYSIS_REPORT_NAME );
  }

  public AnalyzerBasePage( WebDriver driver, String name ) {
    super( driver, name );
  }

  public String extractCubeName( String dataSourceName ) {
    if ( !dataSourceName.contains( ":" ) ) {
      return dataSourceName;
    }

    String[] parts = dataSourceName.split( ":" );
    return parts[1].trim();
  }

  public String extractSchemaName( String dataSourceName ) {
    if ( !dataSourceName.contains( ":" ) ) {
      return dataSourceName;
    }

    String[] parts = dataSourceName.split( ":" );
    return parts[0].trim();
  }

}
