package org.broadleafcommerce.admin.core.view.helpers
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.collections.ArrayCollection;
	import mx.modules.Module;
	
	import org.broadleafcommerce.admin.core.view.AdminContent;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;

	public class AdminContentViewHelper extends ViewHelper
	{
		private var loadHelpers:ArrayCollection = new ArrayCollection();
		
		public function AdminContentViewHelper()
		{
			super();
		}
		
		public function loadModule(module:ModuleConfig):void{
			var lh:LoaderHelper = new LoaderHelper(module);
			loadHelpers.addItem(lh);
			lh.load();
		}
		
		public function loadModules(modules:ArrayCollection):void{
			for each(var module:ModuleConfig in modules){
				//AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).loadModule(module);
	      		var lh:LoaderHelper = new LoaderHelper(module);
	      		loadHelpers.addItem(lh);
	      		lh.load();								
			}			
		}
		
    	public function addModuleToView(module:Module):void{
    		AdminContent(view).contentViewStack.addChild(module);
    		AdminContent(view).contentLinkBar.selectedIndex = 0;
    		AdminContent(view).invalidateDisplayList();
    	}
		
	}
}