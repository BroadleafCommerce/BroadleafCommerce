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

import flash.events.Event;
import flash.events.EventDispatcher;
import flash.utils.getQualifiedClassName;
import mx.core.IFlexDisplayObject;
import mx.core.mx_internal;
import mx.effects.effectClasses.AddRemoveEffectTargetFilter;
import mx.effects.effectClasses.HideShowEffectTargetFilter;
import mx.effects.effectClasses.PropertyChanges;
import mx.events.EffectEvent;
import mx.managers.LayoutManager;

use namespace mx_internal;

/**
 *  Dispatched when the effect finishes playing,
 *  either when the effect finishes playing or when the effect has 
 *  been interrupted by a call to the <code>end()</code> method.
 *
 *  @eventType mx.events.EffectEvent.EFFECT_END
 */
[Event(name="effectEnd", type="mx.events.EffectEvent")]

/**
 *  Dispatched when the effect starts playing.
 *
 *  @eventType mx.events.EffectEvent.EFFECT_START
 */
[Event(name="effectStart", type="mx.events.EffectEvent")]

/**
 *  The Effect class is an abstract base class that defines the basic 
 *  functionality of all Flex effects.
 *  The Effect class defines the base factory class for all effects.
 *  The EffectInstance class defines the base class for all effect
 *  instance subclasses.
 *
 *  <p>You do not create an instance of the Effect class itself
 *  in an application.
 *  Instead, you create an instance of one of the subclasses,
 *  such as Fade or WipeLeft.</p>
 *  
 *  @mxml
 *
 *  <p>The Effect class defines the following properties,
 *  which all of its subclasses inherit:</p>
 *  
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *    <b>Properties</b>
 *    customFilter=""
 *    duration="500"
 *    filter=""
 *    hideFocusRing="false"
 *    perElementOffset="0"
 *    repeatCount="1"
 *    repeatDelay="0"
 *    startDelay="0"
 *    suspendBackgroundProcessing="false|true"
 *    target="<i>effect target</i>"
 *    targets="<i>array of effect targets</i>"
 *     
 *    <b>Events</b>
 *    effectEnd="<i>No default</i>"
 *    efectStart="<i>No default</i>"
 *  /&gt;
 *  </pre>
 *
 *  @see mx.effects.EffectInstance
 * 
 *  @includeExample examples/SimpleEffectExample.mxml
 */
public class Effect extends EventDispatcher implements IEffect
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class methods
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    private static function mergeArrays(a1:Array, a2:Array):Array
    {
        if (a2)
        {
            for (var i2:int = 0; i2 < a2.length; i2++)
            {
                var addIt:Boolean = true;
                
                for (var i1:int = 0; i1 < a1.length; i1++)
                {
                    if (a1[i1] == a2[i2])
                    {
                        addIt = false;
                        break;
                    }
                }
                
                if (addIt)
                    a1.push(a2[i2]);
            }
        }
        
        return a1;
    }

    /**
     *  @private
     */
    private static function stripUnchangedValues(propChanges:Array):Array
    {
        // Go through and remove any before/after values that are the same.
        for (var i:int = 0; i < propChanges.length; i++)
        {
            for (var prop:Object in propChanges[i].start)
            {
                if ((propChanges[i].start[prop] ==
                     propChanges[i].end[prop]) ||
                    (typeof(propChanges[i].start[prop]) == "number" &&
                     typeof(propChanges[i].end[prop])== "number" &&
                     isNaN(propChanges[i].start[prop]) &&
                     isNaN(propChanges[i].end[prop])))
                {
                    delete propChanges[i].start[prop];
                    delete propChanges[i].end[prop];
                }
            }
        }
            
        return propChanges;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  <p>Starting an effect is usually a three-step process:</p>
     *
     *  <ul>
     *    <li>Create an instance of the effect object
     *    with the <code>new</code> operator.</li>
     *    <li>Set properties on the effect object,
     *    such as <code>duration</code>.</li>
     *    <li>Call the <code>play()</code> method
     *    or assign the effect to a trigger.</li>
     *  </ul>
     *
     *  @param target The Object to animate with this effect.
     */
    public function Effect(target:Object = null)
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
     */
    private var _instances:Array /* of EffectInstance */ = [];
    
    /**
     *  @private
     */
    private var _callValidateNow:Boolean = false;
        
    /**
     *  @private
     */
    private var isPaused:Boolean = false;
    
    /**
     *  @private
     */
    mx_internal var filterObject:EffectTargetFilter;
    
    /**
     *  @private
	 *  Used in applyValueToTarget()
     */
    mx_internal var applyActualDimensions:Boolean = true;
    
    /**
     *  @private
     *  Holds the init object passed in by the Transition.
     */
    mx_internal var propertyChangesArray:Array; 
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  className
    //----------------------------------

    /**
     *  @copy mx.effects.IEffect#className
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
    //  customFilter
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the customFilter property.
     */
    private var _customFilter:EffectTargetFilter;
        
    /**
     *  @copy mx.effects.IEffect#customFilter
     */
    public function get customFilter():EffectTargetFilter
    {
        return _customFilter;
    }

    /**
     *  @private
     */
    public function set customFilter(value:EffectTargetFilter):void
    {
        _customFilter = value;
        mx_internal::filterObject = value;
    }
    
    //----------------------------------
    //  duration
    //----------------------------------

    /**
     *  @private
     *  Storage for the duration property.
     */
    private var _duration:Number = 500;
    
    /**
	 *  @private
	 */
	mx_internal var durationExplicitlySet:Boolean = false;

    [Inspectable(category="General", defaultValue="500")]
    
    /** 
     *  @copy mx.effects.IEffect#duration
     */
    public function get duration():Number
    {
        return _duration;
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
    //  effectTargetHost
    //----------------------------------

    /**
	 *  @private
	 *  Storage for the effectTargetHost property.
	 */
	private var _effectTargetHost:IEffectTargetHost;
    
    /**
     *  @copy mx.effects.IEffect#effectTargetHost
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
    //  endValuesCaptured
    //----------------------------------

    /**
     *  A flag containing <code>true</code> if the end values
	 *  of an effect have already been determined, 
     *  or <code>false</code> if they should be acquired from the
	 *  current properties of the effect targets when the effect runs. 
     *  This property is required by data effects because the sequence
	 *  of setting up the data effects, such as DefaultListEffect
	 *  and DefaultTileListEffect, is more complicated than for
	 *  normal effects.
     *
     *  @default false
     */
    protected var endValuesCaptured:Boolean = false;

    //----------------------------------
    //  filter
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the filter property.
     */
    private var _filter:String;
    
    [Inspectable(category="General", enumeration="add,remove,show,hide,move,resize,addItem,removeItem,replacedItem,replacementItem,none", defaultValue="none")]
     
    /**
     *  @copy mx.effects.IEffect#filter
     */
    public function get filter():String
    {
        return _filter;
    }

    /**
     *  @private
     */
    public function set filter(value:String):void
    {
        if (!customFilter)
        {
            _filter = value;
            
            switch (value)
            {
                case "add":
                case "remove":
                {
                    mx_internal::filterObject =
                        new AddRemoveEffectTargetFilter();
                    AddRemoveEffectTargetFilter(mx_internal::filterObject).add =
                        (value == "add");
                    break;
                }
                
                case "hide":
                case "show":
                {
                    mx_internal::filterObject =
                        new HideShowEffectTargetFilter();
                    HideShowEffectTargetFilter(mx_internal::filterObject).show =
                        (value == "show");
                    break;
                }
                
                case "move":
                {
                    mx_internal::filterObject =
                        new EffectTargetFilter();
                    mx_internal::filterObject.filterProperties =
                        [ "x", "y" ];
                    break;
                }
                
                case "resize":
                {
                    mx_internal::filterObject =
                        new EffectTargetFilter();
                    mx_internal::filterObject.filterProperties =
                        [ "width", "height" ];
                    break;
                }
                
                case "addItem":
                {
                    mx_internal::filterObject = new EffectTargetFilter();
                    mx_internal::filterObject.requiredSemantics = {added:true};
                    break;
                }         

                case "removeItem":
                {
                    mx_internal::filterObject = new EffectTargetFilter();
                    mx_internal::filterObject.requiredSemantics = {removed:true};
                    break;
                }                
                
                case "replacedItem":
                {
                    mx_internal::filterObject = new EffectTargetFilter();
                    mx_internal::filterObject.requiredSemantics = {replaced:true};
                    break;
                }                
                
                case "replacementItem":
                {
                    mx_internal::filterObject = new EffectTargetFilter();
                    mx_internal::filterObject.requiredSemantics = {replacement:true};
                    break;
                }                

                default:
                {
                    mx_internal::filterObject = null;
                    break;          
                }
            }
        }
    }

    //----------------------------------
    //  hideFocusRing
    //----------------------------------
    
	/**
	 *  @private
	 *  Storage for the hideFocusRing property.
	 */
	private var _hideFocusRing:Boolean = false;

    /**
     *  @copy mx.effects.IEffect#hideFocusRing
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
    //  instanceClass
    //----------------------------------

    /**
     *  An object of type Class that specifies the effect
     *  instance class class for this effect class. 
     *  
     *  <p>All subclasses of the Effect class must set this property 
     *  in their constructor.</p>
     */
	public var instanceClass:Class = IEffectInstance;

    
    //----------------------------------
    //  isPlaying
    //----------------------------------

    /**
     *  @copy mx.effects.IEffect#isPlaying
     */
    public function get isPlaying():Boolean
    {
        return _instances && _instances.length > 0;
    }
    
    //----------------------------------
    //  perElementOffset
    //----------------------------------

	/**
	 *  @private
	 *  Storage for the perElementOffset property.
	 */
	private var _perElementOffset:Number = 0;

    [Inspectable(defaultValue="0", category="General", verbose="0")]

    /**
     *  @copy mx.effects.IEffect#perElementOffset
     */ 
    public function get perElementOffset():Number
	{
		return _perElementOffset;
	}

	/**
	 *  @private
	 */
	public function set perElementOffset(value:Number):void
	{
		_perElementOffset = value;
	}
    
    //----------------------------------
    //  relevantProperties
    //----------------------------------
    
    /**
     *  @private
     *  Storage for the relevantProperties property.
     */
    private var _relevantProperties:Array /* of String */;
        
    /**
     *  @copy mx.effects.IEffect#relevantProperties
     */
    public function get relevantProperties():Array /* of String */
    {
        if (_relevantProperties)
            return _relevantProperties;
        else
            return getAffectedProperties();
    }

    /**
     *  @private
     */
    public function set relevantProperties(value:Array /* of String */):void
    {
        _relevantProperties = value;
    }
    
    //----------------------------------
    //  relevantStyles
    //----------------------------------
    
    /**
     *  @private
	 *  Storage for the relevantStyles property.
     */
    private var _relevantStyles:Array /* of String */ = [];
        
    /**
     *  @copy mx.effects.IEffect#relevantStyles
     */
    public function get relevantStyles():Array /* of String */
    {
        return _relevantStyles;
    }

    /**
     *  @private
     */
    public function set relevantStyles(value:Array /* of String */):void
    {
        _relevantStyles = value;
    }
    
    //----------------------------------
    //  repeatCount
    //----------------------------------

    [Inspectable(category="General", defaultValue="1")]

    /**
     *  Number of times to repeat the effect.
     *  Possible values are any integer greater than or equal to 0.
     *  A value of 1 means to play the effect once.
     *  A value of 0 means to play the effect indefinitely
     *  until stopped by a call to the <code>end()</code> method.
     *
     *  @default 1
     */
    public var repeatCount:int = 1;
        
    
    //----------------------------------
    //  repeatDelay
    //----------------------------------

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Amount of time, in milliseconds, to wait before repeating the effect.
     *  Possible values are any integer greater than or equal to 0.
     *
     *  @default 0
     */
    public var repeatDelay:int = 0;


    //----------------------------------
    //  startDelay
    //----------------------------------

    [Inspectable(category="General", defaultValue="0")]

    /**
     *  Amount of time, in milliseconds, to wait before starting the effect.
     *  Possible values are any int greater than or equal to 0.
     *  If the effect is repeated by using the <code>repeatCount</code>
     *  property, the <code>startDelay</code> is only applied
     *  to the first time the effect is played.
     *
     *  @default 0
     */
    public var startDelay:int = 0;

    //----------------------------------
    //  suspendBackgroundProcessing
    //----------------------------------

    /**
     *  If <code>true</code>, blocks all background processing
     *  while the effect is playing.
     *  Background processing includes measurement, layout, and
     *  processing responses that have arrived from the server.
     *  The default value is <code>false</code>.
     *
     *  <p>You are encouraged to set this property to
     *  <code>true</code> in most cases, because it improves
     *  the performance of the application.
     *  However, the property should be set to <code>false</code>
     *  if either of the following is true:</p>
     *  <ul>
     *    <li>User input may arrive while the effect is playing,
     *    and the application must respond to the user input
     *    before the effect finishes playing.</li>
     *    <li>A response may arrive from the server while the effect
     *    is playing, and the application must process the response
     *    while the effect is still playing.</li>
     *  </ul>
     *
     *  @default false
     */
    public var suspendBackgroundProcessing:Boolean = false;


    //----------------------------------
    //  target
    //----------------------------------

    /** 
     *  @copy mx.effects.IEffect#target
     */
    public function get target():Object
    {
        if (_targets.length > 0)
            return _targets[0]; 
        else
            return null;
    }
    
    /**
     *  @private
     */
    public function set target(value:Object):void
    {
        _targets.splice(0);
        
        if (value)
            _targets[0] = value;
    }

    //----------------------------------
    //  targets
    //----------------------------------

    /**
     *  @private
     *  Storage for the targets property.
     */
    private var _targets:Array = [];
    
    /**
     *  @copy mx.effects.IEffect#targets
     */
    public function get targets():Array
    {
        return _targets;
    }

    /**
     *  @private
     */
    public function set targets(value:Array):void
    {
        // Strip out null values.
        // Binding will trigger again when the null targets are created.
        var n:int = value.length;
        for (var i:int = n - 1; i > 0; i--)
        {
            if (value[i] == null)
                value.splice(i,1);
        }

        _targets = value;
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
     *  @copy mx.effects.IEffect#triggerEvent
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
     *  @copy mx.effects.IEffect#getAffectedProperties()
     */
    public function getAffectedProperties():Array /* of String */
    {
        // Every subclass should override this method.
        return [];
    }
    
    /**
     *  @copy mx.effects.IEffect#createInstances()
     */
    public function createInstances(targets:Array = null):Array /* of EffectInstance */
    {
        if (!targets)
            targets = this.targets;
            
        var newInstances:Array = [];
        
        // Multiple target support
        var n:int = targets.length;
        var offsetDelay:Number = 0;
        
        for (var i:int = 0; i < n; i++) 
        {
            var newInstance:IEffectInstance = createInstance(targets[i]);
            
            if (newInstance)
            {
                newInstance.startDelay += offsetDelay;
                offsetDelay += perElementOffset;
                newInstances.push(newInstance);
            }
        }
        
        triggerEvent = null;
        
        return newInstances; 
    }

    /**
     *  @copy mx.effects.IEffect#createInstance()
     */
    public function createInstance(target:Object = null):IEffectInstance
    {       
        if (!target)
            target = this.target;
        
        var newInstance:IEffectInstance = null;
        var props:PropertyChanges = null;
        var create:Boolean = true;
        var setPropsArray:Boolean = false;
                
        if (mx_internal::propertyChangesArray)
        {
            setPropsArray = true;
            create = filterInstance(mx_internal::propertyChangesArray,
                                    target);    
        }
         
        if (create) 
        {
            newInstance = IEffectInstance(new instanceClass(target))
            
            initInstance(newInstance);
            
            if (setPropsArray)
            {
                var n:int = mx_internal::propertyChangesArray.length;
                for (var i:int = 0; i < n; i++)
                {
                    if (mx_internal::propertyChangesArray[i].target == target)
                    {
                        newInstance.propertyChanges =
                            mx_internal::propertyChangesArray[i];
                    }
                }
            }
                
            EventDispatcher(newInstance).addEventListener(EffectEvent.EFFECT_START, effectStartHandler);
            EventDispatcher(newInstance).addEventListener(EffectEvent.EFFECT_END, effectEndHandler);
            
            _instances.push(newInstance);
            
            if (triggerEvent)
                newInstance.initEffect(triggerEvent);
        }
        
        return newInstance;
    }

    /**
     *  Copies properties of the effect to the effect instance. 
     *
     *  <p>Flex calls this method from the <code>Effect.createInstance()</code>
     *  method; you do not have to call it yourself. </p>
     *
     *  <p>When you create a custom effect, override this method to 
     *  copy properties from the Effect class to the effect instance class. 
     *  In your override, you must call <code>super.initInstance()</code>. </p>
     *
     *  @param EffectInstance The effect instance to initialize.
     */
    protected function initInstance(instance:IEffectInstance):void
    {
        instance.duration = duration;
        Object(instance).durationExplicitlySet = durationExplicitlySet;
        instance.effect = this;
		instance.effectTargetHost = effectTargetHost;
		instance.hideFocusRing = hideFocusRing;
        instance.repeatCount = repeatCount;
        instance.repeatDelay = repeatDelay;
        instance.startDelay = startDelay;
        instance.suspendBackgroundProcessing = suspendBackgroundProcessing;
    }
    
    /**
     *  @copy mx.effects.IEffect#deleteInstance()
     */
    public function deleteInstance(instance:IEffectInstance):void
    {
        EventDispatcher(instance).removeEventListener(
			EffectEvent.EFFECT_START, effectStartHandler);
        EventDispatcher(instance).removeEventListener(
			EffectEvent.EFFECT_END, effectEndHandler);
        
        var n:int = _instances.length;
        for (var i:int = 0; i < n; i++)
        {
            if (_instances[i] === instance)
                _instances.splice(i, 1);
        }
    }

    /**
     *  @copy mx.effects.IEffect#play()
     */ 
    public function play(targets:Array = null,
                         playReversedFromEnd:Boolean = false):
                         Array /* of EffectInstance */
    {
        // If we have a propertyChangesArray, capture the current values
        // if they haven't been captured already, strip out any unchanged 
        // values, then apply the start values.
        if (targets == null && mx_internal::propertyChangesArray != null)
        {
            if (_callValidateNow)
                LayoutManager.getInstance().validateNow();
            
            if (!endValuesCaptured)
                mx_internal::propertyChangesArray =
                    captureValues(mx_internal::propertyChangesArray, false);
            
            mx_internal::propertyChangesArray =
                stripUnchangedValues(mx_internal::propertyChangesArray);
            
            applyStartValues(mx_internal::propertyChangesArray,
                             this.targets);
        }
        
        var newInstances:Array = createInstances(targets);
                
        var n:int = newInstances.length;
        for (var i:int = 0; i < n; i++) 
        {
            var newInstance:IEffectInstance = IEffectInstance(newInstances[i]);

            Object(newInstance).playReversed = playReversedFromEnd;
            
            newInstance.startEffect();
        }
        
        return newInstances; 
    }
    
    /**
     *  @copy mx.effects.IEffect#pause()
     */
    public function pause():void
    {   
        if (isPlaying && !isPaused)
        {
            isPaused = true;
            
            var n:int = _instances.length;
            for (var i:int = 0; i < n; i++)
            {
                IEffectInstance(_instances[i]).pause();
            }       
        }
    }

    /**
     *  @copy mx.effects.IEffect#stop()
     */
    public function stop():void
    {   
        var n:int = _instances.length;
        for (var i:int = n; i >= 0; i--)
        {
            var instance:IEffectInstance = IEffectInstance(_instances[i]);
            if (instance)
                instance.stop();
        }
    }
    
    /**
     *  @copy mx.effects.IEffect#resume()
     */
    public function resume():void
    {
        if (isPlaying && isPaused)
        {
            isPaused = false;
            var n:int = _instances.length;
            for (var i:int = 0; i < n; i++)
            {
                IEffectInstance(_instances[i]).resume();
            }
        }
    }
        
    /**
     *  @copy mx.effects.IEffect#reverse()
     */
    public function reverse():void
    {
        if (isPlaying)
        {
            var n:int = _instances.length;
            for (var i:int = 0; i < n; i++)
            {
                IEffectInstance(_instances[i]).reverse();
            }
        }
    }
    
    /**
     *  @copy mx.effects.IEffect#end()
     */
    public function end(effectInstance:IEffectInstance = null):void
    {
        if (effectInstance)
        {
            effectInstance.end();
        }
        else
        {
            var n:int = _instances.length;
            for (var i:int = n; i >= 0; i--)
            {
                var instance:IEffectInstance = IEffectInstance(_instances[i]);
                if (instance)
                    instance.end();
            }
        }
    }
    
    /**
     *  Determines the logic for filtering out an effect instance.
     *  The CompositeEffect class overrides this method.
     *
     *  @param propChanges The properties modified by the effect.
     *
     *  @param targ The effect target.
     *
     *  @return Returns <code>true</code> if the effect instance should play.
     */
    protected function filterInstance(propChanges:Array, target:Object):Boolean 
    {
        if (mx_internal::filterObject)
            return mx_internal::filterObject.filterInstance(propChanges, effectTargetHost, target);
        
        return true;
    }
    
    /**
     *  @copy mx.effects.IEffect#captureStartValues()
     */
    public function captureStartValues():void
    {       
        if (targets.length > 0)
        {
            // Reset the PropertyChanges array.
            mx_internal::propertyChangesArray = [];
            _callValidateNow = true;
                        
            // Create a new PropertyChanges object for the sum of all targets.
            var n:int = targets.length;
            for (var i:int = 0; i < n; i++)
            {
                mx_internal::propertyChangesArray.push(
                    new PropertyChanges(targets[i]));
            }
            
            mx_internal::propertyChangesArray =
                captureValues(mx_internal::propertyChangesArray,true);
        }
        endValuesCaptured = false;
    }

    /**
     *  @copy mx.effects.IEffect#captureMoreStartValues()
     */
    public function captureMoreStartValues(targets:Array):void
    {       
        if (targets.length > 0)
        {
            // make temporary PropertyChangesArray
            var additionalPropertyChangesArray:Array = [];
            
            // Create a new PropertyChanges object for the sum of all targets.
            for (var i:int = 0; i < targets.length; i++)
                additionalPropertyChangesArray.push(new PropertyChanges(targets[i]));
            
            additionalPropertyChangesArray = captureValues(additionalPropertyChangesArray,true);
            
            mx_internal::propertyChangesArray = 
                mx_internal::propertyChangesArray.concat(additionalPropertyChangesArray);
        }
    }
    
    /**
     *  @copy mx.effects.IEffect#captureEndValues()
     */
    public function captureEndValues():void
    {
        mx_internal::propertyChangesArray =
            captureValues(mx_internal::propertyChangesArray,false);
        endValuesCaptured = true;
    }
        
    /**
     *  @private
     *  Used internally to grab the values of the relevant properties
     */
    mx_internal function captureValues(propChanges:Array,
									   setStartValues:Boolean):Array
    {
        // Merge Effect.filterProperties and filterObject.filterProperties
        var effectProps:Array = !mx_internal::filterObject ?
                                relevantProperties :
                                mergeArrays(relevantProperties,
                                mx_internal::filterObject.filterProperties);
        
        var valueMap:Object;
        var target:Object;      
        var n:int;
        var i:int;
        var m:int;  
        var j:int;
        
        // For each target, grab the property's value
        // and put it into the propChanges Array. 
        // Walk the targets.
        if (effectProps && effectProps.length > 0)
        {
            n = propChanges.length;
            for (i = 0; i < n; i++)
            {
                target = propChanges[i].target;
                valueMap = setStartValues ? propChanges[i].start : propChanges[i].end;
                                        
                // Walk the properties in the target
                m = effectProps.length;
                for (j = 0; j < m; j++)
                {
                    valueMap[effectProps[j]] = getValueFromTarget(target,effectProps[j]);
                }
            }
        }
        
        var styles:Array = !mx_internal::filterObject ?
                           relevantStyles :
                           mergeArrays(relevantStyles,
                           mx_internal::filterObject.filterStyles);
        
        if (styles && styles.length > 0)
        {         
            n = propChanges.length;
            for (i = 0; i < n; i++)
            {
                target = propChanges[i].target;
                valueMap = setStartValues ? propChanges[i].start : propChanges[i].end;
                                        
                // Walk the properties in the target.
                m = styles.length;
                for (j = 0; j < m; j++)
                {
                    valueMap[styles[j]] = target.getStyle(styles[j]);
                }
            }
        }
        
        return propChanges;
    }
    
    /**
     *  Called by the <code>captureStartValues()</code> method to get the value
     *  of a property from the target.
     *  This function should only be called internally
     *  by the effects framework.
     *  The default behavior is to simply return <code>target[property]</code>.
     *  Effect developers can override this function
     *  if you need a different behavior. 
     *
     *  @param target The effect target.
     *
     *  @param property The target property.
     *
     *  @return The value of the target property. 
     */
    protected function getValueFromTarget(target:Object, property:String):*
    {
        if (property in target)
            return target[property];
        
        return undefined;
    }
    
    /**
     *  @private
     *  Applies the start values found in the array of PropertyChanges
     *  to the relevant targets.
     */
    mx_internal function applyStartValues(propChanges:Array,
                                     targets:Array):void
    {
        var effectProps:Array = relevantProperties;
                    
        var n:int = propChanges.length;
        for (var i:int = 0; i < n; i++)
        {
            var m:int;
            var j:int;

            var target:Object = propChanges[i].target;
            var apply:Boolean = false;
            
            m = targets.length;
            for (j = 0; j < m; j++)
            {
                if (targets[j] == target)
                {   
                    apply = filterInstance(propChanges, target);
                    break;
                }
            }
            
            if (apply)
            {
                // Walk the properties in the target
                m = effectProps.length;
                for (j = 0; j < m; j++)
                {
                    if (effectProps[j] in propChanges[i].start &&
                        effectProps[j] in target)
                    {
                        applyValueToTarget(target, effectProps[j],
                                propChanges[i].start[effectProps[j]],
                                propChanges[i].start);
                    }
                }
                
                // Walk the styles in the target
                m = relevantStyles.length;
                for (j = 0; j < m; j++)
                {
                    if (relevantStyles[j] in propChanges[i].start)
                        target.setStyle(relevantStyles[j], propChanges[i].start[relevantStyles[j]]);
                }
            }
        }
    }
    
    /**
     *  Used internally by the Effect infrastructure.
     *  If <code>captureStartValues()</code> has been called,
     *  then when Flex calls the <code>play()</code> method, it uses this function
     *  to set the targets back to the starting state.
     *  The default behavior is to take the value captured
     *  using the <code>getValueFromTarget()</code> method
     *  and set it directly on the target's property. For example: <pre>
     *  
     *  target[property] = value;</pre>
     *
     *  <p>Only override this method if you need to apply
     *  the captured values in a different way.
     *  Note that style properties of a target are set
     *  using a different mechanism.
     *  Use the <code>relevantStyles</code> property to specify
     *  which style properties to capture and apply. </p>
     *
     *  @param target The effect target.
     *
     *  @param property The target property.
     *
     *  @param value The value of the property. 
     *
     *  @param props Array of Objects, where each Array element contains a 
     *  <code>start</code> and <code>end</code> Object
     *  for the properties that the effect is monitoring. 
     */
    protected function applyValueToTarget(target:Object, property:String, 
                                          value:*, props:Object):void
    {
        if (property in target)
        {
            // The "property in target" test only tells if the property exists
            // in the target, but does not distinguish between read-only and
            // read-write properties. Put a try/catch around the setter and 
            // ignore any errors.
            try
            {
                
                if (applyActualDimensions &&
                    target is IFlexDisplayObject &&
                    property == "height")
                {
                    target.setActualSize(target.width,value);
                }
                else if (applyActualDimensions &&
                         target is IFlexDisplayObject &&
                         property == "width")
                {
                    target.setActualSize(value,target.height);
                }
                else
                {
                    target[property] = value;
                }
            }
            catch(e:Error)
            {
                // Ignore errors
            }
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  This method is called when the effect instance starts playing. 
     *  If you override this method, ensure that you call the super method. 
     *
     *  @param event An event object of type EffectEvent.
     */
    protected function effectStartHandler(event:EffectEvent):void 
    {
        dispatchEvent(event);
    }
    
    /**
     *  Called when an effect instance has finished playing. 
     *  If you override this method, ensure that you call the super method.
     *
     *  @param event An event object of type EffectEvent.
     */
    protected function effectEndHandler(event:EffectEvent):void 
    {   
        var instance:IEffectInstance = IEffectInstance(event.effectInstance);
        
        deleteInstance(instance);

        dispatchEvent(event);
    }

}

}
