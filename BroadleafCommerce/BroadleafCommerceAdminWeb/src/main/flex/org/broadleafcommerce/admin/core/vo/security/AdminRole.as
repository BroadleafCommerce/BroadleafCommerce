package org.broadleafcommerce.admin.core.vo.security
{
	import mx.collections.ArrayCollection;

	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.security.domain.AdminRoleImpl")]
	public class AdminRole
	{
		public var id:String;
	    public var name:String;
	    public var description:String;
		public var allPermissions:ArrayCollection;

	}
}