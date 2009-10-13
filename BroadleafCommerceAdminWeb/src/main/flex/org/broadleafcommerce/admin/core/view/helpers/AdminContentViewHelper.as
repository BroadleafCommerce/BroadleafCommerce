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
    		//AdminContent(view).contentLinkBar.selectedIndex = 0;
    		AdminContent(view).invalidateDisplayList();
    	}
    	
    	public function removeModulesFromView(module:Module):void{
    		AdminContent(view).contentViewStack.removeChild(module);
    		AdminContent(view).invalidateDisplayList();
    	}
    	
    	public function selectFirstModule():void{
    		AdminContent(view).bb.selectedIndex = 0;
    	}

	}
}