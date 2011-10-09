package org.broadleafcommerce.openadmin.client.reflection;

/**
 * Created by IntelliJ IDEA.
 * User: jfischer
 * Date: 9/28/11
 * Time: 8:55 AM
 * To change this template use File | Settings | File Templates.
 */
public interface AsyncClient {

    public void onSuccess(Object instance);
    public void onUnavailable();

}
