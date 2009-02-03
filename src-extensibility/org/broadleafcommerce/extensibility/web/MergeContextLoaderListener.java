package org.broadleafcommerce.extensibility.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Bootstrap listener to start up BroadleafCommerce's root {@link MergeWebApplicationContext}.
 * Simply delegates to {@link MergeContextLoader}.
 *
 * <p>This listener should be registered after
 * {@link org.springframework.web.util.Log4jConfigListener}
 * in <code>web.xml</code>, if the latter is used.
 *
 * @author Jeff Fischer
 */
public class MergeContextLoaderListener implements ServletContextListener {

	private MergeContextLoader contextLoader;


	/**
	 * Initialize the root web application context.
	 */
	public void contextInitialized(ServletContextEvent event) {
		this.contextLoader = createContextLoader();
		this.contextLoader.initWebApplicationContext(event.getServletContext());
	}

	/**
	 * Create the ContextLoader to use. Can be overridden in subclasses.
	 * @return the new ContextLoader
	 */
	protected MergeContextLoader createContextLoader() {
		return new MergeContextLoader();
	}

	/**
	 * Return the ContextLoader used by this listener.
	 * @return the current ContextLoader
	 */
	public MergeContextLoader getContextLoader() {
		return this.contextLoader;
	}


	/**
	 * Close the root web application context.
	 */
	public void contextDestroyed(ServletContextEvent event) {
		if (this.contextLoader != null) {
			this.contextLoader.closeWebApplicationContext(event.getServletContext());
		}
	}

}
