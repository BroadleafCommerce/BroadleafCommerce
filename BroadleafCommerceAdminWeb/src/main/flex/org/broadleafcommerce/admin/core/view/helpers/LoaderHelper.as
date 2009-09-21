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
	import flash.events.Event;
	
	import mx.controls.Alert;
	import mx.core.Application;
	import mx.events.ModuleEvent;
	import mx.modules.IModuleInfo;
	import mx.modules.Module;
	import mx.modules.ModuleManager;
	
	import org.broadleafcommerce.admin.core.control.events.AddModulesToViewEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;
	
	public class LoaderHelper
	{
		public var module:ModuleConfig
		private var moduleInfo:IModuleInfo;
		
		public function LoaderHelper(module:ModuleConfig = null)
		{
			this.module = module;
		}
		
		public function load():void{      
			var fullUrl:String = AppModelLocator.getInstance().configModel.urlPrefix+module.swf;
		  moduleInfo = ModuleManager.getModule(fullUrl); 	     
	      moduleInfo.addEventListener(ModuleEvent.READY,addH);
	      moduleInfo.addEventListener(ModuleEvent.ERROR, handleModuleError);
	      moduleInfo.load();
		}

		public function addH(e:Event):void{  
			var mod:Module = moduleInfo.factory.create() as Module; 
			module.loadedModule = mod;
			AppModelLocator.getInstance().configModel.modulesLoaded.addItem(mod);
			if(AppModelLocator.getInstance().authModel.authenticatedModules.length ==
			  AppModelLocator.getInstance().configModel.modulesLoaded.length){
			  var amtve:AddModulesToViewEvent = new AddModulesToViewEvent();
			  amtve.dispatch();  
			}
		}   

		public function handleModuleError(e:ModuleEvent):void{
			Alert.show("Module loading error: "+e.errorText);
		}

	}
}