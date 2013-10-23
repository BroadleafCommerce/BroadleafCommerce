/*
 * #%L
 * BroadleafCommerce Common Libraries
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
package org.broadleafcommerce.common.persistence;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.broadleafcommerce.common.presentation.AdminPresentation;

/**
 * @author Jeff Fischer
 */
@Embeddable
public class PreviewStatus implements Serializable, Previewable {

    @Column(name = "IS_PREVIEW")
    @AdminPresentation(excluded = true)
    protected Boolean isPreview;

    @Override
    public Boolean getPreview() {
        return isPreview;
    }

    @Override
    public void setPreview(Boolean preview) {
        isPreview = preview;
    }
}
