package org.broadleafcommerce.admin.catalog.model
{
	import org.broadleafcommerce.admin.catalog.vo.media.Media;
	
	[Bindable]
	public class MediaModel
	{
		public var fileFilter1:String = "Images (*.jpg, *.jpeg, *.gif, *.png)";
		public var fileFilter2:String = "*.jpg; *.jpeg; *.gif; *.png"; 		
		
		public var currentMedia:Media = new Media();
	}
}