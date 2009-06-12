////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.scrollClasses
{

import flash.events.MouseEvent;
import flash.geom.Point;
import mx.controls.Button;
import mx.core.mx_internal;
import mx.events.ScrollEventDetail;

use namespace mx_internal;

/**
 *  The ScrollThumb class defines the thumb of a ScrollBar control. 
 *
 *  @see mx.controls.scrollClasses.ScrollBar
 *  @see mx.controls.HScrollBar
 *  @see mx.controls.VScrollBar
 */
public class ScrollThumb extends Button
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
	public function ScrollThumb()
	{
		super();
				
		explicitMinHeight = 10;

		stickyHighlighting = true;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var ymin:Number;

	/**
	 *  @private
	 */
	private var ymax:Number;

	/**
	 *  @private
	 */
	private var datamin:Number;

	/**
	 *  @private
	 */
	private var datamax:Number;
	
	/**
	 *  @private
	 *  Last position of the thumb.
	 */
	private var lastY:Number;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods: Button
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override mx_internal function buttonReleased():void
	{
		super.buttonReleased();

		stopDragThumb();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Set the range of motion for the thumb:
	 *  how far it can move and what data values that covers.
	 */
	mx_internal function setRange(ymin:Number, ymax:Number,
								  datamin:Number, datamax:Number):void
	{
		this.ymin = ymin;
		this.ymax = ymax;
		
		this.datamin = datamin;
		this.datamax = datamax;
	}

	/**
	 *  @private
	 *  Stop dragging the thumb around.
	 */
	private function stopDragThumb():void
	{
		var scrollBar:ScrollBar = ScrollBar(parent);
		
		scrollBar.isScrolling = false;
		
		scrollBar.dispatchScrollEvent(scrollBar.oldPosition, 
                                      ScrollEventDetail.THUMB_POSITION);
        
		scrollBar.oldPosition = NaN;
		
		systemManager.removeEventListener(
			MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);

		// in case we go offscreen
		systemManager.stage.removeEventListener(MouseEvent.MOUSE_MOVE, 
							stage_mouseMoveHandler);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers: Button
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  User pressed on the thumb, so start tracking in case they drag it.
	 */
 	override protected function mouseDownHandler(event:MouseEvent):void
	{
		super.mouseDownHandler(event);

		var scrollBar:ScrollBar = ScrollBar(parent);
        scrollBar.oldPosition = scrollBar.scrollPosition;
		
		lastY = event.localY;
		
		systemManager.addEventListener(
			MouseEvent.MOUSE_MOVE, mouseMoveHandler, true);

		// in case we go offscreen
		systemManager.stage.addEventListener(MouseEvent.MOUSE_MOVE, 
							stage_mouseMoveHandler);
	}

	//--------------------------------------------------------------------------
	//
	//  Event handlers
	//
	//--------------------------------------------------------------------------

	private function stage_mouseMoveHandler(event:MouseEvent):void
	{
		if (event.target != stage)
			return;

		mouseMoveHandler(event);
	}

	/**
	 *  @private
	 *  Drag the thumb around and update the scroll bar accordingly.
	 */
	private function mouseMoveHandler(event:MouseEvent):void
	{
		if (ymin == ymax)
			return;

		var pt:Point = new Point(event.stageX, event.stageY);
		pt = globalToLocal(pt);
		
		var scrollMove:Number = pt.y - lastY;
		scrollMove += y;
		
		if (scrollMove < ymin)
			scrollMove = ymin;
		else if (scrollMove > ymax)
			scrollMove = ymax;

		var scrollBar:ScrollBar = ScrollBar(parent);
		
		scrollBar.isScrolling = true;
		
		$y = scrollMove;

		// In an ideal world, this would probably dispatch an event,
		// however this object is rather hardwired into a scroll bar
		// so we'll just have it tell the scroll bar to change its position.
        var oldPosition:Number = scrollBar.scrollPosition;
		var pos:Number = Math.round(
			(datamax - datamin) * (y - ymin) / (ymax - ymin)) + datamin;
		scrollBar.scrollPosition = pos;
		scrollBar.dispatchScrollEvent(oldPosition,
									  ScrollEventDetail.THUMB_TRACK);
		event.updateAfterEvent();									  
	}

}

}
