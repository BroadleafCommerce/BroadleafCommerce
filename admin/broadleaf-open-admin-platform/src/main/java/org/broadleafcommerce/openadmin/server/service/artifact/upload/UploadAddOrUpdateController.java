/*
 * Copyright 2008-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.service.artifact.upload;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.common.security.service.ExploitProtectionService;
import org.broadleafcommerce.openadmin.client.dto.*;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jfischer
 */
public class UploadAddOrUpdateController extends SimpleFormController {

    private static final Log LOG = LogFactory.getLog(UploadAddOrUpdateController.class);

    protected DynamicEntityService dynamicEntityRemoteService;
    protected ExploitProtectionService exploitProtectionService;

    protected Long maximumFileSizeInBytes = 20L * 1000L * 1000L; // 20mb max by default

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> model = new HashMap<String, String>();
        String callbackName = null;
        try {
            MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
            if (request instanceof MultipartRequest) {
                MultipartRequest multipartRequest = (MultipartRequest) request;
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            }

            //check for XSRF
            String csrfToken = (String) mpvs.getPropertyValue("csrfToken").getValue();
            exploitProtectionService.compareToken(csrfToken);

            PersistencePackage persistencePackage = new PersistencePackage();
            persistencePackage.setPersistencePerspective(new PersistencePerspective());
            persistencePackage.setCsrfToken(csrfToken);
            String ceilingEntity = (String) mpvs.getPropertyValue("ceilingEntityFullyQualifiedClassname").getValue();
            callbackName = (String) mpvs.getPropertyValue("callbackName").getValue();
            String operation = (String) mpvs.getPropertyValue("operation").getValue();
            String customCriteria = (String) mpvs.getPropertyValue("customCriteria").getValue();
            mpvs.removePropertyValue("ceilingEntityFullyQualifiedClassname");
            mpvs.removePropertyValue("sandbox");
            mpvs.removePropertyValue("callbackName");
            mpvs.removePropertyValue("operation");
            mpvs.removePropertyValue("customCriteria");
            persistencePackage.setCeilingEntityFullyQualifiedClassname(ceilingEntity);
            persistencePackage.setCustomCriteria(new String[]{customCriteria});
            Entity entity = new Entity();
            persistencePackage.setEntity(entity);
            entity.setType(new String[]{ceilingEntity});
            List<Property> propertyList = new ArrayList<Property>();
            for (PropertyValue propertyValue : mpvs.getPropertyValues()) {
                if (propertyValue.getValue() instanceof MultipartFile) {
                    MultipartFile file = (MultipartFile) propertyValue.getValue();
                    if (file.getSize() > maximumFileSizeInBytes) {
                        throw new MaxUploadSizeExceededException(maximumFileSizeInBytes);
                    }
                    if (file.getOriginalFilename() == null || file.getOriginalFilename().indexOf(".") < 0) {
                        throw new FileExtensionUnavailableException("Unable to determine file extension for uploaded file. The filename for the uploaded file is: " + file.getOriginalFilename());
                    }
                    Map<String, MultipartFile> fileMap = UploadedFile.getUpload();
                    fileMap.put(propertyValue.getName(), (MultipartFile) propertyValue.getValue());
                    UploadedFile.setUpload(fileMap);
                    entity.setMultiPartAvailableOnThread(true);
                } else {
                    Property property = new Property();
                    property.setName(propertyValue.getName());
                    property.setValue((String) propertyValue.getValue());
                    propertyList.add(property);
                }
            }
            entity.setProperties(propertyList.toArray(new Property[]{}));

            Entity result = null;

            if (operation.equals("add")) {
                result = dynamicEntityRemoteService.add(persistencePackage);
            } else if (operation.equals("update")) {
                result = dynamicEntityRemoteService.update(persistencePackage);
            }

            model.put("callbackName", callbackName);
            model.put("result", buildJSON(result));

            return new ModelAndView("blUploadCompletedView", model);
        } catch (MaxUploadSizeExceededException e) {
            if (callbackName != null) {
                model.put("callbackName", callbackName);
                model.put("error", buildErrorJSON(e.getMessage()));
            }

            return new ModelAndView("blUploadCompletedView", model);
        } catch (FileExtensionUnavailableException e) {
            if (callbackName != null) {
                model.put("callbackName", callbackName);
                model.put("error", buildErrorJSON(e.getMessage()));
            }

            return new ModelAndView("blUploadCompletedView", model);
        } catch (Exception e) {
            e.printStackTrace();
            if (callbackName != null) {
                model.put("callbackName", callbackName);
                model.put("error", buildErrorJSON(e.getMessage()));
            }

            return new ModelAndView("blUploadCompletedView", model);
        } finally {
            UploadedFile.remove();
        }
    }

    protected String buildJSON(Entity entity) throws ServiceException {
        StringBuilder sb = new StringBuilder(200);
        sb.append("{\"type\" : \"");
        sb.append(exploitProtectionService.cleanString(entity.getType()[0]));
        sb.append("\", \"properties\" : [");
        for (int j=0;j<entity.getProperties().length;j++) {
            sb.append("{\"name\" : \"");
            sb.append(exploitProtectionService.cleanString(entity.getProperties()[j].getName()));
            sb.append("\", \"value\" : \"");
            sb.append(exploitProtectionService.cleanString(entity.getProperties()[j].getValue()));
            sb.append("\"}");
            if (j<entity.getProperties().length - 1) {
                sb.append(',');
            }
         }
        sb.append("]}");

        return sb.toString();
    }

    protected String buildErrorJSON(String errorString) throws ServiceException {
        StringBuilder sb = new StringBuilder(50);
        sb.append("{\"error\" : \"");
        sb.append(exploitProtectionService.cleanString(errorString));
        sb.append("\"}");

        return sb.toString();
    }

    protected void bindMultipart(Map<String, List<MultipartFile>> multipartFiles, MutablePropertyValues mpvs) {
        for (Map.Entry<String, List<MultipartFile>> entry : multipartFiles.entrySet()) {
            String key = entry.getKey();
            List<MultipartFile> values = entry.getValue();
            if (values.size() == 1) {
                MultipartFile value = values.get(0);
                mpvs.add(key, value);
            } else {
                mpvs.add(key, values);
            }
        }
    }

    public DynamicEntityService getDynamicEntityRemoteService() {
        return dynamicEntityRemoteService;
    }

    public void setDynamicEntityRemoteService(DynamicEntityService dynamicEntityRemoteService) {
        this.dynamicEntityRemoteService = dynamicEntityRemoteService;
    }

    public Long getMaximumFileSizeInBytes() {
        return maximumFileSizeInBytes;
    }

    public void setMaximumFileSizeInBytes(Long maximumFileSizeInBytes) {
        this.maximumFileSizeInBytes = maximumFileSizeInBytes;
    }

    public ExploitProtectionService getExploitProtectionService() {
        return exploitProtectionService;
    }

    public void setExploitProtectionService(ExploitProtectionService exploitProtectionService) {
        this.exploitProtectionService = exploitProtectionService;
    }
}
