package org.broadleafcommerce.common.config.domain;

import java.io.Serializable;

/**
 * This interface represents a System Property (name/value pair) stored in the database.  It can be used to override
 * Spring-injected properties that are injected using the @Value annotation.
 * <p/>
 * User: Kelly Tisdell
 * Date: 6/20/12
 */
public interface SystemProperty extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);

    public String getValue();

    public void setValue(String value);

}
