package org.broadleafcommerce.test;

import org.broadleafcommerce.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.extensibility.context.StandardConfigLocations;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

public class MergeDependencyInjectionTestExecutionListener extends DependencyInjectionTestExecutionListener {

	private static MergeClassPathXMLApplicationContext mergeContext = null;
	
	@Override
	protected void injectDependencies(TestContext testContext) throws Exception {
		try {
			if (mergeContext == null) {
				String[] contexts = StandardConfigLocations.retrieveAll();
				String[] allContexts = new String[contexts.length + 2];
				System.arraycopy(contexts, 0, allContexts, 0, contexts.length);
				allContexts[allContexts.length-2] = "bl-applicationContext-test.xml";
				allContexts[allContexts.length-1] = "bl-applicationContext-test-security.xml";
				mergeContext = new MergeClassPathXMLApplicationContext(allContexts, new String[]{});
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		Object bean = testContext.getTestInstance();
		AutowireCapableBeanFactory beanFactory = mergeContext.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_NO, false);
		beanFactory.initializeBean(bean, testContext.getTestClass().getName());
		testContext.removeAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE);
	}

}
