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
package org.broadleafcommerce.openadmin.web.controller;

import org.broadleafcommerce.openadmin.server.service.export.AdminExporter;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Phillip Verheyden
 */
public class AdminExportController extends AdminAbstractController {
    
    @Resource(name = "blAdminExporters")
    protected List<AdminExporter> exporters;

    public ModelAndView export(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) throws IOException {
        String exporterName = params.get("exporter");
        AdminExporter exporter = null;
        for (AdminExporter test : exporters) {
            if (test.getName().equals(exporterName)) {
                exporter = test;
            }
        }
        if (exporter == null) {
            throw new RuntimeException("Could not find exporter with name: " + exporterName);
        }
        
        response.setContentType("application/download");
        String fileName = exporter.getFileName();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        
        ServletOutputStream stream = response.getOutputStream();
        exporter.writeExport(stream, params);
        stream.flush();
        
        return null;
    }

    public List<AdminExporter> getExporters() {
        return exporters;
    }
    
    public void setExporters(List<AdminExporter> exporters) {
        this.exporters = exporters;
    }

}
