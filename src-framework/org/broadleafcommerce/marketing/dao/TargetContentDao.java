package org.broadleafcommerce.marketing.dao;

import java.util.List;

import org.broadleafcommerce.marketing.domain.TargetContent;

public interface TargetContentDao {

    public TargetContent readTargetContentById(Long targetContentId);

    public List<TargetContent> readTargetContents();

    public TargetContent maintainTargetContent(TargetContent targetContent);

    public void deleteTargetContent(Long targetContentId);

    public List<TargetContent> readCurrentTargetContentsByPriority(int priority);

    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type);

}
