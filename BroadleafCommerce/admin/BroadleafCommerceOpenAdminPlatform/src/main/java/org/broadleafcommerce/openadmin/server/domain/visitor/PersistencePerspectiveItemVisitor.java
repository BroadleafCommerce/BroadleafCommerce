package org.broadleafcommerce.openadmin.server.domain.visitor;

import org.broadleafcommerce.openadmin.server.domain.ForeignKey;
import org.broadleafcommerce.openadmin.server.domain.JoinStructure;
import org.broadleafcommerce.openadmin.server.domain.MapStructure;
import org.broadleafcommerce.openadmin.server.domain.SimpleValueMapStructure;

public interface PersistencePerspectiveItemVisitor {

	public void visit(JoinStructure joinStructure);
	
	public void visit(MapStructure mapStructure);
	
	public void visit(SimpleValueMapStructure simpleValueMapStructure);
	
	public void visit(ForeignKey foreignKey);
	
}
