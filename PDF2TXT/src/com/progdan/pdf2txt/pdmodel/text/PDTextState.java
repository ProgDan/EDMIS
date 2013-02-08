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
package com.progdan.pdf2txt.pdmodel.text;

import com.progdan.pdf2txt.pdmodel.font.PDFont;

/**
 * This class will hold the current state of the text parameters when executing a
 * content stream.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class PDTextState implements Cloneable
{
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_FILL_TEXT = 0;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_STROKE_TEXT = 1;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_FILL_THEN_STROKE_TEXT = 2;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_NEITHER_FILL_NOR_STROKE_TEXT = 3;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_FILL_TEXT_AND_ADD_TO_PATH_FOR_CLIPPING = 4;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_STROKE_TEXT_AND_ADD_TO_PATH_FOR_CLIPPING = 5;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_FILL_THEN_STROKE_TEXT_AND_ADD_TO_PATH_FOR_CLIPPING = 6;
    /**
     * See PDF Reference 1.5 Table 5.3.
     */
    public static final int RENDERING_MODE_ADD_TEXT_TO_PATH_FOR_CLIPPING = 7;


    //these are set default according to PDF Reference 1.5 section 5.2
    private float characterSpacing = 0;
    private float wordSpacing = 0;
    private float horizontalScaling = 100;
    private float leading = 0;
    private PDFont font;
    private float fontSize;
    private int renderingMode = 0;
    private float rise = 0;
    private boolean knockout = true;

    /**
     * Get the value of the characterSpacing.
     *
     * @return The current characterSpacing.
     */
    public float getCharacterSpacing()
    {
        return characterSpacing;
    }

    /**
     * Set the value of the characterSpacing.
     *
     * @param value The characterSpacing.
     */
    public void setCharacterSpacing(float value)
    {
        characterSpacing = value;
    }

    /**
     * Get the value of the wordSpacing.
     *
     * @return The wordSpacing.
     */
    public float getWordSpacing()
    {
        return wordSpacing;
    }

    /**
     * Set the value of the wordSpacing.
     *
     * @param value The wordSpacing.
     */
    public void setWordSpacing(float value)
    {
        wordSpacing = value;
    }

    /**
     * Get the value of the horizontalScaling.  The default is 100.  This value
     * is the percentage value 0-100 and not 0-1.  So for mathematical operations
     * you will probably need to divide by 100 first.
     *
     * @return The horizontalScaling.
     */
    public float getHorizontalScalingPercent()
    {
        return horizontalScaling;
    }

    /**
     * Set the value of the horizontalScaling.
     *
     * @param value The horizontalScaling.
     */
    public void setHorizontalScalingPercent(float value)
    {
        horizontalScaling = value;
    }

    /**
     * Get the value of the leading.
     *
     * @return The leading.
     */
    public float getLeading()
    {
        return leading;
    }

    /**
     * Set the value of the leading.
     *
     * @param value The leading.
     */
    public void setLeading(float value)
    {
        leading = value;
    }

    /**
     * Get the value of the font.
     *
     * @return The font.
     */
    public PDFont getFont()
    {
        return font;
    }

    /**
     * Set the value of the font.
     *
     * @param value The font.
     */
    public void setFont(PDFont value)
    {
        font = value;
    }

    /**
     * Get the value of the fontSize.
     *
     * @return The fontSize.
     */
    public float getFontSize()
    {
        return fontSize;
    }

    /**
     * Set the value of the fontSize.
     *
     * @param value The fontSize.
     */
    public void setFontSize(float value)
    {
        fontSize = value;
    }

    /**
     * Get the value of the renderingMode.
     *
     * @return The renderingMode.
     */
    public int getRenderingMode()
    {
        return renderingMode;
    }

    /**
     * Set the value of the renderingMode.
     *
     * @param value The renderingMode.
     */
    public void setRenderingMode(int value)
    {
        renderingMode = value;
    }

    /**
     * Get the value of the rise.
     *
     * @return The rise.
     */
    public float getRise()
    {
        return rise;
    }

    /**
     * Set the value of the rise.
     *
     * @param value The rise.
     */
    public void setRise(float value)
    {
        rise = value;
    }

    /**
     * Get the value of the knockout.
     *
     * @return The knockout.
     */
    public boolean getKnockoutFlag()
    {
        return knockout;
    }

    /**
     * Set the value of the knockout.
     *
     * @param value The knockout.
     */
    public void setKnockoutFlag(boolean value)
    {
        knockout = value;
    }

    /**
     * @see Object#clone()
     */
    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch (CloneNotSupportedException ignore)
        {
            //ignore
        }
        return null;
    }
}
