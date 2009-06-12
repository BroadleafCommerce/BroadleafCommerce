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
import flash.filters.GlowFilter;
import mx.core.Application;
import mx.core.mx_internal;
import mx.styles.StyleManager;

/**
 *  The GlowInstance class implements the instance class
 *  for the Glow effect.
 *  Flex creates an instance of this class when it plays a Glow effect;
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
 *  For the Glow effect, 
 *  the <code>TweenEvent.value</code> property contains a 4-item Array, where: </p>
 *  <ul>
 *    <li>value[0]:uint  The value of the target's <code>GlowFilter.color</code> property.</li> 
 *  
 *    <li>value[1]:Number  A value between the values of the <code>Glow.alphaFrom</code> 
 *    and <code>Glow.alphaTo</code> property.</li>
 *  
 *    <li>value[2]:Number  A value between the values of the <code>Glow.blurXFrom</code> 
 *    and <code>Glow.blurXTo</code> property.</li>
 *  
 *    <li>value[3]:Number  A value between the values of the <code>Glow.blurYFrom</code> 
 *    and <code>Glow.blurYTo</code> property.</li>
 *  </ul>
 *
 *  @see mx.effects.Glow
 *  @see mx.events.TweenEvent
 */  
public class GlowInstance extends TweenEffectInstance
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
	public function GlowInstance(target:Object)
	{
		super(target);
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  alphaFrom
	//----------------------------------

	/** 
	 *  Starting transparency level.
	 */
	public var alphaFrom:Number;
	
	//----------------------------------
	//  alphaTo
	//----------------------------------

	/** 
	 *  Ending transparency level.
	 */
	public var alphaTo:Number;

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
	
	//----------------------------------
	//  color
	//----------------------------------

	/** 
	 *  The color of the glow. 
	 */
	public var color:uint = StyleManager.NOT_A_COLOR;
	
	//----------------------------------
	//  inner
	//----------------------------------

	/** 
	 *  The inner flag of the glow.
	 */
	public var inner:Boolean;
	
	//----------------------------------
	//  knockout
	//----------------------------------

	/** 
	 *  The knockout flag of the glow. 
	 */
	public var knockout:Boolean;
	
	//----------------------------------
	//  strength
	//----------------------------------

	/** 
	 *  The strength of the glow. 
	 */
	public var strength:Number;
	
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

		// If nobody assigned a value, give some defaults
		if (isNaN(alphaFrom))
			alphaFrom = 1.0;
		if (isNaN(alphaTo))
			alphaTo = 0;
		if (isNaN(blurXFrom))
			blurXFrom = 5;
		if (isNaN(blurXTo))
			blurXTo = 0;
		if (isNaN(blurYFrom))
			blurYFrom = 5;
		if (isNaN(blurYTo))
			blurYTo = 0;
		if (color == StyleManager.NOT_A_COLOR)
			color = Application.application.getStyle("themeColor");
		if (isNaN(strength))
			strength = 2;
			
		tween = createTween(
			this, [ color, alphaFrom, blurXFrom, blurYFrom ],
			[ color, alphaTo, blurXTo, blurYTo ], duration);
		
		// target.filters = ???
	}

	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		setGlowFilter(value[0], value[1], value[2], value[3]);
	}

	/**
	 *  @private
	 */
	override public function onTweenEnd(value:Object):void
	{
		setGlowFilter(value[0], value[1], value[2], value[3]);
			
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
	private function setGlowFilter(color:uint, alpha:Number,
								   blurX:Number, blurY:Number):void
	{
		var filters:Array = target.filters;
		
		// Remove any existing Glow filters
		var n:int = filters.length;
		for (var i:int = 0; i < n; i++)
		{
			if (filters[i] is GlowFilter)
				filters.splice(i, 1);
		}
		
		if (blurX || blurY || alpha)
			filters.push(new GlowFilter(color, alpha, blurX, blurY,
						strength, 1, inner, knockout));
		
		target.filters = filters;
	}
}

}
