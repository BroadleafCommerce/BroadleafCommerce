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
package ch.elca.el4j.services.xmlmergemod.action;

import ch.elca.el4j.services.xmlmergemod.MergeAction;
import ch.elca.el4j.services.xmlmergemod.OperationFactory;
import ch.elca.el4j.services.xmlmergemod.factory.StaticOperationFactory;
import ch.elca.el4j.services.xmlmergemod.mapper.IdentityMapper;
import ch.elca.el4j.services.xmlmergemod.matcher.TagMatcher;


/**
 * Gathers the operation factory-related behaviour and a default configuration.
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
public abstract class AbstractMergeAction implements MergeAction {

    /**
     * Action factory.
     */
    protected OperationFactory m_actionFactory = new StaticOperationFactory(
        this);
    
    /**
     * Mapper factory.
     */
    protected OperationFactory m_mapperFactory = new StaticOperationFactory(
        new IdentityMapper());
    /**
     * Matcher factory.
     */
    protected OperationFactory m_matcherFactory = new StaticOperationFactory(
        new TagMatcher());

    /**
     * {@inheritDoc}
     */
    public void setMapperFactory(OperationFactory factory) {
        m_mapperFactory = factory;
    }

    /**
     * {@inheritDoc}
     */
    public void setMatcherFactory(OperationFactory factory) {
        m_matcherFactory = factory;

    }

    /**
     * {@inheritDoc}
     */
    public void setActionFactory(OperationFactory factory) {
        m_actionFactory = factory;
    }

}
