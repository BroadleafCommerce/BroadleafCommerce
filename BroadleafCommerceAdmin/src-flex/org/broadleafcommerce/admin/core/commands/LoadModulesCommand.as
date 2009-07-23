package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.modules.IModuleInfo;
	
	import org.broadleafcommerce.admin.core.control.events.LoadModulesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.view.helpers.LoaderHelper;
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
			modules = lme.modules;
			AppModelLocator.getInstance().configModel.modules = modules;
			for each(var module:ModuleConfig in modules){
				//AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).loadModule(module);
	      		var lh:LoaderHelper = new LoaderHelper(module);
	      		lh.load();								
			}
		}
		

	}
}