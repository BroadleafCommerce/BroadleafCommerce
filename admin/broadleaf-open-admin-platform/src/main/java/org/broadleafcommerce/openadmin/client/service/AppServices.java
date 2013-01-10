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

package org.broadleafcommerce.openadmin.client.service;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

/**
 * 
 * @author jfischer
 *
 */
public class AppServices {

    public static final UploadProgressServiceAsync UPLOAD = GWT.create(UploadProgressService.class);
    public static final AdminSecurityServiceAsync SECURITY = GWT.create(AdminSecurityService.class);
    public static final DynamicEntityServiceAsync DYNAMIC_ENTITY = GWT.create(DynamicEntityService.class);
    public static final UtilityServiceAsync UTILITY = GWT.create(UtilityService.class);
    static {
        ServiceDefTarget endpoint = (ServiceDefTarget) DYNAMIC_ENTITY;
        endpoint.setServiceEntryPoint("dynamic.entity.service");
        
        ServiceDefTarget endpoint2 = (ServiceDefTarget) SECURITY;
        endpoint2.setServiceEntryPoint("security.service");

        ServiceDefTarget endpoint3 = (ServiceDefTarget) UPLOAD;
        endpoint3.setServiceEntryPoint("upload.progress.service");

        ServiceDefTarget endpoint4 = (ServiceDefTarget) UTILITY;
        endpoint4.setServiceEntryPoint("utility.service");
    }
    
}
