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
package org.broadleafcommerce.admin.offers.view.events
{
	import flash.events.Event;

	public class WizardEvent extends Event
	{
		public static const WIZARD_NEXT_EVENT:String = "wizardNextEvent";
		public static const WIZARD_PREVIOUS_EVENT:String = "wizardPreviousEvent";
		public static const WIZARD_DONE_EVENT:String = "wizardDoneEvent";
		public static const WIZARD_CANCEL_EVENT:String = "wizardCancelEvent";
		
		public function WizardEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}