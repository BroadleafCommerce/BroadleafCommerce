package org.broadleafcommerce.common.presentation.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Define whether a new collection member is searched for or constructed.
 *
 * @author Jeff Fischer
 */
public enum AddMethodType implements IsSerializable {
    PERSIST,
    LOOKUP
}
