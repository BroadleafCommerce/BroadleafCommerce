package org.broadleafcommerce.openadmin.server.domain;



import org.broadleafcommerce.presentation.BroadleafEnumerationType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by bpolster.
 */
public class SandBoxItemType implements Serializable, BroadleafEnumerationType {

    private static final long serialVersionUID = 1L;

    private static final Map<String, SandBoxItemType> TYPES = new HashMap<String, SandBoxItemType>();

    public static final SandBoxItemType ENTITY  = new SandBoxItemType("ENTITY", "Entity");
    public static final SandBoxItemType PAGE  = new SandBoxItemType("PAGE", "Page");
    public static final SandBoxItemType STRUCTURED_CONTENT  = new SandBoxItemType("STRUCTURED_CONTENT", "Structured Content");
    public static final SandBoxItemType STATIC_ASSET  = new SandBoxItemType("STATIC_ASSET", "Static Asset");

    public static SandBoxItemType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public SandBoxItemType() {
        //do nothing
    }

    public SandBoxItemType(final String type, final String friendlyType) {
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
        } else {
            throw new RuntimeException("Cannot add the type: (" + type + "). It already exists as a type via " + getInstance(type).getClass().getName());
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
        if (getClass() != obj.getClass())
            return false;
        SandBoxItemType other = (SandBoxItemType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
