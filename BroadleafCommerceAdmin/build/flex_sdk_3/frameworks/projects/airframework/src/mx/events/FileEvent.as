////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

import flash.events.Event;
import flash.filesystem.File;

/**
 *  The FileEvent class represents event objects that are specific to
 *  the FileSystemList, FileSystemDataGrid, FileSystemTree
 *  and FileSystemComboBox controls.
 *
 *  @see mx.controls.FileSystemComboBox
 *  @see mx.controls.FileSystemDataGrid
 *  @see mx.controls.FileSystemList
 *  @see mx.controls.FileSystemTree
 * 
 *  @playerversion AIR 1.1
 */
public class FileEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The FileEvent.DIRECTORY_CHANGE constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>directoryChange</code> event.
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
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FileEvent.DIRECTORY_CHANGE</td></tr>
     *  </table>
     *
     *  @eventType directoryChange
     */
    public static const DIRECTORY_CHANGE:String = "directoryChange";

    /**
     *  The FileEvent.DIRECTORY_CHANGING constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>directoryChanging</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FileEvent.DIRECTORY_CHANGING</td></tr>
     *  </table>
     *
     *  @eventType directoryChanging
     */
    public static const DIRECTORY_CHANGING:String = "directoryChanging";

    /**
     *  The FileEvent.DIRECTORY_OPENING constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>directoryOpening</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FileEvent.DIRECTORY_OPENING</td></tr>
     *  </table>
     *
     *  @eventType directoryOpening
     */
    public static const DIRECTORY_OPENING:String = "directoryOpening";

    /**
     *  The FileEvent.DIRECTORY_CLOSING constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>directoryClosing</code> event.
     *
     *  <p>The properties of the event object have the following values:</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FileEvent.DIRECTORY_CLOSING</td></tr>
     *  </table>
     *
     *  @eventType directoryClosing
     */
    public static const DIRECTORY_CLOSING:String = "directoryClosing";

    /**
     *  The FileEvent.FILE_CHOOSE constant defines the value of the
     *  <code>type</code> property of the event object for a
     *  <code>fileChoose</code> event.
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
     *     <tr><td><code>file</code></td><td>The File object associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FileEvent.FILE_CHOOSE</td></tr>
     *  </table>
     *
     *  @eventType select
     */
    public static const FILE_CHOOSE:String = "fileChoose";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param type The event type;
     *  indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event
     *  can bubble up the  display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     *
     *  @param file The File instance associated with this event.
     */
    public function FileEvent(type:String, bubbles:Boolean = false,
                              cancelable:Boolean = false,
                              file:File = null)
    {
        super(type, bubbles, cancelable);

        this.file = file;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  file
    //----------------------------------

    /**
     *  The File instance associated with this event.
     */
    public var file:File;

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
        return new FileEvent(type, bubbles, cancelable, file);
    }

}

}
