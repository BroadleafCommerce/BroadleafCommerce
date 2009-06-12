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

/**
 *  The interface for "drop-in" item renderers.  Most IListItemRenderers
 *  are not "drop-ins".  They are expecting to use a particular field of
 *  the data provider item.  For example, they may assign the "lastName"
 *  property of the item to a Label's <code>text</code> property.  This
 *  is easy to write using data-binding, but has the negative 
 *  consequence that the renderer cannot be re-used in another column
 *  of a DataGrid or another List with different fields.
 *  IDropInListItemRenderer allows a renderer to be re-used.  The list
 *  classes will pass more information to the renderer so that it
 *  can determine which field to use at run-time.
 *
 *  <p>Components that you want to use as drop-in item renderers or drop-in
 *  item editors  must implement the IDropInListItemRenderer interface. 
 *  Many Flex component implement this interface, and therefore are usable
 *  as drop-in item renderers and drop-in item editors in any column or
 *  list.</p>
 *
 *  <p>Drop-in item renderers or drop-in item editors also must implement
 *  the IDataRenderer interface to define the <code>data</code> property.</p> 
 *
 *  <p>When a component is used as a drop-in item renderer or drop-in
 *  item editor, Flex initializes the <code>listData</code> property
 *  of the component with the appropriate data from the list control.
 *  The component can then use the <code>listData</code> property
 *  to initialize the <code>data</code> property of the drop-in
 *  item renderer or drop-in item editor.</p>
 *
 *  <p>The <code>listData</code> property is of type BaseListData, 
 *  where the BaseListData class has four subclasses:
 *  DataGridListData, ListData, TreeListData, and MenuListData. 
 *  The actual data type of the value of the <code>listData</code> property 
 *  depends on the control using the drop-in item renderer or item editor. 
 *  For a DataGrid control, the value is of type DataGridListData, 
 *  for a List control the value is of type ListData,
 *  for a Tree control, the value is of type TreeListData, 
 *  and for a Menu control, the value is of type MenuListData..</p>
 *
 *  <p>The following example shows the setter method for the
 *  <code>data</code> property for the NumericStepper control
 *  that initializes NumericStepper's <code>value</code> property
 *  based on the value of the <code>listData</code> property:</p>
 *
 *  <pre>
 *    public function set data(value:Object):void
 *    {
 *      _data = value;
 *    
 *      this.value = _listData ? parseFloat(_listData.label) : Number(_data);
 *    
 *      dispatchEvent(new FlexEvent(FlexEvent.DATA_CHANGE));
 *    }
 *  </pre>
 *
 *  <p>In the example above, the NumericStepper control ignores the
 *  <code>data</code> property  when setting NumericStepper's
 *  <code>value</code> property, and uses the <code>listData</code>
 *  property instead.</p>
 *
 *  <p>To implement the IDropInListItemRenderer interface,
 *  you define a setter and getter method to implement
 *  the <code>listData</code> property.
 *  Typically, the setter method writes the value of the
 *  <code>listData</code> property to an internal variable.
 *  The list class always assigns this property then sets
 *  the data provider item in the <code>data</code> property.</p>
 *
 *  <p>Notice that the setter method for the <code>listData</code> property 
 *  does not dispatch an event. 
 *  This is because the list classes always set <code>listData</code>, 
 *  then set the <code>data</code> property. 
 *  Setting the <code>data</code> property also dispatches the <code>dataChange</code> event. 
 *  You never set <code>listData</code> on its own, 
 *  so it does not need to dispatch its own event. </p>
 *
 *  <p>The <code>data</code> setter method could call the <code>invalidateProperties()</code> method 
 *  if it did something that required the control to update itself. 
 *  It would then be up to the component developer to write a <code>commitProperties()</code> method 
 *  to determine that <code>listData</code> was modified, and handle it accordingly. </p>
 * 
 *  <p>The getter method returns the current value
 *  of the internal variable,  as the following example shows:</p>
 *  
 *  <pre>
 *    // Internal variable for the property value.
 *    private var _listData:BaseListData;
 *    
 *    // Make the listData property bindable.
 *    [Bindable("dataChange")]
 *    
 *    // Define the getter method.
 *    public function get listData():BaseListData
 *    {
 *      return _listData;
 *    }
 *    
 *    // Define the setter method,
 *    public function set listData(value:BaseListData):void
 *    {
 *      _listData = value;
 *    }
 *  </pre>
 *
 *  @see mx.controls.listClasses.BaseListData
 *  @see mx.core.IDataRenderer
 */
public interface IDropInListItemRenderer
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  listData
    //----------------------------------

    /**
     *  Implements the <code>listData</code> property
     *  using setter and getter methods. 
     */
    function get listData():BaseListData;
    
    /**
     *  @private
     */
    function set listData(value:BaseListData):void;
}

}
