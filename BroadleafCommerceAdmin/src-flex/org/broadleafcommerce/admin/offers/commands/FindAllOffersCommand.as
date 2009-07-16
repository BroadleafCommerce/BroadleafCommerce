package org.broadleafcommerce.admin.offers.commands
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.core.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;
	import org.broadleafcommerce.admin.offers.model.OfferModel;
	import org.broadleafcommerce.admin.offers.vo.Offer;

	public class FindAllOffersCommand implements Command, IResponder
	{
		private var offerModel:OfferModel = AppModelLocator.getInstance().offerModel;
		
		public function execute(event:CairngormEvent):void
		{
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.findAllOffers();
		}
		
		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			this.offerModel.offersList = ArrayCollection(event.result);
			// populate array collection of offers to be filtered
			for each(var offerToBeFiltered:Offer in this.offerModel.offersList){
				this.offerModel.offersListFiltered.addItem(offerToBeFiltered);
			}
		}			
		
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+event);			
		}
		
	}
}