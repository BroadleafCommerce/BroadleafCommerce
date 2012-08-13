/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractAttributeModifierAttrProcessor;

import java.util.HashMap;
import java.util.Map;

/**
 * A Thymeleaf processor that replaces the value attribute with a CSRF token
 * for the session
 * 
 * @author apazzolini
 */
public class CsrfTokenProcessor extends AbstractAttributeModifierAttrProcessor {
	
	protected boolean allowMultipleSorts = false;
	
	/**
	 * Sets the name of this processor to be used in Thymeleaf template
	 */
	public CsrfTokenProcessor() {
		super("csrftoken");
	}
	
	@Override
	public int getPrecedence() {
		return 10000;
	}

	@Override
	protected Map<String, String> getModifiedAttributeValues(Arguments arguments, Element element, String attributeName) {
		Map<String, String> attrs = new HashMap<String, String>();
		
		String csrfToken = null;
		try {
			csrfToken = ProcessorUtils.getExploitProtectionService(arguments).getCSRFToken();
		} catch (ServiceException e) {
			throw new RuntimeException("Could not get a CSRF token for this session", e);
		}
		
		attrs.put("value", csrfToken);
		return attrs;
	}

	@Override
	protected ModificationType getModificationType(Arguments arguments, Element element, String attributeName, String newAttributeName) {
		return ModificationType.SUBSTITUTION;
	}

	@Override
	protected boolean removeAttributeIfEmpty(Arguments arguments, Element element, String attributeName, String newAttributeName) {
		return true;
	}

	@Override
	protected boolean recomputeProcessorsAfterExecution(Arguments arguments, Element element, String attributeName) {
		return false;
	}
}
