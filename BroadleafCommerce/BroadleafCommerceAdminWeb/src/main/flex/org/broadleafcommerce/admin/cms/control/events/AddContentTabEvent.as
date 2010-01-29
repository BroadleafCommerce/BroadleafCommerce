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

	import flash.display.DisplayObject;

	public class AddContentTabEvent extends CairngormEvent
	{
		public static const EVENT_ADD_CONTENT_TAB:String = "add_content_tab_event";
		public var lbl:String;
		public var displayObject:DisplayObject;
		public var icon:Class;

		public function AddContentTabEvent(lbl:String, displayObject:DisplayObject = null, icon:Class=null)
		{
			super(EVENT_ADD_CONTENT_TAB);
			this.lbl = lbl;
			this.displayObject = displayObject;
			this.icon = icon;
		}

	}
}