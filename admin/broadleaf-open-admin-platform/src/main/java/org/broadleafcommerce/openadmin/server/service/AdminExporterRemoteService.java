/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
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
