/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    public List<Store> readAllStores() {
        return storeDao.readAllStores();
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
