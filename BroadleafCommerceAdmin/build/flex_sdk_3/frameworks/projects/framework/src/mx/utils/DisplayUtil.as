////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.utils
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import mx.core.IRawChildrenContainer;

/**
 *  The DisplayUtil utility class is an all-static class with utility methods
 *  related to DisplayObjects.
 *  You do not create instances of the DisplayUtil class;
 *  instead you call static methods such as the 
 *  <code>DisplayUtil.walkDisplayObjects()</code>.
 */
public class DisplayUtil
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Class methods
	//
	//--------------------------------------------------------------------------
		
	/**
	 *  Recursively calls the specified function on each node in the specified DisplayObject's tree,
	 *  passing it a reference to that DisplayObject.
	 *  
	 *  @param displayObject The target DisplayObject.
	 *  @param callbackFunction The method to call on each node in the specified DisplayObject's tree. 
	 */
	public static function walkDisplayObjects(displayObject:DisplayObject,
											  callbackFunction:Function):void
	{
		callbackFunction(displayObject)

		if (displayObject is DisplayObjectContainer)
		{
			var n:int =
				displayObject is IRawChildrenContainer ?
				IRawChildrenContainer(displayObject).rawChildren.numChildren :
				DisplayObjectContainer(displayObject).numChildren;
			
			for (var i:int = 0; i < n; i++)
			{
				var child:DisplayObject =
					displayObject is IRawChildrenContainer ?
					IRawChildrenContainer(displayObject).
					rawChildren.getChildAt(i) :
					DisplayObjectContainer(displayObject).getChildAt(i);

				walkDisplayObjects(child, callbackFunction);
			}
		}
	}
}

}
