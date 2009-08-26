package org.broadleafcommerce.admin.offers.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.containers.TitleWindow;
	import mx.managers.PopUpManager;
	
	import org.broadleafcommerce.admin.offers.control.events.ShowOfferWindowEvent;
	import org.broadleafcommerce.admin.offers.model.OfferModelLocator;
	import org.broadleafcommerce.admin.offers.view.offerWizard.NewOfferWizard;
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
			
			var newOfferWizard:TitleWindow = 
				TitleWindow(PopUpManager.createPopUp(sowe.parent,NewOfferWizard, true));			
			
		}
		
	}
}