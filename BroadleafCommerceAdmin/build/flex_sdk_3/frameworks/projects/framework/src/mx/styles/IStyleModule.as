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

package mx.styles
{

/**
 * Simple interface that defines an <code>unload()</code> method.
 * You can cast an object to an IStyleModule type so that there is no dependency on the StyleModule
 * type in the loading application.
 */
public interface IStyleModule
{
    //--------------------------------------------------------------------------
    //
    //  Methods
    //
    //--------------------------------------------------------------------------

    /**
     * Unloads the style module.
     */
    function unload():void;
}

}
