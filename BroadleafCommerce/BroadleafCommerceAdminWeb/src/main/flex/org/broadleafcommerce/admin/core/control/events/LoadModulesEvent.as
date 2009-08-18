package org.broadleafcommerce.admin.core.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;

	public class LoadModulesEvent extends CairngormEvent
	{
		public static const EVENT_LOAD_MODULES:String = "load_modules_event";

		public var modules:ArrayCollection;

		public function LoadModulesEvent(modules:ArrayCollection)
		{
			super(EVENT_LOAD_MODULES);
			this.modules = modules;
		}
		
	}
}