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
import mx.effects.effectClasses.MaskEffectInstance;
import mx.events.TweenEvent;


/**
 *  Dispatched when the effect starts, which corresponds to the 
 *  first call to the <code>onMoveTweenUpdate()</code> 
 *  and <code>onScaleTweenUpdate()</code> methods. 
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
 *  Dispatched every time the effect updates the target. 
 *  The dispatching of this event corresponds to the 
 *  calls to the <code>onMoveTweenUpdate()</code> 
 *  and <code>onScaleTweenUpdate()</code> methods.
 *
 *  @eventType mx.events.TweenEvent.TWEEN_UPDATE
 */
[Event(name="tweenUpdate", type="mx.events.TweenEvent")]

/**
 *  Dispatched when the effect ends.
 *
 *  <p>When an effect plays a single time, this event occurs
 *  at the same time as an <code>effectEnd</code> event.
 *  If you configure the effect to repeat, 
 *  it occurs at the end of every repetition of the effect,
 *  and the <code>endEffect</code> event occurs
 *  after the effect plays for the final time.</p>
 *
 *  @eventType mx.events.TweenEvent.TWEEN_END
 */
[Event(name="tweenEnd", type="mx.events.TweenEvent")]

/**
 *  The MaskEffect class is an abstract base class for all effects 
 *  that animate a mask, such as the wipe effects and the Iris effect. 
 *  This class encapsulates methods and properties that are common
 *  among all mask-based effects.
 *
 *  <p>A mask effect uses an overlay, called a mask, to perform the effect. 
 *  By default, the mask is a rectangle with the same size
 *  as the target component. </p>
 *
 *  <p>The before or after state of the target component of a mask effect
 *  must be invisible.
 *  That means a mask effect always makes a target component appear on 
 *  the screen, or disappear from the screen.</p>
 *
 *  <p>You use the <code>scaleXFrom</code>, <code>scaleYFrom</code>, 
 *  <code>scaleXTo</code>, and <code>scaleX</code> properties to specify 
 *  the initial and final scale of the mask, where a value of 1.0 corresponds
 *  to scaling the mask to the size of the target component, 2.0 scales 
 *  the mask to twice the size of the component, 0.5 scales the mask to half 
 *  the size of the component, and so on. 
 *  To use any one of these properties, you must specify all four.</p>
 *
 *  <p>You use the <code>xFrom</code>, <code>yFrom</code>, <code>xTo</code>, 
 *  and <code>yTo</code> properties to specify the coordinates of the initial
 *  position and final position of the mask relative to the target component, 
 *  where (0, 0) corresponds to the upper left corner of the target. 
 *  To use any one of these properties, you must specify all four.</p>
 *
 *  <p>The coordinates of the initial and final position of the mask
 *  depend on the type of effect and whether the <code>show</code> property
 *  is <code>true</code> or <code>false</code>.
 *  For example, for the WipeLeft effect with a <code>show</code> value of
 *  <code>false</code>, the coordinates of the initial mask position
 *  are (0, 0),corresponding to the upper-left corner of the target, 
 *  and the coordinates of the final position are the upper-right corner
 *  of the target (width, 0), where width is the width of the target.</p>
 * 
 *  <p>For a <code>show</code> value of <code>true</code> for the WipeLeft
 *  effect, the coordinates of the initial mask position are (width, 0),
 *  and the coordinates of the final position are (0, 0).</p>
 *
 *  @mxml
 *
 *  <p>The MaskEffect class defines the following properties, 
 *  which all of its subclasses inherit:</p>
 *  
 *  <pre>
 *  &lt;mx:<i>tagname</i>
 *    createMaskFunction=""
 *    moveEasingFunction=""
 *    scaleEasingFunction=""
 *    scaleXFrom=""
 *    scaleXTo=""
 *    scaleYFrom=""
 *    scaleYTo=""
 *    show="true|false"
 *    xFrom=""
 *    xTo=""
 *    yFrom=""
 *    yTo=""
 *  /&gt;
 *  </pre>
 *  
 *  @see mx.effects.effectClasses.MaskEffectInstance
 *  @see mx.effects.TweenEffect
 */
public class MaskEffect extends Effect
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private static var AFFECTED_PROPERTIES:Array = [ "visible" ];

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
    public function MaskEffect(target:Object = null)
    {
        super(target);

        instanceClass = MaskEffectInstance;
        hideFocusRing = true;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------
 
    //----------------------------------
    //  createMaskFunction
    //----------------------------------
    
    /**
     *  Function called when the effect creates the mask.
     *  The default value is a function that returns a Rectangle
     *  with the same dimensions as the target. 
     *
     *  <p>The custom mask function has the following signature:</p>
     *
     *  <pre>
     *  public function createLargeMask(targ:Object, bounds:Rectangle):Shape
     *  {
     *      var myMask:Shape = new FlexShape();
     *
     *      // Create mask.
     *
     *      return myMask;
     *  }
     *  </pre>
     *
     *  <p>Your custom mask function takes an argument
     *  corresponding to the target component of the effect, 
     *  and a second argument that defines the dimensions of the 
     *  target so that you can correctly size the mask. 
     *  You use that argument to access properties of the target
     *  component, such as <code>width</code> and <code>height</code>,
     *  so that you can create a mask with the correct size.</p>
     *
     *  <p>The function returns a single Shape object
     *  that defines the mask.</p>   
     */
    public var createMaskFunction:Function;
    
    //----------------------------------
    //  moveEasingFunction
    //----------------------------------

    /**
     *  Easing function to use for moving the mask.
     *  @default null
     */     
    public var moveEasingFunction:Function;
    
    //----------------------------------
    //  persistAfterEnd
    //----------------------------------

    [Inspectable(category="General", format="Boolean", defaultValue="false")]

    /** 
     *  @private
     *  Flag indicating whether the mask is removed automatically
     *  when the effect finishes. If false, it is removed.
     *
     *  @default true
     */
    mx_internal var persistAfterEnd:Boolean = false;

    //----------------------------------
    //  scaleEasingFunction
    //----------------------------------

    /**
     *  Easing function to use for scaling the mask.
     *  @default null
     */ 
    public var scaleEasingFunction:Function;
    
    //----------------------------------
    //  scaleXFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /**
     *  Initial scaleX for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>scaleXFrom</code>, <code>scaleYFrom</code>, 
     *  <code>scaleXTo</code>, and <code>scaleX</code>.</p>
     */
    public var scaleXFrom:Number;
    
    //----------------------------------
    //  scaleXTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /** 
     *  Ending scaleX for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>scaleXFrom</code>, <code>scaleYFrom</code>, 
     *  <code>scaleXTo</code>, and <code>scaleX</code>.</p>
     */
    public var scaleXTo:Number;
    
    //----------------------------------
    //  scaleYFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /** 
     *  Initial scaleY for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>scaleXFrom</code>, <code>scaleYFrom</code>, 
     *  <code>scaleXTo</code>, and <code>scaleX</code>.</p>
     */
    public var scaleYFrom:Number;
    
    //----------------------------------
    //  scaleYTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /**
     *  Ending scaleY for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>scaleXFrom</code>, <code>scaleYFrom</code>, 
     *  <code>scaleXTo</code>, and <code>scaleX</code>.</p>
     */
    public var scaleYTo:Number;

    //----------------------------------
    //  showTarget
    //----------------------------------
 

    /**
     *  @private
     *  Storage for the showTarget property.
     */
    private var _showTarget:Boolean = true;
    
    /**
     *  @private
     */
    private var _showExplicitlySet:Boolean = false;

    [Inspectable(category="General", defaultValue="true")]

    /**
     *  Specifies that the target component is becoming visible, 
     *  <code>true</code>, or invisible, <code>false</code>. 
     *
     *  If you specify this effect for a <code>showEffect</code> or 
     *  <code>hideEffect</code> trigger, Flex sets the 
     *  <code>showTarget</code> property for you, either to 
     *  <code>true</code> if the component becomes visible, 
     *  or <code>false</code> if the component becomes invisible. 
     *  If you use this effect with a different effect trigger, 
     *  you should set it yourself, often within the 
     *  event listener for the <code>startEffect</code> event.
     *
     *  @default true
     */
    public function get showTarget():Boolean
    {
        return _showTarget;
    }

    /**
     *  @private
     */
    public function set showTarget(value:Boolean):void
    {
        _showTarget = value;
        _showExplicitlySet = true;
    }
    
    //----------------------------------
    //  xFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /** 
     *  Initial position's x coordinate for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>xFrom</code>, <code>yFrom</code>, <code>xTo</code>, 
     *  and <code>yTo</code>. </p>
     */
    public var xFrom:Number;
    
    //----------------------------------
    //  xTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /** 
     *  Destination position's x coordinate for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>xFrom</code>, <code>yFrom</code>, <code>xTo</code>, 
     *  and <code>yTo</code>. </p>
     */
    public var xTo:Number;
    
    //----------------------------------
    //  yFrom
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /**
     *  Initial position's y coordinate for mask. 
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>xFrom</code>, <code>yFrom</code>, <code>xTo</code>, 
     *  and <code>yTo</code>. </p>
     */
    public var yFrom:Number;
    
    //----------------------------------
    //  yTo
    //----------------------------------

    [Inspectable(category="General", defaultValue="NaN")]

    /** 
     *  Destination position's y coordinate for mask.
     *
     *  <p>To specify this property,
     *  you must specify all four of these properties:
     *  <code>xFrom</code>, <code>yFrom</code>, <code>xTo</code>, 
     *  and <code>yTo</code>. </p>
     */
    public var yTo:Number;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden properties
    //
    //--------------------------------------------------------------------------
    
    /**
     *  @private
     */
    override public function set hideFocusRing(value:Boolean):void
    {
        super.hideFocusRing = value;
    }
    
    /**
     *  @private
     */
    override public function get hideFocusRing():Boolean
    {
        return super.hideFocusRing;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods
    //
    //--------------------------------------------------------------------------

    /**
     *  Returns the component properties modified by this effect. 
     *  This method returns an Array containing: 
     *  <code>[ "visible", "width", "height" ]</code>.
     *  Since the WipeDown, WipeLeft, WipeRight, and WipeDown effect
     *  subclasses all modify these same  properties, those classes 
     *  do not implement this method. 
     * 
     *  <p>If you subclass the MaskEffect class to create a custom effect, 
     *  and it modifies a different set of properties on the target, 
     *  you must override this method 
     *  and return an Array containing a list of the properties 
     *  modified by your subclass.</p>
     *
     *  @return An Array of Strings specifying the names of the 
     *  properties modified by this effect.
     *
     *  @see mx.effects.Effect#getAffectedProperties()
     */
    override public function getAffectedProperties():Array /* of String */
    {
        return AFFECTED_PROPERTIES;
    }

    /**
     *  @private
     */
    override protected function initInstance(instance:IEffectInstance):void
    {
        super.initInstance(instance);

        var maskEffectInstance:MaskEffectInstance = MaskEffectInstance(instance);
        
        if (_showExplicitlySet)
            maskEffectInstance.showTarget = showTarget;
        maskEffectInstance.xFrom = xFrom;
        maskEffectInstance.yFrom = yFrom;
        maskEffectInstance.xTo = xTo;
        maskEffectInstance.yTo = yTo;
        maskEffectInstance.scaleXFrom = scaleXFrom;
        maskEffectInstance.scaleXTo = scaleXTo;
        maskEffectInstance.scaleYFrom = scaleYFrom;
        maskEffectInstance.scaleYTo = scaleYTo;
        maskEffectInstance.moveEasingFunction = moveEasingFunction;
        maskEffectInstance.scaleEasingFunction = scaleEasingFunction;
        maskEffectInstance.createMaskFunction = createMaskFunction;
        maskEffectInstance.mx_internal::persistAfterEnd = mx_internal::persistAfterEnd;
        
        EventDispatcher(maskEffectInstance).addEventListener(TweenEvent.TWEEN_START, tweenEventHandler);    
        EventDispatcher(maskEffectInstance).addEventListener(TweenEvent.TWEEN_UPDATE, tweenEventHandler);       
        EventDispatcher(maskEffectInstance).addEventListener(TweenEvent.TWEEN_END, tweenEventHandler);
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
