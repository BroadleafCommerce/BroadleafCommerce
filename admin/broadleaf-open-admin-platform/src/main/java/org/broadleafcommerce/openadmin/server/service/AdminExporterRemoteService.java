/*
 * Copyright 2008-2013 the original author or authors.
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

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.dto.AdminExporterDTO;
import org.broadleafcommerce.openadmin.server.service.export.AdminExporter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
@Service("blAdminExporterRemoteService")
public class AdminExporterRemoteService implements AdminExporterService, ApplicationContextAware {

    private static final Log LOG = LogFactory.getLog(AdminExporterRemoteService.class);
    
    //Lazy initialization via the blAdminExporters bean definition because exporters are not
    //provided OOB in Broadleaf
    protected List<AdminExporter> exporters;
    
    @Override
    public List<AdminExporterDTO> getExporters(String type) {
        List<AdminExporterDTO> result = new ArrayList<AdminExporterDTO>();
        if (!CollectionUtils.isEmpty(getExporters())) {
            for (AdminExporter exporter : getExporters()) {
                if (type.equals(exporter.getType())) {
                    AdminExporterDTO dto = new AdminExporterDTO();
                    dto.setName(exporter.getName());
                    dto.setFriendlyName(exporter.getFriendlyName());
                    dto.setAdditionalCriteriaProperties(exporter.getCriteriaFields());
                    result.add(dto);
                }
            }
        }
        
        return result;
    }
    
    public List<AdminExporter> getExporters() {
        return exporters;
    }
    
    public void setExporters(List<AdminExporter> exporters) {
        this.exporters = exporters;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (exporters == null) {
            try {
                setExporters((List<AdminExporter>)applicationContext.getBean("blAdminExporters"));
            } catch (NoSuchBeanDefinitionException e) {
                LOG.debug("blAdminExporters could not be found in your application context");
            } catch (BeansException e) {
                LOG.debug("blAdminExporters could not be obtained");
            }
        }
    }

}
