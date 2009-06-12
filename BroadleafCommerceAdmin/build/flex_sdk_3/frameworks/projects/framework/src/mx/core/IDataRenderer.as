////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The IDataRenderer interface defines the interface for components that have a <code>data</code> property.
 *
 *  <p>Components that are used in an item renderer or item editor
 *  in a list control (such as the List, HorizontalList, TileList, DataGrid,
 *  and Tree controls), or as renderers in a chart are passed the data
 *  to render or edit by using the <code>data</code> property.
 *  The component must implement IDataRenderer so that the host components
 *  can pass this information.
 *  All Flex containers and many Flex components implement IDataRenderer and 
 *  the <code>data</code> property.</p>
 *
 *  <p>In a list control, Flex sets the <code>data</code> property
 *  of an item renderer or item editor to the element in the data provider
 *  that corresponds to the item being rendered or edited. 
 *  For a DataGrid control, the <code>data</code> property 
 *  contains the data provider element for the entire row of the DataGrid
 *  control, not just for the item.</p>
 *
 *  <p>To implement this interface, you define a setter and getter method
 *  to implement the <code>data</code> property.
 *  Typically, the setter method writes the value of the <code>data</code>
 *  property to an internal variable and dispatches a <code>dataChange</code>
 *  event, and the getter method returns the current value of the internal
 *  variable, as the following example shows:</p>
 *  
 *  <pre>
 *    // Internal variable for the property value.
 *    private var _data:Object;
 *    
 *    // Make the data property bindable.
 *    [Bindable("dataChange")]
 *    
 *    // Define the getter method.
 *    public function get data():Object {
 *        return _data;
 *    }
 *    
 *    // Define the setter method, and dispatch an event when the property
 *    // changes to support data binding.
 *    public function set data(value:Object):void {
 *        _data = value;
 *    
 *        dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
 *    }
 *  </pre>
 */
public interface IDataRenderer
{
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  data
	//----------------------------------

	/**
	 *  The data to render or edit.
	 */
	function get data():Object;
	
	/**
	 *  @private
	 */
	function set data(value:Object):void;
}

}
