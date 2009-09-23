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
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.CategoryModel;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.model.SkuModel;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class BuildCatalogChainCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();
		
		public function BuildCatalogChainCommand()
		{
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;			
			var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
			var skuModel:SkuModel = CatalogModelLocator.getInstance().skuModel;



			eventChain.addItem(new StandardizeCatalogObjectsEvent(categoryModel.categoryArray, 
															      productModel.catalogProducts, 
															      skuModel.catalogSkus));
			eventChain.addItem(new AddCategoriesToCatalogTreeEvent(catalogModel.catalogTree, catalogModel.catalogTreeItemArray));		
																					  
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: BuildCatalogCommand.execute()");
			var categoriesArray:ArrayCollection = CatalogModelLocator.getInstance().categoryModel.categoryArray;

			if(categoriesArray.length > 0)
			{	
				
				var codes:ArrayCollection = AppModelLocator.getInstance().configModel.codeTypes;
				var categoryModel:CategoryModel = CatalogModelLocator.getInstance().categoryModel;
				var productModel:ProductModel = CatalogModelLocator.getInstance().productModel;
				categoryModel.categoryMediaCodes = new ArrayCollection();
				productModel.productMediaCodes = new ArrayCollection();
				for each(var codeType:CodeType in codes){
					if(codeType.codeType == "CATEGORY_MEDIA"){
						categoryModel.categoryMediaCodes.addItem(codeType);
					}
					if(codeType.codeType == "PRODUCT_MEDIA") {
						productModel.productMediaCodes.addItem(codeType);
					}	
				}
						
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
			}
		}
		
	}
}