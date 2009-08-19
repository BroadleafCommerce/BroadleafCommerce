package org.broadleafcommerce.admin.search.vo
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.search.domain.SearchInterceptImpl")]
	public class SearchIntercept
	{
		public var id:Number;
		public var term:String;
		public var redirect:String;
	}
}