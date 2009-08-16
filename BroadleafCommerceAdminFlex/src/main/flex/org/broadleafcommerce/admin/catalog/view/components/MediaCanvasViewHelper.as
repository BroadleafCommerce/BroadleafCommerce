package org.broadleafcommerce.admin.catalog.view.components
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import flash.events.Event;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.net.URLRequest;
	import flash.net.URLVariables;
	
	import mx.controls.Alert;
	
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.MediaModel;
	import org.broadleafcommerce.admin.catalog.vo.Media;

	public class MediaCanvasViewHelper extends ViewHelper
	{
		private var fileRef:FileReference;
		private var directory:String;
		private var urlRequest:URLRequest;
		private var media:Media;
		private const FILE_UPLOAD_URL:String = "http://localhost:8080/broadleafadmin/spring/upload";		

		public function MediaCanvasViewHelper()
		{
			super();	
			fileRef = new FileReference();
            fileRef.addEventListener(Event.SELECT, selectHandler);
            fileRef.addEventListener(Event.COMPLETE, completeHandler);
            					
		}

		
		public function uploadImage(directory:String, media:Media):void{
			this.urlRequest = new URLRequest(FILE_UPLOAD_URL);			
	    	var params:URLVariables = new URLVariables();
	    	this.directory = directory;
	    	params.directory = directory;
			urlRequest.data = params;
			this.media = media;
			var mediaModel:MediaModel = CatalogModelLocator.getInstance().mediaModel;
			var fileFilter:FileFilter = new FileFilter(mediaModel.fileFilter1, mediaModel.fileFilter2);
			try {
			    var success:Boolean = fileRef.browse(new Array(fileFilter));
			} catch (error:Error) {
				Alert.show("Unable to browse for files.");
			    trace("Unable to browse for files.");
			}
		}
	
		private function selectHandler(event:Event):void {
		    try {
				fileRef.upload(urlRequest, "file");
		    } catch (error:Error) {
		    	Alert.show("Unable to upload file.");
		        trace("Unable to upload file.");
		    }
		}

		private function completeHandler(event:Event):void {
				this.media.url = directory+fileRef.name;	    
//			Alert.show("Upload complete");
		    trace("uploaded");
		}						
		
	}
}