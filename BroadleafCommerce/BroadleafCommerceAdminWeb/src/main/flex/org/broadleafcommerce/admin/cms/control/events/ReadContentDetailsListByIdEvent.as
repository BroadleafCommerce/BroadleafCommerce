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

	import org.broadleafcommerce.admin.cms.vo.Content;
	import org.broadleafcommerce.admin.cms.vo.ContentDetails;

	public class ReadContentDetailsListByIdEvent extends CairngormEvent
	{
		public static const EVENT_READ_CONTENT_DETAILS_LIST_BY_ID:String = "read_content_details_list_by_id_event";
		public var content:Content;
		public var contentDetails:ContentDetails;

		public function ReadContentDetailsListByIdEvent(content:Content, contentDetails:ContentDetails)
		{
			super(EVENT_READ_CONTENT_DETAILS_LIST_BY_ID);
			this.content = content;
			this.contentDetails = contentDetails;
		}

	}
}