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

package mx.controls
{

import flash.filesystem.File;
import mx.controls.Menu;
import mx.controls.PopUpButton;
import mx.controls.fileSystemClasses.FileSystemControlHelper;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.events.FlexEvent;
import mx.events.MenuEvent;

use namespace mx_internal;

//--------------------------------------
//  Events
//--------------------------------------

/**
 *  Dispatched when a user selects an item from the pop-up menu.
 *
 *  @eventType mx.events.MenuEvent.ITEM_CLICK
 */
[Event(name="itemClick", type="mx.events.MenuEvent")]

//--------------------------------------
//  Other metadata
//--------------------------------------

[IconFile("FileSystemHistoryButton.png")]

[ResourceBundle("aircontrols")]

/**
 *  The FileSystemHistoryButton control defines a single control
 *  with two buttons: a main button on the left
 *  and a secondary button on the right.
 *
 *  <p>The main button can have a text label, an icon, or both on its face.
 *  When a user clicks the main button,
 *  the control dispatches a <code>click</code> event.</p>
 *
 *  <p>Clicking on the secondary (right) button drops down a menu
 *  populated through the <code>dataProvider</code> property.
 *  When a user selects an item from the drop-down menu,
 *  the control dispatches an <code>itemClick</code> event.</p>
 *
 *  <p>Typically, you use two FileSystemHistoryButtons
 *  with a FileSystemList or FileSystemDataGrid
 *  to implement a "Back" or "Forward" control
 *  which lets the user move backward or forward
 *  through the navigation history
 *  of the FileSystemList or FileSystemDataGrid.
 *  To populate the <code>dataProvider</code> property
 *  of a FileSystemHistoryButton control,
 *  use data binding to set its <code>dataProvider</code> property
 *  to either the <code>backHistory</code>
 *  or the <code>forwardHistory</code> property
 *  of the FileSystemList or FileSystemDataGrid control.
 *  To enable or disable it, bind its <code>enabled</code> property
 *  to either the <code>canNavigateBack</code>
 *  or the <code>canNavigateForward</code> property
 *  of the FileSystemList or FileSystemDataGrid control.
 *  If you use data binding to set these properties,
 *  Flex automatically updates them as the user navigates
 *  within the the FileSystemList or FileSystemDataGrid control.</p>
 *
 *  <p>The button does not cause navigation to occur by itself. You must write
 *  event listeners that respond to the <code>click</code> and <code>itemClick</code> events
 *  in order to make the button functional. Typically your code
 *  calls either the <code>navigateBack()</code>
 *  or the <code>navigateForward()</code> method of the FileSystemList
 *  or FileSystemDataGrid control to navigate the control.
 *  For a <code>click</code> event, you do not need to pass an argument
 *  to these methods.
 *  For an <code>itemClick</code> event, pass <code>event.index</code>.</p>
 *
 *  @mxml
 *
 *  <p>The <code>&lt;mx:FileSystemHistoryButton&gt;</code> tag inherits all of the tag
 *  attributes of its superclass and adds the following tag attributes:</p>
 *
 *  <pre>
 *  &lt;mx:FileSystemHistoryButton
 *    <strong>Properties</strong>
 *    dataProvider="<i>undefined</i>"
 * 
 *    <strong>Events</strong>
 *    itemClick="<i>No default</i>"
 *  /&gt;
 *  </pre>
 * 
 *  @see mx.controls.FileSystemList
 *  @see mx.controls.FileSystemDataGrid
 * 
 *  @playerversion AIR 1.1
 */
public class FileSystemHistoryButton extends PopUpButton
{
    include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     */
    public function FileSystemHistoryButton()
    {
        super();

        helper = new FileSystemControlHelper(this, false);

        popUpMenu = new Menu();
        popUpMenu.labelFunction = helper.fileLabelFunction;
        popUpMenu.owner = this;
        popUpMenu.addEventListener(MenuEvent.ITEM_CLICK, itemClickHandler);

        super.popUp = popUpMenu;
    }

    //--------------------------------------------------------------------------
    //
    //  Variables
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     *  An undocumented class that implements functionality
     *  shared by various file system components.
     */
    mx_internal var helper:FileSystemControlHelper;

    /**
	 *  @private
	 */
	private var popUpMenu:Menu;

    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  dataProvider
    //----------------------------------

    /**
     *  The data provider for the FileSystemHistoryButton control. This should
	 *  be a collection of File objects containing directory locations -- commonly
	 *  the <code>backHistory</code> or <code>forwardHistory</code> property of
	 *  a FileSystemList or FileSystemDataGrid control.
     */
    public function get dataProvider():Object
    {
        return popUpMenu.dataProvider;
    }

    /**
     *  @private
     */
    public function set dataProvider(value:Object):void
    {
        popUpMenu.dataProvider = value;
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: UIComponent
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override protected function resourcesChanged():void
    {
        super.resourcesChanged();

        // The name of the COMPUTER pseudo-directory is locale-dependent.
        if (popUpMenu)
        {
        	popUpMenu.invalidateSize();
        	popUpMenu.invalidateDisplayList();
        }
    }

    //--------------------------------------------------------------------------
    //
    //  Overridden methods: PopUpButton
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    override mx_internal function getPopUp():IUIComponent
    {
        super.getPopUp();

        return popUpMenu;
    }

    //--------------------------------------------------------------------------
    //
    //  Event handlers
    //
    //--------------------------------------------------------------------------

    /**
     *  @private
     */
    private function itemClickHandler(event:MenuEvent):void
    {
        dispatchEvent(event);
    }
}

}
