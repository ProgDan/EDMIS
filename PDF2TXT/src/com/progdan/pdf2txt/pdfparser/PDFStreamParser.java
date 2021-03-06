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
package com.progdan.pdf2txt.pdfparser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import java.util.ArrayList;
import java.util.List;

import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSBoolean;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNull;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSObject;
import com.progdan.pdf2txt.cos.COSStream;

import com.progdan.pdf2txt.util.PDFOperator;
import com.progdan.pdf2txt.util.ImageParameters;

import com.progdan.logengine.Category;

/**
 * This will parse a PDF byte stream and extract operands and such.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDFStreamParser extends BaseParser
{
    private static Category log = Category.getInstance(PDFStreamParser.class.getName());
    private List streamObjects = new ArrayList( 100 );
    private RandomAccessFile file;
    private PDFOperator lastBIToken = null;

    /**
     * Constructor that takes a stream to parse.
     *
     * @param stream The stream to read data from.
     * @param raf The random access file.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    public PDFStreamParser( InputStream stream, RandomAccessFile raf ) throws IOException
    {
        super( stream );
        file = raf;
    }

    /**
     * Constructor.
     *
     * @param stream The stream to parse.
     *
     * @throws IOException If there is an error initializing the stream.
     */
    public PDFStreamParser( COSStream stream ) throws IOException
    {
       this( stream.getUnfilteredStream(), stream.getScratchFile() );
    }

    /**
     * This will parse the tokens in the stream.  This will close the
     * stream when it is finished parsing.
     *
     * @throws IOException If there is an error while parsing the stream.
     */
    public void parse() throws IOException
    {
        log.debug( "parse() start" );

        try
        {
            Object token = null;
            while( (token = parseNextToken()) != null )
            {
                streamObjects.add( token );
            }
        }
        catch( IOException io )
        {
            System.out.println( "" + pdfSource );
            throw io;
        }
        finally
        {
            pdfSource.close();
        }
        log.debug( "parse() end" );
    }

    /**
     * This will get the tokens that were parsed from the stream.
     *
     * @return All of the tokens in the stream.
     */
    public List getTokens()
    {
        return streamObjects;
    }

    /**
     * This will parse the next token in the stream.
     *
     * @return The next token in the stream or null if there are no more tokens in the stream.
     *
     * @throws IOException If an io error occurs while parsing the stream.
     */
    private Object parseNextToken() throws IOException
    {
        log.debug( "parseNextToken() start" );
        Object retval = null;

        skipSpaces();
        int nextByte = pdfSource.peek();
        if( ((byte)nextByte) == -1 )
        {
            return null;
        }
        char c = (char)nextByte;
        switch(c)
        {
            case '<':
            {
                int leftBracket = pdfSource.read();//pull off first left bracket
                c = (char)pdfSource.peek(); //check for second left bracket
                pdfSource.unread( leftBracket ); //put back first bracket
                if(c == '<')
                {

                    COSDictionary pod = parseCOSDictionary();
                    skipSpaces();
                    if((char)pdfSource.peek() == 's')
                    {
                        retval = parseCOSStream( pod, file );
                    }
                    else
                    {
                        retval = pod;
                    }
                }
                else
                {
                    retval = parseCOSString();
                }
                break;
            }
            case '[': // array
            {
                retval = parseCOSArray();
                break;
            }
            case '(': // string
                retval = parseCOSString();
                break;
            case '/':   // name
                retval = parseCOSName();
                break;
            case 'n':   // null
            {
                String nullString = readString();
                if( nullString.equals( "null") )
                {
                    retval = COSNull.NULL;
                }
                else
                {
                    retval = PDFOperator.getOperator( nullString );
                }
                break;
            }
            case 't':
            case 'f':
            {
                String next = readString();
                if( next.equals( "true" ) )
                {
                    retval = COSBoolean.TRUE;
                    break;
                }
                else if( next.equals( "false" ) )
                {
                    retval = COSBoolean.FALSE;
                }
                else
                {
                    retval = PDFOperator.getOperator( next );
                }
                break;
            }
            case 'R':
            {
                String line = readString();
                if( line.equals( "R" ) )
                {
                    retval = new COSObject( null );
                }
                else
                {
                    retval = PDFOperator.getOperator( line );
                }
                break;
            }
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
            case '-':
            case '+':
            case '.':
            {
                if( Character.isDigit(c) || c == '-' || c == '+' || c == '.')
                {
                    StringBuffer buf = new StringBuffer();
                    while( Character.isDigit(( c = (char)pdfSource.peek()) )|| c== '-' || c== '+' || c =='.' )
                    {
                        buf.append( c );
                        pdfSource.read();
                    }
                    retval = COSNumber.get( buf.toString() );
                }
                else
                {
                    throw new IOException( "Unknown dir object c='" + c +
                        "' peek='" + (char)pdfSource.peek() + "' " + pdfSource );
                }
                break;
            }
            case 'B':
            {
                String next = readString();
                retval = PDFOperator.getOperator( next );

                if( next.equals( "BI" ) )
                {
                    lastBIToken = (PDFOperator)retval;
                    COSDictionary imageParams = new COSDictionary();
                    lastBIToken.setImageParameters( new ImageParameters( imageParams ) );
                    Object nextToken = null;
                    while( (nextToken = parseNextToken()) instanceof COSName )
                    {
                        Object value = parseNextToken();
                        imageParams.setItem( (COSName)nextToken, (COSBase)value );
                    }
                    //final token will be the image data, maybe??
                    PDFOperator imageData = (PDFOperator)nextToken;
                    lastBIToken.setImageData( imageData.getImageData() );
                }
                break;
            }
            case 'I':
            {
                ImageParameters imageParams = lastBIToken.getImageParameters();
                int expectedBytes = (int)Math.ceil(imageParams.getHeight() * imageParams.getWidth() *
                                    (imageParams.getBitsPerComponent()/8) );
                //Special case for ID operator
                String id = "" + (char)pdfSource.read() + (char)pdfSource.read();
                if( !id.equals( "ID" ) )
                {
                    throw new IOException( "Error: Expected operator 'ID' actual='" + id + "'" );
                }
                ByteArrayOutputStream imageData = new ByteArrayOutputStream();
                boolean foundEnd = false;
                if( this.isWhitespace() )
                {
                    //pull off the whitespace character
                    pdfSource.read();
                }
                int twoBytesAgo = 0;
                int lastByte = pdfSource.read();
                int currentByte = pdfSource.read();
                int count = 0;
                //PDF spec is kinda unclear about this.  Should a whitespace
                //always appear before EI? Not sure, I found a PDF
                //(UnderstandingWebSphereClassLoaders.pdf) which has EI as part
                //of the image data and will stop parsing prematurely if there is
                //not a check for <whitespace>EI<whitespace>.
                while( !(isWhitespace( twoBytesAgo ) &&
                         lastByte == 'E' &&
                         currentByte == 'I' &&
                         isWhitespace() &&
                         count >= expectedBytes
                         ) &&
                       !pdfSource.isEOF() )
                {
                    imageData.write( lastByte );
                    twoBytesAgo = lastByte;
                    lastByte = currentByte;
                    currentByte = pdfSource.read();
                    count++;
                }
                pdfSource.unread( 'I' ); //unread the EI operator
                pdfSource.unread( 'E' );
                retval = PDFOperator.getOperator( "ID" );
                ((PDFOperator)retval).setImageData( imageData.toByteArray() );
                break;
            }
            case ']':
            {
                // some ']' around without its previous '['
                // this means a PDF is somewhat corrupt but we will continue to parse.
                pdfSource.read();
                retval = COSNull.NULL;  // must be a better solution than null...
                break;
            }
            default:
            {
                //we must be an operator
                String operator = readOperator();
                if( operator.trim().length() == 0 )
                {
                    //we have a corrupt stream, stop reading here
                    retval = null;
                }
                else
                {
                    retval = PDFOperator.getOperator( operator );
                }
            }

        }
        if( log.isDebugEnabled() )
        {
            log.debug( "parseNextToken() retval=" + retval + " peek=" + (char)pdfSource.peek() + " end" );
        }

        return retval;
    }

    /**
     * This will read an operator from the stream.
     *
     * @return The operator that was read from the stream.
     *
     * @throws IOException If there is an error reading from the stream.
     */
    protected String readOperator() throws IOException
    {
        skipSpaces();

        //average string size is around 2 and the normal string buffer size is
        //about 16 so lets save some space.
        StringBuffer buffer = new StringBuffer(4);
        while(
            !isWhitespace() &&
            !isClosing() &&
            !pdfSource.isEOF() &&
            pdfSource.peek() != (int)'[' &&
            pdfSource.peek() != (int)'<' &&
            pdfSource.peek() != (int)'(' &&
            pdfSource.peek() != (int)'/' &&
            (pdfSource.peek() < (int)'0' ||
             pdfSource.peek() > (int)'9' ) )
        {
            buffer.append( (char)pdfSource.read() );
        }
        return buffer.toString();
    }
}
