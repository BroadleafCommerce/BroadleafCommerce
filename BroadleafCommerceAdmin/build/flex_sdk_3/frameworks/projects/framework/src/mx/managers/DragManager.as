///////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers
{

import flash.events.MouseEvent;
import mx.automation.Automation;
import mx.core.DragSource;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.Singleton;
import mx.core.mx_internal;
import mx.managers.dragClasses.DragProxy;

/**
 *  Cursor displayed during a copy operation.
 * 
 *  The default value is the "mx.skins.cursor.DragCopy" symbol in the Assets.swf file.
 */
[Style(name="copyCursor", type="Class", inherit="no")]

/**
 *  Skin used to draw the default drag image, 
 *  which is also called the drag proxy.
 * 
 *  @default mx.skins.halo.DefaultDragImage
 */
[Style(name="defaultDragImageSkin", type="Class", inherit="no")]

/**
 *  Cursor displayed during a link operation.
 * 
 *  The default value is the "mx.skins.cursor.DragLink" symbol in the Assets.swf file.
 */
[Style(name="linkCursor", type="Class", inherit="no")]

/**
 *  Cursor displayed during a move operation.
 * 
 *  The default value is the "mx.skins.cursor.DragMove" symbol in the Assets.swf file.
 */
[Style(name="moveCursor", type="Class", inherit="no")]

/**
 *  Cursor displayed for a reject operation.
 * 
 *  The default value is the "mx.skins.cursor.DragReject" symbol in the Assets.swf file.
 */
[Style(name="rejectCursor", type="Class", inherit="no")]

/**
 *  The DragManager class manages drag and drop operations, which let you 
 *  move data from one place to another in a Flex application.
 *  For example, you can select an object, such as an item in a List control
 *  or a Flex control, such as an Image control, and then drag it over
 *  another component to add it to that component.
 *  
 *  <p>All methods and properties of the DragManager are static,
 *  so you do not need to create an instance of it.</p>
 *  
 *  <p>All Flex components support drag and drop operations.
 *  Flex provides additional support for drag and drop to the List,
 *  Tree, and DataGrid controls.</p>
 *  
 *  <p>When the user selects an item with the mouse,
 *  the selected component is called the drag initiator.
 *  The image displayed during the drag operation is called the drag proxy.</p>
 *  
 *  <p>When the user moves the drag proxy over another component,
 *  the <code>dragEnter</code> event is sent to that component. 
 *  If the component accepts the drag, it becomes the drop target
 *  and receives <code>dragOver</code>, <code>dragExit</code>,  
 *  and <code>dragDrop</code> events.</p>
 *  
 *  <p>When the drag is complete, a <code>dragComplete</code> event
 *  is sent to the drag initiator.</p>
 *  
 *  @see mx.core.DragSource
 *  @see mx.events.DragEvent
 *  @see mx.core.UIComponent
 */
public class DragManager
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constant that specifies that the type of drag action is "none".
	 */
	public static const NONE:String = "none";

	/**
	 *  Constant that specifies that the type of drag action is "copy".
	 */
	public static const COPY:String = "copy";

	/**
	 *  Constant that specifies that the type of drag action is "move".
	 */
	public static const MOVE:String = "move";

	/**
	 *  Constant that specifies that the type of drag action is "link".
	 */
	public static const LINK:String = "link";
	
	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------
	
    /**
     *  @private
     *  Linker dependency on implementation class.
     */
    private static var implClassDependency:DragManagerImpl;

    /**
     *  @private
	 *  Storage for the impl getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
     */
    private static var _impl:IDragManager;
	
	/**
	 *  @private
	 *  The singleton instance of DragManagerImpl which was
	 *  registered as implementing the IDragManager interface.
	 */
	private static function get impl():IDragManager
	{
		if (!_impl)
		{
			_impl = IDragManager(
				Singleton.getInstance("mx.managers::IDragManager"));
		}

		return _impl;
	}

	//--------------------------------------------------------------------------
	//
	//  Class properties
	//
	//--------------------------------------------------------------------------
	
    //----------------------------------
	//  dragProxy
    //----------------------------------

	/**
	 *  @private
	 *  Object being dragged around.
	 */
	mx_internal static function get dragProxy():DragProxy
	{
	    return Object(impl).dragProxy;
	}

    //----------------------------------
	//  isDragging
    //----------------------------------

	/**
	 *  Read-only property that returns <code>true</code>
	 *  if a drag is in progress.
	 */
	public static function get isDragging():Boolean
	{
		return impl.isDragging;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Class methods
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
         *
         *  @param allowMove Indicates if a drop target is allowed to move the dragged data.
	 */
	public static function doDrag(
			dragInitiator:IUIComponent, 
			dragSource:DragSource, 
			mouseEvent:MouseEvent,
			dragImage:IFlexDisplayObject = null, // instance of dragged item(s)
			xOffset:Number = 0,
			yOffset:Number = 0,
			imageAlpha:Number = 0.5,
			allowMove:Boolean = true):void
	{
		impl.doDrag(dragInitiator, dragSource, mouseEvent, dragImage, xOffset,
				yOffset, imageAlpha, allowMove);
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
	public static function acceptDragDrop(target:IUIComponent):void
	{
		impl.acceptDragDrop(target);
	}
	
	/**
	 *  Sets the feedback indicator for the drag and drop operation.
	 *  Possible values are <code>DragManager.COPY</code>, <code>DragManager.MOVE</code>,
	 *  <code>DragManager.LINK</code>, or <code>DragManager.NONE</code>.
	 *
	 *  @param feedback The type of feedback indicator to display.
	 */
	public static function showFeedback(feedback:String):void
	{
		impl.showFeedback(feedback);
	}
	
	/**
	 *  Returns the current drag and drop feedback.
	 *
	 *  @return  Possible return values are <code>DragManager.COPY</code>, 
	 *  <code>DragManager.MOVE</code>,
	 *  <code>DragManager.LINK</code>, or <code>DragManager.NONE</code>.
	 */
	public static function getFeedback():String
	{
		return impl.getFeedback();
	}
	
	/**
	 *  @private
	 */
	mx_internal static function endDrag():void
	{
		impl.endDrag();
	}
}
}

