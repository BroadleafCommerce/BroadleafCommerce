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

package mx.core
{

import flash.display.DisplayObject;
import flash.geom.Point;

[ExcludeClass]

/**
 *  @private
 *  Helper class for the rawChildren property of the Container class.
 *  For descriptions of the properties and methods,
 *  see the IChildList interface.
 *
 *  @see mx.core.Container
 */
public class ContainerRawChildrenList implements IChildList
{
	include "../core/Version.as";

	//--------------------------------------------------------------------------
	//
	//  Notes
	//
	//--------------------------------------------------------------------------

	/*

		Although at the level of a Flash DisplayObjectContainer, all
		children are equal, in a Flex Container some children are "more
		equal than others". (George Orwell, "Animal Farm")
		
		In particular, Flex distinguishes between content children and
		non-content (or "chrome") children. Content children are the kind
		that can be specified in MXML. If you put several controls
		into a VBox, those are its content children. Non-content children
		are the other ones that you get automatically, such as a
		background/border, scrollbars, the titlebar of a Panel,
		AccordionHeaders, etc.

		Most application developers are uninterested in non-content children,
		so Container overrides APIs such as numChildren and getChildAt()
		to deal only with content children. For example, Container, keeps
		its own _numChildren counter.

		However, developers of custom containers need to be able to deal
		with both content and non-content children, so they require similar
		APIs that operate on all children.

		For the public API, it would be ugly to have double APIs on Container
		such as getChildAt() and all_getChildAt(). Instead, Container has
		a public rawChildren property which lets you access APIs which
		operate on all the children, in the same way that the
		DisplayObjectContainer APIs do. For example, getChildAt(0) returns
		the first content child, while rawChildren.getChildAt(0) returns
		the first child (either content or non-content).

		This ContainerRawChildrenList class implements the rawChildren
		property. Note that it simply calls a second set of parallel
		mx_internal APIs in Container. (They're named, for example,
		_getChildAt() instead of all_getChildAt()).

		Many of the all-children APIs in Container such as _getChildAt()
		simply call super.getChildAt() in order to get the implementation
		in DisplayObjectContainer. It would be nice if we could eliminate
		_getChildAt() in Container and simply implement the all-children
		version in this class by calling the DisplayObjectContainer method.
		But once Container overrides getChildAt(), there is no way
		to call the supermethod through an instance.

	*/
	
	//--------------------------------------------------------------------------
	//
	//  Constructor
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 *  Constructor.
	 */
	public function ContainerRawChildrenList(owner:Container)
	{
		super();

		this.owner = owner;
	}
	
	//--------------------------------------------------------------------------
	//
	//  Variables
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	private var owner:Container;

	//--------------------------------------------------------------------------
	//
	//  Properties
	//
	//--------------------------------------------------------------------------

	//----------------------------------
	//  numChildren
	//----------------------------------

	/**
	 *  @private
	 */
	public function get numChildren():int
	{
		return owner.mx_internal::$numChildren;
	}

	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

	/**
	 *  @private
	 */
	public function addChild(child:DisplayObject):DisplayObject
	{
		return owner.mx_internal::rawChildren_addChild(child);
	}

	/**
	 *  @private
	 */
	public function addChildAt(child:DisplayObject, index:int):DisplayObject
	{
		return owner.mx_internal::rawChildren_addChildAt(child, index);
	}

	/**
	 *  @private
	 */
	public function removeChild(child:DisplayObject):DisplayObject
	{
		return owner.mx_internal::rawChildren_removeChild(child);
	}

	/**
	 *  @private
	 */
	public function removeChildAt(index:int):DisplayObject
	{
		return owner.mx_internal::rawChildren_removeChildAt(index);
	}

	/**
	 *  @private
	 */
  	public function getChildAt(index:int):DisplayObject
  	{
		return owner.mx_internal::rawChildren_getChildAt(index);
  	}

	/**
	 *  @private
	 */
  	public function getChildByName(name:String):DisplayObject
  	{
		return owner.mx_internal::rawChildren_getChildByName(name);
  	}

	/**
	 *  @private
	 */
  	public function getChildIndex(child:DisplayObject):int
  	{
		return owner.mx_internal::rawChildren_getChildIndex(child);
	}

	/**
	 *  @private
	 */
	public function setChildIndex(child:DisplayObject, newIndex:int):void
	{		
		owner.mx_internal::rawChildren_setChildIndex(child, newIndex);
	}
	
	/**
	 *  @private
	 */
	public function getObjectsUnderPoint(point:Point):Array
	{
		return owner.mx_internal::rawChildren_getObjectsUnderPoint(point);
	}

	/**
	 *  @private
	 */
	public function contains(child:DisplayObject):Boolean
	{
		return owner.mx_internal::rawChildren_contains(child);
	}	
}

}
