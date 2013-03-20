/*
 * Copyright 2008-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.broadleafcommerce.openadmin.server.cto;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Component;

import com.anasoft.os.daofusion.criteria.AssociationPath;
import com.anasoft.os.daofusion.criteria.FilterCriterionProvider;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider;
import com.anasoft.os.daofusion.criteria.SimpleFilterCriterionProvider.FilterDataStrategy;

/**
 * Utility class providing common {@link FilterCriterionProvider}
 * implementations.
 * 
 * @author jfischer
 */
@Component("blFilterCriterionProviders")
public class FilterCriterionProviders {
    
    public static final FilterCriterionProvider LIKE = new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            return Restrictions.ilike(targetPropertyName, (String) directValues[0], MatchMode.START);
        }
    };
    
    public static final FilterCriterionProvider EQ = new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 1) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            return Restrictions.eq(targetPropertyName, directValues[0]);
        }
    };
    
    public static final FilterCriterionProvider ISNULL = new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 1) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            return Restrictions.isNull(targetPropertyName);
        }
    };
    
    public static final FilterCriterionProvider LE = new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            return Restrictions.le(targetPropertyName, directValues[0]);
        }
    };
    
    public static final FilterCriterionProvider BETWEEN = new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 2) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            if (directValues.length > 1) {
                return Restrictions.between(targetPropertyName, directValues[0], directValues[1]);
            } else {
                return Restrictions.eq(targetPropertyName, directValues[0]);
            }
        }
    };

    public static final FilterCriterionProvider BETWEEN_DATE = new SimpleFilterCriterionProvider(FilterDataStrategy.NONE, 2) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            if (directValues.length > 2) {
                return Restrictions.between(targetPropertyName, directValues[0], directValues[2]);
            } else if (directValues[0]==null) {
                return Restrictions.lt(targetPropertyName, directValues[1]);
            } else if (directValues[1]==null) {
                return Restrictions.ge(targetPropertyName, directValues[0]);
            } else {
                return Restrictions.eq(targetPropertyName, directValues[0]);
            }
        }
    };
    
    public static final FilterCriterionProvider COLLECTION_SIZE_EQ = new SimpleFilterCriterionProvider(FilterDataStrategy.DIRECT, 1) {
        @Override
        public Criterion getCriterion(String targetPropertyName, Object[] filterObjectValues, Object[] directValues) {
            return Restrictions.sizeEq(targetPropertyName, (Integer) directValues[0]);
        }
    };
    
    public FilterCriterionProvider getLikeProvider(AssociationPath path) {
        return LIKE;
    }

    public FilterCriterionProvider getEqProvider(AssociationPath path) {
        return EQ;
    }

    public FilterCriterionProvider getIsNullProvider(AssociationPath path) {
        return ISNULL;
    }

    public FilterCriterionProvider getLessThanOrEqualProvider(AssociationPath path) {
        return LE;
    }

    public FilterCriterionProvider getBetweenProvider(AssociationPath path) {
        return BETWEEN;
    }

    public FilterCriterionProvider getBetweenDateProvider(AssociationPath path) {
        return BETWEEN_DATE;
    }

    public FilterCriterionProvider getCollectionSizeEqualsProvider(AssociationPath path) {
        return COLLECTION_SIZE_EQ;
    }

}
