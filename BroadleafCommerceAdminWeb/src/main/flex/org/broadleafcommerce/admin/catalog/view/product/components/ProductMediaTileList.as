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
package org.broadleafcommerce.admin.catalog.view.product.components
{
	import mx.collections.ArrayCollection;
	import mx.controls.TileList;
	
	import org.broadleafcommerce.admin.catalog.model.ProductMedia;

	public class ProductMediaTileList extends TileList
	{
		public function ProductMediaTileList()
		{
			super();
		}
		
		override public function set dataProvider(value:Object):void{
			var newDataProvider:ArrayCollection = new ArrayCollection();
			for(var P in value){
				if(P is String){
					if(value[P] is String){
						var productMedia:ProductMedia = new ProductMedia();
						productMedia.label = P;
						productMedia.source = value[P];
						newDataProvider.addItem(productMedia);
//						ArrayCollection(this.dataProvider).addItem(productMedia);
					}
				}
			}
			super.dataProvider = newDataProvider;
		}
		
	}
}