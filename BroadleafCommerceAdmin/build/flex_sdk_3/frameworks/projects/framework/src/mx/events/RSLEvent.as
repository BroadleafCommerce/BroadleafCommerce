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
import flash.events.ProgressEvent;
import flash.net.URLRequest;

/**
 *  The RSLEvent class represents an event object used by the 
 *  DownloadProgressBar class when an RSL is being downloaded by the Preloader class. 
 *
 *  @see mx.preloaders.DownloadProgressBar
 *  @see mx.preloaders.Preloader
 */
public class RSLEvent extends ProgressEvent
{
    include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class constants
	//
	//--------------------------------------------------------------------------

	/**
	 *  Dispatched when the RSL has finished downloading. 	
	 *  The <code>RSLEvent.RSL_COMPLETE</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>rslComplete</code> event.
     *
	 *  <p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>The number of bytes loaded.</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>The total number of bytes to load.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td><td>Empty</td></tr>
     *     <tr><td><code>rslIndex</code></td><td>The index number of the RSL 
     *       currently being downloaded. </td></tr>
     *     <tr><td><code>rslTotal</code></td><td>The total number of RSLs 
     *       being downloaded. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>url</code></td><td>The URLRequest object that represents 
     *       the location of the RSL being downloaded.</td></tr>
	 *  </table>
	 *
     *  @eventType rslComplete
	 */
	public static const RSL_COMPLETE:String = "rslComplete";
	
	/**
	 *  Dispatched when there is an error downloading the RSL.
	 *  The <code>RSLEvent.RSL_ERROR</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>rslError</code> event.
     *
	 *  <p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>Empty</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>Empty</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td>An error message.<td></td></tr>
     *     <tr><td><code>rslIndex</code></td><td>The index number of the RSL 
     *       currently being downloaded. </td></tr>
     *     <tr><td><code>rslTotal</code></td><td>The total number of RSLs 
     *       being downloaded. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>url</code></td><td>The URLRequest object that represents 
     *       the location of the RSL being downloaded.</td></tr>
	 *  </table>
	 *
     *  @eventType rslError
	 */
	public static const RSL_ERROR:String = "rslError";

	/**
	 *  Dispatched when the RSL is downloading.
	 *  The <code>RSLEvent.RSL_PROGRESS</code> constant defines the value of the 
	 *  <code>type</code> property of the event object for a <code>rslProgress</code> event.
     *
	 *  <p>The properties of the event object have the following values:</p>
	 *  <table class="innertable">
	 *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>bytesLoaded</code></td><td>The number of bytes loaded.</td></tr>
     *     <tr><td><code>bytesTotal</code></td><td>The total number of bytes to load.</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>errorText</code></td>Empty<td></td></tr>
     *     <tr><td><code>rslIndex</code></td><td>The index number of the RSL 
     *       currently being downloaded. </td></tr>
     *     <tr><td><code>rslTotal</code></td><td>The total number of RSLs 
     *       being downloaded. </td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>url</code></td><td>The URLRequest object that represents 
     *       the location of the RSL being downloaded.</td></tr>
	 *  </table>
	 *
     *  @eventType rslProgress
	 */
	public static const RSL_PROGRESS:String = "rslProgress"; 
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructor.
	 * 
	 *  @param type The type of the event. Possible values are:
	 *  <ul>
	 *     <li>"rslProgress" (<code>RSLEvent.RSL_PROGRESS</code>);</li>
	 *     <li>"rslComplete" (<code>RSLEvent.RSL_COMPLETE</code>);</li>
	 *     <li>"rslError" (<code>RSLEvent.RSL_ERROR</code>);</li>
	 *  </ul>
	 *
	 *  @param bubbles  Determines whether the Event object participates in the bubbling stage of the event flow.
	 *
	 *  @param cancelable Determines whether the Event object can be cancelled.
	 *
	 *  @param bytesLoaded The number of bytes loaded at the time the listener processes the event.
	 *
	 *  @param bytesTotal The total number of bytes that will ultimately be loaded if the loading process succeeds.
	 *
	 *  @param rslIndex The index number of the RSL relative to the total. This should be a value between 0 and <code>total - 1</code>.
	 *
	 *  @param rslTotal The total number of RSLs being loaded.
	 *
	 *  @param url The location of the RSL.
	 *
	 *  @param errorText The error message of the error when type is RSLEvent.RSL_ERROR.
	 *
	 *  @tiptext Constructor for <code>RSLEvent</code> objects.
	 */	
	public function RSLEvent(type:String,  bubbles:Boolean = false,
							 cancelable:Boolean = false,
							 bytesLoaded:int = -1, bytesTotal:int = -1,
							 rslIndex:int = -1, rslTotal:int = -1,
							 url:URLRequest = null, errorText:String = null)
	{
		super(type, bubbles, cancelable, bytesLoaded, bytesTotal);
		
		this.rslIndex = rslIndex;
		this.rslTotal = rslTotal;
		this.url = url;
		this.errorText = errorText;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  errorText
	//----------------------------------

	/**
	 *  The error message if the type is RSL_ERROR; otherwise, it is null;
	 */
	public var errorText:String;
	
	//----------------------------------
	//  rslIndex
	//----------------------------------

	/**
	 *  The index number of the RSL currently being downloaded.
	 *  This is a number between 0 and <code>rslTotal - 1</code>.
	 */
	public var rslIndex:int;
	
	//----------------------------------
	//  rslTotal
	//----------------------------------

	/**
	 *  The total number of RSLs being downloaded by the preloader
	 */
	public var rslTotal:int;
	
	//----------------------------------
	//  url
	//----------------------------------

	/**
	 *  The URLRequest object that represents the location
	 *  of the RSL being downloaded.
	 */
	public var url:URLRequest;
	
	//--------------------------------------------------------------------------
	//
	//  Overridden properties: Event
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	override public function clone():Event
	{
		return new RSLEvent(type, bubbles, cancelable,
							bytesLoaded, bytesTotal, rslIndex,
							rslTotal, url, errorText);
	}
}

}
