package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.business.ServiceLocator;
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.control.CairngormEventDispatcher;
	
	import mx.collections.ArrayCollection;
	import mx.core.Application;
	import mx.modules.IModuleInfo;
	import mx.rpc.remoting.mxml.RemoteObject;
	
	import org.broadleafcommerce.admin.core.control.events.GetAdminConfigEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	

	public class InitializeApplicationCommand implements Command
	{
		private var eventChain:ArrayCollection = new ArrayCollection();

		private var moduleInfo:IModuleInfo;

		public function InitializeApplicationCommand()
		{			
			eventChain.addItem(new GetAdminConfigEvent());
		}

		public function execute(event:CairngormEvent):void
		{
			var myService:RemoteObject = mx.rpc.remoting.mxml.RemoteObject(ServiceLocator.getInstance().getRemoteObject("blcAdminService"));
//			myService.endpoint = AppModelLocator.getInstance().configModel.urlPrefix+"/messagebroker/amf";			
			for each(event in eventChain){
				CairngormEventDispatcher.getInstance().dispatchEvent(event);
			}
			
		}
		
	}
}