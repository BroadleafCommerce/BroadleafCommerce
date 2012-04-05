/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLInputFactory;

import com.sun.jersey.core.impl.provider.entity.XMLRootElementProvider;
import com.sun.jersey.json.impl.provider.entity.JSONRootElementProvider;
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
 * This was written in order to support generic lists and entities that Broadleaf happens to know about. This is not
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
public class BroadleafElementProvider implements MessageBodyWriter<Object>, MessageBodyReader<Object> {
	
	private static final Log LOG = LogFactory.getLog(BroadleafElementProvider.class);
	
	@Resource(name="blEntityConfiguration")
	protected EntityConfiguration ec;
	
	@Context
	protected Providers ps;
	
	@Context
	protected Injectable<XMLInputFactory> xif;

    @Context
    protected Injectable<SAXParserFactory> spf;
	
	protected static JSONListElementProvider.App jsonListProvider;
	protected static XMLListElementProvider.App xmlListProvider;
    protected static XMLRootElementProvider.App xmlRootElementProvider;
    protected static JSONRootElementProvider.App jsonRootElementProvider;
	
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

        if (xmlRootElementProvider == null) {
            xmlRootElementProvider = new XMLRootElementProvider.App(spf, ps);
        }

        if (jsonRootElementProvider == null) {
            jsonRootElementProvider = new JSONRootElementProvider.App(ps);
        }
    	
    	if (mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            if (Collection.class.isAssignableFrom(type)) {
    		    jsonListProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
            } else {
                jsonRootElementProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
            }
    	} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE)) {
            if (Collection.class.isAssignableFrom(type)) {
	    	    xmlListProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
            } else {
                xmlRootElementProvider.writeTo(t, type, genericType, annotations, mediaType, httpHeaders, entityStream);
            }
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
            if (Collection.class.isAssignableFrom(type)){
    		    return jsonListProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
            } else {
                return jsonRootElementProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
            }
    	} else if (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE)) {
            if (Collection.class.isAssignableFrom(type)){
	    	    return xmlListProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
            } else {
                return xmlRootElementProvider.readFrom(type, genericType, annotations, mediaType, httpHeaders, entityStream);
            }
    	}
	    
	    return null;
	}
    
    /**
     * If this is a collection, look up the interface from the Broadleaf EntityConfiguration to determine
     * if it should actually be serialized
     */
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation annotations[], MediaType mediaType) {
        
        if (! mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE)
                && ! mediaType.isCompatible(MediaType.TEXT_XML_TYPE)
                && ! mediaType.isCompatible(MediaType.APPLICATION_JSON_TYPE)) {
            return false;
        }
        
	    if (Collection.class.isAssignableFrom(type)) {
            //Look up what's parameterized in the Collection
	        final ParameterizedType pt = (ParameterizedType)genericType;
	        if (pt.getActualTypeArguments().length > 1) {
                if (LOG.isInfoEnabled()) {
	        	    LOG.info("A collection with multiple parameterized types is unsupported");
                }
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
                    if (LOG.isInfoEnabled()) {
	        		    LOG.info(broadleafEntityClass.getName() + " is not annotated with JAXB annotations, skipping serialization");
                    }
	        		return false;
	        	}
	        } catch (NoSuchBeanDefinitionException e) {
                if (LOG.isDebugEnabled()) {
	        	    LOG.debug("Could not find a mapping for " + ((Class<?>)ta).getName());
                }
	        }
	    } else {
            try {
                Class<?> broadleafEntityClass = ec.lookupEntityClass(type.getName());
                if (broadleafEntityClass != null &&
                        (broadleafEntityClass.isAnnotationPresent(XmlRootElement.class) || broadleafEntityClass.isAnnotationPresent(XmlType.class))
                        ) {
                    return true;
                } else {
                    if (LOG.isInfoEnabled()) {
                        LOG.info(broadleafEntityClass.getName() + " is not annotated with JAXB annotations, skipping serialization");
                    }
                    return false;
                }
            } catch (NoSuchBeanDefinitionException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Could not find a mapping for " + type.getName());
                }
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
