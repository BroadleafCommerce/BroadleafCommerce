package org.broadleafcommerce.gwt.workaround;

import org.apache.tools.ant.taskdefs.Javac;
import org.eclipse.jdt.core.JDTCompilerAdapter;

public class JDTCompiler15 extends JDTCompilerAdapter {
	
	@Override
	public void setJavac(Javac attributes) {
		if (attributes.getTarget() == null) {
		      attributes.setTarget("1.5");
		    }
		    if (attributes.getSource() == null) {
		      attributes.setSource("1.5");
		    }
		    super.setJavac(attributes);
	}
	
}
