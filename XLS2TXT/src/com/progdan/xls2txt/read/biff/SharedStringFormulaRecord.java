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

package com.progdan.xls2txt.read.biff;

import com.progdan.xls2txt.common.Assert;
import com.progdan.xls2txt.common.Logger;

import com.progdan.xls2txt.LabelCell;
import com.progdan.xls2txt.CellType;
import com.progdan.xls2txt.StringFormulaCell;
import com.progdan.xls2txt.WorkbookSettings;
import com.progdan.xls2txt.biff.Type;
import com.progdan.xls2txt.biff.IntegerHelper;
import com.progdan.xls2txt.biff.StringHelper;
import com.progdan.xls2txt.biff.FormattingRecords;
import com.progdan.xls2txt.biff.FormulaData;
import com.progdan.xls2txt.biff.WorkbookMethods;
import com.progdan.xls2txt.biff.formula.FormulaParser;
import com.progdan.xls2txt.biff.formula.ExternalSheet;
import com.progdan.xls2txt.biff.formula.FormulaException;

/**
 * A string formula record, manufactured out of the Shared Formula
 * "optimization"
 */
public class SharedStringFormulaRecord extends BaseSharedFormulaRecord
  implements LabelCell, FormulaData, StringFormulaCell
{
  /**
   * The logger
   */
  private static Logger logger = Logger.getLogger
    (SharedStringFormulaRecord.class);

  /**
   * The value of this string formula
   */
  private String value;
  /**
   * A handle to the formatting records
   */
  private FormattingRecords formattingRecords;

  /**
   * Constructs this string formula
   *
   * @param t the record
   * @param excelFile the excel file
   * @param fr the formatting record
   * @param es the external sheet
   * @param nt the workbook
   * @param si the sheet
   * @param ws the workbook settings
   */
  public SharedStringFormulaRecord(Record t,
                                   File excelFile,
                                   FormattingRecords fr,
                                   ExternalSheet es,
                                   WorkbookMethods nt,
                                   SheetImpl si,
                                   WorkbookSettings ws)
  {
    super(t, fr, es, nt, si, excelFile.getPos());
    int pos = excelFile.getPos();

    // Save the position in the excel file
    int filepos = excelFile.getPos();

    // Look for the string record in one of the records after the
    // formula.  Put a cap on it to prevent ednas
    Record nextRecord = excelFile.next();
    int count = 0;
    while (nextRecord.getType() != Type.STRING && count < 4)
    {
      nextRecord = excelFile.next();
      count++;
    }
    Assert.verify(count < 4, " @ " + pos);

    byte[] stringData = nextRecord.getData();
    int chars = IntegerHelper.getInt(stringData[0], stringData[1]);

    boolean unicode = false;
    int startpos = 3;
    if (stringData.length == chars + 2)
    {
      // String might only consist of a one byte length indicator, instead
      // of the more normal 2
      startpos = 2;
      unicode = false;
    }
    else if (stringData[2] == 0x1)
    {
      // unicode string, two byte length indicator
      startpos = 3;
      unicode = true;
    }
    else
    {
      // ascii string, two byte length indicator
      startpos = 3;
      unicode = false;
    }

    if (!unicode)
    {
      value = StringHelper.getString(stringData, chars, startpos, ws);
    }
    else
    {
      value = StringHelper.getUnicodeString(stringData, chars, startpos);
    }

    // Restore the position in the excel file, to enable the SHRFMLA
    // record to be picked up
    excelFile.setPos(filepos);
  }

  /**
   * Accessor for the value
   *
   * @return the value
   */
  public String getString()
  {
    return value;
  }

  /**
   * Accessor for the contents as a string
   *
   * @return the value as a string
   */
  public String getContents()
  {
    return value;
  }

  /**
   * Accessor for the cell type
   *
   * @return the cell type
   */
  public CellType getType()
  {
    return CellType.STRING_FORMULA;
  }

  /**
   * Gets the raw bytes for the formula.  This will include the
   * parsed tokens array.  Used when copying spreadsheets
   *
   * @return the raw record data
   * @exception FormulaException
   */
  public byte[] getFormulaData() throws FormulaException
  {
    // Get the tokens, taking into account the mapping from shared
    // formula specific values into normal values
    FormulaParser fp = new FormulaParser
      (getTokens(), this,
       getExternalSheet(), getNameTable(),
       getSheet().getWorkbook().getSettings());
    fp.parse();
    byte[] rpnTokens = fp.getBytes();

    byte[] data = new byte[rpnTokens.length + 22];

    // Set the standard info for this cell
    IntegerHelper.getTwoBytes(getRow(), data, 0);
    IntegerHelper.getTwoBytes(getColumn(), data, 2);
    IntegerHelper.getTwoBytes(getXFIndex(), data, 4);

    // Set the two most significant bytes of the value to be 0xff in
    // order to identify this as a string
    data[6] = 0;
    data[12] = -1;
    data[13] = -1;

    // Now copy in the parsed tokens
    System.arraycopy(rpnTokens, 0, data, 22, rpnTokens.length);
    IntegerHelper.getTwoBytes(rpnTokens.length, data, 20);

    // Lop off the standard information
    byte[] d = new byte[data.length - 6];
    System.arraycopy(data, 6, d, 0, data.length - 6);

    return d;
  }
}









