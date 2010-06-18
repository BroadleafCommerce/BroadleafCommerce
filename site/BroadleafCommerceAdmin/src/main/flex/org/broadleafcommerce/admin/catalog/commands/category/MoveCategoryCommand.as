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
package org.broadleafcommerce.admin.catalog.commands.category
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.catalog.business.CatalogServiceDelegate;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.MoveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class MoveCategoryCommand implements Command, IResponder
	{
		public function MoveCategoryCommand()
		{
		}

		private var oldParent:Category;
		private var newParent:Category;

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: MoveCategoryCommand.execute()");			
			var mce:MoveCategoryEvent = MoveCategoryEvent(event);
			var movedCategory:Category = mce.movedCategory;
			oldParent = mce.oldParent;
			newParent = mce.newParent;
			
			for (var catIndex:String in movedCategory.allParentCategories){
				var cat:Category = Category(movedCategory.allParentCategories.getItemAt(parseInt(catIndex))); 
				if(cat.id == oldParent.id){
					movedCategory.allParentCategories.removeItemAt(parseInt(catIndex));
					break;
				} 
			}
			movedCategory.allParentCategories.addItem(newParent);
			movedCategory.defaultParentCategory = newParent;
			
			
			for (var i:String in oldParent.allChildCategories){
				var childCat:Category = Category(oldParent.allChildCategories[i]);
				if(childCat.id == movedCategory.id){
					oldParent.allChildCategories.removeItemAt(parseInt(i));
					break;
				}
			}
			
			if(mce.dropIndex > -1){
				newParent.allChildCategories.addItemAt(movedCategory, mce.dropIndex);				
			}else{
				newParent.allChildCategories.addItem(movedCategory);
			}
			

//			var sce:SaveCategoryEvent = new SaveCategoryEvent(movedCategory);			
//			sce.dispatch();
			var delegate:CatalogServiceDelegate = new CatalogServiceDelegate(this);
			delegate.updateCategoryParents(movedCategory, oldParent, newParent);
		}

		public function result(data:Object):void
		{
			trace("DEBUG: MoveCategoryCommand.result()");
			var event:ResultEvent = ResultEvent(data);
			var fce:FindAllCategoriesEvent = new FindAllCategoriesEvent();
			fce.dispatch();
		}
		
		public function fault(info:Object):void
		{
			trace("DEBUG: MoveCategoryCommand.fault()");			
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

		
	}
}