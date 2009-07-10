package org.broadleafcommerce.admin.control.events.catalog.product
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class ViewCurrentProductEvent extends CairngormEvent
	{
		public static const EVENT_VIEW_CURRENT_PRODUCT:String = "view_current_product_event";
		
		public function ViewCurrentProductEvent()
		{
			super(EVENT_VIEW_CURRENT_PRODUCT);
		}
		
	}
}