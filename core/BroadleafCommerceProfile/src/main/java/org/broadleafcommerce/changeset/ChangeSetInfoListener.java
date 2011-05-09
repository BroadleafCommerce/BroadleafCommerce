package org.broadleafcommerce.changeset;

import org.broadleafcommerce.changeset.domain.ChangeSetInfo;
import org.hibernate.envers.RevisionListener;

public class ChangeSetInfoListener implements RevisionListener {

	public void newRevision(Object revisionEntity) {
		ChangeSetInfo info = (ChangeSetInfo) revisionEntity;
		info.setUser(1L);
		info.setChangeset(1L);
	}

}
