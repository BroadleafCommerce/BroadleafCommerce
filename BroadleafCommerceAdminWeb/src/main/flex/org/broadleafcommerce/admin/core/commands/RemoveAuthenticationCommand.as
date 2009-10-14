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
	import mx.modules.ModuleLoader;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.core.business.AdminAuthenticationDelegate;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.AuthenticationModel;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.view.helpers.AdminContentViewHelper;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;
	
	public class RemoveAuthenticationCommand implements Command, IResponder
	{
		
		private var authModel:AuthenticationModel = AppModelLocator.getInstance().authModel;
		private var configModel:ConfigModel = AppModelLocator.getInstance().configModel;
		
		public function RemoveAuthenticationCommand()
		{
		}
		
		public function execute(event:CairngormEvent):void{
			var adminContentVH:AdminContentViewHelper = AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent"));

			adminContentVH.unloadModules();
					
//			removeModulesFromView();
//			unloadModules();
//			clearModules();
			authModel.authenticatedState = AuthenticationModel.STATE_APP_ANONYMOUS;
			var aad:AdminAuthenticationDelegate = new AdminAuthenticationDelegate(this);
			aad.logout();
		}
		
		private function unloadModules():void{
			var adminContentVH:AdminContentViewHelper = AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent"));
//			for each(var moduleConfig:ModuleConfig in authModel.authenticatedModuleConfigs){
//				var url:String = configModel.urlPrefix + moduleConfig.swf;
//				var moduleLoader:ModuleLoader = new ModuleLoader();
//				moduleLoader.url = url;
//				moduleLoader.unloadModule();
//			}				
		}
		
		private function clearModules():void{
//			authModel.authenticatedModuleConfigs = new ArrayCollection();
//			configModel.modulesLoaded = new ArrayCollection();
		} 
		
		private function removeModulesFromView():void{
//			var modulesLoaded:ArrayCollection = AppModelLocator.getInstance().configModel.modulesLoaded;
//			var modules:ArrayCollection = AppModelLocator.getInstance().authModel.authenticatedModuleConfigs;
//			if(modulesLoaded.length == modules.length){
//				for (var index:String in modulesLoaded){
//					AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).removeModulesFromView(ModuleConfig(modules[index]).loadedModule);
//					//Application.application.adminContent.contentViewStack.addChildAt(ModuleConfig(modules[index]).loadedModule,index);
//				}
//					AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).selectFirstModule();			
//			}else{
//				Alert.show("Error loading all modules");
//			}
		}
		
		public function result(data:Object):void{
			
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("ERROR: " + event);
		}
		
	}
}