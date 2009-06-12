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

package mx.controls.treeClasses
{

import flash.events.EventDispatcher;
import flash.utils.Dictionary;
import mx.collections.CursorBookmark;
import mx.collections.ICollectionView;
import mx.collections.IList;
import mx.collections.IViewCursor;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;
import mx.utils.UIDUtil;

[ExcludeClass]

/**
 *  @private
 *  This class provides a heirarchical view (a tree-like) view of a standard collection. 
 *  The collection that this Cursor walks across need not be heirarchical but may be flat. 
 *  This class delegates to the ITreeDataDescriptor for information regarding the tree 
 *  structure of the data it walks across. 
 *  
 *  @see HierarchicalCollectionView
 */
public class HierarchicalViewCursor extends EventDispatcher
									implements IViewCursor
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
	 *  Constructor.
     */
    public function HierarchicalViewCursor(
							collection:HierarchicalCollectionView,
							model:ICollectionView,
							dataDescriptor:ITreeDataDescriptor,
							itemToUID:Function,
							openNodes:Object)
    {
		super();
		
		//fields
        this.collection = collection;
		this.model = model;
        this.dataDescriptor = dataDescriptor;
		this.itemToUID = itemToUID;
		this.openNodes = openNodes;

      
        //events
        collection.addEventListener(CollectionEvent.COLLECTION_CHANGE, collectionChangeHandler, false, 0, true);

		//init
		modelCursor = model.createCursor();
		
		//check to see if the model has more than one top level items
		if (model.length > 1)
			more = true;
		else 
			more = false;
			
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------
 
    /**
     *  @private
     */
    private var dataDescriptor:ITreeDataDescriptor;

    /**
     *  @private
	 *  Its effective offset into the "array".
     */
    private var currentIndex:int = 0;
    
    /**
     *  @private
	 *  The current index into the childNodes array
     */
    private var currentChildIndex:int = 0;
    
    /**
     *  @private
     *  The depth of the current node.
     */
    private var _currentDepth:int = 1; 
    
    /**
     *  @private
	 *  The current set of childNodes we are walking.
     */
	private var childNodes:Object = [];
	
	/**
	 *  @private
	 *  The current set of parentNodes that we have walked from
	 */
	private var parentNodes:Array = [];
    
    /**
     *  @private
	 *  A stack of the currentChildIndex in all parents of the currentNode.
     */
	private var childIndexStack:Array = [];

    /**
     *  @private
     *  The collection that stores the user data
     */
    private var model:ICollectionView;
    
    /**
     *  @private
     *  The collection wrapper of the model
     */
	private var collection:HierarchicalCollectionView;
    
    /**
     *  @private
     */
	private var openNodes:Object;
	
	/**
	 *  @private
	 *  Flag indicating model has more data
	 */ 
	private var more:Boolean;
	
	/**
	 *  @private
	 *  Cursor from the model
	 */ 
	private var modelCursor:IViewCursor;
	
	/**
	 *  @private
	 */
	private var itemToUID:Function;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
	// index
 	//----------------------------------
	/**
	 * @private
	 */
	public function get index():int
	{
		return currentIndex;
	}
	
    //----------------------------------
	//  bookmark
    //----------------------------------
    /**
     *  @private
     */
    public function get bookmark():CursorBookmark
    {
        return new CursorBookmark(currentIndex.toString());
    }

    //---------------------------------- 
	//  current
    //----------------------------------
    
    /**
     *  @private
     */
    public function get current():Object
    {
        try 
        {
        	if (childIndexStack.length == 0)
        	{
        		return modelCursor.current;
        	}
        	else
        	{
        		return childNodes[currentChildIndex];
        	}
 		}
        catch(e:RangeError)
		{
		}
		return null;
    }


	//---------------------------------
	// currentDepth
	//---------------------------------
	/**
	 *  @private
	 */
	public function get currentDepth():int
	{
		return _currentDepth;
	}


	//----------------------------------
	//  beforeFirst
	//----------------------------------
    public function get beforeFirst():Boolean
    {
    	return (currentIndex <= collection.length && current == null);
    }
    
	//----------------------------------
	//  afterLast
	//----------------------------------
    public function get afterLast():Boolean
    {
        return (currentIndex >= collection.length && current == null); 
    } 
    
	//----------------------------------
	//  view
	//----------------------------------
    public function get view():ICollectionView
    {
        return model;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------
	
    /**
     *  @private
     *  Determines if a node is visible on the screen
     */
    private function isItemVisible(node:Object):Boolean
    { 
    	var parentNode:Object = collection.getParentItem(node);
        while (parentNode != null)
        {
        	if (openNodes[itemToUID(parentNode)] == null)
                return false;
            
        	parentNode = collection.getParentItem(parentNode);
        }
	    return true;
    }

    /**
     *  @private
     *  Creates a stack of parent nodes by walking upwards
     */
    private function getParentStack(node:Object):Array
    {
        var nodeParents:Array = [];
        
		// Make a list of parents of the node.
        var obj:Object = collection.getParentItem(node);
        while (obj != null)
        {
            nodeParents.unshift(obj);
            obj = collection.getParentItem(obj);
        }
		return nodeParents;
    }

    /**
     *  @private
	 *  When something happens to the tree, find out if it happened
	 *  to children that occur before the current child in the tree walk.
     */
    private function isNodeBefore(node:Object, currentNode:Object):Boolean
    {
		if (currentNode == null)
			return false;

		var n:int;
		var i:int;
		var childNodes:ICollectionView;
		var sameParent:Object;

        var nodeParents:Array = getParentStack(node);
        var curParents:Array = getParentStack(currentNode);

        // Starting from the root, compare parents
		// (assumes the root must be the same).
        while (nodeParents.length && curParents.length)
        {
            var nodeParent:Object = nodeParents.shift();
            var curParent:Object = curParents.shift();
            
			// If not the same parentm then which ever one is first
			// in the child list is before the other.
            if (nodeParent != curParent)
            {
                // The last parent must be common.
                sameParent = collection.getParentItem(nodeParent);
                
				// Get the child list.
				if (sameParent != null && 
					dataDescriptor.isBranch(sameParent, model) &&
					dataDescriptor.hasChildren(sameParent, model))
            	{
					childNodes = dataDescriptor.getChildren(sameParent, model);
            	}
    			else
    			{
    				childNodes = model; 
    			}
				// Walk it until you hit one or the other.
                n = childNodes.length;
                {
                    var child:Object = childNodes[i];
                    
					if (child == curParent)
                        return false;

                    if (child == nodeParent)
                        return true;
                }
            }
        }

		if (nodeParents.length)
			node = nodeParents.shift();
		if (curParents.length)
			currentNode = curParents.shift();

        // If we get here, they have the same parentage or one or both
		// had a root parent. Who's first?
		childNodes = model; 
        n = childNodes.length;
		for (i = 0; i < n; i++)
        {
            child = childNodes[i];

            if (child == currentNode)
                return false;

            if (child == node)
                return true;
        }
        return false;
    }

    /**
     *  @private
     */
    public function findAny(values:Object):Boolean
    {
        seek(CursorBookmark.FIRST);
        
		var done:Boolean = false;
        while (!done)
        {
            var o:Object = dataDescriptor.getData(current);
            
			var matches:Boolean = true;
            for (var p:String in values)
            {
                if (o[p] != values[p])
                {
                    matches = false;
                    break;
                }
            }

            if (matches)
                return true;

            done = moveNext();
        }

        return false;
    }

    /**
     *  @private
     */
    public function findFirst(values:Object):Boolean
    {
        return findAny(values);
    }

    /**
     *  @private
     */
    public function findLast(values:Object):Boolean
    {
        seek(CursorBookmark.LAST);
        
		var done:Boolean = false;
        while (!done)
        {
            var o:Object = current; 
            
			var matches:Boolean = true;
            for (var p:String in values)
            {
                if (o[p] != values[p])
                {
                    matches = false;
                    break;
                }
            }
            
			if (matches)
                return true;

            done = movePrevious();
        }

        return false;
    }


    /**
     *  @private
     *  Move one node forward from current.  
     *  This may include moving up or down one or more levels.
     */
    public function moveNext():Boolean 
    {
    	var currentNode:Object = current;
        //if there is no currentNode then we cant move forward and must be off the ends
    	if (currentNode == null) 
    		return false; 
    	
		var uid:String = itemToUID(currentNode);
		if (!collection.parentMap.hasOwnProperty(uid))
			collection.parentMap[uid] = parentNodes[parentNodes.length - 1];

		// If current node is a branch and is open, the first child is our next item so return it
		if (openNodes[uid] &&
			dataDescriptor.isBranch(currentNode, model) && 
			dataDescriptor.hasChildren(currentNode, model))
	    {
	        	var previousChildNodes:Object = childNodes;
	            childNodes = dataDescriptor.getChildren(currentNode, model);
				if (childNodes.length > 0)
				{
					childIndexStack.push(currentChildIndex);
					parentNodes.push(currentNode);
					currentChildIndex = 0;
					currentNode = childNodes[0];
					currentIndex++;
					_currentDepth++;
					return true;
				}
				else
					childNodes = previousChildNodes;
	    }

        // Otherwise until we find the next child (could be on any level)
        while (true)
        {
            // If we hit the end of this list, pop up a level.
            if (childNodes != null && 
            	childNodes.length > 0 && 
            	currentChildIndex >= Math.max(childNodes.length - 1, 0))
            {
            	//check for the end of the tree here.
                if (childIndexStack.length < 1 && !more)  
                {
                	currentNode = null;
                    currentIndex++;
                    _currentDepth = 1;
                    return false;
                }
                else 
                {  
                	//pop up to parent
                	currentNode = parentNodes.pop(); 
                	//get parents siblings 
                	var grandParent:Object = parentNodes[parentNodes.length-1];
                	//we could probably assume that a non-null grandparent has descendants 
                	//but the analogy only goes so far... 
                	if (grandParent != null && 
                		dataDescriptor.isBranch(grandParent, model) &&
                		dataDescriptor.hasChildren(grandParent, model))
                	{
	                	childNodes = dataDescriptor.getChildren(grandParent, model);
               		}
               		else
               		{
               			childNodes = [];
               		}
                	//get new current nodes index
                	currentChildIndex = childIndexStack.pop();
                	//pop the level up one
                	_currentDepth--;
                }
            }
            else
            {
            	//if no childnodes then we're probably at the top level
            	if (childIndexStack.length == 0)
				{
					//check for more top level siblings
					//and if we're here the depth should be 1
					_currentDepth = 1;
					more = modelCursor.moveNext();
					if (more) 
					{
						currentNode = modelCursor.current;
						break;
					} 
					else 
					{
						//at the end of the tree
						_currentDepth = 1;
	                    currentIndex++;  //this should push us to afterLast
	                    currentNode = null;
	                    return false;
					}
				}
				else 
				{
					//get the next child node
					try
					{
						currentNode = childNodes[++currentChildIndex];
						break;
					}
					catch(e:RangeError)
					{
						//lets try to recover
						return false;
					}
    			}
            } 
        }
        currentIndex++;
        return true;
    }
    
    /**
     *  @private
	 *  Performs a backward tree walk.
     */
    public function movePrevious():Boolean
    {
    	var currentNode:Object = current;
    	// If there are no items, there's no current node, so return false.
        if (currentNode == null)
			return false;
    	
    	//if not at top level
		if (parentNodes && parentNodes.length > 0)
		{
			if (currentChildIndex == 0)
        	{
        		//at the first node in this branch so move to parent
        		currentNode = parentNodes.pop();
        		currentChildIndex = childIndexStack.pop();
        		var grandParent:Object = parentNodes[parentNodes.length-1];
                //we could probably assume that a non-null grandparent has descendants 
                //but the analogy only goes so far... 
                if (grandParent != null && 
                	dataDescriptor.isBranch(grandParent, model) &&
                	dataDescriptor.hasChildren(grandParent, model))
                {
        			childNodes = dataDescriptor.getChildren(grandParent, model);
                }
               	else
                {
                	//null is valid, but error prone so we'll make it empty
                	childNodes = [];  
                }
        		_currentDepth--;
        		currentIndex--;
        		return true;
        	}
        	else 
        	{
        		// get previous child sibling
        		try 
        		{
        			currentNode = childNodes[--currentChildIndex];
        		}
        		catch(e:RangeError)
        		{
        			//lets try to recover
					return false;
        		}
        	}  	
		}
		//handle top level siblings
		else 
		{
			more = modelCursor.movePrevious();
			if (more)
			{
				//move back one node and then loop through children
				currentNode = modelCursor.current;
			}
			//if past the begining of the tree return false
			else 
			{
				//currentIndex--;  //should be before first
				currentIndex = -1;
				currentNode = null;
				return false;
			}
		}
		while (true)
        {
            // If there are children, walk backwards on the children
            // and consider youself after your children.
			if (openNodes[itemToUID(currentNode)] &&
				dataDescriptor.isBranch(currentNode, model) &&
			    dataDescriptor.hasChildren(currentNode, model))
            {
				var previousChildNodes:Object = childNodes;
            	childNodes = dataDescriptor.getChildren(currentNode, model);
				if (childNodes.length > 0)
				{
            		childIndexStack.push(currentChildIndex);
            		parentNodes.push(currentNode);
            		currentChildIndex = childNodes.length - 1;
            		currentNode = childNodes[currentChildIndex];
           			_currentDepth++;
				}
				else
				{
					childNodes = previousChildNodes;
					break;
				}
            }	
			else
            {
            	//No more open branches so we'll bail
                break;
            }
        }
        currentIndex--; 
        return true;
    }

    /**
     *  @private
     */
    public function seek(bookmark:CursorBookmark, offset:int = 0,
						 prefetch:int = 0):void
    {
    	var value:int;
    	
		if (bookmark == CursorBookmark.FIRST)
        {
            value = 0;
        }
        else if (bookmark == CursorBookmark.CURRENT)
        {
            value = currentIndex;
        }
        else if (bookmark == CursorBookmark.LAST)
        {
            value = collection.length - 1;
        }
        else
        {
            value = int(bookmark.value);
        }
		
        value = Math.max(Math.min(value + offset, collection.length), 0);
        var dc:int = Math.abs(currentIndex - value);
        var de:int = Math.abs(collection.length - value);
		var movedown:Boolean = true;
		// if we're closer to the current than the beginning
        if (dc < value)
        {
            // if we're closer to the end than the current position
            if (de < dc)
            {
                moveToLast();

                if (de == 0)
                {		
                	// if de = 0; we need to be "off the end"
                    moveNext();
                    value = 0;
                }
                else
				{
                    value = de - 1;
				}
                movedown = false;
            }
            else
            {
                movedown = currentIndex < value;
                value = dc;
                // if current is off the end, reset
                if (currentIndex == collection.length)
                {
                    value--;
                    moveToLast();
                }
            }
        }
        else // we're closer to the beginning than the current
        {
            // if we're closer to the end than the beginning
            if (de < value)
            {
                moveToLast();
                if (de == 0)
                {		
                	// if de = 0; we need to be "off the end"
                    moveNext();
                    value = 0;
                }
                else
				{
                    value = de - 1;
				}
                movedown = false;
            }
            else
            {
	            moveToFirst();
            }
        }

        if (movedown)
        {
            while (value && value > 0) 
            {
                moveNext();
                value--;
            }
        }
        else
        {
            while (value && value > 0)  
            {
                movePrevious();
                value--;
            }
        }    
    }
    
    /**
     *  @private
     */
    private function moveToFirst():void
    {
		childNodes = [];
		modelCursor.seek(CursorBookmark.FIRST, 0);
		if (model.length > 1)
			more = true;
		else
			more = false;
        currentChildIndex = 0;
        parentNodes = [];
        childIndexStack = [];
        currentIndex = 0;
        _currentDepth = 1;
    }
    
    /**
     *  @private
     */
    public function moveToLast():void
    {
		childNodes = [];
		childIndexStack = [];
		_currentDepth = 1;
		parentNodes = [];
		var emptyBranch:Boolean = false;
		//first move to the end of the top level collection
		modelCursor.seek(CursorBookmark.LAST, 0);
		//if its a branch and open then get children for the last item
		var currentNode:Object = modelCursor.current;
		//if current node is open get its children
		while (openNodes[itemToUID(currentNode)] &&
			   dataDescriptor.isBranch(currentNode, model) &&
			   dataDescriptor.hasChildren(currentNode, model))
        {
        	var previousChildNodes:Object = childNodes;
        	childNodes = dataDescriptor.getChildren(currentNode, model);
        	if (childNodes != null && childNodes.length > 0)
        	{
        		parentNodes.push(currentNode);
        		childIndexStack.push(currentChildIndex);
        		currentNode = childNodes[childNodes.length - 1];
        		currentChildIndex = childNodes.length - 1;
           		_currentDepth++;
         	}
         	else 
         	{
				childNodes = previousChildNodes;
				break;
         	}
        }
        currentIndex = collection.length - 1;
    }
    
    /**
     *  @private
     */
    public function insert(item:Object):void
    {
        //No impl
    }
    
    /**
     *  @private
     */
    public function remove():Object
    {
        return null;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function collectionChangeHandler(event:CollectionEvent):void
    {
		var i:int;
		var n:int;
        var node:Object;
		var nodeParent:Object
        var parent:Object;
        var parentStack:Array;
        var parentTable:Dictionary;
		var isBefore:Boolean = false;
		

		// get the parent of the current item
        parentStack = getParentStack(current);
		// hash it by parent to map to depth
		parentTable = new Dictionary();
		n = parentStack.length;
		// don't insert the immediate parent
		for (i = 0; i < n - 1; i++)
		{
			// 0 is null parent (the model)
			parentTable[parentStack[i]] = i + 1;
		}
		// remember the current parent
		parent = parentStack[parentStack.length - 1];

		if (event.kind == CollectionEventKind.ADD)
        {
			n = event.items.length;
            if (event.location <= currentIndex)
            {
				currentIndex += n;
				isBefore = true;
            }

			for (i = 0; i < n; i++)
			{
        		node = event.items[i];
				if (isBefore)
				{
					// if the added node is before the current
					// and they share parent's then we have to
					// adjust the currentChildIndex or
					// the stack of child indexes.
					nodeParent = collection.getParentItem(node);
					if (nodeParent == parent)
						currentChildIndex++;
					else if (parentTable[nodeParent] != null)
					{
						childIndexStack[parentTable[nodeParent]]++;
					}
				}
			}
			
        }
        else if (event.kind == CollectionEventKind.REMOVE)
        {
			n = event.items.length;
            if (event.location <= currentIndex)
            {
				if (event.location + n >= currentIndex)
				{
					// the current node was removed
					// the list classes expect that we
					// leave the cursor on whatever falls
					// into that slot
					var newIndex:int = event.location;
					moveToFirst();
					seek(CursorBookmark.FIRST, newIndex);
					for (i = 0; i < n; i++)
					{
        				node = event.items[i];
						delete collection.parentMap[itemToUID(node)];
					}
					return;
				}

                currentIndex -= n;
				isBefore = true;
            }

			for (i = 0; i < n; i++)
			{
        		node = event.items[i];
				if (isBefore)
				{
					// if the removed node is before the current
					// and they share parent's then we have to
					// adjust the currentChildIndex or
					// the stack of child indexes.
					nodeParent = collection.getParentItem(node);
					if (nodeParent == parent)
						currentChildIndex--;
					else if (parentTable[nodeParent] != null)
					{
						childIndexStack[parentTable[nodeParent]]--;
					}
				}
				delete collection.parentMap[itemToUID(node)];
			}
			
        }
    }
}

}
