/*
 * #%L
 * BroadleafCommerce Framework Web
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.core.web.linkeddata.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.core.web.linkeddata.generator.LinkedDataGenerator;
import org.broadleafcommerce.presentation.dialect.AbstractBroadleafTagReplacementProcessor;
import org.broadleafcommerce.presentation.model.BroadleafTemplateContext;
import org.broadleafcommerce.presentation.model.BroadleafTemplateElement;
import org.broadleafcommerce.presentation.model.BroadleafTemplateModel;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * This processor replaces linkedData tags with metadata for search engine optimization. The
 * data is formatted to Schema.org and Google standards.
 *
 * @author Jacob Mitash
 * @author Nathan Moore (nathanmoore).
 */
@Component("blLinkedDataProcessor")
public class LinkedDataProcessor extends AbstractBroadleafTagReplacementProcessor {
    private final Log LOG = LogFactory.getLog(LinkedDataProcessor.class);

    @Resource(name = "blLinkedDataGenerators")
    protected List<LinkedDataGenerator> linkedDataGenerators;

    @Override
    public BroadleafTemplateModel getReplacementModel(final String s, final Map<String, String> map, 
                                                      final BroadleafTemplateContext context) {
        String linkedDataText = "<script type=\"application/ld+json\">\n" +
                                    getData(context.getRequest()) +
                                "\n</script>";

        final BroadleafTemplateModel model = context.createModel();
        final BroadleafTemplateElement linkedData = context.createTextElement(linkedDataText);
        model.addElement(linkedData);

        return model;
    }

    /**
     * Get the metadata for the specific page
     * @param request the user request
     * @return the JSON string representation of the linked data
     */
    protected String getData(final HttpServletRequest request) {
        final String requestUrl = request.getRequestURL().toString();
        final JSONArray schemaObjects = new JSONArray();

        for (final LinkedDataGenerator linkedDataGenerator : linkedDataGenerators) {
            if (linkedDataGenerator.canHandle(request)) {
                try {
                    linkedDataGenerator.getLinkedDataJSON(requestUrl, request, schemaObjects);
                } catch(final JSONException e){
                    // the only reason for this exception to be thrown is a null key being put on a JSONObject, 
                    // which shouldn't ever be expected to actually happen
                    LOG.error("A JSON exception occurred while generating LinkedData", e);
                }
            }
        }
        
        return schemaObjects.toString();
    }

    @Override
    public String getName() {
        return "linkedData";
    }
}
