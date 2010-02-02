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
package org.broadleafcommerce.admin.cms.control
{
	import com.adobe.cairngorm.control.FrontController;

	import org.broadleafcommerce.admin.cms.commands.contentApproval.ReadContentAwaitingApprovalCommand;
	import org.broadleafcommerce.admin.cms.commands.contentApproval.ViewContentApprovalCommand;
	import org.broadleafcommerce.admin.cms.commands.contentBrowser.ReadContentForBrowserCommand;
	import org.broadleafcommerce.admin.cms.commands.contentBrowser.ViewContentBrowserCommand;
	import org.broadleafcommerce.admin.cms.commands.contentCreation.CreateEditContentCommand;
	import org.broadleafcommerce.admin.cms.commands.contentCreation.SaveContentCommand;
	import org.broadleafcommerce.admin.cms.commands.contentHome.AddContentTabCommand;
	import org.broadleafcommerce.admin.cms.commands.contentHome.InitializeContentRolesCommand;
	import org.broadleafcommerce.admin.cms.commands.contentHome.InitializeContentTypesCommand;
	import org.broadleafcommerce.admin.cms.commands.contentHome.InitializeRenderTypesCommand;
	import org.broadleafcommerce.admin.cms.commands.contentHome.RemoveContentTabCommand;
	import org.broadleafcommerce.admin.cms.commands.contentSandbox.ApproveContentCommand;
	import org.broadleafcommerce.admin.cms.commands.contentSandbox.ReadContentForSandboxCommand;
	import org.broadleafcommerce.admin.cms.commands.contentSandbox.SubmitContentFromSandboxCommand;
	import org.broadleafcommerce.admin.cms.commands.contentSandbox.ViewContentSandboxCommand;
	import org.broadleafcommerce.admin.cms.control.events.AddContentTabEvent;
	import org.broadleafcommerce.admin.cms.control.events.ApproveContentEvent;
	import org.broadleafcommerce.admin.cms.control.events.CreateEditContentEvent;
	import org.broadleafcommerce.admin.cms.control.events.InitializeContentRolesEvent;
	import org.broadleafcommerce.admin.cms.control.events.InitializeContentTypesEvent;
	import org.broadleafcommerce.admin.cms.control.events.InitializeRenderTypesEvent;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentAwaitingApprovalEvent;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentForBrowserEvent;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentForSandboxEvent;
	import org.broadleafcommerce.admin.cms.control.events.RemoveContentTabEvent;
	import org.broadleafcommerce.admin.cms.control.events.SaveContentEvent;
	import org.broadleafcommerce.admin.cms.control.events.SubmitContentFromSandboxEvent;
	import org.broadleafcommerce.admin.cms.control.events.ViewContentApprovalEvent;
	import org.broadleafcommerce.admin.cms.control.events.ViewContentBrowserEvent;
	import org.broadleafcommerce.admin.cms.control.events.ViewContentSandboxEvent;

	public class ContentController extends FrontController
	{
		public function ContentController()
		{
			super();
			addCommand(InitializeContentRolesEvent.EVENT_INITIALIZE_CONTENT_ROLES, InitializeContentRolesCommand);
			addCommand(InitializeContentTypesEvent.EVENT_INITIALIZE_CONTENT_TYPES, InitializeContentTypesCommand);
			addCommand(InitializeRenderTypesEvent.EVENT_INITIALIZE_RENDER_TYPES, InitializeRenderTypesCommand);
			addCommand(AddContentTabEvent.EVENT_ADD_CONTENT_TAB, AddContentTabCommand);
			addCommand(RemoveContentTabEvent.EVENT_REMOVE_CONTENT_TAB, RemoveContentTabCommand);
			addCommand(ViewContentApprovalEvent.EVENT_VIEW_CONTENT_APPROVAL, ViewContentApprovalCommand);
			addCommand(ViewContentBrowserEvent.EVENT_VIEW_CONTENT_BROWSER, ViewContentBrowserCommand);
			addCommand(ViewContentSandboxEvent.EVENT_VIEW_CONTENT_SANDBOX, ViewContentSandboxCommand);
			addCommand(ReadContentAwaitingApprovalEvent.EVENT_READ_CONTENT_AWAITING_APPROVAL, ReadContentAwaitingApprovalCommand);
			addCommand(ReadContentForSandboxEvent.EVENT_READ_CONTENT_FOR_SANDBOX, ReadContentForSandboxCommand);
			addCommand(ReadContentForBrowserEvent.EVENT_READ_CONTENT_FOR_BROWSER, ReadContentForBrowserCommand);
			addCommand(CreateEditContentEvent.EVENT_CREATE_EDIT_CONTENT, CreateEditContentCommand);
			addCommand(SubmitContentFromSandboxEvent.EVENT_SUBMIT_CONTENT_FROM_SANDBOX, SubmitContentFromSandboxCommand);
			addCommand(ApproveContentEvent.EVENT_APPROVE_CONTENT, ApproveContentCommand);
			addCommand(SaveContentEvent.EVENT_SAVE_CONTENT, SaveContentCommand);
		}

	}
}