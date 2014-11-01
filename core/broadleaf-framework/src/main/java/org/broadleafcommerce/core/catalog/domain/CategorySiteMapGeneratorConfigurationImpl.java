/*
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.core.catalog.domain;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminPresentationToOneLookup;
import org.broadleafcommerce.common.sitemap.domain.SiteMapGeneratorConfigurationImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * CategorySiteMapGenerator is controlled by this configuration.
 * 
 * @author Joshua Skorton (jskorton)
 */
@Entity
@Table(name = "BLC_CAT_SITE_MAP_GEN_CFG")
@AdminPresentationClass(friendlyName = "CategorySiteMapGeneratorConfigurationImpl")
public class CategorySiteMapGeneratorConfigurationImpl extends SiteMapGeneratorConfigurationImpl implements CategorySiteMapGeneratorConfiguration {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = CategoryImpl.class)
    @JoinColumn(name = "ROOT_CATEGORY_ID", nullable = false)
    @AdminPresentation(friendlyName = "CategorySiteMapGeneratorConfigurationImpl_Root_Category")
    @AdminPresentationToOneLookup()
    protected Category rootCategory;

    @Column(name = "STARTING_DEPTH", nullable = false)
    @AdminPresentation(friendlyName = "CategorySiteMapGeneratorConfigurationImpl_Starting_Depth")
    protected int startingDepth = 1;

    @Column(name = "ENDING_DEPTH", nullable = false)
    @AdminPresentation(friendlyName = "CategorySiteMapGeneratorConfigurationImpl_Ending_Depth")
    protected int endingDepth = 1;

    @Override
    public Category getRootCategory() {
        return rootCategory;
    }

    @Override
    public void setRootCategory(Category rootCategory) {
        this.rootCategory = rootCategory;
    }

    @Override
    public int getStartingDepth() {
        return startingDepth;
    }

    @Override
    public void setStartingDepth(int startingDepth) {
        this.startingDepth = startingDepth;
    }

    @Override
    public int getEndingDepth() {
        return endingDepth;
    }

    @Override
    public void setEndingDepth(int endingDepth) {
        this.endingDepth = endingDepth;
    }

}
