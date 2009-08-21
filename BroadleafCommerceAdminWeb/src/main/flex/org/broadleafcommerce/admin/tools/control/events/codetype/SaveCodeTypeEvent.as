package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class SaveCodeTypeEvent extends CairngormEvent
	{
		
		public static const EVENT_SAVE_CODE_TYPE:String = "event_save_code_type";
		public var codeType:CodeType;
		
		public function SaveCodeTypeEvent(codeType:CodeType)
		{
			super(EVENT_SAVE_CODE_TYPE);
			this.codeType = codeType;
		}
		
	}
}