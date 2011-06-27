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
package org.broadleafcommerce.gwt.admin.server.service.handler;

import java.util.Map;

import javax.annotation.Resource;

import org.broadleafcommerce.gwt.client.datasource.relations.PersistencePerspective;
import org.broadleafcommerce.gwt.client.datasource.results.DynamicResultSet;
import org.broadleafcommerce.gwt.client.datasource.results.Entity;
import org.broadleafcommerce.gwt.client.datasource.results.FieldMetadata;
import org.broadleafcommerce.gwt.client.service.ServiceException;
import org.broadleafcommerce.gwt.server.dao.DynamicEntityDao;
import org.broadleafcommerce.gwt.server.security.util.PasswordReset;
import org.broadleafcommerce.gwt.server.service.handler.CustomPersistenceHandler;
import org.broadleafcommerce.gwt.server.service.module.InspectHelper;
import org.broadleafcommerce.gwt.server.service.module.RecordHelper;
import org.broadleafcommerce.profile.core.domain.Customer;
import org.broadleafcommerce.profile.core.service.CustomerService;
import org.hibernate.tool.hbm2x.StringUtils;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;

/**
 * 
 * @author jfischer
 *
 */
public class CustomerPasswordCustomPersistenceHandler implements CustomPersistenceHandler {
	
	@Resource(name="blCustomerService")
	protected CustomerService customerService;

	public Boolean canHandleFetch(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleAdd(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleRemove(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public Boolean canHandleUpdate(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return customCriteria != null && customCriteria.length > 0 && customCriteria[0].equals("passwordUpdate");
	}

	public Boolean canHandleInspect(String ceilingEntityFullyQualifiedClassname, String[] customCriteria) {
		return false;
	}

	public DynamicResultSet inspect(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, String[] customCriteria, Map<String, FieldMetadata> metadataOverrides, DynamicEntityDao dynamicEntityDao, InspectHelper helper) throws ServiceException {
		throw new RuntimeException("custom inspect not supported");
	}

	public DynamicResultSet fetch(String ceilingEntityFullyQualifiedClassname, PersistencePerspective persistencePerspective, CriteriaTransferObject cto, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom fetch not supported");
	}

	public Entity add(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom add not supported");
	}

	public void remove(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		throw new RuntimeException("custom remove not supported");
	}

	public Entity update(Entity entity, PersistencePerspective persistencePerspective, String[] customCriteria, DynamicEntityDao dynamicEntityDao, RecordHelper helper) throws ServiceException {
		Customer customer = customerService.readCustomerByUsername(entity.findProperty("username").getValue());
		if (StringUtils.isEmpty(customer.getEmailAddress())) {
			throw new ServiceException("Unable to update password because an email address is not available for this customer. An email address is required to send the customer the new system generated password.");
		}
		
		PasswordReset passwordReset = new PasswordReset();
		passwordReset.setUsername(entity.findProperty("username").getValue());
		passwordReset.setPasswordChangeRequired(false);
		passwordReset.setEmail(customer.getEmailAddress());
		passwordReset.setPasswordLength(22);
		passwordReset.setSendResetEmailReliableAsync(false);
		
		customer = customerService.resetPassword(passwordReset);
		
		return entity;
	}
	
	
	
}
