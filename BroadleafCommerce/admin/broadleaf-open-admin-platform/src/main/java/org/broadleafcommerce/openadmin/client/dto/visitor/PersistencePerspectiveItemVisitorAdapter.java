package org.broadleafcommerce.openadmin.client.dto.visitor;

import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;


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
