package org.broadleafcommerce.gwt.client.service;

import java.io.Serializable;

import org.broadleafcommerce.gwt.client.datasource.ResultSet;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous version of {@link GridService}.
 */
public interface GridServiceAsync<Entity extends Serializable> {
    
    void fetch(CriteriaTransferObject cto, AsyncCallback<ResultSet<Entity>> cb);
    
    void add(Entity dto, AsyncCallback<Entity> cb);
    
    void update(Entity dto, AsyncCallback<Entity> cb);
    
    void remove(Entity dto, AsyncCallback<Void> cb);
    
}
