package org.broadleafcommerce.admin.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import org.broadleafcommerce.admin.model.data.Offer;

	public class AddUpdateOfferEvent extends CairngormEvent
	{
		public static const EVENT_ADD_UPDATE_OFFER:String = "event_add_update_offer";
		
		public var offer:Offer;
		
		public function AddUpdateOfferEvent(offer:Offer)
		{
			super(EVENT_ADD_UPDATE_OFFER);
			this.offer = offer;
		}
		
	}
}