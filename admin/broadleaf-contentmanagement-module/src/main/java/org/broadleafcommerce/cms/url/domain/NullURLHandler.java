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

/**
 * 
 */
package org.broadleafcommerce.cms.url.domain;

import org.broadleafcommerce.cms.url.type.URLRedirectType;


/**
 * A Null instance of a URLHandler.   Used by the default URLHandlerServiceImpl implementation to 
 * cache misses (e.g. urls  that are not being handled by forwards and redirects.
 * 
 * @author bpolster
 */
public class NullURLHandler implements URLHandler,java.io.Serializable {
     private static final long serialVersionUID = 1L;

    @Override
    public Long getId() {
        return null;
    }

    @Override
    public void setId(Long id) {
    }

    @Override
    public String getIncomingURL() {
        return null;
    }

    @Override
    public void setIncomingURL(String incomingURL) {
    }

    @Override
    public String getNewURL() {
        return null;
    }

    @Override
    public void setNewURL(String newURL) {
    }

    @Override
    public URLRedirectType getUrlRedirectType() {
        return null;
    }

    @Override
    public void setUrlRedirectType(URLRedirectType redirectType) {
    }

     
}
