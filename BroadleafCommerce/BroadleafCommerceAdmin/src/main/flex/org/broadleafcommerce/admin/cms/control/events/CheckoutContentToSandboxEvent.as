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

	public class CheckoutContentToSandboxEvent extends CairngormEvent
	{
		public static const EVENT_CHECKOUT_CONTENT_TO_SANDBOX:String = "checkout_content_to_sandbox_event";
		public var sandbox:String;
		public var contentIds:ArrayCollection;

		public function CheckoutContentToSandboxEvent(contentIds:ArrayCollection, sandbox:String)
		{
			super(EVENT_CHECKOUT_CONTENT_TO_SANDBOX);
			this.sandbox = sandbox;
			this.contentIds = contentIds;
		}

	}
}