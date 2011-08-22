/*
 * Copyright 2008-2011 the original author or authors.
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
package org.broadleafcommerce.cms.file.domain;

import org.broadleafcommerce.cms.message.domain.ContentMessageValue;
import org.broadleafcommerce.cms.message.domain.ContentMessageValueImpl;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")

public class StaticAssetImpl implements StaticAsset {

    @Id
    @GeneratedValue(generator = "StaticAssetId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StaticAssetId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StaticAssetImpl", allocationSize = 10)
    @Column(name = "STATIC_ASSET_ID")
    protected Long id;

    @Column(name = "FILE_NAME")
    protected String fileName;

    @Column(name ="FULL_URL")
    protected String fullUrl;

    @Column(name = "FILE_SIZE")
    protected Integer fileSize;

    @ManyToOne(targetEntity = StaticAssetFolderImpl.class)
    @JoinColumn(name = "PARENT_FOLDER_ID")
    protected StaticAssetFolder parentFolder;

    @OneToOne(targetEntity = StaticAssetDataImpl.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATIC_ASSET_DATA_ID")
    protected StaticAssetData staticAssetData;

    @OneToMany(mappedBy = "staticAsset", targetEntity = StaticAssetDescriptionImpl.class, cascade = {CascadeType.ALL})
    @MapKey(name = "languageCode")
    @Cascade(value={org.hibernate.annotations.CascadeType.ALL, org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="blCMSElements")
    protected Map<String,StaticAssetDescription> contentMessageValues = new HashMap<String,StaticAssetDescription>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFullUrl() {
        return fullUrl;
    }

    public void setFullUrl(String fullUrl) {
        this.fullUrl = fullUrl;
    }

    public Integer getFileSize() {
        return fileSize;
    }

    public void setFileSize(Integer fileSize) {
        this.fileSize = fileSize;
    }

    public StaticAssetFolder getParentFolder() {
        return parentFolder;
    }

    public void setParentFolder(StaticAssetFolder parentFolder) {
        this.parentFolder = parentFolder;
    }

    public StaticAssetData getStaticAssetData() {
        return staticAssetData;
    }

    public void setStaticAssetData(StaticAssetData staticAssetData) {
        this.staticAssetData = staticAssetData;
    }

    public Map<String, StaticAssetDescription> getContentMessageValues() {
        return contentMessageValues;
    }

    public void setContentMessageValues(Map<String, StaticAssetDescription> contentMessageValues) {
        this.contentMessageValues = contentMessageValues;
    }
}
