package org.broadleafcommerce.admin.offers.model
{
	import com.adobe.cairngorm.CairngormError;
	import com.adobe.cairngorm.CairngormMessageCodes;
	
	public class OfferModelLocator
	{
		private static var modelLocator:OfferModelLocator;


		public static function getInstance():OfferModelLocator
		{
			if(modelLocator == null)
				modelLocator = new OfferModelLocator();
			
			return modelLocator;
		}
		
		public function OfferModelLocator()
		{
			if(modelLocator != null)
				throw new CairngormError(CairngormMessageCodes.SINGLETON_EXCEPTION, "OfferModelLocator");				
		}
		
		public var offerModel:OfferModel = new OfferModel();

	}
}