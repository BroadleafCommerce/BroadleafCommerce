package org.broadleafcommerce.admin.tools.commands.codetype
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	import org.broadleafcommerce.admin.tools.business.ToolsServiceDelegate;
	import org.broadleafcommerce.admin.tools.control.events.codetype.SearchCodeTypesEvent;
	import org.broadleafcommerce.admin.tools.model.CodeTypeModel;
	import org.broadleafcommerce.admin.tools.model.ToolsModelLocator;

	public class SearchCodeTypesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var scte:SearchCodeTypesEvent = SearchCodeTypesEvent(event);
			var keyword:String = scte.keyword;
			var delegate:ToolsServiceDelegate = new ToolsServiceDelegate(this);
			delegate.lookupCodeTypeByKey(keyword);
		}
		
		public function result(data:Object):void{
			var toolsModel:CodeTypeModel = ToolsModelLocator.getInstance().codeTypeModel;
			var event:ResultEvent = ResultEvent(data);
			toolsModel.codeTypes = ArrayCollection(event.result);
			if(toolsModel.codeTypes.length != 0){
				toolsModel.currentCodeType = CodeType(toolsModel.codeTypes.getItemAt(0));
				if(toolsModel.currentCodeType.modifiable == 'true'){
					toolsModel.viewState = CodeTypeModel.STATE_EDIT;
				}else{
					toolsModel.viewState = CodeTypeModel.STATE_VIEW;
				}
			}else{
				toolsModel.viewState = CodeTypeModel.STATE_NONE;
			}
		}		
		
		public function fault(info:Object):void{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: " + event);			
		}
		
	}
}