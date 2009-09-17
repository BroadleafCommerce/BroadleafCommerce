package org.broadleafcommerce.admin.catalog.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class BuildCatalogChainEvent extends CairngormEvent
	{
		
		public static const EVENT_BUILD_CATALOG_TREE:String = "build_catalog_tree_event";
		
		public function BuildCatalogChainEvent()
		{
			super(EVENT_BUILD_CATALOG_TREE);
		}
		
	}
}