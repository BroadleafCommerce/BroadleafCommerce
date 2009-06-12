////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

// AdobePatentID="B518"
// AdobePatentID="B519"

package mx.collections
{
	import mx.collections.IViewCursor;
	import mx.collections.Sort;
	import flash.events.Event;
	import mx.collections.ICollectionView;
	import mx.collections.errors.CollectionViewError;
	import mx.collections.errors.CursorError;
	import mx.events.CollectionEvent;
	import mx.events.CollectionEventKind;
	import mx.core.mx_internal;
	import mx.resources.IResourceManager;
	import mx.resources.ResourceManager;
	import mx.utils.StringUtil;
	import mx.events.PropertyChangeEvent;
	import flash.utils.Dictionary;

	use namespace mx_internal;

    [ResourceBundle("collections")]

	/**
	 * @private
	 *  The ModifiedCollectionView class wraps a ListCollectionView object in order 
	 *  to provide control over when removed, added, and replaced items are actually
	 *  shown. It is used by list data change effects in order to determine the start
	 *  and end state for effects after changes occur in a collection.
	 * 
	 *  Although it is marked as implementing ICollectionView for interface 
	 *  compatibility reasons, many of the properties and methods aren't 
	 *  implemented.
	 */
	public class ModifiedCollectionView implements ICollectionView
	{

	    include "../core/Version.as";
	
	    //--------------------------------------------------------------------------
	    //
	    //  Class constants
	    //
	    //--------------------------------------------------------------------------
	
		public static const REMOVED:String = "removed";
		public static const ADDED:String = "added";
		public static const REPLACED:String = "replaced";
		public static const REPLACEMENT:String = "replacement";
			
	    //--------------------------------------------------------------------------
	    //
	    //  Constructor
	    //
	    //--------------------------------------------------------------------------

		public function ModifiedCollectionView(list:ICollectionView)
		{
			super();
			this.list = list;
		}

	    //--------------------------------------------------------------------------
	    //
	    //  Private variables
	    //
	    //--------------------------------------------------------------------------

		/**
		 *  @private
		 *  Used for accessing localized Error messages.
		 */
		private var resourceManager:IResourceManager =
										ResourceManager.getInstance();
	
	    /**
	     *  @private
	     *  The underlying collection that this view is wrapping.
	     */
		private var list:ICollectionView;
		
	    /**
	     *  @private
	     *  The number of items that have been added/removed from the
	     *  underlying collection which are being ignored/preserved in this
	     *  collection. Any addition to the underlying collection decrements
	     *  this value, any removal increments it.
	     */
		private var deltaLength:int = 0;

	    /**
	     *  @private
	     *  An array of adds/removes from the underlying collection which  
	     *  are being ignored/preserved in this wrapper. This elements in 
	     *  this array are CollectionModification objects storing changes,
	     *  and are kept in sorted order, according to the location in the 
	     *  underlying collection where they occurred.
	     */
		private var deltas:Array = [];

	    private var removedItems:Dictionary = new Dictionary(true);
	    private var addedItems:Dictionary = new Dictionary(true);
	    private var replacedItems:Dictionary = new Dictionary(true);
	    private var replacementItems:Dictionary = new Dictionary(true);
		
	    //--------------------------------------------------------------------------
	    //
	    // ICollectionView Properties
	    //
	    //--------------------------------------------------------------------------

	    /**
	     *  @private
	     */
		public function get length():int
		{
			return list.length + (_showPreserved ? deltaLength : 0);
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function get filterFunction():Function
		{
			return null;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function set filterFunction(value:Function):void
		{
		}

	    //--------------------------------------------------------------------------
	    //
	    // ICollectionView Methods
	    //
	    //--------------------------------------------------------------------------

	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function disableAutoUpdate():void
		{
		}
		
		public function createCursor():IViewCursor
		{
			var internalCursor:IViewCursor = list.createCursor();
			var current:Object = internalCursor.current;
			return new ModifiedCollectionViewCursor(this,internalCursor,current);
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function contains(item:Object):Boolean
		{
			return false;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function get sort():Sort
		{
			return null;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function set sort(value:Sort):void
		{
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function itemUpdated(item:Object, property:Object = null, oldValue:Object = null, newValue:Object = null):void
		{
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function refresh():Boolean
		{
			return false;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function enableAutoUpdate():void
		{
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function hasEventListener(type:String):Boolean
		{
			return false;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function willTrigger(type:String):Boolean
		{
			return false;
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function addEventListener(type:String, listener:Function, useCapture:Boolean = false, priority:int = 0.0, useWeakReference:Boolean = false):void
		{
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function removeEventListener(type:String, listener:Function, useCapture:Boolean = false):void
		{
		}
		
	    /**
	     *  Not supported by ModifiedCollectionView
	     */
		public function dispatchEvent(event:Event):Boolean
		{
			return false;
		}

	    /**
	     *  Create a bookmark for this view.  This method is called by
	     *  ModifiedCollectionViewCursor.
	     *
	     *  @param ModifiedCollectionViewCursor The cursor for which to create the bookmark
	     * 
	     *  @return a new bookmark instance
	     * 
	     *  @throws a CollectionViewError if the index is out of bounds
	     */
	    mx_internal function getBookmark(mcvCursor:ModifiedCollectionViewCursor):ModifiedCollectionViewBookmark
	    {
	    	var index:int = mcvCursor.mx_internal::currentIndex;
	    	
	        if (index < 0 || index > length)
	        {
	        	var message:String = resourceManager.getString(
	        		"collections", "invalidIndex", [ index ]);
	            throw new CollectionViewError(message);
	        }
	
	        var value:Object = mcvCursor.current;

	        return new ModifiedCollectionViewBookmark(value,
		                                              this,
		                                              0,
		                                              index,
		                                              mcvCursor.internalCursor.bookmark,
		                                              mcvCursor.internalIndex);
	
	    }
	
	    /**
	     *  Given a bookmark find the location for the value.  If the
	     *  view has been modified since the bookmark was created attempt
	     *  to relocate the item.  If the bookmark represents an item
	     *  that is no longer in the view (removed or filtered out) return
	     *  -1.
	     *
	     *  @param bookmark the bookmark to locate
	     * 
	     *  @return the new location of the bookmark, -1 if not in the view anymore
	     * 
	     *  @throws CollectionViewError if the bookmark is invalid
	     */
	    mx_internal function getBookmarkIndex(bookmark:CursorBookmark):int
	    {
	        if (!(bookmark is ModifiedCollectionViewBookmark)
	            || ModifiedCollectionViewBookmark(bookmark).view != this)
	        {
	            var message:String = resourceManager.getString(
	            	"collections", "bookmarkNotFound");
	            throw new CollectionViewError(message);
	        }
	
	        var bm:ModifiedCollectionViewBookmark = ModifiedCollectionViewBookmark(bookmark);
	
	        return bm.index;
	    }

		private var itemWrappersByIndex:Array = [];
		private var itemWrappersByCollectionMod:Dictionary = new Dictionary(true);

		
		/**
		 *  Given a cursor, and an index, return a wrapped version of the item at
		 *  that index. The item may come either from the underlying collection
		 *  (retrieved through the cursor) or from the annotations stored within
		 *  the modifiedCollectionView.
		 * 
		 *  This method also adjusts the cursor as necessary.
		 * 
		 */
		mx_internal function getWrappedItemUsingCursor(mcvCursor:ModifiedCollectionViewCursor, 
												  newIndex:int):Object
		{
			// iterate through collection modifications, determining
			// how many items have been added/removed before index
			var adjustedIndex:int = newIndex;
			var item:Object = null;
			var cm:CollectionModification = null;
			var mod0:CollectionModification;
			var isReplacement:Boolean = false;
			
			for (var j:int = 0; j < deltas.length; j++)
			{
				mod0 = deltas[j];
				if (adjustedIndex < mod0.index)
					break; 
				// THIS LOGIC SHOULD BE CONSOLIDATED WITH THE OTHER LOGIC VIA APPROPRIATE REFACTORING
				if (mod0.modificationType == CollectionModification.REPLACE)
				{
					// maybe we need _suppressAdded / _suppressRemoved?
					// the semantics of _showAdded/_showRemoved are maybe a bit screwed up.
					// In fact, the logic below is relying on mod being initialized in a certain way, etc.
					if ((adjustedIndex == mod0.index) && mod0.showOldReplace && _showPreserved)
					{
						cm = mod0;
						break;
					}
					if ((adjustedIndex == mod0.index + 1) && mod0.showOldReplace && mod0.showNewReplace && _showPreserved)
					{
						adjustedIndex--;
						isReplacement = true;
						break;
					}
					if ((adjustedIndex == mod0.index ) && ((!mod0.showOldReplace && mod0.showNewReplace) || !_showPreserved))
					{
						isReplacement = true;
						break;
					}
					adjustedIndex -= mod0.modCount;
						
				}
				else if (isActive(mod0)) // ignoring is true after addItemAction/removeItemAction...though we probably will just remove from list then
				{
					if ((adjustedIndex == mod0.index) && mod0.isRemove)
					{
						cm = mod0;
						break;
					}
					else if (adjustedIndex >= mod0.index)
						adjustedIndex -= mod0.modCount;
				}
			}

			if (cm)
				item = cm.item;
			else
			{
				// We have to fetch the new item from cursor into the underlying collection
				// We'll also adjust the index we maintain for that cursor
				mcvCursor.internalCursor.seek(CursorBookmark.CURRENT,adjustedIndex - mcvCursor.internalIndex);
				item = mcvCursor.internalCursor.current;
				mcvCursor.internalIndex = adjustedIndex;
			}
			
			var itemWrapper:Object;
			if (mod0 && (adjustedIndex == mod0.index) && (mod0.modificationType == CollectionModification.ADD))
				itemWrapper = getUniqueItemWrapper(item,mod0,adjustedIndex)
			else
				itemWrapper = getUniqueItemWrapper(item,cm,adjustedIndex);
				
			return itemWrapper;
		}
				
	    //--------------------------------------------------------------------------
	    //
	    // Public properties
	    //
	    //--------------------------------------------------------------------------
		
		private var _showPreserved:Boolean = false;

		/**
		 *  Enables or suppresses the ability of the collection to show
		 *  previous or "preserved" state. If set to false, the
		 *  ModifiedCollectionView will present a view equivalent to the
		 *  current state of the ListCollectionView it is wrapping. If 
		 *  set to true, it will present a view of the ListCollectionView
		 *  ignoring any changes that have been integrated into the 
		 *  ModifiedCollectionView.
		 */		
		public function get showPreservedState():Boolean
		{
			return _showPreserved;
		}

		public function set showPreservedState(show:Boolean):void
		{
			_showPreserved = show;
		}
		

	    //--------------------------------------------------------------------------
	    //
	    // Public methods
	    //
	    //--------------------------------------------------------------------------

		public function getSemantics(itemWrapper:ItemWrapper):String
		{
			if (removedItems[itemWrapper])
				return ModifiedCollectionView.REMOVED;
			if (addedItems[itemWrapper])
				return ModifiedCollectionView.ADDED;
			// TODO these won't be quite right yet (won't generate two separate item wrappers for replaced & replacement)
			if (replacedItems[itemWrapper])
				return ModifiedCollectionView.REPLACED;
			if (replacementItems[itemWrapper])
				return ModifiedCollectionView.REPLACEMENT;
			return null;
		}
		
	    /**
	     *  Processes a collection event generated by the underlying view. If the
	     *  event is of type ADD, REMOVE, or REPLACE, it is integrated so that
	     *  its effects are ignored if showPreserved is set to true.
	     * 
	     *  @param event A CollectionEvent generated by the ListCollectionView this
	     *  ModifiedCollectionView is wrapping.
	     * 
	     *  @param startItemIndex
	     * 
	     *  @param endItemIndex
	     */
	    public function processCollectionEvent(event:CollectionEvent, startItemIndex:int, endItemIndex:int):void
	    {
	    	switch (event.kind)
	    	{
	    		case CollectionEventKind.ADD:
	    			integrateAddedElements(event, startItemIndex, endItemIndex);
	    			break;
	    		case CollectionEventKind.REMOVE:
	    			integrateRemovedElements(event, startItemIndex, endItemIndex);
	    			break;
	    		case CollectionEventKind.REPLACE:
	    			integrateReplacedElements(event, startItemIndex, endItemIndex);
	    			break;
	    	}
	    }
	    	    		
		/**
		 *  Stops showing an item that has been removed or replaced
		 *  in the underlying ListCollectionView but which is still
		 *  being shown by the ModifiedCollectionView.
		 * 
		 *  This function is meant to be called by ListBase in response to
		 *  a RemoveItemAction effect.
		 * 
		 *  @param item The item to remove from the collection. This must have
		 *  been removed from the original collection.
		 */
		public function removeItem(itemWrapper:ItemWrapper):void
		{
			var mod:CollectionModification = removedItems[itemWrapper] as CollectionModification;

			if (!mod)
			{
				mod = replacedItems[itemWrapper] as CollectionModification;
				if (mod)
				{
					delete replacedItems[itemWrapper];
					// do this here?? don't think so
					// replacementItems[list.getItemAt(mod.index)] = null;

					mod.stopShowingReplacedValue();
					// need more error checking here
					deltaLength--;

					// if we're already showing the replacement value, we
					// can remove this modification
					if (mod.modCount == 0) 
						removeModification(mod);
				}
			}
			else if (removeModification(mod))
			{
				delete removedItems[itemWrapper];
				deltaLength--;
			}
		}
		
		/**
		 *  Starts showing an item that has been added to the 
		 *  underlying ListCollectionView but which is still
		 *  being ignored by the ModifiedCollectionView.
		 * 
		 *  This function is meant to be called by ListBase in response to
		 *  a AddItemAction effect.
		 * 
		 *  @param item The item to start showing in the collection. This must 
		 *  have been added to the original collection.
		 */
		public function addItem(itemWrapper:ItemWrapper):void
		{
			// Don't remove entries from addedItems and replacementItems
			// here...we want to be able to know the semantics of the
			// added/replaced items after the fact.
			
			var mod:CollectionModification = addedItems[itemWrapper] as CollectionModification;
			
			// if this is not an added item, it might be a replacement item
			if (!mod)
			{
				mod = replacementItems[itemWrapper] as CollectionModification;
				if (mod)
				{
					// need more error checking here
					mod.startShowingReplacementValue();
					deltaLength++;
					if (mod.modCount == 0)
						removeModification(mod);
				}
			}
			else if (removeModification(mod))
				deltaLength++;
		}
		
	    //--------------------------------------------------------------------------
	    //
	    // Private methods
	    //
	    //--------------------------------------------------------------------------

		/**
		 *  @private
		 *  Determines if a change to a collection should be considered
		 *  active or suppressed.
		 * 
		 *  Currently, this is just based on the <code>showPreserved</code>
		 *  property.
		 */
		private function isActive(mod:CollectionModification):Boolean
		{
			// might eventually have more individual item processing here
			// if we're showing removed, we have to include this modificatoin
			// if we're showing added, we have to *exclude* it
			return _showPreserved;
		}


		/**
		 *  @private
		 * 
		 *  Removes a particular CollectionModification from the
		 *  deltas array.
		 */
		private function removeModification(mod:CollectionModification):Boolean
		{
			for (var i:int = 0; i < deltas.length; i++)
			{
				if (deltas[i] == mod)
				{
					deltas.splice(i,1);
					return true;
				}
			}
			return false;
		}

		/**
		 *  @private
		 * 
		 *  Does the work of modifying the object to handle a collectionEvent of 
		 *  collectionEventKind.REMOVE so that the removal can be ignored
		 */		
	    private function integrateRemovedElements(event:CollectionEvent, startItemIndex:int, endItemIndex:int):void
	    {
	    	var i:int = 0;
	    	var j:int = 0;
	    	var ignoredElementCount:int = 0;
//	    	var inserted:Boolean = false;
	    	var insertCount:int = event.items.length;
	    	// offset must be used when looking at mod indexes
	    	var offset:int = 0;
	    	
	    	while (i < deltas.length && j < insertCount)
	    	{
				var mod:CollectionModification = CollectionModification(deltas[i]);
				var newMod:CollectionModification = new CollectionModification(event.location, event.items[j],CollectionModification.REMOVE);

				removedItems[getUniqueItemWrapper(event.items[j],newMod,0)] = newMod;

				if (offset != 0)
					mod.index += offset;

				// we want to insert after all deletes at this location but
				// before all adds
				// Adds coinciding with the deleted elements just become deletes
				// (actually, want this behavior to depend on removeItemAction)
				if ((mod.isRemove && mod.index <= newMod.index) || (!mod.isRemove && mod.index < newMod.index))
				{
					i++;
					continue;
				}
				// we are deleting a previously added element
				// we want to mark it as deleted and remove
				else if ((!mod.isRemove) && (mod.index == newMod.index))
				{
					deltas.splice(i+j,1);
				}
				else
				{
					// this is a deletion or marked added element at a point 
					// after where we are adding elements,
					// so we'll just splice in our deleted item.
					deltas.splice(i+j,0,newMod);
					i++;
				}
				offset--;
				j++;
	    	}

			// when we get to this point, either we've inserted all the mods
			// OR we're at the end of the list. So only one of the following
			// two loops will be executed
			
	    	while (i < deltas.length)
	    	{
	    		mod = CollectionModification(deltas[i++]);
	    		mod.index += offset;
	    	}

	    	while (j < insertCount)
	    	{
				deltas.push(newMod = new CollectionModification(event.location, event.items[j],CollectionModification.REMOVE));
				removedItems[getUniqueItemWrapper(event.items[j],newMod,0)] = newMod;
				j++;
	    	}

			deltaLength += event.items.length - ignoredElementCount;
	    }

		/**
		 *  @private
		 * 
		 *  Does the work of modifying the object to handle a collectionEvent of 
		 *  collectionEventKind.ADD so that the addition can be ignored
		 */		
	    private function integrateAddedElements(event:CollectionEvent, startItemIndex:int, endItemIndex:int):void
	    {
	    	var i:int = 0;
	    	var j:int = 0;
	    	var inserted:Boolean = false;
	    	var insertCount:int = event.items.length;
	    	// offset must be used when looking at mod indexes
	    	var offset:int = 0;
	    	
	    	// adding is easier than deleting...we just find the
	    	// right place in our delta array, splice all the modifications,
	    	// and update the indices of subsequent modifications
	    	
	    	while (i < deltas.length && j < insertCount)
	    	{
				var mod:CollectionModification = CollectionModification(deltas[i]);
				var newMod:CollectionModification = new CollectionModification(event.location + j, null,CollectionModification.ADD);
				addedItems[getUniqueItemWrapper(event.items[j],newMod,0)] = newMod;
				

				// we want to insert after all deletes at this location but
				// before all adds
				// Adds coinciding with the deleted elements just become deletes
				// (actually, want this behavior to depend on removeItemAction)
				if ((mod.isRemove && mod.index <= newMod.index) || (!mod.isRemove && mod.index < newMod.index))
				{
					i++;
					continue;
				}

				// this is a deletion or marked added element at a point 
				// after where we are adding elements,
				// so we'll just splice in our deleted item.
				deltas.splice(i+j,0,newMod);

				offset++;
				j++;
				i++;
	    	}

			// when we get to this point, either we've inserted all the mods
			// OR we're at the end of the list. So only one of the following
			// two loops will be executed
			
	    	while (i < deltas.length)
	    	{
	    		mod = CollectionModification(deltas[i++]);
	    		mod.index += offset;
	    	}

	    	while (j < insertCount)
	    	{
				deltas.push(newMod = new CollectionModification(event.location + j, null,CollectionModification.ADD));
				addedItems[getUniqueItemWrapper(event.items[j],newMod,0)] = newMod;
				j++;
	    	}

			deltaLength -= event.items.length;
	    }
	    
		/**
		 *  @private
		 * 
		 *  Does the work of modifying the object to handle a collectionEvent of 
		 *  collectionEventKind.REPLACE so that the replacement can be ignored
		 */		
	    private function integrateReplacedElements(event:CollectionEvent, startItemIndex:int, endItemIndex:int):void
	    {
	    	var i:int = 0;
	    	var j:int = 0;
	    	var inserted:Boolean = false;
	    	var insertCount:int = event.items.length;
	    	// offset must be used when looking at mod indexes
	    	var offset:int = 0;
	    	
	    	// adding is easier than deleting...we just find the
	    	// right place in our delta array, splice all the modifications,
	    	// and update the indices of subsequent modifications
	    	
	    	while (i < deltas.length && j < insertCount)
	    	{
	    		var oldItem:Object = PropertyChangeEvent(event.items[j]).oldValue;
	    		var newItem:Object = PropertyChangeEvent(event.items[j]).newValue;
	    		
				var mod:CollectionModification = CollectionModification(deltas[i]);
				var newMod:CollectionModification = new CollectionModification(event.location + j, oldItem,CollectionModification.REPLACE);
				
				// we want to insert after all deletes at this location but
				// before all adds
				// Adds coinciding with the deleted elements just become deletes
				// (actually, want this behavior to depend on removeItemAction)
				if ((mod.isRemove && mod.index <= newMod.index) || (!mod.isRemove && mod.index < newMod.index))
				{
					i++;
					continue;
				}

				if (((mod.modificationType == CollectionModification.ADD) || (mod.modificationType == CollectionModification.REPLACE))
					&& (mod.index == newMod.index))
				{
					// we've founded an added element that is being replaced, or a replaced element that
					// is being replaced again. We're just going to ignore this modification, so the existing effect
					// if any should show the replacement element being added/replaced, rather than playing a new
					// effect. (Not positive this will work.)
					i++;
					j++;
					// might also need to do some cleanup here, if we're indexing our modifications

					continue;
				}
				// this is a deletion or marked added element at a point 
				// after where we are adding elements,
				// so we'll just splice in our replacement item.
				deltas.splice(i+j,0,newMod);
				replacedItems[getUniqueItemWrapper(oldItem,newMod,event.location + j)] = newMod;
				replacementItems[getUniqueItemWrapper(newItem,newMod,event.location + j,true)] = newMod;

				j++;
				i++;
	    	}

			// when we get to this point, either we've inserted all the mods
			// OR we're at the end of the list. So only one of the following
			// two loops will be executed
			
	    	while (j < insertCount)
	    	{
	    		oldItem = PropertyChangeEvent(event.items[j]).oldValue;
	    		newItem = PropertyChangeEvent(event.items[j]).newValue;
				deltas.push(newMod = new CollectionModification(event.location + j, oldItem,CollectionModification.REPLACE));
				replacedItems[getUniqueItemWrapper(oldItem,newMod,event.location + j)] = newMod;
				replacementItems[getUniqueItemWrapper(newItem,newMod,event.location + j,true)] = newMod;
				j++;
	    	}
	    }
	    
	    private function getUniqueItemWrapper(item:Object,mod:CollectionModification, index:int, isReplacement:Boolean = false):Object
	    {
	    	if (mod && (mod.isRemove || (mod.modificationType == CollectionModification.REPLACE && !isReplacement)))
	    	{
	    		if (!itemWrappersByCollectionMod[mod])
	    			itemWrappersByCollectionMod[mod] = new ItemWrapper(item);
	    		return itemWrappersByCollectionMod[mod];	
	    	}
			
			// TODO This is kind of a hack...clean up the code to simplify
			if (mod && (mod.modificationType == CollectionModification.ADD))
				index = mod.index;

    		if (!itemWrappersByIndex[index])
    			itemWrappersByIndex[index] = new ItemWrapper(item);
    		return itemWrappersByIndex[index];
	    }
	}
}


import mx.collections.ModifiedCollectionView;
import mx.collections.CursorBookmark;
import flash.events.EventDispatcher;
import mx.collections.IViewCursor;
import mx.events.CollectionEvent;
import mx.collections.ICollectionView;
import mx.core.mx_internal;
import mx.collections.errors.CursorError;
import mx.collections.errors.CollectionViewError;
import mx.collections.errors.ItemPendingError;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;
import mx.events.CollectionEventKind;
import mx.events.FlexEvent;
import mx.collections.errors.CursorError;
import flash.display.InteractiveObject;

use namespace mx_internal;

/**
 *  Dispatched whenever the cursor position is updated.
 *
 *  @eventType mx.events.FlexEvent.CURSOR_UPDATE
 */
[Event(name="cursorUpdate", type="mx.events.FlexEvent")]

[ResourceBundle("collections")]

/**
 *  @private
 *  The internal implementation of cursor for the ModifiedCollectionView.
 *  This cursor wraps a cursor to the underlying collection, and maintains
 *  additional state.
 */
class ModifiedCollectionViewCursor extends EventDispatcher implements IViewCursor
{
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static const BEFORE_FIRST_INDEX:int = -1;

    /**
     *  @private
     */
    private static const AFTER_LAST_INDEX:int = -2;



    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  Creates the cursor for the view.
     * 
     *  @param view The ModifiedCollectionView for which this is a cursor.
     * 
     *  @param cursor A cursor into the underlying collection wrapped by the
     *  ModifiedCollectionView.
     * 
     *  @param current The item this cursor is currently pointing at.
     */
    public function ModifiedCollectionViewCursor(view:ModifiedCollectionView, cursor:IViewCursor, current:Object)
    {
        super();

        _view = view;
        
        internalCursor = cursor;

        if (cursor.beforeFirst && !current)
        	internalIndex = BEFORE_FIRST_INDEX;
        else if (cursor.afterLast && !current)
        	internalIndex = AFTER_LAST_INDEX;
        else
        	internalIndex = 0;

		// This probably makes sense...
		// _view.addEventListener(CollectionEvent.COLLECTION_CHANGE, collectionEventHandler, false, 0, true);
        currentIndex = view.length > 0 ? 0 : AFTER_LAST_INDEX;
        if (currentIndex == 0)
        {
            try
            {
                setCurrent(current,false);
            }
            catch(e:ItemPendingError)
            {
                currentIndex = BEFORE_FIRST_INDEX;
                setCurrent(null, false);
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var _view:ModifiedCollectionView;

    /**
     *  @private
     *  An cursor into the underlying collection wrapped by the Modified
     *  collection view.
     */
    public var internalCursor:IViewCursor;

    /**
     *  @private
     *  The current overall index into the ModifiedCollectionView.
     */
    mx_internal var currentIndex:int;

    /**
     *  @private
     *  The position of the internalCursor in its ICollectionView.
     *  This is not part of the IViewCursor interface, so we
     *  maintain it independently.
     */
    public var internalIndex:int;

    /**
     *  @private
     */
    private var currentValue:Object;

    /**
     *  @private
     */
    private var invalid:Boolean;

	/**
	 *  @private
	 *  Used for accessing localized Error messages.
	 */
	private var resourceManager:IResourceManager =
									ResourceManager.getInstance();

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  Get a reference to the view that this cursor is associated with.
     *  @return the associated <code>ICollectionView</code>
     */
    public function get view():ICollectionView
    {
        checkValid();
        return _view;
    }

    [Bindable("cursorUpdate")]
    /**
     *  Provides access the object at the current location referenced by
     *  this cursor within the source collection.
     *  If the cursor is beyond the ends of the collection (beforeFirst,
     *  afterLast) this will return <code>null</code>.
     *
     *  @see mx.collections.IViewCursor#moveNext
     *  @see mx.collections.IViewCursor#movePrevious
     *  @see mx.collections.IViewCursor#seek
     *  @see mx.collections.IViewCursor#beforeFirst
     *  @see mx.collections.IViewCursor#afterLast
     */
    public function get current():Object
    {
        checkValid();

        return currentValue;
    }

    [Bindable("cursorUpdate")]
    
    /**
     *  Provides access to the bookmark of the item returned by the
     *  <code>current</code> property.
     *  The bookmark can be used to move the cursor to a previously visited
     *  item, or one relative to it (see the <code>seek()</code> method for
     *  more information).
     *
     *  @see mx.collections.IViewCursor#current
     *  @see mx.collections.IViewCursor#seek
     */
    public function get bookmark():CursorBookmark
    {
        checkValid();
        if (view.length == 0 || beforeFirst) 
        	return CursorBookmark.FIRST;
        else if (afterLast) 
        	return CursorBookmark.LAST;

        return ModifiedCollectionView(view).getBookmark(this);
    }


    [Bindable("cursorUpdate")]
    /**
     * true if the current is sitting before the first item in the view.
     * If the ICollectionView is empty (length == 0) this will always
     * be true.
     */
    public function get beforeFirst():Boolean
    {
        checkValid();
        return currentIndex == BEFORE_FIRST_INDEX || view.length == 0;
    }


    [Bindable("cursorUpdate")]
    /**
     * true if the cursor is sitting after the last item in the view.
     * If the ICollectionView is empty (length == 0) this will always
     * be true.
     */
    public function get afterLast():Boolean
    {
        checkValid();
        return currentIndex == AFTER_LAST_INDEX || view.length == 0;
    }

    /**
     *  Finds the item with the specified properties within the
     *  collection and positions the cursor on that item.
     *  If the item can not be found no change to the current location will be
     *  made.
     *  <code>findAny()</code> can only be called on sorted views, if the view
     *  isn't sorted a <code>CursorError</code> will be thrown.
     *  <p>
     *  If the associated collection is remote, and not all of the items have
     *  been cached locally this method will begin an asynchronous fetch from the
     *  remote collection, or if one is already in progress wait for it to
     *  complete before making another fetch request.
     *  If multiple items can match the search criteria then the item found is
     *  non-deterministic.
     *  If it is important to find the first or last occurrence of an item in a
     *  non-unique index use the <code>findFirst()</code> or
     *  <code>findLast()</code>.
     *  The values specified must be configured as name-value pairs, as in an
     *  associative array (or the actual object to search for).
     *  The values of the names specified must match those properties specified in
     *  the sort. for example
     *  If properties "x", "y", and "z" are the in the current index, the values
     *  specified should be {x:x-value, y:y-value,z:z-value}.
     *  When all of the data is local this method will return <code>true</code> if
     *  the item can be found and false otherwise.
     *  If the data is not local and an asynchronous operation must be performed,
     *  an <code>ItemPendingError</code> will be thrown.
     *
     *  @see mx.collections.IViewCursor#findFirst
     *  @see mx.collections.IViewCursor#findLast
     *  @see mx.collections.errors.ItemPendingError
     */
    public function findAny(values:Object):Boolean
    {
    	// not implemented
    	return false;
    }

    /**
     *  Finds the first item with the specified properties
     *  within the collection and positions the cursor on that item.
     *  If the item can not be found no change to the current location will be
     *  made.
     *  <code>findFirst()</code> can only be called on sorted views, if the view
     *  isn't sorted a <code>CursorError</code> will be thrown.
     *  <p>
     *  If the associated collection is remote, and not all of the items have been
     *  cached locally this method will begin an asynchronous fetch from the
     *  remote collection, or if one is already in progress wait for it to
     *  complete before making another fetch request.
     *  If it is not important to find the first occurrence of an item in a
     *  non-unique index use <code>findAny()</code> as it may be a little faster.
     *  The values specified must be configured as name-value pairs, as in an
     *  associative array (or the actual object to search for).
     *  The values of the names specified must match those properties specified in
     *  the sort. for example If properties "x", "y", and "z" are the in the current
     *  index, the values specified should be {x:x-value, y:y-value,z:z-value}.
     *  When all of the data is local this method will
     *  return <code>true</code> if the item can be found and false otherwise.
     *  If the data is not local and an asynchronous operation must be performed,
     *  an <code>ItemPendingError</code> will be thrown.
     *
     *  @see mx.collections.IViewCursor#findAny
     *  @see mx.collections.IViewCursor#findLast
     *  @see mx.collections.errors.ItemPendingError
     */
     public function findFirst(values:Object):Boolean
    {
    	// not implemented
    	return false;
    }


    /**
     *  Finds the last item with the specified properties
     *  within the collection and positions the cursor on that item.
     *  If the item can not be found no change to the current location will be
     *  made.
     *  <code>findLast()</code> can only be called on sorted views, if the view
     *  isn't sorted a <code>CursorError</code> will be thrown.
     *  <p>
     *  If the associated collection is remote, and not all of the items have been
     *  cached locally this method will begin an asynchronous fetch from the
     *  remote collection, or if one is already in progress wait for it to
     *  complete before making another fetch request.
     *  If it is not important to find the last occurrence of an item in a
     *  non-unique index use <code>findAny()</code> as it may be a little faster.
     *  The values specified must be configured as  name-value pairs, as in an
     *  associative array (or the actual object to search for).
     *  The values of the names specified must match those properties specified in
     *  the sort. for example If properties "x", "y", and "z" are the in the current
     *  index, the values specified should be {x:x-value, y:y-value,z:z-value}.
     *  When all of the data is local this method will
     *  return <code>true</code> if the item can be found and false otherwise.
     *  If the data is not local and an asynchronous operation must be performed,
     *  an <code>ItemPendingError</code> will be thrown.
     *
     *  @see mx.collections.IViewCursor#findAny
     *  @see mx.collections.IViewCursor#findFirst
     *  @see mx.collections.errors.ItemPendingError
     */
    public function findLast(values:Object):Boolean
    {
    	// not implemented
    	return false;
    }

    /**
     * Insert the specified item before the cursor's current position.
     * If the cursor is <code>afterLast</code> the insertion
     * will happen at the end of the View.  If the cursor is
     * <code>beforeFirst</code> on a non-empty view an error will be thrown.
     */
    public function insert(item:Object):void
    {
    	// not implemented
    }

    /**
     *  Moves the cursor to the next item within the collection. On success
     *  the <code>current</code> property will be updated to reference the object at this
     *  new location.  Returns true if current is valid, false if not (afterLast).
     *  If the data is not local and an asynchronous operation must be performed, an
     *  <code>ItemPendingError</code> will be thrown. See the ItemPendingError docs
     *  as well as the collections documentation for more information on using the
     *  ItemPendingError.
     *
     *  @return true if still in the list, false if current is now afterLast
     *
     *  @see mx.collections.IViewCursor#current
     *  @see mx.collections.IViewCursor#movePrevious
     *  @see mx.collections.errors.ItemPendingError
     *  @see mx.collections.events.ItemAvailableEvent
     *  @example
     *  <pre>
     *    var myArrayCollection:ICollectionView = new ArrayCollection(["Bobby", "Mark", "Trevor", "Jacey", "Tyler"]);
     *    var cursor:IViewCursor = myArrayCollection.createCursor();
     *    while (!cursor.afterLast)
     *    {
     *       trace(cursor.current);
     *       cursor.moveNext();
     *     }
     *  </pre>
     */
    public function moveNext():Boolean
    {
        //the afterLast getter checks validity and also checks length > 0
        if (afterLast)
        {
            return false;
        }
        // we can't set the index until we know that we can move there first.
        var tempIndex:int = beforeFirst ? 0 : currentIndex + 1;
        if (tempIndex >= view.length)
        {
            tempIndex = AFTER_LAST_INDEX;
            setCurrent(null);
        }
        else
        {
            //setCurrent(ModifiedCollectionView(view).getItemAt(tempIndex));
            setCurrent(ModifiedCollectionView(view).mx_internal::getWrappedItemUsingCursor(this,tempIndex));
        }
        currentIndex = tempIndex;
        return !afterLast;
    }

    /**
     *  Moves the cursor to the previous item within the collection. On success
     *  the <code>current</code> property will be updated to reference the object at this
     *  new location.  Returns true if current is valid, false if not (beforeFirst).
     *  If the data is not local and an asynchronous operation must be performed, an
     *  <code>ItemPendingError</code> will be thrown. See the ItemPendingError docs
     * as well as the collections documentation for more information on using the
     * ItemPendingError.
     *
     *  @return true if still in the list, false if current is now beforeFirst
     *
     *  @see mx.collections.IViewCursor#current
     *  @see mx.collections.IViewCursor#moveNext
     *  @see mx.collections.errors.ItemPendingError
     *  @see mx.collections.events.ItemAvailableEvent
     *  @example
     *  <pre>
     *     var myArrayCollection:ICollectionView = new ArrayCollection(["Bobby", "Mark", "Trevor", "Jacey", "Tyler"]);
     *     var cursor:ICursor = myArrayCollection.createCursor();
     *     cursor.seek(CursorBookmark.last);
     *     while (!cursor.beforeFirst)
     *     {
     *        trace(current);
     *        cursor.movePrevious();
     *      }
     *  </pre>
     */
    public function movePrevious():Boolean
    {
        //the afterLast getter checks validity and also checks length > 0
        if (beforeFirst)
        {
            return false;
        }
        // we can't set the index until we know that we can move there first
        var tempIndex:int = afterLast ? view.length - 1 : currentIndex - 1;
        if (tempIndex == -1)
        {
            tempIndex = BEFORE_FIRST_INDEX;
            setCurrent(null);
        }
        else
        {
            //setCurrent(ModifiedCollectionView(view).getItemAt(tempIndex));
            setCurrent(ModifiedCollectionView(view).mx_internal::getWrappedItemUsingCursor(this,tempIndex));
        }
        currentIndex = tempIndex;
        return !beforeFirst;
    }

    /**
     * Remove the current item and return it.  If the cursor is
     * <code>beforeFirst</code> or <code>afterLast</code> throw a
     * CursorError.
     */
    public function remove():Object
    {
    	// not implemented
    	return null;
    }

    /**
     *  Moves the cursor to a location at an offset from the specified
     *  bookmark.
     *  The offset can be negative in which case the cursor is positioned an
     *  offset number of items prior to the specified bookmark.
     *  If the associated collection is remote, and not all of the items have been
     *  cached locally this method will begin an asynchronous fetch from the
     *  remote collection.
     *
     *  If the data is not local and an asynchronous operation must be performed, an
     *  <code>ItemPendingError</code> will be thrown. See the ItemPendingError docs
     *  as well as the collections documentation for more information on using the
     *  ItemPendingError.
     *
     *
     *  @param bookmark <code>CursorBookmark</code> reference to marker information that
     *                 allows repositioning to a specific location.
     *           In addition to supplying a value returned from the <code>bookmark</code>
     *           property, there are three constant bookmark values that can be
     *           specified:
     *            <ul>
     *                <li><code>CursorBookmark.FIRST</code> - seek from
     *                the start (first element) of the collection</li>
     *                <li><code>CursorBookmark.CURRENT</code> - seek from
     *                the current position in the collection</li>
     *                <li><code>CursorBookmark.LAST</code> - seek from the
     *                end (last element) of the collection</li>
     *            </ul>
     *  @param offset indicates how far from the specified bookmark to seek.
     *           If the specified number is negative the cursor will attempt to
     *           move prior to the specified bookmark, if the offset specified is
     *           beyond the end points of the collection the cursor will be
     *           positioned off the end (beforeFirst or afterLast).
     *  @param prefetch indicates the intent to iterate in a specific direction once the
     *           seek operation completes, this reduces the number of required
     *           network round trips during a seek.
     *           If the iteration direction is known at the time of the request
     *           the appropriate amount of data can be returned ahead of the
     *           request to iterate it.
     */
    public function seek(bookmark:CursorBookmark, offset:int = 0, prefetch:int = 0):void
    {
//    	trace("MCVC seek, bookmark = " + bookmark.value + ", offset = " + offset);
        checkValid();
       	var message:String;
        
        if (view.length == 0)
        {
            currentIndex = AFTER_LAST_INDEX;
            setCurrent(null, false);
            return;
        }

        var newIndex:int = currentIndex;
        if (bookmark == CursorBookmark.FIRST)
        {
            newIndex = 0;
            internalIndex = 0;
            internalCursor.seek(CursorBookmark.FIRST);
        }
        else if (bookmark == CursorBookmark.LAST)
        {
            newIndex = view.length - 1;
            internalCursor.seek(CursorBookmark.LAST);
        }
        else if (bookmark != CursorBookmark.CURRENT)
        {
            try
            {
	        	var mcvBookmark:ModifiedCollectionViewBookmark = bookmark as ModifiedCollectionViewBookmark;
                newIndex = ModifiedCollectionView(view).getBookmarkIndex(bookmark);

                if (!mcvBookmark || (newIndex < 0))
                {
                    setCurrent(null);
                    message = resourceManager.getString(
                    	"collections", "bookmarkInvalid");
                    throw new CursorError(message);
                }
                internalIndex = mcvBookmark.internalIndex;
                internalCursor.seek(mcvBookmark.internalBookmark);
            }
            catch(bmError:CollectionViewError)
            {
                    message = resourceManager.getString(
                    	"collections", "bookmarkInvalid");
                    throw new CursorError(message);
            }
        }

        newIndex += offset;

        var newCurrent:Object = null;
        if (newIndex >= view.length)
        {
            currentIndex = AFTER_LAST_INDEX;
        }
        else if (newIndex < 0)
        {
            currentIndex = BEFORE_FIRST_INDEX;
        }
        else
        {
			newCurrent = ModifiedCollectionView(view).mx_internal::getWrappedItemUsingCursor(this,newIndex);
            //newCurrent = ModifiedCollectionView(view).getItemAt(newIndex);
            currentIndex = newIndex;
        }
        setCurrent(newCurrent);
    }

    //--------------------------------------------------------------------------
    //
    // Internal methods
    //
    //--------------------------------------------------------------------------

    private function checkValid():void
    {
        if (invalid)
        {
            var message:String = resourceManager.getString(
            	"collections", "invalidCursor");
            throw new CursorError(message);
        }
    }

    /**
     *  @private
     */
    private function setCurrent(value:Object, dispatch:Boolean = true):void
    {
        currentValue = value;

        if (dispatch)
            dispatchEvent(new FlexEvent(FlexEvent.CURSOR_UPDATE));
    }
		
}

/**
 *  @private
 *  Encapsulates the positional aspects of a cursor within an ModifiedCollectionView.
 *  Only the ModifiedCollectionView should construct this.
 */
class ModifiedCollectionViewBookmark extends CursorBookmark
{
    mx_internal var index:int;
    mx_internal var view:ModifiedCollectionView;
    mx_internal var viewRevision:int;
    // just as MCVCursor wraps a cursor into the underlying collection,
    // this class wraps a bookmark for the wrapped cursor
    mx_internal var internalBookmark:CursorBookmark;
    mx_internal var internalIndex:int;

    /**
     *  @private
     */
    public function ModifiedCollectionViewBookmark(value:Object,
                                               view:ModifiedCollectionView,
                                               viewRevision:int,
                                               index:int,
                                               internalBookmark:CursorBookmark,
                                               internalIndex:int)
    {
        super(value);
        this.view = view;
        this.viewRevision = viewRevision;
        this.index = index;
        this.internalBookmark = internalBookmark;
        this.internalIndex = internalIndex;
    }

    /**
     * Get the approximate index of the item represented by this bookmark
     * in its view.  If the item has been paged out this may throw an
     * ItemPendingError.  If the item is not in the current view -1 will be
     * returned.  This method may also return -1 if index-based location is not
     * possible.
     */
    override public function getViewIndex():int
    {
        return view.getBookmarkIndex(this);
    }
}

// for now, no delta encoding, and no coalescing of blocks.
// not clear if we'd ever have to split a block -- probably
// not, since after deleting elements you can't insert one
// into the middle of that block...we'll just define whether
// an added element shows up after or before the deleted elements
// (i.e., if we delete the item at position 3, then insert at position
// 3, we have to decide whether the order is deleted-added or added-deleted)

/**
 * @private
 *  Represents a single modification to a collection that a 
 *  ModifiedCollectionView can either use or ignore in order to
 *  present "before" and "after" views of the change.
 *  
 *  A CollectionModification represents a single element only
 *  (add/remove/replace)
 */
class CollectionModification
{
	public static const REMOVE:String = "remove";
	public static const ADD:String = "add";
	public static const REPLACE:String = "replace";
	

	public function CollectionModification(index:int, item:Object, modificationType:String)
	{
		super();
		
		this.index = index;
		this.modificationType = modificationType;
		if (modificationType != CollectionModification.ADD)
			this.item = item;
		
		if (modificationType == CollectionModification.REMOVE)
			_modCount = 1;
		else if (modificationType == CollectionModification.ADD)
			_modCount = -1;
		// replaces don't modify the count until we stop showing
		// the old element or start showing the new element
		// (if we do both, we're back to zero, and the CM can be
		// discarded)
	}

    /**
     * The point at which elements in the collection were removed or added
     * (More precisely, the index of the a current element in the collection 
     * to which this modification is attached.
     */
	public var index:int;

	/**
	 * Removed element, if applicable
	 */
	public var item:Object = null;
	
	public var modificationType:String = null;
	
	private var _modCount:int = 0;

	// shouldn't be public
	public var showOldReplace:Boolean = true;
	public var showNewReplace:Boolean = false;
	
	public function get isRemove():Boolean
	{
		return (modificationType == CollectionModification.REMOVE);
	}
	
	
	/**
	 * For CollectionModifications representing replaced elements
	 * in a collection, starts showing the replaced value.
	 * 
	 * For replaces, the original and replacement values may
	 * be shown independently.
	 */
	public function startShowingReplacementValue():void
	{
		showNewReplace = true;
		// should do some error checking here, in case this function is called twice
		_modCount++;
	}

	/**
	 * For CollectionModifications representing replaced elements
	 * in a collection, stops showing the replaced value.
	 * 
	 * For replaces, the original and replacement values may
	 * be shown independently.
	 */
	public function stopShowingReplacedValue():void
	{
		showOldReplace = false;
		// should do some error checking here, in case this function is called twice
		_modCount--;
	}
		
    /**
     * The number of removed elements being preserved in the modified collection,
     * minus the number of added elements not in the original collection
     */
	public function get modCount():int
	{
		return _modCount;
	}
}
