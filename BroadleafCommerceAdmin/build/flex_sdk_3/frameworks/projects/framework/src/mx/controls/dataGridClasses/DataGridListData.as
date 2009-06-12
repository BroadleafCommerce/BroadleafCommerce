////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.dataGridClasses
{

import mx.controls.listClasses.BaseListData;
import mx.core.IUIComponent;

/**
 *  The DataGridListData class defines the data type of the <code>listData</code> property that is
 *  implemented by drop-in item renderers or drop-in item editors for the DataGrid control. 
 *  All drop-in item renderers and drop-in item editors must implement the 
 *  IDropInListItemRenderer interface, which defines the <code>listData</code> property.
 *
 *  <p>Although the properties of this class are writable, you should consider them to 
 *  be read-only. They are initialized by the DataGrid class, and read by an item renderer 
 *  or item editor. Changing these values can lead to unexpected results.</p>
 *
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class DataGridListData extends BaseListData
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param text Text representation of the item data.
	 *
	 *  @param dataField Name of the field or property 
	 *    in the data provider associated with the column.
	 *
	 *  @param columnIndex The column index of the item in the 
	 *    columns for the DataGrid control.
	 *
	 *  @param uid A unique identifier for the item.
	 *
	 *  @param owner A reference to the DataGrid control.
	 *
	 *  @param rowIndex The index of the item in the data provider
	 *  for the DataGrid control.
	 */
	public function DataGridListData(text:String, dataField:String,
									 columnIndex:int, uid:String,
									 owner:IUIComponent, rowIndex:int = 0)
	{
		super(text, uid, owner, rowIndex, columnIndex);

		this.dataField = dataField;
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  Name of the field or property in the data provider associated with the column. 
	 */
	public var dataField:String;

}

}
