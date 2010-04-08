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
	import flash.events.EventDispatcher;
	
	import mx.events.ModuleEvent;
	import mx.modules.IModuleInfo;
	import mx.modules.Module;
	import mx.modules.ModuleManager;
	
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;
	
	public class LoaderHelper extends EventDispatcher
	{
		public var moduleConfig:ModuleConfig;
		public var loadedModule:Module;		
		private var moduleInfo:IModuleInfo;
		private var configModel:ConfigModel = AppModelLocator.getInstance().configModel;
		
		public function LoaderHelper(moduleConfig:ModuleConfig = null)
		{
			this.moduleConfig = moduleConfig;
		}
		
		public function load():void{      
		  var fullUrl:String = AppModelLocator.getInstance().configModel.urlServer+moduleConfig.swf;
		  moduleInfo = ModuleManager.getModule(fullUrl); 	     
	      moduleInfo.addEventListener(ModuleEvent.READY,handleModuleReady); 
	      moduleInfo.addEventListener(ModuleEvent.ERROR, handleModuleError);
	      moduleInfo.load();
		}

		public function handleModuleReady(e:Event):void{  
			var mod:Module = moduleInfo.factory.create() as Module;  
			loadedModule = mod;
			this.dispatchEvent(e);
		}   

		public function handleModuleError(e:ModuleEvent):void{
			dispatchEvent(e);
		}
		
		public function unloadModule():void{
			moduleInfo.unload();
			moduleInfo.release();
		}

	}
}