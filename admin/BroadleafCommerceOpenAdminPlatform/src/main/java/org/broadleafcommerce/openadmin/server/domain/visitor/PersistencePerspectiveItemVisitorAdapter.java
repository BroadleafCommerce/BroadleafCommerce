package org.broadleafcommerce.openadmin.server.domain.visitor;

import org.broadleafcommerce.openadmin.server.domain.ForeignKey;
import org.broadleafcommerce.openadmin.server.domain.JoinStructure;
import org.broadleafcommerce.openadmin.server.domain.MapStructure;
import org.broadleafcommerce.openadmin.server.domain.SimpleValueMapStructure;

public class PersistencePerspectiveItemVisitorAdapter implements PersistencePerspectiveItemVisitor {

	@Override
	public void visit(JoinStructure joinStructure) {
		//do nothing
	}

	@Override
	public void visit(MapStructure mapStructure) {
		//do nothing
	}

	@Override
	public void visit(SimpleValueMapStructure simpleValueMapStructure) {
		//do nothing
	}

	@Override
	public void visit(ForeignKey foreignKey) {
		//do nothing
	}
}
