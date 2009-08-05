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
package org.broadleafcommerce.email.service;

import java.util.HashMap;

import org.broadleafcommerce.email.domain.EmailTarget;
import org.broadleafcommerce.email.service.info.EmailInfo;

/**
 * @author jfischer
 *
 */
public interface EmailService {

    public boolean sendTemplateEmail(String emailAddress, EmailInfo emailInfo,  HashMap<String,Object> props);

    public boolean sendTemplateEmail(EmailTarget emailTarget, EmailInfo emailInfo, HashMap<String,Object> props);

    public boolean sendBasicEmail(EmailInfo emailInfo, EmailTarget emailTarget, HashMap<String,Object> props);

}
