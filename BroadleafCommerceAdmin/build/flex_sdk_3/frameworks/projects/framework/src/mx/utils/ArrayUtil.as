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

package mx.utils
{

/**
 *  The ArrayUtil utility class is an all-static class
 *  with methods for working with arrays within Flex.
 *  You do not create instances of ArrayUtil;
 *  instead you call static methods such as the 
 *  <code>ArrayUtil.toArray()</code> method.
 */
public class ArrayUtil
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------

    /**
     *  Ensures that an Object can be used as an Array.
	 *
     *  <p>If the Object is already an Array, it returns the object. 
     *  If the object is not an Array, it returns an Array
	 *  in which the only element is the Object.
	 *  As a special case, if the Object is null,
	 *  it returns an empty Array.</p>
	 *
     *  @param obj Object that you want to ensure is an array.
	 *
     *  @return An Array. If the original Object is already an Array, 
     * 	the original Array is returned. Otherwise, a new Array whose
     *  only element is the Object is returned or an empty Array if 
     *  the Object was null. 
     */
    public static function toArray(obj:Object):Array
    {
		if (!obj) 
			return [];
		
		else if (obj is Array)
			return obj as Array;
		
		else
		 	return [ obj ];
    }
    
    /**
     *  Returns the index of the item in the Array.
     * 
     *  Note that in this implementation the search is linear and is therefore 
     *  O(n).
     * 
     *  @param item The item to find in the Array. 
     *
     *  @param source The Array to search for the item.
     * 
     *  @return The index of the item, and -1 if the item is not in the list.
     */
    public static function getItemIndex(item:Object, source:Array):int
    {
        var n:int = source.length;
        for (var i:int = 0; i < n; i++)
        {
            if (source[i] === item)
                return i;
        }

        return -1;           
    }
}

}
