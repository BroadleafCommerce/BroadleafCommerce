/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
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
