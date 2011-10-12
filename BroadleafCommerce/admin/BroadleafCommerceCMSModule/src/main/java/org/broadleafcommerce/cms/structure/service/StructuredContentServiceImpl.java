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
package org.broadleafcommerce.cms.structure.service;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.map.LRUMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.locale.domain.Locale;
import org.broadleafcommerce.cms.structure.dao.StructuredContentDao;
import org.broadleafcommerce.cms.structure.domain.StructuredContent;
import org.broadleafcommerce.cms.structure.domain.StructuredContentField;
import org.broadleafcommerce.cms.structure.domain.StructuredContentType;
import org.broadleafcommerce.openadmin.server.dao.SandBoxItemDao;
import org.broadleafcommerce.openadmin.server.domain.*;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mvel2.CompileException;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

/**
 * Created by bpolster.
 */
@Service("blStructuredContentService")
public class StructuredContentServiceImpl implements StructuredContentService {
    private static final Log LOG = LogFactory.getLog(StructuredContentServiceImpl.class);

    @Resource(name="blStructuredContentDao")
    protected StructuredContentDao structuredContentDao;

    @Resource(name="blSandBoxItemDao")
    protected SandBoxItemDao sandBoxItemDao;

    private Map<String, Object> structuredContentRuleDTOMap;

    private static final LRUMap EXPRESSION_CACHE = new LRUMap(1000);


    /**
     * Returns the StructuredContent item associated with the passed in id.
     *
     * @param contentId - The id of the content item.
     * @return The associated structured content item.
     */
    @Override
    public StructuredContent findStructuredContentById(Long contentId) {
        return structuredContentDao.findStructuredContentById(contentId);
    }

    @Override
    public StructuredContentType findStructuredContentTypeById(Long id) {
        return structuredContentDao.findStructuredContentTypeById(id);
    }

    @Override
    public StructuredContentType findStructuredContentTypeByName(String name) {
        return structuredContentDao.findStructuredContentTypeByName(name);
    }

    /**
     * Returns the list of structured content types.
     */
    @Override
    public List<StructuredContentType> retrieveAllStructuredContentTypes() {
        return structuredContentDao.retrieveAllStructuredContentTypes();
    }

    /**
     * Returns the fields associated with the passed in contentId.
     * This is preferred over the direct access from the ContentItem so that the
     * two items can be cached distinctly
     *
     * @param contentId - The id of the content.
     * @return Map of fields for this content id
     */
    @Override
    public Map<String, StructuredContentField> findFieldsByContentId(Long contentId) {
        StructuredContent sc = findStructuredContentById(contentId);
        return structuredContentDao.readFieldsForStructuredContentItem(sc);
    }

    /**
     * Retuns content items for the passed in sandbox that match the passed in criteria.
     * <p/>
     * Merges the sandbox content with the production content.
     *
     * @param sandbox      - the sandbox to find structured content items (null indicates items that are in production for
     *                     sites that are single tenant.
     * @return
     */
    @Override
    public List<StructuredContent> findContentItems(SandBox sandbox, Criteria c) {
        c.add(Restrictions.eq("archivedFlag", false));

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            c.add(Restrictions.isNull("sandbox"));
            return c.list();
        } else {
            Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
            Criterion productionSandboxExpression = null;
            if (sandbox.getSite() == null || sandbox.getSite().getProductionSandbox() == null) {
                productionSandboxExpression = Restrictions.isNull("sandbox");
            } else {
                if (!SandBoxType.PRODUCTION.equals(sandbox.getSandBoxType())) {
                    productionSandboxExpression = Restrictions.eq("sandbox", sandbox.getSite().getProductionSandbox());
                }
            }

            if (productionSandboxExpression != null) {
                c.add(Restrictions.or(currentSandboxExpression,productionSandboxExpression));
            } else {
                c.add(currentSandboxExpression);
            }

            List<StructuredContent> resultList = (List<StructuredContent>) c.list();

            // Iterate once to build the map
            LinkedHashMap returnItems = new LinkedHashMap<Long,StructuredContent>();
            for (StructuredContent content : resultList) {
                returnItems.put(content.getId(), content);
            }

            // Iterate to remove items from the final list
            for (StructuredContent content : resultList) {
                if (content.getOriginalItemId() != null) {
                    returnItems.remove(content.getOriginalItemId());
                }

                if (content.getDeletedFlag()) {
                    returnItems.remove(content.getId());
                }
            }
            return new ArrayList<StructuredContent>(returnItems.values());
        }

    }

    /**
     * Returns the count of items that match the passed in criteria.
     *
     * This counts the items in production + the new items in the sandbox - the
     * existing items that have been deleted in the sandbox.
     *
     * @return the count of items in this sandbox that match the passed in Criteria
     */
    @Override
    public Long countContentItems(SandBox sandbox, Criteria c) {
        c.add(Restrictions.eq("archivedFlag", false));
        c.setProjection(Projections.rowCount());

        if (sandbox == null) {
            // Query is hitting the production sandbox.
            c.add(Restrictions.isNull("sandbox"));
            return (Long) c.uniqueResult();
        } else {
            Criterion currentSandboxExpression = Restrictions.eq("sandbox", sandbox);
            Criterion productionSandboxExpression;
            if (sandbox.getSite() == null || sandbox.getSite().getProductionSandbox() == null) {
                productionSandboxExpression = Restrictions.isNull("sandbox");
            } else {
                // Query is hitting the production sandbox.
                if (sandbox.getId().equals(sandbox.getSite().getProductionSandbox().getId())) {
                    return (Long) c.uniqueResult();
                }
                productionSandboxExpression = Restrictions.eq("sandbox", sandbox.getSite().getProductionSandbox());
            }

            c.add(Restrictions.or(currentSandboxExpression,productionSandboxExpression));

            Long resultCount = (Long) c.list().get(0);
            Long updatedCount = 0L;
            Long deletedCount = 0L;

            // count updated items
            c.add(Restrictions.and(Restrictions.isNotNull("originalItemId"),Restrictions.eq("sandbox", sandbox)));
            updatedCount = (Long) c.list().get(0);

            // count deleted items
            c.add(Restrictions.and(Restrictions.eq("deletedFlag", true),Restrictions.eq("sandbox", sandbox)));
            deletedCount = (Long) c.list().get(0);

            return resultCount - updatedCount - deletedCount;
        }
    }

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     * <p/>
     * Adds the passed in contentItem to the DB.
     * <p/>
     * Creates a sandbox/site if one doesn't already exist.
     */
    @Override
    public StructuredContent addStructuredContent(StructuredContent content, SandBox destinationSandbox) {
        content.setSandbox(destinationSandbox);
        content.setArchivedFlag(false);
        content.setDeletedFlag(false);
        StructuredContent sc = structuredContentDao.addOrUpdateContentItem(content, true);
        if (! isProductionSandBox(destinationSandbox)) {
            sandBoxItemDao.addSandBoxItem(destinationSandbox, SandBoxOperationType.ADD, SandBoxItemType.STRUCTURED_CONTENT, sc.getContentName(), sc.getId(), null);
        }
        return sc;
    }

    /**
     * This method is intended to be called from within the CMS
     * admin only.
     * <p/>
     * Updates the structuredContent according to the following rules:
     * <p/>
     * 1.  If sandbox has changed from null to a value
     * This means that the user is editing an item in production and
     * the edit is taking place in a sandbox.
     * <p/>
     * Clone the item and add it to the new sandbox and set the cloned
     * item's originalItemId to the id of the item being updated.
     * <p/>
     * 2.  If the sandbox has changed from one value to another
     * This means that the user is moving the item from one sandbox
     * to another.
     * <p/>
     * Update the siteId for the item to the one associated with the
     * new sandbox
     * <p/>
     * 3.  If the sandbox has changed from a value to null
     * This means that the item is moving from the sandbox to production.
     * <p/>
     * If the item has an originalItemId, then update that item by
     * setting it's archived flag to true.
     * <p/>
     * Then, update the siteId of the item being updated to be the
     * siteId of the original item.
     * <p/>
     * 4.  If the sandbox is the same then just update the item.
     */
    @Override
    public StructuredContent updateStructuredContent(StructuredContent content, SandBox destSandbox) {
        if (content.getLockedFlag()) {
            throw new IllegalArgumentException("Unable to update a locked record");
        }

        if (checkForSandboxMatch(content.getSandbox(), destSandbox)) {
            return structuredContentDao.addOrUpdateContentItem(content, true);
        } else if (checkForProductionSandbox(content.getSandbox())) {
            // Move from production to destSandbox
            content.setLockedFlag(true);
            content = structuredContentDao.addOrUpdateContentItem(content, false);

            StructuredContent clonedContent = content.cloneEntity();
            clonedContent.setOriginalItemId(content.getId());
            clonedContent.setSandbox(destSandbox);
            StructuredContent returnContent = structuredContentDao.addOrUpdateContentItem(clonedContent, true);
            sandBoxItemDao.addSandBoxItem(destSandbox, SandBoxOperationType.UPDATE, SandBoxItemType.STRUCTURED_CONTENT, returnContent.getContentName(), returnContent.getId(), returnContent.getOriginalItemId());
            return returnContent;
        } else {
            // This should happen via a promote, revert, or reject in the sandbox service
            throw new IllegalArgumentException("Update called when promote or reject was expected.");
        }
    }

    // Returns true if the src and dest sandbox are the same.
    private boolean checkForSandboxMatch(SandBox src, SandBox dest) {
        if (src != null) {
            if (dest != null) {
                return src.getId().equals(dest.getId());
            }
        }
        return (src == null && dest == null);
    }

    // Returns true if the dest sandbox is production.
    private boolean checkForProductionSandbox(SandBox dest) {
        boolean productionSandbox = false;

        if (dest == null) {
            productionSandbox = true;
        } else {
            if (dest.getSite() != null && dest.getSite().getProductionSandbox() != null && dest.getSite().getProductionSandbox().getId() != null) {
                productionSandbox = dest.getSite().getProductionSandbox().getId().equals(dest.getId());
            }
        }

        return productionSandbox;
    }

    /**
     * If deleting and item where content.originalItemId != null
     * then the item is deleted from the database.
     * <p/>
     * If the originalItemId is null, then this method marks
     * the items as deleted within the passed in sandbox.
     *
     * @param content
     * @param destinationSandbox
     * @return
     */
    @Override
    public void deleteStructuredContent(StructuredContent content, SandBox destinationSandbox) {
        content.setDeletedFlag(true);
        updateStructuredContent(content, destinationSandbox);
    }


    private List<StructuredContent> mergeContent(List<StructuredContent> productionList, List<StructuredContent> sandboxList) {
        if (sandboxList == null || sandboxList.size() == 0) {
            return productionList;
        }

        Map<Long,StructuredContent> scMap = new LinkedHashMap<Long,StructuredContent>();
        for(StructuredContent sc : productionList) {
            scMap.put(sc.getId(), sc);
        }

        for(StructuredContent sc : sandboxList) {
            if (sc.getOriginalItemId() != null) {
                scMap.remove(sc.getOriginalItemId());
            }

            if (! sc.getDeletedFlag() && ! sc.getOfflineFlag()) {
                scMap.put(sc.getId(), sc);
            }
        }

        ArrayList<StructuredContent> returnList = new ArrayList<StructuredContent>(scMap.values());

        if (returnList.size()  > 1) {
            Collections.sort(returnList, new BeanComparator("priority"));
        }

        return returnList;
    }


    protected Boolean executeExpression(String expression, Map<String, Object> vars) {
        Serializable exp;
        synchronized (EXPRESSION_CACHE) {
            exp = (Serializable) EXPRESSION_CACHE.get(expression);
            if (exp == null) {
                ParserContext context = new ParserContext();
                try {
                    exp = MVEL.compileExpression(expression, context);
                } catch (CompileException ce) {
                    LOG.warn("Compile exception processing phrase: " + expression,ce);
                    return Boolean.FALSE;
                }

                EXPRESSION_CACHE.put(expression, exp);
            }
        }

        return (Boolean) MVEL.executeExpression(exp, vars);
    }

    /**
     * This method loops through the content and orders by priority.   If multiple items have the same priority,
     * it will randomize the order of those results.   IF the item has a display rule, the code will evaluate
     * the rule before and ensure a match before returning.
     *
     * @return
     */
    private List<StructuredContent> evaluateAndPriortizeContent(List<StructuredContent> structuredContentList, int count, Map<String, Object> ruleDTOs) {

        Iterator<StructuredContent> structuredContentIterator = structuredContentList.iterator();
        List<StructuredContent> returnList = new ArrayList<StructuredContent>();
        List<StructuredContent> tmpList = new ArrayList<StructuredContent>();
        Integer lastPriority = Integer.MIN_VALUE;
        while (structuredContentIterator.hasNext()) {
            StructuredContent sc = structuredContentIterator.next();
            if (! lastPriority.equals(sc.getPriority())) {
                // If we've moved to another priority, then shuffle all of the items
                // with the previous priority and add them to the return list.
                if (tmpList.size() > 1) {
                    Collections.shuffle(tmpList);
                }
                returnList.addAll(tmpList);

                tmpList.clear();

                // If we've added enough items to satisfy the count, then return the
                // list.
                if (returnList.size() == count) {
                    return returnList;
                } else if (returnList.size() > count) {
                    return returnList.subList(0, count);
                } else {
                    if (sc.getDisplayRule() != null && ! "".equals(sc.getDisplayRule())) {
                        if (executeExpression(sc.getDisplayRule(), ruleDTOs)) {
                            tmpList.add(sc);
                        }
                    } else {
                        tmpList.add(sc);
                    }
                }
            } else {
                if (sc.getDisplayRule() != null && ! "".equals(sc.getDisplayRule())) {
                    if (executeExpression(sc.getDisplayRule(), ruleDTOs)) {
                        tmpList.add(sc);
                    }
                } else {
                    tmpList.add(sc);
                }
            }
            lastPriority = sc.getPriority();
        }

        if (tmpList.size() > 1) {
            Collections.shuffle(tmpList);
        }

        returnList.addAll(tmpList);


        if (returnList.size() > count) {
            return returnList.subList(0, count);
        }
        return returnList;
    }

    @Override
    public List<StructuredContent> lookupStructuredContentItemsByType(SandBox sandBox, StructuredContentType contentType, Locale locale, Integer count, Map<String, Object> ruleDTOs) {
        List<StructuredContent> productionContentList = null;
        List<StructuredContent> sandBoxContentList = null;
        productionContentList = structuredContentDao.findActiveStructuredContentByType(getProductionSandBox(sandBox), contentType, locale);
        if (! isProductionSandBox(sandBox)) {
            sandBoxContentList = structuredContentDao.findActiveStructuredContentByType(sandBox, contentType, locale);
        }

        List<StructuredContent> contentList = mergeContent(productionContentList, sandBoxContentList);
        return evaluateAndPriortizeContent(contentList, count, ruleDTOs);
    }

    @Override
    public List<StructuredContent> lookupStructuredContentItemsByName(SandBox sandBox, StructuredContentType contentType, String contentName, org.broadleafcommerce.cms.locale.domain.Locale locale, Integer count, Map<String, Object> ruleDTOs) {
        List<StructuredContent> productionContentList = null;
        List<StructuredContent> sandBoxContentList = null;
        productionContentList = structuredContentDao.findActiveStructuredContentByNameAndType(getProductionSandBox(sandBox), contentType, contentName, locale);
        if (! isProductionSandBox(sandBox)) {
            sandBoxContentList = structuredContentDao.findActiveStructuredContentByNameAndType(sandBox, contentType, contentName, locale);
        }

        List<StructuredContent> contentList = mergeContent(productionContentList, sandBoxContentList);
        return evaluateAndPriortizeContent(contentList, count, ruleDTOs);
    }

    private SandBox getProductionSandBox(SandBox currentSandBox) {
        SandBox productionSandBox = null;
        if (currentSandBox == null || SandBoxType.PRODUCTION.equals(currentSandBox.getSandBoxType())) {
            productionSandBox = currentSandBox;
        } else if (currentSandBox.getSite() != null) {
            productionSandBox = currentSandBox.getSite().getProductionSandbox();
        }
        return productionSandBox;
    }

    private boolean isProductionSandBox(SandBox dest) {
        if (dest == null) {
            return true;
        } else {
            return SandBoxType.PRODUCTION.equals(dest.getSandBoxType());
        }
    }

    @Override
    public void itemPromoted(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STRUCTURED_CONTENT.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }

        StructuredContent sc = structuredContentDao.findStructuredContentById(sandBoxItem.getTemporaryItemId());

        if (sc == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Structured Content Item not found " + sandBoxItem.getTemporaryItemId());
            }
        } else {
            if (isProductionSandBox(destinationSandBox) && sc.getOriginalItemId() != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Structured content promoted to production.  " + sc.getId() + ".  Archiving original item " + sc.getOriginalItemId());
                }
                StructuredContent originalSC = structuredContentDao.findStructuredContentById(sandBoxItem.getOriginalItemId());
                originalSC.setArchivedFlag(Boolean.TRUE);
                structuredContentDao.addOrUpdateContentItem(originalSC, false);

               // We are archiving the old item and making this the new "production item", so
               // null out the original item id before saving.
                sc.setOriginalItemId(null);
                sc.setLockedFlag(false);
            } else {
                sc.setLockedFlag(true);
            }

        }
        sc.setSandbox(destinationSandBox);
        structuredContentDao.addOrUpdateContentItem(sc, false);
    }

    @Override
    public void itemRejected(SandBoxItem sandBoxItem, SandBox destinationSandBox) {
        if (! SandBoxItemType.STRUCTURED_CONTENT.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StructuredContent sc = structuredContentDao.findStructuredContentById(sandBoxItem.getTemporaryItemId());

        if (sc != null) {
            sc.setSandbox(destinationSandBox);
            structuredContentDao.addOrUpdateContentItem(sc, false);
        }
    }

    @Override
    public void itemReverted(SandBoxItem sandBoxItem) {
        if (! SandBoxItemType.STRUCTURED_CONTENT.equals(sandBoxItem.getSandBoxItemType())) {
            return;
        }
        StructuredContent sc = structuredContentDao.findStructuredContentById(sandBoxItem.getTemporaryItemId());

        if (sc != null) {
            sc.setArchivedFlag(Boolean.TRUE);
            structuredContentDao.addOrUpdateContentItem(sc, false);

            StructuredContent originalSc = structuredContentDao.findStructuredContentById(sandBoxItem.getOriginalItemId());
            originalSc.setLockedFlag(false);
            structuredContentDao.addOrUpdateContentItem(originalSc, false);
        }
    }
}
