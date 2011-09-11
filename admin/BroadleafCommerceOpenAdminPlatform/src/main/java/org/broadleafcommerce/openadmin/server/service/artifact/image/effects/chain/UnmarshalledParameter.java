package org.broadleafcommerce.openadmin.server.service.artifact.image.effects.chain;

public class UnmarshalledParameter {
	
	private String name;
	private String value;
	private String type;
	private boolean applyFactor;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the applyFactor
	 */
	public boolean isApplyFactor() {
		return applyFactor;
	}

	/**
	 * @param applyFactor the applyFactor to set
	 */
	public void setApplyFactor(boolean applyFactor) {
		this.applyFactor = applyFactor;
	}

}
