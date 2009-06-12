////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2003-2006 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.managers.layoutClasses
{

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import mx.core.IChildList;
import mx.core.IRawChildrenContainer;
import mx.managers.ILayoutManagerClient;

[ExcludeClass]

/**
 *  @private
 *  The PriorityQueue class provides a general purpose priority queue.
 *  It is used internally by the LayoutManager.
 */
public class PriorityQueue
{
	include "../../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  Constructor.
	 */
	public function PriorityQueue()
	{
		super();
	}

	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------
	
	/**
	 *  @private
	 */
	private var arrayOfArrays:Array /* of Array */ = [];

	/**
	 *  @private
	 *  The smallest occupied index in arrayOfArrays.
	 */
	private var minPriority:int = 0;
	
	/**
	 *  @private
	 *  The largest occupied index in arrayOfArrays.
	 */
	private var maxPriority:int = -1;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public function addObject(obj:Object, priority:int):void
	{		
		if (!arrayOfArrays[priority])
			arrayOfArrays[priority] = [];

		arrayOfArrays[priority].push(obj);

		if (maxPriority < minPriority)
		{
			minPriority = maxPriority = priority;
		}
		else
		{
			if (priority < minPriority)
				minPriority = priority;
			if (priority > maxPriority)
				maxPriority = priority;
		}
	}

	/**
	 *  @private
	 */
	public function removeLargest():Object
	{
		var obj:Object = null;

		if (minPriority <= maxPriority)
		{
			while (!arrayOfArrays[maxPriority] || 
				   arrayOfArrays[maxPriority].length == 0)
			{
				maxPriority--;
				if (maxPriority < minPriority)
					return null;
			}
			
			obj = arrayOfArrays[maxPriority].shift();

			while (!arrayOfArrays[maxPriority] || 
				   arrayOfArrays[maxPriority].length == 0)
			{
				maxPriority--;
				if (maxPriority < minPriority)
					break;
			}
			
		}

		return obj;
	}

	/**
	 *  @private
	 */
	public function removeLargestChild(client:ILayoutManagerClient ):Object
	{
		var obj:Object = null;

		var max:int = maxPriority;
		var min:int = client.nestLevel;

		while (min <= max)
		{
			if (arrayOfArrays[max] && 
				   arrayOfArrays[max].length > 0)
			{
				for (var i:int = 0; i < arrayOfArrays[max].length; i++)
				{
					if (contains(DisplayObject(client), arrayOfArrays[max][i]))
					{
						obj = arrayOfArrays[max][i];
						arrayOfArrays[max].splice(i, 1);
						return obj;
					}
				}
				max--;
			}
			else
			{
				if (max == maxPriority)
					maxPriority--;
				max--;
				if (max < min)
					break;
			}			
		}

		return obj;
	}

	/**
	 *  @private
	 */
	public function removeSmallest():Object
	{
		var obj:Object = null;

		if (minPriority <= maxPriority)
		{
			while (!arrayOfArrays[minPriority] || 
				   arrayOfArrays[minPriority].length == 0)
			{
				minPriority++;
				if (minPriority > maxPriority)
					return null;
			}			

			obj = arrayOfArrays[minPriority].shift();

			while (!arrayOfArrays[minPriority] || 
				   arrayOfArrays[minPriority].length == 0)
			{
				minPriority++;
				if (minPriority > maxPriority)
					break;
			}			
		}

		return obj;
	}

	/**
	 *  @private
	 */
	public function removeSmallestChild(client:ILayoutManagerClient ):Object
	{
		var obj:Object = null;

		var min:int = client.nestLevel;

		while (min <= maxPriority)
		{
			if (arrayOfArrays[min] && 
				   arrayOfArrays[min].length > 0)
			{
				for (var i:int = 0; i < arrayOfArrays[min].length; i++)
				{
					if (contains(DisplayObject(client), arrayOfArrays[min][i]))
					{
						obj = arrayOfArrays[min][i];
						arrayOfArrays[min].splice(i, 1);
						return obj;
					}
				}
				min++;
			}
			else
			{
				if (min == minPriority)
					minPriority++;
				min++;
				if (min > maxPriority)
					break;
			}			
		}

		return obj;
	}

	/**
	 *  @private
	 */
	public function removeAll():void
	{
		arrayOfArrays.splice(0);

		minPriority = 0;
		maxPriority = -1;
	}

	/**
	 *  @private
	 */
	public function isEmpty():Boolean
	{
		return minPriority > maxPriority;
	}

	/**
	 *  @private
	 */
	private function contains(parent:DisplayObject, child:DisplayObject):Boolean
	{
		if (parent is IRawChildrenContainer)
		{
			// trace("using view rawChildren");
			var rawChildren:IChildList = IRawChildrenContainer(parent).rawChildren;
			return rawChildren.contains(child);
		}
		else if (parent is DisplayObjectContainer)
		{
			return DisplayObjectContainer(parent).contains(child);
		}

		return parent == child;
	}

}

}
