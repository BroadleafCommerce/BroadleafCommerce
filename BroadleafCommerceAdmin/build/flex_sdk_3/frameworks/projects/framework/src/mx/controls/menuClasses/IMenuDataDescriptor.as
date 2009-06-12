////////////////////////////////////////////////////////////////////////////////
//
//  ADOBE SYSTEMS INCORPORATED
//  Copyright 2006-2007 Adobe Systems Incorporated
//  All Rights Reserved.
//
//  NOTICE: Adobe permits you to use, modify, and distribute this file
//  in accordance with the terms of the license agreement accompanying it.
//
////////////////////////////////////////////////////////////////////////////////

package mx.controls.menuClasses
{

import mx.collections.ICollectionView;

/**
 *  The IMenuDataDescriptor interface defines the interface that a 
 *  dataDescriptor for a Menu or MenuBar control must implement. 
 *  The interface provides methods for parsing and modifyng a collection
 *  of data that is displayed by a Menu or MenuBar control.
 *
 *  @see mx.collections.ICollectionView
 */
public interface IMenuDataDescriptor
{
	//--------------------------------------------------------------------------
	//
	//  Methods
	//
	//--------------------------------------------------------------------------

    /**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#getChildren()  
     */
	function getChildren(node:Object, model:Object = null):ICollectionView;
	
	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#hasChildren() 
     */
	function hasChildren(node:Object, model:Object = null):Boolean;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#getData() 
     */
	function getData(node:Object, model:Object = null):Object;

    /**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#isBranch() 
     */
	function isBranch(node:Object, model:Object = null):Boolean;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#getType()
     */
	function getType(node:Object):String;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#addChildAt()
     */
    function addChildAt(parent:Object, newChild:Object, index:int,
						model:Object = null):Boolean;

    /**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#removeChildAt()
     */
    function removeChildAt(parent:Object, child:Object, index:int,
						   model:Object = null):Boolean;
	
	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#isEnabled()
     */
	function isEnabled(node:Object):Boolean;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#setEnabled()
     */
	function setEnabled(node:Object, value:Boolean):void;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#isToggled()
     */
	function isToggled(node:Object):Boolean;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#setToggled()
     */
	function setToggled(node:Object, value:Boolean):void;

	/**
     *  @copy mx.controls.treeClasses.DefaultDataDescriptor#getGroupName()
     */
	function getGroupName(node:Object):String;
}

}
