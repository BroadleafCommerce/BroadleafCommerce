package org.broadleafcommerce.marketing.dao;

import java.util.List;

import org.broadleafcommerce.marketing.domain.TargetContent;

public interface TargetContentDao {

    public TargetContent readTargetContentById(Long targetContentId);

    public List<TargetContent> readTargetContents();

    public TargetContent save(TargetContent targetContent);

    public void delete(Long targetContentId);

    public List<TargetContent> readCurrentTargetContentsByPriority(int priority);

    public List<TargetContent> readCurrentTargetContentByNameType(String name, String type);

}
