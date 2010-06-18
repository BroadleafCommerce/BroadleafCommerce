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
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;

	import flash.display.DisplayObject;

	import org.broadleafcommerce.admin.cms.control.events.RemoveContentTabEvent;
	import org.broadleafcommerce.admin.cms.model.ContentModelLocator;

	public class RemoveContentTabCommand implements Command
	{
		public function RemoveContentTabCommand()
		{
		}

		public function execute(event:CairngormEvent):void
		{
			var re:RemoveContentTabEvent = event as RemoveContentTabEvent;
			var tab:DisplayObject = ContentModelLocator.getInstance().contentModel.contentTabNavigator.getChildByName(re.tabName);
			ContentModelLocator.getInstance().contentModel.contentTabNavigator.removeChild(tab);
		}

	}
}