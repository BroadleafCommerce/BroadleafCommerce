package org.broadleafcommerce.core.web.catalog.dialect;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.broadleafcommerce.core.catalog.service.CatalogService;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;

public class CategoriesProcessor extends AbstractMarkupSubstitutionElementProcessor {

	public CategoriesProcessor() {
		super("categories");
	}
	
	@Override
	public int getPrecedence() {
		return 10000;
	}

	@Override
	protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
		CatalogService catalogService = CatalogProcessorUtils.getCatalogService(arguments);
		String resultVar = element.getAttributeValueFromNormalizedName("resultvar");
		Long parentCategory = Long.parseLong(element.getAttributeValueFromNormalizedName("parentcategory"));
		((Map<String, Object>)arguments.getExpressionEvaluationRoot()).put(resultVar, catalogService.findAllSubCategories(catalogService.findCategoryById(parentCategory)));
		return new ArrayList<Node>();
	}

}
