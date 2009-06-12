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
import mx.core.mx_internal;

/**
 *  The MetadataEvent class defines the event type for metadata and cue point events.
 *  These events are used primarily by the VideoDisplay control.
 *
 *  @see mx.controls.VideoDisplay
 */
public class MetadataEvent extends Event 
{
    include "../core/Version.as";
    
    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     * The MetadataEvent.METADATA_RECEIVED constant defines the value of the 
     * <code>type</code> property for a <code>metadataReceived</code> event.
     *
     * <p>This event has the following properties:</p>
     * <table class="innertable" width="100%">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td><code>false</code></td></tr>
     *     <tr><td><code>cancelable</code></td><td><code>false</code>; 
     *        there is no default behavior to cancel.</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the 
     *       event listener that handles the event. For example, if you use 
     *       <code>myButton.addEventListener()</code> to register an event listener, 
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>info</code></td><td>An object describing the FLV 
     *       file, including any cue points. This property contains the same information 
     *       as the <code>VideoDisplay.metadata</code> property.
     *       See the <code>VideoDisplay.metadata</code> property for more information.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>vp</code></td><td>The index of the VideoPlayer object.</td></tr>    
     * </table>
     * 
     * @eventType metadataReceived
     */
    public static const METADATA_RECEIVED:String = "metadataReceived";

    /**
     *  The MetadataEvent.CUE_POINT constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>cuePoint</code> event.
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
     *     <tr><td><code>info</code></td><td>The index of the cue point 
     *       in the VideoDisplay.cuePoint Array.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *  @eventType cuePoint
     */
    public static const CUE_POINT:String = "cuePoint";

    /**
     *  @private
     *  Cue Point type constant.  <code>MetadataEvent.info.type</code>
     *  value for Navigation cue points embedded in FLV.
     *
     *  @see MetadataEvent
     */
    mx_internal static const NAVIGATION:String = "navigation";

    /**
     *  @private
     *  Cue Point type constant.  <code>MetadataEvent.info.type</code>
     *  value for Event cue points embedded in FLV.
     *
     *  @see MetadataEvent
     */
    mx_internal static const EVENT:String = "event";

    /**
     *  The MetadataEvent.ACTION_SCRIPT constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>actionscript</code> event.
     *  These cue points are not embedded in the FLV file but defined
     *  using ActionScript at run time.
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
     *     <tr><td><code>info</code></td><td>The index of the cue point 
     *       in the VideoDisplay.cuePoint Array.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
     *  </table>
     *
     *
     *  @eventType actionscript
     */
    public static const ACTION_SCRIPT:String = "actionscript";

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
     *  @param info For events off type <code>ACTION_SCRIPT</code> and <code>CUE_POINT</code>, 
     *  the index of the cue point in the <code>VideoDisplay.cuePoint</code> Array.
     *  For events off type <code>METADATA_RECEIVED</code>, 
     *  an object describing the FLV  file,  including any cue points, 
     *  which is the same information as the <code>VideoDisplay.metadata</code> property.
     */
    public function MetadataEvent(type:String, bubbles:Boolean = false,
                                  cancelable:Boolean = false,
                                  info:Object = null) 
    {
        super(type, bubbles, cancelable);

        this.info = info;
    }
    
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  info
    //----------------------------------

    /**
     *  For events off type <code>ACTION_SCRIPT</code> and <code>CUE_POINT</code>, 
     *  the index of the cue point in the <code>VideoDisplay.cuePoint</code> Array.
     *
     *  <p>For events off type <code>METADATA_RECEIVED</code>, 
     *  an object describing the FLV  file,  including any cue points, 
     *  which is the same information as the <code>VideoDisplay.metadata</code> property.</p>
     * 
     *  @see mx.controls.VideoDisplay#metadata
     */    
    public var info:Object;

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
        return new MetadataEvent(type, bubbles, cancelable, info);
    }
}

}
