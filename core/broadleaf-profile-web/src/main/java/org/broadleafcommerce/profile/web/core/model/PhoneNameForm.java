/*
 * #%L
 * BroadleafCommerce Profile Web
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
package org.broadleafcommerce.profile.web.core.model;

import org.broadleafcommerce.profile.core.domain.Phone;

/**
 * The Form Backing Bean used by the CustomerPhoneController.  This design was chosen instead
 * of placing multiple values on the request.  This is a smaller scenario since there are not many
 * properties for the CustomerPhoneController, but to be consistent, we did not put a large amount of
 * unnecessary parameters on the request.
 * 
 * @author sconlon
 *
 */
public class PhoneNameForm {
    private Phone phone;
    private String phoneName;

    public Phone getPhone() {
        return phone;
    }

    public String getPhoneName() {
        return phoneName;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public void setPhoneName(String phoneName) {
        this.phoneName = phoneName;
    }
}
