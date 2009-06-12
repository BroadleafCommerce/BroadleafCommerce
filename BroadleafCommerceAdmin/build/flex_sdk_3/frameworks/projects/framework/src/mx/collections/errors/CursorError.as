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

package mx.collections.errors
{

/**
 *  This error is thrown by a collection Cursor.
 *  Errors of this class are thrown by classes
 *  that implement the IViewCursor interface.
 */
public class CursorError extends Error
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    // Constructor.
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
     *
     *  @param message A message providing information about the error cause.
     */
    public function CursorError(message:String)
    {
        super(message);
    }
}

}
