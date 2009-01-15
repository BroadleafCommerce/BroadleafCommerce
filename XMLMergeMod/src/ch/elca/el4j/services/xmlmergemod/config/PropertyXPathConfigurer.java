/*
 * EL4J, the Extension Library for the J2EE, adds incremental enhancements to
 * the spring framework, http://el4j.sf.net
 * Copyright (C) 2006 by ELCA Informatique SA, Av. de la Harpe 22-24,
 * 1000 Lausanne, Switzerland, http://www.elca.ch
 *
 * EL4J is published under the GNU Lesser General Public License (LGPL)
 * Version 2.1. See http://www.gnu.org/licenses/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * For alternative licensing, please contact info@elca.ch
 */
package ch.elca.el4j.services.xmlmergemod.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import ch.elca.el4j.services.xmlmergemod.ConfigurationException;

/**
 * Reads the {@link ch.elca.el4j.services.xmlmergemod.factory.xmlmerge.factory.XPathOperationFactory}
 * configuration from a property file or a map.
 *
 * <script type="text/javascript">printFileStatus
 *   ("$URL$",
 *    "$Revision$",
 *    "$Date$",
 *    "$Author$"
 * );</script>
 * 
 * @author Laurent Bovet (LBO)
 * @author Alex Mathey (AMA)
 */
public class PropertyXPathConfigurer extends AbstractXPathConfigurer {

    /**
     * Default action pathname.
     */
    public static final String DEFAULT_ACTION_KEY = "action.default";

    /**
     * Default mapper pathname.
     */
    public static final String DEFAULT_MAPPER_KEY = "mapper.default";

    /**
     * Default matcher pathname.
     */
    public static final String DEFAULT_MATCHER_KEY = "matcher.default";

    /**
     * XPath pathname prefix.
     */
    public static final String PATH_PREFIX = "xpath.";

    /**
     * Mapper pathname prefix.
     */
    public static final String MAPPER_PREFIX = "mapper.";

    /**
     * Matcher pathname prefix.
     */
    public static final String MATCHER_PREFIX = "matcher.";

    /**
     * Action pathname prefix.
     */
    public static final String ACTION_PREFIX = "action.";
    
    /**
     * Configuration properties.
     */
    Properties m_props;

    /**
     * Set of XPath paths.
     */
    Set m_paths = new LinkedHashSet();

    /**
     * Creates a PropertyXPathConfigurer which reads the configuration from a
     * properties file.
     * 
     * @param propString
     *            A string representing the name of a properties file
     * @throws ConfigurationException
     *             If an error occurred during the creation of the
     *             configurer
     */
    public PropertyXPathConfigurer(String propString)
        throws ConfigurationException {
        m_props = new Properties();
        try {
            m_props.load(new ByteArrayInputStream(propString.getBytes()));
        } catch (IOException ioe) {
            // Should not happen
            throw new ConfigurationException(ioe);
        }
    }

    /**
     * Creates a PropertyXPathConfigurer which reads the configuration from a
     * map.
     * 
     * @param map
     *            A map containing configuration properties
     */
    public PropertyXPathConfigurer(Map map) {
        m_props = new Properties();
        m_props.putAll(map);
    }

    /**
     * Creates a PropertyXPathConfigurer which reads the configuration from a
     * <code>Properties</code> object.
     * 
     * @param properties
     *            The configuration properties
     */
    public PropertyXPathConfigurer(Properties properties) {
        m_props = properties;
    }

    /**
     * {@inheritDoc}
     */
    protected void readConfiguration() throws ConfigurationException {
        String token;

        token = m_props.getProperty(DEFAULT_ACTION_KEY);
        if (token != null) {
            setDefaultAction(token);
        }

        token = m_props.getProperty(DEFAULT_MAPPER_KEY);
        if (token != null) {
            setDefaultMapper(token);
        }

        token = m_props.getProperty(DEFAULT_MATCHER_KEY);
        if (token != null) {
            setDefaultMatcher(token);
        }

        Enumeration keys = m_props.keys();

        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();

            if (key.startsWith(PATH_PREFIX)) {
                m_paths.add(key.substring(PATH_PREFIX.length()));
            }
        }

        Iterator it = m_paths.iterator();
        while (it.hasNext()) {
            String path = (String) it.next();

            token = m_props.getProperty(ACTION_PREFIX + path);
            if (token != null) {
                addAction(m_props.getProperty(PATH_PREFIX + path), token);
            }
            token = m_props.getProperty(MAPPER_PREFIX + path);
            if (token != null) {
                addMapper(m_props.getProperty(PATH_PREFIX + path), token);
            }
            token = m_props.getProperty(MATCHER_PREFIX + path);
            if (token != null) {
                addMatcher(m_props.getProperty(PATH_PREFIX + path), token);
            }
        }

    }

}
