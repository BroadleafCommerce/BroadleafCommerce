package org.broadleafcommerce.admin.offers.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	
	import org.broadleafcommerce.admin.core.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.offers.control.events.AddUpdateOfferEvent;
	import org.broadleafcommerce.admin.offers.control.events.FindAllOffersEvent;
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class AddUpdateOfferCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var auoe:AddUpdateOfferEvent = AddUpdateOfferEvent(event);
			var offer:Offer = auoe.offer;
			var offersList:ArrayCollection = AppModelLocator.getInstance().offerModel.offersList;
//				var currentOffer:Offer = AppModelLocator.getInstance().offerModel.currentOffer;
//				var index:int = offersList.getItemIndex(currentOffer);
//				offersList.removeItemAt(index);
//				offersList.addItemAt(offer,index);
//			}
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.saveOffer(offer);
			
		}
		
		public function result(data:Object):void
		{
			var faoe:FindAllOffersEvent = new FindAllOffersEvent();
			faoe.dispatch();

		}


		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}

	}
}