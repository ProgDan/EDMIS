/*
 * Copyright (C) The Apache Software Foundation. All rights reserved.
 *
 * This software is published under the terms of the Apache Software License
 * version 1.1, a copy of which has been included  with this distribution in
 * the LICENSE.txt file.
 */

// Contributors: Christopher Williams
//               Mathias Bogaert

package com.progdan.logengine;

import com.progdan.logengine.helpers.DateLayout;
import com.progdan.logengine.spi.LoggingEvent;

/**
 TTCC layout format consists of time, thread, category and nested
 diagnostic context information, hence the name.

 <p>Each of the four fields can be individually enabled or
 disabled. The time format depends on the <code>DateFormat</code>
 used.

 <p>Here is an example TTCCLayout output with the
 {@link com.progdan.logengine.helpers.RelativeTimeDateFormat}.

 <pre>
176 [main] INFO  com.progdan.logengine.examples.Sort - Populating an array of 2 elements in reverse order.
225 [main] INFO  com.progdan.logengine.examples.SortAlgo - Entered the sort method.
262 [main] DEBUG com.progdan.logengine.examples.SortAlgo.OUTER i=1 - Outer loop.
276 [main] DEBUG com.progdan.logengine.examples.SortAlgo.SWAP i=1 j=0 - Swapping intArray[0] = 1 and intArray[1] = 0
290 [main] DEBUG com.progdan.logengine.examples.SortAlgo.OUTER i=0 - Outer loop.
304 [main] INFO  com.progdan.logengine.examples.SortAlgo.DUMP - Dump of interger array:
317 [main] INFO  com.progdan.logengine.examples.SortAlgo.DUMP - Element [0] = 0
331 [main] INFO  com.progdan.logengine.examples.SortAlgo.DUMP - Element [1] = 1
343 [main] INFO  com.progdan.logengine.examples.Sort - The next log statement should be an error message.
346 [main] ERROR com.progdan.logengine.examples.SortAlgo.DUMP - Tried to dump an uninitialized array.
        at com.progdan.logengine.examples.SortAlgo.dump(SortAlgo.java:58)
        at com.progdan.logengine.examples.Sort.main(Sort.java:64)
467 [main] INFO  com.progdan.logengine.examples.Sort - Exiting main method.
</pre>

  <p>The first field is the number of milliseconds elapsed since the
  start of the program. The second field is the thread outputting the
  log statement. The third field is the level, the fourth field is
  the category to which the statement belongs.

  <p>The fifth field (just before the '-') is the nested diagnostic
  context.  Note the nested diagnostic context may be empty as in the
  first two statements. The text after the '-' is the message of the
  statement.

  <p><b>WARNING</b> Do not use the same TTCCLayout instance from
  within different appenders. The TTCCLayout is not thread safe when
  used in his way. However, it is perfectly safe to use a TTCCLayout
  instance from just one appender.

  <p>{@link PatternLayout} offers a much more flexible alternative.

  @author Ceki G&uuml;lc&uuml;
  @author <A HREF="mailto:heinz.richter@ecmwf.int">Heinz Richter</a>

*/
public class TTCCLayout extends DateLayout {

  // Internal representation of options
  private boolean threadPrinting    = true;
  private boolean categoryPrefixing = true;
  private boolean contextPrinting   = true;


  protected final StringBuffer buf = new StringBuffer(256);


  /**
     Instantiate a TTCCLayout object with {@link
     com.progdan.logengine.helpers.RelativeTimeDateFormat} as the date
     formatter in the local time zone.

     @since 0.7.5 */
  public TTCCLayout() {
    this.setDateFormat(RELATIVE_TIME_DATE_FORMAT, null);
  }


  /**
     Instantiate a TTCCLayout object using the local time zone. The
     DateFormat used will depend on the <code>dateFormatType</code>.

     <p>This constructor just calls the {@link
     DateLayout#setDateFormat} method.

     */
  public TTCCLayout(String dateFormatType) {
    this.setDateFormat(dateFormatType);
  }


  /**
     The <b>ThreadPrinting</b> option specifies whether the name of the
     current thread is part of log output or not. This is true by default.
   */
  public
  void setThreadPrinting(boolean threadPrinting) {
    this.threadPrinting = threadPrinting;
  }

  /**
     Returns value of the <b>ThreadPrinting</b> option.
   */
  public
  boolean getThreadPrinting() {
    return threadPrinting;
  }

  /**
     The <b>CategoryPrefixing</b> option specifies whether {@link Category}
     name is part of log output or not. This is true by default.
   */
  public
  void setCategoryPrefixing(boolean categoryPrefixing) {
    this.categoryPrefixing = categoryPrefixing;
  }

  /**
     Returns value of the <b>CategoryPrefixing</b> option.
   */
  public
  boolean getCategoryPrefixing() {
    return categoryPrefixing;
  }

  /**
     The <b>ContextPrinting</b> option specifies log output will include
     the nested context information belonging to the current thread.
     This is true by default.
   */
  public
  void setContextPrinting(boolean contextPrinting) {
    this.contextPrinting = contextPrinting;
  }

  /**
     Returns value of the <b>ContextPrinting</b> option.
   */
  public
  boolean getContextPrinting() {
    return contextPrinting;
  }

  /**
   In addition to the level of the statement and message, the
   returned byte array includes time, thread, category and {@link NDC}
   information.

   <p>Time, thread, category and diagnostic context are printed
   depending on options.

    @param event The event to format

  */
  public
  String format(LoggingEvent event) {

    // Reset buf
    buf.setLength(0);

    dateFormat(buf, event);

    if(this.threadPrinting) {
      buf.append('[');
      buf.append(event.getThreadName());
      buf.append("] ");
    }
    buf.append(event.getLevel().toString());
    buf.append(' ');

    if(this.categoryPrefixing) {
      buf.append(event.getLoggerName());
      buf.append(' ');
    }

    if(this.contextPrinting) {
       String ndc = event.getNDC();

      if(ndc != null) {
	buf.append(ndc);
	buf.append(' ');
      }
    }
    buf.append("- ");
    buf.append(event.getRenderedMessage());
    buf.append(LINE_SEP);
    return buf.toString();
  }

 /**
     The TTCCLayout does not handle the throwable contained within
     {@link LoggingEvent LoggingEvents}. Thus, it returns
     <code>true</code>.

     @since version 0.8.4 */
  public
  boolean ignoresThrowable() {
    return true;
  }
}
