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

import java.text.NumberFormat;

import com.progdan.xls2txt.DateCell;
import com.progdan.xls2txt.CellType;
import com.progdan.xls2txt.DateFormulaCell;
import com.progdan.xls2txt.biff.FormattingRecords;
import com.progdan.xls2txt.biff.FormulaData;
import com.progdan.xls2txt.biff.WorkbookMethods;
import com.progdan.xls2txt.biff.formula.FormulaParser;
import com.progdan.xls2txt.biff.formula.ExternalSheet;
import com.progdan.xls2txt.biff.formula.FormulaException;

/**
 * A date formula's last calculated value
 */
class DateFormulaRecord extends DateRecord
  implements DateCell, FormulaData, DateFormulaCell
{
  /**
   * The last calculated value of the formula
   */
  private double value;

  /**
   * The formula as an excel string
   */
  private String formulaString;

  /**
   * A handle to the class needed to access external sheets
   */
  private ExternalSheet externalSheet;

  /**
   * A handle to the name table
   */
  private WorkbookMethods nameTable;

  /**
   * The raw data
   */
  private byte[] data;

  /**
   * Constructs this object from the raw data
   *
   * @param t the basic number formula record
   * @param fr the formatting records
   * @param es the external sheet
   * @param nt the name table
   * @param nf flag indicating whether the 1904 date system is in use
   * @param si the sheet
   */
  public DateFormulaRecord(NumberFormulaRecord t, FormattingRecords fr,
                           ExternalSheet es, WorkbookMethods nt,
                           boolean nf, SheetImpl si)
  {
    super(t, t.getXFIndex(), fr, nf, si);

    externalSheet = es;
    nameTable = nt;
    data = t.getFormulaData();
  }

  /**
   * Returns the cell type
   *
   * @return The cell type
   */
  public CellType getType()
  {
    return CellType.DATE_FORMULA;
  }

  /**
   * Gets the raw bytes for the formula.  This will include the
   * parsed tokens array.  Used when copying spreadsheets
   *
   * @return the raw record data
   */
  public byte[] getFormulaData()
  {
    // Data is already the formula data, so don't do any more manipulation
    return data;
  }

  /**
   * Gets the formula as an excel string
   *
   * @return the formula as an excel string
   * @exception FormulaException
   */
  public String getFormula() throws FormulaException
  {
    // Note that the standard information was lopped off by the NumberFormula
    // record when creating this formula
    if (formulaString == null)
    {
      byte[] tokens = new byte[data.length - 16];
      System.arraycopy(data, 16, tokens, 0, tokens.length);
      FormulaParser fp = new FormulaParser
        (tokens, this, externalSheet, nameTable,
         getSheet().getWorkbook().getSettings());
      fp.parse();
      formulaString = fp.getFormula();
    }

    return formulaString;
  }

  /**
   * Interface method which returns the value
   *
   * @return the last calculated value of the formula
   */
  public double getValue()
  {
    return value;
  }

  /**
   * Dummy implementation in order to adhere to the NumberCell interface
   *
   * @return NULL
   */
  public NumberFormat getNumberFormat()
  {
    return null;
  }

}
