/*
 * Copyright 2008-2009 the original author or authors.
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
package org.broadleafcommerce.store.web.model;

import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.profile.domain.Country;
import org.broadleafcommerce.profile.domain.CountryImpl;
import org.broadleafcommerce.profile.domain.State;
import org.broadleafcommerce.profile.domain.StateImpl;
import org.broadleafcommerce.store.domain.Store;


public class FindAStoreForm {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private State state;
    private String postalCode;
    private Country country;
    private String distance;
    private Map<Store, Double> storeDistanceMap;

    public FindAStoreForm() {
        state = new StateImpl();
        country = new CountryImpl();
        storeDistanceMap = new HashMap<Store, Double>();
    }

    public String getAddressLine1() {
        return addressLine1;
    }
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }
    public String getAddressLine2() {
        return addressLine2;
    }
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getPostalCode() {
        return postalCode;
    }
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    public State getState() {
        return state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public Country getCountry() {
        return country;
    }
    public void setCountry(Country country) {
        this.country = country;
    }
    public String getDistance() {
        return distance;
    }
    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Map<Store, Double> getStoreDistanceMap() {
        return storeDistanceMap;
    }

    public void setStoreDistanceMap(Map<Store, Double> storeDistanceMap) {
        this.storeDistanceMap = storeDistanceMap;
    }
}
