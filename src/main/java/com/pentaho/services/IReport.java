package com.pentaho.services;

import com.pentaho.qa.gui.web.puc.ReportPage;
import com.pentaho.services.Report.Layout;
import com.pentaho.services.Report.Sort;
import com.pentaho.services.Report.Workflow;

public interface IReport {

  //public void close( boolean save );

  public ReportPage create();

  public void edit(IReport report);
  
  public void sort( Layout layout, String field, Sort sort, Workflow workflow );
  public void sorterUp( Layout layout, String field );
  public void sorterDown( Layout layout, String field );

}
