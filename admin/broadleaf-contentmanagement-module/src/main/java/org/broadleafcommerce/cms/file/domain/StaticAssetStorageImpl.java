/*-
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.common.persistence.IdOverrideTableGenerator;
import org.hibernate.Length;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.sql.Blob;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

/**
 * Created by jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET_STRG", indexes = {
        @Index(name = "STATIC_ASSET_ID_INDEX", columnList = "STATIC_ASSET_ID")
})
public class StaticAssetStorageImpl implements StaticAssetStorage {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StaticAssetStorageId")
    @GenericGenerator(
            name = "StaticAssetStorageId",
            type = IdOverrideTableGenerator.class,
            parameters = {
                    @Parameter(name = "segment_value", value = "StaticAssetStorageImpl"),
                    @Parameter(name = "entity_name", value = "org.broadleafcommerce.cms.file.domain.StaticAssetStorageImpl")
            }
    )
    @Column(name = "STATIC_ASSET_STRG_ID")
    protected Long id;

    @Column(name = "STATIC_ASSET_ID", nullable = false)
    protected Long staticAssetId;

    @Lob
    @Column(name = "FILE_DATA", length = Length.LONG32 - 1)
    protected Blob fileData;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Blob getFileData() {
        return fileData;
    }

    @Override
    public void setFileData(Blob fileData) {
        this.fileData = fileData;
    }

    @Override
    public Long getStaticAssetId() {
        return staticAssetId;
    }

    @Override
    public void setStaticAssetId(Long staticAssetId) {
        this.staticAssetId = staticAssetId;
    }
}
