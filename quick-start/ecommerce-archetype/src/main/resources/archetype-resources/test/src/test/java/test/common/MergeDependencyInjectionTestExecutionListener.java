#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.common;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.${artifactId}.context.TestContext;
import org.springframework.${artifactId}.context.support.DependencyInjectionTestExecutionListener;

public class MergeDependencyInjectionTestExecutionListener extends DependencyInjectionTestExecutionListener {

	@Override
	protected void injectDependencies(TestContext ${artifactId}Context) throws Exception {
		MergeClassPathXMLApplicationContext context = BaseTest.getContext();
		Object bean = ${artifactId}Context.getTestInstance();
		AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
		beanFactory.autowireBeanProperties(bean, AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT, true);
		beanFactory.initializeBean(bean, ${artifactId}Context.getTestClass().getName());
		${artifactId}Context.removeAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE);
	}

}
