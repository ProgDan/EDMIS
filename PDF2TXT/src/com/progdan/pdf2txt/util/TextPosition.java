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
package com.progdan.pdf2txt.util;

import com.progdan.pdf2txt.pdmodel.font.PDFont;

/**
 * This represents a character and a position on the screen of those characters.
 *
 * @author Ben Litchfield (ben@csh.rit.edu)
 * @version $Revision: 1.2 $
 */
public class TextPosition
{
    private float x;
    private float y;
    private float width;
    private float widthOfSpace;
    private String c;
    private PDFont font;
    private float fontSize;
    private float wordSpacing;

    /**
     * Constructor.
     *
     * @param xPos The x coordinate of the character.
     * @param yPos The y coordinate of the character.
     * @param widthValue The width of the character.
     * @param spaceWidth The width of the space character.
     * @param string The character to be displayed.
     * @param currentFont The current for for this text position.
     * @param fontSizeValue The new font size.
     * @param ws The word spacing parameter
     */
    public TextPosition(
        float xPos,
        float yPos,
        float widthValue,
        float spaceWidth,
        String string,
        PDFont currentFont,
        float fontSizeValue,
        float ws
        )
    {
        this.x = xPos;
        this.y = yPos;
        this.width = widthValue;
        this.widthOfSpace = spaceWidth;
        this.c = string;
        this.font = currentFont;
        this.fontSize = fontSizeValue;
        this.wordSpacing = ws;
    }

    /**
     * This will the character that will be displayed on the screen.
     *
     * @return The character on the screen.
     */
    public String getCharacter()
    {
        return c;
    }

    /**
     * This will get the x position of the character.
     *
     * @return The x coordinate of the character.
     */
    public float getX()
    {
        return x;
    }

    /**
     * This will get the y position of the character.
     *
     * @return The y coordinate of the character.
     */
    public float getY()
    {
        return y;
    }

    /**
     * This will get with width of this character.
     *
     * @return The width of this character.
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * This will get the font size that this object is
     * suppose to be drawn at.
     *
     * @return The font size.
     */
    public float getFontSize()
    {
        return fontSize;
    }

    /**
     * This will get the font for the text being drawn.
     *
     * @return The font size.
     */
    public PDFont getFont()
    {
        return font;
    }

    /**
     * This will get the current word spacing.
     *
     * @return The current word spacing.
     */
    public float getWordSpacing()
    {
        return wordSpacing;
    }

    /**
     * This will get the width of a space character.  This is useful for some
     * algorithms such as the text stripper, that need to know the width of a
     * space character.
     *
     * @return The width of a space character.
     */
    public float getWidthOfSpace()
    {
        return widthOfSpace;
    }
}
