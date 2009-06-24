package org.broadleafcommerce.admin.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.control.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.EditCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.FindAllCatalogCategoriesCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.NewCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.SaveCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.FindAllCatalogProductsCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.SaveCatalogProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.FindAllCatalogSkusCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.SaveCatalogSkuCommand;
	import org.broadleafcommerce.admin.control.commands.offer.AddUpdateOfferCommand;
	import org.broadleafcommerce.admin.control.commands.offer.FindAllOffersCommand;
	import org.broadleafcommerce.admin.control.commands.offer.ShowOfferWindowCommand;
	import org.broadleafcommerce.admin.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.EditCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.NewCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.SaveCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllCatalogProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.SaveCatalogProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllCatalogSkusEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.SaveCatalogSkuEvent;
	import org.broadleafcommerce.admin.control.events.offer.AddUpdateOfferEvent;
	import org.broadleafcommerce.admin.control.events.offer.FindAllOffersEvent;
	import org.broadleafcommerce.admin.control.events.offer.ShowOfferWindowEvent;
	
	public class BlcAdminController extends FrontController
	{
		public function BlcAdminController()
		{
			super();
			addCommand(ShowOfferWindowEvent.EVENT_SHOW_OFFER_WINDOW,ShowOfferWindowCommand);
			addCommand(AddUpdateOfferEvent.EVENT_ADD_UPDATE_OFFER,AddUpdateOfferCommand);
			addCommand(FindAllCatalogCategoriesEvent.EVENT_FIND_ALL_CATALOG_CATEGORIES,FindAllCatalogCategoriesCommand);
			addCommand(FindAllOffersEvent.EVENT_FIND_ALL_OFFERS, FindAllOffersCommand);
			addCommand(InitializeApplicationEvent.EVENT_INITIALIZE_APPLICATION, InitializeApplicationCommand);
			addCommand(SaveCatalogCategoryEvent.EVENT_SAVE_CATALOG_CATEGORY, SaveCatalogCategoryCommand);
			addCommand(SaveCatalogProductEvent.EVENT_SAVE_CATALOG_PRODUCT, SaveCatalogProductCommand);
			addCommand(FindAllCatalogProductsEvent.EVENT_FIND_ALL_PRODUCTS, FindAllCatalogProductsCommand);
			addCommand(FindAllCatalogSkusEvent.EVENT_FIND_ALL_CATALOG_SKUS, FindAllCatalogSkusCommand);
			addCommand(SaveCatalogSkuEvent.EVENT_SAVE_CATALOG_SKU, SaveCatalogSkuCommand);
			addCommand(EditCatalogCategoryEvent.EVENT_EDIT_CATALOG_CATEGORY, EditCatalogCategoryCommand);
			addCommand(NewCatalogCategoryEvent.EVENT_NEW_CATALOG_CATEGORY, NewCatalogCategoryCommand);
		}
		
	}
}