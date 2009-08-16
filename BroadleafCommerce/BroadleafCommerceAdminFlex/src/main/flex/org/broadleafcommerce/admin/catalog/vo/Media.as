package org.broadleafcommerce.admin.catalog.vo
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.media.domain.MediaImpl")]	
	public class Media
	{
		public var id:Number;
		public var name:String;
		public var url:String;
		public var label:String;
		
		[Transient]
		public var key:String; 

	}
}