/*
 * #%L
 * BroadleafCommerce Open Admin Platform
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
package org.broadleafcommerce.openadmin.web.filter;

import org.broadleafcommerce.common.web.BroadleafTimeZoneResolverImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.util.TimeZone;


/**
 * 
 * @author Phillip Verheyden (phillipuniverse)
 */
@Component("blAdminTimeZoneResolver")
public class BroadleafAdminTimeZoneResolver extends BroadleafTimeZoneResolverImpl {

    @Override
    public TimeZone resolveTimeZone(WebRequest request) {
        //TODO: eventually this should support a using a timezone from the currently logged in Admin user preferences
        return super.resolveTimeZone(request);
    }
}
