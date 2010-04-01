/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.admin.catalog.view.product
{
	import com.adobe.cairngorm.view.ViewHelper;
	
	import flash.events.Event;
	import flash.net.FileFilter;
	import flash.net.FileReference;
	import flash.net.URLRequest;
	import flash.net.URLVariables;
	
	import mx.collections.ArrayCollection;
	import mx.containers.TitleWindow;
	import mx.controls.Alert;
	import mx.managers.PopUpManager;
	
	import org.broadleafcommerce.admin.catalog.control.events.product.EditProductEvent;
	import org.broadleafcommerce.admin.catalog.model.CatalogModelLocator;
	import org.broadleafcommerce.admin.catalog.model.MediaModel;
	import org.broadleafcommerce.admin.catalog.view.media.MediaCanvas;
	import org.broadleafcommerce.admin.catalog.view.media.MediaNewWindow;
	import org.broadleafcommerce.admin.catalog.vo.media.Media;
	import org.broadleafcommerce.admin.catalog.vo.product.Product;
	import org.broadleafcommerce.admin.core.model.AppModelLocator;

	public class ProductCanvasViewHelper extends ViewHelper
	{
		private var fileRef:FileReference;
		private var directory:String;
		private var urlRequest:URLRequest;
		private var media:Media;
		private const FILE_UPLOAD_URL:String = AppModelLocator.getInstance().configModel.urlServer;		

		public function ProductCanvasViewHelper()
		{
			super();	
			fileRef = new FileReference();
            fileRef.addEventListener(Event.SELECT, selectHandler);
            fileRef.addEventListener(Event.COMPLETE, completeHandler);
            					
		}
		
		public function selectCurrentProduct():void{
			var products:ArrayCollection = CatalogModelLocator.getInstance().productModel.catalogProducts;
			var currentProduct:Product = CatalogModelLocator.getInstance().productModel.currentProduct;
			if(currentProduct.id > -1){				
				for each(var product:Product in products){
					if(product.id == currentProduct.id){
						var epe:EditProductEvent = new EditProductEvent(product,false);
						epe.dispatch();
						ProductCanvas(view).productsView.productsDataGrid.selectedItem = product;	
					}
				}
			}
		}
		
		public function editItem(index:int):void{
			MediaCanvas(ProductCanvas(view).productMediaCanvas).mediaDataGrid.editedItemPosition = {columnIndex:0,rowIndex : index};
		}

		public function editMedia(eMedia:Media):void{
			var newMediaWindow:TitleWindow = 
				TitleWindow(PopUpManager.createPopUp(ProductCanvas(view),MediaNewWindow, true));
			MediaNewWindow(newMediaWindow).media = eMedia;				
									
			
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
			    trace("DEBUG: Unable to browse for files.");
			}
		}
	
		private function selectHandler(event:Event):void {
		    try {
				fileRef.upload(urlRequest, "file");
		    } catch (error:Error) {
		    	Alert.show("Unable to upload file.");
		        trace("DEBUG: Unable to upload file.");
		    }
		}


		private function completeHandler(event:Event):void {
			this.media.url = directory+fileRef.name;
		    trace("DEBUG: uploaded");
		}						
		
	}
}