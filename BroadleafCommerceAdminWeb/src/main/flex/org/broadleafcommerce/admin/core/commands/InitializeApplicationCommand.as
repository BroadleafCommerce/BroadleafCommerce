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
package org.broadleafcommerce.admin.core.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.universalmind.cairngorm.events.generator.EventGenerator;
	
	import org.broadleafcommerce.admin.core.control.events.GetAdminConfigEvent;
	import org.broadleafcommerce.admin.core.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.core.control.events.codetype.AdminFindAllCodeTypesEvent;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	

	public class InitializeApplicationCommand implements Command
	{
		public function InitializeApplicationCommand()
		{			
		}

		public function execute(event:CairngormEvent):void
		{
			var iae:InitializeApplicationEvent = InitializeApplicationEvent(event);
			var events:Array = [GetAdminConfigEvent, AdminFindAllCodeTypesEvent];
			var generator:EventGenerator = new EventGenerator(events,null,0,EventGenerator.TRIGGER_PARALLEL);
			generator.dispatch();
		}
		
	}
}