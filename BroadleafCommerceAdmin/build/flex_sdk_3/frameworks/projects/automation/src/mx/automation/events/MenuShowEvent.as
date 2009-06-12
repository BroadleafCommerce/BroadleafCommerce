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

package mx.automation.events
{

import flash.events.Event;
import mx.controls.menuClasses.IMenuBarItemRenderer;

/**
 *  The MenuShowEvent class represents events that are associated with menuBar.
 *
 */
public class MenuShowEvent extends Event
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  The <code>MenuShowEvent.MENU_SHOW</code> constant defines the value of the 
     *  <code>type</code> property of the event object for a <code>menuShow</code> event.
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
     *     <tr><td><code>itemRenderer</code></td><td>The item renderer object 
     *              for the associated menu item.</td></tr>
     *     <tr><td><code>target</code></td><td>The Object that dispatched the event; 
     *       it is not always the Object listening for the event. 
     *       Use the <code>currentTarget</code> property to always access the 
     *       Object listening for the event.</td></tr>
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
     *  Normally called by the Menu object. 
     *
     *  @param type The event type; indicates the action that caused the event.
     *
     *  @param itemRenderer The IMenuBarItemRenderer of the associated menu item.
     * 
     */
    public function MenuShowEvent(type:String, 
                    itemRenderer:IMenuBarItemRenderer = null)
    {
        super(type);

        this.itemRenderer = itemRenderer;
    }

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  itemRenderer
    //----------------------------------

    /**
     *  The item renderer of the associated menu item where the event occurred.
     */
    public var itemRenderer:IMenuBarItemRenderer;

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
        return new MenuShowEvent(type, itemRenderer);
    }
}

}
