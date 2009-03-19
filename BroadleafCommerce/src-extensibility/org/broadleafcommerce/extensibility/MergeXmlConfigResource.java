package org.broadleafcommerce.extensibility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.extensibility.context.merge.MergeManager;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeException;
import org.broadleafcommerce.extensibility.context.merge.exceptions.MergeManagerSetupException;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

public class MergeXmlConfigResource {
	
	private static final Log LOG = LogFactory.getLog(MergeXmlConfigResource.class);
	
	public Resource getMergedConfigResource(InputStream[] sources) throws BeansException {
		Resource configResource = null;
		InputStream merged = null;
		try {
			merged = merge(sources);
			
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
			configResource = new ByteArrayResource(baos.toByteArray());
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Merged config: \n" + serialize(configResource));
			}
		} catch (MergeException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} catch (MergeManagerSetupException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} catch (IOException e) {
			throw new FatalBeanException("Unable to merge source and patch locations", e);
		} finally {
			if (merged != null) {
				try{ merged.close(); } catch (Throwable e) {}
			}
		}
		
		return configResource;
	}

	protected InputStream merge(InputStream[] sources) throws MergeException, MergeManagerSetupException {
		if (sources.length == 1) return sources[0];
		
		InputStream response = null;
		InputStream[] pair = new InputStream[2];
		pair[0] = sources[0];
		for (int j=1;j<sources.length;j++){
			pair[1] = sources[j];
			response = mergeItems(pair);
			try{
				pair[0].close();
			} catch (Throwable e) {}
			try{
				pair[1].close();
			} catch (Throwable e) {}
			pair[0] = response;
		}
		
		return response;
	}
	
	protected InputStream mergeItems(InputStream[] sourceLocations) throws MergeException, MergeManagerSetupException {
		InputStream response = new MergeManager().merge(sourceLocations[0], sourceLocations[1]);
		
		return response;
	}
	
	public String serialize(Resource resource) {
		String response = "";
		try {
			response = serialize(resource.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response;
	}
	
	public String serialize(InputStream in) {
		InputStreamReader reader = null;
		int temp;
		StringBuffer item = new StringBuffer();
		boolean eof = false;
		try {
			reader = new InputStreamReader(in);
			while (!eof) {
				temp = reader.read();
				if (temp == -1) {
					eof = true;
				} else {
					item.append((char) temp);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try{ reader.close(); } catch (Throwable e) {}
			}
		}
		
		return item.toString();
	}
	
	protected byte[] buildArrayFromStream(InputStream source) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		boolean eof = false;
		try{ 
			while (!eof) {
				int temp = source.read();
				if (temp == -1) {
					eof = true;
				} else {
					baos.write(temp);
				}
			}
		} finally {
			try{ source.close(); } catch (Throwable e) {}
		}
		
		return baos.toByteArray();
	}
}
