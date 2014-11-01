/*
 * #%L
 * BroadleafCommerce Integration
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
package org.broadleafcommerce.profile.web.core.service;

import org.broadleafcommerce.profile.core.domain.Phone;
import org.broadleafcommerce.profile.core.service.PhoneService;
import org.broadleafcommerce.profile.dataprovider.PhoneDataProvider;
import org.broadleafcommerce.test.BaseTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

public class PhoneTest extends BaseTest {

    @Resource
    private PhoneService phoneService;

    List<Long> phoneIds = new ArrayList<Long>();
    String userName = new String();

    private Long phoneId;

    @Test(groups = { "createPhone" }, dataProvider = "setupPhone", dataProviderClass = PhoneDataProvider.class, dependsOnGroups = { "readCustomer" })
    @Transactional
    @Rollback(false)
    public void createPhone(Phone phone) {
        userName = "customer1";
        assert phone.getId() == null;
        phone = phoneService.savePhone(phone);
        assert phone.getId() != null;
        phoneId = phone.getId();
    }

    @Test(groups = { "readPhoneById" }, dependsOnGroups = { "createPhone" })
    public void readPhoneById() {
        Phone phone = phoneService.readPhoneById(phoneId);
        assert phone != null;
        assert phone.getId() == phoneId;
    }
}
