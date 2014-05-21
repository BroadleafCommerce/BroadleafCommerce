package org.broadleafcommerce.openadmin.server.service.persistence.module.criteria;

/**
 * Thrown when a problem converting a particular {@link org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FieldPath}
 * from a {@link org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.FilterMapping} is detected
 * during JPA criteria translation for fetch operation.
 *
 * @see org.broadleafcommerce.openadmin.server.service.persistence.module.criteria.CriteriaTranslatorImpl
 * @author Jeff Fischer
 */
public class CriteriaConversionException extends RuntimeException {

    protected FieldPath fieldPath;

    public CriteriaConversionException(String message, FieldPath fieldPath) {
        super(message);
        this.fieldPath = fieldPath;
    }

    public FieldPath getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(FieldPath fieldPath) {
        this.fieldPath = fieldPath;
    }
}
