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

package mx.controls.menuClasses
{

import mx.controls.Menu;

/**
 *  The IMenuItemRenderer interface defines the interface
 *  that a menu item renderer for a Menu control must implement.
 * 
 *  <p>The menu item renderers are often recycled. Once they are created, 
 *  they may be used again simply by being given new data. 
 *  Therefore, in individual implementations, component developers must 
 *  make sure that component properties are not assumed to contain 
 *  specific initial, or default values.</p>
 *
 *  <p>To implement this interface, a component developer must define a 
 *  setter and getter method that implements the <code>menu</code> property.
 *  Typically, the setter method writes the value of the data property
 *  to an internal variable, and the getter method returns the current
 *  value of the internal variable, as the following example shows:</p>
 *  <pre>
 *     // Internal variable for the property value.
 *     private var _menu:Menu;
 * 
 *     // Define the getter method.
 *     public function get menu():Menu
 *     {
 *         return _menu;
 *     }
 * 
 *     // Define the setter method.
 *     public function set menu(value:Menu):void
 *     {
 *         _menu = value;
 *     }
 *  </pre>
 */
public interface IMenuItemRenderer
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  menu
    //----------------------------------

    /**
     *  A reference to this menu item renderer's Menu control, 
     *  if it contains one. This indicates that this menu item
     *  renderer is a branch node, capable of popping up a sub menu.
     * 
     *  @return The reference to the Menu control. 
     */
    function get menu():Menu;
    
    /**
     *  @private
     */
    function set menu(value:Menu):void;
    
    /**
     *  The width of the icon.
     */
    function get measuredIconWidth():Number;
    
    /**
     *  The width of the type icon (radio/check).
     */
    function get measuredTypeIconWidth():Number;
    
    /**
     *  The width of the branch icon.
     */
    function get measuredBranchIconWidth():Number;
}

}
