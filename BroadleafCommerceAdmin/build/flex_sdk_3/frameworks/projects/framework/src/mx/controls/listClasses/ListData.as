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

package mx.controls.listClasses
{

import mx.core.IUIComponent;

/**
 *  The ListData class defines the data type of the <code>listData</code>
 *  property implemented by drop-in item renderers or drop-in item editors
 *  for the List control. 
 *  All drop-in item renderers and drop-in item editors must implement 
 *  the IDropInListItemRenderer interface, which defines
 *  the <code>listData</code> property.
 *
 *  <p>While the properties of this class are writable,
 *  you should consider them to be read only.
 *  They are initialized by the List class,
 *  and read by an item renderer or item editor.
 *  Changing these values can lead to unexpected results.</p>
 *
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */
public class ListData extends BaseListData
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
	 *  @param icon A Class or String object representing the icon 
	 *  for the item in the List control.
	 *
	 *  @param labelField The name of the field of the data provider 
	 *  containing the label data of the List component.
	 *
	 *  @param uid A unique identifier for the item.
	 *
	 *  @param owner A reference to the List control.
	 *
	 *  @param rowIndex The index of the item in the data provider
	 *  for the List control.
	 * 
	 *  @param columnIndex The index of the column in the currently visible columns of the 
     *  control.
	 *
	 */
	public function ListData(text:String, icon:Class, labelField:String,
							 uid:String, owner:IUIComponent, rowIndex:int = 0,
							 columnIndex:int = 0)
	{
		super(text, uid, owner, rowIndex, columnIndex);
		
		this.icon = icon;
		this.labelField = labelField;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  icon
	//----------------------------------

	/**
	 *  A Class representing the icon for the item in the List control computed
	 *  from the list class's <code>itemToIcon()</code> method
	 */
	public var icon:Class;

	//----------------------------------
	//  labelField
	//----------------------------------

	/**
	 *  The value of the <code>labelField</code> property in the list class.
	 *  This is the value normally used to calculate which property should
	 *  be taken from the item in the data provider for the text displayed
	 *  in the item renderer, but is also used by DateField and other
	 *  components to indicate which field to take from the data provider item
	 *  that contains a Date or other non-text property.
	 *
	 *  <p>For example, if a data provider item contains a "hiredDate" property,
	 *  the <code>labelField</code> property can be set to "hiredDate" 
	 *  and the <code>itemRenderer</code> property 
	 *  can be set to DateField. The DateField control then uses the hiredDate
	 *  property.</p>
	 */
	public var labelField:String;
}

}
