/*
 * Copyright 2012 the original author or authors.
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

package org.broadleafcommerce.admin.server.service;

import org.broadleafcommerce.admin.client.dto.AdminExporterDTO;
import org.broadleafcommerce.admin.client.dto.AdminExporterType;
import org.broadleafcommerce.admin.client.service.AdminExporterService;
import org.broadleafcommerce.admin.server.service.export.AdminExporter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Phillip Verheyden
 */
@Service("blAdminExporterRemoteService")
public class AdminExporterRemoteService implements AdminExporterService {

    @Resource(name = "blAdminExporters")
    protected List<AdminExporter> exporters;

    @Override
    public List<AdminExporterDTO> getExporters(AdminExporterType type) {
        List<AdminExporterDTO> result = new ArrayList<AdminExporterDTO>();
        for (AdminExporter exporter : exporters) {
            if (type.equals(exporter.getType())) {
                AdminExporterDTO dto = new AdminExporterDTO();
                dto.setName(exporter.getName());
                dto.setFriendlyName(exporter.getFriendlyName());
                dto.setAdditionalCriteriaProperties(exporter.getCriteriaFields());
                result.add(dto);
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

}
