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
package org.broadleafcommerce.admin.view.catalog.product
{
	import mx.collections.ICollectionView;
	import mx.controls.treeClasses.ITreeDataDescriptor;
	
	import org.broadleafcommerce.admin.catalog.vo.product.Product;

	public class ProductTreeDataDescriptor implements ITreeDataDescriptor
	{
		public function ProductTreeDataDescriptor()
		{
		}

		public function getChildren(node:Object, model:Object=null):ICollectionView
		{
			return Product(node).allSkus;
		}
		
		public function hasChildren(node:Object, model:Object=null):Boolean
		{
			return (Product(node).allSkus.length > 0);
		}
		
		public function isBranch(node:Object, model:Object=null):Boolean
		{
			return (node is Product);
		}
		
		public function getData(node:Object, model:Object=null):Object
		{
			return Product(node);
		}
		
		public function addChildAt(parent:Object, newChild:Object, index:int, model:Object=null):Boolean
		{
			return false;
		}
		
		public function removeChildAt(parent:Object, child:Object, index:int, model:Object=null):Boolean
		{
			return false;
		}
		
	}
}