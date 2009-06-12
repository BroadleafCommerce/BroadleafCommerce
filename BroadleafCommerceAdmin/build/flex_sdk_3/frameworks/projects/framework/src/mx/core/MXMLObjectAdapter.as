////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2004-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.core
{

/**
 *  The MXMLObjectAdapter class is a stub implementation
 *  of the IMXMLObject interface, so that you can implement
 *  the interface without defining all of the methods.
 *  All implementations are the equivalent of no-ops.
 *  If the method is supposed to return something, it is null, 0, or false.
 */
public class MXMLObjectAdapter implements IMXMLObject
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
    public function MXMLObjectAdapter()
    {
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  @inheritDoc
     */
    public function initialized(document:Object, id:String):void
	{
	}
}

}
