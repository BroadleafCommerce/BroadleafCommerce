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
package org.broadleafcommerce.admin.offers.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	import com.adobe.cairngorm.view.ViewLocator;
	
	import org.broadleafcommerce.admin.offers.control.events.ShowOfferWindowEvent;
	import org.broadleafcommerce.admin.offers.model.OfferModelLocator;
	import org.broadleafcommerce.admin.offers.view.OfferCanvasViewHelper;
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class ShowOfferWindowCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			trace("DEBUG: ShowOfferWindowCommand.execute()");
			var sowe:ShowOfferWindowEvent = ShowOfferWindowEvent(event);
			if(sowe.offer != null){
				OfferModelLocator.getInstance().offerModel.currentOffer = sowe.offer;
			}else{
				var offer:Offer = new Offer(); 
				OfferModelLocator.getInstance().offerModel.currentOffer = offer;
			}
			
			OfferCanvasViewHelper(ViewLocator.getInstance().getViewHelper("offerCanvasViewHelper")).showOfferWizard();
		}
		
	}
}