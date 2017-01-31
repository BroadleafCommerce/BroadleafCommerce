/*
 * #%L
 * BroadleafCommerce Export Module
 * %%
 * Copyright (C) 2009 - 2017 Broadleaf Commerce
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
package com.broadleafcommerce.export.admin.web.controller;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.admin.condition.ConditionalOnAdmin;
import org.broadleafcommerce.common.file.service.BroadleafFileService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.broadleafcommerce.export.domain.ExportInfo;
import com.broadleafcommerce.export.service.ExportInfoService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ConditionalOnAdmin
@Component("blAdminExportDownloadController")
@RequestMapping("/export-info")
public class AdminExportDownloadController {

    private static final Log LOG = LogFactory.getLog(AdminExportDownloadController.class);
    
    @Resource(name = "blExportInfoService")
    protected ExportInfoService exportInfoService;
    
    @Resource(name = "blFileService")
    protected BroadleafFileService fileService;
    
    /**
     * Method created to override the admin's default "view details" page and instead have the export
     * file this record represents downloaded as an attachment to the user's browser
     * 
     * @param response
     * @param request
     * @param id
     * @throws IOException
     */
    @RequestMapping("/{id}")
    public void downloadExportFile(HttpServletResponse response,
                                   HttpServletRequest request,
                                   @PathVariable(value = "id") Long id) throws IOException {
        ServletOutputStream stream = response.getOutputStream();
        ExportInfo info = exportInfoService.findExportInfoById(id);
        if (info != null) {
            File f = fileService.getResource(info.getResourcePath());
            InputStream fileStream = new FileInputStream(f);
            byte[] bytes = IOUtils.toByteArray(fileStream);
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + info.getFriendlyResourcePath()); 
            stream.write(bytes);
        } else {
            // This would not be expected to ever happen but in the event it does then the user will just be redirected
            // to the page they came from
            response.sendRedirect(request.getHeader("referer"));
            LOG.error("Could not find an export with the given id " + id);
        }
        stream.close();
        stream.flush();
    }
}
