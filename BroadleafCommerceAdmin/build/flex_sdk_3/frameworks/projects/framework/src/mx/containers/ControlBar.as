////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.containers
{

import mx.core.Container;
import mx.core.ScrollPolicy;
import mx.core.mx_internal;
import mx.core.UIComponent;
import mx.styles.IStyleClient;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]
[Exclude(name="horizontalScrollPolicy", kind="property")]
[Exclude(name="verticalScrollPolicy", kind="property")]

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="backgroundColor", kind="style")]
[Exclude(name="borderColor", kind="style")]
[Exclude(name="borderSides", kind="style")]
[Exclude(name="borderStyle", kind="style")]
[Exclude(name="borderThickness", kind="style")]
[Exclude(name="dropShadowColor", kind="style")]
[Exclude(name="dropShadowEnabled", kind="style")]
[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]
[Exclude(name="shadowDirection", kind="style")]
[Exclude(name="shadowDistance", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("ControlBar.png")]

/**
 *  The ControlBar container lets you place controls
 *  at the bottom of a Panel or TitleWindow container.
 *  The <code>&lt;mx:ControlBar&gt;</code> tag must be the last child tag
 *  of the enclosing tag for the Panel or TitleWindow container.
 *
 *  <p>The ControlBar is a Box with a background
 *  and default style properties.</p>
 *
 *  <p>A ControlBar container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Height is the default or explicit height of the tallest child, plus the top and bottom padding of the container. 
 *               Width is large enough to hold all of its children at the default or explicit width of the children, 
 *               plus any horizontal gap between the children, plus the left and right padding of the container.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>10 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:ControlBar&gt;</code> tag inherits all the tag
 *  attributes but adds no additional attributes:</p>
 *
 *  <pre>
 *  &lt;mx:ControlBar&gt;
 *    ...
 *      <i>child tags</i>
 *    ...
 *  &lt;/mx:ControlBar&gt;
 *  </pre>
 *
 *  @includeExample examples/SimpleControlBarExample.mxml
 */
public class ControlBar extends Box
{
	include "../core/Version.as";
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function ControlBar()
	{
		super();

        // ControlBar defaults to direction="horizontal", but can be changed
		// later if desired
		direction = BoxDirection.HORIZONTAL;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  enabled
	//----------------------------------

    [Inspectable(category="General", enumeration="true,false", defaultValue="true")]

	/**
	 *  @private
	 */
	override public function set enabled(value:Boolean):void
	{
		if (value != super.enabled)
		{
			super.enabled = value;
			
			// Since ControlBar typically has a transparent background and sits on top
			// of a translucent part of the panel, we don't want the default overlay.
			// Instead of the overlay, set the alpha value here.
			alpha = value ? 1 : 0.4;
		}
	}

	//----------------------------------
	//  horizontalScrollPolicy
	//----------------------------------

	[Inspectable(environment="none")]

	/**
	 *  @private
	 */
	override public function get horizontalScrollPolicy():String
	{
		return ScrollPolicy.OFF;
	}

	/**
	 *  @private
	 */
	override public function set horizontalScrollPolicy(value:String):void
	{
		// A ControlBar never scrolls.
		// Its horizontalScrollPolicy is initialized to "off" and can't be changed.
	}

	//----------------------------------
	//  includeInLayout
	//----------------------------------

    [Inspectable(category="General", defaultValue="true")]

	/**
	 *  @private
	 */
	override public function set includeInLayout(value:Boolean):void
	{
		if (includeInLayout != value)
		{
			super.includeInLayout = value;

			var p:Container = parent as Container;
			if (p)
				p.invalidateViewMetricsAndPadding();
		}
	}

	//----------------------------------
	//  verticalScrollPolicy
	//----------------------------------

	[Inspectable(environment="none")]

	/**
	 *  @private
	 */
	override public function get verticalScrollPolicy():String
	{
		return ScrollPolicy.OFF;
	}

	/**
	 *  @private
	 */
	override public function set verticalScrollPolicy(value:String):void
	{
		// A ControlBar never scrolls.
		// Its verticalScrollPolicy is initialized to "off" and can't be changed.
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function invalidateSize():void
	{
		super.invalidateSize();

		// Since controlbar isn't a "child" of Panel, we need to call
		// invalidateViewMetricsAndPadding() here when our size is invalidated.
		// This causes our parent Panel to adjust size.
		if (parent)
			Container(parent).invalidateViewMetricsAndPadding();
	}

	/**
	 *  @private
	 */
	override protected function updateDisplayList(unscaledWidth:Number,
												  unscaledHeight:Number):void
	{
		super.updateDisplayList(unscaledWidth, unscaledHeight);

		// Make sure we don't have an opaque background for the ControlBar,
		// otherwise the background turns white.
		if (contentPane)
			contentPane.opaqueBackground = null;
	}
}

}
