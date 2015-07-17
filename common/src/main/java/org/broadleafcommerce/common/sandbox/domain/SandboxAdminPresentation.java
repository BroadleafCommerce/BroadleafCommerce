/*
 * #%L
 * BroadleafCommerce Framework
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
package org.broadleafcommerce.common.sandbox.domain;

import org.broadleafcommerce.common.presentation.AdminGroupPresentation;
import org.broadleafcommerce.common.presentation.AdminPresentationClass;
import org.broadleafcommerce.common.presentation.AdminTabPresentation;

/**
 * @author Chris Kittrell (ckittrell)
 */
@AdminPresentationClass(
    tabs = {
        @AdminTabPresentation(
            groups = {
                @AdminGroupPresentation(name = SandboxAdminPresentation.GroupName.Description,
                    order = SandboxAdminPresentation.GroupOrder.Description,
                    borderless = true)
            }
        )
    }
)
public interface SandboxAdminPresentation {

    public static class TabName {
    }

    public static class TabOrder {
    }

    public static class GroupName {
        public static final String Description = "SandBoxImpl_Description";
    }

    public static class GroupOrder {
        public static final int Description = 1000;
    }

}
