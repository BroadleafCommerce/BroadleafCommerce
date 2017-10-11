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
package org.broadleafcommerce.cms.file.dao;

import org.broadleafcommerce.cms.file.domain.StaticAssetStorage;
import org.broadleafcommerce.cms.file.domain.StaticAssetStorageImpl;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/8/11
 * Time: 7:31 PM
 * To change this template use File | Settings | File Templates.
 */
@Repository("blStaticAssetStorageDao")
public class StaticAssetStorageDaoImpl implements StaticAssetStorageDao {

    @PersistenceContext(unitName = "blCMSStorage")
    protected EntityManager em;

    @Resource(name="blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    @Override
    public StaticAssetStorage create() {
        return (StaticAssetStorage) entityConfiguration.createEntityInstance("org.broadleafcommerce.cms.file.domain.StaticAssetStorage");
    }

    @Override
    public Blob createBlob(MultipartFile uploadedFile) throws IOException {
        return createBlob(uploadedFile.getInputStream(), uploadedFile.getSize());
    }
    
    @Override
    public Blob createBlob(InputStream uploadedFileInputStream, long fileSize) throws IOException {
        InputStream inputStream = uploadedFileInputStream;
        //We'll work with Blob instances and streams so that the uploaded files are never read into memory
        return em.unwrap(Session.class).getLobHelper().createBlob(inputStream, fileSize);
    }

    @Override
    public StaticAssetStorage readStaticAssetStorageById(Long id) {
        return em.find(StaticAssetStorageImpl.class, id);
    }

    @Override
    public StaticAssetStorage readStaticAssetStorageByStaticAssetId(Long id) {
        Query query = em.createNamedQuery("BC_READ_STATIC_ASSET_STORAGE_BY_STATIC_ASSET_ID");
        query.setParameter("id", id);

        return (StaticAssetStorage) query.getSingleResult();
    }

    @Override
    public StaticAssetStorage save(StaticAssetStorage assetStorage) {
        if (em.contains(assetStorage)) {
            return em.merge(assetStorage);
        }
        em.persist(assetStorage);
        em.flush();
        return assetStorage;
    }

    @Override
    public void delete(StaticAssetStorage assetStorage) {
        if (!em.contains(assetStorage)) {
            assetStorage = readStaticAssetStorageById(assetStorage.getId());
        }
        em.remove(assetStorage);
    }
}
