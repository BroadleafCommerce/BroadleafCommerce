/*
 * #%L
 * broadleaf-enterprise
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

import org.broadleafcommerce.common.persistence.ArchiveStatus;
import org.broadleafcommerce.common.persistence.Status;
import org.broadleafcommerce.common.presentation.AdminPresentation;

import javax.persistence.Embedded;

/**
 * @author by reginaldccole
 */
public final class WeaveArchiveStatus implements Status {

    @Embedded
    protected ArchiveStatus archiveStatus;


    @Override
    public void setArchived(Character archived) {
            getEmbeddableArchiveStatus(true).setArchived(archived);
    }

    private ArchiveStatus getEmbeddableArchiveStatus(boolean assign) {
        ArchiveStatus temp = archiveStatus;
        if (temp == null) {
            temp = new ArchiveStatus();
            if (assign) {
                archiveStatus = temp;
            }
        }
        return temp;
    }

    @Override
    public Character getArchived() {
        return getEmbeddableArchiveStatus(false).getArchived();
    }

    @Override
    public boolean isActive() {
         return 'Y'!=getArchived();
    }
}
