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
package org.broadleafcommerce.cms.page.domain;

import org.broadleafcommerce.openadmin.server.domain.Site;
import org.broadleafcommerce.presentation.AdminPresentation;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bpolster.
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "BLC_PAGE_FOLDER")
@Cache(usage= CacheConcurrencyStrategy.NONSTRICT_READ_WRITE, region="blCMSElements")
public class PageFolderImpl implements PageFolder {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "PageFolderId", strategy = GenerationType.TABLE)
    @TableGenerator(name = "PageFolderId", table = "SEQUENCE_GENERATOR", pkColumnName = "ID_NAME", valueColumnName = "ID_VAL", pkColumnValue = "PageFolderImpl", allocationSize = 10)
    @Column(name = "PAGE_FOLDER_ID")
    protected Long id;

    @Column(name = "NAME", nullable=false)
    @AdminPresentation(friendlyName="Item Name", order=1, group="Description", prominent=true)
    protected String name;

    @ManyToOne(targetEntity = PageFolderImpl.class)
    @JoinColumn(name="PARENT_FOLDER_ID")
    protected PageFolder parentFolder;

    /*@ManyToOne(targetEntity = SiteImpl.class)
    @JoinColumn(name="SITE_ID")*/
    @Transient
    protected Site site;

    @OneToMany(mappedBy="parentFolder", cascade = CascadeType.ALL, targetEntity = PageFolderImpl.class)
    protected List<PageFolder> subFolders = new ArrayList<PageFolder>();

    @Column (name = "DELETED_FLAG")
    @AdminPresentation(friendlyName="Deleted", order=2, group="Description", hidden = true)
    protected Boolean deletedFlag = false;

    @Column (name = "IS_FOLDER_FLAG")
    @AdminPresentation(friendlyName="Is Folder", order=3, group="Folder Flag", hidden = true)
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
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public PageFolder getParentFolder() {
        return parentFolder;
    }

    @Override
    public void setParentFolder(PageFolder parentFolder) {
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
    public List<PageFolder> getSubFolders() {
        return subFolders;
    }

    @Override
    public void setSubFolders(List<PageFolder> subFolders) {
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
    public boolean hasChildFolders(){
        for (PageFolder folder : subFolders) {
            if (PageFolderImpl.class.getName().equals(folder.getClass().getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isFolder() {
        return folderFlag;
    }

}

