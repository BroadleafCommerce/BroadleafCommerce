/*
 * #%L
 * BroadleafCommerce Open Admin Platform
 * %%
 * Copyright (C) 2009 - 2013 Broadleaf Commerce
 * %%
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
 * #L%
 */
package org.broadleafcommerce.openadmin.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.broadleafcommerce.openadmin.dto.BasicFieldMetadata;
import org.broadleafcommerce.openadmin.server.service.persistence.validation.RequiredPropertyValidator;
import org.junit.Test;


/**
 * 
 *
 * @author Phillip Verheyden (phillipuniverse)
 */
public class PropertyValidatorTest {

    @Test
    public void testRequiredValidator() {
        RequiredPropertyValidator validator = new RequiredPropertyValidator();
        BasicFieldMetadata md = new BasicFieldMetadata();

        md.setRequired(true);        
        //null values aren't valid
        assertFalse(validator.validate(null, null, null, md, null, null).isValid());
        //empty strings aren't valid
        assertFalse(validator.validate(null, null, null, md, null, "").isValid());
        //anything else is
        assertTrue(validator.validate(null, null, null, md, null, "testest").isValid());
        assertTrue(validator.validate(null, null, null, md, null, "1.8832").isValid());

        //non-required should pass null and empty
        md.setRequired(false);
        assertTrue(validator.validate(null, null, null, md, null, null).isValid());
        assertTrue(validator.validate(null, null, null, md, null, "").isValid());
        
        //required override should always take precedence
        md.setRequired(true);
        md.setRequiredOverride(false);
        assertTrue(validator.validate(null, null, null, md, null, null).isValid());
        assertTrue(validator.validate(null, null, null, md, null, "").isValid());
        assertTrue(validator.validate(null, null, null, md, null, "testtest").isValid());
        
        md.setRequired(false);
        md.setRequiredOverride(true);
        assertFalse(validator.validate(null, null, null, md, null, null).isValid());
        assertFalse(validator.validate(null, null, null, md, null, "").isValid());
        assertTrue(validator.validate(null, null, null, md, null, "testtest").isValid());
    }
    
}
