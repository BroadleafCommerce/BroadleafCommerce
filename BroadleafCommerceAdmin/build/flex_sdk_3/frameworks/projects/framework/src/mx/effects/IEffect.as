////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.effects
{

import flash.events.Event;
import flash.events.IEventDispatcher;

/**
 *  The IEffect interface defines the base 
 *  interface of all Flex effects.
 *  The IEffectInstance interface defines the base interface for all effect
 *  instance subclasses.
 *
 *  @see mx.effects.IEffectInstance
 */
public interface IEffect extends IAbstractEffect
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
    
    //----------------------------------
    //  className
    //----------------------------------

    /**
     *  The name of the effect class, such as <code>"Fade"</code>.
     *
     *  <p>This is a short, or unqualified, class name
     *  that does not include the package name.
     *  If you need the qualified name, use the 
     *  <code>getQualifiedClassName()</code> method
     *  in the flash.utils package.</p>
     */
    function get className():String;

    //----------------------------------
    //  customFilter
    //----------------------------------

    /**
     *  Specifies a custom filter object, of type EffectTargetFilter,
     *  used by the effect to determine the targets
     *  on which to play the effect. 
     *  
     *  <p>Target filtering is only performed if you call the
     *  <code>captureStartValues()</code> method before playing the effect.
     *  Flex automatically calls the <code>captureStartValues()</code> method
     *  when the effect is part of a transition.</p>
     *  
     *  <p>Use the <code>filter</code> property for simple filtering.
     *  If the <code>customFilter</code> property is non-null,
     *  the <code>filter</code> property is ignored.</p>
     *
     *  @default null
     *
     *  @see mx.effects.EffectTargetFilter
     */
    function get customFilter():EffectTargetFilter;
    
    /**
     *  @private
     */
    function set customFilter(value:EffectTargetFilter):void;

    //----------------------------------
    //  duration
    //----------------------------------

    /** 
     *  Duration of the effect in milliseconds. 
     *
     *  <p>In a Parallel or Sequence effect, the <code>duration</code>
     *  property sets the duration of each effect.
     *  For example, if a Sequence effect has its <code>duration</code>
     *  property set to 3000, each effect in the Sequence takes 3000 ms
     *  to play.</p>
     *
     *  <p>For a repeated effect, the <code>duration</code> property
     *  specifies  the duration of a single instance of the effect. 
     *  Therefore, if an effect has a <code>duration</code> property
     *  set to 2000, and a <code>repeatCount</code> property set to 3, 
     *  the effect takes a total of 6000 ms (6 seconds) to play.</p>
     *
     *  @default 500
     */
    function get duration():Number;
    
    /**
     *  @private
     */
    function set duration(value:Number):void;

    //----------------------------------
    //  effectTargetHost
    //----------------------------------

    /**
     *  A property that lets you access the target list-based control
     *  of a data effect. 
     *  This property enables an instance of an effect class to communicate
     *  with the list-based control on which the effect is playing. 
     */
    function get effectTargetHost():IEffectTargetHost;
    
    /**
     *  @private
     */
    function set effectTargetHost(value:IEffectTargetHost):void;

    //----------------------------------
    //  filter
    //----------------------------------

    /**
     *  Specifies an algorithm for filtering targets for an effect. 
     *  A value of <code>null</code> specifies no filtering.
     *  
     *  <p>Target filtering is only performed if you call the
     *  <code>captureStartValues()</code> method before playing the effect.
     *  Flex automatically calls the <code>captureStartValues()</code> method
     *  when the effect is part of a transition, or part of a data effect
     *  for a list-based control.</p>
     *  
     *  <p>Use this property for simple filtering.
     *  Use the <code>customFilter</code> property for more complex filtering. 
     *  If the <code>customFilter</code> property has a non-null value, 
     *  this property is ignored.</p> 
     *
     *  <p>You can use the following values for the <code>filter</code>
     *  property:</p>
     *
     *  <ul>
     *    <li>A value of <code>"add"</code> plays the effect on any targets 
     *      that are added as a child to a container.</li>
     *    <li>A value of <code>"addItem"</code> plays the effect
     *      on the item renderer for any list items added to a List
     *      or TileList control.</li>
     *    <li>A value of <code>"hide"</code> plays the effect on any targets
     *      whose visible property changed from <code>true</code> to
     *      <code>false</code>.</li>
     *    <li>A value of <code>"move"</code> plays the effect on any targets
     *      that changed their <code>x</code> or <code>y</code>
     *      properties.</li>
     *    <li>A value of <code>"remove"</code> plays the effect on any targets
     *      that are removed as a child of a container.</li>
     *    <li>A value of <code>"removeItem"</code> plays the effect
     *      on the item renderer for any list items removed from a List
     *      or TileList control.</li>
     *    <li>A value of <code>"replacedItem"</code> plays the effect
     *      on the item renderer for any list items replaced in a List
     *      or TileList control by a new item.</li>
     *    <li>A value of <code>"replacementItem"</code> plays the effect
     *      on the item renderer for any list items added to a List
     *      or TileList control that replaces an existing item.</li>
     *    <li>A value of <code>"resize"</code> plays the effect
     *      on any targets that changed their <code>width</code>
     *      or <code>height</code> properties.</li>
     *    <li>A value of <code>"show"</code> plays the effect
     *      on any targets whose visible property changed
     *      from <code>false</code> to <code>true</code>.</li>
     *    <li>A value of <code>""</code> specifies no filtering.</li>
     *  </ul>
     *
     *  @default null
     */
    function get filter():String;
    
    /**
     *  @private
     */
    function set filter(value:String):void;

    //----------------------------------
    //  hideFocusRing
    //----------------------------------

    /**
     *  Determines whether the effect should hide the focus ring
     *  when starting the effect.
     *  The effect target is responsible for the hiding the focus ring. 
     *  Subclasses of the UIComponent class hide the focus ring automatically. 
     *  If the effect target is not a subclass of the UIComponent class,
     *  you must add functionality to it to hide the focus ring.
     *
     *  <p>Set this property to <code>true</code>
     *  to hide the focus ring during the effect.</p>
     *  
     *  <p>For subclasses of Effect, the default value is <code>false</code>. 
     *  For subclasses of MaskEffect, the default value is <code>true</code>.
     *  </p>
     */
    function get hideFocusRing():Boolean;
    
    /**
     *  @private
     */
    function set hideFocusRing(value:Boolean):void;

    //----------------------------------
    //  isPlaying
    //----------------------------------

    /**
     *  A read-only flag which is true if any instances of the effect
     *  are currently playing, and false if none are.
     */
    function get isPlaying():Boolean;

    //----------------------------------
    //  perElementOffset
    //----------------------------------

    /**
     *  Additional delay, in milliseconds, for effect targets
     *  after the first target of the effect.
     *  This value is added to the value
     *  of the <code>startDelay</code> property.
     */ 
    function get perElementOffset():Number;
    
    /**
     *  @private
     */
    function set perElementOffset(value:Number):void;

    //----------------------------------
    //  relevantProperties
    //----------------------------------

    /**
     *  An Array of property names to use when performing filtering. 
     *  This property is used internally and should not be set by 
     *  effect users. 
     *
     *  <p>The default value is equal to the Array returned by 
     *  the <code>getAffectedProperties()</code> method.</p>
     */
    function get relevantProperties():Array /* of String */;

    /**
     *  @private
     */
    function set relevantProperties(values:Array /* of String */):void;
    
    //----------------------------------
    //  relevantStyles
    //----------------------------------

    /**
     *  An Array of style names to use when performing filtering. 
     *  This property is used internally and should not be set by 
     *  effect users. 
     *
     *  <p>The default value is equal to the Array returned by 
     *  the <code>getAffectedProperties()</code> method.</p>
     */
    function get relevantStyles():Array /* of String */;
    
    /**
     *  @private
     */
    function set relevantStyles(values:Array /* of String */):void;
    
    
    //----------------------------------
    //  target
    //----------------------------------

    /** 
     *  The UIComponent object to which this effect is applied.
     *  When an effect is triggered by an effect trigger, 
     *  the <code>target</code> property is automatically set to be 
     *  the object that triggers the effect.
     */
    function get target():Object;
    
    /**
     *  @private
     */
    function set target(value:Object):void;

    //----------------------------------
    //  targets
    //----------------------------------

    /**
     *  An Array of UIComponent objects that are targets for the effect.
     *  When the effect is playing, it performs the effect on each target
     *  in parallel. 
     *  Setting the <code>target</code> property replaces all objects
     *  in this Array. 
     *  When the <code>targets</code> property is set, the <code>target</code>
     *  property returns the first item in this Array. 
     */
    function get targets():Array;
    
    /**
     *  @private
     */
    function set targets(value:Array):void;

    //----------------------------------
    //  triggerEvent
    //----------------------------------

    /**
     *  The Event object passed to this Effect 
     *  by the EffectManager when an effect is triggered,
     *  or <code>null</code> if the effect is not being
     *  played by the EffectManager.
     */
    function get triggerEvent():Event;
    
    /**
     *  @private
     */
    function set triggerEvent(value:Event):void;

    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns an Array of Strings, where each String is the name
     *  of a property that is changed by this effect.
     *  For example, the Move effect returns an Array that contains
     *  <code>"x"</code> and <code>"y"</code>.
     *
     *  <p>Every subclass of Effect must implement this method.
     *  The method is used by the EffectManager 
     *  to ensure that no two effects are trying to animate
     *  the same property of the same object at the same time.</p>
     *
     *  @return An Array of Strings specifying the names of the 
     *  properties modified by this effect.
     */
    function getAffectedProperties():Array /* of String */;

    /**
     *  Takes an Array of target objects and invokes the 
     *  <code>createInstance()</code> method on each target. 
     *
     *  @param targets Array of objects to animate with this effect.
     *
     *  @return Array of effect instance objects, one per target,
     *  for the effect.
     */
    function createInstances(targets:Array = null):Array /* of EffectInstance */;

    /**
     *  Creates a single effect instance and initializes it.
     *  Use this method instead of the <code>play()</code> method
     *  to manipulate the effect instance properties
     *  before the effect instance plays. 
     *  
     *  <p>The effect instance is created with the type 
     *  specified in the <code>instanceClass</code> property.
     *  It is then initialized using the <code>initInstance()</code> method. 
     *  If the instance was created by the EffectManager 
     *  (when the effect is triggered by an effect trigger), 
     *  the effect is further initialized by a call to the 
     *  <code>EffectInstance.initEffect()</code> method.</p>
     * 
     *  <p>Calling the <code>createInstance()</code> method 
     *  does not play the effect.
     *  You must call the <code>startEffect()</code> method
     *  on the returned effect instance. </p>
     *
     *  <p>This function is automatically called by the 
     *  <code>Effect.play()</code> method. </p>
     *
     *  @param target Object to animate with this effect.
     *
     *  @return The effect instance object for the effect.
     */
    function createInstance(target:Object = null):IEffectInstance;

    /**
     *  Removes event listeners from an instance
     *  and removes it from the list of instances.
     */
    function deleteInstance(instance:IEffectInstance):void;

    /**
     *  Begins playing the effect.
     *  You typically call the <code>end()</code> method 
     *  before you call the <code>play()</code> method
     *  to ensure that any previous instance of the effect
     *  has ended before you start a new one.
     *
     *  <p>All subclasses must implement this method.</p>
     *
     *  @param targets Array of target objects on which to play this effect.
     *  If this parameter is specified, then the effect's <code>targets</code>
     *  property is not used.
     *
     *  @param playReversedFromEnd If <code>true</code>,
     *  play the effect backwards.
     *
     *  @return Array of EffectInstance objects, one per target,
     *  for the effect.
     */ 
    function play(targets:Array = null,
                  playReversedFromEnd:Boolean = false):
                  Array /* of EffectInstance */;

    /**
     *  Pauses the effect until you call the <code>resume()</code> method.
     */
    function pause():void;

    /**
     *  Stops the effect, leaving the effect targets in their current state.
     *  Unlike a call to the <code>pause()</code> method, 
     *  you cannot call the <code>resume()</code> method after calling 
     *  the <code>stop()</code> method. 
     *  However, you can call the <code>play()</code> method to restart the effect.
     *
     *  <p>The effect instance dispatches an <code>effectEnd</code> event
     *  when you call this method as part of ending the effect.</p>
     * 
     *  <p>For mask effects, the mask is not removed automatically
     *  when the effect is stopped.
     *  Running further mask effects on the same target(s)
     *  without first removing the mask may produce unexpected results.</p>
     */
    function stop():void;

    /**
     *  Resumes the effect after it has been paused 
     *  by a call to the <code>pause()</code> method. 
     */
    function resume():void;

    /**
     *  Plays the effect in reverse, if the effect is currently playing,
     *  starting from the current position of the effect.
     */
    function reverse():void;

    /**
     *  Interrupts an effect that is currently playing,
     *  and jumps immediately to the end of the effect.
     *  Calling this method invokes the <code>EffectInstance.end()</code>
     *  method.
     *
     *  <p>The effect instance dispatches an <code>effectEnd</code> event
     *  when you call this method as part of ending the effect.</p>
     *
     *  <p>If you pass an effect instance as an argument, 
     *  just that instance is interrupted.
     *  If no argument is passed in, all effect instances currently
     *  spawned from the effect are interrupted.</p>
     *
     *  @param effectInstance EffectInstance to terminate.
     *
     *  @see mx.effects.EffectInstance#end()
     */
    function end(effectInstance:IEffectInstance = null):void;

    /**
     *  Captures the current values of the relevant properties
     *  on the effect's targets. 
     *  Flex automatically calls the <code>captureStartValues()</code>
     *  method when the effect is part of a transition.
     *  
     *  <p>Use this function when you want the effect to figure out the start 
     *  and end values of the effect.
     *  The proper usage of this function is to use it
     *  in the following steps:</p>
     *  
     *  <ol>
     *    <li>Call the <code>captureStartValues()</code> method. 
     *      The effect captures the starting effect values.</li>
     *    <li>Make changes to your effect targets, such as
     *      adding/removing children, altering properties,
     *      changing location, or changing dimensions.</li>
     *    <li>Call the <code>play()</code> method.  
     *      The effect captures the end values.
     *      This function populates the
     *      <code>EffectInstance.propertyChanges</code> property
     *      for each effect instance created by this effect. 
     *      Effect developers can use the <code>propertyChanges</code> property 
     *      to retrieve the start and end values for their effect.</li>
     *  </ol>
     */
    function captureStartValues():void;
    
    /**
     *  Captures the current values of the relevant properties
     *  of an additional set of targets
     * 
     *  <p>This function is used by Flex when a data change
     *  effect is run.</p>
     * 
     *  @param targets Array of targets for which values will be captured
     */
    function captureMoreStartValues(targets:Array):void;

    /**
     *  Captures the current values of the relevant properties
     *  on the effect's targets and saves them as end values.
     *  
     *  <p>Flex automatically calls the <code>captureEndValues()</code> method
     *  when the effect is part of a data change effect.</p>
     */
    function captureEndValues():void;
}

}
