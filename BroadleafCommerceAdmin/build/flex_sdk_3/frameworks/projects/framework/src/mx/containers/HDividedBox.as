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

package mx.containers
{

import mx.core.mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]

[Exclude(name="focusIn", kind="event")]
[Exclude(name="focusOut", kind="event")]

[Exclude(name="focusBlendMode", kind="style")]
[Exclude(name="focusSkin", kind="style")]
[Exclude(name="focusThickness", kind="style")]

[Exclude(name="focusInEffect", kind="effect")]
[Exclude(name="focusOutEffect", kind="effect")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("HDividedBox.png")]

/**
 *  The HDividedBox container lays out its children horizontally
 *  in the same way as HBox container, but it inserts
 *  a draggable divider in the gap between each child.
 *  A user can drag the divider to resize the area allotted to each child.
 *  The <code>&lt;mx:HDividedBox/&gt;</code> tag is the same as
 *  <code>&lt;mx:DividedBox direction="horizontal"/&gt;</code>.
 *  
 *  <p>An HDividedBox container has the following default sizing characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Width is large enough to hold all of its children at the 
 *               default or explicit widths of the children, plus any horizontal gap between the children, plus the left and 
 *               right padding of the container. Height is the default or explicit height of the tallest child 
 *               plus the top and bottom padding of the container.</td>
 *        </tr>
 *        <tr>
 *           <td>Default padding</td>
 *           <td>0 pixels for the top, bottom, left, and right values.</td>
 *        </tr>
 *        <tr>
 *           <td>Default gap</td>
 *           <td>10 pixels for the horizontal and vertical gaps.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *  
 *  <p>The <code>&lt;mx:HDividedBox&gt;</code> tag inherits all of the tag 
 *  attributes of its superclass except <code>direction</code> and adds 
 *  no new tag attributes.</p>
 *  
 *  @includeExample examples/HDividedBoxExample.mxml
 *
 *  @see mx.containers.DividedBox
 *  @see mx.containers.VDividedBox
 */
public class HDividedBox extends DividedBox
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
	public function HDividedBox()
	{
		super();
				
		mx_internal::layoutObject.direction = BoxDirection.HORIZONTAL;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  direction
	//----------------------------------
	
	[Inspectable(environment="none")]	

	/**
	 *  @private
	 *  Don't allow user to change the direction
	 */
	override public function set direction(value:String):void
	{
	}
}

}
