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

package mx.collections
{

import flash.events.IEventDispatcher;
import mx.events.CollectionEvent;

/**
 *  Dispatched when the ICollectionView has been updated in some way.
 *
 *  @eventType mx.events.CollectionEvent.COLLECTION_CHANGE
 */
[Event(name="collectionChange", type="mx.events.CollectionEvent")]

/**
 *  An <code>ICollectionView</code> is a view onto a collection of data.
 *  The view can be modified to show the data sorted according to various
 *  criteria or reduced by filters without modifying the underlying data.
 *  An IViewCursor provides to access items within a
 *  collection. You can modify the collection by using the IViewCursor
 *  interface <code>insert()</code> and <code>remove()</code> methods.
 *
 *  <p>An <code>ICollectionView</code> may be a view onto data that has been
 *  retrieved from a remote location.
 *  When Implementing this interface for data
 *  that may be remote it is important to handle the case where data
 *  may not yet be available, which is indicated by the
 *  <code>ItemPendingError</code>.</p>
 *
 *  <p>The <code>IList</code> interface is an alternative to the
 *  <code>ICollectionView</code> interface.</p>
 *
 *  @see mx.collections.IViewCursor
 *  @see mx.collections.errors.ItemPendingError
 *  @see mx.collections.IList
 */
public interface ICollectionView extends IEventDispatcher
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
     *  The number of items in this view.
	 *  0 means no items, while -1 means that the length is unknown.
     */
    function get length():int;

	//----------------------------------
	//  filterFunction
	//----------------------------------

    /**
     *  A function that the view will use to eliminate items that do not
     *  match the function's criteria.
	 *  A filterFunction is expected to have the following signature:
	 *
	 *  <pre>f(item:Object):Boolean</pre>
	 *
	 *  where the return value is <code>true</code> if the specified item
	 *  should remain in the view.
	 *
     *  <p>If a filter is unsupported, Flex throws an error when accessing
     *  this property.
     *  You must call <code>refresh()</code> after setting the
	 *  <code>filterFunction</code> property for the view to update.</p>
	 *
 	 *  <p>Note: The Flex implementations of ICollectionView retrieve all
	 *  items from a remote location before executing the filter function.
	 *  If you use paging, apply the filter to the remote collection before
	 *  you retrieve the data.</p>
	 *
     *  @see #refresh()
     */
    function get filterFunction():Function;
    
	/**
	 *  @private
	 */
	function set filterFunction(value:Function):void;

	//----------------------------------
	//  sort
	//----------------------------------

    /**
     *  The Sort that will be applied to the ICollectionView.
	 *  Setting the sort does not automatically refresh the view,
	 *  so you must call the <code>refresh()</code> method
	 *  after setting this property.
     *  If sort is unsupported an error will be thrown when accessing
     *  this property.
	 *
	 *  <p>Note: The Flex implementations of ICollectionView retrieve all
	 *  items from a remote location before executing a sort.
	 *  If you use paging with a sorted list, apply the sort to the remote
	 *  collection before you retrieve the data.</p>
	 *
     *  @see #refresh()
     */
    function get sort():Sort;
    
	/**
	 *  @private
	 */
    function set sort(value:Sort):void;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Creates a new IViewCursor that works with this view.
     *
     *  @return A new IViewCursor implementation.
     *
     */
    function createCursor():IViewCursor;

    /**
     *  Returns whether the view contains the specified object.
	 *  Unlike the <code>IViewCursor.find<i>xxx</i></code> methods,
	 *  this search is succesful only if it finds an item that exactly
	 *  matches the parameter.
     *  If the view has a filter applied to it this method may return
	 *  <code>false</code> even if the underlying collection
	 *  does contain the item.
     *
     *  @param item The object to look for.
	 *
     *  @return true if the ICollectionView, after applying any filter,
     *  contains the item; false otherwise.
     */
    function contains(item:Object):Boolean;

    /**
     *  Prevents changes to the collection itself and items within the
	 *  collection from being dispatched by the view.
	 *  Also prevents the view from updating the positions of items
	 *  if the positions change in the collection.
	 *  The changes will be queued and dispatched appropriately
	 *  after <code>enableAutoUpdate</code> is called.
	 *  If more events than updates to a single item occur,
	 *  the view may end up resetting. 
	 *  The <code>disableAutoUpdate</code> method acts cumulatively;
	 *  the same number of calls to <code>enableAutoUpdate</code>
	 *  are required for the view to dispatch events and refresh.
	 *  Note that <code>disableAutoUpdate</code> only affects the
     *  individual view; edits may be detected on an individual
	 *  basis by other views.
     */
    function disableAutoUpdate():void;

    /**
     *  Enables auto-updating.
	 *  See <code>disableAutoUpdate</code> for more information.
	 *
     *  @see #disableAutoUpdate()
     */
    function enableAutoUpdate():void;

    /**
     *  Notifies the view that an item has been updated.
	 *  This method is useful if the contents of the view do not implement
	 *  <code>IPropertyChangeNotifier</code>.
	 *  If the call to this method includes a <code>property</code> parameter,
	 *  the view may be able to optimize its notification mechanism.
     *  Otherwise it may choose to simply refresh the whole view.
     *
     *  @param item The item within the view that was updated.
	 *
     *  @param property The name of the property that was updated.
	 *
     *  @param oldValue The old value of that property. (If property
	 *  was null, this can be the old value of the item.).
	 *
     *  @param newValue The new value of that property. (If property
	 *  was null, there's no need to specify this as the item is assumed
	 *  to be the new value.)
     *
     *  @see mx.events.CollectionEvent
     *  @see mx.core.IPropertyChangeNotifier
     *  @see mx.events.PropertyChangeEvent
     */
    function itemUpdated(item:Object, property:Object = null,
                         oldValue:Object = null, newValue:Object = null):void;

    /**
     *  Applies the sort and filter to the view.
	 *  The ICollectionView does not detect changes to a sort or
	 *  filter automatically, so you must call the <code>refresh()</code>
	 *  method to update the view after setting the <code>sort</code> 
	 *  or <code>filterFunction</code> property.
	 *  If your ICollectionView implementation also implements
	 *  the IMXMLObject interface, you should to call the
	 *  <code>refresh()</code> method from your <code>initialized()</code>
	 *  method.
	 *
     *  <p>Returns <code>true</code> if the refresh was successful
	 *  and <code>false</code> if the sort is not yet complete
	 *  (e.g., items are still pending).
	 *  A client of the view should wait for a CollectionEvent event
	 *  with the <code>CollectionEventKind.REFRESH</code> <code>kind</code>
	 *  property to ensure that the <code>refresh()</code> operation is
	 *  complete.</p>
     *
     *  @return <code>true</code> if the refresh() was complete,
	 *  <code>false</code> if the refresh() is incomplete.
     */
    function refresh():Boolean;
}

}
