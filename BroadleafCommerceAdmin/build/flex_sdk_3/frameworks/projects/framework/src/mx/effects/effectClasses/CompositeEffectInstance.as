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
import mx.effects.EffectInstance;
import mx.effects.IEffectInstance;
import mx.effects.Tween;
import mx.events.EffectEvent;

use namespace mx_internal;
	
/**
 *  The CompositeEffectInstance class implements the instance class
 *  for the CompositeEffect class.
 *  Flex creates an instance of this class  when it plays a CompositeEffect;
 *  you do not create one yourself.
 *
 *  @see mx.effects.CompositeEffect
 */
public class CompositeEffectInstance extends EffectInstance
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
	 *  @param target This argument is ignored for Composite effects.
	 *  It is included only for consistency with other types of effects.
	 */
	public function CompositeEffectInstance(target:Object)
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
	 *  Internal queue of EffectInstances that are currently playing or waiting to be played.
	 *  Used internally by subclasses. 
	 */
	mx_internal var activeEffectQueue:Array /* of EffectInstance */ = [];

	//--------------------------------------------------------------------------
	//
	//  Overridden properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  actualDuration
	//----------------------------------

	/**
	 *  @private
	 *  Used internally to retrieve the actual duration,
	 *  which includes startDelay, repeatCount, and repeatDelay.
	 */
	override mx_internal function get actualDuration():Number
	{
		var value:Number = NaN;

		if (repeatCount > 0)
		{
			value = durationWithoutRepeat * repeatCount +
					(repeatDelay * repeatCount - 1) + startDelay;
		}
		
		return value;
	}	
	
	//----------------------------------
	//  playheadTime
	//----------------------------------

	/**
	 *  @private
	 */	
	private var _playheadTime:Number = 0;
	
	/**
	 *  @private
	 */	
	override public function get playheadTime():Number
	{
		return _playheadTime + super.playheadTime;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  childSets
	//----------------------------------

	/**
	 *  @private
	 */
	mx_internal var childSets:Array = [];
	
	//----------------------------------
	//  durationWithoutRepeat
	//----------------------------------

	/**
	 *  @private
	 *  Used internally to calculate the duration from the children effects
	 *  for one repetition of this effect.
	 */
	mx_internal function get durationWithoutRepeat():Number
	{
		return 0;
	}
	
	//----------------------------------
	//  endEffectCalled
	//----------------------------------

	/**
	 *  @private
	 */
	mx_internal var endEffectCalled:Boolean;
	
	//----------------------------------
	//  timerTween
	//----------------------------------

	/**
	 *  @private
	 *  Used internally to obtain the playheadTime for the composite effect.
	 */
	mx_internal var timerTween:Tween;
	
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
		timerTween = new Tween(this,0,0,durationWithoutRepeat);
		
		super.play();
	}
		
	/**
	 *  @private
	 */
	override public function pause():void
	{	
		super.pause();
		
		if (timerTween)
			timerTween.pause();
	}
	
	/**
	 *  @private
	 */
	override public function stop():void
	{	
		super.stop();
		
		if (timerTween)
			timerTween.stop();
	}

	/**
	 *  @private
	 */
	override public function resume():void
	{
		super.resume();
		
		if (timerTween)
			timerTween.resume();
	}
		
	/**
	 *  @private
	 */
	override public function reverse():void
	{
		super.reverse();
		
		super.playReversed = !playReversed;
		
		if (timerTween)
			timerTween.reverse();
	}
	
	/**
	 *  @private
	 */
	override public function finishEffect():void
	{
		activeEffectQueue = null;
		
		super.finishEffect();
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Adds a new set of child effects to this Composite effect.
	 *  A Sequence effect plays each child effect set one at a time,
	 *  in the order that it is added.
	 *  A Parallel effect plays all child effect sets simultaneously;
	 *  the order in which they are added doesn't matter.
	 *
	 *  @param childSet Array of child effects to be added
	 *  to the CompositeEffect.
	 */
	public function addChildSet(childSet:Array):void
	{
		if (childSet)
		{
			var n:int = childSet.length;
			if (n > 0)
			{
				if (!childSets)
					childSets = [ childSet ];
				else
					childSets.push(childSet);
		
				
				for (var i:int = 0; i < n; i++)
				{
					childSet[i].addEventListener(EffectEvent.EFFECT_END,
												 effectEndHandler);
					childSet[i].mx_internal::parentCompositeEffectInstance =
						this;
				}
			}
		}
	}
	
	/**
	 *  @private
	 *  Check if we have a RotateInstance in one of our childSets array elements
	 */
	mx_internal function hasRotateInstance():Boolean
	{
		if (childSets)
		{
			for (var i:int = 0; i < childSets.length; i++)
			{
				if (childSets[i].length > 0)
				{
					var compChild:CompositeEffectInstance = childSets[i][0] as CompositeEffectInstance;
					
					if (childSets[i][0] is RotateInstance || (compChild && compChild.hasRotateInstance()))
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/**
	 *  @private
	 */		
	override mx_internal function playWithNoDuration():void
	{
		super.playWithNoDuration();
		end();
	}
	
	/**
	 *  Called each time one of the child effects has finished playing. 
	 *  Subclasses must implement this function.
	 *
	 *  @param The child effect.
	 */
	protected function onEffectEnd(childEffect:IEffectInstance):void
	{
		// Needs to be overridden
	}
	
	/**
	 *  @private
	 */		
	public function onTweenUpdate(value:Object):void
	{
		_playheadTime = timerTween ?
						timerTween.mx_internal::playheadTime :
						_playheadTime;
	}
	
	/**
	 *  @private
	 */		
	public function onTweenEnd(value:Object):void
	{
		_playheadTime = timerTween ?
						timerTween.mx_internal::playheadTime :
						_playheadTime;
	}
		
	//--------------------------------------------------------------------------
	//
	//  Overridden event handlers
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	override public function initEffect(event:Event):void
	{
		super.initEffect(event);

		var n:int = childSets.length;
		for (var i:int = 0; i < n; i++)
		{
			var instances:Array = childSets[i];
			var m:int = instances.length;
			for (var j:int = 0; j < m; j++)
			{
				instances[j].initEffect(event);
			}
		}
	}

	/**
	 *  @private
	 */
	mx_internal function effectEndHandler(event:EffectEvent):void
	{
		onEffectEnd(event.effectInstance);
	}
}

}
