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
package com.progdan.pdf2txt.examples.pdmodel;

import java.io.FileOutputStream;
import java.io.IOException;

import com.progdan.pdf2txt.exceptions.COSVisitorException;

import com.progdan.pdf2txt.pdfwriter.COSWriter;

import com.progdan.pdf2txt.pdmodel.PDDocument;
import com.progdan.pdf2txt.pdmodel.PDPage;

/**
 * This will create a blank PDF and write the contents to a file.
 *
 * usage: java com.progdan.pdf2txt.examples.pdmodel.CreateBlankPDF &lt;outputfile.pdf&gt;
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class CreateBlankPDF
{

    /**
     * Creates a new instance of CreateBlankPDF.
     */
    public CreateBlankPDF()
    {
    }

    /**
     * This will create a blank PDF and write the contents to a file.
     *
     * @param file The name of the file to write to.
     *
     * @throws IOException If there is an error writing the data.
     * @throws COSVisitorException If there is an error while generating the document.
     */
    public void create( String file ) throws IOException, COSVisitorException
    {
        PDDocument document = null;
        FileOutputStream output = null;
        COSWriter writer = null;
        try
        {
            document = new PDDocument();
            //Every document requires at least one page, so we will add one
            //blank page.
            PDPage blankPage = new PDPage();
            document.addPage( blankPage );
            output = new FileOutputStream( file );
            writer = new COSWriter( output );
            writer.write( document.getDocument() );
        }
        finally
        {
            if( writer != null )
            {
                writer.close();
            }
            if( output != null )
            {
                output.close();
            }
            if( document != null )
            {
                document.close();
            }
        }
    }

    /**
     * This will create a blank document.
     *
     * @param args The command line arguments.
     *
     * @throws IOException If there is an error writing the document data.
     * @throws COSVisitorException If there is an error generating the data.
     */
    public static void main( String[] args ) throws IOException, COSVisitorException
    {
        if( args.length != 1 )
        {
            usage();
        }
        else
        {
            CreateBlankPDF creator = new CreateBlankPDF();
            creator.create( args[0] );
        }
    }

    /**
     * This will print the usage of this class.
     */
    private static void usage()
    {
        System.err.println( "usage: java com.progdan.pdf2txt.examples.pdmodel.CreateBlankPDF <outputfile.pdf>" );
    }
}
