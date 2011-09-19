/*
 * Copyright 2008-20011 the original author or authors.
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
package org.broadleafcommerce.cms.field.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.broadleafcommerce.cms.field.dao.FileDao;
import org.broadleafcommerce.cms.field.domain.FieldData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created by bpolster.
 */
@Service("blFileService")
public class FileServiceImpl implements FileService {

    private static final Log LOG = LogFactory.getLog(FileServiceImpl.class);

    @Resource(name="blFileDao")
    protected FileDao fileDao;

    @Override
    public FieldData readFieldDataById(Long id) {
        return fileDao.readFieldDataById(id);
    }

    @Override
    public FieldData updateFieldData(FieldData fieldData) {
        return fileDao.updateFieldData(fieldData);
    }

    @Override
    public void delete(FieldData fieldData) {
        fileDao.delete(fieldData);
    }

    @Override
    public FieldData addFieldData(FieldData fieldData) {
        return fileDao.addFieldData(fieldData);
    }

}
