package org.broadleafcommerce.admin.view.events
{
	import flash.events.Event;

	public class WizardEvent extends Event
	{
		public static const WIZARD_NEXT_EVENT:String = "wizardNextEvent";
		public static const WIZARD_PREVIOUS_EVENT:String = "wizardPreviousEvent";
		public static const WIZARD_DONE_EVENT:String = "wizardDoneEvent";
		public static const WIZARD_CANCEL_EVENT:String = "wizardCancelEvent";
		
		public function WizardEvent(type:String, bubbles:Boolean=false, cancelable:Boolean=false)
		{
			super(type, bubbles, cancelable);
		}
		
	}
}