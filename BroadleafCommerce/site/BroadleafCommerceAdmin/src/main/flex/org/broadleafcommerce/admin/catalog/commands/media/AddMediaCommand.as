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
package org.broadleafcommerce.admin.catalog.commands.media
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.ProductModel;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;

	public class AddMediaCommand implements Command
	{
		public function AddMediaCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: execute : ");
			var newMedia:Media = new Media();
			CatalogModelLocator.getInstance().mediaModel.currentMedia = newMedia;	
			var x:ProductModel = CatalogModelLocator.getInstance().productModel;		
		}
		
	}
}