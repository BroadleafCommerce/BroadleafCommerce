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
package org.broadleafcommerce.admin.cms.commands.contentSandbox
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.hydraframework.components.growler.Growler;
	import com.hydraframework.components.growler.model.GrowlDescriptor;

	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.cms.business.ContentServiceDelegate;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentAwaitingApprovalEvent;
	import org.broadleafcommerce.admin.cms.control.events.RefreshSandboxEvent;
	import org.broadleafcommerce.admin.cms.control.events.SubmitContentFromSandboxEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

	public class SubmitContentFromSandboxCommand implements ICommand, IResponder
	{
		private var tabName:String;
		private var sandbox:String;

		public function execute(event:CairngormEvent):void
		{
			var scs:SubmitContentFromSandboxEvent = event as SubmitContentFromSandboxEvent;
			var delegate:ContentServiceDelegate = new ContentServiceDelegate(this);
			delegate.submitContentFromSandbox(scs.contentIds, scs.sandbox, scs.username, scs.note);
			tabName = scs.tabName;
			sandbox = scs.sandbox;
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			//Refresh the Sandbox
			new RefreshSandboxEvent(tabName, sandbox).dispatch();
			new ReadContentAwaitingApprovalEvent().dispatch();

			var growler:Growler = AppModelLocator.getInstance().notificationModel.growlerRef;
			var color:Number = 0xCCCCCC;
			var growlDescriptor:GrowlDescriptor = new GrowlDescriptor("Submitted", "Your content will now be reviewed by a content approver","OK", color);
			growler.growl(growlDescriptor);
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}