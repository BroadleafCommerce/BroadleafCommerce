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

package mx.core
{

/**
 *  Allows a component to support a font context property.
 *  The property will be set on the component by the framework
 *  as the child is added to  the display list.
 * 
 *  A font context is important for components that create flash.text.TextField
 *  objects with embedded fonts.
 *  If an embedded font is not registered using Font.registerFont(), 
 *  TextField objects can only use embedded fonts if they are created
 *  in the context of the embedded font.
 *  This interface provides for tracking the font context of a component.
 */    
public interface IFontContextComponent
{
    //--------------------------------------------------------------------------
    //
    //  Properties
    //
    //--------------------------------------------------------------------------

    //----------------------------------
    //  fontContext
    //----------------------------------

    /**
     *  The module factory that provides the font context for this component.
     */
    function get fontContext():IFlexModuleFactory;
    
    /**
     *  @private
     */
    function set fontContext(moduleFactory:IFlexModuleFactory):void;
}

}
