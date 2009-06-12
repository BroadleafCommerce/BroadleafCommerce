////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.display.DisplayObject;
import flash.display.InteractiveObject;
import flash.display.Sprite;
import flash.display.Stage;
import flash.events.ContextMenuEvent;
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.IOErrorEvent;
import flash.events.MouseEvent;
import flash.events.ProgressEvent;
import flash.text.TextField;
import flash.text.TextFieldType;
import flash.ui.Mouse;

import mx.core.ApplicationGlobals;
import mx.core.EventPriority;
import mx.core.FlexSprite;
import mx.core.mx_internal;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class CursorManagerImpl implements ICursorManager
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var instance:ICursorManager;

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public static function getInstance():ICursorManager
    {
        if (!instance)
            instance = new CursorManagerImpl();

        return instance;
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public function CursorManagerImpl(systemManager:ISystemManager = null)
    {
        super();

        if (instance && !systemManager)
            throw new Error("Instance already exists.");

		if (systemManager)
			this.systemManager = systemManager;
		else
			this.systemManager = ApplicationGlobals.application.systemManager;

    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private var nextCursorID:int = 1;
    
    /**
     *  @private
     */
    private var cursorList:Array = [];
    
    /**
     *  @private
     */
    private var busyCursorList:Array = [];
    
    /**
     *  @private
     */
    private var initialized:Boolean = false;
    
    /**
     *  @private
     */
    private var cursorHolder:Sprite;
    
    /**
     *  @private
     */
    private var currentCursor:DisplayObject;

	/**
     *  @private
     */
	private var listenForContextMenu:Boolean = false;
    
    /*******************************************************************
     * Regarding overTextField, showSystemCursor, and showCustomCursor:
     *    Don't modify or read these variables unless you are certain
     *    you will not create race conditions. E.g. you may get the
     *    wrong (or no) cursor, and get stuck in an inconsistent state.
     */
     
    /**
     *  @private
     */
     private var overTextField:Boolean = false;
     
    /**
     *  @private
     */
    private var showSystemCursor:Boolean = false;
    
    /**
     *  @private
     */
    private var showCustomCursor:Boolean = false;
    
    /**
     *  @private
     * 
     * State variable -- set when there is a custom cursor and the
     * mouse has left the stage. Upon return, mouseMoveHandler will
     * restore the custom cursor and remove the system cursor.
     */
    private var customCursorLeftStage:Boolean = false;
    
    /*******************************************************************/
    
    /**
     *  @private
     */
    private var systemManager:ISystemManager = null;
    
    /**
     *  @private
     */
    private var sourceArray:Array = [];

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  currentCursorID
    //----------------------------------

    /**
     *  @private
     */
    private var _currentCursorID:int = 0 /* CursorManager.NO_CURSOR */;

    /**
     *  ID of the current custom cursor,
     *  or CursorManager.NO_CURSOR if the system cursor is showing.
     */
    public function get currentCursorID():int
    {
        return _currentCursorID;
    }
    
    /**
     *  @private
     */
    public function set currentCursorID(value:int):void
    {
        _currentCursorID = value;
    }

    //----------------------------------
    //  currentCursorXOffset
    //----------------------------------

    /**
     *  @private
     */
    private var _currentCursorXOffset:Number = 0;

    /**
     *  The x offset of the custom cursor, in pixels,
     *  relative to the mouse pointer.
     *       
     *  @default 0
     */
    public function get currentCursorXOffset():Number 
    {
        return _currentCursorXOffset;
    }
    
    /**
     *  @private
     */
    public function set currentCursorXOffset(value:Number):void
    {
        _currentCursorXOffset = value;
    }

    //----------------------------------
    //  currentCursorYOffset
    //----------------------------------

    /**
     *  @private
     */
    private var _currentCursorYOffset:Number = 0;

    /**
     *  The y offset of the custom cursor, in pixels,
     *  relative to the mouse pointer.
     *
     *  @default 0
     */
    public function get currentCursorYOffset():Number 
    {
        return _currentCursorYOffset;
    }
    
    /**
     *  @private
     */
    public function set currentCursorYOffset(value:Number):void
    {
        _currentCursorYOffset = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Makes the cursor visible.
     *  Cursor visibility is not reference-counted.
     *  A single call to the <code>showCursor()</code> method
     *  always shows the cursor regardless of how many calls
     *  to the <code>hideCursor()</code> method were made.
     */
    public function showCursor():void
    {
        if (cursorHolder)
	        cursorHolder.visible = true;
    }
    
    /**
     *  Makes the cursor invisible.
     *  Cursor visibility is not reference-counted.
     *  A single call to the <code>hideCursor()</code> method
     *  always hides the cursor regardless of how many calls
     *  to the <code>showCursor()</code> method were made.
     */
    public function hideCursor():void
    {
    	if (cursorHolder)
	        cursorHolder.visible = false;
    }

    /**
     *  Creates a new cursor and sets an optional priority for the cursor.
     *  Adds the new cursor to the cursor list.
     *
     *  @param cursorClass Class of the cursor to display.
     *
     *  @param priority Integer that specifies
     *  the priority level of the cursor.
     *  Possible values are <code>CursorManagerPriority.HIGH</code>,
     *  <code>CursorManagerPriority.MEDIUM</code>, and <code>CursorManagerPriority.LOW</code>.
     *
     *  @param xOffset Number that specifies the x offset
     *  of the cursor, in pixels, relative to the mouse pointer.
     *
     *  @param yOffset Number that specifies the y offset
     *  of the cursor, in pixels, relative to the mouse pointer.
     *
     *  @param setter The IUIComponent that set the cursor. Necessary (in multi-window environments) 
     *  to know which window needs to display the cursor. 
     * 
     *  @return The ID of the cursor.
     *
     *  @see mx.managers.CursorManagerPriority
     */
    public function setCursor(cursorClass:Class, priority:int = 2,
                                     xOffset:Number = 0,
                                     yOffset:Number = 0):int 
    {
        var cursorID:int = nextCursorID++;
        
        // Create a new CursorQueueItem.
        var item:CursorQueueItem = new CursorQueueItem();
        item.cursorID = cursorID;
        item.cursorClass = cursorClass;
        item.priority = priority;
        item.x = xOffset;
        item.y = yOffset;
        if (systemManager)
        	item.cursorSystemManager = systemManager;
        else
        	item.cursorSystemManager = ApplicationGlobals.application.systemManager;
        
        // Push it onto the cursor list.
        cursorList.push(item);
        
        // Re-sort the cursor list based on priority level.
        cursorList.sort(priorityCompare);

        // Determine which cursor to display
        showCurrentCursor();
        
        return cursorID;
    }
    
    /**
     *  @private
     */
    private function priorityCompare(a:CursorQueueItem, b:CursorQueueItem):int
    {
        if (a.priority < b.priority)
            return -1;
        else if (a.priority == b.priority)
            return 0;
        
        return 1;
    }

    /**
     *  Removes a cursor from the cursor list.
     *  If the cursor being removed is the currently displayed cursor,
     *  the CursorManager displays the next cursor in the list, if one exists.
     *  If the list becomes empty, the CursorManager displays
     *  the default system cursor.
     *
     *  @param cursorID ID of cursor to remove.
     */
    public function removeCursor(cursorID:int):void 
    {
        for (var i:Object in cursorList)
        {
            var item:CursorQueueItem = cursorList[i];
            if (item.cursorID == cursorID)
            {
                // Remove the element from the array.
                cursorList.splice(i, 1); 

                // Determine which cursor to display.
                showCurrentCursor();
                    
                break;
            }
        }
    }
    
    /**
     *  Removes all of the cursors from the cursor list
     *  and restores the system cursor.
     */
    public function removeAllCursors():void
    {
        cursorList.splice(0);
        showCurrentCursor();
    }

    /**
     *  Displays the busy cursor.
     *  The busy cursor has a priority of CursorManagerPriority.LOW.
     *  Therefore, if the cursor list contains a cursor
     *  with a higher priority, the busy cursor is not displayed 
     *  until you remove the higher priority cursor.
     *  To create a busy cursor at a higher priority level,
     *  use the <code>setCursor()</code> method.
     */
    public function setBusyCursor():void 
    {
        var cursorManagerStyleDeclaration:CSSStyleDeclaration =
            StyleManager.getStyleDeclaration("CursorManager");
        
        var busyCursorClass:Class =
            cursorManagerStyleDeclaration.getStyle("busyCursor");
        
        busyCursorList.push(setCursor(busyCursorClass, CursorManagerPriority.LOW));
    }

    /**
     *  Removes the busy cursor from the cursor list.
     *  If other busy cursor requests are still active in the cursor list,
     *  which means you called the <code>setBusyCursor()</code> method more than once,
     *  a busy cursor does not disappear until you remove
     *  all busy cursors from the list.
     */
    public function removeBusyCursor():void 
    {
        if (busyCursorList.length > 0)
            removeCursor(int(busyCursorList.pop()));
    }

    /**
     *  @private
     *  Decides what cursor to display.
     */
    private function showCurrentCursor():void 
    {
        // if there are custom cursors...
        if (cursorList.length > 0)
        {
            if (!initialized)
            {
                // The first time a cursor is requested of the CursorManager,
                // create a Sprite to hold the cursor symbol
                cursorHolder = new FlexSprite();
                cursorHolder.name = "cursorHolder";
                cursorHolder.mouseEnabled = false;

                initialized = true;
            }

            // Get the top most cursor.
            var item:CursorQueueItem = cursorList[0];
                
            // If the system cursor was being displayed, hide it.
            if (currentCursorID == CursorManager.NO_CURSOR)
                Mouse.hide();
			
            // If the current cursor has changed...
            if (item.cursorID != currentCursorID)
            {
                if (cursorHolder.numChildren > 0)
                    cursorHolder.removeChildAt(0);
                
                currentCursor = new item.cursorClass(); 
                
                if (currentCursor)
                {
                    if (currentCursor is InteractiveObject)
                        InteractiveObject(currentCursor).mouseEnabled = false;
                    
                    // Figure out which systemManager to hang the cursor off of. 
                    const tempSystemManager:ISystemManager = item.cursorSystemManager
                                 ? item.cursorSystemManager 
                                 : ApplicationGlobals.application.systemManager;
                    				
                    // If this is a different systemManager, clean up the old one.
                    //
                    // IMPORTANT: we're mutating systemManager here, did you leak an
                    //            event listener in the old one? Clean it up here.
                    if (systemManager && (systemManager != tempSystemManager))
                    {
                    	systemManager.cursorChildren.removeChild(cursorHolder);
                    	
                    	removeSystemManagerHandlers();
                    	removeContextMenuHandlers();
                    	
                        systemManager = tempSystemManager;
                    }
                    
                    if (!systemManager.cursorChildren.contains(cursorHolder))
                    	systemManager.cursorChildren.addChild(cursorHolder);
                    
                    cursorHolder.addChild(currentCursor);

                    addContextMenuHandlers();
                    
                    // make sure systemManager is not other implementation of ISystemManager
                    if (systemManager is SystemManager)
                    {
                    	cursorHolder.x = SystemManager(systemManager).mouseX + item.x;
                    	cursorHolder.y = SystemManager(systemManager).mouseY + item.y;
                    }
                    // WindowedSystemManager
                    else if (systemManager is DisplayObject)
                    {
                    	cursorHolder.x = DisplayObject(systemManager).mouseX + item.x;
                    	cursorHolder.y = DisplayObject(systemManager).mouseY + item.y;
                    }
                    // otherwise
                    else
                    {
                    	cursorHolder.x = item.x;
                    	cursorHolder.y = item.y;
                    }
                    
                    // handle drawing and updating the position of the custom cursor                  	
                    systemManager.stage.addEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler,
                                                         true, EventPriority.CURSOR_MANAGEMENT);
                    
                    // handle the mouse leaving the window/application
                    systemManager.stage.addEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler,
                                                         true, EventPriority.CURSOR_MANAGEMENT);
                }
            	
                currentCursorID = item.cursorID;
                currentCursorXOffset = item.x;
                currentCursorYOffset = item.y;
            }
        }
        // else: there are no custom cursors
        else
        {
            showCustomCursor = false;
            
            if (currentCursorID != CursorManager.NO_CURSOR)
            {
                // There is no cursor in the cursor list to display,
                // so cleanup and restore the system cursor.
                currentCursorID = CursorManager.NO_CURSOR;
                currentCursorXOffset = 0;
                currentCursorYOffset = 0;
                
                cursorHolder.removeChild(currentCursor);
                
                removeSystemManagerHandlers();
                removeContextMenuHandlers();
            }
            Mouse.show();
        }
    }
    
    /**
     *  @private
     * 
     * This assumes systemManager != null.
     */
    private function removeSystemManagerHandlers():void
    {
        const smStage:Stage = systemManager.stage;
        
        // these are definitely set
    	smStage.removeEventListener(MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);
        smStage.removeEventListener(MouseEvent.MOUSE_OUT, mouseOutHandler, true);
    }
    
    /**
     *  @private
     */
    private function addContextMenuHandlers():void
    {
        if (!listenForContextMenu)
        {
            const app:InteractiveObject = systemManager.document as InteractiveObject;
        	const sm:InteractiveObject = systemManager as InteractiveObject;
        	
        	if (app && app.contextMenu)
        	{
        		app.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, contextMenu_menuSelectHandler,
        		                                 true, EventPriority.CURSOR_MANAGEMENT);
        		listenForContextMenu = true;
        	}
        	
        	if (sm && sm.contextMenu)
        	{
        		sm.contextMenu.addEventListener(ContextMenuEvent.MENU_SELECT, contextMenu_menuSelectHandler,
        		                                true, EventPriority.CURSOR_MANAGEMENT);
        		listenForContextMenu = true;
        	}     	
        }
    }
    
    /**
     *  @private
     */
    private function removeContextMenuHandlers():void
    {
        if (listenForContextMenu)
        {
            const app:InteractiveObject = systemManager.document as InteractiveObject;
        	const sm:InteractiveObject = systemManager as InteractiveObject;
        	
        	if (app && app.contextMenu)
        		app.contextMenu.removeEventListener(ContextMenuEvent.MENU_SELECT, contextMenu_menuSelectHandler, true);

        	if (sm && sm.contextMenu)
        		sm.contextMenu.removeEventListener(ContextMenuEvent.MENU_SELECT, contextMenu_menuSelectHandler, true);
   
        	listenForContextMenu = false; 	
        }
    }
    
    /**
     *  @private
     *  Called by other components if they want to display
     *  the busy cursor during progress events.
     */
    public function registerToUseBusyCursor(source:Object):void
    {
        if (source && source is EventDispatcher) 
        {
            source.addEventListener(ProgressEvent.PROGRESS, progressHandler);
            source.addEventListener(Event.COMPLETE, completeHandler);
            source.addEventListener(IOErrorEvent.IO_ERROR, completeHandler);
        }
    }

    /**
     *  @private
     *  Called by other components to unregister
     *  a busy cursor from the progress events.
     */
    public function unRegisterToUseBusyCursor(source:Object):void
    {
        if (source && source is EventDispatcher) 
        {
            source.removeEventListener(ProgressEvent.PROGRESS, progressHandler);
            source.removeEventListener(Event.COMPLETE, completeHandler);
            source.removeEventListener(IOErrorEvent.IO_ERROR, completeHandler);
        }
    }
    
    /**
     *  @private
     */
    private function findSource(target:Object):int
    {
        var n:int = sourceArray.length;
        for (var i:int = 0; i < n; i++)
        {
            if (sourceArray[i] === target)
                return i;
        }
        return -1;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     *  Called when contextMenu is opened
     */
    private function contextMenu_menuSelectHandler(event:ContextMenuEvent):void
    {
        // Restore the custom cursor...
        // In AIR, the cursor doesn't get restored until a mouse move event is issued.
        // Don't change this logic without testing that you didn't modify existing behavior
        // in the Flash Player in Flex 3. It's very delicate, and sort of black magic.
    	showCustomCursor = true;
    	
    	// Standalone player doesn't initially send mouseMove when the contextMenu is closed,
    	// so we need to listen for mouseOver as w`ell.
    	systemManager.stage.addEventListener(MouseEvent.MOUSE_OVER, contextMenuMouseOverHandler,
    	                                     true, EventPriority.CURSOR_MANAGEMENT);
    }

    /**
     *  @private
     * 
     * See contextMenu_menuSelectHandler.
     */
    private function contextMenuMouseOverHandler(event:MouseEvent):void
    {
    	systemManager.stage.removeEventListener(MouseEvent.MOUSE_OVER, contextMenuMouseOverHandler, true);
    	mouseMoveHandler(event);
    }

    /**
     *  @private
     * 
     * Handles the mouse leaving the stage; hides the custom cursor and restores the system cursor.
     */
    private function mouseOutHandler(event:MouseEvent):void
    {
        // relatedObject==null implies the mouse left the stage.
        // this also fires when you are returning from a context menu click.
        //
        // it sometimes fires after you drag off the stage, and back to the stage quickly,
        // and let go of the button -- this seems like a player bug
        if ((event.relatedObject == null) && (cursorList.length > 0))
        {
            //trace("mouseOutHandler", event);
            
            // this will get unset in mouseMoveHandler (since that fires when
            // the mouse returns/glides over the stage)
            customCursorLeftStage = true;
            hideCursor();
            Mouse.show();
        }
    }
    
    /**
     *  @private
     */
    private function mouseMoveHandler(event:MouseEvent):void
    {
        //trace("mouseMove target", event.target);
        //trace("mouseMove x", event.localX, "y", event.localY,
        //            "root=cursorHolder?", rootApplication === cursorHolder);
        
        // handle the mouse returning to the window/application (even if it's just gliding over it).
        // this will show a custom cursor even if the window is not in focus.
        //
        // if the mouse button is held down while dragging off stage, mouseMoves still occur off-stage.
        // if the mouse button is down, and customCursorLeftStage is true (customCursorLeftStage is set to
        // true on mouseOut), then the mouse is off-stage and we shouldn't show the cursor.
        if (customCursorLeftStage)
        {
            //trace("mouseMoveHandler", event);
            
            customCursorLeftStage = false;
            
            // since we did hideCursor() before leaving
            showCursor();

            // only rehide the system cursor if there is a custom cursor still specified
            // (e.g. a busy cursor may have been removed while the mouse was off stage)
            if (cursorList.length > 0)
                Mouse.hide();
        }

		if (systemManager is SystemManager)
        {	
        	cursorHolder.x = SystemManager(systemManager).mouseX + currentCursorXOffset;
        	cursorHolder.y = SystemManager(systemManager).mouseY + currentCursorYOffset;
        }
        else if (systemManager is DisplayObject)
        {
        	cursorHolder.x = DisplayObject(systemManager).mouseX + currentCursorXOffset;
        	cursorHolder.y = DisplayObject(systemManager).mouseY + currentCursorYOffset;
        } 
        else
        {
        	cursorHolder.x = currentCursorXOffset;
        	cursorHolder.y = currentCursorYOffset;
        }

        var target:Object = event.target;
        
        // Do target test.
        if (!overTextField &&
            target is TextField && target.type == TextFieldType.INPUT)
        {   
            overTextField = true;
            showSystemCursor = true;
        } 
        else if (overTextField &&
                 !(target is TextField && target.type == TextFieldType.INPUT))
        {
            overTextField = false;
            showCustomCursor = true;
        }
        
        updateCursorHelper();
    }
    
    /**
     *  @private
     * 
     * Handle switching between system and custom cursor; updates the cursor
     * visibility and type based on showSystemCursor and showCustomCursor.
     */
    private function updateCursorHelper():void
    {
        // in AIR apps, this will be non-null
        const hasNativeWindow:Boolean = systemManager.stage.hasOwnProperty("nativeWindow");
        
        // an app has focus if there's no nativeWindow (browser)
        // or the native window has focus
        const hasFocus:Boolean = (!hasNativeWindow || (systemManager.stage["nativeWindow"]["active"]));

        if (hasFocus)
        {
            if (showSystemCursor)
            {
                showSystemCursor = false;
                hideCursor();
                Mouse.show();
            }
            if (showCustomCursor)
            {
                showCustomCursor = false;
                showCursor();
                Mouse.hide();
            }
        }
        else
        {
            // if we are gliding over a window that doesn't have focus,
            // and there is a custom cursor defined, hide the system cursor,
            // show the custom cursor; otherwise, keep the system cursor.
            if (cursorList.length > 0)
                Mouse.hide();
        }
    }
    
    /**
     *  @private
     * 
     *  Displays the busy cursor if a component is in a busy state.
     */
    private function progressHandler(event:ProgressEvent):void
    {
        // Only pay attention to the first progress call. Ignore all others.
        var sourceIndex:int = findSource(event.target);
        if (sourceIndex == -1)
        {
            // Add the target to the list of objects we are listening for.
            sourceArray.push(event.target);
            
            setBusyCursor();
        }
    }
    
    /**
     *  @private
     */
    private function completeHandler(event:Event):void
    {
        var sourceIndex:int = findSource(event.target);
        if (sourceIndex != -1)
        {
            // Remove from the list of targets we are listening to.
            sourceArray.splice(sourceIndex, 1);
            
            removeBusyCursor();
        }
    }

}

}

import mx.managers.CursorManager;
import mx.managers.CursorManagerPriority;
import mx.managers.ISystemManager;

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: CursorQueueItem
//
////////////////////////////////////////////////////////////////////////////////

/**
 *  @private
 */
class CursorQueueItem
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------
    
    /**
     *  Constructor.
     */
    public function CursorQueueItem()
    {
        super();
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    public var cursorID:int = CursorManager.NO_CURSOR;

    /**
     *  @private
     */
    public var cursorClass:Class = null;

    /**
     *  @private
     */
    public var priority:int = CursorManagerPriority.MEDIUM;
    
     /**
     *  @private
     */
    public var cursorSystemManager:ISystemManager;

    /**
     *  @private
     */
    public var x:Number;

    /**
     *  @private
     */
    public var y:Number;
}
