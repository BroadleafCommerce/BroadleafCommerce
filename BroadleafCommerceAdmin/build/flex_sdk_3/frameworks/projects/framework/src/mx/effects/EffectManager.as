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

package mx.effects
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.FocusEvent;
import flash.utils.Dictionary;
import mx.core.ApplicationGlobals;
import mx.core.EventPriority;
import mx.core.IDeferredInstantiationUIComponent;
import mx.core.IFlexDisplayObject;
import mx.core.IUIComponent;
import mx.core.UIComponent;
import mx.core.UIComponentCachePolicy;
import mx.core.mx_internal;
import mx.events.EffectEvent;
import mx.events.FlexEvent;
import mx.events.MoveEvent;
import mx.events.ResizeEvent;
import mx.managers.ISystemManager;
import mx.managers.SystemManager;
import mx.resources.IResourceManager;
import mx.resources.ResourceManager;

use namespace mx_internal;

[ResourceBundle("effects")]

/**
 *  The EffectManager class listens for events, such as the <code>show</code>
 *  and <code>move</code> events, dispatched by objects in a Flex application.
 *  For each event, corresponding to an event trigger, it determines if 
 *  there is an effect assigned to the object. 
 *  If an effect is defined, it plays the effect. 
 */
public class EffectManager extends EventDispatcher
{
    include "../core/Version.as";
	
 	//--------------------------------------------------------------------------
	//
	//  Class variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Keeps track of all the triggered effects that are currently playing.
	 */
	mx_internal static var effectsPlaying:Array /* of EffectNode */ = [];
    
	/**
     *  @private
	 *  Map with event type as key and effectTrigger as value.
     */
	private static var effectTriggersForEvent:Object = {};
	
	/**
     *  @private
	 *  Map with effectTrigger as key and event type as value.
     */
	private static var eventsForEffectTriggers:Object = {};
	
	/**
	 *  @private
	 *  Array containing miscellaneous info about effect targets.
	 *  An element in the array is an Object with three fields:
	 *  target - reference to the target
	 *  bitmapEffectsCount - number of bitmap effects
	 *                       currently playing on the target
	 *  vectorEffectsCount - number of vector effects
	 *                       currently playing on the target
	 */
	private static var targetsInfo:Array /* of Object */ = [];
	
	/**
	 *  @private
	 *  Remember when suspendEventHandling() has been called
	 *  without a matching resumeEventHandling().
	 */
	private static var eventHandlingSuspendCount:Number = 0;

	/**
	 *  @private
	 */
	mx_internal static var lastEffectCreated:Effect;

	/**
	 *  @private
	 *  Storage for the resourceManager getter.
	 *  This gets initialized on first access,
	 *  not at static initialization time, in order to ensure
	 *  that the Singleton registry has already been initialized.
	 */
	private static var _resourceManager:IResourceManager;
	
	/**
	 *  @private
     *  A reference to the object which manages
     *  all of the application's localized resources.
     *  This is a singleton instance which implements
     *  the IResourceManager interface.
	 */
	private static function get resourceManager():IResourceManager
	{
		if (!_resourceManager)
			_resourceManager = ResourceManager.getInstance();

		return _resourceManager;
	}
	
 	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  After this method is called, the EffectManager class ignores
	 *  all events, and no effects are triggered, until a call to
	 *  <code>resumeEventHandling()</code>.  
	 *  Used internally so that an effect that is updating the screen
	 *  does not cause another effect to be triggered.
	 */
	public static function suspendEventHandling():void
	{
		eventHandlingSuspendCount++;
	}
	
	/**
	 *  Allows the EffectManager class to resume processing events
	 *  after a call to the <code>suspendEventHandling()</code> method.
	 *  Used internally in conjunction with the
	 *  <code>suspendEventHandling()</code> method 
	 *  so that an effect that is updating the screen
	 *  does not cause another effect to be triggered.
	 */
	public static function resumeEventHandling():void
	{
		eventHandlingSuspendCount--;	
	}
	
	/**
	 *  Immediately ends any effects currently playing on a target.
	 *
	 *  @param target The target component on which to end all effects.
	 */
	public static function endEffectsForTarget(target:IUIComponent):void
	{
		// Iterate through the array backward, because calling end()
		// may cause the element to be removed from the array.
		var n:int = mx_internal::effectsPlaying.length;
		for (var i:int = n - 1; i >= 0; i--)
		{
			var otherInst:EffectInstance =
				mx_internal::effectsPlaying[i].instance;
			if (otherInst.target == target)
				otherInst.end();
		}		
	}

	/**
	 *  @private
	 */
	mx_internal static function setStyle(styleProp:String, target:*):void
	{
		// Anytime that any UIComponent's style is changed,
		// check to see if the styleProp that's changing
		// is an effect trigger (e.g., showEffect).
		
		var eventName:String = eventsForEffectTriggers[styleProp];
		if (eventName != null && eventName != "")
		{
			target.addEventListener(eventName,
									EffectManager.mx_internal::eventHandler,
									false, EventPriority.EFFECT);
		}
	}

	/**
	 *  @private
	 *  Internal function used to instantiate an effect
	 */
	mx_internal static function createEffectForType(target:Object,
											   type:String):Effect
	{
		var trigger:String = effectTriggersForEvent[type];
		
		if (trigger == "")
			trigger = type + "Effect"; // For backwards compatibility
		
		var value:Object = target.getStyle(trigger);
				
		if (!value)
			return null;

		if (value is Class)
		{
			var cls:Class = Class(value);
			return new cls(target);
		}
		
		// If we don't find the ID on the parent document, then just move on.
		try
		{
			var effectObj:Effect;
			if (value is String)
			{
				var doc:Object = target.parentDocument;
				// The main Application doesn't have a parentDocument.
				if (!doc)
					doc = ApplicationGlobals.application;
				effectObj = doc[value];
			}
			else if (value is Effect)
			{
				effectObj = Effect(value);
			}
						
			if (effectObj)
			{
				effectObj.target = target;
				return effectObj;
			}	
		}
		catch(e:Error)
		{
		}
		
		var effectClass:Class =
			Class(target.systemManager.getDefinitionByName(
											"mx.effects." + value));
		
		if (effectClass)
			return new effectClass(target);
		
		return null;
	}

	/**
	 *  @private
	 *  Internal function used while playing effects
	 */
	private static function animateSameProperty(a:Effect, b:Effect,
												c:EffectInstance):Boolean
	{
		// This function returns true if "a" and "b" animate
		// the same property of the same object. 

		if (a.target == c.target)
		{
			var aProps:Array = a.getAffectedProperties();
			var bProps:Array = b.getAffectedProperties();

			var n:int = aProps.length;
			var m:int = bProps.length;

			for (var i:int = 0; i < n; i++)
			{
				for (var j:int = 0; j < m; j++)
				{
					if (aProps[i] == bProps[j])
						return true;
				}
			}
		}

		return false;
	}

	/**
	 *  @private
	 *  Should be called by an effect instance before it starts playing,
	 *  to suggest bitmap caching on the target.
	 *  E.g. Fade calls this function in its play().
	 */
	mx_internal static function startBitmapEffect(target:IUIComponent):void
	{
		cacheOrUncacheTargetAsBitmap(target, true, true);
	}

	/**
	 *  @private
	 *  Should be called by an effect instance after it has finished playing,
	 *  to suggest that the cached bitmap for the target can be freed.
	 *  E.g. Fade calls this function in its onTweenEnd().
	 */
	mx_internal static function endBitmapEffect(target:IUIComponent):void
	{
		cacheOrUncacheTargetAsBitmap(target, false, true);
	}

	/**
	 *  @private
	 *  Should be called by an effect instance before it starts playing, to
	 *  suggest that bitmap caching should be turned off on the target.
	 *  E.g. Resize calls this function in its play().
	 */
	mx_internal static function startVectorEffect(target:IUIComponent):void
	{
		cacheOrUncacheTargetAsBitmap(target, true, false);
	}

	/**
	 *  @private
	 *  Should be called by an effect instance after it has finished playing,
	 *  to suggest that bitmap caching may be turned back on on the target.
	 *  E.g. Resize calls this function in its onTweenEnd().
     */
	mx_internal static function endVectorEffect(target:IUIComponent):void
	{
		cacheOrUncacheTargetAsBitmap(target, false, false);
	}

	/**
	 *  @private
	 *  Cache or uncache the target as a bitmap depending on which effects are
	 *  currently playing on the target.
	 *
	 *  @param target The effect target.
	 *
	 *  @param effectStart Whether this is the starting of the effect.
	 *  false means it's the ending of the effect.
	 *
	 *  @param bitmapEffect Whether this is a bitmap effect.
	 *  false means it's a vector effect (like resize, zoom, etc.)
	 *  that wants the target object to be uncached.
	 */
	private static function cacheOrUncacheTargetAsBitmap(
								target:IUIComponent,
								effectStart:Boolean = true,
								bitmapEffect:Boolean = true):void
	{
		var n:int;
		var i:int;

		// Object containing information about the target.
		var info:Object = null;

		n = targetsInfo.length;
		for (i = 0; i < n; i++)
		{
			if (targetsInfo[i].target == target)
			{
				info = targetsInfo[i];
				break;
			}
		}

		// If no info object is available, create an object and push it
		// into the array.
		if (!info)
		{
			info =
			{
				target: target,
				bitmapEffectsCount: 0,
				vectorEffectsCount: 0
			};

			targetsInfo.push(info);
		}

		if (effectStart)
		{
			if (bitmapEffect)
			{
				info.bitmapEffectsCount++;

				// If no vector effects are currently playing,
				// cache the target.
				if (info.vectorEffectsCount == 0 &&
					target is IDeferredInstantiationUIComponent)
				{
					IDeferredInstantiationUIComponent(target).cacheHeuristic = true;
				}
			}
			else
			{
				// If a vector effect started playing, forcibly uncache
				// the target regardless of anything else.
				if (info.vectorEffectsCount++ == 0 &&
					target is IDeferredInstantiationUIComponent &&
					IDeferredInstantiationUIComponent(target).cachePolicy == UIComponentCachePolicy.AUTO)
				{
					target.cacheAsBitmap = false;
				}
			}
		}
		else // effect end
		{
			if (bitmapEffect)
			{
				if (info.bitmapEffectsCount != 0)
					info.bitmapEffectsCount--;

				if (target is IDeferredInstantiationUIComponent)
					IDeferredInstantiationUIComponent(target).cacheHeuristic = false;
			}
			else
			{
				if (info.vectorEffectsCount != 0)
				{
					// If no more vector effects are playing but bitmap
					// effects are still playing, cache the target.
					if (--info.vectorEffectsCount == 0 &&
						info.bitmapEffectsCount != 0)
					{
						// Crank up the counter.
						n = info.bitmapEffectsCount;
						for (i = 0; i < n; i++)
						{
							if (target is IDeferredInstantiationUIComponent)
							IDeferredInstantiationUIComponent(target).cacheHeuristic = true;
						}
					}
				}
			}

			if (info.bitmapEffectsCount == 0 && info.vectorEffectsCount == 0)
			{
				// No more effects are playing on this target, so discard the
				// info object (should speed up lookups).
				n = targetsInfo.length;
				for (i = 0; i < n; i++)
				{
					if (targetsInfo[i].target == target)
					{
						targetsInfo.splice(i, 1);
						break;
					}
				}
			}
		}
	}
	
	/**
	 *  @private
	 *  Called in code generated by MXML compiler.
	 */
	mx_internal static function registerEffectTrigger(name:String,
												 event:String):void
	{
		if (name != "")
		{
			if (event == "")
			{
				// For backwards compatibility.
				var strLen:Number = name.length;
				if (strLen > 6 && name.substring(strLen - 6) == "Effect")
					event = name.substring(0, strLen - 6);
			}
						
			if (event != "")
			{
				effectTriggersForEvent[event] = name;
				eventsForEffectTriggers[name] = event;
			}
		}
	}
	
	/**
	 *  @private
	 */
	mx_internal static function getEventForEffectTrigger(effectTrigger:String):String
	{
		if (eventsForEffectTriggers)
		{
			try 
			{
				return eventsForEffectTriggers[effectTrigger];
			}
			catch(e:Error)
			{
				return "";
			}
		}
		
		return "";
	}

	//--------------------------------------------------------------------------
	//
	//  Class event handlers
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	mx_internal static function eventHandler(eventObj:Event):void
	{	
		// If this event fired because an effect is currently playing
		// (in other words, if an effect was the source of this event),
		// then don't listen to the effect.
		if (!(eventObj.currentTarget is IFlexDisplayObject))
			return;
		
		if (eventHandlingSuspendCount > 0)
			return;
		
		if (eventObj is FocusEvent &&
			(eventObj.type == FocusEvent.FOCUS_OUT ||
			 eventObj.type == FocusEvent.FOCUS_IN))
		{
			var focusEventObj:FocusEvent = FocusEvent(eventObj);
			if (focusEventObj.relatedObject && 
				(focusEventObj.currentTarget.contains(focusEventObj.relatedObject) ||
				 focusEventObj.currentTarget == focusEventObj.relatedObject))
			{
				return;
			}
		}
		
		// Only trigger the event for added and removed if the current target is the same as the target. 
		if ((eventObj.type == Event.ADDED || eventObj.type == Event.REMOVED) && eventObj.target != eventObj.currentTarget)
			return;
			
		if (eventObj.type == Event.REMOVED)
		{
			if (eventObj.target is UIComponent)
			{
				if (UIComponent(eventObj.target).initialized == false)
				{
					return;
				}
				else if (UIComponent(eventObj.target).mx_internal::isEffectStarted)
				{
					for (var i:int = 0; i < UIComponent(eventObj.target).mx_internal::_effectsStarted.length; i++)
					{
						// Don't allow removedEffect to trigger more than one effect at a time
						if (UIComponent(eventObj.target).mx_internal::_effectsStarted[i].triggerEvent.type == Event.REMOVED)
							return;
					}
				}
			}
			
			var targ:DisplayObject = eventObj.target as DisplayObject;
			
			if (targ != null)
			{			
				var parent:DisplayObjectContainer = targ.parent as DisplayObjectContainer;
				
				if (parent != null)
				{
					var index:int = parent.getChildIndex(targ);
					if (index >= 0)
					{
						if (targ is UIComponent)	
						{
							// Since we get the "removed" event before the child is actually removed, 
							// we need to delay adding back the child. We must exit the current 
							// script block must exit before the child can be removed.
							UIComponent(targ).callLater(removedEffectHandler, [targ, parent, index, eventObj]);
						}
					}
				}
			}
		}
		else
		{
			createAndPlayEffect(eventObj, eventObj.currentTarget);	
		}
	}
	
	/**
	 *  @private
	 */	
	private static function createAndPlayEffect(eventObj:Event, target:Object):void
	{
				
		var effectInst:Effect = mx_internal::createEffectForType(target, eventObj.type);
		if (!effectInst)
			return;
		
		if (effectInst is Zoom && eventObj.type == MoveEvent.MOVE)
		{
			var message:String = resourceManager.getString(
				"effects", "incorrectTrigger");
			throw new Error(message);
		}
			
		// If this is a "move" or "resize" event that was caused by 
		// the layout manager doing an initial layout, then don't
		// play any effects.
		// Ditto for "show" or "hide" effects triggered by ViewStack.doLayout.
		if (target.initialized == false)
		{
			var type:String = eventObj.type;
			if (type == MoveEvent.MOVE ||
				type == ResizeEvent.RESIZE ||
				type == FlexEvent.SHOW ||
				type == FlexEvent.HIDE ||
				type == Event.CHANGE)
			{
				effectInst = null;
				return;
			}
		}

		var n:int;
		var i:int;
		var m:int;
		var j:int;

		// Some components contain built-in tweens, which are not managed by
		// the EffectManager.  If one of those tweens is currently playing,
		// and if it's animating a conflicting property, then don't play this
		// tween.
		if (effectInst.target is IUIComponent)
		{
			var tweeningProperties:Array =
				IUIComponent(effectInst.target).tweeningProperties;
			if (tweeningProperties && tweeningProperties.length > 0)
			{
				var effectProperties:Array = effectInst.getAffectedProperties();

				n = tweeningProperties.length;
				m = effectProperties.length;
				
				for (i = 0; i < n; i++)
				{
					for (j = 0; j < m; j++)
					{
						if (tweeningProperties[i] == effectProperties[j])
						{
							effectInst = null;
							return;
						}
					}
				}
			}
		}
		
		// At any given time, only one effect may be animating a given
		// property of a given target object.  If some other effect was
		// previously animating the same properties of my target object,
		// then finish the other effect before starting this new one.
		//
		if (effectInst.target is UIComponent &&
			UIComponent(effectInst.target).mx_internal::isEffectStarted)
		{
			var affectedProps:Array = effectInst.getAffectedProperties();
			for (i = 0; i < affectedProps.length; i++)
			{
				var runningInstances:Array =
					effectInst.target.mx_internal::getEffectsForProperty(affectedProps[i]);
				if (runningInstances.length > 0) 
				{
					if (eventObj.type == ResizeEvent.RESIZE)
						return;

					for (j = 0; j < runningInstances.length; j++)
					{
						var otherInst:EffectInstance = runningInstances[j];
						if (eventObj.type == FlexEvent.SHOW && otherInst.mx_internal::hideOnEffectEnd)
						{
							otherInst.target.removeEventListener(
								FlexEvent.SHOW, otherInst.eventHandler);
							otherInst.mx_internal::hideOnEffectEnd = false;
							
						}

						/*
						if (eventObj.type == MoveEvent.MOVE && 
							((affectedProps[i] == "width") ||
							 (affectedProps[i] == "height") ||
							 (affectedProps[i] == "x") ||
							 (affectedProps[i] == "y")) &&
							 effectInst.target.getStyle("moveEffect") != undefined)
						{
							trace("EM Got Move and ignoring");
							return;
						}
						
						if (eventObj.type == ResizeEvent.RESIZE &&
							((affectedProps[i] == "width") ||
							 (affectedProps[i] == "height")) &&
							 effectInst.target.getStyle("resizeEffect") != undefined)
						{
							return;
						}
						*/

						otherInst.end();
					}
				}
			}
		}
				
		// Pass in event data for effect initialization
		effectInst.triggerEvent = eventObj;
	
		// Tell the effectInst that I'm the listener, so that my "onEffectEnd"
		// method is called when the effect finishes playing.  The
		// onEffectEnd handler will remove this effect from the effectsPlaying
		// array.
		effectInst.addEventListener(EffectEvent.EFFECT_END,
									EffectManager.mx_internal::effectEndHandler);
	
		lastEffectCreated = effectInst;

		var instances:Array = effectInst.play();
		n = instances.length;
		for (i = 0; i < n; i++)
		{
			mx_internal::effectsPlaying.push(
				new EffectNode(effectInst, instances[i]));
		}
		
		// Block all layout, responses from web services, and other background
		// processing until the effect finishes executing.
		if (effectInst.suspendBackgroundProcessing)
			UIComponent.suspendBackgroundProcessing();
	}

	/**
	 *  @private
	 *  Delayed function call when effect is triggered by "removed" event
	 */
	private static function removedEffectHandler(target:DisplayObject, parent:DisplayObjectContainer, index:int, eventObj:Event):void
	{
		suspendEventHandling();
		// Add the child back to the parent so the effect can play upon it
		parent.addChildAt(target, index);
		resumeEventHandling();
		// Use target because the player assigns the Stage to the currentTarget when we leave the scope of the event handler function
		createAndPlayEffect(eventObj, target); 
	}
	
	/**
	 *  @private
	 *  Internal function used while playing effects
	 */
	mx_internal static function effectEndHandler(event:EffectEvent):void
	{
		var effectInst:IEffectInstance = event.effectInstance;
		// This function is called when an effect, which was started
		// earlier by this effect manager, finishes playing.  Remove
		// this effect from the "effectPlaying" list
		var n:int = mx_internal::effectsPlaying.length;
		for (var i:int = n - 1; i >= 0; i--)
		{
			if (mx_internal::effectsPlaying[i].instance == effectInst)
			{
				mx_internal::effectsPlaying.splice(i, 1);
				break;
			}
		}

		// If the event that caused this effect was "hide", then the
		// eventHandler() method set the object's visiblity to true.
		// Now that the effect is finished playing, set visiblity to false.
		if (Object(effectInst).hideOnEffectEnd == true)
		{
			effectInst.target.removeEventListener(
				FlexEvent.SHOW, Object(effectInst).eventHandler);
			effectInst.target.setVisible(false, true);			
		}
		
		if (effectInst.triggerEvent && effectInst.triggerEvent.type == Event.REMOVED)
		{
			var targ:DisplayObject = effectInst.target as DisplayObject;
			
			if (targ != null)
			{			
				var parent:DisplayObjectContainer = targ.parent as DisplayObjectContainer;
				
				if (parent != null)
				{
					// Since we added the child back to the parent when the effect began,
					// we need to remove it once the effect has finished.
					suspendEventHandling();
					parent.removeChild(targ);
					resumeEventHandling();
				}
			}
		}

		// Resume the background processing that was suspended earlier
		if (effectInst.suspendBackgroundProcessing)
			UIComponent.resumeBackgroundProcessing();		
	}

	//--------------------------------------------------------------------------
	//
	//  Diagnostics
	//
	//--------------------------------------------------------------------------
	private static var effects:Dictionary = new Dictionary(true);

	mx_internal static function effectStarted(effect:EffectInstance):void
	{
		effects[effect] = 1;
	}

	mx_internal static function effectFinished(effect:EffectInstance):void
	{
		delete effects[effect];
	}

	mx_internal static function effectsInEffect():Boolean
	{
		for (var i:* in effects)
		{
			return true;
		}
		return false;
	}
}

}

////////////////////////////////////////////////////////////////////////////////
//
//  Helper class: EffectNode
//
////////////////////////////////////////////////////////////////////////////////

import mx.effects.Effect;
import mx.effects.EffectInstance;

/**
 *  @private
 */
class EffectNode
{
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 */
	public function EffectNode(factory:Effect, instance:EffectInstance)
	{
		super();

		this.factory = factory;
		this.instance = instance;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public var factory:Effect;

	/**
	 *  @private
	 */
	public var instance:EffectInstance;
}
