package org.broadleafcommerce.openadmin.server.security.service;

/**
 * Simple key/value pair configuration for a {@link EntityFormModifier}.
 *
 * @see EntityFormModifierData
 * @author Jeff Fischer
 */
public class EntityFormModifierDataPoint {

    protected Object key;
    protected Object value;

    public Object getKey() {
        return key;
    }

    public void setKey(Object key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
