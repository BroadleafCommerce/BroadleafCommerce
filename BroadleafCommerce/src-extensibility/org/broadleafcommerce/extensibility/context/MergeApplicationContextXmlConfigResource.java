package org.broadleafcommerce.extensibility.context;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.MergeXmlConfigResource;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import ch.elca.el4j.services.xmlmergemod.AbstractXmlMergeException;
import ch.elca.el4j.services.xmlmergemod.ConfigurationException;
import ch.elca.el4j.services.xmlmergemod.Configurer;
import ch.elca.el4j.services.xmlmergemod.config.PropertyXPathConfigurer;

public class MergeApplicationContextXmlConfigResource extends MergeXmlConfigResource {

	private static final Log LOG = LogFactory.getLog(MergeApplicationContextXmlConfigResource.class);
	
	/**
	 * Generate a merged configuration resource, loading the definitions from the given streams. Note,
	 * all sourceLocation streams will be merged using standard Spring configuration override rules. However, the patch
	 * streams are fully merged into the result of the sourceLocations simple merge. Patch merges are first executed according
	 * to beans with the same id. Subsequent merges within a bean are executed against tagnames - ignoring any
	 * further id attributes.
	 * 
	 * @param sources array of input streams for the source application context files
	 * @param patches array of input streams for the patch application context files
	 * @throws BeansException
	 */
	public Resource[] getConfigResources(InputStream[] sources, InputStream[] patches) throws BeansException {
		Resource[] configResources = null;
		InputStream merged = null;
		try {
			//Set the first stage for simple replace only - this is the source application context files
			Properties stage1 = new Properties();
			stage1.load(MergeApplicationContextXmlConfigResource.class.getResourceAsStream("MergeApplicationContextXmlConfigResource.1.properties"));
			Configurer configurer = new PropertyXPathConfigurer(stage1);
			merged = merge(sources, configurer);
			
			byte[] mergedArray = buildArrayFromStream(merged);
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Merged Stage1 Sources: \n" + serialize(new ByteArrayInputStream(mergedArray)));
			}
			
			InputStream[] patches2 = new InputStream[patches.length+1];
			patches2[0] = new ByteArrayInputStream(mergedArray);
			System.arraycopy(patches, 0, patches2, 1, patches.length);
			
			//Set the second stage for more complex merge that includes the interiors of matching beans
			Properties stage2 = new Properties();
			stage2.load(MergeApplicationContextXmlConfigResource.class.getResourceAsStream("MergeApplicationContextXmlConfigResource.2.properties"));
			configurer = new PropertyXPathConfigurer(stage2);
			merged = merge(patches2, configurer);
			
			//read the final stream into a byte array
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			boolean eof = false;
			while (!eof) {
				int temp = merged.read();
				if (temp == -1) {
					eof = true;
				} else {
					baos.write(temp);
				}
			}
			configResources = new Resource[]{new ByteArrayResource(baos.toByteArray())};
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Merged ApplicationContext Including Patches: \n" + serialize(configResources[0]));
			}
		} catch (ConfigurationException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} catch (AbstractXmlMergeException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} catch (IOException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} finally {
			if (merged != null) {
				try{ merged.close(); } catch (Throwable e) {}
			}
		}
		
		return configResources;
	}

}
