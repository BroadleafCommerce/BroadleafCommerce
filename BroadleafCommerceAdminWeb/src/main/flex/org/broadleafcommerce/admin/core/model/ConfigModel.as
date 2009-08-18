package org.broadleafcommerce.admin.core.model
{
	import mx.collections.ArrayCollection;
	import mx.core.Application;
	
	public class ConfigModel
	{
		public function ConfigModel()
		{
		}

		public var urlPrefix:String;
		
		public var modulesLoaded:ArrayCollection = new ArrayCollection();
		
		public var modules:ArrayCollection = new ArrayCollection();


	}
}