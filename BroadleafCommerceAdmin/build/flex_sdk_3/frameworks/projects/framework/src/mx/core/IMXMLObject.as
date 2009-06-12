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

package mx.core
{

/**
 *  The IMXMLObject interface defines the APIs that a non-visual component
 *  must implement in order to work properly with the MXML compiler.
 *  Currently, the only supported method is the <code>initialized()</code>
 *  method.
 */
public interface IMXMLObject
{
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Called after the implementing object has been created and all
	 *  component properties specified on the MXML tag have been initialized.
	 *
     *  @param document The MXML document that created this object.
	 *
     *  @param id The identifier used by <code>document</code> to refer
	 *  to this object.
	 *  If the object is a deep property on <code>document</code>,
	 *  <code>id</code> is null.
     */
    function initialized(document:Object, id:String):void;
}

}
