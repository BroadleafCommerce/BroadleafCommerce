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

package mx.effects.effectClasses
{

import flash.events.Event;
import flash.events.MouseEvent;
import mx.core.mx_internal;
import mx.effects.EffectManager;
import mx.events.FlexEvent;

/**
 *  The ZoomInstance class implements the instance class for the Zoom effect.
 *  Flex creates an instance of this class when it plays a Zoom effect;
 *  you do not create one yourself.
 *
 *  <p>Every effect class that is a subclass of the TweenEffect class 
 *  supports the following events:</p>
 *  
 *  <ul>
 *    <li><code>tweenEnd</code>: Dispatched when the tween effect ends. </li>
 *  
 *    <li><code>tweenUpdate</code>: Dispatched every time a TweenEffect 
 *      class calculates a new value.</li> 
 *  </ul>
 *  
 *  <p>The event object passed to the event listener for these events is of type TweenEvent. 
 *  The TweenEvent class defines the property <code>value</code>, which contains 
 *  the tween value calculated by the effect. 
 *  For the Zoom effect, 
 *  the <code>TweenEvent.value</code> property contains a 2-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:Number  A value between the values of the <code>Zoom.zoomWidthFrom</code> 
 *    and <code>Zoom.zoomWidthTo</code> property.</li>
 *  
 *    <li>value[1]:Number  A value between the values of the <code>Zoom.zoomHeightFrom</code> 
 *    and <code>Zoom.zoomHeightTo</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.Zoom
 *  @see mx.events.TweenEvent
 */  
public class ZoomInstance extends TweenEffectInstance
{
    include "../../core/Version.as";

 	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param target The Object to animate with this effect.
	 */
	public function ZoomInstance(target:Object)
	{
		super(target);
	}

 	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var origScaleX:Number;
	
	/**
	 *  @private
	 */
	private var origScaleY:Number;
	
	/**
	 *  @private
	 */
	private var origX:Number;
	
	/**
	 *  @private
	 */
	private var origY:Number;
	
	/**
	 *  @private
	 */
	private var newX:Number;
	
	/**
	 *  @private
	 */
	private var newY:Number;
	
	/**
	 *  @private
	 */
	private var scaledOriginX:Number;
	
	/**
	 *  @private
	 */
	private var scaledOriginY:Number;
	
	/**
	 *  @private
	 */
	private var origPercentWidth:Number;
	
	/**
	 *  @private
	 */
	private var origPercentHeight:Number;

	/**
	 *  @private
	 */
	private var _mouseHasMoved:Boolean = false;
	
	/**
	 *  @private
	 */
	private var show:Boolean = true;
	
 	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  captureRollEvents
	//----------------------------------

	/**
	 *  Prevents the <code>rollOut</code> and <code>rollOver</code> events
	 *  from being dispatched if the mouse has not moved.
	 *  Set this value to <code>true</code> in situations where the target
	 *  toggles between a big and small state without moving the mouse.
	 *
	 *  @default false
	 */
	public var captureRollEvents:Boolean;

	//----------------------------------
	//  originX
	//----------------------------------

	/**
	 *  Number that represents the x-position of the zoom origin,
	 *  or registration point.
	 *  The default value is <code>target.width / 2</code>,
	 *  which is the center of the target.
	 */
	public var originX:Number;
	
	//----------------------------------
	//  originY
	//----------------------------------

	/**
	 *  Number that represents the y-position of the zoom origin,
	 *  or registration point.
	 *  The default value is <code>target.height / 2</code>,
	 *  which is the center of the target.
	 */
	public var originY:Number;
	
	//----------------------------------
	//  zoomHeightFrom
	//----------------------------------

	/**
	 *  Number that represents the scale at which to start the height zoom, 
	 *  as a percent between 0.01 and 1.0. 
	 *  The default value is 0.01, which is very small.
	 */
	public var zoomHeightFrom:Number;
	
	//----------------------------------
	//  zoomHeightTo
	//----------------------------------

	/**
	 *  Number that represents the scale at which to complete the height zoom, 
	 *  as a percent between 0.01 and 1.0. 
	 *  The default value is 1.0, which is the object's normal size.
	 */
	public var zoomHeightTo:Number;
		
	//----------------------------------
	//  zoomWidthFrom
	//----------------------------------

	/**
	 *  Number that represents the scale at which to start the width zoom, 
	 *  as a percent between 0.01 and 1.0. 
	 *  The default value is 0.01, which is very small.
	 */
	public var zoomWidthFrom:Number;
	
	//----------------------------------
	//  zoomWidthTo
	//----------------------------------

	/**
	 *  Number that represents the scale at which to complete the width zoom, 
	 *  as a percent between 0.01 and 1.0. 
	 *  The default value is 1.0, which is the object's normal size.
	 */
	public var zoomWidthTo:Number;
	
 	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	override public function initEffect(event:Event):void
	{
		super.initEffect(event);

		if (event.type == FlexEvent.HIDE || event.type == Event.REMOVED)
		{
			show = false;
		}
		
	}
	
	/**
	 *  @private
	 */
	override public function play():void
	{		
		super.play();

		applyPropertyChanges();
		
		if (isNaN(zoomWidthFrom) && isNaN(zoomWidthTo) 
			&& isNaN(zoomHeightFrom) && isNaN(zoomHeightTo) )
		{
			if (show)
			{
				// This is a "show" effect, so zoom from zero to original
				// size.  (If neither show nor hide effect is specified, 
				// guess "show")
				zoomWidthFrom = zoomHeightFrom = 0;
				zoomWidthTo = target.scaleX;
				zoomHeightTo = target.scaleY;
			}
			else
			{
				// This is a "hide" effect, so zoom down to zero.
				zoomWidthFrom = target.scaleX;
				zoomHeightFrom = target.scaleY;
				zoomWidthTo = zoomHeightTo =  0;
			}
		}
		else 
		{
			// if only height zoom is specified, then we leave the width zoom alone
			if (isNaN(zoomWidthFrom) && isNaN(zoomWidthTo))
			{
				zoomWidthFrom = zoomWidthTo = target.scaleX;
			}
			// if only width zoom is specified, then we leave the height zoom alone
			else if (isNaN(zoomHeightFrom) && isNaN(zoomHeightTo))
			{
				zoomHeightFrom = zoomHeightTo = target.scaleY;
			}
		
			if (isNaN(zoomWidthFrom))
			{
				// If no "from" amount is specified, use the current zoom.
				zoomWidthFrom = target.scaleX;
			}
			else if (isNaN(zoomWidthTo))
			{
				// If no "to" amount is specified, choose a "to" amount of
				// either 1.0 or 0, but make sure "from" and "to" are different
				zoomWidthTo = (zoomWidthFrom == 1.0) ? 0 : 1.0;
			}
			
			if (isNaN(zoomHeightFrom))
			{
				// If no "from" amount is specified, use the current zoom.
				zoomHeightFrom = target.scaleY;
			}
			else if (isNaN(zoomHeightTo))
			{
				// If no "to" amount is specified, choose a "to" amount of
				// either 1.0 or 0, but make sure "from" and "to" are different
				zoomHeightTo = (zoomHeightFrom == 1.0) ? 0 : 1.0;
			} 
		}
		// Guard against bogus input and divide-by-zero
		if (zoomWidthFrom < 0.01)
			zoomWidthFrom = 0.01;
		if (zoomWidthTo < 0.01)
			zoomWidthTo = 0.01;
		if (zoomHeightFrom < 0.01)
			zoomHeightFrom = 0.01;
		if (zoomHeightTo < 0.01)
			zoomHeightTo = 0.01;
		
		// Remember the original appearance
		origScaleX = target.scaleX;
		origScaleY = target.scaleY;
		newX = origX = target.x;
		newY = origY = target.y;
					
		// Use the center position if no origin has been specified.	
		if (isNaN(originX))
		{
			scaledOriginX = target.width / 2;
		}
		else
		{
			// Origin position adjusted for scale
			scaledOriginX = originX * origScaleX;
		}	
		if (isNaN(originY))
		{
			scaledOriginY = target.height / 2;
		}
		else
		{
			// Origin position adjusted for scale
			scaledOriginY = originY * origScaleY;
		}
						
		scaledOriginX = Number(scaledOriginX.toFixed(1));
		scaledOriginY = Number(scaledOriginY.toFixed(1));
	
		// If the object is flexible, set it to not be flexible for the
		// duration of the zoom. It'll still flex when the zoom is complete,
		// but we'll avoid hanging the player (bug 100035).
		origPercentWidth = target.percentWidth;
		if (!isNaN(origPercentWidth))
			target.width = target.width; // Set width to be expressed in pixels
		origPercentHeight = target.percentHeight;
		if (!isNaN(origPercentHeight))
			target.height = target.height;
	
		// Create a tween to resize the object
		tween = createTween(this, [zoomWidthFrom,zoomHeightFrom], [zoomWidthTo,zoomHeightTo], duration);

		// Capture mouse events
		if (captureRollEvents)
		{
			target.addEventListener(MouseEvent.ROLL_OVER, mouseEventHandler, false);
			target.addEventListener(MouseEvent.ROLL_OUT, mouseEventHandler, false);
			target.addEventListener(MouseEvent.MOUSE_MOVE, mouseEventHandler, false);
		}
	}
	
	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		// If we're in the middle of a three-frame instantiation, let it
		// finish before we start changing objects.  Otherwise, we'll
		// dirty measurements after every frame, and the three-frame
		// process will never get past the "measure" step.
		//if (suspendBackgroundProcessing) //UIComponent.useLayoutManager &&)
		//	return;

		// Tell the EffectManager not to listen to the "move" event.
		// Otherwise, moveEffect="Move" would cause a new Move effect
		// to be create with each onTweenUpdate.
		EffectManager.suspendEventHandling();

		// Check if we have been moved since the last time we updated
		if (Math.abs(newX - target.x) > 0.1)
		{
			origX += Number(target.x.toFixed(1)) - newX;
		}
		
		if (Math.abs(newY - target.y) > 0.1)
		{
			origY += Number(target.y.toFixed(1)) - newY;
		}
		
		target.scaleX = value[0];
		target.scaleY = value[1];
		
		var ratioX:Number = value[0] / origScaleX;
		var ratioY:Number = value[1] / origScaleY;
		
		var newOriginX:Number = scaledOriginX * ratioX;
		var newOriginY:Number = scaledOriginY * ratioY;

		newX = scaledOriginX - newOriginX + origX;
		newY = scaledOriginY - newOriginY + origY;
		
		newX = Number(newX.toFixed(1));
		newY = Number(newY.toFixed(1));
		
		// Adjust position relative to the origin	
		target.move(newX,newY);
			
		// Set a flag indicating that LayoutManager.validateNow() should
		// be called after we're finished processing all the effects for
		// this frame.
		tween.mx_internal::needToLayout = true;

		EffectManager.resumeEventHandling();
	}

	/**
	 *  @private
	 */
	override public function onTweenEnd(value:Object):void
	{
		// If object's size was originally specified using percentages,
		// then restore percentages now. That way, the object will
		// resize when its parent is resized.
		if (!isNaN(origPercentWidth))
		{
			var curWidth:Number = target.width;
			
			target.percentWidth = origPercentWidth;
			
			// Setting an object to have percentage widths will set its
			// actual width to undefined. If the parent's autoLayout is
			// false, setting its actual width to undefined will cause it
			// to be rendered as with a width and height of zero. To
			// avoid that situation, set its _width and _height explicitly.	
			if (target.parent && target.parent.autoLayout == false)
				target.mx_internal::_width = curWidth;	
		}
		if (!isNaN(origPercentHeight))
		{
			var curHeight:Number = target.height;

			target.percentHeight = origPercentHeight;

			if (target.parent && target.parent.autoLayout == false)
				target.mx_internal::_height = curHeight;
		}

		super.onTweenEnd(value);
		
		if (mx_internal::hideOnEffectEnd)
		{
			EffectManager.suspendEventHandling();
			
			target.scaleX = origScaleX;
			target.scaleY = origScaleY;
			target.move(origX, origY);
			EffectManager.resumeEventHandling();
		}
	}
	
	/**
	 *  @private
	 */
	override public function finishEffect():void
	{
		// Remove the event listeners
		if (captureRollEvents)
		{
			target.removeEventListener(MouseEvent.ROLL_OVER, mouseEventHandler, false);
			target.removeEventListener(MouseEvent.ROLL_OUT, mouseEventHandler, false);
			target.removeEventListener(MouseEvent.MOUSE_MOVE, mouseEventHandler, false);
		}
		super.finishEffect();
	}
	
 	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function applyPropertyChanges():void
	{
		// Apply the properties in the following priority:
		// scaleX/scaleY, width/height, visible
		var values:PropertyChanges = propertyChanges;
	
		if (values)
		{		
			var useSize:Boolean = false;
			var useScale:Boolean = false;
		
			if (values.end["scaleX"] !== undefined)
			{
				zoomWidthFrom = isNaN(zoomWidthFrom) ? target.scaleX : zoomWidthFrom;
				zoomWidthTo = isNaN(zoomWidthTo) ? values.end["scaleX"] : zoomWidthTo;
				useScale = true;
			}	
					
			if (values.end["scaleY"] !== undefined)
			{
				zoomHeightFrom = isNaN(zoomHeightFrom) ? target.scaleY : zoomHeightFrom;
				zoomHeightTo = isNaN(zoomHeightTo) ? values.end["scaleY"] : zoomHeightTo;
				useScale = true;
			}
						
			if (useScale)
				return;		
					
			if (values.end["width"] !== undefined)
			{
				zoomWidthFrom = isNaN(zoomWidthFrom) ?
						   getScaleFromWidth(target.width) :
						   zoomWidthFrom;
				zoomWidthTo = isNaN(zoomWidthTo) ?
						 getScaleFromWidth(values.end["width"]) :
						 zoomWidthTo;
				useSize = true;
			}
			
			if (values.end["height"] !== undefined)
			{
				zoomHeightFrom = isNaN(zoomHeightFrom) ?
						   getScaleFromHeight(target.height) :
						   zoomHeightFrom;
				zoomHeightTo = isNaN(zoomHeightTo) ?
						 getScaleFromHeight(values.end["height"]) :
						 zoomHeightTo;
				useSize = true;
			}
			
			if (useSize)
				return;
						
			if (values.end["visible"] !== undefined)
				show = values.end["visible"];
		}
	}
	
	/**
	 *  @private
	 */
	private function getScaleFromWidth(value:Number):Number
	{
		return value / (target.width / Math.abs(target.scaleX));
	}
	
	/**
	 *  @private
	 */
	private function getScaleFromHeight(value:Number):Number
	{
		return value / (target.height / Math.abs(target.scaleY));
	}
	
 	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function mouseEventHandler(event:MouseEvent):void
	{
		// The purpose of this logic is to prevent the situation
		// where the target toggles between the mouseOver and mouseOut
		// effects when the mouse is placed at certain positions. 
		// Now we stop sending mouseOut and mouseOver events if the
		// mouse has not moved.
		
		if (event.type == MouseEvent.MOUSE_MOVE)
		{
			_mouseHasMoved = true;
		}
		else if (event.type == MouseEvent.ROLL_OUT ||
				 event.type == MouseEvent.ROLL_OVER)
		{
			if (!_mouseHasMoved)
				event.stopImmediatePropagation();
						
			_mouseHasMoved = false;
		}
	}
}

}
