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
package org.broadleafcommerce.admin.offers.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import flash.display.DisplayObject;
	
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class ShowOfferWindowEvent extends CairngormEvent
	{
		public static const EVENT_SHOW_OFFER_WINDOW:String = "event_show_offer_window";
		
		public var offer:Offer;
		
		public function ShowOfferWindowEvent(offer:Offer= null)
		{
			super(EVENT_SHOW_OFFER_WINDOW);
			this.offer = offer;
		}
		
	}
}