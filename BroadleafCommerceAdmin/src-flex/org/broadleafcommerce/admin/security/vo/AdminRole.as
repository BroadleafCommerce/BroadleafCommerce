package org.broadleafcommerce.admin.security.vo
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