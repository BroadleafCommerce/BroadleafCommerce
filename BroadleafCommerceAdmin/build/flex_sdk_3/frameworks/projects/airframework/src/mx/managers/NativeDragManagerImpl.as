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

package mx.managers
{

import flash.desktop.Clipboard;
import flash.desktop.NativeDragManager;
import flash.desktop.NativeDragOptions;
import flash.display.BitmapData;
import flash.display.DisplayObject;
import flash.display.InteractiveObject;
import flash.events.MouseEvent;
import flash.events.NativeDragEvent;
import flash.geom.Point;
import flash.system.Capabilities;

import mx.core.DragSource;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.events.DragEvent;
import mx.events.FlexEvent;
import mx.managers.dragClasses.DragProxy;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 * 
 *  @playerversion AIR 1.1
 */
public class NativeDragManagerImpl implements IDragManager
{
 
	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private static var sm:ISystemManager;

	/**
	 *  @private
	 */
	private static var instance:IDragManager;

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public static function getInstance():IDragManager
	{
		if (!instance)
		{
			sm = SystemManagerGlobals.topLevelSystemManagers[0];
			instance = new NativeDragManagerImpl();
		}

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
	public function NativeDragManagerImpl()
	{
		super();

		if (instance)
			throw new Error("Instance already exists.");
			
		registerSystemManager(sm);
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Object being dragged around.
	 */
	public var dragProxy:DragProxy;

	/**
	 *  @private
	 */
	private var mouseIsDown:Boolean = false;

	private var _action:String;

	/**
	 *  @private
	 */
	private var _dragInitiator:IUIComponent;
	
	/**
	 *  @private
	 */
	private var _clipboard:Clipboard;
	
	/**
	 *  @private
	 */
	private var _dragImage:IFlexDisplayObject;
	
	private var _offset:Point;
	
	private var _allowedActions:NativeDragOptions;
	
	private var _allowMove:Boolean;
	
	private var _relatedObject:InteractiveObject;
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------
		
	/**
	 *  Read-only property that returns <code>true</code>
	 *  if a drag is in progress.
	 */
	public function get isDragging():Boolean
	{
		return flash.desktop.NativeDragManager.isDragging;// || bDoingDrag;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	

	/**
	 *  Initiates a drag and drop operation.
	 *
	 *  @param dragInitiator IUIComponent that specifies the component initiating
	 *  the drag.
	 *
	 *  @param dragSource DragSource object that contains the data
	 *  being dragged.
	 *
	 *  @param mouseEvent The MouseEvent that contains the mouse information
	 *  for the start of the drag.
	 *
	 *  @param dragImage The image to drag. This argument is optional.
	 *  If omitted, a standard drag rectangle is used during the drag and
	 *  drop operation. If you specify an image, you must explicitly set a 
	 *  height and width of the image or else it will not appear.
	 *
	 *  @param xOffset Number that specifies the x offset, in pixels, for the
	 *  <code>dragImage</code>. This argument is optional. If omitted, the drag proxy
	 *  is shown at the upper-left corner of the drag initiator. The offset is expressed
	 *  in pixels from the left edge of the drag proxy to the left edge of the drag
	 *  initiator, and is usually a negative number.
	 *
	 *  @param yOffset Number that specifies the y offset, in pixels, for the
	 *  <code>dragImage</code>. This argument is optional. If omitted, the drag proxy
	 *  is shown at the upper-left corner of the drag initiator. The offset is expressed
	 *  in pixels from the top edge of the drag proxy to the top edge of the drag
	 *  initiator, and is usually a negative number.
	 *
	 *  @param imageAlpha Number that specifies the alpha value used for the
	 *  drag image. This argument is optional. If omitted, the default alpha
	 *  value is 0.5. A value of 0.0 indicates that the image is transparent;
	 *  a value of 1.0 indicates it is fully opaque. 
	 */
	public function doDrag(
			dragInitiator:IUIComponent, 
			dragSource:DragSource, 
			mouseEvent:MouseEvent,
			dragImage:IFlexDisplayObject = null, // instance of dragged item(s)
			xOffset:Number = 0,
			yOffset:Number = 0,
			imageAlpha:Number = 0.5,
			allowMove:Boolean = true):void
	{
		var proxyWidth:Number;
		var proxyHeight:Number;
		
		// Can't start a new drag if we're already in the middle of one...
		if (isDragging)
			return; 
		
		// Can't do a drag if the mouse isn't down
		if (!(mouseEvent.type == MouseEvent.MOUSE_DOWN ||
			  mouseEvent.type == MouseEvent.CLICK ||
			  mouseIsDown ||
			  mouseEvent.buttonDown)) 
		{ 
			return;
		}
		
		_clipboard = new Clipboard();
		_dragInitiator = dragInitiator;
		_offset = new Point(xOffset, yOffset);
		_allowMove = allowMove;
		
		//adjust offsets for imagePlacement.
		_offset.y -= InteractiveObject(dragInitiator).mouseY;
		_offset.x -= InteractiveObject(dragInitiator).mouseX;
		
		// TODO!!! We need to pass in these values as a function parameter
		_allowedActions = new NativeDragOptions();
		_allowedActions.allowCopy = true;
		_allowedActions.allowLink = true;
		_allowedActions.allowMove = allowMove;
		
		// Transfer the dragSource into a Clipboard
		for (var i:int = 0; i < dragSource.formats.length; i++)
		{
			var format:String = dragSource.formats[i] as String;
			var data:Object = dragSource.dataForFormat(format);
			_clipboard.setData(format, data);
		}	
		
		if (!dragImage)
		{
			// No drag image specified, use default
			var dragManagerStyleDeclaration:CSSStyleDeclaration =
				StyleManager.getStyleDeclaration("DragManager");
			var dragImageClass:Class =
				dragManagerStyleDeclaration.getStyle("defaultDragImageSkin");
			dragImage = new dragImageClass();
			proxyWidth = dragInitiator ? dragInitiator.width : 0;
			proxyHeight = dragInitiator ? dragInitiator.height : 0;
			if (dragImage is IFlexDisplayObject)
				IFlexDisplayObject(dragImage).setActualSize(proxyWidth, proxyHeight);
		}
		else
		{
			proxyWidth = dragImage.width;
			proxyHeight = dragImage.height;
		}

		_dragImage = dragImage; 	
						
		if (dragImage is IUIComponent && dragImage is ILayoutManagerClient && 
			!ILayoutManagerClient(dragImage).initialized && dragInitiator)
		{
			dragImage.addEventListener(FlexEvent.UPDATE_COMPLETE,initiateDrag);
			dragInitiator.systemManager.popUpChildren.addChild(DisplayObject(dragImage));
						
			if (dragImage is ILayoutManagerClient )
			{
				UIComponentGlobals.layoutManager.validateClient(ILayoutManagerClient(dragImage), true);
			}
			
			if(dragImage is IUIComponent)
			{
				dragImage.setActualSize(proxyWidth, proxyHeight);
				proxyWidth = (dragImage as IUIComponent).getExplicitOrMeasuredWidth();
				proxyHeight = (dragImage as IUIComponent).getExplicitOrMeasuredHeight();
			}
			else
			{
				proxyWidth = dragImage.measuredWidth;
				proxyHeight = dragImage.measuredHeight;
			}
			
			if (dragImage is ILayoutManagerClient )
			{
				UIComponentGlobals.layoutManager.validateClient(ILayoutManagerClient(dragImage));
			}
		}
		else
		{ 
			initiateDrag(null, false);
			return;
		}
	}
	
	/**
	 *  Finish up the doDrag once the dragImage has been drawn
	 */ 
	private function initiateDrag(event:FlexEvent, removeImage:Boolean = true):void
	{
		if (removeImage)
			_dragImage.removeEventListener(FlexEvent.UPDATE_COMPLETE, initiateDrag);
		var dragBitmap:BitmapData 	
		if (_dragImage.width && _dragImage.height)
			dragBitmap = new BitmapData(_dragImage.width, _dragImage.height, true, 0x000000);
		else
			dragBitmap = new BitmapData(1, 1, true, 0x000000);
		dragBitmap.draw(_dragImage);
		
		if (removeImage && _dragImage is IUIComponent && _dragInitiator)		
		{
			_dragInitiator.systemManager.popUpChildren.removeChild(DisplayObject(_dragImage));
		}
		
		// TODO!!! include _dragActions as the last param
		flash.desktop.NativeDragManager.doDrag(InteractiveObject(_dragInitiator), _clipboard, dragBitmap, _offset, _allowedActions); 
		//NativeDragManager.dropAction = _allowMove ? DragManager.MOVE : DragManager.COPY;
	}
	
	/**
	 *  Call this method from your <code>dragEnter</code> event handler if you accept
	 *  the drag/drop data.
	 *  For example: 
	 *
	 *  <pre>DragManager.acceptDragDrop(event.target);</pre>
	 *
	 *	@param target The drop target accepting the drag.
	 */
	public function acceptDragDrop(target:IUIComponent):void
	{
		// trace("NativeDragMgr.acceptDragDrop targ",target);
		var dispObj:InteractiveObject = target as InteractiveObject;
		if (dispObj)
			flash.desktop.NativeDragManager.acceptDragDrop(dispObj);
	}
	
	/**
	 *  Sets the feedback indicator for the drag and drop operation.
	 *  Possible values are <code>DragManager.COPY</code>, <code>DragManager.MOVE</code>,
	 *  <code>DragManager.LINK</code>, or <code>DragManager.NONE</code>.
	 *
	 *  @param feedback The type of feedback indicator to display.
	 */
	public function showFeedback(feedback:String):void
	{
		if (feedback == DragManager.MOVE && !_allowedActions.allowMove)
			return;
		else if (feedback == DragManager.COPY && !_allowedActions.allowCopy)
			return;
		else if (feedback == DragManager.LINK && !_allowedActions.allowLink)
			return;
		flash.desktop.NativeDragManager.dropAction = feedback;
	}
	
	/**
	 *  Returns the current drag and drop feedback.
	 *
	 *  @return  Possible return values are <code>DragManager.COPY</code>, 
	 *  <code>DragManager.MOVE</code>,
	 *  <code>DragManager.LINK</code>, or <code>DragManager.NONE</code>.
	 */
	public function getFeedback():String
	{
		return flash.desktop.NativeDragManager.dropAction;
	}
	
	/**
	 *  @Review
	 *  Not Supported by NativeDragManagerImpl
	 */
	public function endDrag():void
	{
	}
			
	/**
	 *  @private
	 *  register ISystemManagers that will listen for events 
	 *  (such as those for additional windows)
	 */
	mx_internal function registerSystemManager(sm:ISystemManager):void
	{
		if (sm.isTopLevel())
		{
			sm.addEventListener(MouseEvent.MOUSE_DOWN, sm_mouseDownHandler);
			sm.addEventListener(MouseEvent.MOUSE_UP, sm_mouseUpHandler);
		}

		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_ENTER, nativeDragEventHandler, true);
		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_COMPLETE, nativeDragEventHandler, true);
		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_DROP, nativeDragEventHandler, true);
		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_EXIT, nativeDragEventHandler, true);
		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_OVER, nativeDragEventHandler, true);
		sm.stage.addEventListener(NativeDragEvent.NATIVE_DRAG_START, nativeDragEventHandler, true); 
	}
	
	/**
	 *  @private
	 *  unregister ISystemManagers that will listen for events 
	 *  (such as those for additional windows)
	 */
	mx_internal function unregisterSystemManager(sm:ISystemManager):void
	{
		if (sm.isTopLevel())
		{
			sm.removeEventListener(MouseEvent.MOUSE_DOWN, sm_mouseDownHandler);
			sm.removeEventListener(MouseEvent.MOUSE_UP, sm_mouseUpHandler);
		}

		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_ENTER, nativeDragEventHandler, true);
		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_COMPLETE, nativeDragEventHandler, true);
		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_DROP, nativeDragEventHandler, true);
		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_EXIT, nativeDragEventHandler, true);
		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_OVER, nativeDragEventHandler, true);
		sm.stage.removeEventListener(NativeDragEvent.NATIVE_DRAG_START, nativeDragEventHandler, true); 
	}
	
	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------
	private function sm_mouseDownHandler(event:MouseEvent):void
	{
		mouseIsDown = true;
	}

	private function sm_mouseUpHandler(event:MouseEvent):void
	{
		mouseIsDown = false;
	}
	
	/**
	 *  Listens for all NativeDragEvents and then redispatches them as DragEvents 
	 */
	private function nativeDragEventHandler(event:NativeDragEvent):void
	{
		var newType:String = event.type.charAt(6).toLowerCase() + event.type.substr(7);
		var dragSource:DragSource = new DragSource();
		var target:DisplayObject = event.target as DisplayObject;		
		var clipboard:Clipboard = event.clipboard;
		var origFormats:Array = clipboard.formats;
		var len:int = origFormats.length;
		var format:String;
		var data:Object;
		
		_allowedActions = event.allowedActions;
		
		//translate either commandKey or controlKey to old-style ctrlKey 
		var ctrlKey:Boolean = false;
		if (Capabilities.os.substring(0,3) == "Mac")
			ctrlKey = event.commandKey;
		else
			ctrlKey = event.controlKey;	
		//default to move if drag is from same app
		if (NativeDragManager.dragInitiator != null)
			flash.desktop.NativeDragManager.dropAction =  (ctrlKey || !_allowMove) ? DragManager.COPY : DragManager.MOVE;
		// Transfer clipboard data to dragSource	
		if (event.type != NativeDragEvent.NATIVE_DRAG_EXIT)
		{
			for (var i:int = 0; i < len; i++)
			{ 
				format = origFormats[i];
				if (clipboard.hasFormat(format))
				{
					data = clipboard.getData(format); 
					dragSource.addData(data,format);
				}
			}
		} 
		if (event.type == NativeDragEvent.NATIVE_DRAG_DROP)
			_relatedObject = event.target as InteractiveObject;
		


		// Need a dragInitiator in NativeDragEvent
		var dragEvent:DragEvent 
			= new DragEvent(newType, false, event.cancelable, 
							NativeDragManager.dragInitiator as IUIComponent, 
							dragSource, event.dropAction, ctrlKey, 
							event.altKey, event.shiftKey);
		dragEvent.buttonDown = event.buttonDown;
		dragEvent.delta = event.delta;
		dragEvent.localX = event.localX;
		dragEvent.localY = event.localY;
		if (newType == DragEvent.DRAG_COMPLETE)
			dragEvent.relatedObject = _relatedObject;
		else
			dragEvent.relatedObject = event.relatedObject;
		// Resend the event as a DragEvent.
		target.dispatchEvent(dragEvent);	 	
	}
}

}

