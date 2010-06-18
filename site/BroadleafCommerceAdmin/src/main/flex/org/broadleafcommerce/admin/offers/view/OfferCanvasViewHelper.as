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
package org.broadleafcommerce.admin.offers.view
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import mx.containers.TitleWindow;
	import mx.managers.PopUpManager;
	
	import org.broadleafcommerce.admin.offers.view.offerWizard.NewOfferWizard;

	public class OfferCanvasViewHelper extends ViewHelper
	{
		public function OfferCanvasViewHelper()
		{
			super();
		}

		private var newOfferWizard:TitleWindow;
		
		public function showOfferWizard():void{
			newOfferWizard = TitleWindow(PopUpManager.createPopUp(OfferCanvas(this.view),NewOfferWizard, true));			
			
		}
		
	}
}