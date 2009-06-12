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

package mx.collections
{

/**
 *  @private
 * 
 *  The ItemWrapper class is a simple envelope for an item in a collection.
 *  Its purpose is to provide a way of distinguishing between duplicate items
 *  in a collection -- i.e., giving them unique IDs. It is used by data change
 *  effects for classes derived by ListBase. Distinguishing between duplicate
 *  elements is particularly important for data change effects because it is
 *  necessary to assign common item renderers to common items in a collection
 */
public class ItemWrapper
{
    include "../core/Version.as";
    
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  Constructs an instance of the wrapper with the specified data.
	 * 
	 *  @param data The data element to be wrapped.
	 */
	public function ItemWrapper(data:Object)
	{
		super();
		this.data = data;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  The data item being wrapped.
	 */ 
    public var data:Object;

}


}