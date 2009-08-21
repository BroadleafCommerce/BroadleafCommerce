package org.broadleafcommerce.admin.core.model
{
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;
	
	[Bindable]
	public class ToolsModel
	{
		public function ToolsModel()
		{
		}
		public static const SERVICE_ID:String = "blCodeTypeService";
		public static const STATE_NONE:String = "none";
		public static const STATE_EDIT:String = "edit_tool";		
		public static const STATE_VIEW:String = "view_tool";
		
		public var viewState:String = STATE_NONE;
		public var currentCodeType:CodeType = new CodeType();
		public var codeTypes:ArrayCollection = new ArrayCollection();
		public var currentCodeTypes:ArrayCollection = new ArrayCollection();
	}
}