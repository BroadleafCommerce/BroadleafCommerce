package org.broadleafcommerce.admin.search.model
{
	import com.adobe.cairngorm.model.IModelLocator;
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	
	public class SearchModelLocator implements IModelLocator
	{
		private static var modelLocator:SearchModelLocator;

		public static function getInstance():SearchModelLocator
		{
			if(modelLocator == null)
				modelLocator = new SearchModelLocator();

			return modelLocator;
		}

		public function SearchModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "SearchModelLocator");
		}

		[Bindable]
		public var searchModel:SearchModel = new SearchModel();

	}
}