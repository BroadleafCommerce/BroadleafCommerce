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
package org.broadleafcommerce.admin.core.control.events
{
	import com.universalmind.cairngorm.events.UMEvent;
	
	import mx.rpc.IResponder;

	public class GetAuthenticationEvent extends UMEvent
	{
		public static const EVENT_ID:String = "get_authentication_event";
		
		public var username:String;
		public var password:String;
		
		public function GetAuthenticationEvent(username:String, password:String, callbacks:IResponder=null)
		{
			super(EVENT_ID,callbacks);
			this.username = username;
			this.password = password;
		}
		
	}
}