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
package com.progdan.pdf2txt.examples.signature;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.security.cert.CertificateFactory;

import java.util.Collection;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSString;

import com.progdan.pdf2txt.pdmodel.PDDocument;

/**
 * This will read a document from the filesystem, decrypt it and do something with the signature.
 *
 * usage: java com.progdan.pdf2txt.examples.signature.ShowSignature &lt;password&gt; &lt;inputfile&gt;
 *
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class ShowSignature
{

    /**
     * This is the entry point for the application.
     *
     * @param args The command-line arguments.
     *
     * @throws Exception If there is an error reading the file.
     */
    public static void main( String[] args ) throws Exception
    {
        ShowSignature show = new ShowSignature();
        show.showSignature( args );
    }

    private void showSignature( String[] args ) throws Exception
    {
        if( args.length != 2 )
        {
            usage();
        }
        else
        {
            String password = args[0];
            String infile = args[1];
            PDDocument document = null;
            try
            {
                document = PDDocument.load( infile );

                if( document.isEncrypted() )
                {
                    document.decrypt( password );
                }
                else
                {
                    System.err.println( "Warning: Document is not encrypted." );
                }

                COSDictionary trailer = document.getDocument().getTrailer();
                COSDictionary root = (COSDictionary)trailer.getDictionaryObject( COSName.ROOT );
                COSDictionary acroForm = (COSDictionary)root.getDictionaryObject( COSName.getPDFName( "AcroForm" ) );
                COSArray fields = (COSArray)acroForm.getDictionaryObject( COSName.getPDFName( "Fields" ) );
                for( int i=0; i<fields.size(); i++ )
                {
                    COSDictionary field = (COSDictionary)fields.getObject( i );
                    COSName type = (COSName)field.getDictionaryObject( COSName.getPDFName( "FT" ) );
                    if( type != null && type.getName().equals( "Sig" ) )
                    {
                        COSDictionary cert = (COSDictionary)field.getDictionaryObject( COSName.getPDFName( "V" ) );
                        System.out.println( "Certificate found" );
                        System.out.println( "Name=" + cert.getDictionaryObject( COSName.getPDFName( "Name" ) ) );
                        System.out.println( "Modified=" + cert.getDictionaryObject( COSName.getPDFName( "M" ) ) );
                        COSName subFilter = (COSName)cert.getDictionaryObject( COSName.getPDFName( "SubFilter" ) );
                        if( subFilter != null )
                        {
                            if( subFilter.getName().equals( "adbe.x509.rsa_sha1" ) )
                            {
                                COSString certString = (COSString)cert.getDictionaryObject(
                                    COSName.getPDFName( "Cert" ) );
                                byte[] certData = certString.getBytes();
                                CertificateFactory factory = CertificateFactory.getInstance( "X.509" );
                                Collection certs = factory.generateCertificates( new ByteArrayInputStream( certData ) );
                                System.out.println( "certs=" + certs );
                            }
                            else
                            {
                                System.err.println( "Unknown certificate type:" + subFilter );
                            }
                        }
                        else
                        {
                            throw new IOException( "Missing subfilter for cert dictionary" );
                        }
                    }
                }
            }
            finally
            {
                document.close();
            }
        }
    }

    /**
     * This will print a usage message.
     */
    private static void usage()
    {
        System.err.println( "usage: java com.progdan.pdf2txt.examples.signature.ShowSignature " +
                            "<password> <inputfile>" );
    }

}
