package com.pentaho.services.utils;

  public enum ExportType{
    PDF("pdf"), CSV("csv"), EXCEL("xlsx"), EXCEL_97("xls");
    
    private String extension;

    private ExportType(String extension) {
      this.extension = extension;
    }

    public String getExtension() {
      return this.extension;
    }
  }

