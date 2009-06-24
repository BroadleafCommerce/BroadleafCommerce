package org.broadleafcommerce.admin.control.events.offer
{
	import com.adobe.cairngorm.control.CairngormEvent;
	
	import flash.display.DisplayObject;
	
	import org.broadleafcommerce.admin.model.data.remote.offer.Offer;

	public class ShowOfferWindowEvent extends CairngormEvent
	{
		public static const EVENT_SHOW_OFFER_WINDOW:String = "event_show_offer_window";
		
		public var offer:Offer;
		public var parent:DisplayObject;
		
		public function ShowOfferWindowEvent( parent:DisplayObject,offer:Offer= null)
		{
			super(EVENT_SHOW_OFFER_WINDOW);
			this.offer = offer;
			this.parent = parent;
		}
		
	}
}