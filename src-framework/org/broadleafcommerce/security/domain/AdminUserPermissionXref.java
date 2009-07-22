/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.security.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


@Entity
@Table(name = "BLC_ADMIN_USER_PERMISSION_XREF")
public class AdminUserPermissionXref {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The category id. */
    @EmbeddedId
    AdminUserPermissionXrefPK adminUserPermissionXrefPK;

    @Column(name = "ADMIN_PERMISSION_ENABLED", nullable = false)
    private boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public AdminUserPermissionXrefPK getAdminUserPermissionXrefPK() {
        return adminUserPermissionXrefPK;
    }

    public static class AdminUserPermissionXrefPK implements Serializable {
        /** The Constant serialVersionUID. */
        private static final long serialVersionUID = 1L;

        @Column(name = "ADMIN_USER_ID", nullable = false)
        private Long adminUserId;

        @Column(name = "ADMIN_PERMISSION_ID", nullable = false)
        private Long adminPermissionId;

        public Long getAdminUserId() {
            return adminUserId;
        }

        public void setAdminUserId(Long adminUserId) {
            this.adminUserId = adminUserId;
        }

        public Long getAdminPermissionId() {
            return adminPermissionId;
        }

        public void setAdminPermissionId(Long adminPermissionId) {
            this.adminPermissionId = adminPermissionId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            else if (!(obj instanceof AdminUserPermissionXrefPK)) return false;

            return adminUserId.equals(((AdminUserPermissionXrefPK) obj).getAdminUserId())
            && adminPermissionId.equals(((AdminUserPermissionXrefPK) obj).getAdminPermissionId());
        }

        @Override
        public int hashCode() {
            return adminUserId.hashCode() + adminPermissionId.hashCode();
        }
    }
}
