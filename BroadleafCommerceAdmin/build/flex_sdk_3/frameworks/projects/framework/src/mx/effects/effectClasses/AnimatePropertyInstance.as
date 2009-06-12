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

import mx.core.mx_internal;
import mx.effects.Tween;

/**
 *  The AnimatePropertyInstance class implements the instance class
 *  for the AnimateProperty effect.
 *  Flex creates an instance of this class when it plays an AnimateProperty
 *  effect; you do not create one yourself.
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
 *  For the AnimateProperty effect, 
 *  the <code>TweenEvent.value</code> property contains a Number between the values of 
 *  the <code>AnimateProperty.fromValue</code> and 
 *  <code>AnimateProperty.toValue</code> properties, for the target 
 *  property specified by <code>AnimateProperty.property</code>.</p>
 *
 *  @see mx.effects.AnimateProperty
 *  @see mx.events.TweenEvent
 */  
public class AnimatePropertyInstance extends TweenEffectInstance
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
	public function AnimatePropertyInstance(target:Object)
	{
		super(target);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  toValue
	//----------------------------------

	/**
	 *  The ending value for the effect.
	 *  The default value is the target's current property value.
	 */
	public var toValue:Number;
	
	//----------------------------------
	//  isStyle
	//----------------------------------

	/**
	 *  If <code>true</code>, the property attribute is a style and you
	 *  set it by using the <code>setStyle()</code> method. 
	 *  
	 *  @default false
	 */
	 public var isStyle:Boolean = false;
		
	//----------------------------------
	//  property
	//----------------------------------

	/**
	 *  The name of the property on the target to animate.
	 *  This attribute is required.
	 */
	public var property:String;
	
	//----------------------------------
	//  roundValue
	//----------------------------------
	
	/**
	 *  If <code>true</code>, round off the interpolated tweened value
	 *  to the nearest integer. 
	 *  This property is useful if the property you are animating
	 *  is an int or uint.
	 *  
	 *  @default false
	 */
	public var roundValue:Boolean = false;	
		
	//----------------------------------
	//  fromValue
	//----------------------------------

	/**
	 *  The starting value of the property for the effect.
	 *  The default value is the target's current property value.
	 */
	public var fromValue:Number;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function play():void
	{
		// Do what effects normally do when they start, namely
		// dispatch an 'effectStart' event from the target.
		super.play();
		
		if (isNaN(fromValue))
		{
			fromValue = getCurrentValue();
		}
		
		if (isNaN(toValue))
		{
			if (propertyChanges && propertyChanges.end[property] !== undefined)
				toValue = propertyChanges.end[property];
			else
				toValue = getCurrentValue();
		}
		
		// Create a Tween object to interpolate the verticalScrollPosition.
		tween = createTween(this, fromValue, toValue, duration);

		// If the caller supplied their own easing equation, override the
		// one that's baked into Tween.
		if (easingFunction != null)
			tween.easingFunction = easingFunction;

		mx_internal::applyTweenStartValues();
	}
	
	
	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		if (!isStyle)
			target[property] = roundValue ? Math.round(Number(value)) : value;	
		else
			target.setStyle(property, value);
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function getCurrentValue():Number
	{
		var currentValue:Number;
		
		if (!isStyle)
			currentValue = target[property];
		else
			currentValue = target.getStyle(property);
		
		return roundValue ? Math.round(currentValue) : currentValue;
	}
}

}
