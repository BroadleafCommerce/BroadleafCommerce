package org.broadleafcommerce.admin.security.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import org.broadleafcommerce.admin.core.vo.security.AdminRole;

	public class SaveRoleEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_ROLE:String = "save_role_event";
		public var role:AdminRole

		public function SaveRoleEvent(role:AdminRole)
		{
			super(EVENT_SAVE_ROLE);
			this.role = role;
		}

	}
}