package org.broadleafcommerce.gwt.server.changeset;

import org.broadleafcommerce.gwt.server.changeset.domain.ChangeSetInfo;
import org.hibernate.envers.RevisionListener;

public class ChangeSetInfoListener implements RevisionListener {

	public void newRevision(Object revisionEntity) {
		ChangeSetInfo info = (ChangeSetInfo) revisionEntity;
		info.setUser(1L);
		info.setChangeset(1L);
	}

}
