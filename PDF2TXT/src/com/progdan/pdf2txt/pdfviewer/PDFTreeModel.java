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
package com.progdan.pdf2txt.pdfviewer;

/**
 * A tree model that uses a cos document.
 *
 *
 * @author  wurtz
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeModel;

//import java.awt.event.*;
import javax.swing.event.TreeModelListener;

import com.progdan.pdf2txt.cos.COSArray;
import com.progdan.pdf2txt.cos.COSBase;
import com.progdan.pdf2txt.cos.COSDictionary;
import com.progdan.pdf2txt.cos.COSName;
import com.progdan.pdf2txt.cos.COSStream;

import com.progdan.pdf2txt.pdmodel.PDDocument;

import java.util.List;

/**
 * A class to model a PDF document as a tree structure.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDFTreeModel implements TreeModel
{
    private PDDocument document;

    /**
     * constructor.
     */
    public PDFTreeModel()
    {
    }

    /**
     * Constructor to take a document.
     *
     * @param doc The document to display in the tree.
     */
    public PDFTreeModel(PDDocument doc)
    {
         setDocument(doc);
    }

    /**
     * Set the document to display in the tree.
     *
     * @param doc The document to display in the tree.
     */
    public void setDocument(PDDocument doc)
    {
        document = doc;
    }
    /**
     * Adds a listener for the <code>TreeModelEvent</code>
     * posted after the tree changes.
     *
     * @param   l       the listener to add
     * @see     #removeTreeModelListener
     *
     */
    public void addTreeModelListener(TreeModelListener l)
    {
    }

    /**
     * Returns the child of <code>parent</code> at index <code>index</code>
     * in the parent's
     * child array.  <code>parent</code> must be a node previously obtained
     * from this data source. This should not return <code>null</code>
     * if <code>index</code>
     * is a valid index for <code>parent</code> (that is <code>index >= 0 &&
     * index < getChildCount(parent</code>)).
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @param index The index into the parent object to location the child object.
     * @return  the child of <code>parent</code> at index <code>index</code>
     *
     */
    public Object getChild(Object parent, int index)
    {
        Object retval = null;
        if( parent instanceof COSArray )
        {
            ArrayEntry entry = new ArrayEntry();
            entry.setIndex( index );
            entry.setValue( ((COSArray)parent).getObject( index ) );
            retval = entry;
        }
        else if( parent instanceof COSStream )
        {
            COSStream stream = (COSStream)parent;
            retval = getChild( stream.getDictionary(), index );
        }
        else if( parent instanceof COSDictionary )
        {
            COSDictionary dict = ((COSDictionary)parent);
            Object key = dict.keyList().get( index );
            Object value = dict.getDictionaryObject( (COSName)key );
            MapEntry entry = new MapEntry();
            entry.setKey( key );
            entry.setValue( value );
            retval = entry;
        }
        else if( parent instanceof MapEntry )
        {
            retval = getChild( ((MapEntry)parent).getValue(), index );
        }
        else if( parent instanceof ArrayEntry )
        {
            retval = getChild( ((ArrayEntry)parent).getValue(), index );
        }
        else
        {
            throw new RuntimeException( "Unknown COS type " + parent.getClass().getName() );
        }
        return retval;
    }

    /** Returns the number of children of <code>parent</code>.
     * Returns 0 if the node
     * is a leaf or if it has no children.  <code>parent</code> must be a node
     * previously obtained from this data source.
     *
     * @param   parent  a node in the tree, obtained from this data source
     * @return  the number of children of the node <code>parent</code>
     *
     */
    public int getChildCount(Object parent)
    {
        int retval = 0;
        if( parent instanceof COSArray )
        {
            retval = ((COSArray)parent).size();
        }
        else if( parent instanceof COSDictionary )
        {
            retval = ((COSDictionary)parent).size();
        }
        else if( parent instanceof MapEntry )
        {
            retval = getChildCount( ((MapEntry)parent).getValue() );
        }
        else if( parent instanceof ArrayEntry )
        {
            retval = getChildCount( ((ArrayEntry)parent).getValue() );
        }
        else if( parent instanceof COSStream )
        {
            retval = getChildCount( ((COSStream)parent).getDictionary() );
        }
        return retval;
    }

    /** Returns the index of child in parent.  If <code>parent</code>
     * is <code>null</code> or <code>child</code> is <code>null</code>,
     * returns -1.
     *
     * @param parent a note in the tree, obtained from this data source
     * @param child the node we are interested in
     * @return the index of the child in the parent, or -1 if either
     *    <code>child</code> or <code>parent</code> are <code>null</code>
     *
     */
    public int getIndexOfChild(Object parent, Object child)
    {
        int retval = -1;
        if( parent != null && child != null )
        {
            if( parent instanceof COSArray )
            {
                COSArray array = (COSArray)parent;
                if( child instanceof ArrayEntry )
                {
                    ArrayEntry arrayEntry = (ArrayEntry)child;
                    retval = arrayEntry.getIndex();
                }
                else
                {
                    retval = array.indexOf( (COSBase)child );
                }
            }
            else if( parent instanceof COSDictionary )
            {
                MapEntry entry = (MapEntry)child;
                COSDictionary dict = (COSDictionary)parent;
                List keys = dict.keyList();
                for( int i=0; retval == -1 && i<keys.size(); i++ )
                {
                    if( keys.get( i ).equals( entry.getKey() ) )
                    {
                        retval = i;
                    }
                }
            }
            else if( parent instanceof MapEntry )
            {
                retval = getIndexOfChild( ((MapEntry)parent).getValue(), child );
            }
            else if( parent instanceof COSStream )
            {
                retval = getIndexOfChild( ((COSStream)parent).getDictionary(), child );
            }
            else if( parent instanceof ArrayEntry )
            {
                retval = getIndexOfChild( ((ArrayEntry)parent).getValue(), child );
            }
            else
            {
                throw new RuntimeException( "Unknown COS type " + parent.getClass().getName() );
            }
        }
        return retval;
    }

    /** Returns the root of the tree.  Returns <code>null</code>
     * only if the tree has no nodes.
     *
     * @return  the root of the tree
     *
     */
    public Object getRoot()
    {
        return document.getDocument().getTrailer();
    }

    /** Returns <code>true</code> if <code>node</code> is a leaf.
     * It is possible for this method to return <code>false</code>
     * even if <code>node</code> has no children.
     * A directory in a filesystem, for example,
     * may contain no files; the node representing
     * the directory is not a leaf, but it also has no children.
     *
     * @param   node  a node in the tree, obtained from this data source
     * @return  true if <code>node</code> is a leaf
     *
     */
    public boolean isLeaf(Object node)
    {
        boolean isLeaf = !(node instanceof COSDictionary ||
                 node instanceof COSArray ||
                 node instanceof COSStream ||
                 (node instanceof MapEntry && !isLeaf(((MapEntry)node).getValue()) ) ||
                 (node instanceof ArrayEntry && !isLeaf(((ArrayEntry)node).getValue()) ));
        return isLeaf;
    }

    /** Removes a listener previously added with
     * <code>addTreeModelListener</code>.
     *
     * @see     #addTreeModelListener
     * @param   l       the listener to remove
     *
     */

    public void removeTreeModelListener(TreeModelListener l)
    {
    }

    /** Messaged when the user has altered the value for the item identified
     * by <code>path</code> to <code>newValue</code>.
     * If <code>newValue</code> signifies a truly new value
     * the model should post a <code>treeNodesChanged</code> event.
     *
     * @param path path to the node that the user has altered
     * @param newValue the new value from the TreeCellEditor
     *
     */
    public void valueForPathChanged(TreePath path, Object newValue)
    {
    }
}
