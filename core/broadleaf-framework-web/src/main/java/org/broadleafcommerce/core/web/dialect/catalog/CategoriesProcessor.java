package org.broadleafcommerce.core.web.dialect.catalog;

import java.util.List;

import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.broadleafcommerce.core.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.web.dialect.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * @author apazzolini
 *
 */
public class CategoriesProcessor extends AbstractModelVariableModifierProcessor {

	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
	public CategoriesProcessor() {
		super("categories");
	}
	
	@Override
	public int getPrecedence() {
		return 10000;
	}

	@Override
	protected void modifyModelAttributes(Arguments arguments, Element element) {
		CatalogService catalogService = ProcessorUtils.getCatalogService(arguments);
		
		String resultVar = element.getAttributeValueFromNormalizedName("resultvar");
		String parentCategory = element.getAttributeValueFromNormalizedName("parentcategory");
		
		// TODO: Potentially write an algorithm that will pick the minimum depth category
		// instead of the first category in the list
		List<Category> categories = catalogService.findCategoriesByName(parentCategory);
		if (categories != null && categories.size() > 0) {
			addToModel(resultVar, catalogService.findAllSubCategories(categories.get(0)));
		}
	}
}
