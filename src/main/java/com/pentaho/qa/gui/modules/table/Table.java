package com.pentaho.qa.gui.modules.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qaprosoft.carina.core.foundation.webdriver.decorator.ExtendedWebElement;

public class Table {
  private Map<String, ExtendedWebElement> headers;
  private List<TableRow> rows;

  public Table() {
    this.headers = new HashMap<String, ExtendedWebElement>();
    this.rows = new ArrayList<TableRow>();
  }

  public List<TableRow> getRows() {
    return rows;
  }

  public void setRows( List<TableRow> rows ) {
    this.rows = rows;
  }

  public Map<String, ExtendedWebElement> getHeaders() {
    return headers;
  }

  public void setHeaders( Map<String, ExtendedWebElement> headers ) {
    this.headers = headers;
  }

  public TableRow findRowByCellValue( String cellHeader, String value ) {
    if ( headers.containsKey( cellHeader ) ) {
      for ( TableRow row : rows ) {
        for ( TableCell cell : row.getCells() ) {
          if ( cellHeader.equals( cell.getName() ) && value.equals( cell.getValue() ) ) {
            return row;
          }
        }
      }
    }
    return null;
  }

  public static class TableCell {
    private String name;
    private String value;
    private ExtendedWebElement element;

    public TableCell( String name, String value, ExtendedWebElement element ) {
      this.name = name;
      this.value = value;
      this.element = element;
    }

    public String getName() {
      return name;
    }

    public void setName( String name ) {
      this.name = name;
    }

    public String getValue() {
      return value;
    }

    public void setValue( String value ) {
      this.value = value;
    }

    public ExtendedWebElement getElement() {
      return element;
    }

    public void setElement( ExtendedWebElement element ) {
      this.element = element;
    }
  }

  public static class TableRow {
    private List<TableCell> cells;

    public TableRow() {
      this.cells = new ArrayList<TableCell>();
    }

    public List<TableCell> getCells() {
      return cells;
    }

    public void setCells( List<TableCell> cells ) {
      this.cells = cells;
    }

    public TableCell getCell( String name ) {
      for ( TableCell cell : cells ) {
        if ( name.equals( cell.getName() ) ) {
          return cell;
        }
      }
      return null;
    }
  }
}
