package org.broadleafcommerce.admin.core.vo.security
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.security.domain.AdminRoleImpl")]
	public class AdminRole
	{
		public var id:int;
	    public var name:String;
	    public var description:String;
		public var allUsers:Object;
		public var allPermissions:Object;

	}
}