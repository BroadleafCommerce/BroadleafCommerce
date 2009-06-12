////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.accessibility
{

import flash.accessibility.Accessibility;
import flash.events.Event;
import mx.collections.ICollectionView;
import mx.collections.CursorBookmark;
import mx.collections.IViewCursor;
import mx.controls.Tree;
import mx.controls.listClasses.IListItemRenderer;
import mx.controls.treeClasses.HierarchicalCollectionView;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.events.TreeEvent;

use namespace mx_internal;

/**
 *  The TreeAccImpl class is the accessibility class for Tree.
 *
 *  @helpid 3009
 *  @tiptext This is the Tree Accessibility Class.
 *  @review
 */
public class TreeAccImpl extends AccImpl
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class initialization
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  Static variable triggering the hookAccessibility() method.
	 *  This is used for initializing TreeAccImpl class to hook its
	 *  createAccessibilityImplementation() method to Tree class 
	 *  before it gets called from UIComponent.
	 */
	private static var accessibilityHooked:Boolean = hookAccessibility();
	
	/**
	 *  @private
	 *  Static method for swapping the createAccessibilityImplementation()
	 *  method of Tree with the TreeAccImpl class.
	 */
	private static function hookAccessibility():Boolean
	{
		Tree.createAccessibilityImplementation =
			createAccessibilityImplementation;

		return true;
	}

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Role of treeItem.
	 */
	private static const ROLE_SYSTEM_OUTLINEITEM:uint = 0x24; 

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_COLLAPSED:uint = 0x00000400;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_EXPANDED:uint = 0x00000200;

	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_FOCUSED:uint = 0x00000004;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_INVISIBLE:uint = 0x00008000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTABLE:uint = 0x00200000;
	
	/**
	 *  @private
	 */
	private static const STATE_SYSTEM_SELECTED:uint = 0x00000002;
	
	/**
	 *  @private
	 *  Event emitted if 1 item is selected.
	 */
	private static const EVENT_OBJECT_FOCUS:uint = 0x8005; 
	
	/**
	 *  @private
	 *  Event emitted if 1 item is selected.
	 */
	private static const EVENT_OBJECT_SELECTION:uint = 0x8006; 
	
	/**
	 *  @private
	 */
	private static const EVENT_OBJECT_STATECHANGE:uint = 0x800A;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Method for creating the Accessibility class.
	 *  This method is called from UIComponent.
	 *  @review
	 */
	mx_internal static function createAccessibilityImplementation(
								component:UIComponent):void
	{
		component.accessibilityImplementation =
			new TreeAccImpl(component);
	}

	/**
	 *  Method call for enabling accessibility for a component.
	 *  This method is required for the compiler to activate
	 *  the accessibility classes for a component.
	 */
	public static function enableAccessibility():void
	{
	}

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param master The UIComponent instance that this AccImpl instance
	 *  is making accessible.
	 */
	public function TreeAccImpl(master:UIComponent)
	{
		super(master);

		role = 0x23;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden properties: AccImpl
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  eventsToHandle
	//----------------------------------

	/**
	 *  @private
	 *	Array of events that we should listen for from the master component.
	 */
	override protected function get eventsToHandle():Array
	{
		return super.eventsToHandle.concat(
			[ "change", TreeEvent.ITEM_OPEN, TreeEvent.ITEM_CLOSE ]);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccessibilityImplementation
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Gets the role for the component.
	 *
	 *  @param childID children of the component
	 */
	override public function get_accRole(childID:uint):uint
	{
		if (childID == 0)
			return role;

		return ROLE_SYSTEM_OUTLINEITEM;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the value of the TreeItem/Tree
	 *  which is spoken out by the screen reader
	 *  The Tree should return the name of the currently selected item
	 *  with m of n string with level info as value when focus moves to Tree.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override public function get_accValue(childID:uint):String
	{
		var accValue:String;
		
		var tree:Tree = Tree(master);
		var index:int;
		var item:Object;

		if (childID == 0)
		{
			index = tree.selectedIndex;
			if (index > -1)
			{
				item = getItemAt(index);
				if (!item)
					return accValue;
				
				if (tree.itemToLabel(item))
					accValue = tree.itemToLabel(item)
				
				accValue += getMOfN(item);
			}
		}
		else
		{
			// Assuming childID is always ItemID + 1
			// because getChildIDArray is not always invoked.
			index = childID - 1;
			
			item = getItemAt(index);
			if (!item)
				return accValue;
			
			accValue = tree.getItemDepth(item, index - tree.verticalScrollPosition) + "";
		}

		return accValue;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the state of the TreeItem.
	 *  States are predefined for all the components in MSAA.
	 *  Values are assigned to each state.
	 *  Depending upon the treeItem being Selected, Selectable,
	 *  Invisible, Offscreen, a value is returned.
	 *
	 *  @param childID uint
	 *
	 *  @return State uint
	 */
	override public function get_accState(childID:uint):uint
	{
		var accState:uint = getState(childID);
		
		if (childID > 0)
		{
			var tree:Tree = Tree(master);

			var index:int = childID - 1;

			// For returning states (OffScreen and Invisible)
			// when the list Item is not in the displayed rows.
			if (index < tree.verticalScrollPosition ||
				index >= tree.verticalScrollPosition + tree.rowCount)
			{
				accState |= STATE_SYSTEM_INVISIBLE;
			}
			else
			{
				accState |= STATE_SYSTEM_SELECTABLE;

				var item:Object = getItemAt(index);

				if (item && tree.dataDescriptor.isBranch(item, tree.dataProvider))
				{
					if (tree.isItemOpen(item))
						accState |= STATE_SYSTEM_EXPANDED;
					else
						accState |= STATE_SYSTEM_COLLAPSED;
				}

				var renderer:IListItemRenderer =
					tree.itemToItemRenderer(item);

				if (renderer != null && tree.isItemSelected(renderer.data))
					accState |= STATE_SYSTEM_SELECTED | STATE_SYSTEM_FOCUSED;
			}
		}
		return accState;
	}

	/**
	 *  @private
	 *  IAccessible method for returning the Default Action.
	 *
	 *  @param childID uint
	 *
	 *  @return name of default action.
	 */
	override public function get_accDefaultAction(childID:uint):String
	{
		if (childID == 0)
			return null;

		var tree:Tree = Tree(master);

		var item:Object = getItemAt(childID - 1);
		if (!item)
			return null;
		
		if (tree.dataDescriptor.isBranch(item, tree.dataProvider))
			return tree.isItemOpen(item) ? "Collapse" : "Expand";

		return null;
	}

	/**
	 *  @private
	 *  IAccessible method for executing the Default Action.
	 *
	 *  @param childID uint
	 */
	override public function accDoDefaultAction(childID:uint):void
	{
		var tree:Tree = Tree(master);

		if (childID == 0 || !tree.enabled)
			return;

		var item:Object = getItemAt(childID - 1);
		if (!item)
			return;
		
		if (tree.dataDescriptor.isBranch(item, tree.dataProvider))
			tree.expandItem(item, !tree.isItemOpen(item)); 
	}

 	/**
	 *  @private
	 *  Method to return an array of childIDs.
	 *
	 *  @return Array
	 */
	override public function getChildIDArray():Array
	{
		var childIDs:Array = [];

		if (Tree(master).dataProvider)
		{
			var n:int = Tree(master).collectionLength;
			for (var i:int = 0; i < n; i++)
			{
				childIDs[i] = i + 1;
			}
		}
		return childIDs;
	}
	
	/**
	 *  @private
	 *  IAccessible method for returning the bounding box of the TreeItem.
	 *
	 *  @param childID uint
	 *
	 *  @return Location Object
	 */
	override public function accLocation(childID:uint):*
	{
		var tree:Tree = Tree(master);
		
		var index:int = childID - 1;
		
		if (index < tree.verticalScrollPosition ||
			index >= tree.verticalScrollPosition + tree.rowCount)
		{
			return null;
		}

		return tree.itemToItemRenderer(getItemAt(index));
	}

	/**
	 *  @private
	 *  IAccessible method for returning the childFocus of the List.
	 *
	 *  @param childID uint
	 *
	 *  @return focused childID.
	 */
	override public function get_accFocus():uint
	{
		var index:int = Tree(master).selectedIndex;
		
		return index >= 0 ? index + 1 : 0;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden methods: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  method for returning the name of the TreeItem/Tree
	 *  which is spoken out by the screen reader
	 *  The TreeItem should return the label as the name
	 *  with m of n string with level info and
	 *  Tree should return the name specified in the Accessibility Panel.
	 *
	 *  @param childID uint
	 *
	 *  @return Name String
	 *  @review
	 */
	override protected function getName(childID:uint):String
	{
		if (childID == 0)
			return "";

		var name:String = "";
		
		var tree:Tree = Tree(master);

		// Assuming childID is always ItemID + 1
		// because getChildIDArray is not always invoked.
		var index:int = childID - 1;
		
		var item:Object = getItemAt(childID - 1);
		if (!item)
			return name;
		
		if (tree.itemToLabel(item))
			name = tree.itemToLabel(item);

		name += getMOfN(item);

		return name;
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	private function getItemAt(index:int):Object
	{
		var iterator:IViewCursor = Tree(master).collectionIterator;
		iterator.seek(CursorBookmark.FIRST, index);
		return iterator.current;
	}

	/**
	 *  @private
	 *  Local method to return m of n String.
	 *
	 *  @param item Object
	 *
	 *  @return string.
	 */
	private function getMOfN(item:Object):String
	{
		var tree:Tree = Tree(master);
		var i:int = 0;
		var n:int = 0;

		var view:HierarchicalCollectionView = HierarchicalCollectionView(tree.wrappedCollection);
		
		var parent:Object = view.getParentItem(item);
		if (parent)
		{
			var childNodes:ICollectionView =
				tree.dataDescriptor.getChildren(parent, tree.collectionIterator.view);
			 
			if (childNodes)
			{
				n = childNodes.length;
				for (i = 0; i < n; i++)
				{
					if (item == childNodes[i])
						break;
				}
			}
		}
		else
		{
			var cursor:IViewCursor = ICollectionView(tree.collectionIterator.view).createCursor();
			while (!cursor.afterLast)
			{
				if (item == cursor.current)
					i = n;
				n++;
				cursor.moveNext();
			}
		}
		
		if (i == n)
			i = 0;

		// Make it 1-based.
		if (n > 0)
			i++;

		return ", " + i + " of " + n;
	}

	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: AccImpl
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Override the generic event handler.
	 *  All AccImpl must implement this
	 *  to listen for events from its master component. 
	 */
	override protected function eventHandler(event:Event):void
	{
		var index:int = Tree(master).selectedIndex;
		
		var childID:uint = index + 1;

		switch (event.type)
		{
			case "change":
			{
				if (index >= 0)
				{
					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_FOCUS);

					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_SELECTION);
				}
				break;
			}
										
			case TreeEvent.ITEM_OPEN:
			case TreeEvent.ITEM_CLOSE:
			{
				if (index >= 0)
				{
					Accessibility.sendEvent(master, childID,
											EVENT_OBJECT_STATECHANGE);
				}
				break;
			}
		}
	}
}

}
