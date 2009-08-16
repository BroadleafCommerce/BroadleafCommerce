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

		public var catalogSkus:ArrayCollection = new ArrayCollection();

		public var currentSku:Sku = new Sku();	
		
		public var viewSkus:ArrayCollection = new ArrayCollection();

	}
}