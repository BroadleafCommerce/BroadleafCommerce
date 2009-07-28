package org.broadleafcommerce.admin.core.vo.security
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.security.domain.AdminUserImpl")]
	public class AdminUser
	{
		public var id:int;
	    public var name:String;
	    public var login:String;
	    public var password:String;
	    public var email:String;
		public var allRoles:Object;

	}
}