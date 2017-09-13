/*
 * #%L
 * BroadleafCommerce Common Libraries
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
