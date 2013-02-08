/*********************************************************************
*
*      Copyright (C) 2002 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package com.progdan.xls2txt.biff.formula;

import java.util.Stack;

import com.progdan.xls2txt.common.Assert;
import com.progdan.xls2txt.common.Logger;

import com.progdan.xls2txt.WorkbookSettings;
import com.progdan.xls2txt.Sheet;
import com.progdan.xls2txt.biff.IntegerHelper;
/**
 * A special attribute control token - typically either a SUM function
 * or an IF function
 */

class Attribute extends Operator implements ParsedThing
{
  /**
   * The logger
   */
  private static Logger logger = Logger.getLogger(Attribute.class);

  /**
   * The options used by the attribute
   */
  private int options;

  /**
   * The word contained in this attribute
   */
  private int word;

  /**
   * The workbook settings
   */
  private WorkbookSettings settings;

  private final static int sumMask  = 0x10;
  private final static int ifMask   = 0x02;
  private final static int gotoMask = 0x08;

  /**
   * If this attribute is an IF functions, sets the associated if conditions
   */
  private VariableArgFunction ifConditions;

  /**
   * Constructor
   */
  public Attribute(WorkbookSettings ws)
  {
    settings = ws;
  }

  /**
   * Constructor for use when this is called when parsing a string
   *
   * @param sf the built in function
   * @param ws the workbook settings
   */
  public Attribute(StringFunction sf, WorkbookSettings ws)
  {
    settings = ws;

    if (sf.getFunction(settings) == Function.SUM)
    {
      options |= sumMask;
    }
    else if (sf.getFunction(settings) == Function.IF)
    {
      options |= ifMask;
    }
  }

  /**
   * Sets the if conditions for this attribute, if it represents an IF function
   *
   * @param vaf a <code>VariableArgFunction</code> value
   */
  void setIfConditions(VariableArgFunction vaf)
  {
    ifConditions = vaf;

    // Sometimes there is not Attribute token, so we need to create
    // an attribute out of thin air.  In that case, make sure the if mask
    options |= ifMask;
  }

  /**
   * Reads the ptg data from the array starting at the specified position
   *
   * @param data the RPN array
   * @param pos the current position in the array, excluding the ptg identifier
   * @return the number of bytes read
   */
  public int read(byte[] data, int pos)
  {
    options = data[pos];
    word = IntegerHelper.getInt(data[pos+1], data[pos+2]);
    return 3;
  }

  /**
   * Queries whether this attribute is a function
   *
   * @return TRUE if this is a function, FALSE otherwise
   */
  public boolean isFunction()
  {
    return (options & (sumMask | ifMask) ) != 0;
  }

  /**
   * Queries whether this attribute is a sum
   *
   * @return TRUE if this is SUM, FALSE otherwise
   */
  public boolean isSum()
  {
    return (options & sumMask) != 0;
  }

  /**
   * Queries whether this attribute is an IF
   *
   * @return TRUE if this is an IF, FALSE otherwise
   */
  public boolean isIf()
  {
    return (options & ifMask) != 0;
  }

  /**
   * Queries whether this attribute is a goto
   *
   * @return TRUE if this is a goto, FALSE otherwise
   */
  public boolean isGoto()
  {
    return (options & gotoMask) != 0;
  }

  /**
   * Gets the operands for this operator from the stack
   */
  public void getOperands(Stack s)
  {
    if ((options & sumMask) != 0)
    {
      ParseItem o1 = (ParseItem) s.pop();

      add(o1);
    }
    else if ((options & ifMask) != 0)
    {
      ParseItem o1 = (ParseItem) s.pop();
      add(o1);
    }
  }

  public void getString(StringBuffer buf)
  {
    if ((options & sumMask) != 0)
    {
      ParseItem[] operands = getOperands();
      buf.append(Function.SUM.getName(settings));
      buf.append('(');
      operands[0].getString(buf);
      buf.append(')');
    }
    else if ((options & ifMask) != 0)
    {
      buf.append(Function.IF.getName(settings));
      buf.append('(');

      ParseItem[] operands = ifConditions.getOperands();

      // Operands are in the correct order for IFs
      for (int i = 0 ; i < operands.length-1 ; i++)
      {
        operands[i].getString(buf);
        buf.append(',');
      }
      operands[operands.length-1].getString(buf);
      buf.append(')');
    }
  }

  /**
   * Gets the token representation of this item in RPN.  The Attribute
   * token is a special case, which overrides anything useful we could do
   * in the base class
   *
   * @return the bytes applicable to this formula
   */
  byte[] getBytes()
  {
    byte[] data = new byte[0];
    if (isSum())
    {
      // Get the data for the operands
      ParseItem[] operands = getOperands();

      // Get the operands in reverse order to get the RPN
      for (int i = operands.length - 1 ; i >= 0 ; i--)
      {
        byte[] opdata = operands[i].getBytes();

        // Grow the array
        byte[] newdata = new byte[data.length + opdata.length];
        System.arraycopy(data, 0, newdata, 0, data.length);
        System.arraycopy(opdata, 0, newdata, data.length, opdata.length);
        data = newdata;
      }

      // Add on the operator byte
      byte[] newdata = new byte[data.length + 4];
      System.arraycopy(data, 0, newdata, 0, data.length);
      newdata[data.length] = Token.ATTRIBUTE.getCode();
      newdata[data.length+1] = sumMask;
      data = newdata;
    }
    else if (isIf())
    {
      return getIf();
    }

    return data;
  }

  /**
   * Gets the associated if conditions with this attribute
   *
   * @return the associated if conditions
   */
  private byte[] getIf()
  {
    ParseItem[] operands = ifConditions.getOperands();

    // The position of the offset to the false portion of the expression
    int falseOffsetPos = 0;
    int gotoEndPos = 0;
    int numArgs = operands.length;

    // First, write out the conditions
    byte[] data = operands[0].getBytes();

    // Grow the array by three and write out the optimized if attribute
    int pos = data.length;
    byte[] newdata = new byte[data.length+4];
    System.arraycopy(data, 0, newdata, 0, data.length);
    data = newdata;
    data[pos] = Token.ATTRIBUTE.getCode();
    data[pos+1] = 0x2;
    falseOffsetPos = pos+2;

    // Get the true portion of the expression and add it to the array
    byte[] truedata = operands[1].getBytes();
    newdata = new byte[data.length + truedata.length];
    System.arraycopy(data, 0, newdata, 0, data.length);
    System.arraycopy(truedata, 0, newdata, data.length, truedata.length);
    data = newdata;

    // Grow the array by three and write out the goto end attribute
    pos = data.length;
    newdata = new byte[data.length+4];
    System.arraycopy(data, 0, newdata, 0, data.length);
    data = newdata;
    data[pos] = Token.ATTRIBUTE.getCode();
    data[pos+1] = 0x8;
    gotoEndPos = pos+2;

    // If the false condition exists, then add that to the array
    if (numArgs > 2)
    {
      // Set the offset to the false expression to be the current position
      IntegerHelper.getTwoBytes(data.length - falseOffsetPos - 2,
                                data, falseOffsetPos);

      // Copy in the false expression
      byte[] falsedata = operands[numArgs-1].getBytes();
      newdata = new byte[data.length + falsedata.length];
      System.arraycopy(data, 0, newdata, 0, data.length);
      System.arraycopy(falsedata, 0, newdata, data.length, falsedata.length);
      data = newdata;

      // Write the goto to skip over the varargs token
      pos = data.length;
      newdata = new byte[data.length+4];
      System.arraycopy(data, 0, newdata, 0, data.length);
      data = newdata;
      data[pos] = Token.ATTRIBUTE.getCode();
      data[pos+1] = 0x8;
      data[pos+2] = 0x3;
    }

    // Grow the array and write out the varargs function
    pos = data.length;
    newdata = new byte[data.length+4];
    System.arraycopy(data, 0, newdata, 0, data.length);
    data = newdata;
    data[pos] = Token.FUNCTIONVARARG.getCode();
    data[pos+1] = (byte) numArgs;
    data[pos+2] = 1;
    data[pos+3] = 0;  // indicates the end of the expression

    // Position the final offsets
    int endPos = data.length - 1;

    if (numArgs < 3)
    {
      // Set the offset to the false expression to be the current position
      IntegerHelper.getTwoBytes(endPos - falseOffsetPos - 5,
                                data, falseOffsetPos);
    }

    // Set the offset after the true expression
    IntegerHelper.getTwoBytes(endPos - gotoEndPos - 2,
                              data, gotoEndPos);

    return data;
  }

  /**
   * Gets the precedence for this operator.  Operator precedents run from
   * 1 to 5, one being the highest, 5 being the lowest
   *
   * @return the operator precedence
   */
  int getPrecedence()
  {
    return 3;
  }

  /**
   * Default behaviour is to do nothing
   *
   * @param colAdjust the amount to add on to each relative cell reference
   * @param rowAdjust the amount to add on to each relative row reference
   */
  public void adjustRelativeCellReferences(int colAdjust, int rowAdjust)
  {
    ParseItem[] operands = getOperands();
    for (int i = 0 ; i < operands.length ; i++)
    {
      operands[i].adjustRelativeCellReferences(colAdjust, rowAdjust);
    }
  }

  /**
   * Called when a column is inserted on the specified sheet.  Tells
   * the formula  parser to update all of its cell references beyond this
   * column
   *
   * @param sheetIndex the sheet on which the column was inserted
   * @param col the column number which was inserted
   * @param currentSheet TRUE if this formula is on the sheet in which the
   * column was inserted, FALSE otherwise
   */
  void columnInserted(int sheetIndex, int col, boolean currentSheet)
  {
    ParseItem[] operands = getOperands();
    for (int i = 0 ; i < operands.length ; i++)
    {
      operands[i].columnInserted(sheetIndex, col, currentSheet);
    }
  }

  /**
   * Called when a column is inserted on the specified sheet.  Tells
   * the formula  parser to update all of its cell references beyond this
   * column
   *
   * @param sheetIndex the sheet on which the column was removed
   * @param col the column number which was removed
   * @param currentSheet TRUE if this formula is on the sheet in which the
   * column was inserted, FALSE otherwise
   */
  void columnRemoved(int sheetIndex, int col, boolean currentSheet)
  {
    ParseItem[] operands = getOperands();
    for (int i = 0 ; i < operands.length ; i++)
    {
      operands[i].columnRemoved(sheetIndex, col, currentSheet);
    }
  }

  /**
   * Called when a column is inserted on the specified sheet.  Tells
   * the formula  parser to update all of its cell references beyond this
   * column
   *
   * @param sheetIndex the sheet on which the row was inserted
   * @param row the row number which was inserted
   * @param currentSheet TRUE if this formula is on the sheet in which the
   * column was inserted, FALSE otherwise
   */
  void rowInserted(int sheetIndex, int row, boolean currentSheet)
  {
    ParseItem[] operands = getOperands();
    for (int i = 0 ; i < operands.length ; i++)
    {
      operands[i].rowInserted(sheetIndex, row, currentSheet);
    }
  }

  /**
   * Called when a column is inserted on the specified sheet.  Tells
   * the formula  parser to update all of its cell references beyond this
   * column
   *
   * @param sheetIndex the sheet on which the row was removed
   * @param row the row number which was removed
   * @param currentSheet TRUE if this formula is on the sheet in which the
   * column was inserted, FALSE otherwise
   */
  void rowRemoved(int sheetIndex, int row, boolean currentSheet)
  {
    ParseItem[] operands = getOperands();
    for (int i = 0 ; i < operands.length ; i++)
    {
      operands[i].rowRemoved(sheetIndex, row, currentSheet);
    }
  }
}




