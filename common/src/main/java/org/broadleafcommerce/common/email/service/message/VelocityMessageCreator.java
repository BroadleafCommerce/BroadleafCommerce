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

package org.broadleafcommerce.common.email.service.message;

import org.apache.velocity.app.VelocityEngine;
import org.broadleafcommerce.common.email.service.info.EmailInfo;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.HashMap;
import java.util.Map;

public class VelocityMessageCreator extends MessageCreator {

    private VelocityEngine velocityEngine;
    private Map<String, Object> additionalConfigItems;
    
    public VelocityMessageCreator(VelocityEngine velocityEngine, JavaMailSender mailSender, Map<String, Object> additionalConfigItems) {
        super(mailSender);
        this.additionalConfigItems = additionalConfigItems;
        this.velocityEngine = velocityEngine;        
    }

    @Override
    public String buildMessageBody(EmailInfo info, Map<String,Object> props) {
        if (props == null) {
            props = new HashMap<String, Object>();
        }

        if (props instanceof HashMap) {
            HashMap<String, Object> hashProps = (HashMap<String, Object>) props;
            @SuppressWarnings("unchecked")
            Map<String,Object> propsCopy = (Map<String, Object>) hashProps.clone();
            if (additionalConfigItems != null) {
                propsCopy.putAll(additionalConfigItems);
            }
            return VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, info.getEmailTemplate(), info.getEncoding(), propsCopy);
        }

        throw new IllegalArgumentException("Property map must be of type HashMap<String, Object>");
    }

    public VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    public Map<String, Object> getAdditionalConfigItems() {
        return additionalConfigItems;
    }

    public void setAdditionalConfigItems(
            Map<String, Object> additionalConfigItems) {
        this.additionalConfigItems = additionalConfigItems;
    }   
}
