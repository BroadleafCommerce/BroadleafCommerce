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
			var modulesLoaded:ArrayCollection = AppModelLocator.getInstance().configModel.modules;
			var modules:ArrayCollection = AppModelLocator.getInstance().configModel.modules;
			if(modulesLoaded.length == modules.length){
				for (var index:String in modulesLoaded){
					AdminContentViewHelper(ViewLocator.getInstance().getViewHelper("adminContent")).addModuleToView(ModuleConfig(modules[index]).loadedModule);
					//Application.application.adminContent.contentViewStack.addChildAt(ModuleConfig(modules[index]).loadedModule,index);
				}
			}else{
				Alert.show("Not done yet");
			}
		}
		
	}
}