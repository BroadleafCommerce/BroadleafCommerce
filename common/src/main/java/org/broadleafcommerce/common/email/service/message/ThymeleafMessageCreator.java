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

import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.broadleafcommerce.common.web.BroadleafRequestContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Iterator;
import java.util.Map;

public class ThymeleafMessageCreator extends MessageCreator {

    private TemplateEngine templateEngine;
    
    public ThymeleafMessageCreator(TemplateEngine templateEngine, JavaMailSender mailSender) {
        super(mailSender);
        this.templateEngine = templateEngine;        
    }

    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        BroadleafRequestContext blcContext = BroadleafRequestContext.getBroadleafRequestContext();
        
        final Context thymeleafContext = new Context();
        if (blcContext != null && blcContext.getJavaLocale() != null) {
            thymeleafContext.setLocale(blcContext.getJavaLocale());             
        }           
        
        if (props != null) {
            Iterator<String> propsIterator = props.keySet().iterator();
            while(propsIterator.hasNext()) {
                String key = propsIterator.next();
                thymeleafContext.setVariable(key, props.get(key));
            }
        }
        
        return this.templateEngine.process( info.getEmailTemplate(), thymeleafContext); 
    }
}
