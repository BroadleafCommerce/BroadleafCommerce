package org.springcommerce.demo.web;

public class ExtensibilityTestBean3 extends ExtensibilityTestBean {
	
	private String testProperty = "new";
	private String testProperty3 = "none3";
	
	/**
	 * @return the testProperty
	 */
	public String getTestProperty() {
		return testProperty;
	}
	
	/**
	 * @param testProperty the testProperty to set
	 */
	public void setTestProperty(String testProperty) {
		this.testProperty = testProperty;
	}
	
	/**
	 * @return the testProperty3
	 */
	public String getTestProperty3() {
		return testProperty3;
	}
	
	/**
	 * @param testProperty3 the testProperty3 to set
	 */
	public void setTestProperty3(String testProperty3) {
		this.testProperty3 = testProperty3;
	}

}
