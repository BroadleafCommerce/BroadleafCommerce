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

package org.broadleafcommerce.openadmin.web.service;

import org.broadleafcommerce.common.exception.ServiceException;
import org.broadleafcommerce.openadmin.client.dto.ClassMetadata;
import org.broadleafcommerce.openadmin.server.domain.PersistencePackageRequest;
import org.broadleafcommerce.openadmin.server.service.AdminEntityService;
import org.broadleafcommerce.openadmin.web.translation.MVELTranslationException;
import org.broadleafcommerce.openadmin.web.translation.RuleBuilderUtil;
import org.broadleafcommerce.openadmin.web.translation.dto.ConditionsDTO;
import org.broadleafcommerce.openadmin.web.translation.dto.RuleBuilderDTO;
import org.springframework.stereotype.Service;

import com.gwtincubator.security.exception.ApplicationSecurityException;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

/**
 * @author Elbert Bautista (elbertbautista)
 */
@Service("blRuleBuilderService")
public class RuleBuilderServiceImpl implements RuleBuilderService {

    @Resource(name = "blAdminEntityService")
    protected AdminEntityService adminEntityService;

    @Override
    public ConditionsDTO buildConditionsDTO(List<RuleBuilderDTO> dtoList, String mvel)
            throws ServiceException, ApplicationSecurityException, MVELTranslationException, IOException {
        ConditionsDTO conditions = null;
        for (RuleBuilderDTO dto : dtoList) {
            PersistencePackageRequest request = PersistencePackageRequest.standard()
                    .withClassName(dto.getCeilingEntity())
                    .withForeignKeys(dto.getForeignKeys())
                    .withConfigKey(dto.getConfigKey());
            ClassMetadata cmd = adminEntityService.getClassMetadata(request);

            RuleBuilderUtil ruleBuilderUtil = new RuleBuilderUtil();
            conditions = ruleBuilderUtil.createConditionsDTO(mvel, cmd.getProperties(), cmd.getPolymorphicEntities());
            //ObjectMapper mapper = new ObjectMapper();
            //json = mapper.writeValueAsString(conditions);
        }

        return conditions;
    }
}
