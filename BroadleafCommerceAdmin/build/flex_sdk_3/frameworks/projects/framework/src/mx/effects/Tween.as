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

import flash.events.EventDispatcher;
import flash.events.IEventDispatcher;
import flash.events.TimerEvent;
import flash.utils.Timer;
import flash.utils.getTimer;
import mx.core.UIComponentGlobals;
import mx.core.mx_internal;
import mx.events.TweenEvent;

use namespace mx_internal;
/**
 *  The Tween class defines a tween, a property animation performed
 *  on a target object over a period of time.
 *  That animation can be a change in position, such as performed by
 *  the Move effect; a change in size, as performed by the Resize or
 *  Zoom effects; a change in visibility, as performed by the Fade or
 *  Dissolve effects; or other types of animations.
 *
 *  <p>When defining tween effects, you typically create an instance
 *  of the Tween class within your override of the 
 *  <code>EffectInstance.play()</code> method.
 *  A Tween instance accepts the <code>startValue</code>,
 *  <code>endValue</code>, and <code>duration</code> properties, 
 *  and an optional easing function to define the animation.</p> 
 *
 *  <p>The Tween object invokes the
 *  <code>mx.effects.effectClasses.TweenEffectInstance.onTweenUpdate()</code> 
 *  callback function on a regular interval on the effect instance
 *  for the duration of the effect, passing to the
 *  <code>onTweenUpdate()</code> method an interpolated value 
 *  between the <code>startValue</code> and <code>endValue</code>.
 *  Typically, the callback function updates some property of the target object, 
 *  causing that object to animate over the duration of the effect.</p>
 *
 *  <p>When the effect ends, the Tween objects invokes the 
 *  <code>mx.effects.effectClasses.TweenEffectInstance.onTweenEnd()</code>
 *  callback function, if you defined one. </p>
 *
 *  @see mx.effects.TweenEffect
 *  @see mx.effects.effectClasses.TweenEffectInstance
 */
public class Tween extends EventDispatcher
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal static var activeTweens:Array = [];
    
    /**
     *  @private
     */
    private static var interval:Number = 10;
    
    /**
     *  @private
     */
    private static var timer:Timer = null;
        
    //--------------------------------------------------------------------------
    //
    //  Class properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  intervalTime
    //----------------------------------

    /**
     *  @private    
     *  Used by effects to get the current effect time tick.
     */
    mx_internal static var intervalTime:Number = NaN;
    
    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static function addTween(tween:Tween):void
    {
        tween.id = activeTweens.length;
        
        activeTweens.push(tween);
        
        if (!timer)
        {
            timer = new Timer(interval);
            timer.addEventListener(TimerEvent.TIMER, timerHandler);
            timer.start();
        }
        else
        {
            timer.start();
        }
        
        if (isNaN(intervalTime))
            intervalTime = getTimer();

        tween.startTime = tween.previousUpdateTime = intervalTime;
    }

    /**
     *  @private
     */
    private static function removeTweenAt(index:int):void
    {
        if (index >= activeTweens.length || index < 0)
            return;

        activeTweens.splice(index, 1);
                
        var n:int = activeTweens.length;
        for (var i:int = index; i < n; i++)
        {
            var curTween:Tween = Tween(activeTweens[i]);
            curTween.id--;
        }
        
        if (n == 0)
        {
            intervalTime = NaN;
            timer.reset();
        }
    }

    /**
     *  @private
     */
    mx_internal static function removeTween(tween:Tween):void
    {
        removeTweenAt(tween.id);
    }

    /**
     *  @private
     */
    private static function timerHandler(event:TimerEvent):void
    {
        var needToLayout:Boolean = false;
        
        var oldTime:Number = intervalTime;
        intervalTime = getTimer();
                
        var n:int = activeTweens.length;
                
        for (var i:int = n; i >= 0; i--)
        {
            var tween:Tween = Tween(activeTweens[i]);
            if (tween)
            {
                tween.needToLayout = false;
                tween.doInterval();
                
                if (tween.needToLayout)
                    needToLayout = true;
            }
        }
                
        // If one of the effects requested a layout, do it now.
        if (needToLayout)
        {
            UIComponentGlobals.layoutManager.validateNow();
        }
        
        event.updateAfterEvent();
    }

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  <p>When the constructor is called, the animation automatically
     *  starts playing.</p>
     *
     *  @param listener Object that is notified at each interval
     *  of the animation. You typically pass the <code>this</code> 
     *  keyword as the value.
     *  The <code>listener</code> must define the 
     *  <code>onTweenUpdate()</code> method and optionally the  
     *  <code>onTweenEnd()</code> method.
     *  The former method is invoked for each interval of the animation,
     *  and the latter is invoked just after the animation finishes.
     *
     *  @param startValue Initial value(s) of the animation.
     *  Either a number or an array of numbers.
     *  If a number is passed, the Tween interpolates
     *  between this number and the number passed
     *  in the <code>endValue</code> parameter.
     *  If an array of numbers is passed, 
     *  each number in the array is interpolated.
     *
     *  @param endValue Final value(s) of the animation.
     *  The type of this argument must match the <code>startValue</code>
     *  parameter.
     *
     *  @param duration Duration of the animation, expressed in milliseconds.
     *
     *  @param minFps Minimum number of times that the
     *  <code>onTweenUpdate()</code> method should be called every second.
     *  The tween code tries to call the <code>onTweenUpdate()</code>
     *  method as frequently as possible (up to 100 times per second).
     *  However, if the frequency falls below <code>minFps</code>, 
     *  the duration of the animation automatically increases.
     *  As a result, an animation that temporarily freezes
     *  (because it is not getting any CPU cycles) begins again
     *  where it left off, instead of suddenly jumping ahead. 
     *
     *  @param updateFunction Specifies an alternative update callback 
     *  function to be used instead of <code>listener.OnTweenUpdate()</code>
     *
     *  @param endFunction Specifies an alternative end callback function
     *  to be used instead of <code>listener.OnTweenEnd()</code>
     */
    public function Tween(listener:Object,
                          startValue:Object, endValue:Object,
                          duration:Number = -1, minFps:Number = -1,
                          updateFunction:Function = null,
                          endFunction:Function = null)
    {
        super();

        if (!listener)
            return;
        
        if (startValue is Array)
            arrayMode = true;

        this.listener = listener;
        this.startValue = startValue;
        this.endValue = endValue;
        
        if (!isNaN(duration) && duration != -1)
            this.duration = duration;

        // If user has specified a minimum number of frames per second,
        // remember the maximum allowable milliseconds between frames.
        if (!isNaN(minFps) && minFps != -1)
            maxDelay = 1000 / minFps;

        this.updateFunction = updateFunction;
        this.endFunction = endFunction;
        
        if (duration == 0)
        {
            id = -1; // use -1 to indicate that this tween was never added
            endTween();
        }
        else
            Tween.addTween(this);
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    mx_internal var needToLayout:Boolean = false;
    
    /**
     *  @private
     */
    private var id:int;
    
    /**
     *  @private
     */
    private var maxDelay:Number = 87.5; // Min frames per second is 12
    
    /**
     *  @private
     */
    private var arrayMode:Boolean;
    
    /**
     *  @private
     */
    private var _doSeek:Boolean = false;
    
    /**
     *  @private
     */
    private var _isPlaying:Boolean = true;
    
    /**
     *  @private
     */
    private var _doReverse:Boolean = false;

    /**
     *  @private
     */
    mx_internal var startTime:Number;
    
    /**
     *  @private
     */
    private var previousUpdateTime:Number;
    
    /**
     *  @private
     */
    private var userEquation:Function = defaultEasingFunction;
    
    /**
     *  @private
     */
    private var updateFunction:Function;
    
    /**
     *  @private
     */
    private var endFunction:Function;
    
    /**
     *  @private
     *  Final value(s) of the animation.
     *  This can be a Number of an Array of Numbers.
     */
    private var endValue:Object;
    
    /**
     *  @private
     *  Initial value(s) of the animation.
     *  This can be a Number of an Array of Numbers.
     */
    private var startValue:Object;
    
    /**
     *  @private
     */
    private var started:Boolean = false;
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  duration
    //----------------------------------

    /**
     *  Duration of the animation, in milliseconds. 
     */
    public var duration:Number = 3000;
    
    //----------------------------------
    //  listener
    //----------------------------------

    /**
     *  Object that is notified at each interval of the animation. 
     */
    public var listener:Object;
    
    //----------------------------------
    //  playheadTime
    //----------------------------------

    /**
     *  @private
     *  Storage for the playheadTime property.
     */
    private var _playheadTime:Number = 0;
    
    /**
     *  @private
     *  The current millisecond position in the tween.
     *  This value is between 0 and duration. 
     *  Use the seek() method to change the position of the tween.
     */
    mx_internal function get playheadTime():Number
    {
        return _playheadTime;
    }
    
    //----------------------------------
    //  playReversed
    //----------------------------------

    /**
     *  @private
     *  Storage for the playReversed property.
     */
    private var _invertValues:Boolean = false;
    
    /**
     *  @private
     *  Starts playing reversed from start of tween.
     *  Setting this property to <code>true</code>
     *  inverts the values returned by the tween.
     *  Using reverse inverts the values and only plays
     *  for as much time that has already elapsed. 
     */
    mx_internal function get playReversed():Boolean
    {
        return _invertValues;
    }

    /**
     *  @private 
     */
    mx_internal function set playReversed(value:Boolean):void
    {
        _invertValues = value;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  By default, the Tween class invokes the 
     *  <code>mx.effects.effectClasses.TweenEffectInstance.onTweenUpdate()</code> 
     *  callback function on a regular interval on the effect instance
     *  for the duration of the effect, and the optional 
     *  <code>mx.effects.effectClasses.TweenEffectInstance.onTweenEnd()</code>
     *  callback function at the end of the effect duration. 
     *
     *  <p>This method lets you specify different methods 
     *  as the update and the end callback functions.</p>
     *
     *  @param updateFunction Specifies the update callback function.
     *
     *  @param endFunction Specifies the end callback function.
     */
    public function setTweenHandlers(updateFunction:Function,
                                     endFunction:Function):void
    {
        this.updateFunction = updateFunction;
        this.endFunction = endFunction;
    }

    /**
     *  Sets the easing function for the animation.
     *  The easing function is used to interpolate between
     *  the <code>startValue</code> value and the <code>endValue</code>.
     *  A trivial easing function does linear interpolation,
     *  but more sophisticated easing functions create the illusion of
     *  acceleration and deceleration, which makes the animation seem
     *  more natural.
     *
     *  <p>If no easing function is specified, an easing function based
     *  on the <code>Math.sin()</code> method is used.</p>
     *
     *  <p>The easing function follows the function signature
     *  popularized by Robert Penner.
     *  The function accepts four arguments.
     *  The first argument is the "current time",
     *  where the animation start time is 0.
     *  The second argument is a the initial value
     *  at the beginning of the animation (a Number).
     *  The third argument is the ending value
     *  minus the initial value.
     *  The fourth argument is the duration of the animation.
     *  The return value is the interpolated value for the current time
     *  (usually a value between the initial value and the ending value).</p>
     *
     *  <p>Flex includes a set of easing functions
     *  in the mx.effects.easing package.</p>
     *
     *  @param easingFunction Function that implements the easing equation. 
     */
    public function set easingFunction(value:Function):void
    {   
        userEquation = value;
    }   
    
    /**
     *  Interrupt the tween, jump immediately to the end of the tween, 
     *  and invoke the <code>onTweenEnd()</code> callback function.
     */
    public function endTween():void
    {
        var event:TweenEvent = new TweenEvent(TweenEvent.TWEEN_END);
        var value:Object = getCurrentValue(duration);

        event.value = value;
        
        dispatchEvent(event); 
        
        if (endFunction != null)
            endFunction(value);
        else
            listener.onTweenEnd(value);

        // If tween has been added, id >= 0
        // but if duration = 0, this might not be the case.
        if (id >= 0)
            Tween.removeTweenAt(id);
    }

    /**
     *  @private
     *  Returns true if the tween has ended.
     */
    mx_internal function doInterval():Boolean
    {
        var tweenEnded:Boolean = false;
        
        // If user specified a minimum frames per second, we can't guarantee
        // that we'll be called often enough to satisfy that request.
        // However, we can avoid skipping over part of the animation.
        // If this callback arrives too late, adjust the animation startTime,
        // so that the animation starts up 'maxDelay' milliseconds
        // after its last update.
        /*
        if (intervalTime - previousUpdateTime > maxDelay)
        {
            startTime += intervalTime - previousUpdateTime - maxDelay;
        }
        */
        previousUpdateTime = intervalTime;
        
        if (_isPlaying || _doSeek)
        {
            
            var currentTime:Number = intervalTime - startTime;
            _playheadTime = currentTime;
            
            var currentValue:Object =
                getCurrentValue(currentTime);

            if (currentTime >= duration && !_doSeek)
            {
                endTween();
                tweenEnded = true;
            }
            else
            {
                if (!started)
                {
                    var startEvent:TweenEvent = new TweenEvent(TweenEvent.TWEEN_START);
                    dispatchEvent(startEvent);
                    started = true;
                }
            
                var event:TweenEvent =
                    new TweenEvent(TweenEvent.TWEEN_UPDATE);
                event.value = currentValue;
                
                dispatchEvent(event);
                
                if (updateFunction != null)
                    updateFunction(currentValue);
                else
                    listener.onTweenUpdate(currentValue);
            }
            
            _doSeek = false;
        }
        return tweenEnded;
    }

    /**
     *  @private
     */
    mx_internal function getCurrentValue(currentTime:Number):Object
    {
        if (duration == 0)
        {
            return endValue;
        }
    
        if (_invertValues)
            currentTime = duration - currentTime;
    
        if (arrayMode) 
        {
            var returnArray:Array = [];
            var n:int = startValue.length;
            for (var i:int = 0; i < n; i++)
            {
                returnArray[i] = userEquation(currentTime, startValue[i],
                                              endValue[i] - startValue[i],
                                              duration);
            }
            return returnArray;
        }
        else
        {
            return userEquation(currentTime, startValue,
                                Number(endValue) - Number(startValue),
                                duration);
        }
    }
    
    /**
     *  @private
     */
    private function defaultEasingFunction(t:Number, b:Number,
                                           c:Number, d:Number):Number
    {
        return c / 2 * (Math.sin(Math.PI * (t / d - 0.5)) + 1) + b;
    }
    
    /**
     *  Advances the tween effect to the specified position. 
     *
     *  @param playheadTime The position, in milliseconds, between 0
     *  and the value of the <code>duration</code> property.
     */ 
    public function seek(playheadTime:Number):void
    {
        // Set value between 0 and duration
        //playheadTime = Math.min(Math.max(playheadTime, 0), duration);
        
        var clockTime:Number = intervalTime;
        
        // Reset the previous update time
        previousUpdateTime = clockTime;
        
        // Reset the start time
        startTime = clockTime - playheadTime;
        
        _doSeek = true;
    }
    
    /**
     *  Plays the effect in reverse,
     *  starting from the current position of the effect.
     */
    public function reverse():void
    {
        if (_isPlaying)
        {
            _doReverse = false;
            seek(duration - _playheadTime);
            _invertValues = !_invertValues;
        }
        else
        {
            _doReverse = !_doReverse;
        }
    }
    
    /**
     *  Pauses the effect until you call the <code>resume()</code> method.
     */
    public function pause():void
    {
        _isPlaying = false;
    }

    /**
     *  Stops the tween, ending it without dispatching an event or calling
     *  the Tween's endFunction or <code>onTweenEnd()</code>. 
     */
    public function stop():void
    {
        if (id >= 0)
            Tween.removeTweenAt(id);
    }
    
    /**
     *  Resumes the effect after it has been paused 
     *  by a call to the <code>pause()</code> method. 
     */
    public function resume():void
    {
        _isPlaying = true;
        
        startTime = intervalTime - _playheadTime;
        if (_doReverse)
        {
            reverse();
            _doReverse = false;
        }
    }   
}

}
