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
	import mx.collections.ICollectionView;
	import mx.controls.treeClasses.ITreeDataDescriptor;
	
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;
	import mx.collections.ArrayCollection;

	public class CategoryTreeDataDescriptor implements ITreeDataDescriptor
	{
		public function CategoryTreeDataDescriptor()
		{
		}

		public function getChildren(node:Object, model:Object=null):ICollectionView
		{
//			return Category(node).allChildCategories;
			var categoryNodes:ArrayCollection = new ArrayCollection();
			for each(var object:Object in Category(node).allChildCategories){
				if(object is Category){
					categoryNodes.addItem(object);
				}
			}
			return categoryNodes;
//			// return Category(node).allChildCategories;
		}
		
		public function hasChildren(node:Object, model:Object=null):Boolean
		{
			//return (Category(node).allChildCategories.length > 0);
//			for each(var object:Object in Category(node).allChildCategories){
//				if(object is Category){
					return true;
//				}
//			}
//			return false;
		}
		
		public function isBranch(node:Object, model:Object=null):Boolean
		{
			var result:Boolean = false;
			
//			if(node is Category){
//				 for each(var cat:Object in Category(node).allChildCategories){
//				 	if(cat is Category){
				 		result = true;
//				 		break;
//				 	}
//				 }
//				
//			} 
			return result;
		}
		
		public function getData(node:Object, model:Object=null):Object
		{
			return Category(node);
		}
		
		public function addChildAt(parent:Object, newChild:Object, index:int, model:Object=null):Boolean
		{
			var childCat:Category = Category(newChild);
			var parentCat:Category = Category(parent);
//			if(Category(parentCat).allChildCategories.contains(childCat)){
//				trace("DEBUG: CategoryTreeDataDescriptor.addChildAt() -- don't change anything since user is dropping to same location"); 
//				return false;
//			}
			if(parentCat == null && model != null && model[0] != null){
				parentCat = 	Category(model[0])	
			}
			childCat.allParentCategories.addItem(parentCat);
			parentCat.allChildCategories.addItemAt(childCat,index);				
		
			var scce:SaveCategoryEvent = new SaveCategoryEvent(childCat);
			scce.dispatch(
			);
//			function ():void{
//				var scce:SaveCategoryEvent = new SaveCategoryEvent(parentCat);
//				scce.dispatch();			
//				});
		
			return true;
		}

		
		public function removeChildAt(parent:Object, child:Object, index:int, model:Object=null):Boolean
		{
			var parentCat:Category = Category(parent);
			parentCat.allChildCategories.removeItemAt(index);;			
			var childCat:Category = Category(child);
			for (var i:String  in childCat.allParentCategories){
				if(parentCat.id == Category(childCat.allParentCategories[i]).id){
					childCat.allParentCategories.removeItemAt(int(i));
				}
			}
			return true;
		}
		
	}
}