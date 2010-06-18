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
package org.broadleafcommerce.admin.cms.commands.contentHome
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;

	import org.broadleafcommerce.admin.cms.model.ContentModel;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.core.model.AuthenticationModel;

	public class InitializeContentRolesCommand implements Command
	{
		public function InitializeContentRolesCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			this.populateContentRoles();
		}

		private function populateContentRoles():void{
			var authModel:AuthenticationModel = AppModelLocator.getInstance().authModel;
			var contentModel:ContentModel = ContentModelLocator.getInstance().contentModel;
			var roles:ArrayCollection = authModel.userPrincipal.allRoles;
			for each(var role:Object in roles) {
				if (role.name == "ROLE_CONTENT_APPROVER"){
					contentModel.contentRoles.isContentApprover = true;
				}
				if (role.name == "ROLE_CONTENT_DEPLOYER"){
					contentModel.contentRoles.isContentDeployer = true;
				}
				if (role.name == "ROLE_CONTENT_EDITOR"){
					contentModel.contentRoles.isContentEditor = true;
				}
			}

			contentModel.loggedInUserSandbox = authModel.userPrincipal.login;
		}
	}
}