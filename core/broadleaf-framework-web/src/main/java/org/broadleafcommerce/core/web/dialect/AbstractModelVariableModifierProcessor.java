package org.broadleafcommerce.core.web.dialect;

import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.ProcessorResult;
import org.thymeleaf.processor.element.AbstractElementProcessor;

/**
 * @author apazzolini
 * 
 * Wrapper class around Thymeleaf's AbstractElementProcessor that facilitates adding Objects
 * to the current evaluation context (model) for processing in the remainder of the page.
 *
 */
public abstract class AbstractModelVariableModifierProcessor extends AbstractElementProcessor {
	
	private Arguments arguments;
	
	public AbstractModelVariableModifierProcessor(String elementName) {
		super(elementName);
	}

	/**
	 * This method will handle calling the modifyModelAttributes abstract method and return
	 * an "OK" processor result
	 */
	@Override
    protected ProcessorResult processElement(final Arguments arguments, final Element element) {
		this.arguments = arguments;
		modifyModelAttributes(arguments, element);
		return ProcessorResult.OK;
    }
	
	/**
	 * Helper method to add a value to the expression evaluation root (model) Map
	 * @param key the key to add to the model
	 * @param value the value represented by the key
	 */
	@SuppressWarnings("unchecked")
	protected void addToModel(String key, Object value) {
		((Map<String, Object>) arguments.getExpressionEvaluationRoot()).put(key, value);
	}
	
	
	/**
	 * This method must be overriding by a processor that wishes to modify the model. It will
	 * be called by this abstract processor in the correct precendence in the evaluation chain.
	 * @param arguments
	 * @param element
	 */
	protected abstract void modifyModelAttributes(Arguments arguments, Element element);
}
