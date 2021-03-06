/**
 * Copyright (c) 2004, ProgDan� Software
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of pdf2txt; nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE REGENTS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * http://progdan.no-ip.org:25000
 *
 */
package com.progdan.pdf2txt.pdmodel.interactive.form;

import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import java.io.IOException;

/**
 * A class for handling the PDF field as a PDPushButton.
 *
 * @author sug
 * @version $Revision: 1.2 $
 */
public class PDPushButton extends PDField
{

    /**
     * @see com.progdan.pdf2txt.pdmodel.field.PDField#COSField(com.progdan.pdf2txt.cos.COSDictionary)
     *
     * @param theAcroForm The acroForm for this field.
     * @param field The field for this push button.
     */
    public PDPushButton( PDAcroForm theAcroForm, COSDictionary field)
    {
        super(theAcroForm, field);
    }

    /**
     * @see as.interactive.pdf.form.cos.COSField#setValue(java.lang.String)
     *
     * @param value The new value for the field.
     *
     * @throws IOException If there is an error creating the appearance stream.
     */
    public void setValue(String value) throws IOException
    {
        COSString fieldValue = new COSString(value);
        getDictionary().setItem( COSName.getPDFName( "V" ), fieldValue );
        getDictionary().setItem( COSName.getPDFName( "DV" ), fieldValue );
    }

    /**
     * getValue gets the fields value to as a string.
     *
     * @return The string value of this field.
     *
     * @throws IOException If there is an error getting the value.
     */
    public String getValue() throws IOException
    {
        return getDictionary().getString( "V" );
    }
}
