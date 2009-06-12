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
import mx.controls.listClasses.IListItemRenderer;

/**
 *  The TreeEvent class represents the event object passed to the event
 *  listener for events that are associated with activities
 *  in tree, such as when a tree branch opens or closes.
 *
 *  @see mx.controls.Tree
 */
public class TreeEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The TreeEvent.ITEM_CLOSE event type constant indicates that a tree
     *  branch closed or collapsed.
     *
     *  <p>The properties of the event object for this event type have the
     *  following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>itemRenderer</code></td>
     *         <td>The ListItemRenderer for the node that closed</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>triggerEvent</code></td>
     *         <td>If the node closed in response to a user action,
     *             identifies it as a keyboard action or a mouse action.</td></tr>
     *     <tr><td><code>item</code></td><td>the Tree item (node) that closed</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>TreeEvent.ITEM_CLOSE</td></tr>
     *  </table>
     *
     *  @eventType itemClose
     */
    public static const ITEM_CLOSE:String = "itemClose";

    /**
     *  The TreeEvent.ITEM_OPEN event type constant indicates that a tree
     *  branch opened or expanded.
     *
     *  <p>The properties of the event object for this event type have the
     *  following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>itemRenderer</code></td>
     *         <td>The ListItemRenderer for the item (node) that opened</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>false</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>triggerEvent</code></td>
     *         <td>If the item (node) opened in response to a user action,
     *             identifies it as a keyboard action or a mouse action.</td></tr>
     *     <tr><td><code>item</code></td><td>the Tree node that opened.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>TreeEvent.ITEM_OPEN</td></tr>
     *  </table>
     *
     *  @eventType itemOpen
     */
    public static const ITEM_OPEN:String = "itemOpen";
    
    /**
     *  The TreeEvent.ITEM_OPENING event type constant is dispatched immediately 
     *  before a tree opens or closes.
     *
     *  <p>The properties of the event object for this event type have the
     *  following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     * 
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>animate</code></td><td>Whether to animate the opening 
     *             or closing operation.</td></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *              event listener that handles the event. For example, if you use
     *              <code>myButton.addEventListener()</code> to register an event
     *              listener, myButton is the value of the <code>currentTarget</code>.</td></tr>
     *     <tr><td><code>dispatchEvent</code></td><td>Whether to dispatch an 
     *             <code>ITEM_OPEN</code> or <code>ITEM_CLOSE</code> event
     *              after the open or close animation is complete. true</td></tr>
     *     <tr><td><code>itemRenderer</code></td>
     *         <td>The ListItemRenderer for the item (node) that opened</td></tr>
     *     <tr><td><code>item</code></td><td>the Tree node that opened.</td></tr>
     *     <tr><td><code>opening</code></td><td>true if the item is opening, false
     *             if it is closing.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event;
     *       it is not always the Object listening for the event.
     *       Use the <code>currentTarget</code> property to always access the
     *       Object listening for the event.</td></tr>
     *     <tr><td><code>triggerEvent</code></td>
     *         <td>If the item opened in response to a user action,
     *             identifies it as a keyboard action or a mouse action.</td></tr>
     *     <tr><td><code>type</code></td><td>TreeEvent.ITEM_OPENING</td></tr>
     *  </table>
     *
     *  @eventType itemOpening
     */
    public static const ITEM_OPENING:String = "itemOpening";
    
    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *  Normally called by the Flex Tree control; not used in application code.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble
     *  up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior associated with the event
     *  can be prevented.
     *
     *  @param item The Tree node (item) to which this event applies.
     *
     *  @param itemRenderer The item renderer object for the cell.
     *
     *  @param triggerEvent If the node opened or closed in response to a
     *  user action, indicates the type of input action.
     */
    public function TreeEvent(type:String, bubbles:Boolean = false,
                              cancelable:Boolean = false,
                              item:Object = null,
                              itemRenderer:IListItemRenderer = null,
                              triggerEvent:Event = null)
    {
        super(type, bubbles, cancelable);

        this.item = item;
        this.itemRenderer = itemRenderer;
        this.triggerEvent = triggerEvent;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  animate
    //----------------------------------

    /**
     *  Whether to animate an opening or closing operation; used for 
     *  ITEM_OPENING type events only.
     */
    public var animate:Boolean;

    //----------------------------------
    //  dispatchEvent
    //----------------------------------

    /**
     *  Whether to dispatch an event (<code>ITEM_OPEN</code> or 
     *  <code>ITEM_CLOSE</code>) after the open or close animation 
     *  is complete. Used for ITEM_OPENING type events only.
     */
    public var dispatchEvent:Boolean;

    //----------------------------------
    //  item
    //----------------------------------

    /**
     *  Storage for the item property.
     *  If you populate the Tree from XML data, access
     *  the properties for the node as
     *  <code>event.item.&64;<i>attribute_name</i></code>.
     */
    public var item:Object;

    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  The ListItemRenderer for the node that closed or opened.
     */
    public var itemRenderer:IListItemRenderer;

    //----------------------------------
    //  opening
    //----------------------------------

    /**
     *  Used for an <code>ITEM_OPENING</code> type events only.
     *  Indicates whether the item 
     *  is opening <code>true</code>, or closing <code>false</code>.
     */
    public var opening:Boolean;

    //----------------------------------
    //  triggerEvent
    //----------------------------------

    /**
     *  The low level MouseEvent or KeyboardEvent that triggered this
     *  event or <code>null</code> if this event was triggered programatically.
     */
    public var triggerEvent:Event;

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
        return new TreeEvent(type, bubbles, cancelable,
                             item, itemRenderer, triggerEvent);
    }
}

}
