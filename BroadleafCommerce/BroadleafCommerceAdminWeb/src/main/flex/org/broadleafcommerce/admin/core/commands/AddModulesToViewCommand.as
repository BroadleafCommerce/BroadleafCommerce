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
package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.view.helpers.AdminContentViewHelper;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;

	public class AddModulesToViewCommand implements Command
	{
		public function AddModulesToViewCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var modulesLoaded:ArrayCollection = AppModelLocator.getInstance().configModel.modulesLoaded;
			var modules:ArrayCollection = AppModelLocator.getInstance().authModel.authenticatedModules;
			if(modulesLoaded.length == modules.length){
				for (var index:String in modulesLoaded){
					AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).addModuleToView(ModuleConfig(modules[index]).loadedModule);
					//Application.application.adminContent.contentViewStack.addChildAt(ModuleConfig(modules[index]).loadedModule,index);
				}
			}else{
				Alert.show("Error loading all modules");
			}
		}
		
	}
}