/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.admin.domain.AdminMainEntity;
import org.broadleafcommerce.common.copy.CreateResponse;
import org.broadleafcommerce.common.copy.MultiTenantCopyContext;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransform;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember;
import org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformTypes;
import org.broadleafcommerce.common.extensibility.jpa.copy.ProfileEntity;
import org.broadleafcommerce.common.presentation.*;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.broadleafcommerce.common.web.Locatable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import javax.persistence.Entity;
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
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.SANDBOX, skipOverlaps = true),
        @DirectCopyTransformMember(templateTokens = DirectCopyTransformTypes.MULTITENANT_SITE)
})
public class URLHandlerImpl implements URLHandler, Locatable, AdminMainEntity, ProfileEntity, URLHandlerAdminPresentation {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "URLHandlerID")
    @GenericGenerator(
            name = "URLHandlerID",
            strategy = "org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
            parameters = {
                    @Parameter(name = "segment_value", value = "URLHandlerImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.cms.url.domain.URLHandlerImpl")
            }
    )
    @Column(name = "URL_HANDLER_ID")
    @AdminPresentation(friendlyName = "URLHandlerImpl_ID", order = 1, group = GroupName.General, visibility = VisibilityEnum.HIDDEN_ALL)
    protected Long id;

    @AdminPresentation(friendlyName = "URLHandlerImpl_incomingURL", order = 1, group = GroupName.General, prominent = true,
            helpText = "urlHandlerIncoming_help",
            validationConfigurations = {
                    @ValidationConfiguration(validationImplementation = "blUriPropertyValidator"),
                    @ValidationConfiguration(
                            validationImplementation = "blUniqueValueValidator",
                            configurationItems = {
                                    @ConfigurationItem(
                                            itemName = ConfigurationItem.ERROR_MESSAGE,
                                            itemValue = "This URL is already in use. Please provide a unique URL."
                                    )
                            }
                    )
            }
    )
    @Column(name = "INCOMING_URL", nullable = false)
    @Index(name = "INCOMING_URL_INDEX", columnNames = {"INCOMING_URL"})
    protected String incomingURL;

    @Column(name = "NEW_URL", nullable = false)
    @AdminPresentation(friendlyName = "URLHandlerImpl_newURL", order = 1, group = GroupName.General, prominent = true,
            helpText = "urlHandlerNew_help", defaultValue = "")
    protected String newURL;

    @Column(name = "URL_REDIRECT_TYPE")
    @AdminPresentation(friendlyName = "URLHandlerImpl_redirectType", order = 4, group = GroupName.Redirect,
            fieldType = SupportedFieldType.BROADLEAF_ENUMERATION,
            broadleafEnumeration = "org.broadleafcommerce.cms.url.type.URLRedirectType",
            prominent = true)
    protected String urlRedirectType;

    @Column(name = "IS_REGEX")
    @AdminPresentation(friendlyName = "URLHandlerImpl_isRegexHandler", order = 1, group = GroupName.General, prominent = true,
            groupOrder = 1,
            helpText = "urlHandlerIsRegexHandler_help")
    @Index(name = "IS_REGEX_HANDLER_INDEX", columnNames = {"IS_REGEX"})
    protected Boolean isRegex = false;

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
    public boolean isRegexHandler() {
        if (isRegex == null) {
            if (hasRegExCharacters(getIncomingURL())) {
                return true;
            }
            return false;
        }
        return isRegex;
    }

    @Deprecated
    @Override
    public void setRegexHandler(boolean regexHandler) {
        this.isRegex = regexHandler;
    }

    @Override
    public void setRegexHandler(Boolean regexHandler) {
        this.isRegex = regexHandler != null ? regexHandler : false;
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
        } else if (isRegexHandler()) {
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
        cloned.setUrlRedirectType(URLRedirectType.getInstance(urlRedirectType));
        cloned.setRegexHandler(isRegex==null?false:isRegex);
        return createResponse;
    }
}
