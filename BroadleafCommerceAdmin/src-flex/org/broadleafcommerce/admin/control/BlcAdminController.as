package org.broadleafcommerce.admin.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.control.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.BuildCatalogTreeCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.RetrieveCatalogCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.AddCategoriesToCatalogTreeCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.EditCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.FindAllCatalogCategoriesCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.NewCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.SaveCatalogCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.AddProductsToCatalogTreeCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.FindAllCatalogProductsCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.NewCatalogProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.SaveCatalogProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.AddSkusToCatalogTreeCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.ViewCatalogProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.FindAllCatalogSkusCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.SaveCatalogSkuCommand;
	import org.broadleafcommerce.admin.control.commands.offer.AddUpdateOfferCommand;
	import org.broadleafcommerce.admin.control.commands.offer.FindAllOffersCommand;
	import org.broadleafcommerce.admin.control.commands.offer.ShowOfferWindowCommand;
	import org.broadleafcommerce.admin.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.control.events.catalog.BuildCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.RetrieveCatalogEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.EditCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCatalogCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.NewCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.SaveCatalogCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.AddProductsToCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllCatalogProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.NewCatalogProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.SaveCatalogProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.AddSkusToCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.ViewCatalogProductEvent;
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
			addCommand(InitializeApplicationEvent.EVENT_INITIALIZE_APPLICATION, InitializeApplicationCommand);
			addCommand(FindAllOffersEvent.EVENT_FIND_ALL_OFFERS, FindAllOffersCommand);
			addCommand(RetrieveCatalogEvent.EVENT_RETRIEVE_CATALOG, RetrieveCatalogCommand);
			addCommand(FindAllCatalogCategoriesEvent.EVENT_FIND_ALL_CATALOG_CATEGORIES,FindAllCatalogCategoriesCommand);
			addCommand(FindAllCatalogProductsEvent.EVENT_FIND_ALL_PRODUCTS, FindAllCatalogProductsCommand);
			addCommand(FindAllCatalogSkusEvent.EVENT_FIND_ALL_CATALOG_SKUS, FindAllCatalogSkusCommand);
			addCommand(BuildCatalogTreeEvent.EVENT_BUILD_CATALOG_TREE, BuildCatalogTreeCommand);
			addCommand(AddCategoriesToCatalogTreeEvent.EVENT_ADD_CATEGORIES_TO_CATALOG_TREE, AddCategoriesToCatalogTreeCommand);
			addCommand(AddProductsToCatalogTreeEvent.EVENT_ADD_PRODUCTS_TO_CATALOG_TREE, AddProductsToCatalogTreeCommand);
			addCommand(AddSkusToCatalogTreeEvent.EVENT_ADD_SKUS_TO_CATALOG_TREE, AddSkusToCatalogTreeCommand);


			addCommand(ShowOfferWindowEvent.EVENT_SHOW_OFFER_WINDOW,ShowOfferWindowCommand);
			addCommand(AddUpdateOfferEvent.EVENT_ADD_UPDATE_OFFER,AddUpdateOfferCommand);
			addCommand(SaveCatalogCategoryEvent.EVENT_SAVE_CATALOG_CATEGORY, SaveCatalogCategoryCommand);
			addCommand(SaveCatalogProductEvent.EVENT_SAVE_CATALOG_PRODUCT, SaveCatalogProductCommand);
			addCommand(SaveCatalogSkuEvent.EVENT_SAVE_CATALOG_SKU, SaveCatalogSkuCommand);
			addCommand(EditCatalogCategoryEvent.EVENT_EDIT_CATALOG_CATEGORY, EditCatalogCategoryCommand);
			addCommand(NewCatalogCategoryEvent.EVENT_NEW_CATALOG_CATEGORY, NewCatalogCategoryCommand);

			addCommand(NewCatalogProductEvent.EVENT_NEW_CATALOG_PRODUCT, NewCatalogProductCommand);
			addCommand(ViewCatalogProductEvent.EVENT_VIEW_CATALOG_PRODUCT, ViewCatalogProductCommand);
		}
		
	}
}