package org.broadleafcommerce.admin.model.view
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.sku.Sku;
	
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