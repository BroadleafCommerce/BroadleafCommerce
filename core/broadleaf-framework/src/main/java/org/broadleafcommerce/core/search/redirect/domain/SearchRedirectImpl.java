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
package org.broadleafcommerce.core.search.redirect.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.RequiredOverride;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.BLCSystemProperty;
import org.broadleafcommerce.common.util.DateUtil;
import org.broadleafcommerce.common.web.resource.resolver.BLCSystemPropertyResourceResolver;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * @author priyeshpatel
 * 
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_SEARCH_INTERCEPT")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "SearchRedirectImpl_friendyName")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE),
})
public class SearchRedirectImpl implements SearchRedirect, java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Transient
    private static final Log LOG = LogFactory.getLog(SearchRedirectImpl.class);
    
    @Id
    @GeneratedValue(generator = "SearchRedirectID")
    @GenericGenerator(
        name="SearchRedirectID",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="SearchRedirectImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.core.search.redirect.domain.SearchRedirectImpl")
        }
    )
    @Column(name = "SEARCH_REDIRECT_ID")
    @AdminPresentation(visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;
    
    @Column(name = "PRIORITY")
    @AdminPresentation(excluded = true)
    protected Integer searchPriority;

    @AdminPresentation(friendlyName = "SearchRedirectImpl_searchTerm", order = 1000, group = "SearchRedirectImpl_description", prominent = true, groupOrder = 1)
    @Column(name = "SEARCH_TERM", nullable = false)
    protected String searchTerm;
    
    @Column(name = "URL", nullable = false)
    @AdminPresentation(friendlyName = "SearchRedirectImpl_url", order = 2000, group = "SearchRedirectImpl_description", prominent = true, groupOrder = 1)
    protected String url;

    /** The active start date. */
    @Column(name = "ACTIVE_START_DATE" )
    @AdminPresentation(friendlyName = "SkuImpl_Sku_Start_Date",
            order = 3000, group = "SearchRedirectImpl_description",
            tooltip = "skuStartDateTooltip", groupOrder = 1,
            requiredOverride = RequiredOverride.REQUIRED,
            defaultValue = "today")
    protected Date activeStartDate;

    /** The active end date. */
    @Column(name = "ACTIVE_END_DATE")
    @Index(name="SEARCH_ACTIVE_INDEX", columnNames={"ACTIVE_START_DATE","ACTIVE_END_DATE"})
    @AdminPresentation(friendlyName = "SkuImpl_Sku_End_Date", order = 4000, group = "SearchRedirectImpl_description", tooltip = "skuEndDateTooltip", groupOrder = 1)
    protected Date activeEndDate;
    
    @Override
    public Date getActiveStartDate() {
        return activeStartDate;
    }

    @Override
    public void setActiveStartDate(Date activeStartDate) {
        this.activeStartDate = activeStartDate;
    }

    @Override
    public Date getActiveEndDate() {
        return activeEndDate;
    }

    @Override
    public void setActiveEndDate(Date activeEndDate) {
        this.activeEndDate = activeEndDate;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getSearchTerm() {
        return searchTerm;
    }

    @Override
    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }
    
    @Override
    public Integer getSearchPriority() {
        return searchPriority;
    }

    @Override
    public void setSearchPriority(Integer searchPriority) {
        this.searchPriority = searchPriority;
    }

    @Override
    public boolean isActive() {
        Long date = SystemTime.asMillis(true);
        boolean isNullActiveStartDateActive = BLCSystemProperty.resolveBooleanSystemProperty("searchRedirect.is.null.activeStartDate.active");

        boolean isActive;
        if (isNullActiveStartDateActive) {
            isActive = (getActiveStartDate() == null || getActiveStartDate().getTime() <= date) && (getActiveEndDate() == null || getActiveEndDate().getTime() > date);
        } else {
            isActive = (getActiveStartDate() != null && getActiveStartDate().getTime() <= date) && (getActiveEndDate() == null || getActiveEndDate().getTime() > date);
        }

        if (LOG.isDebugEnabled() && !isActive) {
            LOG.debug("product, " + id + ", inactive due to date");
        }
        return isActive;
    }

}
