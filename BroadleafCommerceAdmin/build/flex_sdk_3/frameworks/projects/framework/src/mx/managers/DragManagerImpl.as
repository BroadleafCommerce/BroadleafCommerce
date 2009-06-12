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
import flash.display.DisplayObjectContainer;
import flash.events.Event;
import flash.events.IEventDispatcher;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.geom.Point;

import mx.core.DragSource;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.effects.EffectInstance;
import mx.effects.Move;
import mx.effects.Zoom;
import mx.events.DragEvent;
import mx.managers.dragClasses.DragProxy;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

use namespace mx_internal;

[ExcludeClass]

/**
 *  @private
 */
public class DragManagerImpl implements IDragManager
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
			instance = new DragManagerImpl();
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
	public function DragManagerImpl()
	{
		super();

		if (instance)
			throw new Error("Instance already exists.");
			
		if (sm.isTopLevel())
		{
			sm.addEventListener(MouseEvent.MOUSE_DOWN, sm_mouseDownHandler);
			sm.addEventListener(MouseEvent.MOUSE_UP, sm_mouseUpHandler);
		}
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 *  Object that initiated the drag.
	 */
	private var dragInitiator:IUIComponent;

	/**
	 *  @private
	 *  Object being dragged around.
	 */
	public var dragProxy:DragProxy;

	/**
	 *  @private
	 */
	private var bDoingDrag:Boolean = false;

	/**
	 *  @private
	 */
	private var mouseIsDown:Boolean = false;

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
		return bDoingDrag;
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
		if (bDoingDrag)
			return;
		
		// Can't do a drag if the mouse isn't down
		if (!(mouseEvent.type == MouseEvent.MOUSE_DOWN ||
			  mouseEvent.type == MouseEvent.CLICK ||
			  mouseIsDown ||
			  mouseEvent.buttonDown))
		{
			return;
		}    
			
		bDoingDrag = true;
		
		this.dragInitiator = dragInitiator;

		// The drag proxy is a UIComponent with a single child -
		// an instance of the dragImage.
		dragProxy = new DragProxy(dragInitiator, dragSource);
		dragInitiator.systemManager.popUpChildren.addChild(dragProxy);	

		if (!dragImage)
		{
			// No drag image specified, use default
			var dragManagerStyleDeclaration:CSSStyleDeclaration =
				StyleManager.getStyleDeclaration("DragManager");
			var dragImageClass:Class =
				dragManagerStyleDeclaration.getStyle("defaultDragImageSkin");
			dragImage = new dragImageClass();
			dragProxy.addChild(DisplayObject(dragImage));
			proxyWidth = dragInitiator.width;
			proxyHeight = dragInitiator.height;
		}
		else
		{
			dragProxy.addChild(DisplayObject(dragImage));
			if (dragImage is ILayoutManagerClient )
				UIComponentGlobals.layoutManager.validateClient(ILayoutManagerClient (dragImage), true);
			if (dragImage is IUIComponent)
			{
				proxyWidth = (dragImage as IUIComponent).getExplicitOrMeasuredWidth();
				proxyHeight = (dragImage as IUIComponent).getExplicitOrMeasuredHeight();
			}
			else
			{
				proxyWidth = dragImage.measuredWidth;
				proxyHeight = dragImage.measuredHeight;
			}
		}

		dragImage.setActualSize(proxyWidth, proxyHeight);
		dragProxy.setActualSize(proxyWidth, proxyHeight);
		
		// Alpha
		dragProxy.alpha = imageAlpha;

		dragProxy.allowMove = allowMove;
		
		var nonNullTarget:Object = mouseEvent.target;
		if (nonNullTarget == null)
			nonNullTarget = dragInitiator;
		
		var point:Point = new Point(mouseEvent.localX, mouseEvent.localY);
		point = DisplayObject(nonNullTarget).localToGlobal(point);
		point = DisplayObject(dragInitiator.systemManager.topLevelSystemManager).globalToLocal(point);
		var mouseX:Number = point.x;
		var mouseY:Number = point.y;

		// Set dragProxy.offset to the mouse offset within the drag proxy.
		var proxyOrigin:Point = DisplayObject(nonNullTarget).localToGlobal(
						new Point(mouseEvent.localX, mouseEvent.localY));
		proxyOrigin = DisplayObject(dragInitiator).globalToLocal(proxyOrigin);
		dragProxy.xOffset = proxyOrigin.x + xOffset;
		dragProxy.yOffset = proxyOrigin.y + yOffset;
		
		// Call onMouseMove to setup initial position of drag proxy and cursor.
		dragProxy.x = mouseX - dragProxy.xOffset;
		dragProxy.y = mouseY - dragProxy.yOffset;
		
		// Remember the starting location of the drag proxy so it can be
		// "snapped" back if the drop was refused.
		dragProxy.startX = dragProxy.x;
		dragProxy.startY = dragProxy.y;

		// Turn on caching.
		if (dragImage is DisplayObject) 
			DisplayObject(dragImage).cacheAsBitmap = true;
			

		var delegate:Object = dragProxy.automationDelegate;
		if (delegate)
			delegate.recordAutomatableDragStart(dragInitiator, mouseEvent);
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
		if (dragProxy)
			dragProxy.target = target;
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
		if (dragProxy)
		{
			if (feedback == DragManager.MOVE && !dragProxy.allowMove)
				feedback = DragManager.COPY;

			dragProxy.action = feedback;
		}
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
		return dragProxy ? dragProxy.action : DragManager.NONE;
	}
	
	/**
	 *  @private
	 */
	public function endDrag():void
	{
		if (dragProxy)
		{
			var sm:ISystemManager = dragInitiator.systemManager;
			sm.popUpChildren.removeChild(dragProxy);
			
			dragProxy.removeChildAt(0);	// The drag image is the only child
			dragProxy = null;
		}
		
		dragInitiator = null;
		bDoingDrag = false;
	}
			
	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function sm_mouseDownHandler(event:MouseEvent):void
	{
		mouseIsDown = true;
	}
	
	/**
	 *  @private
	 */
	private function sm_mouseUpHandler(event:MouseEvent):void
	{
		mouseIsDown = false;
	}
}

}

