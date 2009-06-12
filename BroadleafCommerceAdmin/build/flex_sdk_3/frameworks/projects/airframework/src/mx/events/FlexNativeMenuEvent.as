////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.events
{

import flash.display.NativeMenu;
import flash.display.NativeMenuItem;
import flash.events.Event;

/**
 *  The FlexNativeMenuEvent class represents events that are associated with menu
 *  activities in FlexNativeMenu.
 *
 *  @see mx.controls.FlexNativeMenu
 * 
 *  @playerversion AIR 1.1
 */
public class FlexNativeMenuEvent extends Event
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The FlexNativeMenuEvent.ITEM_CLICK event type constant indicates that the
     *  user selected a menu item.
     *
     *  <p>The properties of the event object for this event type have the
     *  following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>index</code></td>
     *         <td>The index in the menu of the selected menu item.</td></tr>
     *     <tr><td><code>item</code></td>
     *         <td>The item in the dataProvider that was selected.</td></tr>
     *     <tr><td><code>label</code></td>
     *         <td>The label text of the selected menu item.</td></tr>
     *     <tr><td><code>nativeMenu</code></td>
     *          <td>The specific NativeMenu instance associated with this event.</td></tr>
     *     <tr><td><code>nativeMenuItem</code></td>
     *          <td>The specific NativeMenuItem instance associated with this event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the
     *             event; it is not always the Object listening for the event.
     *             Use the <code>currentTarget</code> property to always access the
     *             Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FlexNativeMenuEvent.ITEM_CLICK</td></tr>
     *  </table>
     *
     *  @eventType itemClick
     */
    public static const ITEM_CLICK:String = "itemClick";

    /**
     *  The FlexNativeMenuEvent.MENU_SHOW type constant indicates that
     *  the mouse pointer rolled a menu or submenu opened.
     *
     *  <p>The properties of the event object for this event type have the
     *  following values.
     *  Not all properties are meaningful for all kinds of events.
     *  See the detailed property descriptions for more information.</p>
     *  <table class="innertable">
     *     <tr><th>Property</th><th>Value</th></tr>
     *     <tr><td><code>bubbles</code></td><td>false</td></tr>
     *     <tr><td><code>cancelable</code></td><td>true</td></tr>
     *     <tr><td><code>currentTarget</code></td><td>The Object that defines the
     *       event listener that handles the event. For example, if you use
     *       <code>myButton.addEventListener()</code> to register an event listener,
     *       myButton is the value of the <code>currentTarget</code>. </td></tr>
     *     <tr><td><code>index</code></td>
     *         <td>-1. This property is not set for this type of event.</td></tr>
     *     <tr><td><code>item</code></td>
     *         <td>null. This property is not set for this type of event.</td></tr>
     *     <tr><td><code>label</code></td>
     *         <td>null. This property is not set for this type of event.</td></tr>
     *     <tr><td><code>nativeMenu</code></td>
     *          <td>The specific NativeMenu instance associated with this event.</td></tr>
     *     <tr><td><code>nativeMenuItem</code></td>
     *          <td>null. This property is not set for this type of event.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the
     *             event; it is not always the Object listening for the event.
     *             Use the <code>currentTarget</code> property to always access the
     *             Object listening for the event.</td></tr>
     *     <tr><td><code>type</code></td><td>FlexNativeMenuEvent.MENU_SHOW</td></tr>
     *  </table>
     *
     *  @eventType menuShow
     */
    public static const MENU_SHOW:String = "menuShow";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  Normally called by the FlexNativeMenu object.
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param bubbles Specifies whether the event can bubble
     *  up the display list hierarchy.
     *
     *  @param cancelable Specifies whether the behavior
     *  associated with the event can be prevented.
     *
     *  @param nativeMenu The specific NativeMenu instance associated with the event.
     *
     *  @param nativeMenuItem The specific NativeMenuItem instance associated with the event.
     *
     *  @param item The item in the dataProvider of the associated menu item.
     *
     *  @param label The label text of the associated menu item.
     *
     *  @param index The index in the menu of the associated menu item.
     */
    public function FlexNativeMenuEvent(type:String, bubbles:Boolean = false,
                              cancelable:Boolean = true, nativeMenu:NativeMenu = null,
                              nativeMenuItem:NativeMenuItem = null, item:Object = null,
                              label:String = null, index:int = -1)
    {
        super(type, bubbles, cancelable);

        this.nativeMenu = nativeMenu;
        this.nativeMenuItem = nativeMenuItem;
        this.item = item;
        this.label = label;
        this.index = index;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  index
    //----------------------------------

    /**
     *  The index of the associated menu item within its parent menu or submenu.
     *  This is -1 for events that aren't associated with an individual item.
     */
    public var index:int;

    //----------------------------------
    //  item
    //----------------------------------

    /**
     *  The specific item in the dataProvider.
     *  This is null for events that aren't associated with an individual item.
     */
    public var item:Object;

    //----------------------------------
    //  label
    //----------------------------------

    /**
     *  The label text of the associated menu item.
     *  This is null for events that aren't associated with an individual item.
     */
    public var label:String;

    //----------------------------------
    //  nativeMenu
    //----------------------------------

    /**
     *  The specific NativeMenu instance associated with the event,
     *  such as the menu displayed.
     */
    public var nativeMenu:NativeMenu;

    //----------------------------------
    //  nativeMenuItem
    //----------------------------------

    /**
     *  The specific NativeMenuItem instance associated with the event,
     *  such as the item clicked.  This is null for events that aren't
     *  associated with an individual item.
     */
    public var nativeMenuItem:NativeMenuItem;

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
        return new FlexNativeMenuEvent(type, bubbles, cancelable, nativeMenu, nativeMenuItem);
    }
}

}
