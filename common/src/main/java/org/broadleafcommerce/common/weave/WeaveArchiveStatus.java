/*
 * #%L
 * broadleaf-enterprise
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
 * %%
 * NOTICE:  All information contained herein is, and remains
 * the property of Broadleaf Commerce, LLC
 * The intellectual and technical concepts contained
 * herein are proprietary to Broadleaf Commerce, LLC
 * and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Broadleaf Commerce, LLC.
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
