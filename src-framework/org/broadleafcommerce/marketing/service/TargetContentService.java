package org.broadleafcommerce.marketing.service;

import java.util.List;

import org.broadleafcommerce.marketing.domain.TargetContent;

public interface TargetContentService {
    public TargetContent findTargetContentById(Long targetContentId);

    public void removeTargetContent(Long targetContentId);

    public TargetContent updateTargetContent(TargetContent targetContent);

    public List<TargetContent> findTargetContents();

    public List<TargetContent> findTargetContentsByPriority(int priority);

    public List<TargetContent> findTargetContentsByNameType(String name, String type);
}
