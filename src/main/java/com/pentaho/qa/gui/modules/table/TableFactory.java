package com.pentaho.qa.gui.modules.table;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.pentaho.qa.gui.modules.table.Table.TableCell;
import com.pentaho.qa.gui.modules.table.Table.TableRow;
import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class TableFactory {
  private Table table;
  private Map<String, String> customHeaderSelectors;
  private Map<String, String> customRowSelectors;

  public TableFactory() {
    this.table = new Table();
  }

  public TableFactory( Map<String, String> customHeaderSelectors, Map<String, String> customRowSelectors ) {
    this();
    this.customHeaderSelectors = customHeaderSelectors;
    this.customRowSelectors = customRowSelectors;
  }

  public Table create( ExtendedWebElement tableRoot, String... headerNames ) {
    populateHeaders( tableRoot, headerNames );
    populateDataRows( tableRoot, headerNames );
    return table;
  }

  private void populateHeaders( ExtendedWebElement tableRoot, String... headerNames ) {
    List<WebElement> headers = tableRoot.getElement().findElements( By.xpath( "./thead/tr/th" ) );
    int index = 0;

    // List<String> listHeaderNames = Arrays.asList(headerNames);
    for ( WebElement header : headers ) {
      // if (listHeaderNames.contains(header.getText())) {
      String customSelector = null;
      if ( customHeaderSelectors != null && customHeaderSelectors.containsKey( headerNames[index] ) ) {
        customSelector = customHeaderSelectors.get( headerNames[index] );
      }
      table.getHeaders().put(
          headerNames[index],
          new ExtendedWebElement( ( customSelector != null ? header.findElement( By.xpath( customSelector ) ) : header ),
          headerNames[index] ) );
      index++;
      // }
    }
  }

  private void populateDataRows( ExtendedWebElement tableRoot, String... headerNames ) {
    List<WebElement> rows = tableRoot.getElement().findElements( By.xpath( "./tbody/tr" ) );
    int rowN = 0;

    // List<String> listHeaderNames = Arrays.asList(headerNames);
    for ( WebElement row : rows ) {
      List<WebElement> columns = row.findElements( By.xpath( "./td" ) );
      TableRow tableRow = new TableRow();
      int colN = 0;
      for ( WebElement column : columns ) {
        String customSelector = null;
        if ( customRowSelectors != null && customRowSelectors.containsKey( headerNames[colN] ) ) {
          customSelector = customRowSelectors.get( headerNames[colN] );
        }
        String name = headerNames[colN] + "(row: " + rowN + ")";
        TableCell tableCell =
            new TableCell( headerNames[colN], column.getText(), new ExtendedWebElement( ( customSelector != null ? column
            .findElement( By.xpath( customSelector ) ) : column ), name ) );
        tableRow.getCells().add( tableCell );
        colN++;
      }
      table.getRows().add( tableRow );
      rowN++;
    }
  }
}
