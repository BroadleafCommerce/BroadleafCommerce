package org.broadleafcommerce.admin.offers.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.offers.commands.AddUpdateOfferCommand;
	import org.broadleafcommerce.admin.offers.commands.FindAllOffersCommand;
	import org.broadleafcommerce.admin.offers.commands.ShowOfferWindowCommand;
	import org.broadleafcommerce.admin.offers.control.events.AddUpdateOfferEvent;
	import org.broadleafcommerce.admin.offers.control.events.FindAllOffersEvent;
	import org.broadleafcommerce.admin.offers.control.events.ShowOfferWindowEvent;
	
	public class OfferController extends FrontController
	{
		public function OfferController()
		{
			super();
			addCommand(FindAllOffersEvent.EVENT_FIND_ALL_OFFERS, FindAllOffersCommand);
			addCommand(ShowOfferWindowEvent.EVENT_SHOW_OFFER_WINDOW,ShowOfferWindowCommand);
			addCommand(AddUpdateOfferEvent.EVENT_ADD_UPDATE_OFFER,AddUpdateOfferCommand);



		}
		
	}
}