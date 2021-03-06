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
package com.progdan.pdf2txt.pdmodel.interactive.annotation;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;

import com.progdan.pdf2txt.pdmodel.common.PDRectangle;
import com.progdan.pdf2txt.pdmodel.common.COSObjectable;

import com.progdan.pdf2txt.pdmodel.PDResources;


/**
 * This class represents an appearance for an annotation.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDAppearanceStream implements COSObjectable
{
    private COSStream stream = null;


    /**
     * Constructor.
     *
     * @param s The cos stream for this appearance.
     */
    public PDAppearanceStream( COSStream s )
    {
        stream = s;
    }

    /**
     * This will return the underlying stream.
     *
     * @return The wrapped stream.
     */
    public COSStream getStream()
    {
        return stream;
    }

    /**
     * @see COSObjectable#getCOSObject()
     */
    public COSBase getCOSObject()
    {
        return stream;
    }

    /**
     * Get the bounding box for this appearance.  This may return null in which
     * case the Rectangle from the annotation should be used.
     *
     * @return The bounding box for this appearance.
     */
    public PDRectangle getBoundingBox()
    {
        PDRectangle box = null;
        COSArray bbox = (COSArray)stream.getDictionaryObject( COSName.getPDFName( "BBox" ) );
        if( bbox != null )
        {
            box = new PDRectangle( bbox );
        }
        return box;
    }

    /**
     * This will set the bounding box for this appearance stream.
     *
     * @param rectangle The new bounding box.
     */
    public void setBoundingBox( PDRectangle rectangle )
    {
        COSArray array = null;
        if( rectangle != null )
        {
            array = rectangle.getCOSArray();
        }
        stream.setItem( COSName.getPDFName( "BBox" ), array );
    }

    /**
     * This will get the resources for this appearance stream.
     *
     * @return The appearance stream resources.
     */
    public PDResources getResources()
    {
        PDResources retval = null;
        COSDictionary dict = (COSDictionary)stream.getDictionaryObject( COSName.RESOURCES );
        if( dict != null )
        {
            retval = new PDResources( dict );
        }
        return retval;
    }

    /**
     * This will set the new resources.
     *
     * @param resources The new resources.
     */
    public void setResources( PDResources resources )
    {
        COSDictionary dict = null;
        if( resources != null )
        {
            dict = resources.getCOSDictionary();
        }
        stream.setItem( COSName.RESOURCES, dict );
    }
}
