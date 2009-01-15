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

/**
 * Constants for built-in actions. The constant names are also used in the
 * configuration.
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
public final class StandardActions {

    /**
     * {@link OrderedMergeAction}.
     */
    public static final OrderedMergeAction MERGE = new OrderedMergeAction();
    
    /**
     * {@link ReplaceAction}
     */
    public static final ReplaceAction REPLACE = new ReplaceAction();
    
    /**
     * {@link OverrideAction}
     */
    public static final OverrideAction OVERRIDE = new OverrideAction();
    
    /**
     * {@link CompleteAction}
     */
    public static final CompleteAction COMPLETE = new CompleteAction();
    
    /**
     * {@link DeleteAction}
     */
    public static final DeleteAction DELETE = new DeleteAction();
    
    /**
     * {@link PreserveAction}
     */
    public static final PreserveAction PRESERVE = new PreserveAction();
    
    /**
     * {@link InsertAction}
     */
    public static final InsertAction INSERT = new InsertAction();
    
    /**
     * {@link DtdInsertAction}
     */
    //public static final DtdInsertAction DTD = new DtdInsertAction();
    
}
