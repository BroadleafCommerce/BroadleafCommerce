package org.broadleafcommerce.core.inventory.service;

import org.broadleafcommerce.core.inventory.domain.FulfillmentLocation;

import java.util.List;

/**
 * Copyright 2012 the original author or authors.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public interface FulfillmentLocationService {

    /**
     * Retrieves all of the fulfillment locations
     * @return {@link List} of {@link FulfillmentLocation}
     */
    public List<FulfillmentLocation> readAll();

    /**
     * Retrieves a {@link FulfillmentLocation} from the given id
     * @param fulfillmentLocationId
     * @return {@link FulfillmentLocation}
     */
    public FulfillmentLocation readById(Long fulfillmentLocationId);

    /**
     * Persists the {@link FulfillmentLocation}
     * @param fulfillmentLocation
     * @return the persisted {@link FulfillmentLocation}
     */
    public FulfillmentLocation save(FulfillmentLocation fulfillmentLocation);

    /**
     * Deletes the passed {@link FulfillmentLocation}
     * @param fulfillmentLocation
     */
    public void delete(FulfillmentLocation fulfillmentLocation);

    /**
     * Sets the defaultLocation flag to false for all fulfillmentLocations
     */
    public void updateOtherDefaultLocationToFalse(FulfillmentLocation fulfillmentLocation);

}
