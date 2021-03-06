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
package com.progdan.pdf2txt.pdmodel;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSNumber;
import com.progdan.pdf2txt.cos.COSInteger;

import com.progdan.pdf2txt.pdmodel.common.COSArrayList;
import com.progdan.pdf2txt.pdmodel.common.COSObjectable;
import com.progdan.pdf2txt.pdmodel.common.PDRectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This represents a page node in a pdf document.
 *
 * @author  Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDPageNode implements COSObjectable
{
    private COSDictionary page;

    /**
     * Creates a new instance of PDPage.
     */
    public PDPageNode()
    {
        page = new COSDictionary();
        page.setItem( COSName.getPDFName( "Type" ), COSName.getPDFName( "Pages" ) );
        page.setItem( COSName.KIDS, new COSArray() );
        page.setItem( COSName.COUNT, new COSInteger( 0 ) );
    }

    /**
     * Creates a new instance of PDPage.
     *
     * @param pages The dictionary pages.
     */
    public PDPageNode( COSDictionary pages )
    {
        page = pages;
    }

    /**
     * This will update the count attribute of the page node.  This only needs to
     * be called if you add or remove pages.  The PDDocument will call this for you
     * when you use the PDDocumnet persistence methods.  So, basically most clients
     * will never need to call this.
     *
     * @return The update count for this node.
     */
    public long updateCount()
    {
        long totalCount = 0;
        List kids = getKids();
        Iterator kidIter = kids.iterator();
        while( kidIter.hasNext() )
        {
            Object next = kidIter.next();
            if( next instanceof PDPage )
            {
                totalCount++;
            }
            else
            {
                PDPageNode node = (PDPageNode)next;
                totalCount += node.updateCount();
            }
        }
        page.setItem( COSName.COUNT, new COSInteger( totalCount ) );
        return totalCount;
    }

    /**
     * This will get the count of descendent page objects.
     *
     * @return The total number of descendent page objects.
     */
    public long getCount()
    {
        return ((COSNumber)page.getDictionaryObject( COSName.COUNT )).intValue();
    }

    /**
     * This will get the underlying dictionary that this class acts on.
     *
     * @return The underlying dictionary for this class.
     */
    public COSDictionary getDictionary()
    {
        return page;
    }

    /**
     * This is the parent page node.
     *
     * @return The parent to this page.
     */
    public PDPageNode getParent()
    {
        PDPageNode parent = null;
        COSDictionary parentDic = (COSDictionary)page.getDictionaryObject( COSName.PARENT );
        if( parentDic != null )
        {
            parent = new PDPageNode( parentDic );
        }
        return parent;
    }

    /**
     * This will set the parent of this page.
     *
     * @param parent The parent to this page node.
     */
    public void setParent( PDPageNode parent )
    {
        page.setItem( COSName.PARENT, parent.getDictionary() );
    }

    /**
     * @see COSObjectable#getCOSObject()
     */
    public COSBase getCOSObject()
    {
        return page;
    }

    /**
     * This will return all kids of this node, either PDPageNode or PDPage.
     *
     * @return All direct descendents of this node.
     */
    public List getKids()
    {
        List actuals = new ArrayList();
        COSArray kids = (COSArray)page.getDictionaryObject( COSName.KIDS );

        for( int i=0; i<kids.size(); i++ )
        {
            COSDictionary kid = (COSDictionary)kids.getObject( i );
            if( COSName.PAGE.equals( kid.getDictionaryObject( COSName.TYPE ) ) )
            {
                actuals.add( new PDPage( kid ) );
            }
            else
            {
                actuals.add( new PDPageNode( kid ) );
            }
        }
        return new COSArrayList( actuals, kids );
    }

    /**
     * This will get the resources at this page node and not look up the hierarchy.
     * This attribute is inheritable, and findResources() should probably used.
     * This will return null if no resources are available at this level.
     *
     * @return The resources at this level in the hierarchy.
     */
    public PDResources getResources()
    {
        PDResources retval = null;
        COSDictionary resources = (COSDictionary)page.getDictionaryObject( COSName.RESOURCES );
        if( resources != null )
        {
            retval = new PDResources( resources );
        }
        return retval;
    }

    /**
     * This will find the resources for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The resources at this level in the hierarchy.
     */
    public PDResources findResources()
    {
        PDResources retval = getResources();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = parent.findResources();
        }
        return retval;
    }

    /**
     * This will set the resources for this page.
     *
     * @param resources The new resources for this page.
     */
    public void setResources( PDResources resources )
    {
        if( resources == null )
        {
            page.removeItem( COSName.RESOURCES );
        }
        else
        {
            page.setItem( COSName.RESOURCES, resources.getCOSDictionary() );
        }
    }

    /**
     * This will get the MediaBox at this page and not look up the hierarchy.
     * This attribute is inheritable, and findMediaBox() should probably used.
     * This will return null if no MediaBox are available at this level.
     *
     * @return The MediaBox at this level in the hierarchy.
     */
    public PDRectangle getMediaBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "MediaBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        return retval;
    }

    /**
     * This will find the MediaBox for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The MediaBox at this level in the hierarchy.
     */
    public PDRectangle findMediaBox()
    {
        PDRectangle retval = getMediaBox();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = parent.findMediaBox();
        }
        return retval;
    }

    /**
     * This will set the mediaBox for this page.
     *
     * @param mediaBox The new mediaBox for this page.
     */
    public void setMediaBox( PDRectangle mediaBox )
    {
        if( mediaBox == null )
        {
            page.removeItem( COSName.getPDFName( "MediaBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "MediaBox" ), mediaBox.getCOSArray() );
        }
    }

/**
     * This will get the CropBox at this page and not look up the hierarchy.
     * This attribute is inheritable, and findCropBox() should probably used.
     * This will return null if no CropBox is available at this level.
     *
     * @return The CropBox at this level in the hierarchy.
     */
    public PDRectangle getCropBox()
    {
        PDRectangle retval = null;
        COSArray array = (COSArray)page.getDictionaryObject( COSName.getPDFName( "CropBox" ) );
        if( array != null )
        {
            retval = new PDRectangle( array );
        }
        return retval;
    }

    /**
     * This will find the CropBox for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The CropBox at this level in the hierarchy.
     */
    public PDRectangle findCropBox()
    {
        PDRectangle retval = getCropBox();
        PDPageNode parent = getParent();
        if( retval == null && parent != null )
        {
            retval = findParentCropBox( parent );
        }

        //default value for cropbox is the media box
        if( retval == null )
        {
            retval = findMediaBox();
        }
        return retval;
    }

    /**
     * This will search for a crop box in the parent and return null if it is not
     * found.  It will NOT default to the media box if it cannot be found.
     *
     * @param node The node
     */
    private PDRectangle findParentCropBox( PDPageNode node )
    {
        PDRectangle rect = node.getCropBox();
        PDPageNode parent = node.getParent();
        if( rect == null && parent != null )
        {
            rect = findParentCropBox( node );
        }
        return rect;
    }

    /**
     * This will set the CropBox for this page.
     *
     * @param cropBox The new CropBox for this page.
     */
    public void setCropBox( PDRectangle cropBox )
    {
        if( cropBox == null )
        {
            page.removeItem( COSName.getPDFName( "CropBox" ) );
        }
        else
        {
            page.setItem( COSName.getPDFName( "CropBox" ), cropBox.getCOSArray() );
        }
    }

    /**
     * A value representing the rotation.  This will be null if not set at this level
     * The number of degrees by which the page should
     * be rotated clockwise when displayed or printed. The value must be a multiple
     * of 90.
     *
     * This will get the rotation at this page and not look up the hierarchy.
     * This attribute is inheritable, and findRotation() should probably used.
     * This will return null if no rotation is available at this level.
     *
     * @return The rotation at this level in the hierarchy.
     */
    public Integer getRotation()
    {
        Integer retval = null;
        COSNumber value = (COSNumber)page.getDictionaryObject( COSName.getPDFName( "Rotate" ) );
        if( value != null )
        {
            retval = new Integer( (int)value.intValue() );
        }
        return retval;
    }

    /**
     * This will find the rotation for this page by looking up the hierarchy until
     * it finds them.
     *
     * @return The rotation at this level in the hierarchy.
     */
    public int findRotation()
    {
        int retval = 0;
        Integer rotation = getRotation();
        if( rotation != null )
        {
            retval = rotation.intValue();
        }
        else
        {
            PDPageNode parent = getParent();
            if( parent != null )
            {
                retval = parent.findRotation();
            }
        }

        return retval;
    }

    /**
     * This will set the rotation for this page.
     *
     * @param rotation The new rotation for this page.
     */
    public void setRotation( int rotation )
    {
        page.setItem( COSName.getPDFName( "Rotate" ), new COSInteger( rotation ) );
    }
}
