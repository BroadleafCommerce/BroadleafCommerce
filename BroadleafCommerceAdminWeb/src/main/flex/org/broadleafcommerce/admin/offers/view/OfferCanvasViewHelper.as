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