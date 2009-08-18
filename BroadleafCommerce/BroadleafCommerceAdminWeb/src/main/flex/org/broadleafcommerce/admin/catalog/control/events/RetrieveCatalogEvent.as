package org.broadleafcommerce.admin.catalog.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class RetrieveCatalogEvent extends CairngormEvent
	{
		public static const EVENT_RETRIEVE_CATALOG:String = "retrieve_catalog_event";
		
		public function RetrieveCatalogEvent()
		{
			super(EVENT_RETRIEVE_CATALOG);
		}
		
	}
}