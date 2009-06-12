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

package mx.containers.errors
{

/**
 *  This error is thrown when a constraint expression is not configured properly;
 *  for example, if the GridColumn reference is invalid.
 */
public class ConstraintError extends Error
{
    include "../../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Constructor
    //
    //--------------------------------------------------------------------------

    /**
     *  Constructor.
	 *
	 *  @param message A message providing information about the error cause.
     */
    public function ConstraintError(message:String)
    {
        super(message);
    }
}

}
