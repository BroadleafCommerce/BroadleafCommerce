package org.broadleafcommerce.profile.core.domain;

import java.io.Serializable;

/**
 * @author Jerry Ocanas (jocanas)
 */
public interface CustomerGroup extends Serializable {

    public Long getId();

    public void setId(Long id);

    public String getName();

    public void setName(String name);
}
