package org.broadleafcommerce.core.web.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.stream.XMLInputFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.stereotype.Component;

import com.sun.jersey.core.impl.provider.entity.XMLListElementProvider;
import com.sun.jersey.json.impl.provider.entity.JSONListElementProvider;
import com.sun.jersey.spi.inject.Injectable;

/**
 * <p>
 * This was written in order to support generic lists that Broadleaf happens to know about. This is not
 * supported by default by Jersey because the isWriteable/isReadable methods only take in Classes and Types
 * as parameters. Default ...ListElementProviders will then check these types, get the parameterized class,
 * then inspect that to see if there is an @XMLRootElement or @XMLType on that class. If not, it will fail.
 * </p>
 * <p>
 * This proves to be problematic for the Broadleaf domain that relies on interfaces for extension. For instance,
 * to obtain a list of subcategories for a category, one would make the following call:
 * </p>
 * 
 * <code>List&lt;Category&gt; categories = categoryService.findAllSubCategories(superCategory);</code>
 * 
 * <p>
 * The parameterized type of the List is Category (specifically, org.broadleafcommerce.catalog.domain.Category). Since
 * none of the domain interfaces are annotated with JAXB annotations, the default Jersey List providers will decide that they
 * cannot handle it. However, via the EntityConfiguration we can look up exactly what implementation corresponds to the
 * parameterized domain interface and determine if that is annotated with proper JAXB annotations for serialization.
 * The default providers can then handle the actual serialization of the List safely since the correct implementations
 * have been added to the JAXBContext via the {@link BroadleafContextResolver}
 * </p>
 * 
 * @author phillipverheyden
 * @see com.sun.jersey.json.impl.provider.entity.JSONListElementProvider
 * @see com.sun.jersey.core.impl.provider.entity.XMLListElementProvider
 *
 */
@Component
@Provider
@Produces(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
@Consumes(value={MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_XML})
public class BroadleafListElementProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
	
	private static final Log LOG = LogFactory.getLog(BroadleafListElementProvider.class);
	
	@Resource(name="blEntityConfiguration")
	protected EntityConfiguration ec;
	
	@Context
	protected Providers ps;
	
	@Context
	protected Injectable<XMLInputFactory> xif;
	
	protected static JSONListElementProvider.App jsonListProvider;
	protected static XMLListElementProvider.App xmlListProvider;
	
    @Override
    public final void writeTo(
            Object t,
            Class<?> type,
            Type genericType,
            Annotation annotations[],
            MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException {
    	
    	if (jsonListProvider == null) {
	    	jsonListProvider = new JSONListElementProvider.App(ps);
	    }
	    
	    if (xmlListProvider == null) {
	    	xmlListProvider = new XMLListElementProvider.App(xif, ps);
	    }
    	
    	if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
    		jsonListProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    	} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE)) { 	
	    	xmlListProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    	}
    }
    
	@Override
	public Object readFrom(Class<Object> type, Type genericType,
			Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		
		if (jsonListProvider == null) {
	    	jsonListProvider = new JSONListElementProvider.App(ps);
	    }
	    
	    if (xmlListProvider == null) {
	    	xmlListProvider = new XMLListElementProvider.App(xif, ps);
	    }
	    
	    if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
    		return jsonListProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    	} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE)) { 	
	    	return xmlListProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
    	}
	    
	    return null;
	}
    
    /**
     * If this is a collection, look up the interface from the Broadleaf EntityConfiguration to determine
     * if it should actually be serialized
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[], MediaType mediaType) {
	    if (Collection.class.isAssignableFrom(type)) {
	    	//Look up what's parameterized in the Collection
	        final ParameterizedType pt = (ParameterizedType)genericType;
	        if (pt.getActualTypeArguments().length > 1) {
	        	LOG.info("A collection with multiple parameterized types is unsupported");
	        	return false;
	        }
	        final Type ta = pt.getActualTypeArguments()[0];
	    	
	        try {
	        	//Based on the parameterized type of the Collection, see if it's something Broadleaf knows about and has XML annotations
	        	Class<?> broadleafEntityClass = ec.lookupEntityClass(((Class<?>)ta).getName());
	        	if (broadleafEntityClass != null && 
	        			(broadleafEntityClass.isAnnotationPresent(XmlRootElement.class) || broadleafEntityClass.isAnnotationPresent(XmlType.class))
	        			) {
	        		return true;
	        	} else {
	        		LOG.info(broadleafEntityClass.getName() + " is not annotated with JAXB annotations, skipping serialization");
	        		return false;
	        	}
	        } catch (NoSuchBeanDefinitionException e) {
	        	LOG.debug("Could not find a mapping for " + ((Class<?>)ta).getName());
	        }
	    }
	    
	    return false;
    }
    
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return isWriteable(type, genericType, annotations, mediaType);
	}
    
	@Override
	public long getSize(Object t, Class<?> type, Type genericType,
			Annotation[] annotations, MediaType mediaType) {
		return -1;
	}

}
