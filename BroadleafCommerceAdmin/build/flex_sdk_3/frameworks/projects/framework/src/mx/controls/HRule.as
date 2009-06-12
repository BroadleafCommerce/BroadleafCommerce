////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls
{

import flash.display.Graphics;
import mx.core.UIComponent;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  The shadow color of the line. 
 *  <ul>
 *    <li>If <code>strokeWidth</code> is 1, shadowColor
 *    does nothing.</li>
 *    <li>If <code>strokeWidth</code> is 2, shadowColor is
 *    the color of the bottom line.</li>
 *    <li>If <code>strokeWidth</code> is greater than 2, shadowColor
 *    is the color of the bottom and right edges of the rectangle.</li>
 *  </ul>
 * 
 *  @default 0xEEEEEE
 */
[Style(name="shadowColor", type="uint", format="Color", inherit="yes")]

/**
 *  The color of the line. 
 *  <ul>
 *    <li>If <code>strokeWidth</code> is 1, strokeColor is
 *    the color of the entire line.</li>
 *    <li>If <code>strokeWidth</code> is 2, strokeColor is 
 *    the color of the top line.</li>
 *    <li>If <code>strokeWidth</code> is greater than 2, strokeColor is
 *    the color of the top and left edges of the rectangle.</li>
 *  </ul> 
 *  
 *  @default 0xC4CCCC
 */
[Style(name="strokeColor", type="uint", format="Color", inherit="yes")]

/**
 *  The thickness of the rule in pixels.
 *  <ul>
 *    <li>If <code>strokeWidth</code> is 1,
 *    the rule is a 1-pixel-wide line.</li>
 *    <li>If <code>strokeWidth</code> is 2,
 *    the rule is two adjacent 1-pixel-wide horizontal lines.</li>
 *    <li>If <code>strokeWidth</code> is greater than 2,
 *    the rule is a hollow rectangle with 1-pixel-wide edges.</li>
 *  </ul>
 * 
 *  @default 2
 */
[Style(name="strokeWidth", type="Number", format="Length", inherit="yes")]
    // Note: stroke-width is inheriting in SVG,
    // although border-width is not inheriting in CSS

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("HRule.png")]

/**
 *  The HRule control creates a single horizontal line.
 *  You typically use this control to create a dividing line
 *  within a container.
 *
 *  <p>The HRule control has the following default properties:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>The default width is 100 pixels, default height is 2 pixels. By default, the HRule control is not resizable; set width and height to percentage values to enable resizing.</td>
 *        </tr>
 *        <tr>
 *           <td>strokeWidth</td>
 *           <td>2 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>strokeColor</td>
 *           <td>0xC4CCCC.</td>
 *        </tr>
 *        <tr>
 *           <td>shadowColor</td>
 *           <td>0xEEEEEE.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:HRule&gt;</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:HRule
 *    <strong>Styles</strong>
 *    strokeColor="0xC4CCCC"
 *    shadowColor="0xEEEEEE"
 *    strokeWidth="2"
 *  /&gt;
 *  </pre>
 *  
 *  @includeExample examples/SimpleHRule.mxml
 *
 *  @see mx.controls.VRule
 */
public class HRule extends UIComponent
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const DEFAULT_PREFERRED_WIDTH:Number = 100;

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function HRule()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function measure():void
    {
        super.measure();

        measuredWidth = DEFAULT_PREFERRED_WIDTH;
        measuredHeight = getStyle("strokeWidth");
    }
    
    /**
     *  @private
     *  The appearance of our horizontal rule is inspired by
     *  the leading browser's rendering of HTML's <HR>.
     *
     *  The only reliable way to draw the 1-pixel lines that are
     *  the borders of the horizontal rule is by filling rectangles!
     *  Otherwise, very short lines become antialised, probably because
     *  the Player is trying to render an endcap.
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        super.updateDisplayList(unscaledWidth, unscaledHeight);

        var g:Graphics = graphics;
        
        g.clear();

        // Look up the style properties
        var strokeColor:Number = getStyle("strokeColor");
        var shadowColor:Number = getStyle("shadowColor");
        var strokeWidth:Number = getStyle("strokeWidth");

        // The thickness of the stroke shouldn't be greater than
        // the unscaledHeight of the bounding rectangle.
        if (strokeWidth > unscaledHeight)
            strokeWidth = unscaledHeight;

        // The horizontal rule extends from the left edge
        // to the right edge of the bounding rectangle and
        // is vertically centered within the bounding rectangle.
        var left:Number = 0;
        var top:Number = (unscaledHeight - strokeWidth) / 2;
        var right:Number = unscaledWidth;
        var bottom:Number = top + strokeWidth;

        if (strokeWidth == 1)
        {
            // **************

            g.beginFill(strokeColor);
            g.drawRect(left, top, unscaledWidth, bottom-top);
            g.endFill();
        }
        else if (strokeWidth == 2)
        {
            // **************
            // oooooooooooooo

            g.beginFill(strokeColor);
            g.drawRect(left, top, unscaledWidth, 1);
            g.endFill();

            g.beginFill(shadowColor);
            g.drawRect(left, bottom - 1, unscaledWidth, 1);
            g.endFill();
        }
        else if (strokeWidth > 2)
        {
            // *************o
            // *            o
            // oooooooooooooo

            g.beginFill(strokeColor);
            g.drawRect(left, top, unscaledWidth - 1, 1);
            g.endFill();

            g.beginFill(shadowColor);
            g.drawRect(right - 1, top, 1, bottom - top - 1);
            g.drawRect(left, bottom - 1, unscaledWidth, 1);
            g.endFill();

            g.beginFill(strokeColor);
            g.drawRect(left, top + 1, 1, bottom - top - 2);
            g.endFill();
        }
    }
}

}
