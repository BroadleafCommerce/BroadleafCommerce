package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class RemoveCodeTypeEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_CODETYPE:String = "remove_codetype_event";
		
		public var codeType:CodeType;
		
		public function RemoveCodeTypeEvent(codeType:CodeType)
		{
			super(EVENT_REMOVE_CODETYPE);
			this.codeType = codeType;
		}
		
	}
}