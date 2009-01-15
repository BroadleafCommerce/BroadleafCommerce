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

import java.util.LinkedHashMap;
import java.util.Map;

import ch.elca.el4j.services.xmlmergemod.Action;
import ch.elca.el4j.services.xmlmergemod.ConfigurationException;
import ch.elca.el4j.services.xmlmergemod.Configurer;
import ch.elca.el4j.services.xmlmergemod.Mapper;
import ch.elca.el4j.services.xmlmergemod.Matcher;
import ch.elca.el4j.services.xmlmergemod.MergeAction;
import ch.elca.el4j.services.xmlmergemod.XmlMerge;
import ch.elca.el4j.services.xmlmergemod.action.OrderedMergeAction;
import ch.elca.el4j.services.xmlmergemod.action.StandardActions;
import ch.elca.el4j.services.xmlmergemod.factory.OperationResolver;
import ch.elca.el4j.services.xmlmergemod.factory.XPathOperationFactory;
import ch.elca.el4j.services.xmlmergemod.mapper.IdentityMapper;
import ch.elca.el4j.services.xmlmergemod.mapper.StandardMappers;
import ch.elca.el4j.services.xmlmergemod.matcher.StandardMatchers;
import ch.elca.el4j.services.xmlmergemod.matcher.TagMatcher;


/**
 * Superclass for configurers using XPathOperationFactory.
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
public abstract class AbstractXPathConfigurer implements Configurer {

    /**
     * Matcher resolver.
     */
    OperationResolver m_matcherResolver = new OperationResolver(
        StandardMatchers.class);

    /**
     * Action resolver.
     */
    OperationResolver m_actionResolver = new OperationResolver(
        StandardActions.class);

    /**
     * Mapper resolver.
     */
    OperationResolver m_mapperResolver = new OperationResolver(
        StandardMappers.class);
    
    /**
     * Root merge action.
     */
    MergeAction m_rootMergeAction = new OrderedMergeAction();
    
    /**
     * Default matcher.
     */
    Matcher m_defaultMatcher = new TagMatcher();

    /**
     * Default mapper.
     */
    Mapper m_defaultMapper = new IdentityMapper();

    /**
     * Default action.
     */
    Action m_defaultAction = new OrderedMergeAction();

    /**
     * Map associating XPath expressions with matchers.
     */
    Map m_matchers = new LinkedHashMap();

    /**
     * Map associating XPath expressions with actions.
     */
    Map m_actions = new LinkedHashMap();

    /**
     * Map associating XPath expressions with mappers.
     */
    Map m_mappers = new LinkedHashMap();

    /**
     * Sets the configurer's default matcher.
     * 
     * @param matcherName
     *            The name of the default matcher
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void setDefaultMatcher(String matcherName)
        throws ConfigurationException {
        m_defaultMatcher = (Matcher) m_matcherResolver.resolve(matcherName);
    }   
    
    /**
     * Sets the configurer's default mapper.
     * 
     * @param mapperName
     *            The name of the default mapper
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void setDefaultMapper(String mapperName)
        throws ConfigurationException {
        m_defaultMapper = (Mapper) m_mapperResolver.resolve(mapperName);
    }  
    
    /**
     * Sets the configurer's default action.
     * 
     * @param actionName
     *            The name of the default action
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void setDefaultAction(String actionName)
        throws ConfigurationException {
        m_defaultAction = (Action) m_actionResolver.resolve(actionName);
    }
    
    /**
     * Sets the configurer's root merge action.
     * 
     * @param actionName
     *            The name of the root merge action
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void setRootMergeAction(String actionName)
        throws ConfigurationException {
        m_rootMergeAction = (MergeAction) m_actionResolver.resolve(actionName);
    }

    /**
     * Adds a matcher for a given XPath expression.
     * 
     * @param xPath
     *            An XPath expression
     * @param matcherName
     *            The name of the matcher to add
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void addMatcher(String xPath, String matcherName)
        throws ConfigurationException {
        m_matchers.put(xPath, (Matcher) m_matcherResolver.resolve(matcherName));
    }

    /**
     * Adds an action for a given XPath expression.
     * 
     * @param xPath
     *            An XPath expression
     * @param actionName
     *            The name of the action to add
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void addAction(String xPath, String actionName)
        throws ConfigurationException {
        m_actions.put(xPath, (Action) m_actionResolver.resolve(actionName));
    }

    /**
     * Adds an mapper for a given XPath expression.
     * 
     * @param xPath
     *            An XPath expression
     * @param mapperName
     *            The name of the mapper to add
     * @throws ConfigurationException
     *             If an error occurred during configuration
     */
    protected final void addMapper(String xPath, String mapperName)
        throws ConfigurationException {
        m_mappers.put(xPath, (Mapper) m_mapperResolver.resolve(mapperName));
    }

    /**
     * {@inheritDoc}
     */
    public final void configure(XmlMerge xmlMerge)
        throws ConfigurationException {
        readConfiguration();

        XPathOperationFactory matcherFactory = new XPathOperationFactory();
        matcherFactory.setDefaultOperation(m_defaultMatcher);
        matcherFactory.setOperationMap(m_matchers);
        m_rootMergeAction.setMatcherFactory(matcherFactory);

        XPathOperationFactory mapperFactory = new XPathOperationFactory();
        mapperFactory.setDefaultOperation(m_defaultMapper);
        mapperFactory.setOperationMap(m_mappers);
        m_rootMergeAction.setMapperFactory(mapperFactory);

        XPathOperationFactory actionFactory = new XPathOperationFactory();
        actionFactory.setDefaultOperation(m_defaultAction);
        actionFactory.setOperationMap(m_actions);
        m_rootMergeAction.setActionFactory(actionFactory);

        xmlMerge.setRootMergeAction(m_rootMergeAction);
    }

    /**
     * Reads the configuration used to configure an XmlMerge.
     * 
     * @throws ConfigurationException
     *             If an error occurred during the read
     */
    protected abstract void readConfiguration() throws ConfigurationException;

    /**
     * Sets the configurer's action resolver.
     * 
     * @param actionResolver
     *            The action resolver to set
     */
    public void setActionResolver(OperationResolver actionResolver) {
        this.m_actionResolver = actionResolver;
    }

    /**
     * Sets the configurer's mapper resolver.
     * 
     * @param mapperResolver
     *            The mapper resolver to set
     */
    public void setMapperResolver(OperationResolver mapperResolver) {
        this.m_mapperResolver = mapperResolver;
    }

    /**
     * Sets the configurer's matcher resolver.
     * 
     * @param matcherResolver
     *            the matcher resolver to set
     */
    public void setMatcherResolver(OperationResolver matcherResolver) {
        this.m_matcherResolver = matcherResolver;
    }

}
