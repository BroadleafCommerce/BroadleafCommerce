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
package org.broadleafcommerce.admin.tools.control.events.codetype
{
	import com.adobe.cairngorm.control.CairngormEvent;
	import org.broadleafcommerce.admin.core.vo.tools.CodeType;

	public class RemoveCodeTypeEvent extends CairngormEvent
	{
		public static const EVENT_REMOVE_CODETYPE:String = "remove_codetype_event";
		
		public var codeType:CodeType;
		
		public function RemoveCodeTypeEvent(codeType:CodeType)
		{
			super(EVENT_REMOVE_CODETYPE);
			this.codeType = codeType;
		}
		
	}
}