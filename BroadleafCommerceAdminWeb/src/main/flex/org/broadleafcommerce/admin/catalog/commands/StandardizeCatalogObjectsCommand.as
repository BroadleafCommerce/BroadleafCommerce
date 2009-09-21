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
package org.broadleafcommerce.admin.catalog.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryTreeItem;
	import org.broadleafcommerce.admin.catalog.vo.category.Category;

	public class StandardizeCatalogObjectsCommand implements Command
	{
		public function StandardizeCatalogObjectsCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: StandardizeCatalogObjectsCommand.execute()");
			var scoe:StandardizeCatalogObjectsEvent = StandardizeCatalogObjectsEvent(event);
			var categoryArray:ArrayCollection = scoe.categoryArray;
			var productArray:ArrayCollection = scoe.productArray;
			var skuArray:ArrayCollection = scoe.skuArray;
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;

			var categoryTreeItemArray:ArrayCollection = new ArrayCollection();

			for each(var refCategory:Category in categoryArray){
				var categoryTreeItem:CategoryTreeItem = new CategoryTreeItem(refCategory);
				categoryTreeItemArray.addItem(categoryTreeItem);
			}
			
			catalogModel.catalogTreeItemArray = categoryTreeItemArray;
			

		}
		
	}
}