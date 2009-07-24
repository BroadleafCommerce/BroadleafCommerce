package org.broadleafcommerce.admin.security.vo
{
	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.security.domain.AdminPermissionImpl")]
	public class AdminPermission
	{
		public var id:String;
	    public var name:String;
	    public var description:String;
	}
}