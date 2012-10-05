package org.broadleafcommerce.openadmin.client;

import java.util.Map;

/**
 * @author Jeff Fischer
 */
public interface i18nPropertiesClient {

    public void onSuccess(Map<String, String> localizedProperties);
    public void onUnavailable(Throwable error);

}
