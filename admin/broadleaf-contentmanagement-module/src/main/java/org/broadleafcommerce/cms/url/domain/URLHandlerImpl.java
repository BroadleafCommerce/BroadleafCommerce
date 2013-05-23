/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 *
 */
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;
import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.PopulateToOneFieldsEnum;
import org.broadleafcommerce.common.presentation.client.SupportedFieldType;
import org.broadleafcommerce.common.presentation.client.VisibilityEnum;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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
public class URLHandlerImpl implements URLHandler, java.io.Serializable {

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

    @AdminPresentation(friendlyName = "URLHandlerImpl_incomingURL", order = 1, group = "URLHandlerImpl_friendyName", prominent = true, groupOrder = 1)
    @Column(name = "INCOMING_URL", nullable = false)
    protected String incomingURL;

    @Column(name = "NEW_URL", nullable = false)
    @AdminPresentation(friendlyName = "URLHandlerImpl_newURL", order = 1, group = "URLHandlerImpl_friendyName", prominent = true, groupOrder = 1)
    protected String newURL;

    @Column(name = "URL_REDIRECT_TYPE")
    @AdminPresentation(friendlyName = "URLHandlerImpl_redirectType", order = 4, group = "URLHandlerImpl_friendyName", fieldType = SupportedFieldType.BROADLEAF_ENUMERATION, broadleafEnumeration = "org.broadleafcommerce.cms.url.type.URLRedirectType", groupOrder = 2, prominent = true)
    protected String urlRedirectType;


    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#getId()
          */
    @Override
    public Long getId() {
        return id;
    }

    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#setId(java.lang.Long)
          */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#getIncomingURL()
          */
    @Override
    public String getIncomingURL() {
        return incomingURL;
    }

    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#setIncomingURL(java.lang.String)
          */
    @Override
    public void setIncomingURL(String incomingURL) {
        this.incomingURL = incomingURL;
    }

    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#getNewURL()
          */
    @Override
    public String getNewURL() {
        return newURL;
    }

    /* (non-Javadoc)
          * @see org.broadleafcommerce.common.url.URLHandler#setNewURL(java.lang.String)
          */
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

}
