package org.broadleafcommerce.core.linked.data;

import org.broadleafcommerce.common.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Jon Fleschler (jfleschler)
 */
public class LinkedDataDestinationType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, LinkedDataDestinationType> TYPES = new LinkedHashMap<>();

    public static final LinkedDataDestinationType PRODUCT  = new LinkedDataDestinationType("PRODUCT", "Product");
    public static final LinkedDataDestinationType CATEGORY  = new LinkedDataDestinationType("CATEGORY", "Category");
    public static final LinkedDataDestinationType HOME  = new LinkedDataDestinationType("HOME", "Home");
    public static final LinkedDataDestinationType DEFAULT  = new LinkedDataDestinationType("DEFAULT", "Default");

    public static LinkedDataDestinationType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public LinkedDataDestinationType() {
        //do nothing
    }

    public LinkedDataDestinationType(final String type, final String friendlyType) {
        this.friendlyType = friendlyType;
        setType(type);
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
        return friendlyType;
    }

    private void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!getClass().isAssignableFrom(obj.getClass()))
            return false;
        LinkedDataDestinationType other = (LinkedDataDestinationType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
