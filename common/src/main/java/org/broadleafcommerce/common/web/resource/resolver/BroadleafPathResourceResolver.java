/*
 * #%L
 * broadleaf-theme
 * %%
 * Copyright (C) 2009 - 2015 Broadleaf Commerce
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
package org.broadleafcommerce.common.web.resource.resolver;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Wraps Spring's {@link PathResourceResolver} for ordering purposes.
 *  
 * @author Brian Polster
 * @since Broadleaf 4.0
 */
@Component("blPathResourceResolver")
public class BroadleafPathResourceResolver extends PathResourceResolver implements Ordered {

    protected static final Log LOG = LogFactory.getLog(BroadleafPathResourceResolver.class);

    private int order = BroadleafResourceResolverOrder.BLC_PATH_RESOURCE_RESOLVER;

    @Override
    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


}
