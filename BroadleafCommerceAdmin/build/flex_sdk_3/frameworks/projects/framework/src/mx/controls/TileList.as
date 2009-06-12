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
import mx.controls.listClasses.TileListItemRenderer;
import mx.core.ClassFactory;

//--------------------------------------
//  Other metadata
//--------------------------------------

[DefaultBindingProperty(source="selectedItem", destination="dataProvider")]

[DefaultProperty("dataProvider")]

[DefaultTriggerEvent("change")]

[IconFile("TileList.png")]

//--------------------------------------
//  Effects
//--------------------------------------

/**
 *  The data effect to play when a change occur to the control's data provider.
 *
 *  <p>By default, the TileList control does not use a data effect. 
 *  For the TileList control, use an instance of the DefaultTileListEffect class.</p>
 *
 * @default undefined
 */
[Effect(name="itemsChangeEffect", event="itemsChange")]

/**
 *  The TileList control displays a number of items laid out in tiles.
 *  It displays a scroll bar on one of its axes to access all items
 *  in the list, depending on the <code>direction</code> property.
 *  You can set the size of the tiles by using <code>rowHeight</code>
 *  and <code>columnWidth</code> properties.
 *  Alternatively, Flex measures the item renderer for the first item
 *  in the dataProvider and uses that size for all tiles.
 *  
 *  <p>The TileList control has the following default sizing 
 *     characteristics:</p>
 *     <table class="innertable">
 *        <tr>
 *           <th>Characteristic</th>
 *           <th>Description</th>
 *        </tr>
 *        <tr>
 *           <td>Default size</td>
 *           <td>Four columns and four rows. Using the default item 
 *               renderer, each cell is 50 by 50 pixels, and the total 
 *               size is 200 by 200 pixels.</td>
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
 *  <p>The <code>&lt;mx:TileList&gt;</code> tag inherits
 *  all of the tag attributes of its superclass, but ignores the
 *  <code>variableRowHeight</code> and <code>wordWrap</code> tag attributes.  
 *  It adds no additional tag attributes.</p>
 *  
 *  <pre>
 *  &lt;mx:TileList/&gt;
 *  </pre>
 *
 *  @includeExample examples/TileListExample.mxml
 */
public class TileList extends TileBase
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
	public function TileList()
	{
		super();

		itemRenderer = new ClassFactory(TileListItemRenderer);
	}
}

}
