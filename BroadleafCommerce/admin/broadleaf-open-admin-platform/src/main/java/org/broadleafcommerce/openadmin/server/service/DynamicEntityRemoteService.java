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

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.openadmin.client.dto.DynamicResultSet;
import org.broadleafcommerce.openadmin.client.dto.Entity;
import org.broadleafcommerce.openadmin.client.dto.PersistencePackage;
import org.broadleafcommerce.openadmin.client.dto.Property;
import org.broadleafcommerce.openadmin.client.service.DynamicEntityService;
import org.broadleafcommerce.openadmin.client.service.ServiceException;
import org.broadleafcommerce.openadmin.server.service.persistence.PersistenceManager;
import org.broadleafcommerce.openadmin.server.service.persistence.TargetModeType;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author jfischer
 */
@Service("blDynamicEntityRemoteService")
public class DynamicEntityRemoteService implements DynamicEntityService, ApplicationContextAware {

    private static class Handler extends URLStreamHandler {
        /** The classloader to find resources from. */
        private final ClassLoader classLoader;

        public Handler() {
            classLoader = getClass().getClassLoader();
        }

        public Handler(ClassLoader classLoader) {
            this.classLoader = classLoader;
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException {
            URL resourceUrl = classLoader.getResource(u.getPath());
            return resourceUrl.openConnection();
        }
    }

    private static Policy getAntiSamyPolicy(String policyFileLocation) {
        try {
            URL url = new URL(null, policyFileLocation, new Handler(ClassLoader.getSystemClassLoader()));
            return Policy.getInstance(url);
        } catch (Exception e) {
            throw new RuntimeException("Unable to create URL", e);
        }
    }

    public static final String DEFAULTPERSISTENCEMANAGERREF = "blPersistenceManager";
    private static final Log LOG = LogFactory.getLog(DynamicEntityRemoteService.class);
    private static final String DEFAULTANTISAMYPOLICYFILELOCATION = "classpath:antisamy-tinymce-1.4.4.xml";

    protected String persistenceManagerRef = DEFAULTPERSISTENCEMANAGERREF;
    private ApplicationContext applicationContext;
    protected String antiSamyPolicyFileLocation = DEFAULTANTISAMYPOLICYFILELOCATION;
    //this is thread safe
    private Policy antiSamyPolicy = getAntiSamyPolicy(antiSamyPolicyFileLocation);
    //this is thread safe for the usage of scan()
    private AntiSamy as = new AntiSamy();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DynamicResultSet inspect(PersistencePackage persistencePackage) throws ServiceException {
        String ceilingEntityFullyQualifiedClassname = persistencePackage.getCeilingEntityFullyQualifiedClassname();
        try {
            PersistenceManager persistenceManager = null;
            try {
                persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
                persistenceManager.setTargetMode(TargetModeType.SANDBOX);
                return persistenceManager.inspect(persistencePackage);
            } finally {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            LOG.error("Problem fetching results for " + ceilingEntityFullyQualifiedClassname, e);
            throw new ServiceException("Unable to fetch results for " + ceilingEntityFullyQualifiedClassname, e);
        }
    }

    public DynamicResultSet fetch(PersistencePackage persistencePackage, CriteriaTransferObject cto) throws ServiceException {
        PersistenceManager persistenceManager = null;
        try {
            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.fetch(persistencePackage, cto);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                LOG.error("Unable to close persistence manager", e);
            }
        }
    }

    protected void cleanEntity(Entity entity) throws ServiceException {
        try {
            for (Property property : entity.getProperties()) {
                if (property.getValue() != null) {
                    CleanResults results = as.scan(property.getValue(), antiSamyPolicy);
                    property.setValue(results.getCleanHTML());
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to clean the passed in entity values", e);
            throw new ServiceException("Unable to clean the passed in entity values", e);
        }
    }

    public Entity add(PersistencePackage persistencePackage) throws ServiceException {
        cleanEntity(persistencePackage.getEntity());
        PersistenceManager persistenceManager = null;
        try {
            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.add(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                LOG.error("Unable to close persistence manager", e);
            }
        }
    }

    public Entity update(PersistencePackage persistencePackage) throws ServiceException {
        cleanEntity(persistencePackage.getEntity());
        PersistenceManager persistenceManager = null;
        try {
            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            return persistenceManager.update(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                LOG.error("Unable to close persistence manager", e);
            }
        }
    }

    public void remove(PersistencePackage persistencePackage) throws ServiceException {
        PersistenceManager persistenceManager = null;
        try {
            persistenceManager = (PersistenceManager) applicationContext.getBean(persistenceManagerRef);
            persistenceManager.setTargetMode(TargetModeType.SANDBOX);
            persistenceManager.remove(persistencePackage);
        } finally {
            try {
                if (persistenceManager != null) {
                    persistenceManager.close();
                }
            } catch (Exception e) {
                LOG.error("Unable to close persistence manager", e);
            }
        }
    }

    public String getPersistenceManagerRef() {
        return persistenceManagerRef;
    }

    public void setPersistenceManagerRef(String persistenceManagerRef) {
        this.persistenceManagerRef = persistenceManagerRef;
    }

    public String getAntiSamyPolicyFileLocation() {
        return antiSamyPolicyFileLocation;
    }

    public void setAntiSamyPolicyFileLocation(String antiSamyPolicyFileLocation) {
        this.antiSamyPolicyFileLocation = antiSamyPolicyFileLocation;
        antiSamyPolicy = getAntiSamyPolicy(antiSamyPolicyFileLocation);
    }
}
