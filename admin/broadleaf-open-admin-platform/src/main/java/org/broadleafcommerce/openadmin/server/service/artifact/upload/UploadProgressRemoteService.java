/*
 * Copyright 2008-2012 the original author or authors.
 *
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
 */

package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.openadmin.client.service.UploadProgressService;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;

/**
 * 
 * @author jfischer
 *
 */
@Service("blUploadRemoteService")
public class UploadProgressRemoteService implements UploadProgressService {

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Override
    public Double getPercentUploadComplete(String callbackName, String csrfToken) throws ServiceException, ApplicationSecurityException {
        exploitProtectionService.compareToken(csrfToken);

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        UploadProgressListener progressListener = (UploadProgressListener) attributes.getRequest().getSession().getAttribute(callbackName);

        return progressListener==null?0D:progressListener.getPercentDone();
    }
    
}
