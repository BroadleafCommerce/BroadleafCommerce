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

package org.broadleafcommerce.vendor.usps.service.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class USPSCommitment {

    private Date commitmentDateAndTime;
    private List<USPSLocation> locations = new ArrayList<USPSLocation>();

    public List<USPSLocation> getLocations() {
        return locations;
    }

    public void setLocations(List<USPSLocation> locations) {
        this.locations = locations;
    }

    public Date getCommitmentDateAndTime() {
        return commitmentDateAndTime;
    }

    public void setCommitmentDateAndTime(Date commitmentDateAndTime) {
        this.commitmentDateAndTime = commitmentDateAndTime;
    }

}
