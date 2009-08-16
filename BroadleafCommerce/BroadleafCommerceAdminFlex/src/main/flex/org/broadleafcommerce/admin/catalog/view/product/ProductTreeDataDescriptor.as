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