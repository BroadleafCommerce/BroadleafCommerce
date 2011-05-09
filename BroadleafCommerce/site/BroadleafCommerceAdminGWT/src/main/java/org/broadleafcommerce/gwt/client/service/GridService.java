package org.broadleafcommerce.gwt.client.service;

import java.io.Serializable;

import org.broadleafcommerce.gwt.client.datasource.ResultSet;

import com.anasoft.os.daofusion.cto.client.CriteriaTransferObject;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * Generic service interface defining common grid-like operations.
 */
public interface GridService<Entity extends Serializable> extends RemoteService {
    
    ResultSet<Entity> fetch(CriteriaTransferObject cto) throws ServiceException;
    
    Entity add(Entity dto) throws ServiceException;
    
    Entity update(Entity dto) throws ServiceException;
    
    void remove(Entity dto) throws ServiceException;
    
}
