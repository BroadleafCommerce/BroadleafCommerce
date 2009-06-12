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

package mx.events
{

import flash.events.Event;

/**
 *  The SliderEvent class represents the event object passed to 
 *  the event listener for the <code>change</code>, <code>thumbDrag</code>, 
 *  <code>thumbPress</code>, and <code>thumbRelease</code> events 
 *  of the HSlider and VSlider classes.
 *
 *  @see mx.controls.HSlider
 *  @see mx.controls.VSlider
 *  @see mx.controls.sliderClasses.Slider
 *  @see mx.events.SliderEventClickTarget
 */
public class SliderEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The <code>SliderEvent.CHANGE</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>change</code> event. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>clickTarget</code></td><td>Specifies whether the slider 
     *       track or a slider thumb was pressed.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>keyCode</code></td><td>If the event was triggered by a key press, 
     *       the keycode for the key.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>thumbIndex</code></td><td>The zero-based index of the thumb
     *       whose position has changed.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>Contains a value indicating the 
     *       type of input action. The value is either <code>InteractionInputType.MOUSE</code> 
     *       or <code>InteractionInputType.KEYBOARD</code>.</td></tr>
     *     <tr><td><code>value</code></td><td>The new value of the slider.</td></tr>
     *  </table>
     *
     *  @eventType change
     */
    public static const CHANGE:String = "change";

    /**
     *  The <code>SliderEvent.THUMB_DRAG</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>thumbDrag</code> event. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>clickTarget</code></td><td>Specifies whether the slider 
     *       track or a slider thumb was pressed.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>keyCode</code></td><td>If the event was triggered by a key press, 
     *       the keycode for the key.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>thumbIndex</code></td><td>The zero-based index of the thumb
     *       whose position has changed.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>Contains a value indicating the 
     *       type of input action. The value is either <code>InteractionInputType.MOUSE</code> 
     *       or <code>InteractionInputType.KEYBOARD</code>.</td></tr>
     *     <tr><td><code>value</code></td><td>The new value of the slider.</td></tr>
     *  </table>
     *
     *  @eventType thumbDrag
     */
    public static const THUMB_DRAG:String = "thumbDrag";

    /**
     *  The <code>SliderEvent.THUMB_PRESS</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>thumbPress</code> event. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>clickTarget</code></td><td>Specifies whether the slider 
     *       track or a slider thumb was pressed.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>keyCode</code></td><td>If the event was triggered by a key press, 
     *       the keycode for the key.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>thumbIndex</code></td><td>The zero-based index of the thumb
     *       whose position has changed.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>Contains a value indicating the 
     *       type of input action. The value is either <code>InteractionInputType.MOUSE</code> 
     *       or <code>InteractionInputType.KEYBOARD</code>.</td></tr>
     *     <tr><td><code>value</code></td><td>The new value of the slider.</td></tr>
     *  </table>
     *
     *  @eventType thumbPress
     */
    public static const THUMB_PRESS:String = "thumbPress";

    /**
     *  The <code>SliderEvent.THUMB_RELEASE</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>thumbRelease</code> event. 
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>clickTarget</code></td><td>Specifies whether the slider 
     *       track or a slider thumb was pressed.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>keyCode</code></td><td>If the event was triggered by a key press, 
     *       the keycode for the key.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>thumbIndex</code></td><td>The zero-based index of the thumb
     *       whose position has changed.</td></tr>
     *     <tr><td><code>triggerEvent</code></td><td>Contains a value indicating the 
     *       type of input action. The value is either <code>InteractionInputType.MOUSE</code> 
     *       or <code>InteractionInputType.KEYBOARD</code>.</td></tr>
     *     <tr><td><code>value</code></td><td>The new value of the slider.</td></tr>
     *  </table>
     *
     *  @eventType thumbRelease
     */
    public static const THUMB_RELEASE:String = "thumbRelease";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble
     *  up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     *
     *  @param thumbIndex The zero-based index of the thumb
     *  whose position has changed.
     *
     *  @param value The new value of the slider.
     *
     *  @param triggerEvent The type of input action. 
     *  The value is either <code>InteractionInputType.MOUSE</code> 
     *  or <code>InteractionInputType.KEYBOARD</code>.
     *
     *  @param clickTarget Whether the slider track or a slider thumb was pressed.
     *
     *  @param keyCode If the event was triggered by a key press, 
     *  the keycode for the key.
     */
    public function SliderEvent(type:String, bubbles:Boolean = false,
                                cancelable:Boolean = false,
                                thumbIndex:int = -1, value:Number = NaN,
                                triggerEvent:Event = null,
                                clickTarget:String = null, keyCode:int = -1)
    {
        super(type, bubbles, cancelable);

        this.thumbIndex = thumbIndex;
        this.value = value;
        this.triggerEvent = triggerEvent;
        this.clickTarget = clickTarget;
        this.keyCode = keyCode;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  clickTarget
    //----------------------------------

    /**
     *  Specifies whether the slider track or a slider thumb was pressed. 
     *  This property can have one of two values: 
     *  <code>SliderEventClickTarget.THUMB</code> 
     *  or <code>SliderEventClickTarget.TRACK</code>.
     *
     *  @see mx.events.SliderEventClickTarget
     */
    public var clickTarget:String;
    
    //----------------------------------
    //  keyCode
    //----------------------------------

    /**
     *  If the event was triggered by a key press, the keycode for the key.
     */
    public var keyCode:int;

    //----------------------------------
    //  thumbIndex
    //----------------------------------

    /**
     *  The zero-based index of the thumb whose position has changed.
     *  If there is only a single thumb, the value is 0.
     *  If there are two thumbs, the value is 0 or 1.
     */
    public var thumbIndex:int;
    
    //----------------------------------
    //  triggerEvent
    //----------------------------------

    /**
     *  Indicates the type of input action. 
     *  The value is either <code>InteractionInputType.MOUSE</code> 
     *  or <code>InteractionInputType.KEYBOARD</code>.
     */
    public var triggerEvent:Event;

    //----------------------------------
    //  value
    //----------------------------------

    /**
     *  The new value of the slider.  
     */
    public var value:Number;
    
    //--------------------------------------------------------------------------
    //
    //  Overridden methods: Event
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function clone():Event
    {
        return new SliderEvent(type, bubbles, cancelable, thumbIndex,
                               value, triggerEvent, clickTarget, keyCode);
    }
}

}
