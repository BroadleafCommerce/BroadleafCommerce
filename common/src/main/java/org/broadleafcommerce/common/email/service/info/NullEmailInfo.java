/*
 * #%L
 * BroadleafCommerce Common Libraries
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
package org.broadleafcommerce.common.email.service.info;

import java.io.IOException;

/**
 * Implementation of EmailInfo that will not send an Email.   The out of box configuration for
 * broadleaf does not send emails but does have hooks to send emails for use cases like
 * registration, forgot password, etc.
 *
 * The email send functionality will not send an email if the passed in EmailInfo is an instance
 * of this class.
 *
 * @author vjain
 *
 */
public class NullEmailInfo extends EmailInfo {
    private static final long serialVersionUID = 1L;

    public NullEmailInfo() throws IOException {
        super();
    }

}
