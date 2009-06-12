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
import mx.core.IUID;

/**
 *  The BaseListData class defines the data type of the <code>listData</code>
 *  property implemented by drop-in item renderers or drop-in item editors. 
 *  All drop-in item renderers and drop-in item editors must implement the 
 *  IDropInListItemRenderer interface, which defines the <code>listData</code>
 *  property.
 *
 *  <p>The <code>listData</code> property is of type BaseListData, 
 *  where the BaseListData class has three subclasses:
 *  DataGridListData, ListData, and TreeListData. 
 *  The actual data type of the value of the <code>listData</code> property 
 *  depends on the control using the drop-in item renderer or item editor. 
 *  For a DataGrid control, the value is of type DataGridListData, 
 *  for a List control the value is of type ListData,
 *  and for a Tree control, the value is of type TreeListData.</p>
 *
 *  <p>When used as a drop-in item renderer or drop-in item editor,
 *  Flex sets the <code>listData</code> property to a BaseListData-derived
 *  class containing information computed about the item in the data provider 
 *  containing the data for the item.</p>
 *
 *  <p>While the properties of this class are writable,
 *  you should consider them to be read only.
 *  They are initialized by the list class,
 *  and read by an item renderer or item editor.
 *  Changing these values can lead to unexpected results.</p>
 *
 *  @see mx.controls.listClasses.IDropInListItemRenderer
 */

public class BaseListData
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
     *  @param label The textual representation of the item data.
     *
     *  @param uid A unique identifier.
     *
     *  @param owner A reference to the list control.
     *
     *  @param rowIndex The index of the row in the currently visible rows of the control.
     * 
     *  @param columnIndex The index of the column in the currently visible columns of the 
     *  control.
     */
    public function BaseListData(label:String, uid:String,
                                 owner:IUIComponent, rowIndex:int = 0,
                                 columnIndex:int = 0)
    {
        super();

        this.label = label;
        this.uid = uid;
        this.owner = owner;
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }


    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
    //  columnIndex
    //----------------------------------

    /**
     *  The index of the column of the List-based control relative 
     *  to the currently visible columns of the control, where the first column 
     *  is at an index of 1. 
     */
    public var columnIndex:int;

    //----------------------------------
    //  label
    //----------------------------------

	[Bindable("dataChange")]

    /**
     *  The textual representation of the item data, based on the list class's
     *  <code>itemToLabel()</code> method.
     */
    public var label:String;
    
    //----------------------------------
    //  owner
    //----------------------------------

    /**
     *  A reference to the list object that owns this item.
     *  This should be a ListBase-derived class.
     *  This property is typed as IUIComponent so that drop-ins
     *  like Label and TextInput don't have to have dependencies
     *  on List and all of its dependencies.
     */
    public var owner:IUIComponent;

    //----------------------------------
    //  rowIndex
    //----------------------------------

    /**
     *  The index of the row of the DataGrid, List, or Tree control relative 
     *  to the currently visible rows of the control, where the first row 
     *  is at an index of 1. 
     *  For example, you click on an item in the control and <code>rowIndex</code> 
     *  is set to 3. 
     *  You then scroll the control to change the row's position in the visible rows 
     *  of the control, and then click on the same row as before. 
     *  The <code>rowIndex</code> now contains a different value corresponding to 
     *  the new index of the row in the currently visible rows.
     */
    public var rowIndex:int;

    //----------------------------------
    //  uid
    //----------------------------------

    /**
     *  @private
     *  Storage for the uid property.
     */
    private var _uid:String;

    /**
     *  The unique identifier for this item.
     */
    public function get uid():String
    {
        return _uid;
    }
    
    /**
     *  @private
     */
    public function set uid(value:String):void
    {
        _uid = value;
    }
}

}
