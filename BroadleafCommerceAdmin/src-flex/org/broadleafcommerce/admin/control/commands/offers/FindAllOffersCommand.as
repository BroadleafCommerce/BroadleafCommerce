package org.broadleafcommerce.admin.control.commands.offers
{
	import com.adobe.cairngorm.commands.Command;
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import mx.collections.ArrayCollection;
	import mx.controls.Alert;
	import mx.rpc.IResponder;
	import mx.rpc.events.FaultEvent;
	import mx.rpc.events.ResultEvent;
	
	import org.broadleafcommerce.admin.model.AppModelLocator;
	import org.broadleafcommerce.admin.model.business.BroadleafCommerceAdminServiceDelegate;
	import org.broadleafcommerce.admin.model.data.remote.Offer;
	import org.broadleafcommerce.admin.model.view.OfferModel;

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