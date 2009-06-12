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

package mx.events
{

/**
 *  The CollectionEventKind class contains constants for the valid values 
 *  of the mx.events.CollectionEvent class <code>kind</code> property.
 *  These constants indicate the kind of change that was made to the collection.
 *
 *  @see mx.events.CollectionEvent
 */
public final class CollectionEventKind
{
	include "../core/Version.as";

    //--------------------------------------------------------------------------
    //
    //  Class constants
    //
    //--------------------------------------------------------------------------

    /**
     *  Indicates that the collection added an item or items.
     */
    public static const ADD:String = "add";

    /**
     *  Indicates that the item has moved from the position identified
     *  by the CollectionEvent <code>oldLocation</code> property to the 
	 *  position identified by the <code>location</code> property.
     */
    public static const MOVE:String = "move";

    /**
     *  Indicates that the collection applied a sort, a filter, or both.
     *  This change can potentially be easier to handle than a RESET.
     */
    public static const REFRESH:String = "refresh";

    /**
     *  Indicates that the collection removed an item or items.
     */
    public static const REMOVE:String = "remove";

    /**
     *  Indicates that the item at the position identified by the 
     *  CollectionEvent <code>location</code> property has been replaced.
     */
    public static const REPLACE:String = "replace";

    /**
    *  Indicates that the collection has internally expanded. 
    *  This event kind occurs when a branch opens in a 
	*  hierarchical collection, for example when a Tree control branch opens.
    */
    mx_internal static const EXPAND:String = "expand";

    /**
     *  Indicates that the collection has changed so drastically that
     *  a reset is required.
     */
    public static const RESET:String = "reset";

    /**
     *  Indicates that one or more items were updated within the collection.
     *  The affected item(s) or their associated ObjectChangeEvent objects
     *  are stored in the <code>items</code> property.
     */
    public static const UPDATE:String = "update";
}

}
