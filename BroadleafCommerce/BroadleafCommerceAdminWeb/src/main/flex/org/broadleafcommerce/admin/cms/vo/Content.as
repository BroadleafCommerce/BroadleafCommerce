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
package org.broadleafcommerce.admin.cms.vo
{

	[Bindable]
	[RemoteClass(alias="org.broadleafcommerce.content.domain.ContentImpl")]
	public class Content
	{
		public var activeEndDate:Date;
		public var activeStartDate:Date;
		public var approvedBy:String;
		public var approvedDate:Date;
		public var browserTitle:String;
		public var contentType:String;
		public var displayRule:String;
		public var deployed:Boolean;
		public var description:String;
		public var id:Number;
		public var keywords:String;
		public var languageCode:String;
		public var lastModifiedDate:Date;
		public var lastModifiedBy:String;
		public var metaDescription:String;
		public var note:String;
		public var online:Boolean;
		public var parentContentId:Number;
		public var priority:Number;
		public var rejectedBy:String;
		public var rejectedDate:Date;
		public var renderTemplate:String;
		public var sandbox:String;
		public var submittedBy:String;
		public var submittedDate:Date;
		public var title:String;
		public var urlTitle:String;

		public function toString():String{
			var contents:String = "";
			contents += "activeEndDate: " + activeEndDate + "\n";
			contents += "activeStartDate: " + activeStartDate + "\n";
			contents += "approvedBy: " + approvedBy + "\n";
			contents += "approvedDate: " + approvedDate + "\n";
			contents += "browserTitle: " + browserTitle + "\n";
			contents += "contentType: " + contentType + "\n";
			contents += "displayRule: " + displayRule + "\n";
			contents += "deployed: " + deployed + "\n";
			contents += "description: " + description + "\n";
			contents += "id: " + id + "\n";
			contents += "keywords: " + keywords + "\n";
			contents += "languageCode: " + languageCode + "\n";
			contents += "lastModifiedDate: " + lastModifiedDate + "\n";
			contents += "lastModifiedBy: " + lastModifiedBy + "\n";
			contents += "metaDescription: " + metaDescription + "\n";
			contents += "note: " + note + "\n";
			contents += "online: " + online + "\n";
			contents += "parentContentId: " + parentContentId + "\n";
			contents += "priority: " + priority + "\n";
			contents += "rejectedBy: " + rejectedBy + "\n";
			contents += "rejectedDate: " + rejectedDate + "\n";
			contents += "renderTemplate: " + renderTemplate + "\n";
			contents += "sandbox: " + sandbox + "\n";
			contents += "submittedBy: " + submittedBy + "\n";
			contents += "submittedDate: " + submittedDate + "\n";
			contents += "title: " + title + "\n";
			contents += "urlTitle: " + urlTitle + "\n";

			return contents;
		}
	}
}