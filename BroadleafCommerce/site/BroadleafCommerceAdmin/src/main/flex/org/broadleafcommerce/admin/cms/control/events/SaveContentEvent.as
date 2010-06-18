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
package org.broadleafcommerce.admin.cms.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	import mx.collections.ArrayCollection;

	import org.broadleafcommerce.admin.cms.vo.Content;

	public class SaveContentEvent extends CairngormEvent
	{
		public static const EVENT_SAVE_CONTENT:String = "save_content_event";
		public var content:Content;
		public var contentDetailsList:ArrayCollection;
		public var sandbox:String;
		public var callingTabName:String;

		public function SaveContentEvent(content:Content, contentDetailsList:ArrayCollection, sandbox:String, callingTabName:String)
		{
			super(EVENT_SAVE_CONTENT);
			this.content = content;
			this.contentDetailsList = contentDetailsList;
			this.sandbox = sandbox;
			this.callingTabName = callingTabName;
		}

	}
}