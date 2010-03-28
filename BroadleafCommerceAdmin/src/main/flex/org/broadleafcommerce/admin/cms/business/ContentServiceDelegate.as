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
package org.broadleafcommerce.admin.cms.business
{
	import mx.collections.ArrayCollection;
	import mx.rpc.AsyncToken;
	import mx.rpc.IResponder;
	import mx.rpc.remoting.RemoteObject;

	import org.broadleafcommerce.admin.cms.vo.Content;


	public class ContentServiceDelegate
	{

        private var responder : IResponder;
        private var contentService:RemoteObject;

		public function ContentServiceDelegate(responder:IResponder)
		{
			trace("DEBUG: new ContentServiceDelegate()");
			this.contentService = ContentServiceLocator.getInstance().getService();
            this.responder = responder;
		}

		public function findContentById(id:Number):void{
			trace("DEBUG: ContentServiceDelegate.findContentById()");
			var call:AsyncToken = contentService.findContentById(id);
			call.addResponder(responder);
		}

		public function findContentDetailsById(id:Number):void{
			trace("DEBUG: ContentServiceDelegate.findContentById()");
			var call:AsyncToken = contentService.findContentDetailsById(id);
			call.addResponder(responder);
		}

		public function findContentDetailsListById(id:Number):void{
			trace("DEBUG: ContentServiceDelegate.findContentDetailsListById()");
			var call:AsyncToken = contentService.findContentDetailsListById(id);
			call.addResponder(responder);
		}

		public function checkoutContentToSandbox(contentIds:ArrayCollection, sandboxName:String):void {
			trace("DEBUG: ContentServiceDelegate.checkoutContentToSandbox()");
			var call:AsyncToken = contentService.checkoutContentToSandbox(contentIds, sandboxName);
			call.addResponder(responder);
		}

		public function submitContentFromSandbox(contentIds:ArrayCollection, sandboxName:String, username:String, note:String):void {
			trace("DEBUG: ContentServiceDelegate.submitContentFromSandbox()");
			var call:AsyncToken = contentService.submitContentFromSandbox(contentIds, sandboxName, username, note);
			call.addResponder(responder);
		}

		public function approveContent(contentIds:ArrayCollection, sandboxName:String, username:String):void {
			trace("DEBUG: ContentServiceDelegate.approveContent()");
			var call:AsyncToken = contentService.approveContent(contentIds, sandboxName, username);
			call.addResponder(responder);
		}

		public function removeContentFromSandbox(contentIds:ArrayCollection, sandbox:String):void {
			trace("DEBUG: ContentServiceDelegate.removeContentFromSandbox()");
			var call:AsyncToken = contentService.removeContentFromSandbox(contentIds, sandbox);
			call.addResponder(responder);
		}

		public function rejectContent(contentIds:ArrayCollection, sandbox:String, username:String):void {
			trace("DEBUG: ContentServiceDelegate.rejectContent()");
			var call:AsyncToken = contentService.rejectContent(contentIds, sandbox, username);
			call.addResponder(responder);
		}

		public function readContentForSandbox(sandbox:String):void {
			trace("DEBUG: ContentServiceDelegate.readContentForSandbox()");
			var call:AsyncToken = contentService.readContentForSandbox(sandbox);
			call.addResponder(responder);
		}

		public function readContentForSandboxAndType(sandbox:String, contentType:String):void {
			trace("DEBUG: ContentServiceDelegate.readContentForSandboxAndType()");
			var call:AsyncToken = contentService.readContentForSandboxAndType(sandbox, contentType);
			call.addResponder(responder);
		}

		public function readContentAwaitingApproval():void {
			trace("DEBUG: ContentServiceDelegate.readContentAwaitingApproval()");
			var call:AsyncToken = contentService.readContentAwaitingApproval();
			call.addResponder(responder);
		}

		public function saveContent(content:Content, contentDetailsList:ArrayCollection):void {
			trace("DEBUG: ContentServiceDelegate.saveContent()");
			var call:AsyncToken = contentService.saveContent(content, contentDetailsList);
			call.addResponder(responder);
		}

		public function readAllContentPageInfos():void {
			trace("DEBUG: ContentServiceDelegate.readAllContentPageInfos()");
			var call:AsyncToken = contentService.readAllContentPageInfos();
			call.addResponder(responder);
		}
	}
}