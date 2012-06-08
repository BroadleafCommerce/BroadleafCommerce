package org.broadleafcommerce.cms.url;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.broadleafcommerce.common.BroadleafEnumerationType;

public class URLRedirectType implements Serializable, BroadleafEnumerationType {
	
    private static final long serialVersionUID = 1L;

    private static final Map<String, URLRedirectType> TYPES = new HashMap<String, URLRedirectType>();

    public static final URLRedirectType FORWARD = new URLRedirectType("FORWARD", "Forward URI");
    public static final URLRedirectType REDIRECT_PERM = new URLRedirectType("REDIRECT_PERM", "Redirect URI Permanently (301)");
    public static final URLRedirectType REDIRECT_TEMP = new URLRedirectType("REDIRECT_TEMP", "Redirect URI Temporarily (302)");

    public static URLRedirectType getInstance(final String type) {
        return TYPES.get(type);
    }

    private String type;
    private String friendlyType;

    public URLRedirectType() {
        //do nothing
    }

    public URLRedirectType(final String type, final String friendlyType) {
    	this.friendlyType = friendlyType;
        setType(type);
    }

    public void setType(final String type) {
        this.type = type;
        if (!TYPES.containsKey(type)) {
            TYPES.put(type, this);
        }
    }

    public String getType() {
        return type;
    }

    public String getFriendlyType() {
		return friendlyType;
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
        URLRedirectType other = (URLRedirectType) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

}
