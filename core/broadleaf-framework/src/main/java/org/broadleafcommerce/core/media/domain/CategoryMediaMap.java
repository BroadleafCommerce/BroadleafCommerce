/*-
 * #%L
 * BroadleafCommerce Framework
 * %%
 * Copyright (C) 2009 - 2025 Broadleaf Commerce
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
package org.broadleafcommerce.core.media.domain;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "BLC_CATEGORY_MEDIA_MAP")
public class CategoryMediaMap implements Serializable {

    /**
     * The Constant serialVersionUID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    CategoryMediaMapPK categoryMediaMapPK;

    @Column(name = "KEY", nullable = false)
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public CategoryMediaMapPK getCategoryMediaMapPK() {
        return categoryMediaMapPK;
    }

    public static class CategoryMediaMapPK implements Serializable {

        /**
         * The Constant serialVersionUID.
         */
        @Serial
        private static final long serialVersionUID = 1L;

        @Column(name = "CATEGORY_ID", nullable = false)
        private Long categoryId;

        @Column(name = "MEDIA_ID", nullable = false)
        private Long mediaId;

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public Long getMediaId() {
            return mediaId;
        }

        public void setMediaId(Long mediaId) {
            this.mediaId = mediaId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            else if (!getClass().isAssignableFrom(obj.getClass())) return false;

            return categoryId.equals(((CategoryMediaMapPK) obj).getCategoryId())
                    && mediaId.equals(((CategoryMediaMapPK) obj).getMediaId());
        }

        @Override
        public int hashCode() {
            return categoryId.hashCode() + mediaId.hashCode();
        }
    }

}
