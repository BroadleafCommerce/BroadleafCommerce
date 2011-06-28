/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.broadleafcommerce.persistence;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.metamodel.Metamodel;

//TODO implement the usage of the sandbox entity manager
/**
 * 
 * @author jfischer
 *
 */
public class BroadleafEntityManager implements EntityManager {
	
	protected final EntityManager standardManager;
	protected final EntityManager sandboxManager;
	
	public BroadleafEntityManager(EntityManager standardManager, EntityManager sandboxManager) {
		this.standardManager = standardManager;
		this.sandboxManager = sandboxManager;
	}

	/**
	 * @param entity
	 * @see javax.persistence.EntityManager#persist(java.lang.Object)
	 */
	public void persist(Object entity) {
		standardManager.persist(entity);
	}

	/**
	 * @param <T>
	 * @param entity
	 * @return
	 * @see javax.persistence.EntityManager#merge(java.lang.Object)
	 */
	public <T> T merge(T entity) {
		return standardManager.merge(entity);
	}

	/**
	 * @param entity
	 * @see javax.persistence.EntityManager#remove(java.lang.Object)
	 */
	public void remove(Object entity) {
		standardManager.remove(entity);
	}

	/**
	 * @param <T>
	 * @param entityClass
	 * @param primaryKey
	 * @return
	 * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object)
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey) {
		return standardManager.find(entityClass, primaryKey);
	}

	/**
	 * @param <T>
	 * @param entityClass
	 * @param primaryKey
	 * @param properties
	 * @return
	 * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, java.util.Map)
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey,
			Map<String, Object> properties) {
		return standardManager.find(entityClass, primaryKey, properties);
	}

	/**
	 * @param <T>
	 * @param entityClass
	 * @param primaryKey
	 * @param lockMode
	 * @return
	 * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, javax.persistence.LockModeType)
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode) {
		return standardManager.find(entityClass, primaryKey, lockMode);
	}

	/**
	 * @param <T>
	 * @param entityClass
	 * @param primaryKey
	 * @param lockMode
	 * @param properties
	 * @return
	 * @see javax.persistence.EntityManager#find(java.lang.Class, java.lang.Object, javax.persistence.LockModeType, java.util.Map)
	 */
	public <T> T find(Class<T> entityClass, Object primaryKey,
			LockModeType lockMode, Map<String, Object> properties) {
		return standardManager.find(entityClass, primaryKey, lockMode,
				properties);
	}

	/**
	 * @param <T>
	 * @param entityClass
	 * @param primaryKey
	 * @return
	 * @see javax.persistence.EntityManager#getReference(java.lang.Class, java.lang.Object)
	 */
	public <T> T getReference(Class<T> entityClass, Object primaryKey) {
		return standardManager.getReference(entityClass, primaryKey);
	}

	/**
	 * 
	 * @see javax.persistence.EntityManager#flush()
	 */
	public void flush() {
		standardManager.flush();
	}

	/**
	 * @param flushMode
	 * @see javax.persistence.EntityManager#setFlushMode(javax.persistence.FlushModeType)
	 */
	public void setFlushMode(FlushModeType flushMode) {
		standardManager.setFlushMode(flushMode);
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getFlushMode()
	 */
	public FlushModeType getFlushMode() {
		return standardManager.getFlushMode();
	}

	/**
	 * @param entity
	 * @param lockMode
	 * @see javax.persistence.EntityManager#lock(java.lang.Object, javax.persistence.LockModeType)
	 */
	public void lock(Object entity, LockModeType lockMode) {
		standardManager.lock(entity, lockMode);
	}

	/**
	 * @param entity
	 * @param lockMode
	 * @param properties
	 * @see javax.persistence.EntityManager#lock(java.lang.Object, javax.persistence.LockModeType, java.util.Map)
	 */
	public void lock(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		standardManager.lock(entity, lockMode, properties);
	}

	/**
	 * @param entity
	 * @see javax.persistence.EntityManager#refresh(java.lang.Object)
	 */
	public void refresh(Object entity) {
		standardManager.refresh(entity);
	}

	/**
	 * @param entity
	 * @param properties
	 * @see javax.persistence.EntityManager#refresh(java.lang.Object, java.util.Map)
	 */
	public void refresh(Object entity, Map<String, Object> properties) {
		standardManager.refresh(entity, properties);
	}

	/**
	 * @param entity
	 * @param lockMode
	 * @see javax.persistence.EntityManager#refresh(java.lang.Object, javax.persistence.LockModeType)
	 */
	public void refresh(Object entity, LockModeType lockMode) {
		standardManager.refresh(entity, lockMode);
	}

	/**
	 * @param entity
	 * @param lockMode
	 * @param properties
	 * @see javax.persistence.EntityManager#refresh(java.lang.Object, javax.persistence.LockModeType, java.util.Map)
	 */
	public void refresh(Object entity, LockModeType lockMode,
			Map<String, Object> properties) {
		standardManager.refresh(entity, lockMode, properties);
	}

	/**
	 * 
	 * @see javax.persistence.EntityManager#clear()
	 */
	public void clear() {
		standardManager.clear();
	}

	/**
	 * @param entity
	 * @see javax.persistence.EntityManager#detach(java.lang.Object)
	 */
	public void detach(Object entity) {
		standardManager.detach(entity);
	}

	/**
	 * @param entity
	 * @return
	 * @see javax.persistence.EntityManager#contains(java.lang.Object)
	 */
	public boolean contains(Object entity) {
		return standardManager.contains(entity);
	}

	/**
	 * @param entity
	 * @return
	 * @see javax.persistence.EntityManager#getLockMode(java.lang.Object)
	 */
	public LockModeType getLockMode(Object entity) {
		return standardManager.getLockMode(entity);
	}

	/**
	 * @param propertyName
	 * @param value
	 * @see javax.persistence.EntityManager#setProperty(java.lang.String, java.lang.Object)
	 */
	public void setProperty(String propertyName, Object value) {
		standardManager.setProperty(propertyName, value);
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getProperties()
	 */
	public Map<String, Object> getProperties() {
		return standardManager.getProperties();
	}

	/**
	 * @param qlString
	 * @return
	 * @see javax.persistence.EntityManager#createQuery(java.lang.String)
	 */
	public Query createQuery(String qlString) {
		return standardManager.createQuery(qlString);
	}

	/**
	 * @param <T>
	 * @param criteriaQuery
	 * @return
	 * @see javax.persistence.EntityManager#createQuery(javax.persistence.criteria.CriteriaQuery)
	 */
	public <T> TypedQuery<T> createQuery(CriteriaQuery<T> criteriaQuery) {
		return standardManager.createQuery(criteriaQuery);
	}

	/**
	 * @param <T>
	 * @param qlString
	 * @param resultClass
	 * @return
	 * @see javax.persistence.EntityManager#createQuery(java.lang.String, java.lang.Class)
	 */
	public <T> TypedQuery<T> createQuery(String qlString, Class<T> resultClass) {
		return standardManager.createQuery(qlString, resultClass);
	}

	/**
	 * @param name
	 * @return
	 * @see javax.persistence.EntityManager#createNamedQuery(java.lang.String)
	 */
	public Query createNamedQuery(String name) {
		return standardManager.createNamedQuery(name);
	}

	/**
	 * @param <T>
	 * @param name
	 * @param resultClass
	 * @return
	 * @see javax.persistence.EntityManager#createNamedQuery(java.lang.String, java.lang.Class)
	 */
	public <T> TypedQuery<T> createNamedQuery(String name, Class<T> resultClass) {
		return standardManager.createNamedQuery(name, resultClass);
	}

	/**
	 * @param sqlString
	 * @return
	 * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String)
	 */
	public Query createNativeQuery(String sqlString) {
		return standardManager.createNativeQuery(sqlString);
	}

	/**
	 * @param sqlString
	 * @param resultClass
	 * @return
	 * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String, java.lang.Class)
	 */
	@SuppressWarnings("rawtypes")
	public Query createNativeQuery(String sqlString, Class resultClass) {
		return standardManager.createNativeQuery(sqlString, resultClass);
	}

	/**
	 * @param sqlString
	 * @param resultSetMapping
	 * @return
	 * @see javax.persistence.EntityManager#createNativeQuery(java.lang.String, java.lang.String)
	 */
	public Query createNativeQuery(String sqlString, String resultSetMapping) {
		return standardManager.createNativeQuery(sqlString, resultSetMapping);
	}

	/**
	 * 
	 * @see javax.persistence.EntityManager#joinTransaction()
	 */
	public void joinTransaction() {
		standardManager.joinTransaction();
	}

	/**
	 * @param <T>
	 * @param cls
	 * @return
	 * @see javax.persistence.EntityManager#unwrap(java.lang.Class)
	 */
	public <T> T unwrap(Class<T> cls) {
		return standardManager.unwrap(cls);
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getDelegate()
	 */
	public Object getDelegate() {
		return standardManager.getDelegate();
	}

	/**
	 * 
	 * @see javax.persistence.EntityManager#close()
	 */
	public void close() {
		standardManager.close();
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#isOpen()
	 */
	public boolean isOpen() {
		return standardManager.isOpen();
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getTransaction()
	 */
	public EntityTransaction getTransaction() {
		return standardManager.getTransaction();
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getEntityManagerFactory()
	 */
	public EntityManagerFactory getEntityManagerFactory() {
		return standardManager.getEntityManagerFactory();
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getCriteriaBuilder()
	 */
	public CriteriaBuilder getCriteriaBuilder() {
		return standardManager.getCriteriaBuilder();
	}

	/**
	 * @return
	 * @see javax.persistence.EntityManager#getMetamodel()
	 */
	public Metamodel getMetamodel() {
		return standardManager.getMetamodel();
	}

	

}
