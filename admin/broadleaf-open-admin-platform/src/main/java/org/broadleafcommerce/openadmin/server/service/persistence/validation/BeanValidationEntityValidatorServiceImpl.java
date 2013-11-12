/*
 * Copyright 2013 the original author or authors.
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

package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import org.broadleafcommerce.common.presentation.AdminPresentation;
import org.broadleafcommerce.common.presentation.ValidationConfiguration;
import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

/**
 * <p>Implementation of the {@link EntityValidatorService} that runs entities through JSR-303 validations. The default
 * behavior of this implementation is to use the out-of-the-box Broadleaf validations (through {@link ValidationConfiguration}
 * <i>in addition to</i> any JSR-303 annotations that you have configured on your entity.</p>
 * 
 * <p>In order to use this validator rather than the default, you will need to include an implementation of {@link Validator}
 * in your Spring root application context (not the servlet). For example, you would modify your applicationContext-admin.xml
 * to inject Spring's default implementation of {@link Validator} (the one used by Spring MVC):</p>
 * <code>
 * <pre>
 * &lt;bean class=&quotorg.springframework.validation.beanvalidation.LocalValidatorFactoryBean&quot/&gt;
 * </pre>
 * </code>
 * Then override the the blEntityValidatorService bean to use this class instead:
 * <code>
 * <pre>
 * &lt;bean id=&quotblEntityValidatorService&quot 
 *        class=&quotorg.broadleafcommerce.openadmin.server.service.persistence.validation.BeanValidationEntityValidatorServiceImpl&quot/&gt;
 * </pre>
 * </code>
 * 
 * <p>For more information on the default Spring JSR-303 validator, check out the docs at
 * 
 * 
 * @author Phillip Verheyden
 * @see {@link EntityValidatorServiceImpl#validate(Entity, Serializable, Map)}
 * @see {@link Validator}
 * @see <a href="http://static.springsource.org/spring/docs/3.1.3.RELEASE/spring-framework-reference/html/validation.html#validation-beanvalidation">Spring Validation Docs</a>
 */
public class BeanValidationEntityValidatorServiceImpl extends EntityValidatorServiceImpl {

    @Autowired
    protected Validator validator;
    
    /**
     * If true (default behavior) this will invoke the default implementation to perform validations hooked up via
     * {@link ValidationConfiguration} from {@link AdminPresentation}.
     */
    protected boolean useDefaultEntityValidations = true;
    
    @Override
    public void validate(Entity entity, Serializable instance, Map<String, FieldMetadata> mergedProperties,
            boolean validateUnsubmittedProperties) {
        
        if (isUseDefaultEntityValidations()) {
            super.validate(entity, instance, mergedProperties, validateUnsubmittedProperties);
        }

        Set<ConstraintViolation<Serializable>> violations = getValidator().validate(instance);
        for (ConstraintViolation<Serializable> violation : violations) {
            entity.addValidationError(violation.getPropertyPath().toString(), violation.getMessage());
        }
    }

    public Validator getValidator() {
        return validator;
    }

    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    public boolean isUseDefaultEntityValidations() {
        return useDefaultEntityValidations;
    }

    public void setUseDefaultEntityValidations(boolean useDefaultEntityValidations) {
        this.useDefaultEntityValidations = useDefaultEntityValidations;
    }

}
