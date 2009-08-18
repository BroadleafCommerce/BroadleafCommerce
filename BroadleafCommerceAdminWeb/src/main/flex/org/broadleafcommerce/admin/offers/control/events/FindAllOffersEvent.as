package org.broadleafcommerce.admin.offers.control.events
{
	import com.adobe.cairngorm.control.CairngormEvent;

	public class FindAllOffersEvent extends CairngormEvent
	{
		
		public static const EVENT_FIND_ALL_OFFERS:String = "event_find_all_offers";
		
		public function FindAllOffersEvent()
		{
			super(EVENT_FIND_ALL_OFFERS);
		}
		
	}
}