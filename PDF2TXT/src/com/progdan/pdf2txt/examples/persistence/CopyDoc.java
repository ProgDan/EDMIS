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
package com.progdan.pdf2txt.examples.persistence;

import java.io.IOException;

import com.progdan.pdf2txt.cos.COSDocument;



import com.progdan.pdf2txt.pdfparser.PDFParser;

import com.progdan.pdf2txt.pdfwriter.COSWriter;
import com.progdan.pdf2txt.exceptions.COSVisitorException;

/**
 * This is an example used to copy a documents contents from a source doc to destination doc
 * via an in-memory document representation.
 *
 * @author Michael Traut
 * @version $Revision: 1.2 $
 */
public class CopyDoc
{
    /**
     * Constructor.
     */
    public CopyDoc()
    {
        super();
    }

    /**
     * This will perform the document copy.
     *
     * @param in The filename used for input.
     * @param out The filename used for output.
     *
     * @throws IOException If there is an error parsing the document.
     * @throws COSVisitorException If there is an error while copying the document.
     */
    public void doIt(String in, String out) throws IOException, COSVisitorException
    {
        java.io.InputStream is = null;
        java.io.OutputStream os = null;
        COSWriter writer = null;
        try
        {
            is = new java.io.FileInputStream(in);
            PDFParser parser = new PDFParser(is);
            parser.parse();

            COSDocument doc = parser.getDocument();

            os = new java.io.FileOutputStream(out);
            writer = new COSWriter(os);

            writer.write(doc);

        }
        finally
        {
            if( is != null )
            {
                is.close();
            }
            if( os != null )
            {
                os.close();
            }
            if( writer != null )
            {
                writer.close();
            }
        }
    }

    /**
     * This will copy a PDF document.
     * <br />
     * see usage() for commandline
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        CopyDoc app = new CopyDoc();
        try
        {
            if( args.length != 2 )
            {
                app.usage();
            }
            else
            {
                app.doIt( args[0], args[1]);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * This will print out a message telling how to use this example.
     */
    private void usage()
    {
        System.err.println( "usage: " + this.getClass().getName() + " <input-file> <output-file>" );
    }
}
