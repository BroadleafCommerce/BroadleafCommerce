package org.broadleafcommerce.admin.control.events.catalog
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class BuildCatalogTreeEvent extends CairngormEvent
	{
		
		public static const EVENT_BUILD_CATALOG_TREE:String = "build_catalog_tree_event";
		
		public function BuildCatalogTreeEvent()
		{
			super(EVENT_BUILD_CATALOG_TREE);
		}
		
	}
}