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

package org.broadleafcommerce.openadmin.server.service;

import com.gwtincubator.security.exception.ApplicationSecurityException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.client.service.UtilityService;
import org.broadleafcommerce.openadmin.server.dao.DynamicEntityDao;
import org.broadleafcommerce.openadmin.server.security.domain.AdminUser;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author jfischer
 */
@Service("blUtilityRemoteService")
public class UtilityRemoteService implements ApplicationContextAware, UtilityService {

    private static final Log LOG = LogFactory.getLog(UtilityRemoteService.class);

    private ApplicationContext applicationContext;
    protected String storeFrontWebAppPrefix;
    protected String assetServerUrlPrefix;

    @Resource(name="blExploitProtectionService")
    protected ExploitProtectionService exploitProtectionService;

    @Resource(name="blDynamicEntityDao")
    protected DynamicEntityDao dynamicEntityDao;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public String getWebAppContext() throws ServiceException {
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            return request.getContextPath();
        } catch (Exception e) {
            LOG.error("problem performing operation", e);
            throw new ServiceException("problem performing operation", e);
        }
    }

    @Override
    public String getStoreFrontWebAppPrefix() throws ServiceException {
        return storeFrontWebAppPrefix;
    }

    public void setStoreFrontWebAppPrefix(String storeFrontWebAppPrefix) {
        this.storeFrontWebAppPrefix = storeFrontWebAppPrefix;
    }

    @Override
    public String getAssetServerUrlPrefix() throws ServiceException {
        return assetServerUrlPrefix;
    }

    public void setAssetServerUrlPrefix(String assetServerUrlPrefix) {
        this.assetServerUrlPrefix = assetServerUrlPrefix;
    }

    @Override
    public String[] getAllItems() throws ServiceException, ApplicationSecurityException {
        return new String[] {getWebAppContext(), storeFrontWebAppPrefix, assetServerUrlPrefix, exploitProtectionService.getCSRFToken()};
    }

    @Override
    public Boolean getWorkflowEnabled(String[] qualifiers) throws ServiceException, ApplicationSecurityException {
        return false;
    }

    @Override
    public void initializeEJB3Configuration() throws ServiceException, ApplicationSecurityException {
        dynamicEntityDao.getPersistentClass(AdminUser.class.getName());
    }

}
