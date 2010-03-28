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
package org.broadleafcommerce.admin.catalog.commands.product
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.view.product.ProductCanvasViewHelper;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class AddProductMediaCommand implements Command
	{
		public function AddProductMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var newMedia:Media = new Media();
			var productMedia:ArrayCollection = CatalogModelLocator.getInstance().productModel.productMedia;
			productMedia.addItem(newMedia);
			
			ProductCanvasViewHelper(ViewLocator.getInstance().getViewHelper("productCanvasViewHelper")).editMedia(newMedia);
			
		}
		
	}
}