/*
 * #%L
 * BroadleafCommerce Integration
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.broadleafcommerce.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.test.context.transaction.TransactionConfigurationAttributes;
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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <code>TestExecutionListener</code> which provides support for executing
 * tests within transactions by using
 * {@link org.springframework.transaction.annotation.Transactional @Transactional}
 * and {@link NotTransactional @NotTransactional} annotations.
 * </p>
 * <p>
 * Changes to the database during a test run with &#064;Transactional will be
 * run within a transaction that will, by default, be automatically
 * <entityManager>rolled back</entityManager> after completion of the test; whereas, changes to the
 * database during a test run with &#064;NotTransactional will <strong>not</strong>
 * be run within a transaction. Similarly, test methods that are not annotated
 * with either &#064;Transactional (at the class or method level) or
 * &#064;NotTransactional will not be run within a transaction.
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
 * When executing transactional tests, it is sometimes useful to be able execute
 * certain <entityManager>set up</entityManager> or <entityManager>tear down</entityManager> code outside of a
 * transaction. <code>TransactionalTestExecutionListener</code> provides such
 * support for methods annotated with
 * {@link BeforeTransaction @BeforeTransaction} and
 * {@link AfterTransaction @AfterTransaction}.
 * </p>
 * <p>
 * This implementation will only wrap those test methods that are explicitly annotated
 * with the Transactional annotation.
 * </p>
 *
 * @author Jeff Fischer
 * @author Sam Brannen
 * @author Juergen Hoeller
 * @see TransactionConfiguration
 * @see org.springframework.transaction.annotation.Transactional
 * @see org.springframework.test.annotation.NotTransactional
 * @see org.springframework.test.annotation.Rollback
 * @see BeforeTransaction
 * @see AfterTransaction
 */
@SuppressWarnings("deprecation")
public class MergeTransactionalTestExecutionListener extends AbstractTestExecutionListener {

    private static final Log logger = LogFactory.getLog(MergeTransactionalTestExecutionListener.class);

    protected final TransactionAttributeSource attributeSource = new AnnotationTransactionAttributeSource();

    private TransactionConfigurationAttributes configAttributes;

    private volatile int transactionsStarted = 0;

    private final Map<Method, TransactionContext> transactionContextCache =
            Collections.synchronizedMap(new IdentityHashMap<Method, TransactionContext>());


    /**
     * If the test method of the supplied {@link TestContext test context} is
     * configured to run within a transaction, this method will run
     * {@link BeforeTransaction @BeforeTransaction methods} and start a new
     * transaction.
     * <p>Note that if a {@link BeforeTransaction @BeforeTransaction method} fails,
     * remaining {@link BeforeTransaction @BeforeTransaction methods} will not
     * be invoked, and a transaction will not be started.
     * @see org.springframework.transaction.annotation.Transactional
     * @see org.springframework.test.annotation.NotTransactional
     */
    @SuppressWarnings("serial")
    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        final Method testMethod = testContext.getTestMethod();
        Assert.notNull(testMethod, "The test method of the supplied TestContext must not be null");

        if (this.transactionContextCache.remove(testMethod) != null) {
            throw new IllegalStateException("Cannot start new transaction without ending existing transaction: " +
                    "Invoke endTransaction() before startNewTransaction().");
        }

        if (!testMethod.isAnnotationPresent(Transactional.class)) {
            return;
        }

        TransactionAttribute transactionAttribute =
                this.attributeSource.getTransactionAttribute(testMethod, testContext.getTestClass());
        TransactionDefinition transactionDefinition = null;
        if (transactionAttribute != null) {
            transactionDefinition = new DelegatingTransactionAttribute(transactionAttribute) {
                public String getName() {
                    return testMethod.getName();
                }
            };
        }

        if (transactionDefinition != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("Explicit transaction definition [" + transactionDefinition +
                        "] found for test context [" + testContext + "]");
            }
            TransactionContext txContext =
                    new TransactionContext(getTransactionManager(testContext), transactionDefinition);
            runBeforeTransactionMethods(testContext);
            startNewTransaction(testContext, txContext);
            this.transactionContextCache.put(testMethod, txContext);
        }
    }

    /**
     * If a transaction is currently active for the test method of the supplied
     * {@link TestContext test context}, this method will end the transaction
     * and run {@link AfterTransaction @AfterTransaction methods}.
     * <p>{@link AfterTransaction @AfterTransaction methods} are guaranteed to be
     * invoked even if an error occurs while ending the transaction.
     */
    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        Method testMethod = testContext.getTestMethod();
        Assert.notNull(testMethod, "The test method of the supplied TestContext must not be null");

        // If the transaction is still active...
        TransactionContext txContext = this.transactionContextCache.remove(testMethod);
        if (txContext != null && !txContext.transactionStatus.isCompleted()) {
            try {
                endTransaction(testContext, txContext);
            }
            finally {
                runAfterTransactionMethods(testContext);
            }
        }
    }

    /**
     * Run all {@link BeforeTransaction @BeforeTransaction methods} for the
     * specified {@link TestContext test context}. If one of the methods fails,
     * however, the caught exception will be rethrown in a wrapped
     * {@link RuntimeException}, and the remaining methods will <strong>not</strong>
     * be given a chance to execute.
     * @param testContext the current test context
     */
    protected void runBeforeTransactionMethods(TestContext testContext) throws Exception {
        try {
            List<Method> methods = getAnnotatedMethods(testContext.getTestClass(), BeforeTransaction.class);
            Collections.reverse(methods);
            for (Method method : methods) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing @BeforeTransaction method [" + method + "] for test context ["
                            + testContext + "]");
                }
                method.invoke(testContext.getTestInstance());
            }
        }
        catch (InvocationTargetException ex) {
            logger.error("Exception encountered while executing @BeforeTransaction methods for test context ["
                    + testContext + "]", ex.getTargetException());
            ReflectionUtils.rethrowException(ex.getTargetException());
        }
    }

    /**
     * Run all {@link AfterTransaction @AfterTransaction methods} for the
     * specified {@link TestContext test context}. If one of the methods fails,
     * the caught exception will be logged as an error, and the remaining
     * methods will be given a chance to execute. After all methods have
     * executed, the first caught exception, if any, will be rethrown.
     * @param testContext the current test context
     */
    protected void runAfterTransactionMethods(TestContext testContext) throws Exception {
        Throwable afterTransactionException = null;

        List<Method> methods = getAnnotatedMethods(testContext.getTestClass(), AfterTransaction.class);
        for (Method method : methods) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Executing @AfterTransaction method [" + method + "] for test context [" +
                            testContext + "]");
                }
                method.invoke(testContext.getTestInstance());
            }
            catch (InvocationTargetException ex) {
                Throwable targetException = ex.getTargetException();
                if (afterTransactionException == null) {
                    afterTransactionException = targetException;
                }
                logger.error("Exception encountered while executing @AfterTransaction method [" + method +
                        "] for test context [" + testContext + "]", targetException);
            }
            catch (Exception ex) {
                if (afterTransactionException == null) {
                    afterTransactionException = ex;
                }
                logger.error("Exception encountered while executing @AfterTransaction method [" + method +
                        "] for test context [" + testContext + "]", ex);
            }
        }

        if (afterTransactionException != null) {
            ReflectionUtils.rethrowException(afterTransactionException);
        }
    }

    /**
     * Start a new transaction for the supplied {@link TestContext test context}.
     * <p>Only call this method if {@link #endTransaction} has been called or if no
     * transaction has been previously started.
     * @param testContext the current test context
     * @throws TransactionException if starting the transaction fails
     * @throws Exception if an error occurs while retrieving the transaction manager
     */
    private void startNewTransaction(TestContext testContext, TransactionContext txContext) throws Exception {
        txContext.startTransaction();
        ++this.transactionsStarted;
        if (logger.isInfoEnabled()) {
            logger.info("Began transaction (" + this.transactionsStarted + "): transaction manager [" +
                    txContext.transactionManager + "]; rollback [" + isRollback(testContext) + "]");
        }
    }

    /**
     * Immediately force a <entityManager>commit</entityManager> or <entityManager>rollback</entityManager> of the
     * transaction for the supplied {@link TestContext test context}, according
     * to the commit and rollback flags.
     * @param testContext the current test context
     * @throws Exception if an error occurs while retrieving the transaction manager
     */
    private void endTransaction(TestContext testContext, TransactionContext txContext) throws Exception {
        boolean rollback = isRollback(testContext);
        if (logger.isTraceEnabled()) {
            logger.trace("Ending transaction for test context [" + testContext + "]; transaction manager [" +
                    txContext.transactionStatus + "]; rollback [" + rollback + "]");
        }
        txContext.endTransaction(rollback);
        if (logger.isInfoEnabled()) {
            logger.info((rollback ? "Rolled back" : "Committed") +
                    " transaction after test execution for test context [" + testContext + "]");
        }
    }

    /**
     * Get the {@link PlatformTransactionManager transaction manager} to use
     * for the supplied {@link TestContext test context}.
     * @param testContext the test context for which the transaction manager
     * should be retrieved
     * @return the transaction manager to use, or <code>null</code> if not found
     * @throws BeansException if an error occurs while retrieving the transaction manager
     */
    protected final PlatformTransactionManager getTransactionManager(TestContext testContext) {
        if (this.configAttributes == null) {
            this.configAttributes = retrieveTransactionConfigurationAttributes(testContext.getTestClass());
        }
        String transactionManagerName = this.configAttributes.getTransactionManagerName();
        try {
            return (PlatformTransactionManager) BaseTest.getContext().getBean(
                    transactionManagerName, PlatformTransactionManager.class);
        }
        catch (BeansException ex) {
            if (logger.isWarnEnabled()) {
                logger.warn("Caught exception while retrieving transaction manager with bean name [" +
                        transactionManagerName + "] for test context [" + testContext + "]", ex);
            }
            throw ex;
        }
    }

    /**
     * Determine whether or not to rollback transactions by default for the
     * supplied {@link TestContext test context}.
     * @param testContext the test context for which the default rollback flag
     * should be retrieved
     * @return the <entityManager>default rollback</entityManager> flag for the supplied test context
     * @throws Exception if an error occurs while determining the default rollback flag
     */
    protected final boolean isDefaultRollback(TestContext testContext) throws Exception {
        return retrieveTransactionConfigurationAttributes(testContext.getTestClass()).isDefaultRollback();
    }

    /**
     * Determine whether or not to rollback transactions for the supplied
     * {@link TestContext test context} by taking into consideration the
     * {@link #isDefaultRollback(TestContext) default rollback} flag and a
     * possible method-level override via the {@link Rollback} annotation.
     * @param testContext the test context for which the rollback flag
     * should be retrieved
     * @return the <entityManager>rollback</entityManager> flag for the supplied test context
     * @throws Exception if an error occurs while determining the rollback flag
     */
    protected final boolean isRollback(TestContext testContext) throws Exception {
        boolean rollback = isDefaultRollback(testContext);
        Rollback rollbackAnnotation = testContext.getTestMethod().getAnnotation(Rollback.class);
        if (rollbackAnnotation != null) {
            boolean rollbackOverride = rollbackAnnotation.value();
            if (logger.isDebugEnabled()) {
                logger.debug("Method-level @Rollback(" + rollbackOverride + ") overrides default rollback [" + rollback
                        + "] for test context [" + testContext + "]");
            }
            rollback = rollbackOverride;
        }
        else {
            if (logger.isDebugEnabled()) {
                logger.debug("No method-level @Rollback override: using default rollback [" + rollback
                        + "] for test context [" + testContext + "]");
            }
        }
        return rollback;
    }

    /**
     * Gets all superclasses of the supplied {@link Class class}, including the
     * class itself. The ordering of the returned list will begin with the
     * supplied class and continue up the class hierarchy.
     * <p>Note: This code has been borrowed from
     * {@link org.junit.internal.runners.TestClass#getSuperClasses(Class)} and
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
     * {@link org.junit.internal.runners.TestClass#getAnnotatedMethods(Class)}
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
     * {@link org.junit.internal.runners.TestClass#isShadowed(Method,List)}.
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
     * {@link org.junit.internal.runners.TestClass#isShadowed(Method,Method)}.
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
     * @param clazz the Class object corresponding to the test class for which
     * the configuration attributes should be retrieved
     * @return a new TransactionConfigurationAttributes instance
     */
    private TransactionConfigurationAttributes retrieveTransactionConfigurationAttributes(Class<?> clazz) {
        Class<TransactionConfiguration> annotationType = TransactionConfiguration.class;
        TransactionConfiguration config = clazz.getAnnotation(annotationType);
        if (logger.isDebugEnabled()) {
            logger.debug("Retrieved @TransactionConfiguration [" + config + "] for test class [" + clazz + "]");
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
     * Internal context holder for a specific test method.
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
