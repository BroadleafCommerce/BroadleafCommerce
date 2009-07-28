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