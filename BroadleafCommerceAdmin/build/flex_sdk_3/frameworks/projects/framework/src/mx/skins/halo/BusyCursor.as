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

package mx.skins.halo
{

import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.InteractiveObject;
import flash.display.Shape;
import flash.display.Sprite;
import flash.events.Event;
import mx.core.FlexShape;
import mx.core.FlexSprite;
import mx.styles.CSSStyleDeclaration;
import mx.styles.StyleManager;

/**
 *  Defines the appearance of the cursor that appears while an operation is taking place. For example, 
 *  while the SWFLoader class loads an asset.
 */
public class BusyCursor extends FlexSprite
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
	public function BusyCursor()
	{
		super();
		
		var cursorManagerStyleDeclaration:CSSStyleDeclaration =
			StyleManager.getStyleDeclaration("CursorManager");
		
		var cursorClass:Class =
			cursorManagerStyleDeclaration.getStyle("busyCursorBackground");
		
		var cursorHolder:DisplayObject = new cursorClass();
		if (cursorHolder is InteractiveObject)
			InteractiveObject(cursorHolder).mouseEnabled = false;
		addChild(cursorHolder);
		
		var xOff:Number = -0.5;
		var yOff:Number = -0.5;

		var g:Graphics;
		
		// Create the minute hand.
		minuteHand = new FlexShape();
		minuteHand.name = "minuteHand";
		g = minuteHand.graphics;
		g.beginFill(0x000000);
		g.moveTo(xOff, yOff);
		g.lineTo(1 + xOff, 0 + yOff);
		g.lineTo(1 + xOff, 5 + yOff);
		g.lineTo(0 + xOff, 5 + yOff);
		g.lineTo(0 + xOff, 0 + yOff);
		g.endFill();
		addChild(minuteHand);
		
		// Create the hour hand.
		hourHand = new FlexShape();
		hourHand.name = "hourHand";
		g = hourHand.graphics;
		g.beginFill(0x000000);
		g.moveTo(xOff, yOff);
		g.lineTo(4 + xOff, 0 + yOff);
		g.lineTo(4 + xOff, 1 + yOff);
		g.lineTo(0 + xOff, 1 + yOff);
		g.lineTo(0 + xOff, 0 + yOff);
		g.endFill();
		addChild(hourHand);
		
		addEventListener(Event.ADDED, handleAdded);
		addEventListener(Event.REMOVED, handleRemoved);
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var minuteHand:Shape;

	/**
	 *  @private
	 */
	private var hourHand:Shape;
	
	//--------------------------------------------------------------------------
	//
	//  Event Handlers
	//
	//--------------------------------------------------------------------------
	private function handleAdded(event:Event):void
	{
		addEventListener(Event.ENTER_FRAME, enterFrameHandler);
	}

	private function handleRemoved(event:Event):void
	{
		removeEventListener(Event.ENTER_FRAME, enterFrameHandler);
	}

	/**
	 *  @private
	 */
	private function enterFrameHandler(event:Event):void
	{
		minuteHand.rotation += 12;
		hourHand.rotation += 1;
	}
}

}
