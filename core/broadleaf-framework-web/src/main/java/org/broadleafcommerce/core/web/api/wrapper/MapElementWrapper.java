package org.broadleafcommerce.core.web.api.wrapper;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * <p>
 * This is a JAXB wrapper to encapsulate a Map<String, Object>
 * <p/>
 * User: Elbert Bautista
 * Date: 4/26/12
 */
@XmlRootElement(name = "element")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class MapElementWrapper {

    @XmlElement
    protected String key;

    @XmlElement
    protected Object value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
