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
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import flash.display.DisplayObject;

	import mx.collections.ArrayCollection;
	import mx.containers.TitleWindow;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.cms.business.ContentServiceDelegate;
	import org.broadleafcommerce.admin.cms.control.events.ReadContentForCheckoutEvent;
	import org.broadleafcommerce.admin.cms.view.contentHome.CheckoutModal;

	public class ReadContentForCheckoutCommand implements ICommand, IResponder
	{
		private var contentType:String;
		private var parentObj:DisplayObject;

		public function execute(event:CairngormEvent):void
		{
			var rc:ReadContentForCheckoutEvent = event as ReadContentForCheckoutEvent;
			var delegate:ContentServiceDelegate = new ContentServiceDelegate(this);
			delegate.readContentForSandboxAndType(null, rc.contentType);
			contentType = rc.contentType;
			parentObj = rc.parentObj;
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var content:ArrayCollection = event.result as ArrayCollection;

			var newFindModal:TitleWindow = TitleWindow(PopUpManager.createPopUp(parentObj, CheckoutModal, true));
			CheckoutModal(newFindModal).contentType = contentType;
			CheckoutModal(newFindModal).content = content;
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}