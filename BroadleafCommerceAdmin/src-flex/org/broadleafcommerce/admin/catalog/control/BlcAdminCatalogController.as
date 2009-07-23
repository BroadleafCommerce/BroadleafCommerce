package org.broadleafcommerce.admin.catalog.control
{
	import com.adobe.cairngorm.control.FrontController;
	
	import org.broadleafcommerce.admin.catalog.commands.BuildCatalogCommand;
	import org.broadleafcommerce.admin.catalog.commands.RetrieveCatalogCommand;
	import org.broadleafcommerce.admin.catalog.commands.StandardizeCatalogObjectsCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.AddCategoriesToCatalogTreeCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.EditCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.FindAllCatalogCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.NewCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.SaveCategoryCommand;
	import org.broadleafcommerce.admin.catalog.commands.category.ViewCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.AddProductsToCategoriesCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.EditProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.FindAllProductsCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.NewProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.SaveProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.UpdateParentsOfProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.product.ViewCurrentProductCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.AddSkusToProductsCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.EditSkuCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.FindAllCatalogSkusCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.NewSkuCommand;
	import org.broadleafcommerce.admin.catalog.commands.sku.SaveCatalogSkuCommand;
	import org.broadleafcommerce.admin.catalog.control.events.BuildCatalogEvent;
	import org.broadleafcommerce.admin.catalog.control.events.RetrieveCatalogEvent;
	import org.broadleafcommerce.admin.catalog.control.events.StandardizeCatalogObjectsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.AddCategoriesToCatalogTreeEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.EditCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.FindAllCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.NewCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.SaveCategoryEvent;
	import org.broadleafcommerce.admin.catalog.control.events.category.ViewCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.AddProductsToCategoriesEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.EditProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.FindAllProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.NewProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.SaveProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.UpdateParentsOfProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.product.ViewCurrentProductEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.AddSkusToProductsEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.EditSkuEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.FindAllSkusEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.NewSkuEvent;
	import org.broadleafcommerce.admin.catalog.control.events.sku.SaveSkuEvent;
	
	public class BlcAdminCatalogController extends FrontController
	{
		public function BlcAdminCatalogController()
		{
			super();
			addCommand(RetrieveCatalogEvent.EVENT_RETRIEVE_CATALOG, RetrieveCatalogCommand);
			addCommand(StandardizeCatalogObjectsEvent.EVENT_STANDARDIZE_CATALOG_OBJECTS, StandardizeCatalogObjectsCommand);
			addCommand(BuildCatalogEvent.EVENT_BUILD_CATALOG_TREE, BuildCatalogCommand);
			addCommand(AddCategoriesToCatalogTreeEvent.EVENT_ADD_CATEGORIES_TO_CATALOG_TREE, AddCategoriesToCatalogTreeCommand);
			addCommand(AddProductsToCategoriesEvent.EVENT_ADD_PRODUCTS_TO_CATALOG_TREE, AddProductsToCategoriesCommand);
			addCommand(AddSkusToProductsEvent.EVENT_ADD_SKUS_TO_CATALOG_TREE, AddSkusToProductsCommand);


			addCommand(FindAllCategoriesEvent.EVENT_FIND_ALL_CATALOG_CATEGORIES,FindAllCatalogCategoriesCommand);
			addCommand(ViewCategoriesEvent.EVENT_VIEW_CATEGORIES, ViewCategoriesCommand);
			addCommand(NewCategoryEvent.EVENT_NEW_CATALOG_CATEGORY, NewCategoryCommand);
			addCommand(EditCategoryEvent.EVENT_EDIT_CATALOG_CATEGORY, EditCategoryCommand);
			addCommand(SaveCategoryEvent.EVENT_SAVE_CATALOG_CATEGORY, SaveCategoryCommand);

			addCommand(FindAllProductsEvent.EVENT_FIND_ALL_PRODUCTS, FindAllProductsCommand);
			addCommand(ViewCurrentProductEvent.EVENT_VIEW_CURRENT_PRODUCT, ViewCurrentProductCommand);
			addCommand(NewProductEvent.EVENT_NEW_CATALOG_PRODUCT, NewProductCommand);
			addCommand(EditProductEvent.EVENT_EDIT_CATALOG_PRODUCT, EditProductCommand);
			addCommand(UpdateParentsOfProductEvent.EVENT_ADD_PARENT_TO_PRODUCT, UpdateParentsOfProductCommand);
			addCommand(SaveProductEvent.EVENT_SAVE_CATALOG_PRODUCT, SaveProductCommand);

			addCommand(FindAllSkusEvent.EVENT_FIND_ALL_CATALOG_SKUS, FindAllCatalogSkusCommand);
			addCommand(EditSkuEvent.EVENT_EDIT_SKU, EditSkuCommand);
			addCommand(SaveSkuEvent.EVENT_SAVE_CATALOG_SKU, SaveCatalogSkuCommand);
			addCommand(NewSkuEvent.EVENT_NEW_SKU, NewSkuCommand);



		}
		
	}
}