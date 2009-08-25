package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.modules.IModuleInfo;
	
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.view.helpers.AdminContentViewHelper;
	import org.broadleafcommerce.admin.core.vo.ModuleConfig;

	public class LoadModulesCommand implements Command
	{
		private var moduleInfo:IModuleInfo;
		private var modules:ArrayCollection;
		private var i:int =0;

		public function LoadModulesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var lme:LoadModulesEvent = LoadModulesEvent(event);
			var userRoles:Object = AppModelLocator.getInstance().authModel.userPrincipal.allRoles;
			var authModules:ArrayCollection = AppModelLocator.getInstance().authModel.authenticatedModules;
			modules = lme.modules;
			AppModelLocator.getInstance().authModel.authenticatedModules = new ArrayCollection();

			for each(var module:ModuleConfig in modules){
				for each(var role:Object in userRoles){
					if(role["name"] != null && role["name"] is String){
						if(module.authenticatedRoles.indexOf(String(role["name"])) > -1){
							try{
								AppModelLocator.getInstance().authModel.authenticatedModules.addItem(module);								
							} catch (error:Error){
								Alert.show("Error loading module: "+module.label+": "+error.message);
							}
							
						}
					}
				}
			}

//			for each(var module2:ModuleConfig in AppModelLocator.getInstance().authModel.authenticatedModules){
//				AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).loadModule(module2);
//			}

			AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).loadModules(lme.modules);
		}


	}
}