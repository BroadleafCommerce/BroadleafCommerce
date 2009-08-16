package org.broadleafcommerce.admin.core.vo.security
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.security.domain.AdminUserImpl")]
	public class AdminUser
	{
		public var id:String;
	    public var name:String;
	    public var login:String;
	    public var password:String;
	    public var email:String;
		public var allRoles:ArrayCollection;

	}
}