/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.security.commands.roles
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;

	import org.broadleafcommerce.admin.security.business.SecurityServiceDelegate;
	import org.broadleafcommerce.admin.security.control.events.DeleteAdminRoleEvent;
	import org.broadleafcommerce.admin.security.control.events.FindAllRolesEvent;


	public class DeleteAdminRoleCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var de:DeleteAdminRoleEvent = event as DeleteAdminRoleEvent;
			var delegate:SecurityServiceDelegate = new SecurityServiceDelegate(this);
			delegate.deleteAdminRole(de.role);
		}

		public function result(data:Object):void
		{
			var reloadRoles:FindAllRolesEvent = new FindAllRolesEvent();
			reloadRoles.dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}