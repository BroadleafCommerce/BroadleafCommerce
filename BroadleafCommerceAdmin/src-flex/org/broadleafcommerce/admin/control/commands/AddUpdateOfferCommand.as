package org.broadleafcommerce.admin.control.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	
	import org.broadleafcommerce.admin.control.events.AddUpdateOfferEvent;
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.data.Offer;

	public class AddUpdateOfferCommand implements Command
	{
		public function execute(event:CairngormEvent):void
		{
			var auoe:AddUpdateOfferEvent = AddUpdateOfferEvent(event);
			var offer:Offer = auoe.offer;
			var offersList:ArrayCollection = AppModelLocator.getInstance().offerModel.offersList;
			if(offer.id < 1){
				offer.id = offersList.length+1;
				offersList.addItem(offer);				
			}
//			Right now this is being updated due to binding, so the following is not required.
//			else{
//				var currentOffer:Offer = AppModelLocator.getInstance().offerModel.currentOffer;
//				var index:int = offersList.getItemIndex(currentOffer);
//				offersList.removeItemAt(index);
//				offersList.addItemAt(offer,index);
//			}
			
			
		}
		
	}
}