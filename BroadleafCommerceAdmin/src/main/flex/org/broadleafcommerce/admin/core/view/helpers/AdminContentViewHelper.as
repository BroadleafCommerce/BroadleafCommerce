/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.core.view.helpers
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.events.ModuleEvent;
	import mx.modules.Module;
	
	import org.broadleafcommerce.admin.core.view.AdminContent;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;

	public class AdminContentViewHelper extends ViewHelper
	{
		private var loadHelpers:ArrayCollection = new ArrayCollection();
		private var loadedCount:int;
		private var toLoadCount:int;

		public function AdminContentViewHelper()
		{
			super();
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
    		AdminContent(view).contentViewStack.addChild(module);
    		//AdminContent(view).contentLinkBar.selectedIndex = 0;
    		AdminContent(view).invalidateDisplayList();
    	}
    	
    	private function removeModulesFromView():void{
    		var adminContentView:AdminContent = AdminContent(view);
    		for each(var loadHelper:LoaderHelper in loadHelpers){
	    		adminContentView.contentViewStack.removeChild(loadHelper.loadedModule);
	    		adminContentView.invalidateDisplayList();
    		}
    		loadHelpers = new ArrayCollection();    			
    	}
    	
    	public function selectFirstModule():void{
    		AdminContent(view).bb.selectedIndex = 0;
    	}

	}
}