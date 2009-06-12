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

import mx.core.mx_internal;
import mx.core.UITextField;
import mx.events.FlexEvent;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

include "../styles/metadata/LeadingStyle.as"


//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="horizontalAlign", kind="style")]
[Exclude(name="verticalAlign", kind="style")]

//--------------------------------------
//  Other metadata
//-------------------------------------- 

[DefaultBindingProperty(source="text",destination="text")]

[IconFile("Text.png")]

/**
 *  The Text control displays multiline, noneditable text.
 *  Use the Label control if you need only a single line of text.
 *
 *  <p>The Text control does not support scroll bars.
 *  If you need scrolling, you should use a non-editable TextArea control.</p>
 *
 *  <p>You can format the text in a Text control using HTML tags,
 *  which are applied after the control's CSS styles are applied.
 *  You can also put padding around the four sides of the text.</p>
 *
 *  <p>The text in a Text control is selectable by default,
 *  but you can make it unselectable by setting the <code>selectable</code>
 *  property to <code>false</code>.</p>
 *
 *  <p>If the control is not as wide as the text, the text will wordwrap.
 *  The text is always aligned top-left in the control.</p>
 *
 *  <p>To size a Text component, it's common to specify an explicit width
 *  and let Flex determine the height as required to display all the text.
 *  If you specify an explicit height, some of the text may get clipped;
 *  unlike Label, Text does not truncate its text with "...".
 *  It's also common to use percentage widths and heights with Text.</p>
 * 
 *  <p>If you leave both the width and the height unspecified,
 *  Flex calculates them based on any explicit line breaks
 *  in the text, with no wordwrapping within lines.
 *  For example, if you set the <code>text</code> property,
 *  the newline character <code>"\n"</code> causes a line break.
 *  If you set the <code>htmlText</code> property, the HTML markup
 *  <code>&lt;br&gt;</code> causes a line break.
 *  If your <code>text</code> or <code>htmlText</code> is lengthy
 *  and doesn't contain line breaks, you can get a very wide Text
 *  component; you can set the <code>maxWidth</code> to limit
 *  how wide the component is allowed to grow.</p>
 *
 *  <p>Text controls do not have backgrounds or borders
 *  and cannot take focus.</p>
 *
 *  <p>The Text control has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Flex sizes the control to fit the text, with the width long enough to fit the longest line of text and height tall enough to fit the number of lines. If you do not specify a pixel width, the height is determined by the number of explicit line breaks in the text string. If the text length changes, the control resizes to fit the new text.</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>ChMaximum sizear03</td>
 *           <td>10000 by 10000 pixels</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:Text&gt;</code> tag inherits all of the tag attributes
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:Text
 *  leading="2"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/TextExample.mxml
 *
 *  @see mx.controls.Label
 *  @see mx.controls.TextInput
 *  @see mx.controls.TextArea
 *  @see mx.controls.RichTextEditor
 */
public class Text extends Label
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Implementation notes
    //
    //--------------------------------------------------------------------------

    /*
        Like any component, a Text component's width can be determined
        by an explicitWidth, a percentWidth, or a measuredWidth,
        and similarly for its height.

        Therefore there are nine measurement/layout cases.
        Eight of them are easily handled by the standard LayoutManager
        sequence of calling measure() and then updateDisplayList().

        However, when a percentage width but no height is specified,
        the situation is more complicated.
        The problem is that in measure() we need to compute the measuredHeight
        by wordwrapping the text within some appropriate width, but we don't
        know what that width (some percentage of the parent's available width)
        is until updateDisplayList() has passed it as unscaledWidth.

        The same problem occurs when left and right constraints are specified
        (and therefore the actual width depends on the parent's width)
        but we need to compute a measuredHeight.

        Therefore in these two cases we have to make a second measurement/layout
        pass by calling invalidateSize() and invalidateDisplayList()
        inside updateDisplayList(), which will cause measure() and
        updateDisplayList() to be called a second time.

    */

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function Text()
    {
        super();

        selectable = true;
        truncateToFit = false;

        addEventListener(FlexEvent.UPDATE_COMPLETE, updateCompleteHandler);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var lastUnscaledWidth:Number = NaN;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
	private var widthChanged:Boolean = true;
	
    /**
     *  @private
     */
    override public function set explicitWidth(value:Number):void
    {
    	// Due to player bugs relating to scaling text, we
    	// have to be careful to set wordWrap appropriately
    	// (which we do in commitProperties).
    	// Also have to re-measure when width changes, because width
    	// can affect height.
    	if (value != explicitWidth)
    	{
    		widthChanged = true;
   			invalidateProperties();
    		invalidateSize();
    	}
    	super.explicitWidth = value;
    }

    /**
     *  @private
     */
    override public function set percentWidth(value:Number):void
    {
    	// Due to player bugs relating to scaling text, we
    	// have to be careful to set wordWrap appropriately
    	// (which we do in commitProperties).
    	// Also have to re-measure when width changes, because width
    	// can affect height.
    	if (value != percentWidth)
    	{
    		widthChanged = true;
 			invalidateProperties();
    		invalidateSize();
    	}
    	super.percentWidth = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function childrenCreated():void
    {
        super.childrenCreated();
        textField.wordWrap = true;
        textField.multiline = true;
        textField.mouseWheelEnabled = false;

    }

	override protected function commitProperties():void
	{
		super.commitProperties();
		// if explicitWidth or percentWidth changed, we want to set
		// wordWrap appropriately before measuring()
		if (widthChanged)
		{
			textField.wordWrap = !isNaN(percentWidth) || !isNaN(explicitWidth);
			widthChanged = false;
		}
	}

    /**
     *  @private
     *
     *  If the Text component has an explicit width,
     *  its text wordwraps within that width,
     *  and the measured height is tall enough to display all the text.
     *  (If there is an explicit height or a percent height in this case,
     *  the text may get clipped.)
     *
     *  If it doesn't have an explicit height,
     *  the measured height is tall enough to display all the text.
     *  If there is an explicit height or a percent height,
     *  the text may get clipped.
     *
     *  If the Text doesn't have an explicit width,
     *  the measured width is based on explicit line breaks
     *  (e.g, \n, &lt;br&gt;, etc.).
     *  For example, if the text is
     *
     *      The question of the day is:
     *      What is the right algorithm for Text?
     *
     *  with a line break between the two lines, then the measured width
     *  will be wide enough to see all of the second line,
     *  and the measured height will be tall enough for two lines.
     *
     *  For lengthy text without explicit line breaks,
     *  this will produce unusably wide layout.
     */
    override protected function measure():void
    {
        if (isSpecialCase())
        {
            if (!isNaN(lastUnscaledWidth))
            {
                measureUsingWidth(lastUnscaledWidth);
            }
            else
            {
                // We're not ready to measure yet.
                // We need updateDisplayList() to first tell us the unscaledWidth
                // that has been calculated from the percentWidth.
                measuredWidth = 0;
                measuredHeight = 0;
            }
            return;
        }

        measureUsingWidth(explicitWidth);
        
    }
    
    /**
     *  @private
     */
    override protected function updateDisplayList(unscaledWidth:Number,
                                                  unscaledHeight:Number):void
    {
        // Don't call super.updateDisplayList();
        // Text has a different layout algorithm than Label.
        if (isSpecialCase())
        {
            var firstTime:Boolean = isNaN(lastUnscaledWidth) || lastUnscaledWidth != unscaledWidth;
            lastUnscaledWidth = unscaledWidth;
            if (firstTime)
            {
                invalidateSize();
                return;
            }
        }

        // The textField occupies the entire Text bounds minus the padding.
        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingBottom:Number = getStyle("paddingBottom");
        textField.setActualSize(unscaledWidth - paddingLeft - paddingRight,
                                unscaledHeight - paddingTop - paddingBottom);

        textField.x = paddingLeft;
        textField.y = paddingTop;
        // Although we also set wordWrap in commitProperties(), we do 
        // this here to handle width being set through setActualSize().
        if (Math.floor(width) < Math.floor(measuredWidth))
			textField.wordWrap =  true;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  The cases that requires a second pass through the LayoutManager
     *  are <mx:Text width="N%"/> (the control is to use the percentWidth
     *  but the measuredHeight) and <mx:Text left="N" right="N"/>
     *  (the control is to use the parent's width minus the constraints
     *  but the measuredHeight).
     */
    private function isSpecialCase():Boolean
    {
        var left:Number = getStyle("left");
        var right:Number = getStyle("right");
        
        return (!isNaN(percentWidth) || (!isNaN(left) && !isNaN(right))) &&
               isNaN(explicitHeight) &&
               isNaN(percentHeight);
    }

    /**
     *  @private
     */
    private function measureUsingWidth(w:Number):void
    {
        /*
        var t:String = isHTML ? explicitHTMLText : text; 

        // If the text is null, empty, or a single character,
        // make the measured size big enough to hold
        // a capital and decending character using the current font.
        if (!t || t.length < 2)
            t = "Wj";
        */

        // Don't call super.measure();
        // Text has a different measurement algorithm than Label.

        var paddingLeft:Number = getStyle("paddingLeft");
        var paddingTop:Number = getStyle("paddingTop");
        var paddingRight:Number = getStyle("paddingRight");
        var paddingBottom:Number = getStyle("paddingBottom");
        
        // Ensure that the proper CSS styles get applied
        // to the textField before measuring text.
        // Otherwise the callLater(validateNow) in UITextField's
        // styleChanged() method can apply the CSS styles too late.
        textField.validateNow();
        
        textField.autoSize = "left";

        // If we know what width to use for word wrapping,
        // determine the height by wordwrapping to that width.
        if (!isNaN(w))
        {
            textField.width = w - paddingLeft - paddingRight;

            measuredWidth = Math.ceil(textField.textWidth) + UITextField.TEXT_WIDTH_PADDING;
            measuredHeight = Math.ceil(textField.textHeight) + UITextField.TEXT_HEIGHT_PADDING;
            // Round up because embedded fonts can produce fractional values;
            // if a parent container rounds a component's actual width or height
            // down, the component may not be wide enough to display the text.
        }

        // If we don't know what width to use for word wrapping,
        // determine the measured width and height
        // from the explicit line breaks such as "\n" and "<br>".
        else
        {
        	var oldWordWrap:Boolean = textField.wordWrap;
            textField.wordWrap = false;
            
            measuredWidth = Math.ceil(textField.textWidth) + UITextField.TEXT_WIDTH_PADDING;
            measuredHeight = Math.ceil(textField.textHeight) + UITextField.TEXT_HEIGHT_PADDING;
            // Round up because embedded fonts can produce fractional values;
            // if a parent container rounds a component's actual width or height
            // down, the component may not be wide enough to display the text.
            
            textField.wordWrap = oldWordWrap;
        }
        
        textField.autoSize = "none";

        // Add in the padding.
        measuredWidth += paddingLeft + paddingRight;
        measuredHeight += paddingTop + paddingBottom;

        if (isNaN(explicitWidth))
        {
            // it could be any size
            measuredMinWidth = DEFAULT_MEASURED_MIN_WIDTH;
            measuredMinHeight = DEFAULT_MEASURED_MIN_HEIGHT;
        }
        else
        {
            // lock in the content area
            measuredMinWidth = measuredWidth;
            measuredMinHeight = measuredHeight;
        }

    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function updateCompleteHandler(event:FlexEvent):void
    {
        lastUnscaledWidth = NaN;
    }
}

}
