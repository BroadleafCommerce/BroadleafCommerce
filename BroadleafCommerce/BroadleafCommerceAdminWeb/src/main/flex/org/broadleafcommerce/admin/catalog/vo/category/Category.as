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
package org.broadleafcommerce.admin.catalog.vo.category
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.catalog.domain.CategoryImpl")]
	public class Category
	{
		public var id:int;
		public var name:String;
		public var url:String;
		public var urlKey:String;
		public var defaultParentCategory:Category;
		public var description:String;
		public var activeStartDate:Date;
		public var activeEndDate:Date;
		public var displayTemplate:String;
		public var allChildCategories:ArrayCollection = new ArrayCollection();
		public var allParentCategories:ArrayCollection = new ArrayCollection();
		public var categoryImages:Object = new Object();
		public var categoryMedia:Object = new Object();
		public var longDescription:String;
		public var featuredProducts:ArrayCollection = new ArrayCollection();

	}
}