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
 *  The <code>CollectionViewError</code> class represents general errors
 *  within a collection that are not related to specific activities
 *  such as Cursor seeking.
 *  Errors of this class are thrown by the ListCollectionView class.
 */
public class CollectionViewError extends Error
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
    public function CollectionViewError(message:String)
    {
        super(message);
    }
}

}
