package org.broadleafcommerce.util;

import java.io.Serializable;
import java.util.List;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Statistics;
import net.sf.ehcache.Status;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.event.RegisteredEventListeners;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class HydratedCacheDecorator implements Ehcache {
	
	private final Ehcache source;

	public HydratedCacheDecorator(Ehcache source) {
		this.source = source;
	}

	public void bootstrap() {
		source.bootstrap();
	}

	public long calculateInMemorySize() throws IllegalStateException, CacheException {
		return source.calculateInMemorySize();
	}

	public void clearStatistics() {
		source.clearStatistics();
	}

	public Object clone() throws CloneNotSupportedException {
		return source.clone();
	}

	public void dispose() throws IllegalStateException {
		source.dispose();
	}

	public void evictExpiredElements() {
		source.evictExpiredElements();
	}

	public void flush() throws IllegalStateException, CacheException {
		source.flush();
	}

	public Element get(Object arg0) throws IllegalStateException, CacheException {
		return source.get(arg0);
	}

	public Element get(Serializable arg0) throws IllegalStateException, CacheException {
		return source.get(arg0);
	}

	public BootstrapCacheLoader getBootstrapCacheLoader() {
		return source.getBootstrapCacheLoader();
	}

	public RegisteredEventListeners getCacheEventNotificationService() {
		return source.getCacheEventNotificationService();
	}

	public CacheManager getCacheManager() {
		return source.getCacheManager();
	}

	public long getDiskExpiryThreadIntervalSeconds() {
		return source.getDiskExpiryThreadIntervalSeconds();
	}

	public int getDiskStoreHitCount() {
		return source.getDiskStoreHitCount();
	}

	public int getDiskStoreSize() throws IllegalStateException {
		return source.getDiskStoreSize();
	}

	public String getGuid() {
		return source.getGuid();
	}

	public int getHitCount() {
		return source.getHitCount();
	}

	public List getKeys() throws IllegalStateException, CacheException {
		return source.getKeys();
	}

	public List getKeysNoDuplicateCheck() throws IllegalStateException {
		return source.getKeysNoDuplicateCheck();
	}

	public List getKeysWithExpiryCheck() throws IllegalStateException, CacheException {
		return source.getKeysWithExpiryCheck();
	}

	public int getMaxElementsInMemory() {
		return source.getMaxElementsInMemory();
	}

	public MemoryStoreEvictionPolicy getMemoryStoreEvictionPolicy() {
		return source.getMemoryStoreEvictionPolicy();
	}

	public int getMemoryStoreHitCount() {
		return source.getMemoryStoreHitCount();
	}

	public long getMemoryStoreSize() throws IllegalStateException {
		return source.getMemoryStoreSize();
	}

	public int getMissCountExpired() {
		return source.getMissCountExpired();
	}

	public int getMissCountNotFound() {
		return source.getMissCountNotFound();
	}

	public String getName() {
		return source.getName();
	}

	public Element getQuiet(Object arg0) throws IllegalStateException, CacheException {
		return source.getQuiet(arg0);
	}

	public Element getQuiet(Serializable arg0) throws IllegalStateException, CacheException {
		return source.getQuiet(arg0);
	}

	public int getSize() throws IllegalStateException, CacheException {
		return source.getSize();
	}

	public Statistics getStatistics() throws IllegalStateException {
		return source.getStatistics();
	}

	public int getStatisticsAccuracy() {
		return source.getStatisticsAccuracy();
	}

	public Status getStatus() {
		return source.getStatus();
	}

	public long getTimeToIdleSeconds() {
		return source.getTimeToIdleSeconds();
	}

	public long getTimeToLiveSeconds() {
		return source.getTimeToLiveSeconds();
	}

	public void initialise() {
		source.initialise();
	}

	public boolean isDiskPersistent() {
		return source.isDiskPersistent();
	}

	public boolean isElementInMemory(Object arg0) {
		return source.isElementInMemory(arg0);
	}

	public boolean isElementInMemory(Serializable arg0) {
		return source.isElementInMemory(arg0);
	}

	public boolean isElementOnDisk(Object arg0) {
		return source.isElementOnDisk(arg0);
	}

	public boolean isElementOnDisk(Serializable arg0) {
		return source.isElementOnDisk(arg0);
	}

	public boolean isEternal() {
		return source.isEternal();
	}

	public boolean isExpired(Element arg0) throws IllegalStateException, NullPointerException {
		return source.isExpired(arg0);
	}

	public boolean isKeyInCache(Object arg0) {
		return source.isKeyInCache(arg0);
	}

	public boolean isOverflowToDisk() {
		return source.isOverflowToDisk();
	}

	public boolean isValueInCache(Object arg0) {
		return source.isValueInCache(arg0);
	}

	public void put(Element arg0, boolean arg1) throws IllegalArgumentException, IllegalStateException, CacheException {
		source.put(arg0, arg1);
	}

	public void put(Element arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
		source.put(arg0);
	}

	public void putQuiet(Element arg0) throws IllegalArgumentException, IllegalStateException, CacheException {
		source.putQuiet(arg0);
	}

	public boolean remove(Object arg0, boolean arg1) throws IllegalStateException {
		return source.remove(arg0, arg1);
	}

	public boolean remove(Object arg0) throws IllegalStateException {
		return source.remove(arg0);
	}

	public boolean remove(Serializable arg0, boolean arg1) throws IllegalStateException {
		return source.remove(arg0, arg1);
	}

	public boolean remove(Serializable arg0) throws IllegalStateException {
		return source.remove(arg0);
	}

	public void removeAll() throws IllegalStateException, CacheException {
		source.removeAll();
	}

	public void removeAll(boolean arg0) throws IllegalStateException, CacheException {
		source.removeAll(arg0);
	}

	public boolean removeQuiet(Object arg0) throws IllegalStateException {
		return source.removeQuiet(arg0);
	}

	public boolean removeQuiet(Serializable arg0) throws IllegalStateException {
		return source.removeQuiet(arg0);
	}

	public void setBootstrapCacheLoader(BootstrapCacheLoader arg0) throws CacheException {
		source.setBootstrapCacheLoader(arg0);
	}

	public void setCacheManager(CacheManager arg0) {
		source.setCacheManager(arg0);
	}

	public void setDiskStorePath(String arg0) throws CacheException {
		source.setDiskStorePath(arg0);
	}

	public void setName(String arg0) {
		source.setName(arg0);
	}

	public void setStatisticsAccuracy(int arg0) {
		source.setStatisticsAccuracy(arg0);
	}

	public String toString() {
		return source.toString();
	}

}
