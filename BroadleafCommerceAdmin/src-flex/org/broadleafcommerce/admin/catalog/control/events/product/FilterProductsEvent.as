package org.broadleafcommerce.admin.catalog.control.events.product
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FilterProductsEvent extends CairngormEvent
	{
		public static const EVENT_FILTER_PRODUCTS:String = "filter_products_event";
		
		public var filterString:String;
		
		public function FilterProductsEvent(filterString:String)
		{
			super(EVENT_FILTER_PRODUCTS);
			this.filterString = filterString;
		}
		
	}
}