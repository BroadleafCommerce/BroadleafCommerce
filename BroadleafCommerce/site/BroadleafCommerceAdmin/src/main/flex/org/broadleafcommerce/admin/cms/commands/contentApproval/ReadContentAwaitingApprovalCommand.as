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
package org.broadleafcommerce.admin.cms.commands.contentApproval
{
	import com.adobe.cairngorm.commands.ICommand;
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;

	import org.broadleafcommerce.admin.cms.business.ContentServiceDelegate;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;
	import org.broadleafcommerce.admin.cms.vo.Content;

	public class ReadContentAwaitingApprovalCommand implements ICommand, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var delegate:ContentServiceDelegate = new ContentServiceDelegate(this);
			delegate.readContentAwaitingApproval();
		}

		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var content:ArrayCollection = event.result as ArrayCollection;
			var grouped:ArrayCollection = new ArrayCollection();

			//transform content into groupings for datagrid
			for each (var itm:Content in content){
				var found:Boolean = false;
				var foundObject:Object = null;
				for each (var grpObj:Object in grouped){
					if (itm.sandbox == grpObj.sandbox){
						found = true;
						foundObject = grpObj;
						break;
					}
				}

				if (found){
					foundObject.numItems = Number(foundObject.numItems) + 1;
				} else {
					var aa:Object = new Object();
					aa.sandbox = itm.sandbox;
					aa.submittedBy = itm.submittedBy;
					aa.submittedDate = itm.submittedDate;
					aa.note = itm.note;
					aa.numItems = 1;

					grouped.addItem(aa);
				}
			}


			ContentModelLocator.getInstance().contentModel.contentAwaitingApproval = grouped;
		}

		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}

	}
}