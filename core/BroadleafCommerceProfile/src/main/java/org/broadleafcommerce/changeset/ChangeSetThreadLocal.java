package org.broadleafcommerce.changeset;

import org.broadleafcommerce.changeset.dao.ChangeSetDao;

public class ChangeSetThreadLocal {
	
	private static final ThreadLocal<ChangeSetDao> changeSetDao = new ThreadLocal<ChangeSetDao>();
	private static final ThreadLocal<Long> user  = new ThreadLocal<Long>();
	private static final ThreadLocal<Long> changeSet = new ThreadLocal<Long>();
	
	public static ChangeSetDao getChangeSetDao() {
		return ChangeSetThreadLocal.changeSetDao.get();
	}
	
	public static void setChangeSetDao(ChangeSetDao changeSetDao) {
		ChangeSetThreadLocal.changeSetDao.set(changeSetDao);
	}
	
	public static Long getUser() {
		return ChangeSetThreadLocal.user.get();
	}
	
	public static void setUser(Long user) {
		ChangeSetThreadLocal.user.set(user);
	}
	
	public static Long getChangeSet() {
		return ChangeSetThreadLocal.changeSet.get();
	}
	
	public static void setChangeSet(Long changeSet) {
		ChangeSetThreadLocal.changeSet.set(changeSet);
	}
	
	
}
