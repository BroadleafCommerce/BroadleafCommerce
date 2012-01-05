#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.${artifactId}.common;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.broadleafcommerce.common.extensibility.context.MergeClassPathXMLApplicationContext;
import org.broadleafcommerce.common.extensibility.context.StandardConfigLocations;
import org.springframework.${artifactId}.context.TestExecutionListeners;
import org.springframework.${artifactId}.context.support.DirtiesContextTestExecutionListener;
import org.springframework.${artifactId}.context.${artifactId}ng.AbstractTestNGSpringContextTests;
import org.springframework.${artifactId}.context.transaction.TransactionConfiguration;

@TransactionConfiguration(transactionManager = "blTransactionManager", defaultRollback = true)
@TestExecutionListeners(inheritListeners = false, value = {MergeDependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class, MergeTransactionalTestExecutionListener.class})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

	private static MergeClassPathXMLApplicationContext mergeContext = null;
	
	public static MergeClassPathXMLApplicationContext getContext() {
		try {
			if (mergeContext == null) {
				String[] contexts = StandardConfigLocations.retrieveAll(StandardConfigLocations.SERVICECONTEXTTYPE);
				String[] allContexts = new String[contexts.length + 7];
				String[] tempContexts = new String[] {
					"bl-open-admin-contentClient-applicationContext.xml",
                    "bl-open-admin-contentCreator-applicationContext.xml",
                    "bl-admin-applicationContext.xml",
					"mycompany-applicationContext.xml",
					"mycompany-core-applicationContext-entity.xml",
					"mycompany-applicationContext-${artifactId}-security.xml",
					"mycompany-applicationContext-${artifactId}.xml"
				};
				System.arraycopy(contexts, 0, allContexts, 0, contexts.length);
				System.arraycopy(tempContexts, 0, allContexts, contexts.length, tempContexts.length);
				mergeContext = new MergeClassPathXMLApplicationContext(allContexts, new String[]{});
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return mergeContext;
	}
	
	@PersistenceContext(unitName = "blPU")
    protected EntityManager em;

}
