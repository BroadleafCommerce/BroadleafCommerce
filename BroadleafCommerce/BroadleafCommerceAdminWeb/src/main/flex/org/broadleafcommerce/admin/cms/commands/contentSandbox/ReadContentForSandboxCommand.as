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

	import mx.collections.ArrayCollection;
	import mx.containers.TabNavigator;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.cms.business.ContentServiceDelegate;
	import org.broadleafcommerce.admin.cms.control.events.AddContentTabEvent;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentForSandboxEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.cms.view.contentSandbox.ContentSandboxCanvas;

	public class ReadContentForSandboxCommand implements ICommand, IResponder
	{
		private var sandboxName:String;
		private var icon:Class;

		public function execute(event:CairngormEvent):void
		{
			var rcs:ReadContentForSandboxEvent = event as ReadContentForSandboxEvent;
			var delegate:ContentServiceDelegate = new ContentServiceDelegate(this);
			delegate.readContentForSandbox(rcs.sandbox);
			sandboxName = rcs.sandbox;
			icon = rcs.icon;
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var content:ArrayCollection = event.result as ArrayCollection;
			var navigator:TabNavigator = ContentModelLocator.getInstance().contentModel.contentTabNavigator;

			var sandboxTab:ContentSandboxCanvas = new ContentSandboxCanvas();
			sandboxTab.content = content;
			sandboxTab.sandboxName = sandboxName;
			sandboxTab.percentWidth=100;
			sandboxTab.percentHeight=100;
			new AddContentTabEvent("Sandbox: " + sandboxName, sandboxTab, null).dispatch();
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}