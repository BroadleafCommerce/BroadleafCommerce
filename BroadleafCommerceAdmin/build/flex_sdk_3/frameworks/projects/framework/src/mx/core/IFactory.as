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
 *  The IFactory interface defines the interface that factory classes
 *  such as ClassFactory must implement.
 *  An object of type IFactory is a "factory object" which Flex uses
 *  to generate multiple instances of another class, each with identical
 *  properties.
 *
 *  <p>For example, a DataGridColumn has an <code>itemRenderer</code> of type
 *  IFactory; it calls <code>itemRenderer.newInstance()</code> to create
 *  the cells for a particular column of the DataGrid.</p>
 *
 *  @see mx.core.ClassFactory
 */
public interface IFactory
{
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  Creates an instance of some class (determined by the class that
	 *  implements IFactory).
	 *
	 *  @return The newly created instance.
	 */
	function newInstance():*;
}

}
