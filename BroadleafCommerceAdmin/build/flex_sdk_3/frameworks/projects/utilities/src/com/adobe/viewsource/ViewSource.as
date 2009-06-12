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

package com.adobe.viewsource
{

import flash.display.InteractiveObject;
import flash.events.ContextMenuEvent;
import flash.events.Event;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.ui.ContextMenu;
import flash.ui.ContextMenuItem;

public class ViewSource
{
	/**
	 *  Adds a "View Source" context menu item
	 *  to the context menu of the given object.
	 *  Creates a context menu if none exists.
	 *
	 *  @param obj The object to attach the context menu item to.
	 *
	 *  @param url The URL of the source viewer that the "View Source"
	 *  item should open in the browser.
	 *
	 *  @param hideBuiltIns Optional, defaults to true.
	 *  If true, and no existing context menu is attached
	 *  to the given item, then when we create the context menu,
	 *  we hide all the hideable built-in menu items.
	 */
	public static function addMenuItem(obj:InteractiveObject, url:String,
									   hideBuiltIns:Boolean = true):void
	{
		if (obj.contextMenu == null)
		{
			obj.contextMenu = new ContextMenu();
			if (hideBuiltIns)
				obj.contextMenu.hideBuiltInItems();
		}
	
		var item:ContextMenuItem = new ContextMenuItem("View Source");
		
		item.addEventListener(ContextMenuEvent.MENU_ITEM_SELECT, 
			function(event:ContextMenuEvent):void
			{
				if (event.target == item)
					navigateToURL(new URLRequest(url), "_blank");
			}
		);
		
		obj.contextMenu.customItems.push(item);
	}
}
	
}
