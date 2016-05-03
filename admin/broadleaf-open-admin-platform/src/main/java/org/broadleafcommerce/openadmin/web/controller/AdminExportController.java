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
