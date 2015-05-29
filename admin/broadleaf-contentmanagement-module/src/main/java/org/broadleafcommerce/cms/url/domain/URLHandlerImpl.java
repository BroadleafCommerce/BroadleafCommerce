/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2014 Broadleaf Commerce
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
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.web.Locatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;


/**
 * @author priyeshpatel
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_URL_HANDLER")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "blStandardElements")
@AdminPresentationClass(populateToOneFields = PopulateToOneFieldsEnum.TRUE, friendlyName = "URLHandlerImpl_friendyName")
@DirectCopyTransform({
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps=true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class URLHandlerImpl implements URLHandler, Locatable, AdminMainEntity, ProfileEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "URLHandlerID")
    @GenericGenerator(
        name="URLHandlerID",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="URLHandlerImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.url.domain.URLHandlerImpl")
        }
    )
    @Column(name = "URL_HANDLER_ID")
    @AdminPresentation(friendlyName = "URLHandlerImpl_ID", order = 1, group = "URLHandlerImpl_friendyName", groupOrder = 1, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @AdminPresentation(friendlyName = "URLHandlerImpl_incomingURL", order = 1, group = "URLHandlerImpl_friendyName", prominent = true, groupOrder = 1,
            helpText = "urlHandlerIncoming_help")
    @Column(name = "INCOMING_URL", nullable = false)
    @Index(name="INCOMING_URL_INDEX", columnNames={"INCOMING_URL"})
    protected String incomingURL;

    @Column(name = "NEW_URL", nullable = false)
    @AdminPresentation(friendlyName = "URLHandlerImpl_newURL", order = 1, group = "URLHandlerImpl_friendyName", prominent = true, groupOrder = 1,
            helpText = "urlHandlerNew_help")
    protected String newURL;

    @Column(name = "URL_REDIRECT_TYPE")
    @AdminPresentation(friendlyName = "URLHandlerImpl_redirectType", order = 4, group = "URLHandlerImpl_friendyName", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration = "org.broadleafcommerce.cms.url.type.URLRedirectType", groupOrder = 2, prominent = true)
    protected String urlRedirectType;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getIncomingURL() {
        return incomingURL;
    }

    @Override
    public void setIncomingURL(String incomingURL) {
        this.incomingURL = incomingURL;
    }

    @Override
    public String getNewURL() {
        return newURL;
    }

    @Override
    public void setNewURL(String newURL) {
        this.newURL = newURL;
    }

    @Override
    public URLRedirectType getUrlRedirectType() {
        return URLRedirectType.getInstance(urlRedirectType);
    }

    @Override
    public void setUrlRedirectType(URLRedirectType redirectType) {
        this.urlRedirectType = redirectType.getType();
    }

    @Override
    public String getMainEntityName() {
        return getIncomingURL();
    }

    @Override
    public String getLocation() {
        String location = getIncomingURL();
        if (location == null) {
            return null;
        } else if (hasRegExCharacters(location)) {
            return getNewURL();
        } else {
            return location;
        }
    }

    /**
     * In a preview environment, {@link #getLocation()} attempts to navigate to the 
     * provided URL.    If the URL contains a Regular Expression, then we can't 
     * navigate to it. 
     * 
     * @param location
     * @return
     */
    protected boolean hasRegExCharacters(String location) {
        return location.contains(".") ||
                location.contains("(") ||
                location.contains(")") ||
                location.contains("?") ||
                location.contains("*") ||
                location.contains("^") ||
                location.contains("$") ||
                location.contains("[") ||
                location.contains("{") ||
                location.contains("|") ||
                location.contains("+") ||
                location.contains("\\");
    }

    @Override
    public <G extends URLHandler> CreateResponse<G> createOrRetrieveCopyInstance(MultiTenantCopyContext context) throws CloneNotSupportedException {
        CreateResponse<G> createResponse = context.createOrRetrieveCopyInstance(this);
        if (createResponse.isAlreadyPopulated()) {
            return createResponse;
        }
        URLHandler cloned = createResponse.getClone();
        cloned.setIncomingURL(incomingURL);
        cloned.setNewURL(newURL);
        cloned.setUrlRedirectType( URLRedirectType.getInstance(urlRedirectType));

        return createResponse;
    }
}
