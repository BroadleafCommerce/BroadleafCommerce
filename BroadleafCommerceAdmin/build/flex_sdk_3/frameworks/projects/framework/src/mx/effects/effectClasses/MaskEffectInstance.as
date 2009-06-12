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

import flash.display.BitmapData;
import flash.display.DisplayObject;
import flash.display.Graphics;
import flash.display.Shape;
import flash.events.Event;
import flash.filters.DropShadowFilter;
import flash.geom.Matrix;
import flash.geom.Rectangle;
import flash.utils.getTimer;
import mx.controls.SWFLoader;
import mx.core.Container;
import mx.core.FlexShape;
import mx.core.IInvalidating;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.effects.EffectInstance;
import mx.effects.EffectManager;
import mx.effects.Tween;
import mx.events.FlexEvent;
import mx.events.ResizeEvent;
import mx.events.TweenEvent;

use namespace mx_internal;

/**
 *  The MaskEffectInstance class is an abstract base class 
 *  that implements the instance class for 
 *  the MaskEffect class. 
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
 *  For the Mask effect, 
 *  the <code>TweenEvent.value</code> property contains a 4-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:Number  The value of the target's <code>x</code> property.</li> 
 *  
 *    <li>value[1]:Number  The value of the target's <code>y</code> property.</li>
 *  
 *    <li>value[2]:Number  The value of the target's <code>scaleX</code> property.</li>
 *  
 *    <li>value[3]:Number  The value of the target's <code>scaleY</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.MaskEffect
 *  @see mx.events.TweenEvent
 */  
public class MaskEffectInstance extends EffectInstance
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
	public function MaskEffectInstance(target:Object)
	{
		super(target);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  Contains the effect mask, either the default mask created 
	 *  by the <code>defaultCreateMask()</code> method, 
	 *  or the one specified by the function passed to the 
	 *  <code>createMaskFunction</code> property.
	 */
	protected var effectMask:Shape;
	
	/**
	 *  The actual size of the effect target, including any drop shadows. 
	 *  Flex calculates the value of this property; you do not have to set it. 
	 */
	protected var targetVisualBounds:Rectangle;
	
	/**
	 *  @private
	 */
	private var effectMaskRefCount:Number = 0;
	
	/**
	 *  @private
	 */
	private var invalidateBorder:Boolean = false;
	
	/**
	 *  @private
	 */
	private var moveTween:Tween;
	
	/**
	 *  @private
	 */
	private var origMask:DisplayObject;
	
	/** 
	 *  @private
	 */
	private var origScrollRect:Rectangle;
	
	/**
	 *  @private
	 */
	private var scaleTween:Tween;
	
	/**
	 *  @private
	 */
	private var tweenCount:int = 0;
	
	/**
	 *  @private
	 */
	private var currentMoveTweenValue:Object;
	
	/**
	 *  @private
	 */
	private var currentScaleTweenValue:Object;
	
	/**
	 *  @private
	 */
	private var MASK_NAME:String = "_maskEffectMask";
	
	/**
	 *  @private
	 */	
	private var dispatchedStartEvent:Boolean = false;
	
	/**
	 *  @private
	 */	
	private var useSnapshotBounds:Boolean = true;

	/**
	 *  @private
	 */	
	private var stoppedEarly:Boolean = false;

	/** 
	 *  @private 
	 */
	mx_internal var persistAfterEnd:Boolean = false;
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//--------------------------------------------------------------------------
	//  createMaskFunction
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Storage for the createMaskFunction property.
	 */
	private var _createMaskFunction:Function;
		
	/**
	 *  Function called when the effect creates the mask.
	 *  The default value is a function that returns a Rectangle
	 *  with the same dimensions as the effect target. 
	 *
	 *  <p>You can use this property to specify your own callback function to draw the mask. 
	 *  The function must have the following signature:</p>
	 * 
	 *  <pre>
	 *  public function createLargeMask(targ:Object, bounds:Rectangle):Shape {
	 *    var myMask:Shape = new Shape();
	 *    // Create mask.
	 *  
	 *    return myMask;
	 *  }
	 *  </pre>
	 *
	 *  <p>You set this property to the name of the function, 
	 *  as the following example shows for the WipeLeft effect:</p>
	 * 
	 *  <pre>
	 *    &lt;mx:WipeLeft id="showWL" createMaskFunction="createLargeMask" showTarget="false"/&gt;</pre>
	 */
	public function get createMaskFunction():Function
	{
		return _createMaskFunction != null ?
			   _createMaskFunction :
			   defaultCreateMask;
	}

	/**
	 *  @private
	 */
	public function set createMaskFunction(value:Function):void
	{
		_createMaskFunction = value;
	}
	
	//----------------------------------
	//  moveEasingFunction
	//----------------------------------

	/**
	 *  Easing function to use for moving the mask.
	 */		
	public var moveEasingFunction:Function;
	
	//--------------------------------------------------------------------------
	//  playheadTime
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function get playheadTime():Number
	{
		var value:Number;
		
		if (moveTween)
			value = moveTween.mx_internal::playheadTime;
		
		else if (scaleTween)
			value = scaleTween.mx_internal::playheadTime;
		
		else
			return 0;
			
		return value + super.playheadTime;	
		
	}
	
	//--------------------------------------------------------------------------
	//  playReversed
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override mx_internal function set playReversed(value:Boolean):void
	{
		if (moveTween)
			moveTween.playReversed = value;
		
		if (scaleTween)
			scaleTween.playReversed = value;
		
		super.playReversed = value;	
	}
	
	//----------------------------------
	//  scaleEasingFunction
	//----------------------------------

	/**
	 *  Easing function to use for scaling the mask.
	 */	
	public var scaleEasingFunction:Function;	
		
	//----------------------------------
	//  scaleXFrom
	//----------------------------------

	/**
	 *  Initial scaleX for mask.
	 */
	public var scaleXFrom:Number;
	
	//----------------------------------
	//  scaleXTo
	//----------------------------------

	/** 
	 *  Ending scaleX for mask.
	 */
	public var scaleXTo:Number;

	//----------------------------------
	//  scaleYFrom
	//----------------------------------
	
	/** 
	 *  Initial scaleY for mask.
	 */
	public var scaleYFrom:Number;
	
	//----------------------------------
	//  scaleYTo
	//----------------------------------

	/**
	 *  Ending scaleY for mask.
	 */
	public var scaleYTo:Number;

	//----------------------------------
	//  showTarget
	//----------------------------------

	[Inspectable(category="General", defaultValue="true")]
	
	/**
	 *  @private
	 *  Storage for the showTarget property.
	 */
	private var _showTarget:Boolean = true;
	
	/**
	 *  @private
	 */
	private var _showExplicitlySet:Boolean = false;
	
	/**
     *  Specifies that the target component is becoming visible, 
     *  <code>false</code>, or invisible, <code>true</code>.
	 *
	 *  @default true
	 */
	public function get showTarget():Boolean
	{
		return _showTarget;
	}

	/**
	 *  @private
	 */
	public function set showTarget(value:Boolean):void
	{
		_showTarget = value;
		_showExplicitlySet = true;
	}
	
	//----------------------------------
	//  targetArea
	//----------------------------------

	/**
	 *  The area where the mask is applied on the target.
	 *  The dimensions are relative to the target itself.
	 *  By default, the area is the entire target and is created like this: 
	 *  <code>new Rectangle(0, 0, target.width, target.height);</code>
	 */
	public var targetArea:Rectangle;
	
	//----------------------------------
	//  xFrom
	//----------------------------------

	/** 
	 *  Initial position's x coordinate for mask.  
	 */
	public var xFrom:Number;
	
	//----------------------------------
	//  xTo
	//----------------------------------

	/** 
	 *  Destination position's x coordinate for mask.  
	 */
	public var xTo:Number;
	
	//----------------------------------
	//  yFrom
	//----------------------------------

	/**
	 *  Initial position's y coordinate for mask.  
	 */
	public var yFrom:Number;
	
	//----------------------------------
	//  yTo
	//----------------------------------

	/** 
	 *  Destination position's y coordinate for mask.  
	 */
	public var yTo:Number;
	
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

		switch (event.type)
		{	
			case "childrenCreationComplete":
			case FlexEvent.CREATION_COMPLETE:
			case FlexEvent.SHOW:
			case Event.ADDED:
			case "resizeEnd":
			{
				showTarget = true;
				break;
			}
		
			case FlexEvent.HIDE:
			case Event.REMOVED:
			case "resizeStart":
			{
				showTarget = false;
				break;
			}
			case Event.RESIZE:
			{
				// don't use the snapshot because it will be the wrong size
				useSnapshotBounds = false; 
				break;
			}
		}
	}

	/**
	 *  @private
	 */
	override public function startEffect():void
	{
		// Init the mask only once when the effect is played.
		initMask();
		
		// Register to be notified if the target object is resized.
		target.addEventListener(ResizeEvent.RESIZE, eventHandler);

		// This will call playEffect eventually.
		super.startEffect();
	}

	/**
	 *  @private
	 */
	override public function play():void
	{		
		super.play();
		
		// This allows the MaskEffect subclass to set the effect properties.
		initMaskEffect();
		
		EffectManager.mx_internal::startVectorEffect(IUIComponent(target));
				
		//EffectManager.mx_internal::startBitmapEffect(target);

		// Move Tween
		
		if (!isNaN(xFrom) &&
			!isNaN(yFrom) &&
			!isNaN(xTo) &&
			!isNaN(yTo))
		{ 
			tweenCount++;
			
			moveTween = new Tween(this, [ xFrom, yFrom ],
								  [ xTo, yTo ], duration, 
								  -1, onMoveTweenUpdate, onMoveTweenEnd);
	
			moveTween.playReversed = playReversed;
	
			// If the caller supplied their own easing equation, override the
			// one that's baked into Tween.
			if (moveEasingFunction != null)
				moveTween.easingFunction = moveEasingFunction;
	
			
		}

		// Scale Tween
		
		if (!isNaN(scaleXFrom) &&
			!isNaN(scaleYFrom) &&
			!isNaN(scaleXTo) &&
			!isNaN(scaleYTo))
		{ 
			tweenCount++;
			
			scaleTween = new Tween(this, [ scaleXFrom, scaleYFrom ],
								   [ scaleXTo, scaleYTo ], duration,
								   -1, onScaleTweenUpdate, onScaleTweenEnd);
	
			scaleTween.playReversed = playReversed;
	
			// If the caller supplied their own easing equation, override the
			// one that's baked into Tween.
			if (scaleEasingFunction != null)
				scaleTween.easingFunction = scaleEasingFunction;
		}
		
		dispatchedStartEvent = false;
		
		// Call these after tween creation so that saveTweenValues knows which values to dispatch
		if (moveTween)
		{
			// Set the animation to the initial value
			// before the screen refreshes.
			onMoveTweenUpdate(moveTween.mx_internal::getCurrentValue(0));
		}
		
		if (scaleTween)
		{
			// Set the animation to the initial value
			// before the screen refreshes.
			onScaleTweenUpdate(scaleTween.mx_internal::getCurrentValue(0));
		}
	}
	
	/**
	 *  Pauses the effect until you call the <code>resume()</code> method.
	 */
	override public function pause():void
	{	
		super.pause();
	
		if (moveTween)
			moveTween.pause();

		if (scaleTween)
			scaleTween.pause();
	}

	/**
	 *  @private
	 */
	override public function stop():void
	{
		stoppedEarly = true;
		super.stop();
		
		if (moveTween)
			moveTween.stop();
		
		if (scaleTween)
			scaleTween.stop();
	}	
	
	/**
	 *  Resumes the effect after it has been paused 
	 *  by a call to the <code>pause()</code> method. 
	 */
	override public function resume():void
	{
		super.resume();
	
		if (moveTween)
			moveTween.resume();

		if (scaleTween)
			scaleTween.resume();
	}
		
	/**
	 *  Plays the effect in reverse,
	 *  starting from the current position of the effect.
	 */
	override public function reverse():void
	{
		super.reverse();
		
		if (moveTween)
			moveTween.reverse();

		if (scaleTween)
			scaleTween.reverse();
			
		super.playReversed = !playReversed;
	}
	
	/**
	 *  @private
	 */
	override public function end():void
	{
		stopRepeat = true;
		
		if (moveTween)
			moveTween.endTween();

		if (scaleTween)
			scaleTween.endTween();
	}
		
	/**
	 *  @private
	 */
	override public function finishEffect():void
	{
		target.removeEventListener(ResizeEvent.RESIZE, eventHandler);
		
		if (!persistAfterEnd && !stoppedEarly)
			removeMask();
		
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
	private function initMask():void
	{
		if (!effectMask)
		{
			if (useSnapshotBounds)
				targetVisualBounds = getVisibleBounds(DisplayObject(target));
			else
				targetVisualBounds = new Rectangle(0, 0, target.width, target.height);
			effectMask = createMaskFunction(target, targetVisualBounds);

			// For Containers we need to add the mask
			// to the "allChildren" collection so it doesn't get
			// treated as a content child.
			if (target is Container)
				target.rawChildren.addChild(effectMask); 
			else
				target.addChild(effectMask); 

			effectMask.name = MASK_NAME;	
			effectMaskRefCount = 0;
		}

		effectMask.x = 0;
		effectMask.y = 0;
		effectMask.alpha = .3;
		effectMask.visible = false;

		// If this object already had a transparency mask, then save off
		// the original mask, so that we can restore it when we're done.
		if (effectMaskRefCount++ == 0)
		{
			if (target.mask)
				origMask = target.mask;	

			target.mask = effectMask;	
				
			if (target.scrollRect)
			{
				origScrollRect = target.scrollRect;
				target.scrollRect = null;
			}		
		}
		
		invalidateBorder = target is Container && 
						   Container(target).border != null &&
						   Container(target).border is IInvalidating && 
						   DisplayObject(Container(target).border).filters != null;
	}
	
	/**
	 *  Creates the default mask for the effect.
	 *
	 *  @param targ The effect target.
	 *  @param bounds The actual visual bounds of the target which includes drop shadows
	 *  
	 *  @return A Shape object that defines the mask.
	 */
	protected function defaultCreateMask(targ:Object, bounds:Rectangle):Shape
	{
		// By default, create a mask that is the shape of the target.		
		var targetWidth:Number = bounds.width / Math.abs(targ.scaleX);
		var targetHeight:Number = bounds.height / Math.abs(targ.scaleY);
		
		if (targ is SWFLoader)
		{
			// Make sure the loader's content has been sized
			targ.validateDisplayList(); 
			if (targ.content)
			{
				targetWidth = targ.contentWidth;
				targetHeight = targ.contentHeight;
			}
		}
		
		var newMask:Shape = new FlexShape();
				
		var g:Graphics = newMask.graphics;
		g.beginFill(0xFFFF00);
		g.drawRect(0, 0, targetWidth, targetHeight);
		g.endFill();
	
		if (target.rotation == 0)
		{
			newMask.width = targetWidth;
			newMask.height = targetHeight;
		}
		else
		{
			var angle:Number = targ.rotation * Math.PI / 180;
			
			var sin:Number = Math.sin(angle);
			var cos:Number = Math.cos(angle);
			
			newMask.width =  Math.abs(targetWidth * cos - targetHeight * sin);
			newMask.height = Math.abs(targetWidth * sin + targetHeight * cos);
		}
		
		return newMask;
	}
	
	/**
	 *  Initializes the <code>move</code> and <code>scale</code>
	 *  properties of the effect. 
	 *  All subclasses should override this function.
	 *  Flex calls it after the mask has been created,
	 *  but before the tweens are created. 
	 */
	protected function initMaskEffect():void
	{
		if (!_showExplicitlySet &&
			propertyChanges &&
			propertyChanges.start["visible"] !== undefined)
		{
			_showTarget = !propertyChanges.start["visible"];
		}
	}
	
	/**
	 *  @private
	 *  Returns a rectangle that describes the visible region of the component, including any dropshadows
	 */
	private function getVisibleBounds(targ:DisplayObject):Rectangle
	{	
		var bitmap:BitmapData = new BitmapData(targ.width + 200, targ.height + 200, true, 0x00000000);
		var m:Matrix = new Matrix();
		m.translate(100, 100);
		bitmap.draw(targ, m);
		var actualBounds:Rectangle = bitmap.getColorBoundsRect(0xFF000000, 0x00000000, false);
		
		actualBounds.x = actualBounds.x - 100;
		actualBounds.y = actualBounds.y - 100;

		bitmap.dispose();
		
		if (actualBounds.width < targ.width)
		{
			actualBounds.width = targ.width;
			actualBounds.x = 0;
		}
		if (actualBounds.height < targ.height)
		{
			actualBounds.height = targ.height;
			actualBounds.y = 0;
		}
		
		return actualBounds;
	}

	/** 
	 *  Callback method that is called when the x and y position 
	 *  of the mask should be updated by the effect. 
	 *  You do not call this method directly. 
     *  This method implements the method of the superclass. 
     *
 	 *  @param value Contains an interpolated 
	 *  x and y value for the mask position, where <code>value[0]</code> 
	 *  contains the new x position of the mask, 
	 *  and <code>value[1]</code> contains the new y position.    
	 */
	protected function onMoveTweenUpdate(value:Object):void 
	{
		saveTweenValue(value,null);
	
		if (effectMask)
		{
			effectMask.x = value[0];
			effectMask.y = value[1];
		}

		if (invalidateBorder)
			IInvalidating(Container(target).border).invalidateDisplayList();
	}

	/** 
	 *  Callback method that is called when the x and y position 
	 *  of the mask should be updated by the effect for the last time. 
	 *  You do not call this method directly. 
     *  This method implements the method of the superclass. 
     *
 	 *  @param value Contains the final 
	 *  x and y value for the mask position, where <code>value[0]</code> 
	 *  contains the x position of the mask, 
	 *  and <code>value[1]</code> contains the y position.    
	 */
	protected function onMoveTweenEnd(value:Object):void
	{
		onMoveTweenUpdate(value);

		finishTween();
	}
	
	/** 
	 *  Callback method that is called when the 
	 *  <code>scaleX</code> and <code>scaleY</code> properties 
	 *  of the mask should be updated by the effect. 
	 *  You do not call this method directly. 
     *  This method implements the method of the superclass. 
     *
 	 *  @param value Contains an interpolated 
	 *  <code>scaleX</code> and <code>scaleY</code> value for the mask, 
	 *  where <code>value[0]</code> 
	 *  contains the new <code>scaleX</code> value of the mask, 
	 *  and <code>value[1]</code> contains the new <code>scaleY</code> value.    
	 */
	protected function onScaleTweenUpdate(value:Object):void 
	{
		saveTweenValue(null, value);
	
		if (effectMask)
		{
			effectMask.scaleX = value[0];
			effectMask.scaleY = value[1];
		}
	}
	
	/** 
	 *  Callback method that is called when the 
	 *  <code>scaleX</code> and <code>scaleY</code> properties 
	 *  of the mask should be updated by the effect for the last time. 
	 *  You do not call this method directly. 
     *  This method implements the method of the superclass. 
     *
 	 *  @param value Contains the final 
	 *  <code>scaleX</code> and <code>scaleY</code> value for the mask, 
	 *  where <code>value[0]</code> 
	 *  contains the <code>scaleX</code> value of the mask, 
	 *  and <code>value[1]</code> contains the <code>scaleY</code> value.    
	 */
	protected function onScaleTweenEnd(value:Object):void
	{
		onScaleTweenUpdate(value);
		
		finishTween();
	}

	/**
	 *  @private
	 */
	private function finishTween():void
	{
		if (tweenCount == 0 || --tweenCount == 0)
		{
			EffectManager.mx_internal::endVectorEffect(IUIComponent(target));
			
			var values:Array = [];
			var value:Object;
			if (moveTween)
			{
				value = moveTween.getCurrentValue(duration);
				values.push(value[0]);
				values.push(value[1]);
			}
			else
			{
				values.push(null);
				values.push(null);
			}
			
			if (scaleTween)
			{
				value = scaleTween.getCurrentValue(duration);
				values.push(value[0]);
				values.push(value[1]);
			}
			else
			{
				values.push(null);
				values.push(null);
			}
			
			dispatchEvent(new TweenEvent(TweenEvent.TWEEN_END, false, false, values));
					
			finishRepeat();
		}
	}

	/**
	 *  @private
	 */
	private function removeMask():void
	{
		// Although it wasn't the original intended design, it turns out that
		// two mask effects can play simultaneously inside a <parallel> effect.
		// The only gotcha is that we shouldn't clear the mask until both
		// effects are done.  The solution: a reference count.
		if (--effectMaskRefCount == 0)
		{
			if (origMask == null || (origMask && origMask.name != MASK_NAME))
				target.mask = origMask;
			
			if (origScrollRect)
			{
				target.scrollRect = origScrollRect;
			}
					
			if (target is Container)
				target.rawChildren.removeChild(effectMask); 
			else
				target.removeChild(effectMask); 	
				
			effectMask = null;	
		}
	}
	
	/**
	 *  @private
	 */	
	private function saveTweenValue(moveValue:Object, scaleValue:Object):void
	{
		if (moveValue != null)
		{
			currentMoveTweenValue = moveValue;
		}
		else if (scaleValue != null)
		{
			currentScaleTweenValue = scaleValue;
		}
		
		if ((moveTween == null || currentMoveTweenValue != null)
			&& (scaleTween == null || currentScaleTweenValue != null))
		{
			var values:Array = [];
			if (currentMoveTweenValue)
			{
				values.push(currentMoveTweenValue[0]);
				values.push(currentMoveTweenValue[1]);
			}
			else
			{
				values.push(null);
				values.push(null);
			}
			
			if (currentScaleTweenValue)
			{
				values.push(currentScaleTweenValue[0]);
				values.push(currentScaleTweenValue[1]);
			}
			else
			{
				values.push(null);
				values.push(null);
			}
			
			if (!dispatchedStartEvent)
			{
				dispatchEvent(new TweenEvent(TweenEvent.TWEEN_START));
				dispatchedStartEvent = true;
			}
			
			dispatchEvent(new TweenEvent(TweenEvent.TWEEN_UPDATE, false, false, values));

			currentMoveTweenValue = null;
			currentScaleTweenValue = null;
		}
	}
		
	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override mx_internal function eventHandler(event:Event):void
	{
		super.eventHandler(event);

		// This function is called if the target object is resized.
		if (event.type == ResizeEvent.RESIZE)
		{		
			var tween:Tween = moveTween;
			if (!tween && scaleTween)
				tween = scaleTween;
			
			if (tween)
			{
				// Remember the amount of the effect that has already been
				// played.
				var elapsed:Number = getTimer() - tween.mx_internal::startTime;
	
				// Destroy the old tween object. Set its listener to a dummy 
				// object, so that the onTweenEnd function is not called.
				if (moveTween)
					Tween.mx_internal::removeTween(moveTween);
				
				if (scaleTween)
					Tween.mx_internal::removeTween(scaleTween);
				
				// Reset the tween count
				tweenCount = 0;
				removeMask();
				
				// The onTweenEnd function wasn't called, so decrement the 
				// effectMaskRefCount here to keep it in balance.
				//effectMaskRefCount--;		
				// Restart the effect and create a new mask.  This is necessary
				// so that the mask's size matches the target object's new size.
				initMask();
				play();
		
				// Set the tween's clock, so that it thinks 'elapsed'
				// milliseconds of the animation have already played.
				if (moveTween)
				{
					moveTween.mx_internal::startTime -= elapsed;
					// Update the screen before a repaint occurs
					moveTween.mx_internal::doInterval();
				}
				
				if (scaleTween)
				{
					scaleTween.mx_internal::startTime -= elapsed;
					// Update the screen before a repaint occurs
					scaleTween.mx_internal::doInterval();
				} 
			}
		}
	}
}

}
