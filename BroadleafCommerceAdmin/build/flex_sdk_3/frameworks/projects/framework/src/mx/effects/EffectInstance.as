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

package mx.effects
{

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.events.TimerEvent;
import flash.utils.Timer;
import flash.utils.getQualifiedClassName;
import flash.utils.getTimer;
import mx.core.UIComponent;
import mx.core.mx_internal;
import mx.effects.effectClasses.PropertyChanges;
import mx.events.EffectEvent;
import mx.events.FlexEvent;

use namespace mx_internal;

/**
 *  The EffectInstance class represents an instance of an effect
 *  playing on a target.
 *  Each target has a separate effect instance associated with it.
 *  An effect instance's lifetime is transitory.
 *  An instance is created when the effect is played on a target
 *  and is destroyed when the effect has finished playing. 
 *  If there are multiple effects playing on a target at the same time 
 *  (for example, a Parallel effect), there is a separate effect instance
 *  for each effect.
 * 
 *  <p>Effect developers must create an instance class
 *  for their custom effects.</p>
 *
 *  @see mx.effects.Effect
 */
public class EffectInstance extends EventDispatcher implements IEffectInstance
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param target UIComponent object to animate with this effect.
     */
    public function EffectInstance(target:Object)
    {
        super();

        this.target = target;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  Timer used to track startDelay and repeatDelay.
     */
    mx_internal var delayTimer:Timer;
    
    /**
     *  @private
     *  Starting time of delayTimer.
     */
    private var delayStartTime:Number = 0;
    
    /**
     *  @private
     *  Elapsed time of delayTimer when paused.
     *  Used by resume() to figure out amount of time remaining.
     */
    private var delayElapsedTime:Number = 0;
    
    /**
     *  @private
     *  Internal flag remembering whether the user
     *  explicitly specified a duration or not.
     */
    mx_internal var durationExplicitlySet:Boolean = false;

    /**
     *  @private
     *  If this is a "hide" effect, the EffectManager sets this flag
     *  as a reminder to hide the object when the effect finishes.
     */
    mx_internal var hideOnEffectEnd:Boolean = false;
    
    /**
     *  @private
     *  Pointer back to the CompositeEffect that created this instance.
     *  Value is null if we are not the child of a CompositeEffect
     */
    mx_internal var parentCompositeEffectInstance:EffectInstance;
    
    /** 
     *  @private
     *  Number of times that the instance has been played.
     */
    private var playCount:int = 0;
    
    /**
     *  @private
     *  Used internally to prevent the effect from repeating
     *  once the effect has been ended by calling end().
     */
    mx_internal var stopRepeat:Boolean = false;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  actualDuration
    //----------------------------------

    /**
     *  @private
     *  Used internally to determine the duration
     *  including the startDelay, repeatDelay, and repeatCount values.
     */
    mx_internal function get actualDuration():Number 
    {
        var value:Number = NaN;

        if (repeatCount > 0)
        {
            value = duration * repeatCount +
                    (repeatDelay * repeatCount - 1) + startDelay;
        }
        
        return value;
    }
    
    //----------------------------------
    //  className
    //----------------------------------

    /**
     *  @copy mx.effects.IEffectInstance#className
     */
    public function get className():String
    {
        var name:String = getQualifiedClassName(this);
        
        // If there is a package name, strip it off.
        var index:int = name.indexOf("::");
        if (index != -1)
            name = name.substr(index + 2);
        
        return name;
    }
    
    //----------------------------------
    //  duration
    //----------------------------------

    /**
     *  @private
     *  Storage for the duration property.
     */
    private var _duration:Number = 500;
    
    [Inspectable(category="General", defaultValue="500")]
    
    /** 
     *  @copy mx.effects.IEffectInstance#duration
     */
    public function get duration():Number
    {
        if (!mx_internal::durationExplicitlySet &&
            mx_internal::parentCompositeEffectInstance)
        {
            return mx_internal::parentCompositeEffectInstance.duration;
        }
        else
        {
            return _duration;
        }
    }
    
    /**
     *  @private
     */
    public function set duration(value:Number):void
    {
        mx_internal::durationExplicitlySet = true;
        _duration = value;
    }

    //----------------------------------
    //  effect
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the effect property.
     */
    private var _effect:IEffect;

    /**
     *  @copy mx.effects.IEffectInstance#effect
     */
    public function get effect():IEffect
    {
        return _effect;
    }
    
    /**
     *  @private
     */
    public function set effect(value:IEffect):void
    {
        _effect = value;
    }
    

    //----------------------------------
    //  effectTargetHost
    //----------------------------------

    /**
     *  @private
     *  Storage for the effectTargetHost property.
     */
    private var _effectTargetHost:IEffectTargetHost;
    
    /**
     *  @copy mx.effects.IEffectInstance#effectTargetHost
     */
    public function get effectTargetHost():IEffectTargetHost
    {
        return _effectTargetHost;
    }

    /**
     *  @private
     */
    public function set effectTargetHost(value:IEffectTargetHost):void
    {
        _effectTargetHost = value;
    }

    //----------------------------------
    //  hideFocusRing
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the hideFocusRing property.
     */
    private var _hideFocusRing:Boolean;

    /**
     *  @copy mx.effects.IEffectInstance#hideFocusRing
     */
    public function get hideFocusRing():Boolean
    {
        return _hideFocusRing;
    }
        
    /**
     *  @private
     */
    public function set hideFocusRing(value:Boolean):void
    {
        _hideFocusRing = value;
    }

    //----------------------------------
    //  playheadTime
    //----------------------------------
    
    /**
     *  @copy mx.effects.IEffectInstance#playheadTime
     */
    public function get playheadTime():Number 
    {
        return Math.max(playCount - 1, 0) * duration + 
               Math.max(playCount - 2, 0) * repeatDelay + 
               (playReversed ? 0 : startDelay);
    }
    
    //----------------------------------
    //  playReversed
    //----------------------------------

    /**
     *  @private
     *  Storage for the playReversed property. 
     */
    private var _playReversed:Boolean;
    
    /**
     *  @private
     *  Used internally to specify whether or not this effect
     *  should be played in reverse.
     *  Set this value before you play the effect. 
     */
    mx_internal function get playReversed():Boolean
    {
        return _playReversed;
    }
    
    /**
     *  @private
     */
    mx_internal function set playReversed(value:Boolean):void 
    {
        _playReversed = value;
    }
    
    //----------------------------------
    //  propertyChanges
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the propertyChanges property. 
     */
    private var _propertyChanges:PropertyChanges;

    /**
     *  @copy mx.effects.IEffectInstance#propertyChanges
     */
    public function get propertyChanges():PropertyChanges
    {
        return _propertyChanges;
    }

    /**
     *  @private
     */
    public function set propertyChanges(value:PropertyChanges):void
    {
        _propertyChanges = value;
    }
    
    //----------------------------------
    //  repeatCount
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the repeatCount property. 
     */
    private var _repeatCount:int = 0;

    /**
     *  @copy mx.effects.IEffectInstance#repeatCount
     */
    public function get repeatCount():int
    {
        return _repeatCount;
    }
    
    /**
     *  @private
     */
    public function set repeatCount(value:int):void
    {
        _repeatCount = value;
    }
    
    //----------------------------------
    //  repeatDelay
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the repeatDelay property. 
     */
    private var _repeatDelay:int = 0;

    /**
     *  @copy mx.effects.IEffectInstance#repeatDelay
     */
    public function get repeatDelay():int
    {
        return _repeatDelay;
    }
    
    /**
     *  @private
     */
    public function set repeatDelay(value:int):void
    {
        _repeatDelay = value;
    }
    
    //----------------------------------
    //  startDelay
    //----------------------------------

    /**
     *  @private
     *  Storage for the startDelay property. 
     */
    private var _startDelay:int = 0;

    /**
     *  @copy mx.effects.IEffectInstance#startDelay
     */
    public function get startDelay():int
    {
        return _startDelay;
    }

    /**
     *  @private
     */
    public function set startDelay(value:int):void
    {
        _startDelay = value;
    }
    
    //----------------------------------
    //  suspendBackgroundProcessing
    //----------------------------------

    /**
     *  @private
     *  Storage for the suspendBackgroundProcessing property. 
     */
    private var _suspendBackgroundProcessing:Boolean = false;

    /**
     *  @copy mx.effects.IEffectInstance#suspendBackgroundProcessing
     */
    public function get suspendBackgroundProcessing():Boolean
    {
        return _suspendBackgroundProcessing;
    }

    /**
     *  @private
     */
    public function set suspendBackgroundProcessing(value:Boolean):void
    {
        _suspendBackgroundProcessing = value;
    }
    
    //----------------------------------
    //  target
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the target property. 
     */
    private var _target:Object;

    /**
     *  @copy mx.effects.IEffectInstance#target
     */
    public function get target():Object
    {
        return _target;
    }

    /**
     *  @private
     */
    public function set target(value:Object):void
    {
        _target = value;
    }
    
    //----------------------------------
    //  triggerEvent
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the triggerEvent property. 
     */
    private var _triggerEvent:Event;

    /**
     *  @copy mx.effects.IEffectInstance#triggerEvent
     */
    public function get triggerEvent():Event
    {
        return _triggerEvent;
    }

    /**
     *  @private
     */
    public function set triggerEvent(value:Event):void
    {
        _triggerEvent = value;
    }
        
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @copy mx.effects.IEffectInstance#initEffect()
     */
    public function initEffect(event:Event):void
    {
        triggerEvent = event;
        
        switch (event.type)
        {
            case "resizeStart":
            case "resizeEnd":
            {
                if (!mx_internal::durationExplicitlySet)
                    duration = 250;
                break;
            }
            
            case FlexEvent.HIDE:
            {
                target.setVisible(true, true);
                hideOnEffectEnd = true;     
                // If somebody else shows us, then cancel the hide when the effect ends
                target.addEventListener(FlexEvent.SHOW, eventHandler);      
                break;
            }
        }
    }
    
    /**
     *  @copy mx.effects.IEffectInstance#startEffect()
     */
    public function startEffect():void
    {   
        EffectManager.effectStarted(this);

        if (target is UIComponent)
        {
            UIComponent(target).effectStarted(this);
        }
        
        if (startDelay > 0 && !playReversed)
        {
            delayTimer = new Timer(startDelay, 1);
            delayStartTime = getTimer();
            delayTimer.addEventListener(TimerEvent.TIMER, delayTimerHandler);
            delayTimer.start();
        }
        else
        {
            play();
        }
    }
            
    /**
     *  @copy mx.effects.IEffectInstance#play()
     */
    public function play():void
    {
        playCount++;
        
        dispatchEvent(new EffectEvent(EffectEvent.EFFECT_START, false, false, this));
        
        if (target)
		{
            target.dispatchEvent(new EffectEvent(EffectEvent.EFFECT_START, false, false, this));
		}
    }
    
    /**
     *  @copy mx.effects.IEffectInstance#pause()
     */
    public function pause():void
    {   
        if (delayTimer && delayTimer.running && !isNaN(delayStartTime))
        {
            delayTimer.stop(); // Pause the timer
            delayElapsedTime = getTimer() - delayStartTime;
        }
    }

    /**
     *  @copy mx.effects.IEffectInstance#stop()
     */
    public function stop():void
    {   
        if (delayTimer)
            delayTimer.reset();
        stopRepeat = true;
        finishEffect();
    }
    
    /**
     *  @copy mx.effects.IEffectInstance#resume()
     */
    public function resume():void
    {
        if (delayTimer && !delayTimer.running && !isNaN(delayElapsedTime))
        {
            delayTimer.delay = !playReversed ? delayTimer.delay - delayElapsedTime : delayElapsedTime;
            delayTimer.start();
        }
    }
        
    /**
     *  @copy mx.effects.IEffectInstance#reverse()
     */
    public function reverse():void
    {
        if (repeatCount > 0)
            playCount = repeatCount - playCount + 1;
    }
    
    /**
     *  @copy mx.effects.IEffectInstance#end()
     */
    public function end():void
    {
        if (delayTimer)
            delayTimer.reset();
        stopRepeat = true;
        finishEffect();
    }
    
    /**
     *  @copy mx.effects.IEffectInstance#finishEffect()
     */
    public function finishEffect():void
    {
        playCount = 0;
    
        dispatchEvent(new EffectEvent(EffectEvent.EFFECT_END,
                                     false, false, this));
        
        if (target)
        {
            target.dispatchEvent(new EffectEvent(EffectEvent.EFFECT_END,
                                                 false, false, this));
        }
        
        if (target is UIComponent)
        {
            UIComponent(target).effectFinished(this);
        }

        EffectManager.effectFinished(this);
    }

    /**
     *  @copy mx.effects.IEffectInstance#finishRepeat()
     */
    public function finishRepeat():void
    {
        if (!stopRepeat && playCount != 0 &&
            (playCount < repeatCount || repeatCount == 0))
        {
            if (repeatDelay > 0)
            {
                delayTimer = new Timer(repeatDelay, 1);
                delayStartTime = getTimer();
                delayTimer.addEventListener(TimerEvent.TIMER,
                                            delayTimerHandler);
                delayTimer.start();
            }
            else
            {
                play();
            }
        }
        else
        {
            finishEffect();
        }
    }
    
    /**
     *  @private
     */
    mx_internal function playWithNoDuration():void
    {
        duration = 0;
        repeatCount = 1;
        repeatDelay = 0;
        startDelay = 0;
        
        startEffect();
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  If someone explicitly sets the visibility of the target object
     *  to true, clear the flag that is remembering to hide the 
     *  target when this effect ends.
     */
    mx_internal function eventHandler(event:Event):void
    {
        if (event.type == FlexEvent.SHOW && hideOnEffectEnd == true)
        {
            hideOnEffectEnd = false;
            event.target.removeEventListener(FlexEvent.SHOW, eventHandler);
        }
    }
    
    /**
     *  @private
     */
    private function delayTimerHandler(event:TimerEvent):void
    {
        delayTimer.reset();
        delayStartTime = NaN;
        delayElapsedTime = NaN;
        play();
    }
}

}
