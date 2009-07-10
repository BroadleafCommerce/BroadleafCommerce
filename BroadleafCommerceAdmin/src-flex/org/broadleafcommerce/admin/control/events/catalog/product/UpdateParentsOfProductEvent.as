package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.model.data.remote.catalog.product.Product;

	public class UpdateParentsOfProductEvent extends CairngormEvent
	{
		public static const EVENT_ADD_PARENT_TO_PRODUCT:String = "add_parent_to_product_event";
		
		public var product:Product;
		public var parents:ArrayCollection;
		
		public function UpdateParentsOfProductEvent(product:Product, parents:ArrayCollection)
		{
			super(EVENT_ADD_PARENT_TO_PRODUCT);
			this.product = product;
			this.parents = parents;
		}
		
	}
}