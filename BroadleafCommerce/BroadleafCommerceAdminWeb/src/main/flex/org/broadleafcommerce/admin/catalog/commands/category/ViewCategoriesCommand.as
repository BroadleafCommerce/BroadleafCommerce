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

	import org.broadleafcommerce.admin.catalog.model.CatalogModel;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;

	public class ViewCategoriesCommand implements Command
	{
		public function ViewCategoriesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: ViewCategoriesCommand.execute()");
			var catalogModel:CatalogModel = CatalogModelLocator.getInstance().catalogModel;
			catalogModel.viewState = CatalogModel.STATE_VIEW_CATEGORY;

			CatalogModelLocator.getInstance().categoriesSelected = true;
			CatalogModelLocator.getInstance().productsSelected = false;
		}

	}
}