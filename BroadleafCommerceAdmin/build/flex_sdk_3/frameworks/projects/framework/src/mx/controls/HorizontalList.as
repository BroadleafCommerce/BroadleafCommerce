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

package mx.controls
{

import mx.controls.listClasses.TileBase;
import mx.controls.listClasses.TileBaseDirection;
import mx.core.mx_internal;
import mx.core.ScrollPolicy;

use namespace mx_internal;

//--------------------------------------
//  Excluded APIs
//--------------------------------------

[Exclude(name="direction", kind="property")]
[Exclude(name="maxColumns", kind="property")]
[Exclude(name="maxRows", kind="property")]
[Exclude(name="variableRowHeight", kind="property")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="selectedItem", destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("HorizontalList.png")]

/**
 *  The HorizontalList control displays a horizontal list of items. 
 *  If there are more items than can be displayed at once, it
 *  can display a horizontal scroll bar so the user can access
 *  all items in the list.
 *
 *  <p>The HorizontalList control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Four columns, with size determined by the 
 *               cell dimensions.</td>
 *        </tr>
 *        <tr>
 *           <td>Minimum size</td>
 *           <td>0 pixels.</td>
 *        </tr>
 *        <tr>
 *           <td>Maximum size</td>
 *           <td>5000 by 5000.</td>
 *        </tr>
 *     </table>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:HorizontalList&gt;</code> tag inherits all of the 
 *  tag attributes of its superclass and it adds no new tag attributes.</p>
 *
 *  <pre>
 *  &lt;mx:HorizontalList/&gt
 *  </pre>
 *
 *  @includeExample examples/HorizontalListExample.mxml
 */
public class HorizontalList extends TileBase
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
	public function HorizontalList()
	{
		super();

		_horizontalScrollPolicy = ScrollPolicy.AUTO;
		_verticalScrollPolicy = ScrollPolicy.OFF;

		direction = TileBaseDirection.VERTICAL;
		maxRows = 1;
		defaultRowCount = 1;
	}
}

}
