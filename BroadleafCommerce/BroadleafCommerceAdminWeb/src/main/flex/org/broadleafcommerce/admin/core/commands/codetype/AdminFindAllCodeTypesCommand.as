package org.broadleafcommerce.admin.core.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.AdminToolsDelegate;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.ConfigModel;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	
	public class AdminFindAllCodeTypesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void{
			trace("DEBUG: AdminFindAllCodeTypesCommand.execute()");
			var delegate:AdminToolsDelegate = new AdminToolsDelegate(this);
			delegate.findAllCodeTypes();
		}

		public function result(data:Object):void{
			trace("DEBUG: AdminFindAllCodeTypesCommand.result()");
			// The following line is needed so that the remoteObject is properly 
			// created as a CodeType
			var codeType:CodeType = new CodeType();
			var event:ResultEvent = ResultEvent(data);
			var codes:ArrayCollection = ArrayCollection(event.result);
			var configModel:ConfigModel = AppModelLocator.getInstance().configModel;  
			
			configModel.codeTypes = codes;
			
		}
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);
		}		
	}
}