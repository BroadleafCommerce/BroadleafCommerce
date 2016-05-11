/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.core.store.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.core.store.dao.StoreDao;
import org.broadleafcommerce.core.store.domain.Store;
import org.broadleafcommerce.core.store.domain.ZipCode;
import org.broadleafcommerce.profile.core.domain.Address;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("blStoreService")
public class StoreServiceImpl implements StoreService {

    // private final static int MAXIMUM_DISTANCE = Integer.valueOf(25);
    @Resource(name = "blStoreDao")
    private StoreDao storeDao;

    @Resource(name = "blZipCodeService")
    private ZipCodeService zipCodeService;

    public Store readStoreById(Long id) {
        return storeDao.readStoreById(id);
    }

    public Store readStoreByStoreName(String storeName) {
        return storeDao.readStoreByStoreName(storeName);
    }

    public Store readStoreByStoreCode(String storeCode) {
        return storeDao.readStoreByStoreCode(storeCode);
    }

    public List<Store> readAllStores() {
        return storeDao.readAllStores();
    }

    public List<Store> readAllStoresByState(String state) {
        return storeDao.readAllStoresByState(state);
    }

    @Override
    @Transactional("blTransactionManager")
    public Store saveStore(Store store) {
        return storeDao.save(store);
    }

    public Map<Store, Double> findStoresByAddress(Address searchAddress, double distance) {
        Map<Store, Double> matchingStores = new HashMap<Store, Double>();
        for (Store store : readAllStores()) {
            Double storeDistance = findStoreDistance(store, Integer.parseInt(searchAddress.getPostalCode()));
            if (storeDistance != null && storeDistance <= distance) {
                matchingStores.put(store, storeDistance);
            }
        }

        return matchingStores;
    }

    private Double findStoreDistance(Store store, Integer zip) {
        ZipCode zipCode = zipCodeService.findZipCodeByZipCode(zip);
        if (zipCode == null) {
            return null;
        }
        // A constant used to convert from degrees to radians.
        double degreesToRadians = 57.3;
        double storeDistance = 3959 * Math.acos((Math.sin(zipCode.getZipLatitude() / degreesToRadians) * Math.sin(store.getLatitude() / degreesToRadians))
                + (Math.cos(zipCode.getZipLatitude() / degreesToRadians) * Math.cos(store.getLatitude() / degreesToRadians) * Math.cos((store.getLongitude() / degreesToRadians) - (zipCode.getZipLongitude() / degreesToRadians))));
        return storeDistance;
    }
}
