package org.broadleafcommerce.core.web.catalog.dialect;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.spring3.context.SpringWebContext;
import org.thymeleaf.standard.expression.StandardExpressionProcessor;

public class TestProcessor extends AbstractTextChildModifierAttrProcessor {

	public TestProcessor() {
		super("test");
	}
	
	@Override
	protected String getText(Arguments arguments, Element element, String attributeName) {
		SpringWebContext s = ((SpringWebContext) arguments.getContext()) ;
		return "Andre: " + StandardExpressionProcessor.processExpression(arguments, element.getAttributeValue(attributeName));
	}

	@Override
	public int getPrecedence() {
		return 10;
	}

}
