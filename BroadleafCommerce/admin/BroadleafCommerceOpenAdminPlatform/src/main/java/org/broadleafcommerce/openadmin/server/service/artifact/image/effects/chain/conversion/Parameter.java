package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain.conversion;

public class Parameter {

	private Class parameterClass;
	private Object parameterInstance;
	
	/**
	 * @return the parameterClass
	 */
	public Class getParameterClass() {
		return parameterClass;
	}
	
	/**
	 * @param parameterClass the parameterClass to set
	 */
	public void setParameterClass(Class parameterClass) {
		this.parameterClass = parameterClass;
	}
	
	/**
	 * @return the parameterInstance
	 */
	public Object getParameterInstance() {
		return parameterInstance;
	}
	
	/**
	 * @param parameterInstance the parameterInstance to set
	 */
	public void setParameterInstance(Object parameterInstance) {
		this.parameterInstance = parameterInstance;
	}
	
}
