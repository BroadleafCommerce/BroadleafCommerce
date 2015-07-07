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
package org.broadleafcommerce.common.email.service.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

public class NullMessageCreator extends MessageCreator {
    
    private static final Log LOG = LogFactory.getLog(NullMessageCreator.class);
    
    public NullMessageCreator(JavaMailSender mailSender) {
        super(mailSender);  
    }
    
    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        return info.getEmailTemplate();
    }
    
    @Override
    public void sendMessage(final Map<String,Object> props) throws MailException {
        LOG.warn("NullMessageCreator is defined -- specify a real message creator to send emails");
    }
    
}
