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
	
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.effects.EffectInstance;
import mx.effects.IEffectInstance;
import mx.effects.Tween;

use namespace mx_internal;

/**
 *  The SequenceInstance class implements the instance class 
 *  for the Sequence effect.
 *  Flex creates an instance of this class when it plays a Sequence effect;
 *  you do not create one yourself.
 *
 *  @see mx.effects.Sequence
 */  
public class SequenceInstance extends CompositeEffectInstance
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
	 *  @param target This argument is ignored for Sequence effects.
	 *  It is included only for consistency with other types of effects.
	 */
	public function SequenceInstance(target:Object)
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
	private var activeChildCount:Number;
	
	/**
	 *  @private
	 *  Used internally to store the sum of all previously playing effects.
	 */
	private var currentInstanceDuration:Number = 0;	
	
	/**
	 *  @private
	 *  Used internally to track the set of effect instances
	 *  that the Sequence is currently playing.
	 */
	private var currentSet:Array;
	
	/**
	 *  @private
	 *  Used internally to track the index number of the current set
	 *  of playing effect instances
	 */
	private var currentSetIndex:int = -1;
				
	/**
	 *  @private 
	 */
	private var startTime:Number = 0;
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

 	//----------------------------------
	//  durationWithoutRepeat
	//----------------------------------

	/**
	 *  @private
	 */
	override mx_internal function get durationWithoutRepeat():Number
	{
		var _duration:Number = 0;
		
		var n:int = childSets.length;
		for (var i:int = 0; i < n; i++)
		{
			var instances:Array = childSets[i];
			_duration += instances[0].actualDuration;
		}
		
		return _duration;
	}
	
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
		// Create a new queue.
		activeEffectQueue = [];

		// Start at the beginning or the end
		// depending if we are playing backwards.
		currentSetIndex = playReversed ? childSets.length : -1;
		
		var n:int;
		var i:int;
		var m:int;
		var j:int;
		
		// Each childSets contains an instance of an effect for each target.
		// Flatten these instances into the effectQueue.
		// Put a null object between each effect so that the sequence knows
		// when to stop and wait for the previous instances to finish. 
		n = childSets.length;
		for (i = 0; i < n; i++)
		{
			var instances:Array = childSets[i];
			activeEffectQueue.push(instances);
		}
				
		// Dispatch an effectStart event from the target.
		super.play();

		startTime = Tween.intervalTime;
		
		if (activeEffectQueue.length == 0)
		{
			 finishRepeat();
			 return;
		}

		playNextChildSet();
	}
	
	/**
	 *  @private
	 */
	override public function pause():void
	{	
		super.pause();
		
		if (currentSet && currentSet.length > 0)
		{
			var n:int = currentSet.length;
			for (var i:int = 0; i < n; i++)
			{
				currentSet[i].pause();
			}
		}
	}

	/**
	 *  @private
	 */
	override public function stop():void
	{
		if (activeEffectQueue && activeEffectQueue.length > 0)
		{
			var queueCopy:Array = activeEffectQueue.concat();
			activeEffectQueue = null;
			
			// Call stop on the currently playing set
			var currentInstances:Array = queueCopy[currentSetIndex];
			var currentCount:int = currentInstances.length;
			
			for (var i:int = 0; i < currentCount; i++)
				currentInstances[i].stop();

			// For instances that have yet to run, we will delete them
			// without dispatching events.
			// (Another alternative would have been add them into
			// currentInstances and currentSet, then just stop them
			// along with the others. In this case, they would have
			// dispatched effectEnd events).
			var n:int = queueCopy.length;
			for (var j:int = currentSetIndex + 1; j < n; j++)
			{
				var waitingInstances:Array = queueCopy[j];
				var m:int = waitingInstances.length;
				
				for (var k:int = 0; k < m; k++)
				{
					var instance:IEffectInstance = waitingInstances[k];
					instance.effect.deleteInstance(instance);
				}
			}
		}
		
		super.stop();
	}	

	/**
	 *  @private
	 */
	override public function resume():void
	{
		super.resume();
		
		if (currentSet && currentSet.length > 0)
		{
			var n:int = currentSet.length;
			for (var i:int = 0; i < n; i++)
			{
				currentSet[i].resume();
			}
		}
	}
				
	/**
	 *  @private
	 */
	override public function reverse():void
	{
		super.reverse();
		
		if (currentSet && currentSet.length > 0)
		{
			// PlayNextChildSet handles the logic of playing previously completed effects
			var n:int = currentSet.length;
			for (var i:int = 0; i < n; i++)
			{
				currentSet[i].reverse();
			}
		}
	}
	
	/**
	 *  Interrupts any effects that are currently playing, skips over
	 *  any effects that haven't started playing, and jumps immediately
	 *  to the end of the composite effect.
	 */
	override public function end():void
	{
		endEffectCalled = true;
		
		// activeEffectQueue are all effects to play
		// currentSetIndex is where we want to start
		// if play() hasn't been called on us yet (b/c of startDelay), 
		// activeEffectQueue will have nothing in it.  In this case,
		// we leave the component in it's current state, rather than 
		// call .playWithNoDuration() on all the effects that haven't 
		// been run (or even added to the activeEffectQueue yet)
		if (activeEffectQueue && activeEffectQueue.length > 0)
		{
			var queueCopy:Array = activeEffectQueue.concat();
			activeEffectQueue = null;
			
			// Call end on the currently playing set
			var currentInstances:Array = queueCopy[currentSetIndex];
			var currentCount:int = currentInstances.length;
			
			for (var i:int = 0; i < currentCount; i++)
			{
				currentInstances[i].end();
			}
			
			var n:int = queueCopy.length;
			for (var j:int = currentSetIndex + 1; j < n; j++)
			{
				var waitingInstances:Array = queueCopy[j];
				var m:int = waitingInstances.length;
				
				for (var k:int = 0; k < m; k++)
				{
					EffectInstance(waitingInstances[k]).playWithNoDuration();
				}
			}
		}
		
		super.end();
	}

	/**
	*  Each time a child effect of SequenceInstance finishes, 
	*  Flex calls the <code>onEffectEnd()</code> method.
	*  For SequenceInstance, it plays the next effect.
	*  This method implements the method of the superclass.
	*
	*  @param childEffect The child effect.
	*/
	override protected function onEffectEnd(childEffect:IEffectInstance):void
	{
		// Each child effect notifies us when it is finished.
		// Remove the notifying child from childSets,
		// so that the end() method doesn't call it.
		// When the last child notifies us that it's finished,
		// notify our listener that we're finished.
		// Resume the background processing that was suspended earlier
		if (Object(childEffect).suspendBackgroundProcessing)
			UIComponent.resumeBackgroundProcessing();
		
		// See endEffect, above.
		if (endEffectCalled)
			return;	
		
		for (var i:int = 0; i < currentSet.length; i++)
		{
			if (childEffect == currentSet[i])
			{
				currentSet.splice(i, 1);
				break;
			}
		}	
		
		if (currentSet.length == 0)
		{
			if (false == playNextChildSet())
				finishRepeat();
		}
	}
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private function playNextChildSet(offset:Number = 0):Boolean
	{
		if (!playReversed)
		{
			if (!activeEffectQueue ||
				currentSetIndex++ >= activeEffectQueue.length - 1)
			{
				return false;
			}
		}
		else
		{
			if (currentSetIndex-- <= 0)
				return false;
		}
	
		var childEffect:EffectInstance;
		var instances:Array = activeEffectQueue[currentSetIndex];
		
		currentSet = [];
		
		for (var i:int = 0; i < instances.length; i++)
		{
			childEffect = instances[i];
			
			/*
			if (childEffect is TweenEffectInstance)
			{
				var offset:Number =
					Tween.intervalTime - startTime - currentInstanceDuration;
				offset = isNaN(offset) ? 0 : offset;
				TweenEffectInstance(childEffect).seek(offset);
			}
			*/
			
			currentSet.push(childEffect);
			childEffect.playReversed = playReversed;
			// Block all layout, responses from web services, and other
			// background processing until the effect finishes executing.
			if (childEffect.suspendBackgroundProcessing)
				UIComponent.suspendBackgroundProcessing();	
			childEffect.startEffect();
		}
		
		currentInstanceDuration += childEffect.actualDuration;
		
		/*
		while (activeEffectQueue[activeChildCount])
		{
			childEffect = activeEffectQueue[activeChildCount];
			activeChildCount++;
			
			
			if (childEffect is TweenEffectInstance)
			{
				var offset:Number =
					Tween.intervalTime - startTime - currentInstanceDuration;
				offset = isNaN(offset) ? 0 : offset;
				TweenEffectInstance(childEffect).seek(offset);
			}
			childEffect.startEffect();
			// Block all layout, responses from web services, and other
			// background processing until the effect finishes executing.
			if (childEffect.suspendBackgroundProcessing)
				UIComponent.suspendBackgroundProcessing();					
			
			if (!activeEffectQueue)
				break;
		}
		
		currentInstanceDuration += childEffect.duration;
		*/

		return true;
	}
}

}
