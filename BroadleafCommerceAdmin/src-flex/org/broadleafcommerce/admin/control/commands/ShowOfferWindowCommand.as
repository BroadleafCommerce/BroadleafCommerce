package org.broadleafcommerce.admin.control.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.containers.TitleWindow;
	import mx.managers.PopUpManager;
	
	import org.broadleafcommerce.admin.control.events.ShowOfferWindowEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.Offer;
	import org.broadleafcommerce.admin.model.data.conditions.AlwaysCondition;
	import org.broadleafcommerce.admin.view.components.offerWizard.NewOfferWizard;

	public class ShowOfferWindowCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var sowe:ShowOfferWindowEvent = ShowOfferWindowEvent(event);
			if(sowe.offer != null){
				AppModelLocator.getInstance().offerModel.currentOffer = sowe.offer;
			}else{
				var offer:Offer = new Offer();
				offer.qualifyConditions = new ArrayCollection([new AlwaysCondition()]);
				offer.applyToConditions = new ArrayCollection([new AlwaysCondition()]);
				AppModelLocator.getInstance().offerModel.currentOffer = offer;
			}
			
			var newOfferWizard:TitleWindow = 
				TitleWindow(PopUpManager.createPopUp(sowe.parent,NewOfferWizard, true));			
			
		}
		
	}
}