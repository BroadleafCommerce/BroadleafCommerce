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

package mx.controls.menuClasses
{

import mx.controls.MenuBar;
import mx.controls.listClasses.IListItemRenderer;
import mx.core.IDataRenderer;
import mx.core.IUIComponent;
import mx.core.mx_internal;
import mx.styles.ISimpleStyleClient;

use namespace mx_internal;

/**
 *  The IMenuBarItemRenderer interface defines the interface
 *  that an item renderer for the top-level menu bar of a 
 *  MenuBar control must implement.
 *  The item renderer defines the look of the individual buttons 
 *  in the top-level menu bar. 
 * 
 *  To implement this interface, you must define a 
 *  setter and getter method that implements the <code>menuBar</code>, 
 *  <code>menuBarItemIndex</code>, and <code>menuBarItemState</code> properties. 
 * 
 *  @see mx.controls.MenuBar 
 *  @see mx.controls.menuClasses.MenuBarItem 
 */

public interface IMenuBarItemRenderer extends IDataRenderer, IUIComponent, ISimpleStyleClient, IListItemRenderer 
{
        
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  menuBar
    //----------------------------------

    /**
     *  Contains a reference to the item renderer's MenuBar control. 
     */
    function get menuBar():MenuBar;
    
    /**
     *  @private
     */
    function set menuBar(value:MenuBar):void;

    //----------------------------------
    //  menuBarItemIndex
    //----------------------------------

    /**
     *  Contains the index of this item renderer relative to
     *  other item renderers in the MenuBar control. 
     *  The index of the first item renderer,
     *  the left most renderer, is 0 and increases by 1 as you
     *  move to the right in the MenuBar control. 
     */
    function get menuBarItemIndex():int;
    
    /**
     *  @private
     */
    function set menuBarItemIndex(value:int):void;
    
    
    //----------------------------------
    //  menuBarItemState
    //----------------------------------

    /**
     *  Contains the current state of this item renderer. 
     *  The possible values are <code>"itemUpSkin"</code>, 
     *  <code>"itemDownSkin"</code>, and <code>"itemOverSkin"</code>. 
     */
    function get menuBarItemState():String;
    
    /**
     *  @private
     */
    function set menuBarItemState(value:String):void;


}
}