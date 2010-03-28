/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.catalog.view.category.components
{

import mx.collections.ArrayCollection;
import mx.collections.ICollectionView;
import mx.controls.treeClasses.ITreeDataDescriptor;
import mx.events.CollectionEvent;
import mx.events.CollectionEventKind;

import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
import org.broadleafcommerce.admin.catalog.model.CategoryTreeItem;
import org.broadleafcommerce.admin.catalog.vo.category.Category;

/**
 *  The DefaultDataDescriptor class provides a default implementation for
 *  accessing and manipulating data for use in controls such as Tree and Menu.
 *
 *  This implementation handles e4x XML and object nodes in similar but different
 *  ways. See each method description for details on how the method
 *  accesses values in nodes of various types.
 *
 *  This class is the default value of the Tree, Menu, MenuBar, and
 *  PopUpMenuButton control <code>dataDescriptor</code> properties.
 *
 *  @see mx.controls.treeClasses.ITreeDataDescriptor
 *  @see mx.controls.menuClasses.IMenuDataDescriptor
 *  @see mx.controls.Menu
 *  @see mx.controls.MenuBar
 *  @see mx.controls.PopUpMenuButton
 *  @see mx.controls.Tree
 */
public class CategoryDefaultDataDescriptor implements ITreeDataDescriptor
{

    // The getChildren method requires the node to be an Object
    // with a children field.
    // If the field contains an ArrayCollection, it returns the field
    // Otherwise, it wraps the field in an ArrayCollection.
    public function getChildren(node:Object,
        model:Object=null):ICollectionView
    {
    	trace("DEBUG: CategoryDefaultDataDescriptor.getChildren()");            	
        try
        {
            if (node is Object) {
                if(node.children is ArrayCollection){
//                	var categoryChildren:ArrayCollection = new ArrayCollection();
//                	for each(var obj:Object in node.children){
//                		if (obj is CategoryTreeItem) categoryChildren.addItem(obj);
//                	}
                    return node.children;
//                    return categoryChildren;
                }else{
                    return new ArrayCollection(node.children);
                }
            }
        }
        catch (e:Error) {
            trace("[Descriptor] exception checking for getChildren");
        }
        return null;
    }

    // The isBranch method simply returns true if the node is an
    // Object with a children field.
    // It does not support empty branches, but does support null children
    // fields.
    public function isBranch(node:Object, model:Object=null):Boolean {
    	trace("DEBUG: CategoryDefaultDataDescriptor.isBranch()");            	
        try {
            if (node is Object) {
                if (node.children != null)  {
                    return true;
                }
            }
        }
        catch (e:Error) {
            trace("[Descriptor] exception checking for isBranch");
        }
        return false;
    }

    // The hasChildren method Returns true if the
    // node actually has children. 
    public function hasChildren(node:Object, model:Object=null):Boolean {
    	trace("DEBUG: CategoryDefaultDataDescriptor.hasChildren()");            	
        if (node == null) 
            return false;
        var children:ICollectionView = getChildren(node, model);
        try {
            if (children.length > 0)
                return true;
        }
        catch (e:Error) {
        }
        return false;
    }
    // The getData method simply returns the node as an Object.
    public function getData(node:Object, model:Object=null):Object {
    	trace("DEBUG: CategoryDefaultDataDescriptor.getData()");            	
        try {
            return Category(node);
        }
        catch (e:Error) {
        }
        return null;
    }

    // The addChildAt method does the following:
    // If the parent parameter is null or undefined, inserts
    // the child parameter as the first child of the model parameter.
    // If the parent parameter is an Object and has a children field,
    // adds the child parameter to it at the index parameter location.
    // It does not add a child to a terminal node if it does not have
    // a children field.
    public function addChildAt(parent:Object, child:Object, index:int, 
            model:Object=null):Boolean {
    	trace("DEBUG: CategoryDefaultDataDescriptor.addChildAt()");            	
        var event:CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.MOVE;
        event.items = [child];
        event.location = index;
        if (!parent) {
//            var iterator:IViewCursor = model.createCursor();
//            iterator.seek(CursorBookmark.FIRST, index);
//            iterator.insert(child);
			parent = model[0];
			index = parent.children.length;
//			return false;
        }
        //else 
        if (parent is Object) {
            if (parent != null) {
                if(parent.children is ArrayCollection) {
                	if(!parent.children.contains(child)){                		
                    	parent.children.addItemAt(child, index);                    
	                    if (model){
	                        model.dispatchEvent(event);
	                        model.itemUpdated(parent);
	                    }
	                    //saveChildCategory(parent,child);
	                    return true;
                	}
                	return false;
                }
                else {
                    parent.children.splice(index, 0, child);
                    
                    if (model)
                        model.dispatchEvent(event);
                    // saveChildCategory(parent,child);
                    return true;
                }
            }
        }
        return false;
    }
    
    private function saveChildCategory(parentObj:Object,childObj:Object):void{
    	trace("DEBUG: CategoryDefaultDataDescriptor.saveChildCategory()");            	
    	var child:Category = Category(childObj);
    	var parent:Category = Category(parentObj);
    	child.allParentCategories.addItem(parent);
    	var sce:SaveCategoryEvent = new SaveCategoryEvent(child);
    	sce.dispatch();
    	
    }

    // The removeChildAt method does the following:
    // If the parent parameter is null or undefined,
    // removes the child at the specified index
    // in the model.
    // If the parent parameter is an Object and has a children field,
    // removes the child at the index parameter location in the parent.
    public function removeChildAt(parent:Object, child:Object, index:int, model:Object=null):Boolean
    {
    	trace("DEBUG: CategoryDefaultDataDescriptor.removeChildAt()");            	
        var event:CollectionEvent = new CollectionEvent(CollectionEvent.COLLECTION_CHANGE);
        event.kind = CollectionEventKind.REMOVE;
        event.items = [child];
        event.location = index;

        //handle top level where there is no parent
        if (!parent)
        {
//            var iterator:IViewCursor = model.createCursor();
//            iterator.seek(CursorBookmark.FIRST, index);
//            iterator.remove();
//            if (model)
//                model.dispatchEvent(event);
//            return true;
			return false;
        }
        else if (parent is Object)
        {
            if (parent.children != undefined)
            {
                parent.children.splice(index, 1);
                if (model) 
                    model.dispatchEvent(event);
                removeChildFromParent(parent,child, index);
                return true;
            }
        }
        return false;
    }

	private function removeChildFromParent(parent:Object, child:Object, index:int):void{
    	trace("DEBUG: CategoryDefaultDataDescriptor.removeChildFromParent()");            	
		var parentCat:Category = Category(parent);
		parentCat.allChildCategories.removeItemAt(index);			
		var childCat:Category = Category(child);
		for (var i:String  in childCat.allParentCategories){
			if(parentCat.id == Category(childCat.allParentCategories[i]).id){
				childCat.allParentCategories.removeItemAt(int(i));
//				var sce:SaveCategoryEvent = new SaveCategoryEvent(childCat);
//				sce.dispatch();	
			}
		}	
	}

}

}
