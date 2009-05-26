package org.broadleafcommerce.marketing.service;

import java.util.List;

import javax.annotation.Resource;

import org.broadleafcommerce.marketing.dao.TargetContentDao;
import org.broadleafcommerce.marketing.domain.TargetContent;
import org.springframework.stereotype.Service;

@Service("targetContentService")
public class TargetContentServiceImpl implements TargetContentService {

    @Resource
    private TargetContentDao targetContentDao;

    @Override
    public TargetContent findTargetContentById(Long targetContentId) {
        return targetContentDao.readTargetContentById(targetContentId);
    }

    @Override
    public List<TargetContent> findTargetContents() {
        return targetContentDao.readTargetContents();
    }

    @Override
    public List<TargetContent> findTargetContentsByNameType(String name, String type) {
        return targetContentDao.readCurrentTargetContentByNameType(name, type);
    }

    @Override
    public List<TargetContent> findTargetContentsByPriority(int priority) {
        return targetContentDao.readCurrentTargetContentsByPriority(priority);
    }

    @Override
    public void removeTargetContent(Long targetContentId) {
        targetContentDao.delete(targetContentId);
    }

    @Override
    public TargetContent updateTargetContent(TargetContent targetContent) {
        return targetContentDao.save(targetContent);
    }

}
