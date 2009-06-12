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

import mx.effects.effectClasses.TweenEffectInstance;
import mx.events.TweenEvent;
import flash.events.EventDispatcher;

/**
 *  Dispatched when the tween effect starts, which corresponds to the 
 *  first call to the <code>onTweenUpdate()</code> method.
 *  Flex also dispatches the first <code>tweenUpdate</code> event 
 *  for the effect at the same time.
 *
 *  <p>The <code>Effect.effectStart</code> event is dispatched 
 *  before the <code>tweenStart</code> event.</p>
 *
 *  @eventType mx.events.TweenEvent.TWEEN_START
 */
[Event(name="tweenStart", type="mx.events.TweenEvent")]

/**
 *  Dispatched every time the tween effect updates the target.
 *  This event corresponds to a call to 
 *  the <code>TweenEffectInstance.onTweenUpdate()</code> method.
 *
 *  @eventType mx.events.TweenEvent.TWEEN_UPDATE
 */
[Event(name="tweenUpdate", type="mx.events.TweenEvent")]

/**
 *  Dispatched when the tween effect ends.
 *  This event corresponds to a call to 
 *  the <code>TweenEffectInstance.onTweenEnd()</code> method.
 *
 *  <p>When a tween effect plays a single time, this event occurs
 *  at the same time as an <code>effectEnd</code> event.
 *  If you configure the tween effect to repeat, 
 *  it occurs at the end of every repetition of the effect,
 *  and the <code>endEffect</code> event occurs
 *  after the effect plays for the final time.</p>
 *
 *  @eventType mx.events.TweenEvent.TWEEN_END
 */
[Event(name="tweenEnd", type="mx.events.TweenEvent")]

/**
 *  The TweenEffect class is the superclass for all effects
 *  that are based on the Tween object.
 *  This class encapsulates methods and properties that are common
 *  among all Tween-based effects, to avoid duplication of code elsewhere.
 *
 *  <p>You create a subclass of the TweenEffect class to define
 *  an effect that plays an animation over a period of time. 
 *  For example, the Resize effect modifies the size of its target
 *  over a specified duration.</p>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:TweenEffect&gt;</code> tag
 *  inherits all of the tag attributes of its superclass,
 *  and adds the following tag attributes:</p>
 *  
 *  <pre>
 *  &lt;mx:TagName
 *    <b>Properties</b>
 *    easingFunction="<i>easing function name; no default</i>"
 *     
 *    <b>Events</b>
 *    tweenEnd="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.Tween
 *  @see mx.effects.effectClasses.TweenEffectInstance 
 * 
 *  @includeExample examples/SimpleTweenEffectExample.mxml
 */
public class TweenEffect extends Effect
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
     *  @param target The Object to animate with this effect.
     */
    public function TweenEffect(target:Object = null)
    {
        super(target);

        instanceClass = TweenEffectInstance;    
    }
        
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
 
    //----------------------------------
    //  easingFunction
    //----------------------------------
    
    /**
     *  The easing function for the animation.
     *  The easing function is used to interpolate between the initial value
     *  and the final value.
     *  A trivial easing function would simply do linear interpolation,
     *  but more sophisticated easing functions create the illusion of
     *  acceleration and deceleration, which makes the animation seem
     *  more natural.
     *
     *  <p>If no easing function is specified, an easing function based
     *  on the <code>Math.sin()</code> method is used.</p>
     *
     *  <p>The easing function follows the function signature popularized
     *  by Robert Penner.
     *  The function accepts four arguments.
     *  The first argument is the "current time",
     *  where the animation start time is 0.
     *  The second argument is the initial value
     *  at the beginning of the animation (a Number).
     *  The third argument is the ending value minus the initial value.
     *  The fourth argument is the duration of the animation.
     *  The return value is the interpolated value for the current time.
     *  This is usually a value between the initial value
     *  and the ending value.</p>
     *
     *  <p>The value of this property must be a function object.</p>
     *
     *  <p>Flex includes a set of easing functions
     *  in the mx.effects.easing package.</p>
     *
     */ 
    public var easingFunction:Function = null;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override protected function initInstance(instance:IEffectInstance):void
    {
        super.initInstance(instance);
        
        TweenEffectInstance(instance).easingFunction = easingFunction;

        EventDispatcher(instance).addEventListener(TweenEvent.TWEEN_START, tweenEventHandler);      
        EventDispatcher(instance).addEventListener(TweenEvent.TWEEN_UPDATE, tweenEventHandler);         
        EventDispatcher(instance).addEventListener(TweenEvent.TWEEN_END, tweenEventHandler);
    }
    
    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  Called when the TweenEffect dispatches a TweenEvent.
     *  If you override this method, ensure that you call the super method.
     *
     *  @param event An event object of type TweenEvent.
     */
    protected function tweenEventHandler(event:TweenEvent):void
    {
        dispatchEvent(event);
    }
}

}
