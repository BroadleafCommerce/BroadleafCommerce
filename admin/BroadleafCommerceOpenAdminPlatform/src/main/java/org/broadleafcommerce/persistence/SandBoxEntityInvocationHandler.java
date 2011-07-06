package org.broadleafcommerce.persistence;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SandBoxEntityInvocationHandler<T> implements InvocationHandler {

	private T delegateStandard;
	private T delegateSandBox;
	
	public SandBoxEntityInvocationHandler(T delegateStandard, T delegateSandBox) {
		this.delegateStandard = delegateStandard;
		this.delegateSandBox = delegateSandBox;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//TODO take a look at the method and return the raw result of the sandBox delegate if the field
		//is dirty. 
		return null;
	}

}
