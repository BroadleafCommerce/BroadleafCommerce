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
import flash.utils.getQualifiedClassName;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.events.PropertyChangeEvent;
import mx.events.PropertyChangeEventKind;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.utils.IXMLNotifiable;
import mx.utils.XMLNotifier;
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

[ResourceBundle("collections")]

/**
 *  @private
 *  A simple implementation of IList that uses a backing XMLList.
 *  No ItemPendingErrors since the data is always local.
 */
public class XMLListAdapter extends EventDispatcher implements IList, IXMLNotifiable
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    // Constructor
    // 
    //--------------------------------------------------------------------------

    /**
     *  Construct a new XMLListAdapter using the specified XMLList as its source.
     *  If no source is specified an empty XMLList will be used.
     */
    public function XMLListAdapter(source:XMLList = null)
    {
		super();

        disableEvents();
        this.source = source;
        enableEvents();
    }
    
    //--------------------------------------------------------------------------
    //
    //  Variables
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
     *  The number of items in this list.  
     * 
     *  @return -1 if the length is unknown while 0 means no items
     */
    public function get length():int
    {
        return source.length();
    }
    
    //----------------------------------
    // source
    //----------------------------------
    
    private var _source:XMLList;
    
    /**
     *  The source XMLList for this XMLListAdapter.  
     *  Any changes done through the IList interface will be reflected in the 
     *  source XMLList.  
     *  If no source XMLList was supplied the XMLListAdapter will create one 
     *  internally.
     *  Changes made directly to the underlying XMLList (e.g., calling 
     *  <code>delete theList[someIndex]</code> will not cause <code>CollectionEvents</code> 
     *  to be dispatched.
     */
    public function get source():XMLList
    {
        return _source;
    }
    
    public function set source(s:XMLList):void
    {
        var i:int;
        var len:int;
        if (_source && _source.length())
        {
            len = _source.length();
            for (i = 0; i < len; i++)
            {
                stopTrackUpdates(_source[i]);
            }
        }
        _source  = s ? s : XMLList(<></>);

		// get a seed UID.  XML seems to have problems cleaning up
		// UIDs, so this makes UIDs more similar and uses less
		// memory
		seedUID = UIDUtil.createUID();
		uidCounter = 0;

        len = _source.length();
        for (i = 0; i < len; i++)
        {
            startTrackUpdates(_source[i], seedUID + uidCounter.toString());
			uidCounter++;
        }
        
        if (_dispatchEvents == 0)
        {
			var event:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
			event.kind = CollectionEventKind.RESET;
			dispatchEvent(event);
        }
    }    

    //--------------------------------------------------------------------------
    //
    // Methods
    // 
    //--------------------------------------------------------------------------

    /**
     *  Add the specified item to the end of the list.
     *  Equivalent to addItemAt(item, length);
     *  @param item the item to add
     */
    public function addItem(item:Object):void
    {
        addItemAt(item, length);
    }
    
    /**
     *  Add the item at the specified index.  Any item that was after
     *  this index is moved out by one.  If the list is shorter than 
     *  the specified index it will grow to accomodate the new item.
     * 
     *  @param item the item to place at the index
     *  @param index the index at which to place the item
     *  @throws RangeError if index is less than 0     
     */
    public function addItemAt(item:Object, index:int):void
    {
        var message:String;
        
        if (index < 0 || index > length) 
        {
        	message = resourceManager.getString(
        		"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
        }

		if (!(item is XML) && !(item is XMLList && item.length() == 1))
		{
			message = resourceManager.getString(
				"collections", "invalidType");
			throw new Error(message);
		}
        	
		setBusy();

    	//e4x doesn't provide an insertion operator so you tend to do
    	//addition.  if we're inserting at the first item we you add
    	//the old 1st to the new one.  if inserting in the middle or end
    	//you just add to the one before
        if (index == 0)
            source[0] = length > 0 ? item + source[0] : item;
        else
            source[index - 1] += item;

        startTrackUpdates(item, seedUID + uidCounter.toString());
		uidCounter++;

        if (_dispatchEvents == 0)
        {
            var event:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
            event.kind = CollectionEventKind.ADD;
            event.items.push(item);
            event.location = index;
            dispatchEvent(event);
        }

		clearBusy();
    }
    
    /**
     *  Get the item at the specified index.
     * 
     *  @param index the index in the list from which to retrieve the item
     *  @param	prefetch int indicating both the direction and amount of items
     *			to fetch during the request should the item not be local.
     *  @return the item at that index, null if there is none
     *  @throws ItemPendingError if the data for that index needs to be 
     *                          loaded from a remote location
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
     *  Return the index of the item if it is in the list such that
     *  getItemAt(index) == item.  Note: unlike IViewCursor.findXXX
     *  <code>getItemIndex</code> cannot take a representative object, it is
     *  searching for an exact match.
     * 
     *  @param item the item to find
     *  @return the index of the item, -1 if the item is not in the list.
     */
    public function getItemIndex(item:Object):int
    {
        if (item is XML && source.contains(XML(item)))
        {
	        var len:int = length;
	        for (var i:int = 0; i < len; i++)
	        {
	            var search:Object = source[i];
	            if (search === item)
	            {
	                return i;
	            }
	        }
        }
        return -1;           
    }
    
    /**
     *  Notify the view that an item has been updated.  This is useful if the
     *  contents of the view do not implement <code>IEventDispatcher</code> 
     *  and dispatches a <code>PropertyChangeEvent</code>.  If a property
     *  is specified the view may be able to optimize its notification mechanism.
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
     * Remove all items from the list.
     */
    public function removeAll():void
    {
        if (length > 0)
        {
            for (var i:int=length - 1; i >= 0; i--)
            {
                stopTrackUpdates(source[i]);
                delete source[i];
            }
            
            if (_dispatchEvents == 0)
            {
               var event:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
               event.kind = CollectionEventKind.RESET;
               dispatchEvent(event);
            }
        }
    }

    /**
     *  Remove the item at the specified index and return it.  Any items
     *  that were after this index are now one index earlier.
     *
     *  @param index the index from which to remove the item
     *  @return the item that was removed
     *  @throws RangeError is index is less than 0 or greater than length     
     */
    public function removeItemAt(index:int):Object
    {
        if (index < 0 || index >= length)
        {
        	var message:String = resourceManager.getString(
        		"collections", "outOfBounds", [ index ]);
        	throw new RangeError(message);
        }

		setBusy();

        var removed:Object = source[index];
        delete source[index];
        stopTrackUpdates(removed);
        if (_dispatchEvents == 0)
        {
            var event:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
            event.kind = CollectionEventKind.REMOVE;
            event.location = index;
            event.items.push(removed);
            dispatchEvent(event);
        }

		clearBusy();

        return removed;
    }
    
    /**
     *  Place the item at the specified index.  If an item was already
     *  at that index the new item will replace it and it will be returned.
     *  If the list is shorter than the specified index it will grow to 
     *  to accomodate the new item.
     *
     *  @param item the new value for the index
     *  @param index the index at which to place the item
     *  @return the item that was replaced, null if none
     *  @throws RangeError if index is less than 0     
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
        startTrackUpdates(item, seedUID + uidCounter.toString());
		uidCounter++;

        if (_dispatchEvents == 0)
        {
            var event:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
            event.kind = CollectionEventKind.REPLACE;
            var updateInfo:PropertyChangeEvent =
				new PropertyChangeEvent(PropertyChangeEvent.PROPERTY_CHANGE);
            updateInfo.kind = PropertyChangeEventKind.UPDATE;
            updateInfo.oldValue = oldItem;
            updateInfo.newValue = item;
            event.location = index;
            event.items.push(updateInfo);
            dispatchEvent(event);
        }
        return oldItem;            
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
        var s:XMLList = source;
        var len:int = s.length();
        var ret:Array = [];
        for (var i:int = 0; i < len; i++)
        {
            ret[i] = s[i];
        }
        return ret;
    }

	/**
     *  Pretty prints the contents of this XMLListAdapter to a string and returns it.
     */
	override public function toString():String
	{
		if (source)
			return source.toString();
		else
			return getQualifiedClassName(this);
	}
    
	/**
     *  True if we're processing a addItem or removeItem call
     */
	public function busy():Boolean
	{
		return (_busy != 0)
	}

    //--------------------------------------------------------------------------
    //
    // Internal Methods
    // 
    //--------------------------------------------------------------------------

	/**
	 *  Enables event dispatch for this list.
	 */
	protected function enableEvents():void
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
	protected function disableEvents():void
	{
		_dispatchEvents--;
	}
	
	/**
	 *  clears busy flag
	 */
	private function clearBusy():void
	{
		_busy++;
		if (_busy > 0)
			_busy = 0;
	}
	
	/**
	 *  Sets busy flag.  Tree DP's check it so they
	 *  know whether to fake events for it or not.
	 */
	private function setBusy():void
	{
		_busy--;
	}

    /**
     *  Called whenever any of the contained items in the list fire an
     *  ObjectChange event.  
     *  Wraps it in a CollectionEventKind.UPDATE.
     */    
    protected function itemUpdateHandler(event:PropertyChangeEvent):void
    {
    	if (_dispatchEvents == 0)
    	{
	        var updateEvent:CollectionEvent =
				new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
	        updateEvent.kind = CollectionEventKind.UPDATE;
	        updateEvent.items.push(event);
	        dispatchEvent(updateEvent);
	    }
    }
    
    /**
     * Called whenever an XML object contained in our list is updated
     * in some way.  The initial implementation stab is very lenient,
     * any changeType will cause an update no matter how much further down
     * in a hierarchy.  
     */
    public function xmlNotification(currentTarget:Object, 
                                        type:String, 
                                        target:Object, 
                                        value:Object, 
                                        detail:Object):void
    {
        var prop:String;
        var oldValue:Object;
        var newValue:Object;
        
        if (currentTarget === target)
        {
	        switch(type)
	        {
	            case "attributeAdded":
	            {
	                prop = "@" + String(value);
	                newValue = detail;
	                break;
	            }

	            case "attributeChanged":
	            {
	                prop = "@" + String(value);
	                oldValue = detail;
	                newValue = target[prop];
	                break;
	            }

	            case "attributeRemoved":
	            {
	                prop = "@" + String(value);
	                oldValue = detail;
	                break;
	            }

	            case "nodeAdded":
	            {
	                prop = value.localName();
	                newValue = value;
	                break;
	            }

	            case "nodeChanged":
	            {
	                prop = value.localName();
	                oldValue = detail;
	                newValue = value;
	                break;
	            }

	            case "nodeRemoved":
	            {
	                prop = value.localName();
	                oldValue = value;
	                break;
	            }

	            case "textSet":
	            {
	                prop = String(value);
	                newValue = String(target[prop]);
	                oldValue = detail;
	                break;
	            }

	            default:
				{
	                break;
				}
	        }
        }
		else if (type == "textSet")
		{
			var par:* = target.parent();
			if (par != undefined)
			{
				var gpar:* = par.parent();
				if (gpar === currentTarget)
				{
					prop = par.name().toString();
					newValue = value;
					oldValue = detail;
				}
			}
		}

        itemUpdated(currentTarget, prop, oldValue, newValue);
    }
    
    /** 
     *  This is called by addItemAt and when the source is initially
     *  assigned.
     */
    protected function startTrackUpdates(item:Object, uid:String):void
    {
        XMLNotifier.getInstance().watchXML(item, this, uid);
    }
    
    /** 
     *  This is called by removeItemAt, removeAll, and before a new
     *  source is assigned.
     */
    protected function stopTrackUpdates(item:Object):void
    {
        XMLNotifier.getInstance().unwatchXML(item, this);
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
    
	/**
	 *  non-zero if we're processing an addItem or removeItem
	 */
	private var _busy:int = 0;

	private var seedUID:String;

	private var uidCounter:int = 0;
        
}

}
