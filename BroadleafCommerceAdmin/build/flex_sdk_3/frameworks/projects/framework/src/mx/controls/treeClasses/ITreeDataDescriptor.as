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

package mx.controls.treeClasses
{

import mx.collections.ICollectionView;

/**
 *  Interface providing methods for parsing and adding nodes
 *  to a collection of data that is displayed by a Tree control.
 *
 *  @see mx.collections.ICollectionView
 */
public interface ITreeDataDescriptor
{
    /**
     *  Provides access to a node's children, returning a collection
     *  view of children if they exist.
     *  A node can return any object in the collection as its children;
     *  children need not be nested.
     *  It is best-practice to return the same collection view for a
     *  given node.
     *
     *  @param node The node object currently being evaluated.
     *
     *  @param model The entire collection that this node is a part of.
     *
     *  @return An collection view containing the child nodes.
     */
    function getChildren(node:Object, model:Object = null):ICollectionView;

    /**
     *  Tests for the existence of children in a non-terminating node.
     *  
     *  @param node The current node.
     *  
     *  @param model The entire collection that this node is a part of.
     *  
     *  @return <code>true</code> if the node has at least one child.
     */
    function hasChildren(node:Object, model:Object = null):Boolean;

    /**
     *  Tests a node for termination.
     *  Branches are non-terminating but are not required
     *  to have any leaf nodes.
     *
     *  @param node The node object currently being evaluated.
     *
     *  @param model The entire collection that this node is a part of.
     *
     *  @return A Boolean indicating if this node is non-terminating.
     */
    function isBranch(node:Object, model:Object = null):Boolean;

    /**
     *  Gets the data from a node.
     *
     *  @param node The node object from which to get the data.
     *
     *  @param model The collection that contains the node.
     *
     *  @return The requested data.
     */
     function getData(node:Object, model:Object = null):Object;

    /**
     *  Adds a child node to a node at the specified index.
     *
     *  @param node The node object that will parent the child.
     *
     *  @param child The node object that will be parented by the node.
     *
     *  @param index The 0-based index of where to put the child node.
     *
     *  @param model The entire collection that this node is a part of.
     *  
     *  @return <code>true</code> if successful.
     */
    function addChildAt(parent:Object, newChild:Object,
                        index:int, model:Object = null):Boolean;

    /**
     *  Removes a child node to a node at the specified index.
     *
     *  @param node The node object that is the parent of the child.
     *
     *  @param child The node object that will be removed.
     *
     *  @param index The 0-based index of the soon to be deleted node.
     *
     *  @param model The entire collection that this node is a part of.
     *  
     *  @return <code>true</code> if successful.
     */
    function removeChildAt(parent:Object, child:Object,
                           index:int, model:Object = null):Boolean;
}

}
