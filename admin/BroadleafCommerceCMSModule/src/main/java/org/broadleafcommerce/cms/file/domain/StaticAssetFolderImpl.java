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

import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.openadmin.server.domain.SiteImpl;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_STATIC_ASSET_FOLDER")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class StaticAssetFolderImpl implements StaticAssetFolder {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "StaticAssetId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "StaticAssetId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "StaticAssetFolderImpl", allocationSize = 10)
    @Column(name = "STATIC_ASSET_ID")
    protected Long id;

    @Column (name = "NAME", nullable = false)
    @AdminPresentation(friendlyName="Item Name", order=1, group = "Asset Details")
    protected String name;

    @ManyToOne(targetEntity = StaticAssetFolderImpl.class)
    @JoinColumn(name="PARENT_FOLDER_ID")
    protected StaticAssetFolder parentFolder;

    /*@ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")*/
    @Transient
    protected Site site;

    @OneToMany(mappedBy="parentFolder", cascade = CascadeType.ALL, targetEntity = StaticAssetFolderImpl.class)
    protected List<StaticAssetFolder> subFolders;

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName="Deleted Flag", hidden = true)
    protected Boolean deletedFlag = false;

    @Column (name = "IS_FOLDER_FLAG")
    @AdminPresentation(friendlyName="Is Folder", hidden = true)
    protected Boolean folderFlag = true;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public StaticAssetFolder getParentFolder() {
        return parentFolder;
    }

    @Override
    public void setParentFolder(StaticAssetFolder parentFolder) {
        this.parentFolder = parentFolder;
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public List<StaticAssetFolder> getSubFolders() {
        return subFolders;
    }

    @Override
    public void setSubFolders(List<StaticAssetFolder> subFolders) {
        this.subFolders = subFolders;
    }

    @Override
    public Boolean getDeletedFlag() {
        return deletedFlag;
    }

    @Override
    public void setDeletedFlag(Boolean deletedFlag) {
        this.deletedFlag = deletedFlag;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean getFolderFlag() {
        return folderFlag;
    }

    @Override
    public void setFolderFlag(Boolean folderFlag) {
        this.folderFlag = folderFlag;
    }
}

