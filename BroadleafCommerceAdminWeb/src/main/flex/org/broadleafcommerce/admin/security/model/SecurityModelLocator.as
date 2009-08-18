package org.broadleafcommerce.admin.security.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	import com.adobe.cairngorm.model.IModelLocator;

	import mx.collections.ArrayCollection;

	public class SecurityModelLocator implements IModelLocator
	{

		private static var modelLocator:SecurityModelLocator;

		public static function getInstance():SecurityModelLocator
		{
			if(modelLocator == null)
				modelLocator = new SecurityModelLocator();

			return modelLocator;
		}

		public function SecurityModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "SecurityModelLocator");
		}

		[Bindable]
		public var securityModel:SecurityModel = new SecurityModel();

		[Bindable]
		public var adminUsers:ArrayCollection = new ArrayCollection();

		[Bindable]
		public var adminPermissions:ArrayCollection = new ArrayCollection();

		[Bindable]
		public var adminRoles:ArrayCollection = new ArrayCollection();
	}
}