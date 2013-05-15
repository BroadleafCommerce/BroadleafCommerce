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

package org.broadleafcommerce.common;

import java.util.Map;

/**
 * Created by bpolster.
 */
public interface RequestDTO {

    /**
     * @return  returns the request not including the protocol, domain, or query string
     */
    public String getRequestURI();

    /**
     * @return Returns the URL and parameters.
     */
    public String getFullUrLWithQueryString();

    /**
     * @return true if this request came in through HTTPS
     */
    public Boolean isSecure();

    public Map<String, String> getRequestDTOAttributes();

    public void setRequestDTOAttributes(Map<String, String> requestDTOAttributes);
}
