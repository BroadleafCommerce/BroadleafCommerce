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
package com.broadleafcommerce.export.event.consumer;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.file.domain.FileWorkArea;
import org.broadleafcommerce.common.file.service.BroadleafFileService;

import com.broadleafcommerce.export.domain.ExportInfo;
import com.broadleafcommerce.export.domain.type.SupportedExportEncoding;
import com.broadleafcommerce.export.domain.type.SupportedExportType;
import com.broadleafcommerce.export.event.factory.AbstractExportEventFactory;
import com.broadleafcommerce.export.service.ExportInfoService;
import com.broadleafcommerce.jobsevents.domain.SystemEvent;
import com.broadleafcommerce.jobsevents.domain.SystemEventDetail;
import com.broadleafcommerce.jobsevents.service.AbstractSystemEventConsumer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.annotation.Resource;

public abstract class AbstractExportEventConsumer extends AbstractSystemEventConsumer {

    protected abstract void export(SystemEvent event, OutputStream output) throws IOException;
    
    public abstract String getExportFileName();
    
    public abstract String getEntityType();
    
    public abstract String getExportFriendlyName();
    
    private static Log LOG = LogFactory.getLog(AbstractExportEventConsumer.class);
    
    @Resource(name = "blFileService")
    protected BroadleafFileService fileService;
    
    @Resource(name = "blExportInfoService")
    protected ExportInfoService exportInfoService;
    
    @Override
    public void consumeEvent(SystemEvent event) {
        Date currentDate = new Date();
        FileWorkArea workArea = fileService.initializeWorkArea();
        File file = new File(FilenameUtils.concat(workArea.getFilePathLocation(), getExportFileName() + "-" + currentDate.getTime()));
        FileOutputStream output = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            export(event, output);
            
            // We want to get the file size now because after it's moved via the fileService it won't exist anymore
            // Additionally we don't want to create the ExportInfo record before the file is moved in the case that
            // the call to the file service is unsuccessful and returns an error
            Long fileSize = file.length();
            String fileLocation = fileService.addOrUpdateResourceForPath(workArea, file, true);
            createExportInfo(fileSize, currentDate, fileLocation, getShareable(event), getAdminUserId(event));
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } finally {
            fileService.closeWorkArea(workArea);
            IOUtils.closeQuietly(output);
        }
    }
    
    protected void createExportInfo(Long fileSize, Date date, String fileLocation, Boolean shareable, Long adminUserId) {
        ExportInfo info = exportInfoService.create();
        info.setAdminUserId(adminUserId);
        info.setDateCreated(date);
        info.setEntityType(getEntityType());
        info.setName(getExportFriendlyName());
        info.setResourcePath(fileLocation);
        info.setFriendlyResourcePath(getExportFileName());
        info.setShared(shareable);
        info.setSize(fileSize);
        
        exportInfoService.save(info);
    }
    
    protected SupportedExportEncoding getEncoding(SystemEvent event) {
        SystemEventDetail encodingDetail = event.getEventDetails().get(AbstractExportEventFactory.ENCODING);
        if (encodingDetail != null && SupportedExportEncoding.getInstance(encodingDetail.getValue()) != null) {
             return SupportedExportEncoding.getInstance(encodingDetail.getValue());
        }
        return SupportedExportEncoding.UTF8;
    }
    
    protected SupportedExportType getFormat(SystemEvent event) {
        SystemEventDetail formatDetail = event.getEventDetails().get(AbstractExportEventFactory.FORMAT);
        if (formatDetail != null && SupportedExportEncoding.getInstance(formatDetail.getValue()) != null) {
             return SupportedExportType.getInstance(formatDetail.getValue());
        }
        return SupportedExportType.CSV;
    }
    
    protected boolean getShareable(SystemEvent event) {
        SystemEventDetail shareableDetail = event.getEventDetails().get(AbstractExportEventFactory.SHAREABLE);
        if (shareableDetail != null && shareableDetail.getValue() != null) {
            return Boolean.parseBoolean(shareableDetail.getValue());
        }
        return false;
    }
    
    protected Long getAdminUserId(SystemEvent event) {
        SystemEventDetail adminUserDetail = event.getEventDetails().get(AbstractExportEventFactory.ADMIN_USER);
        if (adminUserDetail != null) {
            try {
                return Long.parseLong(adminUserDetail.getValue());
            } catch (NumberFormatException e) {}
        }
        return null;
    }
}
