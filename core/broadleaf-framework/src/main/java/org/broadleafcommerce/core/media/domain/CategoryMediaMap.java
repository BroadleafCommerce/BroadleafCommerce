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

package org.broadleafcommerce.core.media.domain;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "BLC_CATEGORY_MEDIA_MAP")
public class CategoryMediaMap implements Serializable {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    @EmbeddedId
    CategoryMediaMapPK categoryMediaMapPK;

    @Column(name = "KEY", nullable = false)
    private String key;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public CategoryMediaMapPK getCategoryMediaMapPK() {
        return categoryMediaMapPK;
    }

    public static class CategoryMediaMapPK implements Serializable {
        /** The Constant serialVersionUID. */
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
            else if (!(obj instanceof CategoryMediaMapPK)) return false;

            return categoryId.equals(((CategoryMediaMapPK) obj).getCategoryId()) &&
            mediaId.equals(((CategoryMediaMapPK) obj).getMediaId());
        }

        @Override
        public int hashCode() {
            return categoryId.hashCode() + mediaId.hashCode();
        }
    }
}
