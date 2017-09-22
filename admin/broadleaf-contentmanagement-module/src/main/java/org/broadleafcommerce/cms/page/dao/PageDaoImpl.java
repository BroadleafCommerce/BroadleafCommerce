/*
 * #%L
 * BroadleafCommerce CMS Module
 * %%
 * Copyright (C) 2009 - 2016 Broadleaf Commerce
 * %%
 * Licensed under the Broadleaf Fair Use License Agreement, Version 1.0
 * (the "Fair Use License" located  at http://license.broadleafcommerce.org/fair_use_license-1.0.txt)
 * unless the restrictions on use therein are violated and require payment to Broadleaf in which case
 * the Broadleaf End User License Agreement (EULA), Version 1.1
 * (the "Commercial License" located at http://license.broadleafcommerce.org/commercial_license-1.1.txt)
 * shall apply.
 * 
 * Alternatively, the Commercial License may be replaced with a mutually agreed upon license (the "Custom License")
 * between you and Broadleaf Commerce. You may not use this file except in compliance with the applicable license.
 * #L%
 */
package org.broadleafcommerce.cms.page.dao;

import org.apache.commons.lang3.time.DateUtils;
import org.broadleafcommerce.cms.page.domain.Page;
import org.broadleafcommerce.cms.page.domain.PageField;
import org.broadleafcommerce.cms.page.domain.PageFieldImpl;
import org.broadleafcommerce.cms.page.domain.PageImpl;
import org.broadleafcommerce.cms.page.domain.PageTemplate;
import org.broadleafcommerce.cms.page.domain.PageTemplateImpl;
import org.broadleafcommerce.common.locale.domain.Locale;
import org.broadleafcommerce.common.persistence.EntityConfiguration;
import org.broadleafcommerce.common.sandbox.domain.SandBox;
import org.broadleafcommerce.common.sandbox.domain.SandBoxImpl;
import org.broadleafcommerce.common.time.SystemTime;
import org.broadleafcommerce.common.util.DateUtil;
import org.hibernate.ejb.QueryHints;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by bpolster.
 */
@Repository("blPageDao")
public class PageDaoImpl implements PageDao {

    private static SandBox DUMMY_SANDBOX = new SandBoxImpl();

    {
        DUMMY_SANDBOX.setId(-1l);
    }

    @PersistenceContext(unitName = "blPU")
    protected EntityManager em;

    @Resource(name = "blEntityConfiguration")
    protected EntityConfiguration entityConfiguration;

    protected Long currentDateResolution = 10 * 60 * 1000L;

    protected Date cachedDate = SystemTime.asDate();


    @Override
    public Page readPageById(Long id) {
        return em.find(PageImpl.class, id);
    }

    @Override
    public List<PageField> readPageFieldsByPageId(Long pageId) {
        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<PageField> criteria = builder.createQuery(PageField.class);
        Root<PageFieldImpl> pageField = criteria.from(PageFieldImpl.class);
        criteria.select(pageField);

        Path<Object> path = pageField.get("page").get("id");
        criteria.where(builder.equal(pageField.get("page").get("id"), pageId));

        TypedQuery<PageField> query = em.createQuery(criteria);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public PageTemplate readPageTemplateById(Long id) {
        return em.find(PageTemplateImpl.class, id);
    }


    @Override
    public PageTemplate savePageTemplate(PageTemplate template) {
        return em.merge(template);
    }

    @Override
    public Page updatePage(Page page) {
        return em.merge(page);
    }

    @Override
    public void delete(Page page) {
        if (!em.contains(page)) {
            page = readPageById(page.getId());
        }
        em.remove(page);
    }

    @Override
    public Page addPage(Page clonedPage) {
        return em.merge(clonedPage);
    }

    @Override
    public List<Page> findPageByURI(String uri) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Page> criteriaQuery = builder.createQuery(Page.class);
        Root pageRoot = criteriaQuery.from(PageImpl.class);
        criteriaQuery.select(pageRoot);
        
        List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.equal(pageRoot.get("fullUrl"), uri));

        Date currentDate = DateUtil.getCurrentDateAfterFactoringInDateResolution(cachedDate, getCurrentDateResolution());

        addActiveDateRestrictions(builder, pageRoot, restrictions, currentDate, currentDate);
        addOfflineRestriction(builder, pageRoot, restrictions);

        criteriaQuery.where(restrictions.toArray(new Predicate[restrictions.size()]));
        
        return getResultForQueryAndCache(criteriaQuery);
    }
    
    protected void addActiveDateRestrictions(final CriteriaBuilder builder, final Root pageRoot, final List<Predicate> restrictions, Date afterStartDate, Date beforeEndDate) {
        restrictions.add(builder.or(
                builder.isNull(pageRoot.get("activeStartDate")),
                builder.lessThanOrEqualTo(pageRoot.get("activeStartDate").as(Date.class), afterStartDate)));
        restrictions.add(builder.or(
                builder.isNull(pageRoot.get("activeEndDate")),
                builder.greaterThanOrEqualTo(pageRoot.get("activeEndDate").as(Date.class), beforeEndDate)));
    }

    protected void addOfflineRestriction(final CriteriaBuilder builder, final Root pageRoot, final List<Predicate> restrictions) {
        restrictions.add(builder.or(
                builder.isNull(pageRoot.get("offlineFlag")),
                builder.isFalse(pageRoot.get("offlineFlag"))));
    }
    
    protected List<Page> getResultForQueryAndCache(final CriteriaQuery<Page> criteriaQuery) {
        TypedQuery<Page> q = em.createQuery(criteriaQuery);
        q.setHint(QueryHints.HINT_CACHEABLE, true);
        q.setHint(QueryHints.HINT_CACHE_REGION, "query.Cms");

        try {
            return q.getResultList();
        } catch (NoResultException | EntityNotFoundException e) {
            return Collections.emptyList();
        }
    }
    
    @Override
    public List<Page> findPageByURIAndActiveDate(final String uri, final Date activeDate) {
        final CriteriaBuilder builder = em.getCriteriaBuilder();
        final CriteriaQuery<Page> criteriaQuery = builder.createQuery(Page.class);
        final Root pageRoot = criteriaQuery.from(PageImpl.class);
        criteriaQuery.select(pageRoot);

        final List<Predicate> restrictions = new ArrayList<>();
        restrictions.add(builder.equal(pageRoot.get("fullUrl"), uri));

        final Date nextDay = DateUtils.addDays(activeDate, 1);

        addOfflineRestriction(builder, pageRoot, restrictions);
        addActiveDateRestrictions(builder, pageRoot, restrictions, nextDay, activeDate);
        
        criteriaQuery.where(restrictions.toArray(new Predicate[restrictions.size()]));

        List<Page> pages = getResultForQueryAndCache(criteriaQuery);
        
        return filterInactive(pages);
    }
    
    protected List<Page> filterInactive(final List<Page> pages) {
        final ListIterator<Page> pagesIterator = pages.listIterator();
        
        while (pagesIterator.hasNext()) {
            final Page page = pagesIterator.next();
            
            if (!isActiveNow(page)) {
                pagesIterator.remove();
            }
        }

        return pages;
    }
    
    protected boolean isActiveNow(final Page page) {
        final Date now = new Date();
        final Date activeStartDate = page.getActiveStartDate();
        final Date activeEndDate = page.getActiveEndDate();

        return !((activeStartDate != null && now.before(activeStartDate))
                 || (activeEndDate != null && now.after(activeEndDate)));
    }

    @Override
    public List<Page> findPageByURI(Locale fullLocale, Locale languageOnlyLocale, String uri) {
        Query query;

        if (languageOnlyLocale == null) {
            languageOnlyLocale = fullLocale;
        }
        query = em.createNamedQuery("BC_READ_PAGE_BY_URI");
        query.setParameter("fullLocale", fullLocale);
        query.setParameter("languageOnlyLocale", languageOnlyLocale);
        query.setParameter("uri", uri);
        query.setHint(QueryHints.HINT_CACHEABLE, true);

        try {
            return query.getResultList();
        } catch (NoResultException | EntityNotFoundException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Page> readAllPages() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Page> criteria = builder.createQuery(Page.class);
        Root<PageImpl> page = criteria.from(PageImpl.class);

        criteria.select(page);

        try {
            Query query = em.createQuery(criteria);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Page> readOnlineAndIncludedPages(int limit, int offset, String sortBy) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Page> criteria = builder.createQuery(Page.class);
        Root<PageImpl> page = criteria.from(PageImpl.class);
        criteria.select(page);
        criteria.where(builder.and(
                builder.or(builder.isFalse(page.get("offlineFlag").as(Boolean.class)), builder.isNull(page.get("offlineFlag").as(Boolean.class))),
                builder.or(builder.isFalse(page.get("excludeFromSiteMap").as(Boolean.class)), builder.isNull(page.get("excludeFromSiteMap").as(Boolean.class)))));
        criteria.orderBy(builder.asc(page.get(sortBy)));
        TypedQuery<Page> query = em.createQuery(criteria);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        query.setHint(QueryHints.HINT_CACHEABLE, true);
        return query.getResultList();
    }

    @Override
    public List<PageTemplate> readAllPageTemplates() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<PageTemplate> criteria = builder.createQuery(PageTemplate.class);
        Root<PageTemplateImpl> template = criteria.from(PageTemplateImpl.class);

        criteria.select(template);

        try {
            Query query = em.createQuery(criteria);
            query.setHint(QueryHints.HINT_CACHEABLE, true);
            return query.getResultList();
        } catch (NoResultException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Page> findPageByURI(Locale locale, String uri) {
        return findPageByURI(locale, null, uri);
    }

    @Override
    public void detachPage(Page page) {
        em.detach(page);
    }

    public Long getCurrentDateResolution() {
        return currentDateResolution;
    }

    public void setCurrentDateResolution(Long currentDateResolution) {
        this.currentDateResolution = currentDateResolution;
    }

}
