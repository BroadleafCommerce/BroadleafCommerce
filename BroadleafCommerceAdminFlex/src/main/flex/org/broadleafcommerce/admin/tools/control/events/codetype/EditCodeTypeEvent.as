package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.tools.vo.CodeType;
	
	public class EditCodeTypeEvent extends CairngormEvent
	{
		
		public static const EVENT_EDIT_CODE_TYPE:String = "event_edit_code_type";
		
		public var codeType:CodeType;
		
		public function EditCodeTypeEvent(codeType:CodeType)
		{
			super(EVENT_EDIT_CODE_TYPE);
			this.codeType = codeType;
		}

	}
}