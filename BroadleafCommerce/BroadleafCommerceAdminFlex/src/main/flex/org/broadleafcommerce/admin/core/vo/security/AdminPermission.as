package org.broadleafcommerce.admin.core.vo.security
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