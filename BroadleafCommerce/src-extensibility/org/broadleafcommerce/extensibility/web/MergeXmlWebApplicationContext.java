package org.broadleafcommerce.extensibility.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.broadleafcommerce.extensibility.context.MergeApplicationContextXmlConfigResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * {@link org.springframework.web.context.WebApplicationContext} implementation
 * which takes its configuration from XML documents, understood by an
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 *
 * <p>By default, the configuration will be taken from "/WEB-INF/applicationContext.xml"
 * for the root context, and "/WEB-INF/test-servlet.xml" for a context with the namespace
 * "test-servlet" (like for a DispatcherServlet instance with the servlet-name "test").
 *
 * <p>The config location defaults can be overridden via the "contextConfigLocation"
 * context-param of {@link org.springframework.web.context.ContextLoader} and servlet
 * init-param of {@link org.springframework.web.servlet.FrameworkServlet}. Config locations
 * can either denote concrete files like "/WEB-INF/context.xml" or Ant-style patterns
 * like "/WEB-INF/*-context.xml" (see {@link org.springframework.util.PathMatcher}
 * javadoc for pattern details).
 *
 * <p>Note: In case of multiple config locations, later bean definitions will
 * override ones defined in earlier loaded files. This can be leveraged to
 * deliberately override certain bean definitions via an extra XML file.
 *
 * <p>In addition to standard configuration, this implementation also takes a list of
 * patch configuration files that are merged into the configuration provided above.
 * {@link org.broadleafcommerce.extensibility.MergeXmlConfigResourceFactory}. The patch
 * file locations are set via the "patchConfigLocation" context-param of
 * {@link org.broadleafcommerce.extensibility.web.MergeContextLoader}. Patch locations
 * can either denote concrete files like "/WEB-INF/patch.xml" or Ant-style patterns
 * like "/WEB-INF/*-context.xml" (see {@link org.springframework.util.pathMatcher}
 * javadoc for pattern details).
 *
 * @author Jeff Fischer
 */
public class MergeXmlWebApplicationContext extends XmlWebApplicationContext {

	private String patchLocation;

	/**
	 * Load the bean definitions with the given XmlBeanDefinitionReader.
	 * <p>The lifecycle of the bean factory is handled by the refreshBeanFactory method;
	 * therefore this method is just supposed to load and/or register bean definitions.
	 * <p>Delegates to a ResourcePatternResolver for resolving location patterns
	 * into Resource instances.
	 * @throws org.springframework.beans.BeansException in case of bean registration errors
	 * @throws java.io.IOException if the required XML document isn't found
	 * @see #refreshBeanFactory
	 * @see #getConfigLocations
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		String[] broadleafConfigLocations = getStandardConfigLocations();
		
		InputStream[] sources = new InputStream[broadleafConfigLocations.length];
		for (int i = 0; i < broadleafConfigLocations.length; i++) {
			sources[i] = MergeXmlWebApplicationContext.class.getClassLoader().getResourceAsStream(broadleafConfigLocations[i]);
		}
		String patchLocation = getPatchLocation();
		String[] patchLocations = StringUtils.tokenizeToStringArray(patchLocation, CONFIG_LOCATION_DELIMITERS);
		InputStream[] patches = new InputStream[patchLocations.length];
		for (int i = 0; i < patchLocations.length; i++) {
			if (patchLocations[i].startsWith("classpath")) {
				patches[i] = MergeXmlWebApplicationContext.class.getClassLoader().getResourceAsStream(patchLocations[i].substring("classpath*:".length(), patchLocations[i].length()));
			} else {
				Resource resource = getResourceByPath(patchLocations[i]);
				patches[i] = resource.getInputStream();
			}
		}
		Resource[] resources = new MergeApplicationContextXmlConfigResource().getConfigResources(sources, patches);

		reader.loadBeanDefinitions(resources);
	}
	
	private String[] getStandardConfigLocations() throws IOException {
		String[] response;
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(MergeXmlWebApplicationContext.class.getResourceAsStream("StandardConfigLocations.txt")));
			ArrayList<String> items = new ArrayList<String>();
			boolean eof = false;
			while (!eof) {
				String temp = reader.readLine();
				if (temp == null) {
					eof = true;
				} else {
					if (!temp.startsWith("#") && temp.trim().length() > 0) {
						items.add(temp.trim());
					}
				}
			}
			response = new String[]{};
			response = items.toArray(response);
		} finally {
			if (reader != null) {
				try{ reader.close(); } catch (Throwable e) {}
			}
		}
		
		return response;
	}

	/**
	 * @return the patchLocation
	 */
	public String getPatchLocation() {
		return patchLocation;
	}

	/**
	 * @param patchLocation the patchLocation to set
	 */
	public void setPatchLocation(String patchLocation) {
		this.patchLocation = patchLocation;
	}

}
