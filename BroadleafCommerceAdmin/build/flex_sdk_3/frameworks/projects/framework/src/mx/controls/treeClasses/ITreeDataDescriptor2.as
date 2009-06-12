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

package mx.controls.treeClasses
{

import mx.collections.ICollectionView;
import mx.collections.IViewCursor;

/**
 *  The ITreeDataDescriptor2 Interface defines methods for parsing and adding nodes
 *  to a collection of data that is displayed by a Tree control.
 *
 *  @see mx.collections.ICollectionView
 */
public interface ITreeDataDescriptor2 extends ITreeDataDescriptor
{
    /**
     *  Returns an ICollectionView instance that makes the hierarchical data appear
     *  as if it was a linear ICollectionView instance.
     *
     *  @param hierarchicalData The hierarchical data.
     *
     *  @param uidFunction A function that takes an Object and returns the UID, as a String. 
     *  This parameter is usually the <code>Tree.itemToUID()</code> method.
     *
     *  @param openItems The items that have been opened or set opened.
     *
     *  @param model The collection to which this node belongs.
     * 
     *  @return An ICollectionView instance.
     *
     *  @see mx.controls.Tree
     */
    function getHierarchicalCollectionAdaptor(hierarchicalData:ICollectionView, 
                                                uidFunction:Function, 
                                                openItems:Object,
                                                model:Object = null):ICollectionView;

    /**
     *  Returns the depth of the node, meaning the number of ancestors it has.
     *
     *  @param node The Object that defines the node.
     * 
     *  @param iterator An IViewCursor instance that could be used to do the calculation.
     *
     *  @param model The collection to which this node belongs.
     *  
     *  @return The depth of the node, where 0 corresponds to the top level, 
     *  and -1 if the depth cannot be calculated.
     */
    function getNodeDepth(node:Object, iterator:IViewCursor, model:Object = null):int;

    /**
     *  Returns the parent of the node
     *  The parent of a top-level node is <code>null</code>.
     *
     *  @param node The Object that defines the node.
     *
     *  @param collection An ICollectionView instance that could be used to do the calculation.
     *
     *  @param model The collection to which this node belongs.
     * 
     *  @return The parent node containing the node as child, 
     *  <code>null</code> for a top-level node,  
     *  and <code>undefined</code> if the parent cannot be determined.
     */
    function getParent(node:Object, collection:ICollectionView, model:Object = null):Object;
}

}
