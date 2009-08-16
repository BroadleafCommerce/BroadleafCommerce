package org.broadleafcommerce.admin.catalog.control.events.sku
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.catalog.vo.sku.Sku;

	public class EditSkuEvent extends CairngormEvent
	{
		public static const EVENT_EDIT_SKU:String = "edit_sku_event";
		
		public var sku:Sku;
		public var showSkusView:Boolean;
		
		public function EditSkuEvent(sku:Sku, showSkusView:Boolean)
		{
			super(EVENT_EDIT_SKU);
			this.sku = sku;
			this.showSkusView = showSkusView;
		}
		
	}
}