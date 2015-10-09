/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.common.sitemap.domain.weave;

import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatable;
import org.broadleafcommerce.common.extensibility.jpa.SiteDiscriminatableType;
import org.broadleafcommerce.common.presentation.AdminPresentationCollection;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfiguration;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;

/**
 * This class is meant as a template to provide overriding of the annotations on fields in 
 * <code>org.broadleafcommerce.common.sitemap.domain.SiteMapConfigurationImpl</code>.  This provides a
 * stop gap measure to allow someone to weave in the appropriate annotations in 4.0.x without forcing a schema change on those 
 * who prefer not to use it.  This should likely be removed in 4.1 for fixed annotations on the entity itself.
 * 
 * @author Jeff Fischer
 *
 */
public abstract class OptionalEnterpriseSiteMapConfigurationSiteMapGeneratorConfigurationTemplate {

    @OneToMany(mappedBy = "siteMapConfiguration", targetEntity = SiteMapGeneratorConfigurationImpl.class, cascade = { CascadeType.ALL }, orphanRemoval = true)
    @AdminPresentationCollection(friendlyName = "SiteMapConfigurationImpl_Generator_Configurations")
    @SiteDiscriminatable(type = SiteDiscriminatableType.SITE)
    protected List<SiteMapGeneratorConfiguration> siteMapGeneratorConfigurations = new ArrayList<SiteMapGeneratorConfiguration>();

}
