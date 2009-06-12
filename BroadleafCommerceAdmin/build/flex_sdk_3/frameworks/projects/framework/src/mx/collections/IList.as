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

package mx.collections 
{
    
import flash.events.IEventDispatcher;
import mx.events.CollectionEvent;

/**
 *  Dispatched when the IList has been updated in some way.
 *
 *  @eventType mx.events.CollectionEvent.COLLECTION_CHANGE
 */
[Event(name="collectionChange", type="mx.events.CollectionEvent")]

/**
 *  A collection of items organized in an ordinal fashion.  
 *  Provides access and manipulation methods based on index.  
 *  
 *  <p>An <code>IList</code> may be a view onto data
 *  that has been retrieved from a  remote location.  
 *  When writing for a collection that may be remote,
 *  it is important to handle the case where data
 *  may not yet be available, which is indicated
 *  by the  <code>ItemPendingError</code>.</p>
 *  
 *  <p>The <code>ICollectionView</code> is an alternative
 *  to the <code>IList</code>.</p>
 *
 *  @see mx.collections.errors.ItemPendingError
 *  @see mx.collections.ICollectionView
 *  @see mx.collections.ListCollectionView
 */
public interface IList extends IEventDispatcher
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  length
    //----------------------------------

    /**
     *  The number of items in this collection. 
     *  0 means no items while -1 means the length is unknown. 
     */
    function get length():int;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Adds the specified item to the end of the list.
     *  Equivalent to <code>addItemAt(item, length)</code>.
     *
     *  @param item The item to add.
     */
    function addItem(item:Object):void;
    
    /**
     *  Adds the item at the specified index.  
     *  The index of any item greater than the index of the added item is increased by one.  
     *  If the the specified index is less than zero or greater than the length
     *  of the list, a RangeError is thrown.
     * 
     *  @param item The item to place at the index.
     *
     *  @param index The index at which to place the item.
     *
     *  @throws RangeError if index is less than 0 or greater than the length of the list. 
     */
    function addItemAt(item:Object, index:int):void;
    
    /**
     *  Gets the item at the specified index.
     * 
     *  @param index The index in the list from which to retrieve the item.
     *
     *  @param prefetch An <code>int</code> indicating both the direction
     *  and number of items to fetch during the request if the item is
     *  not local.
     *
     *  @return The item at that index, or <code>null</code> if there is none.
     *
     *  @throws mx.collections.errors.ItemPendingError if the data for that index needs to be 
     *  loaded from a remote location.
     *
     *  @throws RangeError if <code>index &lt; 0</code>
     *  or <code>index >= length</code>.
     */
    function getItemAt(index:int, prefetch:int = 0):Object;
    
    /**
     *  Returns the index of the item if it is in the list such that
     *  getItemAt(index) == item.
     * 
     *  <p>Note: unlike <code>IViewCursor.find<i>xxx</i>()</code> methods,
     *  The <code>getItemIndex()</code> method cannot take a parameter with 
     *  only a subset of the fields in the item being serched for; 
     *  this method always searches for an item that exactly matches
     *  the input parameter.</p>
     * 
     *  @param item The item to find.
     *
     *  @return The index of the item, or -1 if the item is not in the list.
     */
    function getItemIndex(item:Object):int;
    
    /**
     *  Notifies the view that an item has been updated.  
     *  This is useful if the contents of the view do not implement 
     *  <code>IEventDispatcher</code> and dispatches a 
     *  <code>PropertyChangeEvent</code>.  
     *  If a property is specified the view may be able to optimize its 
     *  notification mechanism.
     *  Otherwise it may choose to simply refresh the whole view.
     *
     *  @param item The item within the view that was updated.
     *
     *  @param property The name of the property that was updated.
     *
     *  @param oldValue The old value of that property. (If property was null,
     *  this can be the old value of the item.)
     *
     *  @param newValue The new value of that property. (If property was null,
     *  there's no need to specify this as the item is assumed to be
     *  the new value.)
     *
     *  @see mx.events.CollectionEvent
     *  @see mx.events.PropertyChangeEvent
     */
    function itemUpdated(item:Object, property:Object = null, 
                         oldValue:Object = null, 
                         newValue:Object = null):void;

    /** 
     *  Removes all items from the list.
     *
     *  <p>If any item is not local and an asynchronous operation must be
     *  performed, an <code>ItemPendingError</code> will be thrown.</p>
     *
     *  <p>See the ItemPendingError documentation as well as
     *  the collections documentation for more information
     *   on using the <code>ItemPendingError</code>.</p>
     */
    function removeAll():void;

    /**
     *  Removes the item at the specified index and returns it.  
     *  Any items that were after this index are now one index earlier.
     *
     *  @param index The index from which to remove the item.
     *
     *  @return The item that was removed.
     *
     *  @throws RangeError is index is less than 0 or greater than length. 
     */
    function removeItemAt(index:int):Object;
    
    /**
     *  Places the item at the specified index.  
     *  If an item was already at that index the new item will replace it
     *  and it will be returned.
     *
     *  @param item The new item to be placed at the specified index.
     *
     *  @param index The index at which to place the item.
     *
     *  @return The item that was replaced, or <code>null</code> if none.
     *
     *  @throws RangeError if index is less than 0 or greater than length.
     */
    function setItemAt(item:Object, index:int):Object;
    
    /**
     *  Returns an Array that is populated in the same order as the IList
     *  implementation.
     *  This method can throw an ItemPendingError.
     *
     *  @return The array.
     *  
     *  @throws mx.collections.errors.ItemPendingError If the data is not yet completely loaded
     *  from a remote location.
     */ 
    function toArray():Array;
}

}
