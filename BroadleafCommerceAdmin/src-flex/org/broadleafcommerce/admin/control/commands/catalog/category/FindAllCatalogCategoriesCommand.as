package org.broadleafcommerce.admin.control.commands.catalog.category
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
	import org.broadleafcommerce.admin.model.data.remote.catalog.category.Category;

	public class FindAllCatalogCategoriesCommand implements Command, IResponder
	{
		public function execute(event:CairngormEvent):void
		{
			var delegate:BroadleafCommerceAdminServiceDelegate = new BroadleafCommerceAdminServiceDelegate(this);
			delegate.findAllCategories();
		}
		
		public function result(data:Object):void
		{
			var event:ResultEvent = ResultEvent(data);
			var rawCats:ArrayCollection = ArrayCollection(event.result);
			var rootCats:ArrayCollection = new ArrayCollection();
			var subCatIds:ArrayCollection = new ArrayCollection();
			var categoryTree:ArrayCollection = rawCats;
			for (var i:String  in rawCats){
				var category:Category = rawCats[i];
				if(category.defaultParentCategory != null){
					for each(var category2:Category in rawCats){
						if(category.defaultParentCategory.id == category2.id){
							if(category2.allChildCategories == null){
								category2.allChildCategories = new ArrayCollection();								
							}
							category2.allChildCategories.addItem(category);
						}
					}			
					subCatIds.addItem(int(i));
				}else{
					rootCats.addItem(category);
				}
			}
			AppModelLocator.getInstance().catalogCategories = rootCats;
		}
		
		public function fault(info:Object):void
		{
			var event:FaultEvent = FaultEvent(info);
			Alert.show("Error: "+ event);
		}
		
	}
}