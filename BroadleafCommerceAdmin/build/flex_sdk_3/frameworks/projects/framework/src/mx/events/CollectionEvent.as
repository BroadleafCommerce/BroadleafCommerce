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

/**
 *  The mx.events.CollectionEvent class represents an event that is  
 *  dispatched when the associated collection changes.
 *
 *  @see FlexEvent#CURSOR_UPDATE
 */
public class CollectionEvent extends Event
{
	include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The CollectionEvent.COLLECTION_CHANGE constant defines the value of the
     *  <code>type</code> property of the event object for an event that is
     *  dispatched when a collection has changed.
     *
     *  <p>The properties of the event object have the following values.
     *  Not all properties are meaningful for all kinds of events.
	 *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>items</code></td><td>An Array of objects with
     *       information about the items affected by the event.
	 * 		 The contents of this field depend on the event kind;
	 *       for details see the <code>items</code> property</td></tr>
     *     <tr><td><code>kind</code></td><td>The kind of event.
     *       The valid values are defined in the CollectionEventKind 
	 *       class as constants.</td></tr>
     *     <tr><td><code>location</code></td><td>Location within the target collection
     *         of the item(s) specified in the <code>items</code> property.</td></tr>
     *     <tr><td><code>oldLocation</code></td><td>the previous location in the collection
     *         of the item specified in the <code>items</code> property.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>CollectionEvent.COLLECTION_CHANGE</td></tr>
     *  </table>
     *
     *  @eventType collectionChange
     */
    public static const COLLECTION_CHANGE:String = "collectionChange";

    //--------------------------------------------------------------------------
    //
    // Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
     *  @param type The event type; indicates the action that triggered the event.
     *
     *  @param bubbles Specifies whether the event can bubble
     *  up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     *
     *  @param kind Indicates the kind of event that occured.
     *  The parameter value can be one of the values in the CollectionEventKind 
	 *  class, or <code>null</code>, which indicates that the kind is unknown.
     *
     *  @param location When the <code>kind</code> is
     *  <code>CollectionEventKind.ADD</code>,
     *  <code>CollectionEventKind.MOVE</code>,
     *  <code>CollectionEventKind.REMOVE</code>, or
     *  <code>CollectionEventKind.REPLACE</code>,
     *  this value indicates at what location the item(s) specified
     *  in the <code>items property</code> can be found
     *  within the target collection.
     *
     *  @param oldLocation When the <code>kind</code> is
     *  <code>CollectionEventKind.MOVE</code>, this value indicates
     *  the old location within the target collection
     *  of the item(s) specified in the <code>items</code> property.
      *
     *  @param items Array of objects with information about the items 
	 *  affected by the event, as described in the <code>items</code> property.
     *  When the <code>kind</code> is <code>CollectionEventKind.REFRESH</code>
     *  or <code>CollectionEventKind.RESET</code>, this Array has zero length.
     */
    public function CollectionEvent(type:String, bubbles:Boolean = false,
                                    cancelable:Boolean = false,
                                    kind:String = null, location:int = -1,
                                    oldLocation:int = -1, items:Array = null)
    {
        super(type, bubbles, cancelable);

        this.kind = kind;
        this.location = location;
        this.oldLocation = oldLocation;
        this.items = items ? items : [];
    }

    //--------------------------------------------------------------------------
    //
    // Properties
    //
    //--------------------------------------------------------------------------

	//----------------------------------
	//  kind
	//----------------------------------

    /**
     *  Indicates the kind of event that occurred.
     *  The property value can be one of the values in the 
	 *  CollectionEventKind class, 
	 *  or <code>null</code>, which indicates that the kind is unknown.
	 * 
     *  @default null
	 * 
	 *  @see CollectionEventKind
     */
    public var kind:String;

	//----------------------------------
	//  items
	//----------------------------------

    /**
	 *  When the <code>kind</code> is <code>CollectionEventKind.ADD</code>
     *  or <code>CollectionEventKind.REMOVE</code> the <code>items</code> property
     *  is an Array of added/removed items.
	 *  When the <code>kind</code> is <code>CollectionEventKind.REPLACE</code>
     *  or <code>CollectionEventKind.UPDATE</code> the <code>items</code> property
     *  is an Array of PropertyChangeEvent objects with information about the items
     *  affected by the event.
     *  When a value changes, query the <code>newValue</code> and
     *  <code>oldValue</code> fields of the PropertyChangeEvent objects
     *  to find out what the old and new values were.
     *  When the <code>kind</code> is <code>CollectionEventKind.REFRESH</code>
     *  or <code>CollectionEventKind.RESET</code>, this array has zero length.
     *
     *  @default [ ]
     *
     *  @see PropertyChangeEvent
     */
    public var items:Array;

	//----------------------------------
	//  location
	//----------------------------------

    /**
     *  When the <code>kind</code> value is <code>CollectionEventKind.ADD</code>,
     *  <code>CollectionEventKind.MOVE</code>,
     *  <code>CollectionEventKind.REMOVE</code>, or
     *  <code>CollectionEventKind.REPLACE</code>, this property is the 
	 *  zero-base index in the collection of the item(s) specified in the 
	 *  <code>items</code> property.
     *
     *  @see CollectionEventKind
     *
     *  @default -1
     */
    public var location:int;

	//----------------------------------
	//  oldLocation
	//----------------------------------

    /**
     *  When the <code>kind</code> value is <code>CollectionEventKind.MOVE</code>,
     *  this property is the zero-based index in the target collection of the
     *  previous location of the item(s) specified by the <code>items</code> property.
     *
     *  @default -1
     */
    public var oldLocation:int;

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: Object
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override public function toString():String
    {
        return formatToString("CollectionEvent", "kind", "location",
							  "oldLocation", "type", "bubbles",
							  "cancelable", "eventPhase");
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
        return new CollectionEvent(type, bubbles, cancelable, kind, location, oldLocation, items);
    }
}

}
