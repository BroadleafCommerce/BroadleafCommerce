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
	
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.FindAllSkusEvent;

	public class RetrieveCatalogCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		public function RetrieveCatalogCommand()
		{
			trace("DEBUG: new RetrieveCatalogCommand()");
			eventChain.addItem(new FindAllCategoriesEvent());
			//eventChain.addItem(new FindAllProductsEvent());
			//eventChain.addItem(new FindAllSkusEvent());
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: RetrieveCatalogCommand.execute()");
				for each(var event:CairngormEvent in eventChain){
					event.dispatch();
				}
		}
		
	}
}