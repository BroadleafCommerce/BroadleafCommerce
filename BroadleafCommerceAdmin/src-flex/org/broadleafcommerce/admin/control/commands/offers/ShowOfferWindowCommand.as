package org.broadleafcommerce.admin.control.commands.offers
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.containers.TitleWindow;
	import mx.managers.PopUpManager;
	
	import org.broadleafcommerce.admin.control.events.ShowOfferWindowEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.remote.Offer;
	import org.broadleafcommerce.admin.view.offer.offerWizard.NewOfferWizard;

	public class ShowOfferWindowCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var sowe:ShowOfferWindowEvent = ShowOfferWindowEvent(event);
			if(sowe.offer != null){
				AppModelLocator.getInstance().offerModel.currentOffer = sowe.offer;
			}else{
				var offer:Offer = new Offer(); 
				AppModelLocator.getInstance().offerModel.currentOffer = offer;
			}
			
			var newOfferWizard:TitleWindow = 
				TitleWindow(PopUpManager.createPopUp(sowe.parent,NewOfferWizard, true));			
			
		}
		
	}
}