/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
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
package org.broadleafcommerce.core.web.seo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.page.dto.PageDTO;
import org.broadleafcommerce.core.catalog.domain.Category;
import org.broadleafcommerce.core.catalog.domain.Product;
import org.broadleafcommerce.presentation.condition.ConditionalOnTemplating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * @author Chris Kittrell (ckittrell)
 */
@Service("blSeoPropertyService")
@ConditionalOnTemplating
public class SeoPropertyServiceImpl implements SeoPropertyService {

    protected static final Log LOG = LogFactory.getLog(SeoPropertyServiceImpl.class);

    @Autowired
    protected Environment env;

    @Resource(name = "blSeoPropertyGenerators")
    protected List<SeoPropertyGenerator> generators;

    @Override
    public Map<String, String> getSeoProperties(Product product) {
        Map<String, String> properties = new HashMap<>();

        for (SeoPropertyGenerator generator : generators) {
            Map<String, String> propertiesFromGenerator = generator.gatherSeoProperties(product);

            properties.putAll(propertiesFromGenerator);
        }

        return properties;
    }

    @Override
    public Map<String, String> getSeoProperties(Category category) {
        Map<String, String> properties = new HashMap<>();

        for (SeoPropertyGenerator generator : generators) {
            Map<String, String> propertiesFromGenerator = generator.gatherSeoProperties(category);

            properties.putAll(propertiesFromGenerator);
        }

        return properties;
    }

    @Override
    public Map<String, String> getSeoProperties(PageDTO page) {
        Map<String, String> properties = new HashMap<>();

        for (SeoPropertyGenerator generator : generators) {
            Map<String, String> propertiesFromGenerator = generator.gatherSeoProperties(page);

            properties.putAll(propertiesFromGenerator);
        }

        return properties;
    }

}
