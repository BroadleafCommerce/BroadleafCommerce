package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	public class RemoveCodeTypeFormEvent extends CairngormEvent
	{
		
		public static const EVENT_REMOVE_CODE_TYPE_FORM:String = "event_remove_code_type_form";
		
		public function RemoveCodeTypeFormEvent()
		{
			super(EVENT_REMOVE_CODE_TYPE_FORM);
		}

	}
}