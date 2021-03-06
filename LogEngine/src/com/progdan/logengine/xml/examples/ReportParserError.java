
package com.progdan.logengine.xml.examples;

import com.progdan.logengine.helpers.LogLog;

/**

   This class is needed for validating a logengine.dtd derived XML file.

   @author Joe Kesselman

   @since 0.8.3

 */
public class ReportParserError implements org.xml.sax.ErrorHandler {

  void report(String msg, org.xml.sax.SAXParseException e) {
    LogLog.error(msg+e.getMessage()+ "\n\tat line="+ e.getLineNumber()+
		 " col="+e.getColumnNumber()+ " of "+
		 "SystemId=\""+e.getSystemId()+
		 "\" PublicID = \""+e.getPublicId()+'\"');
  }

  public void warning(org.xml.sax.SAXParseException e) {
    report("WARNING: ", e);
  }

  public void error(org.xml.sax.SAXParseException e) {
    report("ERROR: ", e);
  }

  public void fatalError(org.xml.sax.SAXParseException e) {
    report("FATAL: ", e);
  }
}
