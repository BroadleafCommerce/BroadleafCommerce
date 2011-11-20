package org.broadleafcommerce.openadmin.client.dto.visitor;

import org.broadleafcommerce.openadmin.client.dto.ForeignKey;
import org.broadleafcommerce.openadmin.client.dto.JoinStructure;
import org.broadleafcommerce.openadmin.client.dto.MapStructure;
import org.broadleafcommerce.openadmin.client.dto.SimpleValueMapStructure;


public interface PersistencePerspectiveItemVisitor {

	public void visit(JoinStructure joinStructure);
	
	public void visit(MapStructure mapStructure);
	
	public void visit(SimpleValueMapStructure simpleValueMapStructure);
	
	public void visit(ForeignKey foreignKey);
	
}
