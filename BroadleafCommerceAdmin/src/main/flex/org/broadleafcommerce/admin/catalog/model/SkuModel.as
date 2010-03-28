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
package org.broadleafcommerce.admin.catalog.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;
	
	[Bindable]
	public class SkuModel
	{
		public function SkuModel()
		{
		}
		
		public static const STATE_NONE:String = "none";
		public static const STATE_NEW:String = "new_sku";
		public static const STATE_EDIT:String = "edit_sku";
		
		public var viewState:String = STATE_NEW;

		public var catalogSkus:ArrayCollection = new ArrayCollection();

		public var currentSku:Sku = new Sku();	
		
		public var viewSkus:ArrayCollection = new ArrayCollection();

	}
}