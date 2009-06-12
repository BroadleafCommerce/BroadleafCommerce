////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2005-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events 
{

import flash.events.Event;
import mx.core.mx_internal;

use namespace mx_internal;

/**
 *  The VideoEvent class represents the event object passed to the event listener for 
 *  events dispatched by the VideoDisplay control, and defines the values of 
 *  the <code>VideoDisplay.state</code> property.
 *
 *  @see mx.controls.VideoDisplay
 */
public class VideoEvent extends Event 
{
    include "../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------
	
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  immediately after a call to the 
     *  <code>play()</code> or <code>load()</code> method.
     *
     *  <p>This is a responsive state. In the responsive state, calls to 
     *  the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
     *  and <code>pause()</code> methods are executed immediately.</p>
     */	
	public static const BUFFERING:String = "buffering";

	/**
	 *  The <code>VideoEvent.CLOSE</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>close</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType close
	 */
	public static const CLOSE:String = "close";
	
    /**
	 *  The <code>VideoEvent.COMPLETE</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>complete</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType complete
     */    
    public static const COMPLETE:String = "complete"; 

    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when the VideoDisplay control was unable to load the video stream. 
     *  This state can occur when there is no connection to a server, 
     *  the video stream is not found, or for other reasons.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const CONNECTION_ERROR:String = "connectionError";
	
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when the video stream has timed out or is idle.
     *
     *  <p>This is a responsive state. In the responsive state, calls to 
     *  the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
     *  and <code>pause()</code> methods are executed immediately.</p>
     */
	public static const DISCONNECTED:String = "disconnected";

    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  during execution of queued command. 
     *  There will never be a <code>stateChange</code> event dispatched for
     *  this state; it is for internal use only.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const EXEC_QUEUED_CMD:String = "execQueuedCmd";

    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  immediately after a call to the 
     *  <code>play()</code> or <code>load()</code> method.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const LOADING:String = "loading";
	
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when an FLV file is loaded, but play is paused. 
     *  This state is entered when you call the <code>pause()</code> 
     *  or <code>load()</code> method.
     *
     *  <p>This is a responsive state. In the responsive state, calls to 
     *  the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
     *  and <code>pause()</code> methods are executed immediately.</p>
     */ 
    public static const PAUSED:String = "paused";
     
    /**
	 *  The <code>VideoEvent.PLAYHEAD_UPDATE</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>playheadUpdate</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType playheadUpdate
     */    
    public static const PLAYHEAD_UPDATE:String = "playheadUpdate"; 
    
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when an FLV file is loaded and is playing. 
     *  This state is entered when you call the <code>play()</code> 
     *  method.
     *
     *  <p>This is a responsive state. In the responsive state, calls to 
     *  the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
     *  and <code>pause()</code> methods are executed immediately.</p>
     */		
    public static const PLAYING:String = "playing";
    
    /**
	 *  The <code>VideoEvent.READY</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>ready</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType ready
     */    		
    public static const READY:String = "ready";

    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when the VideoDisplay control is resizing.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const RESIZING:String = "resizing";
	
    /**
	 *  The <code>VideoEvent.REWIND</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>rewind</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType rewind
     */    		
    public static const REWIND:String = "rewind";

    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  during an autorewind triggered
     *  when play stops.  After the rewind completes, the state changes to 
     *  <code>STOPPED</code>.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const REWINDING:String = "rewinding";
	
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  for a seek occurring 
     *  due to the <code>VideoDisplay.playHeadTime</code> property being set.
     *
     *  <p>This is a unresponsive state. If the control is unresponsive, calls to the 
	 *  <code>play()</code>, <code>load()</code>, <code>stop()</code>,
	 *  and <code>pause()</code> methods are queued, 
	 *  and then executed when the control changes to the responsive state.</p>
     */
    public static const SEEKING:String = "seeking";	
     
    /**
	 *  The <code>VideoEvent.STATE_CHANGE</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>stateChange</code> event.
	 * 
     *	<p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>playheadTime</code></td><td>The location of the playhead 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>state</code></td><td>The value of the 
     *       <code>VideoDisplay.state</code> property when the event occurs.</td></tr>
     *     <tr><td><code>stateResponsive</code></td><td>The value of the 
     *       <code>VideoDisplay.stateResponsive</code> property 
     *       when the event occurs.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
	 *  </table>
	 *
	 *  @eventType stateChange
	 */    		
    public static const STATE_CHANGE:String = "stateChange";
     
    /**
     *  The value of the <code>VideoDisplay.state</code> property 
     *  when an FLV file is loaded but play has stopped. 
     *  This state is entered  when you call the <code>stop()</code> method
     *  or when the playhead reaches the end of the video stream.
     *
     *  <p>This is a responsive state. In the responsive state, calls to 
     *  the <code>play()</code>, <code>load()</code>, <code>stop()</code>, 
     *  and <code>pause()</code> methods are executed immediately.</p>
     */	
    public static const STOPPED:String = "stopped";
    
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
	 *  @param bubbles Specifies whether the event can bubble up the display list hierarchy.
	 *
	 *  @param cancelable Specifies whether the behavior associated with 
	 *  the event can be prevented.
	 *
	 *  @param state The value of the <code>VideoDisplay.state</code> property 
	 *  when the event occurs.
	 *
	 *  @param playeheadTime The location of the playhead when the event occurs. 	
     */
	public function VideoEvent(type:String, bubbles:Boolean = false,
							   cancelable:Boolean = false,
							   state:String = null, playheadTime:Number = NaN) 
	{
		super(type, bubbles, cancelable);

		this.state = state;
		this.playheadTime = playheadTime;
	}
	
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
    //  playheadTime
    //----------------------------------

    /**
	 *  The location of the playhead of the VideoDisplay control 
	 *  when the event occurs.
     */   
    public var playheadTime:Number;

	//----------------------------------
    //  state
    //----------------------------------

    /**
	 *  The value of the <code>VideoDisplay.state</code> property 
	 *  when the event occurs.
	 *
	 *  @see mx.controls.VideoDisplay#state
     */      
	public var state:String;

	//----------------------------------
    //  stateResponsive
    //----------------------------------

	/**
	 *  The value of the <code>VideoDisplay.stateResponsive</code> property 
	 *  when the event occurs.
	 *
	 *  @see mx.controls.VideoDisplay#stateResponsive
	 */
	public function get stateResponsive():Boolean
	{
		switch (state) 
		{
			case DISCONNECTED:
			case STOPPED:
			case PLAYING:
			case PAUSED:
			case BUFFERING:
			{
				return true;
			}

			default:
			{
				return false;
			}
		}
	}

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
		return new VideoEvent(type, bubbles, cancelable,
							  state, playheadTime);
	}
}

}
