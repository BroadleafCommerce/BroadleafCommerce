package org.broadleafcommerce.admin.catalog.view
{
	import com.adobe.cairngorm.view.ViewHelper;

	public class CatalogCanvasViewHelper extends ViewHelper
	{
		public function CatalogCanvasViewHelper()
		{
			super();
		}
		
		public function getViewIndex():String{
			return CatalogCanvas(view).currentState;
		}
		
	}
}