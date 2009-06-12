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

import flash.text.TextLineMetrics;
import mx.core.FlexVersion;
import mx.core.mx_internal;

use namespace mx_internal;

//--------------------------------------
//  Styles
//--------------------------------------

/**
 *  Corner radius of the highlighted rectangle around a LinkButton.
 * 
 *  @default 4
 */
[Style(name="cornerRadius", type="Number", format="Length", inherit="no")]

/**
 *  Color of a LinkButton as a user moves the mouse pointer over it.
 * 
 *  @default 0xEEFEE6
 */
[Style(name="rollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Background color of a LinkButton as a user presses it.
 * 
 *  @default 0xB7F39B
 */
[Style(name="selectionColor", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of a LinkButton as a user moves the mouse pointer over it.
 * 
 *  @default 0x2B333C
 */
[Style(name="textRollOverColor", type="uint", format="Color", inherit="yes")]

/**
 *  Text color of a LinkButton as a user presses it.
 * 
 *  @default 0x2B333C
 */
[Style(name="textSelectedColor", type="uint", format="Color", inherit="yes")]

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="emphasized", kind="property")]

[Exclude(name="borderColor", kind="style")]
[Exclude(name="fillAlphas", kind="style")]
[Exclude(name="fillColors", kind="style")]
[Exclude(name="highlightAlphas", kind="style")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[AccessibilityClass(implementation="mx.accessibility.LinkButtonAccImpl")]

[IconFile("LinkButton.png")]

/**
 *  The LinkButton control is a borderless Button control
 *  whose contents are highlighted when a user moves the mouse over it.
 *  These traits are often exhibited by HTML links
 *  contained within a browser page.
 *  In order for the LinkButton control to perform some action,
 *  you must specify a <code>click</code> event handler,  
 *  as you do with a Button control.
 *
 *  <p>The LinkButton control has the following default characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Width and height large enough for the text</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>Undefined</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:LinkButton&gt;</code> tag inherits all of the tag attributes 
 *  of its superclass, and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:LinkButton
 *    <b>Styles</b>
 *    cornerRadius="4""
 *    rollOverColor="0xEEFEE6"
 *    selectionColor="0xB7F39B"
 *    textRollOverColor="0x2B333C"
 *    textSelectedColor="0x2B333C"
 *  /&gt;
 *  </pre>
 *
 *  @includeExample examples/LinkButtonExample.mxml
 * 
 *  @see mx.controls.LinkBar
 */
public class LinkButton extends Button
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class mixins
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Placeholder for mixin by LinkButtonAccImpl.
	 */
	mx_internal static var createAccessibilityImplementation:Function;

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function LinkButton()
	{
		super();

		// Sprite variables.
		buttonMode = true; // enables the hand cursor
		
		// Old Padding logic variables
		extraSpacing = 4;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  emphasized
	//----------------------------------

	[Inspectable(environment="none")]

	/**
	 *  @private
	 *  A LinkButton doesn't have an emphasized state, so _emphasized
	 *  is set false in the constructor and can't be changed via this setter.
	 */
    override public function set emphasized(value:Boolean):void
    {
    }

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: UIComponent
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Called by the initialize() method of UIComponent
	 *  to hook in the accessibility code.
	 */
	override protected function initializeAccessibility():void
	{
		if (createAccessibilityImplementation != null)
			createAccessibilityImplementation(this);
	}
	
	/**
	 *  @private
	 */
	override protected function measure():void
	{
		super.measure();

    	if (FlexVersion.compatibilityVersion < FlexVersion.VERSION_3_0)
		{
	
			var textWidth:Number = 8;
			var textHeight:Number = 8;
	
			if (label)
			{
				var lineMetrics:TextLineMetrics = measureText(label);
				textWidth += lineMetrics.width;
				textHeight += lineMetrics.height;
			}
	
			textWidth += (getStyle("paddingLeft") +
						  getStyle("paddingRight"));
	
			// Make sure any pending icon changes are committed before measuring.
			// commitProperties() should always be called before measure(),
			// but bug 103305 is a case where it isn't.
			//
			//removed icon and iconchanged in api scrub replaced with icon style
			//if (iconChanged)
			//	changeIcons();
	
			viewIcon();
			viewSkin();
	
			var iconWidth:Number = currentIcon ? currentIcon.width : 0;
			var iconHeight:Number = currentIcon ? currentIcon.height : 0;
	
			var w:Number = 0;
			var h:Number = 0;
	
			if (labelPlacement == ButtonLabelPlacement.LEFT ||
				labelPlacement == ButtonLabelPlacement.RIGHT)
			{
				if (label && label.length > 0)
					w = textWidth + iconWidth;
				else
					w = iconWidth;
				if (iconWidth != 0)
					w += getStyle("horizontalGap");
				h = Math.max(textHeight, iconHeight);
			}
			else
			{
				w = Math.max(textWidth, iconWidth);
				if (label && label.length > 0)
					h = textHeight + iconHeight;
				else
					h = iconHeight;
				if (iconHeight != 0)
					h += getStyle("verticalGap");
			}
	
			if (label && label != "")
				w += extraSpacing;
	
			w = Math.max(20, w);
			h = Math.max(14, h);
	
			measuredMinWidth = measuredWidth = w;
			measuredMinHeight = measuredHeight = h;
		}
	}

}

}
