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

package mx.controls.listClasses
{

import flash.events.IEventDispatcher;
import mx.core.IDataRenderer;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.managers.ILayoutManagerClient;
import mx.styles.ISimpleStyleClient;

/**
 *  Item renderers and item editors for list components must implement 
 *  the IListItemRenderer interface.
 *  The IListItemRenderer interface is a set of several other interfaces. 
 *  It does not define any new class methods or properties. 
 *
 *  <p>The set of interfaces includes the following:
 *  IDataRenderer, IFlexDisplayObject, ILayoutManagerClient,
 *  ISimpleStyleClient, IUIComponent.
 *  The UIComponent class implements all of these interfaces,
 *  except the IDataRenderer interface. 
 *  Therefore, if you create a custom item renderer or item editor
 *  as a subclass  of the UIComponent class, you only have to implement
 *  the IDataRenderer interface and then you can add to the class
 *  definition that the class implements IDataRenderer and IListItemRenderer.</p>
 *
 *	<p>IListItemRenderers are generally dedicated to displaying a particular
 *  field from the data provider item and cannot be re-used in other
 *  DataGrid columns or in other lists with different fields.  If you want
 *  to create a renderer that can be re-used you can also implement
 *  IDropInListItemRenderer, and the list will pass more data to
 *  the renderer that allows the renderer to be re-used with different
 *  data fields.</p>
 *
 *  <p>Item renderers and item editors are passed data from a list class'
 *  data provider using the IDataRenderer interface.
 *  Renderers and editors that implement the IDropInListItemRenderer
 *  interface get other information from the list class.
 *  The item renderer or item editor uses one or both pieces of information
 *  to display the data.</p>
 *
 *  <p>The renderers and editors are often recycled.
 *  Once they are created, they may be used again simply by being given
 *  a new data and optional <code>listData</code> property.
 *  Therefore in your implementation you must make sure that component
 *  properties are not assumed to contain their initial, or default values.</p>
 */
public interface IListItemRenderer extends IDataRenderer, IEventDispatcher,
										   IFlexDisplayObject,
										   ILayoutManagerClient,
										   ISimpleStyleClient, IUIComponent
{
}

}
