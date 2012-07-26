/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.core.web.processor;

import org.broadleafcommerce.common.web.dialect.AbstractModelVariableModifierProcessor;
import org.broadleafcommerce.core.rating.domain.RatingSummary;
import org.broadleafcommerce.core.rating.service.RatingService;
import org.broadleafcommerce.core.rating.service.type.RatingType;
import org.broadleafcommerce.core.web.util.ProcessorUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;

/**
 * A Thymeleaf processor that will add the product ratings and reviews to the model
 *
 * @author jfridye
 */
public class RatingsProcessor extends AbstractModelVariableModifierProcessor {

    /**
     * Sets the name of this processor to be used in Thymeleaf template
     *
     * NOTE: Thymeleaf normalizes the attribute names by converting all to lower-case
     * we will use the underscore instead of camel case to avoid confusion
     *
     */
    public RatingsProcessor() {
        super("ratings");
    }

    @Override
    public int getPrecedence() {
        return 10000;
    }

    @Override
    protected void modifyModelAttributes(Arguments arguments, Element element) {

    	RatingService ratingService = ProcessorUtils.getRatingService(arguments);

        String ratingVar = element.getAttributeValue("ratingsVar");
        String itemId = element.getAttributeValue("itemId");

        RatingSummary ratingSummary = ratingService.readRatingSummary(itemId, RatingType.PRODUCT);
        
        if (ratingSummary != null) {
            addToModel(ratingVar, ratingSummary);
        }
        
    }

}
