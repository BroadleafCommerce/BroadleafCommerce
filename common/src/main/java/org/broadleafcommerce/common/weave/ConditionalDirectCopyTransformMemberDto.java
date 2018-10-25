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
package org.broadleafcommerce.common.weave;

import java.io.Serializable;

/**
 * Simple data object to hold direct copy transform config information. This object also
 * defines the Spring property whose value must be true for the configuration to take effect.
 * This information is roughly analogous to the {@link org.broadleafcommerce.common.extensibility.jpa.copy.DirectCopyTransformMember}
 * annotation.
 *
 * @author Jeff Fischer
 */
public class ConditionalDirectCopyTransformMemberDto implements Serializable {

    protected String[] templateTokens;
    protected boolean renameMethodOverlaps = false;

    /**
     * <p>Defaults to false.</p>
     * <p>skipOverlaps is useful if you want to make sure the load time weaving does not try to insert methods you have
     * already implemented. For example, if you have already implemented the Status interface and methods (e.g. Offer),
     * then you don't want the system to try to overwrite these.</p>
     *
     * @return
     */
    protected boolean skipOverlaps = false;
    protected String conditionalProperty;
    protected Boolean conditionalValue;

    public String[] getTemplateTokens() {
        return templateTokens;
    }

    public void setTemplateTokens(String[] templateTokens) {
        this.templateTokens = templateTokens;
    }

    public boolean isRenameMethodOverlaps() {
        return renameMethodOverlaps;
    }

    public void setRenameMethodOverlaps(boolean renameMethodOverlaps) {
        this.renameMethodOverlaps = renameMethodOverlaps;
    }

    public boolean isSkipOverlaps() {
        return skipOverlaps;
    }

    public void setSkipOverlaps(boolean skipOverlaps) {
        this.skipOverlaps = skipOverlaps;
    }

    public String getConditionalProperty() {
        return conditionalProperty;
    }

    public void setConditionalProperty(String conditionalProperty) {
        this.conditionalProperty = conditionalProperty;
    }

    public Boolean getConditionalValue() {
        return conditionalValue;
    }

    public void setConditionalValue(Boolean conditionalValue) {
        this.conditionalValue = conditionalValue;
    }
}
