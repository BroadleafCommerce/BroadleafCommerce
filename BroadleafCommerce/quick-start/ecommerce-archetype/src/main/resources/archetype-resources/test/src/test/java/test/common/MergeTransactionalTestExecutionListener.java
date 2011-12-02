#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.${artifactId}.annotation.NotTransactional;
import org.springframework.${artifactId}.annotation.Rollback;
import org.springframework.${artifactId}.context.TestContext;
import org.springframework.${artifactId}.context.support.AbstractTestExecutionListener;
import org.springframework.${artifactId}.context.transaction.AfterTransaction;
import org.springframework.${artifactId}.context.transaction.BeforeTransaction;
import org.springframework.${artifactId}.context.transaction.TransactionConfiguration;
import org.springframework.${artifactId}.context.transaction.TransactionConfigurationAttributes;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.DelegatingTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * <p>
 * <code>TestExecutionListener</code> which provides support for executing
 * ${artifactId}s within transactions by using
 * {@link org.springframework.transaction.annotation.Transactional @Transactional}
 * and {@link NotTransactional @NotTransactional} annotations.
 * </p>
 * <p>
 * Changes to the database during a ${artifactId} run with &${symbol_pound}064;Transactional will be
 * run within a transaction that will, by default, be automatically
 * <entityManager>rolled back</entityManager> after completion of the ${artifactId}; whereas, changes to the
 * database during a ${artifactId} run with &${symbol_pound}064;NotTransactional will <strong>not</strong>
 * be run within a transaction. Similarly, ${artifactId} methods that are not annotated
 * with either &${symbol_pound}064;Transactional (at the class or method level) or
 * &${symbol_pound}064;NotTransactional will not be run within a transaction.
 * </p>
 * <p>
 * Transactional commit and rollback behavior can be configured via the
 * class-level {@link TransactionConfiguration @TransactionConfiguration} and
 * method-level {@link Rollback @Rollback} annotations.
 * {@link TransactionConfiguration @TransactionConfiguration} also provides
 * configuration of the bean name of the {@link PlatformTransactionManager} that
 * is to be used to drive transactions.
 * </p>
 * <p>
 * When executing transactional ${artifactId}s, it is sometimes useful to be able execute
 * certain <entityManager>set up</entityManager> or <entityManager>tear down</entityManager> code outside of a
 * transaction. <code>TransactionalTestExecutionListener</code> provides such
 * support for methods annotated with
 * {@link BeforeTransaction @BeforeTransaction} and
 * {@link AfterTransaction @AfterTransaction}.
 * </p>
 * <p>
 * This implementation will only wrap those ${artifactId} methods that are explicitly annotated
 * with the Transactional annotation.
 * </p>
 *
 * @author Jeff Fischer
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @see TransactionConfiguration
 * @see org.springframework.transaction.annotation.Transactional
 * @see org.springframework.${artifactId}.annotation.NotTransactional
 * @see org.springframework.${artifactId}.annotation.Rollback
 * @see BeforeTransaction
 * @see AfterTransaction
 */
public class MergeTransactionalTestExecutionListener extends AbstractTestExecutionListener {

	private static final Log logger = LogFactory.getLog(MergeTransactionalTestExecutionListener.class);

	protected final TransactionAttributeSource attributeSource = new AnnotationTransactionAttributeSource();

	private TransactionConfigurationAttributes configAttributes;

	private volatile int transactionsStarted = 0;

	private final Map<Method, TransactionContext> transactionContextCache =
			Collections.synchronizedMap(new IdentityHashMap<Method, TransactionContext>());


	/**
	 * If the ${artifactId} method of the supplied {@link TestContext ${artifactId} context} is
	 * configured to run within a transaction, this method will run
	 * {@link BeforeTransaction @BeforeTransaction methods} and start a new
	 * transaction.
	 * <p>Note that if a {@link BeforeTransaction @BeforeTransaction method} fails,
	 * remaining {@link BeforeTransaction @BeforeTransaction methods} will not
	 * be invoked, and a transaction will not be started.
	 * @see org.springframework.transaction.annotation.Transactional
	 * @see org.springframework.${artifactId}.annotation.NotTransactional
	 */
	@SuppressWarnings("serial")
	@Override
	public void beforeTestMethod(TestContext ${artifactId}Context) throws Exception {
		final Method ${artifactId}Method = ${artifactId}Context.getTestMethod();
		Assert.notNull(${artifactId}Method, "The ${artifactId} method of the supplied TestContext must not be null");

		if (this.transactionContextCache.remove(${artifactId}Method) != null) {
			throw new IllegalStateException("Cannot start new transaction without ending existing transaction: " +
					"Invoke endTransaction() before startNewTransaction().");
		}

		if (!${artifactId}Method.isAnnotationPresent(Transactional.class)) {
			return;
		}

		TransactionAttribute transactionAttribute =
				this.attributeSource.getTransactionAttribute(${artifactId}Method, ${artifactId}Context.getTestClass());
		TransactionDefinition transactionDefinition = null;
		if (transactionAttribute != null) {
			transactionDefinition = new DelegatingTransactionAttribute(transactionAttribute) {
				public String getName() {
					return ${artifactId}Method.getName();
				}
			};
		}

		if (transactionDefinition != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Explicit transaction definition [" + transactionDefinition +
						"] found for ${artifactId} context [" + ${artifactId}Context + "]");
			}
			TransactionContext txContext =
					new TransactionContext(getTransactionManager(${artifactId}Context), transactionDefinition);
			runBeforeTransactionMethods(${artifactId}Context);
			startNewTransaction(${artifactId}Context, txContext);
			this.transactionContextCache.put(${artifactId}Method, txContext);
		}
	}

	/**
	 * If a transaction is currently active for the ${artifactId} method of the supplied
	 * {@link TestContext ${artifactId} context}, this method will end the transaction
	 * and run {@link AfterTransaction @AfterTransaction methods}.
	 * <p>{@link AfterTransaction @AfterTransaction methods} are guaranteed to be
	 * invoked even if an error occurs while ending the transaction.
	 */
	@Override
	public void afterTestMethod(TestContext ${artifactId}Context) throws Exception {
		Method ${artifactId}Method = ${artifactId}Context.getTestMethod();
		Assert.notNull(${artifactId}Method, "The ${artifactId} method of the supplied TestContext must not be null");

		// If the transaction is still active...
		TransactionContext txContext = this.transactionContextCache.remove(${artifactId}Method);
		if (txContext != null && !txContext.transactionStatus.isCompleted()) {
			try {
				endTransaction(${artifactId}Context, txContext);
			}
			finally {
				runAfterTransactionMethods(${artifactId}Context);
			}
		}
	}

	/**
	 * Run all {@link BeforeTransaction @BeforeTransaction methods} for the
	 * specified {@link TestContext ${artifactId} context}. If one of the methods fails,
	 * however, the caught exception will be rethrown in a wrapped
	 * {@link RuntimeException}, and the remaining methods will <strong>not</strong>
	 * be given a chance to execute.
	 * @param ${artifactId}Context the current ${artifactId} context
	 */
	protected void runBeforeTransactionMethods(TestContext ${artifactId}Context) throws Exception {
		try {
			List<Method> methods = getAnnotatedMethods(${artifactId}Context.getTestClass(), BeforeTransaction.class);
			Collections.reverse(methods);
			for (Method method : methods) {
				if (logger.isDebugEnabled()) {
					logger.debug("Executing @BeforeTransaction method [" + method + "] for ${artifactId} context ["
							+ ${artifactId}Context + "]");
				}
				method.invoke(${artifactId}Context.getTestInstance());
			}
		}
		catch (InvocationTargetException ex) {
			logger.error("Exception encountered while executing @BeforeTransaction methods for ${artifactId} context ["
					+ ${artifactId}Context + "]", ex.getTargetException());
			ReflectionUtils.rethrowException(ex.getTargetException());
		}
	}

	/**
	 * Run all {@link AfterTransaction @AfterTransaction methods} for the
	 * specified {@link TestContext ${artifactId} context}. If one of the methods fails,
	 * the caught exception will be logged as an error, and the remaining
	 * methods will be given a chance to execute. After all methods have
	 * executed, the first caught exception, if any, will be rethrown.
	 * @param ${artifactId}Context the current ${artifactId} context
	 */
	protected void runAfterTransactionMethods(TestContext ${artifactId}Context) throws Exception {
		Throwable afterTransactionException = null;

		List<Method> methods = getAnnotatedMethods(${artifactId}Context.getTestClass(), AfterTransaction.class);
		for (Method method : methods) {
			try {
				if (logger.isDebugEnabled()) {
					logger.debug("Executing @AfterTransaction method [" + method + "] for ${artifactId} context [" +
							${artifactId}Context + "]");
				}
				method.invoke(${artifactId}Context.getTestInstance());
			}
			catch (InvocationTargetException ex) {
				Throwable targetException = ex.getTargetException();
				if (afterTransactionException == null) {
					afterTransactionException = targetException;
				}
				logger.error("Exception encountered while executing @AfterTransaction method [" + method +
						"] for ${artifactId} context [" + ${artifactId}Context + "]", targetException);
			}
			catch (Exception ex) {
				if (afterTransactionException == null) {
					afterTransactionException = ex;
				}
				logger.error("Exception encountered while executing @AfterTransaction method [" + method +
						"] for ${artifactId} context [" + ${artifactId}Context + "]", ex);
			}
		}

		if (afterTransactionException != null) {
			ReflectionUtils.rethrowException(afterTransactionException);
		}
	}

	/**
	 * Start a new transaction for the supplied {@link TestContext ${artifactId} context}.
	 * <p>Only call this method if {@link ${symbol_pound}endTransaction} has been called or if no
	 * transaction has been previously started.
	 * @param ${artifactId}Context the current ${artifactId} context
	 * @throws TransactionException if starting the transaction fails
	 * @throws Exception if an error occurs while retrieving the transaction manager
	 */
	private void startNewTransaction(TestContext ${artifactId}Context, TransactionContext txContext) throws Exception {
		txContext.startTransaction();
		++this.transactionsStarted;
		if (logger.isInfoEnabled()) {
			logger.info("Began transaction (" + this.transactionsStarted + "): transaction manager [" +
					txContext.transactionManager + "]; rollback [" + isRollback(${artifactId}Context) + "]");
		}
	}

	/**
	 * Immediately force a <entityManager>commit</entityManager> or <entityManager>rollback</entityManager> of the
	 * transaction for the supplied {@link TestContext ${artifactId} context}, according
	 * to the commit and rollback flags.
	 * @param ${artifactId}Context the current ${artifactId} context
	 * @throws Exception if an error occurs while retrieving the transaction manager
	 */
	private void endTransaction(TestContext ${artifactId}Context, TransactionContext txContext) throws Exception {
		boolean rollback = isRollback(${artifactId}Context);
		if (logger.isTraceEnabled()) {
			logger.trace("Ending transaction for ${artifactId} context [" + ${artifactId}Context + "]; transaction manager [" +
					txContext.transactionStatus + "]; rollback [" + rollback + "]");
		}
		txContext.endTransaction(rollback);
		if (logger.isInfoEnabled()) {
			logger.info((rollback ? "Rolled back" : "Committed") +
					" transaction after ${artifactId} execution for ${artifactId} context [" + ${artifactId}Context + "]");
		}
	}

	/**
	 * Get the {@link PlatformTransactionManager transaction manager} to use
	 * for the supplied {@link TestContext ${artifactId} context}.
	 * @param ${artifactId}Context the ${artifactId} context for which the transaction manager
	 * should be retrieved
	 * @return the transaction manager to use, or <code>null</code> if not found
	 * @throws BeansException if an error occurs while retrieving the transaction manager
	 */
	protected final PlatformTransactionManager getTransactionManager(TestContext ${artifactId}Context) {
		if (this.configAttributes == null) {
			this.configAttributes = retrieveTransactionConfigurationAttributes(${artifactId}Context.getTestClass());
		}
		String transactionManagerName = this.configAttributes.getTransactionManagerName();
		try {
			return (PlatformTransactionManager) BaseTest.getContext().getBean(
					transactionManagerName, PlatformTransactionManager.class);
		}
		catch (BeansException ex) {
			if (logger.isWarnEnabled()) {
				logger.warn("Caught exception while retrieving transaction manager with bean name [" +
						transactionManagerName + "] for ${artifactId} context [" + ${artifactId}Context + "]", ex);
			}
			throw ex;
		}
	}

	/**
	 * Determine whether or not to rollback transactions by default for the
	 * supplied {@link TestContext ${artifactId} context}.
	 * @param ${artifactId}Context the ${artifactId} context for which the default rollback flag
	 * should be retrieved
	 * @return the <entityManager>default rollback</entityManager> flag for the supplied ${artifactId} context
	 * @throws Exception if an error occurs while determining the default rollback flag
	 */
	protected final boolean isDefaultRollback(TestContext ${artifactId}Context) throws Exception {
		return retrieveTransactionConfigurationAttributes(${artifactId}Context.getTestClass()).isDefaultRollback();
	}

	/**
	 * Determine whether or not to rollback transactions for the supplied
	 * {@link TestContext ${artifactId} context} by taking into consideration the
	 * {@link ${symbol_pound}isDefaultRollback(TestContext) default rollback} flag and a
	 * possible method-level override via the {@link Rollback} annotation.
	 * @param ${artifactId}Context the ${artifactId} context for which the rollback flag
	 * should be retrieved
	 * @return the <entityManager>rollback</entityManager> flag for the supplied ${artifactId} context
	 * @throws Exception if an error occurs while determining the rollback flag
	 */
	protected final boolean isRollback(TestContext ${artifactId}Context) throws Exception {
		boolean rollback = isDefaultRollback(${artifactId}Context);
		Rollback rollbackAnnotation = ${artifactId}Context.getTestMethod().getAnnotation(Rollback.class);
		if (rollbackAnnotation != null) {
			boolean rollbackOverride = rollbackAnnotation.value();
			if (logger.isDebugEnabled()) {
				logger.debug("Method-level @Rollback(" + rollbackOverride + ") overrides default rollback [" + rollback
						+ "] for ${artifactId} context [" + ${artifactId}Context + "]");
			}
			rollback = rollbackOverride;
		}
		else {
			if (logger.isDebugEnabled()) {
				logger.debug("No method-level @Rollback override: using default rollback [" + rollback
						+ "] for ${artifactId} context [" + ${artifactId}Context + "]");
			}
		}
		return rollback;
	}

	/**
	 * Gets all superclasses of the supplied {@link Class class}, including the
	 * class itself. The ordering of the returned list will begin with the
	 * supplied class and continue up the class hierarchy.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass${symbol_pound}getSuperClasses(Class)} and
	 * adapted.
	 * @param clazz the class for which to retrieve the superclasses.
	 * @return all superclasses of the supplied class.
	 */
	private List<Class<?>> getSuperClasses(Class<?> clazz) {
		ArrayList<Class<?>> results = new ArrayList<Class<?>>();
		Class<?> current = clazz;
		while (current != null) {
			results.add(current);
			current = current.getSuperclass();
		}
		return results;
	}

	/**
	 * Gets all methods in the supplied {@link Class class} and its superclasses
	 * which are annotated with the supplied <code>annotationType</code> but
	 * which are not <entityManager>shadowed</entityManager> by methods overridden in subclasses.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass${symbol_pound}getAnnotatedMethods(Class)}
	 * and adapted.
	 * @param clazz the class for which to retrieve the annotated methods
	 * @param annotationType the annotation type for which to search
	 * @return all annotated methods in the supplied class and its superclasses
	 */
	private List<Method> getAnnotatedMethods(Class<?> clazz, Class<? extends Annotation> annotationType) {
		List<Method> results = new ArrayList<Method>();
		for (Class<?> eachClass : getSuperClasses(clazz)) {
			Method[] methods = eachClass.getDeclaredMethods();
			for (Method eachMethod : methods) {
				Annotation annotation = eachMethod.getAnnotation(annotationType);
				if (annotation != null && !isShadowed(eachMethod, results)) {
					results.add(eachMethod);
				}
			}
		}
		return results;
	}

	/**
	 * Determines if the supplied {@link Method method} is <entityManager>shadowed</entityManager>
	 * by a method in supplied {@link List list} of previous methods.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass${symbol_pound}isShadowed(Method,List)}.
	 * @param method the method to check for shadowing
	 * @param previousMethods the list of methods which have previously been processed
	 * @return <code>true</code> if the supplied method is shadowed by a
	 * method in the <code>previousMethods</code> list
	 */
	private boolean isShadowed(Method method, List<Method> previousMethods) {
		for (Method each : previousMethods) {
			if (isShadowed(method, each)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines if the supplied {@link Method current method} is
	 * <entityManager>shadowed</entityManager> by a {@link Method previous method}.
	 * <p>Note: This code has been borrowed from
	 * {@link org.junit.internal.runners.TestClass${symbol_pound}isShadowed(Method,Method)}.
	 * @param current the current method
	 * @param previous the previous method
	 * @return <code>true</code> if the previous method shadows the current one
	 */
	private boolean isShadowed(Method current, Method previous) {
		if (!previous.getName().equals(current.getName())) {
			return false;
		}
		if (previous.getParameterTypes().length != current.getParameterTypes().length) {
			return false;
		}
		for (int i = 0; i < previous.getParameterTypes().length; i++) {
			if (!previous.getParameterTypes()[i].equals(current.getParameterTypes()[i])) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Retrieves the {@link TransactionConfigurationAttributes} for the
	 * specified {@link Class class} which may optionally declare or inherit a
	 * {@link TransactionConfiguration @TransactionConfiguration}. If a
	 * {@link TransactionConfiguration} annotation is not present for the
	 * supplied class, the <entityManager>default values</entityManager> for attributes defined in
	 * {@link TransactionConfiguration} will be used instead.
	 * @param clazz the Class object corresponding to the ${artifactId} class for which
	 * the configuration attributes should be retrieved
	 * @return a new TransactionConfigurationAttributes instance
	 */
	private TransactionConfigurationAttributes retrieveTransactionConfigurationAttributes(Class<?> clazz) {
		Class<TransactionConfiguration> annotationType = TransactionConfiguration.class;
		TransactionConfiguration config = clazz.getAnnotation(annotationType);
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieved @TransactionConfiguration [" + config + "] for ${artifactId} class [" + clazz + "]");
		}

		String transactionManagerName;
		boolean defaultRollback;
		if (config != null) {
			transactionManagerName = config.transactionManager();
			defaultRollback = config.defaultRollback();
		}
		else {
			transactionManagerName = (String) AnnotationUtils.getDefaultValue(annotationType, "transactionManager");
			defaultRollback = (Boolean) AnnotationUtils.getDefaultValue(annotationType, "defaultRollback");
		}

		TransactionConfigurationAttributes configAttributes =
				new TransactionConfigurationAttributes(transactionManagerName, defaultRollback);
		if (logger.isDebugEnabled()) {
			logger.debug("Retrieved TransactionConfigurationAttributes [" + configAttributes + "] for class [" + clazz + "]");
		}
		return configAttributes;
	}


	/**
	 * Internal context holder for a specific ${artifactId} method.
	 */
	private static class TransactionContext {

		private final PlatformTransactionManager transactionManager;

		private final TransactionDefinition transactionDefinition;

		private TransactionStatus transactionStatus;

		public TransactionContext(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
			this.transactionManager = transactionManager;
			this.transactionDefinition = transactionDefinition;
		}

		public void startTransaction() {
			this.transactionStatus = this.transactionManager.getTransaction(this.transactionDefinition);
		}

		public void endTransaction(boolean rollback) {
			if (rollback) {
				this.transactionManager.rollback(this.transactionStatus);
			}
			else {
				this.transactionManager.commit(this.transactionStatus);
			}
		}
	}

}
