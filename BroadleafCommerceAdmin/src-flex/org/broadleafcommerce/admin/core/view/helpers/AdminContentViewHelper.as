package org.broadleafcommerce.admin.core.view.helpers
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.modules.Module;
	
	import org.broadleafcommerce.admin.core.view.AdminContent;

	public class AdminContentViewHelper extends ViewHelper
	{
		public function AdminContentViewHelper()
		{
			super();
		}
		
		
    	public function addModuleToView(module:Module):void{
    		AdminContent(view).contentViewStack.addChild(module);
    		AdminContent(view).contentLinkBar.selectedIndex = 0;
    	}
		
	}
}