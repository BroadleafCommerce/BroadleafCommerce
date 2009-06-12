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
import mx.core.mx_internal;
import mx.events.FlexEvent;

/**
 *  The FadeInstance class implements the instance class
 *  for the Fade effect.
 *  Flex creates an instance of this class when it plays a Fade effect;
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
 *  For the Fade effect, 
 *  the <code>TweenEvent.value</code> property contains a Number between the values of the 
 *  <code>Fade.alphaFrom</code> and <code>Fade.alphaTo</code> properties.</p>
 *
 *  @see mx.effects.Fade
 *  @see mx.events.TweenEvent
 */  
public class FadeInstance extends TweenEffectInstance
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
	public function FadeInstance(target:Object)
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
	 *  The original transparency level.
	 */
	private var origAlpha:Number = NaN;
	
	/** 
	 *  @private
	 */
	private var restoreAlpha:Boolean;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  alphaFrom
	//----------------------------------

	/** 
	 *  Initial transparency level between 0.0 and 1.0, 
	 *  where 0.0 means transparent and 1.0 means fully opaque. 
	 */
	public var alphaFrom:Number;
	
	//----------------------------------
	//  alphaFrom
	//----------------------------------

	/** 
	 *  Final transparency level between 0.0 and 1.0, 
	 *  where 0.0 means transparent and 1.0 means fully opaque.
	 */
	public var alphaTo:Number;
	
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
				if (isNaN(alphaFrom))
					alphaFrom = 0;
				if (isNaN(alphaTo))
					alphaTo = target.alpha;
				break;
			}
		
			case FlexEvent.HIDE:
			case Event.REMOVED:
			case "resizeStart":
			{
				restoreAlpha = true;
				if (isNaN(alphaFrom))
					alphaFrom = target.alpha;
				if (isNaN(alphaTo))
					alphaTo = 0;
				break;
			}
		}
	}
	
	/**
	 *  @private
	 */
	override public function play():void
	{
		// Dispatch an effectStart event from the target.
		super.play();

		// Try to cache the target as a bitmap.
		//EffectManager.mx_internal::startBitmapEffect(target);

		// Remember the original value of the target object's alpha
		origAlpha = target.alpha;

		var values:PropertyChanges = propertyChanges;
		
		// If nobody assigned a value, make this a "show" effect.
		if (isNaN(alphaFrom) && isNaN(alphaTo))
		{	
			if (values && values.end["alpha"] !== undefined)
			{
				alphaFrom = origAlpha;
				alphaTo = values.end["alpha"];
			}
			else if (values && values.end["visible"] !== undefined)
			{
				alphaFrom = values.start["visible"] ? origAlpha : 0;
				alphaTo = values.end["visible"] ? origAlpha : 0;
			}
			else
			{
				alphaFrom = 0;
				alphaTo = origAlpha;
			}
		}
		else if (isNaN(alphaFrom))
		{
			alphaFrom = (alphaTo == 0) ? origAlpha : 0;
		}
		else if (isNaN(alphaTo))
		{
			if (values && values.end["alpha"] !== undefined)
			{
				alphaTo = values.end["alpha"];
			}
			else
			{
				alphaTo = (alphaFrom == 0) ? origAlpha : 0;	
			}
		}		
		
		tween = createTween(this, alphaFrom, alphaTo, duration);
		target.alpha = tween.mx_internal::getCurrentValue(0)
	}

	/**
	 *  @private
	 */
	override public function onTweenUpdate(value:Object):void
	{
		target.alpha = value;
	}

	/**
	 *  @private
	 */
	override public function onTweenEnd(value:Object):void
	{
		// Call super function first so we don't clobber resetting the alpha.
		super.onTweenEnd(value);	
			
		if (mx_internal::hideOnEffectEnd || restoreAlpha)
		{
			target.alpha = origAlpha;
		}
	}
}

}
