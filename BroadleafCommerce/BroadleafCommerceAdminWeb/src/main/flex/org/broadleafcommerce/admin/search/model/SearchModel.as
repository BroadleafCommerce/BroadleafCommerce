package org.broadleafcommerce.admin.search.model
{
	import mx.collections.ArrayCollection;
	import org.broadleafcommerce.admin.search.vo.SearchIntercept;
	
	[Bindable]
	public class SearchModel
	{
		public function SearchModel()
		{
		}
		public var currentSearchIntercept:SearchIntercept = new SearchIntercept();

		public var searchInterceptList:ArrayCollection = new ArrayCollection();


		public static const SERVICE_ID:String = "blSearchService";

	}
}