/*
 * #%L
 * BroadleafCommerce CMS Module
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
package org.broadleafcommerce.cms.file.domain;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.Parameter;

import java.sql.Blob;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created by jfischer
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET_STRG")
public class StaticAssetStorageImpl implements StaticAssetStorage {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StaticAssetStorageId")
    @GenericGenerator(
        name="StaticAssetStorageId",
        strategy="org.broadleafcommerce.common.persistence.IdOverrideTableGenerator",
        parameters = {
            @Parameter(name="segment_value", value="StaticAssetStorageImpl"),
            @Parameter(name="entity_name", value="org.broadleafcommerce.cms.file.domain.StaticAssetStorageImpl")
        }
    )
    @Column(name = "STATIC_ASSET_STRG_ID")
    protected Long id;

    @Column(name ="STATIC_ASSET_ID", nullable = false)
    @Index(name="STATIC_ASSET_ID_INDEX", columnNames={"STATIC_ASSET_ID"})
    protected Long staticAssetId;

    @Column (name = "FILE_DATA", length = Integer.MAX_VALUE - 1)
    @Lob
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
