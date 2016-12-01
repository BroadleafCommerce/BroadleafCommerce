/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.media.domain;

import org.broadleafcommerce.common.util.UnknownUnwrapTypeException;

/**
 * A null safe media object.
 * @author bpolster
 *
 */
public class MediaDto {

    protected long id;
    protected String url = "";
    protected String title = "";
    protected String altText = "";
    protected String tags = "";

    
    public Long getId() {
        return id;
    }

    
    public void setId(Long id) {
        this.id = id;
    }

    
    public String getUrl() {
        return url;
    }

    
    public void setUrl(String url) {
        this.url = url;
    }

    
    public String getTitle() {
        return title;
    }

    
    public void setTitle(String title) {
        this.title = title;
    }

    
    public String getAltText() {
        return altText;
    }

    
    public void setAltText(String altText) {
        this.altText = altText;
    }
    
    
    public String getTags() {
        return tags;
    }
    
    
    public void setTags(String tags) {
        this.tags = tags;
    }

    
    public boolean isUnwrappableAs(Class unwrapType) {
        return false;
    }

    
    public <T> T unwrap(Class<T> unwrapType) {
        throw new UnknownUnwrapTypeException(unwrapType);
    }


}
