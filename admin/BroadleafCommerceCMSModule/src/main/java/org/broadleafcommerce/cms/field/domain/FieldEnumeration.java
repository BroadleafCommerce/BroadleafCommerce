package org.broadleafcommerce.cms.field.domain;

import java.util.List;

/**
 * Created by jfischer
 */
public interface FieldEnumeration {
    List<FieldEnumerationItem> getEnumerationItems();

    void setEnumerationItems(List<FieldEnumerationItem> enumerationItems);

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);
}
