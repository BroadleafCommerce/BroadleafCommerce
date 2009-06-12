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

package mx.managers
{

import flash.display.DisplayObject;
import flash.geom.Point;
import mx.core.IChildList;
import mx.core.mx_internal;

[ExcludeClass]

/**
 *  @private
 *  A SystemManager has various types of children,
 *  such as the Application, popups, 
 *  tooltips, and custom cursors.
 *  You can access the just the custom cursors through
 *  the <code>cursors</code> property,
 *  the tooltips via <code>toolTips</code>, and
 *  the popups via <code>popUpChildren</code>.  Each one returns
 *  a SystemChildrenList which implements IChildList.  The SystemManager's
 *  IChildList methods return the set of children that aren't popups, tooltips
 *  or cursors.  To get the list of all children regardless of type, you
 *  use the rawChildrenList property which returns this SystemRawChildrenList.
 */
public class SystemRawChildrenList implements IChildList
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
	public function SystemRawChildrenList(owner:SystemManager)
	{
		super();

		this.owner = owner;
	}

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var owner:SystemManager;

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @copy mx.core.IChildList#numChildren
	 */
	public function get numChildren():int
	{
		return owner.mx_internal::$numChildren;
	}

	/**
	 *  @copy mx.core.IChildList#getChildAt
	 */
  	public function getChildAt(index:int):DisplayObject
  	{
		return owner.mx_internal::rawChildren_getChildAt(index);
  	}

	/**
	 *  @copy mx.core.IChildList#addChild
	 */
	public function addChild(child:DisplayObject):DisplayObject
  	{
		return owner.mx_internal::rawChildren_addChild(child);
  	}
	
	/**
	 *  @copy mx.core.IChildList#addChildAt
	 */
	public function addChildAt(child:DisplayObject, index:int):DisplayObject
  	{
		return owner.mx_internal::rawChildren_addChildAt(child,index);
  	}
	
	/**
	 *  @copy mx.core.IChildList#removeChild
	 */
	public function removeChild(child:DisplayObject):DisplayObject
  	{
		return owner.mx_internal::rawChildren_removeChild(child);
  	}
	
	/**
	 *  @copy mx.core.IChildList#removeChildAt
	 */
	public function removeChildAt(index:int):DisplayObject
  	{
		return owner.mx_internal::rawChildren_removeChildAt(index);
  	}
	
	/**
	 *  @copy mx.core.IChildList#getChildByName
	 */
  	public function getChildByName(name:String):DisplayObject
  	{
		return owner.mx_internal::rawChildren_getChildByName(name);
	}
	
	/**
	 *  @copy mx.core.IChildList#getChildIndex
	 */
  	public function getChildIndex(child:DisplayObject):int
  	{
		return owner.mx_internal::rawChildren_getChildIndex(child);
  	}
	
	/**
	 *  @copy mx.core.IChildList#setChildIndex
	 */
	public function setChildIndex(child:DisplayObject, newIndex:int):void
  	{
		owner.mx_internal::rawChildren_setChildIndex(child, newIndex);
  	}
	
	/**
	 *  @copy mx.core.IChildList#getObjectsUnderPoint
	 */
	public function getObjectsUnderPoint(point:Point):Array
	{
		return owner.mx_internal::rawChildren_getObjectsUnderPoint(point);
	}
	
	/**
	 *  @copy mx.core.IChildList#contains
	 */
	public function contains(child:DisplayObject):Boolean
	{
		return owner.mx_internal::rawChildren_contains(child);
	}
}

}
