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
package ch.elca.el4j.services.xmlmergemod;

/**
 * An action merging the contents of the specified elements. The factories for
 * actions to apply to children elements are configurable through this
 * interface.
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
public interface MergeAction extends Action {

    /**
     * Sets the action's mapper factory.
     * @param factory The action's mapper factory
     */
    public void setMapperFactory(OperationFactory factory);

    /**
     * Sets the action's matcher factory.
     * @param factory The action's matcher factory
     */
    public void setMatcherFactory(OperationFactory factory);

    /**
     * Sets the action's action factory.
     * @param factory The action's action factory
     */
    public void setActionFactory(OperationFactory factory);
}
