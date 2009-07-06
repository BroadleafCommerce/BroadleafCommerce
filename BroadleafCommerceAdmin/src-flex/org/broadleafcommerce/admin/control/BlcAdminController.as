package org.broadleafcommerce.admin.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.control.commands.InitializeApplicationCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.BuildCatalogCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.RetrieveCatalogCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.StandardizeCatalogObjectsCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.AddCategoriesToCatalogTreeCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.EditCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.FindAllCatalogCategoriesCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.NewCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.category.SaveCategoryCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.AddProductsToCategoriesCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.EditProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.FindAllProductsCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.NewProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.product.SaveProductCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.AddSkusToProductsCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.FindAllCatalogSkusCommand;
	import org.broadleafcommerce.admin.control.commands.catalog.sku.SaveCatalogSkuCommand;
	import org.broadleafcommerce.admin.control.commands.offer.AddUpdateOfferCommand;
	import org.broadleafcommerce.admin.control.commands.offer.FindAllOffersCommand;
	import org.broadleafcommerce.admin.control.commands.offer.ShowOfferWindowCommand;
	import org.broadleafcommerce.admin.control.events.InitializeApplicationEvent;
	import org.broadleafcommerce.admin.control.events.catalog.BuildCatalogEvent;
	import org.broadleafcommerce.admin.control.events.catalog.RetrieveCatalogEvent;
	import org.broadleafcommerce.admin.control.events.catalog.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.EditProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.NewProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.product.SaveProductEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.AddSkusToProductsEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.FindAllSkusEvent;
	import org.broadleafcommerce.admin.control.events.catalog.sku.SaveSkuEvent;
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
			addCommand(FindAllCategoriesEvent.EVENT_FIND_ALL_CATALOG_CATEGORIES,FindAllCatalogCategoriesCommand);
			addCommand(FindAllProductsEvent.EVENT_FIND_ALL_PRODUCTS, FindAllProductsCommand);
			addCommand(FindAllSkusEvent.EVENT_FIND_ALL_CATALOG_SKUS, FindAllCatalogSkusCommand);
			addCommand(BuildCatalogEvent.EVENT_BUILD_CATALOG_TREE, BuildCatalogCommand);
			addCommand(StandardizeCatalogObjectsEvent.EVENT_STANDARDIZE_CATALOG_OBJECTS, StandardizeCatalogObjectsCommand);
			addCommand(AddCategoriesToCatalogTreeEvent.EVENT_ADD_CATEGORIES_TO_CATALOG_TREE, AddCategoriesToCatalogTreeCommand);
			addCommand(AddProductsToCategoriesEvent.EVENT_ADD_PRODUCTS_TO_CATALOG_TREE, AddProductsToCategoriesCommand);
			addCommand(AddSkusToProductsEvent.EVENT_ADD_SKUS_TO_CATALOG_TREE, AddSkusToProductsCommand);


			addCommand(ShowOfferWindowEvent.EVENT_SHOW_OFFER_WINDOW,ShowOfferWindowCommand);
			addCommand(AddUpdateOfferEvent.EVENT_ADD_UPDATE_OFFER,AddUpdateOfferCommand);
			addCommand(SaveCategoryEvent.EVENT_SAVE_CATALOG_CATEGORY, SaveCategoryCommand);
			addCommand(SaveProductEvent.EVENT_SAVE_CATALOG_PRODUCT, SaveProductCommand);
			addCommand(SaveSkuEvent.EVENT_SAVE_CATALOG_SKU, SaveCatalogSkuCommand);
			addCommand(EditCategoryEvent.EVENT_EDIT_CATALOG_CATEGORY, EditCategoryCommand);
			addCommand(NewCategoryEvent.EVENT_NEW_CATALOG_CATEGORY, NewCategoryCommand);

			addCommand(NewProductEvent.EVENT_NEW_CATALOG_PRODUCT, NewProductCommand);
			addCommand(EditProductEvent.EVENT_EDIT_CATALOG_PRODUCT, EditProductCommand);
		}
		
	}
}