package org.broadleafcommerce.admin.core.view.helpers
{
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.ModuleEvent;
	import mx.modules.Module;
	
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;
	
	public class ModuleLoader
	{
		private var loadedCount:int;
		private var toLoadCount:int;
		private var loadHelpers:ArrayCollection = new ArrayCollection();
		
		public function ModuleLoader()
		{
		}

		public function loadModules(modules:ArrayCollection):void{
			loadedCount = 0;
			toLoadCount = modules.length;
			for each(var module:ModuleConfig in modules){
				//AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).loadModule(module);
	      		var lh:LoaderHelper = new LoaderHelper(module);
	      		loadHelpers.addItem(lh);
	      		lh.addEventListener(ModuleEvent.READY,handleModuleLoaded);
	      		lh.addEventListener(ModuleEvent.ERROR,handleModuleError);
	      		lh.load();
			}
		}

		public function unloadModules():void{
			for each(var loadHelper:LoaderHelper in loadHelpers){
				loadHelper.unloadModule();
			}
			removeModulesFromView();
		}

		private function handleModuleLoaded(event:ModuleEvent):void{
			loadedCount++;
			checkLoadHelperStatus();
		}
		
		private function handleModuleError(event:ModuleEvent):void{
			toLoadCount --;
			Alert.show("Module loading error: "+event.errorText);			
			checkLoadHelperStatus();
		}
		
		private function checkLoadHelperStatus():void{
			if(loadedCount == toLoadCount){
				loadedCount = 0;
				toLoadCount = 0;
				for each(var loadHelper:LoaderHelper in loadHelpers){
					var module:Module = loadHelper.loadedModule;
					addModuleToView(module);
				} 
			}
		}

    	private function addModuleToView(module:Module):void{
    		trace("adding module "+module.name);
//    		AdminContent(view).contentViewStack.addChild(module);
    		//AdminContent(view).contentLinkBar.selectedIndex = 0;
//    		AdminContent(view).invalidateDisplayList();
    	}
    	
    	private function removeModulesFromView():void{
//    		var adminContentView:AdminContent = AdminContent(view);
    		for each(var loadHelper:LoaderHelper in loadHelpers){
//	    		adminContentView.contentViewStack.removeChild(loadHelper.loadedModule);
//	    		adminContentView.invalidateDisplayList();
    		}
    		loadHelpers = new ArrayCollection();    			
    	}
    	

	}
}