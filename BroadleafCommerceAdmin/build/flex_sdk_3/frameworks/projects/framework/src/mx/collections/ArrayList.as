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
    
import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import flash.utils.IDataInput;
import flash.utils.IDataOutput;
import flash.utils.IExternalizable;
import flash.utils.getQualifiedClassName;
import mx.core.IPropertyChangeNotifier;
import mx.core.mx_internal;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.PropertyChangeEvent;
import mx.events.PropertyChangeEventKind;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.ArrayUtil;
import mx.utils.UIDUtil;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when the IList has been updated in some way.
 *  
 *  @eventType mx.events.CollectionEvent.COLLECTION_CHANGE
 */
[Event(name="collectionChange", type="mx.events.CollectionEvent")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[ExcludeClass]

[RemoteClass(alias="flex.messaging.io.ArrayList")]

[ResourceBundle("collections")]

/**
 *  @private
 *  A simple implementation of IList that uses a backing Array.
 *  This base class will not throw ItemPendingErrors but it
 *  is possible that a subclass might.
 */
public class ArrayList extends EventDispatcher
	   implements IList, IExternalizable, IPropertyChangeNotifier
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Construct a new ArrayList using the specified array as its source.
     *  If no source is specified an empty array will be used.
     */
    public function ArrayList(source:Array = null)
    {
		super();

        disableEvents();
        this.source = source;
        enableEvents();
        _uid = UIDUtil.createUID();
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Used for accessing localized Error messages.
	 */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    // Properties
    // 
    //--------------------------------------------------------------------------
    
    //----------------------------------
    // length
    //----------------------------------

    /**
     *  Get the number of items in the list.  An ArrayList should always
     *  know its length so it shouldn't return -1, though a subclass may 
     *  override that behavior.
     *
     *  @return int representing the length of the source.
     */
    public function get length():int
    {
    	if (source)
        	return source.length;
        else
        	return 0;
    }
    
    //----------------------------------
    // source
    //----------------------------------
    
    /**
     *  The source array for this ArrayList.  
     *  Any changes done through the IList interface will be reflected in the 
     *  source array.  
     *  If no source array was supplied the ArrayList will create one internally.
     *  Changes made directly to the underlying Array (e.g., calling 
     *  <code>theList.source.pop()</code> will not cause <code>CollectionEvents</code> 
     *  to be dispatched.
     *
	 *  @return An Array that represents the underlying source.
     */
    public function get source():Array
    {
        return _source;
    }
    
    public function set source(s:Array):void
    {
        var i:int;
        var len:int;
        if (_source && _source.length)
        {
            len = _source.length;
            for (i = 0; i < len; i++)
            {
                stopTrackUpdates(_source[i]);
            }
        }
        _source  = s ? s : [];
        len = _source.length;
        for (i = 0; i < len; i++)
        {
            startTrackUpdates(_source[i]);
        }
        
        if (_dispatchEvents == 0)
        {
           var event:CollectionEvent =
			new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
           event.kind = CollectionEventKind.RESET;
           dispatchEvent(event);
        }
    }
    
    //----------------------------------
    // uid -- mx.core.IPropertyChangeNotifier
    //----------------------------------
    
    /**
     *  Provides access to the unique id for this list.
     *  
     *  @return String representing the internal uid. 
     */  
    public function get uid():String
    {
    	return _uid;
    }
    
    public function set uid(value:String):void
    {
    	_uid = value;
	}

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Get the item at the specified index.
     * 
     *  @param 	index the index in the list from which to retrieve the item
     *  @param	prefetch int indicating both the direction and amount of items
     *			to fetch during the request should the item not be local.
     *  @return the item at that index, null if there is none
     *  @throws ItemPendingError if the data for that index needs to be 
     *                           loaded from a remote location
     *  @throws RangeError if the index < 0 or index >= length
     */
    public function getItemAt(index:int, prefetch:int = 0):Object
    {
        if (index < 0 || index >= length)
		{
			var message:String = resourceManager.getString(
				"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
		}
            
        return source[index];
    }
    
    /**
     *  Place the item at the specified index.  
     *  If an item was already at that index the new item will replace it and it 
     *  will be returned.
     *
     *  @param 	item the new value for the index
     *  @param 	index the index at which to place the item
     *  @return the item that was replaced, null if none
     *  @throws RangeError if index is less than 0 or greater than or equal to length
     */
    public function setItemAt(item:Object, index:int):Object
    {
        if (index < 0 || index >= length) 
		{
			var message:String = resourceManager.getString(
				"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
		}
        
        var oldItem:Object = source[index];
        source[index] = item;
        stopTrackUpdates(oldItem);
        startTrackUpdates(item);
        
        //dispatch the appropriate events 
        if (_dispatchEvents == 0)
        {
        	var hasCollectionListener:Boolean = 
        		hasEventListener(CollectionEvent.COLLECTION_CHANGE);
        	var hasPropertyListener:Boolean = 
        		hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE);
        	var updateInfo:PropertyChangeEvent; 
        	
        	if (hasCollectionListener || hasPropertyListener)
        	{
        		updateInfo = new PropertyChangeEvent(PropertyChangeEvent.PROPERTY_CHANGE);
        		updateInfo.kind = PropertyChangeEventKind.UPDATE;
            	updateInfo.oldValue = oldItem;
            	updateInfo.newValue = item;
            	updateInfo.property = index;
        	}
        	
        	if (hasCollectionListener)
        	{
           		var event:CollectionEvent =
					new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
            	event.kind = CollectionEventKind.REPLACE;
            	event.location = index;
            	event.items.push(updateInfo);
            	dispatchEvent(event);
         	}
         	
         	if (hasPropertyListener)
         	{
            	dispatchEvent(updateInfo);
          	}
        }
        return oldItem;    
    }
    
    /**
     *  Add the specified item to the end of the list.
     *  Equivalent to addItemAt(item, length);
     * 
     *  @param item the item to add
     */
    public function addItem(item:Object):void
    {
        addItemAt(item, length);
    }
    
    /**
     *  Add the item at the specified index.  
     *  Any item that was after this index is moved out by one.  
     * 
     *  @param item the item to place at the index
     *  @param index the index at which to place the item
     *  @throws RangeError if index is less than 0 or greater than the length
     */
    public function addItemAt(item:Object, index:int):void
    {
        if (index < 0 || index > length) 
		{
			var message:String = resourceManager.getString(
				"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
		}
        	
        source.splice(index, 0, item);

        startTrackUpdates(item);
        internalDispatchEvent(CollectionEventKind.ADD, item, index);
    }
    
    /**
     *  Return the index of the item if it is in the list such that
     *  getItemAt(index) == item.  
     *  Note that in this implementation the search is linear and is therefore 
     *  O(n).
     * 
     *  @param item the item to find
     *  @return the index of the item, -1 if the item is not in the list.
     */
    public function getItemIndex(item:Object):int
    {
    	return ArrayUtil.getItemIndex(item, source);
    }
    
    /**
     *  Removes the specified item from this list, should it exist.
     *
     *	@param	item Object reference to the item that should be removed.
     *  @return	Boolean indicating if the item was removed.
     */
    public function removeItem(item:Object):Boolean
    {
    	var index:int = getItemIndex(item);
    	var result:Boolean = index >= 0;
    	if (result)
    		removeItemAt(index);

    	return result;
    }
    
    /**
     *  Remove the item at the specified index and return it.  
     *  Any items that were after this index are now one index earlier.
     *
     *  @param index the index from which to remove the item
     *  @return the item that was removed
     *  @throws RangeError is index < 0 or index >= length
     */
    public function removeItemAt(index:int):Object
    {
        if (index < 0 || index >= length)
		{
			var message:String = resourceManager.getString(
				"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
		}

        var removed:Object = source.splice(index, 1)[0];
        stopTrackUpdates(removed);
        internalDispatchEvent(CollectionEventKind.REMOVE, removed, index);
        return removed;
    }
    
    /** 
     *  Remove all items from the list.
     */
    public function removeAll():void
    {
        if (length > 0)
        {
            var len:int = length;
            for (var i:int = 0; i < len; i++)
            {
                stopTrackUpdates(source[i]);
            }

            source.splice(0, length);
			internalDispatchEvent(CollectionEventKind.RESET);
        }    
    }
    
    /**
     *  Notify the view that an item has been updated.  
     *  This is useful if the contents of the view do not implement 
     *  <code>IEventDispatcher</code>.  
     *  If a property is specified the view may be able to optimize its 
     *  notification mechanism.
     *  Otherwise it may choose to simply refresh the whole view.
     *
     *  @param item The item within the view that was updated.
	 *
     *  @param property A String, QName, or int
	 *  specifying the property that was updated.
	 *
     *  @param oldValue The old value of that property.
	 *  (If property was null, this can be the old value of the item.)
	 *
     *  @param newValue The new value of that property.
	 *  (If property was null, there's no need to specify this
	 *  as the item is assumed to be the new value.)
     *
     *  @see mx.events.CollectionEvent
     *  @see mx.core.IPropertyChangeNotifier
     *  @see mx.events.PropertyChangeEvent
     */
     public function itemUpdated(item:Object, property:Object = null, 
                                 oldValue:Object = null, 
                                 newValue:Object = null):void
    {
        var event:PropertyChangeEvent =
			new PropertyChangeEvent(PropertyChangeEvent.PROPERTY_CHANGE);
        
		event.kind = PropertyChangeEventKind.UPDATE;
        event.source = item;
        event.property = property;
        event.oldValue = oldValue;
        event.newValue = newValue;
        
		itemUpdateHandler(event);        
    }    
    
    /**
     *  Return an Array that is populated in the same order as the IList
     *  implementation.  
     * 
     *  @throws ItemPendingError if the data is not yet completely loaded
     *  from a remote location
     */ 
    public function toArray():Array
    {
        return source.concat();
    }
    
    /**
     *  Ensures that only the source property is seralized.
     *  @private
     */
    public function readExternal(input:IDataInput):void
    {
    	source = input.readObject();
    }
    
    /**
     *  Ensures that only the source property is serialized.
     *  @private
     */
    public function writeExternal(output:IDataOutput):void
    {
    	output.writeObject(_source);
    }

	/**
     *  Pretty prints the contents of this ArrayList to a string and returns it.
     */
    override public function toString():String
	{
		if (source)
			return source.toString();
		else
			return getQualifiedClassName(this);	
	}	
    
    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

	/**
	 *  Enables event dispatch for this list.
	 */
	private function enableEvents():void
	{
		_dispatchEvents++;
		if (_dispatchEvents > 0)
			_dispatchEvents = 0;
	}
	
	/**
	 *  Disables event dispatch for this list.
	 *  To re-enable events call enableEvents(), enableEvents() must be called
	 *  a matching number of times as disableEvents().
	 */
	private function disableEvents():void
	{
		_dispatchEvents--;
	}
	
	/**
	 *  Dispatches a collection event with the specified information.
	 *
	 *  @param kind String indicates what the kind property of the event should be
	 *  @param item Object reference to the item that was added or removed
	 *  @param location int indicating where in the source the item was added.
	 */
	private function internalDispatchEvent(kind:String, item:Object = null, location:int = -1):void
	{
    	if (_dispatchEvents == 0)
    	{
    		if (hasEventListener(CollectionEvent.COLLECTION_CHANGE))
    		{
		        var event:CollectionEvent =
					new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
		        event.kind = kind;
		        event.items.push(item);
		        event.location = location;
		        dispatchEvent(event);
		    }

	    	// now dispatch a complementary PropertyChangeEvent
	    	if (hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE) && 
	    	   (kind == CollectionEventKind.ADD || kind == CollectionEventKind.REMOVE))
	    	{
	    		var objEvent:PropertyChangeEvent =
					new PropertyChangeEvent(PropertyChangeEvent.PROPERTY_CHANGE);
	    		objEvent.property = location;
	    		if (kind == CollectionEventKind.ADD)
	    			objEvent.newValue = item;
	    		else
	    			objEvent.oldValue = item;
	    		dispatchEvent(objEvent);
	    	}
	    }
	}
	
    /**
     *  Called whenever any of the contained items in the list fire an
     *  ObjectChange event.  
     *  Wraps it in a CollectionEventKind.UPDATE.
     */    
    protected function itemUpdateHandler(event:PropertyChangeEvent):void
    {
		internalDispatchEvent(CollectionEventKind.UPDATE, event);
		// need to dispatch object event now
    	if (_dispatchEvents == 0 && hasEventListener(PropertyChangeEvent.PROPERTY_CHANGE))
    	{
    		var objEvent:PropertyChangeEvent = PropertyChangeEvent(event.clone());
    		var index:uint = getItemIndex(event.target);
    		objEvent.property = index.toString() + "." + event.property;
    		dispatchEvent(objEvent);
    	}
    }
    
    /** 
     *  If the item is an IEventDispatcher watch it for updates.  
     *  This is called by addItemAt and when the source is initially
     *  assigned.
     */
    protected function startTrackUpdates(item:Object):void
    {
        if (item && (item is IEventDispatcher))
        {
            IEventDispatcher(item).addEventListener(
				                        PropertyChangeEvent.PROPERTY_CHANGE, 
                                        itemUpdateHandler, false, 0, true);
        }
    }
    
    /** 
     *  If the item is an IEventDispatcher stop watching it for updates.
     *  This is called by removeItemAt, removeAll, and before a new
     *  source is assigned.
     */
    protected function stopTrackUpdates(item:Object):void
    {
        if (item && item is IEventDispatcher)
        {
            IEventDispatcher(item).removeEventListener(
				                        PropertyChangeEvent.PROPERTY_CHANGE, 
                                        itemUpdateHandler);    
        }
    }
    
    //--------------------------------------------------------------------------
    //
    // Variables
    // 
    //--------------------------------------------------------------------------

	/**
	 *  indicates if events should be dispatched.
	 *  calls to enableEvents() and disableEvents() effect the value when == 0
	 *  events should be dispatched. 
	 */
	private var _dispatchEvents:int = 0;
    private var _source:Array;
    private var _uid:String;
}

}
