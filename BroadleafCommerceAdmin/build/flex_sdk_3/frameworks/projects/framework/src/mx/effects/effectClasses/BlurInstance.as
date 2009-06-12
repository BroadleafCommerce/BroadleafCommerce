////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects.effectClasses
{

import flash.events.Event;
import flash.filters.BlurFilter;
import mx.core.mx_internal;

/**
 *  The BlurInstance class implements the instance class
 *  for the Blur effect.
 *  Flex creates an instance of this class when it plays a Blur effect;
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
 *  The TweenEvent class  defines the property <code>value</code>, which contains 
 *  the tween value calculated by the effect. 
 *  For the Blur effect, 
 *  the <code>TweenEvent.value</code> property contains a 2-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:Number  A value between the values of the <code>Blur.blurXTo</code> 
 *    and <code>Blur.blurXFrom</code> property, applied to the 
 *    target's <code>BlurFilter.blurX</code> property.</li> 
 *  
 *    <li>value[1]:Number  A value between the values of the <code>Blur.blurYTo</code> 
 *    and <code>Blur.blurYFrom</code> property, applied to the 
 *    target's <code>BlurFilter.blurY</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.Blur
 *  @see mx.events.TweenEvent
 */  
public class BlurInstance extends TweenEffectInstance
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
	public function BlurInstance(target:Object)
	{
		super(target);
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  blurXFrom
	//----------------------------------

	/** 
	 *  The starting amount of horizontal blur.
	 */
	public var blurXFrom:Number;
	
	//----------------------------------
	//  blurXTo
	//----------------------------------

	/** 
	 *  The ending amount of horizontal blur.
	 */
	public var blurXTo:Number;

	//----------------------------------
	//  blurYFrom
	//----------------------------------

	/** 
	 *  The starting amount of vertical blur.
	 */
	public var blurYFrom:Number;
	
	//----------------------------------
	//  blurYTo
	//----------------------------------

	/** 
	 *  The ending amount of vertical blur.
	 */
	public var blurYTo:Number;
	
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
	}
	
	/**
	 *  @private
	 */
	override public function play():void
	{
		// Dispatch an effectStart event from the target.
		super.play();

		// If nobody assigned a value, give some defaults.
		if (isNaN(blurXFrom))
			blurXFrom = 4;
		if (isNaN(blurXTo))
			blurXTo = 0;
		if (isNaN(blurYFrom))
			blurYFrom = 4;
		if (isNaN(blurYTo))
			blurYTo = 0;

		tween = createTween(this, [ blurXFrom, blurYFrom ],
								  [ blurXTo, blurYTo ], duration);
		
		// target.filters = ???
	}

	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		setBlurFilter(value[0], value[1]);
	}

	/**
	 *  @private
	 */
	override public function onTweenEnd(value:Object):void
	{
		setBlurFilter(value[0], value[1]);
			
		super.onTweenEnd(value);	
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function setBlurFilter(blurX:Number, blurY:Number):void
	{
		var filters:Array = target.filters;
		
		// Remove any existing Blur filters
		var n:int = filters.length;
		for (var i:int = 0; i < n; i++)
		{
			if (filters[i] is BlurFilter)
				filters.splice(i, 1);
		}
		
		if (blurX || blurY)
			filters.push(new BlurFilter(blurX, blurY));
		
		target.filters = filters;
	}
}

}
