package mx.events
{

import flash.events.Event;
import mx.effects.IEffectInstance;

/**
 *  Represents event objects that are specific to Flex effects. 
 *  Flex effects dispatch the following types of events:
 *  <ul>
 *    <li><code>effectStart</code></li>
 *    <li><code>effectEnd</code></li>
 *  </ul>
 *
 *  @see mx.effects.Effect
 */
public class EffectEvent extends Event
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
     *  The <code>EffectEvent.EFFECT_END</code> constant defines the value of the 
     *  <code>type</code> property of the event object for an 
     *  <code>effectEnd</code> event. 
	 *
	 *  <p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>effectInstance</code></td><td>The effect instance object 
     *       for the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @see mx.effects.Effect
     *
     *  @eventType effectEnd
	 */
	public static const EFFECT_END:String = "effectEnd";
	
	/**
     *  The <code>EffectEvent.EFFECT_START</code> constant defines the value of the 
     *  <code>type</code> property of the event object for an 
     *  <code>effectStart</code> event. 
     *  
	 *  <p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>effectInstance</code></td><td>The effect instance object 
     *       for the event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @see mx.effects.Effect
     *
     *  @eventType effectStart
	 */
	public static const EFFECT_START:String = "effectStart";
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 *
	 *  @param eventType The event type; indicates the action that caused the event.
	 *
	 *  @param bubbles Specifies whether the event can bubble up the 
	 *  display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior associated with the event can be prevented.
	 *
	 *  @param effectInstance The effect instance that triggered the event.
	 */
	public function EffectEvent(eventType:String, bubbles:Boolean = false,
								cancelable:Boolean = false,
								effectInstance:IEffectInstance = null)
	{
		super(eventType, bubbles, cancelable);

		this.effectInstance = effectInstance;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  effectInstance
	//----------------------------------
	
	/**
	 *  The effect instance object for the event.
	 *  You can use this property to access the properties of the effect
	 *  instance object from within your event listener.
	 */
	public var effectInstance:IEffectInstance;
	
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
		return new EffectEvent(type, bubbles, cancelable, effectInstance);
	}
}

}
