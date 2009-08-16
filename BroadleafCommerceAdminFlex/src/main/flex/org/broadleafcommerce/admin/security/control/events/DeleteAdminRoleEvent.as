package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.core.vo.security.AdminRole;

	public class DeleteAdminRoleEvent extends CairngormEvent
	{
		public static const EVENT_DELETE_ADMIN_ROLE:String = "delete_admin_role";
		public var role:AdminRole;

		public function DeleteAdminRoleEvent(role:AdminRole)
		{
			super(EVENT_DELETE_ADMIN_ROLE);
			this.role = role;
		}

	}
}