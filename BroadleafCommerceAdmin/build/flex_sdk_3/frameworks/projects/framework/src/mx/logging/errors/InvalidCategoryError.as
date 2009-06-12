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

package mx.logging.errors
{

/**
 *  This error is thrown when a category specified for a logger
 *  contains invalid characters or is malformed.
 *  This error is thrown by the following method:
 *  <ul>
 *    <li><code>Log.getLogger()</code> if a category specified
 *    is malformed.</li>
 *  </ul>
 */
public class InvalidCategoryError extends Error
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
	 *  @param message The message that describes this error.
     */
    public function InvalidCategoryError(message:String)
    {
        super(message);
    }

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

         /**
	 *  Returns the messge as a String.
	 *  
	 *  @return The message.
	 *  
	 */
	public function toString():String
    {
        return String(message);
    }
}

}
