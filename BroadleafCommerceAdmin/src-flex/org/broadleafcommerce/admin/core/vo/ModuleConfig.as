package org.broadleafcommerce.admin.core.vo
{
	import mx.modules.Module;
	
	public class ModuleConfig
	{
		public function ModuleConfig()
		{
		}

		public var name:String;
		public var label:String;
		public var swf:String;
		public var displayOrder:int;
		public var loadedModule:Module;
	}
}