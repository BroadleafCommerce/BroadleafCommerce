package org.broadleafcommerce.openadmin.server.service.persistence.validation;

import java.io.Serializable;
import java.util.Map;

import javax.annotation.Nonnull;

import org.broadleafcommerce.openadmin.dto.Entity;
import org.broadleafcommerce.openadmin.dto.FieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.module.RecordHelper;

/**
 * A Spring bean extending this class will automatically be called when validation is done on the entity specified by
 * the generic type.
 * 
 * The persistent entity class that Hibernate is aware of should be used as the generic type. For example,
 * SomeEntityImpl instead of SomeEntity interface.
 * 
 * In the implementation of validate {@link Entity#addValidationError(String, String)} and
 * {@link Entity#addValidationError(String, String)} can be used to create an error that is displayed to the user before
 * an add or update occurs. {@link Entity#isValidationFailure()} can be used to see if the core validation found any
 * issues like required fields being blank to decide if any additional validation should be executed.
 * 
 * @param <T> Persistence Entity implementation to validate
 */
public abstract class BroadleafEntityValidator<T> {

	/**
	 * Validation that should be done on the specified entity after core validation is completed.
	 */
	public abstract void validate(Entity submittedEntity, @Nonnull T instance,
			Map<String, FieldMetadata> propertiesMetadata, RecordHelper recordHelper,
			boolean validateUnsubmittedProperties);

	@SuppressWarnings("unchecked")
	void validate(Entity submittedEntity, @Nonnull Serializable instance, Map<String, FieldMetadata> propertiesMetadata,
			RecordHelper recordHelper, boolean validateUnsubmittedProperties) {
		validate(submittedEntity, (T) instance, propertiesMetadata, recordHelper, validateUnsubmittedProperties);
	}
}
