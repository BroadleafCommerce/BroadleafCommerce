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

/**
 *  Dispatched whenever the cursor position is updated.
 *
 *  @eventType mx.events.FlexEvent.CURSOR_UPDATE
 */
[Event(name="cursorUpdate", type="mx.events.FlexEvent")]

/**
 *  Defines the interface for enumerating a collection view bi-directionally.
 *  This cursor provides find, seek, and bookmarking capabilities along
 *  with the modification methods insert and remove.  
 *  When a cursor is first retrieved from a view, (typically by the ICollectionView
 *  <code>createCursor()</code> method) the value of the 
 *  <code>current</code> property should be the first
 *  item in the view, unless the view is empty.
 */
public interface IViewCursor extends IEventDispatcher
{
    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  afterLast
    //----------------------------------

    [Bindable("cursorUpdate")]
    
    /**
     *  If the cursor is located after the last item in the view, 
     *  this property is <code>true</code> .
     *  If the ICollectionView is empty (length == 0),
     *  this property is <code>true</code>.
     */
    function get afterLast():Boolean;

    //----------------------------------
    //  beforeFirst
    //----------------------------------

    [Bindable("cursorUpdate")]
    
    /**
     *  If the cursor is located before the first item in the view,
     *  this property is <code>true</code>.
     *  If the ICollectionView is empty (length == 0),
     *  this property is <code>true</code>.
     */
    function get beforeFirst():Boolean;

    //----------------------------------
    //  bookmark
    //----------------------------------

    [Bindable("cursorUpdate")]
    
    /**
     *  Provides access to a bookmark that corresponds to the item
     *  returned by the <code>current</code> property.
     *  The bookmark can be used to move the cursor
     *  to a previously visited item, or to a position relative to that item.
     *  (See the <code>seek()</code> method for more information.)
     *
     *  @see #current
     *  @see #seek()
     */
    function get bookmark():CursorBookmark;

    //----------------------------------
    //  current
    //----------------------------------

    [Bindable("cursorUpdate")]
    
    /**
     *  Provides access the object at the location
     *  in the source collection referenced by this cursor.
     *  If the cursor is beyond the ends of the collection
     *  (<code>beforeFirst</code>, <code>afterLast</code>)
     *  this will return <code>null</code>.
     *
     *  @see #moveNext()
     *  @see #movePrevious()
     *  @see #seek()
     *  @see #beforeFirst
     *  @see #afterLast
     */
    function get current():Object;

    //----------------------------------
    //  view
    //----------------------------------

    /**
     *  A reference to the ICollectionView with which this cursor is associated.
     */
    function get view():ICollectionView;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Finds an item with the specified properties within the collection
     *  and positions the cursor to that item.
     *  If the item can not be found, the cursor location does not change.
     *
     *  <p>The <code>findAny()</code> method can only be called on sorted views;
     *  if the view isn't sorted, a <code>CursorError</code> is thrown.</p>
     *  
     *  <p>If the associated collection is remote, and not all of the items
     *  have been cached locally, this method begins an asynchronous fetch
     *  from the remote collection. If one is already in progress, this method
     *  waits for it to complete before making another fetch request.</p>
     *
     *  <p>If multiple items can match the search criteria then the item found
     *  is non-deterministic.
     *  If it is important to find the first or last occurrence of an item
     *  in a non-unique index, use the <code>findFirst()</code> or
     *  <code>findLast()</code> method.</p>
     *
     *  <p>If the data is not local and an asynchronous operation must be
     *  performed, an ItemPendingError is thrown.</p>
     *  
     *  @param values The search criteria. The values in the Object must be configured as name-value pairs,
     *  as in an associative array (or be the actual object to search for). The values of the names specified must match properties
     *  specified in the sort. For example, if properties <code>x</code>, <code>y</code>, and
     *  <code>z</code> are in the current sort, the values specified should be
     *  <code>{x: <i>x-value</i>, y: <i>y-value</i>, z: <i>z-value</i>}</code>.
     *
     *  @return When all of the data is local this method returns
     *  <code>true</code> if the item can be found and <code>false</code>
     *  otherwise. 
     *
     *  @see #findFirst()
     *  @see #findLast()
     *  @see mx.collections.errors.ItemPendingError
     */
    function findAny(values:Object):Boolean;

    /**
     *  Finds the first item with the specified properties within the collection
     *  and positions the cursor to that item.
     *  If the item can not be found, no cursor location does not change.
     *
     *  <p>The <code>findFirst()</code> method can only be called on sorted views;
     *  if the view isn't sorted, a <code>CursorError</code> is thrown.</p>
     *  
     *  <p>If the associated collection is remote, and not all of the items
     *  have been cached locally, this method begins an asynchronous fetch
     *  from the remote collection. If one is already in progress, this method
     *  waits for it to complete before making another fetch request.</p>
     *
     *  <p>If it is not important to find the first occurrence of an item
     *  in a non-unique index, use <code>findAny()</code>, which may be
     *  a little faster than the <code>findFirst() method</code>.</p>
     *
     *  <p>If the data is not local and an asynchronous operation must be
     *  performed, an ItemPendingError is thrown.</p>
     *
     *  @param values The search criteria. The values in the Object must be configured as name-value pairs,
     *  as in an associative array (or be the actual object to search for). The values of the names specified must match properties
     *  specified in the sort. For example, if properties <code>x</code>, <code>y</code>, and
     *  <code>z</code> are in the current sort, the values specified should be
     *  <code>{x: <i>x-value</i>, y: <i>y-value</i>, z: <i>z-value</i>}</code>.
     *
     *  @return When all of the data is local this method returns
     *  <code>true</code> if the item can be found and <code>false</code>
     *  otherwise. 
     *  
     *  @see #findAny()
     *  @see #findLast()
     *  @see mx.collections.errors.ItemPendingError
     */
    function findFirst(values:Object):Boolean;

    /**
     *  Finds the last item with the specified properties within the collection
     *  and positions the cursor on that item.
     *  If the item can not be found, the cursor location does not chanage.
     *
     *  <p>The <code>findLast()</code> method can only be called on sorted views;
     *  if the view isn't sorted, a <code>CursorError</code> is thrown.</p>
     *  
     *  <p>If the associated collection is remote, and not all of the items
     *  have been cached locally, this method begins an asynchronous fetch
     *  from the remote collection. If one is already in progress, this method
     *  waits for it to complete before making another fetch request.</p>
     *
     *  <p>If it is not important to find the last occurrence of an item
     *  in a non-unique index, use the <code>findAny()</code> method, which
     *  may be a little faster.</p>
     *
     *  <p>If the data is not local and an asynchronous operation must be
     *  performed, an ItemPendingError is thrown.</p>
     *
     *  @param values The search criteria. The values in the Object must be configured as name-value pairs,
     *  as in an associative array (or be the actual object to search for). The values of the names specified must match properties
     *  specified in the sort. For example, if properties <code>x</code>, <code>y</code>, and
     *  <code>z</code> are in the current sort, the values specified should be
     *  <code>{x: <i>x-value</i>, y: <i>y-value</i>, z: <i>z-value</i>}</code>.
     *
     *  @return When all of the data is local this method returns
     *  <code>true</code> if the item can be found and <code>false</code>
     *  otherwise. 
     *  
     *  @see #findAny()
     *  @see #findFirst()
     *  @see mx.collections.errors.ItemPendingError
     */
    function findLast(values:Object):Boolean;

    /**
     *  Inserts the specified item before the cursor's current position.
     *  If the cursor is <code>afterLast</code>,
     *  the insertion occurs at the end of the view.
     *  If the cursor is <code>beforeFirst</code> on a non-empty view,
     *  an error is thrown.
     *  
     *  @param item The item to insert before the cursor's current position.
     */
    function insert(item:Object):void;

    /**
     *  Moves the cursor to the next item within the collection.
     *  On success the <code>current</code> property is updated
     *  to reference the object at this new location.
     *  Returns <code>true</code> if the resulting <code>current</code> 
     *  property is valid, or <code>false</code> if not 
     *  (the property value is <code>afterLast</code>).
     *
     *  <p>If the data is not local and an asynchronous operation must be performed,
     *  an ItemPendingError is thrown.
     *  See the ItemPendingError documentation and  the collections
     *  documentation for more information on using the ItemPendingError.</p>
     *
     *  @return <code>true</code> if still in the list,
     *  <code>false</code> if the <code>current</code> value initially was
     *  or now is <code>afterLast</code>.
     *
     *  @see #current
     *  @see #movePrevious()
     *  @see mx.collections.errors.ItemPendingError
     *
     *  @example
     *  <pre>
     *  var myArrayCollection:ICollectionView = new ArrayCollection([ "Bobby", "Mark", "Trevor", "Jacey", "Tyler" ]);
     *  var cursor:IViewCursor = myArrayCollection.createCursor();
     *  while (!cursor.afterLast)
     *  {
     *      trace(cursor.current);
     *      cursor.moveNext();
     *  }
     *  </pre>
     */
    function moveNext():Boolean;

    /**
     *  Moves the cursor to the previous item within the collection.
     *  On success the <code>current</code> property is updated
     *  to reference the object at this new location.
     *  Returns <code>true</code> if the resulting <code>current</code> 
     *  property is valid, or <code>false</code> if not 
     *  (the property value is <code>beforeFirst</code>).
     *
     *  <p>If the data is not local and an asynchronous operation must be performed,
     *  an ItemPendingError is thrown.
     *  See the ItemPendingError documentation and the collections
     *  documentation for more information on using the ItemPendingError.</p>
     *
     *  @return <code>true</code> if still in the list,
     *  <code>false</code> if the <code>current</code> value initially was or
     *  now is <code>beforeFirst</code>.
     *
     *  For example:
     *  <pre>
     *  var myArrayCollection:ICollectionView = new ArrayCollection([ "Bobby", "Mark", "Trevor", "Jacey", "Tyler" ]);
     *  var cursor:IViewCursor = myArrayCollection.createCursor();
     *  cursor.seek(CursorBookmark.last);
     *  while (!cursor.beforeFirst)
     *  {
     *      trace(current);
     *      cursor.movePrevious();
     *  }
     *  </pre>
     *
     *  @see #current
     *  @see #moveNext()
     *  @see mx.collections.errors.ItemPendingError
     */
    function movePrevious():Boolean;

    /**
     *  Removes the current item and returns it.
     *  If the cursor location is <code>beforeFirst</code> or 
     *  <code>afterLast</code>, throws a CursorError. 
     *  If you remove any item other than the last item,
     *  the cursor moves to the next item. If you remove the last item, the
     *  cursor is at the AFTER_LAST bookmark.
     *  
     *  <p>If the item after the removed item is not local and an asynchronous 
     *  operation must be performed, an ItemPendingError is thrown. 
     *  See the ItemPendingError documentation and the collections
     *  documentation  for more information on using the ItemPendingError.</p>
     * 
     *  @return The item that was removed.
     *  
     *  @see mx.collections.errors.ItemPendingError
     */
    function remove():Object;

    /**
     *  Moves the cursor to a location at an offset from the specified
     *  bookmark.
     *  The offset can be negative, in which case the cursor is positioned
     *  an <code>offset</code> number of items prior to the specified bookmark.
     *
     *  <p>If the associated collection is remote, and not all of the items
     *  have been cached locally, this method begins an asynchronous fetch
     *  from the remote collection.</p>
     *
     *  <p>If the data is not local and an asynchronous operation
     *  must be performed, an ItemPendingError is thrown.
     *  See the ItemPendingError documentation and the collections
     *  documentation for more information on using the ItemPendingError.</p>
     *
     *  @param bookmark <code>CursorBookmark</code> reference to marker
     *  information that allows repositioning to a specific location.
     *  You can set this parameter to value returned from the
     *  <code>bookmark</code> property, or to one of the following constant 
     *  bookmark values:
     *  <ul>
     *    <li><code>CursorBookmark.FIRST</code> -
     *    Seek from the start (first element) of the collection</li>
     *    <li><code>CursorBookmark.CURRENT</code> -
     *    Seek from the current position in the collection</li>
     *    <li><code>CursorBookmark.LAST</code> -
     *    Seek from the end (last element) of the collection</li>
     *  </ul>
     *
     *  @param offset Indicates how far from the specified bookmark to seek.
     *  If the specified number is negative, the cursor attempts to
     *  move prior to the specified bookmark.
     *  If the offset specified is beyond the end of the collection,
     *  the cursor is be positioned off the end, to the 
     *  <code>beforeFirst</code> or <code>afterLast</code> location.
     *
     *  @param prefetch Used for remote data. Indicates an intent to iterate 
     *  in a specific direction once the seek operation completes.
     *  This reduces the number of required network round trips during a seek.
     *  If the iteration direction is known at the time of the request,
     *  the appropriate amount of data can be returned ahead of the request
     *  to iterate it.
     * 
     *  @see mx.collections.errors.ItemPendingError
    */
    function seek(bookmark:CursorBookmark, offset:int = 0, prefetch:int = 0):void;
}

}
